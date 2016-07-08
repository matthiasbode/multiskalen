/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import java.util.HashMap;
import java.util.Iterator;
import org.graph.undirected.SimpleGraph;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class DefaultWeightedSimpleGraph<V,W> extends SimpleGraph<V>
                                 implements WeightedSimpleGraph<V, W> {

    private final HashMap<V, W>           vertexWeights;
    private final HashMap<Pair<V,V>, W>   edgeWeights;
    
    public DefaultWeightedSimpleGraph() {
        super();
        this.vertexWeights = new HashMap<V, W>();
        this.edgeWeights = new HashMap<Pair<V, V>, W>();
    }

    

    public void setVertexWeight(V vertex, W weight) {
        vertexWeights.put(vertex, weight);
    }


    public W getVertexWeight(V vertex) {
        return vertexWeights.get(vertex);
    }

    @Override
    public void setEdgeWeight(Pair<V,V> e, W weight) {
        if(edges.contains(e))
            edgeWeights.put(e, weight);
        else if(edges.contains(e.transposition()))
            edgeWeights.put(e.transposition(), weight);
    }

    @Override
    public W getEdgeWeight(Pair<V,V> e) {
        if(edges.contains(e))
            return edgeWeights.get(e);
        else if(edges.contains(e.transposition()))
            return edgeWeights.get(e.transposition());
        return null;
    }
    
    @Override
    public W getEdgeWeight(V sourceVertex, V targetVertex) {
        return getEdgeWeight(new Pair<V,V>(sourceVertex, targetVertex));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("WeightedSimpleGraph: ({");
        Iterator<V> e = vertices.iterator();
        Iterator<Pair<V,V>> i = edges.iterator();

        if (e.hasNext()) {
            buf.append(e.next());
        }
        for (; e.hasNext();) {
            buf.append(", " + e.next());
        }
        buf.append("}; {");
        if (i.hasNext()) {
            Pair edge = i.next();
            buf.append(edge + " --> " + getEdgeWeight(edge));
        }
        for (; i.hasNext();) {
            Pair edge = i.next();
            buf.append(", " + edge + " --> " + getEdgeWeight(edge));
        }
        buf.append("})");
        return buf.toString();
    } 
}
