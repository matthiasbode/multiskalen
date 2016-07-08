package org.graph.undirected;

import org.util.UndirectedVertexContainer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.util.Pair;

/**
 * Die Klasse SimpleGraph beschreibt einen ungerichteten Graphen.
 * @author rinke
 */
public class SimpleGraph<V> implements UnDirected<V> {
    
    protected final HashSet<V> vertices;
    protected final HashSet<Pair<V, V>> edges;

    protected final HashMap<V, UndirectedVertexContainer<V>> vertexMap;

    public SimpleGraph() {
        this.vertices = new HashSet<V>();
        this.edges = new HashSet<Pair<V, V>>();
        this.vertexMap = new HashMap<V, UndirectedVertexContainer<V>>();
    }
    
    
    public int numberOfVertices() {
        return vertices.size();
    }
    
    
    public int numberOfEdges() {
        return edges.size();
    }
    
    
    /** Adds the vertex to this graph,
     *  if it isn't already a node of the graph.
     *
     *  @param vertex The vertex to add.
     */
    @Override
    public boolean addVertex(V vertex) {
        if (!containsVertex(vertex)) {
            vertices.add(vertex);
            vertexMap.put(vertex, new UndirectedVertexContainer<V>());
            return true;
        }
        return false;
    }
    
    
    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        boolean changed = false;
        for (V v : vertices) {
            if(removeVertex(v))
                changed = true;
        }
        return changed;
    }

    

    @Override
    public boolean removeVertex(V vertex) {
        if(containsVertex(vertex)) {
            for (V neighbour : getVertexContainer(vertex).neighbours) {
                getVertexContainer(neighbour).neighbours.remove(neighbour);
                removeEdge(vertex, neighbour);
            }
            return true;
        }
        return false;
    }
    

    @Override
    public boolean addEdge(Pair<V, V> edge) {
        //Test auf Schleifen
        if (edge.getFirst().equals(edge.getSecond())) {
            return false;
        }
        if(containsEdge(edge)) {
           return false;
        }
        boolean add = edges.add(edge);
        if(add) {
            getVertexContainer(edge.getFirst()).addNeighbour(edge.getSecond());
            getVertexContainer(edge.getSecond()).addNeighbour(edge.getFirst());
        }
        return add;
    }
    
    
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex) {
        boolean add = edges.add(new Pair<V, V>(sourceVertex, targetVertex));
        if(add) {
            getVertexContainer(sourceVertex).addNeighbour(targetVertex);
            getVertexContainer(targetVertex).addNeighbour(sourceVertex);
        }
        return add;
    }

   
    /** Removes the specified edge from this simple graph, if it is present.
     *
     *  @param edge The specified edge will be removed from this graph.
     */
    @Override
    public boolean removeEdge(Pair<V, V> edge) {
        if (containsEdge(edge)) { 
            getVertexContainer(edge.getFirst()).neighbours.remove(edge.getSecond());
            getVertexContainer(edge.getSecond()).neighbours.remove(edge.getFirst());
            return (edges.remove(edge) || edges.remove(edge.transposition()));
        }
        return false; 
    }

    @Override
    public boolean removeEdge(V source, V target) {
        return removeEdge(new Pair<V, V>(source, target));
    }
    
    
    @Override
    public int degreeOf(V vertex) {
        return getVertexContainer(vertex).getDegree();
    }

    @Override
    public Set<V> getNeighbours(V vertex) {
        return getVertexContainer(vertex).neighbours;
    }

    @Override
    public Set<Pair<V, V>> neighbourEdgesOf(V vertex) {
        HashSet<Pair<V, V>> neighbourEdges = new HashSet<Pair<V, V>>();
        for (V neighbour : getVertexContainer(vertex).neighbours) {
            neighbourEdges.add(new Pair<V, V>(vertex, neighbour));
        }
        return neighbourEdges;
    }

    @Override
    public Set<Pair<V, V>> getAllEdges(V sourceVertex, V targetVertex) {
        Pair<V,V> edge = new Pair<V,V>(sourceVertex, targetVertex);
        if(edges.contains(edge) || edges.contains(edge.transposition())) {
            Set<Pair<V, V>> set = new HashSet<Pair<V, V>>();
            set.add(edge);
            return set;
        }
        return null;
    }


    @Override
    public boolean containsVertex(V vertex) {
        return vertices.contains(vertex);
    }
    

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        Pair<V,V> edge = new Pair<V,V>(sourceVertex, targetVertex);
        return (edges.contains(edge) || edges.contains(edge.transposition()));
    }

    @Override
    public boolean containsEdge(Pair<V, V> edge) {
        return (edges.contains(edge) || edges.contains(edge.transposition()));
    }
    

    @Override
    public Set<V> vertexSet() {
        return vertices;
    }
    

    @Override
    public Set<Pair<V, V>> edgeSet() {
        return edges;
    }

    @Override
    public Set<Pair<V, V>> edgesOf(V vertex) {
        Set<Pair<V, V>> set = new HashSet<Pair<V, V>>();
        for (V e : getVertexContainer(vertex).neighbours) {
            set.add(new Pair<V, V>(vertex, e));
        }
        return set;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Pair<V, V>> edges) {
        boolean changed = false;
        for (Pair<V, V> edge : edges) {
            if(removeEdge(edge))
                changed = true;
        }
        return changed;
    }

    @Override
    public Set<Pair<V, V>> removeAllEdges(V source, V target) {
        Pair<V,V> edge = new Pair<V,V>(source, target);
        if(containsEdge(edge)) {
            Set<Pair<V, V>> set = new HashSet<Pair<V, V>>();
            set.add(edge);
            return set;
        }
        return null;
    }

    
    protected UndirectedVertexContainer<V> getVertexContainer(V vertex) {
        return vertexMap.get(vertex);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("SimpleGraph: ({");
        Iterator<V> e = vertices.iterator();
        Iterator<Pair<V,V>> i = edges.iterator();

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
}
