/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.util;

import java.util.Comparator;
import org.graph.weighted.Weighted;

/**
 *
 * @author rinke
 */
public class EdgeComparator<V> implements Comparator<Pair<V,V>> {
    Weighted<V, ? extends Comparable> graph;

    public EdgeComparator(Weighted<V, ? extends Comparable> graph) {
        this.graph = graph;
    }

    @Override
    public int compare(Pair t, Pair t1) {
        return graph.getEdgeWeight(t).compareTo(graph.getEdgeWeight(t1));
    }
}
