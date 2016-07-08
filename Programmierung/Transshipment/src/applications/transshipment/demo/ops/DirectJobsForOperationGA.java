/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.ops;

import applications.transshipment.ga.permutationModeDirectJob.PermutationJobIndividual;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.coevolving.GABundle;
import ga.basics.StartPopulationGenerator;
import ga.crossover.Crossover;
import ga.crossover.TwoPointCrossover;
import ga.crossover.UniformOrderBasedCrossOver;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import ga.mutation.Mutation;
import ga.mutation.SwapMutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutate;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.RankingSelection;
import ga.selection.Selection;

/**
 *
 * @author bode
 */
public class DirectJobsForOperationGA implements OperationSubGA<PermutationJobIndividual> {

    @Override
    public GABundle<PermutationJobIndividual> getGA(StartPopulationGenerator<PermutationJobIndividual> startPopGen, boolean parallel) {

        double xOverOps = 1.0;
        double xmutOps = 0.5;
        Selection<PermutationJobIndividual> selOps = new RankingSelection<>();
        Crossover<PermutationJobIndividual> crossOps =  new UniformOrderBasedCrossOver<>();
        Mutation<PermutationJobIndividual> mutOps = new SwapMutation<>();

        AcceptanceMechanism<PermutationJobIndividual> acceptanceOps = new ThresholdAcceptance<>(); // new Elitismus<>(eliteOps); //

        NextGenerationAlgorithm<PermutationJobIndividual> nextGenOps = null;

        if (parallel) {
            nextGenOps = new SelectCrossMutateParallel<>(xOverOps, xmutOps, selOps, mutOps, crossOps);

        } else {
            nextGenOps = new SelectCrossMutate<>(xOverOps, xmutOps, selOps, mutOps, crossOps);
        }
        return new GABundle<PermutationJobIndividual>(nextGenOps, acceptanceOps);
    }

}
