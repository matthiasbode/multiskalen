/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.storage.StorageInteraction;

/**
 *
 * @author bode
 */
public class HandoverPointInteractionRule implements StorageInteraction {

    @Override
    public boolean canTransferBetween(ConveyanceSystem c1, ConveyanceSystem c2) {
        if (c1 instanceof Crane && c2 instanceof Crane) {
            return false;
        }

        if (c1 instanceof LCSystem && c2 instanceof LCSystem) {
            return false;
        }
        return !c1.equals(c2);
    }
}
