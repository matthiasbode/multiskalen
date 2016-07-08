/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.individuals;

import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public interface SuperIndividualCreator<E extends SuperIndividual> {

    public E create(Map<Class, Individual> subIndividuals);

}
