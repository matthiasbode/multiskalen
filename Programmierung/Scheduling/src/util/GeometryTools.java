/*
 * To change this template, choose GeometryTools | Templates
 * and open the template in the editor.
 */
package util;

import bijava.geometry.dim2.PolygonalCurve2d;
import java.awt.Shape;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class GeometryTools {

    /**
     * Ermittelt, ob b vollstaendig in a enthalten ist.
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean areaContainsArea(Area a, Area b) {
        Area a_ = (Area) a.clone();
        a_.add(b);
        return a.equals(a_);
    }

    public static double distancePointLine(Pair<Point2d, Point2d> edge, Point2d p1) {
        Vector3d b = new Vector3d(edge.getSecond().x - edge.getFirst().x, edge.getSecond().y - edge.getFirst().y, 0);
        Vector3d p_a = new Vector3d(p1.x - edge.getFirst().x, p1.y - edge.getFirst().y, 0);
        Vector3d d = new Vector3d();
        d.cross(b, p_a);
        return d.length() / b.length();
    }

    public static Point2d transform(AffineTransform transform, Point2d p) {
        Point2D tmp = new Point2D.Double(p.x, p.y);
        transform.transform(tmp, tmp);
        return new Point2d(tmp.getX(), tmp.getY());
    }

    public static Point2d[] transform(AffineTransform transform, Point2d[] points) {
        Point2d[] res = new Point2d[points.length];
        for (int i = 0; i < points.length; i++) {
            Point2d p = points[i];
            Point2D tmp = new Point2D.Double(p.x, p.y);
            Point2D transformed = transform.transform(tmp, null);
            res[i] = new Point2d(transformed.getX(), transformed.getY());
        }
        return res;
    }

    public static Vector3d getOrientation(AffineTransform localToWorld) {
        Point2D pStart = new Point2D.Double(0, 0);
        Point2D pEnde = new Point2D.Double(1, 0);
        localToWorld.transform(pStart, pStart);
        localToWorld.transform(pEnde, pEnde);
        Vector3d orientation = new Vector3d(pEnde.getX() - pStart.getX(),
                pEnde.getY() - pStart.getY(), 0);
        orientation.normalize();
        return orientation;
    }

    public static Vector3d getOrientation(AffineTransform localToWorld, Point2d first, Point2d second) {
        AffineTransform transformation = getRotationTransformation(first, second);
        transformation.preConcatenate(localToWorld);
        Point2D pStart = new Point2D.Double(0, 0);
        Point2D pEnde = new Point2D.Double(second.distance(first), 0);

        transformation.transform(pStart, pStart);
        transformation.transform(pEnde, pEnde);
        Vector3d orientation = new Vector3d(pEnde.getX() - pStart.getX(),
                pEnde.getY() - pStart.getY(), 0);
        orientation.normalize();
        return orientation;
    }

    public static AffineTransform getRotationTransformation(Point2d first, Point2d second) {
        Vector2d xAxis = new Vector2d(1, 0);

        Point2d bP1 = first.x < second.x ? first : second;
        Point2d bP2 = bP1.equals(first) ? second : first;

        Vector2d v = new Vector2d();
        v.sub(bP2, bP1);

        AffineTransform currentTransformation = new AffineTransform();
        double theta = xAxis.angle(v);

        if (bP2.x > bP1.x && bP2.y < bP1.y) {
            theta *= -1;
        }
        currentTransformation.rotate(theta);
        currentTransformation.preConcatenate(AffineTransform.getTranslateInstance(bP1.x, bP1.y));
        return currentTransformation;
    }

    public static AffineTransform getBasisTransformation(AffineTransform localToWorld, AffineTransform tranformation) {
        try {
            AffineTransform at = new AffineTransform();
            at.concatenate(localToWorld.createInverse());
            at.concatenate(tranformation);
            at.concatenate(localToWorld);
            AffineTransform res = new AffineTransform(localToWorld);
            res.concatenate(at);
            return res;
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(GeometryTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static bijava.geometry.dim2.Point2d[] getBoxPoints(java.awt.geom.Point2D p1, java.awt.geom.Point2D p2, double breite) {
        return getBoxPoints(new bijava.geometry.dim2.Point2d(p1.getX(), p1.getY()), new bijava.geometry.dim2.Point2d(p2.getX(), p2.getY()), breite);
    }

    /**
     * Diese Methode gibt die Punkte der Box an, die um die Kante von p1 zu p2
     * entsteht, wenn man senkrecht zu dieser eine Kante um die Hälfte der
     * Breite nach oben und um die Hälfte der breite nach unten verschiebt. Die
     * Reihenfolge der Punkte ist dabei im Uhrzeigersinn, beginnent beim Knoten,
     * der durch die Addition der halben Senkrechten zum ersten Punkt entsteht,
     * gegeben.
     *
     * @param p1
     * @param p2
     * @return
     */
    public static bijava.geometry.dim2.Point2d[] getBoxPoints(bijava.geometry.dim2.Point2d p1, bijava.geometry.dim2.Point2d p2, double breite) {
        Vector2d vec = new Vector2d(p2.x - p1.x, p2.y - p1.y);
        vec.normalize();
        Vector2d normal = new Vector2d(-vec.y, vec.x);
        normal.scale(breite / 2.0);
        bijava.geometry.dim2.Point2d[] points = new bijava.geometry.dim2.Point2d[4];
        points[0] = new bijava.geometry.dim2.Point2d(p1);
        points[0].add(normal);
        points[3] = new bijava.geometry.dim2.Point2d(p1);
        points[3].sub(normal);
        points[1] = new bijava.geometry.dim2.Point2d(p2);
        points[1].add(normal);
        points[2] = new bijava.geometry.dim2.Point2d(p2);
        points[2].sub(normal);
        return points;
    }

    public static bijava.geometry.dim2.Point2d[] getNormalTranslatedPoints(bijava.geometry.dim2.Point2d p1, bijava.geometry.dim2.Point2d p2, double verschiebung) {
        Vector2d vec = new Vector2d(p2.x - p1.x, p2.y - p1.y);
        vec.normalize();
        Vector2d normal = new Vector2d(-vec.y, vec.x);
        normal.scale(verschiebung);
        bijava.geometry.dim2.Point2d[] points = new bijava.geometry.dim2.Point2d[4];
        points[0] = new bijava.geometry.dim2.Point2d(p1);
        points[0].add(normal);
        points[1] = new bijava.geometry.dim2.Point2d(p2);
        points[1].add(normal);
        return points;
    }

    public static PolygonalCurve2d getCurve(ArrayList<javax.vecmath.Point2d> nodes) {
        PolygonalCurve2d res = new PolygonalCurve2d();
        for (Point2d point2d : nodes) {
            res.appendPoint(new bijava.geometry.dim2.Point2d(point2d.x, point2d.y));
        }
        return res;
    }

    public static Shape createArrow(double startX, double startY, double endX, double endY, double width) {
        double dx = endX - startX;
        double dy = endY - startY;
        double D = (float) Math.sqrt(dx * dx + dy * dy);
        double z = (dx <= 0) ? startX - D : startX + D;
        double dec = (dx <= 0) ? width : -width;
        GeneralPath gp = new GeneralPath();
        // the shape on an horizontal line
        gp.moveTo(startX, startY - width / 2);
        gp.lineTo(z + dec, startY - width / 2);
        gp.lineTo(z + dec, startY - width);
        gp.lineTo(z, startY);
        gp.lineTo(z + dec, startY + width);
        gp.lineTo(z + dec, startY + width / 2);
        gp.lineTo(startX, startY + width / 2);
        gp.closePath();
        double alpha = (dx > 0) ? Math.asin(dy / D) : -Math.asin(dy / D);
        // transform the shape to follow the line direction
        return alpha != 0
                ? gp.createTransformedShape(AffineTransform.getRotateInstance(alpha, startX, startY))
                : gp;
    }
}
