package bijava.geometry.dim2;
import bijava.geometry.CoordinateValue;
import bijava.geometry.EuclideanPoint;
import bijava.geometry.NaturalElement;
import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;
import java.util.ArrayList;

/**
 *  ConvexPolygon2d.java is a class for a closed curve as a convex polygon in a
 *  twodimensional space. A polygon is convex, when from each point of the
 *  polygon all points of the polygon are visible.
 *
 *  <p><strong>Version:</strong><br>
 *  <dd>1.1, April 2006</dd>
 *  <p><strong>Author:</strong><br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dipl.-Ing. Mario Hoecker</dd>
 */

public class ConvexPolygon2d extends SimplePolygon2d implements NaturalElement {
    
    protected ConvexPolygon2d(){ super();};
    
    /**
     * Constructs a convex polygon with three points.
     * @param p0, p1, p2  three points.
     */
    public ConvexPolygon2d(Point2d p0, Point2d p1, Point2d p2) {
        super(p0, p1, p2);
    }
    
    /**
     * Constructs a convex polygon with an array of points.
     * @param points  array of points.
     */
    public ConvexPolygon2d(Point2d[] points) {
        super(points);
        if (points.length > 3 && !isConvex()) throw new IllegalArgumentException("polygon is not convex");
    }

    /**
     * Constructs a convex polygon with a convex polygon.
     * @param pg convex polygon.
     */
    public ConvexPolygon2d(ConvexPolygon2d pg) {
        super(pg);
    }

    @Override
    public ConvexPolygon2d clone() {
        return new ConvexPolygon2d(this);
    }
    
    @Override
    public double[] getNaturalElementCoordinates(EuclideanPoint p) {
        if (p instanceof Point2d) return getNaturalElementCoordinates((Point2d)p);
        return null;
    }
    
    /**
     * Gets the natural element coordinates of a twodimensional point to this polygon.
     * @param p a twodimensional point.
     * @return an array with the natural element coordinates of the twodimensional point to this polygon.
     */
    public double[] getNaturalElementCoordinates(Point2d p) {
        if (p == null) return null;
        /* initial the result */
        double[] result = new double[points.length];
        for (int i = 0; i < points.length; i++)
            result[i] = 0.;
        /* case the point is equal with a sampling point: the sampling point has a total natural element coordinate */
        for (int i = 0; i < points.length; i++)
            if (points[i].equals(p)) {
            result[i] = 1.;
            return result;
            }
        /* case the point is located on an edge: linear interpolation between the concerned sampling points;
           the point is located on an edge if the distance between the point and the edge is less than a maximal limit;
           the criterion for the maximal limit is the minimal side length of this polygon */
        double minlength = Double.POSITIVE_INFINITY;
        for (int i = 0; i < points.length; i++) {
            Point2d p0 = points[i], p1 = points[(i + 1) % points.length];
            minlength = Math.min(minlength, p0.distance(p1));
        }
        double maxlimit = minlength * 1.E-10;
        int ori = this.getOrientation();
        for (int i = 0; i < points.length; i++) {
            Point2d p0 = points[i], p1 = points[(i + 1) % points.length];
            // direction parameter of the point to an edge of this polygon
            double r = ((p.x - p0.x) * (p1.x - p0.x) + (p.y - p0.y) * (p1.y - p0.y))
            / ((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y));
            if (r > 0. && r < 1.) {
                // distance of the point to an edge of this polygon
                double ds = Math.abs(((p1.x - p0.x) * (p.y - p0.y) - (p1.y - p0.y) * (p.x - p0.x))
                / (Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y))));
                if (ds == 0.) {
                    result[i] = 1. - r;
                    result[(i + 1) % points.length] = r;
                    return result;
                }
                if (ds <= maxlimit) {
                    double area = (- p0.x * p.y - p1.x * p0.y - p.x * p1.y + p0.x * p1.y + p1.x * p.y + p.x * p0.y) / 2.;
                    if ((ori * area) > 0.) {
                        result[i] = 1. - r;
                        result[(i + 1) % points.length] = r;
                        return result;
                    }
                    return null;
                }
            }
        }
        /* case this polygon doesn't contains the point: the result is a null pointer */
        if (!this.contains(p)) return null;
        /* compute the opened voronoi regions of the sampling points represented by sorted voronoi vertices */
        Point2d[][] vertices = new Point2d[points.length][], bisectors = new Point2d[points.length + 1][2];
        ArrayList<Point2d> temp = new ArrayList<Point2d>();
        for (int i = 0; i < points.length; i++) {
            // compute the perpendicular bisectors of the sides between the sampling point and other sampling points
            bisectors[0] = new Point2d[] {points[i], points[(i + 1) % points.length]};
            bisectors[points.length] = new Point2d[] {points[(i + points.length - 1) % points.length], points[i]};
            for (int j = 0; j < points.length - 1; j++) {
                Point2d p0 = points[i], p1 = points[(i + 1 + j) % points.length];
                double xm = p0.x + (p1.x - p0.x) * 0.5, ym = p0.y + (p1.y - p0.y) * 0.5, dx = (p0.y - p1.y), dy = (p1.x - p0.x);
                bisectors[j + 1] = new Point2d[] {new Point2d(xm, ym), new Point2d(xm + ori * dx, ym + ori * dy)};
            }
            // compute the voronoi vertices of the sampling point by a running intersection with the perpendicular bisectors
            Point2d startp = points[i];
            int id = 0;
            // while the last perpendicular bisector has not achieved
            while (id != bisectors.length - 1) {
                // search the nearest cutpoint to the last cutpoint
                double smin = Double.POSITIVE_INFINITY;
                int nextid = -1;
                for (int j = id + 1; j < bisectors.length; j++) {
                    if (id == 0 && j == bisectors.length - 1) continue;
                    Point2d p00 = startp, p01 = bisectors[id][1], p10 = bisectors[j][0], p11 = bisectors[j][1];
                    // direction parameter of cutpoint of the last and a following perpendicular bisector
                    double s = (p00.x * (p10.y - p11.y) + p10.x * (p11.y - p00.y) + p11.x * (p00.y - p10.y))
                    / (p00.x * (p10.y - p11.y) + p01.x * (p11.y - p10.y) + p10.x * (p01.y - p00.y) + p11.x * (p00.y - p01.y));
                    if (s <= smin && s > 0.) {
                        smin = s;
                        nextid = j;
                    }
                }
                startp = startp.add(bisectors[id][1].sub(startp).mult(smin));
                // save the nearest cutpoint to the last cutpoint
                temp.add(startp);
                // change to the concerned perpendicular bisector
                id = nextid;
            }
            vertices[i] = temp.toArray(new Point2d[1]);
            temp.clear();
        }
        /* compute the partial areas and the total area of the voronoi region of the point */
        Point2d[][] vertices_p = new Point2d[points.length][];
        double[] area_part = new double[points.length];
        double area = 0.;
        for (int i = 0; i < points.length; i++) {
            // compute the perpendicular bisector of the side between the point and the sampling point
            Point2d p0 = p, p1 = points[i];
            double xm = p0.x + (p1.x - p0.x) * 0.5, ym = p0.y + (p1.y - p0.y) * 0.5, dx = (p0.y - p1.y), dy = (p1.x - p0.x);
            Point2d p10 = new Point2d(xm, ym), p11 = new Point2d(xm + dx, ym + dy);
            // compute a section with the perpendicular bisector and the voronoi region of the sampling point
            int n = 0;
            for (int j = 0; j < vertices[i].length - 1 && n < 2; j++) {
                Point2d p00 = vertices[i][j], p01 = vertices[i][j + 1];
                if (n == 1) temp.add(p00);
                // direction parameter of cutpoint of the perpendicular bisector and an edge of the voronoi region of the sampling point
                double s = (p00.x * (p10.y - p11.y) + p10.x * (p11.y - p00.y) + p11.x * (p00.y - p10.y))
                / (p00.x * (p10.y - p11.y) + p01.x * (p11.y - p10.y) + p10.x * (p01.y - p00.y) + p11.x * (p00.y - p01.y));
                if ((j == 0 && s <= 1.) || (j == vertices[i].length - 2 && s > 0.) || (s > 0. && s <= 1.)) {
                    temp.add(p00.add(p01.sub(p00).mult(s)));
                    n++;
                }
            }
            if (temp.size() == 0) {
                vertices_p[i] = null;
            } else {
                vertices_p[i] = temp.toArray(new Point2d[1]);
                temp.clear();
                area_part[i] = 0.;
                for (int j = 0; j < vertices_p[i].length; j++)
                    area_part[i] += (vertices_p[i][j].x - vertices_p[i][(j + 1) % vertices_p[i].length].x)
                    * (vertices_p[i][j].y + vertices_p[i][(j + 1) % vertices_p[i].length].y) / 2.;
                // save the partial area
                area_part[i] = Math.abs(area_part[i]);
                // add the partial area to the total area
                area += area_part[i];
            }
        }
        /* compute the natural element coordinates of the point in this polygon */
        for (int i = 0; i < points.length; i++)
            if (vertices_p[i] != null)
                result[i] = area_part[i] / area;
        return result;
    }
    
    public CoordinateValue[] getNatElemCoord(EuclideanPoint p) {
        if (p instanceof Point2d){
            Point2d pp = (Point2d) p;
            double[] coord = getNaturalElementCoordinates(pp);
            CoordinateValue[] rvalue = new CoordinateValue[coord.length];
            for(int i=0; i<coord.length;i++){
                rvalue[i] = new CoordinateValue(pp, coord[i]);
            }
        }
        return null;
    }
    
    public boolean contains(EuclideanPoint p) {
        if (p instanceof Point2d) return contains((Point2d)p);
        return false;
    }

//------------------------------------------------------------------------------//
/** Liefert die natuerlichen Element-Koordinaten als Funktion von globalen
 *  Koordinaten. Die Funktionen werden in einem Feld zurueckgegeben, das fuer
 *  jeden Eckpunkt des konvexen Polygons die Natuerliche-Elementkoordinaten-
 *  Funktion am entsprechenden Index enthaelt.<br>
 *  Die Methode ist so implementiert, dass die Funktionen als Instanzen von
 *  inneren anonymen Klassen erzeugt werden.
 *
 *  @return Ein Feld von natuerlichen ElementkoordinatenFunktionen. Die Funktion
 *          am Index i gehoert zu dem i-ten Eckpunkt des konvexen Polygons.     */
//------------------------------------------------------------------------------//
    @Override
    public NaturalElementCoordinateFunction[] getLocalCoordinateFunction() {

        NaturalElementCoordinateFunction[] necfs = new NaturalElementCoordinateFunction[this.points.length];

        for (int j=0; j<necfs.length; j++) {
            final int i = j;
            
            necfs[i] = new NaturalElementCoordinateFunction() {
                @Override
                public VectorNd getGradient(PointNd p) {

                    if (p.dim() != 2)
                        throw new IllegalArgumentException("Dimension passt nicht!");
                    Point2d p2d = new Point2d(p.x[0], p.x[1]);
                    if (contains(p2d))
                        throw new UnsupportedOperationException("Noch mal viel nachdenken!"); // TODO
        //                return getNaturalElementCoordinates(p2d)[index];
                    return new VectorNd(0.,0.);
                }

                @Override
                public double getValue(PointNd p) {
                    if (p.dim() != 2)
                        throw new IllegalArgumentException("Dimension passt nicht!");
                    Point2d p2d = new Point2d(p.x[0], p.x[1]);
                    if (contains(p2d))
                        return getNaturalElementCoordinates(p2d)[i];
                    return 0.0;
                }
            };
        }
            
        
        return necfs;

    }


}