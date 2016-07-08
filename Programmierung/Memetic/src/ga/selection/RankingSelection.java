package ga.selection;

import ga.individuals.Individual;
import ga.Parameters;
import ga.basics.Population;
import java.util.ArrayList;

/**
 *
 * @author hoecker
 */
public class RankingSelection<C extends Individual> implements Selection<C> {

    public static final double S_MIN = 1.;
    public static final double S_MAX = 2.;
    public double s;
    private Population pop;
    private ArrayList<C> ranking;
    private double[] p;

    public RankingSelection(double s) {
        if (s <= S_MIN || s > S_MAX) {
            throw new IllegalArgumentException("s ausserhalb (" + S_MIN + "," + S_MAX + "]");
        }
        this.s = s;
    }

    public RankingSelection() {
        this(1.8);
    }

    @Override
    public RankingSelection<C> clone() {
        return new RankingSelection<>(this.s);
    }

    @Override
    public C selectFromPopulation(Population<C> pop) {
        if (this.p == null || this.pop != pop || this.pop.numberOfGenerations != pop.numberOfGenerations) {
            this.computeProbabilities(pop);
        }
        double r = Parameters.getRandom().nextDouble();
        double sumP = 0.;

        for (int i = 0; i < this.p.length; i++) {
            sumP += p[i];
            if (r <= sumP) {
                return ranking.get(i);
            }
        }
        return null;
    }

    private double[] computeProbabilities(Population<C> pop) {
        this.pop = pop;
        this.ranking = pop.getIndividualsSortedList();
        this.p = new double[ranking.size()];
        int mue = pop.size(); // ggf. muss die Elite abgezogen werden
        for (int i = 0; i < p.length; i++) {
            int rank = i;
            p[i] = ((2. - s) / mue) + ((2. * rank * (s - 1.)) / (mue * (mue - 1.)));
        }
        return p;
    }

    @Override
    public String toString() {
        return "RankingSelection{" + '}';
    }
    
    
}
