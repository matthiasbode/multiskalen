/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.acceptance;

import ga.basics.IndividualComparator;
import ga.individuals.Individual;
import ga.basics.Population;
import ga.individuals.IntegerIndividual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author bode
 */
public class ThresholdAcceptance<I extends Individual> implements AcceptanceMechanism<I> {

    @Override
    public Population<I> getFilteredNewPopulation(Population<I> oldPop, Population<I> newPop) {
//        double threshold = oldPop.last().getFitness();
//        Population<I> newFiltered = new Population(newPop.getIndividualType(), newPop.numberOfGenerations);
//        for (I old : oldPop.individuals()) {
//            newFiltered.add(old);
//        }
//        for (I newCandidate : newPop.individuals()) {
//            if(newCandidate.getFitness() > threshold){
//                I last = newFiltered.last();
//                newFiltered.remove(last);
//                newFiltered.add(newCandidate);
//            }
//        }
//       
//        return newFiltered;
        int size = oldPop.size();
        Population<I> newFiltered = new Population(newPop.getIndividualType(), newPop.numberOfGenerations);

        ArrayList<I> union = new ArrayList<>();
        union.addAll(oldPop.individuals());
        union.addAll(newPop.individuals());

        for (Iterator<I> iterator = union.iterator(); iterator.hasNext();) {
            I next = iterator.next();
            if (Double.isNaN(next.getFitness())) {
                iterator.remove();
            }
        }
        Collections.sort(union, new IndividualComparator<I>());

        while (newFiltered.size() < size) {
            newFiltered.add(union.remove(union.size() - 1));
        }

        return newFiltered;
    }

//    public static void main(String[] args) {
//        Population<IntegerIndividual> old = new Population(0);
//        IntegerIndividual integerIndividual = new IntegerIndividual(1);
//        integerIndividual.setFitness(-8.0);
//        IntegerIndividual integerIndividual1 = new IntegerIndividual(2);
//        integerIndividual1.setFitness(-4.0);
//        IntegerIndividual integerIndividual2 = new IntegerIndividual(3);
//        integerIndividual2.setFitness(-3.0);
//        IntegerIndividual integerIndividual3 = new IntegerIndividual(4);
//        integerIndividual3.setFitness(-1.0);
//        IntegerIndividual integerIndividual4 = new IntegerIndividual(5);
//        integerIndividual4.setFitness(-9.0);
//        IntegerIndividual integerIndividual5 = new IntegerIndividual(6);
//        integerIndividual5.setFitness(-7.0);
//        old.add(integerIndividual);
//        old.add(integerIndividual1);
//        old.add(integerIndividual2);
//        old.add(integerIndividual3);
//        old.add(integerIndividual4);
//        old.add(integerIndividual5);
//
//        Population<IntegerIndividual> new1 = new Population(1);
//
//        IntegerIndividual aintegerIndividual = new IntegerIndividual(7);
//        aintegerIndividual.setFitness(-20.0);
//        IntegerIndividual aintegerIndividual1 = new IntegerIndividual(8);
//        aintegerIndividual1.setFitness(-24.0);
//        IntegerIndividual aintegerIndividual2 = new IntegerIndividual(9);
//        aintegerIndividual2.setFitness(-23.0);
//        IntegerIndividual aintegerIndividual3 = new IntegerIndividual(10);
//        aintegerIndividual3.setFitness(-2.0);
//        IntegerIndividual aintegerIndividual4 = new IntegerIndividual(11);
//        aintegerIndividual4.setFitness(-9.0);
//        IntegerIndividual aintegerIndividual5 = new IntegerIndividual(12);
//        aintegerIndividual5.setFitness(-7.0);
//        new1.add(aintegerIndividual);
//        new1.add(aintegerIndividual1);
//        new1.add(aintegerIndividual2);
//        new1.add(aintegerIndividual3);
//        new1.add(aintegerIndividual4);
//        new1.add(aintegerIndividual5);
//
//        ThresholdAcceptance<IntegerIndividual> t = new ThresholdAcceptance<>();
//        Population<IntegerIndividual> filteredNewPopulation = t.getFilteredNewPopulation(old, new1);
//
//        for (IntegerIndividual i : filteredNewPopulation.getIndividualsSortedList()) {
//            System.out.println(i + ":" + i.getFitness());
//        }
//    }
}
