/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.storage;

import applications.mmrcsp.model.operations.SubOperation;
import applications.mmrcsp.model.operations.SubOperations;
import applications.mmrcsp.model.resources.Resource;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.Map;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class SubStoreOperation extends DefaultStoreOperation implements SubOperation {

    public SubStoreOperation(LoadUnit lu, LoadUnitStorage resource, FieldElement duration) {
        super(lu, resource, duration);
    }

    public SubStoreOperation(int number, LoadUnit lu, LoadUnitStorage resource, FieldElement duration, Map<Resource, FieldElement> demands, SubOperations subResourceDemand) {
        super(number, lu, resource, duration, demands, subResourceDemand);
    }

    @Override
    public SubStoreOperation clone() {
        return new SubStoreOperation(number, lu, resource, duration, demands, subResourceDemand);
    }

    @Override
    public String toString() {
        return "SubStoreOperation{lu=" + lu + ", resource=" + resource + ", duration=" + duration + '}';
    }

}
