package bijava.geometry.dim2;

import bijava.geometry.EuclideanPoint;
import java.io.Serializable;
import java.util.List;

/**
 * Polygon2d.java is a class for a closed curve as a po in a twodimensional space.
 * 
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 1.3, May 2008
 */
public class Polygon2d extends PolygonalCurve2d implements Cloneable, Serializable {

//    protected Point2d[] points; // array of points

    protected Polygon2d() {
        super();
    }

    /**
     * Constructs a twodimensional po from three points p0, p1, p2.
     * 
     * @param p0 1st point.
     * @param p1 2nd point.
     * @param p2 3rd point.
     */
    public Polygon2d(Point2d p0, Point2d p1, Point2d p2) {
        points = new Point2d[3];
        points[0] = p0;
        points[1] = p1;
        points[2] = p2;
    }

    /**
     * Constructs a twodimensional po from an array of points pts.
     * 
     * @param pts array of points.
     */
    public Polygon2d(Point2d[] pts) {
        if (pts.length < 3) {
            throw new IllegalArgumentException("Number of points < 3.");
        }
        points = pts;
    }

    /**
     * Constructs a twodimensional po from a collection of points.
     * 
     * @param coll collection of points.
     */
    public Polygon2d(List<? extends Point2d> coll) {
        points = coll.toArray(new Point2d[coll.size()]);
    }

    /**
     * Constructs a twodimensional po from a bounding box bb.
     * 
     * @param bb bounding box.
     */
    public Polygon2d(BoundingBox2d bb) {
        points = new Point2d[4];
        points[0] = bb.getP0();
        points[1] = bb.getP1();
        points[2] = bb.getP2();
        points[3] = bb.getP3();
    }

    /**
     * Constructs a twodimensional polygon from a polygon pg with cloning the points.
     *
     * @param pg polygon.
     */
    public Polygon2d(Polygon2d pg) {
        points = new Point2d[pg.points.length];

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point2d(pg.points[i]);
        }
    }

//    @Override
//    public Polygon2d clone() {
//        return new Polygon2d(this.points);
//    }

//    /**
//     * Gets a point of this po.
//     * @param index save index into the array of points.
//     * @return point of this po.
//     */
//    public Point2d getPointAt(int index) {
//        if (index < 0 || index > points.length - 1) {
//            throw new IllegalArgumentException("index out of range");
//        }
//        return points[index];
//    }

//    /**
//     * Gets the points of this po as array.
//     * @return points of this po as array.
//     */
//    public Point2d[] getPoints() {
//        return points;
//    }

    /**
     * Tests this po on simplicity.
     * !!!! Methode in PolygonalCurve2d ist nicht ok !!!!!!!
     * @return <code>true</code> if this po is simple.
     */
    public boolean isSimple() {
        for (int i = 0; i < points.length; i++) {
            Point2d P00 = points[i], P01 = points[(i + 1) % points.length];

            for (int j = 0; j < points.length - 3; j++) {
                Point2d P10 = points[(i + 2 + j) % points.length], P11 = points[(i + 3 + j) % points.length];
                // direction parameters of cutpoint
                double s = (P00.x * (P10.y - P11.y) + P10.x * (P11.y - P00.y) + P11.x * (P00.y - P10.y)) / (P00.x * (P10.y - P11.y) + P01.x * (P11.y - P10.y) + P10.x * (P01.y - P00.y) + P11.x * (P00.y - P01.y));
                double t = (P00.x * (P01.y - P10.y) + P01.x * (P10.y - P00.y) + P10.x * (P00.y - P01.y)) / (P00.x * (P11.y - P10.y) + P01.x * (P10.y - P11.y) + P10.x * (P00.y - P01.y) + P11.x * (P01.y - P00.y));

                if (s >= 0. && s <= 1. && t >= 0. && t <= 1.) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Indicates if the corner at the point specified by the index is convex.
     * 
     * @param index index into the array of points, beginning with 0.
     * @return <code>true</code> if the corner at the point specified by the index is convex.
     */
    public boolean isConvexAtPoint(int index) {
        if (index < 0 || index > points.length - 1) {
            throw new IllegalArgumentException("Index out of range.");
        }
        if (points.length == 3) {
            return true;
        }
        Point2d p0 = points[(index + points.length - 1) % points.length];
        Point2d pi = points[index];
        Point2d p1 = points[(index + 1) % points.length];

        return this.getOrientation() - Polygon2d.getOrientation(p0, pi, p1) == 0;
    }
    
    /**
     * Indicates if <code>this</code> is convex.
     * A po is convex if all corners are convex.
     * 
     * @return <code>true</code> if <code>this</code> is convex.
     */
    public boolean isConvex() {
        if (points.length == 3) {
            return true;
        }
        int ori = this.getOrientation();

        // Schleife ueber alle Ecken i
        for (int i = 0; i < points.length; i++) {
            Point2d p0 = points[i];
            Point2d pi = points[(i + 1) % points.length];
            Point2d p1 = points[(i + 2) % points.length];

            if (ori - Polygon2d.getOrientation(p0, pi, p1) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the length.
     * 
     * @return length.
     */
    @Override
    public double getLength() {
        double l = super.getLength();
        l += points[points.length - 1].distance(points[0]);
        return l;
    }

    /**
     * Gets the length of the edge at the specified index.
     * 
     * @param index index of the edge, beginning with 0.
     * @return length of the edge at the specified index.
     */
    public double getEdgeLength(int index) {
        if (index < 0 || index > points.length - 1) {
            throw new IllegalArgumentException("index out of range");
        }
        return points[index].distance(points[(index + 1) % points.length]);
    }
    
    /**
     * Gets the area.
     * 
     * @return area.
     */
    public double getArea() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the orientation.
     * Returns '-1' if this po has a clockwise orientation or '1' 
     * if this po has an anticlockwise orientation.
     * 
     * @return orientation.
     */
    public int getOrientation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Indicates if <code>this</code> is positive oriented.
     * 
     * @return <code>true</code> if <code>this</code> is positive oriented.
     */
    public boolean isPositiveOriented() {
        return this.getOrientation() == 1;
    }

    /**
     * Changes the orientation of <code>this</code>.
     */
    public void changeOrientation() {
        Point2d[] antiOriPoints = new Point2d[points.length];

        for (int i = 0; i < points.length; i++) {
            antiOriPoints[i] = points[points.length - 1 - i];
        }
        for (int i = 0; i < points.length; i++) {
            points[i] = antiOriPoints[i];
        }
    }

    /**
     * Gets the bounding box.
     * 
     * @return bounding box.
     */
//    public BoundingBox2d getBoundingBox() {
//        double xmin = Double.POSITIVE_INFINITY;
//        double xmax = Double.NEGATIVE_INFINITY;
//        double ymin = xmin;
//        double ymax = xmax;
//        
//        for (int i = 0; i < points.length; i++) {
//            if (points[i].x < xmin) {
//                xmin = points[i].x;
//            }
//            if (points[i].x > xmax) {
//                xmax = points[i].x;
//            }
//            if (points[i].y < ymin) {
//                ymin = points[i].y;
//            }
//            if (points[i].y > ymax) {
//                ymax = points[i].y;
//            }
//        }
//        return new BoundingBox2d(new Point2d(xmin, ymin), (xmax - xmin), (ymax - ymin));
//    }

//    /**
//     * Gets the center.
//     *
//     * @return center.
//     */
//    public Point2d getCenter() {
//        Point2d cp = new Point2d();
//
//        for (int i = 0; i < points.length; i++) {
//            cp.x += points[i].x;
//            cp.y += points[i].y;
//        }
//        cp.x /= points.length;
//        cp.y /= points.length;
//        return cp;
//    }
//    
    /**
     * Gets the centroid (center of mass).
     * 
     * @return centroid.
     */
    public Point2d getCentroid() {
        double cx = 0., cy = 0.;
        
        for (int i = 0; i < points.length; i++) {
            Point2d P0 = points[i], P1 = points[(i + 1) % points.length];
            cx += (P0.x + P1.x) * (P0.x * P1.y - P0.y * P1.x);
            cy += (P0.y + P1.y) * (P0.x * P1.y - P0.y * P1.x);
        }
        double fak = 6. * this.getOrientation() * this.getArea();
        return new Point2d(cx / fak, cy / fak);
    }
    

    /**
     * Indicates if <code>this</code> contains a point p.
     * 
     * @param p point.
     * @return <code>true</code> if <code>this</code> contains p.
     */
    public boolean contains(EuclideanPoint p) {
        if(isSimple())
           return  new SimplePolygon2d(points).contains((Point2d) p);

        return (p instanceof Point2d) ? this.contains((Point2d) p) : false;
    }
    
//    /**
//     * Indicates if <code>this</code> contains a point p.
//     * 
//     * @param p point.
//     * @return <code>true</code> if <code>this</code> contains p.
//     */
//    public boolean contains(Point2d p) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    
    /**
     * Gets the distance vector to a point p.
     * 
     * @param p point.
     * @return distance vector to <code>other</code>.
     */
    @Override
    public Vector2d distanceVector(Point2d p) {
        if (this.contains(p)) {
            return new Vector2d();
        }
        Vector2d vMin = null;
        double dMin = Double.POSITIVE_INFINITY;
        Edge2d edi = new Edge2d(null, null);
        
        for (int i = 0; i < points.length; i++) {
            edi.p0 = points[i];
            edi.p1 = points[(i + 1) % points.length];
            Vector2d vi = edi.distanceVector(p);
            double di = vi.length();

            if (di < dMin) {
                dMin = di;
                vMin = vi;
            }
        }
        return vMin;
    }

    /**
     * Indicates if this polygon is congruent with other polygon po.
     * 
     * @param po twodimensional polygon.
     * @return <code>true</code> if this polygon is congruent with other polygon.
     */
    public boolean isCongruent(Polygon2d po) {
        if (po == this) {
            return true;
        }
        if (po == null) {
            return false;
        }
        if (points.length != po.points.length) {
            return false;
        }
        int numberOfEqualEdges = 0;
        int numberOfEqualAntiorientedEdges = 0;

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < po.points.length; j++) {
                if (points[i].equals(po.points[j]) && points[(i + 1) % points.length].equals(po.points[(j + 1) % po.points.length])) {
                    numberOfEqualEdges++;
                    break;
                } else if (points[i].equals(po.points[(j + 1) % po.points.length]) && points[(i + 1) % points.length].equals(po.points[j])) {
                    numberOfEqualAntiorientedEdges++;
                    break;
                }
            }
        }
        return (numberOfEqualEdges == points.length || numberOfEqualAntiorientedEdges == points.length);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Polygon2d) ? (this.equals((Polygon2d) o)) : false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.points != null ? this.points.hashCode() : 0);
        return hash;
    }

    /**
     * Indicates if this polygon equals other polygon po.
     * 
     * @param po twodimensional polygon.
     * @return <code>true</code> if this polygon equals other polygon.
     */
    public boolean equals(Polygon2d po) {
        if (po == this) {
            return true;
        }
        if (po == null) {
            return false;
        }
        if (points.length != po.points.length) {
            return false;
        }
        int numberOfEqualEdges = 0;

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < po.points.length; j++) {
                if (points[i].equals(po.points[j]) && points[(i + 1) % points.length].equals(po.points[(j + 1) % po.points.length])) {
                    numberOfEqualEdges++;
                    break;
                }
            }
        }
        return (numberOfEqualEdges == points.length);
    }

    @Override
    public String toString() {
        String s = super.toString() + ": ";
        for (int i = 0; i < points.length; i++) {
            s += points[i];
        }
        return s;
    }
    
    /**
     * Gets the area of a triangle p0-p1-p2. 
     * 
     * @param p0 1st point of triangle.
     * @param p1 2nd point of triangle.
     * @param p2 3rd point of triangle.
     * @return area.
     */
    public static double getArea(Point2d p0, Point2d p1, Point2d p2) {
        double A = (p1.x * p2.y - p1.y * p2.x + p2.x * p0.y - p2.y * p0.x + p0.x * p1.y - p0.y * p1.x) / 2.;
        return A < 0. ? -A : A;
    }

    /**
     * Gets the orientation of a triangle p0-p1-p2.
     * Returns '-1' if the triangle has a clockwise orientation or '1' 
     * if the triangle has an anticlockwise orientation.
     * 
     * @param p0 1st point of triangle.
     * @param p1 2nd point of triangle.
     * @param p2 3rd point of triangle.
     * @return orientation.
     */
    public static int getOrientation(Point2d p0, Point2d p1, Point2d p2) {
        double A = (p1.x * p2.y - p1.y * p2.x + p2.x * p0.y - p2.y * p0.x + p0.x * p1.y - p0.y * p1.x) / 2.;
        return A < 0. ? -1 : 1;
    }
}
