/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.algorithms.pathsearch;

import java.util.Comparator;
import java.util.HashMap;
import org.graph.weighted.EdgeWeight;

/**
 *
 * @author nilsrinke
 */
public class VertexComparator<V, W extends EdgeWeight<W>> implements Comparator<V> {

    private HashMap<V,W> weightPerVertex;


    public VertexComparator(HashMap<V, W> weightPerVertex) {
        this.weightPerVertex = weightPerVertex;
    }

    @Override
    public int compare(V t, V t1) {
        return weightPerVertex.get(t).compareTo(weightPerVertex.get(t1));
    }
}