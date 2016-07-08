/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.acceptance;

import ga.individuals.Individual;
import ga.basics.Population;

/**
 *
 * @author bode
 */
public class Elitismus<I extends Individual> implements AcceptanceMechanism<I> {

    private double eliteRate;

    public Elitismus(double eliteRate) {
        this.eliteRate = eliteRate;
    }

    @Override
    public Population<I> getFilteredNewPopulation(Population<I> oldPop, Population<I> newPop) {
        int numberOfOldPop = (int) (oldPop.size() * eliteRate + 0.5);
        if (numberOfOldPop == 0 && eliteRate > 0) {
            numberOfOldPop = 1;
        }
        Population<I> newPopFiltered = new Population(newPop.getIndividualType(), newPop.numberOfGenerations);
        newPopFiltered.addAll(oldPop.getIndividualsSortedList().subList(oldPop.size() - numberOfOldPop, oldPop.size()));
        int individualsFromNewPop = oldPop.size() - newPopFiltered.size();
        newPopFiltered.addAll(newPop.getIndividualsSortedList().subList(newPop.size() - individualsFromNewPop, newPop.size()));
        return newPopFiltered;
    }
}
