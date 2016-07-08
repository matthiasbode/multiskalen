/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.LoadUnitOperation;
import java.util.ArrayList;
import java.util.HashMap;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class LoadUnitPositions extends ArrayList<LoadUnitOperation> {

    LoadUnit lu;
    public HashMap<LoadUnitOperation, FieldElement> startTimes;

    public LoadUnitPositions(LoadUnit lu) {
        startTimes = new HashMap<>();
        this.lu = lu;
    }

    public void put(FieldElement start, LoadUnitOperation op) {
        if (!this.contains(op)) {
            this.add(op);
        }
        startTimes.put(op, start);
    }

    @Override
    public String toString() {
        String r = "LoadUnitPositions{" + "lu=" + lu + ",\n";
        for (LoadUnitOperation k : startTimes.keySet()){
            r += k + "-->" + TimeSlot.longToFormattedDateString(startTimes.get(k).longValue()) + "\n";
        }
        r += " '}'";
        return r;
    }

}
