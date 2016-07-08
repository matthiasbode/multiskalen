/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import javafx.animation.Interpolator;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import math.geometry.PositionAndOrientation3DInTime;

/**
 *
 * @author Matthias
 */
public class LoadUnitPositionAndOrientation3DInTime extends PositionAndOrientation3DInTime {

    private final Operation operation;
    Interpolator interpolator;

    public LoadUnitPositionAndOrientation3DInTime(Operation operation, Long time, Point3d position, Vector3d orientation) {
        super(time, position, orientation);
        this.operation = operation;
        if (operation instanceof StoreOperation) {
            interpolator = Interpolator.DISCRETE;
        }
        if (operation instanceof TransportOperation) {
            interpolator = Interpolator.LINEAR;
        }
    }

    public LoadUnitPositionAndOrientation3DInTime(Operation operation, Interpolator interpolator, Long time, Point3d position, Vector3d orientation) {
        super(time, position, orientation);
        this.operation = operation;
        this.interpolator = interpolator;
    }

    public LoadUnitPositionAndOrientation3DInTime(Operation operation, Interpolator interpolator, Long time, Point2d position, Vector3d orientation) {
        super(time, position, orientation);
        this.operation = operation;
        this.interpolator = interpolator;
    }
    

    public LoadUnitPositionAndOrientation3DInTime(Operation operation, Long time, Point2d position, Vector3d orientation) {
        super(time, position, orientation);
        this.operation = operation;
        if (operation instanceof StoreOperation) {
            interpolator = Interpolator.DISCRETE;
        }
        if (operation instanceof TransportOperation) {
            interpolator = Interpolator.LINEAR;
        }
    }

    public Operation getOperation() {
        return operation;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

}
