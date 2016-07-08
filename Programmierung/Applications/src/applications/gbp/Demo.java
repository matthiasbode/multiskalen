/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp;

import applications.gbp.ma.*;
import ga.acceptance.Elitismus;
import ga.algorithms.SimpleMA;
import ga.Parameters;
import ga.basics.Population;
import ga.crossover.Crossover;
import ga.fittnessLandscapeAnalysis.FDC;
import applications.FitnessLandscape3DBuilder;
import ga.fittnessLandscapeAnalysis.LocalOptimaCollection;
import ga.fittnessLandscapeAnalysis.fdplots.FitnessDistancePlotter;
import ga.localSearch.LocalSearch;
import ga.mutation.Mutation;
import ga.nextGeneration.memetic.StandardMemetic;
import ga.selection.RankingSelection;
import ga.selection.Selection;
import java.util.ArrayList;
import java.util.logging.Level;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class Demo {

    public static void main(String[] args) {
        Parameters.initializeLogger(Level.INFO);
        /**
         * Graph erzeugen
         */
        int numberOfNodes = 70;
        double densityMean = 3.0;
        double densityDeviation = 2.0;

        WeightedDirectedGraph<Integer, Double> graph = Builder.buildRandomGraph(numberOfNodes, densityMean, densityDeviation);
//        ExportToYED.exportToGraphML(graph, "/home/bode/Desktop/graph.graphml", true);
        ArrayList<Integer> nodes = new ArrayList<Integer>(graph.vertexSet());

        GBPFitness env = new GBPFitness(graph, nodes);

        int NUMINDIVIDUUMS = 20;
        int GENERATIONS = 4;

        Selection<BiPartitionIndividual> selection = new RankingSelection<BiPartitionIndividual>();
        Mutation<BiPartitionIndividual> mutation = new BPMutation();
        Crossover<BiPartitionIndividual> crossover = new HUXRecombination();
        LocalSearch<BiPartitionIndividual> localSearch = new KernighanLinLocalSearch<Integer>(graph, nodes);

//        SelectCrossMutate<BiPartitionChromosome,Integer> nextGenAlg = new SelectCrossMutate<BiPartitionChromosome>(0.3, 0.3, 0.93, 0.9, selection, mutation, crossover);
        StandardMemetic<BiPartitionIndividual> nextGenAlg
                = new StandardMemetic<BiPartitionIndividual>(0.97, 0.97, selection, mutation, crossover, localSearch);

        GBPHammingMetric metric = new GBPHammingMetric();
        GBPRangeFunction range = new GBPRangeFunction(graph, nodes);

        /**
         * Listener
         */
        LocalOptimaCollection localOptimaCollection = new LocalOptimaCollection();
        nextGenAlg.addGAListener(localOptimaCollection);

//        NeighboringCostsVisualizer ncv = new NeighboringCostsVisualizer(graph,env);
//        nextGenAlg.addGAListener(ncv);
//        SOMListener somL = new SOMListener();
//        nextGenAlg.addGAListener(somL);
        FitnessLandscape3DBuilder builder = new FitnessLandscape3DBuilder(metric, env);
        nextGenAlg.addGAListener(builder);

        Population<BiPartitionIndividual> pop = new Population<BiPartitionIndividual>(BiPartitionIndividual.class, 0);

        InitialSolutionDeterminator.initializePopulation(pop, NUMINDIVIDUUMS, graph.numberOfVertices(), metric);

        SimpleMA<BiPartitionIndividual> sga = new SimpleMA<BiPartitionIndividual>(pop, env, nextGenAlg, new Elitismus(0.1), localSearch, GENERATIONS);
        FDC<BiPartitionIndividual> fdc = new FDC<BiPartitionIndividual>(localOptimaCollection, metric);
        sga.addGAListener(fdc);
        FitnessDistancePlotter<BiPartitionIndividual> plotter = new FitnessDistancePlotter(localOptimaCollection, metric, range);
        plotter.initGraph("Distance to optimum", "Cut size difference");
        sga.addGAListener(plotter);
        sga.fitnessLim = 0.;
//        tick = System.currentTimeMillis();
        sga.run();
//        tock = System.currentTimeMillis();

        Parameters.logger.info("########################");
        Parameters.logger.info("GA");
        Parameters.logger.info("Fittest Individual: " + sga.getPopulation().getFittestIndividual());
        Parameters.logger.info("fit: " + sga.getPopulation().getFittestIndividual().getFitness());
//        Parameters.logger.info("Duration: " +(tock-tick));
//        Parameters.logger.info("########################");

    }
}
