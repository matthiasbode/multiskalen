/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals;

import applications.transshipment.ga.TransshipmentSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;

/**
 *
 * @author bode
 */
public class ImplicitSuperIndividual extends TransshipmentSuperIndividual<ImplicitOperationIndividual, ImplicitModeIndividual> {

    public ImplicitSuperIndividual() {
    }

    public ImplicitSuperIndividual(ImplicitOperationIndividual operationIndividual, ImplicitModeIndividual modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

}
