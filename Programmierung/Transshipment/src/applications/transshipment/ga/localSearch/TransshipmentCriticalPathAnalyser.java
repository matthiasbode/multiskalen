/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.localSearch;

import applications.mmrcsp.ga.localsearch.CriticalPath;
import applications.mmrcsp.model.basics.ExtendedActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class TransshipmentCriticalPathAnalyser {

    public static Map<RoutingTransportOperation, CriticalPath<RoutingTransportOperation>> getCriticalPaths(LoadUnitJobSchedule schedule, ExtendedActivityOnNodeGraph<RoutingTransportOperation> aon) {

        HashMap<RoutingTransportOperation, CriticalPath<RoutingTransportOperation>> criticalPaths = new HashMap<>();
        /**
         * Enthält DNFs, die folge von schon vorhandenen DNFs sind. Key:
         * Darauffolgende DNF Value: Quelle des DNFs
         */
        HashMap<RoutingTransportOperation, RoutingTransportOperation> followingDNF = new HashMap<>();
        /**
         * Enthält die RoutingTransportOperations, die nicht eingeplant werden
         * konnten.
         */
        Set<Operation> didNotFinish = schedule.getDidNotFinish();
        /**
         * Rückwärtssuche, welcher Pfad kritisch ist. Ausgehend von den
         * DNF-Operationen.
         */
        OperationLoop:
        for (Operation operation : didNotFinish) {
            RoutingTransportOperation dnfRTop = (RoutingTransportOperation) operation;
            CriticalPath critcalPath = new CriticalPath();
            critcalPath.appendVertex(dnfRTop);
            RoutingTransportOperation current = dnfRTop;
            PathLoop:
            while (current != null) {
                LinkedHashSet<RoutingTransportOperation> predecessors = aon.getPredecessors(current);
                if (predecessors.isEmpty()) {
                    break;
                }
                FieldElement latestEndOfPredecessors = new LongValue(Long.MIN_VALUE);
                RoutingTransportOperation predecessor = null;
                PredLoop:
                for (RoutingTransportOperation preCan : predecessors) {
                    MultiScaleTransportOperation mScaleTop = schedule.getScheduledOperation(preCan);
                    if (!schedule.isScheduled(mScaleTop)) {
                        if (didNotFinish.contains(mScaleTop.getRoutingTransportOperation())) {
                            followingDNF.put(dnfRTop, preCan);
                            /*
                             Einer der Vorgänger dieser Operation ist bereits DNF gewesen.
                             */
                            CriticalPath otherPath = criticalPaths.get(preCan);
                            if (otherPath != null) {
                                critcalPath.appendVertexInFront(preCan);
                                critcalPath.setAffectedBy(otherPath);
                                criticalPaths.put(dnfRTop, critcalPath);
                                continue OperationLoop;
                            } else {
                                predecessor = preCan;
                                break PredLoop;
                            }
                        }
                        continue;
                    }
                    /**
                     * Es wird der späteste Beendigungszeitpunkt bestimmt.
                     */
                    FieldElement endOfmScaleTop = schedule.get(mScaleTop).add(mScaleTop.getDuration());
                    if (endOfmScaleTop.isGreaterThan(latestEndOfPredecessors)) {
                        latestEndOfPredecessors = endOfmScaleTop;
                        predecessor = preCan;
                    }
                }

                critcalPath.appendVertexInFront(predecessor);
                current = predecessor;
            }
            criticalPaths.put(dnfRTop, critcalPath);
        }

        return criticalPaths;

    }

    public static ArrayList<CriticalPath<RoutingTransportOperation>> getCriticalBlocks(CriticalPath<RoutingTransportOperation> criticalPath) {
        ArrayList<CriticalPath<RoutingTransportOperation>> blocks = new ArrayList<>();

        CriticalPath currentPath = new CriticalPath();
        RoutingTransportOperation vertex = criticalPath.getVertexAt(0);
        currentPath.appendVertex(vertex);

        for (int i = 1; i < criticalPath.getNumberOfVertices(); i++) {
            RoutingTransportOperation nextVertex = criticalPath.getVertexAt(i);
            if (vertex.getResource().equals(nextVertex.getResource())) {
                currentPath.appendVertex(nextVertex);
            } else {
                blocks.add(currentPath);
                currentPath = new CriticalPath();
                currentPath.appendVertex(nextVertex);
            }
            vertex = nextVertex;
        }
        blocks.add(currentPath);
        return blocks;

    }

    /**
     * Nachbarschaft: Eine Operation innerhalb des Blocks verschieben, bei der
     * sowohl Vorgänger und Nachfolger auch innerhalb des Blocks liegen. Kann
     * ungültig sein: Verschieben zur ersten oder letzten möglichen Positon als
     * Nachbarschaft.
     *
     * Hinweis: Abschätzung, welcher Move gut wäre.
     */
}
