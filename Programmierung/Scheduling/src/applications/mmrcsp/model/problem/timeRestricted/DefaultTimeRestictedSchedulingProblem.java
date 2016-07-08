/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.problem.timeRestricted;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.DefaultSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author bode
 * @param <E>
 */
public class DefaultTimeRestictedSchedulingProblem<E extends Operation> extends DefaultSchedulingProblem<E> implements TimeRestrictedSchedulingProblem<E> {

    
    private Map<E, EarliestAndLatestStartsAndEnds> ealosae;

    public DefaultTimeRestictedSchedulingProblem(TimeSlot optimizationTimeSlot, Collection<E> operations, Collection<? extends Resource> resources, ActivityOnNodeGraph<E> activityOnNodeDiagramm, Map<E, EarliestAndLatestStartsAndEnds> ealosae) {
        super(optimizationTimeSlot,operations, resources, activityOnNodeDiagramm);
        this.ealosae = ealosae;
    }

    public DefaultTimeRestictedSchedulingProblem(TimeSlot optimizationTimeSlot, Collection<E> operations, Collection<? extends Resource> resources, Map<E, EarliestAndLatestStartsAndEnds> ealosae) {
        super(optimizationTimeSlot,operations, resources);
        this.ealosae = ealosae;
    }

    public DefaultTimeRestictedSchedulingProblem(TimeSlot optimizationTimeSlot,Collection<E> operations, Collection<? extends Resource> resources, ScheduleManagerBuilder ruleBuilder, Map<E, EarliestAndLatestStartsAndEnds> ealosae) {
        super(optimizationTimeSlot,operations, resources, ruleBuilder);
        this.ealosae = ealosae;
    }

    public DefaultTimeRestictedSchedulingProblem(TimeSlot optimizationTimeSlot, Map<E, EarliestAndLatestStartsAndEnds> ealosae, Collection<E> operations, Collection<? extends Resource> resources, ScheduleManagerBuilder ruleBuilder, ActivityOnNodeGraph<E> activityOnNodeDiagramm) {
        super(optimizationTimeSlot,operations, resources, ruleBuilder, activityOnNodeDiagramm);
        this.ealosae = ealosae;
    }

    @Override
    public Map<E, EarliestAndLatestStartsAndEnds> getEalosaes() {
        return ealosae;
    }

   
}
