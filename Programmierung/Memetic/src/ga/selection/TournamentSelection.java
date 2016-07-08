package ga.selection;

import ga.basics.IndividualComparator;
import ga.individuals.Individual;
import ga.basics.Population;
import java.util.ArrayList;
import java.util.Collections;

/**
 * deterministische Turnierselektion fuer Pop mit transitiver Ordnung
 *
 * Turniergroesse n
 *
 * @author milbradt
 */
public class TournamentSelection<I extends Individual> implements Selection<I> {

    int n;

    /**
     * Creates a new instance of TournamentSelection
     */
    public TournamentSelection(int n) {
        this.n = n;
    }

    @Override
    public TournamentSelection clone() {
        return new TournamentSelection(this.n);
    }

    @Override
    public I selectFromPopulation(Population<I> pop) {
//        I fittest = pop.getRandomIndividual();
        ArrayList<I> inds = new ArrayList<I>();
        for (int i = 0; i < n; i++) {
            I tmp = pop.getRandomIndividual();
//            if (tmp.getFitness().compareTo(fittest.getFitness()) > 0) {
//                fittest = tmp;
//            }
            inds.add(tmp);
        }
        Collections.sort(inds, new IndividualComparator<I>());
        
//        return fittest;
         return inds.get(inds.size() - 1);
    }
}
