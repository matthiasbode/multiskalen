/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.ops;

import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.coevolving.GABundle;
import ga.basics.StartPopulationGenerator;
import ga.crossover.Crossover;
import ga.crossover.LOXRecombination;
import ga.individuals.subList.ListIndividual;
import ga.mutation.ListIndividualSwap;
import ga.mutation.Mutation;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutate;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.RankingSelection;
import ga.selection.Selection;

/**
 *
 * @author bode
 */
public class DirectOperationGA implements OperationSubGA<ListIndividual<RoutingTransportOperation>> {

    @Override
    public GABundle<ListIndividual<RoutingTransportOperation>> getGA(StartPopulationGenerator<ListIndividual<RoutingTransportOperation>> startPopGen, boolean parallel) {

        double xOverOps = 1.0;
        double xmutOps = 0.5;
        Selection<ListIndividual<RoutingTransportOperation>> selOps = new RankingSelection<>();
        Crossover<ListIndividual<RoutingTransportOperation>> crossOps = new LOXRecombination();
        Mutation<ListIndividual<RoutingTransportOperation>> mutOps = new ListIndividualSwap();

        AcceptanceMechanism<ListIndividual<RoutingTransportOperation>> acceptanceOps = new ThresholdAcceptance<>(); // new Elitismus<>(eliteOps); //

        NextGenerationAlgorithm<ListIndividual<RoutingTransportOperation>> nextGenOps = null;

        if (parallel) {
            nextGenOps = new SelectCrossMutateParallel<>(xOverOps, xmutOps, selOps, mutOps, crossOps);

        } else {
            nextGenOps = new SelectCrossMutate<>(xOverOps, xmutOps, selOps, mutOps, crossOps);
        }
        return new GABundle<ListIndividual<RoutingTransportOperation>>(nextGenOps, acceptanceOps);
    }

}
