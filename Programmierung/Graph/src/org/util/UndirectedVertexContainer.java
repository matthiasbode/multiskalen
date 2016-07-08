package org.util;


import java.util.HashSet;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public class UndirectedVertexContainer<V> {

    private static int counter;

    private final int id;

    public final HashSet<V> neighbours;

    public UndirectedVertexContainer() {
        id = counter++;
        neighbours = new HashSet<V>();
    }
    

    public boolean addNeighbour(V vertex) {
        return neighbours.add(vertex);
    }

    public HashSet<V> getNeighbours() {
        return neighbours;
    }
    
    
    public int getDegree() {
        return neighbours.size();
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(obj == this)
            return true;
        if(obj.getClass() != this.getClass())
            return false;
        UndirectedVertexContainer container = (UndirectedVertexContainer) obj;
        if(id == container.id)
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        return hash;
    } 
}