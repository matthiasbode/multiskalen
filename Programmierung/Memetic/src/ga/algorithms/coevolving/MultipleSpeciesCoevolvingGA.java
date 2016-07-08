/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving;

import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.acceptance.AcceptanceMechanism;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import ga.Parameters;
import ga.algorithms.GAAlgorithm;
import ga.basics.Population;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.listeners.IndividualEvent;
import ga.listeners.IndividualListener;
import ga.nextGeneration.NextGenerationAlgorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author bode
 * @param <I>
 */
public class MultipleSpeciesCoevolvingGA<I extends SuperIndividual> extends CoevolvingGA<I> {

    public static final String superIND = "SUPER_INDIVIDUM";
    /**
     * Die Populationen der Subspezien und der zugehörige genetische Algorithmus
     */
    private final LinkedHashMap<Population<? extends Individual>, GABundle> species;

    /**
     * Evaluierungsfunktion für das übergeordnete Individuum, welches als
     * Subindividuen je eins aus den species enthält.
     */
    private final FitnessEvalationFunction<I> fitnessFunction;
    public AcceptanceMechanism<I> acceptanceMechanism;
    private Population<I> superPopulation;
    private final int numGenerations;
    private final Class<I> superType;
    public double fitnessMax = Double.NEGATIVE_INFINITY;
    public double fitnessLim = Double.POSITIVE_INFINITY;
    private MultipleSpeciesIndividualGenerator<I> superIndividualGenerator;
    private MultipleSpeciesIndividualGenerator<I> startSuperIndividualGenerator;

    public MultipleSpeciesCoevolvingGA(Class<I> superType, LinkedHashMap<Population<? extends Individual>, GABundle> species, FitnessEvalationFunction<I> fitnessFunction, MultipleSpeciesIndividualGenerator superIndividualGenerator, MultipleSpeciesIndividualGenerator<I> startSuperIndividualGenerator, AcceptanceMechanism<I> acceptanceMechanism, int numGenerations) {
        this.species = species;
        this.fitnessFunction = fitnessFunction;
        this.numGenerations = numGenerations;
        this.superIndividualGenerator = superIndividualGenerator;
        this.startSuperIndividualGenerator = startSuperIndividualGenerator;
        this.acceptanceMechanism = acceptanceMechanism;
        this.superType = superType;
    }

    public MultipleSpeciesCoevolvingGA(Class<I> superType, LinkedHashMap<Population<? extends Individual>, GABundle> species, FitnessEvalationFunction<I> fitnessFunction, MultipleSpeciesIndividualGenerator superIndividualGenerator, MultipleSpeciesIndividualGenerator<I> startSuperIndividualGenerator, int numGenerations) {
        this.species = species;
        this.fitnessFunction = fitnessFunction;
        this.numGenerations = numGenerations;
        this.superIndividualGenerator = superIndividualGenerator;
        this.startSuperIndividualGenerator = startSuperIndividualGenerator;
        this.superType = superType;
    }

    public void run() {
        /**
         * Startfitnessberechnung.
         */
        Parameters.logger.log(Level.FINE, "Start bei Generation 0");
        this.superPopulation = new Population(superType, -1);
        for (Population population : species.keySet()) {
            computeFitnessMax(population, startSuperIndividualGenerator);

            Individual fittestSubIndividual = population.getFittestIndividual();
            I fittestSuperIndividual = (I) fittestSubIndividual.additionalObjects.get(superIND);

            Parameters.logger.fine("CoEvolving_ Initial fittest: " + Parameters.doubleFormat.format(fittestSubIndividual.getFitness()) + "\t Fittest Individual: " + fittestSuperIndividual);
        }

        /**
         * Schleife für die Generationen.
         */
        while (fitnessMax < fitnessLim && superPopulation.numberOfGenerations < numGenerations) {
            Parameters.logger.log(Level.FINE, "Generationsdurchlauf Nr: " + (superPopulation.numberOfGenerations + 1));
            /**
             * Superpopulation erzeugen.
             */
            this.superPopulation = new Population(superType, superPopulation.numberOfGenerations + 1);
            /**
             * Schleife über alle Species.
             */
            ArrayList<Population<? extends Individual>> speciesToIterate = new ArrayList<>(species.keySet());
            for (Population<? extends Individual> population : speciesToIterate) {
                /**
                 * Berechnung der nächsten Generation
                 */
                GABundle bundle = species.get(population);
                NextGenerationAlgorithm nextGenAlg = bundle.nextGenAlgo;

                Population newPopulation = nextGenAlg.computeNextGeneration(population);
                updatePopulationInformation(newPopulation, population, bundle);
            }
            I fittestIndividual = this.getPopulation().getFittestIndividual();
            Parameters.logger.log(Level.FINE, "Fittest SuperIndividuum: " + fittestIndividual + ":" + Arrays.toString(fittestIndividual.getFitnessVector()));

        }
        for (GABundle gABundle : species.values()) {
            gABundle.nextGenAlgo.fire(new IndividualEvent(gABundle.nextGenAlgo));
        }
    }

    /**
     * Methode aktualisiert die Populationsinformationen. Dazu wird zunächst die
     * Fitness der neuen Subindividuen bestimmt, dann mittels des
     * Filterkriteriums die neue Generation aus den neuen Individuen und ggf.
     * Elite-Individuen aus der alten Population generiert und die
     * Superpopulation aktualisiert.
     *
     * @param newSubPopulation
     * @param oldSubPopulation
     * @param bundle
     */
    private void updatePopulationInformation(Population<? extends Individual> newSubPopulation, Population<? extends Individual> oldSubPopulation, GABundle bundle) {
        AcceptanceMechanism acceptance = bundle.acceptanceMechanism;
        /**
         * Bestimmung der Fitness
         */
        this.computeFitnessMax(newSubPopulation, superIndividualGenerator);

        /**
         * Anwenden von Akzeptanzfiltern
         */
        Population filteredNewPopulation = acceptance.getFilteredNewPopulation(oldSubPopulation, newSubPopulation);

        newSubPopulation = filteredNewPopulation;
        /**
         * Bestimmung der Fitness, falls Reset vorgenommen wurde
         */
        this.computeFitnessMax(newSubPopulation, superIndividualGenerator);

        /**
         * Aktualisierung der Population
         */
        species.remove(oldSubPopulation);
        species.put(newSubPopulation, bundle);

        /**
         * Aktualisierung SuperPopulation
         */
//        IdentityHashMap<Individual, I> newMap = new IdentityHashMap<>();
        for (Individual subIndividual : newSubPopulation.individuals()) {
            I superIndividual = (I) subIndividual.additionalObjects.get(superIND);
//            newMap.put(subIndividual, superIndividual);
            this.superPopulation.add(superIndividual);
        }
//        bundle.subToSupMap = newMap;
        System.gc();
    }

    private void computeFitnessMax(Population<? extends Individual> subPopulation, MultipleSpeciesIndividualGenerator<I> generator) {
        HashMap<Class, Population> pops = new HashMap<>();
        for (Population population : species.keySet()) {
            pops.put(population.getIndividualType(), population);
        }
        for (Individual subIndividual : subPopulation.individuals()) {
            if (subIndividual.getFitness() != null && !Double.isNaN(subIndividual.getFitness())) {
                continue;
            }
            /**
             * Bestimme Gesamtfitness
             */
            I superIndividual = generator.getSuperIndividual(subIndividual, pops);
            double[] fitness = fitnessFunction.computeFitness(superIndividual);
            subIndividual.setFitness(fitness);
            superIndividual.setFitness(fitness);
            subIndividual.additionalObjects.put(superIND, superIndividual);
        }

        Individual fittestSubIndividual = subPopulation.getFittestIndividual();
        double fitness = fittestSubIndividual.getFitness();
        if (fitness >= fitnessMax) {
            fitnessMax = fitness;
        }

    }

    public Population<I> getPopulation() {
        return superPopulation;
    }

    public void addIndividualLister(IndividualListener<I> listener) {
        for (GABundle gABundle : species.values()) {
            gABundle.nextGenAlgo.addGAListener(listener);
        }
    }

    @Override
    public Map<Population<? extends Individual>, GABundle> getSpecies() {
        return this.species;
    }

    @Override
    public FitnessEvalationFunction<I> getFitnessEvalationFunction() {
        return this.fitnessFunction;
    }

}
