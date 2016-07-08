/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.acceptance.reset;

import ga.acceptance.*;
import ga.individuals.Individual;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import java.util.ArrayList;

/**
 *
 * @author bode
 */
public class ResetElitismus<I extends Individual> extends Elitismus<I> {


    public int maxStagnation;

    /**
     * Verwaltet die Fitness-Entwicklung für etwaigen Neustart.
     */
    private ArrayList<Double> fitness = new ArrayList<Double>();

    private StartPopulationGenerator<I> popGenerator;

    public ResetElitismus(int maxStagnation, StartPopulationGenerator popGenerator, double eliteRate) {
        super(eliteRate);
        this.maxStagnation = maxStagnation;
        this.popGenerator = popGenerator;
    }

    @Override
    public Population<I> getFilteredNewPopulation(Population<I> oldPop, Population<I> newPop) {
        Population<I> newPopFiltered = super.getFilteredNewPopulation(oldPop, newPop);
        /**
         * Fitnesswert hinzufügen
         */
        double fittest = newPopFiltered.getFittestIndividual().getFitness();

        int anzahlAnGenerationenOhneVerbesserung = 0;
        for (int i = fitness.size()-1; i > - 1; i--) {
            if (fitness.get(i) == fittest) {
                anzahlAnGenerationenOhneVerbesserung++;
            }
        }
        if (anzahlAnGenerationenOhneVerbesserung >= maxStagnation) {
            Population<I> resetedPopulation = new Population(newPop.getIndividualType(), newPop.numberOfGenerations);

            ArrayList<I> individualsSortedList = newPopFiltered.getIndividualsSortedList();
            int numberFromOld = individualsSortedList.size() / 2;

            resetedPopulation.addAll(individualsSortedList.subList(0, numberFromOld));
            Population<I> newGeneratePopulation = popGenerator.generatePopulation(individualsSortedList.size() - numberFromOld);

            resetedPopulation.addAll(newGeneratePopulation.getIndividualsSortedList());
        }
        fitness.add(fittest);
        return newPopFiltered;
    }
}
