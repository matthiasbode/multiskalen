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
import math.FieldElement;
import math.LongValue;
import math.geometry.ParametricLinearCurve3d;

/**
 *
 * @author bode
 */
public class CraneMacroCalculator implements TransportationTimeCalculator<Crane> {

    public static long SetupTime = 45 * 1000L;

    @Override
    public FieldElement getTransportationTime(Crane c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit loadUnit) {
        ParametricLinearCurve3d tm = CraneMotionCalculator.getTransportMotionInLocalCoordinates(c, from, to, loadUnit);
        Long crispDuration = (tm != null) ? tm.getDuration() : 0L;
        crispDuration += SetupTime;
        return new LongValue(crispDuration);
    }

}
