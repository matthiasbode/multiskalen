/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals.ops;

import applications.mmrcsp.model.modes.JobOperationList;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.TerminalProblem;
import static applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules.lengthOfInterval;
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
public class OperationStartPopulationGenerator implements StartPopulationGenerator<ImplicitOperationIndividual> {


    TerminalProblem problem;
    boolean timeOriented;

    public OperationStartPopulationGenerator(TerminalProblem problem) {
        this(problem, true);
    }

    public OperationStartPopulationGenerator(TerminalProblem problem, boolean timeOriented) {
        this.problem = problem;
        this.timeOriented = timeOriented;
    }

    @Override
    public Population<ImplicitOperationIndividual> generatePopulation(int anzahl, Object... additionalObjects) {
        ArrayList<ImplicitOperationIndividual> res = new ArrayList<>();
        List<OperationPriorityRules.Identifier> auswahl = new ArrayList<>(Arrays.asList(OperationPriorityRules.Identifier.values()));
        auswahl.remove(OperationPriorityRules.Identifier.SETUP);
        /**
         * Problem: Unterschiedlich lang!!!
         */

        int numberOfGens = 0;
        if (timeOriented) {
            numberOfGens = (int) (problem.getOptimizationTimeSlot().getDuration().longValue() / lengthOfInterval);
        } else {
            for (LoadUnitJob loadUnitJob : problem.getJobs()) {
                int max = 0;
                for (JobOperationList<RoutingTransportOperation> jobOperationList : loadUnitJob.getRoutings()) {
                    max = Math.max(max, jobOperationList.size());
                }
                numberOfGens += max;
            }
        }


        for (int i = 0; i < anzahl; i++) {
            ArrayList<OperationPriorityRules.Identifier> currentRules = new ArrayList<>();
            for (int j = 0; j < numberOfGens; j++) {
                currentRules.add(auswahl.get(RandomUtilities.getRandomValue(Parameters.getRandom(), 0, auswahl.size() - 1)));
//                currentRules.add(OperationPriorityRules.Identifier.LST);
            }
            ImplicitOperationIndividual ind = new ImplicitOperationIndividual(currentRules);
            res.add(ind);
        }
        Population<ImplicitOperationIndividual> pop = new Population<>(ImplicitOperationIndividual.class, res);
        return pop;
    }

}
