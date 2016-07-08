/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.EALOSAEBuilder;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.problem.timeRestricted.TimeRestrictedSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.priorityManagement.EligibleSort;
import applications.mmrcsp.model.schedule.priorityManagement.StandardChooser;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class ParallelScheduleScheme implements ScheduleGenerationScheme<Operation, SchedulingProblem<Operation>> {

    public TreeMultimap<Integer, Operation> order = TreeMultimap.create(Ordering.natural(), Ordering.usingToString());

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH.mm.ss");

    @Override
    public Schedule getSchedule(List<Operation> activityList, SchedulingProblem<Operation> p, TimeSlot timeSlot) {
        boolean debug = false;

        InstanceHandler handler = new InstanceHandler(p.getScheduleManagerBuilder());
        ActivityOnNodeGraph<Operation> graph = p.getActivityOnNodeDiagramm();

        Map<Operation, EarliestAndLatestStartsAndEnds> ealosaes = null;
        if (p instanceof TimeRestrictedSchedulingProblem) {
            TimeRestrictedSchedulingProblem rP = (TimeRestrictedSchedulingProblem) p;
            ealosaes = rP.getEalosaes();
        }
        if (ealosaes == null) {
            ealosaes = EALOSAEBuilder.ealosaes(graph, timeSlot);
        }

        LinkedHashSet<Operation> notActiveOperations = new LinkedHashSet<>();
        LinkedHashSet<Operation> operationsWithoutNotFinishedPreds = new LinkedHashSet<>();
        LinkedHashSet<Operation> eligibleActivites = new LinkedHashSet<>();
        LinkedHashSet<Operation> finishedActivites = new LinkedHashSet<>();

        List<Operation> list = new ArrayList<>(activityList);

        Schedule result = new Schedule(handler);

        /**
         * Setze zunächst auf Simulationsbeginn
         */
        FieldElement t = timeSlot.getFromWhen();
        /**
         * Initialisiere ausführbare Operationen mit denen der ersten
         * Knotenklasse
         */
        for (Operation operation : activityList) {

            /**
             * Wenn keine Vorgänger
             */
            if (graph.getOperation2NodeClasses().get(operation) == 0) {
                eligibleActivites.add(operation);
            } else {
                notActiveOperations.add(operation);
            }
        }

        EligibleSort<Operation> chooser = new StandardChooser(activityList);
        ParallelTools.updateEligibleActivites(t, result, notActiveOperations, operationsWithoutNotFinishedPreds, finishedActivites, eligibleActivites, list, ealosaes, graph);
        /**
         * Schleife über alle Operationen
         */
        TreeSet<FieldElement> scheduledEndTimes = new TreeSet<>();

        int i = 0;
        OperationLoop:
        /**
         * solange noch Element einzuplanen sind
         */

        while (true) {

            System.out.println("Zeit:" + t);

            LinkedList<Operation> currentOperations = new LinkedList<>(eligibleActivites);

            /**
             * Aufsplitten nach Ressource Bestimme für jedes Conveyance-System
             * den Zeitpunkt, an dem es wieder angefragt werden kann. -
             * Map<Resource,FieldElement>
             * - bestimme minimales für die Schleife an sich.
             */
            currentOperations = chooser.getOrderedOperations(currentOperations);
//            System.out.println(currentOperations);

//            changeSupport.firePropertyChange("EligibleSet", null, new ParallelScheduleGenerationScheme.Event(currentOperations, ealosaes, graph));
            /**
             * Schleife für den aktuellen Zeitschritt
             */
            CurrentTimeStepLoop:
            while (!currentOperations.isEmpty()) {
                /**
                 * Bestimme Operation, die eingeplant werden soll.
                 */

                Operation operation = currentOperations.poll();

                boolean canSchedule = true;
                for (Resource k : operation.getRequieredResources()) {
                    /**
                     * #################################################################
                     * START: Bestimmen von genaueren Start/Ziel und
                     * entsprechender Operation
                     * #################################################################
                     */
                    ScheduleRule rule = handler.get(k);
//                    System.out.println("Test:" + k +"\t"+operation);
                    if (!rule.canSchedule(result, operation, t)) {
                        canSchedule = false;
                        continue CurrentTimeStepLoop;
                    }
                }

                if (canSchedule) {
                    /**
                     * Einplanen
                     */
                    FieldElement startOfOperation = t;
//                    if(result.fuzzyWorkloadParameters.get(operation) != null){
//                        FieldElement newStart= result.fuzzyWorkloadParameters.get(operation).adaptedStart;
//                        if(newStart !=  null){
//                            startOfOperation = newStart;
//                        }
//                    }
                    result.schedule(operation, startOfOperation);
                    EALOSAEBuilder.<Operation>updateEaloses(operation, operation.getDuration(), startOfOperation, ealosaes, graph);
                    String st = "Eingeplant: " + startOfOperation.longValue() + "\t" + operation;
                    System.out.println((long) ((DiscretizedFuzzyInterval) t).getC1());
                    System.out.println(st);
                    System.out.println("Ende:" + startOfOperation.add(operation.getDuration()));
                    System.out.println("Lambdas: " + result.fuzzyWorkloadParameters.get(operation));
                    if (operation.getId() == 2) {
                        debug = true;
                    }

                    order.put(i, operation);

                    /**
                     * aktuelle Operation erfüllt Nebenbediungen, daher wird sie
                     * weiter verwendet und aus der Liste entfernt.
                     */
                    activityList.remove(operation);

                    /**
                     * T-Next für das Resource bestimmen
                     */
                    FieldElement c = startOfOperation.add(operation.getDuration());

                    scheduledEndTimes.add(c);
                }

            }
            System.out.println(i);

            if (i == 482) {
                ParallelTools.PLOT = true;
            }
            i++;

//            if (debug) {
//           
//                t = FuzzyFactory.createLinearIntervalFromPoints(1432765843000L,	1432766263000L, 1432766880000L,	1432767300000L);
//                debug = false;
//            } 
//            else {
            FieldElement newT = ParallelTools.getNextTime(t, result, scheduledEndTimes, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes, timeSlot);
            if (!t.equals(timeSlot.getFromWhen()) && !newT.isGreaterThan(t)) {
//                    throw new IllegalArgumentException("Zeit muss wachsen!");
                System.out.println("Zeit muss wachsen");

            }
            t = newT;
//            }
//            t = ParallelTools.maxFromFuzzyNumbers(t, newT);
            /**
             * Ende aktueller Zeitschritt
             */
            ParallelTools.updateEligibleActivites(t, result, notActiveOperations, operationsWithoutNotFinishedPreds, finishedActivites, eligibleActivites, list, ealosaes, graph);
            if (ParallelTools.isEnd(notActiveOperations, null, eligibleActivites)) {
                break;
            }

//            ParallelTools.updateEligibleActivites(t, result, notActiveOperations, operationsWithoutNotFinishedPreds, finishedActivites, eligibleActivites, list, ealosaes, graph);
//            ParallelTools.updateNewTimeOperations(t, result, notActiveOperations, operationsWithoutNotFinishedPreds, finishedActivites, eligibleActivites, list, ealosaes, graph);
            if (t.isGreaterThan(timeSlot.getUntilWhen())) {
                throw new UnknownError("TimeSlot überschritten");
            }

        }
        return result;
    }

}
