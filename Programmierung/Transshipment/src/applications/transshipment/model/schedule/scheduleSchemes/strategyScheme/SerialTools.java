/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.strategyScheme;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import math.FieldElement;
import math.LongValue;
import math.Tools;

/**
 *
 * @author bode
 */
public class SerialTools {

    /**
     *
     * @param nextTFromJustScheduled nächster Startzeitpunkt, bestimmt aus den
     * in diesem Zeitschritt eingeplanten Operationen.
     */
    public static FieldElement getNextTime(FieldElement oldT,
            LinkedHashMap<ConveyanceSystem, FieldElement> nextTsFromJustScheduled,
            LinkedHashSet<RoutingTransportOperation> notActiveOperations,
            LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds,
            LinkedHashSet<RoutingTransportOperation> eligibleActivites,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes) {

        FieldElement nextTFromJustScheduled = null;
        for (FieldElement c : nextTsFromJustScheduled.values()) {
            /**
             * T-Next unter Umständen aktualisieren
             */

            if (nextTFromJustScheduled == null || c.isLowerThan(nextTFromJustScheduled)) {
                nextTFromJustScheduled = c;
            }
        }

        if (nextTFromJustScheduled != null) {
            return nextTFromJustScheduled;
        }

        /**
         * keine zuvor eingeplanten Operationen. --> Durchsuche zunächst die
         * noch nicht eingeplanten Operationen, die aber schon ausführbar sind.
         * Hier dürfe eigentlich nicht so viel passieren.
         */
        if (!eligibleActivites.isEmpty()) {
            FieldElement tNeu = null;
            for (RoutingTransportOperation routingTransportOperation : eligibleActivites) {
                FieldElement possibleStart = ealosaes.get(routingTransportOperation).getAvailableStartTimeSlot().getFromWhen();
                if (possibleStart.isGreaterThan(oldT)) {
                    tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
                }
            }
            if ((tNeu != null) && !tNeu.equals(oldT) && tNeu.isGreaterThan(oldT)) {
                return tNeu;
            }
        }

        /**
         * Momentan kann auch keine Operation mehr eingeplant werden. Bestimme
         * nächsten Zeitpunkt aus den Operationen, die keinen noch nicht
         * eingeplanten Vorgänger haben.
         */
        FieldElement tNeu = null;
        for (RoutingTransportOperation routingTransportOperation : operationsWithoutNotFinishedPreds) {
            FieldElement possibleStart = ealosaes.get(routingTransportOperation).getAvailableStartTimeSlot().getFromWhen();
            if (possibleStart.isGreaterThan(oldT)) {
                tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
            }
        }
        if (tNeu != null && !tNeu.equals(oldT) && tNeu.isGreaterThan(oldT)) {
            return tNeu;
        }

        /**
         * Bestimme nächsten Startpunkt aus den noch nicht aktiven Operationen.
         */
        tNeu = null;
        for (RoutingTransportOperation routingTransportOperation : notActiveOperations) {
            FieldElement possibleStart = ealosaes.get(routingTransportOperation).getAvailableStartTimeSlot().getFromWhen();
            if (possibleStart.isGreaterThan(oldT)) {
                tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
            }
        }
        if (tNeu != null && !tNeu.equals(oldT) && tNeu.isGreaterThan(oldT)) {
            return tNeu;
        }

        return oldT.add(new LongValue(5 * 1000l));
    }

    public static void updateEligibleActivites(LoadUnitJobSchedule schedule, LinkedHashSet<RoutingTransportOperation> notActiveOperations, LinkedHashSet<RoutingTransportOperation> finishedActivites, LinkedHashSet<RoutingTransportOperation> eligibleActivites, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {

        LinkedHashSet<RoutingTransportOperation> candidates = new LinkedHashSet<>();

        /**
         * Für die gerade geplantenOperationen wird die Nachfolgeroperation auf
         * aktiv gesetzt
         */
        for (Iterator<RoutingTransportOperation> it = eligibleActivites.iterator(); it.hasNext();) {
            RoutingTransportOperation rtop = it.next();
            if (schedule.isScheduled(rtop)) {
                it.remove();
                finishedActivites.add(rtop);
                candidates.addAll(graph.getSuccessors(rtop));
            }
        }

        for (RoutingTransportOperation rTop : candidates) {

            LinkedHashSet<RoutingTransportOperation> preds = new LinkedHashSet<>(graph.getPredecessors(rTop));
            preds.removeAll(schedule.getDidNotFinish());

            if (finishedActivites.containsAll(preds)) {
                eligibleActivites.add(rTop);
                notActiveOperations.remove(rTop);
            }
        }

    }

    public static boolean isEnd(LinkedHashSet<RoutingTransportOperation> notActiveOperations, LinkedHashSet<RoutingTransportOperation> eligibleActivites) {
        if (notActiveOperations.isEmpty() && eligibleActivites.isEmpty()) {
            return true;
        }
        return false;
    }
}
