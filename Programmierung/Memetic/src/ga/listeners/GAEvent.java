/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.listeners;

import ga.algorithms.GAAlgorithm;
import ga.individuals.Individual;
import ga.basics.Population;

/**
 *
 * @author bode
 */
public class GAEvent<C extends Individual> extends java.util.EventObject {

    public enum StatusGAEvent {
        FINISHED,
        NEXTGENERATION, 
        NEXTGENERATION_BEFOREFILTERING
    }

    public StatusGAEvent status;
  
    public int populationNumber;
    public Population<C> population;

    public GAEvent(GAAlgorithm source, StatusGAEvent status, Population<C> population) {
        super(source);
        this.status = status;
        this.population = population;
       
        this.populationNumber = population.numberOfGenerations;
    }

    

    @Override
    public GAAlgorithm getSource() {
        return (GAAlgorithm) super.getSource();
    }
}
