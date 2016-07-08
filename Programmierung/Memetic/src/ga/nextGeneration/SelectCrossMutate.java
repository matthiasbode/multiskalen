package ga.nextGeneration;

import ga.acceptance.AcceptanceMechanism;
import ga.crossover.Crossover;
import ga.individuals.Individual;
import ga.Parameters;
import ga.algorithms.GAAlgorithm;
import ga.mutation.Mutation;
import ga.selection.Selection;
import ga.basics.Population;
import java.util.Collection;
import java.util.logging.Level;

/**
 *
 * @author hoecker
 */
public class SelectCrossMutate<I extends Individual> extends NextGenerationAlgorithm<I> {

    public SelectCrossMutate(double xOverRate, double xMutaRate, Selection parentSelectAlg, Mutation mutationAlgorithm, Crossover crossoverAlgorithm) {
        super( xOverRate, xMutaRate, parentSelectAlg, mutationAlgorithm, crossoverAlgorithm);

    }

    @Override
    public Population<I> computeNextGeneration(Population<I> oldPopulation) {
        int size = oldPopulation.size();
        if (size == 1) {
            final Population<I> newPopulation = new Population(oldPopulation.getIndividualType(), oldPopulation.numberOfGenerations + 1);
            newPopulation.addAll(oldPopulation.individuals());
            return newPopulation;
        }
        Population<I> newPopulation = new Population(oldPopulation.getIndividualType(), oldPopulation.numberOfGenerations + 1);


        /*
         * Erzeuge genausoviele Individuen wie in der alten Population
         */
        while (newPopulation.size() < size) {
            Parameters.logger.log(Level.FINER, "NextGeneration:" + newPopulation.size());
            // Selektion
            I selection1 = selection.selectFromPopulation(oldPopulation);
            I selection2 = selection.selectFromPopulation(oldPopulation);

            //Rekombination
            Collection<? extends I> recombine = crossover.recombine(selection1, selection2, xOverRate);
            for (I c : recombine) {
                //Mutation
                I next = mutation.mutate(c, super.xMutaRate);
                newPopulation.add(next);
            }
        }
        return newPopulation;
    }
}
