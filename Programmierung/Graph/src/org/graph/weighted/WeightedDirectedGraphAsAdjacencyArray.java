/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.graph.directed.DirectedGraphAsAdjacencyArray;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class WeightedDirectedGraphAsAdjacencyArray<V,W>
                extends DirectedGraphAsAdjacencyArray<V>
                implements WeightedDirectedGraph<V, W> {
    
    protected final ArrayList<W> weightsPerEdge;

    public WeightedDirectedGraphAsAdjacencyArray() {
        super();
        weightsPerEdge = new ArrayList<W>();
    }
    
    public WeightedDirectedGraphAsAdjacencyArray(WeightedDirectedGraph<V,W> graph) {
        super(graph);
        weightsPerEdge = new ArrayList<W>(graph.numberOfEdges());
        BiMap<Integer, V> inverse = vertexMap.inverse();
        for (int i = 0; i < linkToAdjacencyArray.size(); i++) {
            int lower = linkToAdjacencyArray.get(i);
            int upper;
            if(i < linkToAdjacencyArray.size()-1)
                upper = linkToAdjacencyArray.get(i+1);
            else
                upper = lastIndexOfAdjacencyArray; 
            for (int j = lower; j < upper; j++) {
                V first = inverse.get(i);
                V second = inverse.get(adjacencyArray.get(j));
                weightsPerEdge.add(graph.getEdgeWeight(first, second));
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
            pos=lastIndexOfAdjacencyArray;
        else
            pos = linkToAdjacencyArray.get(vertexMap.get(sourceVertex)+1);
        adjacencyArray.add(pos, vertexMap.get(targetVertex));
        weightsPerEdge.add(pos, null);
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
    public boolean addEdge(V sourceVertex, V targetVertex, W weight) {
        if(!containsVertex(sourceVertex) || !containsVertex(targetVertex))
            return false;
        if(containsEdge(sourceVertex, targetVertex)) {
            return false;
        }

        int pos;
        if(vertexMap.get(sourceVertex) == linkToAdjacencyArray.size()-1)
            pos=lastIndexOfAdjacencyArray;
        else
            pos = linkToAdjacencyArray.get(vertexMap.get(sourceVertex)+1);
        adjacencyArray.add(pos, vertexMap.get(targetVertex));
        weightsPerEdge.add(pos, null);
        for (int i = vertexMap.get(sourceVertex)+1; i < linkToAdjacencyArray.size(); i++) {
            linkToAdjacencyArray.set(i, linkToAdjacencyArray.get(i)+1);
            if(i == weightsPerEdge.size())
                weightsPerEdge.add(weight);
            else
                weightsPerEdge.set(i, weight);
        }
        lastIndexOfAdjacencyArray++;
        
        return true;
    }
    
    
    @Override
    public boolean addEdge(Pair<V, V> edge, W weight) {
        return addEdge(edge.getFirst(), edge.getSecond(), weight);
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
            weightsPerEdge.remove(indexToRemove);
            for (int i = sourceVertexIndex+1; i < linkToAdjacencyArray.size(); i++)
                linkToAdjacencyArray.set(i, linkToAdjacencyArray.get(i)-1);
            lastIndexOfAdjacencyArray--;
        }
        return remove;
    }
    

    @Override
    public boolean removeEdge(Pair<V, V> e) {
        return this.removeEdge(e.getFirst(), e.getSecond());
    }

    @Override
    public void setEdgeWeight(Pair<V,V> e, W weight) {
        if(containsEdge(e)) {
            int sourceVertexIndex = vertexMap.get(e.getFirst());
            int targetVertexIndex = vertexMap.get(e.getSecond());

            int lower = linkToAdjacencyArray.get(sourceVertexIndex);
            int upper;
            if(sourceVertexIndex != linkToAdjacencyArray.size()-1)
                upper = linkToAdjacencyArray.get(sourceVertexIndex+1);
            else
                upper = lastIndexOfAdjacencyArray;
            for (int i = lower; i < upper; i++)
                if(adjacencyArray.get(i)==targetVertexIndex) {
                    weightsPerEdge.set(i, weight);
                    break;
                }
        }
    }
    
    
    @Override
    public W getEdgeWeight(V sourceVertex, V targetVertex) {
        return getEdgeWeight(new Pair<V, V>(sourceVertex, targetVertex));
        
    }
    

    @Override
    public W getEdgeWeight(Pair<V,V> e) {
        if(containsEdge(e)) {
            int sourceVertexIndex = vertexMap.get(e.getFirst());
            int targetVertexIndex = vertexMap.get(e.getSecond());

            int lower = linkToAdjacencyArray.get(sourceVertexIndex);
            int upper;
            if(sourceVertexIndex != linkToAdjacencyArray.size()-1)
                upper = linkToAdjacencyArray.get(sourceVertexIndex+1);
            else
                upper = lastIndexOfAdjacencyArray;
            for (int i = lower; i < upper; i++)
                if(adjacencyArray.get(i)==targetVertexIndex)
                    return weightsPerEdge.get(i);
        }
        return null;
    }
    
    
    @Override
    public WeightedDirectedGraph<V, W> dual() {
        WeightedDirectedGraphAsAdjacencyArray<V,W> dual =
                              new WeightedDirectedGraphAsAdjacencyArray<V, W>();
        for (V v : vertexSet()) {
            dual.addVertex(v);
        }
        for (Pair<V, V> edge : edgeSet()) {
            dual.addEdge(edge.transposition());
            dual.setEdgeWeight(edge.transposition(), getEdgeWeight(edge));
        }
        return dual;
    }
    
    
    /** Returns a string representation of this weighted directed graph.
     *
     *  The string representation consists of a list of the graphs's nodes and
     *  edges without an order enclosed in braces ("{}"). The nodes and edges are
     *  separated by the characters ", " (comma and space).
     *  Nodes are converted to strings as by String.valueOf(Object).
     *
     *  @return The method returns a string representation of this directed
     *          graph.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("WeightedDirectedGraphAsAdjacencyArray: ({");
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
            buf.append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        for (; i.hasNext();) {
            Pair edge = i.next();
            buf.append(", ").append(edge).append(" --> ").append(getEdgeWeight(edge));
        }
        return buf.toString();
    }

    protected ArrayList<Integer> getAdjacencyArray() {
        return adjacencyArray;
    }

    protected ArrayList<Integer> getLinkToAdjacencyArray() {
        return linkToAdjacencyArray;
    }

    protected HashBiMap<V, Integer> getVertexMap() {
        return vertexMap;
    }
    
    protected int[] getSuccessors(int vertex) {
        int lower = linkToAdjacencyArray.get(vertex);
        int upper;
        if(vertex != linkToAdjacencyArray.size()-1)
            upper = linkToAdjacencyArray.get(vertex+1);
        else
            upper = lastIndexOfAdjacencyArray;
        int[] succs = new int[upper-lower];
        for (int i = 0; i < upper-lower; i++)
            succs[i] = adjacencyArray.get(i+lower);
        return succs;
    }
    
    
    protected W getEdgeWeight(int sourceVertexIndex, int targetVertexIndex) {
            int lower = linkToAdjacencyArray.get(sourceVertexIndex);
            int upper;
            if(sourceVertexIndex != linkToAdjacencyArray.size()-1)
                upper = linkToAdjacencyArray.get(sourceVertexIndex+1);
            else
                upper = lastIndexOfAdjacencyArray;
            for (int i = lower; i < upper; i++)
                if(adjacencyArray.get(i)==targetVertexIndex)
                    return weightsPerEdge.get(i);
            return null;
    }
    
    public static void main(String[] args) {
        
        WeightedDirectedGraphAsAdjacencyArray<String, Double> test =
                                    new WeightedDirectedGraphAsAdjacencyArray<String, Double>();
        
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
        
        test.setEdgeWeight(new Pair<String,String>("A", "B"), 5.);
        test.setEdgeWeight(new Pair<String,String>("A", "D"), 5.);
        test.setEdgeWeight(new Pair<String,String>("B", "C"), 5.);
        test.setEdgeWeight(new Pair<String,String>("B", "D"), 5.);
        test.setEdgeWeight(new Pair<String,String>("B", "E"), 5.);
        test.setEdgeWeight(new Pair<String,String>("C", "E"), 5.);
        test.setEdgeWeight(new Pair<String,String>("D", "E"), 5.);
        test.setEdgeWeight(new Pair<String,String>("D", "F"), 5.);
        test.setEdgeWeight(new Pair<String,String>("E", "F"), 5.);
        test.setEdgeWeight(new Pair<String,String>("E", "G"), 5.);
        test.setEdgeWeight(new Pair<String,String>("F", "G"), 5.);
        
        System.out.println(test.getEdgeWeight(new Pair<String,String>("A", "B")));
        System.out.println(test.linkToAdjacencyArray);
        System.out.println(test.adjacencyArray);
        System.out.println(test.edgeSet());
        
    }
}