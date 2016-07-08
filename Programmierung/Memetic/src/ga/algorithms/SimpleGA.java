package ga.algorithms;

import ga.Parameters;
import ga.individuals.Individual;
import ga.acceptance.AcceptanceMechanism;
import ga.basics.*;
import ga.listeners.GAEvent;
import ga.listeners.IndividualEvent;
import ga.nextGeneration.NextGenerationAlgorithm;

/**
 *
 * @author hoecker, bode
 */
public class SimpleGA<I extends Individual> extends GAAlgorithm<I> {

    protected Population<I> currentPopulation;
    protected FitnessEvalationFunction<I> fitnessFunction;
    protected AcceptanceMechanism<I> acceptanceMechanism;
    protected NextGenerationAlgorithm<I> nextGenAlg;
    public int numGenerations;
    public double fitnessMax = Double.NEGATIVE_INFINITY;
    public double fitnessLim = Double.POSITIVE_INFINITY;

    public SimpleGA(Population<I> pop, FitnessEvalationFunction<I> env, NextGenerationAlgorithm<I> nextGenAlg, AcceptanceMechanism<I> acceptanceMechanism, int numGenerations) {
        if (pop == null || env == null || nextGenAlg == null) {
            throw new NullPointerException();
        }
        if (numGenerations < 0) {
            throw new IllegalArgumentException("number of generations < 0");
        }
        this.acceptanceMechanism = acceptanceMechanism;
        this.currentPopulation = pop;
        this.fitnessFunction = env;
        this.nextGenAlg = nextGenAlg;
        this.numGenerations = numGenerations;
    }

    public void run() {
        this.computeFitnessMax(currentPopulation);
        while (fitnessMax < fitnessLim && currentPopulation.numberOfGenerations < numGenerations) {
            Population<I> nextGen = nextGenAlg.computeNextGeneration(currentPopulation);
            /**
             * Bestimme Fitness aller Individuen
             */
            this.computeFitnessMax(nextGen);
            /**
             * Filtere die neuen Individuen.
             */
            currentPopulation = acceptanceMechanism.getFilteredNewPopulation(currentPopulation, nextGen);
            GAEvent gaEvent = new GAEvent(this, GAEvent.StatusGAEvent.NEXTGENERATION, currentPopulation);
            fire(gaEvent);
        }
        GAEvent gaEvent = new GAEvent(this, GAEvent.StatusGAEvent.FINISHED, currentPopulation);
        fire(gaEvent);
    }

    private void computeFitnessMax(Population<I> population) {
        for (I i : population.individuals()) {
            i.setFitness(fitnessFunction.computeFitness(i));
        }

        Individual<I> fittestIndividual = population.getFittestIndividual();
        double fitness = fittestIndividual.getFitness();
        if (fitness > fitnessMax) {
            fitnessMax = fitness;
            Parameters.logger.fine("Fittest Individual: " + population.numberOfGenerations + ": " + fittestIndividual);
            Parameters.logger.fine("fit: " + fittestIndividual.getFitness());
        }
    }

    public Population<I> getPopulation() {
        return currentPopulation;
    }

    @Override
    public FitnessEvalationFunction<I> getFitnessEvalationFunction() {
        return this.fitnessFunction;
    }

}
