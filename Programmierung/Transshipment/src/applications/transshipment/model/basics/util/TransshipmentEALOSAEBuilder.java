/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.EALOSAEBuilder;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.LoadUnitPositions;
import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import math.FieldElement;
import math.Tools;
import org.graph.algorithms.TopologicalSort;
import org.graph.directed.DefaultDirectedGraph;

/**
 *
 * @author bode
 */
public class TransshipmentEALOSAEBuilder {

    /**
     * Berechnet anhand der zeitlichen Verfügbarkeit der Start- und
     * Zielressourcen die Zeitfenster, in denen ein Job stattfinden kann.
     *
     * @param problem
     * @return
     */
    public static Map<LoadUnitJob, TimeSlot> calcJobTimeWindows(MultiJobTerminalProblem problem) {
        return calcJobTimeWindows(problem, null);
    }

    /**
     * Berechnet anhand der zeitlichen Verfügbarkeit der Start- und
     * Zielressourcen die Zeitfenster, in denen ein Job stattfinden kann. Wurde
     * ein scheudle übergeben, so wird überprüft, welche Operationen für den
     * entsprechenden Job bereits eingeplant wurden. Dementsprechend wird das
     * Zeitfenster für die nächsten Operationen im Job und somit für den
     * gesamten Job zeitlich nach hinten verschoben.
     *
     * @param problem
     * @param schedule
     * @return
     */
    public static Map<LoadUnitJob, TimeSlot> calcJobTimeWindows(MultiJobTerminalProblem problem, LoadUnitJobSchedule schedule) {
        Map<LoadUnitJob, TimeSlot> result = new HashMap<>();
        for (LoadUnitJob job : problem.getJobs()) {
            FieldElement fromWhen = job.getOrigin().getTemporalAvailability().getFromWhen();
            FieldElement untilWhen = job.getDestination().getTemporalAvailability().getUntilWhen();
            if (schedule != null) {
                LoadUnitPositions operationsForLoadUnit = schedule.getOperationsForLoadUnit(job.getLoadUnit());
                if (operationsForLoadUnit != null) {
                    if (!operationsForLoadUnit.isEmpty()) {
                        LoadUnitOperation lastOperation = operationsForLoadUnit.get(operationsForLoadUnit.size() - 1);
                        if (lastOperation instanceof TransportOperation) {
                            fromWhen = schedule.get(lastOperation).add(lastOperation.getDuration());
                        }
                        if (lastOperation instanceof StoreOperation) {
                            fromWhen = schedule.get(lastOperation);
                        }
                    }
                }
            }
            if (fromWhen.isGreaterThan(untilWhen)) {
                result.put(job, TimeSlot.nullTimeSlot);
                System.err.println("NullTimeWindow for Job " + job);
            } else {
                result.put(job, new TimeSlot(fromWhen, untilWhen));
            }

        }
        return result;
    }

    /**
     * Sortiert die Operationen zuvor topologisch.
     *
     * @param component
     * @param timeWindows
     * @return
     */
    public static Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes(DefaultDirectedGraph<RoutingTransportOperation> component, Map<LoadUnitJob, TimeSlot> timeWindows) {
        try {
            List<Set<RoutingTransportOperation>> topoSort = TopologicalSort.topologicalSort(component);
            return ealosaes(component, topoSort, timeWindows);
        } catch (java.lang.IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Gibt für einen Graphen bzw. eine Zusammenhangskomponente die frühsten und
     * spätesten Operationen Starts und Enden an
     *
     * @param component Graph, für den die EALOSAEs bestimmt werden sollen.
     * @param topoSort Topologische Sortierung des Graphens.
     * @param timeWindows Zeitfenster zum Ausführen der jeweiligen Jobs.
     * @return
     */
    public static Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes(DefaultDirectedGraph<RoutingTransportOperation> component, List<Set<RoutingTransportOperation>> topoSort, Map<LoadUnitJob, TimeSlot> timeWindows) {

        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> res = initialEalosaes(component, topoSort, timeWindows);
        if (res == null) {
            return null;
        }
        
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = EALOSAEBuilder.<RoutingTransportOperation>ealosaes(res, component, topoSort);
        return ealosaes; 
    }

    /**
     * Initialisiert die EALOSAEs ohne Beziehungen zwischen Operationen zu
     * berücksichtigen.
     *
     * @param component
     * @param topoSort
     * @param ealosaes
     * @return
     */
    public static LinkedHashMap<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> initialEalosaes(DefaultDirectedGraph<RoutingTransportOperation> component, List<Set<RoutingTransportOperation>> topoSort, Map<LoadUnitJob, TimeSlot> timeWindows) {
        LinkedHashMap<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = new LinkedHashMap<>();

        for (RoutingTransportOperation top : component.vertexSet()) {
            ealosaes.put(top, new EarliestAndLatestStartsAndEnds(top.getDuration()));
        }

        /**
         * ###############################################################
         * TEMPAVAIL-Betrachtung als Initiallösung ES / EE
         * ###############################################################
         */
        for (int i = 0; i < topoSort.size(); i++) {
            Set<RoutingTransportOperation> verticesInNodeClass = topoSort.get(i);
            /**
             * Gehe über alle Knoten der Knotenklasse
             */
            for (RoutingTransportOperation routingTransportOperation : verticesInNodeClass) {
                EarliestAndLatestStartsAndEnds currentEALOSAE = ealosaes.get(routingTransportOperation);
                if (currentEALOSAE == null) {
                    throw new NullPointerException("Keine EALOSAEs gesetzt");
                }
                if (routingTransportOperation.getDuration() == null) {
                    throw new NullPointerException("Keine Dauer gesetzt");
                }
                if (routingTransportOperation.getDestination().getTemporalAvailability() == null) {
                    throw new NullPointerException("Keine TimeSlot gesetzt");
                }
                if (routingTransportOperation.getDestination().getTemporalAvailability().getFromWhen() == null) {
                    throw new NullPointerException("Keine FromWhen gesetzt");
                }

                FieldElement earliestExecutionStart = Tools.max(routingTransportOperation.getOrigin().getTemporalAvailability().getFromWhen(), routingTransportOperation.getDestination().getTemporalAvailability().getFromWhen());

                /**
                 * Berücksichtigung von ggf. schon bereits eingeplanten
                 * Operationen.
                 */
                TimeSlot timeWindow = timeWindows.get(routingTransportOperation.getJob());
                if (timeWindow != null) {
                    earliestExecutionStart = Tools.max(timeWindow.getFromWhen(), earliestExecutionStart);
                }

                FieldElement earliestExecutionEnd = earliestExecutionStart.add(currentEALOSAE.getTransportationTime());
                currentEALOSAE.setEarliest(earliestExecutionStart, earliestExecutionEnd);
                /**
                 * Test, und ggf. Listeneraufruf.
                 */
                if (!currentEALOSAE.testValues()) {
                    return null;
                }

            }
        }

        /**
         * ###############################################################
         * TEMPAVAIL-Betrachtung als Initiallösung LS / LE
         * ###############################################################
         */
        for (int i = topoSort.size() - 1; i >= 0; i--) {
            Set<RoutingTransportOperation> verticesInNodeClass = topoSort.get(i);
            /**
             * Gehe über alle Knoten der Knotenklasse
             */
            for (RoutingTransportOperation routingTransportOperation : verticesInNodeClass) {
                EarliestAndLatestStartsAndEnds currentEALOSAE = ealosaes.get(routingTransportOperation);
                FieldElement latestExecutionEnd = Tools.min(routingTransportOperation.getOrigin().getTemporalAvailability().getUntilWhen().add(routingTransportOperation.getDuration()), routingTransportOperation.getDestination().getTemporalAvailability().getUntilWhen());
                FieldElement latestExecutionStart = latestExecutionEnd.sub(routingTransportOperation.getDuration());
                currentEALOSAE.setLatest(latestExecutionStart, latestExecutionEnd);
                /**
                 * Test, und ggf. Listeneraufruf.
                 */
                if (!currentEALOSAE.testValues()) {
                    return null;
                }
            }
        }
        return ealosaes;
    }
 
    
}
