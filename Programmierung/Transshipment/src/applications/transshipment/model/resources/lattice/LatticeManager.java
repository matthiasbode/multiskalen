/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.lattice;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.mmrcsp.model.schedule.rules.SharedResourceManager;
import java.awt.geom.Area;
import java.util.ArrayList;

/**
 *
 * @author bode
 * @param <E>
 */
public class LatticeManager<E extends SharedResource> implements SharedResourceManager<E> {

    public static int nx = 40;
    private Structured2DGrid grid;
    private E resource;

    public LatticeManager(E resource) {
        /**
         * Lediglich Ausbreitung von 1 in y-Richtung
         */
        this.setGrid(resource.getGeneralOperatingArea(), nx, 1);
        this.setTemporalAvailability(resource.getTemporalAvailability());
    }

    public void setGrid(Area a, int nx, int ny) {
        this.grid = new Structured2DGrid(a.getBounds2D(), nx, ny);
    }

    public Structured2DGrid getGrid() {
        return grid;
    }

    public ArrayList<CellResource2D> getCells(javax.vecmath.Point2d from, javax.vecmath.Point2d to) {
        return grid.getCells(from, to);
    }

    public void setTemporalAvailability(TimeSlotList tempAvail) {
        for (CellResource2D cellResource2D : grid.getCells()) {
            cellResource2D.setTemporalAvailability(tempAvail);
        }
    }

    public void setTemporalAvailability(TimeSlot tempAvail) {
        for (CellResource2D cellResource2D : grid.getCells()) {
            cellResource2D.setTemporalAvailability(tempAvail);
        }
    }

    public E getResource() {
        return resource;
    }

}
