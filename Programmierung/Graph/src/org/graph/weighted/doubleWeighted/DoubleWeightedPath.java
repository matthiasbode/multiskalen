package org.graph.weighted.doubleWeighted;

import org.graph.weighted.*;
import org.util.Pair;
import org.graph.Path;

/**
 * A DoubleWeightedPath is a path, which can be weigthed with a double value,
 * e.g. with the length of the path.
 *
 * @author Nils Rinke
 */
public class DoubleWeightedPath<E> extends Path<E>{
    /**
     * weight of this path
     */
    protected double weight;


    /**
     * Constructs an empty weighted path.
     */
    public DoubleWeightedPath() {
        super();
    }

    
    /**
     * Constructs a path containing the nodes of the specified
     * array with the specified weight.
     *
     * @param weight the weight of this path.
     * @param vertices the array whose elements represent the path.
     * @throws NullPointerException if the specified array is null
     */
    public DoubleWeightedPath(double weight, E... vertices) {
        super(vertices);
        this.weight = weight;
    }

    /**
     * Constructs a path containing the edges of the specified
     * list with the specified weight.
     *
     * @param weight the weight of this path.
     * @param pathEdges the list whose edges represent the path.
     * @throws NullPointerException if the specified list is null
     */
    public DoubleWeightedPath(double weight, Path<E> path) {
        super(path);
        this.weight = weight;
    }


    /**
     *
     * @return
     */
    public double getWeight() {
        return weight;
    }


    /**
     * 
     * @param weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }


    /**
     *
     * @param edge
     * @param edgeWeight
     * @return
     */
    public boolean appendEdge(Pair<E, E> edge, double edgeWeight) {
        boolean append = super.appendEdge(edge);
        if(append)
            weight += edgeWeight;
        return append;
    }


    /**
     *
     * @param path
     * @return
     */
    public DoubleWeightedPath<E> product(DoubleWeightedPath<E> path) {
        DoubleWeightedPath<E> productPath = new DoubleWeightedPath<E>(
                                  weight, super.product(path));
        productPath.weight += path.weight;
        return productPath;
    }


    /**
     * @inheritDoc
     * @return
     */
    @Override
    public DoubleWeightedPath<E> invertPath() {
        return new DoubleWeightedPath<E>(weight, super.invertPath());
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DoubleWeightedPath<E> other = (DoubleWeightedPath<E>) obj;
        if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
            return false;
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
        return hash;
    }
}