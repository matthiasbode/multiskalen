package org.graph.weighted;

import org.util.Pair;
import org.graph.Path;

/**
 * A WeightedPath is a path, which can be weigthed, e.g. with the length of the
 * path.
 *
 * @author Nils Rinke
 */
public class WeightedPath<E, W extends EdgeWeight<W>> extends Path<E>{
    /**
     * weight of this path
     */
    protected W weight;


    /**
     * Constructs an empty weighted path.
     */
    public WeightedPath() {
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
    public WeightedPath(W weight, E... vertices) {
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
    public WeightedPath(W weight, Path<E> path) {
        super(path);
        this.weight = weight;
    }
    
    public void appendPath(WeightedPath<E,W> path) {
        for(Pair<E,E> edge: path.getPathEdges()) {            
            appendEdge(edge);
        }              
        if(path.getWeight().doubleValue()!=path.getWeight().getNullElement().doubleValue())
            setWeight(weight.product(path.getWeight()));
    }


    /**
     *
     * @return
     */
    public W getWeight() {
        return weight;
    }


    /**
     * 
     * @param weight
     */
    public void setWeight(W weight) {
        this.weight = weight;
    }


    /**
     *
     * @param edge
     * @param edgeWeight
     * @return
     */
    public boolean appendEdge(Pair<E, E> edge, W edgeWeight) {
        boolean append = super.appendEdge(edge);
        if(append){
            if(pathList.size()==1)
                weight = edgeWeight;
            else
                weight = weight.product(edgeWeight);
        }
        return append;
    }

    public void appendEdgeInFront(Pair<E, E> edge, W edgeWeight){
        super.appendEdgeInFront(edge);
        if(pathList.size()==1)
            weight = edgeWeight;
        else
            weight = weight.product(edgeWeight);
    }

    @Override
    public WeightedPath<E, W> clone() {
        WeightedPath<E, W> clone = new WeightedPath(weight, this);
        return clone;
    }




    /**
     *
     * @param path
     * @return
     */
    public WeightedPath<E,W> product(WeightedPath<E,W> path) {
        WeightedPath<E,W> productPath = new WeightedPath<E,W>(
                                  weight, super.product(path));
        if(weight==null)
            productPath.weight = path.weight;
        else
            productPath.weight = weight.product(path.weight);
        return productPath;
    }

    public WeightedPath<E, W> productInFront(WeightedPath<E, W> path){
        if(!pathList.isEmpty()){
            if(this.getStartVertex().equals(path.getEndVertex())) {
                for (Pair<E,E> edge : pathList){
                    path.pathList.add(edge);
                }
            }
            else{
                System.out.println("path: "+this);
                System.out.println("path, der davor soll: "+path);
                throw new IllegalArgumentException("Last vertex of this path has to be"+
                             " first vertex of the second path!");
            }
        path.weight = weight.product(path.weight);
        }
        return path;
    }


    /**
     * @inheritDoc
     * @return
     */
    @Override
    public WeightedPath<E,W> invertPath() {
        return new WeightedPath<E,W>(weight, super.invertPath());
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WeightedPath<E, W> other = (WeightedPath<E, W>) obj;
        if (this.weight != other.weight && (this.weight == null || !this.weight.equals(other.weight))) {
            return false;
        }
        return super.equals(other);
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.weight != null ? this.weight.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        String message = "";
        if(getNumberOfVertices()==1)
            message += start;
        else
            for (Pair<E, E> e : pathList) {
                message += e.toString();
            }
        return "<" + message + "> --> "+getWeight().toString();
    }    
}