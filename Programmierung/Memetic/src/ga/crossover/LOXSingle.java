/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.Parameters;
import ga.individuals.Individual;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import util.RandomUtilities;

/**
 *
 * @author bode
 * @param <E>
 */
public class LOXSingle<E extends Individual> implements Crossover<E> {

    @Override
    public Collection<? extends E> recombine(E c1, E c2, double xOverRate) {
        ArrayList<E> childs = new ArrayList<>();
        int size = c1.size();

        E child1 = (E) c1.clone();
        E child2 = (E) c2.clone();
        if (size != 1) {

            /**
             * Head
             */
            // choose two random numbers for the start and end indices of the slice
            // (one can be at index "size")
            int number1 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
            int number2;
            do {
                number2 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
            } while (number1 == number2);

            // make the smaller the start and the larger the end
            int start = Math.min(number1, number2);
            int end = Math.max(number1, number2);

            // instantiate two child parents
            final ArrayList<E> list1 = new ArrayList<>(c1.getList());
            final ArrayList<E> list2 = new ArrayList<>(c2.getList());

            List<E> subList1 = c1.getList().subList(start, end);
            List<E> subList2 = c2.getList().subList(start, end);

            list1.removeAll(subList2);
            list2.removeAll(subList1);

            list1.addAll(start, subList2);
            list2.addAll(start, subList1);

            if (list1.size() != c1.size() || list2.size() != c2.size()) {
                throw new UnknownError("Größe stimmt nicht überein");
            }

            child1.setChromosome(list1);
            child2.setChromosome(list2);
        } else {
            child1.setChromosome(c1.getList());
            child2.setChromosome(c2.getList());
        }

        return childs;
    }

}
