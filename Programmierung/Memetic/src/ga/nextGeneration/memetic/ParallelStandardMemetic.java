package ga.nextGeneration.memetic;

import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.crossover.Crossover;
import ga.individuals.Individual;
import ga.Parameters;
import ga.algorithms.GAAlgorithm;
import ga.mutation.Mutation;
import ga.selection.Selection;
import ga.basics.Population;
import ga.localSearch.LocalSearch;
import ga.nextGeneration.NextGenerationAlgorithm;
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
public class ParallelStandardMemetic<I extends Individual> extends NextGenerationAlgorithm<I> {

    private LocalSearch<I> local;

    public ParallelStandardMemetic(double xOverRate, double xMutaRate, Selection parentSelectAlg, Mutation mutationAlgorithm, Crossover crossoverAlgorithm, LocalSearch<I> local) {
        super(xOverRate,xMutaRate, parentSelectAlg, mutationAlgorithm, crossoverAlgorithm);
        this.local = local;
    }

    @Override
    public Population<I> computeNextGeneration(final Population<I> oldPopulation) {
        final int size = oldPopulation.size();

        final Population<I> newPopulation = new Population(oldPopulation.getIndividualType(), oldPopulation.numberOfGenerations + 1);
        ArrayList<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < Parameters.NUMBER_OF_RECOMBINATION_THREADS; i++) {
            Runnable creator = new Runnable() {
                Selection<I> sel = ParallelStandardMemetic.this.selection.clone();

                @Override
                public void run() {
                    while (newPopulation.size()< size) {
                        // Selektion
                        I selection1 = sel.selectFromPopulation(oldPopulation);
                        I selection2 = sel.selectFromPopulation(oldPopulation);
                        if (selection1 == null || selection2 == null) {
                            throw new NoSuchElementException("Auswahl von Individuen nicht erfolgreich.");
                        }

                        //Rekombination
                        Collection<? extends I> offsprings = crossover.recombine(selection1, selection2, xOverRate);
                        Collection<I> localOptima = new ArrayList<>();
                        for (I child : offsprings) {
                            localOptima.add(local.localSearch(child));
                        }
                        for (I c : localOptima) {
                            //Mutation
                            I next = mutation.mutate(c, xMutaRate);
                            if (!next.equals(c)) {
                                next = local.localSearch(next);
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
