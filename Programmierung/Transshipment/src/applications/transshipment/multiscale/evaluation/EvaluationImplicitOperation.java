/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.evaluation;

import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.ga.implicit.decode.ImplicitOperationDecoder;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;

import applications.transshipment.multiscale.model.MicroProblem;
import java.util.Collection;
import java.util.HashSet;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class EvaluationImplicitOperation implements LoadUnitFitnessEvalationFunction<ImplicitOperationIndividual> {

    public static double maxDNF = 20;
    public static double maxDistance = 7000;
    private final MicroProblem problem;
    private final Transshipment_ImplicitScheduleGenerationScheme scheduleScheme;
    private LoadUnitJobSchedule initialSchedule;
    private Collection<RoutingTransportOperation> operationsToSchedule;

    public EvaluationImplicitOperation(Collection<RoutingTransportOperation> operationsToSchedule, MicroProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.operationsToSchedule = operationsToSchedule;
    }

    public EvaluationImplicitOperation(Collection<RoutingTransportOperation> operationsToSchedule, LoadUnitJobSchedule initialSchedule, MicroProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.initialSchedule = initialSchedule;
        this.operationsToSchedule = operationsToSchedule;
    }

    @Override
    public double[] computeFitness(ImplicitOperationIndividual ind) {

        LoadUnitJobSchedule schedule = getSchedule(ind);
        int dnf = schedule.getDnfJobs().size();
        HashSet<RoutingTransportOperation> notScheduledOperationsInCurrentTimeSlot = new HashSet<>(problem.getOperations());
        notScheduledOperationsInCurrentTimeSlot.removeAll(schedule.getScheduledRoutingTransportOperations());
//
//        double ruestDistance = CraneAnalysis.getMittlereRuestFahrtDistanceCrane(schedule, problem);

        FieldElement e = schedule.getLastScheduleEventTime();
        return new double[]{-dnf, -notScheduledOperationsInCurrentTimeSlot.size(), -e.longValue()};
    }

    @Override
    public LoadUnitJobSchedule getSchedule(ImplicitOperationIndividual ind) {
        ImplicitOperationDecoder dd = new ImplicitOperationDecoder(operationsToSchedule, initialSchedule, ind, problem.getEalosaes(), problem.getActivityOnNodeDiagramm(), problem, scheduleScheme);
        LoadUnitJobSchedule schedule = dd.getSchedule();
        return schedule;
    }
}
