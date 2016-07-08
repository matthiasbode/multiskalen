/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.dnf;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.util.Collection;
import java.util.Map;
import math.FieldElement;

/**
 *
 * @author bode
 */
public interface DNFTreatment {
    public boolean setDNF(RoutingTransportOperation failedOperation, FieldElement t, Collection<RoutingTransportOperation> list, LoadUnitJobSchedule s, ActivityOnNodeGraph<RoutingTransportOperation> graph, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes);
}
