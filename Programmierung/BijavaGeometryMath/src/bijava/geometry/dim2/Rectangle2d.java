package bijava.geometry.dim2;

import bijava.geometry.EuclideanPoint;
/** Rechteck in algemeiner Lage 
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @version 1.0
 */
public class Rectangle2d extends Quadrangle2d{
    
    protected double a; // Area
    
    protected Rectangle2d(){ super();};
    
    public Rectangle2d(Point2d p, Vector2d b, double height) {
        super();
        points[0] = p;
        points[1] = p.add(new Point2d(b.x,b.y));
        Point2d h = new Point2d(-b.y,b.x);
        h = h.mult(height/h.norm());
        points[3] = p.add(h);
        points[2] = points[1].add(h);
        
        a = b.length()*height;
    }
    public Rectangle2d(Rectangle2d re) {
        super(re);
    }

    @Override
    public Rectangle2d clone() {
        return new Rectangle2d(this);
    }
    
    public double[] getNaturalElementCoordinates(EuclideanPoint p) {
        if (p instanceof Point2d) return getBiLinearElementCoordinates((Point2d)p);
        return null;
    }
    
    /**
     * Computes the natural element coordinates of a twodimensional point to this rectangle.
     * @param p  a twodimensional point.
     * @return an array with the natural element coordinates of the twodimensional point to this rectangle.
     */
    public double[] getNaturalElementCoordinates(Point2d p) {
        return getBiLinearElementCoordinates(p);
    }
    
    private Point2d[] getIntersectionPoints(Point2d p){	// Schnittpunkte der Kanten und der Geraden durch den uebergebenen Punkt und die SGK
        Point2d[] sp = new Point2d[4];
        
        Point2d h = new Point2d(p.x+1,p.y+1);	// Hilspunkt auf der Geraden parallel zu 0-1 bzw. 2-3 durch p
        if (points[1].x==points[0].x)	// Steigung von 0-1 bzw. 2-3 ist unendlich
            h.x = p.x;
        else
            h.y = (points[1].y-points[0].y)/(points[1].x-points[0].x) * (h.x-p.x) + p.y;
        sp[1] = AlgGeometry2d.getSchnittpunkt(points[1],points[2],p,h);
        sp[3] = AlgGeometry2d.getSchnittpunkt(points[3],points[0],p,h);
        
        h = new Point2d(p.x+1,p.y+1);	// Hilfspunkt auf der Geraden parallel zu 0-3 bzw. 1-2 durch p
        if (points[3].x==points[0].x)	// Steigung von 0-3 bzw. 1-2 ist unendlich
            h.x = p.x;
        else
            h.y = (points[3].y-points[0].y)/(points[3].x-points[0].x) * (h.x-p.x) + p.y;
        sp[0] = AlgGeometry2d.getSchnittpunkt(points[0],points[1],p,h);
        sp[2] = AlgGeometry2d.getSchnittpunkt(points[2],points[3],p,h);
        return sp;
    }
    
    public double getArea() {
        return a;
    }
    
}
