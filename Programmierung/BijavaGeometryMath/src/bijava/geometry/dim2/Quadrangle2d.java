package bijava.geometry.dim2;

import bijava.geometry.EuclideanPoint;
import bijava.geometry.NaturalElement;

/**
 * Beschreibt ein allgemeines konvexes Viereck
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @version 1.0
 */
public class Quadrangle2d extends ConvexPolygon2d {
    
    protected Quadrangle2d(){ 
        super();
        points = new Point2d[4];
    }
    
    /** Creates a new instance of Quadrangle2d */
    public Quadrangle2d(Point2d p0, Point2d p1, Point2d p2, Point2d p3) {
        super(new Point2d[]{p0,p1,p2,p3});
    }

    public Quadrangle2d(Quadrangle2d re) {
        super(re);
    }

    @Override
    public Quadrangle2d clone() {
        return new Quadrangle2d(this);
    }
    
    public double[] getBiLinearElementCoordinates(Point2d p) {
        double[] nek = new double[4];
        if (!(contains(p)))
            return nek;
        Quadrangle2d v;
        Point2d[] sp = getIntersectionPoints(p);
        double a = getArea();
        
        v = new Quadrangle2d(sp[1],points[2],sp[2],p);
        nek[0] = v.getArea()/a;
        v = new Quadrangle2d(sp[2],points[3],sp[3],p);
        nek[1] = v.getArea()/a;
        v = new Quadrangle2d(sp[3],points[0],sp[0],p);
        nek[2] = v.getArea()/a;
        v = new Quadrangle2d(sp[0],points[1],sp[1],p);
        nek[3] = v.getArea()/a;
        
        return nek;
    }
    
    private Point2d[] getSGK(){	// Schnittpunkte der gegenueber liegenden Kanten
        Point2d[] sgk = new Point2d[2];
        sgk[0] = AlgGeometry2d.getSchnittpunkt(points[0],points[1],points[2],points[3]);
        sgk[1] = AlgGeometry2d.getSchnittpunkt(points[0],points[3],points[2],points[1]);
        return sgk;
    }
    
    private Point2d[] getIntersectionPoints(Point2d p){	// Schnittpunkte der Kanten und der Geraden durch den uebergebenen Punkt und die SGK
        
        Point2d[] sp = new Point2d[4];
        Point2d[] sgk = getSGK();
        
        if (sgk[0]==null){	// 0-1 und 2-3 sind parallel
            Point2d h = new Point2d(p.x+1,p.y+1);	// Hilspunkt auf der Geraden parallel zu 0-1 bzw. 2-3 durch p
            if (points[1].x==points[0].x)	// Steigung von 0-1 bzw. 2-3 ist unendlich
                h.x = p.x;
            else
                h.y = (points[1].y-points[0].y)/(points[1].x-points[0].x) * (h.x-p.x) + p.y;
            
            sp[1] = AlgGeometry2d.getSchnittpunkt(points[1],points[2],p,h);
            sp[3] = AlgGeometry2d.getSchnittpunkt(points[3],points[0],p,h);
        }else{
            sp[1] = AlgGeometry2d.getSchnittpunkt(points[1],points[2],p,sgk[0]);
            sp[3] = AlgGeometry2d.getSchnittpunkt(points[3],points[0],p,sgk[0]);
        }
        
        if (sgk[1]==null){
            Point2d h = new Point2d(p.x+1,p.y+1);	// Hilspunkt auf der Geraden parallel zu 0-3 bzw. 1-2 durch p
            if (points[3].x==points[0].x)	// Steigung von 0-3 bzw. 1-2 ist unendlich
                h.x = p.x;
            else
                h.y = (points[3].y-points[0].y)/(points[3].x-points[0].x) * (h.x-p.x) + p.y;
            
            sp[0] = AlgGeometry2d.getSchnittpunkt(points[0],points[1],p,h);
            sp[2] = AlgGeometry2d.getSchnittpunkt(points[2],points[3],p,h);
        }else{
            sp[0] = AlgGeometry2d.getSchnittpunkt(points[0],points[1],p,sgk[1]);
            sp[2] = AlgGeometry2d.getSchnittpunkt(points[2],points[3],p,sgk[1]);
        }
        return sp;
    }
    
    
    /**
     * Gets the area.
     * Overrides the method in <code>SimplePolygon2d</code>.
     *
     * @return the area of this Quadrangle2d.
     */
    public double getArea() {
        return AlgGeometry2d.getArea(points[0],points[1],points[2])+AlgGeometry2d.getArea(points[2],points[3],points[0]);
    }
    
    public boolean contains(Point2d p) {
        Triangle2d d1 = new Triangle2d(points[0],points[1],points[2]);
        Triangle2d d2 = new Triangle2d(points[0],points[3],points[2]);
        return (d1.contains(p) || d2.contains(p));
    }
}
