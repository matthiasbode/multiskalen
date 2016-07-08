/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.transport;

import applications.fuzzy.operation.BetaOperation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.basics.util.LoadUnitPositionAndOrientation3DInTime;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.animation.Interpolator;
import math.FieldElement;

/**
 *
 * @author bode
 * @param <E>
 */
public class MultiScaleTransportOperation<E extends ConveyanceSystem> implements SingleResourceTransportOperation<E>, BetaOperation {

    private static int counter = 0;
    protected int number;
    protected LoadUnit lu;
    protected Map<Resource, FieldElement> demands;
    protected FieldElement duration;
    protected E cs;
    protected SubOperations subResourceDemand;
    protected LoadUnitStorage origin;
    protected LoadUnitStorage destination;
    protected RoutingTransportOperation routingTransportOperation;
    private List<LoadUnitPositionAndOrientation3DInTime> keyPoints;
    private double lambda = 0.5;

    public MultiScaleTransportOperation(E cs, LoadUnitStorage origin, LoadUnitStorage destination) {
        this.number = counter++;
        this.cs = cs;
        this.origin = origin;
        this.destination = destination;
    }

    public MultiScaleTransportOperation(RoutingTransportOperation routingTransportOperation, LoadUnitStorage origin, LoadUnitStorage destination) {
        this.number = counter++;
        this.lu = routingTransportOperation.getLoadUnit();
        this.cs = (E) routingTransportOperation.getResource();
        this.routingTransportOperation = routingTransportOperation;
        this.origin = origin;
        this.destination = destination;
        this.subResourceDemand = new SubOperations();
        this.demands = new LinkedHashMap<>();
        for (Resource resource : routingTransportOperation.getRequieredResources()) {
            this.demands.put(resource, routingTransportOperation.getDemand(resource));
        }
    }

    public MultiScaleTransportOperation(RoutingTransportOperation routingTransportOperation) {
        this.number = counter++;
        this.lu = routingTransportOperation.getLoadUnit();
        this.cs = (E) routingTransportOperation.getResource();
        this.origin = routingTransportOperation.getOrigin();
        this.destination = routingTransportOperation.getDestination();
        this.routingTransportOperation = routingTransportOperation;
        this.subResourceDemand = new SubOperations();
        this.demands = new LinkedHashMap<>();
        for (Resource resource : routingTransportOperation.getRequieredResources()) {
            this.demands.put(resource, routingTransportOperation.getDemand(resource));
        }
    }

    public MultiScaleTransportOperation(int number, LoadUnit lu, Map<Resource, FieldElement> demands, FieldElement duration, E cs, SubOperations subResourceDemand, LoadUnitStorage origin, LoadUnitStorage destination, RoutingTransportOperation routingTransportOperation) {
        this.number = number;
        this.lu = lu;
        this.demands = new HashMap<>();
        for (Resource r : demands.keySet()) {
            this.demands.put(r, demands.get(r).clone());
        }
        this.duration = duration.clone();
        this.cs = cs;
        this.subResourceDemand = subResourceDemand.clone();
        this.origin = origin;
        this.destination = destination;
        this.routingTransportOperation = routingTransportOperation;

    }

    @Override
    public LoadUnitStorage getOrigin() {
        return this.origin;
    }

    @Override
    public LoadUnitStorage getDestination() {
        return this.destination;
    }

    @Override
    public LoadUnit getLoadUnit() {
        return lu;
    }

    @Override
    public FieldElement getDemand(Resource r) {
        return this.demands.get(r);
    }

    @Override
    public Set<Resource> getRequieredResources() {
        return demands.keySet();
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
    public int getId() {
        return this.number;
    }

    @Override
    public void setDuration(FieldElement duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
//        return "MultiScaleTransport: " + this.getId() +"/"+ this.routingTransportOperation.getId()+ ": LU [" + getLoadUnit().getID() + "]FROM [" + origin + "] TO [" + destination + "] VIA [" + this.getRequieredResources() + "]" + " Duration:" + this.getDuration().longValue();
        return "MultiScaleTransport:  : LU [" + getLoadUnit().getID() + "]FROM [" + origin + "] TO [" + destination + "] VIA [" + this.getRequieredResources() + "]" + " Duration:" + this.getDuration().longValue();
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.number;
        hash = 41 * hash + Objects.hashCode(this.lu);
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
        final MultiScaleTransportOperation<?> other = (MultiScaleTransportOperation<?>) obj;
        if (this.number != other.number) {
            return false;
        }
        if (!Objects.equals(this.lu, other.lu)) {
            return false;
        }
        return true;
    }

    @Override
    public E getResource() {
        return cs;
    }

    @Override
    public SubOperations getSubOperations() {
        return subResourceDemand;
    }

    @Override
    public void setSubOperations(SubOperations demand) {
        this.subResourceDemand = demand;
    }

    public RoutingTransportOperation getRoutingTransportOperation() {
        return routingTransportOperation;
    }

    @Override
    public List<LoadUnitPositionAndOrientation3DInTime> getKeyPoints() {
        if (keyPoints == null) {
            setStandardKeyPoints();
        }
        return keyPoints;
    }

    public void setKeyPoints(List<LoadUnitPositionAndOrientation3DInTime> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public void setStandardKeyPoints() {
        this.keyPoints = new ArrayList<>();
        keyPoints.add(new LoadUnitPositionAndOrientation3DInTime(this, Interpolator.LINEAR, 0L, this.getOrigin().getCenterOfGeneralOperatingArea(), null));
        keyPoints.add(new LoadUnitPositionAndOrientation3DInTime(this, Interpolator.LINEAR, this.getDuration().longValue(), this.getDestination().getCenterOfGeneralOperatingArea(), null));
    }

    @Override
    public MultiScaleTransportOperation<E> clone() {

        return new MultiScaleTransportOperation<>(number, lu, demands, duration, cs, subResourceDemand, origin, destination, routingTransportOperation);

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
