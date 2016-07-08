/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale;

import applications.transshipment.multiscale.model.MicroProblem;
import applications.transshipment.multiscale.initializer.MicroInitializerImplicit;
import applications.transshipment.multiscale.initializer.MacroInitializerImplicit;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.JoNComponent;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.analysis.GA.ImplicitWriter;
import applications.transshipment.analysis.GA.MultiScaleFitnessWriter;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.ga.implicit.individuals.HammingDistanceSuperImplicit;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.HammingDistanceMode;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.ops.HammingDistanceOp;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.basics.util.StartofRoutesCalculator;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.multiscale.initializer.MicroInitializerDirect;
import applications.transshipment.multiscale.transform.TransformMicroToMacro;
import applications.transshipment.start.debug.Test;
import com.google.common.collect.Iterators;
import com.google.gson.reflect.TypeToken;
import ga.algorithms.GAAlgorithm;
import ga.algorithms.coevolving.CoevolvingGA;
import ga.basics.Population;
import ga.fittnessLandscapeAnalysis.Diversity;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import ga.listeners.analysis.FitnessEvolution;
import ga.metric.Metric;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import math.FieldElement;
import math.LongValue;
import math.Tools;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class MultiScaleScheduler {

    public boolean directMicro = true;
    public static LongValue durationTimeSlot = new LongValue(30 * 60 * 1000);
    public static int numberOfRoutes = 5;
    File folder;
    ScheduleWriter sw;
    LoadUnitOrientatedScheduleWriter slu;
    ImplicitWriter writer;
    MultiScaleFitnessWriter fitnesswriter;

    public MultiScaleScheduler(File folder, boolean directMicro) {
        this.directMicro = directMicro;
        this.folder = folder;
        System.out.println(folder.getAbsolutePath());
        sw = new ScheduleWriter();
        slu = new LoadUnitOrientatedScheduleWriter();


    }

    public LoadUnitJobSchedule calc(MultiJobTerminalProblem originalProblem) {
        fitnesswriter = new MultiScaleFitnessWriter(folder);
        HashMap<Class, Metric> metrices = new HashMap();
        metrices.put(ImplicitSuperIndividual.class, new HammingDistanceSuperImplicit());
        metrices.put(ImplicitModeIndividual.class, new HammingDistanceMode());
        metrices.put(ImplicitOperationIndividual.class, new HammingDistanceOp());

        List<LoadUnitJob> jobsToSchedule = new ArrayList<>(originalProblem.getJobs());
        jobsToSchedule.addAll(originalProblem.getStammRelation());

        FieldElement untilWhen = originalProblem.getTerminal().getTemporalAvailability().getUntilWhen();
        System.out.println("Gesamt-TimeSlot: " + originalProblem.getTerminal().getTemporalAvailability());
        /**
         * Der endgültig Plan, der immer aktualisiert wird.
         */
        LoadUnitJobSchedule schedule = MultiJobTerminalProblemFactory.createNewSchedule(originalProblem);

        sw.analysis(schedule, originalProblem, folder);
        slu.analysis(schedule, originalProblem, folder);

        TransformMicroToMacro transform = new TransformMicroToMacro();
        /**
         * Zeitfenster, das
         */
        TimeSlot currentTimeSlot = new TimeSlot(originalProblem.getTerminal().getTemporalAvailability().getFromWhen(), originalProblem.getTerminal().getTemporalAvailability().getFromWhen().add(durationTimeSlot));

        MultiJobTerminalProblem macroProblem = MultiJobTerminalProblemFactory.createMacroForMultiScale(schedule, originalProblem, jobsToSchedule, originalProblem.getOptimizationTimeSlot());

        System.out.println("###################################");

//        InputStream streamOp = Test.class.getResourceAsStream("macroAvoidStorageOperation.txt");
//        InputStream streamMode = Test.class.getResourceAsStream("macroAvoidStorageMode.txt");

//        Type listType = new TypeToken<ArrayList<OperationPriorityRules.Identifier>>() {
//        }.getType();
//        List<OperationPriorityRules.Identifier> list = JSONSerialisierung.importJSON(streamOp, listType);
//        System.out.println(list);
//        ImplicitOperationIndividual opInd = new ImplicitOperationIndividual(list);
//
//        Type listType2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
//        }.getType();
//        List<LoadUnitJobPriorityRules.Identifier> list2 = JSONSerialisierung.importJSON(streamMode, listType2);
//        System.out.println(list2);
//        ImplicitModeIndividual modImplicit = new ImplicitModeIndividual(list2);

        List<ImplicitModeIndividual> macroModeIndividuals = new ArrayList<>();
//        macroModeIndividuals.add(modImplicit);
        List<ImplicitOperationIndividual> macroOperationIndividuals = new ArrayList<>();
//        macroOperationIndividuals.add(opInd);

        int i = 0;
        while (currentTimeSlot.getUntilWhen().isLowerThan(untilWhen)) {
            System.out.println("Schleifendurchlauf");
            System.out.println(currentTimeSlot);
            System.out.println("jobsToSchedule: " + jobsToSchedule.size());

            /**
             * *
             * ##############################################################
             * Optimierung auf Macro-Ebene.
             * ##############################################################
             */
            File file = new File(folder, "Macro" + i);
            file.mkdirs();
            CoevolvingGA<ImplicitSuperIndividual> macro = MacroInitializerImplicit.initMacro(transform.transform(schedule, macroProblem), macroProblem, macroOperationIndividuals, macroModeIndividuals, originalProblem.getOptimizationTimeSlot());

            FitnessEvolution<ImplicitSuperIndividual> evalFitness = new FitnessEvolution<>(file);
//            Diversity div = new Diversity(file, metrices);
            macro.addGAListener(evalFitness);
            macro.addGAListener(fitnesswriter);
//            macro.addGAListener(div);
            macro.run();

            ImplicitSuperIndividual fittestMacro = macro.getPopulation().getFittestIndividual();
//            macroModeIndividuals = (Collection<ImplicitModeIndividual>) macro.getSubPopulation(ImplicitModeIndividual.class).individuals();
            macroOperationIndividuals = (List<ImplicitOperationIndividual>) macro.getSubPopulation(ImplicitOperationIndividual.class).getIndividualsSortedList();

            LoadUnitFitnessEvalationFunction<ImplicitSuperIndividual> fitnessFunction = (LoadUnitFitnessEvalationFunction<ImplicitSuperIndividual>) macro.getFitnessEvalationFunction();
            LoadUnitJobSchedule macroSchedule = fitnessFunction.getSchedule(fittestMacro);

            sw.analysis(macroSchedule, macroProblem, file);
            slu.analysis(macroSchedule, macroProblem, file);

            JSONSerialisierung.exportJSON(new File(file, i + "indOp_" + fittestMacro.getFitness().toString().replace(".", " _") + ".txt"), fittestMacro.getOperationIndividual().getChromosome(), true);
            JSONSerialisierung.exportJSON(new File(file, i + "indMode_" + fittestMacro.getFitness().toString().replace(".", " _") + ".txt"), fittestMacro.getModeIndividual().getChromosome(), true);

            /**
             * Problem: Welche Operationen sollen eingeplant werden? Noch
             * vorsichtshalber ein paar mehr aus dem Zeitfenster, falls möglich?
             * oder eher erst einmal nicht?
             */
//            Collection<RoutingTransportOperation> operationsToSchedule = macroSchedule.getActiveSet(currentTimeSlot);
//            operationsToSchedule.removeAll(schedule.getScheduledRoutingTransportOperations());
            TimeSlot ts = currentTimeSlot.clone();
            ts.setFromWhen(originalProblem.getOptimizationTimeSlot().getFromWhen());
            Collection<RoutingTransportOperation> operationsToSchedule = macroSchedule.getOperationsScheduledInInterval(ts);
            operationsToSchedule.removeAll(schedule.getScheduledRoutingTransportOperations());
            System.out.println("Fitness Macro: " + fittestMacro.getFitness());
            System.out.println("--------------");
            System.out.println("From Makro2Mikro: " + operationsToSchedule.size() + "/" + macroSchedule.getScheduledOperations().size());

            /**
             * ##############################################################
             * Erzeuge Teilproblem.
             * ##############################################################
             */
            ActivityOnNodeGraph<RoutingTransportOperation> aon = macroSchedule.aon;
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae = macroSchedule.ealosae;

            TimeSlot microTimeSlot = currentTimeSlot.clone();
            microTimeSlot.setUntilWhen(microTimeSlot.getUntilWhen().add(new LongValue(10 * 60 * 1000)));
            MicroProblem subProblem = MultiJobTerminalProblemFactory.createMicroForMultiScale(microTimeSlot, schedule, operationsToSchedule, ealosae, aon, originalProblem);

            Population<ImplicitSuperIndividual> population = macro.getPopulation();

            Runtime rt = Runtime.getRuntime();
            System.out.println("Speicherverbrauch vor GC Makro : \t" + (rt.totalMemory() - rt.freeMemory()));
            macro = null;
            System.gc();
            System.out.println("Speicherverbrauch nach GC Makro : \t" + (rt.totalMemory() - rt.freeMemory()));
//             TransshipmentParameter.DEBUG = true;
            /**
             * Operationen aus alloverSchedule werden gleich eingeplant.
             */
            file = new File(folder, "Micro" + i);
            file.mkdirs();

            LoadUnitJobSchedule microSchedule = null;

            GAAlgorithm micro;
            if (!directMicro) {
                micro = MicroInitializerImplicit.initMicro(population, schedule, subProblem);
//                FitnessEvolution<ImplicitOperationIndividual> evalFitnessMicro = new FitnessEvolution<>(file);
//                Diversity divMicro = new Diversity(file, metrices);
//                micro.addGAListener(evalFitnessMicro);
//                micro.addGAListener(divMicro);
                micro.addGAListener(fitnesswriter);
                micro.run();
                ImplicitOperationIndividual fittestMicro = (ImplicitOperationIndividual) micro.getPopulation().getFittestIndividual();
                System.out.println("Fitness Micro: " + fittestMicro.getFitness());
                LoadUnitFitnessEvalationFunction<ImplicitOperationIndividual> fitnessFunctionMicro = (LoadUnitFitnessEvalationFunction<ImplicitOperationIndividual>) micro.getFitnessEvalationFunction();
                microSchedule = fitnessFunctionMicro.getSchedule(fittestMicro);
                JSONSerialisierung.exportJSON(new File(file, i + "indOp_" + fittestMicro.getFitness().toString().replace(".", " _") + ".txt"), fittestMicro.getChromosome(), true);
            } else {
                micro = new MicroInitializerDirect().initMicro(population, schedule, subProblem);
//                FitnessEvolution<ListIndividual<RoutingTransportOperation>> evalFitnessMicro = new FitnessEvolution<>(file);
//                micro.addGAListener(evalFitnessMicro);
                micro.addGAListener(fitnesswriter);
                micro.run();
                ListIndividual<RoutingTransportOperation> fittestMicro = (ListIndividual<RoutingTransportOperation>) micro.getPopulation().getFittestIndividual();
                System.out.println("Fitness Micro: " + fittestMicro.getFitness());
                LoadUnitFitnessEvalationFunction<ListIndividual<RoutingTransportOperation>> fitnessFunctionMicro = (LoadUnitFitnessEvalationFunction<ListIndividual<RoutingTransportOperation>>) micro.getFitnessEvalationFunction();
                microSchedule = fitnessFunctionMicro.getSchedule(fittestMicro);
                ArrayList<ArrayList<Integer>> exportList = new ArrayList<>();
                for (SubListIndividual<RoutingTransportOperation> chromosome : fittestMicro.getChromosome()) {
                    ArrayList<Integer> operations = new ArrayList<>();
                    for (Operation op : chromosome.getChromosome()) {
                        operations.add(op.getId());
                    }
                    exportList.add(operations);
                }
                JSONSerialisierung.exportJSON(new File(file, i + "indOp_" + fittestMicro.getFitness().toString().replace(".", " _") + ".txt"), exportList, true);
            }

            rt = Runtime.getRuntime();
            System.out.println("Speicherverbrauch vor GC Mikro : \t" + (rt.totalMemory() - rt.freeMemory()));
            micro = null;
            subProblem = null;
            System.gc();
            System.out.println("Speicherverbrauch nach GC Mikro : \t" + (rt.totalMemory() - rt.freeMemory()));

            ArrayList<RoutingTransportOperation> notScheduledMicro = new ArrayList<>(operationsToSchedule);
            notScheduledMicro.removeAll(microSchedule.getScheduledRoutingTransportOperations());

            System.out.println("-----Nicht eingeplant---------");
            for (RoutingTransportOperation notScheduledMicro1 : notScheduledMicro) {
                int connectionComponent = Integer.MAX_VALUE;
                for (JoNComponent<LoadUnitJob> object : originalProblem.getJobOnNodeDiagramm().getConnectionComponents()) {
                    if (object.vertexSet().contains(notScheduledMicro1.getJob())) {
                        connectionComponent = object.getNumber();
                    }
                    break;
                }
                System.out.println(connectionComponent + ":\t" + notScheduledMicro1);
            }

            file = new File(folder, "Micro" + i);
            file.mkdirs();
            sw.analysis(microSchedule, originalProblem, file);
            slu.analysis(microSchedule, originalProblem, file);

            System.out.println("-------");
            System.out.println("Append Schedule");

            /**
             * Aktualisiere den Gesamtschedule
             */
            schedule = microSchedule;

            System.out.println("--------------");

            /**
             * Update der Constraints auf Macro-Ebene
             */
            originalProblem.getScheduleManagerBuilder().updateStartTimesForResources(currentTimeSlot.getFromWhen(), schedule, originalProblem.getTerminal().getConveyanceSystems());

            /**
             * Bestimmung der neuen Startzeit.
             */
            FieldElement nextStart = null;
            for (ConveyanceSystem conveyanceSystem : originalProblem.getTerminal().getConveyanceSystems()) {
                Collection<Operation> operationsForResource = schedule.getOperationsForResource(conveyanceSystem);
                if (operationsForResource.isEmpty()) {
                    System.err.println("Keine Operationen eingeplant für: " + conveyanceSystem);
                } else {
                    Operation lastOperation = Iterators.getLast(operationsForResource.iterator());
                    FieldElement lastStart = schedule.get(lastOperation);
                    FieldElement lastEnd = lastStart.add(lastOperation.getDuration());
                    System.out.println(conveyanceSystem + "--->" + TimeSlot.longToFormattedDateString(lastEnd.longValue()));
                    nextStart = nextStart == null ? lastEnd : Tools.min(lastEnd, nextStart);
                }
            }
            currentTimeSlot.move(durationTimeSlot);
            currentTimeSlot.setFromWhen(nextStart);

            /**
             * ##############################################################
             * Streiche DNFs und bereits eingeplante Jobs.
             * ##############################################################
             */
            jobsToSchedule.removeAll(schedule.getDnfJobs());
            jobsToSchedule.removeAll(schedule.getScheduledJobs());

            /**
             * ##############################################################
             * Neue Startpositionen für die Jobs setzten.
             * ##############################################################
             */
            HashMap<LoadUnitJob, LoadUnitStorage> currentPosition = StartofRoutesCalculator.getCurrentPosition(originalProblem.getJobs(), schedule);
            for (LoadUnitJob loadUnitJob : currentPosition.keySet()) {
                loadUnitJob.setCurrentOrigin(currentPosition.get(loadUnitJob));
            }

            /**
             * ##############################################################
             * MacroProblem neudefinieren.
             * ##############################################################
             */
            macroProblem = MultiJobTerminalProblemFactory.createMacroForMultiScale(schedule, originalProblem, jobsToSchedule, new TimeSlot(nextStart, originalProblem.getOptimizationTimeSlot().getUntilWhen()));


            System.out.println("Scheduled Jobs: " + schedule.getScheduledJobs().size());
            System.out.println("DNFs Gesamtschedule: " + schedule.getDnfJobs().size());
            System.out.println("#################################");
            i++;
        }
        fitnesswriter.close();
        return schedule;
    }

}
