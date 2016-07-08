/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage.simpleStorage;

import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author bode
 */
public interface LocationBasedStorage extends LoadUnitStorage {
    public int getNumberOfStorageLocations();
    public ArrayList<SimpleStorageRow> getRows();
    public Collection<? extends StorageLocation> getStorageLocations();
    public LocationBasedStorage getSection(LocationBasedStorage storage);
    /**
     * Die minimale Länge einer StorageLocation
     * @return 
     */
    public double getMinLocLen();
}
