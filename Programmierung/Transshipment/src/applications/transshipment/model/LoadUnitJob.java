/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author bode
 */
public class LoadUnitJob extends MultiModeJob<RoutingTransportOperation> implements Comparable<LoadUnitJob> {

    private final LoadUnit loadUnit;
    private boolean notDirectlyTransportable = false;
    private HashMap<LoadUnitStorage, JobOperationList<RoutingTransportOperation>> toDNFStorageRoutes = new HashMap<>();
    private LoadUnitStorage currentStart;

    public LoadUnitJob(LoadUnit lu) {
        super();
        this.loadUnit = lu;
        this.currentStart = lu.getOrigin();

    }

    public HashMap<LoadUnitStorage, JobOperationList<RoutingTransportOperation>> getToDNFStorageRoutes() {
        return toDNFStorageRoutes;
    }

    /**
     * Hinterlegt DNF-Routen für die verschiedenen LoadUnitStorages der Routen.
     *
     * @param position
     * @param routing
     */
    public void addDNFStorageRoute(LoadUnitStorage position, JobOperationList<RoutingTransportOperation> routing) {
        this.toDNFStorageRoutes.put(position, routing);
    }

    public LoadUnit getLoadUnit() {
        return loadUnit;
    }

    public LoadUnitStorage getOrigin() {
        if (currentStart == null) {
            return loadUnit.getOrigin();
        }
        return currentStart;
    }

    public void setCurrentOrigin(LoadUnitStorage currentStart) {
        this.currentStart = currentStart;
    }

    public LoadUnitStorage getDestination() {
        return loadUnit.getDestination();
    }

    @Override
    public String toString() {
        return "LoadUnitJob{" + loadUnit.getID() + ": " + loadUnit.getOrigin() + "-->" + loadUnit.getDestination() + " }";
    }

    @Override
    public int compareTo(LoadUnitJob o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.loadUnit);
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
        final LoadUnitJob other = (LoadUnitJob) obj;
        if (!Objects.equals(this.loadUnit, other.loadUnit)) {
            return false;
        }
        return super.equals(obj);
    }

    public boolean isNotDirectlyTransportable() {
        return notDirectlyTransportable;
    }

    public void setNotDirectlyTransportable(boolean notDirectlyTransportable) {
        this.notDirectlyTransportable = notDirectlyTransportable;
    }
}
