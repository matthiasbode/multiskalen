/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.Map;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class MultiScaleLCSTransportOperation extends MultiScaleTransportOperation {

    public MultiScaleLCSTransportOperation(RoutingTransportOperation routingTransportOperation) {
        super(routingTransportOperation);
    }

    public MultiScaleLCSTransportOperation(RoutingTransportOperation routingTransportOperation, LoadUnitStorage origin, LoadUnitStorage destination) {
        super(routingTransportOperation, origin, destination);
    }

    public MultiScaleLCSTransportOperation(int number, LoadUnit lu, Map demands, FieldElement duration, ConveyanceSystem cs, SubOperations subResourceDemand, LoadUnitStorage origin, LoadUnitStorage destination, RoutingTransportOperation routingTransportOperation) {
        super(number, lu, demands, duration, cs, subResourceDemand, origin, destination, routingTransportOperation);
    }
    
    

    @Override
    public FieldElement getDemand(Resource r) {
        if (r instanceof LCSystem) {
            return super.getDemand(r);
        }
        if (r instanceof Agent) {
            Agent a = (Agent) r;
            LCSystem superConveyanceSystem = (LCSystem) a.getSuperConveyanceSystem();
            return super.getDemand(superConveyanceSystem);
        } else {
            return null;
        }
    }

    @Override
    public MultiScaleLCSTransportOperation clone() {
       return new MultiScaleLCSTransportOperation(number, lu, demands, duration, cs, subResourceDemand, origin, destination, routingTransportOperation);
    }

    
    
    
    
}
