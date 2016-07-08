/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.schedule.rules;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author bode
 */
public class DefaultScheduleRules extends ScheduleManagerBuilder {

    private Map<Resource, Double> capacity;

    public DefaultScheduleRules(Map<Resource, Double> capacity) {
        this.capacity = capacity;

    }

    public DefaultScheduleRules() {
        this.capacity = new LinkedHashMap<>();
    }

    @Override
    public ScheduleRule build(Resource r, InstanceHandler handler) {
        return new DefaultEarliestScheduleRule(r, capacity.get(r));
    }

    public void put(Resource r, Double capacity) {
        this.capacity.put(r, capacity);
    }

    @Override
    public SharedResourceManager build(SharedResource r) {
        throw new UnsupportedOperationException("Kann von der Standardimplementierung nicht umgesetzt werden");
    }

}
