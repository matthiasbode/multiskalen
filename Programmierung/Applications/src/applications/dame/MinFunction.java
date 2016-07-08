package applications.dame;

import ga.acceptance.Elitismus;
import ga.basics.FitnessEvalationFunction;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.basics.Population;
import ga.algorithms.SimpleGA;
import ga.individuals.DoubleIndividual;
import ga.crossover.UniformCrossOver;
import ga.nextGeneration.SelectCrossMutate;
import ga.mutation.SwapMutation;
import ga.selection.RankingSelection;


public class MinFunction implements FitnessEvalationFunction<DoubleIndividual> {

    @Override
    public double[] computeFitness(DoubleIndividual i) {
        double x = i.getValueAt(0);
        return new double[] {-(Math.pow(2 - Math.exp(-(x - 1) * (x - 1)) - 2 * Math.exp(-(x - 3) * (x - 3)),2))} ;
    }

    public static void main(String[] args) {
        int NUMINDIVIDUUMS = 20;
        int GENERATIONS = 100;
        Population<DoubleIndividual> pop = new Population(Dame.class, 0);
        NextGenerationAlgorithm<DoubleIndividual> nextGenAlg = new SelectCrossMutate(0.4, 0.2, new RankingSelection(), new SwapMutation(), new UniformCrossOver());

        for (int i = 0; i < NUMINDIVIDUUMS; i++) {
            pop.add(DoubleIndividual.createIndividual(1, -10., 10.));
        }
        FitnessEvalationFunction env = new MinFunction();
        SimpleGA sga = new SimpleGA(pop, env, nextGenAlg, new Elitismus(0.1), GENERATIONS);
        sga.run();
    }
}
