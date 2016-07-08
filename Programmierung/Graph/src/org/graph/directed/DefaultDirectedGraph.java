package org.graph.directed;

import static java.lang.Math.E;
import java.util.Collection;
import java.util.Set;
import org.util.Pair;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.util.VertexContainer;

/**
 * In the most common sense of the term, a graph is an ordered pair G := (V, E)
 * comprising a set V of vertices together with a set E of edges, which are
 * 2-element subsets of V.
 *
 * @author Nils Rinke
 * @param <V>
 */
public class DefaultDirectedGraph<V> implements DirectedGraph<V> {

    private final static long serialVersionUID = 1896;
    protected final LinkedHashSet<V> vertices;
    protected final LinkedHashSet<Pair<V, V>> edges;
    protected final LinkedHashMap<V, VertexContainer<V>> vertexMap;

    /**
     * Creates an empty graph. It hasn't any vertices or edges.
     *
     */
    public DefaultDirectedGraph() {
        this.vertices = new LinkedHashSet<V>();
        this.edges = new LinkedHashSet<Pair<V, V>>();
        this.vertexMap = new LinkedHashMap<V, VertexContainer<V>>();
    }

    public DefaultDirectedGraph(Collection<V> verticies, Collection<Pair<V, V>> edges) {
        this.vertices = new LinkedHashSet<V>();
        this.edges = new LinkedHashSet<Pair<V, V>>();
        this.vertexMap = new LinkedHashMap<V, VertexContainer<V>>();
        for (V v : verticies) {
            this.addVertex(v);
        }
        for (Pair<V, V> pair : edges) {
            this.addEdge(pair);
        }
    }

    /**
     * Creates an new graph and fills it with the vertices and edges of the
     * specified graph.
     *
     * Only the references to the vertices will be copied. The objects of the
     * specified graph won't be copied.
     *
     * @param graph The references to the nodes and edges of the spectified
     * graph will be copied.
     * @throws java.lang.NullPointerException The method throws this exception
     * if the specified graph is <tt>null</tt>.
     */
    public DefaultDirectedGraph(DirectedGraph<V> graph) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }

        this.vertexMap = new LinkedHashMap<V, VertexContainer<V>>(graph.vertexSet().size());
        Set<V> vertexSet = graph.vertexSet();
        this.vertices = new LinkedHashSet<V>(vertexSet.size());
        for (V vertex : vertexSet) {
            addVertexWithoutCheckContains(vertex);
        }
        Set<Pair<V, V>> edgeSet = graph.edgeSet();
        this.edges = new LinkedHashSet<Pair<V, V>>(edgeSet.size());
        for (Pair<V, V> edge : edgeSet) {
            addEdgeWithoutCheckContains(edge.getFirst(), edge.getSecond());
        }
    }

    public DefaultDirectedGraph(DirectedGraph<V> graph, Collection<V> verticiesToAdd) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }

        this.vertexMap = new LinkedHashMap<V, VertexContainer<V>>(graph.vertexSet().size());
        Set<V> vertexSet = graph.vertexSet();
        this.vertices = new LinkedHashSet<V>(vertexSet.size());

        for (V vertex : verticiesToAdd) {
            addVertexWithoutCheckContains(vertex);
        }

        Set<Pair<V, V>> edgeSet = graph.edgeSet();
        this.edges = new LinkedHashSet<Pair<V, V>>(edgeSet.size());
        for (Pair<V, V> edge : edgeSet) {
            if (verticiesToAdd.contains(edge.getFirst()) && verticiesToAdd.contains(edge.getSecond())) {
                addEdgeWithoutCheckContains(edge.getFirst(), edge.getSecond());
            }
        }
    }

    /**
     * Returns the number of vertices in this graph.
     *
     * @return The method returns the number of vertices in this graph.
     */
    @Override
    public int numberOfVertices() {
        return vertices.size();
    }

    /**
     * Returns the number of edges in this directed graph.
     *
     * @return The method returns the number of edges in this directed graph.
     */
    @Override
    public int numberOfEdges() {
        return edges.size();
    }

    @Override
    public HashSet<V> vertexSet() {
        return vertices;
    }

    @Override
    public HashSet<Pair<V, V>> edgeSet() {
        return edges;
    }

    /**
     * Returns <code>true</code> if this graph contains the specified
     * <code>vertex</code>.
     *
     *
     * @param vertex The <code>vertex</code> will be checked for containment in
     * this graph.
     */
    @Override
    public boolean containsVertex(V vertex) {
        if (vertex == null) {
            return false;
        }
        return vertices.contains(vertex);
    }

    /**
     * Adds the vertex to this graph, if it isn't already a node of the graph.
     *
     * @param vertex The vertex to add.
     */
    @Override
    public boolean addVertex(V vertex) {
        if (!containsVertex(vertex)) {
            vertices.add(vertex);
            vertexMap.put(vertex, new VertexContainer());
            return true;
        }
        return false;
    }

    /**
     * Removes the specified vertex from this weighted directed graph, if it is
     * present. The incident edges will also be removed.
     *
     * @param vertex The specified vertex will be removed from this graph.
     */
    @Override
    public boolean removeVertex(V vertex) {
        if (vertices.contains(vertex)) {

            synchronized (this) {
                VertexContainer<V> container = vertexMap.remove(vertex);

                for (V successor : container.getSuccessors()) {
                    getVertexContainer(successor).getPredecessors().remove(vertex);
                    edges.remove(new Pair<V, V>(vertex, successor));
                }
                for (V predecessor : container.getPredecessors()) {
                    getVertexContainer(predecessor).getSuccessors().remove(vertex);
                    edges.remove(new Pair<V, V>(predecessor, vertex));
                }
                vertices.remove(vertex);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        boolean ret = false;
        for (V e : vertices) {
            if (removeVertex(e)) {
                ret = true;
            }
        }
        return ret;
    }

    protected void removeVertexConnections(V vertex) {
        if (vertices.contains(vertex)) {
            synchronized (this) {
                VertexContainer<V> container = getVertexContainer(vertex);
                for (V successor : container.getSuccessors()) {
                    getVertexContainer(successor).getPredecessors().remove(vertex);
                    edges.remove(new Pair<V, V>(vertex, successor));
                }
                for (V predecessor : container.getPredecessors()) {
                    getVertexContainer(predecessor).getSuccessors().remove(vertex);
                    edges.remove(new Pair<V, V>(predecessor, vertex));
                }
            }
        }
    }

    protected void removeVertexWithContainer(V vertex) {
        if (vertices.contains(vertex)) {
            synchronized (this) {
                vertexMap.remove(vertex);
                vertices.remove(vertex);
            }
        }
    }

    /**
     * Returns the indegree of the specified vertex in this graph. The indegree
     * of a vertex is definied as the number of its predecessors.
     *
     * @param vertex
     * @return The method returns the indegree of the specified vertex.
     * @throws java.util.NoSuchElementException The method throws the exception
     * if the vertex doesn't exists in this graph.
     */
    @Override
    public int inDegreeOf(V vertex) throws NoSuchElementException {
        if (!containsVertex(vertex)) {
            throw new NoSuchElementException(vertex
                    + " isn't a vertex of this graph!");
        }
        return getVertexContainer(vertex).getPredecessors().size();
    }

    /**
     * Returns the predecessors for the specified vertex in this graph.
     *
     * @param vertex This object is the vertex with the predecessors.
     * @return The method Returns a set of all predecessors of the specified
     * vertex in this directed graph.
     * @throws java.util.NoSuchElementException The method throws the exception
     * if the vertex doesn't exists in this directed graph.
     */
    @Override
    public LinkedHashSet<V> getPredecessors(V vertex) throws NoSuchElementException {
        if (!containsVertex(vertex)) {
            throw new NoSuchElementException(vertex
                    + " isn't a vertex of this graph!");
        }
        return getVertexContainer(vertex).getPredecessors();
    }

    /**
     * Returns a <code>HashSet</code> over the successor edges of the specified
     * vertex in this directed graph.
     *
     * @param vertex
     * @return The method Returns an Set over the successor edges of the
     * specified vertex in this directed graph.
     * @throws java.util.NoSuchElementException If this graph doesn't contains
     * the specified vertex.
     */
    @Override
    public HashSet<Pair<V, V>> incomingEdgesOf(V vertex)
            throws NoSuchElementException {
        if (!this.containsVertex(vertex)) {
            throw new NoSuchElementException(vertex
                    + " isn't a vertex of this graph!");
        }
        HashSet<Pair<V, V>> succEdges = new HashSet<Pair<V, V>>();
        VertexContainer<V> container = vertexMap.get(vertex);
        for (V successor : container.getPredecessors()) {
            succEdges.add(new Pair<V, V>(vertex, successor));
        }
        return succEdges;
    }

    /**
     * Returns the outdegree of the specified vertex in this graph. The oudegree
     * of a vertex is definied as the number of its successors.
     *
     * @param vert This object is the vertex with the successors.
     * @return The method returns the outdegree of the specified vertex.
     * @throws java.util.NoSuchElementException The method throws the exception
     * if the vertex doesn't exists in this graph.
     */
    @Override
    public int outDegreeOf(V vertex) throws NoSuchElementException {
        if (!containsVertex(vertex)) {
            throw new NoSuchElementException(vertex
                    + " isn't a vertex of this graph!");
        }
        return getVertexContainer(vertex).getSuccessors().size();
    }

    /**
     * Returns a <code>HashSet</code> over the successors of the specified
     * vertex in this directed graph.
     *
     * @param vertex This object is the vertex with the successors.
     * @return The method Returns a set over the successors of the specified
     * vertex in this graph.
     * @throws java.util.NoSuchElementException If the vertex doesn't exists in
     * this graph.
     */
    @Override
    public LinkedHashSet<V> getSuccessors(V vertex) throws NoSuchElementException {
        if (!containsVertex(vertex)) {
            throw new NoSuchElementException(vertex
                    + " isn't a vertex of this graph!");
        }
        return getVertexContainer(vertex).getSuccessors();
    }

    /**
     * Gibt die Nachfahren eines Knotens an.
     *
     * @param vertex
     * @return
     */
    public LinkedHashSet<V> getDescendats(V vertex) {
        LinkedHashSet<V> res = new LinkedHashSet<>();

        Stack<V> stack = new Stack<>();
        stack.add(vertex);

        while (!stack.isEmpty()) {
            V pop = stack.pop();
            LinkedHashSet<V> successors = getSuccessors(pop);
            res.addAll(successors);
            for (V suc : successors) {
                stack.push(suc);
            }
        }
        return res;
    }

    /**
     * Returns a <code>HashSet</code> over the successor edges of the specified
     * vertex in this directed graph.
     *
     * @param vertex
     * @return The method Returns an Set over the successor edges of the
     * specified vertex in this directed graph.
     * @throws java.util.NoSuchElementException If this graph doesn't contains
     * the specified vertex.
     */
    @Override
    public HashSet<Pair<V, V>> outgoingEdgesOf(V vertex)
            throws NoSuchElementException {
        if (!this.containsVertex(vertex)) {
            throw new NoSuchElementException(vertex
                    + " isn't a vertex of this graph!");
        }
        HashSet<Pair<V, V>> succEdges = new HashSet<Pair<V, V>>();
        VertexContainer<V> container = vertexMap.get(vertex);
        for (V successor : container.getSuccessors()) {
            succEdges.add(new Pair<V, V>(vertex, successor));
        }
        return succEdges;
    }

    /**
     * Returns true if this directed graph contains the specified edge.
     *
     * @param edge The edge will be checked for containment in this graph.
     * @return The method returns <code>true</code> if this directed graph
     * contains the specified edge.
     */
    @Override
    public boolean containsEdge(Pair<V, V> edge) {
        return edges.contains(edge);
    }

    /**
     * Returns true if this directed graph contains the edge with the specified
     * elements.
     *
     *
     * @param first This object is the first element of the edge.
     * @param second This object is the second element of the edge.
     * @return The method returns <code>true</code> if this directed graph
     * contains the edge with the specified elements.
     */
    @Override
    public boolean containsEdge(V first, V second) {
        return edges.contains(new Pair<V, V>(first, second));
    }

    /**
     * Adds the specified Edge as an edge to this directed graph, if it isn't
     * already in the graph.
     *
     * @param edge If this parameter or one of the elements of the Edge is
     * <tt>null</tt> the edge won't be added.
     */
    @Override
    public boolean addEdge(Pair<V, V> edge) {
        if (edge == null) {
            return false;
        }
        synchronized (this) {
            if (!containsEdge(edge)) {
                V first = edge.getFirst();
                if (!containsVertex(first)) {
                    return false;
                }
                V second = edge.getSecond();
                if (!containsVertex(second)) {
                    return false;
                }
                getVertexContainer(first).getSuccessors().add(second);
                getVertexContainer(second).getPredecessors().add(first);
                edges.add(edge);
            }
        }
        return true;
    }

    /**
     * Adds an edge with the specified elements to this directed graph, if it
     * isn't already in the graph.
     *
     * @param first If the first element of the edge is <tt>null</tt> the edge
     * won't be added.
     * @param second If the second element of the edge is <tt>null</tt> the edge
     * won't be added.
     */
    @Override
    public boolean addEdge(V first, V second) {
        return addEdge(new Pair<V, V>(first, second));
    }

    /**
     * Removes the specified edge from this directed graph, if it is present.
     *
     * @param edge The specified edge will be removed from this graph.
     */
    @Override
    public boolean removeEdge(Pair<V, V> edge) {
        if (edges.contains(edge)) {
            synchronized (this) {
                V first = edge.getFirst();
                if (first == null) {
                    return false;
                }
                V second = edge.getSecond();
                if (second == null) {
                    return false;
                }
                getVertexContainer(first).getSuccessors().remove(second);
                getVertexContainer(second).getPredecessors().remove(first);
                edges.remove(edge);
            }
        }
        return true;
    }

    /**
     * Removes the edge with the specified elements from this directed graph, if
     * it is present.
     *
     * @param first This object is the first element of the edge.
     * @param second This object is the scond element of the edge.
     */
    @Override
    public boolean removeEdge(V first, V second) {
        return removeEdge(new Pair<V, V>(first, second));
    }

    /**
     * Returns the edges in this directed graph.
     *
     * @return The method returns an {@link HashSet} of the edges in this graph.
     */
    public HashSet<Pair<V, V>> edges() {
        return edges;
    }

    public int numberOfNeighbours(V vertex) {
        VertexContainer<V> container = vertexMap.get(vertex);
        int neighbours = container.getSuccessors().size();
        for (V pred : container.getPredecessors()) {
            if (!container.getSuccessors().contains(pred)) {
                neighbours++;
            }
        }
        return neighbours;
    }

//    public DirectedGraph<E> getCore2() {
//        DirectedGraph<E> core = new DirectedGraph<E>(this);
//        Queue<E> removeableVertices = new LinkedList<E>();
//        for (E vertex : vertices) 
//            if(numberOfNeighbours(vertex)<2)
//                removeableVertices.offer(vertex);
//        while(!removeableVertices.isEmpty()){
//            E toRemove = removeableVertices.poll();
//
//            HashSet<E>  removePredecessors = core.predecessors(toRemove);
//            HashSet<E>  removeSuccessors   = core.successors(toRemove);
//            core.removeVertex(toRemove);
//            for (E predecessor : removePredecessors)
//                if(core.numberOfNeighbours(predecessor) < 2 && !removeableVertices.contains(predecessor))
//                    removeableVertices.offer(predecessor);
//            for (E successor : removeSuccessors)
//                if(core.numberOfNeighbours(successor) < 2 && !removeableVertices.contains(successor))
//                    removeableVertices.offer(successor);
//        }
//        return core;
//    }
    /**
     * Compares the specified graph, if it is a subgraph of this directed graph.
     *
     * Returns <tt>true</tt> if for each vertex of the graph an <tt>equal</tt>
     * vertex exists in this graph and for each edge of the graph an
     * <tt>equal</tt> edge exists in this graph.
     *
     * @param graph The specified graph will be compared for being a subgraph of
     * this weighted directed graph.
     * @return The method returns <tt>true</tt> if the specified graph is a
     * subgraph of this weighted directed graph.
     */
    public boolean sub(DefaultDirectedGraph<V> graph) {
        if (graph == null) {
            return false;
        }
        synchronized (this) {
            if (graph.numberOfVertices() > numberOfVertices()) {
                return false;
            }
            if (graph.numberOfEdges() > numberOfEdges()) {
                return false;
            }
            for (V vertex : vertices) {
                if (!containsVertex(vertex)) {
                    return false;
                }
            }
            for (Pair<V, V> edge : edges) {
                if (!containsEdge(edge)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Compares the specified object with this directed graph for equality.
     * <p>
     * Returns <tt>true</tt> if the given object is a subgraph of this graph
     * with the same number of vertices and edges.
     *
     * @param object The object will be compared for equality with this graph.
     * @return The method returns <tt>true</tt>
     * if the specified object is equal to this directed graph.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        DefaultDirectedGraph graph = (DefaultDirectedGraph) object;
        if (vertices.equals(graph.vertices) && edges.equals(graph.edges)) {
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.vertices != null ? this.vertices.hashCode() : 0);
        hash = 89 * hash + (this.edges != null ? this.edges.hashCode() : 0);
        return hash;
    }

//    /** Returns the dual graph of this directed graph.
//     *
//     *  @return The dual graph is a directed graph.
//     */
//    public synchronized DirectedGraph<V> dual() {
//        DirectedGraph<V> dual = new DirectedGraph<V>();
//        dual.vertices.addAll(vertices);
//        for (Edge<V, V> edge : edges) {
//            dual.addEdge(edge.transposition());
//        }
//        return dual;
//    }
    /**
     * Returns the complementary graph of this directed graph.
     *
     * @return The complementary graph is a directed graph.
     */
    public synchronized DefaultDirectedGraph<V> complement() {
        DefaultDirectedGraph<V> complement = new DefaultDirectedGraph<V>();
        complement.vertices.addAll(vertices);
        for (V e1 : vertices) {
            for (V e2 : vertices) {
                complement.addEdge(e1, e2);
            }
        }
        for (Pair<V, V> edge : edges) {
            complement.removeEdge(edge);
        }
        return complement;
    }

    /**
     * Returns the section of this directed graph with the specified graph.
     *
     * @param graph The graph to calculate the section with.
     * @return The section is a directed graph.
     * @throws java.lang.NullPointerException The method throws this exception
     * if the specified graph is <tt>null</tt>.
     */
    public synchronized DefaultDirectedGraph<V> section(DefaultDirectedGraph<V> graph) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }
        DefaultDirectedGraph<V> g = new DefaultDirectedGraph<V>();
        for (V e : vertices) {
            if (graph.containsVertex(e)) {
                g.addVertex(e);
            }
        }
        for (Pair<V, V> edge : edges) {
            if (graph.containsEdge(edge)) {
                g.addEdge(edge);
            }
        }
        return g;
    }

    /**
     * Returns the dual graph of this directed graph.
     *
     * @return The dual graph is a directed graph.
     */
    public synchronized DefaultDirectedGraph<V> transposition() {
        DefaultDirectedGraph<V> dual = new DefaultDirectedGraph<V>();
        for (V vertex : vertices) {
            dual.addVertexWithoutCheckContains(vertex);
        }
        for (Pair<V, V> edge : edges) {
            dual.addEdge(edge.getSecond(), edge.getFirst());
        }
        return dual;
    }

    /**
     * Returns the union of this directed graph with the specified one.
     *
     * @param graph The graph to calculate the union with.
     * @return The union is a directed graph.
     * @throws java.lang.NullPointerException The method throws this exception
     * if the specified graph is <tt>null</tt>.
     */
    public synchronized DefaultDirectedGraph<V> union(DefaultDirectedGraph<V> graph) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }
        DefaultDirectedGraph<V> union = new DefaultDirectedGraph<V>(this);
        union.vertices.addAll(graph.vertices);
        for (Pair<V, V> edge : graph.edges) {
            union.addEdge(edge);
        }
        return union;
    }

    /**
     * Returns the product of this directed graph and specified directed graph.
     *
     * @param graph The specified graph must have the same vertices as this.
     * @return The product is a graph.
     * @throws java.lang.NullPointerException The method throws this exception
     * if the specified graph is <tt>null</tt>.
     */
    public synchronized DefaultDirectedGraph<V> product(DefaultDirectedGraph<V> graph) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }
        if (!vertices.equals(graph.vertices)) {
            throw new NullPointerException("The vertices of the graphs are "
                    + "not the same!");
        }
        DefaultDirectedGraph<V> product = new DefaultDirectedGraph<V>();
        product.vertices.addAll(vertices);
        for (Pair<V, V> edge12 : edges) {
            for (Pair<V, V> edge21 : graph.edges) {
                if (edge12.getSecond().equals(edge21.getFirst())) {
                    product.addEdge(edge12.getFirst(), edge21.getSecond());
                }
            }
        }
        return product;
    }

    /**
     * Returns a string representation of this directed graph.
     *
     * The string representation consists of a list of the graph vertices and
     * edges without an order enclosed in braces ("{}"). The vertices and edges
     * are separated by the characters ", " (comma and space). Vertices are
     * converted to strings as by String.valueOf(Object).
     *
     * @return The method returns a string representation of this directed
     * graph.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("({");
        Iterator<V> e = vertices.iterator();
        Iterator<Pair<V, V>> i = edges.iterator();

        if (e.hasNext()) {
            buf.append(e.next());
        }
        for (; e.hasNext();) {
            buf.append(", " + e.next());
        }
        buf.append("}; {");
        if (i.hasNext()) {
            buf.append(i.next());
        }
        for (; i.hasNext();) {
            buf.append(", " + i.next());
        }
        buf.append("})");
        return buf.toString();
    }

    protected VertexContainer<V> getVertexContainer(V vertex) {
        return vertexMap.get(vertex);
    }

    /**
     * Adds the specified ordered Edge as an edge to this directed graph without
     * calling the {@link containsVertex(E vertex)}-Method.
     *
     * @param edge The specified ordered Edge to add.
     */
    protected boolean addEdgeWithoutCheckContains(V first, V second) {
        getVertexContainer(first).getSuccessors().add(second);
        getVertexContainer(second).getPredecessors().add(first);
        return edges.add(new Pair<V, V>(first, second));
    }

    public boolean addEdgeWithoutCheckContains(Pair<V, V> pair) {
        V first = pair.getFirst();
        V second = pair.getSecond();
        getVertexContainer(first).getSuccessors().add(second);
        getVertexContainer(second).getPredecessors().add(first);
        return edges.add(pair);
    }

    /**
     * Adds the specified vertex to this directed graph without calling the
     * <code>contains</code>-Method.
     *
     * @param vertex The vertex to add.
     */
    public boolean addVertexWithoutCheckContains(V vertex) {
        vertexMap.put(vertex, new VertexContainer<V>());
        return vertices.add(vertex);
    }

    @Override
    public Set<Pair<V, V>> getAllEdges(V sourceVertex, V targetVertex) {
        HashSet<Pair<V, V>> result = new HashSet<Pair<V, V>>();

        for (Pair<V, V> pair : edges) {
            if (pair.getFirst().equals(sourceVertex) && pair.getSecond().equals(targetVertex)) {
                result.add(pair);
            }
        }
        return result;
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
    public void changeVertex(V v_old, V v_new) {
        Set<V> pres = getPredecessors(v_old);
        Set<V> succs = getSuccessors(v_old);
        removeVertex(v_old);
        addVertex(v_new);
        for (V succ : succs) {
            addEdge(v_new, succ);
        }
        for (V pre : pres) {
            addEdge(pre, v_new);
        }
    }

}
