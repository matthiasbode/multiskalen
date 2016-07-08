/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.scheduling;

import applications.fuzzy.scheduling.rules.defaultImplementation.DefaultEarliestFuzzyScheduleRule;
import applications.mmrcsp.model.schedule.rules.*;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author bode, brandt
 */
public class DefaultFuzzyScheduleRulesBuilder extends ScheduleManagerBuilder {

    private Map<Resource, Double> capacity;

    public DefaultFuzzyScheduleRulesBuilder(Map<Resource, Double> capacity) {
        this.capacity = capacity;

    }

    public DefaultFuzzyScheduleRulesBuilder() {
        this.capacity = new LinkedHashMap<>();
    }

    @Override
    public ScheduleRule build(Resource r, InstanceHandler handler) {
        // Anpassung für Fuzzy Probleme
        return new DefaultEarliestFuzzyScheduleRule(r, capacity.get(r));
    }

    public void put(Resource r, Double capacity) {
        this.capacity.put(r, capacity);
    }

    @Override
    public SharedResourceManager build(SharedResource r) {
        throw new UnsupportedOperationException("Kann von der Standardimplementierung nicht umgesetzt werden");
    }

}
