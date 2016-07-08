/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.lattice;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperation;
import applications.mmrcsp.model.operations.SubOperations;
import applications.mmrcsp.model.operations.SingleResourceOperation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import math.DoubleValue;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class CellOperation implements SingleResourceOperation, SubOperation {

    private static int counter = 0;
    private final int number;
    private CellResource2D cell;
    private FieldElement duration;
    private Map<Resource, FieldElement> demand;
    private SubOperations subResourceDemand;

    public CellOperation(CellResource2D cell, FieldElement duration) {
        this.cell = cell;
        this.duration = duration;
        this.number = counter++;
        this.demand = new HashMap<>();
        this.demand.put(cell, new DoubleValue(1.));
    }

    public CellOperation(int number, CellResource2D cell, FieldElement duration, Map<Resource, FieldElement> demand, SubOperations subResourceDemand) {
        this.number = number;
        this.cell = cell;
        this.duration = duration.clone();
        this.demand = new HashMap<>();
        for (Resource r : demand.keySet()) {
            this.demand.put(r, duration.clone());
        }
        this.subResourceDemand = subResourceDemand.clone();
    }

    @Override
    public FieldElement getDemand(Resource r) {
        if (!r.equals(cell)) {
            return new DoubleValue(0);
        } else {
            return demand.get(r);
        }
    }

    @Override
    public void setDemand(Resource r, FieldElement rik) {
        this.demand.put(r, rik);
    }

    @Override
    public FieldElement getDuration() {
        return duration;
    }

    @Override
    public void setDuration(FieldElement duration) {
        this.duration = duration;
    }

    @Override
    public int getId() {
        return number;
    }

    @Override
    public Resource getResource() {
        return cell;
    }

    @Override
    public SubOperations getSubOperations() {
        return subResourceDemand;
    }

    @Override
    public void setSubOperations(SubOperations demand) {
        this.subResourceDemand = demand;
    }

    @Override
    public Set<Resource> getRequieredResources() {
        return demand.keySet();
    }

    @Override
    public CellOperation clone() {
        return new CellOperation(number, cell, duration, demand, subResourceDemand);
    }
}
