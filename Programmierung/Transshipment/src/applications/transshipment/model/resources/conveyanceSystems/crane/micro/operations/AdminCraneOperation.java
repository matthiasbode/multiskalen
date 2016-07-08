/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorage;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.FieldElement;
import math.LongValue;
import math.geometry.DynamicPolygonalRegion;

/**
 *
 * @author bode
 */
public class AdminCraneOperation extends CraneMicroTransportOperation {

    public AdminCraneOperation(Crane c, StartEndPosition origin, StartEndPosition destination) {
        super(null, c, origin, destination);
        setDuration(new LongValue(1));
    }

    public AdminCraneOperation(Crane c, StartEndPosition origin, StartEndPosition destination, DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor) {
        super(workingAreaRepresentationWithSettingUpCorridor, c, origin, destination);
        setDuration(new LongValue(1));
    }

    public AdminCraneOperation(DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor, DynamicPolygonalRegion workingAreaRepresentation, int number, LoadUnit lu, Map<Resource, FieldElement> demands, FieldElement duration, Crane cs, SubOperations subResourceDemand, LoadUnitStorage origin, LoadUnitStorage destination, RoutingTransportOperation routingTransportOperation) {
        super(workingAreaRepresentationWithSettingUpCorridor, workingAreaRepresentation, number, lu, demands, duration, cs, subResourceDemand, origin, destination, routingTransportOperation);
    }

    
    public static class StartEndPosition extends SimpleStorage {

        public Point3d pos;

        public StartEndPosition(Point3d pos) {
            super();
            this.pos = pos;
        }

        @Override
        public Area getGeneralOperatingArea() {
            Rectangle2D.Double aDouble = new Rectangle2D.Double(pos.x, pos.y, 1, 1);
            return new Area(aDouble);
        }

        @Override
        public Point2d getCenterOfGeneralOperatingArea() {
            return new Point2d(pos.x, pos.y);
        }

    }

    @Override
    public AdminCraneOperation clone() {
        return new AdminCraneOperation(workingAreaRepresentationWithSettingUpCorridor, workingAreaRepresentation, number, lu, demands, duration, this.cs, subResourceDemand, origin, destination, routingTransportOperation);
    }
    
    
}
