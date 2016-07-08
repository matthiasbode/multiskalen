package ga.algorithms;

import ga.Parameters;
import ga.individuals.Individual;
import com.google.common.collect.Iterables;
import static ga.Parameters.maxStagnation;
import ga.acceptance.AcceptanceMechanism;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.basics.*;
import ga.listeners.GAEvent;
import ga.listeners.IndividualEvent;
import ga.listeners.coevolutionary.CoEvoGAEvent;
import ga.nextGeneration.NextGenerationAlgorithm;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hoecker, bode
 */
public class ParallelGA<I extends Individual> extends GAAlgorithm<I> {

    protected Population<I> currentPopulation;
    protected FitnessEvalationFunction<I> fitnessFunction;
    protected AcceptanceMechanism<I> acceptanceMechanism;
    protected NextGenerationAlgorithm<I> nextGenAlg;
    public int numGenerations;
    public double fitnessMax = Double.NEGATIVE_INFINITY;
    public double fitnessLim = Double.POSITIVE_INFINITY;
    private double[] oldbestFitness;
    private int stagnation = 0;

    public ParallelGA(Population<I> pop, FitnessEvalationFunction<I> env, NextGenerationAlgorithm<I> nextGenAlg, AcceptanceMechanism<I> acceptanceMechanism, int numGenerations) {
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
        while (fitnessMax < fitnessLim && currentPopulation.numberOfGenerations < numGenerations && stagnation <= maxStagnation) {
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
        Parameters.getThreadPool().shutdown();
        GAEvent gaEventEnd = new GAEvent(this, GAEvent.StatusGAEvent.FINISHED, currentPopulation);
        fire(gaEventEnd);
    }

    private void computeFitnessMax(Population<I> population) {
        ArrayList<Future<?>> evaluationThreads = new ArrayList<>();

        Iterable<List<I>> partition = Iterables.partition(population.individuals(), Math.max(1, population.individuals().size() / Parameters.NUMBER_OF_THREADS));

        for (List<I> list : partition) {

            EvaluationRunnable<I> runnable = new EvaluationRunnable(list, fitnessFunction);
            evaluationThreads.add(Parameters.getThreadPool().submit(runnable));
        }

        for (Future<?> future : evaluationThreads) {
            try {
                future.get();
            } catch (ExecutionException ex) {
                ex.getCause().printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(MultipleSpeciesCoevolvingParallelGA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        evaluationThreads = null;

        Individual<I> fittestIndividual = population.getFittestIndividual();
        double fitness = fittestIndividual.getFitness();
        Parameters.logger.fine("Fittest Individual: " + population.numberOfGenerations + ": " + fittestIndividual);
        Parameters.logger.fine("fit: " + Arrays.toString(fittestIndividual.getFitnessVector()));
        if (fitness > fitnessMax) {
            fitnessMax = fitness;
        }

        double[] fitnessVector = fittestIndividual.getFitnessVector();
        if (oldbestFitness != null) {
            boolean better = false;
            for (int i = 0; i < fitnessVector.length; i++) {
                double newI = fitnessVector[i];
                double oldI = oldbestFitness[i];
                if (newI > oldI) {
                    better = true;
                    break;
                }
            }
            if (!better) {
                stagnation++;
            } else {
                stagnation = 0;
            }
            Parameters.logger.fine("Stagnation: " + stagnation);
        }
        oldbestFitness = fitnessVector;
    }

    public Population<I> getPopulation() {
        return currentPopulation;
    }

    @Override
    public FitnessEvalationFunction<I> getFitnessEvalationFunction() {
        return this.fitnessFunction;
    }
}
