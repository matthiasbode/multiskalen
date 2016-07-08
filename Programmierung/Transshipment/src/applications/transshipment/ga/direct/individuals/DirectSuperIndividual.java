/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.individuals;

import applications.transshipment.ga.TransshipmentSuperIndividual;
import ga.individuals.subList.ListIndividual;
import ga.individuals.IntegerIndividual;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;

/**
 *
 * @author bode
 */
public class DirectSuperIndividual extends TransshipmentSuperIndividual<ListIndividual<RoutingTransportOperation>, IntegerIndividual> {

    public DirectSuperIndividual(ListIndividual<RoutingTransportOperation> operationIndividual, IntegerIndividual modeIndividual) {
        super(operationIndividual, modeIndividual);
    }

}
