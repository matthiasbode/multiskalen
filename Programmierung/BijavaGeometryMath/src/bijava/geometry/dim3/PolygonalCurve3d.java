package bijava.geometry.dim3;

/**
 * PolygonalCurve3d.java provides attributes and methods for a
 * polygonal curve in a threedimensional space.
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dr.-Ing. Martin Rose<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 1.0, october 2006
 */
public class PolygonalCurve3d implements Curve3d {
    protected Point3d[] points; // array of points
    
    /**
     * Constructs a threedimensional polygonal curve.
     * @param points array of points.
     */
    public PolygonalCurve3d(Point3d[] points) {
        if (points.length < 2) throw new IllegalArgumentException("number of points < 2");
        this.points = points;
    }
    
    /**
     * Gets the points of this polygonal curve as array.
     * qreturn points of this polygonal curve as array.
     */
    public Point3d[] getPoints() {
        return points;
    }
    
    /**
     * Gets a point of this polygonal curve.
     * @param index save index into the array of points.
     * @return point of this polygonal curve.
     */
    public Point3d getPointAt(int index) {
        if (index < 0 || index > points.length - 1) throw new IllegalArgumentException("index out of range");
        return points[index];
    }
    
    /**
     * Gets the point to an argument s.
     * @param s the argument s.
     * @return the point on this polygonal curve to the argument s.
     */
    public Point3d getPoint(double s) {
        if (s < 0.) return null;
        if (s == 0.) return points[0];
        double l = 0.;
        double d = 0.;
        for (int i = 1; i < points.length; i++) {
            d = points[i - 1].distance(points[i]);
            l += d;
            if (l >= s) {
                double sf = 1. - ((l - s) / d);
                Point3d p1 = points[i - 1];
                Point3d p2 = points[i];
                Point3d p = new Point3d();
                p.x = p1.x + sf * (p2.x - p1.x);
                p.y = p1.y + sf * (p2.y - p1.y);
                p.z = p1.z + sf * (p2.z - p1.z);
                return p;
            }
        }
        return null;
    }
    
    /**
     * Gets the length of this polygonal curve.
     * @return length of this polygonal curve.
     */
    public double getLength() {
        double l = 0.;
        for (int i = 0; i < points.length - 1; i++)
            l += points[i].distance(points[i + 1]);
        return l;
    }
    
    /**
     * Gets the bounding box of this polygonal curve.
     * @return bounding box of this polygonal curve.
     */
    public BoundingBox3d getBoundingBox() {
        return new BoundingBox3d(points);
    }
    
    /**
     * Gets a part of this polygonal curve.
     * @param index0 save index of the first point of the part of this polygonal curve.
     * @param index1 save index of the last point of the part of this polygonal curve.
     * @return a part of this polygonal curve as <code>PolygonalCurve3d</code>.
     */
    public PolygonalCurve3d getPart(int index0, int index1) {
        if (index0 < 0 || index0 > points.length - 1 || index1 < 0 || index1 > points.length - 1)
            throw new IllegalArgumentException("index out of range");
        int size = Math.abs(index1 - index0) + 1;
        Point3d[] pts = new Point3d[size];
        if (index0 < index1)
            for (int i = 0; i < size; i++)
                pts[i] = points[index0 + i];
        else
            for (int i = 0; i < size; i++)
                pts[i] = points[index1 + i];
        return new PolygonalCurve3d(pts);
    }
    
    /**
     * Closes this polygonal curve and constructs a polygon.
     * @return a polygon in a threedimensional space.
     */
    public Polygon3d close() {
        return new Polygon3d(points);
    }
    
    /**
     * Tests this polygonal curve for congruence with <code>other</code>.
     * @param curve threedimensional polygonal curve.
     * @return <code>true</code> if this polygonal curve is congruent with <code>other</code>.
     */
    public boolean isCongruent(PolygonalCurve3d curve) {
        if (curve == this) return true;
        if (curve == null) return false;
        if (points.length != curve.points.length) return false;
        if (points[0].equals(curve.points[0])) {
            for (int i = 1; i < points.length; i++)
                if (!points[i].equals(curve.points[i]))
                    return false;
            return true; }
        if (points[0].equals(curve.points[points.length - 1])) {
            for (int i = 1; i < points.length; i++)
                if (!points[i].equals(curve.points[points.length - 1 - i]))
                    return false;
            return true; }
        return false;
    }
    
    /**
     * Tests this polygonal curve for equality with other <code>Object</code>.
     * Overrides the method in <code>Object</code>.
     * @param o object.
     * @return <code>true</code> if this polygonal curve equals other <code>Object</code>.
     */
    public boolean equals(Object o) {
        if(o instanceof PolygonalCurve3d) return (this.equals((PolygonalCurve3d) o));
        return false;
    }
    
    /**
     * Tests this polygonal curve for equality with <code>other</code>.
     * @param curve threedimensional polygonal curve.
     * @return <code>true</code> if this polygonal curve equals <code>other</code>.
     */
    public boolean equals(PolygonalCurve3d curve) {
        if (curve == this) return true;
        if (curve == null) return false;
        if (points.length != curve.points.length) return false;
        for (int i = 0; i < points.length; i++)
            if (!points[i].equals(curve.points[i]))
                return false;
        return true;
    }
    
    /**
     * Converts this polygonal curve into a <code>String</code>.
     * return the attributes of this polygonal curve as <code>String</code>.
     */
    public String toString() {
        String s = super.toString() + ": " + points[0];
        for (int i = 1; i < points.length; i++) s += ", " + points[i];
        return s;
    }
}