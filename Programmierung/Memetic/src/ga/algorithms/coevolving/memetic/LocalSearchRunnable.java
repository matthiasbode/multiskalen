/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.memetic;

import ga.algorithms.coevolving.GABundle;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingGA;
import ga.individuals.Individual;
import ga.basics.Population;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.localSearch.LocalSearch;
import java.util.List;

/**
 *
 * @author bode
 */
public class LocalSearchRunnable<I extends SuperIndividual> implements Runnable {

    final LocalSearch<I> localSearch;
    final List<Individual> list;
    final GABundle<I> bundle;
    final Population<Individual> localPop;
    final Class<Individual> individualType;

    public LocalSearchRunnable(LocalSearch<I> localSearch, List<Individual> list, GABundle<I> bundle, Population<Individual> localPop, Class<Individual> individualType) {
        this.localSearch = localSearch;
        this.list = list;
        this.bundle = bundle;
        this.localPop = localPop;
        this.individualType = individualType;
    }

    @Override
    public void run() {
        /**
         * Schleife über alle Unterindividuuen
         */
        for (Individual individual : list) {
            /**
             * Hole SuperIndividuum
             */
            I superIndividual =  (I) individual.additionalObjects.get(MultipleSpeciesCoevolvingGA.superIND);
            /**
             * Eigentliche Lokale Suche auf dem SuperIndividuum
             */
            I localSuperIndividual = localSearch.localSearch(superIndividual);
            /**
             * Füge das lokale SubIndividuum richtig hinzu.
             */
            for (Individual subIndividual : localSuperIndividual.getList()) {
                /**
                 * Suche das hier überarbeitete Individuum raus, und füge es
                 * hinzu.
                 */
                if (subIndividual.getClass().equals(individualType)) {
                    localPop.add(subIndividual);
                    subIndividual.setFitness(localSuperIndividual.getFitness());
                    subIndividual.additionalObjects.put(MultipleSpeciesCoevolvingGA.superIND, localSuperIndividual);
                }
            }
        }
    }
}
