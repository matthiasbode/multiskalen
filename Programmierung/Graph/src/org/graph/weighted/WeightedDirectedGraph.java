/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import org.graph.directed.DirectedGraph;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public interface WeightedDirectedGraph<V, W> extends DirectedGraph<V>, Weighted<V, W> {
    
    public WeightedDirectedGraph<V,W> dual();
    
    public boolean addEdge(V sourceVertex, V targetVertex, W weight);
    
    public boolean addEdge(Pair<V,V> edge, W weight);
    
}
