/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals.modes;

import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import ga.Parameters;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class ModeStartPopulationGenerator implements StartPopulationGenerator<ImplicitModeIndividual> {

    MultiJobTerminalProblem problem;

    public ModeStartPopulationGenerator(MultiJobTerminalProblem problem) {
        this.problem = problem;
    }

    /**
     * Erzeugt eine Startpopulation indem zufällig für jede
     * Zusammenhangskomponente eine Strategie gewählt wird.
     *
     * @param anzahl
     * @param additionalObjects
     * @return
     */
    @Override
    public Population<ImplicitModeIndividual> generatePopulation(int anzahl, Object... additionalObjects) {
        ArrayList<ImplicitModeIndividual> res = new ArrayList<>();
        int numberOfConnectionComponents = problem.getJobOnNodeDiagramm().getConnectionComponents().size();
        List<LoadUnitJobPriorityRules.Identifier> auswahl = Arrays.asList(LoadUnitJobPriorityRules.Identifier.values());

        for (int i = 0; i < anzahl; i++) {
            ArrayList<LoadUnitJobPriorityRules.Identifier> currentRules = new ArrayList<>();
            for (int j = 0; j < numberOfConnectionComponents; j++) {
                currentRules.add(auswahl.get(RandomUtilities.getRandomValue(Parameters.getRandom(), 0, auswahl.size() - 1)));
            }
            ImplicitModeIndividual ind = new ImplicitModeIndividual(currentRules);
            res.add(ind);
        }
        Population<ImplicitModeIndividual> pop = new Population<>(ImplicitModeIndividual.class, res);
        return pop;
    }

   

}
