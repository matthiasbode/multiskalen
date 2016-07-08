/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import applications.mmrcsp.model.resources.SubResource;
import applications.mmrcsp.model.resources.SuperResource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.DefaultLoadUnitResource;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.loadunits.LoadUnitTypen;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.DoubleValue;
import math.FieldElement;
import util.GeometryTools;

/**
 *
 * @author bode
 */
public class Wagon extends DefaultLoadUnitResource implements LoadUnitStorage, SubResource, SuperResource, SuperTrainType {

    public final static double StandardLaenge = 20.0; // alt = Wagen.StandardLaenge;
    public final static double StandardBreite = 2.54;
    public final static double StandardLoadingSpace = StandardLaenge - 1.5;
    public final static double StandardAxisDistance = StandardLoadingSpace - 0.6;
    public final static double wagonLoadingHeight = 1.2;
    private Train train;
    protected Rectangle2D rect;           //Flaeche des Wagen
    private AffineTransform transform = new AffineTransform();
    protected double total_length;
    protected double total_width;
    protected double loading_space;     //Verfügbare Ladeflaeche
    protected double axis_distance;     //Abstand zwischen den Achsen des Wagons
    protected double overhang;
    protected ArrayList<Slot> slots;    //SubResourc in Reihenfolge von anfang bis ende Wagen
    protected static int counter = 0;
    protected final int number;
    protected final String ID;
    private Double minLocLen = null;

    public Wagon(Train train, int anzSlots, int posOnTrain) {
        this(train, Wagon.StandardLaenge, Wagon.StandardBreite, Wagon.StandardLoadingSpace, Wagon.StandardAxisDistance, anzSlots, new Point2d(Wagon.StandardLaenge / 2. * (posOnTrain + posOnTrain - 1), Wagon.StandardBreite / 2.));
    }

    public Wagon(Train train, int posOnTrain, LoadUnitTypen... typen) {
        this(train, Wagon.StandardLaenge, Wagon.StandardBreite, Wagon.StandardLoadingSpace, Wagon.StandardAxisDistance, new Point2d(Wagon.StandardLaenge / 2. * (posOnTrain + posOnTrain - 1), Wagon.StandardBreite / 2.), typen);
    }

    /**
     *
     * @param train
     * @param length
     * @param width
     * @param loadingspace - Laenge der beladbaren Flaeche
     * @param axisdistance - Abstand zwischen den Wagenachsen
     * @param anzSlot - Anzahl der Plaetze fuer LoadUnit
     * @param center - Mittelpunkt der Flaeche des Wagens
     */
    public Wagon(Train train, double length, double width, double loadingspace, double axisdistance, int anzSlot, Point2d center) {

        if (loadingspace > length) {
            throw new UnknownError("Ladelaenge darf nicht groesser als Laenge (ueber Puffer) sein.");
        }

        this.train = train;
        this.total_length = length;
        this.total_width = width;
        this.loading_space = loadingspace;
        this.axis_distance = axisdistance;
        this.overhang = (total_length - loading_space) / 2.;
        this.rect = new Rectangle2D.Double(0, 0, total_length, total_width);
        number = counter++;
        this.transform.translate(center.getX() - total_length / 2., center.getY() - total_width / 2.);
        ID = getClass().getName() + "-" + number;
        slots = new ArrayList<>(anzSlot);
        createSlots(anzSlot);
        setTemporalAvailability(train.getTemporalAvailability());
    }

    public Wagon(Train train, double length, double width, double loadingspace, double axisdistance, Point2d center, LoadUnitTypen... typen) {

        if (loadingspace > length) {
            throw new UnknownError("Ladelaenge darf nicht groesser als Laenge (ueber Puffer) sein.");
        }

        this.train = train;
        this.total_length = length;
        this.total_width = width;
        this.loading_space = loadingspace;
        this.axis_distance = axisdistance;
        this.overhang = (total_length - loading_space) / 2.;
        this.rect = new Rectangle2D.Double(0, 0, total_length, total_width);
        number = counter++;
        this.transform.translate(center.getX() - total_length / 2., center.getY() - total_width / 2.);
        ID = getClass().getName() + "-" + number;
        slots = new ArrayList<>();
        createSlots(typen);
        setTemporalAvailability(train.getTemporalAvailability());
    }

    protected void createSlots(int anz) {
        if (anz != 0) {
            double breite = total_width - 0.04;
            double laenge = loading_space / ((double) anz);
            for (int i = 0; i < anz; i++) {
                Rectangle2D rec = new Rectangle2D.Double(0, 0, laenge, breite);
                AffineTransform at = AffineTransform.getTranslateInstance(overhang + i * laenge, 0);
                at.preConcatenate(transform);
                slots.add(new Slot(this, rec, at));
            }
        }
    }

    protected void createSlots(LoadUnitTypen... typen) {

        double gap = 0.04;
        double breite = total_width - gap;
        double currentX = overhang;

        for (LoadUnitTypen typ : typen) {
            double length = typ.length + 0.1;
            Rectangle2D rec = new Rectangle2D.Double(0, 0, length, breite);
            AffineTransform at = AffineTransform.getTranslateInstance(currentX, gap / 2.);
            currentX += length;
            at.preConcatenate(transform);
            Slot slot = new Slot(this, rec, at);
            slots.add(slot);
        }
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        return true;
    }

    @Override
    public Area getGeneralOperatingArea() {
        Area area = new Area(rect);
        area.transform(transform);
        return area;
    }

    @Override
    public Train getSuperResource() {
        return train;
    }

    @Override
    public LoadUnitStorage getSubResource(Area area) {
        ArrayList<StorageLocation> res = new ArrayList<>();
        for (StorageLocation storageLocation : this.slots) {
            Area tmp = new Area(area);
            tmp.intersect(storageLocation.getGeneralOperatingArea());
            if (!tmp.isEmpty()) {
                res.add(storageLocation);
            }
        }
        return new TrainSubStorage(this, res);
    }

    public double getWidth() {
        return this.rect.getHeight();
    }

    public double getLength() {
        return this.rect.getWidth();
    }

    public void setTransform(AffineTransform wT) {
        AffineTransform old = this.transform;
        this.transform = wT;
        
        for (Slot slot : slots) {
            
            AffineTransform slotTransform = slot.getTransformLocalToGlobal();
            try {
                slotTransform.preConcatenate(old.createInverse());
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(Wagon.class.getName()).log(Level.SEVERE, null, ex);
            }
            slotTransform.preConcatenate(wT);
            
            slot.setTransformLocalToGlobal(slotTransform);
        }

    }

    @Override
    public int getNumberOfStorageLocations() {
        return this.slots.size();
    }

    @Override
    public List<Slot> getStorageLocations() {
        return this.slots;
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
    public boolean equals(Object obj) {
        if (obj instanceof Wagon) {
            Wagon wagon = (Wagon) obj;
            return wagon.slots.size() == this.slots.size() && slots.containsAll(wagon.slots);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.slots);
        return hash;
    }

    @Override
    public double getMinLocLen() {
        minLocLen = Double.POSITIVE_INFINITY;
        for (Slot slot : this.slots) {
            if (slot.getLength() < minLocLen) {
                minLocLen = slot.getLength();
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
    public void setTemporalAvailability(TimeSlot tempAvail) {
        super.setTemporalAvailability(tempAvail); //To change body of generated methods, choose Tools | Templates.
        for (Slot slot : slots) {
            slot.setTemporalAvailability(tempAvail);
        }
    }

    @Override
    public ArrayList<SimpleStorageRow> getRows() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "Wagon{" + number + ", train=" + train.getNumber() + '}';
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, Wagon.wagonLoadingHeight/2.);
    }

    @Override
    public String getID() {
        return "Wagon " + number;

    }

}
