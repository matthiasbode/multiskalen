/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.evaluation;

import applications.transshipment.ga.direct.decode.ExplicitModeDecoder;
import applications.transshipment.ga.direct.decode.ExplicitOperationDecoder;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.direct.individuals.DirectSuperIndividual;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import ga.basics.FitnessEvalationFunction;

/**
 *
 * @author bode
 */
public class CraneIdleDistanceMinimization implements FitnessEvalationFunction<DirectSuperIndividual> {

    private final MultiJobTerminalProblem problem;
    private final Transshipment_ActivityListScheduleScheme scheduleScheme;
    private final PriorityDeterminator priorityDeterminator;

    public CraneIdleDistanceMinimization(MultiJobTerminalProblem problem, Transshipment_ActivityListScheduleScheme scheduleScheme, PriorityDeterminator priorityDeterminator) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.priorityDeterminator = priorityDeterminator;

    }

    @Override
    public double[] computeFitness(DirectSuperIndividual ind) {
        ExplicitModeDecoder d = new ExplicitModeDecoder(ind.getModeIndividual(), problem);
        ExplicitOperationDecoder dd = new ExplicitOperationDecoder(priorityDeterminator, ind.getOperationIndividual(), d.getEalosaes(), d.getChosenOperations(), d.getGraph(), problem, scheduleScheme, problem.getOptimizationTimeSlot());
        LoadUnitJobSchedule schedule = dd.getSchedule();
        return new double[]{CraneAnalysis.getMacroCraneIdleDistance(problem, schedule)};
    }

}
