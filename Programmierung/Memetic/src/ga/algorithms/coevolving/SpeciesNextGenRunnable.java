/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving;

import ga.acceptance.AcceptanceMechanism;
import ga.individuals.Individual;
import ga.Parameters;
import ga.basics.Population;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.nextGeneration.NextGenerationAlgorithm;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
/**
 * Runnable für die Erzeugung einer neuen Generation einer der SubPopulationen.
 * Methode aktualisiert die Populationsinformationen. Dazu wird zunächst die
 * Fitness der neuen Subindividuen bestimmt, dann mittels des Filterkriteriums
 * die neue Generation aus den neuen Individuen und ggf. Elite-Individuen aus
 * der alten Population generiert und die Superpopulation aktualisiert.
 */
public class SpeciesNextGenRunnable<I extends SuperIndividual> implements Runnable {

    private Population oldSubPopulation;
    protected MultipleSpeciesCoevolvingParallelGA<I> ga;

    public SpeciesNextGenRunnable(MultipleSpeciesCoevolvingParallelGA<I> ga, Population subPopulation) {
        this.oldSubPopulation = subPopulation;
        this.ga = ga;
    }

    @Override
    public void run() {
        /**
         * Berechnung der nächsten Generation
         */
        GABundle<I> bundle = ga.species.get(oldSubPopulation);
        NextGenerationAlgorithm nextGenAlg = bundle.nextGenAlgo;

        /**
         * Erzeugen der neuen Generation.
         */
        Population<Individual> newSubPopulation = nextGenAlg.computeNextGeneration(oldSubPopulation);
        newSubPopulation = postProcessing(bundle, newSubPopulation);
        updateSuperPopulation(bundle, newSubPopulation);

    }

    /**
     * Bestimmen der Fitness der einzelnen Individuen
     *
     * @param bundle
     * @param newSubPopulation
     * @return
     */
    protected Population<Individual> postProcessing(GABundle<I> bundle, Population<Individual> newSubPopulation) {
        /**
         * Bestimmung der Fitness
         */
        try {
            ga.computeFitnessMax(newSubPopulation, ga.getSuperIndividualGenerator());
        } catch (Exception e) {
            Logger.getLogger(MultipleSpeciesCoevolvingParallelGA.class.getName()).log(Level.SEVERE, null, e);
        }
        return newSubPopulation;
    }

    protected void updateSuperPopulation(GABundle<I> bundle, Population<Individual> newSubPopulation) {

        /**
         * Anwenden von Akzeptanzfiltern
         */
        AcceptanceMechanism acceptance = bundle.acceptanceMechanism;
        Population filteredNewPopulation = acceptance.getFilteredNewPopulation(oldSubPopulation, newSubPopulation);

        Individual fittestSubIndividualBefore = newSubPopulation.getFittestIndividual();
        I fittestSuperBefore = (I) fittestSubIndividualBefore.additionalObjects.get(MultipleSpeciesCoevolvingGA.superIND);
//        Parameters.logger.fine("SubPopulation / current fittest: " + newSubPopulation.getIndividualType().getSimpleName() + "-->" + Parameters.doubleFormat.format(fittestSubIndividualBefore.getFitness()) + "\t Fittest Individual: " + fittestSuperBefore);

        Individual fittestSubIndividualAfter = filteredNewPopulation.getFittestIndividual();
        I fittestSuperAfter = (I) fittestSubIndividualAfter.additionalObjects.get(MultipleSpeciesCoevolvingGA.superIND);
//        if (fittestSubIndividualAfter.equals(fittestSubIndividualBefore)) {
//            Parameters.logger.fine("SubPopulation / new Fittest!" + newSubPopulation.getIndividualType().getSimpleName() + Parameters.doubleFormat.format(fittestSubIndividualAfter.getFitness()) + "\t Fittest Individual: " + fittestSuperAfter);
//        }
        newSubPopulation = filteredNewPopulation;

        /**
         * Aktualisierung der Population
         */
        ga.species.remove(oldSubPopulation);
        ga.species.put(newSubPopulation, bundle);

        /**
         * Aktualisierung SuperPopulation
         */
        for (Individual subIndividual : newSubPopulation.individuals()) {
            I superIndividual = (I) subIndividual.additionalObjects.get(MultipleSpeciesCoevolvingGA.superIND);
//            if (superIndividual == null) {
//                throw new UnknownError("Kein Mapping zu SuperIndividuum hinterlegt!");
//            }
            if (superIndividual != null) {
                ga.getPopulation().add(superIndividual);
            }
        }
        ga.getPreviousPopulations().put(newSubPopulation.getIndividualType(), newSubPopulation);
        System.gc();
    }
}
