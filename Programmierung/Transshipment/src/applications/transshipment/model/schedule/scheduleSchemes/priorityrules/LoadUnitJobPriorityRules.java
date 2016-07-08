/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.priorityrules;

import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs.MinAvailabilityComparator;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs.LatestStartComparator;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs.MinDistanceComparator;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bode
 */
public class LoadUnitJobPriorityRules {

    protected EnumMap<Identifier, Comparator<LoadUnitJob>> map = new EnumMap<>(Identifier.class);

    public enum Identifier {

        MINDIS,
        MAXAVAIL,
        LST;
    }

    public LoadUnitJobPriorityRules(JobOnNodeDiagramm<LoadUnitJob> jobDiagramm, Map<LoadUnitJob, TimeSlot> timeWindows) {
        map.put(Identifier.MAXAVAIL, new MinAvailabilityComparator<>(jobDiagramm.vertexSet(), timeWindows));
        map.put(Identifier.LST, new LatestStartComparator<>(jobDiagramm.vertexSet(), timeWindows));
        map.put(Identifier.MINDIS, new MinDistanceComparator(jobDiagramm.vertexSet()));
    }

    public Comparator<LoadUnitJob> getMap(Identifier r) {
        return map.get(r);
    }

    public Set<Identifier> getIdentifiers() {
        return map.keySet();
    }

}
