/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.storage;

import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 *
 * @author bode
 */
public interface StoreOperation extends LoadUnitOperation{

    @Override
    public LoadUnitStorage getResource();

}
