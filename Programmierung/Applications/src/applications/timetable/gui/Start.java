/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.dame.MinFunction;
import applications.timetable.xml.XMLImport;
import ga.algorithms.SimpleGA;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import ga.basics.Population;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.nextGeneration.SelectCrossMutate;
import ga.selection.RankingSelection;
import applications.timetable.model.Initialization;
import applications.timetable.model.ProblemDefinition;
import applications.timetable.model.TimeTableMatrix;
import applications.timetable.model.TimeTableMatrixCrossover;
import applications.timetable.model.TimeTableMatrixMutation;
import ga.acceptance.Elitismus;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author bode
 */
public class Start {

    public static File file = new File("/home/bode/TimeTableProject/problemDefinition.xml");

    public static void main(String[] args) {

        ProblemDefinition problemDefintion = XMLImport.<ProblemDefinition>importXML(file, ProblemDefinition.class, false);

        int NUMINDIVIDUUMS = 20;
        int GENERATIONS = 100;
        Population<TimeTableMatrix> pop = new Population<>(TimeTableMatrix.class, 0);
        ArrayList<TimeTableMatrix> buildInitialPopulation = Initialization.buildInitialPopulation(problemDefintion, NUMINDIVIDUUMS);
        for (TimeTableMatrix individual : buildInitialPopulation) {
            pop.add(individual);
        }

        NextGenerationAlgorithm<TimeTableMatrix> nextGenAlg = new SelectCrossMutate<>(0.4, 0.2, new RankingSelection<TimeTableMatrix>(), new TimeTableMatrixMutation(2, 6), new TimeTableMatrixCrossover());

        FitnessEvalationFunction env = new MinFunction();
        SimpleGA sga = new SimpleGA(pop, env, nextGenAlg, new Elitismus(0.1), GENERATIONS);
        sga.run();
    }
}
