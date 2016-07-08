/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicitModeDirectOps.individuals;

import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.routeDetermination.ImplicitRouteChooser;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import ga.individuals.subList.SubListIndividual;
import ga.individuals.subList.ListIndividual;
import ga.Parameters;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class AdaptedVertexClassStartPopulationGenerator implements StartPopulationGenerator<ListIndividual<RoutingTransportOperation>> {

    private MultiJobTerminalProblem problem;

    public AdaptedVertexClassStartPopulationGenerator(MultiJobTerminalProblem problem) {
        this.problem = problem;
    }

    @Override
    public Population<ListIndividual<RoutingTransportOperation>> generatePopulation(int anzahl, Object... additionalObjects) {

        int numberOfConnectionComponents = problem.getJobOnNodeDiagramm().getConnectionComponents().size();
        List<LoadUnitJobPriorityRules.Identifier> auswahl = Arrays.asList(LoadUnitJobPriorityRules.Identifier.values());

        ArrayList<LoadUnitJobPriorityRules.Identifier> currentRules = new ArrayList<>();
        for (int j = 0; j < numberOfConnectionComponents; j++) {
            currentRules.add(auswahl.get(RandomUtilities.getRandomValue(Parameters.getRandom(), 0, auswahl.size() - 1)));
        }
        ImplicitModeIndividual Mind = new ImplicitModeIndividual(currentRules);
        ImplicitRouteChooser chooser = new ImplicitRouteChooser(problem, Mind.getChromosome());

//        problem.setActivityOnNodeDiagramm(chooser.getActivityOnNodeDiagramm());
        
        /**
         * Bestimme ein Acitivity on Node Diagramm für die Individuen zur Erzeugung
         */
        List<Set<RoutingTransportOperation>> topologicalSort = chooser.getActivityOnNodeDiagramm().getNodeClasses();
        System.out.println("Anzahl an Operationen: " +chooser.getActivityOnNodeDiagramm().numberOfVertices());

        /**
         * ####################################################################
         * Erzeugen von OperationenIndividuen.
         * ####################################################################
         */
        Population<ListIndividual<RoutingTransportOperation>> operationPop = new Population<>(ListIndividual.class, 0);
        for (int i = 0; i < anzahl; i++) {
            /**
             * Zunächst werden alle Operationen bestimmt, die keinen Vorgänger
             * haben, diese werden zu den activeOperations hinzugefügt.
             */
            ListIndividual<RoutingTransportOperation> ind = new ListIndividual<RoutingTransportOperation>();
            for (int knotenklasse = 0; knotenklasse < topologicalSort.size(); knotenklasse++) {
                ArrayList<RoutingTransportOperation> classList = new ArrayList<>(topologicalSort.get(knotenklasse));
                Collections.shuffle(classList, Parameters.getRandom());
                SubListIndividual<RoutingTransportOperation> oList = new SubListIndividual<RoutingTransportOperation>(classList);
                ind.set(knotenklasse, oList);
            }
            operationPop.add(ind);
        }

        return operationPop;
    }

 

}
