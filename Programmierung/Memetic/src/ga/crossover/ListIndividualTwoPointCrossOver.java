/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.Parameters;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class ListIndividualTwoPointCrossOver<E> implements Crossover<ListIndividual<E>> {

    @Override
    public Collection<? extends ListIndividual<E>> recombine(ListIndividual<E> c1, ListIndividual<E> c2, double xOverRate) {
        ListIndividual<E> clone1 = c1.clone();
        ListIndividual<E> clone2 = c1.clone();
//        int i = RandomUtilities.getRandomValue(Parameters.getRandom(),0,c1.getChromosome().size()-1);
        for (int i = 0; i < c1.getChromosome().size(); i++) {
            SubListIndividual<E> subListIndividual1 = c1.getChromosome().get(i);
            SubListIndividual<E> subListIndividual2 = c2.getChromosome().get(i);
            PMX<SubListIndividual<E>> cross = new PMX<>();
            List<? extends SubListIndividual<E>> recombine = cross.recombine(subListIndividual1, subListIndividual2, xOverRate);
            clone1.set(i, recombine.get(0));
            clone2.set(i, recombine.get(1));
        }
        ArrayList<ListIndividual<E>> arrayList = new ArrayList<ListIndividual<E>>();
        arrayList.add(clone1);
        arrayList.add(clone2);
        return arrayList;
    }

}
