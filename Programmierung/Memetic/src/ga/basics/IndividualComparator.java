/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.basics;

import ga.individuals.Individual;
import java.util.Comparator;

/**
 *
 * @author bode
 * @param <I>
 */
public class IndividualComparator<I extends Individual> implements Comparator<I> {

//    @Override
//    public int compare(I o1, I o2) {
//        int c = Double.compare(o1.getFitness(), o2.getFitness());
//        if (c == 0) {
//            return Integer.compare(o1.getNumber(), o2.getNumber());
//        }
//        return c;
//    }

    @Override
    public int compare(I o1, I o2) {
        for (int i = 0; i < o1.getFitnessVector().length; i++) {
            double f1_i = o1.getFitnessVector()[i];
            double f2_i = o2.getFitnessVector()[i];
            int c = Double.compare(f1_i, f2_i);
            if (c != 0) {
                return c;
            }
        }
        return Integer.compare(o1.getNumber(), o2.getNumber());
    }
}
