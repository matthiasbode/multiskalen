/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.ga.priority;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.ga.ListIndividualPriorityMapComparator;
import ga.individuals.subList.SubListIndividual;
import ga.individuals.subList.ListIndividual;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bode
 * @param <E>
 */
public class StandardPriorityDeterminator<E extends Operation> implements PriorityDeterminator<E,  ListIndividual<E>> {

    /**
     *
     * @param graph
     * @param indOps
     * @return
     */
    @Override
    public List<E> getPriorites(ActivityOnNodeGraph<E> graph, ListIndividual<E> indOps) {
        int numberOfVertexClasses = indOps.size();
        final HashMap<E, ListIndividualPriorityMapComparator.PriorityItem> priorities = new HashMap<>();

        /**
         * Schleife über alle Knotenklassen.
         */
        for (int vertexClassNumber = 0; vertexClassNumber < numberOfVertexClasses; vertexClassNumber++) {
            /**
             * Aktuelle Knotenklasse
             */
            SubListIndividual<E> vertexClass = indOps.get(vertexClassNumber);
            for (int i = 0; i < vertexClass.size(); i++) {
                E routingTransportOperation = vertexClass.get(i);
                /**
                 * Falls in erster Knotenklasse, keine Vorgänger, die
                 * berücksichtigt werden müssen
                 */
                if (vertexClassNumber == 0) {
                    priorities.put(routingTransportOperation, new ListIndividualPriorityMapComparator.PriorityItem(i, i, vertexClassNumber));
                }/**
                 * Ansonsten bestimme Prio aus Summe von Prio der Liste und
                 * maximum der Vorgänger
                 */
                else {
                    LinkedHashSet<E> predecessors = graph.getPredecessors(routingTransportOperation);
                    int maxPredecessorPriority = 0;
                    for (E pred : predecessors) {
                        ListIndividualPriorityMapComparator.PriorityItem item = priorities.get(pred);
                        if (item == null) {
                            continue;
//                            throw new UnknownError("Keine Proirität für Vorgängerknoten gesetzt!");
                        }
                        Integer predPrio = item.priority;
                        if (predPrio > maxPredecessorPriority) {
                            maxPredecessorPriority = predPrio;
                        }
                    }
                    priorities.put(routingTransportOperation, new ListIndividualPriorityMapComparator.PriorityItem(i + maxPredecessorPriority, i, vertexClassNumber));
                }
            }
        }

        /**
         * Sortieren der Operationen.
         */
        LinkedList<E> operationList = new LinkedList<>();
        operationList.addAll(priorities.keySet());
        Collections.sort(operationList, new ListIndividualPriorityMapComparator<E>(priorities));
        return operationList;
    }
}
