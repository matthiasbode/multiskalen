/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.macro;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator;
import applications.transshipment.model.resources.conveyanceSystems.fuzzyBlackBox.TransportationTimeCalculator;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;
import math.geometry.ParametricLinearCurve3d;

/**
 *
 * @author bode
 */
public class CraneFuzzyCalculator implements TransportationTimeCalculator<Crane> {

    public static long SetupTime = 41 * 1000L;
    @Override
    public FuzzyNumber getTransportationTime(Crane c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit loadUnit) {
        ParametricLinearCurve3d tm = CraneMotionCalculator.getTransportMotionInLocalCoordinates(c, from, to, loadUnit);
        Long crispDuration = (tm != null) ? tm.getDuration() : 0L;
        crispDuration += SetupTime;
        return FuzzyFactory.createLinearInterval(crispDuration, 10 * 1000);
    }

}
