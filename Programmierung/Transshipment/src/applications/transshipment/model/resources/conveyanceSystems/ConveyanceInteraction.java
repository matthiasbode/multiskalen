/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems;

import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 *
 * @author bode
 */
public interface ConveyanceInteraction  {
    public boolean canInteractWith(LoadUnitStorage lus);    
    
    
}
