/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.algorithms.pathsearch;


import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.graph.weighted.WeightedRootedTree;
import org.util.Pair;


/**
 *
 * @author Nils Rinke
 */
public class ParallelDijkstra<E, B extends EdgeWeight<B>> implements PairShortestPathAlgorithm<E,B> {
    private boolean finish;

    private final CyclicBarrier barrier;
    private final Worker forwardWorker;
    private final Worker reverseWorker;

    private Thread forwardThread;
    private Thread reverseThread;

    private B mue;
    private B einsWeight;
    private Pair<E,E> connectionEdge;

    
    private WeightedDirectedGraph<E, B> wGraph;

    public ParallelDijkstra() {
        barrier = new CyclicBarrier(2, new Merger());
        forwardWorker = new Worker();
        forwardWorker.forward=true;
        forwardThread  = new Thread(forwardWorker);
        reverseWorker = new Worker();
        reverseThread  = new Thread(reverseWorker);

        forwardWorker.otherWorker = reverseWorker;
        reverseWorker.otherWorker = forwardWorker;
    }
    
    
    public ParallelDijkstra(WeightedDirectedGraph<E, B> graph) {
        barrier = new CyclicBarrier(2, new Merger());
        forwardWorker = new Worker();
        forwardWorker.forward=true;
        forwardThread  = new Thread(forwardWorker);
        reverseWorker = new Worker();
        reverseThread  = new Thread(reverseWorker);

        forwardWorker.otherWorker = reverseWorker;
        reverseWorker.otherWorker = forwardWorker;
        
        this.wGraph = graph;
        forwardWorker.setGraph(graph);
        reverseWorker.setGraph(graph.dual());
        mue = wGraph.getEdgeWeight(wGraph.edgeSet().iterator().next()).getNullElement();
        einsWeight = mue.getEinsElement();
    }


    /** Calculates a shortest path between two vertices in a weighted
     *  directed graph by starting from the source and destination at the same
     *  time.
     *
     *
     *  @param graph        weighted directed graph
     *  @param source       source vertex of the shortest path
     *  @param destination  destination vertex of the shortest path
     *  @return	The method returns the shortest weighted path.
     */
    @Override
    public WeightedPath<E, B> singlePairShortestPath(
                        WeightedDirectedGraph<E, B> graph, E source, E destination) {
        mue = graph.getEdgeWeight(graph.edgeSet().iterator().next()).getNullElement();
        einsWeight = mue.getEinsElement();

        forwardWorker.setComponents(source, graph);
        reverseWorker.setComponents(destination, graph.dual());
        finish = false;
        
        if(forwardThread.getState().equals(Thread.State.TERMINATED))
            forwardThread = new Thread(forwardWorker);
        if(reverseThread.getState().equals(Thread.State.TERMINATED))
            reverseThread = new Thread(reverseWorker);
        forwardThread.start();
        reverseThread.start();

        // Warten, bis der Thread beendet ist: finish
        try {
           reverseThread.join();
        } catch (InterruptedException e) {
           // Thread wurde abgebrochen
        }
        WeightedPath<E,B> path = forwardWorker.searchTree.getWeightedPath(
                                              source,connectionEdge.getFirst());
        path.appendEdge(connectionEdge, graph.getEdgeWeight(connectionEdge));
        return path.product(reverseWorker.searchTree.getWeightedPath(
                         destination, connectionEdge.getSecond()).invertPath());
    }
    
    /** Calculates a shortest path between two vertices in a weighted
     *  directed graph by starting from the source and destination at the same
     *  time.
     *
     *
     *  @param source       source vertex of the shortest path
     *  @param destination  destination vertex of the shortest path
     *  @return	The method returns the shortest weighted path.
     */
    public WeightedPath<E, B> singlePairShortestPath(E source, E destination) {
        forwardWorker.setSource(source);
        reverseWorker.setSource(destination);
        mue = wGraph.getEdgeWeight(wGraph.edgeSet().iterator().next()).getNullElement();
        connectionEdge=null;
        finish = false;
        
        if(forwardThread.getState().equals(Thread.State.TERMINATED))
            forwardThread = new Thread(forwardWorker);
        if(reverseThread.getState().equals(Thread.State.TERMINATED))
            reverseThread = new Thread(reverseWorker);
        forwardThread.start();
        reverseThread.start();

        // Warten, bis der Thread beendet ist: finish
        try {
           reverseThread.join();
        } catch (InterruptedException e) {
           // Thread wurde abgebrochen
        }
        WeightedPath<E,B> path = forwardWorker.searchTree.getWeightedPath(
                                              source,connectionEdge.getFirst());
        path.appendEdge(connectionEdge, wGraph.getEdgeWeight(connectionEdge));
        if(connectionEdge.getSecond().equals(destination))
            return path;
        return path.product(reverseWorker.searchTree.getWeightedPath(
                         destination, connectionEdge.getSecond()).invertPath());
    }

    @Override
    public String toString() {
        return "Dijkstra (parallelisiert)";
    }
    
    

    
    private class Worker implements Runnable {
        E source;
        WeightedDirectedGraph<E, B> graph;
        WeightedRootedTree<E,B> searchTree;
        HashSet<E> initialSet;
        PriorityQueue<Item<E,B>> candidates;

        boolean forward;
        Worker otherWorker;

        public Worker() {
            candidates = new PriorityQueue<Item<E,B>>();
        }
        
        public void setSource(E source) {
            this.source = source;
            searchTree = new WeightedRootedTree<E,B>(source);
            searchTree.setVertexWeight(source, einsWeight);
            if(!candidates.isEmpty())
                candidates.clear();
            candidates.add(new Item<E,B>(source, einsWeight));
            initialSet = new HashSet<E>(graph.vertexSet());
            initialSet.remove(source);
        }
        
        public void setGraph(WeightedDirectedGraph<E, B> graph) {
            this.graph = graph;
        }

        public void setComponents(E source, WeightedDirectedGraph<E, B> graph) {
            this.source = source;
            this.graph = graph;
            searchTree = new WeightedRootedTree<E,B>(source);
            searchTree.setVertexWeight(source, einsWeight);
            candidates.clear();
            candidates.add(new Item<E,B>(source, einsWeight));
            initialSet = new HashSet<E>(graph.vertexSet());
            initialSet.remove(source);
        }


        @Override
        public void run() {
            //while a candidate is in the candidate set
            while (!finish && !candidates.isEmpty()) {
                Item<E,B> candidate = candidates.poll();
                B distance = candidate.weight;

                for (E node : graph.getSuccessors(candidate.node)) {
                    //successor in the initial set
                    if (initialSet.contains(node)) {
                        initialSet.remove(node);
                        B edge = graph.getEdgeWeight(candidate.node, node);
                        B dist = distance.product(edge);
                        candidates.add(new Item<E,B>(node, dist));
                        searchTree.addChild(node, candidate.node, edge);

                        if(otherWorker.searchTree.containsVertex(node)) {
                            B tmpMue = dist.product(otherWorker.searchTree.getVertexWeight(node));
                            if(tmpMue.compareTo(mue)<0) {
                                mue = tmpMue;
                                if(forward)
                                    connectionEdge = new Pair<E, E>(candidate.node, node);
                                else
                                    connectionEdge = new Pair<E, E>(node, candidate.node);
                            }
                        }
                    }
    //..successor in the candidate set..........................................//
                    else {
                        for (Iterator<Item<E,B>> i = candidates.iterator(); i.hasNext();) {
                            Item<E,B> c = i.next();

                            if (node.equals(c.node)) {
                                B edge = graph.getEdgeWeight(candidate.node, node);
                                B dist = distance.product(edge);

                                if(dist.compareTo(c.weight) < 0) {
                                    c.weight = dist;
                                    candidates.remove(c);
                                    candidates.add(c);
                                    searchTree.removeVertex(c.node);
                                    searchTree.addChild(c.node, candidate.node,edge);
                                }
                                break;
                            }
                        }
                    }
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

    private class Merger implements Runnable {
        @Override
        public void run() {
            if(forwardWorker.candidates.peek().weight.product(reverseWorker.candidates.peek().weight).compareTo(mue)>=0) {
                finish = true;
//                startThread.interrupt();
//                destThread.interrupt();
            }
        }
    }

    
    private class Item<E,B extends EdgeWeight<B>> implements Comparable<Item<E,B>> {
        E node;    // Knoten
        B weight;  // Gewicht

        Item(E node, B weight) {
            this.node = node;
            this.weight = weight;
        }

        @Override
        public int compareTo(Item<E,B> item) {
            return (weight.compareTo(item.weight));
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Item) {
                return node.equals(((Item) o).node);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 67 * hash + (this.node != null ? this.node.hashCode() : 0);
            return hash;
        }
    }
}
