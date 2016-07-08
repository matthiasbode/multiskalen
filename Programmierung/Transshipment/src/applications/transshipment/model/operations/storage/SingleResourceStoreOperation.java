/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.storage;

import applications.mmrcsp.model.operations.SingleResourceOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 *
 * @author bode
 */
public interface SingleResourceStoreOperation extends SingleResourceOperation, StoreOperation {

    @Override
    public LoadUnitStorage getResource();
}
