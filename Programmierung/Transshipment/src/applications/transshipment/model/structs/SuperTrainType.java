/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.structs;

import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import java.util.Collection;

/**
 *
 * @author bode
 */
public interface SuperTrainType extends TrainType, LocationBasedStorage{
    @Override
    public Collection<Slot> getStorageLocations();
}
