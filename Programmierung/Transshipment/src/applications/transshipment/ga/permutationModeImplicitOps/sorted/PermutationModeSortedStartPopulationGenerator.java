/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps.sorted;

import applications.mmrcsp.model.basics.JoNComponent;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import ga.Parameters;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import ga.individuals.IntegerIndividual;
import ga.individuals.subList.SubListIndividual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author bode
 */
public class PermutationModeSortedStartPopulationGenerator implements StartPopulationGenerator<PermutationModeIndividualSorted> {
    
    MultiJobTerminalProblem problem;
    public int numberOfRoutes = 9;
    
    public PermutationModeSortedStartPopulationGenerator(MultiJobTerminalProblem problem, int numberOfRoutes) {
        this.problem = problem;
        this.numberOfRoutes = numberOfRoutes;
    }

    /**
     * Erzeugt eine Startpopulation indem zufällig für jede
     * Zusammenhangskomponente eine Strategie gewählt wird.
     *
     * @param anzahl
     * @return
     */
    @Override
    public Population<PermutationModeIndividualSorted> generatePopulation(int anzahl, Object... additionalObjects) {
        ArrayList<PermutationModeIndividualSorted> res = new ArrayList<>();
        List<JoNComponent<LoadUnitJob>> connectionComponents = new ArrayList<>(problem.getJobOnNodeDiagramm().getConnectionComponents());
        int numberOfConnectionComponents = connectionComponents.size();
        
        for (int i = 0; i < anzahl; i++) {
            List<SubListIndividual<LoadUnitJob>> list = new ArrayList<>();
            for (int j = 0; j < numberOfConnectionComponents; j++) {
                JoNComponent<LoadUnitJob> component = connectionComponents.get(j);
                List<LoadUnitJob> jobs = new ArrayList<>(component.vertexSet());
                Collections.shuffle(jobs, Parameters.getRandom());
                SubListIndividual<LoadUnitJob> subIndividual = new SubListIndividual(jobs);
                list.add(subIndividual);
            }
            List<Integer> order = new ArrayList<>();
            for (int j = 0; j < numberOfRoutes; j++) {
                order.add(j);
            }
            Collections.shuffle(order, Parameters.getRandom());
            PermutationModeIndividualSorted ind = new PermutationModeIndividualSorted(list, new IntegerIndividual(order));
            res.add(ind);
        }
        Population<PermutationModeIndividualSorted> pop = new Population<>(PermutationModeIndividualSorted.class, res);
        
        return pop;
    }
    
}
