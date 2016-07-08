package bijava.geometry.dim2;

import bijava.geometry.EuclideanPoint;
import bijava.geometry.NaturalElement;
import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;
import bijava.math.Function;
import bijava.math.function.AbstractDifferentialFunction2d;
import bijava.math.function.ConstantFunction2d;
import bijava.math.function.DifferentialScalarFunction2d;
import bijava.math.function.ScalarFunction2d;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;

/**
 *  Triangle2d is a class for a closed curve with three points
 *  as a polygon in a twodimensional space.
 *
 *  <p><strong>Version:</strong><br>
 *  <dd>1.0, October 2005</dd>
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @author Mario Hoecker
 */
public class Triangle2d extends ConvexPolygon2d implements NaturalElement{
    
    public double koeffmat[][] = new double[3][3];
    private double area = 0.;
    
    public double minEdgeLength, maxEdgeLength, xi;
    
    /**
     * Constructs a triangle.
     *
     * @param p0, p1, p2 - three points.
     */
    public Triangle2d(Point2d points0, Point2d points1, Point2d points2) {
        super(points0, points1, points2);
        
        minEdgeLength=points[0].distance(points[1]);
        minEdgeLength=Math.min(minEdgeLength,points[1].distance(points[2]));
        minEdgeLength=Math.min(minEdgeLength,points[2].distance(points[0]));
        
        maxEdgeLength=points[0].distance(points[1]);
        maxEdgeLength=Math.max(maxEdgeLength,points[1].distance(points[2]));
        maxEdgeLength=Math.max(maxEdgeLength,points[2].distance(points[0]));
        
        xi = maxEdgeLength/minEdgeLength;
        
        area = getArea();
        if (area <= Math.pow(Math.min(Math.min(points0.distance(points1),points0.distance(points2)), points2.distance(points1)),2.) * repsilon) throw new IllegalArgumentException("a, b or c are colinear");

        koeffmat[0][0] = ((points1.x-points1.x) * (points2.y-points1.y) - (points2.x-points1.x) * (points1.y-points1.y))
                                                                / (2.*area);
        koeffmat[1][0] = ((points2.x-points1.x) * (points0.y-points1.y) - (points0.x-points1.x) * (points2.y-points1.y))
                                                                / (2.*area);
        koeffmat[2][0] = ((points0.x-points1.x) * (points1.y-points1.y) - (points1.x-points1.x) * (points0.y-points1.y))
                                                                / (2.*area);
        
        koeffmat[0][1] = (points1.y -  points2.y) / (2.*area);
        koeffmat[1][1] = (points2.y -  points0.y) / (2.*area);
        koeffmat[2][1] = (points0.y -  points1.y) / (2.*area);
        koeffmat[0][2] = (points2.x -  points1.x) / (2.*area);
        koeffmat[1][2] = (points0.x -  points2.x) / (2.*area);
        koeffmat[2][2] = (points1.x -  points0.x) / (2.*area);
        
    }
    
    /**
     * Constructs a triangle.
     * If the array has less or more than three points, the method
     * terminates the currently running Java Virtual Machine.
     *
     * @param points - array of points.
     */
    public Triangle2d(Point2d[] points) {
        super(points);
        
        if (points.length != 3) throw new IllegalArgumentException("number of points != 3");
    }
    
    /**
     * Constructs a triangle from a triangle.
     *
     * @param tr triangle.
     */
    public Triangle2d(Triangle2d tr) {
        super(tr);
        koeffmat[0][0] = tr.koeffmat[0][0];
        koeffmat[0][1] = tr.koeffmat[0][1];
        koeffmat[0][2] = tr.koeffmat[0][2];
        koeffmat[1][0] = tr.koeffmat[1][0];
        koeffmat[1][1] = tr.koeffmat[1][1];
        koeffmat[1][2] = tr.koeffmat[1][2];
        koeffmat[2][0] = tr.koeffmat[2][0];
        koeffmat[2][1] = tr.koeffmat[2][1];
        koeffmat[2][2] = tr.koeffmat[2][2];
        area = tr.area;
        minEdgeLength = tr.minEdgeLength;
        maxEdgeLength = tr.maxEdgeLength;
        xi = tr.xi;
    }

    @Override
    public Triangle2d clone() {
        return new Triangle2d(this);
    }
    
    /**
     * Gets the area.
     * Overrides the method in <code>SimplePolygon2d</code>.
     *
     * @return the area of this triangle.
     */
    @Override
    public final double getArea() {
        return Math.abs(0.5*((points[1].x-points[0].x)*(points[2].y-points[0].y)-(points[2].x-points[0].x)*(points[1].y-points[0].y)));
    }
    
    public static double getArea(Point2d d1, Point2d d2, Point2d d3) { 
        return Math.abs(0.5*((d2.x-d1.x)*(d3.y-d1.y)-(d3.x-d1.x)*(d2.y-d1.y)));
    }
    
    /**
     * Gets the orientation.
     * Returns '-1' if this polygon has a clockwise orientation or
     * '1' if this polygon has a anticlockwise orientation.
     * Overrides the method in <code>SimplePolygon2d</code>.
     *
     * @return the orientation of this triangle.
     */
    @Override
    public final int getOrientation() {
        double area2 =((points[1].x-points[0].x)*(points[2].y-points[0].y)-(points[2].x-points[0].x)*(points[1].y-points[0].y));  // Kai 23.01.08 durch umsortieren
        
        if (area2 < 0.0)
            return -1;
        else
            return  1;
    }
    
    /**
     * Point in a triangle.
     * Overrides the method in <code>SimplePolygon2d</code>.
     *
     * @param p - a <code>Point2d</code>.
     * @return <code>true</code> if this triangle contains <code>other</code>.
     */
    @Override
    public final boolean contains(Point2d p) {
        if (p == null)
            return false;

        double a2 = (points[1].x-points[0].x)*(points[2].y-points[0].y)-(points[2].x-points[0].x)*(points[1].y-points[0].y); // vorzeichenbehafteter Flaecheninhalt
        // erste Teilflaeche
        if ((points[1].x * (points[2].y - p.y) + points[2].x * (p.y - points[1].y) +
                p.x * (points[1].y - points[2].y))/a2 <= -repsilon)
            return false;
        // zweite Teilflaeche
        if ((points[2].x * (points[0].y - p.y) + points[0].x * (p.y - points[2].y) +
                p.x * (points[2].y - points[0].y))/a2 <= -repsilon)
            return false;
        // dritte Teilflaeche
        if ((points[0].x * (points[1].y - p.y) + points[1].x * (p.y - points[0].y) +
                p.x * (points[0].y - points[1].y))/a2 <= -repsilon)
            return false;       
        return true;
    }
    @Override
    public boolean contains(EuclideanPoint p) {
        if (p instanceof Point2d) return contains((Point2d)p);
        else if (p instanceof PointNd && ((PointNd)p).dim()==2)
            return contains(new Point2d(((PointNd)p).x[0], ((PointNd)p).x[1]));
        return false;
    }
    
    /**
     * Gets the natural coordinates of a point.
     *
     * @param p - a <code>Point2d</code>.
     * @return <code>double[]</code> representing the natural
     *         coordinates of <code>other</code>.
     */
    public double[] getNaturefromCart(Point2d p) {
        if (p == null)
            return null;
        
        double a2 = (points[1].x-points[0].x)*(points[2].y-points[0].y)-(points[2].x-points[0].x)*(points[1].y-points[0].y); // vorzeichenbehafteter Flaecheninhalt
        double[] erg = new double[3];
        // erste Teilflaeche
        erg[0] = points[1].x * (points[2].y - p.y) + points[2].x * (p.y - points[1].y) +
                p.x * (points[1].y - points[2].y);
        erg[0] /= a2;
        if (erg[0] <= -repsilon)
            return null;
        // zweite Teilflaeche
        erg[1] = points[2].x * (points[0].y - p.y) + points[0].x * (p.y - points[2].y) +
                p.x * (points[2].y - points[0].y);
        erg[1] /= a2;
        if (erg[1] <= -repsilon)
            return null;
        // dritte Teilflaeche
        erg[2] = points[0].x * (points[1].y - p.y) + points[1].x * (p.y - points[0].y) +
                p.x * (points[0].y - points[1].y);
        erg[2] /= a2;
        if (erg[2] <= -repsilon)
            return null;
        
        if(erg[0] <= repsilon)
            erg[0] = 0;
        else if(erg[0] >= 1 - repsilon)
            erg[0] = 1;
        
        if(erg[1] <= repsilon)
            erg[1] = 0;
        else if(erg[1] >= 1 - repsilon)
            erg[1] = 1;
        
        if(erg[2] <= repsilon)
            erg[2] = 0;
        else if(erg[2] >= 1 - repsilon)
            erg[2] = 1;
        
        return erg;
    }
    
    /**
     * Gets the barycentric coordinates of a point.
     *
     * @param p - a <code>Point2d</code>.
     * @return <code>double[]</code> representing the barycentric
     *         coordinates of <code>other</code>.
     */
    public double[] getBarycentricCoord(Point2d p) {
        return getNaturefromCart(p);
    }
    
    public DifferentialScalarFunction2d[] getBarycentricCoordFunction(){
        
        DifferentialScalarFunction2d[] bary = new DifferentialScalarFunction2d[3];
        
        final double A = getArea();
        
        for(int j=0;j<3;j++){
            
            final int i = j;
            
            bary[i] = new AbstractDifferentialFunction2d(){
                
                public double getValue(Point2d p){
                    return Triangle2d.getArea(points[(i+1)%3],points[(i+2)%3],p) / A;
                }
                
                public Vector2d getGradient (Point2d p){
                    return new Vector2d((points[(i+1)%3].y - points[(i+2)%3].y)/A/2., (points[(i+2)%3].x - points[(i+1)%3].x)/A/2. );
                }
                
                @Override
                public ScalarFunction2d[] getDerivation(){
                    ScalarFunction2d[] bary = new ScalarFunction2d[2];
                    bary[0]=new ConstantFunction2d((points[(i+1)%3].y - points[(i+2)%3].y)/A/2.);
                    bary[0]=new ConstantFunction2d((points[(i+2)%3].x - points[(i+1)%3].x)/A/2.);
                    return bary;
                }                
            };
            
        }
        return bary;
    }
    
    /**
     * Gets the voronoiradius.
     *
     * @return the voronoiradius of this triangle.
     */
    public double getVoronoiRadius() {
        return points[0].distance(getVoronoiPoint());
    }
    
    /**
     * Gets the voronoipoint.
     *
     * @return the voronoipoint of this triangle.
     */
    public Point2d getVoronoiPoint() {

        double[] center = new double[2];
        double nenner = 4. * (points[0].x - points[1].x) * (points[1].y - points[2].y) - 4. * (points[1].x - points[2].x) * (points[0].y - points[1].y);
        
        if (nenner != 0) {
            double zaehler1 = 2.
                    * (points[0].x * points[0].x + points[0].y * points[0].y - points[1].x * points[1].x - points[1].y * points[1].y)
                    * (points[1].y - points[2].y) - 2.
                    * (points[1].x * points[1].x + points[1].y * points[1].y - points[2].x * points[2].x - points[2].y * points[2].y)
                    * (points[0].y - points[1].y);
            double zaehler2 = 2.
                    * (points[1].x * points[1].x + points[1].y * points[1].y - points[2].x * points[2].x - points[2].y * points[2].y)
                    * (points[0].x - points[1].x) - 2.
                    * (points[0].x * points[0].x + points[0].y * points[0].y - points[1].x * points[1].x - points[1].y * points[1].y)
                    * (points[1].x - points[2].x);
            center[0] = zaehler1 / nenner;
            center[1] = zaehler2 / nenner;
        }
        return new Point2d(center[0],center[1]); 
    }
    
    /**
     * Point in the delaunaycircle of a triangle.
     *
     * @param p - a <code>Point2d</code>.
     * @return <code>true</code> if the delaunaycircle of this triangle
     *         contains <code>other</code>.
     */
    public boolean inDelaunayCircle(Point2d p) {
        if (p == null)
            return false;
        double radius = getVoronoiRadius();
        double epsilon = radius*repsilon;
        return (getVoronoiPoint().distance(p) < radius-epsilon);
    }

    /**
     * Computes the incentre of a triangle.
     * The inCentre of a triangle is the point which is equidistant
     * from the sides of the triangle.
     * It is also the point at which the bisectors
     * of the triangle's angles meet.
     * It is the centre of the incircle, which
     * is the unique circle that is tangent to each of the triangle's three sides.
     * 
     * @return the point which is the incentre of the triangle
     */
    public Point2d getInCentre() {
        // the lengths of the sides, labelled by their opposite vertex
        double len0 = points[1].distance(points[2]);
        double len1 = points[0].distance(points[2]);
        double len2 = points[0].distance(points[1]);
        double circum = len0 + len1 + len2;

        double inCentreX = (len0 * points[0].x + len1 * points[1].x + len2 * points[2].x) / circum;
        double inCentreY = (len0 * points[0].y + len1 * points[1].y + len2 * points[2].y) / circum;
        return new Point2d(inCentreX, inCentreY);
    }

    /**
     * Computes the barycentre (centre of mass) of a triangle.
     * This is also the point at which the triangle's three
     * medians intersect (a triangle median is the segment from a vertex of the triangle to the
     * midpoint of the opposite side).
     * The centroid divides each median in a ratio of 2:1.
     *
     * @return the barycenter of the triangle
     */
    public Point2d getBarycentre() {
        double x = (points[0].x + points[1].x + points[2].x) / 3;
        double y = (points[0].y + points[1].y + points[2].y) / 3;
        return new Point2d(x, y);
    }

    /** index aus 0,1,2 bzw. -1 wenn nicht gefunden */
    public int getIndex(Point2d p) {
        for (int i = 0; i < 3; i++) {
            if (this.points[i].equals(p)) {
                return i;
            }
        }
        return -1;
    }
    
    /** Elementsize connecting to a vector */
    public double getVectorSize(double vx, double vy) {
        double dl = 0.;
        int i1 = 0, i2 = 1, i3 = 2, i = 0;

        double normV = Function.norm(vx, vy);

        if (normV >= 0.001) {
            do {
                i1 = (0 + i) % 3;
                i2 = (1 + i) % 3;
                i3 = (2 + i) % 3;

                double p1x = points[i1].x + vx;
                double p1y = points[i1].y + vy;

                double s = (points[i1].x * (points[i2].y - points[i3].y) + points[i2].x * (points[i3].y - points[i1].y) +
                        points[i3].x * (points[i1].y - points[i2].y)) / (points[i1].x * (points[i2].y - points[i3].y) +
                        p1x * (points[i3].y - points[i2].y) + points[i2].x * (p1y - points[i1].y) +
                        points[i3].x * (points[i1].y - p1y));
                double t = (points[i1].x * (p1y - points[i2].y) + p1x * (points[i2].y - points[i1].y) +
                        points[i2].x * (points[i1].y - p1y)) / (points[i1].x * (points[i3].y - points[i2].y) +
                        p1x * (points[i2].y - points[i3].y) + points[i2].x * (points[i1].y - p1y) +
                        points[i3].x * (p1y - points[i1].y));

                if (t < 1.00001 && t > -0.00001) // geschnitten
                {
                    dl = Math.abs(s) * normV;
                }

                i++;
            } while ((i <= 3) && (dl == 0.));

            if (dl == 0.) {
                System.out.println("error in element size computation");
                return minEdgeLength;
            }
        } else {
            return minEdgeLength;
        }
        return (dl);
    }
    
    public double[] getNaturalElementCoordinates(EuclideanPoint p) {
        if(p instanceof Point2d) return getNaturefromCart((Point2d)p);
        return null ;
    }
    
    public Edge2d[] getEdges(){
        return new Edge2d[]{new Edge2d(points[0], points[1]),new Edge2d(points[1], points[2]),new Edge2d(points[2], points[0])};
    }
    
    public static void main(String[] args) {
        Point2d p1 = new Point2d(150,300);
        Point2d p2 = new Point2d(400,280);
        Point2d p3 = new Point2d(280,500);
        
        Triangle2d t = new Triangle2d(p1, p2, p3);
        System.out.println(t.getVoronoiPoint());
        System.out.println(t.getVoronoiRadius());
        System.out.println(t.inDelaunayCircle(new Point2d(200., 300.)));
        
        System.out.println(t.getBarycentre());

        System.out.println(t.contains(t.getBarycentre()));
        System.out.println(t.contains(p3));
        double[] koord = t.getNaturefromCart(p3);
        System.out.println(koord[0]); System.out.println(koord[1]); System.out.println(koord[2]);
    }

    @Override
    public NaturalElementCoordinateFunction[] getLocalCoordinateFunction() {
        return new NaturalElementCoordinateFunction[]{
                new NaturalElementCoordinateFunction(this, new PointNd(points[0].x, points[0].y)){
                    @Override
                    public VectorNd getGradient(PointNd p) {
                        Point2d p2d = new Point2d(p.x[0],p.x[1]);
                        if(contains(p2d)) {
                            return new VectorNd(getBarycentricCoordFunction()[0].getGradient(p2d).getCoords());
                        }
                        return new VectorNd(new double[]{0.,0.});
                    }

                    @Override
                    public double getValue(PointNd p) {
                        Point2d p2d = new Point2d(p.x[0],p.x[1]);
                        if(contains(p2d))
                            return getBarycentricCoord(p2d)[0];
                        return 0.;
                    }
                },

        new NaturalElementCoordinateFunction(this, new PointNd(points[1].x, points[1].y)){
                    @Override
                    public VectorNd getGradient(PointNd p) {
                        Point2d p2d = new Point2d(p.x[0],p.x[1]);
                        if(contains(p2d)) {
                            return new VectorNd(getBarycentricCoordFunction()[1].getGradient(p2d).getCoords());
                        }
                        return new VectorNd(new double[]{0.,0.});
                    }

                    @Override
                    public double getValue(PointNd p) {
                        Point2d p2d = new Point2d(p.x[0],p.x[1]);
                        if(contains(p2d))
                            return getBarycentricCoord(p2d)[1];
                        return 0.;
                    }
                },
                new NaturalElementCoordinateFunction(this, new PointNd(points[2].x, points[2].y)){
                    @Override
                    public VectorNd getGradient(PointNd p) {
                        Point2d p2d = new Point2d(p.x[0],p.x[1]);
                        if(contains(p2d)) {
                            return new VectorNd(getBarycentricCoordFunction()[2].getGradient(p2d).getCoords());
                        }
                        return new VectorNd(new double[]{0.,0.});
                    }

                    @Override
                    public double getValue(PointNd p) {
                        Point2d p2d = new Point2d(p.x[0],p.x[1]);
                        if(contains(p2d))
                            return getBarycentricCoord(p2d)[2];
                        return 0.;
                    }
                }
         } ;
    }
}