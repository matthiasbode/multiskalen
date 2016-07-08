/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.transport;

import applications.mmrcsp.model.operations.SingleResourceOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;

/**
 *
 * @author bode
 * @param <E>
 */
public interface SingleResourceTransportOperation<E extends ConveyanceSystem>  extends TransportOperation, SingleResourceOperation{
    @Override
    public E getResource();
}
