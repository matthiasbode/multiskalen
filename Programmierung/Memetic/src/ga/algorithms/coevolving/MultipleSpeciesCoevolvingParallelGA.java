/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.algorithms.coevolving;

import ga.algorithms.coevolving.individuals.MultipleSpeciesIndividualGenerator;
import com.google.common.collect.Iterables;
import ga.acceptance.AcceptanceMechanism;
import static ga.algorithms.coevolving.MultipleSpeciesCoevolvingGA.superIND;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import ga.Parameters;
import static ga.Parameters.maxStagnation;
import ga.basics.Population;
import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.listeners.GAEvent;
import ga.listeners.IndividualEvent;
import ga.listeners.IndividualListener;
import ga.listeners.coevolutionary.CoEvoGAEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 * @param <I>
 */
public class MultipleSpeciesCoevolvingParallelGA<I extends SuperIndividual> extends CoevolvingGA<I> {

    /**
     * Die Populationen der Subspezien und der zugehörige genetische Algorithmus
     */
    protected LinkedHashMap<Population<? extends Individual>, GABundle> species;
    public AcceptanceMechanism<I> acceptanceMechanism;
    /**
     * Evaluierungsfunktion für das übergeordnete Individuum, welches als
     * Subindividuen je eins aus den species enthält.
     */
    private FitnessEvalationFunction<I> fitnessFunction;
    private Population<I> superPopulation;
    private Population<I> oldSuperPopulation;

    private int maxGenerations;
    public double fitnessMax = Double.NEGATIVE_INFINITY;
    public double fitnessLim = Double.POSITIVE_INFINITY;
    /**
     * Vorgängerpopulationen und Vorschriften für die Fitnessbestimmung.
     */
    private MultipleSpeciesIndividualGenerator<I> superIndividualGenerator;
    private MultipleSpeciesIndividualGenerator<I> startSuperIndividualGenerator;
    private HashMap<Class, Population> previousPopulations = new HashMap<>();
    protected ExecutorService subPopulationPool;
    protected ArrayList<Future<?>> subPopulationThreads;

    private final Class<I> superType;

    private double[] oldbestFitness;
    private int stagnation = 0;

    public MultipleSpeciesCoevolvingParallelGA(Class<I> superType, LinkedHashMap<Population<? extends Individual>, GABundle> species, FitnessEvalationFunction<I> fitnessFunction, MultipleSpeciesIndividualGenerator superIndividualGenerator, MultipleSpeciesIndividualGenerator<I> startSuperIndividualGenerator, AcceptanceMechanism<I> acceptanceMechanism, int numGenerations) {
        this.species = species;
        this.fitnessFunction = fitnessFunction;
        this.maxGenerations = numGenerations;
        this.superIndividualGenerator = superIndividualGenerator;
        this.startSuperIndividualGenerator = startSuperIndividualGenerator;
        this.subPopulationPool = Executors.newFixedThreadPool(species.size());
        this.acceptanceMechanism = acceptanceMechanism;
        this.superType = superType;
    }

    /**
     * Starten der Berechnung.
     */
    public void run() {

        /**
         * Startfitnessberechnung.
         */
        initPopulations();
        subPopulationThreads = new ArrayList<>();

        /**
         * Schleife für die Generationen.
         */
        while (fitnessMax < fitnessLim && superPopulation.numberOfGenerations < maxGenerations && stagnation <= maxStagnation) {
            Parameters.logger.log(Level.FINE, "Generationsdurchlauf: " + (superPopulation.numberOfGenerations + 1));
            Parameters.logger.log(Level.FINEST, "Größe Superpopulation: " + superPopulation.size());
            for (Population population : species.keySet()) {
                Parameters.logger.log(Level.FINEST, "Größe : " + population.getIndividualType() + ":" + population.size());
            }
            /**
             * Superpopulation erzeugen.
             */
            this.oldSuperPopulation = superPopulation;
            this.superPopulation = new Population(superType, superPopulation.numberOfGenerations + 1);
            /**
             * Schleife über alle Species.
             */

            initNextGenRunnables();

            for (Future<?> speciesNextGenThread : subPopulationThreads) {
                try {
                    speciesNextGenThread.get();
                } catch (ExecutionException ex) {
                    ex.getCause().printStackTrace();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MultipleSpeciesCoevolvingParallelGA.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            subPopulationThreads.clear();

            if (acceptanceMechanism != null && oldSuperPopulation != null && oldSuperPopulation.size() != 0) {
                superPopulation = acceptanceMechanism.getFilteredNewPopulation(oldSuperPopulation, superPopulation);
            }

            I fittestIndividual = this.getPopulation().getFittestIndividual();
            double[] fitnessVector = fittestIndividual.getFitnessVector();
            if (oldbestFitness != null) {
                boolean better = false;
                for (int i = 0; i < fitnessVector.length; i++) {
                    double newI = fitnessVector[i];
                    double oldI = oldbestFitness[i];
                    if (newI > oldI) {
                        better = true;
                        break;
                    }
                }
                if (!better) {
                    stagnation++;
                } else {
                    stagnation = 0;
                }
                Parameters.logger.fine("Stagnation: " + stagnation);
            }
            oldbestFitness = fitnessVector;

            Parameters.logger.log(Level.FINER, "MEAN:" + this.getPopulation().getMeanFitness());

            Parameters.logger.log(Level.FINE, "Fittest SuperIndividuum:  (" + this.getPopulation().size() + ")" + Arrays.toString(fittestIndividual.getFitnessVector()));

            GAEvent gaEvent = new CoEvoGAEvent(this, GAEvent.StatusGAEvent.NEXTGENERATION, superPopulation, species.keySet());
            fire(gaEvent);
        }
        GAEvent gaEvent = new GAEvent(this, GAEvent.StatusGAEvent.FINISHED, superPopulation);
        Parameters.getThreadPool().shutdown();
        subPopulationPool.shutdown();
        fire(gaEvent);
        for (GABundle gABundle : species.values()) {
            gABundle.nextGenAlgo.fire(new IndividualEvent(gABundle.nextGenAlgo));
        }
    }

    protected void initNextGenRunnables() {
        for (Population population : species.keySet()) {
            SpeciesNextGenRunnable subPopRunnable = new SpeciesNextGenRunnable(this, population);
            subPopulationThreads.add(this.subPopulationPool.submit(subPopRunnable));
        }
    }

    /**
     * Initialisierung der Populationen, Fitnessbestimmung der
     * Startpopulationen.
     */
    protected void initPopulations() {
        for (Population subPopulation : species.keySet()) {
            this.previousPopulations.put(subPopulation.getIndividualType(), subPopulation);
        }
        /**
         * Startfitnessberechnung.
         */
        Parameters.logger.log(Level.FINE, "Start bei Generation -1");
        this.superPopulation = new Population(superType, -1);
        ArrayList<Future<?>> inititialThreads = new ArrayList<>();
        for (final Population population : species.keySet()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        computeFitnessMax(population, startSuperIndividualGenerator);
                    } catch (Exception e) {
                        Logger.getLogger(MultipleSpeciesCoevolvingParallelGA.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            };
            Future<?> future = Parameters.getThreadPool().submit(runnable);
            inititialThreads.add(future);
        }
        for (Future<?> future : inititialThreads) {
            try {
                future.get();
            } catch (ExecutionException ex) {
                ex.getCause().printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(MultipleSpeciesCoevolvingParallelGA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (Population<? extends Individual> population : species.keySet()) {
            Individual fittestSubIndividual = population.getFittestIndividual();
            I fittestSuperIndividual = (I) fittestSubIndividual.additionalObjects.get(superIND);
            Parameters.logger.fine("Parallel_Co_evolving_Initial fittest: " + Parameters.doubleFormat.format(fittestSubIndividual.getFitness()) + "\t Fittest Individual: " + fittestSuperIndividual);
            for (Individual individual : population.individuals()) {
                this.superPopulation.add((I) individual.additionalObjects.get(MultipleSpeciesCoevolvingGA.superIND));
            }

        }
        inititialThreads = null;
    }

    public void computeFitnessMax(Population<Individual> newSubPopulation, MultipleSpeciesIndividualGenerator<I> generator) {
        ArrayList<Future<?>> evaluationThreads = new ArrayList<>();
        Iterable<List<Individual>> partition = Iterables.partition(newSubPopulation.individuals(), Math.max(1, newSubPopulation.individuals().size() / Parameters.NUMBER_OF_THREADS));

        for (List<Individual> list : partition) {
            MultiSpeciesEvaluationRunnable<I> runnable = new MultiSpeciesEvaluationRunnable(list, fitnessFunction, generator.clone(), this.previousPopulations);
            evaluationThreads.add(Parameters.getThreadPool().submit(runnable));
        }

        for (Future<?> future : evaluationThreads) {
            try {
                future.get();
            } catch (ExecutionException ex) {
                ex.getCause().printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(MultipleSpeciesCoevolvingParallelGA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        evaluationThreads = null;

        Individual fittestSubIndividual = newSubPopulation.getFittestIndividual();
        double fitness = fittestSubIndividual.getFitness();
        if (fitness >= fitnessMax) {
            fitnessMax = fitness;
        }

    }

    public Population getSubPopulation(Class<?> cls) {
        for (Population population : species.keySet()) {
            if (population.getIndividualType().equals(cls)) {
                return population;
            }
        }
        return null;
    }

    public Population<I> getPopulation() {
        return superPopulation;
    }

    protected void setSuperPopulation(Population<I> superPopulation) {
        this.superPopulation = superPopulation;
    }

    public MultipleSpeciesIndividualGenerator<I> getSuperIndividualGenerator() {
        return superIndividualGenerator;
    }

    public HashMap<Class, Population> getPreviousPopulations() {
        return previousPopulations;
    }

    public void addIndividualLister(IndividualListener<I> listener) {
        for (GABundle gABundle : species.values()) {
            gABundle.nextGenAlgo.addGAListener(listener);
        }
    }

    @Override
    public Map<Population<? extends Individual>, GABundle> getSpecies() {
        return this.species;
    }

    @Override
    public FitnessEvalationFunction<I> getFitnessEvalationFunction() {
        return this.fitnessFunction;
    }

}
