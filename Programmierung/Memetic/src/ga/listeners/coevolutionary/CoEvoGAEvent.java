/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.listeners.coevolutionary;

import ga.algorithms.GAAlgorithm;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.basics.Population;
import ga.listeners.GAEvent;
import java.util.Collection;

/**
 *
 * @author bode
 */
public class CoEvoGAEvent<C extends SuperIndividual> extends GAEvent<C> {

    public Collection<Population> subpopulations;

    public CoEvoGAEvent(GAAlgorithm source, StatusGAEvent status, Population<C> population, Collection<Population> subpopulations) {
        super(source, status, population);
        this.subpopulations = subpopulations;
    }

}
