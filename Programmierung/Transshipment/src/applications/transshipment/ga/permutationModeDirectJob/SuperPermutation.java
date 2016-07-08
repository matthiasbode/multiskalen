/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeDirectJob;

import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.ga.permutationModeImplicitOps.PermutationModeIndividual;

/**
 *
 * @author bode
 */
public class SuperPermutation extends TransshipmentSuperIndividual<PermutationJobIndividual, PermutationModeIndividual> {

    public SuperPermutation() {
    }

    public SuperPermutation(PermutationJobIndividual operationIndividual, PermutationModeIndividual modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

}
