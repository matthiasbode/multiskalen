/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.memetic;

import com.google.common.collect.Iterables;
import ga.algorithms.coevolving.GABundle;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingGA;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.algorithms.coevolving.SpeciesNextGenRunnable;
import ga.individuals.Individual;
import ga.Parameters;
import ga.basics.Population;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.localSearch.LocalSearch;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class MemeticSpeciesNextGenRunnable<I extends SuperIndividual> extends SpeciesNextGenRunnable<I> {

    LocalSearch<I> localSearch;

    public MemeticSpeciesNextGenRunnable(MultipleSpeciesCoevolvingParallelGA<I> ga, Population subPopulation, LocalSearch<I> localSearch) {
        super(ga, subPopulation);
        this.localSearch = localSearch;
    }

    /**
     * Lokale Optimum im Post-Processing
     *
     * @param bundle
     * @param newSubPopulation
     * @return neue Unterpopulation
     */
    @Override
    protected Population<Individual> postProcessing(final GABundle<I> bundle, final Population<Individual> newSubPopulation) {
        /**
         * Bestimmung der Fitness der einzelnen Individuen
         */
        super.postProcessing(bundle, newSubPopulation);
        Individual fittestIndividualBefore = newSubPopulation.getFittestIndividual();
        I superBefore = (I) fittestIndividualBefore.additionalObjects.get(MultipleSpeciesCoevolvingGA.superIND);

        /**
         * Neue Population mit lokalen Optima bestimmen
         */
        final Population<Individual> populationAfterLocal = new Population(newSubPopulation.getIndividualType(), newSubPopulation.numberOfGenerations);
        ArrayList<Future<?>> futures = new ArrayList<>();
        Class<Individual> individualType = newSubPopulation.getIndividualType();
        Iterable<List<Individual>> partition = Iterables.partition(newSubPopulation.individuals(), newSubPopulation.individuals().size() / Parameters.NUMBER_OF_RECOMBINATION_THREADS);

        for (final List<Individual> list : partition) {
            LocalSearchRunnable r = new LocalSearchRunnable(localSearch, list, bundle, populationAfterLocal, individualType);
            futures.add(Parameters.getThreadPool().submit(r));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ex) {
                ex.getCause().printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(MemeticSpeciesNextGenRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Individual fittestIndividualAfter = populationAfterLocal.getFittestIndividual();
        I superAfter = (I) fittestIndividualAfter.additionalObjects.get(MultipleSpeciesCoevolvingGA.superIND);

        Parameters.logger.info("Evaluierung: " + newSubPopulation.getIndividualType().getSimpleName());
        Parameters.logger.info("=================================");
        Parameters.logger.info(newSubPopulation.getMeanFitness() + "---->" + populationAfterLocal.getMeanFitness());
        Parameters.logger.info("Vor lokaler Optimierung: " + fittestIndividualBefore.toString() + Parameters.doubleFormat.format(fittestIndividualBefore.getFitness()) + "\t/\t" + superBefore);
        if (fittestIndividualBefore.equals(fittestIndividualAfter)) {
            Parameters.logger.info("keine lokale Verbesserung");
        } else {
            Parameters.logger.info("Nach lokalen Optimierung:" + fittestIndividualAfter.toString() + Parameters.doubleFormat.format(fittestIndividualAfter.getFitness()) + "\t/\t" + superAfter);
        }
        Parameters.logger.info("=================================");

        return populationAfterLocal;
    }

}
