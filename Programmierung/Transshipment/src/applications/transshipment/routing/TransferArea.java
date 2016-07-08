/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.awt.geom.Area;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.FieldElement;
import util.SimpleLinkedSet;

/**
 *
 * @author bode
 */
public class TransferArea implements LoadUnitStorage {

    public static int counter = 0;
    public final int number;
    public static final TransferArea startTransferArea = new TransferArea();
    public static final TransferArea endTransferArea = new TransferArea();
    /**
     * Die beiden Transportsysteme, welche dieser TransferArea zugeordnet sind.
     *
     */
    private ConveyanceSystem conveyanceSystem1, conveyanceSystem2;
    /**
     * Das dieser TransferArea zugeordnete Lagersystem
     */
    private LoadUnitStorage baseStorage;
    /**
     * Die gemeinsame Flaeche der Lagernden und transportierenden Ressourcen.
     */
    private Area area;
    private TimeSlotList tempAvail;

    /**
     * Erstellt eine neue TransferArea.
     *
     * @param conveyanceSystem1 Erste transportierende Ressource
     * @param conveyanceSystem2 Zweite transportierende Ressource
     * @param storage Lagernde Ressource
     * @param area Gemeinsame Flaeche der Ressourcen
     */
    public TransferArea(ConveyanceSystem conveyanceSystem1, ConveyanceSystem conveyanceSystem2, LoadUnitStorage storage, Area area) {
        if (conveyanceSystem1 == null && conveyanceSystem2 == null) {
            throw new IllegalArgumentException("TransferArea needs at least one ConveyanceSystem");
        }
        this.conveyanceSystem1 = conveyanceSystem1;
        this.conveyanceSystem2 = conveyanceSystem2;
        this.baseStorage = storage;
        this.area = area;
        this.number = counter++;
    }

    public TransferArea(LoadUnitStorage baseStorage) {
        this.baseStorage = baseStorage;
        this.number = counter++;
    }

    /**
     * Erstellt eine neue TransferArea als Kopie von einer bereits bestehenden
     * TransferArea.
     *
     * @param tA Die zu kopierende TransferArea
     */
    public TransferArea(TransferArea tA) {
        // Keine Referenzen sondern neue Objekte.
        this.conveyanceSystem1 = tA.conveyanceSystem1;
        this.conveyanceSystem2 = tA.conveyanceSystem2;
        this.baseStorage = tA.baseStorage;
        this.area = new Area(tA.area);
        this.number = counter++;
    }

    private TransferArea() {
        this.number = counter++;
    }

    /**
     * Gibt das StorageSystem der TransferArea zurueck.
     *
     * @return StorageSystem der TransferArea
     */
    public LoadUnitStorage getStorageSystem() {
        return baseStorage;
    }

    /**
     * Setzt das Storagesystem der TransferArea
     *
     * @param stor Neues StorageSystem
     */
    public void setStorageSystem(LoadUnitStorage stor) {
        this.baseStorage = stor;
    }

    /**
     * Gibt die transportierenden Resourcen der TransferArea zurueck.
     *
     * @return Transportierende Resourcen der TransferArea
     */
    public SimpleLinkedSet<ConveyanceSystem> getConveyanceSystems() {
        SimpleLinkedSet<ConveyanceSystem> cvs = new SimpleLinkedSet<>();
        if (conveyanceSystem1 != null) {
            cvs.add(conveyanceSystem1);
        }
        if (conveyanceSystem2 != null) {
            cvs.add(conveyanceSystem2);
        }
        return cvs;
    }

    @Override
    public String toString() {
        String s = this.number + "\t";
        for (ConveyanceSystem cs : getConveyanceSystems()) {
            s += cs + "|";
        }
        s += this.baseStorage; 
//        s += "|"+this.baseStorage.getGeneralOperatingArea().getBounds2D();
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.number;
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
        final TransferArea other = (TransferArea) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    /**
     * Gibt an, ob die TransferArea das Transportsystem enthaelt.
     *
     * @param res Das zu pruefende Transportsystem
     * @return true, wenn das Transportsystem enthalten ist, sonst false.
     */
    public boolean containsConveyanceSystem(ConveyanceSystem res) {
        if (res == null) {
            return false;
        }
        if (!getConveyanceSystems().contains(res)) {
            return false;
        }
        return true;
    }

    /**
     * Bestimmt, ob alle LoadUnitRessourcen dieser TransferArea mit einer
     * Ladeeinheit umgehen koennen.
     *
     * @param loadunit Die Ladeeinheit, fuer die Ueberprueft werden soll, ob sie
     * verwendet werden kann.
     * @return          <code>true</code>, wenn alle Ressourcen die Ladeeinheit verwenden
     * koennen, sonst <code>false</code>.
     */
    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        for (ConveyanceSystem cvs : getConveyanceSystems()) {
            if (!cvs.canHandleLoadUnit(loadunit)) {
                return false;
            }

        }

        return baseStorage.canHandleLoadUnit(loadunit);

    }

    public ConveyanceSystem getConveyanceSystem1() {
        return conveyanceSystem1;
    }

    public ConveyanceSystem getConveyanceSystem2() {
        return conveyanceSystem2;
    }

    @Override
    public LoadUnitStorage getSubResource(Area area) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (area == null) {
            throw new IllegalArgumentException("Keine Fläche gesetzt");
        }
        return area;
    }

    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
       return baseStorage.getCenterOfGeneralOperatingArea();
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        if (tempAvail == null) {
            tempAvail = new TimeSlotList();
        }
        return tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FieldElement getDemand(LoadUnit lu) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Point3d getPosition() {
        return baseStorage.getPosition();
    }

    @Override
    public String getID() {
        return "TransferArea " + number;

    }
}
