/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.storage;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.basics.util.LoadUnitPositionAndOrientation3DInTime;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.vecmath.Point3d;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class SuperStoreOperation implements SingleResourceStoreOperation {

    private static int counter = 0;
    private final int number;
    private final LoadUnit lu;
    private final LoadUnitStorage resource;
    private FieldElement duration;
    private Map<Resource, FieldElement> demands;
    private SubOperations subResourceDemand;

    public SuperStoreOperation(LoadUnit lu, LoadUnitStorage resource, SubOperations subOperations, FieldElement duration) {
        this.lu = lu;
        this.resource = resource;
        if (duration.longValue() < 0) {
            throw new IllegalArgumentException("Negative Dauer einer Lagerung");
        }
        this.subResourceDemand = subOperations;
        this.duration = duration;
        this.demands = new HashMap<>();
        this.demands.put(resource, resource.getDemand(lu));
        this.number = counter++;
    }

    public SuperStoreOperation(int number, LoadUnit lu, LoadUnitStorage resource, FieldElement duration, Map<Resource, FieldElement> demands, SubOperations subResourceDemand) {
        this.number = number;
        this.lu = lu;
        this.resource = resource;
        this.duration = duration.clone();
        this.demands = new HashMap<>();
        for (Resource r : demands.keySet()) {
            this.demands.put(r, demands.get(r).clone());
        }
        this.subResourceDemand = subResourceDemand.clone();
        
    }

    @Override
    public LoadUnitStorage getResource() {
        return resource;
    }

    @Override
    public FieldElement getDemand(Resource r) {
        return this.demands.get(r);
    }

    @Override
    public Set<Resource> getRequieredResources() {
        return this.demands.keySet();
    }

    @Override
    public void setDemand(Resource r, FieldElement rik) {
        this.demands.put(r, rik);
    }

    @Override
    public FieldElement getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration(FieldElement duration) {
        if (duration.longValue() < 0) {
            throw new IllegalArgumentException("Negative Dauer einer Lagerung: " + duration);
        }
        if (subResourceDemand != null) {
            for (Operation su : subResourceDemand.getSubOperations()) {
                su.setDuration(duration);
            }
        }
        this.duration = duration;
    }

    @Override
    public int getId() {
        return this.number;
    }

    @Override
    public LoadUnit getLoadUnit() {
        return this.lu;
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
    public String toString() {
        return "SuperStoreOperation{lu=" + lu + ", resource=" + resource +  ", Fläche:" + resource.getGeneralOperatingArea().getBounds2D()+ ", duration=" + duration + '}';
    }

    @Override
    public List<LoadUnitPositionAndOrientation3DInTime> getKeyPoints() {
        ArrayList<LoadUnitPositionAndOrientation3DInTime> res = new ArrayList<>();
        Point3d position = resource.getPosition();
        LoadUnitPositionAndOrientation3DInTime posStart = new LoadUnitPositionAndOrientation3DInTime(this, 0L, position, null);
        LoadUnitPositionAndOrientation3DInTime posEnde = new LoadUnitPositionAndOrientation3DInTime(this, this.getDuration().longValue(), position, null);
        res.add(posStart);
        res.add(posEnde);
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.number;
        hash = 53 * hash + Objects.hashCode(this.lu);
        hash = 53 * hash + Objects.hashCode(this.resource);
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
        final SuperStoreOperation other = (SuperStoreOperation) obj;
        if (this.number != other.number) {
            return false;
        }
        if (!Objects.equals(this.lu, other.lu)) {
            return false;
        }
        if (!Objects.equals(this.resource, other.resource)) {
            return false;
        }
        return true;
    }

    
    
    @Override
    public SuperStoreOperation clone() {
        return new SuperStoreOperation(number, lu, resource, duration, demands, subResourceDemand);
    }

}
