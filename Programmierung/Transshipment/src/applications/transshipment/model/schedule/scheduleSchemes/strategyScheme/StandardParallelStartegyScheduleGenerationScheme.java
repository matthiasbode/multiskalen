/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.strategyScheme;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.EALOSAEBuilder;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.mmrcsp.model.schedule.scheduleSchemes.ParallelTools;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
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
import applications.transshipment.model.schedule.scheduleSchemes.StorageToolMethods;
import applications.transshipment.model.schedule.scheduleSchemes.Tools;
import applications.transshipment.model.schedule.scheduleSchemes.TransshipmentParallelTools;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.AlphaCutSet;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeSet;
import math.Field;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class StandardParallelStartegyScheduleGenerationScheme implements Transshipment_ImplicitScheduleGenerationScheme {

    private final DNFTreatment dnfTreatment;

    public StandardParallelStartegyScheduleGenerationScheme(DNFTreatment dnfTreatment) {
        this.dnfTreatment = dnfTreatment;
    }

    @Override

    public LoadUnitJobSchedule getSchedule(LoadUnitJobSchedule result, Collection<RoutingTransportOperation> operationsToSchedule, List<OperationPriorityRules.Identifier> inputDecisionStrategies, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> originalEalosaes, TerminalProblem p, ActivityOnNodeGraph<RoutingTransportOperation> graph, TimeSlot timeSlot) {
        if (result == null) {
            throw new NullPointerException("Eingabeschedule ist NULL");
        }
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = Tools.cloneEalosaes(originalEalosaes);
        for (Iterator<RoutingTransportOperation> iterator = operationsToSchedule.iterator(); iterator.hasNext();) {
            RoutingTransportOperation top = iterator.next();
            if (result.isScheduled(top)) {
                iterator.remove();
            }
        }

        List<OperationPriorityRules.Identifier> decisionStrategies = new ArrayList<>(inputDecisionStrategies);

        LinkedHashSet<RoutingTransportOperation> notActiveOperations = new LinkedHashSet<>();
        LinkedHashSet<RoutingTransportOperation> operationsWithoutNotFinishedPreds;
        LinkedHashSet<RoutingTransportOperation> eligibleActivites = new LinkedHashSet<>();

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
         * Schleife über alle Operationen
         */
        OperationPriorityRules strategies = new OperationPriorityRules(result, graph, ealosaes);

        OperationLoop:
        /**
         * solange noch Element einzuplanen sind
         */

        while (!TransshipmentParallelTools.isEnd(notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites) && t.isLowerThan(timeSlot.getUntilWhen())) {

            LinkedList<RoutingTransportOperation> currentOperations = new LinkedList<>(eligibleActivites);
            currentOperations.retainAll(operationsToSchedule);

            if (!TransshipmentParameter.TimeStepBasedPriorityDetermination) {
                strategies.setAdditionalInformation(result, eligibleActivites, null, null);
            }

            /**
             * Operationen nach momentaner Strategie sortieren
             */
            long durationOfInterval = OperationPriorityRules.lengthOfInterval;
            int index = (int) (decisionStrategies.size() - Math.ceil(p.getOptimizationTimeSlot().getUntilWhen().longValue() - t.longValue()) / new Double(durationOfInterval));
            OperationPriorityRules.Identifier strategy = decisionStrategies.get(index);
            Comparator<RoutingTransportOperation> comp;
            if (TransshipmentParameter.TimeStepBasedPriorityDetermination) {
                comp = strategies.getMap(strategy, result, currentOperations, ealosaes, graph);
            } else {
                comp = strategies.getMap(strategy);
            }

            Collections.sort(currentOperations, comp);
//            if (TimeSlot.longToFormattedDateString(t.longValue()).equals("03.02.2011;00:17:39")) {
//                System.out.println("angucken");
//            }
            if (TransshipmentParameter.DEBUG) {
                System.out.println("#############################################################");
                System.out.println("Current Time: " + TimeSlot.longToFormattedDateString(t.longValue()));
//                if(TimeSlot.longToFormattedDateString(t.longValue()).equals("02.02.2011;22:25:17")){
//                    System.out.println("gucken!");
//                }
                System.out.println(t);
//                System.out.println("Ausführbare Operationen: " + eligibleActivites.size());
//                System.out.println("Nicht Ausführbare Operationen: " + notActiveOperations.size());
//                System.out.println("Operationen ohne Vorgänger: " + operationsWithoutNotFinishedPreds.size());
                System.out.println("DNF: " + result.getDnfJobs().size());
                System.out.println(strategy);
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

                FieldElement tR = t;

                if (t instanceof DiscretizedFuzzyInterval) {
                    DiscretizedFuzzyInterval tt = (DiscretizedFuzzyInterval) t.clone();
                    DiscretizedFuzzyInterval earliestStart = (DiscretizedFuzzyInterval) ealosaes.get(rtop).getEarliestStart();

                    AlphaCutSet[] alphaCutSets = new AlphaCutSet[tt.getAlphaCutSets().length];
                    for (int i = 0; i < tt.getAlphaCutSets().length; i++) {
                        AlphaCutSet alphaCutSetTT = tt.getAlphaCutSets()[i];
                        AlphaCutSet alphaCutSetES = earliestStart.getAlphaCutSets()[i];
                        alphaCutSets[i] = new AlphaCutSet(alphaCutSetTT.getAlpha(), Math.max(alphaCutSetTT.getMin(), alphaCutSetES.getMin()), Math.max(alphaCutSetTT.getMax(), alphaCutSetES.getMax()));
                    }
                    tR = new DiscretizedFuzzyInterval(alphaCutSets);
                }

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
                FieldElement startStore = tR.add(rtop.getDuration());
                List<StoreOperation> storageOperationsAtDestination = StorageToolMethods.getStorageOperationsAtDestination(startStore, p, rtop, ealosaes, result, result.getHandler());
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
                    if (!rule.canSchedule(result, j, tR)) {
                        trials++;
                        continue;
                    }

                    bundle = rule.getBundle(result, j, tR);

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
                    if (!availableStartTimeSlot.contains(tR.longValue())) {
                        System.err.println("Passt nicht: " + availableStartTimeSlot + ":-->" + TimeSlot.longToFormattedDateString(tR.longValue()));
                    }
                    if (TransshipmentParameter.DEBUG) {
                        if (bundle.getStartTime(j) instanceof LongValue) {
                            System.out.println("Eingeplant: " + TimeSlot.longToFormattedDateString(bundle.getStartTime(j).longValue()) + ":" + j);
                            System.out.println("Endzeit: \t" + TimeSlot.longToFormattedDateString(bundle.getStartTime(j).add(j.getDuration()).longValue()));
                        } else {
                            DiscretizedFuzzyInterval startTime = (DiscretizedFuzzyInterval) bundle.getStartTime(j);
                            System.out.println("Eingeplant: \t" + startTime + ":" + j);
                            System.out.println("Endzeit: \t" + startTime.add((FuzzyNumber) j.getDuration()));
                        }
                    }

                    /**
                     * Einplanen und Anpassen der EALOSAEs
                     */
                    result.scheduleBundle(bundle, tR);
                    EALOSAEBuilder.<RoutingTransportOperation>updateEaloses(j.getRoutingTransportOperation(), j.getDuration(), bundle.getStartTime(j), ealosaes, graph);
                    Collections.sort(currentOperations, comp);

//                    {
//                        LambdaOperation o = j;
//                        FieldElement start = bundle.getStartTime(o);
//                        LinearizedFunction1d necessityFunction = FuzzyDemandUtilities.getNecessityFunction(o, (FuzzyInterval) start);
//                        FuzzyNumber endTime = (FuzzyNumber) start.add(o.getDuration());
//                        FuzzyNumber startTime = (FuzzyNumber) start;
//
//                        //            if (b.getId() == 255) {
//                        long dx = 1000;
//                        GregorianCalendar endeTS = new GregorianCalendar(2011, 1, 2, 22, 45);
//                        GregorianCalendar startTS = new GregorianCalendar(2011, 1, 2, 22, 00);
//                        FuzzyFunctionPlotter plotter = new FuzzyFunctionPlotter("Debugging");
//                        plotter.addFunction(startTime.membership, startTS.getTimeInMillis(), endeTS.getTimeInMillis(), dx, "Start");
//                        plotter.addFunction(endTime.getMembershipFunction(), startTS.getTimeInMillis(), endeTS.getTimeInMillis(), dx, "End");
//                        JFreeChart areaChart = plotter.getAreaChart(TimeSlot.create(startTS.getTimeInMillis(), endeTS.getTimeInMillis()));
//                        File file = new File("/home/bode/Dokumente/Promo/Ergebnisse/Problems", o.getId() + "_StartAndEnd.png");
//                        try {
//                            ChartUtilities.saveChartAsPNG(file, areaChart, 2200, 435);
//                        } catch (IOException ex) {
//                            Logger.getLogger(FuzzyDemandUtilities.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//
//                        plotter = new FuzzyFunctionPlotter("Debugging");
//                        plotter.addFunction(necessityFunction, startTS.getTimeInMillis(), endeTS.getTimeInMillis(), dx, "Notwendigkeit");
//                        areaChart = plotter.getChart(TimeSlot.create(startTS.getTimeInMillis(), endeTS.getTimeInMillis()));
//                        file = new File("/home/bode/Dokumente/Promo/Ergebnisse/Problems", o.getId() + "_Notwendigkeit.png");
//                        try {
//                            ChartUtilities.saveChartAsPNG(file, areaChart, 2200, 435);
//                        } catch (IOException ex) {
//                            Logger.getLogger(FuzzyDemandUtilities.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
                    /**
                     * T-Next für das ConveyanceSystem bestimmen
                     */
                    FieldElement c = bundle.getStartTime(j).add(j.getDuration());
                    scheduledEndTimes.add(c);
                    continue CurrentTimeStepLoop;
                }
                /**
                 * Für die aktuelle Operation konnte nichts eingeplant werden.
                 */
                continue;
            }
            /**
             * Ende aktueller Zeitschritt
             */
            TransshipmentParallelTools.testDNF(dnfTreatment, t, result, notActiveOperations, operationsWithoutNotFinishedPreds, graph.vertexSet(), eligibleActivites, ealosaes, graph);

            FieldElement newT = TransshipmentParallelTools.getNextTime(t, scheduledEndTimes, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes, timeSlot, result);
//            if (newT.isGreaterThan(timeSlot.getUntilWhen())) {
//                System.out.println("angucken!");
//                ArrayList<RoutingTransportOperation> arrayList = new ArrayList<>(operationsToSchedule);
//                arrayList.removeAll(result.getScheduledRoutingTransportOperations());
//
//                ArrayList<RoutingTransportOperation> restelgiable = new ArrayList<>(eligibleActivites);
//                restelgiable.retainAll(arrayList);
//
//                ArrayList<RoutingTransportOperation> restnoNotFinished = new ArrayList<>(operationsWithoutNotFinishedPreds);
//                restnoNotFinished.retainAll(arrayList);
//
//                ArrayList<RoutingTransportOperation> restNotActive = new ArrayList<>(notActiveOperations);
//                restNotActive.retainAll(arrayList);
//                newT = TransshipmentParallelTools.getNextTime(t, scheduledEndTimes, notActiveOperations, operationsWithoutNotFinishedPreds, eligibleActivites, ealosaes, timeSlot, result);
//            }
            if (newT == null) {
                throw new UnknownError("Null als nächsten Zeitpunkt bestimmt");
            }
//            if (newT instanceof DiscretizedFuzzyInterval) {
//                t = DiscretizedFuzzyInterval.max((DiscretizedFuzzyInterval) t, (DiscretizedFuzzyInterval) newT);
//            } else {
            t = newT;
//            }

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
//        ArrayList<RoutingTransportOperation> arrayList = new ArrayList<>(operationsToSchedule);
//        arrayList.removeAll(result.getScheduledRoutingTransportOperations());
//
//        ArrayList<RoutingTransportOperation> restelgiable = new ArrayList<>(eligibleActivites);
//        restelgiable.retainAll(arrayList);
//
//        ArrayList<RoutingTransportOperation> restnoNotFinished = new ArrayList<>(operationsWithoutNotFinishedPreds);
//        restnoNotFinished.retainAll(arrayList);
        result.t = t.clone();
        return result;
    }

}
