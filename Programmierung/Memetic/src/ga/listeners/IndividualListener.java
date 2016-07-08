/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.listeners;

import ga.individuals.Individual;

/**
 *
 * @author bode
 */
public interface IndividualListener<C extends Individual> {

    public void newIndividual(IndividualEvent<C> event);

    

    public void finished();
}
