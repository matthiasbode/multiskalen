/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.acceptance.reset;

import ga.Parameters;
import ga.acceptance.*;
import ga.individuals.Individual;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author bode
 */
public class ResetThreshold<I extends Individual> extends ThresholdAcceptance<I> {

    public int maxStagnation;
    public int originalMaxStagnation;
    
    /**
     * Verwaltet die Fitness-Entwicklung für etwaigen Neustart.
     */
    private ArrayList<Double> fitness = new ArrayList<Double>();

    private StartPopulationGenerator<I> popGenerator;

    public ResetThreshold(int maxStagnation, StartPopulationGenerator popGenerator) {
        super();
        this.maxStagnation = maxStagnation;
        this.popGenerator = popGenerator;
        this.originalMaxStagnation = maxStagnation;
    }

    @Override
    public Population<I> getFilteredNewPopulation(Population<I> oldPop, Population<I> newPop) {
        Population<I> newPopFiltered = super.getFilteredNewPopulation(oldPop, newPop);
        /**
         * Fitnesswert hinzufügen
         */
        double fittest = newPopFiltered.getFittestIndividual().getFitness();

        int anzahlAnGenerationenOhneVerbesserung = 0;
        for (int i = fitness.size() - 1; i > - 1; i--) {
            if (fitness.get(i).equals(fittest)) {
                anzahlAnGenerationenOhneVerbesserung++;
            } else {
                break;
            }
        }
        if (anzahlAnGenerationenOhneVerbesserung >= maxStagnation) {

            Parameters.logger.fine("RESET der Population " + oldPop.getIndividualType() + " nach " + anzahlAnGenerationenOhneVerbesserung + " ohne Verbesserung in Generation " + oldPop.numberOfGenerations);
            Population<I> resetedPopulation = new Population(newPop.getIndividualType(), newPop.numberOfGenerations);
            resetedPopulation.reseted = true;
            ArrayList<I> individualsSortedList = new ArrayList<>(newPopFiltered.getIndividualsSortedList());
            int numberFromOld = individualsSortedList.size()/ 4;//Math.max(1, (int) (individualsSortedList.size() / 10. + 0.5));
            Collections.reverse(individualsSortedList);
            resetedPopulation.addAll(individualsSortedList.subList(0, numberFromOld));

            Population<I> newGeneratePopulation = popGenerator.generatePopulation(individualsSortedList.size() - numberFromOld);
            resetedPopulation.addAll(newGeneratePopulation.individuals());
            fitness.clear();
            maxStagnation = originalMaxStagnation*2;
            return resetedPopulation;
        }
        fitness.add(fittest);
        return newPopFiltered;
    }

    public static void main(String[] args) {
        ArrayList<Double> fitness = new ArrayList<Double>();
        fitness.add(2.0);
        fitness.add(6.0);
        fitness.add(6.0);
        fitness.add(6.0);
        fitness.add(6.0);
        fitness.add(6.0);
        fitness.add(7.0);
        fitness.add(7.0);

        double fittest = 7.0;
        int anzahlAnGenerationenOhneVerbesserung = 0;
        for (int i = fitness.size() - 1; i > - 1; i--) {
            if (fitness.get(i) == fittest) {
                anzahlAnGenerationenOhneVerbesserung++;
            } else {
                break;
            }
        }
        System.out.println("Anzahl ohne Verbesserungen: " + anzahlAnGenerationenOhneVerbesserung);

    }
}
