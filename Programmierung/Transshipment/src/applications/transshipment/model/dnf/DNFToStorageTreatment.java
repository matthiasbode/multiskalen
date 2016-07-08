/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.dnf;

 
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.routing.RouteFinder;
import java.util.Collection;
import java.util.Map;
import math.FieldElement;
import math.LongValue;

/**
 * DNF gesetzte Operationen werden versucht in ein zuvor definiertes Lager
 * umzuladen, wenn möglich. Alle nachfolgenden Operationen des gleichen Jobs
 * müssen ebenfalls DNF gesetzt. Andere, abhängige Jobs werden versucht
 * weiterhin abgearbeitet zu werden.
 *
 * @author bode
 */
public class DNFToStorageTreatment implements DNFTreatment {

    private LoadUnitStorage dnfStorages;
    private RouteFinder routeFinder;
    private MultiJobTerminalProblem problem;
    public static FieldElement durationToTransshipDNF = new LongValue(5 * 60 * 1000);

    public DNFToStorageTreatment(LoadUnitStorage dnfStorages, RouteFinder routeFinder, MultiJobTerminalProblem problem) {
        this.dnfStorages = dnfStorages;
        this.routeFinder = routeFinder;
        this.problem = problem;
    }

    @Override
    public boolean setDNF(RoutingTransportOperation failedRoutingOperation, FieldElement t, Collection<RoutingTransportOperation> activityList, LoadUnitJobSchedule s, ActivityOnNodeGraph<RoutingTransportOperation> graph, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes) {
        LoadUnitJob job = failedRoutingOperation.getJob();
        JobOperationList<RoutingTransportOperation> routing = failedRoutingOperation.getRouting();
        /**
         * Markiere den Job als DNF
         */
        s.addDNFJob(failedRoutingOperation);

//        /**
//         * Falls es sich bereits um einen DNFJob handelt
//         */
//        if (s.getDnfJobs().contains(job)) {
//            /**
//             * Entferne alle Operationen aus der Queue und aus dem Graphen, die
//             * noch nicht eingeplant sind.
//             */
//        for (RoutingTransportOperation operationToDelete : routing) {
//            activityList.remove(operationToDelete);
//        }
//            return false;
//        }

//
//        /**
//         * Bestimme letzte ausgeführte TransportOperation und damit die
//         * momentane Position der LU
//         */
//        LoadUnitStorage currentPosition = routing.get(0).getOrigin();
//        for (RoutingTransportOperation routingTransportOperation : routing) {
//            MultiScaleTransportOperation top = activityList.get(routingTransportOperation);
//            if (s.isScheduled(top)) {
//                currentPosition = top.getDestination();
//            } else {
//                break;
//            }
//        }
//
//
//
//
//
//        /**
//         * Hole DNF-Route als Ausweichmöglichkeit
//         */
//        Routing<RoutingTransportOperation> dnfRouting = job.getToDNFStorageRoutes().get(currentPosition);
//
//
//        /**
//         * Aktualisiere AON
//         */
//        for (RoutingTransportOperation macroscopicTransportOperation : dnfRouting) {
//            graph.addVertex(macroscopicTransportOperation);
//        }
//        for (int i = 0; i < dnfRouting.size() - 1; i++) {
//            graph.addEdge(dnfRouting.get(i), dnfRouting.get(i + 1));
//        }
//
//        FieldElement earliestStart = t;
//        FieldElement earliestEnd = t;
//        /**
//         * Bestimme neue MultiscaleOperation und füge dieser der ActivityList
//         * hinzu. Bestimme EALOSAEs für die neuen Operationen!
//         */
//        int indexToInsertAt = 0;
//        for (RoutingTransportOperation operation : dnfRouting) {
//            /**
//             * Bestimme Transportdauer der neuen Operation
//             */
//            FieldElement transportationTime = problem.getScheduleRule(operation.getResource()).getTransportationTime(operation.getOrigin(), operation.getDestination(), operation.getLoadUnit());
//            operation.setDuration(transportationTime);
//            /**
//             * Erzeuge MultiScaleOperation
//             */
//            MultiScaleTransportOperation detailedOperation = problem.getScheduleRule(operation.getResource()).getDetailedOperation(operation);
//            /**
//             * Füge diese der Aktivitätsliste hinzu. Hier müsste eigentilch noch
//             * erfolgen, dass die Operation an die Stelle der momentanen
//             * Operation kommt.
//             */
//            activityList.put(indexToInsertAt++, detailedOperation, operation);
//            /**
//             * Bestimme Bedarf der MultiScaleOperation
//             */
//            problem.getScheduleRule(operation.getResource()).determineDurationAndDemands(detailedOperation);
//            /**
//             * Bestimme Ealosaes für die neue Operation
//             */
//            EarliestAndLatestStartsAndEnds ealosae = new EarliestAndLatestStartsAndEnds(operation, operation.getDuration());
//            earliestEnd = earliestStart.add(operation.getDuration());
//            ealosae.setEarliest(earliestStart, earliestEnd);
//            FieldElement latestEnd = earliestStart.add(durationToTransshipDNF);
//            ealosae.setLatest(latestEnd.sub(operation.getDuration()), latestEnd);
//            earliestStart = earliestEnd;
//            ealosaes.put(operation, ealosae);
//        }
//
//
//
//
//
//        /**
//         * Letzte Operation im neuen Routing.
//         */
//        RoutingTransportOperation last = dnfRouting.get(0);
//        /**
//         * Suche alle Nachfolger im Graphen von den
//         * macroscopicTransportOperationen des Routings, die noch nicht
//         * eingeplant sind und setzte die Vorgänger auf die neuen Operationen.
//         */
//        for (RoutingTransportOperation operationToDelete : routing) {
//            MultiScaleTransportOperation top = activityList.get(operationToDelete);
//            activityList.remove(top);
//            /**
//             * Operation noch nicht eingeplant
//             */
//            if (!s.isScheduled(top)) {
//
//
//                /**
//                 * Füge eine Kante zwischen der letzten Operation dieses
//                 * Routings und den Nachfolgern der ursprünglichen Routen hinzu.
//                 */
//                LinkedHashSet<RoutingTransportOperation> successors = graph.getSuccessors(operationToDelete);
//                graph.removeVertex(operationToDelete);
//
//                for (RoutingTransportOperation suc : successors) {
//                    if (!graph.addEdge(last, suc, last.getDuration())) {
//                        throw new IllegalArgumentException("Kante nicht hinzufügbar");
//                    }
//                    /**
//                     * Aktualisiere EarliestStart der Nachfolgeoperationen
//                     */
//                    EarliestAndLatestStartsAndEnds ealosaeSuc = ealosaes.get(suc);
//                    if (ealosaeSuc.getEarliestOperationStart().isLowerThan(earliestEnd)) {
//                        ealosaeSuc.setEarliest(earliestEnd, earliestEnd.add(suc.getDuration()));
//                    }
//                }
//            }
//        }
//
//
//        /**
//         * Neue Job muss aufgenommen werden in den Schedule
//         */
        return true;
    }
}
