package bijava.graphics.canvas2D;

import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.Polygon2d;
import java.awt.Color;
import java.awt.geom.GeneralPath;

public class Polygon2D extends GraphicShape{

    private static final long serialVersionUID = 1L;

    public Polygon2D(float x[], float y[]){
        super(new GeneralPath());
        gp.moveTo(x[0],y[0]);
        for (int i = 1; i < y.length; i++)
                gp.lineTo(x[i],y[i]);
        gp.closePath();
    }

    public Polygon2D(float x[], float y[], Color filled){
        super(new GeneralPath());
        this.filled = true;
        this.filledcolor = filled;
        gp.moveTo(x[0],y[0]);
        for (int i = 1; i < y.length; i++)
                gp.lineTo(x[i],y[i]);
        gp.closePath();
    }

    public Polygon2D(float x[], float y[], Color border, Color filled){
        super(new GeneralPath());
        this.color = border;
        this.filledcolor = filled;
        gp.moveTo(x[0],y[0]);
        for (int i = 1; i < y.length; i++)
                gp.lineTo(x[i],y[i]);
        gp.closePath();
    }

    public Polygon2D(Polygon2d poly) {
        super(new GeneralPath());
        Point2d[] pts = poly.getPoints();
        gp.moveTo(pts[0].x,pts[0].y);
        for (int i = 1; i < pts.length; i++)
                gp.lineTo(pts[i].x,pts[i].y);
        gp.closePath();
    }

    public Polygon2D(Polygon2d poly, Color border) {
        this(poly);
        this.color = border;
    }
}
