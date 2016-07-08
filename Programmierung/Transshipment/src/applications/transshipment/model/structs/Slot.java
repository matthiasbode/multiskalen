/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.structs;

import applications.transshipment.model.loadunits.LoadUnitTypen;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 *
 * @author bode
 */
public class Slot extends StorageLocation implements TrainType {

    /**
     * eine ReservationForLuOperation die besagt, fuer welche LU der Slot ab
     * wann zwecks Endlagerung reserviert ist oder null, falls keine
     * Reservierung vorliegt
     */
    protected ReservationForLuOperation finalStoringReservation;
    LoadUnitTypen typ;

    public Slot(TrainType parent, Rectangle2D area, AffineTransform transformLocalToGlobal) {
        super(parent, area, transformLocalToGlobal);
    }

    public Slot(LoadUnitTypen typ, TrainType parent, Rectangle2D area, AffineTransform transformLocalToGlobal) {
        super(parent, area, transformLocalToGlobal);
        this.typ = typ;
    }

    @Override
    public TrainType getSuperResource() {
        return (TrainType) super.getSuperResource(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
//        Train train = Train.getTrain(this);
//        String t = this.getRec()+"\t"+ this.getTransformLocalToGlobal().toString();// train.getTrack().getID()+"\t" + train.getTrack().getCenterOfGeneralOperatingArea();
        return "Slot{" + getNumber() + "," + this.getCenterOfGeneralOperatingArea()+ "," + getSuperResource().toString() + '}';
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, Wagon.wagonLoadingHeight);
    }

    public Set<ReservationForLuOperation> getFinalStoringReservations() {
        if (finalStoringReservation == null) {
            return null;
        }
        HashSet<ReservationForLuOperation> h = new HashSet<ReservationForLuOperation>();
        h.add(finalStoringReservation);
        return h;
    }

    public boolean setFinalStoringReservation(ReservationForLuOperation r) {
        if (finalStoringReservation != null && ! r.getResource().equals(this.finalStoringReservation.getResource())) {
            throw new UnsupportedOperationException("Schon reserviert!");
        }
        finalStoringReservation = r;
        return true;
    }

    public LoadUnitTypen getTyp() {
        return typ;
    }

}
