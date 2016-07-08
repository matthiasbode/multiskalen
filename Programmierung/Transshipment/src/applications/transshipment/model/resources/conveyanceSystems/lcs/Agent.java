/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.DefaultLoadUnitResource;
import applications.transshipment.model.resources.conveyanceSystems.SubConveyanceSystem;
import java.awt.geom.Area;

/**
 *
 * @author bode
 */
public class Agent extends DefaultLoadUnitResource implements ConveyanceSystem, SubConveyanceSystem {

    private final int number;
    private static int count = 0;
    private LCSystem lcSystem;
    private Area generalOperatingArea;
    protected double smin, smax;
    protected double width, tiefe, y_min;
    /**
     * maximale Krangeschwindigkeit des Portals (in Laengsrichtung) [m/sec]
     */
    private double vmax = 3.0;
    /**
     * maximale Kranbeschleunigung des Portals (in Laengsrichtung) [m/sec^2]
     */
    private double amax = 0.5;

    /**
     *
     * @param lcSystem
     * @param generalOperatingArea
     */
    public Agent(LCSystem lcSystem, Area generalOperatingArea) {
        this.number = count++;
        this.lcSystem = lcSystem;
        this.generalOperatingArea = generalOperatingArea;
    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        return true;
    }

    @Override
    public Area getGeneralOperatingArea() {
        return generalOperatingArea;
    }

    /**
     * maximale Geschwindigkeit des Agenten [m/sec]
     *
     * @return
     */
    public double getVmax() {
        return vmax;
    }

    public void setVmax(double vmax) {
        this.vmax = vmax;
    }

    /**
     * maximale Beschleunigung des Agenten [m/sec^2]
     *
     * @return
     */
    public double getAmax() {
        return amax;
    }

    public void setAmax(double amax) {
        this.amax = amax;
    }

    public LCSystem getLcSystem() {
        return lcSystem;
    }

    @Override
    public String toString() {
        return "Agent{" + getNumber() + '}';
    }

    @Override
    public String getID() {
        return "Agent{" + getNumber() + '}';
    }

    @Override
    public ConveyanceSystem getSuperConveyanceSystem() {
        return lcSystem;
    }

    public int getNumber() {
        return number;
    }
}
