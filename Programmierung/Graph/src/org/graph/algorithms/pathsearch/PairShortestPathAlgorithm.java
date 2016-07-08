package org.graph.algorithms.pathsearch;

import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.doubleWeighted.DoubleWeightedPath;
import org.graph.weighted.WeightedPath;

/**
 *
 * @author Nils Rinke
 */
public interface PairShortestPathAlgorithm<V,W extends EdgeWeight<W>> {

   /** Calculates a shortest path between two vertices in a weighted
     * directed graph.
     *
     *
     *  @param graph        weighted directed graph
     *  @param source       source vertex of the shortest path
     *  @param destination  destination vertex of the shortest path
     *  @return	The method returns the shortest weighted path.
     */
    public WeightedPath<V,W> singlePairShortestPath(
                    WeightedDirectedGraph<V,W> graph, V source, V destination);
                                                      
}
