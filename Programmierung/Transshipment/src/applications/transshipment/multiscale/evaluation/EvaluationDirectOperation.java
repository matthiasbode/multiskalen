/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.evaluation;

import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import applications.transshipment.multiscale.model.MicroProblem;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.subList.ListIndividual;
import java.util.HashSet;
import java.util.List;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class EvaluationDirectOperation implements LoadUnitFitnessEvalationFunction<ListIndividual<RoutingTransportOperation>> {

    private final MicroProblem problem;
    private final Transshipment_ActivityListScheduleScheme scheduleScheme;
    private final PriorityDeterminator priorityDeterminator;
    private LoadUnitJobSchedule initialSchedule;

    public EvaluationDirectOperation(MicroProblem problem, Transshipment_ActivityListScheduleScheme scheduleScheme, LoadUnitJobSchedule initialSchedule, PriorityDeterminator priorityDeterminator) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.priorityDeterminator = priorityDeterminator;
        this.initialSchedule = initialSchedule;
    }

    @Override
    public double[] computeFitness(ListIndividual<RoutingTransportOperation> ind) {
        LoadUnitJobSchedule schedule = getSchedule(ind);
//
        HashSet<RoutingTransportOperation> notScheduledOperationsInCurrentTimeSlot = new HashSet<>(problem.getOperations());
        notScheduledOperationsInCurrentTimeSlot.removeAll(schedule.getScheduledRoutingTransportOperations());
//
//        double ruestDistance = CraneAnalysis.getMittlereRuestFahrtDistanceCrane(schedule, problem);
//
//        return new double[]{-notScheduledOperationsInCurrentTimeSlot.size(),-ruestDistance};
        int dnf = schedule.getDnfJobs().size();
//        HashSet<RoutingTransportOperation> notScheduledOperationsInCurrentTimeSlot = new HashSet<>(problem.getOperations());
//        notScheduledOperationsInCurrentTimeSlot.removeAll(schedule.getScheduledRoutingTransportOperations());
//
//        double ruestDistance = CraneAnalysis.getMittlereRuestFahrtDistanceCrane(schedule, problem);

        FieldElement e = schedule.getLastScheduleEventTime();
        return new double[]{-dnf, -notScheduledOperationsInCurrentTimeSlot.size(), -e.longValue()};
    }

    @Override
    public LoadUnitJobSchedule getSchedule(ListIndividual<RoutingTransportOperation> ind) {
        List<RoutingTransportOperation> activityList = priorityDeterminator.getPriorites(problem.getActivityOnNodeDiagramm(), ind);
        LoadUnitJobSchedule schedule;
        if (initialSchedule == null) {
            schedule = MultiJobTerminalProblemFactory.createNewSchedule(problem);
        } else {
            InstanceHandler handler = new InstanceHandler(problem.getScheduleManagerBuilder());
            schedule = new LoadUnitJobSchedule(initialSchedule, handler);
        }
        schedule = scheduleScheme.getSchedule(schedule, activityList, problem.getEalosaes(), problem, problem.getActivityOnNodeDiagramm(), problem.getOptimizationTimeSlot());

        return schedule;
    }
}
