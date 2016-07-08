package bijava.geometry.dim3;

/**
 * Edge3d.java
 * @author Hoecker
 * @version 0.1
 */
public class Edge3d {
    
    public Point3d p0, p1;
    
    public Edge3d(Point3d p0, Point3d p1) {
        this.p0 = p0;
        this.p1 = p1;
    }
    
    public double getLength() {
        return p0.distance(p1);
    }
    
}