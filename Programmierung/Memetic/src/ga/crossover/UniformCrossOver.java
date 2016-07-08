package ga.crossover;

import ga.individuals.Individual;
import ga.Parameters;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author hoecker
 */
public class UniformCrossOver<I extends Individual> implements Crossover<I> {

    @Override
    public Collection<I> recombine(I c1, I c2, double xOverRate) {
        I cNeu = (I) c1.clone();
        ArrayList<I> result = new ArrayList<>();
        if (Parameters.getRandom().nextDouble() > xOverRate) {
            result.add(cNeu);
            return result;
        }

        int laenge = Math.min(c1.size(), c2.size());
        for (int i = 0; i < laenge; i++) {
            if (Parameters.getRandom().nextDouble() > 0.5) {
                cNeu.set(i, c2.get(i));
            }
        }

        result.add(cNeu);
        return result;

    }
}
