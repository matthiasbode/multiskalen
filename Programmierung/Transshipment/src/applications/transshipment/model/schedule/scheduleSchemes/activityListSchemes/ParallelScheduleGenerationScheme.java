/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes;

import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import applications.transshipment.model.schedule.scheduleSchemes.StorageToolMethods;
import applications.mmrcsp.model.schedule.priorityManagement.EligibleSort;
import applications.mmrcsp.model.schedule.priorityManagement.StandardChooser;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.EALOSAEBuilder;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.dnf.DNFTreatment;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.rules.ConveyanceSystemRule;
import applications.transshipment.model.schedule.scheduleSchemes.Tools;
import applications.transshipment.model.schedule.scheduleSchemes.TransshipmentParallelTools;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class ParallelScheduleGenerationScheme implements Transshipment_ActivityListScheduleScheme {

    private final DNFTreatment dnfTreatment;

    public ParallelScheduleGenerationScheme(DNFTreatment dnfTreatment) {
        this.dnfTreatment = dnfTreatment;
    }

    @Override
    public LoadUnitJobSchedule getSchedule(LoadUnitJobSchedule result, List<RoutingTransportOperation> activityList, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> originalEalosaes, TerminalProblem p, ActivityOnNodeGraph<RoutingTransportOperation> graph, TimeSlot timeSlot) {
        if (result == null) {
            throw new NullPointerException("Eingabeschedule ist NULL");
        }
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = Tools.cloneEalosaes(originalEalosaes);

        LinkedHashSet<RoutingTransportOperation> notActiveOperations = new LinkedHashSet<>();
        LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds = new LinkedHashSet<>();
        LinkedHashSet<RoutingTransportOperation> eligibleActivites = new LinkedHashSet<>();

        List<RoutingTransportOperation> list = new ArrayList<>(activityList);

        /**
         * Setze zunächst auf Simulationsbeginn
         */
        FieldElement t = timeSlot.getFromWhen();

        operationsWithoutNotFinishedPreds = TransshipmentParallelTools.initOperationsWithoutNotFinishedPreds(result, graph.vertexSet(), graph);
        notActiveOperations.addAll(graph.vertexSet());
        notActiveOperations.removeAll(operationsWithoutNotFinishedPreds);
        /**
         * Entferne die bereits eingeplanten
         */
        for (Iterator<RoutingTransportOperation> iterator = notActiveOperations.iterator(); iterator.hasNext();) {
            RoutingTransportOperation rtop = iterator.next();
            if (result.isScheduled(rtop)) {
                iterator.remove();
            }
        }

        TransshipmentParallelTools.updateEligibleActivites(t, result, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes, graph);

        TreeSet<FieldElement> scheduledEndTimes = new TreeSet<>();
        LinkedHashMap<Resource, FieldElement> newStartTimesForResource = ScheduleManagerBuilder.getNewStartTimesForResource(t, result, p.getTerminal().getConveyanceSystems());

        for (Resource keySet : newStartTimesForResource.keySet()) {
            FieldElement verfuegbarAb = newStartTimesForResource.get(keySet);
            if (!t.equals(verfuegbarAb) && verfuegbarAb.isGreaterThan(t)) {
                scheduledEndTimes.add(verfuegbarAb);
            }
        }

        /**
         * Sortiert die Operationen nach ihrer List in der übergebenen
         * ActivityList.
         */
        EligibleSort<RoutingTransportOperation> chooser = new StandardChooser<>(list);

        /**
         * Schleife über alle Operationen
         */
        OperationLoop:
        /**
         * solange noch Element einzuplanen sind
         */

        while (!TransshipmentParallelTools.isEnd(notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites) && t.isLowerThan(timeSlot.getUntilWhen())) {

            LinkedList<RoutingTransportOperation> currentOperations = new LinkedList<>(eligibleActivites);

            /**
             * Aufsplitten nach Ressource Bestimme für jedes Conveyance-System
             * den Zeitpunkt, an dem es wieder angefragt werden kann. -
             * Map<ConveyanceSystem,FieldElement>
             * - bestimme minimales für die Schleife an sich.
             */
            currentOperations = chooser.getOrderedOperations(currentOperations);

            if (TransshipmentParameter.DEBUG) {
                System.out.println("#############################################################");
                System.out.println("Current Time: " + TimeSlot.longToFormattedDateString(t.longValue()));
//                System.out.println("Ausführbare Operationen: " + eligibleActivites.size());
//                System.out.println("Nicht Ausführbare Operationen: " + notActiveOperations.size());
//                System.out.println("Operationen ohne Vorgänger: " + operationsWithoutNotFinishedPreds.size());
                System.out.println("DNF: " + result.getDnfJobs().size());
            }
            /**
             * Schleife für den aktuellen Zeitschritt
             */
            CurrentTimeStepLoop:
            while (!currentOperations.isEmpty()) {
                /**
                 * Bestimme Operation, die eingeplant werden soll.
                 */

                RoutingTransportOperation rtop = currentOperations.poll();
                ConveyanceSystem resource = rtop.getResource();
                ConveyanceSystemRule rule = (ConveyanceSystemRule) result.getHandler().get(resource);

                /**
                 * TODO: Warum kann das passieren?
                 */
                if (result.getDnfJobs().contains(rtop.getJob())) {
//                    System.err.println("Job bereits schon in den DNFs");
                    continue;
                }

                /**
                 * #################################################################
                 * START: Bestimmen von genaueren Start/Ziel und entsprechender
                 * Operation
                 * #################################################################
                 */
                FieldElement startStore = t.add(rtop.getDuration());
                List<StoreOperation> storageOperationsAtDestination = StorageToolMethods.getStorageOperationsAtDestination(startStore,p, rtop, ealosaes, result, result.getHandler());
                if (storageOperationsAtDestination.isEmpty()) {
                    continue;
                }

                int trials = 0;
                DESTINATIONLOOP:
                while (trials < TransshipmentParameter.MAX_DESTINATIONTEST && !storageOperationsAtDestination.isEmpty()) {

                    StoreOperation storeAtDestination = storageOperationsAtDestination.remove(0);
                    LoadUnitStorage origin = result.getOrigin(rtop);
                    MultiScaleTransportOperation j = rule.getDetailedOperation(rtop, origin, storeAtDestination.getResource());

                    /**
                     * #################################################################
                     * Bestimmen von genaueren Start/Ziel ENDE
                     * #################################################################
                     */
                    TransportBundle bundle = null;

                    /**
                     * Ressourcenanforderungen zu hoch, Zeitfenster muss
                     * angepasst werden
                     */
                    if (!rule.canSchedule(result, j, t)) {
                        continue;
                    }

                    bundle = rule.getBundle(result, j, t);

                    if (bundle == null) {
                        throw new UnknownError("Nicht einplanbar!");
                    }

                    /**
                     * Test, ob Transport fristgerecht gestartet werden kann.
                     */
                    if (bundle.getStartTime(j).isGreaterThan(ealosaes.get(rtop).getLatestStart())) {
                        continue;
                    }
                    /**
                     * Test, ob Transport fristgerecht abgearbeitet werden kann.
                     */
                    if (!bundle.setStoreAtDestination(storeAtDestination, rtop, ealosaes, result)) {
                        continue;
                    }
                    TimeSlot availableStartTimeSlot = ealosaes.get(j.getRoutingTransportOperation()).getAvailableStartTimeSlot();
                    if (!availableStartTimeSlot.contains(t.longValue())) {
                        System.err.println("Passt nicht: " + availableStartTimeSlot + ":-->" + TimeSlot.longToFormattedDateString(t.longValue()));
                    }
                    if (TransshipmentParameter.DEBUG) {
                        if (bundle.getStartTime(j) instanceof LongValue) {
                            System.out.println("Eingeplant: " + TimeSlot.longToFormattedDateString(bundle.getStartTime(j).longValue()) + ":" + j);
                        }
                        else{
                            DiscretizedFuzzyInterval startTime = (DiscretizedFuzzyInterval) bundle.getStartTime(j);
                            System.out.println("Eingeplant: " + TimeSlot.longToFormattedDateString((long) startTime.getC1()) + TimeSlot.longToFormattedDateString((long) startTime.getC2())+":" + j);
                        }
                    }

                    /**
                     * Einplanen und Anpassen der EALOSAEs
                     */
                    result.scheduleBundle(bundle, t);
                    EALOSAEBuilder.<RoutingTransportOperation>updateEaloses(j.getRoutingTransportOperation(), j.getDuration(), bundle.getStartTime(j), ealosaes, graph);
                    list.remove(rtop);

                    /**
                     * T-Next für das ConveyanceSystem bestimmen
                     */
                    FieldElement c = bundle.getStartTime(j).add(j.getDuration());
                    scheduledEndTimes.add(c);
                    continue CurrentTimeStepLoop;
                }
            }
            /**
             * Ende aktueller Zeitschritt
             */
            TransshipmentParallelTools.testDNF(dnfTreatment, t, result, notActiveOperations, operationsWithoutNotFinishedPreds, graph.vertexSet(), eligibleActivites, ealosaes, graph);
            FieldElement newT = TransshipmentParallelTools.getNextTime(t, scheduledEndTimes, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes, timeSlot, result);
            if (newT == null) {
                throw new UnknownError("Null als nächsten Zeitpunkt bestimmt");
            }
            t = newT;
            scheduledEndTimes.remove(t);
            for (Iterator<FieldElement> it = scheduledEndTimes.iterator(); it.hasNext();) {
                FieldElement fieldElement = it.next();
                if (fieldElement.isLowerThan(t)) {
                    it.remove();
                }
            }
            TransshipmentParallelTools.updateEligibleActivites(t, result, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes, graph);
        }

        TransshipmentParameter.logger.finer("Anzahl DNF-Jobs:" + result.getDnfJobs().size());
        TransshipmentParameter.logger.finer("Anzahl eingeplanter Jobs:" + result.getScheduledJobs().size());
        result.ealosae = ealosaes;
        result.t = t.clone();
        return result;
    }

}
