/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import applications.mmrcsp.model.resources.PositionedResource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.LoadUnitResource;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */
public class Terminal implements PositionedResource {

    private Collection<ConveyanceSystem> conveyanceSystems;
    private Collection<LoadUnitStorage> storages;
    private Collection<RailroadTrack> gleise;
    private Collection<SharedResource> sharedResources = new LinkedHashSet<>();
    private Area area;
    private TimeSlotList temporalAvailability;
    private LoadUnitStorage dnfStorage;

    public Terminal(TimeSlotList temporalAvailability, Collection<ConveyanceSystem> conveyanceSystems, Collection<LoadUnitStorage> storages, Collection<RailroadTrack> gleise, LoadUnitStorage dnfStorage) {
        this.conveyanceSystems = conveyanceSystems;
        this.storages = storages;
        this.gleise = gleise;
        this.setTemporalAvailability(temporalAvailability);
        this.dnfStorage = dnfStorage;
    }

    public Terminal(TimeSlot temporalAvailability, Collection<ConveyanceSystem> conveyanceSystems, Collection<LoadUnitStorage> storages, Collection<RailroadTrack> gleise, LoadUnitStorage dnfStorage) {
        this.conveyanceSystems = conveyanceSystems;
        this.storages = storages;
        this.gleise = gleise;
        this.setTemporalAvailability(temporalAvailability);
        this.dnfStorage = dnfStorage;
    }

    public Terminal(TimeSlot temporalAvailability, Collection<ConveyanceSystem> conveyanceSystems, Collection<LoadUnitStorage> storages, Collection<RailroadTrack> gleise, Collection<SharedResource> sharedResources, LoadUnitStorage dnfStorage) {
        this.sharedResources = sharedResources;
        this.conveyanceSystems = conveyanceSystems;
        this.storages = storages;
        this.gleise = gleise;
        this.setTemporalAvailability(temporalAvailability);
        this.dnfStorage = dnfStorage;
    }

    public LoadUnitStorage getDnfStorage() {
        return dnfStorage;
    }

    public Collection<ConveyanceSystem> getConveyanceSystems() {
        return conveyanceSystems;
    }

    public Collection<LoadUnitStorage> getStorages() {
        return storages;
    }

    public Collection<RailroadTrack> getGleise() {
        return gleise;
    }

    public Collection<SharedResource> getSharedResources() {
        return sharedResources;
    }

    public void addSharedResource(SharedResource resource) {
        this.sharedResources.add(resource);
    }

    public StorageLocation getStorageLocation(String id) {
        for (LoadUnitStorage loadUnitStorage : storages) {
            if (loadUnitStorage instanceof SimpleStorageRow) {
                SimpleStorageRow sr = (SimpleStorageRow) loadUnitStorage;
                for (StorageLocation storageLocation : sr.getStorageLocations()) {
                    if (storageLocation.getID().equals(id)) {
                        return storageLocation;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (area == null) {
            area = new Area();
            for (ConveyanceSystem conveyanceSystem : conveyanceSystems) {
                area.add(conveyanceSystem.getGeneralOperatingArea());
            }
            for (LoadUnitStorage loadUnitStorage : storages) {
                area.add(loadUnitStorage.getGeneralOperatingArea());
            }
            for (RailroadTrack railroadTrack : gleise) {
                area.add(railroadTrack.getGeneralOperatingArea());
            }
        }
        return area;
    }

    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
        return new Point2d(getGeneralOperatingArea().getBounds2D().getCenterX(), getGeneralOperatingArea().getBounds2D().getCenterY());
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        return temporalAvailability;
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.temporalAvailability = tempAvail;
        for (LoadUnitResource loadUnitResource : getAllResources()) {
            loadUnitResource.setTemporalAvailability(tempAvail);
        }
        for (SharedResource sharedResource : getSharedResources()) {
            sharedResource.setTemporalAvailability(tempAvail);
        }
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.temporalAvailability = new TimeSlotList(tempAvail);
        for (LoadUnitResource loadUnitResource : getAllResources()) {
            loadUnitResource.setTemporalAvailability(tempAvail);
        }
        for (SharedResource sharedResource : getSharedResources()) {
            sharedResource.setTemporalAvailability(tempAvail);
        }
    }

    public Collection<LoadUnitResource> getAllResources() {
        ArrayList<LoadUnitResource> result = new ArrayList<>();
        result.addAll(getConveyanceSystems());
        result.addAll(getStorages());
        return result;
    }

    @Override
    public String getID() {
        return "Terminal";
    }
}
