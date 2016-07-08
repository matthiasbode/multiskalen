///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.transshipment.multiscale;
//
//import applications.transshipment.multiscale.model.Scale;
//import applications.transshipment.multiscale.model.MicroProblem;
//import applications.transshipment.multiscale.initializer.MacroInitializerImplicit_JSon;
//import applications.transshipment.multiscale.initializer.MicroInitializerDirect;
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
//import applications.transshipment.analysis.ScheduleWriter;
//import applications.transshipment.analysis.WorkloadPlotter;
//import applications.transshipment.ga.directModeImplicitOps.DirectModeImplicitOpsSuperIndividual;
//import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
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
//import ga.individuals.IntegerIndividual;
//import ga.individuals.subList.ListIndividual;
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
//public class MultiScaleSchedulerOpImplicitModeJSon {
//
//    public static LongValue durationTimeSlot = new LongValue(30 * 60 * 1000);
//    public static int numberOfRoutes = 9;
//
//    public RealTimeSchedule calc(MultiJobTerminalProblem problem) {
//        List<LoadUnitJob> jobsToSchedule = new ArrayList<>(problem.getJobs());
//
//        FieldElement untilWhen = problem.getTerminal().getTemporalAvailability().getUntilWhen();
//        System.out.println("Gesamt-TimeSlot: " + problem.getTerminal().getTemporalAvailability());
//        /**
//         * Der endgültig Plan, der immer aktualisiert wird.
//         */
//        RealTimeSchedule schedule = new RealTimeSchedule(new InstanceHandler(problem.getScheduleManagerBuilder()));
//
//        /**
//         * Zeitfenster, das
//         */
//        TimeSlot currentTimeSlot = new TimeSlot(problem.getTerminal().getTemporalAvailability().getFromWhen(), problem.getTerminal().getTemporalAvailability().getFromWhen().add(durationTimeSlot));
//
//        MultiJobTerminalProblem macroProblem = problem.clone();
//        Population<IntegerIndividual> modeInds = null;
//        Population<ImplicitOperationIndividual> opInds = null;
//
//        System.out.println("###################################");
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
//            List<Set<LoadUnitJob>> connectionComponents = new ArrayList<>();
//            for (JoNComponent<LoadUnitJob> joNComponent : macroProblem.getJobOnNodeDiagramm().getConnectionComponents()) {
//                connectionComponents.add(new HashSet<>(joNComponent.vertexSet()));
//            }
//            MultipleSpeciesCoevolvingParallelGA<DirectModeImplicitOpsSuperIndividual> macro = MacroInitializerImplicit_JSon.initMacro(problem, opInds, modeInds);
//            macro.run();
//            DirectModeImplicitOpsSuperIndividual fittestMacro = macro.getSuperPopulation().getFittestIndividual();
//            LoadUnitJobSchedule macroSchedule = fittestMacro.getSchedule();
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
//            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae = (Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds>) fittestMacro.additionalObjects.get(Schedule.KEY_EALOSAE);
//
//            if (aon == null || ealosae == null) {
//                throw new NoSuchElementException("AON oder EALOSAE nicht gesetzt");
//            }
//
//            ActivityOnNodeGraph<RoutingTransportOperation> subGraph = aon.getSubGraph(operationsToSchedule);
//            MicroProblem subProblem = new MicroProblem(currentTimeSlot, macroSchedule, operationsToSchedule, ealosae, subGraph, problem);
//
//            ParallelGA<ListIndividual<RoutingTransportOperation>> micro = MicroInitializerDirect.initMicro(subProblem);
//            micro.run();
//            ListIndividual<RoutingTransportOperation> fittestMicro = micro.getPopulation().getFittestIndividual();
//            System.out.println("Fitness Micro: " + fittestMicro.getFitness());
//            LoadUnitJobSchedule microSchedule = (LoadUnitJobSchedule) fittestMicro.additionalObjects.get(Schedule.KEY_SCHEDULE);
//
//            /**
//             * Aktualisiere den Gesamtschedule
//             */
//            schedule.appendSchedule(microSchedule);
//
//            /**
//             * Update der Constraints auf Macro-Ebene
//             */
//            problem.getScheduleManagerBuilder().updateStartTimesForResources(schedule,problem.getTerminal().getConveyanceSystems());
//
//            /**
//             * Bestimmung der neuen Startzeit.
//             */
//            FieldElement nextStart = null;
//            for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
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
//            HashMap<LoadUnitJob, LoadUnitStorage> currentPosition = StartofRoutesCalculator.getCurrentPosition(problem.getJobs(), schedule);
//            for (LoadUnitJob loadUnitJob : currentPosition.keySet()) {
//                loadUnitJob.setCurrentOrigin(currentPosition.get(loadUnitJob));
//            }
//
//            /**
//             * MacroProblem neudefinieren.
//             */
//            macroProblem = MultiJobTerminalProblemFactory.createForMultiScale(schedule, problem.getTerminal(), problem.getTrains(), jobsToSchedule, problem.getScheduleManagerBuilder(),  problem.getRouteFinder().getBewertung());
//            System.out.println("Scheduled Jobs: " + schedule.getScheduledJobs().size());
//            System.out.println("DNFs Gesamtschedule: " + schedule.getDnfJobs().size());
//            System.out.println("#################################");
//        }
//        return schedule;
//    }
//
//    public static void main(String[] args) {
//        MultiScaleSchedulerOpImplicitModeJSon scheduler = new MultiScaleSchedulerOpImplicitModeJSon();
//        /**
//         * Erzeuge Problem
//         */
//
//        applications.transshipment.generator.projekte.duisburg.DuisburgGenerator g = new applications.transshipment.generator.projekte.duisburg.DuisburgGenerator();
//        MultiJobTerminalProblem problem = g.generateTerminalProblem(Scale.macro, numberOfRoutes);
//
////        applications.transshipment.demo.newLCS.Generator g = new applications.transshipment.demo.newLCS.Generator();
////        MultiJobTerminalProblem problem = g.generateTerminalProblem(Scale.macro, numberOfRoutes, false);
//        RealTimeSchedule schedule = scheduler.calc(problem);
//
//        Parameters.logger.log(Level.INFO, "Operationen gesamt: " + schedule.getScheduledOperations().size());
//
//        ArrayList<Analysis> analyser = new ArrayList<>();
////        analyser.add(new DurationList());
//        analyser.add(new ScheduleWriter());
////        analyser.add(new JobEalosaeAnalysis());
//        analyser.add(new WorkloadPlotter());
//        analyser.add(new HeatMapPlotter());
////        analyser.add(new Train2TrainAnalysis());
////        analyser.add(new AuswertungGenerator());
//
//        for (Analysis a : analyser) {
//            a.analysis(schedule, problem, TransshipmentParameter.debugFolder);
//        }
//
//    }
//}
