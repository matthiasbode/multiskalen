package bijava.geometry.dim2;

import bijava.geometry.FalseSpaceDimensionException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SimplePolygon2d.java is a class for a closed curve as a simple polygon in a
 * twodimensional space. A polygon is simple, at what all the tmpPoints are different
 * and at what each two edges has no common points except for the common vertex.
 * 
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 1.3, May 2008
 */
public class SimplePolygon2d extends Polygon2d implements Region2d {

    protected double repsilon = 1e-7;

    protected SimplePolygon2d() {
        super();
    }

    /**
     * Constructs a twodimensional simple polygon from three points p0, p1, p2.
     * 
     * @param p0 1st point.
     * @param p1 2nd point.
     * @param p2 3rd point.
     */
    public SimplePolygon2d(Point2d p0, Point2d p1, Point2d p2) {
        super(p0, p1, p2);
        double area = this.getArea();
        
        if (area <= Math.pow(Math.min(Math.min(p0.distance(p1), p0.distance(p2)), p2.distance(p1)), 2.) * repsilon) {
            throw new IllegalArgumentException("a, b or c are colinear.");
        }
    }

    /**
     * Constructs a twodimensional simple polygon from an array of points pts.
     * 
     * @param pts array of points.
     */
    public SimplePolygon2d(Point2d[] pts) {
        super(pts);
        //if (!this.isSimple()) throw new IllegalArgumentException("polygon is not simple");
        if (pts.length == 3) {
            double area = this.getArea();
            
            if (area <= Math.pow(Math.min(Math.min(pts[0].distance(pts[1]), pts[0].distance(pts[2])), pts[2].distance(pts[1])), 2.) * repsilon) {
                throw new IllegalArgumentException("a, b or c are colinear.");
            }
        }
    }

    /**
     * Constructs a twodimensional simple polygon from a simple polygon pg with cloning the points.
     *
     * @param pg simple polygon.
     */
    public SimplePolygon2d(SimplePolygon2d pg) {
        super(pg);
    }

    @Override
    public SimplePolygon2d clone() {
        return new SimplePolygon2d(this);
    }

    @Override
    public double getArea() {
        double area = 0.;
        
        for (int i = 0; i < points.length - 1; i++) {
            area += (points[i].x - points[i + 1].x) * (points[i].y + points[i + 1].y);
        }
        area += (points[points.length - 1].x - points[0].x) * (points[points.length - 1].y + points[0].y);
        area /= 2.;
        return Math.abs(area);
    }

    @Override
    public int getOrientation() {
        double area = 0.;
        
        for (int i = 0; i < points.length - 1; i++) {
            area += (points[i].x - points[i + 1].x) * (points[i].y + points[i + 1].y);
        }
        area += (points[points.length - 1].x - points[0].x) * (points[points.length - 1].y + points[0].y);
        area /= 2.;
        return (area < 0.) ? -1 : 1;
    }

    @Override
    public boolean isConvex() {
        Point2d[] p = points;
        double z = 0., oldZ = 0.;
        
        for (int i = 0; i < p.length - 3; i++) {
            oldZ = z;
            z = (p[i + 1].x - p[i].x) * (p[i + 2].y - p[i + 1].y) - (p[i + 1].y - p[i].y) * (p[i + 2].x - p[i + 1].x);
            if ((oldZ > 0 && z < 0) || (oldZ < 0 && z > 0)) {
                return false;
            }
        }
        return true;
//        int ori = this.getOrientation();
//        
//        for (int i = 0; i < pts.length; i++) {
//            Point2d p0 = pts[i], p1 = pts[(i + 1) % pts.length], p2 = pts[(i + 2) % pts.length];
//            double area = (p1.x * p2.y - p1.y * p2.x + p2.x * p0.y - p2.y * p0.x + p0.x * p1.y - p0.y * p1.x);  // / 2.; - spielt keine Rolle KK
//            
//            if ((ori == 1 && area < 0.) || (ori == -1 && area > 0.)) {
//                return false;
//            }
//        }
//        return true;
    }
    
    @Override
    public boolean contains(Point2d p) {
        if (p == null) {
            return false;
        }
        Point2d[] pts = new Point2d[points.length];
        for (int i = 0; i < points.length; i++) {
            pts[i] = new Point2d(points[i].x, points[i].y);
        }
        /* translational displacement, till p is the origin */
        for (int i = 0; i < pts.length; i++) {
            pts[i].x -= p.x;
            pts[i].y -= p.y;
        }
        int crossings = 0;
        /* check every edge e = (i-1, i) on a cut with the ray */
        for (int i = 0; i < points.length; i++) {
            int i1 = (i + points.length - 1) % points.length;
            // if e cuts the x-axis
            if ((pts[i].y > 0. && pts[i1].y <= 0.) || (pts[i1].y > 0. && pts[i].y <= 0.)) {
                // e cuts the ray, generate cutpoint with the x-axis
                double x = (pts[i].x * pts[i1].y - pts[i1].x * pts[i].y) / (pts[i1].y - pts[i].y);
                // crosses the ray, if intersection greater than zero
                if (x > 0.) {
                    crossings++;
                }
            }
        }
        /* p is in, if the number of crossings is odd */
        return crossings % 2 != 0;
    }

    /**
     * Tests if this polygon contains other polygon po.
     * @param po other polygon.
     * @return <code>true</code> if this polygon contains other polygon.
     */
    public boolean contains(SimplePolygon2d po) {
        if (po == this) {
            return true;
        }
        if (po == null) {
            return false;
        }
        if (this.intersects(po)) {
            return false;
        }
        return this.contains(po.points[0]);
    }

    /**
     * Tests if this polygon intersects other polygon po.
     * @param po other polygon.
     * @return <code>true</code> if this polygon intersects other polygon.
     */
    public boolean intersects(SimplePolygon2d po) {
        if (po == this) {
            return true;
        }
        if (po == null) {
            return false;
        }
        for (int i = 0; i < points.length; i++) {
            Point2d P00 = points[i], P01 = points[(i + 1) % points.length];
            
            for (int j = 0; j < po.points.length; j++) {
                Point2d P10 = po.points[j], P11 = po.points[(j + 1) % po.points.length];
                // compute direction parameters of cutpoint
                double s = (P00.x * (P10.y - P11.y) + P10.x * (P11.y - P00.y) + P11.x * (P00.y - P10.y)) / (P00.x * (P10.y - P11.y) + P01.x * (P11.y - P10.y) + P10.x * (P01.y - P00.y) + P11.x * (P00.y - P01.y));
                double t = (P00.x * (P01.y - P10.y) + P01.x * (P10.y - P00.y) + P10.x * (P00.y - P01.y)) / (P00.x * (P11.y - P10.y) + P01.x * (P10.y - P11.y) + P10.x * (P00.y - P01.y) + P11.x * (P01.y - P00.y));
                // test parameters
//                if (s >= 0. && s <= 1. && t >= 0. && t <= 1.) {
//                    return true;
//                }
                if (s > 0. && s < 1. && t > 0. && t < 1.) {
                    return true;
                }
                if (s == 0. && t >= 0. && t <= 1.) {
                    return po.contains(P00.add(P01.sub(P00).mult(repsilon)));
                }
                if (t == 0. && s >= 0. && s <= 1.) {
                    return this.contains(P10.add(P11.sub(P10).mult(repsilon)));
                }
            }
        }
        return false;
    }

    /**
     * Gets a triangulation of this polygon.
     * Uses the 'Schneckenhaus-Verfahren'.
     * @return a triangulation of this polygon.
     */
    public Triangle2d[] getTriangulation() {
        // copy of polygon with positive orientation
        int ori = this.getOrientation();
        Point2d[] copyPoints = new Point2d[points.length];
        
        for (int i = 0; i < points.length; i++) {
            if (ori == 1) {
                copyPoints[i] = points[i];
            } else {
                copyPoints[i] = points[points.length - 1 - i];
            }
        }
        // cut off sigular triangles util the copy of polygon is triangulated
        ArrayList<Triangle2d> triangles = new ArrayList<Triangle2d>();
        
        while (copyPoints.length > 2) {
            for (int i = 0; i < copyPoints.length; i++) {
                Point2d p0 = copyPoints[i];
                Point2d p1 = copyPoints[(i + 1) % copyPoints.length];
                Point2d p2 = copyPoints[(i + 2) % copyPoints.length];
                Triangle2d t = new Triangle2d(p0, p1, p2);
                // criterion of visibility one: 'p1' is a convex corner
                boolean criterionOne = false;
                
                if (t.getOrientation() == 1) {
                    criterionOne = true;
                }
                // criterion of visibility two: 'p2' is visible from 'p0'
                boolean criterionTwo = true;
                
                for (int j = 0; j < (copyPoints.length - 3) && criterionTwo; j++) {
                    Point2d copyPoint = copyPoints[(i + 3 + j) % copyPoints.length];
                    
                    if (t.contains(copyPoint)) {
                        criterionTwo = false;
                    }
                }
                if (criterionOne && criterionTwo) {
                    triangles.add(t);
                    Point2d[] copyPoints_red = new Point2d[copyPoints.length - 1];
                    
                    for (int j = 0; j < (copyPoints.length - 1); j++) {
                        copyPoints_red[j] = copyPoints[(i + 2 + j) % copyPoints.length];
                    }
                    copyPoints = copyPoints_red;
                    break;
                }
            }
        }
        return triangles.toArray(new Triangle2d[triangles.size()]);
    }

    /**
     * Gets the union of two simple polygons.
     * 
     * @param po a simple polygon with which to compute the union.
     * @return array of simple polygons representing the tmpPoints in this polygon and <code>other</code>.
     */
    public SimplePolygon2d[] union(SimplePolygon2d po) {
        if (po == this) {
            return new SimplePolygon2d[]{this};
        }
        if (po == null) {
            return new SimplePolygon2d[]{this};
        }
        return this.operation(po, 0);
    }

    /**
     * Gets the section of two simple polygons.
     * 
     * @param po a simple polygon with which to compute the section.
     * @return array of simple polygons representing the tmpPoints common to this polygon and <code>other</code>.
     */
    public SimplePolygon2d[] section(SimplePolygon2d po) {
        if (po == this) {
            return new SimplePolygon2d[]{this};
        }
        if (po == null) {
            return new SimplePolygon2d[0];
        }
        return this.operation(po, 1);
    }

    /**
     * Gets the difference of two simple polygons.
     * 
     * @param po a simple polygon with which to compute the difference.
     * @return array of simple polygons representing the tmpPoints in this polygon not in <code>other</code>.
     */
    public SimplePolygon2d[] difference(SimplePolygon2d po) {
        if (po == this) {
            return new SimplePolygon2d[0];
        }
        if (po == null) {
            return new SimplePolygon2d[]{this};
        }
        return this.operation(po, 2);
    }

    /**
     * Gets the symmetric difference of two simple polygons.
     * 
     * @param po a simple polygon with which to compute the symmetric difference.
     * @return array of simple polygons representing the tmpPoints in this polygon not in <code>other</code>
     *   and the tmpPoints in <code>other</code> not in this polygon.
     */
    public SimplePolygon2d[] symDifference(SimplePolygon2d po) {
        if (po == this) {
            return new SimplePolygon2d[0];
        }
        if (po == null) {
            return new SimplePolygon2d[]{this};
        }
        SimplePolygon2d[] diff0 = this.difference(po);
        SimplePolygon2d[] diff1 = po.difference(this);

        SimplePolygon2d[] result = new SimplePolygon2d[diff0.length + diff1.length];
        
        for (int i = 0; i < diff0.length; i++) {
            result[i] = diff0[i];
        }
        for (int i = 0; i < diff1.length; i++) {
            result[diff0.length + i] = diff1[i];
        }
        return result;
    }

    private SimplePolygon2d[] operation(SimplePolygon2d polygon, int modus) {
        // clone polygons
        SimplePolygon2d this_polygon = new SimplePolygon2d(this.points);
        SimplePolygon2d other_polygon = new SimplePolygon2d(polygon.points);

        // set orientation
        int this_ori = this_polygon.getOrientation();
        int other_ori = other_polygon.getOrientation();
        if (this_ori == -1) {
            this_polygon.changeOrientation();
        }
        if (((modus == 0 || modus == 1) && other_ori == -1) || (modus == 2 && other_ori == 1)) {
            other_polygon.changeOrientation();
        }
        // filter general cases
        if (this_polygon.isCongruent(other_polygon)) {
            if (modus == 0 || modus == 1) {
                return new SimplePolygon2d[]{this_polygon};
            }
            return new SimplePolygon2d[0];
        }
        if (!this_polygon.intersects(other_polygon)) {
            if (this_polygon.contains(other_polygon.points[0])) {
                if (modus == 0) {
                    return new SimplePolygon2d[]{this_polygon};
                }
                if (modus == 1) {
                    return new SimplePolygon2d[]{other_polygon};
                }
                return new SimplePolygon2d[]{this_polygon, other_polygon};
            }
            if (other_polygon.contains(this_polygon.points[0])) {
                if (modus == 0) {
                    return new SimplePolygon2d[]{other_polygon};
                }
                if (modus == 1) {
                    return new SimplePolygon2d[]{this_polygon};
                }
                return new SimplePolygon2d[0];
            }
            if (modus == 0) {
                return new SimplePolygon2d[]{this_polygon, other_polygon};
            }
            if (modus == 1) {
                return new SimplePolygon2d[0];
            }
            return new SimplePolygon2d[]{this_polygon};
        }
        // set positions
        while (true) {
            boolean no_displacement = true;
            
            // position of this polygon to other
            for (int i = 0; i < this_polygon.points.length && no_displacement; i++) {
                Point2d p0 = this_polygon.points[i], p1 = this_polygon.points[(i + 1) % this_polygon.points.length];
                
                for (int j = 0; j < other_polygon.points.length && no_displacement; j++) {
                    Point2d p = other_polygon.points[j];
                    double r = ((p.x - p0.x) * (p1.x - p0.x) + (p.y - p0.y) * (p1.y - p0.y)) / ((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y));
                    double dist = Math.abs(((p1.x - p0.x) * (p.y - p0.y) - (p1.y - p0.y) * (p.x - p0.x)) / (Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y))));
                    
                    if (r >= 0. - 1.0E-15 && r <= 1. + 1.0E-15 && dist <= 1.0E-15) {
                        // translation to outside
                        other_polygon.points[j].x = p0.x + r * (p1.x - p0.x) + (p0.y - p1.y) * 1.0E-7;
                        other_polygon.points[j].y = p0.y + r * (p1.y - p0.y) + (p1.x - p0.x) * 1.0E-7;
                        no_displacement = false;
                    }
                }
            }
            // position of other to this polygon
            for (int i = 0; i < other_polygon.points.length && no_displacement; i++) {
                Point2d p0 = other_polygon.points[i], p1 = other_polygon.points[(i + 1) % other_polygon.points.length];
                
                for (int j = 0; j < this_polygon.points.length && no_displacement; j++) {
                    Point2d p = this_polygon.points[j];
                    double r = ((p.x - p0.x) * (p1.x - p0.x) + (p.y - p0.y) * (p1.y - p0.y)) / ((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y));
                    double dist = Math.abs(((p1.x - p0.x) * (p.y - p0.y) - (p1.y - p0.y) * (p.x - p0.x)) / (Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y))));
                    
                    if (r >= 0. - 1.0E-15 && r <= 1. + 1.0E-15 && dist <= 1.0E-15) {
                        // translation to outside
                        if (modus == 2) {
                            this_polygon.points[j].x = p0.x + r * (p1.x - p0.x) - (p0.y - p1.y) * 1.0E-7;
                            this_polygon.points[j].y = p0.y + r * (p1.y - p0.y) - (p1.x - p0.x) * 1.0E-7;
                        } else {
                            this_polygon.points[j].x = p0.x + r * (p1.x - p0.x) + (p0.y - p1.y) * 1.0E-7;
                            this_polygon.points[j].y = p0.y + r * (p1.y - p0.y) + (p1.x - p0.x) * 1.0E-7;
                        }
                        no_displacement = false;
                    }
                }
            }
            if (no_displacement) {
                break;
            }
        }

        // filter general cases after displacement
        if (!this_polygon.intersects(other_polygon)) {
            if (this_polygon.contains(other_polygon.points[0])) {
                if (modus == 0) {
                    return new SimplePolygon2d[]{this_polygon};
                }
                if (modus == 1) {
                    return new SimplePolygon2d[]{other_polygon};
                }
                return new SimplePolygon2d[]{this_polygon, other_polygon};
            }
            if (other_polygon.contains(this_polygon.points[0])) {
                if (modus == 0) {
                    return new SimplePolygon2d[]{other_polygon};
                }
                if (modus == 1) {
                    return new SimplePolygon2d[]{this_polygon};
                }
                return new SimplePolygon2d[0];
            }
            if (modus == 0) {
                return new SimplePolygon2d[]{this_polygon, other_polygon};
            }
            if (modus == 1) {
                return new SimplePolygon2d[0];
            }
            return new SimplePolygon2d[]{this_polygon};
        }
        // compute cutpoints
        ArrayList<Point2d>[] this_cutPts = new ArrayList[this_polygon.points.length];
        
        for (int i = 0; i < this_polygon.points.length; i++) {
            this_cutPts[i] = new ArrayList<Point2d>();
        }
        ArrayList<Point2d>[] other_cutPts = new ArrayList[other_polygon.points.length];
        
        for (int i = 0; i < other_polygon.points.length; i++) {
            other_cutPts[i] = new ArrayList<Point2d>();
        }
        int cutPts_size = 0;
        
        for (int i = 0; i < this_polygon.points.length; i++) {
            Point2d P00 = this_polygon.points[i], P01 = this_polygon.points[(i + 1) % this_polygon.points.length];
            
            for (int j = 0; j < other_polygon.points.length; j++) {
                Point2d P10 = other_polygon.points[j], P11 = other_polygon.points[(j + 1) % other_polygon.points.length];
                // direction parameters of cutpoint
                double s = (P00.x * (P10.y - P11.y) + P10.x * (P11.y - P00.y) + P11.x * (P00.y - P10.y)) / (P00.x * (P10.y - P11.y) + P01.x * (P11.y - P10.y) + P10.x * (P01.y - P00.y) + P11.x * (P00.y - P01.y));
                double t = (P00.x * (P01.y - P10.y) + P01.x * (P10.y - P00.y) + P10.x * (P00.y - P01.y)) / (P00.x * (P11.y - P10.y) + P01.x * (P10.y - P11.y) + P10.x * (P00.y - P01.y) + P11.x * (P01.y - P00.y));
                
                if (s > 0. && s < 1. && t > 0. && t < 1.) {
                    Point2d cutPt = P00.add(P01.sub(P00).mult(s));
                    this_cutPts[i].add(cutPt);
                    other_cutPts[j].add(cutPt);
                    cutPts_size++;
                }
            }
        }
        // sort cutpoints by minimal distance to first point
        for (int i = 0; i < this_polygon.points.length; i++) {
            int size = this_cutPts[i].size();
            
            if (size > 1) {
                for (int j = 0; j < size; j++) {
                    int index = j;
                    Point2d cutPtj = this_cutPts[i].get(j);
                    double distj = this_polygon.points[i].distance(cutPtj);
                    
                    for (int k = j + 1; k < size; k++) {
                        Point2d cutPtk = this_cutPts[i].get(k);
                        double distk = this_polygon.points[i].distance(cutPtk);
                        
                        if (distk < distj) {
                            distj = distk;
                            index = k;
                        }
                    }
                    this_cutPts[i].add(j, this_cutPts[i].remove(index));
                }
            }
        }
        for (int i = 0; i < other_polygon.points.length; i++) {
            int size = other_cutPts[i].size();
            
            if (size > 1) {
                for (int j = 0; j < size; j++) {
                    int index = j;
                    Point2d cutPtj = other_cutPts[i].get(j);
                    double distj = other_polygon.points[i].distance(cutPtj);
                    
                    for (int k = j + 1; k < size; k++) {
                        Point2d cutPtk = other_cutPts[i].get(k);
                        double distk = other_polygon.points[i].distance(cutPtk);
                        
                        if (distk < distj) {
                            distj = distk;
                            index = k;
                        }
                    }
                    other_cutPts[i].add(j, other_cutPts[i].remove(index));
                }
            }
        }
        // create status lists for interpretation
        boolean exiting = other_polygon.contains(this_polygon.points[0]);
        
        if (modus == 0 || modus == 2) {
            exiting = !exiting;
        }
        Point2d[] list0 = new Point2d[this_polygon.points.length + cutPts_size];
        char[] status0 = new char[this_polygon.points.length + cutPts_size];
        int index = 0;
        
        for (int i = 0; i < this_polygon.points.length; i++) {
            list0[index] = this_polygon.points[i];
            status0[index] = '-';
            index++;
            int size = this_cutPts[i].size();
            
            for (int j = 0; j < size; j++) {
                list0[index] = this_cutPts[i].get(j);
                
                if (exiting) {
                    status0[index] = '1';
                } else {
                    status0[index] = '0';
                }
                exiting = !exiting;
                index++;
            }
        }
        exiting = this_polygon.contains(other_polygon.points[0]);
        
        if (modus == 1 || modus == 2) {
            exiting = !exiting;
        }
        Point2d[] list1 = new Point2d[other_polygon.points.length + cutPts_size];
        char[] status10 = new char[other_polygon.points.length + cutPts_size];
        index = 0;
        
        for (int i = 0; i < other_polygon.points.length; i++) {
            list1[index] = other_polygon.points[i];
            status10[index] = '-';
            index++;
            int size = other_cutPts[i].size();
            
            for (int j = 0; j < size; j++) {
                list1[index] = other_cutPts[i].get(j);
                
                if (exiting) {
                    status10[index] = '1';
                } else {
                    status10[index] = '0';
                }
                exiting = !exiting;
                index++;
            }
        }
        this_cutPts = other_cutPts = null;

        // create pointer
        int[] pointer01 = new int[list0.length];
        
        for (int i = 0; i < list0.length; i++) {
            pointer01[i] = -1;
        }
        int[] pointer10 = new int[list1.length];
        
        for (int i = 0; i < list1.length; i++) {
            pointer10[i] = -1;
        }
        for (int i = 0; i < list0.length; i++) {
            if (status0[i] != '-') {
                for (int j = 0; j < list1.length && pointer01[i] == -1; j++) {
                    if (status10[j] != '-') {
                        if (list0[i].equals(list1[j])) {
                            pointer01[i] = j;
                            pointer10[j] = i;
                        }
                    }
                }
            }
        }
        // create entering list of this polygon
        ArrayList<Integer> entlist0 = new ArrayList<Integer>();
        
        for (int i = 0; i < status0.length; i++) {
            if (status0[i] == '0') {
                entlist0.add(i);
            }
        }
        // interpretation
        int npg = 0, np;
        ArrayList<SimplePolygon2d> result = new ArrayList<SimplePolygon2d>();
        ArrayList<Point2d> tmpPoints = new ArrayList<Point2d>();
        Point2d[] currentList;
        char[] currentStatus;

        while (entlist0.size() > 0) {
            currentList = list0;
            currentStatus = status0;
            index = entlist0.remove(0).intValue();
            Point2d startp = currentList[index];
            tmpPoints.add(startp);
            np = 1;
            index++;
            Point2d nextp = currentList[index % currentList.length];
            
            do {
                tmpPoints.add(nextp);
                np++;
                
                if (currentStatus[index % currentStatus.length] == '0') {
                    currentList = list0;
                    currentStatus = status0;
                    index = pointer10[index % pointer10.length];
                    int index_remove = -1, entlist0_size = entlist0.size();
                    
                    for (int i = 0; i < entlist0_size && index_remove == -1; i++) {
                        if (entlist0.get(i).intValue() == index) {
                            index_remove = i;
                        }
                    }
                    entlist0.remove(index_remove);
                } else if (currentStatus[index % currentStatus.length] == '1') {
                    currentList = list1;
                    currentStatus = status10;
                    index = pointer01[index % pointer01.length];
                }
                index++;
                nextp = currentList[index % currentList.length];
                
            } while (!nextp.equals(startp));
            
            result.add(new SimplePolygon2d(tmpPoints.toArray(new Point2d[np])));
            npg++;
            tmpPoints.clear();
        }
        return result.toArray(new SimplePolygon2d[npg]);
    }
    
    // STATISCHE FUNKTIONEN
    public static int getOrientation(Point2d[] pts) {
        double area = 0.;

        for (int i = 0; i < pts.length - 1; i++) {
            area += (pts[i].x - pts[i + 1].x) * (pts[i].y + pts[i + 1].y);
        }
        area += (pts[pts.length - 1].x - pts[0].x) * (pts[pts.length - 1].y + pts[0].y);
        area /= 2.;
        return (area < 0.) ? -1 : 1;
    }

    public static boolean isPositiveOriented(Point2d[] pts) {
        return SimplePolygon2d.getOrientation(pts) == 1;
    }

    public static Point2d[] changeOrientation(Point2d[] pts) {
        Point2d[] antiOriPoints = new Point2d[pts.length];

        for (int i = 0; i < pts.length; i++) {
            antiOriPoints[i] = pts[pts.length - 1 - i];
        }
        for (int i = 0; i < pts.length; i++) {
            pts[i] = antiOriPoints[i];
        }
        return pts;
    }

    public static double getArea(Point2d[] pts) {
        double area = 0.;

        for (int i = 0; i < pts.length - 1; i++) {
            area += (pts[i].x - pts[i + 1].x) * (pts[i].y + pts[i + 1].y);
        }
        area += (pts[pts.length - 1].x - pts[0].x) * (pts[pts.length - 1].y + pts[0].y);
        area /= 2.;
        return Math.abs(area);
    }

    public static boolean contains(Point2d[] pts, Point2d p) {
        Point2d[] pts_ = new Point2d[pts.length];
        for (int i = 0; i < pts.length; i++) {
            pts_[i] = new Point2d(pts[i].x, pts[i].y);
        }
        /* translational displacement, till p is the origin */
        for (int i = 0; i < pts_.length; i++) {
            pts_[i].x -= p.x;
            pts_[i].y -= p.y;
        }
        int crossings = 0;
        /* check every edge e = (i-1, i) on a cut with the ray */
        for (int i = 0; i < pts.length; i++) {
            int i1 = (i + pts.length - 1) % pts.length;
            // if e cuts the x-axis
            if ((pts_[i].y > 0. && pts_[i1].y <= 0.) || (pts_[i1].y > 0. && pts_[i].y <= 0.)) {
                // e cuts the ray, generate cutpoint with the x-axis
                double x = (pts_[i].x * pts_[i1].y - pts_[i1].x * pts_[i].y) / (pts_[i1].y - pts_[i].y);
                // crosses the ray, if intersection greater than zero
                if (x > 0.) {
                    crossings++;
                }
            }
        }
        /* p is in, if the number of crossings is odd */
        return crossings % 2 != 0;
    }

    public static boolean intersects(Point2d[] pts, Point2d p0, Point2d p1) {
        if (SimplePolygon2d.contains(pts, p0)) {
            return true;
        }
        for (int i = 0; i < pts.length; i++) {
            Point2d p0i = pts[i];
            Point2d p1i = pts[(i + 1) % pts.length];

            if (Edge2d.intersects(p0i, p1i, p0, p1)) {
                return true;
            }
        }
        return false;
    }

    public static double distance(Point2d[] pts, Point2d p) {
        if (SimplePolygon2d.contains(pts, p)) {
            return 0.;
        }
        double d = Double.POSITIVE_INFINITY;

        for (int i = 0; i < pts.length; i++) {
            Point2d p0i = pts[i];
            Point2d p1i = pts[(i + 1) % pts.length];

            double di = Edge2d.distance(p0i, p1i, p);
            d = (di < d) ? di : d;
        }
        return d;
    }
    
    public static double distance(Point2d[] pts, Point2d p0, Point2d p1) {
        if (SimplePolygon2d.intersects(pts, p0, p1)) {
            return 0.;
        }
        double d = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < pts.length; i++) {
            Point2d p0i = pts[i];
            Point2d p1i = pts[(i + 1) % pts.length];
        
            double di = Edge2d.distance(p0i, p1i, p0, p1);
            d = (di < d) ? di : d;
        }
        return d;
    }
    
    public void translate(Point2d delta) {
        for (Point2d p : points)
            try {
                p.translate(delta);
            } catch (FalseSpaceDimensionException ex) {
                Logger.getLogger(SimplePolygon2d.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
