/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.macro;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotion3DOverTime;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator;
import static applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator.getMotionInLocalCoordinates;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;
import java.awt.geom.Rectangle2D;
import java.util.TreeSet;
import javax.vecmath.Point3d;
import math.geometry.ParametricLinearCurve3d;

/**
 *
 * @author bode
 */
public class CraneFuzzyCalculatorExtended {

    public static FuzzyNumber getFuzzyTransportationTime(Crane c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit loadUnit) {
        TreeSet<Long> points = new TreeSet<>();

        ParametricLinearCurve3d tm = CraneMotionCalculator.getTransportMotionInLocalCoordinates(c, from, to, loadUnit);
        Long crispDuration = (tm != null) ? tm.getDuration() : 0L;
        return FuzzyFactory.createLinearInterval(crispDuration, 5 * 1000);
        
//
//        double xFrom = from.getPosition().x;
//        double yFrom = from.getPosition().y;
//
//        double xTo = to.getPosition().x;
//        double yTo = to.getPosition().y;
//
//        double vmax_X = c.getVmax_crane();
//        double vmax_Y = c.getVmax_crab();
//
//        Rectangle2D bounds2DFrom = from.getGeneralOperatingArea().getBounds2D();
//        double minXFrom = bounds2DFrom.getMinX();
//        double maxXFrom = bounds2DFrom.getMaxX();
//        double minYFrom = bounds2DFrom.getMinY();
//        double maxYFrom = bounds2DFrom.getMaxY();
//
//        Rectangle2D bounds2DTo = to.getGeneralOperatingArea().getBounds2D();
//        double minXTo = bounds2DTo.getMinX();
//        double maxXTo = bounds2DTo.getMaxX();
//        double minYTo = bounds2DTo.getMinY();
//        double maxYTo = bounds2DTo.getMaxY();
//
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, minYFrom, 0), new Point3d(maxXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, minYFrom, 0), new Point3d(maxXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, minYFrom, 0), new Point3d(minXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, minYFrom, 0), new Point3d(minXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, minYFrom, 0), new Point3d(maxXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, minYFrom, 0), new Point3d(maxXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, minYFrom, 0), new Point3d(minXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, minYFrom, 0), new Point3d(minXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, maxYFrom, 0), new Point3d(maxXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, maxYFrom, 0), new Point3d(maxXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, maxYFrom, 0), new Point3d(minXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(minXFrom, maxYFrom, 0), new Point3d(minXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, maxYFrom, 0), new Point3d(maxXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, maxYFrom, 0), new Point3d(maxXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, maxYFrom, 0), new Point3d(minXTo, minYTo, 0), from, to, loadUnit).getDuration());
//        points.add(getTransportMotionInLocalCoordinates(c, new Point3d(maxXFrom, maxYFrom, 0), new Point3d(minXTo, maxYTo, 0), from, to, loadUnit).getDuration());
//
//        return FuzzyFactory.createLinearInterval(crispDuration, crispDuration, Math.abs(crispDuration-points.first()), Math.abs(crispDuration-points.last()), FuzzyFactory.DEFAULT_RESOLUTION);

    }

}
