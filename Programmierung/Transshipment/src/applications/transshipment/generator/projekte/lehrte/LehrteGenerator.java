/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.lehrte;

import applications.transshipment.generator.projekte.duisburg.*;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.transshipment.model.basics.util.LoadUnitJobActivityOnNodeBuilder;
import applications.transshipment.ga.direct.routeDetermination.CycleRemover;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.generator.LoadUnitGeneratorFromJSON;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.basics.util.DefaultRuleMapper;
import applications.transshipment.model.basics.util.Mapper;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.routing.evaluation.EvalFunction_TransportOperation_TimeMovement;
import java.io.InputStream;
import java.util.List;

/**
 * Beispiel zum Testen.
 *
 * @author bode
 */
public class LehrteGenerator {

    public static TimeSlot ts;

    public MultiJobTerminalProblem generateTerminalProblem(Scale scale, int numberOfRoutes) {
        return generateTerminalProblem(scale, numberOfRoutes, false);
    }

    @Deprecated
    public MultiJobTerminalProblem generateTerminalProblem(Scale scale, int numberOfRoutes, boolean removeCircles) {
        LehrteInputParameters parameters = new LehrteInputParameters();
        Terminal terminal = new LehrteTerminalGenerator().generateTerminal(parameters);
        List<Train> trains = new DuisburgTrainGenerator().generateTrains(terminal, parameters);

        InputStream resource = DuisburgTerminalGenerator.class.getResourceAsStream("transportprogramme/tp1.json");
        List<LoadUnitJob> jobs = new LoadUnitGeneratorFromJSON(resource, 0).generateJobs(trains, terminal);

        Mapper mapper = new DefaultRuleMapper( );
 
        InstanceHandler handler = new InstanceHandler(mapper.getScheduleRuleBuilder(terminal, scale));
        EvalFunction_TransportOperation_TimeMovement eval = new EvalFunction_TransportOperation_TimeMovement(handler, TransshipmentParameter.transportOperation_TimeMovement_DurationWeight);

        /**
         * Routensuche
         *
         */
        ts = terminal.getTemporalAvailability().getAllOverTimeSlot();

        MultiJobTerminalProblem problem = MultiJobTerminalProblemFactory.create(terminal, trains, jobs, mapper, eval, ts, scale);
        /**
         * Berechne Routen.
         */
        problem.getRouteFinder().calculateRoutes(numberOfRoutes);

        if (removeCircles) {
            /**
             * Entferne Zyklen.
             */
            List<LoadUnitJob> removeCycles = CycleRemover.removeCycles(problem);
            problem.notDirectlyTransportable = removeCycles;
        }
        /**
         * Berechne großer AON und bildet zeitliche Restriktionen.
         */
        ActivityOnNodeGraph<RoutingTransportOperation> alloverGraph = LoadUnitJobActivityOnNodeBuilder.buildAlloverGraph(problem);
        problem.setActivityOnNodeDiagramm(alloverGraph);
        return problem;
    }
}
