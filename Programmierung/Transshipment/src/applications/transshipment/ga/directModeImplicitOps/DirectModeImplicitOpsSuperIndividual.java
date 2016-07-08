/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.directModeImplicitOps;

import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import ga.individuals.IntegerIndividual;

/**
 *
 * @author bode
 */
public class DirectModeImplicitOpsSuperIndividual extends TransshipmentSuperIndividual<ImplicitOperationIndividual, IntegerIndividual> {

    public DirectModeImplicitOpsSuperIndividual() {
    }

    public DirectModeImplicitOpsSuperIndividual(ImplicitOperationIndividual operationIndividual, IntegerIndividual  modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

}
