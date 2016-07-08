/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.evaluation;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.ga.LoadUnitFitnessEvalationFunction;
import applications.transshipment.ga.implicit.decode.ImplicitModeDecoder;
import applications.transshipment.ga.implicit.decode.ImplicitOperationDecoder;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.schedule.scheduleSchemes.Tools;
import applications.transshipment.model.schedule.scheduleSchemes.Transshipment_ImplicitScheduleGenerationScheme;
import java.util.Collection;

import java.util.Map;
import java.util.TreeMap;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;
import math.function.StepFunction;

/**
 *
 * @author bode
 */
public class MinEvaluationSuperIndividual implements LoadUnitFitnessEvalationFunction<ImplicitSuperIndividual> {

    private final MultiJobTerminalProblem problem;
    private final Transshipment_ImplicitScheduleGenerationScheme scheduleScheme;

    public TimeSlot timeslot;
    public static boolean useRI;
    public static long period = 10 * 60 * 1000;
    private LoadUnitJobSchedule initialSchedule;

    public MinEvaluationSuperIndividual(MultiJobTerminalProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
    }

    public MinEvaluationSuperIndividual(LoadUnitJobSchedule initialSchedule, MultiJobTerminalProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.initialSchedule = initialSchedule;
    }

    public MinEvaluationSuperIndividual(LoadUnitJobSchedule initialSchedule, MultiJobTerminalProblem problem, Transshipment_ImplicitScheduleGenerationScheme scheduleScheme, TimeSlot timeslot) {
        this.problem = problem;
        this.scheduleScheme = scheduleScheme;
        this.initialSchedule = initialSchedule;
        this.timeslot = timeslot;
    }

    @Override
    public double[] computeFitness(ImplicitSuperIndividual ind) {
        /**
         * Anzahl an DNFs
         */
        LoadUnitJobSchedule schedule = getSchedule(ind);
        ind.setDNF(schedule.getDnfJobs().size());

        if (useRI) {
            double ressourceLeveling = getRessourceLeveling(schedule);
            return new double[]{-ind.getDNF(), -ressourceLeveling};// meanSlack};
        } else {
            long minSlack = getMinSlack(schedule);
            return new double[]{-ind.getDNF(), minSlack};// meanSlack};
        }

    }

    public long getMeanSlack(Schedule schedule) {
        LoadUnitJobSchedule jschedule = (LoadUnitJobSchedule) schedule;
        Collection<RoutingTransportOperation> scheduledRoutingTransportOperations = jschedule.getScheduledRoutingTransportOperations();
        long sum = 0;
        for (RoutingTransportOperation scheduledRoutingTransportOperation : scheduledRoutingTransportOperations) {
            EarliestAndLatestStartsAndEnds ealosae = jschedule.originalEalosae.get(scheduledRoutingTransportOperation);
            if (ealosae == null) {
                continue;
            }
            FieldElement sub = ealosae.getLatestStart().sub(schedule.get(scheduledRoutingTransportOperation));
            sum += sub.longValue();
        }
        return sum / scheduledRoutingTransportOperations.size() / 1000;
    }

    public long getDegressiveMeanSlack(Schedule schedule) {
        LoadUnitJobSchedule jschedule = (LoadUnitJobSchedule) schedule;
        Collection<RoutingTransportOperation> scheduledRoutingTransportOperations = jschedule.getScheduledRoutingTransportOperations();
        long sum = 0;
        for (RoutingTransportOperation scheduledRoutingTransportOperation : scheduledRoutingTransportOperations) {
            EarliestAndLatestStartsAndEnds ealosae = jschedule.originalEalosae.get(scheduledRoutingTransportOperation);
            if (ealosae == null) {
                continue;
            }
            FieldElement sub = ealosae.getLatestStart().sub(schedule.get(scheduledRoutingTransportOperation));
            sum += Math.sqrt(sub.longValue());
        }
        return sum / scheduledRoutingTransportOperations.size() / 1000;
    }

    public long getMinSlack(Schedule schedule) {
        LoadUnitJobSchedule jschedule = (LoadUnitJobSchedule) schedule;
        Collection<RoutingTransportOperation> scheduledRoutingTransportOperations = jschedule.getScheduledRoutingTransportOperations();
        long min = Long.MAX_VALUE;
        for (RoutingTransportOperation scheduledRoutingTransportOperation : scheduledRoutingTransportOperations) {
            EarliestAndLatestStartsAndEnds ealosae = jschedule.originalEalosae.get(scheduledRoutingTransportOperation);
            if (ealosae == null) {
                continue;
            }
            FieldElement sub = ealosae.getLatestStart().sub(schedule.get(scheduledRoutingTransportOperation));
            if (sub.longValue() < min) {
                min = sub.longValue();
            }
        }
        return min / 1000;
    }

    public double getRessourceLeveling(Schedule schedule) {

        if (timeslot == null) {
            timeslot = problem.getOptimizationTimeSlot();
        }
        double distance = 0;
        int n = 0;
        for (Resource resource : schedule.getResources()) {
            if (resource instanceof ConveyanceSystem) {
                ScheduleRule scheduleRule = schedule.getHandler().get(resource);
                if (scheduleRule instanceof ScalarFunctionBasedRule) {
                    n += schedule.getOperationsForResource(resource).size();
                    ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;
                    StepFunction workloadFunction = (StepFunction) sfb.getWorkloadFunction();
                    int i = 0;
                    double integral = 0;
                    TreeMap<Integer, Double> integrals = new TreeMap<>();

                    for (long currentStart = timeslot.getFromWhen().longValue(); currentStart < timeslot.getUntilWhen().longValue(); currentStart += period) {

                        LongValue start = new LongValue(currentStart);
                        LongValue ende = new LongValue(currentStart + period);

                        double currentInt = workloadFunction.getIntegral(start, ende).doubleValue();
                        StepFunction sf = new StepFunction(start, ende, new DoubleValue(sfb.getMax()));
                        currentInt = currentInt / sf.getIntegral(start, ende).doubleValue();
                        integrals.put(i, currentInt);
                        integral += currentInt;
                        i++;
                    }

                    integral = integral / integrals.size();
                    double sum = 0;
                    for (Double currentInt : integrals.values()) {
                        sum += (currentInt - integral) * (currentInt - integral);
                    }
                    distance += sum;
                }
            }
        }
        return distance;
    }

    @Override
    public LoadUnitJobSchedule getSchedule(ImplicitSuperIndividual ind) {
        ImplicitModeDecoder d = new ImplicitModeDecoder(ind.getModeIndividual(), problem);
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = d.getEalosaes();
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> originalealosaes = Tools.cloneEalosaes(ealosaes);
//        EALOSAEPlotter.plot(problem, new File("C:\\Users\\Bode\\Documents\\Promo\\Ealosae"), ealosaes);
        ActivityOnNodeGraph<RoutingTransportOperation> graph = d.getGraph();
        ImplicitOperationDecoder dd;

        for (LoadUnitJob loadUnitJob : d.getNotRouteable()) {
            problem.getJobs().remove(loadUnitJob);
        }

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
        schedule.originalEalosae = originalealosaes;

        return schedule;
    }
}
