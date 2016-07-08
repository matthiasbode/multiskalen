package ga.nextGeneration.memetic;

import ga.Parameters;
import ga.individuals.Individual;
import ga.basics.*;
import ga.crossover.Crossover;
import ga.mutation.Mutation;
import ga.selection.Selection;
 
import ga.listeners.IndividualEvent;
import ga.localSearch.LocalSearch;
import ga.nextGeneration.NextGenerationAlgorithm;
import java.util.Collection;
import java.util.logging.Level;

/**
 *
 * @author bode
 */
public class StandardMemetic<I extends Individual> extends NextGenerationAlgorithm<I>  {

    
    LocalSearch<I> local;

    public StandardMemetic(double xOverRate, double xMutaRate, Selection<I> parentSelectAlg, Mutation<I> mutationAlgorithm, Crossover<I> crossoverAlgorithm, LocalSearch<I> local) {
        super( xOverRate, xMutaRate, parentSelectAlg, mutationAlgorithm, crossoverAlgorithm);
        this.local = local;
    }

    @Override
    public Population<I> computeNextGeneration(Population<I> pop) {
        Population<I> nextGeneration = new Population<>(pop.getIndividualType(), pop.numberOfGenerations + 1);
        int size = pop.size();
        if (size == 1) {
            nextGeneration.addAll(pop.individuals());
            return nextGeneration;
        }

        nextGeneration.add((I) pop.getFittestIndividual().clone());
        Parameters.logger.log(Level.FINE, "Population {0}", nextGeneration.numberOfGenerations);
        while (nextGeneration.size() < size) {
            /**
             * #################################################### Reproduktion
             * ####################################################
             */
            // Selektion
            I selection1 = selection.selectFromPopulation(pop);
            I selection2 = selection.selectFromPopulation(pop);
            // Kreuzen mit Reproduktion
            Collection<? extends I> recombine = crossover.recombine(selection1, selection2, xOverRate);
            for (I child : recombine) {

               fire(new IndividualEvent<>(this, IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL, nextGeneration.numberOfGenerations, child));

                Parameters.logger.finest("++++++++++++++++++");
                Parameters.logger.log(Level.FINEST, selection1 + " gekreuzt  " + selection2);
                Parameters.logger.log(Level.FINEST, "gekreuzt: " + child);

                /**
                 * #################################################### Lokale
                 * Suche ####################################################
                 */
                if (!child.equals(selection1) && !child.equals(selection2)) {
                    I old = child;
                    child = (local.localSearch(child));
                    fire(new IndividualEvent<I>(this, IndividualEvent.StatusIndividualEvent.NEW_LS_INDIVIDUAL, nextGeneration.numberOfGenerations, child, old));
                }

                /**
                 * #################################################### NEUES
                 * INDIVIDUUM IN NEXTGENERATION HINZUFÜGEN
                 * ####################################################
                 */
                nextGeneration.add(child);
                Parameters.logger.log(Level.FINEST, "localSearch Reproduction in Next: " + child);
                if (nextGeneration.size() == size) {
                    break;
                }

                /**
                 * #################################################### Mutation
                 * ####################################################
                 */
                I mutated = (mutation.mutate(child, super.xMutaRate));

                if (!mutated.equals(child)) {

                    Parameters.logger.log(Level.FINEST, "mutated: {0}", mutated);
                    Parameters.logger.finest("++++++++++++++++++");

                   fire(new IndividualEvent(this, IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL, nextGeneration.numberOfGenerations, mutated));
                    /**
                     * ####################################################
                     * Lokale Suche nach Mutation
                     * ####################################################
                     */
                    I localAfterMutation = local.localSearch(mutated);
                   fire(new IndividualEvent(this, IndividualEvent.StatusIndividualEvent.NEW_LS_INDIVIDUAL, nextGeneration.numberOfGenerations, localAfterMutation, mutated));

                    Parameters.logger.finest("next: " + localAfterMutation);
                    Parameters.logger.finest("++++++++++++++++++");

                    /**
                     * ####################################################
                     * NEUES INDIVIDUUM IN NEXTGENERATION
                     * ####################################################
                     */
                    nextGeneration.add(localAfterMutation);
                }
            }

        }

        return nextGeneration;
    }

     

  
}
