/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms;

import java.util.HashSet;
import java.util.Stack;
import org.graph.Path;
import org.graph.directed.DirectedGraph;
import org.graph.directed.DirectedGraphAsAdjacencyArray;
import org.graph.directed.RootedTree;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author rinke
 */
public final class StructureAnalysis {

    private StructureAnalysis() {
    }

    public static <V> boolean isAcyclic(DirectedGraph<V> graph) {
        if(graph.vertexSet().size() == 1)
            return true;
        
        HashSet<V> rootVertices = new HashSet<V>();
        for (V v : graph.vertexSet()) {
            if(graph.inDegreeOf(v) == 0)
                rootVertices.add(v);
        }
        if(rootVertices.isEmpty())
            return false;
        for (V root : rootVertices) {
            RootedTree<V> tree = new RootedTree<V>(root);

            Stack<V> candidates = new Stack<V>();
            candidates.push(root);

            while (!candidates.isEmpty()) {
                V k = candidates.pop();
                for (V n : graph.getSuccessors(k)) {
                    if (!tree.containsVertex(n)) {
                        candidates.push(n);
                        tree.addChild(n, k);
                    } else if(tree.isDescendant(k, n))
                            return false;
                }
            }
        }
        return true;
    }
    
    public static <V> Path<V> getCycle(DirectedGraph<V> graph) {
        if(graph.vertexSet().size() == 1)
            return null;
        
        
        for (V root : graph.vertexSet()) {
            RootedTree<V> tree = new RootedTree<V>(root);

            Stack<V> candidates = new Stack<V>();
            candidates.push(root);

            while (!candidates.isEmpty()) {
                V k = candidates.pop();
                for (V n : graph.getSuccessors(k)) {
                    if (!tree.containsVertex(n)) {
                        candidates.push(n);
                        tree.addChild(n, k);
                    } else if(tree.isDescendant(k, n)) {
                        Path<V> path = tree.getPath(n, k);
                        path.appendVertex(n);
                        return path;
                    }
                }
            }
        }
        return null;
    }
    
    
    
    public static void main(String[] args) {
        DirectedGraphAsAdjacencyArray<Integer> graph =
                                   new DirectedGraphAsAdjacencyArray<Integer>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addEdge(1, 2);
        
        System.out.println(isAcyclic(graph));
        
        graph.addEdge(2, 1);
        
        System.out.println(isAcyclic(graph));
        
        graph.addVertex(3);
        graph.addEdge(3, 1);
        
        System.out.println(isAcyclic(graph));
        System.out.println(getCycle(graph));
        
        graph.removeEdge(2, 1);
        
        System.out.println(isAcyclic(graph));

    }
}
