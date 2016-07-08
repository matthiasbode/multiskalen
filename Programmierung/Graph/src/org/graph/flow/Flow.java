/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.flow;

import java.util.HashMap;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class Flow<V> extends HashMap<Pair<V, V>, Double> {

    private FlowNetwork<V> flowNetwork;

    public Flow(FlowNetwork<V> flowNetwork) {
        this.flowNetwork = flowNetwork;
        for (Pair<V, V> edge : flowNetwork.edgeSet()) {
            this.put(edge, 0.);
        }
    }

    public boolean istZulaessig() {
        for (V vertex : flowNetwork.vertexSet()) {
            if(vertex.equals(flowNetwork.getSource()) || vertex.equals(flowNetwork.getSink())){
                continue;
            }
            double rein = 0;
            double raus = 0;
            for (V pre : flowNetwork.getPredecessors(vertex)) {
                Pair<V, V> prePair = new Pair<V, V>(pre, vertex);
                if (this.get(prePair) > flowNetwork.getEdgeWeight(prePair).capacity) {
                    return false;
                }
                rein += this.get(prePair);
            }
            for (V suc : flowNetwork.getSuccessors(vertex)) {
                Pair<V, V> sucPair = new Pair<V, V>(vertex, suc);
                if (this.get(sucPair) > flowNetwork.getEdgeWeight(sucPair).capacity) {
                    return false;
                }
                raus += this.get(sucPair);
            }

            if (rein != raus) {
                return false;
            }
        }
        return true;
    }
}