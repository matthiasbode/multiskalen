/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane;

import applications.mmrcsp.model.resources.Resource;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.DefaultLoadUnitResource;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import bijava.geometry.dim2.Point2d;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

/**
 *
 * @author bode
 */
public class Crane extends DefaultLoadUnitResource implements ConveyanceSystem {

    public final int number;
    private static int count = 0;
    private CraneRunway craneRunway;
    private Area generalOperatingArea;
    public double smin, smax;
    public double width, tiefe, y_min;
    /**
     * hoechste einnehmbare Katzposition [m]
     */
    public double max_zpos_crab = 6.5;
    /**
     * niedrigste einnehmbare Katzposition [m]
     */
    public double min_zpos_crab = 0.;
    /**
     * maximale Krangeschwindigkeit des Portals (in Laengsrichtung) [m/sec]
     */
    public double vmax_crane = 1.67;
    /**
     * maximale Kranbeschleunigung des Portals (in Laengsrichtung) [m/sec^2]
     */
    public double amax_crane = 0.25;
    /**
     * maximale Katzfahrgeschwindigkeit (in Querrichtung) [m/sec]
     */
    public double vmax_crab = 2.0;
    /**
     * maximale Katzfahrbeschleunigung (in Querrichtung) [m/sec^2]
     */
    public double amax_crab = 0.25;
    /**
     * maximale Greifwerkzeuggeschwindigkeit (in Vertikalrichtung) [m/sec]
     */
    public double vmax_z = 0.75;
    /**
     * maximale Greifwerkzeugbeschleunigung (in Vertikalrichtung) [m/sec^2]
     */
    public double amax_z = 0.25;

    /**
     * Zeigt an, ob bei saemtlichen Berechnungen die Beschleunigung
     * beruecksichtigt werden soll, oder nicht
     */
    public boolean regardAcceleration = true;

    long standardPositionierungsAufwandRetrieving = 10000l;
    long standardPositionierungsAufwandStoring = 10000l;

    public Crane(CraneRunway craneRunway, double smin, double smax, double width, double ueberspannWeite) {
        this(craneRunway, smin, smax, width, ueberspannWeite, -ueberspannWeite / 2.);
    }

    public Crane(CraneRunway craneRunway, double smin, double smax, double width, double ueberspannWeite, double yminOffset) {
        this.number = count++;
        this.craneRunway = craneRunway;
        this.smin = smin - width / 2.;
        this.smax = smax + width / 2.;
        this.tiefe = ueberspannWeite;
        this.y_min = yminOffset;
        this.width = width;

    }

    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        return true;
    }

    @Override
    public Area getGeneralOperatingArea() {
        if (generalOperatingArea == null) {
            Point2d[] points = new Point2d[4];
            points[0] = new Point2d(smin + width / 2., y_min);
            points[1] = new Point2d(smax - width / 2., y_min);
            points[2] = new Point2d(smax - width / 2., y_min + tiefe);
            points[3] = new Point2d(smin + width / 2., y_min + tiefe);
            for (int i = 0; i < points.length; i++) {
                points[i] = craneRunway.localToWorld(points[i]);
            }
            GeneralPath gp = new GeneralPath();
            gp.moveTo(points[0].x, points[0].y);
            gp.lineTo(points[1].x, points[1].y);
            gp.lineTo(points[2].x, points[2].y);
            gp.lineTo(points[3].x, points[3].y);
            gp.closePath();
            this.generalOperatingArea = new Area(gp);
            System.out.println("");
        }

        return generalOperatingArea;
    }

    /**
     * maximale Krangeschwindigkeit des Portals (in Laengsrichtung) [m/sec]
     *
     * @return
     */
    public double getVmax_crane() {
        return vmax_crane;
    }

    public void setVmax_crane(double vmax_crane) {
        this.vmax_crane = vmax_crane;
    }

    /**
     * maximale Kranbeschleunigung des Portals (in Laengsrichtung) [m/sec^2]
     *
     * @return
     */
    public double getAmax_crane() {
        return amax_crane;
    }

    public void setAmax_crane(double amax_crane) {
        this.amax_crane = amax_crane;
    }

    /**
     * maximale Katzfahrgeschwindigkeit (in Querrichtung) [m/sec]
     *
     * @return
     */
    public double getVmax_crab() {
        return vmax_crab;
    }

    public void setVmax_crab(double vmax_crab) {
        this.vmax_crab = vmax_crab;
    }

    /**
     * maximale Katzfahrbeschleunigung (in Querrichtung) [m/sec^2]
     *
     * @return
     */
    public double getAmax_crab() {
        return amax_crab;
    }

    public void setAmax_crab(double amax_crab) {
        this.amax_crab = amax_crab;
    }

    /**
     * maximale Greifwerkzeuggeschwindigkeit (in Vertikalrichtung) [m/sec]
     *
     * @return
     */
    public double getVmax_z() {
        return vmax_z;
    }

    public void setVmax_z(double vmax_z) {
        this.vmax_z = vmax_z;
    }

    /**
     * maximale Greifwerkzeugbeschleunigung (in Vertikalrichtung) [m/sec^2]
     *
     * @return
     */
    public double getAmax_z() {
        return amax_z;
    }

    public void setAmax_z(double amax_z) {
        this.amax_z = amax_z;
    }

    /**
     * hoechste einnehmbare Katzposition [m]
     *
     * @return
     */
    public double getMax_zpos_crab() {
        return max_zpos_crab;
    }

    public void setMax_zpos_crab(double max_zpos_crab) {
        this.max_zpos_crab = max_zpos_crab;
    }

    /**
     * niedrigste einnehmbare Katzposition [m]
     *
     * @return
     */
    public double getMin_zpos_crab() {
        return min_zpos_crab;
    }

    public void setMin_zpos_crab(double min_zpos_crab) {
        this.min_zpos_crab = min_zpos_crab;
    }

    public CraneRunway getCraneRunway() {
        return craneRunway;
    }

    @Override
    public String toString() {
        return "Crane{" + getNumber() + '}';
    }

    public int getNumber() {
        return number;
    }

    public double getCrossOverHeight(Resource r) {
        return max_zpos_crab;
    }

    public long getPositionierungsAufwandRetrieving(LoadUnitStorage from, LoadUnitStorage to, LoadUnit lu) {
        return standardPositionierungsAufwandRetrieving;
    }

    public long getPositionierungsAufwandStoring(LoadUnitStorage from, LoadUnitStorage to, LoadUnit lu) {
        return standardPositionierungsAufwandStoring;
    }

    public double getSmin() {
        return smin;
    }

    public double getSmax() {
        return smax;
    }

    @Override
    public String getID() {
        return "Crane{" + getNumber() + '}';
    }

}
