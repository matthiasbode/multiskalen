package ga.algorithms;

import ga.Parameters;
import ga.individuals.Individual;
import com.google.common.collect.Iterables;
import ga.acceptance.AcceptanceMechanism;
import ga.basics.*;
import ga.nextGeneration.NextGenerationAlgorithm;
import ga.localSearch.LocalSearch;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class SimpleMA<I extends Individual> extends SimpleGA<I> {

    public LocalSearch<I> localSearch;

    public SimpleMA(Population<I> pop, FitnessEvalationFunction<I> env, NextGenerationAlgorithm<I> nextGenAlg, AcceptanceMechanism<I> acceptanceMechanism, LocalSearch<I> localSearch, int numGenerations) {
        super(pop, env, nextGenAlg, acceptanceMechanism, numGenerations);
        this.localSearch = localSearch;
    }

    @Override
    public void run() {
        initialLocalSearch();
        super.run();
        
    }

    private void initialLocalSearch() {
        Parameters.logger.finer("Lokale Startsuche");
        Population<I> localedPop = new Population<>(this.currentPopulation.getIndividualType(), 0);

        /**
         * #####################################################################
         * Parallelisierte Lokale Suche beim Start
         * #####################################################################
         */
        ArrayList<Future<List<I>>> evaluationThreads = new ArrayList<>();
        
        
        Iterable<List<I>> partition = Iterables.partition(currentPopulation.individuals(), Math.max(1, currentPopulation.individuals().size() / Parameters.NUMBER_OF_THREADS));
        for (List<I> list : partition) {
            InitialLocalSearch localSearch = new InitialLocalSearch(list);
            evaluationThreads.add(Parameters.getThreadPool().submit(localSearch));
        }

        for (Future<List<I>> future : evaluationThreads) {
            try {
                List<I> localPartition = future.get();
                localedPop.addAll(localPartition);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleMA.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(SimpleMA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.currentPopulation = localedPop;
        Parameters.logger.finer("Lokale Startsuche beendet");
    }

    class InitialLocalSearch implements Callable<List<I>> {

        private final List<I> partialPop;

        public InitialLocalSearch(List<I> p) {
            this.partialPop = p;
        }

        @Override
        public List<I> call() throws Exception {
            ArrayList<I> localOptima = new ArrayList<>();
            for (I i : partialPop) {
                I localOptimimumIndividual = localSearch.localSearch(i);
                localOptima.add(localOptimimumIndividual);
            }
            return localOptima;
        }
    }
}
