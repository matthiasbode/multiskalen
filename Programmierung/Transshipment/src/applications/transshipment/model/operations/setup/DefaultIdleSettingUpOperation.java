/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.setup;

import applications.fuzzy.operation.BetaOperation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class DefaultIdleSettingUpOperation implements IdleSettingUpOperation, BetaOperation {

    private final ConveyanceSystem resource;
    private HashMap<Resource, FieldElement> demands;
    private FieldElement duration;
    private LoadUnitStorage start;
    private LoadUnitStorage end;
    private static int counter = 0;
    private final int number;
    private SubOperations subResourceDemand;
    private double lambda = 0.5;

    public DefaultIdleSettingUpOperation(ConveyanceSystem cs, LoadUnitStorage start, LoadUnitStorage end) {
        this.resource = cs;
        this.start = start;
        this.end = end;
        this.demands = new HashMap<>();
        this.demands.put(cs, new DoubleValue(1.0));
        this.number = counter++;
        this.subResourceDemand = new SubOperations();
    }

    public DefaultIdleSettingUpOperation(ConveyanceSystem resource, FieldElement duration, LoadUnitStorage start, LoadUnitStorage end, int number, HashMap<Resource, FieldElement> demands, SubOperations subResourceDemand) {
        this.resource = resource;
        this.duration = duration.clone();
        this.start = start;
        this.end = end;
        this.number = number;
        this.subResourceDemand = subResourceDemand.clone();
        this.demands = new HashMap<>();
        for (Resource r : demands.keySet()) {
            this.demands.put(r, demands.get(r).clone());
        }
    }

    @Override
    public ConveyanceSystem getResource() {
        return resource;
    }

    @Override
    public FieldElement getDemand(Resource r) {
        return this.demands.get(r);
    }

    @Override
    public void setDemand(Resource r, FieldElement rik) {
        this.demands.put(r, rik);
    }

    @Override
    public FieldElement getDuration() {
        return duration;
    }

    @Override
    public void setDuration(FieldElement duration) {
        if (duration instanceof LongValue && duration.longValue()== 0L) {
            this.duration = new LongValue(1);
        } else {
            this.duration = duration;
        }
    }

    @Override
    public int getId() {
        return this.number;
    }

    @Override
    public LoadUnitStorage getStart() {
        return start;
    }

    @Override
    public LoadUnitStorage getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return  ": DefaultIdleSettingUpOperation{" + "resource=" + resource + ", duration=" + duration + ", start=" + start + ", end=" + end + '}';
    }

    @Override
    public SubOperations getSubOperations() {
        return subResourceDemand;
    }

    @Override
    public void setSubOperations(SubOperations demand
    ) {
        this.subResourceDemand = demand;
    }

    @Override
    public Set<Resource> getRequieredResources() {
        return this.demands.keySet();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.resource);
        hash = 59 * hash + Objects.hashCode(this.start);
        hash = 59 * hash + Objects.hashCode(this.end);
        hash = 59 * hash + this.number;
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
        final DefaultIdleSettingUpOperation other = (DefaultIdleSettingUpOperation) obj;
        if (!Objects.equals(this.resource, other.resource)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    
    @Override
    public DefaultIdleSettingUpOperation clone() {
        return new DefaultIdleSettingUpOperation(resource, duration, start, end, number, demands, subResourceDemand);
    }

    @Override
    public double getBeta() {
        return lambda;
    }

    @Override
    public void setBeta(double lambda) {
        this.lambda = lambda;
    }
}
