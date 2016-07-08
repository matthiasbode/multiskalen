package bijava.graphics;

import bijava.geometry.dim2.Edge2d;
import bijava.geometry.dim2.Point2d;
 
public class IsoLine extends Edge2d {
    double value;
    
    public IsoLine(Point2d p0, Point2d p1, double value) {
        super(p0,p1);
        this.value = value;
    }
    
    public Point2d getP0() {
        return p0;
    }
    
    public Point2d getP1() {
        return p1;
    }
    
    public double getValue() {
        return value;
    }
}