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
 * @author Bode
 * @param <E>
 */
public class DefaultSchedulingProblem<E extends Operation> implements SchedulingProblem<E> {

    private TimeSlot optimizationTimeSlot;

    private Collection<E> operations;
    private Collection<? extends Resource> resources;
    private ScheduleManagerBuilder ruleBuilder;
    private ActivityOnNodeGraph<E> activityOnNodeDiagramm;

    public DefaultSchedulingProblem(TimeSlot optimizationTimeSlot, Collection<E> operations, Collection<? extends Resource> resources, ActivityOnNodeGraph<E> activityOnNodeDiagramm) {
        this.operations = operations;
        this.resources = resources;
        this.activityOnNodeDiagramm = activityOnNodeDiagramm;
        this.optimizationTimeSlot = optimizationTimeSlot;
    }

    public DefaultSchedulingProblem(TimeSlot optimizationTimeSlot, Collection<E> operations, Collection<? extends Resource> resources) {
        this.operations = operations;
        this.resources = resources;
        this.optimizationTimeSlot = optimizationTimeSlot;
    }

    public DefaultSchedulingProblem(TimeSlot optimizationTimeSlot, Collection<E> operations, Collection<? extends Resource> resources, ScheduleManagerBuilder ruleBuilder) {
        this.operations = operations;
        this.resources = resources;
        this.ruleBuilder = ruleBuilder;
        this.optimizationTimeSlot = optimizationTimeSlot;
    }

    public DefaultSchedulingProblem(TimeSlot optimizationTimeSlot, Collection<E> operations, Collection<? extends Resource> resources, ScheduleManagerBuilder ruleBuilder, ActivityOnNodeGraph<E> activityOnNodeDiagramm) {
        this.operations = operations;
        this.resources = resources;
        this.ruleBuilder = ruleBuilder;
        this.activityOnNodeDiagramm = activityOnNodeDiagramm;
        this.optimizationTimeSlot = optimizationTimeSlot;
    }

    public Collection<? extends Resource> getResources() {
        return resources;
    }

    public Collection<E> getOperations() {
        return operations;
    }

    public ActivityOnNodeGraph<E> getActivityOnNodeDiagramm() {
        return activityOnNodeDiagramm;
    }

    public void setActivityOnNodeDiagramm(ActivityOnNodeGraph<E> activityOnNodeDiagramm) {
        this.activityOnNodeDiagramm = activityOnNodeDiagramm;
//        activityOnNodeDiagramm.calculateComponentsAndNodeClasses();
    }

    public void setResources(Collection<Resource> resources) {
        this.resources = resources;
    }

    public void setOperations(Collection<E> operations) {
        this.operations = operations;
    }

    public ScheduleManagerBuilder getScheduleManagerBuilder() {
        return ruleBuilder;
    }

    @Override
    public void setRuleBuilder(ScheduleManagerBuilder ruleBuilder) {
        this.ruleBuilder = ruleBuilder;
    }

    @Override
    public void setTemporalAvailability(TimeSlot slot) {
        for (Resource resource : resources) {
            resource.setTemporalAvailability(slot);
        }
    }

    public TimeSlot getOptimizationTimeSlot() {
        return optimizationTimeSlot.clone();
    }
    
    
}
