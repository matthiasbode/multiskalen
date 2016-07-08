/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.scheduleSchemes.old;

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
import applications.mmrcsp.model.schedule.scheduleSchemes.ParallelTools;
import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class ParallelScheduleScheme_old implements ScheduleGenerationScheme<Operation, SchedulingProblem<Operation>> {

    @Override
    public Schedule getSchedule(List<Operation> activityList, SchedulingProblem<Operation> p, TimeSlot timeSlot) {
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
        OperationLoop:
        /**
         * solange noch Element einzuplanen sind
         */

        while (true) {

            LinkedList<Operation> currentOperations = new LinkedList<>(eligibleActivites);

            /**
             * Aufsplitten nach Ressource Bestimme für jedes Conveyance-System
             * den Zeitpunkt, an dem es wieder angefragt werden kann. -
             * Map<Resource,FieldElement>
             * - bestimme minimales für die Schleife an sich.
             */
            currentOperations = chooser.getOrderedOperations(currentOperations);

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

                    if (!rule.canSchedule(result, operation, t)) {
                        canSchedule = false;
                        continue CurrentTimeStepLoop;
                    }
                }

                if (canSchedule) {
                    /**
                     * Einplanen
                     */
                    result.schedule(operation, t);
//                    System.out.println("Eingeplant: "+t.longValue() );
//                    System.out.println(operation);

                    /**
                     * aktuelle Operation erfüllt Nebenbediungen, daher wird sie
                     * weiter verwendet und aus der Liste entfernt.
                     */
                    activityList.remove(operation);
                }
                /**
                 * T-Next für das Resource bestimmen
                 */
                FieldElement c = t.add(operation.getDuration());
                
                scheduledEndTimes.add(c);
                
            }
            /**
             * Ende aktueller Zeitschritt
             */
            ParallelTools.updateEligibleActivites(t, result, notActiveOperations, operationsWithoutNotFinishedPreds, finishedActivites, eligibleActivites, list, ealosaes, graph);
            if (ParallelTools.isEnd(notActiveOperations, null, eligibleActivites)) {
                break;
            }
            FieldElement newT = ParallelTools_old.getNextTime(t, scheduledEndTimes, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes);
            if (!t.equals(timeSlot.getFromWhen()) && !newT.isGreaterThan(t)) {
                newT = ParallelTools_old.getNextTime(t, scheduledEndTimes, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes);
                throw new IllegalArgumentException("Zeit muss wachsen!");
            }
            t = newT;
            
            if (t.isGreaterThan(timeSlot.getUntilWhen())) {
                throw new UnknownError("TimeSlot überschritten");
            }

        }

        return result;
    }

}
