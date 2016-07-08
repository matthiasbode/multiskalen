/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.crossover;

import ga.Parameters;
import ga.individuals.Individual;
import ga.individuals.IntegerIndividual;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bode
 */
public class UniformOrderBasedCrossOver<I extends Individual> implements Crossover<I> {

    @Override
    public List<I> recombine(I c1, I c2, double xOverRate) {
        if (c1.size() != c2.size()) {
            throw new IllegalArgumentException("Unterschiedliche Länge der Gene");
        }
        ArrayList<I> result = new ArrayList<>();
        int[] bitmask = new int[c1.size()];
        for (int i = 0; i < bitmask.length; i++) {
            bitmask[i] = Parameters.getRandom().nextDouble() < 0.5 ? 1 : 0;
        }

        I clone1 = (I) c1.clone();

        I clone2 = (I) c2.clone();
        for (Object object : clone1.getChromosome()) {
            if (object == null) {
                throw new UnknownError("NUll?????");
            }
        }
        for (Object object : clone2.getChromosome()) {
            if (object == null) {
                throw new UnknownError("NUll?????");
            }
        }
        ArrayList rest1 = new ArrayList<>();
        ArrayList rest2 = new ArrayList<>();

        for (int i = 0; i < bitmask.length; i++) {
            if (bitmask[i] == 1) {
                clone2.getChromosome().set(i, null);
                rest2.add(c2.getChromosome().get(i));
            } else {
                clone1.getChromosome().set(i, null);
                rest1.add(c1.getChromosome().get(i));
            }
        }

        LinkedList order1 = new LinkedList(c2.getChromosome());
        LinkedList order2 = new LinkedList(c1.getChromosome());

        order1.retainAll(rest1);
        order2.retainAll(rest2);

        for (int i = 0; i < clone1.getChromosome().size(); i++) {
            if (clone1.getChromosome().get(i) == null) {
                Object poll = order1.poll();
                if (poll == null) {
                    throw new UnknownError("Null-Wert");
                }
                clone1.getChromosome().set(i, poll);

            }
        }

        for (int i = 0; i < clone2.getChromosome().size(); i++) {
            if (clone2.getChromosome().get(i) == null) {
                Object poll = order2.poll();
                if (poll == null) {
                    throw new UnknownError("Null-Wert");
                }
                clone2.getChromosome().set(i, poll);
            }
        }

        result.add(clone1);
        result.add(clone2);
        if (clone1.getChromosome() == null || clone2.getChromosome() == null) {
            throw new UnknownError("Null-Chromosome");
        }
         for (Object object : clone1.getChromosome()) {
            if (object == null) {
                throw new UnknownError("NUll?????");
            }
        }
        for (Object object : clone2.getChromosome()) {
            if (object == null) {
                throw new UnknownError("NUll?????");
            }
        }
        return result;
    }

    public static void main(String[] args) {
        IntegerIndividual i1 = new IntegerIndividual(1, 2, 3, 4, 5);
        IntegerIndividual i2 = new IntegerIndividual(4, 3, 5, 2, 1);
        UniformOrderBasedCrossOver<IntegerIndividual> cross = new UniformOrderBasedCrossOver();
        Collection<IntegerIndividual> recombine = cross.recombine(i1, i2, 1.0);
        for (IntegerIndividual object : recombine) {
            System.out.println(object.getChromosome());
        }
    }

}
