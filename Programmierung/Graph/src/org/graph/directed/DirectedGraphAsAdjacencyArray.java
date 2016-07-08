/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.directed;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class DirectedGraphAsAdjacencyArray<V> implements DirectedGraph<V> {
    protected final HashBiMap<V, Integer> vertexMap;
    protected final ArrayList<Integer> linkToAdjacencyArray;
    protected final ArrayList<Integer> adjacencyArray;
    
    private int counter = 0;
    protected int lastIndexOfAdjacencyArray = 0;

    public DirectedGraphAsAdjacencyArray() {
        vertexMap = HashBiMap.create();
        linkToAdjacencyArray = new ArrayList<Integer>();
        adjacencyArray = new ArrayList<Integer>();
    }
    
    
    public DirectedGraphAsAdjacencyArray(DirectedGraph<V> graph) {
        if (graph == null) {
            throw new NullPointerException("The parameter 'graph' is null!");
        }
        vertexMap = HashBiMap.create();
        linkToAdjacencyArray = new ArrayList<Integer>(graph.numberOfVertices());
        adjacencyArray = new ArrayList<Integer>(graph.numberOfEdges());
        
        Iterator<V> vertexIterator = graph.vertexSet().iterator();
        for (int i = 0; i < graph.numberOfVertices(); i++) {
            V vertex = vertexIterator.next();
            int node_id = counter++;
            vertexMap.put(vertex, node_id);
            linkToAdjacencyArray.add(lastIndexOfAdjacencyArray);
            lastIndexOfAdjacencyArray += graph.outDegreeOf(vertex); 
        }
        BiMap<Integer, V> inverseMap = vertexMap.inverse();
        for (int i = 0; i < counter; i++) {
            V vertex = inverseMap.get(i);
            for (V v : graph.getSuccessors(vertex)) {
                adjacencyArray.add(vertexMap.get(v));
            }  
        }
    }
    
    
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex) {
        if(!containsVertex(sourceVertex) || !containsVertex(targetVertex))
            return false;
        if(containsEdge(sourceVertex, targetVertex)) {
            return false;
        }
        int pos;
        if(vertexMap.get(sourceVertex) == linkToAdjacencyArray.size()-1)
            pos = lastIndexOfAdjacencyArray;
        else
            pos = linkToAdjacencyArray.get(vertexMap.get(sourceVertex)+1);
        adjacencyArray.add(pos, vertexMap.get(targetVertex));
        for (int i = vertexMap.get(sourceVertex)+1; i < linkToAdjacencyArray.size(); i++)
            linkToAdjacencyArray.set(i, linkToAdjacencyArray.get(i)+1);
        lastIndexOfAdjacencyArray++;
        return true;
    }
    

    @Override
    public boolean addEdge(Pair<V, V> edge) {
        return addEdge(edge.getFirst(), edge.getSecond());
    }
    

    @Override
    public boolean addVertex(V v) {
        if(!containsVertex(v)) {
            int node_id = counter++;
            vertexMap.put(v, node_id);
            linkToAdjacencyArray.add(lastIndexOfAdjacencyArray);
            return true;
        }
        return false;
    }
    

    @Override
    public Set<Pair<V, V>> getAllEdges(V sourceVertex, V targetVertex) {
        if(containsEdge(sourceVertex, targetVertex)) {
            Set<Pair<V, V>> res = new HashSet<Pair<V, V>>();
            res.add(new Pair<V, V>(sourceVertex, targetVertex));
            return res;
        }
        return null;
    }
    

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        int sourceVertexIndex = vertexMap.get(sourceVertex);
        int targetVertexIndex = vertexMap.get(targetVertex);
        
        int lower = linkToAdjacencyArray.get(vertexMap.get(sourceVertex));
        int upper;
        if(sourceVertexIndex != linkToAdjacencyArray.size()-1)
            upper = linkToAdjacencyArray.get(sourceVertexIndex+1);
        else
            upper = lastIndexOfAdjacencyArray;
        for (int i = lower; i < upper; i++) {
            if(adjacencyArray.get(i)==targetVertexIndex)
                return true;
        }
        return false;
    }
    

    @Override
    public boolean containsEdge(Pair<V, V> e) {
        return containsEdge(e.getFirst(), e.getSecond());
    }
    

    @Override
    public boolean containsVertex(V v) {
        return vertexMap.containsKey(v);
    }
    

    @Override
    public Set<Pair<V, V>> edgeSet() {
        Set<Pair<V, V>> res = new HashSet<Pair<V, V>>();
        BiMap<Integer, V> inverseMap = vertexMap.inverse();
        int lower = 0;
        for (int i = 1; i < linkToAdjacencyArray.size(); i++) {
            int upper = linkToAdjacencyArray.get(i);
            for (int j = lower; j < upper; j++)        
                res.add(new Pair<V, V>(inverseMap.get(i-1), inverseMap.get(adjacencyArray.get(j))));
            lower = upper;
        }
        for (int i = lower; i < lastIndexOfAdjacencyArray; i++) {
            res.add(new Pair<V, V>(inverseMap.get(linkToAdjacencyArray.size()-1), inverseMap.get(adjacencyArray.get(i))));
        }
        return res;
    }
    

    @Override
    public Set<Pair<V, V>> edgesOf(V vertex) {
        Set<Pair<V, V>> res = new HashSet<Pair<V, V>>();
        BiMap<Integer, V> inverseMap = vertexMap.inverse();
        int lower = 0;
        for (int i = 1; i < linkToAdjacencyArray.size(); i++) {
            int upper = linkToAdjacencyArray.get(i);
            for (int j = lower; j < upper; j++) {
                int index = vertexMap.get(vertex);
                if(index == i) {
                    res.add(new Pair<V, V>(vertex, inverseMap.get(adjacencyArray.get(j))));
                }
                if(index == adjacencyArray.get(j)) {
                    res.add(new Pair<V, V>(inverseMap.get(linkToAdjacencyArray.get(i)), vertex));
                }      
            }
            lower = upper;
        }
        return res;
    }
    

    @Override
    public boolean removeAllEdges(Collection<? extends Pair<V, V>> edges) {
        boolean graphChanged = false;
        for (Pair<V, V> edge : edges)
            if(removeEdge(edge))
                graphChanged = true;
        return graphChanged;
    }
    

    @Override
    public Set<Pair<V, V>> removeAllEdges(V source, V target) {
        Set<Pair<V, V>> edgeSet = new HashSet<Pair<V, V>>();
        if(removeEdge(source, target))
            edgeSet.add(new Pair<V, V>(source, target));
        return edgeSet;
    }
    

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        boolean graphChanged = false;
        for (V v : vertices)
            if(removeVertex(v))
                graphChanged = true;
        return graphChanged;
    }
    

    @Override
    public boolean removeEdge(V source, V target) {
        boolean remove = false;
        int sourceVertexIndex = vertexMap.get(source);
        int targetVertexIndex = vertexMap.get(target);

        int lower = linkToAdjacencyArray.get(sourceVertexIndex);
        int upper;
        if(sourceVertexIndex != linkToAdjacencyArray.size()-1)
            upper = linkToAdjacencyArray.get(sourceVertexIndex+1);
        else
            upper = lastIndexOfAdjacencyArray;

        int indexToRemove = 0;
        for (int i = lower; i < upper; i++) {
            if(adjacencyArray.get(i)==targetVertexIndex) {
                indexToRemove = i;
                remove = true;
                break;
            }
        }
        if(remove) {
            adjacencyArray.remove(indexToRemove);
            for (int i = sourceVertexIndex+1; i < linkToAdjacencyArray.size(); i++)
                linkToAdjacencyArray.set(i, linkToAdjacencyArray.get(i)-1);
            lastIndexOfAdjacencyArray--;
        }
        return remove;
    }
    

    @Override
    public boolean removeEdge(Pair<V, V> e) {
        return removeEdge(e.getFirst(), e.getSecond());
    }
    

    @Override
    public boolean removeVertex(V vertex) {
        if(containsVertex(vertex)) {
            int vertexIndex = vertexMap.get(vertex);

            //delete all edges starting at vertex
            int lower = linkToAdjacencyArray.get(vertexIndex);
            int upper;
            if(vertexIndex < linkToAdjacencyArray.size()-1)
                upper = linkToAdjacencyArray.get(vertexIndex+1);
            else
                upper = lastIndexOfAdjacencyArray;
            int delta = upper-lower;
            for (int i = 0; i < delta; i++)
                adjacencyArray.remove(lower);
            for (int i = vertexIndex+1; i < linkToAdjacencyArray.size(); i++)
                linkToAdjacencyArray.set(i, linkToAdjacencyArray.get(i)-delta);
            
            //delete all edges ending at vertex
            for (V predecessor : getPredecessors(vertex)) {
                removeEdge(predecessor, vertex);
            }
            
            //remove vertex from map and link
            vertexMap.remove(vertex);
            linkToAdjacencyArray.remove(vertexIndex);
            
            //decrease every vertex with a higher index by 1
            ArrayList<V> resort = new ArrayList<V>();
            for (V v : vertexMap.keySet())
                if(vertexMap.get(v) > vertexIndex)
                    resort.add(v);
            for (V v : resort)
                vertexMap.forcePut(v, vertexMap.get(v)-1);
            
            //actualize all relevant indizees in adjacency array
            for (int i = 0; i < lastIndexOfAdjacencyArray; i++)
                if(adjacencyArray.get(i) > vertexIndex)
                    adjacencyArray.set(i, adjacencyArray.get(i)-1);
            return true;
        }
        return false;
    }
    

    @Override
    public Set<V> vertexSet() {
        HashSet<V> vertices = new HashSet<V>(vertexMap.size());
        for (V v : vertexMap.keySet()) {
            vertices.add(v);
        }
        return vertices;
//        return vertexMap.keySet();
    }
    

    @Override
    public int inDegreeOf(V vertex) {
        int index = vertexMap.get(vertex);
        int indegree = 0;
        for (Integer integer : adjacencyArray) {
            if(integer==index)
                indegree++;
        }
        return indegree;
    }
    

    @Override
    public int outDegreeOf(V vertex) {
        int indexOfVertex = vertexMap.get(vertex);
        int lower = linkToAdjacencyArray.get(indexOfVertex);
        int upper;
        if(indexOfVertex != linkToAdjacencyArray.size()-1)
            return linkToAdjacencyArray.get(indexOfVertex+1) - lower;
        else
            return lastIndexOfAdjacencyArray-lower;
    }

    @Override
    public Set<Pair<V, V>> incomingEdgesOf(V vertex) {
        Set<Pair<V, V>> incomingEdges = new HashSet<Pair<V, V>>();
        BiMap<Integer, V> inverseMap = vertexMap.inverse();
        int indexOfVertex = vertexMap.get(vertex);
        int lower = 0;
        for (int i = 1; i < linkToAdjacencyArray.size(); i++) {
            int upper = linkToAdjacencyArray.get(i);
            for (int j = lower; j < upper; j++) {
                if(adjacencyArray.get(j) == indexOfVertex)
                    incomingEdges.add(new Pair<V, V>(inverseMap.get(i-1), vertex));
            }
            lower = upper;
        }
        return incomingEdges;
    }

    @Override
    public Set<Pair<V, V>> outgoingEdgesOf(V vertex) {
        Set<Pair<V, V>> outgoingEdges = new HashSet<Pair<V, V>>();
        BiMap<Integer, V> inverseMap = vertexMap.inverse();
        int indexOfVertex = vertexMap.get(vertex);
        int lower = linkToAdjacencyArray.get(indexOfVertex);
        int upper;
        if(indexOfVertex != linkToAdjacencyArray.size()-1)
            upper = linkToAdjacencyArray.get(indexOfVertex+1);
        else
            upper = linkToAdjacencyArray.size()-1;
        for (int i = lower; i < upper; i++)
            outgoingEdges.add(new Pair<V, V>(vertex, inverseMap.get(adjacencyArray.get(i))));
        return outgoingEdges;
    }

    @Override
    public Set<V> getPredecessors(V vertex) {
        Set<V> preds = new HashSet<V>();
        BiMap<Integer, V> inverseMap = vertexMap.inverse();
        int indexOfVertex = vertexMap.get(vertex);
        int lower = 0;
        for (int i = 1; i < linkToAdjacencyArray.size(); i++) {
            int upper = linkToAdjacencyArray.get(i);
            for (int j = lower; j < upper; j++) {
                if(adjacencyArray.get(j) == indexOfVertex)
                    preds.add(inverseMap.get(i-1));
            }
            lower = upper;
        }
        return preds;
    }

    @Override
    public Set<V> getSuccessors(V vertex) {
        Set<V> succs = new HashSet<V>();
        BiMap<Integer, V> inverseMap = vertexMap.inverse();
        int indexOfVertex = vertexMap.get(vertex);
        int lower = linkToAdjacencyArray.get(indexOfVertex);
        int upper;
        if(indexOfVertex != linkToAdjacencyArray.size()-1)
            upper = linkToAdjacencyArray.get(indexOfVertex+1);
        else
            upper = lastIndexOfAdjacencyArray;
        for (int i = lower; i < upper; i++)
            succs.add(inverseMap.get(adjacencyArray.get(i)));
        return succs;
    }
    
    @Override
    public int numberOfVertices() {
        return linkToAdjacencyArray.size();
    }

    @Override
    public int numberOfEdges() {
        return lastIndexOfAdjacencyArray;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("DirectedGraphAsAdjacencyArray: ({");
        Iterator<V> e = vertexMap.keySet().iterator();
        Iterator<Pair<V,V>> i = edgeSet().iterator();

        if (e.hasNext()) {
            buf.append(e.next());
        }
        for (; e.hasNext();) {
            buf.append(", ").append(e.next());
        }
        buf.append("}; {");
        if (i.hasNext()) {
            Pair edge = i.next();
            buf.append(edge);
        }
        for (; i.hasNext();) {
            Pair edge = i.next();
            buf.append(", ").append(edge);
        }
        buf.append("})");
        return buf.toString();
    } 
    
    
    public static void main(String[] args) {
        
        DirectedGraphAsAdjacencyArray<String> test =
                                    new DirectedGraphAsAdjacencyArray<String>();
        test.addVertex("A");
        test.addVertex("B");      
        test.addVertex("C");
        test.addVertex("D");
        
        test.addEdge("A","B");
        test.addEdge("A","D");
        test.addEdge("B","C");
        test.addEdge("B","D");
        
        test.addVertex("E");
        test.addVertex("F");
        test.addVertex("G");

        test.addEdge("A","B");
        test.addEdge("A","D");
        test.addEdge("B","C");
        test.addEdge("B","D");
        test.addEdge("B","E");
        test.addEdge("C","E");
        test.addEdge("D","E");
        test.addEdge("D","F");
        test.addEdge("E","F");
        test.addEdge("E","G");
        test.addEdge("F","G");
        System.out.println(test + ": " + test.adjacencyArray.size() + " - " + test.lastIndexOfAdjacencyArray);
        System.out.println("linkToAdjacencyArray: " +test.linkToAdjacencyArray);
        System.out.println("adjacencyArray: " +test.adjacencyArray);
        System.out.println(test.inDegreeOf("A") + " predecessors of A: " + test.getPredecessors("A"));
        System.out.println(test.outDegreeOf("A")+ " successors of A: " + test.getSuccessors("A") + " --> " + test.outgoingEdgesOf("A"));
        System.out.println(test.inDegreeOf("B") + " predecessors of B: " + test.getPredecessors("B") + " --> " + test.incomingEdgesOf("B"));
        System.out.println(test.outDegreeOf("B")+ " successors of B: " + test.getSuccessors("B"));
        System.out.println(test.inDegreeOf("G") + " predecessors of G: " + test.getPredecessors("G")+ " --> " + test.incomingEdgesOf("G"));
        System.out.println(test.outDegreeOf("G")+ " successors of G: " + test.getSuccessors("G") + " --> " + test.outgoingEdgesOf("G"));
        
        System.out.println(test.removeEdge("C", "E"));
        System.out.println("linkToAdjacencyArray: " +test.linkToAdjacencyArray);
        System.out.println("adjacencyArray: " +test.adjacencyArray);
        
        System.out.println(test.removeEdge("G", "F"));
        System.out.println("linkToAdjacencyArray: " +test.linkToAdjacencyArray);
        System.out.println("adjacencyArray: " +test.adjacencyArray);
        
        System.out.println(test.removeVertex("D"));
        System.out.println("linkToAdjacencyArray: " +test.linkToAdjacencyArray);
        System.out.println("adjacencyArray: " +test.adjacencyArray);
        System.out.println(test);
    }

    @Override
    public void changeVertex(V v_old, V v_new) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
