/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.individuals;

import ga.individuals.Individual;
import ga.basics.Population;
import java.util.Map;

/**
 *
 * @author bode
 * @param <I>
 */
public interface MultipleSpeciesIndividualGenerator< I extends Individual> extends Cloneable{
    public I getSuperIndividual(Individual subIndividual, Map<Class, Population> populations);
    public MultipleSpeciesIndividualGenerator<I> clone();
}
