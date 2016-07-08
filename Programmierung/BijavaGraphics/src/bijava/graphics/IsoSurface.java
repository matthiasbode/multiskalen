package bijava.graphics;

import bijava.geometry.dim3.Point3d;

public class IsoSurface {
    Point3d[] points;
    double minVal, maxVal;
    
    public IsoSurface(Point3d[] points, double minVal, double maxVal) {
        this.points = points;
        this.minVal = minVal;
        this.maxVal = maxVal;
    }
    
    public Point3d[] getPoints() {
        return points;
    }
    
    public double getValue() {
        return (minVal + maxVal) / 2.;
    }
}