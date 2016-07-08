package org.graph.algorithms.pathsearch.doubleWeighted;

import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.doubleWeighted.DoubleWeightedRootedTree;
import org.graph.weighted.doubleWeighted.DoubleWeightedTree;
import org.graph.weighted.WeightedRootedTree;
import org.graph.weighted.WeightedTree;


/**
 *
 * @author nilsrinke
 */
public interface SingleShortestPathAlgorithm<V,W extends EdgeWeight<W>>
                    extends PairShortestPathAlgorithm<V, W>{

    /**
     * Sucht einen Weg im bewerteten schlichten Graph graph
     * zwischen dem Knoten start und dem Knoten end.
     *
     * @param graph bewerteter schlichter Graph.
     * @param source Start-Knoten.
     * @param destination End-Knoten.
     * @return Kantenzug eines Weges als bewerteter schlichter Graph.
     */
    public DoubleWeightedRootedTree<V> singleSourceShortestPath(WeightedDirectedGraph<V,Double> graph,
                                                      V source);

    /**
     * Sucht einen Weg im bewerteten schlichten Graph graph
     * zwischen dem Knoten start und dem Knoten end.
     *
     * @param graph bewerteter schlichter Graph.
     * @param source Start-Knoten.
     * @param destination End-Knoten.
     * @return Kantenzug eines Weges als bewerteter schlichter Graph.
     */
    public DoubleWeightedRootedTree<V> singleDestinationShortestPath(
                                    WeightedDirectedGraph<V,Double> graph, V destination);
    
    
    /**
     * Sucht einen Weg im bewerteten schlichten Graph graph
     * zwischen dem Knoten start und dem Knoten end.
     *
     * @param graph bewerteter schlichter Graph.
     * @param source Start-Knoten.
     * @param destination End-Knoten.
     * @return Kantenzug eines Weges als bewerteter schlichter Graph.
     */
    public WeightedRootedTree<V,W> singleSourceShortestPathGeneric(WeightedDirectedGraph<V,W> graph,
                                                      V source);

    /**
     * Sucht einen Weg im bewerteten schlichten Graph graph
     * zwischen dem Knoten start und dem Knoten end.
     *
     * @param graph bewerteter schlichter Graph.
     * @param source Start-Knoten.
     * @param destination End-Knoten.
     * @return Kantenzug eines Weges als bewerteter schlichter Graph.
     */
    public WeightedRootedTree<V,W> singleDestinationShortestPathGeneric(
                                    WeightedDirectedGraph<V,W> graph, V destination);

}
