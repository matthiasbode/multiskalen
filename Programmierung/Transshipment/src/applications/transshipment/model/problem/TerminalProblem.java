/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.problem;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.problem.timeRestricted.TimeRestrictedSchedulingProblem;
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
public interface TerminalProblem extends TimeRestrictedSchedulingProblem<RoutingTransportOperation> {

    public StorageInteraction getStorageInteractionRule(LoadUnitStorage storage);

    public ConveyanceInteraction getConveyanceSystemInteractionRule(ConveyanceSystem cs);

    public Terminal getTerminal();

    public List<Train> getTrains();

    public List<LoadUnitJob> getJobs();

    public Map<LoadUnitJob, TimeSlot> getJobTimeWindows();

    public Scale getScale();

    public TransportGraph getStaticTransportGraph();

    public Collection<LoadUnitJob> getStammRelation();
}
