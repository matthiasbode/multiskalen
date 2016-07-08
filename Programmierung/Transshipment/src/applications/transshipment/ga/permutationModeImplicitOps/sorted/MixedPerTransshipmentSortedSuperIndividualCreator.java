/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps.sorted;

import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import ga.algorithms.coevolving.individuals.SuperIndividualCreator;
import ga.individuals.Individual;
import java.util.Map;

/**
 *
 * @author bode
 */
public class MixedPerTransshipmentSortedSuperIndividualCreator implements SuperIndividualCreator<MixedPerSortedSuperIndividual> {

    @Override
    public MixedPerSortedSuperIndividual create(Map<Class, Individual> subIndividuals) {
        return new MixedPerSortedSuperIndividual((ImplicitOperationIndividual) subIndividuals.get(ImplicitOperationIndividual.class), (PermutationModeIndividualSorted) subIndividuals.get(PermutationModeIndividualSorted.class));
    }

}
