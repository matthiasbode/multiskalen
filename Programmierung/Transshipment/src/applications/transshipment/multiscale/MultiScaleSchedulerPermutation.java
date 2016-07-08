///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.transshipment.multiscale;
//
//import applications.transshipment.multiscale.model.Scale;
//import applications.transshipment.multiscale.model.MicroProblem;
//import applications.transshipment.multiscale.initializer.MacroInitializerPermutationImplicit;
//import applications.transshipment.multiscale.initializer.MicroInitializerImplicit;
//import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
//import applications.mmrcsp.model.basics.JoNComponent;
//import applications.mmrcsp.model.basics.TimeSlot;
//import applications.mmrcsp.model.operations.Operation;
//import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
//import applications.mmrcsp.model.schedule.Schedule;
//import applications.mmrcsp.model.schedule.rules.InstanceHandler;
//import applications.transshipment.TransshipmentParameter;
//import applications.transshipment.analysis.Analysis;
//import applications.transshipment.analysis.HeatMapPlotter;
//import applications.transshipment.analysis.LoadUnitOrientatedScheduleWriter;
//import applications.transshipment.analysis.WorkloadPlotter;
//import applications.transshipment.demo.TestClass;
//import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
//import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeImplicitOpsSuperIndividual;
//import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeIndividual;
//
//import applications.transshipment.model.LoadUnitJob;
//import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
//import applications.transshipment.model.operations.transport.RoutingTransportOperation;
//import applications.transshipment.model.problem.MultiJobTerminalProblem;
//import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
//import applications.transshipment.model.resources.storage.LoadUnitStorage;
//import applications.transshipment.model.schedule.LoadUnitJobSchedule;
//import applications.transshipment.model.schedule.realTime.RealTimeSchedule;
//import applications.transshipment.model.schedule.realTime.StartofRoutesCalculator;
//import ga.algorithms.ParallelGA;
//import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
//import ga.basics.Parameters;
//import ga.basics.Population;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.Set;
//import java.util.TreeSet;
//import java.util.logging.Level;
//import math.FieldElement;
//import math.LongValue;
//import math.Tools;
//
///**
// *
// * @author bode
// */
//public class MultiScaleSchedulerPermutation implements TestClass {
//
//    public static LongValue durationTimeSlot = new LongValue(20 * 60 * 1000);
//    public static int numberOfRoutes = 4;
//    MultiJobTerminalProblem problem;
//
//    public RealTimeSchedule calc(MultiJobTerminalProblem originalProblem) {
//
//        List<LoadUnitJob> jobsToSchedule = new ArrayList<>(originalProblem.getJobs());
//
//        FieldElement untilWhen = originalProblem.getTerminal().getTemporalAvailability().getUntilWhen();
//        System.out.println("Gesamt-TimeSlot: " + originalProblem.getTerminal().getTemporalAvailability());
//        /**
//         * Der endgültig Plan, der immer aktualisiert wird.
//         */
//        RealTimeSchedule schedule = new RealTimeSchedule(new InstanceHandler(originalProblem.getScheduleManagerBuilder()));
//
//        /**
//         * Zeitfenster, das
//         */
//        TimeSlot currentTimeSlot = new TimeSlot(originalProblem.getTerminal().getTemporalAvailability().getFromWhen(), originalProblem.getTerminal().getTemporalAvailability().getFromWhen().add(durationTimeSlot));
//
//        List<Set<LoadUnitJob>> connectionComponents = new ArrayList<>();
//        for (JoNComponent<LoadUnitJob> joNComponent : originalProblem.getJobOnNodeDiagramm().getConnectionComponents()) {
//            connectionComponents.add(new HashSet<>(joNComponent.vertexSet()));
//        }
//
////        MultiJobTerminalProblem macroProblem = MultiJobTerminalProblemFactory.createForMultiScale(schedule, originalProblem.getTerminal(), originalProblem.getTrains(), jobsToSchedule, originalProblem.getScheduleManagerBuilder(), originalProblem.getRouteFinder().getShortestPathAlgorithm(), originalProblem.getRouteFinder().getBewertung());
//        MultiJobTerminalProblem macroProblem = originalProblem.clone();
//        Population<PermutationModeIndividual> modeInds = null;
//        Population<ImplicitOperationIndividual> opInds = null;
//        System.out.println("###################################");
//
//        LoadUnitOrientatedScheduleWriter sw = new LoadUnitOrientatedScheduleWriter();
//
//        while (currentTimeSlot.getUntilWhen().isLowerThan(untilWhen)) {
//            schedule.currentTime = currentTimeSlot.getFromWhen().longValue();
//            long currentStart = currentTimeSlot.getFromWhen().longValue();
//            System.out.println("Schleifendurchlauf");
//            System.out.println(currentTimeSlot);
//            System.out.println("jobsToSchedule: " + jobsToSchedule.size());
//
//            /**
//             * Optimierung auf Macro-Ebene
//             */
//            MultipleSpeciesCoevolvingParallelGA<PermutationModeImplicitOpsSuperIndividual> macro = MacroInitializerPermutationImplicit.initMacro(connectionComponents, macroProblem, opInds, modeInds, currentStart);
//            macro.run();
//            PermutationModeImplicitOpsSuperIndividual fittestMacro = macro.getSuperPopulation().getFittestIndividual();
//            LoadUnitJobSchedule macroSchedule = fittestMacro.getSchedule();
//
//            File file = new File(TransshipmentParameter.debugFolder, "Macro_" + (TimeSlot.longToFormattedDateString(currentStart)));
//            file.mkdir();
//            sw.analysis(macroSchedule, originalProblem, file);
//
//            opInds = macro.getSubPopulation(ImplicitOperationIndividual.class);
//            modeInds = macro.getSubPopulation(PermutationModeIndividual.class);
//
//            /**
//             * Problem: Welche Operationen sollen eingeplant werden? Noch
//             * vorsichtshalber ein paar mehr aus dem Zeitfenster, falls möglich?
//             * oder eher erst einmal nicht?
//             */
//            Collection<RoutingTransportOperation> operationsToSchedule = macroSchedule.getActiveSet(currentTimeSlot);
//            System.out.println("DNF Macro: " + fittestMacro.getDNF());
//            System.out.println("Fitness Macro: " + fittestMacro.getFitness());
//            System.out.println("From Makro2Mikro: " + operationsToSchedule.size() + "/" + macroSchedule.getScheduledOperations().size());
//
//            /*
//             * Erzeuge Teilproblem
//             */
//            ActivityOnNodeGraph<RoutingTransportOperation> aon = (ActivityOnNodeGraph<RoutingTransportOperation>) fittestMacro.additionalObjects.get(Schedule.KEY_AON);
//            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae  = (Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds>) fittestMacro.additionalObjects.get(Schedule.KEY_EALOSAE);
//            if(aon == null || ealosae == null){
//                throw new NoSuchElementException("Activity-On-Node oder EALOSAE nicht gefunden");
//            }
//            
//            
//            ActivityOnNodeGraph<RoutingTransportOperation> subGraph = aon.getSubGraph(operationsToSchedule);
//            MicroProblem subProblem = new MicroProblem(currentTimeSlot, macroSchedule, operationsToSchedule, ealosae, subGraph, originalProblem);
//            ParallelGA<ImplicitOperationIndividual> micro = MicroInitializerImplicit.initMicro(subProblem);
//            micro.run();
//            ImplicitOperationIndividual fittestMicro = micro.getPopulation().getFittestIndividual();
//            System.out.println("Fitness Micro: " + fittestMicro.getFitness());
//            LoadUnitJobSchedule microSchedule = fittestMicro.schedule;
//            operationsToSchedule.removeAll(microSchedule.getScheduledRoutingTransportOperations());
//            System.out.println("Nicht eingeplante Operationen:");
//            for (RoutingTransportOperation routingTransportOperation : operationsToSchedule) {
//                System.out.println(routingTransportOperation);
//            }
//
//            file = new File(TransshipmentParameter.debugFolder, "Micro" + (TimeSlot.longToFormattedDateString(currentStart)));
//            file.mkdir();
//            sw.analysis(microSchedule, originalProblem, file);
//
//            /**
//             * Aktualisiere den Gesamtschedule
//             */
//            schedule.appendSchedule(microSchedule);
//
//            /**
//             * Update der Constraints auf Macro-Ebene
//             */
//            originalProblem.getScheduleManagerBuilder().updateStartTimesForResources(schedule, problem.getTerminal().getConveyanceSystems());
//
//            /**
//             * Bestimmung der neuen Startzeit.
//             */
//            FieldElement nextStart = null;
//            for (ConveyanceSystem conveyanceSystem : originalProblem.getTerminal().getConveyanceSystems()) {
//                TreeSet<Operation> operationsForResource = schedule.getOperationsForResource(conveyanceSystem);
//                Operation lastOperation = operationsForResource.last();
//                FieldElement lastStart = schedule.get(lastOperation);
//                FieldElement lastEnd = lastStart.add(lastOperation.getDuration());
//                System.out.println(conveyanceSystem + "--->" + TimeSlot.longToFormattedDateString(lastEnd.longValue()));
//                nextStart = nextStart == null ? lastEnd : Tools.min(lastEnd, nextStart);
//            }
//            currentTimeSlot.move(durationTimeSlot);
//            currentTimeSlot.setFromWhen(nextStart);
//
//            /**
//             * Streiche DNFs und bereits eingeplante Jobs
//             */
//            jobsToSchedule.removeAll(schedule.getDnfJobs());
//            jobsToSchedule.removeAll(schedule.getScheduledJobs());
//
//            /**
//             * Neue Startpositionen für die Jobs setzten.
//             */
//            HashMap<LoadUnitJob, LoadUnitStorage> currentPosition = StartofRoutesCalculator.getCurrentPosition(originalProblem.getJobs(), schedule);
//            for (LoadUnitJob loadUnitJob : currentPosition.keySet()) {
//                loadUnitJob.setCurrentOrigin(currentPosition.get(loadUnitJob));
//            }
//
//            /**
//             * MacroProblem neudefinieren.
//             */
//            macroProblem = MultiJobTerminalProblemFactory.createForMultiScale(schedule, originalProblem.getTerminal(), originalProblem.getTrains(), jobsToSchedule, originalProblem.getScheduleManagerBuilder(),  originalProblem.getRouteFinder().getBewertung());
//            file = new File(TransshipmentParameter.debugFolder, "AllOver" + (TimeSlot.longToFormattedDateString(currentStart)));
//            file.mkdir();
//            sw.analysis(schedule, originalProblem, file);
//
//            System.out.println("Scheduled Jobs: " + schedule.getScheduledJobs().size());
//            System.out.println("DNFs Gesamtschedule: " + schedule.getDnfJobs().size());
//            System.out.println("#################################");
//        }
//
//        return schedule;
//    }
//
//    public static void main(String[] args) {
//        MultiScaleSchedulerPermutation scheduler = new MultiScaleSchedulerPermutation();
//
//        applications.transshipment.generator.projekte.duisburg.DuisburgGenerator g = new applications.transshipment.generator.projekte.duisburg.DuisburgGenerator();
//        MultiJobTerminalProblem p = g.generateTerminalProblem(Scale.macro, numberOfRoutes);
//
//        RealTimeSchedule schedule = scheduler.calc(p);
//
//        Parameters.logger.log(Level.INFO, "Operationen gesamt: " + schedule.getScheduledOperations().size());
//
//        ArrayList<Analysis> analyser = new ArrayList<>();
//        analyser.add(new LoadUnitOrientatedScheduleWriter());
//        analyser.add(new WorkloadPlotter());
//        analyser.add(new HeatMapPlotter());
//
//        for (Analysis a : analyser) {
//            a.analysis(schedule, p, TransshipmentParameter.debugFolder);
//        }
//
//    }
//
//    @Override
//    public LoadUnitJobSchedule getBestSchedule() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
//        calc(problem);
//    }
//
//}
