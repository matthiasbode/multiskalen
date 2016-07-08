/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;

/**
 *
 * @author bode
 */
public class DefaultStorageInteraction implements StorageInteraction {

    @Override
    public boolean canTransferBetween(ConveyanceSystem c1, ConveyanceSystem c2) {
        if (c1 instanceof LCSystem || c2 instanceof LCSystem) {
            return false;
        }
        return true;
    }
}
