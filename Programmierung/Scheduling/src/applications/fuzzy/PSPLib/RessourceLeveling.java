/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.PSPLib;

import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.scheduling.rules.FuzzyFunctionBasedRule;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.scheduleSchemes.ScheduleGenerationScheme;
import ga.individuals.subList.ListIndividual;
import java.util.List;
import java.util.TreeMap;
import util.ScheduleFitnessEvalationFunction;

/**
 *
 * @author Matthias
 */
public class RessourceLeveling implements ScheduleFitnessEvalationFunction<ListIndividual<Operation>> {

    public ScheduleGenerationScheme<Operation, SchedulingProblem<Operation>> sgs;
    public SchedulingProblem<Operation> problem;
    public final PriorityDeterminator determinator;
    public TimeSlot slot;
    
    public long period = 10*60*1000;

    public RessourceLeveling(ScheduleGenerationScheme<Operation, SchedulingProblem<Operation>> sgs, SchedulingProblem<Operation> problem, PriorityDeterminator determinator, TimeSlot slot) {
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
        double fitness = 0;
        for (Resource resource : schedule.getResources()) {
            ScheduleRule scheduleRule = schedule.getHandler().get(resource);
            FuzzyFunctionBasedRule sfb = (FuzzyFunctionBasedRule) scheduleRule;
            LinearizedFunction1d workloadFunction = sfb.getWorkloadFunction();
            int i = 0;
            double integral = 0;
            TreeMap<Integer, Double> integrals = new TreeMap<>();
            for (long currentStart = slot.getFromWhen().longValue(); currentStart < slot.getUntilWhen().longValue(); currentStart += period) {
                double currentInt = workloadFunction.getIntegral(currentStart, currentStart + period);
                integrals.put(i, currentInt);
                integral += currentInt;
                i++;
            }

            double sum = 0;
            for (Double currentInt : integrals.values()) {
                sum += Math.pow(currentInt - integral, 2);
            }
            fitness += sum;
        }
        return -  fitness / (60*1000*60);
    }

    @Override
    public Schedule getSchedule(ListIndividual<Operation> ind) {
        List<Operation> activityList = determinator.getPriorites(problem.getActivityOnNodeDiagramm(), ind);
        Schedule schedule = sgs.getSchedule(activityList, problem, slot);
        return schedule;
    }
}
