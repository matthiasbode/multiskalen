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
public class NonFilteredNewPop<I extends Individual> implements AcceptanceMechanism<I> {

    public NonFilteredNewPop() {
    }

    @Override
    public Population<I> getFilteredNewPopulation(Population<I> oldPop, Population<I> newPop) {
        return newPop;
    }
}
