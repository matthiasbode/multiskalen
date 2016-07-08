/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms.pathsearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class ClassicalYenAlgorithm<E, B extends EdgeWeight<B>>
                                        implements KShortestPathAlgorithm<E, B>{
    /**
     *
     */
    private PairShortestPathAlgorithm<E, B> shortestPathAlgorithm;


    /**
     *
     */
    private Comparator<WeightedPath<E,B>> pathComparator;

    private HashMap<WeightedPath<E,B>,Double> weightPerCandidate = new HashMap<WeightedPath<E, B>, Double>();
    
    public ClassicalYenAlgorithm() {
        shortestPathAlgorithm = new DijkstraAlgorithm<E, B>();
    }


    public ClassicalYenAlgorithm(PairShortestPathAlgorithm<E, B> shortestPathAlgorithm) {
        this.shortestPathAlgorithm = shortestPathAlgorithm;
    }


    public void setPathComparator(Comparator<WeightedPath<E, B>>
                                                               pathComparator) {
        this.pathComparator = pathComparator;
    }

    public PairShortestPathAlgorithm<E, B> getShortestPathAlgorithm() {
        return shortestPathAlgorithm;
    }

    public void setShortestPathAlgorithm(PairShortestPathAlgorithm<E, B> shortestPathAlgorithm) {
        this.shortestPathAlgorithm = shortestPathAlgorithm;
    }


    /**
     *
     * @inheritDoc
     */
    @Override
    public ArrayList<WeightedPath<E, B>> kShortestPaths(
                    WeightedDirectedGraph<E, B> graph, E source, E destination, int K) {
        weightPerCandidate.clear();
        
        ArrayList<WeightedPath<E, B>> shortestPaths =
                                            new ArrayList<WeightedPath<E, B>>();

        WeightedPath<E, B> shortestPath =
                shortestPathAlgorithm.singlePairShortestPath(graph,
                                                source, destination);

        shortestPaths.add(shortestPath);

        //Comparator for comparing to WeightedPaths
            pathComparator = new Comparator<WeightedPath<E,B>>() {
                public int compare(WeightedPath<E, B> t, WeightedPath<E, B> t1){
                    return t.getWeight().compareTo(t1.getWeight());
                }
            };

        //candidates for the k-th shortest path
        PriorityQueue<WeightedPath<E,B>> pathCandidates =
                     new PriorityQueue<WeightedPath<E, B>>(
                          K*shortestPath.getNumberOfVertices(), pathComparator);

        Pair<E,E> doNotUse = graph.edgeSet().iterator().next();
        B nullElement = graph.getEdgeWeight(doNotUse).getNullElement();
        B einsElement = graph.getEdgeWeight(doNotUse).getEinsElement();
        doNotUse = null;

        int k=1;

        while (k<K) {
//            System.out.println("Neue Suche nach: " + shortestPaths.get(k-1));
            ArrayList<WeightedPath<E,B>> tmpStore = new ArrayList<WeightedPath<E, B>>();
            while(!pathCandidates.isEmpty()) {
                tmpStore.add(pathCandidates.poll());
            }
            for (WeightedPath<E, B> weightedPath : tmpStore) {
                pathCandidates.add(weightedPath);
            }
            //last added path
            WeightedPath<E,B> actualPath =
                    shortestPaths.get(shortestPaths.size()-1);

            //temporary path, which represents the path from start vertex to the
            //first vertex of the edge, which weight is set to the einselement
            WeightedPath<E,B> tempPath =
                                     new WeightedPath<E, B>(einsElement,source);
            
            //loop over all edges of the k-1-shortest path
            for (Pair<E, E> edge : actualPath.getPathEdges()) {
                B edgeWeight = graph.getEdgeWeight(edge);
                if(graph.outDegreeOf(edge.getFirst())!=1) {
                    graph.removeEdge(edge);
                    
                    Map<Pair<E,E>, B> edgesOfShortestPaths = new HashMap<Pair<E, E>, B>();
                    for (WeightedPath<E, B> weightedPath : shortestPaths) {
                        if(weightedPath.containsSubPath(tempPath)) {
                            for (Pair<E, E> sPathEdge : weightedPath.getPathEdges()) {
                                if(sPathEdge.getFirst().equals(edge.getFirst())) {
                                    edgesOfShortestPaths.put(sPathEdge, graph.getEdgeWeight(sPathEdge));
                                    graph.removeEdge(sPathEdge);
                                    break;
                                }
                            }
                        }
                    }
                    WeightedPath<E,B> tmpPath =
                            shortestPathAlgorithm.singlePairShortestPath(
                                graph, edge.getFirst(), destination);
                    if(tmpPath != null) {
                        WeightedPath<E,B> candidate = tempPath.product(tmpPath);

                        if(!pathCandidates.contains(candidate)) {
                            if(shortestPaths.contains(candidate))
                                throw new UnsupportedOperationException(
                                       "Kandidat: " + candidate + " darf nie "
                                       + "in gefundenen Pfaden enthalten sein");
                            pathCandidates.add(candidate);
                        }
                    }
                    for (Pair<E, E> sPathEdge : edgesOfShortestPaths.keySet()) {
                        graph.addEdge(sPathEdge, edgesOfShortestPaths.get(sPathEdge));
                    }
                }
                tempPath.appendEdge(edge);
                tempPath.setWeight(tempPath.getWeight().product(edgeWeight));

                //set the edge weight to the original value
                graph.addEdge(edge, edgeWeight);
                
            }
            if(pathCandidates.isEmpty())
                return shortestPaths;
            //the k-th shortest path is the first path in the queue
            shortestPaths.add(pathCandidates.poll());

            //increasing k
            k++;
        }        
        return shortestPaths;
    }

    @Override
    public String toString() {
        return "ClassicalYenAlgorithm{" + " using : " + shortestPathAlgorithm + '}';
    }

}