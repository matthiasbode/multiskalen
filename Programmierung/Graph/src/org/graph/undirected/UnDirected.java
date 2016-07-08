/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.undirected;

import java.util.Set;
import org.graph.Graph;
import org.util.Pair;

/**
 *
 * @author Nils Rinke
 */
public interface UnDirected<V> extends Graph<V> {

    /**
     * Returns the degree of the specified vertex. The degree of a vertex
     * in a undirected graph is the number of its neighbours.
     * 
     * @param vertex vertex whose degree is to be calculated.
     *
     * @return the degree of the specified vertex.
     */
    public int degreeOf(V vertex);
    
    
    /**
     * Returns a set of all neighbours of the specified vertex.
     *
     * @param vertex the vertex for which the set of neighbours to be
     * returned.
     *
     * @return a set of all neighbours of the specified vertex.
     */
    public Set<V> getNeighbours(V vertex);
    
    
    /**
     * Returns a set of all edges touching the specified vertex.
     *
     * @param vertex the vertex for which the list of touching edges to be
     * returned.
     *
     * @return a set of all edges touching from the specified vertex.
     */
    public Set<Pair<V,V>> neighbourEdgesOf(V vertex);
}
