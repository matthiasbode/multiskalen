/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;

/**
 *
 * @author bode
 */
public interface SubConveyanceSystem extends ConveyanceSystem {
    public ConveyanceSystem getSuperConveyanceSystem();
}
