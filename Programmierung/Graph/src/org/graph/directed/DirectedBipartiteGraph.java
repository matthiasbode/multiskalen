package org.graph.directed;

import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.graph.KPartiteGraph;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class DirectedBipartiteGraph<V> implements DirectedGraph<V>, KPartiteGraph<V>{

    protected final DirectedGraph<V> graph;
    protected final Predicate<V> v1_predicate;
    protected final Predicate<V> v2_predicate;

    public DirectedBipartiteGraph(DirectedGraph<V> graph,
                                    Predicate<V> v1_predicate,
                                    Predicate<V> v2_predicate) {
        this.graph = graph;
        this.v1_predicate = v1_predicate;
        this.v2_predicate = v2_predicate;
    }
    
    

    public DirectedBipartiteGraph(Predicate<V> v1_predicate, Predicate<V> v2_predicate) {
        this.graph = new DefaultDirectedGraph<V>();
        this.v1_predicate = v1_predicate;
        this.v2_predicate = v2_predicate;
    }
    
    
    @Override
    public boolean addEdge(V first, V second) {
        if(v1_predicate.apply(first) && v1_predicate.apply(second))
            return false;
        if(v2_predicate.apply(first) && v2_predicate.apply(second))
            return false;
        return graph.addEdge(first, second);
    }
    
    

    @Override
    public Collection<V> getVertices(Predicate<V> predicate) {
        Collection<V> partitionVertices = new HashSet<V>();
        for (V v : graph.vertexSet()) {
            if(predicate.apply(v))
                partitionVertices.add(v);
        }
        return partitionVertices;
    }
    

    @Override
    public Collection<Predicate<V>> getPartitions() {
        Collection<Predicate<V>> partitions = new HashSet<Predicate<V>>();
        partitions.add(v1_predicate);
        partitions.add(v2_predicate);
        return partitions;
    }
    
    public Collection<Pair<V,V>> getEdges(Predicate<V> from_predicate, Predicate<V> to_predicate) {
        Collection<Pair<V,V>> belongingEdges = new HashSet<Pair<V,V>>();
        for (Pair<V, V> edge : graph.edgeSet()) {
            if(from_predicate.apply(edge.getFirst()) && to_predicate.apply(edge.getSecond()))
                belongingEdges.add(edge);
        }
        return belongingEdges;
    }
    
    
    @Override
    public int inDegreeOf(V vertex) {
        return graph.inDegreeOf(vertex);
    }

    @Override
    public int outDegreeOf(V vertex) {
        return graph.outDegreeOf(vertex);
    }

    @Override
    public Set<Pair<V, V>> incomingEdgesOf(V vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    public Set<Pair<V, V>> outgoingEdgesOf(V vertex) {
        return graph.outgoingEdgesOf(vertex);
    }

    @Override
    public Set<V> getPredecessors(V vertex) {
        return graph.getPredecessors(vertex);
    }

    @Override
    public Set<V> getSuccessors(V vertex) {
        return graph.getSuccessors(vertex);
    }

    @Override
    public Set<Pair<V, V>> getAllEdges(V sourceVertex, V targetVertex) {
        return graph.getAllEdges(sourceVertex, targetVertex);
    }

    @Override
    public boolean addVertex(V v) {
        return graph.addVertex(v);
    }

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        return graph.containsEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean containsEdge(Pair<V, V> e) {
        return graph.containsEdge(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return graph.containsVertex(v);
    }

    @Override
    public Set<Pair<V, V>> edgeSet() {
        return graph.edgeSet();
    }

    @Override
    public Set<Pair<V, V>> edgesOf(V vertex) {
        return graph.edgesOf(vertex);
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Pair<V, V>> edges) {
        return graph.removeAllEdges(edges);
    }

    @Override
    public Set<Pair<V, V>> removeAllEdges(V source, V target) {
        return graph.removeAllEdges(source, target);
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        return graph.removeAllVertices(vertices);
    }

    @Override
    public boolean removeEdge(V source, V target) {
        return graph.removeEdge(source, target);
    }

    @Override
    public boolean removeEdge(Pair<V, V> e) {
        return graph.removeEdge(e);
    }

    @Override
    public boolean removeVertex(V v) {
        return graph.removeVertex(v);
    }

    @Override
    public Set<V> vertexSet() {
        return graph.vertexSet();
    }

    @Override
    public boolean addEdge(Pair<V, V> edge) {
        if(v1_predicate.apply(edge.getFirst()) && v1_predicate.apply(edge.getSecond()))
            return false;
        if(v2_predicate.apply(edge.getFirst()) && v2_predicate.apply(edge.getSecond()))
            return false;
        return graph.addEdge(edge);
    }
    
    
    /** Returns a string representation of this directed graph.
     *
     *  The string representation consists of a list of the graph vertices and
     *  edges without an order enclosed in braces ("{}"). The vertices and edges
     *  are separated by the characters ", " (comma and space).
     *  Vertices are converted to strings as by String.valueOf(Object).
     *
     *  @return The method returns a string representation of this directed
     *          graph.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("(V1={");
        Iterator<V> v_it = getVertices(v1_predicate).iterator();

        if (v_it.hasNext()) {
            buf.append(v_it.next());
        }
        for (; v_it.hasNext();) {
            buf.append(", ").append(v_it.next());
        }
        buf.append("}, V2={");
        v_it = getVertices(v2_predicate).iterator();

        if (v_it.hasNext()) {
            buf.append(v_it.next());
        }
        for (; v_it.hasNext();) {
            buf.append(", ").append(v_it.next());
        }
        buf.append("}; E1={");
        Iterator<Pair<V,V>> e_it = getEdges(v1_predicate, v2_predicate).iterator();
        if (e_it.hasNext()) {
            buf.append(e_it.next());
        }
         for (; e_it.hasNext();) {
            buf.append(", ").append(e_it.next());
        }
        buf.append("}, E2={");
        e_it = getEdges(v2_predicate, v1_predicate).iterator();
        if (e_it.hasNext()) {
            buf.append(e_it.next());
        }
         for (; e_it.hasNext();) {
            buf.append(", ").append(e_it.next());
        }
        buf.append("})");
        return buf.toString();
    }

    @Override
    public int numberOfVertices() {
        return graph.vertexSet().size();
    }

    @Override
    public int numberOfEdges() {
        return graph.edgeSet().size();
    }

    @Override
    public void changeVertex(V v_old, V v_new) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}