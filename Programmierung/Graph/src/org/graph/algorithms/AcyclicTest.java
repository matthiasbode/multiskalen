/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms;

import java.util.HashSet;
import java.util.Stack;
import org.graph.directed.DefaultDirectedGraph;
import org.graph.directed.DirectedGraph;
import org.graph.directed.RootedTree;

/**
 *
 * @author bode
 */
public class AcyclicTest<V> {

    private HashSet<V> visited;
    private DirectedGraph<V> graph;

    public AcyclicTest(DirectedGraph<V> graph) {
        this.graph = graph;
    }

    public boolean isAcyclic() {
        if (graph.vertexSet().size() == 1) {
            return true;
        }

        HashSet<V> rootVertices = new HashSet<V>();
        for (V v : graph.vertexSet()) {
            if (graph.inDegreeOf(v) == 0) {
                rootVertices.add(v);
            }
        }
        if (rootVertices.isEmpty()) {
            return false;
        }
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
                    } else if (tree.isDescendant(k, n)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        DefaultDirectedGraph<Integer> graph = new DefaultDirectedGraph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        AcyclicTest test = new AcyclicTest(graph);
        System.out.println(test.isAcyclic());
    }
}
