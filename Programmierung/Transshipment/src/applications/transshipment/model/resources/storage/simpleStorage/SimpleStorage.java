/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage.simpleStorage;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.DefaultLoadUnitResource;
import applications.transshipment.model.resources.storage.LoadUnitStorage;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.DoubleValue;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class SimpleStorage extends DefaultLoadUnitResource implements LoadUnitStorage, LocationBasedStorage {

    private final int number;
    private static int count = 0;
    private ArrayList<SimpleStorageRow> rows;
    private Area area;
    private Double minLocLen = null;

    public SimpleStorage(ArrayList<SimpleStorageRow> rows) {
        this.rows = rows;
        this.number = count++;
    }

    public SimpleStorage() {
        this.rows = new ArrayList<>();
        this.number = count++;
    }

    public void addStorageRow(SimpleStorageRow row) {
        this.rows.add(row);
        this.getGeneralOperatingArea();
        this.area.add(row.getGeneralOperatingArea());
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        return true;
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (this.area == null) {
            this.area = new Area();
            for (SimpleStorageRow simpleStorageRow : rows) {
                area.add(simpleStorageRow.getGeneralOperatingArea());
            }
        }
        return this.area;
    }

    @Override
    public LoadUnitStorage getSubResource(Area area) {
        ArrayList<SimpleStorageRow> set = new ArrayList<>();
        for (SimpleStorageRow simpleStorageRow : rows) {
            SimpleStorageRow.SimpleSubStorageRow subResource = simpleStorageRow.getSubResource(area);
            if (subResource != null) {
                set.add(subResource);
            }
        }
        return new DefaultSubStorage(this, set);
    }

    @Override
    public String toString() {
        return "SimpleStorage{" + getNumber() + '}';
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
    public Collection<StorageLocation> getStorageLocations() {
        ArrayList<StorageLocation> res = new ArrayList<>();
        for (SimpleStorageRow simpleStorageRow : rows) {
            res.addAll(simpleStorageRow.getStorageLocations());
        }
        return res;

    }

    @Override
    public LocationBasedStorage getSection(LocationBasedStorage storage) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public double getMinLocLen() {
        minLocLen = Double.POSITIVE_INFINITY;
        for (SimpleStorageRow simpleStorageRow : rows) {
            if (simpleStorageRow.getMinLocLen() < minLocLen) {
                minLocLen = simpleStorageRow.getMinLocLen();
            }
        }
        return minLocLen;
    }

    @Override
    public FieldElement getDemand(LoadUnit loadUnit) {
        int need = (int) Math.ceil((loadUnit.getLength() + 2 * loadUnit.getLongitudinalDistance()) / this.getMinLocLen());
        return new DoubleValue(need);
    }

    public int getNumber() {
        return number;
    }

    @Override
    public ArrayList<SimpleStorageRow> getRows() {
        return this.rows;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        super.setTemporalAvailability(tempAvail); //To change body of generated methods, choose Tools | Templates.
        for (SimpleStorageRow simpleStorageRow : rows) {
            simpleStorageRow.setTemporalAvailability(tempAvail);
        }
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        super.setTemporalAvailability(tempAvail); //To change body of generated methods, choose Tools | Templates.
        for (SimpleStorageRow simpleStorageRow : rows) {
            simpleStorageRow.setTemporalAvailability(tempAvail);
        }
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, 0);
    }

    @Override
    public String getID() {
       return toString();
    }

}
