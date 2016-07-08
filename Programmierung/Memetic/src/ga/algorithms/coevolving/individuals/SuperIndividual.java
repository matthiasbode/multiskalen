/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.individuals;

import ga.individuals.Individual;
import java.util.Arrays;

/**
 *
 * @author bode
 */
public abstract class SuperIndividual extends Individual<Individual> {

    public SuperIndividual(Individual... subind) {
        chromosome.addAll(Arrays.asList(subind));
    }

    public SuperIndividual() {
    }
    
    
    
}
