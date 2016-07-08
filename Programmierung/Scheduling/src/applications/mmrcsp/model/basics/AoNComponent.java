/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics;

import applications.mmrcsp.model.operations.Operation;
import java.util.Collection;
import java.util.LinkedHashMap;
import math.FieldElement;
import org.util.Pair;

/**
 *
 * @author Matthias
 */
public class AoNComponent<E extends Operation> extends ActivityOnNodeGraph<E> {

    private static int counter = 0;
    private final ActivityOnNodeGraph<E> superGraph;
    private final int number;

    public AoNComponent(ActivityOnNodeGraph<E> superGraph) {
        this.superGraph = superGraph;
        this.number = counter++;
    }

    public AoNComponent(ActivityOnNodeGraph<E> superGraph, Collection<E> verticies, Collection<Pair<E, E>> edges, LinkedHashMap<Pair<E, E>, FieldElement> edgeWeights) {
        super(verticies, edges, edgeWeights);
        this.superGraph = superGraph;
          this.number = counter++;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.number;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AoNComponent<?> other = (AoNComponent<?>) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }
    
    

}
