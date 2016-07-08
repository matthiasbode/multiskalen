/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.acceptance;

import ga.individuals.Individual;
import ga.basics.Population;

/**
 * Dient dazu eine Schnittstelle zu definieren, in deren Implementierung 
 * entschieden wird, welche Individuen in die nächste Generation kommen.
 * Zunächst werden alle Individuen in die neue Generation hinzugefügt. 
 * Daraufhin wird mit den alten Individuen verglichen und es können 
 * Mechanismen wie Elitismus, etc. umgesetzt werden.
 * @author bode
 */
public interface AcceptanceMechanism<I extends Individual> {
    public Population<I> getFilteredNewPopulation(Population<I> oldPop, Population<I> newPop);
}
