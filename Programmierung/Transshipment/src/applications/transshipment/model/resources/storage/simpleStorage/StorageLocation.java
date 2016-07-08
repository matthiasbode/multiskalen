/*
 * To change this template, choose GeometryTools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage.simpleStorage;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.SubResource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.DoubleValue;
import math.FieldElement;

/**
 * *
 * Diese Klasse beschreibt einen Stellplatz. Ein Stellplatz hat als Kapazität
 * seine Länge, also die Breite des Rechtecks, das den Stellplatz beschreibt.
 *
 * @author bode
 */
public class StorageLocation implements LoadUnitStorage, SubResource {

    private TimeSlotList timeAvailList;
    /**
     * Klassenzaehler. Dieser gibt an, wie viele Objekte dieser Klasse bereits
     * erstellt wurden. Er wird bei der Bestimmung der eindeutigen ID genutzt.
     */
    private static int counter = 0;
    /**
     * Nummer dieses Lagerplatzes. Dies ist der Wert, der in {@link #counter}
     * eingestellt war, als dieses Objekt erzeugt wurde. Diese Zahl ist Teil der
     * eindeutigen ID.
     */
    public final int number;
    /**
     * Die ID wird bei Erstellung eines Objektes dieser Klasse aus dem
     * {@link #PREFIX} und der {@link #number} gebildet.
     */
    protected final String ID;
    /**
     * Die Oberressource dieses Lagerplatzes. Diese wird in der von SubResource
     * vorgeschriebenen Methode {@link #getSuperResource() } zurueckgegeben.
     */
    private LoadUnitStorage _super;
    /**
     * Die Grundflaeche des Lagerplatzes. Muss bei Erstellung uebergeben werden.
     */
    private Rectangle2D rec;
    private Area area;
    /**
     * Transformationsobjekt, welches von lokal auf global transformiert
     */
    private AffineTransform transformLocalToGlobal;
    /**
     * Prefix der eindeutigen ID. Hier: "STOLO"
     */
    public static final String PREFIX = "STOLO";

    /**
     * Erstellt einen neuen Lagerplatz. Das uebergebene LoadUnitStorage wird als
     * SuperResource eingetragen, die uebergebene Flaeche stellt die
     * Grundflaeche des neuen Lagerplatzes da.
     *
     * @param parent SuperResource
     * @param rec Grundflaeche des Lagerplatzes
     * @param transformLocalToGlobal Transformationsmatrix, die die
     * Transformation von lokalen zu globalen Koordinaten angibt.
     */
    public StorageLocation(LoadUnitStorage parent, Rectangle2D area, AffineTransform transformLocalToGlobal) {
        _super = parent;
        this.rec = area;
        this.transformLocalToGlobal = transformLocalToGlobal;
        number = counter++;
        ID = PREFIX + "-" + number;
        Point2D p1 = new Point2D.Double(area.getMinX(), area.getMinY());
        Point2D p2 = new Point2D.Double(area.getMinX(), area.getMaxY());
        Point2D p3 = new Point2D.Double(area.getMaxX(), area.getMaxY());
        Point2D p4 = new Point2D.Double(area.getMaxX(), area.getMinY());
        transformLocalToGlobal.transform(p1, p1);
        transformLocalToGlobal.transform(p2, p2);
        transformLocalToGlobal.transform(p3, p3);
        transformLocalToGlobal.transform(p4, p4);
    }

    public void setSuper(LoadUnitStorage sup) {
        if (_super != null) {
            throw new UnsupportedOperationException("_super darf nicht geaendert werden.");
        }
        _super = sup;
    }

    @Override
    public StorageLocation getSubResource(Area area) {
        return (area.contains(this.getGeneralOperatingArea().getBounds2D())) ? this : null;
//
//        if (GeometryTools.areaContainsArea(this.getGeneralOperatingArea(), area)) {
//            return this;
//        }
//        return null;
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        if (loadunit.getLength() > rec.getWidth()) {
            return false;
        }
        if (loadunit.getWidth() > rec.getHeight()) {
            return false;
        }
        return true;
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (this.area == null) {
            this.area = new Area(rec);
            this.area.transform(transformLocalToGlobal);
        }
        return this.area;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.number;
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
        final StorageLocation other = (StorageLocation) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
        double x = this.getGeneralOperatingArea().getBounds2D().getCenterX();
        double y = this.getGeneralOperatingArea().getBounds2D().getCenterY();
        return new Point2d(x, y);
    }

    public AffineTransform getTransformLocalToGlobal() {
        return new AffineTransform(transformLocalToGlobal);
    }

    public void setTransformLocalToGlobal(AffineTransform transformLocalToGlobal) {
        this.transformLocalToGlobal = transformLocalToGlobal;
        this.area = new Area(rec);
        this.area.transform(transformLocalToGlobal);  
    }

    
    
    @Override
    public TimeSlotList getTemporalAvailability() {
        return this.timeAvailList.clone();
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.timeAvailList = tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.timeAvailList = new TimeSlotList(tempAvail);
    }

    public double getLength() {
        return this.rec.getWidth();
    }

    public double getWidth() {
        return this.rec.getHeight();
    }

    @Override
    public Resource getSuperResource() {
        return _super;
    }

    public String getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "StorageLocation{" + ID + '}';
    }

    public int getNumber() {
        return number;
    }

    @Override
    public FieldElement getDemand(LoadUnit loadUnit) {
        return new DoubleValue(1.0);
    }

    public Rectangle2D getRec() {
        return rec;
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, 0);
    }
}
