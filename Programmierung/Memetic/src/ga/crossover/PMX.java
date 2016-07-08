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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class PMX<I extends Individual> implements Crossover<I> {

    @Override
    public List<I> recombine(I parent1, I parent2, double xOverRate) {
        if (Parameters.getRandom().nextDouble() > xOverRate) {
            ArrayList<I> result = new ArrayList<>();
            result.add(parent1);
            result.add(parent2);
            return result;
        }

        int size = parent1.size();

        // choose two random numbers for the start and end indices of the slice
        // (one can be at index "size")
        int number1 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size - 1);
        int number2 = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, size);

        // make the smaller the start and the larger the end
        int point1 = Math.min(number1, number2);
        int point2 = Math.max(number1, number2);

        if (parent1.size() != parent2.size()) {
            throw new IllegalArgumentException("Unterschiedliche Länge der Gene");
        }
        ArrayList<I> result = new ArrayList<>();

        final I offspring1 = (I) parent1.clone();
        final I offspring2 = (I) parent2.clone();

        int length = point2 - point1;
        if (length < 0) {
            length += parent1.size();
        }

        Map<Object, Object> mapping1 = new HashMap<>(length * 2); // Big enough map to avoid re-hashing.
        Map<Object, Object> mapping2 = new HashMap<>(length * 2);
        for (int i = 0; i < length; i++) {
            int index = (i + point1) % parent1.size();
            Object item1 = offspring1.get(index);
            Object item2 = offspring2.get(index);
            offspring1.set(index, item2);
            offspring2.set(index, item1);
            mapping1.put(item1, item2);
            mapping2.put(item2, item1);
        }

        checkUnmappedElements(offspring1.getChromosome(), mapping2, point1, point2);
        checkUnmappedElements(offspring2.getChromosome(), mapping1, point1, point2);

        result.add(offspring1);
        result.add(offspring2);

        if (offspring2.getChromosome()
                == null || offspring2.getChromosome() == null) {
            throw new UnknownError("Null-Chromosome");
        }
        for (Object object
                : offspring2.getChromosome()) {
            if (object == null) {
                throw new UnknownError("NUll?????");
            }
        }
        for (Object object
                : offspring2.getChromosome()) {
            if (object == null) {
                throw new UnknownError("NUll?????");
            }
        }
        return result;

    }

    /**
     * Checks elements that are outside of the partially mapped section to see
     * if there are any duplicate items in the list. If there are, they are
     * mapped appropriately.
     */
    private void checkUnmappedElements(List<Object> offspring,
            Map<Object, Object> mapping,
            int mappingStart,
            int mappingEnd) {
        for (int i = 0; i < offspring.size(); i++) {
            if (!isInsideMappedRegion(i, mappingStart, mappingEnd)) {
                Object mapped = offspring.get(i);
                while (mapping.containsKey(mapped)) {
                    mapped = mapping.get(mapped);
                }
                offspring.set(i, mapped);
            }
        }
    }

    /**
     * Checks whether a given list position is within the partially mapped
     * region used for cross-over.
     *
     * @param position The list position to check.
     * @param startPoint The starting index (inclusive) of the mapped region.
     * @param endPoint The end index (exclusive) of the mapped region.
     * @return True if the specified position is in the mapped region, false
     * otherwise.
     */
    private boolean isInsideMappedRegion(int position,
            int startPoint,
            int endPoint) {
        boolean enclosed = (position < endPoint && position >= startPoint);
        boolean wrapAround = (startPoint > endPoint && (position >= startPoint || position < endPoint));
        return enclosed || wrapAround;
    }

    public static void main(String[] args) {
        IntegerIndividual i1 = new IntegerIndividual(1, 2, 3, 4, 5, 6, 7, 8);
        IntegerIndividual i2 = new IntegerIndividual(7, 1, 2, 6, 3, 8, 5, 4);
        PMX<IntegerIndividual> cross = new PMX<>();
        Collection<IntegerIndividual> recombine = cross.recombine(i1, i2, 1.0);
        for (IntegerIndividual object : recombine) {
            System.out.println(object.getChromosome());
        }
    }

}
