/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.lattice;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.SubResource;
import applications.mmrcsp.model.resources.SuperResource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.vecmath.Point2d;

/**
 * Gitterstruktur für ein LCSystem.
 *
 * @author bode
 */
public abstract class Lattice<E extends CellResource2D> implements SuperResource {

    private TimeSlotList timeAvailList;

    public Lattice() {
    }

    public Lattice(Collection<E> cells) {
        setCells(cells);
    }

    public abstract Collection<E> getCells();

    public abstract void setCells(Collection<E> cells);

    public Area getArea() {
        Area a = new Area();
        for (E cell : getCells()) {
            a.add(cell.getGeneralOperatingArea());
        }
        return a;
    }

    public void addCell(E cell) {
        this.getCells().add(cell);
    }

    public Collection<E> getIntersectingCells(Point2d p) {
        LinkedHashSet<E> res = new LinkedHashSet<>();
        for (E cell : getCells()) {
            if (cell.contains(new Point2D.Double(p.x, p.y))) {
                res.add(cell);
            }
        }
        return res;
    }

    @Override
    public SubResource getSubResource(Area a) {
        LinkedHashSet<E> res = new LinkedHashSet<>();
        for (E cell : getCells()) {
            Area area = new Area(a);
            area.intersect(cell.getGeneralOperatingArea());
            if (!area.isEmpty()) {
                res.add(cell);
            }
        }
        return new SubLattice(this, res);
    }

    @Override
    public TimeSlotList getTemporalAvailability() {
        return timeAvailList.clone();
    }

    @Override
    public void setTemporalAvailability(TimeSlotList tempAvail) {
        this.timeAvailList = tempAvail;
    }

    @Override
    public void setTemporalAvailability(TimeSlot tempAvail) {
        this.timeAvailList = new TimeSlotList(tempAvail);
    }

    @Override
    public String getID() {
        return toString();
    }

    public class SubLattice<E extends CellResource2D> extends Lattice<E> implements SubResource {

        private SuperResource sr;

        public SubLattice(SuperResource sr, Collection<E> cells) {
            super(cells);
            this.sr = sr;
        }
        private Collection<E> cells = new LinkedHashSet<>();

        @Override
        public Collection getCells() {
            return cells;
        }

        @Override
        public void setCells(Collection cells) {
            this.cells = cells;
        }

        @Override
        public SuperResource getSuperResource() {
            return sr;
        }
    }
}
