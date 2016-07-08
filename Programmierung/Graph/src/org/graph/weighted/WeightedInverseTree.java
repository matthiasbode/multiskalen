/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

import java.util.HashMap;
import java.util.NoSuchElementException;
import org.graph.directed.InverseTree;
import org.util.Pair;

/**
 *
 * @author Oliver
 */
public class WeightedInverseTree<V,W extends EdgeWeight<W>> extends InverseTree<V> implements WeightedDirectedGraph<V, W>, VertexWeight<V, W> {

    private HashMap<V, W> vertexWeights;
    private HashMap<Pair<V,V>, W> edgeWeights;    
    
    public WeightedInverseTree(V bottom) {
        super(bottom);
        
        vertexWeights = new HashMap<V, W>();
        edgeWeights = new HashMap<Pair<V, V>, W>();
    }   
    
    public WeightedDirectedGraph<V, W> dual() {
        WeightedDirectedGraph<V,W> dual =
                                       new DefaultWeightedDirectedGraph<V, W>();
        for (V v : vertexSet()) {
            dual.addVertex(v);
        }
        for (Pair<V, V> edge : edgeSet()) {
            dual.addEdge(edge.transposition());
            dual.setEdgeWeight(edge.transposition(), getEdgeWeight(edge));
        }
        return dual;
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, W weight) {
        return this.addEdge(new Pair<V, V>(sourceVertex, targetVertex), weight);
    }

    @Override
    public boolean addEdge(Pair<V, V> edge, W weight) {
        return this.addParent(edge.getFirst(), edge.getSecond(), weight);
    }
    
    public boolean addParent(V parent, V child, W weight) {
        boolean added = super.addParent(parent, child);
        if(added) {
            setEdgeWeight(new Pair<V,V>(parent, child), weight);
            vertexWeights.put(parent, getVertexWeight(child).product(weight));            
        }
        return added;
    }        

    @Override
    public void setEdgeWeight(Pair<V, V> e, W weight) {
        try {
            if(edgeWeights.containsKey(e))
                edgeWeights.remove(e);
            edgeWeights.put(e, weight);
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    @Override
    public W getEdgeWeight(Pair<V, V> e) {
        return edgeWeights.get(e);
    }

    @Override
    public W getEdgeWeight(V startVertex, V targetVertex) {
        return getEdgeWeight(new Pair<V, V>(startVertex, targetVertex));
    }

    @Override
    public void setVertexWeight(V vertex, W weight) {
        try {
            if(vertexWeights.containsKey(vertex))
                vertexWeights.remove(vertex);
            vertexWeights.put(vertex, weight);
        } catch (NoSuchElementException ex) {
            throw ex;
        }        
    }

    @Override
    public W getVertexWeight(V vertex) {
        return vertexWeights.get(vertex);
    }
    
    @Override
    public String toString() {
        String str = "";
        for(Pair<V,V> edge: this.edgeSet()) {
            str += edge.toString() + " --> " + this.getEdgeWeight(edge)+", ";
        }
        return str;
    }
    
    public WeightedPath<V,W> getWeightedPath(V start, V end) {
        if(containsVertex(start) && containsVertex(end)) {
            if(isPredecessor(start, end)) {
                WeightedPath<V,W> path = new WeightedPath<V,W>(getEdgeWeight(start, getChild(start)),start);
                V child = this.getChild(start);
                while(!child.equals(end)) {
                    path.appendVertex(child);
                    path.setWeight(getEdgeWeight(child, getChild(child)).product(path.getWeight()));
                    child = getChild(child);
                }
                path.appendVertex(child);
                return path;
            }
            if(start.equals(end))
                return new WeightedPath<V,W>(getVertexWeight(start).getNullElement(),start);
            throw new NullPointerException("The start vertex is not a " +
                    "predecessor of the end vertex.");            
        }
        throw new NullPointerException("Start or end vertex is not a "+
                "vertex of this graph.");
    }    
    
//    private boolean isPredecessor(V pre, V suc) {
//        if(pre.equals(getBottom()))
//            return false;
//        if(suc.equals(getBottom()))
//            return true;
//        if(containsVertex(pre) && containsVertex(suc)) {
//            V child = getSuccessors(pre).iterator().next();
//            while(!child.equals(getBottom())) {
//                if(child.equals(suc))
//                    return true;
//                child = getSuccessors(child).iterator().next();
//            }
//            return false;
//        }
//        else return false;
//    }    
    
}
