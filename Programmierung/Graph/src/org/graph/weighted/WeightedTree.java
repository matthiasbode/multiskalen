package org.graph.weighted;

import org.graph.weighted.*;
import java.util.HashMap;
import java.util.NoSuchElementException;
import org.graph.undirected.Tree;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class WeightedTree<V, W extends EdgeWeight<W>> extends Tree<V>
                        implements WeightedUndirectedGraph<V,W>, VertexWeight<V, W> {

    private final HashMap<V, W> vertexWeights;
    private final HashMap<Pair<V,V>, W> edgeWeights;
    
    public WeightedTree(V root) {
        super(root);

        this.vertexWeights = new HashMap<V, W>();
        this.edgeWeights = new HashMap<Pair<V, V>, W>();
    }

    



    /**
     * 
     * @param child
     * @param parent
     * @param edgeWeight
     */
    public boolean addConnection(V child, V parent, W edgeWeight) {
        boolean add = addConnection(child, parent);
        if(add) {
            setEdgeWeight(parent, child, edgeWeight);
            vertexWeights.put(child, getVertexWeight(parent).product(edgeWeight));
        }
        return add;
    }


    /** Gets the weight with the specified index of the specified WeightedNode.
     *
     *  @param vertex  The WeightedNode has to be a member of this graph.
     *  @param index The index discribe the position of the weight.
     *
     *  @throws      java.util.NoSuchElementException The method throws this
     *               exception if the specified WeightedNode doesn't exist in this graph.
     *  @throws      java.lang.IndexOutOfBoundsException The method throws
     *               this exception if the specified index is negative or larger
     *               than the number of weights of each WeightedNode.
     */
    @Override
    public W getVertexWeight(V vertex) {
        return vertexWeights.get(vertex);
    }
    

    @Override
    public boolean removeVertex(V vertex) {
        boolean removed = super.removeVertex(vertex);
        if(removed)
            vertexWeights.remove(vertex);
        return removed;
    }

   
    /** Sets the weight with the specified index of the specified node.
     *
     *  @param vertex   The WeightedNode has to be a memeber of this graph.
     *  @param index  The index discribe the position of the weight.
     *  @param weight The weight of the node.
     *
     *  @throws       java.util.NoSuchElementException The method throws this
     *                exception if the specified WeightedNode doesn't exist in this graph.
     *  @throws       java.lang.IndexOutOfBoundsException The method throws
     *                this exception if the specified index is negative or larger
     *                than the number of weights of each WeightedNode.
     */
    @Override
    public void setVertexWeight(V vertex, W weight) {
        vertexWeights.put(vertex, weight);
    }


    /** Gets the weight with the specified index of the specified edge.
     *
     *
     *  @param edge  The edge has to be a memeber of this graph.
     *  @param index The index discribe the position of the weight.
     *
     *  @throws      java.util.NoSuchElementException The method throws this
     *               exception if the specified edge doesn't exist in this graph.
     *  @throws      java.lang.IndexOutOfBoundsException The method throws
     *               this exception if the specified index is negative or larger
     *               than the number of weights of each edge.
     */
    @Override
    public W getEdgeWeight(Pair<V,V> edge) {
        return edgeWeights.get(edge);
    }


    /** Gets the weight with the specified index of the edge with the specified
     *  elements.
     *
     *  @param child  This object is the first element of the edge (ordered pair).
     *  @param parent This object is the second element of the edge (ordered pair).
     *
     *  @throws       java.util.NoSuchElementException The method throws this
     *                exception if the specified edge doesn't exist in this graph.
     *  @throws       java.lang.IndexOutOfBoundsException The method throws
     *                this exception if the specified index is negative or larger
     *                than the number of weights of each edge.
     */
    public W getEdgeWeight(V first, V second) {
        return getEdgeWeight(new Pair<V, V>(first, second));
    }


    /** Sets the weight with the specified index of the specified edge.
     *
     *
     *  @param edge   The edge has to be a memeber of this graph.
     *  @param index  The index discribe the position of the weight.
     *  @param weight The weight of the edge.
     *
     *  @throws java.util.NoSuchElementException The method throws this
     *          exception if the specified edge doesn't exist in this graph.
     */
    @Override
    public void setEdgeWeight(Pair<V,V> edge, W weight) {
        try {
            if(edgeWeights.containsKey(edge))
                edgeWeights.remove(edge);
            edgeWeights.put(edge, weight);
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }


    /** Sets the weight with the specified index of the edge with the specified
     *  elements.
     *
     *
     *  @param child  This object is the first element of the edge (ordered pair).
     *  @param parent This object is the second element of the edge (ordered pair).
     *  @param weight The weight of the edge.
     *
     *  @throws       java.util.NoSuchElementException The method throws this
     *                exception if the specified edge doesn't exist in this graph.
     *  @throws       java.lang.IndexOutOfBoundsException The method throws
     *                this exception if the specified index is negative or larger
     *                than the number of weights of each edge.
     */
    public void setEdgeWeight(V first, V second, W weight) {
        setEdgeWeight(new Pair<V, V>(first, second), weight);
    }
}