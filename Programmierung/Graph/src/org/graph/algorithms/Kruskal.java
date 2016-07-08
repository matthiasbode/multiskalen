/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms;


import java.util.PriorityQueue;
import org.graph.undirected.SimpleGraph;
import org.graph.undirected.Tree;
import org.graph.weighted.DefaultWeightedSimpleGraph;
import org.util.Pair;
import org.util.EdgeComparator;
import org.util.UnionFind;

/**
 *
 * @author rinke
 */
public final class Kruskal {

    private Kruskal() {
    }
    
    /**
     * Calculates the minimum spanning tree with kruskal's algorithm. First, all
     * edges are listed in ascending order. Then all edges are analysed by a
     * union-find-structure, if an edge is part of the minimum spanning tree or
     * not.
     * <p>
     * For now, the result type is a tree and not a weighted tree.
     * 
     * @param <V>
     * @param graph
     * @return 
     */
    public static <V> Tree<V> getSpanningTree(DefaultWeightedSimpleGraph<V, Double> graph) {
        PriorityQueue<Pair<V,V>> edgesSortedByWeight =
                new PriorityQueue<Pair<V,V>>(graph.numberOfEdges(),
                                        new EdgeComparator<V>(graph));
        for (Pair<V, V> edge : graph.edgeSet())
            edgesSortedByWeight.offer(edge);
        
        /*Union-Find-Structure for deciding, if an edge is part of the spanning
          tree or not
         */
        UnionFind<V> unionFind = new UnionFind<V>(graph.vertexSet());
        
        //temporarily graph for all tree edges
        SimpleGraph<V> spanningTree = new SimpleGraph<V>();
        for (V v : graph.vertexSet())
            spanningTree.addVertex(v);


        while (!edgesSortedByWeight.isEmpty()) {
            Pair<V,V> candidate = edgesSortedByWeight.poll();
            if(!unionFind.find(candidate.getFirst()).equals(unionFind.find(candidate.getSecond()))) {
                spanningTree.addEdge(candidate);
                unionFind.union(candidate.getFirst(), candidate.getSecond());
            }
        }
        
        //the final tree will be computed by breadthfirstsearch
        for (V v : spanningTree.vertexSet()) {
            if(spanningTree.degreeOf(v) == 1)
                return BreadthFirstSearch.computeBreadthSearchTree(spanningTree, v);
        }
        return null;
    }
    
    /**example taken from
     * <a href="http://de.wikipedia.org/wiki/Algorithmus_von_Kruskal">
     * http://de.wikipedia.org/wiki/Algorithmus_von_Kruskal</a>.
     */
    public static void main(String[] args) {
        
        DefaultWeightedSimpleGraph<String, Double> test = new DefaultWeightedSimpleGraph<String, Double>();
        test.addVertex("A");
        test.addVertex("B");
        test.addVertex("C");
        test.addVertex("D");
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
        test.setEdgeWeight(new Pair("A","B"), 7.);
        test.setEdgeWeight(new Pair("A","D"), 5.);
        test.setEdgeWeight(new Pair("B","C"), 8.);
        test.setEdgeWeight(new Pair("B","D"), 9.);
        test.setEdgeWeight(new Pair("B","E"), 7.);
        test.setEdgeWeight(new Pair("C","E"), 5.);
        test.setEdgeWeight(new Pair("D","E"), 15.);
        test.setEdgeWeight(new Pair("D","F"), 6.);
        test.setEdgeWeight(new Pair("E","F"), 8.);
        test.setEdgeWeight(new Pair("E","G"), 9.);
        test.setEdgeWeight(new Pair("F","G"), 11.);
        System.out.println(test);
        System.out.println(Kruskal.getSpanningTree(test));
    }
}
