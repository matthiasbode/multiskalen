package ga.crossover;

import ga.individuals.Individual;
import java.util.Collection;

/**
 *
 * @author hoecker
 */
public interface Crossover<I extends Individual> {
 
   public Collection<? extends I> recombine(I c1, I c2, double xOverRate);

}
