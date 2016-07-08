/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import java.util.HashMap;
import org.graph.directed.Multigraph;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class WeightedMultigraph<V, B> extends Multigraph<V> implements Weighted<V, B> {

    HashMap<Pair<V, V>, B> weights = new HashMap<>();

    public WeightedMultigraph() {
        super();
    }

    public WeightedMultigraph(Multigraph<V> graph) {
        super(graph);
    }

    @Override
    public void setEdgeWeight(Pair<V, V> e, B weight) {
        this.weights.put(e, weight);
    }

    @Override
    public B getEdgeWeight(Pair<V, V> e) {
        return this.weights.get(e);
    }

    @Override
    public B getEdgeWeight(V startVertex, V targetVertex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
