/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import org.graph.algorithms.pathsearch.PairShortestPathAlgorithm;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.util.FibonacciHeap;
import org.util.FibonacciHeapNode;

/**
 *
 * @author rinke
 */
public class DijkstraForAdjacencyArray<V,W extends EdgeWeight<W>> implements PairShortestPathAlgorithm<V,W> {
    private final FibonacciHeap<Integer> fibonacciCandidates;
    private final HashMap<Integer,Item> map;

    

    public DijkstraForAdjacencyArray() {

        fibonacciCandidates = new FibonacciHeap<Integer>();
        map = new HashMap<Integer, Item>();
    }
    
    

    @Override
    public WeightedPath<V, W> singlePairShortestPath(WeightedDirectedGraph<V, W> graph, V source, V destination) {
        if(graph instanceof WeightedDirectedGraphAsAdjacencyArray) {
            WeightedDirectedGraphAsAdjacencyArray<V,W> graphAAA = (WeightedDirectedGraphAsAdjacencyArray<V,W>) graph;
            W einsWeight = graphAAA.weightsPerEdge.get(0).getEinsElement();
            fibonacciCandidates.clear();
            map.clear();
        
            HashBiMap<V, Integer> vertexMap = graphAAA.getVertexMap();
            BiMap<Integer, V> inverse = vertexMap.inverse();
            int sourceIndex = vertexMap.get(source);
            int destIndex = vertexMap.get(destination);
            Item sourceItem = new Item(sourceIndex, sourceIndex, einsWeight);

            fibonacciCandidates.insert(sourceItem,0);
            map.put(sourceIndex, sourceItem);
            //while a candidate is in the candidate set
            while (!fibonacciCandidates.isEmpty()) {
                Item candidate = (Item) fibonacciCandidates.removeMin();
                W distance  = candidate.weight;

                //returns shortest path if the end is reached
                if (candidate.getData()==destIndex) {
                    WeightedPath<V,W> path =
                                      new WeightedPath<V, W>(distance, destination);
                    int parent = map.get(candidate.getData()).parent;
                    while(parent != sourceIndex) {
                        path.appendVertexInFront(inverse.get(parent));
                        parent = map.get(parent).parent;
                    }
                    path.appendVertexInFront(inverse.get(parent));
                    return path;
                }

                //analyses successors of the candidate
                else {
                    for (int succ : graphAAA.getSuccessors(candidate.getData())) {
                        //successor in the initial set
                        if (!map.containsKey(succ)) {
                            W edge = graphAAA.getEdgeWeight(candidate.getData(), succ);
                            W dist = distance.product(edge);
                            Item succNode = new Item(succ, candidate.getData(), dist);
                            fibonacciCandidates.insert(succNode, dist.doubleValue());
                            map.put(succ, succNode);
                        }
                        else {
                            W edge = graphAAA.getEdgeWeight(candidate.getData(), succ);
                            W dist = distance.product(edge);

                            Item succItem = map.get(succ);
                            if(dist.compareTo(succItem.weight) < 0) {
                                succItem.parent = candidate.getData();
                                succItem.weight = dist;
                                fibonacciCandidates.decreaseKey(succItem, dist.doubleValue());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    
    
    private class Item extends FibonacciHeapNode<Integer>{
        int parent; 
        W weight;

        Item(int vertex, int parent, W weight) {
            super(vertex);
            this.parent = parent;
            this.weight = weight;
        }
    }
}
