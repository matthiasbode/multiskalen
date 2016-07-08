/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.algorithms.pathsearch;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.graph.Path;

import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.util.Pair;

/**
 *
 * @author nilsrinke
 */
public class YenParallelized<E, B extends EdgeWeight<B>>
                                        implements KShortestPathAlgorithm<E, B>{
    private Thread[] threads;
    private Worker[] worker;
//    private int numberOfThreads = Runtime.getRuntime().availableProcessors();
    private int numberOfThreads = 2;
    private CyclicBarrier barrier;
    private boolean finish = false;

    /**
     *
     */
    private PairShortestPathAlgorithm<E, B> shortestPathAlgorithm;


    private final ArrayList<WeightedPath<E, B>>  shortestPaths;

    /**
     * candidates for the k-th shortest path
     */
    private final PriorityQueue<WeightedPath<E,B>> pathCandidates;

    /**
     *
     */
    private Comparator<WeightedPath<E,B>> pathComparator;


    private B nullElement;
    private B einsElement;

    private WeightedPath<E,B> actualPath;
    private E source;
    private E destination;

    private int k;
    private int K;

    public YenParallelized() {
        this(new DijkstraAlgorithm<E, B>());
    }


    public YenParallelized(PairShortestPathAlgorithm<E, B> shortestPathAlgorithm) {
        this.shortestPathAlgorithm = shortestPathAlgorithm;
        barrier = new CyclicBarrier(numberOfThreads, new Merger());
        this.shortestPaths = new ArrayList<WeightedPath<E, B>>();
        this.pathComparator = new PathComparator(shortestPaths);
        this.pathCandidates=new PriorityQueue<WeightedPath<E, B>>(1, this.pathComparator);
        worker = (Worker[]) Array.newInstance(Worker.class,numberOfThreads);
        threads = new Thread[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            if(i%numberOfThreads==0)
                worker[i] = new Worker();
            else
                worker[i] = new ReverseWorker();
            threads[i] = new Thread(worker[i]);
        }
    }



    /**
     *
     * @inheritDoc
     */
    @Override
    public ArrayList<WeightedPath<E, B>> kShortestPaths(
                             WeightedDirectedGraph<E, B> graph, E source, E destination,
                             int K) {
        this.source = source;
        this.destination = destination;

        WeightedPath<E, B> shortestPath =
                      shortestPathAlgorithm.singlePairShortestPath(graph,
                                                                   source,
                                                                   destination);

        shortestPaths.add(shortestPath);

        Pair<E,E> doNotUse = graph.edgeSet().iterator().next();
        nullElement = graph.getEdgeWeight(doNotUse).getNullElement();
        einsElement = graph.getEdgeWeight(doNotUse).getEinsElement();
        doNotUse = null;

        this.K = K;
        this.k = 1;

        actualPath = shortestPath;
        int numberOfEdges = shortestPath.getNumberOfEdges();
        int slices = (int) (1.0 * numberOfEdges / numberOfThreads + .5);
        
//        for (int i = 0; i < numberOfThreads; i++) {
//            int lower = slices * i;
//            int upper = slices * (i + 1);
//            if (upper >= numberOfEdges) {
//                upper = numberOfEdges;
//            }
//            worker[i].lowerBorder = lower;
//            worker[i].upperBorder = upper;
//            worker[i].graphCopy = new WeightedGraph<E, B>(graph);
//            worker[i].subPath = actualPath.getSubPath(
//                                      source, actualPath.get(lower).getFirst());
//            if(threads[i].getState().equals(Thread.State.TERMINATED))
//                threads[i] = new Thread(worker[i]);
//        }
        for (int i = 0; i < numberOfThreads; i++) {
            int lower = 0;
            int upper = numberOfEdges;
            worker[i].lowerBorder = lower;
            worker[i].upperBorder = upper;
            if(i%numberOfThreads==0) {
                worker[i].graphCopy = new DefaultWeightedDirectedGraph<E, B>(graph);
                worker[i].subPath = actualPath.getSubPath(source, source);
            }
            else {
                ((ReverseWorker)worker[i]).dualGraph = graph.dual();
                ((ReverseWorker)worker[i]).invertPath = actualPath.invertPath();
                worker[i].subPath = actualPath.invertPath().getSubPath(destination, destination);
            }
            
            if(threads[i].getState().equals(Thread.State.TERMINATED))
                threads[i] = new Thread(worker[i]);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
           threads[0].join();
        } catch (InterruptedException e) {
           // Thread wurde abgebrochen
        }
        return shortestPaths;
    }


    private class Worker implements Runnable {
        WeightedDirectedGraph<E, B> graphCopy;

        int lowerBorder;
        int upperBorder;

        PairShortestPathAlgorithm<E, B> algorithm;

        Path<E> subPath;

        public Worker() {
            if(shortestPathAlgorithm instanceof AStarAlgorithm) {
                AStarAlgorithm<E, B> aStar = (AStarAlgorithm<E, B>) shortestPathAlgorithm;
                algorithm = new AStarAlgorithm<E, B>(aStar.Hx);
            } else if(shortestPathAlgorithm instanceof DijkstraAlgorithm)
                algorithm = new DijkstraAlgorithm<E, B>();
        }


        @Override
        public void run() {
            while (!finish) {
                B weight;
                if(lowerBorder==0)
                    weight = einsElement;
                else
                    weight = graphCopy.getEdgeWeight(subPath.get(0));
                for (int i = 1; i < subPath.getNumberOfEdges(); i++) {
                    weight = weight.product(graphCopy.getEdgeWeight(subPath.get(i)));
                }
                WeightedPath<E,B> tempPath =
                                     new WeightedPath<E, B>(weight, subPath);
                for (int i = lowerBorder; i < upperBorder; i++) {
                    Pair<E,E> edge = actualPath.get(i);
                    B edgeWeight = graphCopy.getEdgeWeight(edge);
                    int outdegree = graphCopy.outDegreeOf(edge.getFirst());
                    if(outdegree > 2 || (outdegree==2 && !graphCopy.getSuccessors(edge.getFirst()).contains(tempPath.getLastVertex()))) {
                        graphCopy.setEdgeWeight(edge, nullElement);
                        WeightedPath<E, B> tmpPath =
                                algorithm.singlePairShortestPath(
                                graphCopy, edge.getFirst(), destination);
                        if(tmpPath!=null) {
                            WeightedPath<E, B> candidate = tempPath.product(tmpPath);
                            if (!shortestPaths.contains(candidate))
                                pathCandidates.add(candidate);
                        }
                        graphCopy.setEdgeWeight(edge, edgeWeight);
                    }
                    tempPath.appendEdge(edge);
                    tempPath.setWeight(tempPath.getWeight().product(edgeWeight));               
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ReverseWorker extends Worker /*implements Runnable*/ {
        WeightedDirectedGraph<E, B> dualGraph;
        WeightedPath<E,B> invertPath;

//        int lowerBorder;
//        int upperBorder;
//
//        PairShortestPathAlgorithm<E, B> algorithm;

//        Path<E> subPath;

        public ReverseWorker() {
            if(shortestPathAlgorithm instanceof AStarAlgorithm) {
                AStarAlgorithm<E, B> aStar = (AStarAlgorithm<E, B>) shortestPathAlgorithm;
                algorithm = new AStarAlgorithm<E, B>(aStar.Hx);
            } else if(shortestPathAlgorithm instanceof DijkstraAlgorithm)
                algorithm = new DijkstraAlgorithm<E, B>();
        }


        @Override
        public void run() {
            while (!finish) {
                B weight;
                if(lowerBorder==0)
                    weight = einsElement;
                else
                    weight = dualGraph.getEdgeWeight(subPath.get(0));
                for (int i = 1; i < subPath.getNumberOfEdges(); i++) {
                    weight = weight.product(dualGraph.getEdgeWeight(subPath.get(i)));
                }
                WeightedPath<E,B> tempPath =
                                     new WeightedPath<E, B>(weight, subPath);
                for (int i = lowerBorder; i < upperBorder; i++) {
                    Pair<E,E> edge = invertPath.get(i);
                    B edgeWeight = dualGraph.getEdgeWeight(edge);
                    int outdegree = dualGraph.outDegreeOf(edge.getFirst());
                    if(outdegree > 2 || (outdegree==2 && !dualGraph.getSuccessors(edge.getFirst()).contains(tempPath.getLastVertex()))) {
                        dualGraph.setEdgeWeight(edge, nullElement);
                        WeightedPath<E, B> tmpPath =
                                algorithm.singlePairShortestPath(
                                dualGraph, edge.getFirst(), source);
                        if(tmpPath!=null) {
                            WeightedPath<E, B> candidate = tempPath.product(tmpPath);
                            candidate = candidate.invertPath();
                            if (!shortestPaths.contains(candidate))
                                pathCandidates.add(candidate);
                        }
                        dualGraph.setEdgeWeight(edge, edgeWeight);
                    }
                    tempPath.appendEdge(edge);
                    tempPath.setWeight(tempPath.getWeight().product(edgeWeight));
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class Merger implements Runnable {

        @Override
        public void run() {
            shortestPaths.add(pathCandidates.poll());
            //increasing k
            k++;
            if(k<K) {
                actualPath = shortestPaths.get(shortestPaths.size()-1);
                int numberOfEdges = actualPath.getNumberOfEdges();
                int slices = (int) (1.0 * numberOfEdges / numberOfThreads + .5);

                for (int i = 0; i < numberOfThreads; i++) {
                    int lower = 0;
                    int upper = numberOfEdges;
                    worker[i].lowerBorder = lower;
                    worker[i].upperBorder = upper;
                    if(i%numberOfThreads==0) {
                        worker[i].subPath = actualPath.getSubPath(source, source);
                    }
                    else {
                        ((ReverseWorker)worker[i]).invertPath = actualPath.invertPath();
                        worker[i].subPath = actualPath.invertPath().getSubPath(destination, destination);
                    }
                }
                
//                for (int i = 0; i < numberOfThreads; i++) {
//                    int lower = slices * i;
//                    int upper = slices * (i + 1);
//                    if (upper >= numberOfEdges) {
//                        upper = numberOfEdges;
//                    }
//                    worker[i].lowerBorder = lower;
//                    worker[i].upperBorder = upper;
//                    worker[i].subPath = actualPath.getSubPath(
//                                      source, actualPath.get(lower).getFirst());
//                }

            } else
                finish = true;
        }
    }
}
