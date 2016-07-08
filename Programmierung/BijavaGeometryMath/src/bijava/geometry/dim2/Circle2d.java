package bijava.geometry.dim2;

/**
 * @author kaapke
 */
public class Circle2d implements Region2d{
    
    public Point2d m;
    public double r;
            
    public Circle2d(Point2d m, double r) {
        if (r < 0) throw new IllegalArgumentException("radius < 0");
        this.m = m;
        this.r = r;
    }

    /** 
     * Der Kreis enthaelt p, wenn die Kreisungleich mit einer Genauigkeit von eps erfuellt ist.
     * @param p
     * @return True, falls der Kreis den Punkt p enthaelt.
     */
    public boolean contains(Point2d p) {
        return ( m.distance(p) <= r);
    }
    
    public String toString() {
        return new String("Circle: MP=" + m + ", r=" + r);
    }
}
