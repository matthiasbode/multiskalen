/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.directed;

import java.util.Set;
import org.graph.Graph;
import org.util.Pair;

/**
 *
 * @author Nils Rinke
 */
public interface DirectedGraph<V> extends Graph<V>
{

    /**
     * Returns the "in degree" of the specified vertex. An in degree of a vertex
     * in a directed graph is the number of inward directed edges from that
     * vertex.
     * 
     * @param vertex vertex whose degree is to be calculated.
     *
     * @return the degree of the specified vertex.
     */
    public int inDegreeOf(V vertex);
    
    
    /**
     * Returns the "out degree" of the specified vertex. An out degree of a
     * vertex in a directed graph is the number of outward directed edges from
     * that vertex.
     * 
     * @param vertex vertex whose degree is to be calculated.
     *
     * @return the degree of the specified vertex.
     */
    public int outDegreeOf(V vertex);
    

    /**
     * Returns a set of all edges incoming into the specified vertex.
     *
     * @param vertex the vertex for which the list of incoming edges to be
     * returned.
     *
     * @return a set of all edges incoming into the specified vertex.
     */
    public Set<Pair<V,V>> incomingEdgesOf(V vertex);

    

    /**
     * Returns a set of all edges outgoing from the specified vertex.
     *
     * @param vertex the vertex for which the list of outgoing edges to be
     * returned.
     *
     * @return a set of all edges outgoing from the specified vertex.
     */
    public Set<Pair<V,V>> outgoingEdgesOf(V vertex);
    
    
    /**
     * Returns a set of all predecessors of the specified vertex.
     *
     * @param vertex the vertex for which the set of predecessors to be
     * returned.
     *
     * @return a set of all predecessors of the specified vertex.
     */
    public Set<V> getPredecessors(V vertex);


    /**
     * Returns a set of all successors of the specified vertex.
     *
     * @param vertex the vertex for which the set of successors to be
     * returned.
     *
     * @return a set of all successors of the specified vertex.
     */
    public Set<V> getSuccessors(V vertex);

    public void changeVertex(V v_old, V v_new);
}
