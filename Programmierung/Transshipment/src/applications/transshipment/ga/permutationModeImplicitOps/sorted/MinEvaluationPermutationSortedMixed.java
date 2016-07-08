/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.permutationModeImplicitOps.sorted;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.ga.implicit.decode.ImplicitOperationDecoder;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;
import ga.basics.FitnessEvalationFunction;
import java.util.Map;

/**
 *
 * @author bode
 */
public class MinEvaluationPermutationSortedMixed implements LoadUnitFitnessEvalationFunction<MixedPerSortedSuperIndividual> {

    private final MultiJobTerminalProblem problem;
    private final Transshipment_ImplicitScheduleGenerationScheme scheduleScheme;

    public MinEvaluationPermutationSortedMixed(MultiJobTerminalProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
    }

    @Override
    public double[] computeFitness(MixedPerSortedSuperIndividual ind) {

//        ind.setSchedule(schedule);
//        ind.additionalObjects.put(Schedule.KEY_AON, problem.getActivityOnNodeDiagramm());
//        ind.additionalObjects.put(Schedule.KEY_EALOSAE, problem.getEalosaes());
//        ind.getOperationIndividual().schedule = schedule;
        LoadUnitJobSchedule schedule = getSchedule(ind);
        /**
         * Anzahl an DNFs
         */
        ind.setDNF(schedule.getDnfJobs().size());
        ind.setIdleCraneDistance(CraneAnalysis.getMacroCraneIdleDistance(problem, schedule));
        double scaledDNF = ind.getDNF();
        double scaledCraneIdle = ind.getIdleCraneDistance() / 10000;
        double wDNF = 0.75;
        double wDistance = 1 - wDNF;
        return new double[]{-((wDNF * scaledDNF) + (wDistance * scaledCraneIdle))};
    }

    @Override
    public LoadUnitJobSchedule getSchedule(MixedPerSortedSuperIndividual ind) {
        PermutationModeDecoderSorted d = new PermutationModeDecoderSorted(ind.getModeIndividual(), problem);
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = d.getEalosaes();
        ActivityOnNodeGraph<RoutingTransportOperation> graph = d.getGraph();
//        
//        for (RoutingTransportOperation routingTransportOperation : graph.vertexSet()) {
//            if(ealosaes.get(routingTransportOperation)== null){
//                throw new UnknownError("Keine EALOSAE hinterlegt");
//            }
//        }

        ImplicitOperationDecoder dd = new ImplicitOperationDecoder(graph.vertexSet(), ind.getOperationIndividual(), ealosaes, graph, problem, scheduleScheme);
        LoadUnitJobSchedule schedule = dd.getSchedule();
        for (LoadUnitJob loadUnitJob : d.getNotRouteable()) {
            schedule.addDNFJob(loadUnitJob);
        }
        return schedule;
    }
}
