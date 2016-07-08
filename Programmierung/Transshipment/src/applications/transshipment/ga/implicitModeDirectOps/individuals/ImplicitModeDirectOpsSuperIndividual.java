/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicitModeDirectOps.individuals;

import applications.transshipment.ga.TransshipmentSuperIndividual;
import ga.individuals.subList.ListIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;

/**
 *
 * @author bode
 */
public class ImplicitModeDirectOpsSuperIndividual extends TransshipmentSuperIndividual<ListIndividual, ImplicitModeIndividual> {

    public ImplicitModeDirectOpsSuperIndividual() {
    }

    public ImplicitModeDirectOpsSuperIndividual(ListIndividual operationIndividual, ImplicitModeIndividual modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

}
