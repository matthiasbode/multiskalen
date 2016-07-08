/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.micro.operations;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.operations.SubOperations;
import applications.transshipment.model.basics.util.LoadUnitPositionAndOrientation3DInTime;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.transport.MultiScaleTransportOperation;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.animation.Interpolator;
import javax.vecmath.Point3d;
import math.FieldElement;
import math.geometry.DynamicPolygonalRegion;
import math.geometry.ParametricLinearCurve3d;

/**
 *
 * @author bode
 */
public class CraneMicroTransportOperation extends MultiScaleTransportOperation<Crane> {

    DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor;
    DynamicPolygonalRegion workingAreaRepresentation;

    public CraneMicroTransportOperation(DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor, Crane cs, LoadUnitStorage origin, LoadUnitStorage destination) {
        super(cs, origin, destination);
        this.workingAreaRepresentationWithSettingUpCorridor = workingAreaRepresentationWithSettingUpCorridor;
    }

    public CraneMicroTransportOperation(RoutingTransportOperation routingTransportOperation, LoadUnitStorage origin, LoadUnitStorage destination, DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor, DynamicPolygonalRegion workingAreaRepresentation) {
        super(routingTransportOperation, origin, destination);
        this.workingAreaRepresentationWithSettingUpCorridor = workingAreaRepresentationWithSettingUpCorridor;
        this.workingAreaRepresentation = workingAreaRepresentation;
    }

    public CraneMicroTransportOperation(DynamicPolygonalRegion workingAreaRepresentationWithSettingUpCorridor, DynamicPolygonalRegion workingAreaRepresentation, int number, LoadUnit lu, Map<Resource, FieldElement> demands, FieldElement duration, Crane cs, SubOperations subResourceDemand, LoadUnitStorage origin, LoadUnitStorage destination, RoutingTransportOperation routingTransportOperation) {
        super(number, lu, demands, duration, cs, subResourceDemand, origin, destination, routingTransportOperation);
        this.workingAreaRepresentationWithSettingUpCorridor = workingAreaRepresentationWithSettingUpCorridor.clone();
        this.workingAreaRepresentation = workingAreaRepresentation.clone();
        this.workingAreaRepresentation.moveToZero();
        this.workingAreaRepresentationWithSettingUpCorridor.moveToZero();
    }

    public DynamicPolygonalRegion getWorkingAreaRepresentationWithSettingUpCorridor() {
        return workingAreaRepresentationWithSettingUpCorridor;
    }

    public DynamicPolygonalRegion getWorkingAreaRepresentation() {
        return workingAreaRepresentation;
    }

    /**
     *
     */
    @Override
    public void setStandardKeyPoints() {
        List<LoadUnitPositionAndOrientation3DInTime> points = new ArrayList<>();
        ParametricLinearCurve3d transportMotion = CraneMotionCalculator.getTransportMotion(this.getResource(), this.getOrigin(), this.getDestination(), this.getLoadUnit());
        boolean add = false;
        for (int i = 0; i < transportMotion.numberOfPts(); i++) {
            Point3d pointAt = transportMotion.getPointAt(i);
            long timeAt = transportMotion.getTimeAt(i);
            Interpolator interpolator = Interpolator.LINEAR;
             
            LoadUnitPositionAndOrientation3DInTime p = new LoadUnitPositionAndOrientation3DInTime(this, interpolator, timeAt, pointAt, null);
            if (p.getPosition().equals(this.getOrigin().getPosition())) {
                add = true;
            }
            if (add) {
                points.add(p);
            }
            if (p.getPosition().equals(this.getDestination().getPosition())) {
                break;
            }
        }
        this.setKeyPoints(points);
    }

    @Override
    public CraneMicroTransportOperation clone() {
        return new CraneMicroTransportOperation(workingAreaRepresentationWithSettingUpCorridor, workingAreaRepresentation, number, lu, demands, duration, this.cs, subResourceDemand, origin, destination, routingTransportOperation);
    }

}
