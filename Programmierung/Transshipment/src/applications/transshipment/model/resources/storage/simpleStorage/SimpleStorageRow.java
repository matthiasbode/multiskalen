/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage.simpleStorage;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.SubResource;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.DefaultLoadUnitResource;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import static applications.transshipment.model.resources.conveyanceSystems.lcs.HandoverPoint.PREFIX;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSHandover;
import applications.transshipment.model.structs.Slot;

import bijava.geometry.dim2.PolygonalCurve2d;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import math.DoubleValue;
import math.FieldElement;
import util.GeometryTools;

/**
 *
 * @author bode
 */
public class SimpleStorageRow extends DefaultLoadUnitResource implements LoadUnitStorage, LocationBasedStorage {

    private final int number;
    private static int count = 0;
    private static final double defaultLength = 3.1;
    private String ID;
    public static final String PREFIX = "SimpleStorageRow";
    /*
     * Minimale Breite der Ladeeinheit
     */
    protected double minLocWidth;
    /*
     * Minimale Länge der Ladeeinheit
     */
    protected double minLocLen;
    /*
     * Map, die zu der Postion in natürlichen Koordinaten die 
     * StorageLocation verwaltet
     */
    public TreeMap<Double, StorageLocation> locs = new TreeMap<Double, StorageLocation>();
    /**
     * Grundfläche des Lagers
     */
    private Area operatingArea;

    public SimpleStorageRow(String name, double len, double width, Point2d... points) {
        this(name, len, width, false, points);
    }

    public SimpleStorageRow(SimpleStorageRow row) {
        this.number = count++;
        this.locs = row.locs;
        this.minLocLen = row.minLocLen;
        this.minLocWidth = row.minLocWidth;
        this.operatingArea = row.operatingArea;
        makeID(PREFIX);
    }

    public SimpleStorageRow(ArrayList<? extends StorageLocation> locations) {
        this.number = count++;

        if (locations.size() != 1) {
            bijava.geometry.dim2.Point2d[] bijavaPoints = new bijava.geometry.dim2.Point2d[locations.size()];
            for (int i = 0; i < locations.size(); i++) {
                StorageLocation sl = locations.get(i);
                Point2d point2d = new bijava.geometry.dim2.Point2d(sl.getCenterOfGeneralOperatingArea().x, sl.getCenterOfGeneralOperatingArea().y);
                bijavaPoints[i] = new bijava.geometry.dim2.Point2d(point2d.x, point2d.y);
            }
            PolygonalCurve2d baseLine = new PolygonalCurve2d(bijavaPoints);

            double tmpminLocLen = Double.POSITIVE_INFINITY;
            for (StorageLocation loc : locations) {
                if (loc.getLength() < tmpminLocLen) {
                    tmpminLocLen = loc.getLength();
                }
            }

            minLocLen = tmpminLocLen;

            minLocWidth = Double.POSITIVE_INFINITY;
            for (StorageLocation loc : locations) {
                if (loc.getWidth() < minLocWidth) {
                    minLocWidth = loc.getWidth();
                }
            }

            this.locs = new TreeMap<>();

            for (int i = 0; i < baseLine.size(); i++) {
                StorageLocation storageLocation = locations.get(i);
                double s = baseLine.getLengthToPoint(i);
                locs.put(s, storageLocation);
            }
        } else {
            StorageLocation sl = locations.get(0);
            this.locs = new TreeMap<>();
            locs.put(sl.getCenterOfGeneralOperatingArea().x, sl);
            minLocLen = sl.getLength();
            minLocWidth = sl.getWidth();
        }
        makeID(PREFIX);
    }

    public SimpleStorageRow(double len, double width, boolean onlyAtPoints, Point2d... points) {
        this(null, len, width, onlyAtPoints, points);
    }

    /**
     * Eine Kurve kann angegeben werden, entlang der so viele Locations wie
     * möglich platziert werden. TODO: FRAGE: klappt es eine Box über 2 Stolos
     * zu stellen?
     *
     * @param curve
     * @param len
     */
    public SimpleStorageRow(String name, double len, double width, boolean onlyAtPoints, Point2d... points) {
        this.number = count++;
        if (name != null) {
            this.ID = name;
        } else {
            this.ID = PREFIX + "-" + getNumber();
        }

        bijava.geometry.dim2.Point2d[] bijavaPoints = new bijava.geometry.dim2.Point2d[points.length];
        for (int i = 0; i < points.length; i++) {
            Point2d point2d = points[i];
            bijavaPoints[i] = new bijava.geometry.dim2.Point2d(point2d.x, point2d.y);
        }
        PolygonalCurve2d baseLine = new PolygonalCurve2d(bijavaPoints);

        minLocLen = len;
        minLocWidth = width;
        locs = new TreeMap<>();

        if (onlyAtPoints) {
            for (int i = 0; i < baseLine.size(); i++) {
                AffineTransform wT = null;
                if (i < baseLine.size() - 2) {
                    Point2d p1 = baseLine.getPoints()[i];
                    Point2d p2 = baseLine.getPoints()[i + 1];
                    wT = GeometryTools.getRotationTransformation(p1, p2);
                } else {

                    Point2d p1 = baseLine.getPoints()[i];
                    Point2d ptemp = baseLine.getPoints()[i - 1];
                    Vector2d vec = new Vector2d(p1);
                    vec.sub(ptemp);
                    Point2d p2 = new Point2d(p1);
                    p2.add(vec);
                    wT = GeometryTools.getRotationTransformation(p1, p2);
                }
                wT.concatenate(AffineTransform.getTranslateInstance(0, -minLocWidth / 2.));
                Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, minLocLen, minLocWidth);
                StorageLocation storageLocation = new StorageLocation(this, rect, wT);
                double s = baseLine.getLengthToPoint(i);
                locs.put(s, storageLocation);
            }
        } else {
            double s = 0;
            while (s < baseLine.getLength()) {
                Point2d p1 = baseLine.getPoint(s);
                double oldS = s;
                s += minLocLen;
                Point2d p2 = baseLine.getPoint(s);
                if (p2 == null) {
                    break;
                }
                AffineTransform wT = GeometryTools.getRotationTransformation(p1, p2);
                wT.concatenate(AffineTransform.getTranslateInstance(0, -minLocWidth / 2.));
                Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, minLocLen, minLocWidth);
                StorageLocation storageLocation = new StorageLocation(this, rect, wT);
                locs.put(oldS, storageLocation);
            }
        }
    }

    public SimpleStorageRow() {
        super();
        this.number = count++;
        this.ID = PREFIX + "-" + getNumber();
    }

    public SimpleStorageRow(Rectangle2D area) {
        this(null, defaultLength, area.getHeight(), new Point2d(area.getMinX(), area.getCenterY()), new Point2d(area.getMaxX(), area.getCenterY()));
    }

    public SimpleStorageRow(Rectangle2D area, double len) {
        this(null, len, area.getHeight(), new Point2d(area.getMinX(), area.getCenterY()), new Point2d(area.getMaxX(), area.getCenterY()));
    }

    public SimpleStorageRow(String name, Rectangle2D area, double len) {
        this(name, len, area.getHeight(), new Point2d(area.getMinX(), area.getCenterY()), new Point2d(area.getMaxX(), area.getCenterY()));
    }

    public SimpleStorageRow(TreeMap<Double, StorageLocation> locs, Point2d... points) {
        this.number = count++;
        this.locs = locs;
        this.ID = PREFIX + "-" + getNumber();
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        if (this.getStorageLocations().iterator().next() instanceof Slot) {
            if (!(minLocWidth >= loadunit.getWidth())) {
                System.err.println("Breite zu klein: " + minLocWidth + " SDSrow: {" + this + "}");
                return false;
            }

            return true;
        }
        if (!(minLocWidth >= loadunit.getWidth() + 2 * loadunit.getTransversalDistance())) {
            System.err.println("Breite zu klein: " + minLocWidth + " SDSrow: {" + this + "}");
            return false;
        }

        return true;
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (operatingArea == null) {
            operatingArea = new Area();
            for (StorageLocation storageLocation : locs.values()) {
                operatingArea.add(storageLocation.getGeneralOperatingArea());
            }
        }
        return operatingArea;
    }

    @Override
    public SimpleSubStorageRow getSubResource(Area area) {
        ArrayList<StorageLocation> set = new ArrayList<>();
        StorageLocation sl;
        for (StorageLocation loc : locs.values()) {
            sl = loc.getSubResource(area);
            if (sl != null) {
                set.add(sl);
            }
        }
        if (set.isEmpty()) {
            return null;
        }
        SimpleSubStorageRow r = new SimpleSubStorageRow(set, this);
        r.setTemporalAvailability(this.getTemporalAvailability());
        return r;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleStorageRow) {
            SimpleStorageRow sdsr = (SimpleStorageRow) obj;
            return sdsr.locs.size() == locs.size() && locs.values().containsAll(sdsr.locs.values());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.locs != null ? this.locs.hashCode() : 0);
        return hash;
    }

    @Override
    public Collection<StorageLocation> getStorageLocations() {
        return locs.values();
    }

    @Override
    public int getNumberOfStorageLocations() {
        return this.locs.size();
    }

    @Override
    public SimpleSubStorageRow getSection(LocationBasedStorage storage) {
        LinkedHashSet<StorageLocation> result = new LinkedHashSet<>();
        for (StorageLocation storageLocation : storage.getStorageLocations()) {
            if (this.locs.containsValue(storageLocation)) {
                result.add(storageLocation);
            }
        }
        return new SimpleSubStorageRow(result, this);
    }

    @Override
    public double getMinLocLen() {
        return this.minLocLen;
    }

    @Override
    public FieldElement getDemand(LoadUnit loadUnit) {
        int need = (int) Math.ceil((loadUnit.getLength() + 2 * loadUnit.getLongitudinalDistance()) / this.getMinLocLen());
        return new DoubleValue(need);
    }

    @Override
    public ArrayList<SimpleStorageRow> getRows() {
        ArrayList<SimpleStorageRow> res = new ArrayList<>();
        res.add(this);
        return res;
    }

    public static class SimpleSubStorageRow extends SimpleStorageRow implements SubResource {

        public static final String PREFIX = "SimpleSubStorageRow";
        public static final String PREFIXRack = "SubRack";
        private SimpleStorageRow superResource;

        public SimpleSubStorageRow(Collection<? extends StorageLocation> locs, SimpleStorageRow _super) {
            super();

            this.superResource = _super;
            this.minLocWidth = superResource.minLocWidth;
            double tmpminLocLen = Double.POSITIVE_INFINITY;
            for (StorageLocation loc : locs) {
                if (loc.getLength() < tmpminLocLen) {
                    tmpminLocLen = loc.getLength();
                }
            }
            minLocLen = tmpminLocLen;
            if (_super.getTemporalAvailability() != null) {
                this.setTemporalAvailability(_super.getTemporalAvailability());
            }

            if (locs.isEmpty()) {
                throw new IllegalArgumentException("Constructor needs at least one StorageLocation!");
            }

            TreeMap<Double, StorageLocation> lMap = new TreeMap<>();
            for (StorageLocation storageLocation : locs) {
                Double s = null;
                for (Double candidate : _super.locs.keySet()) {
                    if (_super.locs.get(candidate).equals(storageLocation)) {
                        s = candidate;
                        break;
                    }
                }
                lMap.put(s, storageLocation);
            }
            this.locs = lMap;
            if (_super instanceof LCSHandover) {
                makeID(PREFIXRack);
            } else {
                makeID(PREFIX);
            }
        }

        @Override
        public Resource getSuperResource() {
            return this.superResource;
        }
    }

    @Override
    public String toString() {
        return ID;//+ "\t" + this.getCenterOfGeneralOperatingArea();
    }

    public void makeID(String PREFIX) {
        ID = PREFIX + "{";
        if (locs.size() > 6) {
            TreeSet<StorageLocation> slocs = new TreeSet<>(new Comparator<StorageLocation>() {
                @Override
                public int compare(StorageLocation o1, StorageLocation o2) {
                    return Integer.compare(o1.number, o2.number);
                }
            });
            slocs.addAll(locs.values());
            ID += slocs.first().number + " ";
            ID += "... ";
            ID += slocs.last().number + " ";
        } else {
            for (StorageLocation sl : locs.values()) {
                ID += sl.number + " ";
            }
        }
        ID += '}';
    }

    public int getNumber() {
        return number;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        super.setTemporalAvailability(tempAvail); //To change body of generated methods, choose Tools | Templates.
        for (StorageLocation storageLocation : this.getStorageLocations()) {
            storageLocation.setTemporalAvailability(tempAvail);
        }
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        super.setTemporalAvailability(tempAvail); //To change body of generated methods, choose Tools | Templates.
        for (StorageLocation storageLocation : this.getStorageLocations()) {
            storageLocation.setTemporalAvailability(tempAvail);
        }
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, 0);
    }

    public String getID() {
        return ID;
    }

}
