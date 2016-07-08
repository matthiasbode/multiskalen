/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.PSPLib;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.schedule.Schedule;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.interval.FuzzyInterval;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.subList.ListIndividual;
import java.util.List;
import java.util.Set;
import math.FieldElement;
import math.LongValue;
import util.ScheduleFitnessEvalationFunction;

/**
 *
 * @author bode
 */
public class MinMakeSpanEval implements ScheduleFitnessEvalationFunction<ListIndividual<Operation>> {

    public ScheduleGenerationScheme<Operation, SchedulingProblem<Operation>> sgs;
    public SchedulingProblem<Operation> problem;
    public final PriorityDeterminator determinator;
    public TimeSlot slot;

    public MinMakeSpanEval(ScheduleGenerationScheme<Operation, SchedulingProblem<Operation>> sgs, SchedulingProblem<Operation> problem, PriorityDeterminator determinator, TimeSlot slot) {
        this.sgs = sgs;
        this.problem = problem;
        this.determinator = determinator;
        this.slot = slot;
    }

    @Override
    public double[] computeFitness(ListIndividual<Operation> i) {
        Schedule schedule = getSchedule(i);
        return new double[]{getFitness(schedule)};
    }

    public double getFitness(Schedule schedule) {
        FieldElement lastScheduleTime = schedule.getScheduleEventTimes().get(schedule.getScheduleEventTimes().size() - 1);
        Set<Operation> activeSet = schedule.getActiveSet(lastScheduleTime.longValue());
        FieldElement endTime = lastScheduleTime;
        for (Operation operation : activeSet) {
            FieldElement opEndTime = endTime.add(operation.getDuration());
            opEndTime = new LongValue((long) ((FuzzyNumber) opEndTime).getC2());
            if (opEndTime.isGreaterThan(endTime)) {
                endTime = opEndTime;
            }
        }
        FuzzyInterval endFuzzy = (FuzzyInterval) endTime;
        return -(endFuzzy.getC2() - problem.getOptimizationTimeSlot().getFromWhen().longValue()) / (1000 * 60);
    }

    @Override
    public Schedule getSchedule(ListIndividual<Operation> ind) {
        List<Operation> activityList = determinator.getPriorites(problem.getActivityOnNodeDiagramm(), ind);
        Schedule schedule = sgs.getSchedule(activityList, problem, slot);
        return schedule;
    }

}
