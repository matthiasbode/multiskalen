package ga.selection;

import ga.individuals.Individual;
import ga.Parameters;
import ga.basics.Population;
import java.util.ArrayList;
import java.util.Random;
import util.RandomUtilities;

/**
 *
 * @author hoecker
 */
public class FitnessProportionalSelection<C extends Individual> implements Selection<C> {

    private Population pop;
    private ArrayList<C> ranking;
    private double[] p;
    private Random random = Parameters.getRandom();

    public FitnessProportionalSelection() {
    }

    @Override
    public FitnessProportionalSelection<C> clone() {
        return new FitnessProportionalSelection();
    }

    @Override
    public C selectFromPopulation(Population<C> pop) {
        if (this.p == null | this.pop != pop || this.pop.numberOfGenerations != pop.numberOfGenerations) {
            this.computeProbabilities(pop);
        }
        double r = random.nextDouble();
        double sumP = 0.;

        for (int i = 0; i < p.length; i++) {
            sumP += p[i];
            if (r <= sumP) {
                return ranking.get(i);
            }
        }
        return null;
    }

    private void computeProbabilities(Population pop) {
        this.pop = pop;
        ranking = pop.getIndividualsSortedList();
        double sumf = 0.;
        for (Individual i : ranking) {
            Double fitness = i.getFitness();
            if(Double.isNaN(fitness)){
                throw new UnknownError("Keine Fitness gesetzt.");
            }
            sumf += i.getFitness();
        }
        p = new double[ranking.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = ranking.get(i).getFitness() / sumf;
        }
    }
}