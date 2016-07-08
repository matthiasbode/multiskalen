package ga.nextGeneration;

import ga.individuals.Individual;
import ga.basics.Population;
import ga.selection.Selection;
import ga.crossover.Crossover;
import ga.listeners.IndividualEvent;
import ga.listeners.IndividualListener;
import ga.mutation.Mutation;
import java.util.ArrayList;

/**
 * NextGenerationAlgorithm.java provides algorithms to compute the next
 * generation of a population.
 *
 * @author Mario Hoecker
 * @param <C>
 */
public abstract class NextGenerationAlgorithm<C extends Individual> {

    public double xOverRate;                            // rate for a local crossover
    public double xMutaRate;                            // rate for a local mutation
    public boolean recombElite = false;                 // pointer to recombine the elite
    public boolean createImmig = false;                 // pointer to create immigrants

    public Selection<C> selection;    // parent selection algorithm
    public Mutation<C> mutation;
    public Crossover<C> crossover;
    ArrayList<IndividualListener> listeners = new ArrayList<>();

    /**
     * Creates a new next genertion algorithm.
     *
     * @param eliteRate rate for the survival of the fittest.
     * @param immigRate rate for the immigrants.
     * @param xOverRate rate for a local crossover.
     * @param xMutaRate rate for a local mutation.
     * @param parentSelectAlg parent selection algorithm.
     */
    public NextGenerationAlgorithm(double xOverRate, double xMutaRate, Selection parentSelectAlg, Mutation<C> mutationAlgorithm, Crossover<C> crossoverAlgorithm) {
        if (xOverRate < 0. || xOverRate > 1.) {
            throw new IllegalArgumentException("Rate ausserhalb [0,1].");
        }
        if (xMutaRate < 0. || xMutaRate > 1.) {
            throw new IllegalArgumentException("Rate ausserhalb [0,1].");
        }
        this.xOverRate = xOverRate;
        this.xMutaRate = xMutaRate;
        this.selection = parentSelectAlg;
        this.mutation = mutationAlgorithm;
        this.crossover = crossoverAlgorithm;

    }

    /**
     * Creates the next generation of the population pop.
     *
     * @param pop <code>Population</code>.
     * @return <code>Population</code> as the next generation of pop.
     */
    public abstract Population<C> computeNextGeneration(Population<C> pop);

    public void addGAListener(IndividualListener<C> listener) {
        this.listeners.add(listener);
    }

    public void removeGAListener(IndividualListener<C> listener) {
        this.listeners.remove(listener);
    }

    public void fire(IndividualEvent<C> event) {
        for (IndividualListener iListener : listeners) {
            if (event.status == IndividualEvent.StatusIndividualEvent.FINISH) {
                iListener.finished();
            }
            iListener.newIndividual(event);
        }
    }

}
