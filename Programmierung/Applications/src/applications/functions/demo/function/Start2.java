/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions.demo.function;

import applications.functions.function3d.PlotListener;
import ga.acceptance.Elitismus;
import ga.algorithms.SimpleGA;
import ga.individuals.Individual;
import ga.basics.Population;
import ga.individuals.DoubleIndividual;
import ga.crossover.ContiniousCrossover;
import ga.localSearch.ContinuousSpaceHillClimbing;
import ga.mutation.ContiniousMutationMemetic;
import ga.nextGeneration.memetic.StandardMemetic;
import ga.selection.RankingSelection;

import java.util.ArrayList;

/**
 *
 * @author bode
 */
public class Start2 {

    public static void main(String[] args) {
        int NUMINDIVIDUUMS = 5;
        int GENERATIONS = 12;

        FunctionFitness2 fit = new FunctionFitness2();

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

        Population<DoubleIndividual> pop = new Population<>(DoubleIndividual.class, initial);

        RankingSelection<DoubleIndividual> selection = new RankingSelection<>();
        ContiniousMutationMemetic swap = new ContiniousMutationMemetic();
        ContiniousCrossover cross = new ContiniousCrossover();
        StandardMemetic<DoubleIndividual> nextGenAlg = new StandardMemetic<>(0.9, 0.8, selection, swap, cross, new ContinuousSpaceHillClimbing(fit));
        PlotListener<DoubleIndividual> listener = new PlotListener<>();
        nextGenAlg.addGAListener(listener);
        SimpleGA sga = new SimpleGA(pop, fit, nextGenAlg, new Elitismus(0.1), GENERATIONS);
        sga.fitnessLim = 6.0;
        sga.run();
    }
}
