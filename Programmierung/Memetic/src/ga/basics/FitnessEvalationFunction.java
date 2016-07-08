package ga.basics;

import ga.individuals.Individual;

/**
 *
 * @author milbradt
 */
public interface FitnessEvalationFunction<I extends Individual> {

    public double[]  computeFitness(I i);
}
