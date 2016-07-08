/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeDirectJob;

import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.transshipment.ga.permutationModeImplicitOps.*;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;

import ga.basics.FitnessEvalationFunction;
import java.util.Map;

/**
 *
 * @author bode
 */
public class MinEvaluationPermutationJobPermutationMode implements LoadUnitFitnessEvalationFunction<SuperPermutation> {

    private MultiJobTerminalProblem problem;
    private Transshipment_ActivityListScheduleScheme scheduleScheme;
    PriorityDeterminator<RoutingTransportOperation, PermutationJobIndividual> determinator;

    public MinEvaluationPermutationJobPermutationMode(MultiJobTerminalProblem problem, PriorityDeterminator<RoutingTransportOperation, PermutationJobIndividual> determinator, Transshipment_ActivityListScheduleScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.determinator = determinator;
    }

    @Override
    public double[] computeFitness(SuperPermutation ind) {

        LoadUnitJobSchedule schedule = getSchedule(ind);
//        ind.setSchedule(schedule);
//        ind.additionalObjects.put(Schedule.KEY_AON, problem.getActivityOnNodeDiagramm());
//        ind.additionalObjects.put(Schedule.KEY_EALOSAE, problem.getEalosaes());
        /**
         * Anzahl an DNFs
         */
        ind.setDNF(schedule.getDnfJobs().size());
        ind.setIdleCraneDistance(CraneAnalysis.getMacroCraneIdleDistance(problem, schedule));
        ind.setCraneLongitudinalDistance(CraneAnalysis.getCraneLongitudinalDistance(problem, schedule, false));
        double scaledDNF = ind.getDNF();
        double scaledCraneIdle = ind.getCraneLongitudinalDistance() / 10000;
        double wDNF = 1.;
        double wDistance = 1 - wDNF;
        return new double[]{-((wDNF * scaledDNF) + (wDistance * scaledCraneIdle))};
    }

    @Override
    public LoadUnitJobSchedule getSchedule(SuperPermutation ind) {
        PermutationModeDecoder d = new PermutationModeDecoder(ind.getModeIndividual(), problem);
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = d.getEalosaes();
        ActivityOnNodeGraph<RoutingTransportOperation> graph = d.getGraph();

        ExplicitJobDecoder dd = new ExplicitJobDecoder(determinator, ind.getOperationIndividual(), ealosaes, graph.vertexSet(), problem, scheduleScheme);
        LoadUnitJobSchedule schedule = dd.getSchedule();
        for (LoadUnitJob loadUnitJob : d.getNotRouteable()) {
            schedule.addDNFJob(loadUnitJob);
        }
        return schedule;
    }
}
