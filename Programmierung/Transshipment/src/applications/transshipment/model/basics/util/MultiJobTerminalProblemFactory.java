/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.TransshipmentParameter;
import static applications.transshipment.TransshipmentParameter.numberOfRoutes;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.InteractionMapper;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.LoadUnitResource;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import applications.transshipment.multiscale.model.MicroProblem;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.routing.KShortestPathDefaultRouteFinder;
import applications.transshipment.routing.RouteFinder;
import applications.transshipment.routing.TransferArea;
import applications.transshipment.routing.baeiko.DefaultRouteFinder;
import applications.transshipment.routing.evaluation.EvalFunction_TransportOperation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.graph.algorithms.pathsearch.KShortestPathAlgorithm;
import org.graph.weighted.DoubleEdgeWeight;

/**
 *
 * @author bode
 */
public class MultiJobTerminalProblemFactory {

    /**
     * Erzeugt ein MultiJobTerminalProblem mit einem DefaultRoute-Finder unter
     * Zuhilfenhame eines RealTimeSchedule
     *
     * @param schedule
     * @param terminal
     * @param trains
     * @param jobs
     * @param scale
     * @param eval
     * @return
     */
    public static MultiJobTerminalProblem createMacroForMultiScale(LoadUnitJobSchedule schedule, MultiJobTerminalProblem originalProblem, List<LoadUnitJob> jobs, TimeSlot optimizationTimeSlot) {
        EvalFunction_TransportOperation eval = originalProblem.getRouteFinder().getBewertung();
        Terminal terminal = originalProblem.getTerminal();
        List<Train> trains = originalProblem.getTrains();

        InteractionMapper m = new InteractionMapper(terminal);
        DefaultRuleMapper mapper = new DefaultRuleMapper();
        ScheduleManagerBuilder builder = mapper.getScheduleRuleBuilder(terminal, Scale.macro);

        MultiJobTerminalProblem problem = new MultiJobTerminalProblem(terminal, trains, jobs, m, builder, optimizationTimeSlot);

        problem.setJobTimeWindows(TransshipmentEALOSAEBuilder.calcJobTimeWindows(problem, schedule));
        JobOnNodeDiagramm newGraph = new JobOnNodeDiagramm(originalProblem.getJobOnNodeDiagramm(), jobs);

        /**
         * Das wird am Anfang einmal gemacht, danach nicht mehr, da die
         * Einstellungen übernommen werden vom Grundproblem.
         */
//         ScheduleTools.specifyDestinations(problem);
//        ScheduleTools.findExactOriginsANDReserveStamm(problem, mapper.getScheduleRuleBuilder(terminal, Scale.micro));
        problem.setJobOnNodeDiagramm(newGraph);

        /**
         * JobOnNodeDiagramm erstellen
         */
        List<LoadUnitResource> resources = new ArrayList<>();
        resources.addAll(terminal.getConveyanceSystems());
        resources.addAll(terminal.getStorages());

        RouteFinder finder = new DefaultRouteFinder(problem, resources, eval);
        problem.setRouteFinder(finder);

        /**
         * Abfangen von DNFs aus Routensuche.
         */
        List<LoadUnitJob> newDnfJobs = new ArrayList<>();
        for (LoadUnitJob loadUnitJob : problem.getJobTimeWindows().keySet()) {
            if (problem.getJobTimeWindows().get(loadUnitJob).equals(TimeSlot.nullTimeSlot)) {
                newDnfJobs.add(loadUnitJob);
            }
        }
        for (LoadUnitJob loadUnitJob : newDnfJobs) {
            schedule.getDnfJobs().add(loadUnitJob);
        }

        /**
         * Berechne Routen für Macro-Problem.
         */
        /**
         * TODO: Wieder einkommentieren!!!
         */
//        problem.getRouteFinder().calculateRoutes(numberOfRoutes);
        /**
         * Berechne großer AON und bildet zeitliche Restriktionen.
         */
        ActivityOnNodeGraph<RoutingTransportOperation> alloverGraph = LoadUnitJobActivityOnNodeBuilder.buildAlloverGraph(problem);
        problem.setActivityOnNodeDiagramm(alloverGraph);
        problem.setScale(Scale.macro);
        return problem;

    }

    public static MicroProblem createMicroForMultiScale(TimeSlot currentTimeSlot, LoadUnitJobSchedule allOverSchedule, Collection<RoutingTransportOperation> operationsToSchedule, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae, ActivityOnNodeGraph<RoutingTransportOperation> subGraph, MultiJobTerminalProblem superProblem) {
        DefaultRuleMapper mapper = new DefaultRuleMapper();
        ScheduleManagerBuilder builder = mapper.getScheduleRuleBuilder(superProblem.getTerminal(), Scale.micro);
        MicroProblem microProblem = new MicroProblem(currentTimeSlot, allOverSchedule, operationsToSchedule, ealosae, subGraph, superProblem, builder);
        microProblem.setScale(Scale.micro);
        return microProblem;
    }

    /**
     * Erzeugt ein MultiJobTerminal Problem unter Zuhilfenahme eines
     * KShortestPathalgorithmus und eines RealTimeSchedule
     *
     * @param schedule
     * @param terminal
     * @param trains
     * @param jobs
     * @param builder
     * @param routing
     * @param eval
     * @return
     */
    public static MultiJobTerminalProblem createForMultiScale(LoadUnitJobSchedule schedule, Terminal terminal, List<Train> trains, List<LoadUnitJob> jobs, ScheduleManagerBuilder builder, KShortestPathAlgorithm<TransferArea, DoubleEdgeWeight> routing, EvalFunction_TransportOperation eval, TimeSlot optimizationTimeSlot) {
        InteractionMapper m = new InteractionMapper(terminal);
        MultiJobTerminalProblem problem = new MultiJobTerminalProblem(terminal, trains, jobs, m, builder, optimizationTimeSlot);

        problem.setJobTimeWindows(TransshipmentEALOSAEBuilder.calcJobTimeWindows(problem, schedule));
//        ScheduleTools.specifyDestinations(problem);
//        ScheduleTools.findExactOriginsANDReserveStamm(problem);
        problem.setJobOnNodeDiagramm(problem.getJobOnNodeDiagramm());

        /**
         * JobOnNodeDiagramm erstellen
         */
        List<LoadUnitResource> resources = new ArrayList<>();
        resources.addAll(terminal.getConveyanceSystems());
        resources.addAll(terminal.getStorages());

        RouteFinder finder = new KShortestPathDefaultRouteFinder(problem, resources, routing, eval);
        problem.setRouteFinder(finder);

        /**
         * Abfangen von DNFs aus Routensuche.
         */
        List<LoadUnitJob> newDnfJobs = new ArrayList<>();
        for (LoadUnitJob loadUnitJob : problem.getJobTimeWindows().keySet()) {
            if (problem.getJobTimeWindows().get(loadUnitJob).equals(TimeSlot.nullTimeSlot)) {
                newDnfJobs.add(loadUnitJob);
            }
        }
        for (LoadUnitJob loadUnitJob : newDnfJobs) {
            schedule.getDnfJobs().add(loadUnitJob);
        }

        /**
         * Berechne Routen für Macro-Problem.
         */
        problem.getRouteFinder().calculateRoutes(numberOfRoutes);

        /**
         * Berechne großer AON und bildet zeitliche Restriktionen.
         */
        ActivityOnNodeGraph<RoutingTransportOperation> alloverGraph = LoadUnitJobActivityOnNodeBuilder.buildAlloverGraph(problem);
        problem.setActivityOnNodeDiagramm(alloverGraph);

        return problem;
    }

    /**
     * Erzeugt ein MultiJobTerminalProblem unter Festlegung eines
     * KShortesPath-Algorithmus
     *
     * @param terminal
     * @param trains
     * @param jobs
     * @param builder
     * @param routing
     * @param eval
     * @return
     */
    public static MultiJobTerminalProblem create(Terminal terminal, List<Train> trains, List<LoadUnitJob> jobs, ScheduleManagerBuilder builder, KShortestPathAlgorithm<TransferArea, DoubleEdgeWeight> routing, EvalFunction_TransportOperation eval, TimeSlot optimizationTimeSlot) {
        InteractionMapper m = new InteractionMapper(terminal);
        MultiJobTerminalProblem problem = new MultiJobTerminalProblem(terminal, trains, jobs, m, builder, optimizationTimeSlot);

        problem.setJobTimeWindows(TransshipmentEALOSAEBuilder.calcJobTimeWindows(problem));
        ScheduleTools.specifyDestinations(problem);
        ScheduleTools.findExactOriginsANDReserveStamm(problem);
        problem.setJobOnNodeDiagramm(LoadUnitJobOnNodeDiagrammBuilder.build(problem.getJobs()));

        List<LoadUnitResource> resources = new ArrayList<>();
        resources.addAll(terminal.getConveyanceSystems());
        resources.addAll(terminal.getStorages());

        RouteFinder finder = new KShortestPathDefaultRouteFinder(problem, resources, routing, eval);
        problem.setRouteFinder(finder);

        return problem;
    }

    /**
     * Erzeugt ein MultiJobTerminalProblem wobei das DefaultRouting gewählt
     * wird.
     *
     * @param terminal
     * @param trains
     * @param jobs
     * @param builder
     * @param eval
     * @return
     */
    public static MultiJobTerminalProblem create(Terminal terminal, List<Train> trains, List<LoadUnitJob> jobs, Mapper mapper, EvalFunction_TransportOperation eval, TimeSlot optimizationTimeSlot, Scale scale) {
        ScheduleManagerBuilder builder = mapper.getScheduleRuleBuilder(terminal, scale);

        InteractionMapper m = new InteractionMapper(terminal);
        MultiJobTerminalProblem problem = new MultiJobTerminalProblem(terminal, trains, jobs, m, builder, optimizationTimeSlot);
        problem.setJobTimeWindows(TransshipmentEALOSAEBuilder.calcJobTimeWindows(problem));
        
        List<LoadUnitResource> resources = new ArrayList<>();
        resources.addAll(terminal.getConveyanceSystems());
        resources.addAll(terminal.getStorages());
        RouteFinder finder = new DefaultRouteFinder(problem, resources, eval);
        problem.setRouteFinder(finder);
        problem.setScale(scale);

        ScheduleTools.specifyDestinations(problem);
        ScheduleTools.findExactOriginsANDReserveStamm(problem, mapper.getScheduleRuleBuilder(terminal, Scale.micro));
        problem.setJobOnNodeDiagramm(LoadUnitJobOnNodeDiagrammBuilder.build(problem.getJobs()));

        return problem;
    }

    public static LoadUnitJobSchedule createNewSchedule(TerminalProblem p) {
        InstanceHandler handler = new InstanceHandler(p.getScheduleManagerBuilder());
        LoadUnitJobSchedule result = new LoadUnitJobSchedule(handler);
        ScheduleTools.scheduleStoreOnBeginning(result, p);
        return result;
    }

}
