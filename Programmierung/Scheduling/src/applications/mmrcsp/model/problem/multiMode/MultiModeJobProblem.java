/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.problem.multiMode;

import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.modes.JobOperation;
import applications.mmrcsp.model.problem.DefaultSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author bode
 * @param <E>
 */
public class MultiModeJobProblem<E extends JobOperation, F extends MultiModeJob<E>> extends DefaultSchedulingProblem<E> {

    private List<F> jobs;
    protected JobOnNodeDiagramm<F> jobOnNodeDiagramm;

    public MultiModeJobProblem(TimeSlot optimizationTimeSlot, List<F> jobs, Collection<? extends Resource> resources, ScheduleManagerBuilder builder) {
        super(optimizationTimeSlot, new ArrayList<E>(), resources, builder);
        this.setJobs(jobs);
    }

    public MultiModeJobProblem(TimeSlot optimizationTimeSlot, List<F> jobs, Collection<? extends Resource> resources, ScheduleManagerBuilder builder, JobOnNodeDiagramm<F> jobOnNodeDiagramm) {
        super(optimizationTimeSlot, new ArrayList<E>(), resources, builder);
        this.setJobs(jobs);
        this.setJobOnNodeDiagramm(jobOnNodeDiagramm);
        ActivityOnNodeBuilder.<E>build(jobOnNodeDiagramm);
    }

    public MultiModeJobProblem(TimeSlot optimizationTimeSlot, List<F> jobs, Collection<? extends Resource> resources) {
        super(optimizationTimeSlot, new ArrayList<E>(), resources);
        this.setJobs(jobs);
    }

    public void setJobs(List<F> jobs) {
        this.jobs = jobs;
        for (MultiModeJob<E> job : jobs) {
            for (JobOperationList<E> routing : job.getRoutings()) {
                for (E operation : routing) {
                    this.getOperations().add(operation);
                }
            }
        }
    }

    public void remove(F job) {

        this.jobs.remove(job);
        if (this.jobOnNodeDiagramm != null) {
            this.jobOnNodeDiagramm.removeVertex(job);
        }
        for (JobOperationList<E> routing : job.getRoutings()) {
            for (E operation : routing) {
                this.getOperations().remove(operation);
            }
        }

    }

    public void setJobOnNodeDiagramm(JobOnNodeDiagramm<F> jobOnNodeDiagramm) {
        this.jobOnNodeDiagramm = jobOnNodeDiagramm;
    }

    public JobOnNodeDiagramm<? extends MultiModeJob<E>> getJobOnNodeDiagramm() {
        return jobOnNodeDiagramm;
    }

    public List<F> getJobs() {
        return jobs;
    }

}
