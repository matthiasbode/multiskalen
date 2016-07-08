/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.individuals.subList.SubListIndividual;
import ga.individuals.subList.ListIndividual;
import ga.Parameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class LOXRecombination<E> implements Crossover<ListIndividual<E>> {

    @Override
    public Collection<? extends ListIndividual<E>> recombine(ListIndividual<E> c1, ListIndividual<E> c2, double xOverRate) {
        if (Parameters.getRandom().nextDouble() > xOverRate) {
            ArrayList<ListIndividual<E>> result = new ArrayList<>();
            result.add(c1);
            result.add(c2);
            return result;
        }

        ListIndividual<E> child1 = c1.clone();
        ListIndividual<E> child2 = c1.clone();
        child1.getList().clear();
        child2.getList().clear();

        for (int knotenklasse = 0; knotenklasse < c1.getList().size(); knotenklasse++) {
            SubListIndividual<E> operationListIndividual1 = c1.getList().get(knotenklasse);
            SubListIndividual<E> operationListIndividual2 = c2.getList().get(knotenklasse);

            int size = operationListIndividual1.size();
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
                final ArrayList<E> list1 = new ArrayList<>(operationListIndividual1.getList());
                final ArrayList<E> list2 = new ArrayList<>(operationListIndividual2.getList());

                List<E> subList1 = operationListIndividual1.getList().subList(start, end);
                List<E> subList2 = operationListIndividual2.getList().subList(start, end);

                list1.removeAll(subList2);
                list2.removeAll(subList1);

                list1.addAll(start, subList2);
                list2.addAll(start, subList1);

                if (list1.size() != operationListIndividual1.size() || list2.size() != operationListIndividual2.size()) {
                    throw new UnknownError("Größe stimmt nicht überein");
                }

                child1.set(knotenklasse, new SubListIndividual(list1));
                child2.set(knotenklasse, new SubListIndividual(list2));
            } else {
                child1.set(knotenklasse, new SubListIndividual(operationListIndividual1.getList()));
                child2.set(knotenklasse, new SubListIndividual(operationListIndividual2.getList()));
            }

        }
        ArrayList<ListIndividual<E>> result = new ArrayList<>();
        result.add(child1);
        result.add(child2);
        return result;

    }
}
