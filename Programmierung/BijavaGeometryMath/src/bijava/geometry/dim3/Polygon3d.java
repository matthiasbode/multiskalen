package bijava.geometry.dim3;

/**
 * Polygon3d.java provides attributes and methods for a
 * closed polygon threedimensional space.
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 1.0, october 2006
 */
public class Polygon3d {
    protected Point3d[] points; // array of points
    
    /**
     * Constructs a threedimensional polygon.
     * @param points array of points.
     */
    public Polygon3d(Point3d[] points) {
        if (points.length < 3) throw new IllegalArgumentException("less than three points");
        this.points = points;
    }
    
    /**
     * Gets the points of this polygon as array.
     * @return points of this polygon as array.
     */
    public Point3d[] getPoints() {
        return points;
    }
    
    /**
     * Gets a point of this polygon.
     * @param index save index into the array of points.
     * @return point of this polygon.
     */
    public Point3d getPoint(int index) {
        if (index < 0 || index > points.length - 1) throw new IllegalArgumentException("index out of range");
        return points[index];
    }
    
    /**
     * Gets the length of this polygon.
     * @return length of this polygon.
     */
    public double getLength() {
        double l = 0.;
        for (int i = 0; i < points.length - 1; i++)
            l += points[i].distance(points[i + 1]);
        l += points[points.length - 1].distance(points[0]);
        return l;
    }
    
    /**
     * Get the length of the edge at the specified index.
     * @param index index of the edge, beginning with 0.
     * @return length of the edge at the specified index.
     */
    public double getEdgeLength(int index) {
        if (index < 0 || index > points.length - 1) throw new IllegalArgumentException("index out of range");
        return points[index].distance(points[(index + 1) % points.length]);
    }
    
    /**
     * Gets the bounding box of this polygon.
     * @return bounding box of this polygon.
     */
    public BoundingBox3d getBoundingBox() {
        return new BoundingBox3d(points);
    }
    
    /**
     * Tests this polygon for congruence with <code>other</code>.
     * @param polygon threedimensional polygon.
     * @return <code>true</code> if this polygon is congruent with <code>other</code>.
     */
    public boolean isCongruent(Polygon3d polygon) {
        if (polygon == this) return true;
        if (polygon == null) return false;
        if (points.length != polygon.points.length) return false;
        int numberOfEqualEdges = 0;
        int numberOfEqualAntiorientedEdges = 0;
        for (int i = 0; i < points.length; i++)
            for (int j = 0; j < polygon.points.length; j++) {
            if (points[i].equals(polygon.points[j]) && points[(i+1)%points.length].equals(polygon.points[(j+1)%polygon.points.length])) {
                numberOfEqualEdges++;
                break;
            } else if (points[i].equals(polygon.points[(j+1)%polygon.points.length]) && points[(i+1)%points.length].equals(polygon.points[j])) {
                numberOfEqualAntiorientedEdges++;
                break;
            }
            }
        return (numberOfEqualEdges == points.length || numberOfEqualAntiorientedEdges == points.length);
    }
    
    /**
     * Tests this polygon for equality with other <code>Object</code>.
     * Overrides the method in <code>Object</code>.
     * @param o object.
     * @return <code>true</code> if this polygon equals other <code>Object</code>.
     */
    public boolean equals(Object o){
        if(o instanceof Polygon3d) return (this.equals((Polygon3d) o));
        return false;
    }
    
    /**
     * Tests this polygon for equality with <code>other</code>.
     * @param polygon twodimensional polygon.
     * @return <code>true</code> if this polygon equals <code>other</code>.
     */
    public boolean equals(Polygon3d polygon) {
        if (polygon == this) return true;
        if (polygon == null) return false;
        if (points.length != polygon.points.length) return false;
        int numberOfEqualEdges = 0;
        for (int i = 0; i < points.length; i++)
            for (int j = 0; j < polygon.points.length; j++)
                if (points[i].equals(polygon.points[j]) && points[(i + 1) % points.length].equals(polygon.points[(j + 1) % polygon.points.length])) {
            numberOfEqualEdges++;
            break;
                }
        return (numberOfEqualEdges == points.length);
    }
    
    /**
     * Converts this polygon into a <code>String</code>.
     * return the attributes of this polygon as <code>String</code>.
     */
    public String toString() {
        String s = super.toString() + ": " + points[0];
        for (int i = 1; i < points.length; i++) s += ", " + points[i];
        return s;
    }
}