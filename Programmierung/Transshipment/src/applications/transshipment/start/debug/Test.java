/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start.debug;

import applications.transshipment.multiscale.model.MicroProblem;
import applications.transshipment.multiscale.initializer.MicroInitializerImplicit;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.GA.ImplicitWriter;
import applications.transshipment.analysis.Schedule.LoadUnitOrientatedScheduleWriter;
import applications.transshipment.analysis.Schedule.ScheduleWriter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.ga.implicit.decode.ImplicitModeDecoder;
import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.basics.util.StartofRoutesCalculator;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.evaluation.EvaluationImplicitOperation;
import com.google.common.collect.Iterators;
import com.google.gson.reflect.TypeToken;
import ga.basics.Population;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import math.FieldElement;
import math.LongValue;
import math.Tools;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class Test {

    public static LongValue durationTimeSlot = new LongValue(30 * 60 * 1000);
    public static int numberOfRoutes = 4;
    File folder;
    ScheduleWriter sw;
    LoadUnitOrientatedScheduleWriter slu;
    ImplicitWriter writer;

    public Test() {
        folder = ProjectOutput.create();
        System.out.println(folder.getAbsolutePath());
        sw = new ScheduleWriter();
        slu = new LoadUnitOrientatedScheduleWriter();

    }

    public LoadUnitJobSchedule calc(MultiJobTerminalProblem originalProblem) {
        /**
         * Der endgültig Plan, der immer aktualisiert wird.
         */
        LoadUnitJobSchedule schedule = MultiJobTerminalProblemFactory.createNewSchedule(originalProblem);

        /**
         * Jobs, die eingeplant werden sollen.
         */
        List<LoadUnitJob> jobsToSchedule = new ArrayList<>(originalProblem.getJobs());

        FieldElement untilWhen = originalProblem.getTerminal().getTemporalAvailability().getUntilWhen();
        System.out.println("Gesamt-TimeSlot: " + originalProblem.getTerminal().getTemporalAvailability());

        /**
         * Einlesen eines vorgegeben Plans
         */
        InputStream streamOp = Test.class.getResourceAsStream("MicroOp_-7 _0.txt");
        InputStream streamMode = Test.class.getResourceAsStream("MicroMode_-7 _0.txt");
        Type listType = new TypeToken<ArrayList<OperationPriorityRules.Identifier>>() {
        }.getType();
        List<OperationPriorityRules.Identifier> list = JSONSerialisierung.importJSON(streamOp, listType);
        System.out.println(list);
        ImplicitOperationIndividual opInd = new ImplicitOperationIndividual(list);

        Type listType2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
        }.getType();
        List<LoadUnitJobPriorityRules.Identifier> list2 = JSONSerialisierung.importJSON(streamMode, listType2);
        System.out.println(list2);
        ImplicitModeIndividual modImplicit = new ImplicitModeIndividual(list2);

        
        /**
         * Auswerten
         */
        DNFToStorageTreatment dnfTreat = new DNFToStorageTreatment(originalProblem.getTerminal().getDnfStorage(), originalProblem.getRouteFinder(), originalProblem);
        StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfTreat);

        MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(originalProblem, sgs);
        ImplicitSuperIndividual implicitSuperIndividual = new ImplicitSuperIndividual(opInd, modImplicit);
        Population<ImplicitSuperIndividual> pop = new Population(ImplicitSuperIndividual.class, 0);
        pop.add(implicitSuperIndividual);

        ImplicitModeDecoder d = new ImplicitModeDecoder(implicitSuperIndividual.getModeIndividual(), originalProblem);

        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae = d.getEalosaes();
        ActivityOnNodeGraph<RoutingTransportOperation> aon = d.getGraph();

        LoadUnitJobSchedule refSchedule = eval.getSchedule(implicitSuperIndividual);
        System.out.println("Fitness: " + refSchedule.getDnfJobs().size());
        File file = new File(folder, "Start" + 0);
        file.mkdirs();
        sw.analysis(refSchedule, originalProblem, file);
        slu.analysis(refSchedule, originalProblem, file);
        
        
        
        
        TimeSlot currentTimeSlot = new TimeSlot(originalProblem.getTerminal().getTemporalAvailability().getFromWhen(), originalProblem.getTerminal().getTemporalAvailability().getFromWhen().add(durationTimeSlot));

        
        MultiJobTerminalProblem macroProblem = MultiJobTerminalProblemFactory.createMacroForMultiScale(schedule, originalProblem, jobsToSchedule, originalProblem.getOptimizationTimeSlot());
        System.out.println("###################################");
        Collection<ImplicitModeIndividual> macroModeIndividuals = null;
        Collection<ImplicitOperationIndividual> macroOperationIndividuals = null;
        TransshipmentParameter.DEBUG = true;
        
        int i = 0;
        while (currentTimeSlot.getUntilWhen().isLowerThan(untilWhen)) {
//            System.out.println("------------------");
//            System.out.println("Schleifendurchlauf");
//            System.out.println(currentTimeSlot);
//            System.out.println("jobsToSchedule: " + jobsToSchedule.size());

            /**
             * *
             * ##############################################################
             * Optimierung auf Macro-Ebene.
             * ##############################################################
             */
            originalProblem.getOptimizationTimeSlot().getFromWhen();
            int numberOfGensNeeded = (int) Math.ceil(macroProblem.getOptimizationTimeSlot().getDuration().longValue() / new Double(OperationPriorityRules.lengthOfInterval));
            for (ImplicitSuperIndividual in : pop.individuals()) {
                int start = in.getOperationIndividual().getChromosome().size() - numberOfGensNeeded;
                List<OperationPriorityRules.Identifier> subList = in.getOperationIndividual().getChromosome().subList(start, in.getOperationIndividual().getChromosome().size());
                in.getOperationIndividual().setChromosome(subList);
            }
            /**
             * Problem: Welche Operationen sollen eingeplant werden? Noch
             * vorsichtshalber ein paar mehr aus dem Zeitfenster, falls möglich?
             * oder eher erst einmal nicht?
             */
            Collection<RoutingTransportOperation> operationsToSchedule = refSchedule.getOperationsScheduledInInterval(currentTimeSlot);
            operationsToSchedule.removeAll(schedule.getScheduledRoutingTransportOperations());

//            System.out.println("--------------");
//            System.out.println("From Makro2Mikro: " + operationsToSchedule.size() + "/" + refSchedule.getScheduledOperations().size());
            /**
             * ##############################################################
             * Erzeuge Teilproblem.
             * ##############################################################
             */
            if (aon == null || ealosae == null) {
                throw new NoSuchElementException("AON oder EALOSAE nicht gesetzt");
            }

//            ActivityOnNodeGraph<RoutingTransportOperation> subGraph = aon.getSubGraph(operationsToSchedule);
            MicroProblem subProblem = MultiJobTerminalProblemFactory.createMicroForMultiScale(currentTimeSlot, schedule, operationsToSchedule, ealosae, aon, originalProblem);
//            subProblem.getScheduleManagerBuilder().updateStartTimesForResources(currentTimeSlot.getFromWhen(), schedule, originalProblem.getTerminal().getConveyanceSystems());
            /**
             * Operationen aus alloverSchedule werden gleich eingeplant.
             */
//            GAAlgorithm<ImplicitOperationIndividual> micro = MicroInitializerImplicit.initMicro(macro.getPopulation(), schedule, subProblem);
//            micro.run();

            EvaluationImplicitOperation evalMicro = new EvaluationImplicitOperation(operationsToSchedule, schedule, subProblem, sgs);
            Population<ImplicitOperationIndividual> startPopulation = MicroInitializerImplicit.getStartPopulation(pop, subProblem);
            ImplicitOperationIndividual fittestMicro = startPopulation.individuals().iterator().next();
            LoadUnitJobSchedule microSchedule = evalMicro.getSchedule(fittestMicro);

//            System.out.println("Fitness Micro: " + microSchedule.getDnfJobs().size());
            file = new File(folder, "Micro" + i);
            file.mkdirs();
            sw.analysis(microSchedule, subProblem.getSuperProblem(), file);
            slu.analysis(microSchedule, subProblem.getSuperProblem(), file);

            JSONSerialisierung.exportJSON(new File(file, i + "indOp_" + fittestMicro.getFitness().toString().replace(".", " _") + ".txt"), fittestMicro.getChromosome(), true);

//            System.out.println("-------");
//            System.out.println("Append Schedule");
            /**
             * Aktualisiere den Gesamtschedule
             */
            schedule = microSchedule;
//            System.out.println("--------------");

            /**
             * Update der Constraints auf Macro-Ebene
             */
//            macroProblem.getScheduleManagerBuilder().updateStartTimesForResources(currentTimeSlot.getFromWhen(), schedule, originalProblem.getTerminal().getConveyanceSystems());
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
                    TimeSlot.longToFormattedDateString(lastEnd.longValue());
//                    System.out.println(conveyanceSystem + "--->" + TimeSlot.longToFormattedDateString(lastEnd.longValue()));
                    /**
                     * Hier max, damit dasselbe rauskommt.
                     */
                    if (lastEnd.isGreaterThan(currentTimeSlot.getUntilWhen())) {
                        nextStart = nextStart == null ? lastEnd : Tools.min(lastEnd, nextStart);
                    }
                }
            }

            if (nextStart == null) {
                break;
            }

            nextStart = Tools.min(schedule.t, nextStart);
            currentTimeSlot.move(durationTimeSlot);
            ealosae = schedule.ealosae;
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
//            System.out.println("Scheduled Jobs: " + schedule.getScheduledJobs().size());
//            System.out.println("DNFs Gesamtschedule: " + schedule.getDnfJobs().size());
//            System.out.println("#################################");
            i++;
        }
        return schedule;
    }

}
