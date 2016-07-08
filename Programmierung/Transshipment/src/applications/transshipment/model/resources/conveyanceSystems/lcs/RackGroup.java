/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs;

import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.LoadUnitStorageGroup;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author bode
 */
public class RackGroup extends LoadUnitStorageGroup<LCSHandover> implements LCSHandover, LocationBasedStorage {

    public RackGroup(List<? extends LCSHandover> loadUnitStorages) {
        super(loadUnitStorages);
    }

    public HashMap<StorageLocation, RackNode> getNodes() {
        HashMap<StorageLocation, RackNode> result = new HashMap<StorageLocation, RackNode>();
        for (LCSHandover lCSHandover : group) {
            result.putAll(lCSHandover.getNodes());
        }
        return result;
    }

    @Override
    public LoadUnitStorage getSubResource(Area area) {
        List<LCSHandover> s = new ArrayList<>();
        for (LCSHandover e : group) {
            HandoverPoint.SubHandoverPoint sr = (HandoverPoint.SubHandoverPoint) e.getSubResource(area);
            if (sr != null) {
                s.add(sr);
            }
        }
        return (s.isEmpty()) ? null : new RackGroup(s);
    }

    @Override
    public int getNumberOfStorageLocations() {
        int number = 0;
        for (LCSHandover e : group) {
            number += e.getNumberOfStorageLocations();
        }
        return number;
    }

    @Override
    public ArrayList<SimpleStorageRow> getRows() {
        ArrayList<SimpleStorageRow> result = new ArrayList<SimpleStorageRow>();
        for (LCSHandover lCSHandover : group) {
            result.addAll(lCSHandover.getRows());
        }
        return result;
    }

    @Override
    public Collection<? extends StorageLocation> getStorageLocations() {
        ArrayList<StorageLocation> storageLocations = new ArrayList<>();
        for (LCSHandover lCSHandover : group) {
            storageLocations.addAll(lCSHandover.getStorageLocations());
        }
        return storageLocations;
    }

    @Override
    public LocationBasedStorage getSection(LocationBasedStorage storage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getMinLocLen() {
        double minLocLen = Double.MAX_VALUE;
        for (LCSHandover lCSHandover : group) {
            double minLocLen1 = lCSHandover.getMinLocLen();
            if (minLocLen1 < minLocLen) {
                minLocLen = minLocLen1;
            }
        }
        return minLocLen;
    }

}
