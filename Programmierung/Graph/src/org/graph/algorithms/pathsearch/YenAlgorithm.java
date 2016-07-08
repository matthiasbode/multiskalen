/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.algorithms.pathsearch;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.util.Pair;

/**
 *
 * @author nilsrinke
 */
public class YenAlgorithm<E, B extends EdgeWeight<B>>
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
    
    public YenAlgorithm() {
        shortestPathAlgorithm = new DijkstraAlgorithm<E, B>();
    }


    public YenAlgorithm(PairShortestPathAlgorithm<E, B> shortestPathAlgorithm) {
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
            pathComparator = new PathComparator(shortestPaths);
//            pathComparator = new Comparator<WeightedPath<E,B>>() {
//                public int compare(WeightedPath<E, B> t, WeightedPath<E, B> t1){
//                    return t.getWeight().compareTo(t1.getWeight());
//                }
//            };

        //candidates for the k-th shortest path
        PriorityQueue<WeightedPath<E,B>> pathCandidates =
                     new PriorityQueue<WeightedPath<E, B>>(
                          K*shortestPath.getNumberOfVertices(), pathComparator);

        Pair<E,E> doNotUse = graph.edgeSet().iterator().next();
        B nullElement = graph.getEdgeWeight(doNotUse).getNullElement();
        B einsElement = graph.getEdgeWeight(doNotUse).getEinsElement();
        doNotUse = null;

        WeightedDirectedGraph<E,B> dual = graph.dual();
        int k=1;

        while (k<K) {
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
                    graph.setEdgeWeight(edge, nullElement);
                    WeightedPath<E,B> tmpPath =
                            shortestPathAlgorithm.singlePairShortestPath(
                                graph, edge.getFirst(), destination);
                    WeightedPath<E,B> candidate = tempPath.product(tmpPath);

                    if(!shortestPaths.contains(candidate))
                        pathCandidates.add(candidate);
                }
                tempPath.appendEdge(edge);
                tempPath.setWeight(tempPath.getWeight().product(edgeWeight));

                //set the edge weight to the original value
                graph.setEdgeWeight(edge, edgeWeight);
                
            }
            WeightedPath<E,B> invertedPath = actualPath.invertPath();
            tempPath = new WeightedPath<E, B>(einsElement,destination);
            for (Pair<E, E> edge : invertedPath.getPathEdges()) {
                B edgeWeight = dual.getEdgeWeight(edge.getFirst(),edge.getSecond());
                if(dual.outDegreeOf(edge.getFirst())!=1) {
                    dual.setEdgeWeight(edge, nullElement);
                    WeightedPath<E,B> tmpPath =
                            shortestPathAlgorithm.singlePairShortestPath(
                                      dual, edge.getFirst(), source);
                    WeightedPath<E,B> candidate = tempPath.product(tmpPath);
                    candidate = candidate.invertPath();
                    if(!shortestPaths.contains(candidate))
                        pathCandidates.add(candidate);
                }
                tempPath.appendEdge(edge);
                tempPath.setWeight(tempPath.getWeight().product(edgeWeight));

                //set the edge weight to the original value
                graph.setEdgeWeight(edge, edgeWeight);

            }
            //the k-th shortest path is the first path in the queue
            shortestPaths.add(pathCandidates.poll());

            //increasing k
            k++;
        }        
        return shortestPaths;
    }

    
    class PathComparator implements Comparator<WeightedPath<E,B>> {
        ArrayList<WeightedPath<E, B>> shortestPaths;

        public PathComparator(ArrayList<WeightedPath<E, B>> shortestPaths) {
            this.shortestPaths = shortestPaths;
        }


        @Override
        public int compare(WeightedPath<E, B> p1, WeightedPath<E, B> p2) {
            if(shortestPaths.isEmpty())
                return p1.getWeight().compareTo(p2.getWeight());
            if(p1.getWeight().equals(p1.getWeight().getNullElement())) {
                if(p2.getWeight().equals(p2.getWeight().getNullElement()))
                    return 0;
                return 1;
            } else if(p2.getWeight().equals(p2.getWeight().getNullElement())) {
                return -1;
            }

            double edgeOverlapping = getProcentualOverlap(p1);
//            System.out.println("overlap: " + edgeOverlapping);
            double weight = shortestPaths.get(0).getWeight().doubleValue()/
                            p1.getWeight().doubleValue();
//            System.out.println("weight: " + weight);
            double c1 = edgeOverlapping*weight;
            weightPerCandidate.put(p1, c1);

            edgeOverlapping = getProcentualOverlap(p2);
            weight = shortestPaths.get(0).getWeight().doubleValue()/
                     p2.getWeight().doubleValue();
            double c2 = edgeOverlapping*weight;
            weightPerCandidate.put(p2, c2);
            if(c1 > c2)
                return -1;
            if(c1 == c2)
                return 0;
            else
                return +1;
        }


        private double getProcentualOverlap(WeightedPath<E, B> path) {
            ArrayList<Double> overlapping = new ArrayList<Double>();
            double edgeOverlapping = 0;
            for (WeightedPath<E, B> weightedPath : shortestPaths) {
                int overlap=0;
                for (Pair<E, E> edge : weightedPath.getPathEdges()) {
                    if(path.getPathEdges().contains(edge))
                        overlap++;
                }
                edgeOverlapping += 1.*overlap/weightedPath.getNumberOfEdges();
                overlapping.add(1.*overlap/weightedPath.getNumberOfEdges());
            }
            return 1-(edgeOverlapping/shortestPaths.size());
//            return 1-Collections.max(overlapping);
        }
    }

    @Override
    public String toString() {
        return "YenAlgorithm{" + " using : " + shortestPathAlgorithm + '}';
    }

}