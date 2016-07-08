package ga.nextGeneration;

import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.crossover.Crossover;
import ga.individuals.Individual;
import ga.Parameters;
import ga.algorithms.GAAlgorithm;
import ga.mutation.Mutation;
import ga.selection.Selection;
import ga.basics.Population;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hoecker
 */
public class SelectCrossMutateParallel<I extends Individual> extends NextGenerationAlgorithm<I> {

    public SelectCrossMutateParallel(double xOverRate, double xMutaRate, Selection parentSelectAlg, Mutation mutationAlgorithm, Crossover crossoverAlgorithm) {
        super(xOverRate, xMutaRate, parentSelectAlg, mutationAlgorithm, crossoverAlgorithm);

    }

    @Override
    public Population<I> computeNextGeneration(final Population<I> oldPopulation) {
        final int size = oldPopulation.size();
        if (size == 1) {
            final Population<I> newPopulation = new Population(oldPopulation.getIndividualType(), oldPopulation.numberOfGenerations + 1);
            newPopulation.addAll(oldPopulation.individuals());
            return newPopulation;
        }
        final Population<I> newPopulation = new Population(oldPopulation.getIndividualType(), oldPopulation.numberOfGenerations + 1);
        ArrayList<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < Parameters.NUMBER_OF_RECOMBINATION_THREADS; i++) {
            Runnable creator = new Runnable() {
                Selection<I> sel = SelectCrossMutateParallel.this.selection.clone();

                @Override
                public void run() {
                    while (newPopulation.size() < size) {
                        // Selektion
                        I selection1 = sel.selectFromPopulation(oldPopulation);
                        I selection2 = sel.selectFromPopulation(oldPopulation);
                        if (selection1 == null || selection2 == null) {
                            throw new NoSuchElementException("Auswahl von Individuen nicht erfolgreich.");
                        }

                        //Rekombination
                        Collection<? extends I> offsprings = crossover.recombine(selection1, selection2, xOverRate);
                        for (I c : offsprings) {
                            if (c == null) {
                                throw new NullPointerException("NullPointer bei Recombination");
                            }
                            //Mutation
                            I next = mutation.mutate(c, xMutaRate);
                            Boolean b = !next.equals(c);
                            next.additionalObjects.put(Mutation.mutated, b);
                            if (next == null) {
                                throw new NullPointerException("NullPointer bei Mutation");
                            }
                            newPopulation.add(next);
                        }
                    }

                }
            };
            Future<?> submit = Parameters.getThreadPool().submit(creator);
            futures.add(submit);
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ex) {
                ex.getCause().printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(MultipleSpeciesCoevolvingParallelGA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return newPopulation;
    }
}
