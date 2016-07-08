/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import com.google.common.base.Predicate;
import java.util.HashMap;
import org.graph.directed.DirectedBipartiteGraph;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class WeightedDirectedBipartiteGraph<V,W> 
            extends DirectedBipartiteGraph<V>
            implements Weighted<V, W> {

    private final HashMap<Pair<V,V>, W>   edgeWeights;
    
    public WeightedDirectedBipartiteGraph(Predicate<V> v1_predicate,
                                          Predicate<V> v2_predicate) {
        super(v1_predicate, v2_predicate);
        edgeWeights = new HashMap<Pair<V, V>, W>();
    }
    

    @Override
    public void setEdgeWeight(Pair<V,V> e, W weight) {
        edgeWeights.put(e, weight);
    }

    
    @Override
    public W getEdgeWeight(Pair<V,V> e) {
        return edgeWeights.get(e);
    }

    
    @Override
    public W getEdgeWeight(V startVertex, V targetVertex) {
        return edgeWeights.get(new Pair<V, V>(startVertex, targetVertex));
    }  
}