/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.evaluation;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.ga.implicit.decode.ImplicitModeDecoder;
import applications.transshipment.ga.implicit.decode.ImplicitOperationDecoder;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;

import java.util.Map;

/**
 *
 * @author bode
 */
public class MinEvaluationSuperIndividualMultiObjective implements LoadUnitFitnessEvalationFunction<ImplicitSuperIndividual> {

    public static double maxDNF = 20;
    public static double maxDistance = 7000;
    private MultiJobTerminalProblem problem;
    private Transshipment_ImplicitScheduleGenerationScheme scheduleScheme;

    private LoadUnitJobSchedule initialSchedule;

    public MinEvaluationSuperIndividualMultiObjective(MultiJobTerminalProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
    }

    public MinEvaluationSuperIndividualMultiObjective(LoadUnitJobSchedule initialSchedule, MultiJobTerminalProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.initialSchedule = initialSchedule;
    }

    @Override
    public double[] computeFitness(ImplicitSuperIndividual ind) {
        LoadUnitJobSchedule schedule = getSchedule(ind);
        ind.setDNF(schedule.getDnfJobs().size());
        ind.setCraneLongitudinalDistance(CraneAnalysis.getCraneLongitudinalDistance(problem, schedule, false));
        return new double[]{-ind.getDNF()};
    }

    @Override
    public LoadUnitJobSchedule getSchedule(ImplicitSuperIndividual ind) {
        ImplicitModeDecoder d = new ImplicitModeDecoder(ind.getModeIndividual(), problem);
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = d.getEalosaes();
        ActivityOnNodeGraph<RoutingTransportOperation> graph = d.getGraph();
        ImplicitOperationDecoder dd;
        if (initialSchedule == null) {
            dd = new ImplicitOperationDecoder(graph.vertexSet(), ind.getOperationIndividual(), ealosaes, graph, problem, scheduleScheme);
        } else {
            dd = new ImplicitOperationDecoder(graph.vertexSet(), initialSchedule, ind.getOperationIndividual(), ealosaes, graph, problem, scheduleScheme);
        }
        LoadUnitJobSchedule schedule = dd.getSchedule();
        for (LoadUnitJob loadUnitJob : d.getNotRouteable()) {
            schedule.addDNFJob(loadUnitJob);
        }

        schedule.aon = graph;
        schedule.ealosae = ealosaes;

        return schedule;
    }
}
