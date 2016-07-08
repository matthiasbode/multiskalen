/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps.sorted;

import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;

/**
 *
 * @author bode
 */
public class MixedPerSortedSuperIndividual extends TransshipmentSuperIndividual<ImplicitOperationIndividual, PermutationModeIndividualSorted> {

    public MixedPerSortedSuperIndividual() {
    }

    public MixedPerSortedSuperIndividual(ImplicitOperationIndividual operationIndividual, PermutationModeIndividualSorted modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

}
