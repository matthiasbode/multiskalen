/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import applications.mmrcsp.model.resources.SubResource;
import applications.mmrcsp.model.resources.SuperResource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.DefaultLoadUnitResource;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.loadunits.LoadUnitTypen;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import bijava.geometry.dim2.PolygonalCurve2d;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.DoubleValue;
import math.FieldElement;
import util.GeometryTools;

/**
 *
 * @author bode
 */
public class Train extends DefaultLoadUnitResource implements LoadUnitStorage, SuperResource, SuperTrainType {

    public enum Arrangement {

        left,
        right
    }
    private ArrayList<Wagon> wagons = new ArrayList<Wagon>();
    private TimeSlotList tempAvail;
    private RailroadTrack track;
    private Area area;
    private Double minLocLen = null;

    private static int counter = 0;
    private final int number;

    public Train(int numberOfWagons, int numberOfSlotsPerWagon) {
        this.number = counter++;
        wagons = new ArrayList<>(numberOfWagons);
        for (int i = 1; i <= numberOfWagons; i++) {
            Wagon w = new Wagon(this, numberOfSlotsPerWagon, i);
            wagons.add(w);
        }
    }

    public Train(int numberOfWagons, LoadUnitTypen... typen) {
        this.number = counter++;
        wagons = new ArrayList<>(numberOfWagons);
        for (int i = 1; i <= numberOfWagons; i++) {
            Wagon w = new Wagon(this, i, typen);
            wagons.add(w);
        }
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        return tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.tempAvail = tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.tempAvail = new TimeSlotList(tempAvail);
        for (Wagon wagon : wagons) {
            wagon.setTemporalAvailability(tempAvail);
        }
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (area == null) {
            area = new Area();
            for (Wagon wagon : wagons) {
                area.add(wagon.getGeneralOperatingArea());
            }
        }
        return area;
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        return true;
    }

    @Override
    public LoadUnitStorage getSubResource(Area area) {
        ArrayList<StorageLocation> res = new ArrayList<>();
        for (Wagon wagon : this.wagons) {
            for (StorageLocation storageLocation : wagon.getStorageLocations()) {
                Area tmp = new Area(area);
                tmp.intersect(storageLocation.getGeneralOperatingArea());
                if (!tmp.isEmpty()) {
                    res.add(storageLocation);
                }
            }
        }
        return new TrainSubStorage(this, res);
    }

    public void arrangeTrain(PolygonalCurve2d baseLine, double start, Arrangement arrangement) {
        for (Wagon wagon : this.wagons) {
            double length = wagon.getLength();
            Point2d p1 = baseLine.getPoint(start);
            if (arrangement.equals(Arrangement.left)) {
                start += length;
            } else {
                start -= length;
            }
            Point2d p2 = baseLine.getPoint(start);
            AffineTransform wT = GeometryTools.getRotationTransformation(p1, p2);
            wT.concatenate(AffineTransform.getTranslateInstance(0, -wagon.getWidth() / 2.));
            wagon.setTransform(wT);
        }
        System.out.println(this.toString() + this.getPosition());
    }

    public void setTrack(RailroadTrack track, double start, Arrangement arrangement) {
        this.arrangeTrain(track.getBaseLine(), arrangement == Arrangement.left ? start : start + track.getBaseLine().getLength(), arrangement);
        this.setTrack(track);
    }

    public void setTrack(RailroadTrack track) {
        this.track = track;
    }

    public RailroadTrack getTrack() {
        return track;
    }

    public ArrayList<Wagon> getWagons() {
        return wagons;
    }

    @Override
    public double getMinLocLen() {
        minLocLen = Double.POSITIVE_INFINITY;
        for (Wagon wagon : this.wagons) {
            if (wagon.getMinLocLen() < minLocLen) {
                minLocLen = wagon.getMinLocLen();
            }
        }
        return minLocLen;
    }

    @Override
    public FieldElement getDemand(LoadUnit loadUnit) {
        int need = (int) Math.ceil((loadUnit.getLength() + 2 * loadUnit.getLongitudinalDistance()) / this.getMinLocLen());
        return new DoubleValue(need);
    }

    public static Train getTrain(LoadUnitStorage typeTrain) {
        LoadUnitStorage result = typeTrain;
        while (!(result instanceof Train)) {
            if (result instanceof SubResource) {
                SubResource sr = (SubResource) result;
                result = (LoadUnitStorage) sr.getSuperResource();
            } else {
                return null;
            }
        }
        Train t = (Train) result;
        return t;
    }

    @Override
    public int getNumberOfStorageLocations() {
        int count = 0;
        for (Wagon wagon : wagons) {
            count += wagon.getNumberOfStorageLocations();
        }
        return count;
    }

    @Override
    public List<Slot> getStorageLocations() {
        ArrayList<Slot> locs = new ArrayList<>();
        for (Wagon wagon : wagons) {
            locs.addAll(wagon.getStorageLocations());
        }
        return locs;
    }

    @Override
    public LocationBasedStorage getSection(LocationBasedStorage storage) {
        ArrayList<Slot> res = new ArrayList<>();
        Collection<Slot> storageLocations = this.getStorageLocations();
        for (StorageLocation slot : storage.getStorageLocations()) {
            if (storageLocations.contains(slot)) {
                res.add((Slot) slot);
            }
        }
        return new TrainSubStorage(this, res);

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.wagons);
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
        final Train other = (Train) obj;
        if (!Objects.equals(this.wagons, other.wagons)) {
            return false;
        }
        return true;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public ArrayList<SimpleStorageRow> getRows() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "Train{" + "tempAvail=" + tempAvail + ", number=" + number + '}';
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, Wagon.wagonLoadingHeight);
    }

    @Override
    public String getID() {
        return "Train " + number;

    }

}
