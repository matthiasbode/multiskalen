/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.scheduleSchemes;

import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyDemandUtilities;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.AlphaCutSet;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import fuzzy.number.discrete.interval.FuzzyInterval;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import math.Field;
import math.FieldElement;
import math.Tools;

/**
 *
 * @author bode
 */
public class ParallelTools {

    public static boolean PLOT;

    /**
     *
     * @param oldT
     * @param nextTsFromJustScheduled
     * @param notActiveOperations
     * @param operationsWithoutNotFinishedPreds
     * @param eligibleActivites
     * @param ealosaes
     * @return
     */
    public static <O extends Operation, R extends Resource> FieldElement getNextTime(
            FieldElement oldT,
            Schedule s,
            TreeSet<FieldElement> scheduledEndTimes,
            LinkedHashSet<O> notActiveOperations,
            LinkedHashSet<O> operationsWithoutNotFinishedPreds,
            LinkedHashSet<O> eligibleActivites,
            Map<O, EarliestAndLatestStartsAndEnds> ealosaes, TimeSlot timeSlot) {

        FieldElement nextTFromList = !scheduledEndTimes.isEmpty() ? scheduledEndTimes.pollFirst() : null;
        if (nextTFromList != null && nextTFromList.equals(timeSlot.getFromWhen())) {
            return oldT;
        }
        /**
         * Bestimme kleinsten EarliestStart der Operationen ohne Vorgänger.
         */
        FieldElement tNeu = null;
        for (O operation : operationsWithoutNotFinishedPreds) {
            FieldElement possibleStart = ealosaes.get(operation).getAvailableStartTimeSlot().getFromWhen();
            if (possibleStart.isGreaterThan(oldT)) {
                tNeu = tNeu == null ? possibleStart : Tools.min(possibleStart, tNeu);
            }
        }

        FieldElement result = null;
        if (tNeu != null && nextTFromList != null) {
            result = Field.min(nextTFromList, tNeu);
        } else if (tNeu != null) {
            result = tNeu;
        } else if (nextTFromList != null) {
            result = nextTFromList;
        }

        if (result != null && result.isGreaterThan(oldT)) {
            return result;
        }

//        return oldT.add(Field.getEinsElement(oldT.getClass()).mult(1 * 1000));
        DiscretizedFuzzyInterval oldTFuzz = (DiscretizedFuzzyInterval) oldT;
        long aT = (long) (oldTFuzz.getC1() + 1 * 1000);
        return maxFromFuzzyNumbers(oldT, FuzzyFactory.createCrispValue(aT));
//        return FuzzyFactory.CutLeftSideOfIntervall(oldTFuzz, 5*1000);

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
        updateNewTimeOperations(currentTime, schedule, notActiveOperations, operationsWithoutNotFinishedPreds, finishedActivites, eligibleActivites, list, ealosaes, graph);

    }

    /**
     * Es wurde eine neue Zeit bestimmt und unter den möglichen Kandidaten, die
     * jetzt ausführbar werden könnten zur neuen Zeit newTime werden die
     * passenden bestimmt.
     *
     * @param <O>
     * @param <R>
     * @param newTime
     * @param schedule
     * @param notActiveOperations
     * @param operationsWithoutNotFinishedPreds
     * @param finishedActivites
     * @param eligibleActivites
     * @param list
     * @param ealosaes
     * @param graph
     */
    public static <O extends Operation, R extends Resource> void updateNewTimeOperations(FieldElement newTime, Schedule schedule, LinkedHashSet<O> notActiveOperations, LinkedHashSet<O> operationsWithoutNotFinishedPreds, LinkedHashSet<O> finishedActivites, LinkedHashSet<O> eligibleActivites, List<O> list, Map<O, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<O> graph) {
        for (Iterator<O> it = operationsWithoutNotFinishedPreds.iterator(); it.hasNext();) {
            O operation = it.next();

            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(operation);
            if (ealosae.isDNF()) {
                continue;
            }
            if (newTime instanceof DiscretizedFuzzyInterval) {
                if (!strongGreaterEqual((DiscretizedFuzzyInterval) newTime, (DiscretizedFuzzyInterval) ealosae.getEarliestStart())) {
                    continue;
                }
            } else {
                if (!ealosae.getAvailableStartTimeSlot().contains(newTime.longValue())) {
                    continue;
                }
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

    public static FieldElement maxFromFuzzyNumbers(FieldElement a, FieldElement b) {
        if (a instanceof DiscretizedFuzzyInterval && b instanceof DiscretizedFuzzyInterval) {
            LinkedHashSet<DiscretizedFuzzyInterval> set = new LinkedHashSet<>();
            set.add((DiscretizedFuzzyInterval) b);
            set.add((DiscretizedFuzzyInterval) a);
            return maxFromFuzzyNumbers(set);
        }
        return a;
    }

    public static DiscretizedFuzzyInterval maxFromFuzzyNumbers(LinkedHashSet<DiscretizedFuzzyInterval> times) {
        DiscretizedFuzzyInterval first = times.iterator().next();
        int numberOfAlphaCuts = first.getNumberOfAlphaCuts();
        for (DiscretizedFuzzyInterval time : times) {
            if (time.getNumberOfAlphaCuts() != numberOfAlphaCuts) {
                throw new RuntimeException("Max-Befehl nur möglich auf gleichaufgelösten DisretizedFuzzyIntervals");
            }
        }

        AlphaCutSet[] set = new AlphaCutSet[numberOfAlphaCuts];
        for (int i = 0; i < numberOfAlphaCuts; i++) {
            double alpha = first.getAlphaCutSet(i).getAlpha();
            double min = Double.NEGATIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (DiscretizedFuzzyInterval time : times) {
                AlphaCutSet alphaCutSet = time.getAlphaCutSet(i);
                if (alphaCutSet.getMin() > min) {
                    min = alphaCutSet.getMin();
                    if (i > 0) {
                        if (min == set[i - 1].getMin()) {
                            min++;
                        }
                    }
                }
                if (alphaCutSet.getMax() > max) {
                    max = alphaCutSet.getMax();
                    if (i > 0) {
                        if (max == set[i - 1].getMax()) {
                            max--;
                        }
                    }
                }
                set[i] = new AlphaCutSet(alpha, min, max);
            }
        }

        return new DiscretizedFuzzyInterval(set);
    }

    public static boolean strongGreaterEqual(DiscretizedFuzzyInterval o1, DiscretizedFuzzyInterval o2) {
        for (int i = 0; i < o1.getNumberOfAlphaCuts(); i++) {
            AlphaCutSet alphaCutSetO1 = o1.getAlphaCutSet(i);
            AlphaCutSet alphaCutSetO2 = o2.getAlphaCutSet(i);
            if (alphaCutSetO1.getMin() < alphaCutSetO2.getMin()) {
                return false;
            }
            if (alphaCutSetO1.getMax() < alphaCutSetO2.getMax()) {
                return false;
            }
        }
        return true;
    }

}
