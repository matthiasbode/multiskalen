/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.transport;

import applications.transshipment.model.operations.LoadUnitOperation;
import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 *
 * @author bode
 */
public interface TransportOperation extends LoadUnitOperation {
    
    /**
     * Gibt die @link{LoadUnitResource} zurueck, von welcher aus die
     * Transportoperation startet.
     *
     * @return  Ursprungs @link{LoadUnitResource}
     */
    public LoadUnitStorage getOrigin();

    /**
     * Gibt die @link{LoadUnitResource} zurueck, zu welcher die
     * Transportoperation durchgefuehrt wird.
     *
     * @return  Ziel @link{LoadUnitResource}
     */
    public LoadUnitStorage getDestination();

    

}
