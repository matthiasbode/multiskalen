/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.individuals.TwoGenListIndividual;
import ga.Parameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class OXTwoGenListRecombination<K, T> implements Crossover<TwoGenListIndividual<K, T>> {

    @Override
    public Collection<? extends TwoGenListIndividual<K, T>> recombine(TwoGenListIndividual<K, T> p1, TwoGenListIndividual<K, T> p2, double xOverRate) {
        TwoGenListIndividual<K, T> c1 = p1;
        TwoGenListIndividual<K, T> c2 = p2;

        if (Parameters.getRandom().nextDouble() > xOverRate) {
            ArrayList<TwoGenListIndividual<K, T>> result = new ArrayList<>();
            result.add(p1);
            result.add(p2);
            return result;
        }
        int size = c1.getHead().size();

        /**
         * Head
         */
        // choose two random numbers for the start and end indices of the slice
        // (one can be at index "size")
        final int number1 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
        final int number2 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size);

        // make the smaller the start and the larger the end
        final int start = Math.min(number1, number2);
        final int end = Math.max(number1, number2);

        // instantiate two child parents
        final ArrayList<K> child1Head = new ArrayList<>();
        final ArrayList<K> child2Head = new ArrayList<>();

        // add the sublist in between the start and end points to the children
        child1Head.addAll(c1.getHead().subList(start, end));
        child2Head.addAll(c2.getHead().subList(start, end));

        // iterate over each object in the parents

        K currentObjectParent1;
        K currentObjectParent2;
        for (int i = 0; i < size; i++) {


            // get the index of the current object
            int currentIndex = (end + i) % size;

            // get the object at the current index in each of the two parent parents
            currentObjectParent1 = c1.getHead().get(currentIndex);
            currentObjectParent2 = c2.getHead().get(currentIndex);

            // if child 1 does not already contain the current object in parent2 , add it
            if (!child1Head.contains(currentObjectParent2)) {
                child1Head.add(currentObjectParent2);
            }

            // if child 2 does not already contain the current object in parent 1, add it
            if (!child2Head.contains(currentObjectParent1)) {
                child2Head.add(currentObjectParent1);
            }
        }

        // rotate the lists so the original slice is in the same place as in the
        // parent parents
        Collections.rotate(child1Head, start);
        Collections.rotate(child2Head, start);


        /**
         * Tail
         */
        int point = (int) (Parameters.getRandom().nextDouble() * size);

        // instantiate two child parents
        final ArrayList<T> child1Tail = new ArrayList<>();
        final ArrayList<T> child2Tail = new ArrayList<>();

        // Half from one, half from the other
        for (int i = 0; i < c1.getTail().size(); i++) {
            if (i > point) {
                child1Tail.add(c1.getTail().get(i));
                child2Tail.add(c2.getTail().get(i));
            } else {
                child1Tail.add(c2.getTail().get(i));
                child2Tail.add(c1.getTail().get(i));
            }
        }



        ArrayList<TwoGenListIndividual<K, T>> result = new ArrayList<>();
        result.add(new TwoGenListIndividual<>(child1Head, child1Tail));
        result.add(new TwoGenListIndividual<>(child2Head, child2Tail));
        return result;
    }

    public static void main(String[] args) {

        TwoGenListIndividual<Integer, Integer> ta = new TwoGenListIndividual(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, new Integer[]{1, 1, 1, 0, 0, 0, 1, 1, 1});
        TwoGenListIndividual<Integer, Integer> tb = new TwoGenListIndividual(new Integer[]{5, 6, 7, 8, 1, 2, 3, 4, 9}, new Integer[]{0, 0, 0, 1, 1, 1, 0, 0, 0});
        System.out.println(ta);
        System.out.println(tb);
        System.out.println("============");
        OXTwoGenListRecombination<Integer, Integer> cross = new OXTwoGenListRecombination();
        Collection<? extends TwoGenListIndividual<Integer, Integer>> recombine = cross.recombine(ta, tb, 1.0);
        for (TwoGenListIndividual<Integer, Integer> twoGenListChromosome : recombine) {
            System.out.println(twoGenListChromosome);
        }

    }
}
