package bijava.geometry.dim2;

import bijava.geometry.EuclideanPoint;
/** Rechteck mit zum koordinatensystem paralellen Kanten
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @version 1.0
 */
public class CartesianRectangle2d extends Rectangle2d
{
    
    public CartesianRectangle2d(Point2d p, double width, double height)
    {
        super();
        points[0] = p;
        points[1] = new Point2d(p.x+width,p.y);
        points[2] = new Point2d(p.x+width,p.y+height);
        points[3] = new Point2d(p.x,p.y+height);
        
        a=width*height;
    }

    public CartesianRectangle2d(CartesianRectangle2d re) {
        super(re);
    }

    @Override
    public CartesianRectangle2d clone() {
        return new CartesianRectangle2d(this);
    }
    
    public double[] getNaturalElementCoordinates(EuclideanPoint p) {
        if (p instanceof Point2d) return this.getNaturalElementCoordinates((Point2d)p);
        return null;
    }

    public double[] getNaturalElementCoordinates(Point2d p)	// natuerliche Elementkoordinaten
    {
        double[] nec = new double[4];
        if (!(contains(p)))
            return nec;
        double a = getArea();

        nec[0] = (points[2].x-p.x)*(points[2].y-p.y)/a;
        nec[1] = (p.x-points[3].x)*(points[3].y-p.y)/a;
        nec[2] = (p.x-points[0].x)*(p.y-points[0].y)/a;
        nec[3] = (points[1].x-p.x)*(p.y-points[1].y)/a;
        return nec;
    }
    
    public double[] getNECDerivations(Point2d p, int variable)	// natuerliche Elementkoordinaten
    {
        double[] d = new double[4];
        if (!(contains(p)))
            return d;
        if (variable == 0)
        {
            d[0] = -1.0*(points[2].y-p.y)/a;
            d[1] = (points[3].y-p.y)/a;
            d[2] = (p.y-points[0].y)/a;
            d[3] = -1.0*(p.y-points[1].y)/a;
        }
        if (variable == 1)
        {
            d[0] = (points[2].x-p.x)*(-1.0)/a;
            d[1] = (p.x-points[3].x)*(-1.0)/a;
            d[2] = (p.x-points[0].x)/a;
            d[3] = (points[1].x-p.x)/a;
        }
        return d;
    }

    public boolean contains(Point2d p)
    {
        return ((p.x>=points[0].x) && (p.x<=points[1].x) && (p.y>=points[0].y) && (p.y<=points[3].y));
    }
}