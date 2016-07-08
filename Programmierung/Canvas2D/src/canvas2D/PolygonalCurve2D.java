/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package canvas2D;

import bijava.geometry.dim2.Edge2d;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.PolygonalCurve2d;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

public class PolygonalCurve2D extends GraphicShape {

    private static final long serialVersionUID = 1L;

    public PolygonalCurve2D(float x[], float y[]) {
        super(new GeneralPath());
        gp.moveTo(x[0], y[0]);
        for (int i = 1; i < y.length; i++) {
            gp.lineTo(x[i], y[i]);
        }
//        gp.closePath();
    }

    public PolygonalCurve2D(double x[], double y[]) {
        super(new GeneralPath.Double());
        gp.moveTo(x[0], y[0]);
        for (int i = 1; i < y.length; i++) {
            gp.lineTo(x[i], y[i]);
        }
//        gp.closePath();
    }

    public PolygonalCurve2D(Edge2d edge) {
        this(new double[]{edge.p0.x, edge.p1.x}, new double[]{edge.p0.y, edge.p1.y});
    }

    public PolygonalCurve2D(float x[], float y[], Color filled) {
        super(new GeneralPath());
        this.filled = true;
        this.filledcolor = filled;
        gp.moveTo(x[0], y[0]);
        for (int i = 1; i < y.length; i++) {
            gp.lineTo(x[i], y[i]);
        }
//        gp.closePath();
    }

    public PolygonalCurve2D(float x[], float y[], Color border, Color filled) {
        super(new GeneralPath());
        this.color = border;
        this.filledcolor = filled;
        gp.moveTo(x[0], y[0]);
        for (int i = 1; i < y.length; i++) {
            gp.lineTo(x[i], y[i]);
        }
//        gp.closePath();
    }

    public PolygonalCurve2D(PolygonalCurve2d poly) {
        super(new Path2D.Double());
        Point2d[] pts = poly.getPoints();
        gp.moveTo(pts[0].x, pts[0].y);
        for (int i = 1; i < pts.length; i++) {
            gp.lineTo(pts[i].x, pts[i].y);
        }
//        gp.closePath();
    }

    public PolygonalCurve2D(PolygonalCurve2d poly, Color border) {
        this(poly);
        this.color = border;
    }
}
