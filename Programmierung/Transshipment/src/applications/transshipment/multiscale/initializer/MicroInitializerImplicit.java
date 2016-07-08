/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.initializer;

import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.ops.OperationStartPopulationGenerator;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.multiscale.model.MicroProblem;
import applications.transshipment.multiscale.evaluation.EvaluationImplicitOperation;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.GAAlgorithm;
import ga.algorithms.ParallelGA;
import ga.algorithms.SimpleGA;
import ga.basics.Population;
import ga.crossover.Crossover;
import ga.crossover.UniformCrossOver;
import ga.mutation.Mutation;
import ga.mutation.SwapMutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutate;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.Selection;
import ga.selection.TournamentSelection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class MicroInitializerImplicit {

    public static int numberOfIndOperationsMicro = 1;
    public static int GENERATIONSMicro = 0;
    public static boolean parallel = true;

    public static GAAlgorithm<ImplicitOperationIndividual> initMicro(MicroProblem problem) {
        return initMicro(null, null, problem);
    }

    public static GAAlgorithm<ImplicitOperationIndividual> initMicro(Population<ImplicitSuperIndividual> macroPop, LoadUnitJobSchedule initialschedule, MicroProblem problem) {

        /**
         * DNF-Behandlung
         */
        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getSuperProblem().getRouteFinder(), problem.getSuperProblem());

        /**
         * Initialisiere ScheduleScheme
         */
        Transshipment_ImplicitScheduleGenerationScheme defaultScheduleScheme = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);

        /**
         * Auswertung am Gesamten? Problematisch hier.... Auswertung nur im
         * Teilbereich? Wie sehen dann die Auswirkungen auf die höhrere Ebene
         * aus?
         */
        EvaluationImplicitOperation eval = new EvaluationImplicitOperation(problem.getOperations(), initialschedule, problem, defaultScheduleScheme);

        /**
         * GA für Operationen.
         */
        Selection<ImplicitOperationIndividual> selOps = new TournamentSelection<>(4);
        Crossover<ImplicitOperationIndividual> crossOps = new UniformCrossOver<>();
        Mutation<ImplicitOperationIndividual> mutOps = new SwapMutation<>();
        double xOverOps = 1.0;
        double xmutOps = 0.5;
        AcceptanceMechanism<ImplicitOperationIndividual> acceptanceOps = new ThresholdAcceptance<>();

        Population<ImplicitOperationIndividual> startPop =  getStartPopulation(macroPop, problem);
        
        NextGenerationAlgorithm<ImplicitOperationIndividual> nextGenOps = null;

        if (parallel) {
            nextGenOps = new SelectCrossMutateParallel<>(xOverOps, xmutOps, selOps, mutOps, crossOps);
            return new ParallelGA<>(startPop, eval, nextGenOps, acceptanceOps, GENERATIONSMicro);
        } else {
            nextGenOps = new SelectCrossMutate<>(xOverOps, xmutOps, selOps, mutOps, crossOps);
            return new SimpleGA<>(startPop, eval, nextGenOps, acceptanceOps, GENERATIONSMicro);
        }
    }

    public static  Population<ImplicitOperationIndividual> getStartPopulation(Population<ImplicitSuperIndividual> macroPop, MicroProblem problem) {
        Population<ImplicitOperationIndividual> startPop = null;
//        if (macroPop != null) {
//            startPop = new Population<>(ImplicitOperationIndividual.class, 0);
//            ArrayList<ImplicitSuperIndividual> individualsSortedList = macroPop.getIndividualsSortedList();
//            int numberOfGensNeeded = (int) Math.ceil(problem.getOptimizationTimeSlot().getDuration().longValue() / new Double(OperationPriorityRules.lengthOfInterval));
//
//            for (int i = 0; i < numberOfIndOperationsMicro; i++) {
//                List<OperationPriorityRules.Identifier> chromosome = individualsSortedList.get(i).getOperationIndividual().getChromosome();
//                List<OperationPriorityRules.Identifier> subList = chromosome.subList(0, numberOfGensNeeded);
//                startPop.add(new ImplicitOperationIndividual(subList));
//            }
//        } else {
            OperationStartPopulationGenerator vPop = new OperationStartPopulationGenerator(problem, true);
            startPop = vPop.generatePopulation(numberOfIndOperationsMicro);
//        }
        return startPop;
    }
}
