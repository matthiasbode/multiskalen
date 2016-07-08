/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.fuzzyBlackBox;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import fuzzy.number.FuzzyNumber;
import math.FieldElement;

/**
 *
 * @author bode
 */
public interface TransportationTimeCalculator<C extends ConveyanceSystem> {

    public FieldElement getTransportationTime(C c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit loadUnit);
}
