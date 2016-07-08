/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving.memetic;

import com.google.common.collect.Iterables;
import ga.acceptance.AcceptanceMechanism;
import ga.algorithms.SimpleMA;
import ga.algorithms.coevolving.GABundle;
import ga.algorithms.coevolving.MultipleSpeciesCoevolvingParallelGA;
import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import ga.basics.FitnessEvalationFunction;
import ga.Parameters;
import ga.basics.Population;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.individuals.Individual;
import ga.localSearch.LocalSearch;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class MemeticMultipleSpeciesCoevolvingParallel<I extends SuperIndividual> extends MultipleSpeciesCoevolvingParallelGA<I> {

    public LocalSearch<I> localSearch;

    public MemeticMultipleSpeciesCoevolvingParallel(Class<I> superType, LinkedHashMap<Population<? extends Individual>, GABundle> species, FitnessEvalationFunction<I> fitnessFunction, MultipleSpeciesIndividualGenerator superIndividualGenerator, MultipleSpeciesIndividualGenerator<I> startSuperIndividualGenerator, LocalSearch<I> localSearch,  AcceptanceMechanism<I> acceptanceMechanism, int numGenerations) {
        super(superType, species, fitnessFunction, superIndividualGenerator, startSuperIndividualGenerator, acceptanceMechanism, numGenerations);
        this.localSearch = localSearch;
    }

    @Override
    protected void initPopulations() {
        super.initPopulations();
        initialLocalSearch();
    }

    @Override
    protected void initNextGenRunnables() {
        for (Population population : species.keySet()) {
            MemeticSpeciesNextGenRunnable subPopRunnable = new MemeticSpeciesNextGenRunnable(this,population, localSearch);
            subPopulationThreads.add(this.subPopulationPool.submit(subPopRunnable));
        }
    }

    private void initialLocalSearch() {
        Parameters.logger.finer("Lokale Startsuche");
        Population<I> localedPop = new Population<>(this.getPopulation().getIndividualType(), 0);

        /**
         * #####################################################################
         * Parallelisierte Lokale Suche beim Start
         * #####################################################################
         */
        ArrayList<Future<List<I>>> evaluationThreads = new ArrayList<>();
        
        Iterable<List<I>> partition = Iterables.partition(this.getPopulation().individuals(), Math.max(1, this.getPopulation().individuals().size() / Parameters.NUMBER_OF_THREADS));
        for (List<I> list : partition) {
            InitialLocalSearch initSearch = new InitialLocalSearch(list);
            evaluationThreads.add(Parameters.getThreadPool().submit(initSearch));
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

        this.setSuperPopulation(localedPop);
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
