package applications.dame;

import ga.acceptance.Elitismus;
import ga.basics.FitnessEvalationFunction;
import ga.basics.Population;
import ga.algorithms.SimpleGA;
import ga.metric.EuklidMetricInteger;
import ga.crossover.UniformCrossOver;
import ga.individuals.IntegerIndividual;
import ga.mutation.SwapMutation;
import ga.nextGeneration.memetic.StandardMemetic;
import ga.selection.RankingSelection;
import ga.localSearch.LocalSearchCombinatoricInteger;

public class NDamenProblem implements FitnessEvalationFunction<IntegerIndividual> {

    @Override
    public double[] computeFitness(IntegerIndividual ind) {
        IntegerIndividual c = ind;
        int n = c.size();
        int anzahlkollisionen = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    if ((c.get(j) == c.get(i)) || (Math.abs(c.get(i) - c.get(j)) == Math.abs(i - j))) {
                        anzahlkollisionen++;
                    }
                }
            }
        }
        anzahlkollisionen /= 2;
        return new double[]{-anzahlkollisionen};
    }

    public static void main(String[] args) {

        FitnessEvalationFunction env = new NDamenProblem();
        int N = 8;
        int NUMINDIVIDUUMS = 8;
        int GENERATIONS = 30;
        StandardMemetic<IntegerIndividual> nextGenAlg = new StandardMemetic<IntegerIndividual>(0.93, 0.9, new RankingSelection<IntegerIndividual>(), new SwapMutation<IntegerIndividual>(), new UniformCrossOver<IntegerIndividual>(), new LocalSearchCombinatoricInteger(env, 0, 7));
        Population<IntegerIndividual> pop = new Population<>(IntegerIndividual.class, 0);
        for (int i = 0; i < NUMINDIVIDUUMS; i++) {
            pop.add(new Dame(N));
        }

        SimpleGA sga = new SimpleGA(pop, env, nextGenAlg, new Elitismus(0.1), GENERATIONS);

//        IntegerMetric m = new IntegerMetric();
        EuklidMetricInteger m = new EuklidMetricInteger();
        //        FitnessLandscapeEvaluation lis = new FitnessLandscapeEvaluation(m);
        //        nextGenAlg.addGAListener(lis);

//                BuilderListener fbuilder = new BuilderListener(m);
//                nextGenAlg.addGAListener(fbuilder);
//        File file = new File("/home/bode/MemeticGraphs/FitnessLandscape.graphml");
//        File file = new File("Z:\\Eigene Dateien\\Documents\\Bauinformatik\\Memetic\\Presi Cork\\src\\FitnessLandscape.graphml");
//        YEDIndividualListener l = new YEDIndividualListener(file);
//        nextGenAlg.addGAListener(l);

        sga.fitnessLim = 0.;
        sga.run();
        System.out.println(sga.getPopulation().getFittestIndividual() + ":" + sga.getPopulation().getFittestIndividual().getFitness());
    }
}
