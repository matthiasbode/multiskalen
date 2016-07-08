/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.listeners;

import ga.basics.Population;
import ga.individuals.Individual;
import ga.nextGeneration.NextGenerationAlgorithm;

/**
 *
 * @author bode
 */
public class IndividualEvent<C extends Individual> extends java.util.EventObject {

    public enum StatusIndividualEvent {
        FINISH,
        NEW_GA_INDIVIDUAL,
        NEW_LS_INDIVIDUAL,
    }

    public StatusIndividualEvent status;
    public C originIndividual;
    public C individual;
    public int populationNumber;
    public Population<C> population;

   
    public IndividualEvent(NextGenerationAlgorithm source, StatusIndividualEvent status, int populationNumber, C individual) {
        super(source);
        this.populationNumber = populationNumber;
        this.status = status;
        this.individual = individual;
    }

    public IndividualEvent(NextGenerationAlgorithm source, StatusIndividualEvent status, int populationNumber, C individual, C originIndividual) {
        super(source);
        this.status = status;
        this.originIndividual = originIndividual;
        this.individual = individual;
        this.populationNumber = populationNumber;
    }

    public IndividualEvent(NextGenerationAlgorithm source) {
        super(source);
        this.status = StatusIndividualEvent.FINISH;
    }
    
    

    
    @Override
    public NextGenerationAlgorithm getSource() {
        return (NextGenerationAlgorithm) super.getSource();
    }
}
