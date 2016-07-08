/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.operations;

import applications.mmrcsp.model.resources.Resource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class OperationImplementation implements Operation {

    private FieldElement duration;
    static int counter = 0;
    public int number;
    /**
     * r_{ik}*
     */
    protected LinkedHashMap<Resource, DoubleValue> demand;

    public OperationImplementation(FieldElement duration, Map<Resource, DoubleValue> demand) {
        this.duration = duration;
        this.number = counter++;
        this.demand = new LinkedHashMap<>(demand);
    }

    public OperationImplementation(FieldElement duration) {
        this.duration = duration;
        this.number = counter++;
        this.demand = new LinkedHashMap<>();
    }

    public OperationImplementation(long duration) {
        this.duration = new LongValue(duration);
        this.number = counter++;
        this.demand = new LinkedHashMap<>();
    }

    public OperationImplementation(long duration, int number) {
        this.duration = new LongValue(duration);
        this.number = number;
        counter = number++;
        this.demand = new LinkedHashMap<>();
    }

    public OperationImplementation(FieldElement duration, int number) {
        this.duration = duration;
        this.number = number;
        counter = number++;
        this.demand = new LinkedHashMap<>();
    }

    @Override
    public FieldElement getDuration() {
        return duration;
    }

    @Override
    public int getId() {
        return number;
    }

    @Override
    public String toString() {
        return " {S_" + number + ":" + "}";
    }

    @Override
    public FieldElement getDemand(Resource r) {
        FieldElement rDemand = this.demand.get(r);
        if (rDemand == null) {
            return new DoubleValue(0);
        }
        return rDemand;
    }

    @Override
    public void setDemand(Resource r, FieldElement rik) {
        if (rik instanceof DoubleValue) {
            this.demand.put(r, (DoubleValue) rik);
        } else {
            throw new IllegalArgumentException("Nur scharfer Bedarf!");
        }
    }

    @Override
    public void setDuration(FieldElement duration) {
        this.duration = duration;
    }

    @Override
    public Set<Resource> getRequieredResources() {
        return this.demand.keySet();
    }

    @Override
    public OperationImplementation clone() {
        OperationImplementation clone = new OperationImplementation(this.duration.longValue(), this.number);
        for (Resource r : demand.keySet()) {
            DoubleValue dem = demand.get(r);
            clone.setDemand(r, dem.clone());
        }
        return clone;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.number;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OperationImplementation other = (OperationImplementation) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }
    
    
}
