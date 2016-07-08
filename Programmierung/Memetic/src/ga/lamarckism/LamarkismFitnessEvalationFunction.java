/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.lamarckism;

import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public abstract class LamarkismFitnessEvalationFunction<E extends Individual> implements FitnessEvalationFunction<E> {

    @Override
    public double[] computeFitness(E i) {
        double fitness = calculateFitness(i);
        bequeath(i);
        return new double[]{fitness};
    }

    public abstract double calculateFitness(E i);

    /**
     * Während der Evaluierung "gelerntes" führt zu Veränderungen im Gen.
     * @param Individum, auf das das gelernte angewandt werden soll i. 
     */
    public abstract void bequeath(E i);
}
