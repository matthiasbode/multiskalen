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
public interface GAListener<C extends Individual >  {

    public void nextGeneration(GAEvent<C> event);

    public void finished(GAEvent<C> event);

}
