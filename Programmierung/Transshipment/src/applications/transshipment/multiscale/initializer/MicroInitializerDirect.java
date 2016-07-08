/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.initializer;

import applications.mmrcsp.ga.populationGenerators.VertexClassStartPopulationGenerator;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.ga.priority.StandardPriorityDeterminator;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes.ParallelScheduleGenerationScheme;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import applications.transshipment.multiscale.model.MicroProblem;
import applications.transshipment.multiscale.evaluation.EvaluationDirectOperation;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.GAAlgorithm;
import ga.algorithms.ParallelGA;
import ga.basics.Population;
import ga.crossover.Crossover;
import ga.crossover.LOXRecombination;
import ga.crossover.ListIndividualUniformOrderedCrossover;
import ga.individuals.subList.ListIndividual;
import ga.mutation.ListIndividualSwap;
import ga.mutation.Mutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.FitnessProportionalSelection;
import ga.selection.RankingSelection;
import ga.selection.Selection;
import ga.selection.TournamentSelection;

/**
 *
 * @author bode
 */
public class MicroInitializerDirect {

    public static int numberOfIndOperationsMicro = 40;
    public static int GENERATIONSMicro = 10;
    public Selection<ListIndividual<RoutingTransportOperation>> selOps = new TournamentSelection(4);
    public Crossover<ListIndividual<RoutingTransportOperation>> crossOps = new ListIndividualUniformOrderedCrossover<>();
    public Mutation<ListIndividual<RoutingTransportOperation>> mutOps = new ListIndividualSwap();
    public double xOverOps = 1.0;
    public double xmutOps = 0.5;
    public AcceptanceMechanism<ListIndividual<RoutingTransportOperation>> acceptanceOps = new ThresholdAcceptance<>(); // new Elitismus<>(eliteOps); //

    public GAAlgorithm<ListIndividual<RoutingTransportOperation>> initMicro(Population<ImplicitSuperIndividual> macroPop, LoadUnitJobSchedule initialschedule, MicroProblem problem) {

        VertexClassStartPopulationGenerator vPop = new VertexClassStartPopulationGenerator(problem, problem.getOperations());

        /**
         * DNF-Behandlung
         */
        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(problem.getTerminal().getDnfStorage(), problem.getSuperProblem().getRouteFinder(), problem.getSuperProblem());

        /**
         * Initialisiere ScheduleScheme
         */
        Transshipment_ActivityListScheduleScheme defaultScheduleScheme = new ParallelScheduleGenerationScheme(dnfToStorageTreatment);//new SerialScheduleGenerationWithStorageConsideration(problem, dnfToStorageTreatment, false);

        /**
         * Auswertung am Gesamten? Problematisch hier.... Auswertung nur im
         * Teilbereich? Wie sehen dann die Auswirkungen auf die höhrere Ebene
         * aus?
         */
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        EvaluationDirectOperation eval = new EvaluationDirectOperation(problem, defaultScheduleScheme, initialschedule, priorityDeterminator);

        /**
         * GA für Operationen.
         */
        NextGenerationAlgorithm<ListIndividual<RoutingTransportOperation>> nextGenOps = null;
        nextGenOps = new SelectCrossMutateParallel<>(xOverOps, xmutOps, selOps, mutOps, crossOps);
        Population<ListIndividual<RoutingTransportOperation>> startPop = vPop.generatePopulation(numberOfIndOperationsMicro);
        ParallelGA<ListIndividual<RoutingTransportOperation>> ga = new ParallelGA<ListIndividual<RoutingTransportOperation>>(startPop, eval, nextGenOps, acceptanceOps, GENERATIONSMicro);
       
        return ga;
    }
}
