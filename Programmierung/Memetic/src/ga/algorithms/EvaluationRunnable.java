/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms;

import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import java.util.Collection;

/**
 *
 * @author bode
 */
/**
 * Runnable zum Bestimmen der Fitness einer Partition einer der Subpopulationen.
 *
 * @param <I>
 */
public class EvaluationRunnable<I extends Individual> implements Runnable {

    Collection<I> partition;
    FitnessEvalationFunction<I> fitnessFunction;

    public EvaluationRunnable(Collection<I> partition, FitnessEvalationFunction<I> fitnessFunction) {
        this.partition = partition;
        this.fitnessFunction = fitnessFunction;

    }

    @Override
    public void run() {
        for (I individual : partition) {
            if(individual == null){
                throw new NullPointerException("Nullindividum bis zur Auswertung gekommen!");
            }
            /**
             * Bestimme Gesamtfitness
             */
            double[] fitness = fitnessFunction.computeFitness(individual);
            /**
             * Setzten der Fitness und der Verknüpfungen.
             */
            individual.setFitness(fitness);
        }
    }
}
