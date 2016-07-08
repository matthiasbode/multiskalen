/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.undirected;

import java.util.Collection;
import java.util.Set;
import org.util.DiscreteSet;
import org.util.Pair;
import org.util.Relation;

/**
 *
 * @author rinke
 */
public class HyperGraph<V,E> implements UnDirected<V> {
    
    private final DiscreteSet<V> vertices;
    private final DiscreteSet<E> edges;
    private final Relation<E, V> relation;

    public HyperGraph() {
        vertices = new DiscreteSet<V>();
        edges = new DiscreteSet<E>();
        relation = new Relation<E, V>();
    }
    
    

    @Override
    public int degreeOf(V vertex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<V> getNeighbours(V vertex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Pair<V, V>> neighbourEdgesOf(V vertex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Pair<V, V>> getAllEdges(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addEdge(Pair<V, V> edge) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addVertex(V v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsEdge(Pair<V, V> e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsVertex(V v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Pair<V, V>> edgeSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Pair<V, V>> edgesOf(V vertex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Pair<V, V>> edges) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Pair<V, V>> removeAllEdges(V source, V target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeEdge(V source, V target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeEdge(Pair<V, V> e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeVertex(V v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<V> vertexSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public int numberOfVertices() {
        return vertices.size();
    }

    @Override
    public int numberOfEdges() {
        return edges.size();
    }
}
