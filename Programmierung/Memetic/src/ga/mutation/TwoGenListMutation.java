package ga.mutation;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import ga.individuals.TwoGenListIndividual;
import ga.mutation.Mutation;

/**
 *
 * @author bode
 */
public class TwoGenListMutation<K, T> implements Mutation<TwoGenListIndividual<K, T>> {

    public TwoGenListMutation() {
    }

    @Override
    public TwoGenListIndividual<K, T> mutate(TwoGenListIndividual<K, T> c, double xMutationRate) {
        return c;
    }

   
}
