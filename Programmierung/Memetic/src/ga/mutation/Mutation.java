package ga.mutation;

import ga.individuals.Individual;

/**
 *
 * @author hoecker
 * @param <I>
 */
public interface Mutation<I extends Individual> {

    public static final String mutated = "MUTATED";

    public I mutate(I c, double xMutationRate);

}
