/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import static applications.mmrcsp.model.schedule.scheduleSchemes.ParallelTools.strongGreaterEqual;
import static applications.transshipment.TransshipmentParameter.legacy;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.dnf.DNFTreatment;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;
import math.Field;
import math.FieldElement;
import math.Tools;

/**
 *
 * @author bode
 */
public class TransshipmentParallelTools {

    /**
     *
     * @param oldT
     * @param scheduledEndTimes
     * @param notActiveOperations
     * @param operationsWithoutNotFinishedPreds
     * @param eligibleActivites
     * @param ealosaes
     * @return
     */
    public static FieldElement getNextTime(FieldElement oldT,
            TreeSet<FieldElement> scheduledEndTimes,
            LinkedHashSet<RoutingTransportOperation> notActiveOperations,
            LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds,
            LinkedHashSet<RoutingTransportOperation> eligibleActivites,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, TimeSlot timeSlot, LoadUnitJobSchedule schedule) {

//        if (oldT instanceof DiscretizedFuzzyInterval) {
//            DiscretizedFuzzyInterval oldFuzzyT = (DiscretizedFuzzyInterval) oldT;
//
//            boolean allScheduled = true;
//            for (RoutingTransportOperation eligibleActivite : eligibleActivites) {
//                if (!schedule.isScheduled(eligibleActivite)) {
//                    allScheduled = false;
//                    break;
//                }
//            }
//            if (!allScheduled) {
//                long newAT = (long) (oldFuzzyT.getC1()) + 1000;
////                if (newAT < oldFuzzyT.getM2()) {
//                    return DiscretizedFuzzyInterval.max(FuzzyFactory.createCrispValue(newAT), oldFuzzyT);
////                }
//            }
//        }
        /**
         * Bestimme das minimum aus den Operationsenden der gerade laufenden
         * Operationen.
         */
        FieldElement min = null;
        FieldElement nextTFromList;
        if (legacy) {
            nextTFromList = !scheduledEndTimes.isEmpty() ? scheduledEndTimes.pollFirst() : null;
        } else {
            nextTFromList = !scheduledEndTimes.isEmpty() ? scheduledEndTimes.first() : null;
        }
        if (nextTFromList != null && !nextTFromList.equals(oldT) && nextTFromList.isGreaterThan(oldT)) {
            min = nextTFromList;
        }

        /**
         * Bestimme kleinsten EarliestStart der Operationen ohne Vorgänger.
         */
        FieldElement tNeu = null;
        for (RoutingTransportOperation operation : operationsWithoutNotFinishedPreds) {
            FieldElement possibleStart = ealosaes.get(operation).getAvailableStartTimeSlot().getFromWhen();
            if (possibleStart.isGreaterThan(oldT)) {
                tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
            }
        }
        FieldElement minES = null;
        if (tNeu != null && !tNeu.equals(oldT) && tNeu.isGreaterThan(oldT)) {
//            minES = tNeu;//tNeu.add(Field.getEinsElement(tNeu.getClass()).mult(1000));
            minES = tNeu.add(Field.getEinsElement(tNeu.getClass()).mult(1000));
        }

        if (min != null && minES != null) {
            return Field.min(min, minES);
        } else if (minES != null) {
            return minES;
        } else if (min != null) {
            return min;
        }
//        
//                if (min != null && minES != null) {
//            return Field.min(Field.min(min, minES),oldT.add(Field.getEinsElement(oldT.getClass()).mult(60 * 1000)));
//        } else if (minES != null) {
//            return Field.min(minES,oldT.add(Field.getEinsElement(oldT.getClass()).mult(60 * 1000)));
//        } else if (min != null) {
//            return Field.min(min,oldT.add(Field.getEinsElement(oldT.getClass()).mult(60 * 1000)));
//        }
        return oldT.add(Field.getEinsElement(oldT.getClass()).mult(5 * 1000));
    }

    public static void updateEligibleActivites(FieldElement currentTime, LoadUnitJobSchedule schedule, LinkedHashSet<RoutingTransportOperation> notActiveOperations, LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds, LinkedHashSet<RoutingTransportOperation> eligibleActivites, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {

        LinkedHashSet<RoutingTransportOperation> candidates = new LinkedHashSet<>();

        /**
         * Für die gerade geplantenOperationen wird die Nachfolgeroperation auf
         * aktiv gesetzt. Die Nachfolger werden zu den möglichen neuen
         * ausführbaren Operationen hinzugefügt.
         */
        for (Iterator<RoutingTransportOperation> it = eligibleActivites.iterator(); it.hasNext();) {
            RoutingTransportOperation rtop = it.next();
            if (schedule.isScheduled(rtop)) {
                it.remove();
//                finishedActivites.add(rtop);
                candidates.addAll(graph.getSuccessors(rtop));
            }
        }

        /**
         * Für die Kandidaten wird überprüft, ob alle ihre Vorgänger schon
         * eingeplant sind.
         */
        for (RoutingTransportOperation rTop : candidates) {
            LinkedHashSet<RoutingTransportOperation> preds = new LinkedHashSet<>(graph.getPredecessors(rTop));
            preds.removeAll(schedule.getDidNotFinish());
            boolean add = true;
            for (RoutingTransportOperation pred : preds) {
                if (!schedule.isScheduled(pred)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                operationsWithoutNotFinishedPreds.add(rTop);
            }
        }
        /**
         * Über alle Operationen ohne noch nicht abgefertigten Vorgänger wird
         * iteriert und getestet, ob die EALOSAES bereits eingehalten werden.
         */
        updateNewTimeActivites(currentTime, schedule, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes, graph);

    }

    public static void updateNewTimeActivites(FieldElement currentTime, LoadUnitJobSchedule schedule, LinkedHashSet<RoutingTransportOperation> notActiveOperations, LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds, LinkedHashSet<RoutingTransportOperation> eligibleActivites, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {

        for (Iterator<RoutingTransportOperation> it = operationsWithoutNotFinishedPreds.iterator(); it.hasNext();) {
            RoutingTransportOperation routingTransportOperation = it.next();
//            if (!operationsToSchedule.contains(routingTransportOperation)) {
//                continue;
//            }

            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(routingTransportOperation);
            if (ealosae == null) {
                throw new NullPointerException("Keine EALOSAE gefunden");
            }

            if (ealosae.isDNF()) {
                continue;
            }

            /**
             * Anders als bei Masmoudi es muss streng größergleich gelten, damit
             * nicht eine Ladeeinheit abgeholt werden kann, bevor sie eintrifft.
             */
            if (currentTime instanceof DiscretizedFuzzyInterval) {
                if (!strongGreaterEqual((DiscretizedFuzzyInterval) currentTime, (DiscretizedFuzzyInterval) ealosae.getEarliestStart())) {
                    continue;
                }
            } else {
                if (currentTime.longValue() < ealosae.getEarliestStart().longValue()) {
                    continue;
                }
            }
            eligibleActivites.add(routingTransportOperation);
            it.remove();
            notActiveOperations.remove(routingTransportOperation);

        }
    }

    private static boolean testDNF(FieldElement latestStart, FieldElement t) {
        return latestStart.isLowerThan(t);
    }

    public static void testDNF(DNFTreatment dnfTreatment, FieldElement t, LoadUnitJobSchedule schedule, LinkedHashSet<RoutingTransportOperation> notActiveOperations, LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds, Collection<RoutingTransportOperation> list, LinkedHashSet<RoutingTransportOperation> eligibleActivites, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {
        Collection<LoadUnitJob> jobsToSetDNF = new LinkedHashSet<>();
        for (Iterator<RoutingTransportOperation> it = notActiveOperations.iterator(); it.hasNext();) {
            RoutingTransportOperation rTop = it.next();
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(rTop);
            if (ealosae.isDNF() || testDNF(ealosae.getLatestStart(), t)) {
                dnfTreatment.setDNF(rTop, t, list, schedule, graph, ealosaes);
                jobsToSetDNF.add(rTop.getJob());
                it.remove();
            }
        }
        for (Iterator<RoutingTransportOperation> it = eligibleActivites.iterator(); it.hasNext();) {
            RoutingTransportOperation mTop = it.next();
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(mTop);
            if (ealosae.isDNF() || testDNF(ealosae.getLatestStart(), t)) {
                dnfTreatment.setDNF(mTop, t, list, schedule, graph, ealosaes);
                jobsToSetDNF.add(mTop.getJob());
                it.remove();
            }
        }
        for (Iterator<RoutingTransportOperation> it = operationsWithoutNotFinishedPreds.iterator(); it.hasNext();) {
            RoutingTransportOperation mTop = it.next();
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(mTop);
            if (ealosae.isDNF() || testDNF(ealosae.getLatestStart(), t)) {
                dnfTreatment.setDNF(mTop, t, list, schedule, graph, ealosaes);
                jobsToSetDNF.add(mTop.getJob());
                it.remove();
            }
        }

        for (LoadUnitJob job : jobsToSetDNF) {
            for (JobOperationList<RoutingTransportOperation> routing : job.getRoutings()) {
                for (RoutingTransportOperation routingTransportOperation : routing) {
                    notActiveOperations.remove(routingTransportOperation);
                    eligibleActivites.remove(routingTransportOperation);
                    operationsWithoutNotFinishedPreds.remove(routingTransportOperation);
                }
            }
        }

    }

    public static boolean isEnd(LinkedHashSet<RoutingTransportOperation> notActiveOperations, LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds, LinkedHashSet<RoutingTransportOperation> eligibleActivites) {
        if (notActiveOperations.isEmpty() && eligibleActivites.isEmpty() && operationsWithoutNotFinishedPreds.isEmpty()) {
            return true;
        }
        return false;
    }

    public static LinkedHashSet<RoutingTransportOperation> initOperationsWithoutNotFinishedPreds(LoadUnitJobSchedule schedule, Collection<RoutingTransportOperation> operationsToSchedule, ActivityOnNodeGraph<RoutingTransportOperation> graph) {
        LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds = new LinkedHashSet<>();

        for (RoutingTransportOperation op : operationsToSchedule) {
            if (schedule.isScheduled(op)) {
                continue;
            }
            LinkedHashSet<RoutingTransportOperation> predecessors = graph.getPredecessors(op);
            boolean add = true;

            for (RoutingTransportOperation predecessor : predecessors) {
                if (!schedule.isScheduled(predecessor)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                operationsWithoutNotFinishedPreds.add(op);
            }
        }
        return operationsWithoutNotFinishedPreds;
    }

}
