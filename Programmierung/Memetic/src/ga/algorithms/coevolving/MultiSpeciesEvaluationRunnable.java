/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving;

import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import ga.basics.Population;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author bode
 */
/**
 * Runnable zum Bestimmen der Fitness einer Partition einer der Subpopulationen.
 *
 * @param <I>
 */
public class MultiSpeciesEvaluationRunnable<I extends Individual> implements Runnable {

    Collection<Individual> partition;
    FitnessEvalationFunction<I> fitnessFunction;
    MultipleSpeciesIndividualGenerator<I> generator;
   
    Map<Class, Population> pops;

    public MultiSpeciesEvaluationRunnable(Collection<Individual> partition, FitnessEvalationFunction<I> fitnessFunction, MultipleSpeciesIndividualGenerator<I> generator,  Map<Class, Population> pops) {
        this.partition = partition;
        this.fitnessFunction = fitnessFunction;
        this.generator = generator;

        this.pops = pops;
    }

    @Override
    public void run() {
        for (Individual subIndividual : partition) {
            /**
             * Bestimme SuperIndividuum
             */
            I superIndividual = generator.getSuperIndividual(subIndividual, pops);
            for (Object object : superIndividual.getChromosome()) {
                if (object == null) {
                    throw new NullPointerException("SubIndividuum fehlt");
                }
            }
            if (superIndividual == null) {
                throw new NullPointerException("Kein SuperIndividuum erzeugt");
            }
            /**
             * Bestimme Gesamtfitness
             */
            double[] fitness = fitnessFunction.computeFitness(superIndividual);
            /**
             * Setzten der Fitness und der Verknüpfungen.
             */
            subIndividual.setFitness(fitness);
            superIndividual.setFitness(fitness);
            subIndividual.additionalObjects.put(MultipleSpeciesCoevolvingGA.superIND,superIndividual);
        }
    }
}
