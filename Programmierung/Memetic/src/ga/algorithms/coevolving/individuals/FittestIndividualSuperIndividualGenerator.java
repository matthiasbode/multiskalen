/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.individuals;

import ga.individuals.Individual;
import ga.basics.Population;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bode
 */
public class FittestIndividualSuperIndividualGenerator<E extends SuperIndividual> implements MultipleSpeciesIndividualGenerator<E> {

    private SuperIndividualCreator<E> creator;

    public FittestIndividualSuperIndividualGenerator(SuperIndividualCreator<E> creator) {
        this.creator = creator;
    }

    @Override
    public E getSuperIndividual(Individual subIndividual, Map<Class, Population> populations) {
        Map<Class, Individual> fittest = new HashMap<>();
        fittest.put(subIndividual.getClass(), subIndividual);
        for (Class other : populations.keySet()) {
            if (other.equals(subIndividual.getClass())) {
                continue;
            }
            Individual fittestIndividual = populations.get(other).getFittestIndividual();
            fittest.put(other, fittestIndividual);
        }
        return creator.create(fittest);
    }

    @Override
    public MultipleSpeciesIndividualGenerator<E> clone() {
        return this;
    }

}
