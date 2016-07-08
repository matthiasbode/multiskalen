/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import org.util.Pair;

/**
 *
 * @author rinke
 */
public interface Weighted<V, W> {
    
    public void setEdgeWeight(Pair<V,V> e, W weight);
    
    public W getEdgeWeight(Pair<V,V> e);
    
    public W getEdgeWeight(V startVertex, V targetVertex);
}