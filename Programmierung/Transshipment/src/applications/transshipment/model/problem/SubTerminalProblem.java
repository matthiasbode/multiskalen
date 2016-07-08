/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.problem;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.problem.timeRestricted.DefaultTimeRestictedSchedulingProblem;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceInteraction;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.StorageInteraction;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.routing.TransportGraph;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bode
 */
public class SubTerminalProblem extends DefaultTimeRestictedSchedulingProblem<RoutingTransportOperation> implements TerminalProblem {

    private final MultiJobTerminalProblem superProblem;
    private Scale scale;

    public SubTerminalProblem(Collection<RoutingTransportOperation> operations,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae,
            ActivityOnNodeGraph<RoutingTransportOperation> graph,
            MultiJobTerminalProblem superProblem, ScheduleManagerBuilder builder, TimeSlot optimizationTimeSlot, Scale scale) {
        super(optimizationTimeSlot, ealosae, operations, superProblem.getResources(), builder, graph);
        this.superProblem = superProblem;
        this.scale = scale;

    }

    public SubTerminalProblem(Collection<RoutingTransportOperation> operations,
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosae,
            ActivityOnNodeGraph<RoutingTransportOperation> graph,
            MultiJobTerminalProblem superProblem, TimeSlot optimizationTimeSlot, Scale scale) {
        super(optimizationTimeSlot, ealosae, operations, superProblem.getResources(), superProblem.getScheduleManagerBuilder(), graph);
        this.superProblem = superProblem;
        this.scale = scale;
    }

    @Override
    public StorageInteraction getStorageInteractionRule(LoadUnitStorage storage) {
        return superProblem.getStorageInteractionRule(storage);
    }

    @Override
    public ConveyanceInteraction getConveyanceSystemInteractionRule(ConveyanceSystem cs) {
        return superProblem.getConveyanceSystemInteractionRule(cs);
    }

    @Override
    public Terminal getTerminal() {
        return superProblem.getTerminal();
    }

    @Override
    public List<Train> getTrains() {
        return superProblem.getTrains();
    }

    public MultiJobTerminalProblem getSuperProblem() {
        return superProblem;
    }

    @Override
    public List<LoadUnitJob> getJobs() {
        return superProblem.getJobs();
    }

    @Override
    public Map<LoadUnitJob, TimeSlot> getJobTimeWindows() {
        return superProblem.getJobTimeWindows();
    }

//    @Override
//    public LoadUnitJobSchedule getStartSchedule() {
//        return superProblem.startSchedule;
//    }
    @Override
    public Collection<LoadUnitJob> getStammRelation() {
        return superProblem.getStammRelation();
    }

    @Override
    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    @Override
    public TransportGraph getStaticTransportGraph() {
        return superProblem.getStaticTransportGraph();
    }
}
