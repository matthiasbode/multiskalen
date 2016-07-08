/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.transport;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.util.LoadUnitPositionAndOrientation3DInTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import math.DoubleValue;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class DefaultSingleResourceTransportOperation implements SingleResourceTransportOperation {

    private final int number;
    private static int counter = 0;
    private final LoadUnitStorage origin;
    private final LoadUnitStorage destination;
    private final LoadUnitJob job;
    private final ConveyanceSystem conveyanceSystem;
    private HashMap<Resource, FieldElement> demand;
    private FieldElement duration;
    private SubOperations subResourceDemand;
    private List<LoadUnitPositionAndOrientation3DInTime> keyPoints;

    public DefaultSingleResourceTransportOperation(LoadUnitStorage origin, LoadUnitStorage destination, LoadUnitJob job, ConveyanceSystem cs) {
        this.origin = origin;
        this.destination = destination;
        this.job = job;
        this.conveyanceSystem = cs;
        this.demand = new HashMap<>();
        this.demand.put(cs, new DoubleValue(1.0));
        this.number = counter++;
    }

    public DefaultSingleResourceTransportOperation(int number, LoadUnitStorage origin, LoadUnitStorage destination, LoadUnitJob job, ConveyanceSystem conveyanceSystem, HashMap<Resource, FieldElement> demand, FieldElement duration, SubOperations subResourceDemand) {
        this.number = number;
        this.origin = origin;
        this.destination = destination;
        this.job = job;
        this.conveyanceSystem = conveyanceSystem;
        this.demand = new HashMap<>();
        for (Resource r : demand.keySet()) {
            this.demand.put(r, duration.clone());
        }
        this.duration = duration.clone();
        this.subResourceDemand = subResourceDemand.clone();
    }

    @Override
    public LoadUnitStorage getOrigin() {
        return origin;
    }

    @Override
    public LoadUnitStorage getDestination() {
        return destination;
    }

    @Override
    public LoadUnit getLoadUnit() {
        return job.getLoadUnit();
    }

    @Override
    public FieldElement getDemand(Resource r) {
        return new DoubleValue(1.0);
    }

    @Override
    public void setDemand(Resource r, FieldElement rik) {
        throw new UnsupportedOperationException("Bedarf kann bei SingleResourceOperationen nicht angepasst werden");
    }

    @Override
    public FieldElement getDuration() {
        return duration;
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDuration(FieldElement duration) {
        this.duration = duration;
    }

    @Override
    public ConveyanceSystem getResource() {
        return conveyanceSystem;
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
        return this.demand.keySet();
    }

    public void setKeyPoints(List<LoadUnitPositionAndOrientation3DInTime> keyPoints) {
        this.keyPoints = keyPoints;
    }

    @Override
    public List<LoadUnitPositionAndOrientation3DInTime> getKeyPoints() {
        return keyPoints;
    }

    @Override
    public DefaultSingleResourceTransportOperation clone() {
        return new DefaultSingleResourceTransportOperation(number, origin, destination, job, conveyanceSystem, demand, duration, subResourceDemand);
    }

}
