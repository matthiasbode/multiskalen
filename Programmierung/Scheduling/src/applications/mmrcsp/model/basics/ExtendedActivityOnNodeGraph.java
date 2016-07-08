/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import math.FieldElement;
import org.util.Pair;

/**
 * Beschreibt neben der Knotenmenge der Vorgängerbeziehungen (technologische
 * Vorschriften) in einer weiteren Kantenmenge weitere Beziehungen, die nach der
 * Einplanung auf einer Maschine gegeben sind.
 *
 * @author bode
 * @param <E>
 */
public class ExtendedActivityOnNodeGraph<E extends Operation> extends ActivityOnNodeGraph<E> {

    HashMap<Resource, Set<Pair<E, E>>> disjunctiveEdges = new HashMap<>();

    public ExtendedActivityOnNodeGraph(ActivityOnNodeGraph<E> aon) {
        super(aon);
    }

    public boolean addDisjunctiveEdge(Resource r, Pair<E, E> edge, FieldElement weight) {
        Set<Pair<E, E>> rSet = this.disjunctiveEdges.get(r);
        if (rSet == null) {
            rSet = new HashSet<>();
            disjunctiveEdges.put(r, rSet);
        }
        rSet.add(edge);
        return super.addEdge(edge, weight);
    }

    public HashMap<Resource, Set<Pair<E, E>>> getDisjunctiveEdges() {
        return disjunctiveEdges;
    }
    
    public boolean isDisjunctiveConnected(Pair<E, E> edge){
        for (Resource resource : disjunctiveEdges.keySet()) {
            if(disjunctiveEdges.get(resource).contains(edge))
                return true;
        }
        return false;
    }
    
    

}
