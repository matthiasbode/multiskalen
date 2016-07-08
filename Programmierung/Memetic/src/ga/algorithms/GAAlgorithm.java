/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms;

import ga.basics.FitnessEvalationFunction;
import ga.basics.Population;
import ga.individuals.Individual;

import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import java.util.ArrayList;

/**
 *
 * @author bode
 */
public abstract class GAAlgorithm<I extends Individual> {

    ArrayList<GAListener> listeners = new ArrayList<>();

    public GAAlgorithm() {
    }

    public abstract FitnessEvalationFunction<I> getFitnessEvalationFunction();
    public abstract void run();

    public abstract Population<I> getPopulation();

    public void addGAListener(GAListener<I> listener) {
        this.listeners.add(listener);
    }

    public void removeGAListener(GAListener<I> listener) {
        this.listeners.remove(listener);
    }

    public void fire(GAEvent<I> event) {
        for (GAListener gAListener : listeners) {
            if (event.status == GAEvent.StatusGAEvent.NEXTGENERATION) {
                gAListener.nextGeneration(event);
            }
            if (event.status == GAEvent.StatusGAEvent.FINISHED) {
                gAListener.finished(event);
            }
        }
    }
    
   

}
