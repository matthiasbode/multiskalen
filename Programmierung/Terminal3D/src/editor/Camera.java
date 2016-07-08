/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 *
 * @author hagedorn
 */
public class Camera extends Group {

    double insets = 10;

    /**
     * Translation für den View des Users
     */
    Translate t = new Translate();

    /**
     * Sicherstellung, dass Rotation und Skalierung relative zum Center
     * stattfindet.
     */
    Translate p = new Translate();
    Translate ip = new Translate();

    Scale s = new Scale();

    SubScene scene;

    public Camera() {
        super();
//        getTransforms().addAll(t, p, rx, rz, ry, s, ip);
    }

    public void setScene(SubScene scene) {
        this.scene = scene;
    }

    public void setLocalPivot(double x, double y, double z) {
        ip.setX(-x);
        ip.setY(-y);
        ip.setZ(-z);
        p.setX(x);
        p.setY(y);
        p.setZ(z);
    }

    public void zoomOnPoint(double scale, Point3D screenPoint) {
//        Point3D worldPoint = parentToLocal(screenPoint);
//        setLocalPivot(worldPoint.getX(), worldPoint.getY(), worldPoint.getZ());
        this.s.setX(scale);
        this.s.setY(scale);
        this.s.setZ(scale);
//        Point3D newWorldPoint = parentToLocal(screenPoint);
//        double dx = (newWorldPoint.getX() - worldPoint.getX());
//        double dy = (newWorldPoint.getY() - worldPoint.getY());
//        this.t.setX(this.t.getX() + dx * scale);
//        this.t.setY(this.t.getY() + dy * scale);
    }

    public void zoom(double scale) {
        this.s.setX(scale);
        this.s.setY(scale);
        this.s.setZ(scale);
    }

    public void resetCam2D(Bounds b, double width, double height) {

        this.getTransforms().clear();
        Rectangle rectangle = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());

        List<Transform> updateTransform = WorldToScreenConverter.updateTransform(rectangle, width, height);
        
        this.getTransforms().add(t);
        this.getTransforms().add(p);
        this.getTransforms().addAll(updateTransform);
        this.getTransforms().add(s);
        this.getTransforms().add(ip);

    }

}
