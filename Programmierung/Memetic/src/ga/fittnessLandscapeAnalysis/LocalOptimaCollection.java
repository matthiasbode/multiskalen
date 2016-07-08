/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis;

import ga.individuals.Individual;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import ga.listeners.IndividualEvent;
import ga.listeners.IndividualListener;
import java.util.LinkedHashSet;

/**
 *
 * @author bode
 */
public class LocalOptimaCollection<C extends Individual> extends LinkedHashSet<C> implements IndividualListener<C> {

    @Override
    public void newIndividual(IndividualEvent<C> event) {
        if (event.status.equals(IndividualEvent.StatusIndividualEvent.NEW_LS_INDIVIDUAL)) {
            this.add(event.individual);
        }
    }

    @Override
    public void finished() {

    }
 
}
