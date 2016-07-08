/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.restrictions;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.restrictions.instances.ResourceRestriction;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import math.FieldElement;
import java.util.Map;
import java.util.NavigableSet;
import math.DoubleValue;

/**
 * Diese Klasse wertet aus, ob alle Restriktionen bezüglich Resourcenbedarf für
 * alle Operationen und Resourcen erfüllt sind.
 *
 * @author bode
 */
public class ResourceRestrictions {

    private LinkedHashMap<Resource, ResourceRestriction> restrictions = new LinkedHashMap<>();

    public ResourceRestrictions(Map<Resource, Double> resources) {
        for (Resource resource : resources.keySet()) {
            ResourceRestriction restriction = new ResourceRestriction(resource, new DoubleValue(resources.get(resource)));
            restrictions.put(resource, restriction);
        }
    }

    public boolean complyRestriction(Schedule s, Resource r, long time) {
        ResourceRestriction restriction = restrictions.get(r);
        Set<Operation> activeSet = s.getActiveSet(time, r);
        ArrayList<FieldElement> x = new ArrayList<>();
        for (Operation operation : activeSet) {
            //TODO: Vielleicht anpassen, unscharfer Bedarf?
            x.add(operation.getDemand(r));
        }
        return restriction.comply(x);
    }

    public boolean complyRestriction(Schedule s, Resource r) {
        NavigableSet<FieldElement> scheduleEventTimes = s.getScheduleEventTimes(r);
        for (FieldElement time : scheduleEventTimes) {
            //TODO: Test, ob das so okay ist
            if (!complyRestriction(s, r, time.longValue())) {
                return false;
            }
        }
        return true;
    }

    public Collection<Resource> getResources() {
        return restrictions.keySet();
    }
}
