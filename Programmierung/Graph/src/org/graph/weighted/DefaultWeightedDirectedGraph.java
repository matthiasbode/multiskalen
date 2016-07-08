package org.graph.weighted;

import java.util.Collection;
import org.util.Pair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import org.graph.directed.DefaultDirectedGraph;

/**
 * The class "DefaultWeightedDirectedGraph" provides properties and methods of
 * objects for weighted directed graphs. A weighted directed graph is a graph
 * where the edges (and the nodes) are weigthed.
 *
 * <p>
 * It is important, that a priori graphs doesn't have an order. Therefore you
 * can't access to a vertex or edge at a certain position in the graph.</p>
 *
 * <p>
 * To tests the equality of the objects that are the vertices in the class
 * <code>DefaultWeightedDirectedGraph</code> we use the <code>equals</code>
 * method. Therefore you should overwrite the <code>equals</code> method of this
 * objects to define their equality. If you don't do it, two object are equal
 * when their addresses are the same (identity).</p>
 *
 * <p>
 * <strong>Version: </strong> <br><dd>1.1, March 2005</dd></p>
 * <p>
 * <strong>Author: </strong> <br>
 * <dd>University of Hannover</dd>
 * <dd>Institute of Computer Science in Civil Engineering</dd>
 */
public class DefaultWeightedDirectedGraph<V, W> extends DefaultDirectedGraph<V> implements Cloneable, WeightedDirectedGraph<V, W> {

    private final static long serialVersionUID = 1896;

    protected final LinkedHashMap<V, W> vertexWeights;
    protected final LinkedHashMap<Pair<V, V>, W> edgeWeights;

    /**
     * Creates an empty weighted directed graph. It hasn't any nodes or edges.
     *
     */
    public DefaultWeightedDirectedGraph() {
        super();
        this.vertexWeights = new LinkedHashMap<V, W>();
        this.edgeWeights = new LinkedHashMap<Pair<V, V>, W>();
    }

    /**
     * Creates an new weighted directed graph and fills it with the vertices and
     * edges of the specified weighted directed graph.
     *
     * Only the references to the vertices will be copied. The objects of the
     * specified graph won't be copied.
     *
     * @param graph The references to the vertices and edges of the spectified
     * graph will be copied.
     * @throws java.lang.NullPointerException The method throws this exception
     * if the specified graph is <tt>null</tt>.
     */
    public DefaultWeightedDirectedGraph(DefaultWeightedDirectedGraph<V, W> graph) {
        super(graph);

        this.vertexWeights = new LinkedHashMap<V, W>();
        this.edgeWeights = new LinkedHashMap<Pair<V, V>, W>();

        for (Pair<V, V> edge : graph.edges) {
            edgeWeights.put(edge, graph.getEdgeWeight(edge));
        }
    }

    public DefaultWeightedDirectedGraph(WeightedDirectedGraph<V, W> graph) {
        super(graph);

        this.vertexWeights = new LinkedHashMap<V, W>();
        this.edgeWeights = new LinkedHashMap<Pair<V, V>, W>();

        for (Pair<V, V> edge : graph.edgeSet()) {
            edgeWeights.put(edge, graph.getEdgeWeight(edge));
        }
    }

    public DefaultWeightedDirectedGraph(Collection<V> verticies, Collection<Pair<V, V>> edges, LinkedHashMap<Pair<V, V>, W> edgeWeights) {
        super(verticies, edges);
        this.edgeWeights = edgeWeights;
        this.vertexWeights = new LinkedHashMap<V, W>();
    }

    /**
     * Gets the weight with the specified index of the specified vertex.
     *
     * @param vertex The vertex has to be a member of this graph.
     *
     * @throws java.util.NoSuchElementException The method throws this exception
     * if the specified vertex doesn't exist in this graph.
     */
    public W getVertexWeight(V vertex) {
        return vertexWeights.get(vertex);
    }

    /**
     * Sets the weight with the specified index of the specified vertex.
     *
     * @param vertex The vertex has to be a memeber of this graph.
     * @param weight The weight of the vertex.
     *
     * @throws java.util.NoSuchElementException The method throws this exception
     * if the specified vertex doesn't exist in this graph.
     */
    public void setVertexWeight(V vertex, W weight) {
//        if(vertexWeights.containsKey(vertex))
//            vertexWeights.remove(vertex);
        vertexWeights.put(vertex, weight);
    }

    /**
     * Gets the weight of the specified edge
     *
     * @param edge The edge has to be a member of this graph.
     *
     * @throws java.util.NoSuchElementException The method throws this exception
     * if the specified edge doesn't exist in this graph.
     */
    @Override
    public W getEdgeWeight(Pair<V, V> edge) {
        return edgeWeights.get(edge);
    }

    /**
     * Gets the weight of the edge with the specified elements.
     *
     * @param first This object is the first element of the edge.
     * @param second This object is the second element of the edge.
     *
     * @throws java.util.NoSuchElementException The method throws this exception
     * if the specified edge doesn't exist in this graph.
     */
    @Override
    public W getEdgeWeight(V first, V second) {
        return getEdgeWeight(new Pair<V, V>(first, second));
    }

    /**
     * Sets the weight of the specified edge.
     *
     * @param edge The edge has to be a member of this graph.
     * @param weight The weight of the edge.
     *
     * @throws java.util.NoSuchElementException The method throws this exception
     * if the specified edge doesn't exist in this graph.
     */
    @Override
    public void setEdgeWeight(Pair<V, V> edge, W weight) {
        edgeWeights.put(edge, weight);
    }

    /**
     * Sets the weight of the edge with the specified elements.
     *
     * @param first This object is the first element of the edge.
     * @param second This object is the second element of the edge.
     * @param weight The weight of the edge.
     *
     * @throws java.util.NoSuchElementException The method throws this exception
     * if the specified edge doesn't exist in this graph.
     */
    public void setEdgeWeight(V first, V second, W weight) {
        setEdgeWeight(new Pair<V, V>(first, second), weight);
    }

    /**
     * Adds the edge with the specified elements and the given weight to this
     * weighted Graph.
     *
     * @param first
     * @param second
     * @param weight
     * @return
     */
    @Override
    public boolean addEdge(V first, V second, W weight) {
        Pair edge = new Pair<>(first, second);
        return addEdge(edge, weight);
    }

    @Override
    public boolean addEdge(Pair<V, V> edge, W weight) {

        V first = edge.getFirst();
        V second = edge.getSecond();
        if (first == null || second == null) {
            System.err.println("Hinzufügen nicht möglich!");
            return false;
        }
        synchronized (this) {
            if (!containsEdge(first, second)) {
                if (!containsVertex(first)) {
                    System.err.println("Hinzufügen nicht möglich!");
                    return false;
                }
                if (!containsVertex(second)) {
                    System.err.println("Hinzufügen nicht möglich!");
                    return false;
                }
                getVertexContainer(first).getSuccessors().add(second);
                getVertexContainer(second).getPredecessors().add(first);
                edges.add(edge);
                edgeWeights.put(edge, weight);
            }
        }
        return true;
    }

//    @Override
//    public WeightedGraph<E,W> getCore2() {
//        WeightedGraph<E,W> core = new WeightedGraph<E, W>();
//        DirectedGraph<E> tmpCore = super.getCore2();
//        core.vertices.addAll(tmpCore.getVertices());
//        core.edges.addAll(tmpCore.);
//        for (Edge<E, E> edge : tmpCore.edges)
//            core.setEdgeWeight(edge, getEdgeWeight(edge));
//        return core;
//    }
    /**
     * Compares the specified object with this weighted directed graph for
     * equality.
     *
     * Returns <tt>true</tt> if the given object is a subgraph of this graph
     * with the same number of nodes and edges.
     *
     * @param object The object will be compared for equality with this graph.
     * @return The method returns <tt>true</tt>
     * if the specified object is equal to this weighted directed graph.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DefaultWeightedDirectedGraph)) {
            return false;
        }
        DefaultWeightedDirectedGraph graph = (DefaultWeightedDirectedGraph) object;
        if (graph.numberOfVertices() != numberOfVertices()) {
            return false;
        }
        if (graph.numberOfEdges() != numberOfEdges()) {
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
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.vertices != null ? this.vertices.hashCode() : 0);
        hash = 89 * hash + (this.edges != null ? this.edges.hashCode() : 0);
        return hash;
    }

    /**
     * Returns the dual graph of this weighted directed graph. The weight of an
     * edge in this graph will mapped to its transposition in the dual graph.
     *
     * @return The dual graph is a weighted directed graph.
     */
    @Override
    public synchronized DefaultWeightedDirectedGraph<V, W> dual() {
        DefaultWeightedDirectedGraph<V, W> dual = new DefaultWeightedDirectedGraph<V, W>();
        for (V vertex : vertices) {
            dual.addVertexWithoutCheckContains(vertex);
        }
        for (Pair<V, V> edge : edges) {
            dual.addEdge(edge.getSecond(), edge.getFirst(), edgeWeights.get(edge));
        }
        return dual;
    }

    /**
     * Returns the union of this weighted directed graph with the specified
     * graph.
     *
     * @param graph The graph to calculate the union with.
     * @return The union is a weighted directed graph.
     * @throws java.lang.NullPointerException The method throws this exception
     * if the specified graph is <tt>null</tt>.
     */
    public synchronized DefaultWeightedDirectedGraph<V, W> union(DefaultWeightedDirectedGraph<V, W> graph) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }
        DefaultWeightedDirectedGraph<V, W> union = new DefaultWeightedDirectedGraph<V, W>(this);
        for (V e : graph.vertices) {
            union.addVertex(e);
        }
        for (Pair<V, V> edge : graph.edges) {
            if (!union.containsEdge(edge)) {
                union.addEdge(edge);
                union.setEdgeWeight(edge, graph.getEdgeWeight(edge));
            }
        }
        return union;
    }

    /**
     * Returns the product of this weighted directed graph and specified
     * weighted directed graph. All weights of the edges in the product have the
     * value 0.0.
     *
     * @param graph The specified graph must have the same nodes as this one.
     * @return The product is a weighted directed graph.
     * @throws java.lang.NullPointerException The method throws this exception
     * if the specified graph is <tt>null</tt>.
     */
    public synchronized DefaultWeightedDirectedGraph<V, W> product(DefaultWeightedDirectedGraph<V, W> graph) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }
        DefaultWeightedDirectedGraph<V, W> product = new DefaultWeightedDirectedGraph<V, W>();
        product.vertices.addAll(vertices);
        product.vertices.addAll(graph.vertices);
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
     * Returns a string representation of this weighted directed graph.
     *
     * The string representation consists of a list of the graphs's nodes and
     * edges without an order enclosed in braces ("{}"). The nodes and edges are
     * separated by the characters ", " (comma and space). Nodes are converted
     * to strings as by String.valueOf(Object).
     *
     * @return The method returns a string representation of this directed
     * graph.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("WeightedDirectedGraph: ({");
        Iterator<V> e = vertices.iterator();
        Iterator<Pair<V, V>> i = edges.iterator();

        if (e.hasNext()) {
            buf.append(e.next());
        }
        for (; e.hasNext();) {
            buf.append(", ").append(e.next());
        }
        buf.append("}; {");
        if (i.hasNext()) {
            Pair edge = i.next();
            buf.append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        for (; i.hasNext();) {
            Pair edge = i.next();
            buf.append(", ").append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        buf.append("})");
        return buf.toString();
    }
}
