/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geometry;

import applications.mmrcsp.model.operations.Operation;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author bode
 */
public class PositionAndOrientation3DInTime {

    private Long time;
    private final Point3d position;
    private final Vector3d orientation;
    

    public PositionAndOrientation3DInTime(Long time, Point3d position, Vector3d orientation) {
        this.time = time;
        this.position = position;
        this.orientation = orientation;
    }

    public PositionAndOrientation3DInTime(Long time, Point2d position, Vector3d orientation) {
        this.time = time;
        this.position = new Point3d(position.x, position.y, 0);
        this.orientation = orientation;
    }

    public Vector3d getOrientation() {
        return orientation;
    }

    public Point3d getPosition() {
        return position;
    }

    public Long getTime() {
        return time;
    }

    public void moveInTime(Long dt) {
        this.time += dt;
    }

    @Override
    public String toString() {
        return "PositionAndOrientation3DInTime{" + "time=" + time + ", position=" + position + ", orientation=" + orientation + '}';
    }

}
