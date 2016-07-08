/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import com.google.common.collect.Iterators;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sortiert RoutingTransportOperations dahingehend, dass die Operationen mit der
 * geringsten frühsten Endzeit vorne in der Liste zu finden sind.
 *
 * @author bode
 */
public class MinSetupDistance implements OperationRules<RoutingTransportOperation> {

    HashMap<ConveyanceSystem, Operation> lastOperationPerResource;

    @Override
    public int compare(RoutingTransportOperation o1, RoutingTransportOperation o2) {

        TransportOperation top1 = (TransportOperation) lastOperationPerResource.get(o1.getResource());
        TransportOperation top2 = (TransportOperation) lastOperationPerResource.get(o2.getResource());

        if (top1 != null && top2 != null) {
            double distance1 = top1.getDestination().getCenterOfGeneralOperatingArea().distance(o1.getOrigin().getCenterOfGeneralOperatingArea());
            double distance2 = top2.getDestination().getCenterOfGeneralOperatingArea().distance(o2.getOrigin().getCenterOfGeneralOperatingArea());
            if (distance1 != distance2) {
                return Double.compare(distance1, distance2);
            }
        }
        return 0;
    }

    @Override
    public void setAdditionalInformation(Schedule schedule, Collection<RoutingTransportOperation> operationsToSchedule, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {
        lastOperationPerResource = new LinkedHashMap<>();
        if (schedule != null) {
            for (Resource resource : schedule.getResources()) {
                if (resource instanceof ConveyanceSystem) {
                    Collection<Operation> operationsForResource = schedule.getOperationsForResource(resource);
                    if (!operationsForResource.isEmpty()) {
                        lastOperationPerResource.put((ConveyanceSystem) resource, Iterators.getLast(operationsForResource.iterator()));
                    }
                }
            }
        }
    }

}
