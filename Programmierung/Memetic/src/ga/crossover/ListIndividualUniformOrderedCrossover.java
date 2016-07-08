/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author bode
 */
public class ListIndividualUniformOrderedCrossover<E> implements Crossover<ListIndividual<E>> {

    @Override
    public Collection<? extends ListIndividual<E>> recombine(ListIndividual<E> c1, ListIndividual<E> c2, double xOverRate) {
        ListIndividual<E> child1 = c1.clone();
        ListIndividual<E> child2 = c2.clone();
        for (int i = 0; i < c1.getChromosome().size(); i++) {
            SubListIndividual<E> subListIndividual1 = c1.getChromosome().get(i);
            SubListIndividual<E> subListIndividual2 = c2.getChromosome().get(i);
            UniformOrderBasedCrossOver<SubListIndividual<E>> cross = new UniformOrderBasedCrossOver<>();
            List<SubListIndividual<E>> recombine = cross.recombine(subListIndividual1, subListIndividual2, xOverRate);
            child1.set(i, recombine.get(0));
            child2.set(i, recombine.get(1));
        }
        ArrayList<ListIndividual<E>> arrayList = new ArrayList<>();
        arrayList.add(child1);
        arrayList.add(child2);
        return arrayList;
    }

}
