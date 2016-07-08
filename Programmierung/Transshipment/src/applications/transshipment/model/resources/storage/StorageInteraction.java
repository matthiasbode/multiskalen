/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;

/**
 * Gibt an, ob eine Zwischenabstellung für den Austausch zwischen zwei Transportsystem geeignet ist.
 * @author bode
 */
public interface StorageInteraction   {
     public boolean canTransferBetween(ConveyanceSystem c1, ConveyanceSystem c2);
}
