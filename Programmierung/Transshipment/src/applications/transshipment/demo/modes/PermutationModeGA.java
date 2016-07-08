/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.modes;

import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeIndividual;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.acceptance.reset.ResetThreshold;
import ga.algorithms.coevolving.GABundle;
import ga.basics.StartPopulationGenerator;
import ga.crossover.ListIndividualUniformOrderedCrossover;
import ga.mutation.ListIndividualSwap;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutate;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.RankingSelection;
import ga.selection.Selection;

/**
 *
 * @author bode
 */
public class PermutationModeGA implements ModeSubGA<PermutationModeIndividual> {

    @Override
    public GABundle<PermutationModeIndividual> getGA(StartPopulationGenerator<PermutationModeIndividual> mPop, boolean parallel) {
        Selection<PermutationModeIndividual> selMode = new RankingSelection<>();
        ListIndividualUniformOrderedCrossover<PermutationModeIndividual> crossMode = new ListIndividualUniformOrderedCrossover<>();
        ListIndividualSwap<PermutationModeIndividual> mutMode = new ListIndividualSwap<>();

        double xOverMode = 1.0;
        double xmutMode = 0.5;
        AcceptanceMechanism<PermutationModeIndividual> acceptanceMode = new ResetThreshold<>(3, mPop); //new Elitismus<>(eliteMode); 

        NextGenerationAlgorithm nextGenMode = null;
        if (parallel) {
            nextGenMode = new SelectCrossMutateParallel<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        } else {
            nextGenMode = new SelectCrossMutate<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        }

        return new GABundle<>(nextGenMode, acceptanceMode);
    }

}
