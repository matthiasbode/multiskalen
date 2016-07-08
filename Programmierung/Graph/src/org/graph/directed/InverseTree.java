/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.directed;

import java.util.ArrayList;
import java.util.Set;
import org.graph.Path;

/**
 *
 * @author Oliver
 */
public class InverseTree<V> extends DefaultDirectedGraph<V> {
    
    private V bottom;
//    private ArrayList<V> leafs = new ArrayList<V>();
    
    public InverseTree(V bottom) {
        super();
        this.bottom = bottom;
        super.addVertex(bottom);
    }
    
    public boolean addParent(V parent, V child) {
        if(containsVertex(parent))
            throw new IllegalArgumentException("a vertex in an inversetree can't"
                    + " have more then one child");
        if(parent.equals(bottom))
            throw new IllegalArgumentException("a edge to the bottom in an"
                    + " inversetree is not allowed");        
        super.addVertexWithoutCheckContains(parent);
        super.addEdgeWithoutCheckContains(parent, child);
//        leafs.add(parent);
//        leafs.remove(child);
        return true;
    }
    
    @Override
    public boolean addVertex(V vertex) {
        throw new UnsupportedOperationException("Adding a single vertex to a " +
                "inversetree can't garantee the tree definition. Use " +
                "<tt>addParent(E parent, E child)</tt> instead");
    }    
    
    @Override
    public boolean addEdge(V first,V second) {
        return addParent(first, second);
    }
    
    public ArrayList<V> getLeafs() {
        ArrayList<V> leafs = new ArrayList<V>();
        for(V v: vertices) 
            if(getSuccessors(v).isEmpty()) leafs.add(v);        
        return leafs;
    }
    
    public boolean isLeaf(V vertex) {
        return !getSuccessors(vertex).isEmpty();
    }
    
    public V getBottom() {
        return bottom;
    }
    
    public Path<V> getPath(V start, V end) {
        if(containsVertex(start) && containsVertex(end)) {
            if(isPredecessor(start, end)) {
                Path<V> path = new Path<V>(start);
                V child = getChild(start);
                while(!child.equals(end)) {
                    path.appendVertex(child);
                    child = getChild(child);
                }
                path.appendVertex(child);
                return path;
            }
            if(start.equals(end))
                return new Path<V>(start);
            throw new NullPointerException("The start vertex is not a " +
                    "predecessor of the end vertex.");            
        }
        throw new NullPointerException("Start or end vertex is not a "+
                "vertex of this graph.");
    }
    
    public V getChild(V parent) {
        if(parent.equals(bottom))
            return bottom;
        return getSuccessors(parent).iterator().next();
    }
    
    public boolean isPredecessor(V pre, V suc) {
        if(pre.equals(bottom))
            return false;
        if(suc.equals(bottom))
            return true;
        if(containsVertex(pre) && containsVertex(suc)) {
            V child = getSuccessors(pre).iterator().next();
            while(!child.equals(bottom)) {
                if(child.equals(suc))
                    return true;
                child = getSuccessors(child).iterator().next();
            }
            return false;
        }
        else return false;
    }
    
}
