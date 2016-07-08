/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.flow;

import org.graph.weighted.DefaultWeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class FlowNetwork<V> extends DefaultWeightedDirectedGraph<V, FlowWeight> {
    private V source;
    private V sink;


    public FlowNetwork(V source, V sink) {
        super();
        this.source = source;
        this.sink = sink;
        this.addVertex(source);
        this.addVertex(sink);
    }
    

    public V getSink() {
        return sink;
    }

    
    public V getSource() {
        return source;
    }

    
    @Override
    public boolean addEdge(V first, V second, FlowWeight bewertung) {
        if(first.equals(sink) || second.equals(source)){
            return false;
        }
        return super.addEdge(first, second, bewertung);
    }

    
    @Override
    public boolean addEdge(V first, V second) {
        if(first.equals(sink) || second.equals(source)){
            return false;
        }
        return super.addEdge(first, second);
    }
}