package ga.selection;

import ga.individuals.Individual;
import ga.basics.Population;

/**
 *
 * @author hoecker
 */
public class DuelSelection<C extends Individual> implements Selection<C> {

    @Override
    public C selectFromPopulation(Population<C> pop) {
        C in1 = pop.getRandomIndividual();
        C in2 = pop.getRandomIndividual();

        return (in1.getFitness().compareTo(in2.getFitness()) > 0) ? in1 : in2;
    }

    @Override
    public DuelSelection<C> clone() {
        return new DuelSelection<>();
    }

}
