/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics.util;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.HashMap;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import math.Field;
import math.FieldElement;
import org.graph.algorithms.TopologicalSort;
import org.graph.directed.DefaultDirectedGraph;

/**
 *
 * @author bode
 */
public class EALOSAEBuilder {
    
    public static void updateEaloses(Operation scheduledOperation, FieldElement duration, FieldElement start,
            Map<? extends Operation, EarliestAndLatestStartsAndEnds> e, ActivityOnNodeGraph<? extends Operation> g) {
        
        ActivityOnNodeGraph<Operation> graph = (ActivityOnNodeGraph<Operation>) g;
        Map< Operation, EarliestAndLatestStartsAndEnds> ealosaes = (Map< Operation, EarliestAndLatestStartsAndEnds>) e;
        
        EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(scheduledOperation);
        FieldElement end = start.add(duration);
        ealosae.setEarliest(start, end);
        ealosae.setLatest(start, end);
        
        LinkedHashSet<Operation> successors = graph.getSuccessors(scheduledOperation);
        for (Operation suc : successors) {
            EarliestAndLatestStartsAndEnds current = ealosaes.get(suc);
            FieldElement currentStart = Field.max(current.getEarliestStart(), end);
            if(current.getEarliestEnd() instanceof DiscretizedFuzzyInterval){
                currentStart = DiscretizedFuzzyInterval.max((DiscretizedFuzzyInterval)current.getEarliestStart(), (DiscretizedFuzzyInterval)end);
            }
            current.setEarliest(currentStart, currentStart.add(suc.getDuration()));
            if (!current.testValues()) {
                current.setDNF(true);
            }
        }
    }

    /**
     * Gibt für einen Graphen bzw. eine Zusammenhangskomponente die frühsten und
     * spätesten Operationen Starts und Enden an
     *
     * @param res StartEalosaes, gibt die Zeitfenster für die Operationen vor,
     * die laut Problemstellung gegeben sind.
     * @param component Graph, für den die EALOSAEs bestimmt werden sollen.
     * @param topoSort Topologische Sortierung des Graphens.
     * @return
     */
    public static <E extends Operation> Map<E, EarliestAndLatestStartsAndEnds> ealosaes(Map<E, EarliestAndLatestStartsAndEnds> res, DefaultDirectedGraph<E> component, List<Set<E>> topoSort) {

        /**
         * Für jede Knotenklasse aufgrund der Vorgänger die Earliest-Starts
         * bestimmen, erste Knotenklasse hat keine Vorgänger.
         */
        for (int i = 0; i < topoSort.size() - 1; i++) {
            Set<E> verticesInNodeClass = topoSort.get(i);
            for (E routingTransportOperation : verticesInNodeClass) {
                
                EarliestAndLatestStartsAndEnds currentEALOSAE = res.get(routingTransportOperation).clone();
                if (!currentEALOSAE.isScheduled()) {
                    /**
                     * Bestimme das maximale EarliestEnd der Vorgänger
                     */
                    LinkedHashSet<E> sucs = component.getSuccessors(routingTransportOperation);
                    for (E suc : sucs) {
                        FieldElement earliestStartSuc = res.get(suc).getEarliestStart();
                        if (currentEALOSAE.getEarliestEnd().isGreaterThan(earliestStartSuc)) {
                            res.get(suc).setEarliest(currentEALOSAE.getEarliestEnd(), currentEALOSAE.getEarliestEnd().add(suc.getDuration()));
                        }
                    }
                    /**
                     * Test und ggf. Listeneraufruf.
                     */
                    if (!currentEALOSAE.testValues()) {
                        return null;
                    }
                }
                
            }
        }

        /**
         * Für jede Knotenklasse aufgrund der Nachfolger die Latest-Starts
         * bestimmen, letzte Knotenklasse hat keine Nachfolger.
         */
        for (int i = topoSort.size() - 1; i >= 0; i--) {
            Set<E> verticesInNodeClass = topoSort.get(i);
            for (E routingTransportOperation : verticesInNodeClass) {
                
                EarliestAndLatestStartsAndEnds currentEALOSAE = res.get(routingTransportOperation).clone();
                if (!currentEALOSAE.isScheduled()) {
                    LinkedHashSet<E> preds = component.getPredecessors(routingTransportOperation);
                    
                    for (E pred : preds) {
                        if (currentEALOSAE.getLatestStart().isLowerThan(res.get(pred).getLatestEnd())) {
                            res.get(pred).setLatest(currentEALOSAE.getLatestStart().sub(pred.getDuration()), currentEALOSAE.getLatestStart());
                        }
                    }
                    /**
                     * Test, und ggf. Listeneraufruf.
                     */
                    if (!currentEALOSAE.testValues()) {
                        return null;
                    }
                }
                
            }
        }
        return res;
    }

    /**
     * Sortiert die Operationen zuvor topologisch.
     *
     * @param <E> Datentyp der Operationen
     * @param graph Graph
     * @param res Initial-Ealosaes laut Problemdefinition
     * @return
     */
    public static <E extends Operation> Map<E, EarliestAndLatestStartsAndEnds> ealosaes(DefaultDirectedGraph<E> graph, Map<E, EarliestAndLatestStartsAndEnds> res) {
        try {
            List<Set<E>> topoSort = TopologicalSort.topologicalSort(graph);
            return ealosaes(res, graph, topoSort);
        } catch (java.lang.IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static <E extends Operation> Map<E, EarliestAndLatestStartsAndEnds> ealosaes(DefaultDirectedGraph<E> graph, TimeSlot t) {
        Map<E, EarliestAndLatestStartsAndEnds> res = new HashMap<>();
        for (E e : graph.vertexSet()) {
            EarliestAndLatestStartsAndEnds ealosae = new EarliestAndLatestStartsAndEnds(e.getDuration());
            ealosae.setEarliest(t.getFromWhen(), t.getFromWhen().add(e.getDuration()));
            ealosae.setLatest(t.getUntilWhen(), t.getUntilWhen().add(e.getDuration()));
            res.put(e, ealosae);
        }
        return ealosaes(graph, res);
    }
}
