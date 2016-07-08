/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo.modes;

import applications.transshipment.ga.permutationModeImplicitOps.sorted.PermutationModeIndividualSorted;
import applications.transshipment.ga.permutationModeImplicitOps.sorted.TwoPartCrossover;
import applications.transshipment.ga.permutationModeImplicitOps.sorted.TwoPartSwap;
import ga.acceptance.AcceptanceMechanism;
import ga.acceptance.ThresholdAcceptance;
import ga.algorithms.coevolving.GABundle;
import ga.basics.StartPopulationGenerator;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutate;
import ga.nextGeneration.SelectCrossMutateParallel;
import ga.selection.RankingSelection;
import ga.selection.Selection;

/**
 *
 * @author bode
 */
public class PermutationModeOrderedGA implements ModeSubGA<PermutationModeIndividualSorted> {

    @Override
    public GABundle<PermutationModeIndividualSorted> getGA(StartPopulationGenerator<PermutationModeIndividualSorted> mPop, boolean parallel) {
        Selection<PermutationModeIndividualSorted> selMode = new RankingSelection<>();
        TwoPartCrossover crossMode = new TwoPartCrossover();
        TwoPartSwap mutMode = new TwoPartSwap();
 
        double xOverMode = 1.0;
        double xmutMode = 0.5;
        AcceptanceMechanism<PermutationModeIndividualSorted> acceptanceMode = new ThresholdAcceptance<>(); //new Elitismus<>(eliteMode); 

        NextGenerationAlgorithm nextGenMode = null;
        if (parallel) {
            nextGenMode = new SelectCrossMutateParallel<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        } else {
            nextGenMode = new SelectCrossMutate<>(xOverMode, xmutMode, selMode, mutMode, crossMode);
        }
        return new GABundle<>(nextGenMode, acceptanceMode);
    }

}
