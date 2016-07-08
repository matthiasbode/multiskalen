/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.basics;

import ga.individuals.Individual;

/**
 *
 * @author bode
 * @param <I>
 */
public interface StartPopulationGenerator<I extends Individual> {

    public Population<I> generatePopulation(int anzahl, Object... additionalObjects);
}
