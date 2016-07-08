package org.graph.directed;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import org.util.Pair;

/**
 *
 * @author Nils Rinke
 * @param <V>
 */
public class Multigraph<V> implements DirectedGraph<V> {

    protected HashSet<V> vertices;
    protected HashSet<Pair<V, V>> edges;

    public Multigraph() {
        this.vertices = new HashSet<V>();
        this.edges = new HashSet();
    }

    public Multigraph(Multigraph<V> graph) {
        this.vertices = new HashSet<>(graph.vertices);
        this.edges = new HashSet<>(graph.edges);
    }

    @Override
    public Set<Pair<V, V>> getAllEdges(V sourceVertex, V targetVertex) {
        Set<Pair<V, V>> res = new HashSet<Pair<V, V>>();
        for (Pair<V, V> edge : edges) {
            if (edge.getFirst().equals(sourceVertex)
                    && edge.getSecond().equals(targetVertex)) {
                res.add(edge);
            }
        }
        return res;
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex) {
        throw new UnsupportedOperationException("Only addEdge(Pair<V,V>) - Method is allowed");
    }

    @Override
    public boolean addEdge(Pair<V, V> edge) {
        return edges.add(edge);
    }

    @Override
    public boolean addVertex(V v) {
        return vertices.add(v);
    }

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
//        return edges.contains(new Pair<V, V>(sourceVertex, targetVertex));
        throw new UnsupportedOperationException("Only containsEdge(Pair<V,V>) - Method is allowed");
    }

    @Override
    public boolean containsEdge(Pair<V, V> e) {
        return edges.contains(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return vertices.contains(v);
    }

    @Override
    public Set<Pair<V, V>> edgeSet() {
        return edges;
    }

    @Override
    public Set<Pair<V, V>> edgesOf(V vertex) {
        Set<Pair<V, V>> res = new HashSet<Pair<V, V>>();
        for (Pair<V, V> edge : edges) {
            if (edge.getFirst().equals(vertex)
                    || edge.getSecond().equals(vertex)) {
                res.add(edge);
            }
        }
        return res;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Pair<V, V>> edges) {
        return edges.removeAll(edges);
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
        return this.edges.remove(e);
    }

    @Override
    public boolean removeVertex(V v) {
        Set<Pair<V, V>> edgesToDelete = incomingEdgesOf(v);
        edgesToDelete.addAll(outgoingEdgesOf(v));
        edges.removeAll(edgesToDelete);
        return this.vertices.remove(v);
    }

    @Override
    public Set<V> vertexSet() {
        return vertices;
    }

    @Override
    public int numberOfVertices() {
        return vertices.size();
    }

    @Override
    public int numberOfEdges() {
        return edges.size();
    }

    @Override
    public int inDegreeOf(V vertex) {
        return this.incomingEdgesOf(vertex).size();
    }

    @Override
    public int outDegreeOf(V vertex) {
        return this.outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Pair<V, V>> incomingEdgesOf(V vertex) {
        Set<Pair<V, V>> res = new HashSet<Pair<V, V>>();
        for (Pair<V, V> edge : edges) {
            if (edge.getSecond().equals(vertex)) {
                res.add(edge);
            }
        }
        return res;
    }

    @Override
    public Set<Pair<V, V>> outgoingEdgesOf(V vertex) {
        Set<Pair<V, V>> res = new HashSet<Pair<V, V>>();
        for (Pair<V, V> edge : edges) {
            if (edge.getFirst().equals(vertex)) {
                res.add(edge);
            }
        }
        return res;
    }

    @Override
    public Set<V> getPredecessors(V vertex) {
        Set<Pair<V, V>> incomingEdgesOf = incomingEdgesOf(vertex);
        Set<V> result = new HashSet<>();
        for (Pair<V, V> v : incomingEdgesOf) {
            result.add(v.getFirst());
        }
        return result;
    }

    @Override
    public Set<V> getSuccessors(V vertex) {
        Set<Pair<V, V>> incomingEdgesOf = outgoingEdgesOf(vertex);
        Set<V> result = new HashSet<>();
        for (Pair<V, V> v : incomingEdgesOf) {
            result.add(v.getSecond());
        }
        return result;
    }

    @Override
    public void changeVertex(V v_old, V v_new) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
