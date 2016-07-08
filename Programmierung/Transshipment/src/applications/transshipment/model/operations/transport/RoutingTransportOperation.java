/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.operations.transport;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.modes.JobOperation;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.routing.TransferArea;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import math.DoubleValue;
import math.FieldElement;
import org.util.Pair;

/**
 * Eine makroskopische Transportoperation beschreibt lediglich Start und Ziel
 * eines Transportes, das ausführende ConveyanceSystem und den Ressourcenbedarf.
 *
 * @author bode
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class RoutingTransportOperation implements JobOperation<RoutingTransportOperation> {

    private JobOperationList<RoutingTransportOperation> routing;
    private LoadUnitJob job;
    private Pair<TransferArea, TransferArea> pair;
    private Map<Resource, FieldElement> demands;
    private FieldElement duration;
    private ConveyanceSystem cs;
    private static int counter = 0;
    private final int id;

    public RoutingTransportOperation(TransferArea origin, TransferArea destination, LoadUnitJob job, ConveyanceSystem cs) {
        this.pair = new Pair<>(origin, destination);
        this.job = job;
        this.demands = new LinkedHashMap<>();
        this.demands.put(cs, new DoubleValue(1.0));
        this.cs = cs;
        this.id = counter++;
    }

  

    public TransferArea getFirst() {
        return this.pair.getFirst();
    }

    public TransferArea getSecond() {
        return this.pair.getSecond();
    }


    public LoadUnitStorage getOrigin() {
        return this.getFirst().getStorageSystem();
    }

    public LoadUnitStorage getDestination() {
        return this.getSecond().getStorageSystem();
    }

    public LoadUnit getLoadUnit() {
        return job.getLoadUnit();
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
    public void setDuration(FieldElement duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return ": LU [" + job.getLoadUnit().getID() + "]FROM [" + this.getFirst().getStorageSystem() + "] TO [" + this.getSecond().getStorageSystem() + "] VIA [" + this.getRequieredResources() + "]" + " Duration:" + this.getDuration();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.getId();
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
        final RoutingTransportOperation other = (RoutingTransportOperation) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        return true;
    }

    public ConveyanceSystem getResource() {
        return cs;
    }

    public LoadUnitJob getJob() {
        return job;
    }

    @Override
    public void setRouting(JobOperationList<RoutingTransportOperation> r) {
        this.routing = r;
    }

    @Override
    public JobOperationList<RoutingTransportOperation> getRouting() {
        return routing;
    }

    @XmlValue
    @Override
    public int getId() {
        return id;
    }

    @Override
    public RoutingTransportOperation clone() {
       throw new UnsupportedOperationException("RoutingTransportOperations dürfen nicht geklont werden");
    }

}
