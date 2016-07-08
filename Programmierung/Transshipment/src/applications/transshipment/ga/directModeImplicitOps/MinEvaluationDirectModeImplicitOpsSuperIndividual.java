/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.directModeImplicitOps;

import applications.transshipment.ga.direct.decode.ExplicitModeDecoder;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.ga.implicit.decode.ImplicitOperationDecoder;
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
public class MinEvaluationDirectModeImplicitOpsSuperIndividual implements LoadUnitFitnessEvalationFunction<DirectModeImplicitOpsSuperIndividual> {

    private MultiJobTerminalProblem problem;
    private Transshipment_ImplicitScheduleGenerationScheme scheduleScheme;

    public MinEvaluationDirectModeImplicitOpsSuperIndividual(MultiJobTerminalProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
    }

    @Override
    public double[] computeFitness(DirectModeImplicitOpsSuperIndividual ind) {
        ExplicitModeDecoder d = new ExplicitModeDecoder(ind.getModeIndividual(), problem);

        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = d.getEalosaes();
        ActivityOnNodeGraph<RoutingTransportOperation> graph = d.getGraph();

        ImplicitOperationDecoder dd = new ImplicitOperationDecoder(graph.vertexSet(), ind.getOperationIndividual(), ealosaes, graph, problem, scheduleScheme);
        LoadUnitJobSchedule schedule = dd.getSchedule();
//        ind.setSchedule(schedule); 
//        ind.additionalObjects.put(Schedule.KEY_AON, problem.getActivityOnNodeDiagramm());
//        ind.additionalObjects.put(Schedule.KEY_EALOSAE, problem.getEalosaes());
        /**
         * Anzahl an DNFs
         */
        ind.setDNF(schedule.getDnfJobs().size());
        ind.setIdleCraneDistance(CraneAnalysis.getCraneLongitudinalDistance(problem, schedule, false));

        return new double[]{-ind.getDNF()};
    }

    @Override
    public LoadUnitJobSchedule getSchedule(DirectModeImplicitOpsSuperIndividual ind) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
