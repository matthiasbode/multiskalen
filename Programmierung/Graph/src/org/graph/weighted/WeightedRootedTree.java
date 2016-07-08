package org.graph.weighted;

import org.graph.directed.RootedTree;
import org.util.Pair;
import java.util.HashMap;
import java.util.NoSuchElementException;
import org.graph.weighted.VertexWeight;
import org.graph.weighted.WeightedDirectedGraph;


/**
 * A tree is a weakly connected acyclic Graph. In a tree any two vertices are
 * connected by exactly one simple path.
 * Mathematical a tree is defined by a Graph G := (V, E), where |E| = |V|-1
 * <p>
 * To ensure, that the definition of a tree is not hurt, the method
 * <tt>addVertex(V vertex)</tt>, inherited from the <tt>Graph</tt>-class hasn't
 * any effect.
 * <p>
 * The method <tt>addEdge(V first, V second)</tt> calls the
 * <tt>addChild(V child, V parent)</tt> method.
 *
 * @author Nils Rinke
 */
public class WeightedRootedTree<V,W extends EdgeWeight<W>> extends RootedTree<V> implements WeightedDirectedGraph<V, W>, VertexWeight<V, W> {

    private HashMap<V, W> vertexWeights;
    private HashMap<Pair<V,V>, W> edgeWeights;
    
    public WeightedRootedTree(V root) {
        super(root);

        this.vertexWeights = new HashMap<V, W>();
        this.edgeWeights = new HashMap<Pair<V, V>, W>();
    }
    
    public WeightedRootedTree(WeightedRootedTree<V,W> tree) {
        super(tree.getRoot());
        this.vertexWeights = new HashMap<V, W>();
        this.edgeWeights = new HashMap<Pair<V, V>, W>();
        setVertexWeight(tree.getRoot(), tree.getVertexWeight(tree.getRoot()));        
        addIterative(tree, tree.getRoot());
    }        
    
    private void addIterative(WeightedRootedTree<V,W> tree, V vertex) {
        for(V suc: tree.getSuccessors(vertex)) {
            addChild(suc, vertex, tree.getEdgeWeight(vertex, suc));
            addIterative(tree, suc);
        }
    }

    

    /**
     * Gets the path between the specified child to the specified ancestor.
     *
     * @param child the new node to be added to the tree
     * @param ancestor the ancestor node of the child
     * @param the Path from the ancestor to the child
     */
    public WeightedPath<V,W> getWeightedPath(V ancestor, V child) {
        WeightedPath<V, W> weightedPath =
                new WeightedPath<V, W>(null, getPath(ancestor, child));
        W weight;
        if(ancestor.equals(child))
            weight = getEdgeWeight(edges.iterator().next()).getEinsElement();
        else
            weight = getEdgeWeight(weightedPath.get(0));
        for (int i = 1; i < weightedPath.getNumberOfEdges(); i++) {
            weight = weight.product(getEdgeWeight(weightedPath.get(i)));
        }
        weightedPath.setWeight(weight);
        return weightedPath;
    }


    /**
     * 
     * @param child
     * @param parent
     * @param edgeWeight
     */
    public boolean  addChild(V child, V parent, W edgeWeight) {
        boolean added = addChild(child, parent);
        if(added) {
            setEdgeWeight(parent, child, edgeWeight);
            vertexWeights.put(child, getVertexWeight(parent).product(edgeWeight));
        }
        return added;
    }

    
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, W weight) {
        return addChild(targetVertex, sourceVertex, weight);
    }
    
    @Override
    public boolean addEdge(Pair<V, V> edge, W weight) {
        return addChild(edge.getSecond(), edge.getFirst(), weight);
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
        try {
            if(vertexWeights.containsKey(vertex))
                vertexWeights.remove(vertex);
            vertexWeights.put(vertex, weight);
        } catch (NoSuchElementException ex) {
            throw ex;
        }
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
    @Override
    public W getEdgeWeight(V first, V second) {
//        for (Edge<V, V> edge : edges) {
//            if(edge.getFirst().equals(first) && edge.getSecond().equals(second)){
//                return edgeWeights.get(edge);
//            }
//        }
//        return null;
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
//        for (Edge<V, V> edge : edges) {
//            if(edge.getFirst().equals(first) && edge.getSecond().equals(second)){
//                edgeWeights.put(edge, weight);
//            }
//        }
        setEdgeWeight(new Pair<V, V>(first, second), weight);
    }


    @Override
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
    public String toString() {
        String str = "";
        for(Pair<V,V> edge: this.edgeSet()) {
            str += edge.toString() + " --> " + this.getEdgeWeight(edge)+", ";
        }
        return str;
    }
}