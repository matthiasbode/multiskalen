package org.graph.algorithms;


import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.PriorityQueue;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.doubleWeighted.DoubleWeightedPath;
import org.util.FibonacciHeap;
import org.util.FibonacciHeapNode;

/**
 * DijkstraAlgorithm.java implementiert den Dijkstra-Algorithmus
 * zur Suche von Wegen in bewerteten schlichten Graphen.
 *
 * @author Nils Rinke
 */
public class Dijkstra<V> {

    private final IdentityHashMap<V,Item> itemPerVertex;
    private final DijkstraComparator comparator;
    private final PriorityQueue<V> candidates;

    private final FibonacciHeap<V> fibonacciCandidates;
    private final IdentityHashMap<V,Item> map;

    
    public Dijkstra() {
        itemPerVertex  = new IdentityHashMap<V, Item>();
        comparator     = new DijkstraComparator(itemPerVertex);
        candidates     = new PriorityQueue<V>(1, comparator);

        fibonacciCandidates = new FibonacciHeap<V>();
        map = new IdentityHashMap<V, Item>();
    }
    
    
    public DoubleWeightedPath<V> shortestPath(
                        WeightedDirectedGraph<V, Double> graph, V source, V destination) {
        fibonacciCandidates.clear();
        map.clear();
        
        Item sourceItem = new Item(source, source, 0.);
//        sourceItem.weight.setDynamicSearch(useDynamic);
        fibonacciCandidates.insert(sourceItem,0);
        map.put(source, sourceItem);
        //while a candidate is in the candidate set
        while (!fibonacciCandidates.isEmpty()) {
            Item candidate = (Item) fibonacciCandidates.removeMin();
            double distance  = candidate.weight;

            //returns shortest path if the end is reached
            if (candidate.getData().equals(destination)) {
                DoubleWeightedPath<V> path =
                                  new DoubleWeightedPath<V>(distance, destination);
                V parent = map.get(candidate.getData()).parent;
                while(!parent.equals(source)) {
                    path.appendVertexInFront(parent);
                    parent = map.get(parent).parent;
                }
                path.appendVertexInFront(parent);
                return path;
            }

            //analyses successors of the candidate
            else {
                for (V succ : graph.getSuccessors(candidate.getData())) {
                    //successor in the initial set
                    if (!map.containsKey(succ)) {
                        double edge = graph.getEdgeWeight(candidate.getData(), succ);
                        double dist = distance + edge;
//                        System.out.println(distance.getClass()+"...."+distance.isUsingDynamicSearch()+" und "+edge.isUsingDynamicSearch()+" = "+dist.isUsingDynamicSearch());
//                        verticesToWeightsMap.put(succ, dist);
                        Item succNode = new Item(succ, candidate.getData(), dist);
                        fibonacciCandidates.insert(succNode, dist);
                        map.put(succ, succNode);
                    }
                    else {
                        double edge = graph.getEdgeWeight(candidate.getData(), succ);
                        double dist = distance + edge;

                        Item succItem = map.get(succ);
                        if(dist < succItem.weight) {
//                            verticesToWeightsMap.put(succ, dist);
                            succItem.parent = candidate.getData();
                            succItem.weight = dist;
                            fibonacciCandidates.decreaseKey(succItem, dist);
                        }
                    }
                }
            }
        }
        return null;
    }




    private class Item extends FibonacciHeapNode<V>{
        V parent; 
        double weight;

        Item(V vertex, V parent, double weight) {
            super(vertex);
            this.parent = parent;
            this.weight = weight;
        }
    }

    /**
     * 
     */
    private class DijkstraComparator implements Comparator<V> {
        IdentityHashMap<V, Item> weightPerVertex;

        DijkstraComparator(IdentityHashMap<V, Item> itemPerVertex) {
            this.weightPerVertex = itemPerVertex;
        }

        @Override
        public int compare(V o1, V o2) {
            return Double.compare(weightPerVertex.get(o1).weight, weightPerVertex.get(o2).weight);
        }
    }

    @Override
    public String toString() {
        return "Dijkstra-Algorithmus";
    }
}