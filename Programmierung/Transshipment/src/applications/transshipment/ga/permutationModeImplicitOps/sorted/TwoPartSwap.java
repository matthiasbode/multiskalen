/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps.sorted;

import applications.transshipment.model.LoadUnitJob;
import ga.individuals.IntegerIndividual;
import ga.individuals.subList.ListIndividual;
import ga.mutation.ListIndividualSwap;
import ga.mutation.Mutation;
import ga.mutation.SwapMutation;

/**
 *
 * @author bode
 */
public class TwoPartSwap implements Mutation<PermutationModeIndividualSorted> {

    ListIndividualSwap<LoadUnitJob> swapJobs = new ListIndividualSwap<>();
    SwapMutation<IntegerIndividual> routeOrder = new SwapMutation<>();

    @Override
    public PermutationModeIndividualSorted mutate(PermutationModeIndividualSorted c, double xMutationRate) {
        ListIndividual<LoadUnitJob> mutated1 = swapJobs.mutate(c.jobsSort, xMutationRate);
        IntegerIndividual mutate2 = routeOrder.mutate(c.routeOrder, xMutationRate);
        return new PermutationModeIndividualSorted(mutated1, mutate2);
    }
}
