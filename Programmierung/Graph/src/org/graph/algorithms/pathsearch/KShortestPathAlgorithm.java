/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.algorithms.pathsearch;

import org.graph.weighted.EdgeWeight;
import java.util.ArrayList;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.doubleWeighted.DoubleWeightedPath;
import org.graph.weighted.WeightedPath;

/**
 *
 * @author nilsrinke
 * @param <V>
 * @param <W>
 */
public interface KShortestPathAlgorithm<V,W extends EdgeWeight<W>> {
        
    /**
     * Calculates the <code>K</code>-shortest path in a given network.
     *
     * @param graph underlying network
     * @param source source vertex in the network
     * @param destination destination vertex in the network
     * @return a list which contains the 0...k-shortest paths
     */
    public ArrayList<WeightedPath<V,W>> kShortestPaths(
               WeightedDirectedGraph<V,W> graph, V source, V destination,int K);
}
