/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.problem;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import java.util.Collection;

/**
 *
 * @author bode
 * @param <E>
 */
public interface SchedulingProblem<E extends Operation> {

    public  TimeSlot getOptimizationTimeSlot();
    
    public Collection<? extends Resource> getResources();

    public Collection<E> getOperations();

    public ActivityOnNodeGraph<E> getActivityOnNodeDiagramm();

    public void setActivityOnNodeDiagramm(ActivityOnNodeGraph<E> activityOnNodeDiagramm);

    public void setResources(Collection<Resource> resources);

    public void setOperations(Collection<E> operations);

    public ScheduleManagerBuilder getScheduleManagerBuilder();

    public void setRuleBuilder(ScheduleManagerBuilder ruleBuilder);
    
    public void setTemporalAvailability(TimeSlot slot);
}
