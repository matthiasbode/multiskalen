/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.mmrcsp.model.schedule.rules.SharedResourceManager;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * Resource, die von mehreren anderen Ressourcen geteilt wird, also
 * beispielsweise ein CraneRunway oder ein LCSystem.
 *
 * @author bode
 * @param <E>
 */
public class DefaultSharedResource<E extends Resource> implements SharedResource<E> {

    protected ArrayList<E> sharingResources = new ArrayList<>();
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
    private TimeSlotList timeAvailList;
    /**
     * Die Grundflaeche des Lagerplatzes. Muss bei Erstellung uebergeben werden.
     */
    private Area area;

    public DefaultSharedResource(Rectangle2D rec) {
        this.number = counter++;
    }

    public DefaultSharedResource(Area a) {
        this(a.getBounds2D());
    }

    public DefaultSharedResource() {
        this.number = counter++;
    }

    @Override
    public Collection<E> getSharingResources() {
        return sharingResources;
    }

    @Override
    public void addSharingResource(E r) {
        this.sharingResources.add(r);
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        return timeAvailList.clone();
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.timeAvailList = tempAvail;
        for (E e : this.getSharingResources()) {
            e.setTemporalAvailability(this.getTemporalAvailability());
        }
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.timeAvailList = new TimeSlotList(tempAvail);
        for (E e : this.getSharingResources()) {
            e.setTemporalAvailability(this.getTemporalAvailability());
        }
    }

    @Override
    public Area getGeneralOperatingArea() {
        return this.area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
        double x = this.getGeneralOperatingArea().getBounds2D().getCenterX();
        double y = this.getGeneralOperatingArea().getBounds2D().getCenterY();
        return new Point2d(x, y);
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "DefaultSharedResource{" + "number=" + number + '}';
    }

    @Override
    public String getID() {
        return toString();
    }

}
