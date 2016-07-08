/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes;

import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bode
 */
public class Tools {

    public static Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> cloneEalosaes(Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes) {
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> result = new HashMap<>();
        for (RoutingTransportOperation routingTransportOperation : ealosaes.keySet()) {
            result.put(routingTransportOperation, ealosaes.get(routingTransportOperation).clone());
        }
        return result;
    }
}
