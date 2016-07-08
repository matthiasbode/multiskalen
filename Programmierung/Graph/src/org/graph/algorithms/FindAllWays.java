/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms;

import java.util.HashSet;
import java.util.Stack;
import org.graph.Path;
import org.graph.directed.DefaultDirectedGraph;
import org.graph.directed.RootedTree;

/**
 *
 * @author bode
 */
public final class FindAllWays {

    private FindAllWays() {
    }

    /**
     * Methode zum Erzeugen eines Tiefensuchbaums.
     *
     * @param root Wurzel des Suchbaums
     * @return {@link RootedTree} fuer den Knoten <code>root</code>
     */
    public static <V> HashSet<Path<V>> computeDepthSearchTree(DefaultDirectedGraph<V> graph, V root, V end) {

        HashSet<Path<V>> paths = new HashSet<Path<V>>();

        Stack<Path<V>> candidates = new Stack<Path<V>>();
        candidates.push(new Path<V>(root));

        while (!candidates.isEmpty()) {
            Path<V> k = candidates.pop();
            if (k.getLastVertex().equals(end)) {
                paths.add(k);
            }
            for (V n : graph.getSuccessors(k.getLastVertex())) {
                Path<V> kNew = new Path<>(k);
                kNew.appendVertex(n);
                candidates.push(kNew);
            }
        }

        return paths;
    }
    
    public static void main(String[] args) {
        DefaultDirectedGraph<Integer> graph = new DefaultDirectedGraph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);
        
        graph.addEdge(4,1);
        graph.addEdge(4,5);
        graph.addEdge(4,6);
        graph.addEdge(1,2);
        graph.addEdge(1,5);
        graph.addEdge(2,3);
        graph.addEdge(2,6);
        graph.addEdge(3,6);
        graph.addEdge(5,3);
        graph.addEdge(5,6);
        
        HashSet<Path<Integer>> computeDepthSearchTree = FindAllWays.computeDepthSearchTree(graph, 4, 6);
        for (Path<Integer> path : computeDepthSearchTree) {
            System.out.println(path);
        }
    }
}

