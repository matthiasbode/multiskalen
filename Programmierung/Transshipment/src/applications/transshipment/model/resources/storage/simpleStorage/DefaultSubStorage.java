/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage.simpleStorage;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.SubResource;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.DefaultLoadUnitResource;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.TreeSet;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.DoubleValue;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class DefaultSubStorage extends DefaultLoadUnitResource implements SubResource, LoadUnitStorage, LocationBasedStorage {

    public static final String PREFIX = "DefaultSubStorage";
    private String ID;
    private LoadUnitStorage superResource;
    private ArrayList<SimpleStorageRow> rows;
//    private Collection<? extends StorageLocation> locs;
    private Area area;
    private Double minLocLen = null;

//    public DefaultSubStorage(LoadUnitStorage superResource, Collection<? extends StorageLocation> locs) {
//        this.superResource = superResource;
//        this.locs = locs;
//        this.setTemporalAvailability(superResource.getTemporalAvailability());
//        makeID(PREFIX);
//    }
    public DefaultSubStorage(LoadUnitStorage superResource, ArrayList<SimpleStorageRow> rows) {
        this.superResource = superResource;
        this.rows = rows;
        this.setTemporalAvailability(superResource.getTemporalAvailability());
        makeID(PREFIX);
    }

    public DefaultSubStorage(ArrayList<? extends StorageLocation> locs, LoadUnitStorage superResource) {
        this.superResource = superResource;
        SimpleStorageRow sr = new SimpleStorageRow(locs);
        this.rows = new ArrayList<>();
        this.rows.add(sr);
        this.setTemporalAvailability(superResource.getTemporalAvailability());
        makeID(PREFIX);
    }

    @Override
    public Resource getSuperResource() {
        return this.superResource;
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        return true;
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (area == null) {
            this.area = new Area();
            for (SimpleStorageRow simpleStorageRow : rows) {
                area.add(simpleStorageRow.getGeneralOperatingArea());
            }
//            for (StorageLocation storageLocation : locs) {
//                area.add(storageLocation.getGeneralOperatingArea());
//            }
        }
        return area;
    }

    @Override
    public LoadUnitStorage getSubResource(Area area) {
        ArrayList<SimpleStorageRow> res = new ArrayList<>();
        for (SimpleStorageRow simpleStorageRow : rows) {
            SimpleStorageRow.SimpleSubStorageRow subResource = simpleStorageRow.getSubResource(area);
            res.add(subResource);
        }
//        for (StorageLocation storageLocation : this.locs) {
//            Area tmp = new Area(area);
//            tmp.intersect(storageLocation.getGeneralOperatingArea());
//            if (!tmp.isEmpty()) {
//                res.add(storageLocation);
//            }
//        }
        return new DefaultSubStorage(superResource, res);
    }

    @Override
    public LocationBasedStorage getSection(LocationBasedStorage storage) {
        ArrayList<SimpleStorageRow> res = new ArrayList<>();
        for (SimpleStorageRow simpleStorageRow : rows) {
            SimpleStorageRow.SimpleSubStorageRow subResource = simpleStorageRow.getSection(storage);
            res.add(subResource);
        }
//        HashSet<StorageLocation> res = new HashSet<>();
//        for (StorageLocation storageLocation : storage.getStorageLocations()) {
//            if (this.locs.contains(storageLocation)) {
//                res.add(storageLocation);
//            }
//        }
        return new DefaultSubStorage(superResource, res);
    }

    public void makeID(String PREFIX) {
        ID = PREFIX + "{";
        if (getNumberOfStorageLocations() > 6) {
            TreeSet<StorageLocation> slocs = new TreeSet<>(new Comparator<StorageLocation>() {
                @Override
                public int compare(StorageLocation o1, StorageLocation o2) {
                    return Integer.compare(o1.number, o2.number);
                }
            });
            slocs.addAll(getStorageLocations());
            ID += slocs.first().number + " ";
            ID += "... ";
            ID += slocs.last().number + " ";
        } else {
            for (StorageLocation sl : getStorageLocations()) {
                ID += sl.number + " ";
            }
        }
        ID += '}';
    }

    @Override
    public String toString() {
        return ID + "\t" + this.getCenterOfGeneralOperatingArea();
    }

    @Override
    public int getNumberOfStorageLocations() {
        int number = 0;
        for (SimpleStorageRow simpleStorageRow : rows) {
            number += simpleStorageRow.getNumberOfStorageLocations();
        }
        return number;
    }

    @Override
    public Collection<? extends StorageLocation> getStorageLocations() {
        HashSet<StorageLocation> res = new HashSet<>();
        for (SimpleStorageRow simpleStorageRow : rows) {
            res.addAll(simpleStorageRow.getStorageLocations());
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.superResource);
        hash = 17 * hash + Objects.hashCode(this.rows);
        hash = 17 * hash + Objects.hashCode(this.area);
        hash = 17 * hash + Objects.hashCode(this.minLocLen);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultSubStorage other = (DefaultSubStorage) obj;
        if (!Objects.equals(this.superResource, other.superResource)) {
            return false;
        }
        if (!Objects.equals(this.rows, other.rows)) {
            return false;
        }
        if (!Objects.equals(this.area, other.area)) {
            return false;
        }
        if (!Objects.equals(this.minLocLen, other.minLocLen)) {
            return false;
        }
        return true;
    }

    @Override
    public double getMinLocLen() {
        if (minLocLen == null) {
            minLocLen = Double.POSITIVE_INFINITY;
            for (SimpleStorageRow simpleStorageRow : rows) {

                if (simpleStorageRow.getMinLocLen() < minLocLen) {
                    minLocLen = simpleStorageRow.getMinLocLen();
                }

            }
        }
        return minLocLen;
    }

    @Override
    public FieldElement getDemand(LoadUnit loadUnit) {
        int need = (int) Math.ceil((loadUnit.getLength() + 2 * loadUnit.getLongitudinalDistance()) / this.getMinLocLen());
        return new DoubleValue(need);
    }

    @Override
    public ArrayList<SimpleStorageRow> getRows() {
        return rows;
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, 0);
    }

    @Override
    public String getID() {
        return ID;
    }
}
