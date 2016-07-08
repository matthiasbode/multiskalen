/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.modes;

import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.reset.ResetThreshold;
import ga.algorithms.coevolving.GABundle;
import ga.basics.StartPopulationGenerator;
import ga.crossover.Crossover;
import ga.crossover.TwoPointCrossover;
import ga.individuals.IntegerIndividual;
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
public class IntegerModeGA implements ModeSubGA<IntegerIndividual> {

    @Override
    public GABundle<IntegerIndividual> getGA(StartPopulationGenerator<IntegerIndividual> mPop, boolean parallel) {
        Selection<IntegerIndividual> selMode = new RankingSelection<>();
        Crossover<IntegerIndividual> crossMode = new TwoPointCrossover<>();
        Mutation<IntegerIndividual> mutMode = new SwapMutation<>();
        double xOverMode = 1.0;
        double xmutMode = 0.5;
        AcceptanceMechanism<IntegerIndividual> acceptanceMode = new ResetThreshold<>(3, mPop); //new Elitismus<>(eliteMode); 

        NextGenerationAlgorithm<IntegerIndividual> nextGenMode = null;
        if (parallel) {
            nextGenMode = new SelectCrossMutateParallel<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        } else {
            nextGenMode = new SelectCrossMutate<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        }
        return new GABundle<>(nextGenMode, acceptanceMode);
    }

}
