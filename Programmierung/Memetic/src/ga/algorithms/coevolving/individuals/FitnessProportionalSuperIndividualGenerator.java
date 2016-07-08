/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.individuals;

import ga.individuals.Individual;
import ga.basics.Population;
import ga.selection.FitnessProportionalSelection;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 *
 * @author bode
 * @param <E>
 */
public class FitnessProportionalSuperIndividualGenerator<E extends SuperIndividual> implements MultipleSpeciesIndividualGenerator<E> {

    SuperIndividualCreator<E> creator;
    Map<Class, FitnessProportionalSelection<?>> selection = new HashMap<>();

    public FitnessProportionalSuperIndividualGenerator(SuperIndividualCreator<E> creator, Collection<Class> subIndividuals) {
        this.creator = creator;
        for (Class subIndividual : subIndividuals) {
            selection.put(subIndividual, new FitnessProportionalSelection());
        }
    }

    public FitnessProportionalSuperIndividualGenerator(SuperIndividualCreator<E> creator, Class... subIndividuals) {
        this.creator = creator;
        for (Class subIndividual : subIndividuals) {
            selection.put(subIndividual, new FitnessProportionalSelection());
        }
    }

    @Override
    public E getSuperIndividual(Individual subIndividual, Map<Class, Population> populations) {
        if (selection.size() == 0) {
            for (Class cls : populations.keySet()) {
                selection.put(cls, new FitnessProportionalSelection());
            }
        }
        Map<Class, Individual> choosen = new HashMap<>();
        choosen.put(subIndividual.getClass(), subIndividual);

        for (Class other : populations.keySet()) {
            if (other.equals(subIndividual.getClass())) {
                continue;
            }

            Individual c = null;
            Population pop = populations.get(other);
            if (!pop.reseted) {
                c = selection.get(other).selectFromPopulation(pop);
            } else {
                c = pop.getRandomIndividual();
            }
            if (c == null) {
                throw new UnknownError("Was hat hier nicht geklappt");
            }
            choosen.put(other, c);
        }
        return creator.create(choosen);
    }

    @Override
    public FitnessProportionalSuperIndividualGenerator clone() {
        return new FitnessProportionalSuperIndividualGenerator(creator);
    }

}
