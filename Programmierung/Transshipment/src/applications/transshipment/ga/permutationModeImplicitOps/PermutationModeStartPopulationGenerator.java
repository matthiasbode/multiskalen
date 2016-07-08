/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps;

import applications.mmrcsp.model.basics.JoNComponent;
import applications.transshipment.model.LoadUnitJob;
import ga.Parameters;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import ga.individuals.subList.SubListIndividual;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class PermutationModeStartPopulationGenerator implements StartPopulationGenerator<PermutationModeIndividual> {

    List<Set<LoadUnitJob>> connectionComponents;

   

    public PermutationModeStartPopulationGenerator(List<Set<LoadUnitJob>> connectionComponents) {
        this.connectionComponents = connectionComponents;
    }

    /**
     * Erzeugt eine Startpopulation indem zufällig für jede
     * Zusammenhangskomponente eine Strategie gewählt wird.
     *
     * @param anzahl
     * @return
     */
    @Override
    public Population<PermutationModeIndividual> generatePopulation(int anzahl, Object... additionalObjects) {
        ArrayList<PermutationModeIndividual> res = new ArrayList<>();

         
        int numberOfConnectionComponents = connectionComponents.size();

        for (int i = 0; i < anzahl; i++) {
            List<SubListIndividual<LoadUnitJob>> list = new ArrayList<>();
            for (int j = 0; j < numberOfConnectionComponents; j++) {
                Set<LoadUnitJob> component = connectionComponents.get(j);
                List<LoadUnitJob> jobs = new ArrayList<>(component);
                Collections.shuffle(jobs, Parameters.getRandom());
                SubListIndividual<LoadUnitJob> subIndividual = new SubListIndividual(jobs);
                if (!subIndividual.getChromosome().containsAll(jobs)) {
                    throw new IllegalArgumentException("Fehler beim Erzeugen!");
                }
                list.add(subIndividual);
            }
            PermutationModeIndividual ind = new PermutationModeIndividual(list);
            res.add(ind);
        }
        Population<PermutationModeIndividual> pop = new Population<>(PermutationModeIndividual.class, res);

        return pop;
    }

}
