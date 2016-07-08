/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps;

import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;

/**
 *
 * @author bode
 */
public class PermutationModeImplicitOpsSuperIndividual extends TransshipmentSuperIndividual<ImplicitOperationIndividual, PermutationModeIndividual> {

    public PermutationModeImplicitOpsSuperIndividual() {
    }

    public PermutationModeImplicitOpsSuperIndividual(ImplicitOperationIndividual operationIndividual, PermutationModeIndividual modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

}
