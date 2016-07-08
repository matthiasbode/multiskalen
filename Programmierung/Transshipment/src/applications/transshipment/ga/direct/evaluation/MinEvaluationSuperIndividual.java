/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.evaluation;

import applications.transshipment.ga.direct.decode.ExplicitModeDecoder;
import applications.transshipment.ga.direct.decode.ExplicitOperationDecoder;
import applications.transshipment.ga.direct.individuals.DirectSuperIndividual;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import ga.individuals.IntegerIndividual;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.problem.MultiJobTerminalProblem;

import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import ga.basics.FitnessEvalationFunction;

/**
 *
 * @author bode
 */
public class MinEvaluationSuperIndividual implements LoadUnitFitnessEvalationFunction<DirectSuperIndividual> {

    private final MultiJobTerminalProblem problem;
    private final Transshipment_ActivityListScheduleScheme scheduleScheme;
    private final PriorityDeterminator priorityDeterminator;

    public MinEvaluationSuperIndividual(MultiJobTerminalProblem problem, Transshipment_ActivityListScheduleScheme scheduleScheme, PriorityDeterminator priorityDeterminator) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.priorityDeterminator = priorityDeterminator;
    }

    @Override
    public double[] computeFitness(DirectSuperIndividual ind) {
        LoadUnitJobSchedule schedule = getSchedule(ind);

        /**
         * Anzahl an DNFs
         */
        ind.setDNF(schedule.getDnfJobs().size());
        ind.setIdleCraneDistance(CraneAnalysis.getMacroCraneIdleDistance(problem, schedule));

        double scaledDNF = ind.getDNF();
        double scaledCraneIdle = ind.getIdleCraneDistance() / 10000;
        double wDNF = 1.0;//0.25;
        double wDistance = 1 - wDNF;
        return new double[]{ -((wDNF * scaledDNF) + (wDistance * scaledCraneIdle))};
//        return -ind.getDNF();
    }

    @Override
    public LoadUnitJobSchedule getSchedule(DirectSuperIndividual ind) {
        ExplicitModeDecoder d = new ExplicitModeDecoder(ind.getModeIndividual(), problem);
        ExplicitOperationDecoder dd = new ExplicitOperationDecoder(priorityDeterminator, ind.getOperationIndividual(), d.getEalosaes(), d.getChosenOperations(), d.getGraph(), problem, scheduleScheme, problem.getOptimizationTimeSlot());
        LoadUnitJobSchedule schedule = dd.getSchedule();

//        ind.setSchedule(schedule); 
//        ind.additionalObjects.put(Schedule.KEY_AON, problem.getActivityOnNodeDiagramm());
//        ind.additionalObjects.put(Schedule.KEY_EALOSAE, problem.getEalosaes());
        IntegerIndividual newIndModes = d.getNewIndModes();
        ind.setModeIndividual(newIndModes);
        return schedule;
    }
}
