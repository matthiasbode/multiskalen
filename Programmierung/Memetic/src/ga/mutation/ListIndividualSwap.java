/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.mutation;

import ga.individuals.subList.SubListIndividual;
import ga.individuals.subList.ListIndividual;
import ga.Parameters;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class ListIndividualSwap<E> implements Mutation<ListIndividual<E>> {

    @Override
    public ListIndividual<E> mutate(ListIndividual<E> ind, double xMutationRate) {
        if (Parameters.getRandom().nextDouble() > xMutationRate) {
            return ind;
        }
        ListIndividual<E> child = ind.clone();

        /**
         * Wähle zunächst Knotenklasse.
         */
        int size;
        SubListIndividual<E> list;
        do {
            int vertexClass = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, child.size() - 1);
            list = child.get(vertexClass);
            size = list.size();
        } while (size <= 1);

        /**
         * Tausche dann Positionen.
         */
        int index1 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
        int index2;
        do {
            index2 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
        } while (index1 == index2);

        E temp = list.get(index1);
        list.set(index1, list.get(index2));
        list.set(index2, temp);
        return child;
    }
}
