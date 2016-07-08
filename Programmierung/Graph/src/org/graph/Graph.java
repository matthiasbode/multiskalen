/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph;

import java.util.Collection;
import java.util.Set;
import org.util.Pair;


/**
 *
 * @author Nils Rinke
 */
public interface Graph<V> {
    
    /**
     * Returns a set of all edges connecting source vertex to target vertex if
     * such vertices exist in this graph. If any of the vertices does not exist
     * or is <code>null</code>, returns <code>null</code>. If both vertices
     * exist but no edges found, returns an empty set.
     *
     * <p>In undirected graphs, some of the returned edges may have their source
     * and target vertices in the opposite order. In simple graphs the returned
     * set is either singleton set or empty set.</p>
     *
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return a set of all edges connecting source vertex to target vertex.
     */
    public Set<Pair<V,V>> getAllEdges(V sourceVertex, V targetVertex);


    /**
     * Creates a new edge in this graph, going from the source vertex to the
     * target vertex, and returns the created edge. Some graphs do not allow
     * edge-multiplicity. In such cases, if the graph already contains an edge
     * from the specified source to the specified target, than this method does
     * not change the graph and returns <code>null</code>.
     *
     * <p>The source and target vertices must already be contained in this
     * graph. If they are not found in graph IllegalArgumentException is
     * thrown.</p>
     *
     * <p>This method creates the new edge <code>e</code> using this graph's
     * <code>EdgeFactory</code>. For the new edge to be added <code>e</code>
     * must <i>not</i> be equal to any other edge the graph (even if the graph
     * allows edge-multiplicity). More formally, the graph must not contain any
     * edge <code>e2</code> such that <code>e2.equals(e)</code>. If such <code>
     * e2</code> is found then the newly created edge <code>e</code> is
     * abandoned, the method leaves this graph unchanged returns <code>
     * null</code>.</p>
     *
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return <code>true</code> if added to the graph, otherwise 
     *
     * @throws IllegalArgumentException if source or target vertices are not
     * found in the graph.
     * @throws NullPointerException if any of the specified vertices is <code>
     * null</code>.
     *
     */
    public boolean addEdge(V sourceVertex, V targetVertex);

    
    /**
     * Adds the specified edge to this graph, going from the source vertex to
     * the target vertex. More formally, adds the specified edge, <code>
     * e</code>, to this graph if this graph contains no edge <code>e2</code>
     * such that <code>e2.equals(e)</code>. If this graph already contains such
     * an edge, the call leaves this graph unchanged and returns <tt>false</tt>.
     * Some graphs do not allow edge-multiplicity. In such cases, if the graph
     * already contains an edge from the specified source to the specified
     * target, than this method does not change the graph and returns <code>
     * false</code>. If the edge was added to the graph, returns <code>
     * true</code>.
     *
     * <p>The source and target vertices must already be contained in this
     * graph. If they are not found in graph IllegalArgumentException is
     * thrown.</p>
     *
     * @param edge edge to be added to this graph.
     *
     * @return <tt>true</tt> if this graph did not already contain the specified
     * edge.
     *
     * @throws IllegalArgumentException if source or target vertices are not
     * found in the graph.
     * @throws ClassCastException if the specified edge is not assignment
     * compatible with the class of edges produced by the edge factory of this
     * graph.
     * @throws NullPointerException if any of the specified vertices is <code>
     * null</code>.
     *
     */
    public boolean addEdge(Pair<V,V> edge);

    /**
     * Adds the specified vertex to this graph if not already present. More
     * formally, adds the specified vertex, <code>v</code>, to this graph if
     * this graph contains no vertex <code>u</code> such that <code>
     * u.equals(v)</code>. If this graph already contains such vertex, the call
     * leaves this graph unchanged and returns <tt>false</tt>. In combination
     * with the restriction on constructors, this ensures that graphs never
     * contain duplicate vertices.
     *
     * @param v vertex to be added to this graph.
     *
     * @return <tt>true</tt> if this graph did not already contain the specified
     * vertex.
     *
     * @throws NullPointerException if the specified vertex is <code>
     * null</code>.
     */
    public boolean addVertex(V v);

    /**
     * Returns <tt>true</tt> if and only if this graph contains an edge going
     * from the source vertex to the target vertex. In undirected graphs the
     * same result is obtained when source and target are inverted. If any of
     * the specified vertices does not exist in the graph, or if is <code>
     * null</code>, returns <code>false</code>.
     *
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return <tt>true</tt> if this graph contains the specified edge.
     */
    public boolean containsEdge(V sourceVertex, V targetVertex);

    /**
     * Returns <tt>true</tt> if this graph contains the specified edge. More
     * formally, returns <tt>true</tt> if and only if this graph contains an
     * edge <code>e2</code> such that <code>e.equals(e2)</code>. If the
     * specified edge is <code>null</code> returns <code>false</code>.
     *
     * @param e edge whose presence in this graph is to be tested.
     *
     * @return <tt>true</tt> if this graph contains the specified edge.
     */
    public boolean containsEdge(Pair<V,V> e);

    /**
     * Returns <tt>true</tt> if this graph contains the specified vertex. More
     * formally, returns <tt>true</tt> if and only if this graph contains a
     * vertex <code>u</code> such that <code>u.equals(v)</code>. If the
     * specified vertex is <code>null</code> returns <code>false</code>.
     *
     * @param v vertex whose presence in this graph is to be tested.
     *
     * @return <tt>true</tt> if this graph contains the specified vertex.
     */
    public boolean containsVertex(V v);

    /**
     * Returns a set of the edges contained in this graph. The set is backed by
     * the graph, so changes to the graph are reflected in the set. If the graph
     * is modified while an iteration over the set is in progress, the results
     * of the iteration are undefined.
     *
     * <p>The graph implementation may maintain a particular set ordering (e.g.
     * via {@link java.util.LinkedHashSet}) for deterministic iteration, but
     * this is not required. It is the responsibility of callers who rely on
     * this behavior to only use graph implementations which support it.</p>
     *
     * @return a set of the edges contained in this graph.
     */
    public Set<Pair<V,V>> edgeSet();

    /**
     * Returns a set of all edges touching the specified vertex. If no edges are
     * touching the specified vertex returns an empty set.
     *
     * @param vertex the vertex for which a set of touching edges is to be
     * returned.
     *
     * @return a set of all edges touching the specified vertex.
     *
     * @throws IllegalArgumentException if vertex is not found in the graph.
     * @throws NullPointerException if vertex is <code>null</code>.
     */
    public Set<Pair<V,V>> edgesOf(V vertex);

    /**
     * Removes all the edges in this graph that are also contained in the
     * specified edge collection. After this call returns, this graph will
     * contain no edges in common with the specified edges. This method will
     * invoke the {@link #removeEdge(Object)} method.
     *
     * @param edges edges to be removed from this graph.
     *
     * @return <tt>true</tt> if this graph changed as a result of the call
     *
     * @throws NullPointerException if the specified edge collection is <tt>
     * null</tt>.
     *
     * @see #removeEdge(Object)
     * @see #containsEdge(Object)
     */
    public boolean removeAllEdges(Collection<? extends Pair<V,V>> edges);
    

    /**
     * Removes all the edges going from the specified source vertex to the
     * specified target vertex, and returns a set of all removed edges. Returns
     * <code>null</code> if any of the specified vertices does not exist in the
     * graph. If both vertices exist but no edge is found, returns an empty set.
     * This method will either invoke the {@link #removeEdge(Object)} method, or
     * the {@link #removeEdge(Object, Object)} method.
     *
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return the removed edges, or <code>null</code> if no either vertex not
     * part of graph
     */
    public Set<Pair<V,V>> removeAllEdges(V source, V target);

    /**
     * Removes all the vertices in this graph that are also contained in the
     * specified vertex collection. After this call returns, this graph will
     * contain no vertices in common with the specified vertices. This method
     * will invoke the {@link #removeVertex(Object)} method.
     *
     * @param vertices vertices to be removed from this graph.
     *
     * @return <tt>true</tt> if this graph changed as a result of the call
     *
     * @throws NullPointerException if the specified vertex collection is <tt>
     * null</tt>.
     *
     * @see #removeVertex(Object)
     * @see #containsVertex(Object)
     */
    public boolean removeAllVertices(Collection<? extends V> vertices);

    /**
     * Removes an edge going from source vertex to target vertex, if such
     * vertices and such edge exist in this graph. Returns the edge if removed
     * or <code>null</code> otherwise.
     *
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return <code>true</code> if and only if the graph contained the
     * specified edge.
     */
    public boolean removeEdge(V source, V target);

    /**
     * Removes the specified edge from the graph. Removes the specified edge
     * from this graph if it is present. More formally, removes an edge <code>
     * e2</code> such that <code>e2.equals(e)</code>, if the graph contains such
     * edge. Returns <tt>true</tt> if the graph contained the specified edge.
     * (The graph will not contain the specified edge once the call returns).
     *
     * <p>If the specified edge is <code>null</code> returns <code>
     * false</code>.</p>
     *
     * @param e edge to be removed from this graph, if present.
     *
     * @return <code>true</code> if and only if the graph contained the
     * specified edge.
     */
    public boolean removeEdge(Pair<V,V> e);

    /**
     * Removes the specified vertex from this graph including all its touching
     * edges if present. More formally, if the graph contains a vertex <code>
     * u</code> such that <code>u.equals(v)</code>, the call removes all edges
     * that touch <code>u</code> and then removes <code>u</code> itself. If no
     * such <code>u</code> is found, the call leaves the graph unchanged.
     * Returns <tt>true</tt> if the graph contained the specified vertex. (The
     * graph will not contain the specified vertex once the call returns).
     *
     * <p>If the specified vertex is <code>null</code> returns <code>
     * false</code>.</p>
     *
     * @param v vertex to be removed from this graph, if present.
     *
     * @return <code>true</code> if the graph contained the specified vertex;
     * <code>false</code> otherwise.
     */
    public boolean removeVertex(V v);

    /**
     * Returns a set of the vertices contained in this graph. The set is backed
     * by the graph, so changes to the graph are reflected in the set. If the
     * graph is modified while an iteration over the set is in progress, the
     * results of the iteration are undefined.
     *
     * <p>The graph implementation may maintain a particular set ordering (e.g.
     * via {@link java.util.LinkedHashSet}) for deterministic iteration, but
     * this is not required. It is the responsibility of callers who rely on
     * this behavior to only use graph implementations which support it.</p>
     *
     * @return a set view of the vertices contained in this graph.
     */
    public Set<V> vertexSet();
    
    
    public int numberOfVertices();
    
    public int numberOfEdges();
}
