/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs;

import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */
public class HandoverPoint extends SimpleStorageRow implements LCSHandover {

    public static final String PREFIX = "HandoverPoint";
    private HashMap<StorageLocation, RackNode> nodes;

    public HandoverPoint(Rectangle2D area) {
        super(area);
        makeID(PREFIX);
        createRackNodes();
    }

    public HandoverPoint(SimpleStorageRow row) {
        super(row);
        makeID(PREFIX);
        createRackNodes();
    }

    public void createRackNodes() {
        nodes = new HashMap<>();

        for (StorageLocation storageLocation : this.getStorageLocations()) {
            Point2d centerOfGeneralOperatingArea = storageLocation.getCenterOfGeneralOperatingArea();
            RackNode rackNode = new RackNode(new bijava.geometry.dim2.Point2d(centerOfGeneralOperatingArea.x, centerOfGeneralOperatingArea.y));
            nodes.put(storageLocation, rackNode);
        }

    }

    public Collection<RackNode> getRackNodes() {
        return nodes.values();
    }

    public Map<StorageLocation, RackNode> getNodes() {
        return nodes;
    }

    @Override
    public SimpleStorageRow.SimpleSubStorageRow getSubResource(Area area) {
        SimpleSubStorageRow subResource = super.getSubResource(area);
        if (subResource != null) {
            return new SubHandoverPoint(this, subResource);
        } else {
            return null;
        }
    }

    public static class SubHandoverPoint extends SimpleStorageRow.SimpleSubStorageRow implements LCSHandover {

        private HashMap<StorageLocation, RackNode> nodes = new HashMap<>();

        public SubHandoverPoint(LCSHandover point, SimpleStorageRow.SimpleSubStorageRow row) {
            super(row.getStorageLocations(), row);
            makeID("Sub" + HandoverPoint.PREFIX);
            this.setTemporalAvailability(row.getTemporalAvailability());

            for (StorageLocation storageLocation : row.getStorageLocations()) {
                RackNode racknode = point.getNodes().get(storageLocation);
                if (racknode == null) {
                    throw new IllegalArgumentException("Kein Node gefunden.");
                }
                this.nodes.put(storageLocation, racknode);
            }
        }

        @Override
        public HashMap<StorageLocation, RackNode> getNodes() {
            return nodes;
        }

    }
}
