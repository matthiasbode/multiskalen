/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.mutation;

import ga.individuals.Individual;
import ga.individuals.Individual;
import ga.Parameters;

/**
 *
 * @author hofmann
 */
public class SwapMutation<C extends Individual> implements Mutation<C> {

    /**
     * Methode vertauscht ggf. (siehe xMutationRate) die Werte zweier Gene
     *
     * @param chrom zu veränderndes Chromosom
     * @param xMutationRate [0,1] bestimmt die Wahrscheinlichkeit, dass das
     * Chromosom überhaupt verändert wird
     */
    @Override
    public C mutate(C ind, double xMutationRate) {
        if (Parameters.getRandom().nextDouble() > xMutationRate) {
            return ind;
        }
        int size = ind.size();
        if (size == 1) {
            return ind;
        }
        C c = (C) ind.clone();
        int index1 = (int) Math.round(Parameters.getRandom().nextDouble() * (size - 1));
        int index2;
        do {
            index2 = (int) Math.round(Parameters.getRandom().nextDouble() * (size - 1));
        } while (index1 == index2);

        Object temp = ind.get(index1);
        c.set(index1, ind.get(index2));
        c.set(index2, temp);
        return c;
    }

    @Override
    public String toString() {
        return "SwapMutation{" + '}';
    }
    
}
