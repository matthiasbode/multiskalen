/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicitModeDirectOps.evaluation;

import applications.transshipment.ga.direct.decode.ExplicitOperationDecoder;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.ga.implicit.decode.ImplicitModeDecoder;
import applications.transshipment.ga.implicitModeDirectOps.individuals.ImplicitModeDirectOpsSuperIndividual;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.problem.MultiJobTerminalProblem;

import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ActivityListScheduleScheme;
import ga.basics.FitnessEvalationFunction;
import java.util.HashSet;

/**
 *
 * @author bode
 */
public class MinEvaluationSuperIndividual implements LoadUnitFitnessEvalationFunction<ImplicitModeDirectOpsSuperIndividual> {

    private final MultiJobTerminalProblem problem;
    private final Transshipment_ActivityListScheduleScheme scheduleScheme;
    private final PriorityDeterminator priorityDeterminator;

    public MinEvaluationSuperIndividual(MultiJobTerminalProblem problem, Transshipment_ActivityListScheduleScheme scheduleScheme, PriorityDeterminator priorityDeterminator) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.priorityDeterminator = priorityDeterminator;
    }

    @Override
    public double[] computeFitness(ImplicitModeDirectOpsSuperIndividual ind) {

//        ind.setSchedule(schedule); 
//        ind.additionalObjects.put(Schedule.KEY_AON, problem.getActivityOnNodeDiagramm());
//        ind.additionalObjects.put(Schedule.KEY_EALOSAE, problem.getEalosaes());
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
        return new double[]{-((wDNF * scaledDNF) + (wDistance * scaledCraneIdle))};
    }

    @Override
    public LoadUnitJobSchedule getSchedule(ImplicitModeDirectOpsSuperIndividual ind) {
        ImplicitModeDecoder d = new ImplicitModeDecoder(ind.getModeIndividual(), problem);
        ActivityOnNodeGraph<RoutingTransportOperation> graph = d.getGraph();
        HashSet<RoutingTransportOperation> operationsToSchedule = graph.vertexSet();
        ExplicitOperationDecoder dd = new ExplicitOperationDecoder(priorityDeterminator, ind.getOperationIndividual(), d.getEalosaes(),operationsToSchedule, graph, problem, scheduleScheme, problem.getOptimizationTimeSlot());
        LoadUnitJobSchedule schedule = dd.getSchedule();
        return schedule;
    }
}
