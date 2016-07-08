/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.DefaultSubStorage;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author bode
 */
public class TrainSubStorage extends DefaultSubStorage implements SuperTrainType {

    public static final String PREFIX = "SubTrainType";

    public TrainSubStorage(LoadUnitStorage superResource, ArrayList<? extends StorageLocation> locs) {
        super(locs, superResource);
        makeID(PREFIX);
    }

    @Override
    public Collection<Slot> getStorageLocations() {
        Collection<? extends StorageLocation> storageLocations = super.getStorageLocations();
        ArrayList<Slot> result = new ArrayList<>();
        for (StorageLocation storageLocation : storageLocations) {
            result.add((Slot) storageLocation);
        }
        return result;
    }

}
