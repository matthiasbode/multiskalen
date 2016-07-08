/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.macro;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.conveyanceSystems.fuzzyBlackBox.TransportationTimeCalculator;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */
public class LCSTransportTimeCalculator implements TransportationTimeCalculator<LCSystem> {

    @Override
    public FuzzyNumber getTransportationTime(LCSystem system, LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit loadUnit) {
        Agent agent = system.getSharingResources().iterator().next();
        Point2d fromEx = origin.getCenterOfGeneralOperatingArea();
        Point2d toEx = destination.getCenterOfGeneralOperatingArea();
        double laenge = Math.abs(fromEx.x - toEx.x) + Math.abs(fromEx.y - toEx.y);
        long dauer = (long) ((laenge / agent.getVmax()) * 1000);
        dauer += 2 * LCSystem.rendezvousTime;
        return FuzzyFactory.createLinearInterval(dauer, 2 * 1000);

    }

}
