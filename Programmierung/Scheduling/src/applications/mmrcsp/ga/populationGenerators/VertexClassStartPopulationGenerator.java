/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.ga.populationGenerators;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;

import ga.individuals.subList.SubListIndividual;
import ga.individuals.subList.ListIndividual;
import ga.Parameters;
import ga.basics.Population;
import ga.basics.StartPopulationGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bode
 */
public class VertexClassStartPopulationGenerator<E extends Operation> implements StartPopulationGenerator<ListIndividual<E>> {

    private SchedulingProblem<E> problem;
    private Collection<E> operationsToSchedule = new HashSet<>();

    public VertexClassStartPopulationGenerator(SchedulingProblem<E> problem) {
        this.problem = problem;
        this.operationsToSchedule = problem.getActivityOnNodeDiagramm().vertexSet();
    }

    public VertexClassStartPopulationGenerator(SchedulingProblem subProblem, Collection<E> operationsToSchedule) {
        this.problem = subProblem;
        this.operationsToSchedule = operationsToSchedule;
    }

    @Override
    public Population<ListIndividual<E>> generatePopulation(int anzahl, Object... additionalObjects) {
        List<Set<E>> topologicalSort = problem.getActivityOnNodeDiagramm().getNodeClasses();

        /**
         * ####################################################################
         * Erzeugen von OperationenIndividuen.
         * ####################################################################
         */
        Population<ListIndividual<E>> operationPop = new Population<>(ListIndividual.class, 0);
        for (int i = 0; i < anzahl; i++) {
            /**
             * Zunächst werden alle Operationen bestimmt, die keinen Vorgänger
             * haben, diese werden zu den activeOperations hinzugefügt.
             */
            ListIndividual<E> ind = new ListIndividual<E>();
            for (int knotenklasse = 0; knotenklasse < topologicalSort.size(); knotenklasse++) {
                ArrayList<E> classList = new ArrayList<>(topologicalSort.get(knotenklasse));
                classList.retainAll(operationsToSchedule);
                if(classList.isEmpty()){
                    break;
                }
                Collections.shuffle(classList, Parameters.getRandom());
                SubListIndividual<E> oList = new SubListIndividual<E>(classList);
                ind.set(knotenklasse, oList);
            }
            operationPop.add(ind);
        }

        return operationPop;
    }

}
