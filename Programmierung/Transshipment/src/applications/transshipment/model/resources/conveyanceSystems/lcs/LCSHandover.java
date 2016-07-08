/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs;

import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import java.util.Map;

/**
 *
 * @author bode
 */
public interface LCSHandover extends LoadUnitStorage, LocationBasedStorage {

    public  Map<StorageLocation, RackNode> getNodes();

}
