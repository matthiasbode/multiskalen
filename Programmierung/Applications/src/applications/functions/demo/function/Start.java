/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions.demo.function;

import ga.algorithms.SimpleGA;
import ga.basics.Population;
import ga.individuals.DoubleIndividual;
import ga.mutation.SwapMutation;
import ga.crossover.UniformCrossOver;
import applications.functions.function3d.PlotListener;
import ga.Parameters;
import ga.acceptance.Elitismus;
import ga.nextGeneration.memetic.StandardMemetic;
import ga.selection.FitnessProportionalSelection;

import java.util.ArrayList;
import ga.localSearch.ContinuousSpaceHillClimbing;
import java.lang.reflect.Parameter;
import java.util.logging.Level;

/**
 *
 * Beispiel, in dem Lösungen in einem 3D-Raum eingezeichnet werden.
 *
 * @author bode
 */
public class Start {

    public static void main(String[] args) {
        int NUMINDIVIDUUMS = 10;
        int GENERATIONS = 4;

        FunctionFitness fit = new FunctionFitness();

        ArrayList<DoubleIndividual> initial = new ArrayList<>();

        System.out.println("Startpopulation");
        for (int i = 0; i < NUMINDIVIDUUMS; i++) {
            DoubleIndividual start = DoubleIndividual.createIndividual(2, -5, 5);
            start.setFitness(fit.computeFitness(start));
            System.out.println(start);
//            if(new Point2d(start.chrom.valList[0],start.chrom.valList[1]).distance(new Point2d(3.12,2.16)) < 2){
//                i--;
//                continue;
//            }
            initial.add(start);
        }

        Population<DoubleIndividual> pop = new Population<DoubleIndividual>(DoubleIndividual.class, initial);

        Parameters.initializeLogger(Level.FINE);
        FitnessProportionalSelection selection = new FitnessProportionalSelection();
        SwapMutation<DoubleIndividual> swap = new SwapMutation<DoubleIndividual>();
        UniformCrossOver<DoubleIndividual> cross = new UniformCrossOver<DoubleIndividual>();
        ContinuousSpaceHillClimbing local = new ContinuousSpaceHillClimbing(fit);
        DoubleIndividual localOptima = local.localSearch(initial.get(0));
        System.out.println("localOptima; " +localOptima);
        StandardMemetic nextGenAlg = new StandardMemetic(0.5, 0.9, selection, swap, cross,local );
        PlotListener<DoubleIndividual> listener = new PlotListener<DoubleIndividual>();
        nextGenAlg.addGAListener(listener);
        SimpleGA sga = new SimpleGA(pop, fit, nextGenAlg, new Elitismus(0.1), GENERATIONS);
        sga.fitnessLim = 2.5;
        sga.run();
        System.out.println("Ende");
    }
}
