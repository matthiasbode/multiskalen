/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs;

import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.basics.TimeSlot;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class MinAvailabilityComparator<J extends MultiModeJob> implements Comparator<J> {

    public HashMap<J, FieldElement> maxDurations = new HashMap<>();

    public MinAvailabilityComparator(Collection<J> jobs, Map<J, TimeSlot> timeWindows) {
        for (J loadUnitJob : jobs) {
            FieldElement endTimeWindow = timeWindows.get(loadUnitJob).getUntilWhen();
            FieldElement startTimeWindow = timeWindows.get(loadUnitJob).getFromWhen();
            FieldElement diff = endTimeWindow.sub(startTimeWindow);
            maxDurations.put(loadUnitJob, diff);
        }
    }

    @Override
    public int compare(J o1, J o2) {
        if (maxDurations.get(o1).isGreaterThan(maxDurations.get(o2))) {
            return -1;
        }
        if (maxDurations.get(o1).isLowerThan(maxDurations.get(o2))) {
            return 1;
        } else {
            return o1.toString().compareTo(o2.toString());
        }
    }
}
