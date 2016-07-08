/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving;

import ga.acceptance.AcceptanceMechanism;
import ga.individuals.Individual;
import ga.nextGeneration.NextGenerationAlgorithm;

/**
 *
 * @author bode
 */
public class GABundle<I extends Individual> {

    public NextGenerationAlgorithm<I> nextGenAlgo;
    public AcceptanceMechanism<I> acceptanceMechanism;

    public GABundle(NextGenerationAlgorithm<I> nextGenAlgo, AcceptanceMechanism<I> acceptanceMechanism) {
        this.nextGenAlgo = nextGenAlgo;
        this.acceptanceMechanism = acceptanceMechanism;
    }
}
