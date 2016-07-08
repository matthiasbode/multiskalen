/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.modes;

import applications.mmrcsp.model.operations.OperationImplementation;
import applications.mmrcsp.model.resources.Resource;
import java.util.Map;
import math.DoubleValue;
import math.FieldElement;

/**
 *
 * @author behrensd
 */
public class DefaultJobOperation extends OperationImplementation implements JobOperation {

    private JobOperationList<DefaultJobOperation> routing;

    public DefaultJobOperation(FieldElement duration, Map<Resource, DoubleValue> demand) {
        super(duration, demand);
    }

    public DefaultJobOperation(FieldElement duration) {
        super(duration);
    }

    public DefaultJobOperation(long duration) {
        super(duration);
    }

    public DefaultJobOperation(FieldElement duration, int number) {
        super(duration, number);
    }

    @Override
    public void setRouting(JobOperationList r) {
        this.routing = r;
    }

    @Override
    public JobOperationList getRouting() {
        return routing;
    }

    @Override
    public DefaultJobOperation clone() {
        DefaultJobOperation clone = new DefaultJobOperation(this.getDuration().clone(), number);
        for (Resource r : demand.keySet()) {
            DoubleValue dem = demand.get(r);
            clone.setDemand(r, dem.clone());
        }
        clone.setRouting(this.getRouting());
        return clone;
    }

}
