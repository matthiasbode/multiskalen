/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.scheduleSchemes.old;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import math.Field;
import math.FieldElement;
import math.LongValue;
import math.Tools;

/**
 *
 * @author bode
 */
public class ParallelTools_old {

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
    public static <O extends Operation, R extends Resource> FieldElement getNextTime(
            FieldElement oldT,
            TreeSet<FieldElement> scheduledEndTimes,
            LinkedHashSet<O> notActiveOperations,
            LinkedHashSet<O> operationsWithoutNotFinishedPreds,
            LinkedHashSet<O> eligibleActivites,
            Map<O, EarliestAndLatestStartsAndEnds> ealosaes) {

        
        FieldElement nextTFromList = !scheduledEndTimes.isEmpty() ? scheduledEndTimes.pollFirst() : null;

        /**
         * Sollte der neue Startzeitpunkt größer sein, als der alte, so wird
         * dieser um eins erhöht und zurückgegeben
         */
        if (nextTFromList != null && !nextTFromList.equals(oldT) && nextTFromList.isGreaterThan(oldT)) {
            return nextTFromList.add(Field.getEinsElement(nextTFromList.getClass()));
        }

        /**
         * Abfangen und Sonderbehandlung für Start.
         */
        if (oldT.equals(Field.getNullElement(oldT.getClass()))) {
            return oldT;
        }

        /**
         * keine zuvor eingeplanten Operationen. --> Durchsuche zunächst die
         * noch nicht eingeplanten Operationen, die aber schon ausführbar sind.
         * Hier dürfe eigentlich nicht so viel passieren.
         */
        if (!eligibleActivites.isEmpty()) {
            FieldElement tNeu = null;
            for (O operation : eligibleActivites) {
                FieldElement possibleStart = ealosaes.get(operation).getAvailableStartTimeSlot().getFromWhen();
                if (possibleStart.isGreaterThan(oldT)) {
                    tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
                }
            }
            if ((tNeu != null) && !tNeu.equals(oldT) && tNeu.isGreaterThan(oldT)) {
                return tNeu.add(Field.getEinsElement(nextTFromList.getClass()));
            }
        }

        /**
         * Momentan kann auch keine Operation mehr eingeplant werden. Bestimme
         * nächsten Zeitpunkt aus den Operationen, die keinen noch nicht
         * eingeplanten Vorgänger haben.
         */
        FieldElement tNeu = null;

        for (O operation : operationsWithoutNotFinishedPreds) {
            FieldElement possibleStart = ealosaes.get(operation).getAvailableStartTimeSlot().getFromWhen();
            if (possibleStart.isGreaterThan(oldT)) {
                tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
            }
        }
        if (tNeu != null && !tNeu.equals(oldT) && tNeu.isGreaterThan(oldT)) {
            return tNeu.add(Field.getEinsElement(tNeu.getClass()));
        }

        /**
         * Bestimme nächsten Startpunkt aus den noch nicht aktiven Operationen.
         */
        tNeu = null;
        for (O operation : notActiveOperations) {
            FieldElement possibleStart = ealosaes.get(operation).getAvailableStartTimeSlot().getFromWhen();
            if (possibleStart.isGreaterThan(oldT)) {
                tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
            }
        }
        if (tNeu != null && !tNeu.equals(oldT) && tNeu.isGreaterThan(oldT)) {
            return tNeu.add(Field.getEinsElement(tNeu.getClass()));
        }

        return oldT.add(Field.getEinsElement(oldT.getClass()));

    }

    public static <O extends Operation, R extends Resource> void updateEligibleActivites(FieldElement currentTime, Schedule schedule, LinkedHashSet<O> notActiveOperations, LinkedHashSet<O> operationsWithoutNotFinishedPreds, LinkedHashSet<O> finishedActivites, LinkedHashSet<O> eligibleActivites, List<O> list, Map<O, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<O> graph) {

        LinkedHashSet<O> candidates = new LinkedHashSet<>();

        /**
         * Für die gerade geplantenOperationen wird die Nachfolgeroperation auf
         * aktiv gesetzt. Die Nachfolger werden zu den möglichen neuen
         * ausführbaren Operationen hinzugefügt.
         */
        for (Iterator<O> it = eligibleActivites.iterator(); it.hasNext();) {
            O operation = it.next();
            if (schedule.isScheduled(operation)) {
                it.remove();
                finishedActivites.add(operation);
                candidates.addAll(graph.getSuccessors(operation));
            }
        }

        for (O rTop : candidates) {
            LinkedHashSet<O> preds = new LinkedHashSet<>(graph.getPredecessors(rTop));
            preds.removeAll(schedule.getDidNotFinish());
            if (finishedActivites.containsAll(preds)) {
                operationsWithoutNotFinishedPreds.add(rTop);
            }

        }

        for (Iterator<O> it = operationsWithoutNotFinishedPreds.iterator(); it.hasNext();) {
            O operation = it.next();

            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(operation);
            if (ealosae.isDNF()) {
                continue;
            }
            if (!ealosae.getAvailableStartTimeSlot().contains(currentTime.longValue())) {
                continue;
            }

            eligibleActivites.add(operation);
            it.remove();
            notActiveOperations.remove(operation);
        }

    }

    public static <O extends Operation, R extends Resource> void testDNF(FieldElement t, Schedule schedule, LinkedHashSet<O> notActiveOperations, LinkedHashSet<O> operationsWithoutNotFinishedPreds, List<O> list, LinkedHashSet<O> eligibleActivites, Map<O, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<O> graph) {
        if (ealosaes == null) {
            return;
        }
        Collection<O> operationDNF = new HashSet<>();

        for (Iterator<O> it = notActiveOperations.iterator(); it.hasNext();) {
            O rTop = it.next();
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(rTop);
            if (ealosae.isDNF() || ealosae.getLatestStart().isLowerThan(t)) {
                operationDNF.add(rTop);
                it.remove();
            }
        }
        for (Iterator<O> it = eligibleActivites.iterator(); it.hasNext();) {
            O mTop = it.next();
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(mTop);
            if (ealosae.isDNF() || ealosae.getLatestStart().isLowerThan(t)) {

                operationDNF.add(mTop);
                it.remove();
            }
        }
        for (Iterator<O> it = operationsWithoutNotFinishedPreds.iterator(); it.hasNext();) {
            O mTop = it.next();
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(mTop);
            if (ealosae.isDNF() || ealosae.getLatestStart().isLowerThan(t)) {

                operationDNF.add(mTop);
                it.remove();
            }
        }

        for (O o : operationDNF) {
            notActiveOperations.remove(o);
            eligibleActivites.remove(o);
            operationsWithoutNotFinishedPreds.remove(o);
        }

    }

    public static <O extends Operation, R extends Resource> boolean isEnd(LinkedHashSet<O> notActiveOperations, LinkedHashSet<O> operationsWithoutNotFinishedPreds, LinkedHashSet<O> eligibleActivites) {
        return notActiveOperations.isEmpty() && eligibleActivites.isEmpty() && (operationsWithoutNotFinishedPreds == null || operationsWithoutNotFinishedPreds.isEmpty());
    }
}
