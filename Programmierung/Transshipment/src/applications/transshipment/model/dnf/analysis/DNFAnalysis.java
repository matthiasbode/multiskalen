///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.transshipment.model.dnf.analysis;
//
//import applications.mmrcsp.model.modes.JobOperationList;
//import applications.mmrcsp.model.operations.Operation;
//import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
//import applications.transshipment.model.LoadUnitJob;
//import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
//import applications.transshipment.model.operations.transport.RoutingTransportOperation;
//import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
//import applications.transshipment.model.schedule.LoadUnitJobSchedule;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import math.FieldElement;
//
///**
// * Bestimmung der Gründe, dass eine Operation DNF wurde. Grundsätzlich gibt es
// * verschieden Gründe, warum eine Operation DNF gesetzt wird: - Lager voll. -
// * Operation wird sehr spät aktiv, da die Vorgängeroperationen spät ausgeführt
// * wurden. - Operation ist sehr lange aktiv, wird allerdings spät auf der
// * Ressource behandelt.
// *
// * @author bode
// */
//public class DNFAnalysis {
//
//    static HashMap<ConveyanceSystem, Long> avgDurations = new HashMap<>();
//
//    public static Map<LoadUnitJob, DNFReason> getReasons(LoadUnitJobSchedule s) {
//
//
//        HashMap<LoadUnitJob, DNFReason> result = new HashMap<>();
//        Set<Operation> didNotFinish = s.getDidNotFinish();
//
////        Set<Operation> scheduledOperations = s.getScheduledOperations();
////        Set<Operation> operationToAnalyse = new HashSet<>();
////        for (Operation operation : scheduledOperations) {
////            if (operation instanceof MultiScaleTransportOperation) {
////                MultiScaleTransportOperation mstop = (MultiScaleTransportOperation) operation;
////                RoutingTransportOperation routingTransportOperation = mstop.getRoutingTransportOperation();
////                if (!didNotFinish.contains(routingTransportOperation)) {
////                    operationToAnalyse.add(routingTransportOperation);
////                }
////            }
////        }
//        for (Operation operation : didNotFinish) {
//            if (operation instanceof RoutingTransportOperation) {
//                RoutingTransportOperation routingTransportOperation = (RoutingTransportOperation) operation;
//                DNFReason reason = getReason(operation, s);
//                if (reason != null) {
//                    result.put(routingTransportOperation.getJob(), reason);
//                }
//            }
//        }
//        return result;
//    }
//
//    public static DNFReason getReason(Operation operation, LoadUnitJobSchedule s) {
//
//        if (operation instanceof RoutingTransportOperation) {
//
//
//            RoutingTransportOperation routingTransportOperation = (RoutingTransportOperation) operation;
//            DNFReason reason = new DNFReason(routingTransportOperation);
//            MultiScaleTransportOperation mstop = s.getActivityList().get(routingTransportOperation);
//
//
//
//            /**
//             * Bestimme die Position der Operation innerhalb der Aktivitätsliste
//             */
//            reason.indexInList = s.getActivityList().indexOf(mstop);
//            Routing<RoutingTransportOperation> routing = routingTransportOperation.getRouting();
//            /**
//             * Bestimme die Position der Operation innerhalb des zugehörigen
//             * Routings
//             */
//            reason.indexInRouting = routing.indexOf(routingTransportOperation);
//
//
//            /**
//             * Bestimme die Position der Vorgängeroperation innerhalb der
//             * Aktivitätsliste
//             */
//            if (reason.indexInRouting > 0) {
//                RoutingTransportOperation prev = routing.get(reason.indexInRouting - 1);
//                EarliestAndLatestStartsAndEnds prevEalosae = s.getEalosaes().get(prev);
//
//                MultiScaleTransportOperation prevMSTOP = s.getActivityList().get(prev);
//                reason.prevOperation = prev;
//                reason.indexOfPreviousInList = s.getActivityList().indexOf(prevMSTOP);
//
//                double prevStart = s.get(prevMSTOP).longValue();
//
//                /**
//                 * Prozentuale Angabe in Bezug zum möglichen EALOSAE-Fenster,
//                 * wann die Vorgängeroperation eingeplant wurde
//                 */
//                reason.prevEALOSAEpercent = (prevStart - prevEalosae.getEarliestOperationStart().longValue()) / (prevEalosae.getLatestOperationStart().sub(prevEalosae.getEarliestOperationStart())).longValue();
//
//            }
//            List<MultiScaleTransportOperation> resourceList = s.getActivityList().getResourceList(mstop.getResource());
//            /**
//             * Bestimme die Position der Operation innerhalb der Aktivitätsliste
//             * der ausführenden Ressource
//             */
//            reason.indexOfOperationOnResource = resourceList.indexOf(mstop);
//
//
//            /**
//             * Bestimme Zeitpunkt des Aktivsetzen für diese Operation in Bezug
//             * auf die EALOSAEs
//             */
//            EarliestAndLatestStartsAndEnds ealosae = s.getEalosaes().get(routingTransportOperation);
//            FieldElement bezugsStart = ealosae.getEarliestOperationStart();
//            if (reason.prevEALOSAEpercent != 0) {
//                RoutingTransportOperation prev = routing.get(reason.indexInRouting - 1);
//                MultiScaleTransportOperation prevMSTOP = s.getActivityList().get(prev);
//                bezugsStart = s.get(prevMSTOP).add(prevMSTOP.getDuration());
//            }
//            Map.Entry<FieldElement, Set<Operation>> higherEntry = s.getTimeToOperationMap(mstop.getResource()).higherEntry(bezugsStart);
//            if (higherEntry == null) {
//                return null;
//            }
//            Operation firstOperationInEALOSAE = higherEntry.getValue().iterator().next();
//            int indexOfFirstOperationINEALOSAE = resourceList.indexOf(firstOperationInEALOSAE);
//            reason.numberOfOperationsSinceES = reason.indexOfOperationOnResource - indexOfFirstOperationINEALOSAE;
//
//            Long avgDuration = avgDurations.get(mstop.getResource());
//            if (avgDuration == null) {
//                avgDuration = 0L;
//                for (MultiScaleTransportOperation multiScaleTransportOperation : resourceList) {
//                    avgDuration += multiScaleTransportOperation.getDuration().longValue();
//                }
//                avgDuration /= resourceList.size();
//                avgDurations.put(mstop.getResource(), avgDuration);
//            }
//
//            reason.percentResource = new Double(reason.numberOfOperationsSinceES * avgDuration) / (ealosae.getLatestOperationStart().sub(ealosae.getEarliestOperationStart())).longValue();
//
//            return reason;
//        }
//        return null;
//    }
//}
