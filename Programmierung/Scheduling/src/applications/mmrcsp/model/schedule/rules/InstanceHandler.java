/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.rules;

import applications.mmrcsp.model.basics.TimeSlotList;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import javafx.scene.transform.Scale;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author Matthias
 */
public class InstanceHandler {

    private final ScheduleManagerBuilder builder;
    private final LinkedHashMap<Resource, ScheduleRule> rules = new LinkedHashMap<>();
    private final LinkedHashMap<SharedResource, SharedResourceManager> sharedManager = new LinkedHashMap<>();

 
    public InstanceHandler(ScheduleManagerBuilder builder) {
        this.builder = builder;
    }

     
    

    public ScheduleRule get(Resource r) throws NoSuchElementException {
        ScheduleRule rule = rules.get(r);
        if (rule == null) {
            rule = builder.build(r, this);
            if (rule == null) {
//                System.err.println("Keine Einplanvorschriften für folgende Ressource hinterlegt: " + r);
                return null;
            }
            rules.put(r, rule);
        }
        return rule;
    }

    public SharedResourceManager getSharedManager(SharedResource r) {
        SharedResourceManager manager = sharedManager.get(r);
        if (manager == null) {
            manager = builder.build(r);
            sharedManager.put(r, manager);
        }
        return manager;
    }

    public Set<Resource> getResources() {
        return rules.keySet();
    }

    public FieldElement getStartTimeForResource(Resource r) {
        FieldElement startTimeForResource = builder.getStartTimeForResource(r);
        if (startTimeForResource == null) {
            TimeSlotList temporalAvailability = r.getTemporalAvailability();
            if (!temporalAvailability.isEmpty()) {
                return temporalAvailability.getFromWhen();
            }
            return new LongValue(0);
        }
        return startTimeForResource;
    }
}
