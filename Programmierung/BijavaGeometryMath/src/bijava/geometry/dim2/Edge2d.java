package bijava.geometry.dim2;

import bijava.geometry.EuclideanPoint;
import bijava.geometry.NaturalElement;
import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;
import bijava.math.Function;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;

/**
 * Edge2d.java provides attributes and methods for edges
 * in a twodimensional space.
 * 
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dr.-Ing. habil. Peter Milbradt<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 1.3, May 2008
 */
public class Edge2d implements NaturalElement{

    public Point2d p0; // 1st point
    public Point2d p1; // 2nd point
    
    protected final static double epsilon = 1e-6;

    /**
     * Constructs an edge in a twodimensional space.
     * 
     * @param p0 1st point.
     * @param p1 2nd point.
     */
    public Edge2d(Point2d p0, Point2d p1) {
        this.p0 = p0;
        this.p1 = p1;
    }

    public Point2d getCenter() {
        return new Point2d((p0.x+p1.x)/2.,(p0.y+p1.y)/2.);
    }

    /**
     * Gets the length.
     * 
     * @return length.
     */
    public double getLength() {
        return p0.distance(p1);
    }

    /**
     * Gets the bounding box.
     * 
     * @return bounding box.
     */
    public BoundingBox2d getBoundingBox() {
        double xmin, xmax, ymin, ymax;
        
        if (p0.x < p1.x) {
            xmin = p0.x;
            xmax = p1.x;
        } else {
            xmin = p1.x;
            xmax = p0.x;
        }
        if (p0.y < p1.y) {
            ymin = p0.y;
            ymax = p1.y;
        } else {
            ymin = p1.y;
            ymax = p0.y;
        }
        
        return new BoundingBox2d(new Point2d(xmin, ymin), xmax - xmin, ymax - ymin);
    }

    /**
     * Indicates if <code>this</code> contains a point (x, y).
     * 
     * @param x coordinate in 1st dimension.
     * @param y coordinate in 2nd dimension.
     * @return <code>true</code> if <code>this</code> contains <code>other</code>.
     */
    public boolean contains(double x, double y) {
//        double dx = p1.x - p0.x;
//        double dy = p1.y - p0.y;
//
//        double lambdaX = 0., lambdaY = 0.;
//        double repsilon = epsilon * this.getLength();
//
//        if (Math.abs(dx) >= repsilon) {
//            lambdaX = (x - p0.x) / dx;
//        }
//        if (Math.abs(dy) >= repsilon) {
//            lambdaY = (y - p0.y) / dy;
//        }
//
//        // Fallunterscheidung fuer dx = 0
//        if (lambdaX <= -repsilon || lambdaX >= 1. + repsilon) {
//            return false;
//        }
//        if (lambdaY <= -repsilon || lambdaY >= 1. + repsilon) {
//            return false;
//        }
//
//        // geaendert von Tim (06-12-11), macht keinen Sinn
////        if (Math.abs(lambdaX - lambdaY) >= repsilon) return false;
//
//        return true;
        
        double repsilon = epsilon * this.getLength();
        
        return this.distance(x, y) < repsilon;
    }

    /**
     * Indicates if <code>this</code> contains a point p.
     * 
     * @param p point.
     * @return <code>true</code> if <code>this</code> contains <code>other</code>.
     */
    public boolean contains(Point2d p) {
        if (p == p0 || p == p1) {
            return true;
        }
        return this.contains(p.x, p.y);
    }
    
    /**
     * Indicates if <code>this</code> contains an edge ed.
     * 
     * @param ed edge.
     * @return <code>true</code> if <code>this</code> contains <code>other</code>.
     */
    public boolean contains(Edge2d ed) {
        return this.contains(ed.p0) && this.contains(ed.p1);
    }

    /**
     * Gets the natural coordinates of a point (x, y).
     * 
     * @param x coordinate in 1st dimension.
     * @param y coordinate in 2nd dimension.
     * @return natural coordinates of <code>other</code>.
     */
    public double[] getNaturefromCart(double x, double y) {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;

        double lambdaX = 0., lambdaY = 0.;
        double repsilon = epsilon * this.getLength();

        if (Math.abs(dx) >= repsilon) {
            lambdaX = (x - p0.x) / dx;
        }
        if (Math.abs(dy) >= repsilon) {
            lambdaY = (y - p0.y) / dy;
        }

        // Fallunterscheidung fuer dx = 0
        if (lambdaX <= -repsilon || lambdaX >= 1. + repsilon) {
            return null;
        }
        if (lambdaY <= -repsilon || lambdaY >= 1. + repsilon) {
            return null;
        }

        // geaendert von Tim (06-12-11), macht keinen Sinn
//        if (Math.abs(lambdaX - lambdaY) >= repsilon) return null;

        double lambda = Function.norm(lambdaX, lambdaY);

        return new double[]{lambda, 1. - lambda};
    }

    /**
     * Gets the natural coordinates of a point p.
     * 
     * @param p point.
     * @return natural coordinates of <code>other</code>.
     */
    public double[] getNaturefromCart(Point2d p) {
        if (p == p0) {
            return new double[]{1., 0.};
        }
        if (p == p1) {
            return new double[]{0., 1.};
        }
        return this.getNaturefromCart(p.x, p.y);
    }

    /**
     * Gets the distance between the action line and a point (x, y).
     * 
     * @param x coordinate in 1st dimension.
     * @param y coordinate in 2nd dimension.
     * @return distance between the action line and <code>other</code>.
     */
    public double distanceLine(double x, double y) {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        double dx1 = x - p0.x;
        double dy1 = y - p0.y;
        return Math.abs((dx * dy1 - dy * dx1) / Math.sqrt(dx * dx + dy * dy));
    }

    /**
     * Gets the distance between the action line and a point p.
     * 
     * @param p point.
     * @return distance between the action line and <code>other</code>.
     */
    public double distanceLine(Point2d p) {
        return this.distanceLine(p.x, p.y);
    }

    /**
     * Gets the distance to a point (x, y).
     * 
     * @param x coordinate in 1st dimension.
     * @param y coordinate in 2nd dimension.
     * @return distance to <code>other</code>.
     */
    public double distance(double x, double y) {
        double s = this.getDirectionParam(x, y);
        
        if (s <= 0.) {
            double dx = p0.x - x;
            double dy = p0.y - y;
            return Math.sqrt(dx * dx + dy * dy);
        } else if (s >= 1.) {
            double dx = p1.x - x;
            double dy = p1.y - y;
            return Math.sqrt(dx * dx + dy * dy);
        } else {
            return this.distanceLine(x, y);
        }
    }

    /**
     * Gets the distance to a point p.
     * 
     * @param p point.
     * @return distance to <code>other</code>.
     */
    public double distance(Point2d p) {
        double s = this.getDirectionParam(p);
        
        if (s <= 0.) {
            return p0.distance(p);
        } else if (s >= 1.) {
            return p1.distance(p);
        } else {
            return this.distanceLine(p);
        }
    }
    
    /**
     * Gets the distance vector to a point p.
     * 
     * @param p point.
     * @return distance vector to <code>other</code>.
     */
    public Vector2d distanceVector(Point2d p) {
        double lx = p1.x - p0.x;
        double ly = p1.y - p0.y;
        double dx = p.x - p0.x;
        double dy = p.y - p0.y;
        double s = (dx * lx + dy * ly) / (lx * lx + ly * ly);

        if (s > 0. && s < 1.) {
            double theta = Math.atan(ly / lx);
            double sin = Math.sin(theta);
            double cos = Math.cos(theta);
            double tmp = -sin * dx + cos * dy;
            dx = -sin * tmp;
            dy = cos * tmp;

        } else if (s >= 1.) {
            dx = p.x - p1.x;
            dy = p.y - p1.y;
        }
        return new Vector2d(dx, dy);
    }

    /**
     * Gets the distance vector to an edge ed.
     * 
     * @param ed edge.
     * @return distance vector to <code>other</code>.
     */
    public Vector2d distanceVector(Edge2d ed) {
        if (this.intersects(ed)) {
            return new Vector2d();
        }
        Vector2d[] v = {
            this.distanceVector(ed.p0),
            this.distanceVector(ed.p1),
            ed.distanceVector(this.p0),
            ed.distanceVector(this.p1)
        };
        Vector2d vMin = v[0];
        double dMin = v[0].length();

        for (int i = 1; i < v.length; i++) {
            double di = v[i].length();

            if (di < dMin) {
                dMin = di;
                vMin = v[i];
            }
        }
        return vMin;
    }

    /**
     * Computes the included angle at the point c composed
     * of the points and the edge.
     * 
     * @param e edge.
     * @param p point.
     * @return included angle.
     */
    public double computeIncludedAngle(Edge2d e, Point2d p) {
        Point2d A = e.p0;
        Point2d B = e.p1;
        double a = B.distance(p);
        double b = A.distance(p);
        double c = A.distance(B);
        return Math.acos(((a * a) + (b * b) - (c * c)) / (2 * a * b));
    }
    
    /**
     * Gets the direction parameter s of a point (x, y):<br>
     * (x, y) = p0 + s * p1.
     * 
     * @param x coordinate in 1st dimension.
     * @param y coordinate in 2nd dimension.
     * @return direction parameter of <code>other</code>.
     */
    public double getDirectionParam(double x, double y) {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        double dx1 = x - p0.x;
        double dy1 = y - p0.y;
        return (dx1 * dx + dy1 * dy) / (dx * dx + dy * dy);
    }

    /**
     * Gets the direction parameter s of a point p:<br>
     * p = p0 + s * p1.
     * 
     * @param p point.
     * @return direction parameter of <code>other</code>.
     */
    public double getDirectionParam(Point2d p) {
        return this.getDirectionParam(p.x, p.y);
    }

    /**
     * Indicates if <code>this</code> intersects other edge.
     * 
     * @param edge e.
     * @return <code>true</code> if <code>this</code> intersects <code>other</code>.
     */
    public boolean intersects(Edge2d e) {
        if (e == this) {
            return false;
        }
        return Edge2d.intersects(p0, p1, e.p0, e.p1);
    }

    /**
     * Gets the intersection point of <code>this</code> and other edge.
     * 
     * @param e edge.
     * @return intersection point of <code>this</code> and <code>other</code>.
     */
    public Point2d getIntersectionPoint(Edge2d e) {
        if (e == this) {
            return null;
        }
        return Edge2d.getIntersectionPoint(p0, p1, e.p0, e.p1);
    }
    
    
    /**
     *  Schnittpunkt der Geraden durch einen Punkt p senkrecht zur Kante 
     */
    public Point2d getIntersectionPoint(Point2d p) {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;

//        double t = (dx*(p.x - p0.x) - dy* (p0.y - p.y))/ (dx*dx+dy*dy);
        double t = getDirectionParam(p);
        double x = p0.x + t * dx;
        double y = p0.y + t * dy;

        return new Point2d(x, y);
    }

    /**
     * Gets the intersection point of a point vertically to the edge
     * 
     * @param p a point 
     */
    public Point2d getVerticalProjection(Point2d p) {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;
        double t = getDirectionParam(p);
        double x = p0.x + t * dx;
        double y = p0.y + t * dy;
        return new Point2d(x, y);
    }

    /**
     * Gets the direction parameters of cutpoint of <code>this</code> and other edge.
     * 
     * @param e edge.
     * @return direction parameters of cutpoint of <code>this</code> and <code>other</code>.
     */
    public double[] getDirectionParamofCutPoint(Edge2d e) {
        if (e == this) {
            return null;
        }
        return Edge2d.getDirectionParamofCutPoint(p0, p1, e.p0, e.p1);
    }

    /**
     * Indicates if <code>this</code> and other edge has equal action lines.
     * 
     * @param e edge.
     * @return <code>true</code> if <code>this</code> and <code>other</code> has equal action lines.
     */
    public boolean equalsLine(Edge2d e) {
        if (e == this) {
            return true;
        }
        return (this.distanceLine(e.p0) == 0. && this.distanceLine(e.p1) == 0.);
    }

    /**
     * Indicates if <code>this</code> and other edge are congruent.
     * 
     * @param e edge.
     * @return <code>true</code> if <code>this</code> and <code>other</code> are congruent.
     */
    public boolean isCongruent(Edge2d e) {
        if (e == this) {
            return true;
        }
        return (p0.equals(e.p0) && p1.equals(e.p1)) || (p0.equals(e.p1) && p1.equals(e.p0));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Edge2d)) {
            return false;
        }
        Edge2d edge = (Edge2d) o;
        
        return p0.equals(edge.p0) && p1.equals(edge.p1);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.p0 != null ? this.p0.hashCode() : 0);
        hash = 11 * hash + (this.p1 != null ? this.p1.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + p0 + ", " + p1;
    }


    // Winkel eines Vector zur positiven x-Achse
    public double winkel(Edge2d e) {
        double v1x=p1.x-p0.x;
        double v1y=p1.y-p0.y;
        
        double v2x=e.p1.x-e.p0.x;
        double v2y=e.p1.y-e.p0.y;

        double laenge1 = Math.sqrt(Math.pow(v1x, 2) + Math.pow(v1y, 2));
        double laenge2 = Math.sqrt(Math.pow(v2x, 2) + Math.pow(v2y, 2));

        double alpha = Math.acos((v1x*v2x+v1y*v2y)/laenge1/laenge2)*180 /Math.PI;

        return alpha;
    }

    /**
     * Gets the point on the action line of an edge p0-p1 to a length. 
     * 
     * @param p0 1st point of edge.
     * @param p1 2nd point of edge.
     * @param length length.
     * @return point on the action line.
     */
    public static Point2d getPointFromLength(Point2d p0, Point2d p1, double length) {
        return getPointFromNorm(p0, p1, length / p0.distance(p1));
    }

    /**
     * Gets the point on the action line of an edge p0-p1 to a normed length s. 
     * 
     * @param p0 1st point of edge.
     * @param p1 2nd point of edge.
     * @param s normed length.
     * @return point on the action line.
     */
    public static Point2d getPointFromNorm(Point2d p0, Point2d p1, double s) {
        return new Point2d(p0.x + s * (p1.x - p0.x), p0.y + s * (p1.y - p0.y));
    }

    /**
     * Gets a normal of an edge p0-p1 with the length d.
     * 
     * @param p0 1st point of edge.
     * @param p1 2nd point of edge.
     * @param d length of the normal.
     * @return normal with the length d.
     */
    public static Vector2d getNormal(Point2d p0, Point2d p1, double d) {
        if (d == 0.) {
            return new Vector2d();
        }
        Vector2d normal = new Vector2d(p0.y - p1.y, p1.x - p0.x);
        double s = d / normal.length();
        normal.x *= s;
        normal.y *= s;
        return normal;
    }

    /**
     * Gets the angle included at point p by the edges p0-p and p-p1.
     * 
     * @param p0 1st point of 1st edge.
     * @param p 2nd point of 1st edge and 1st point of 2nd edge.
     * @param p1 2nd point of 2nd edge.
     * @return included angle.
     */
    public static double getAngleIncludedAt(Point2d p0, Point2d p, Point2d p1) {
        double a = p.distance(p1);
        double b = p1.distance(p0);
        double c = p0.distance(p);
        return Math.acos((c * c + a * a - b * b) / (2. * c * a));
    }
    
    public static boolean contains(Point2d p0, Point2d p1, Point2d p) {
        if (p0.equals(p) || p1.equals(p)) {
            return true;
        }
        double repsilon = epsilon * p0.distance(p1);
        return Edge2d.distance(p0, p1, p) < repsilon;
    }

    /**
     * Indicates if edge i intersects edge j.
     * 
     * @param p0i 1st point of edge i.
     * @param p1i 2nd point of edge i.
     * @param p0j 1st point of edge j.
     * @param p1j 2nd point of edge j.
     * @return <code>true</code> if edge i intersects edge j.
     */
    public static boolean intersects(Point2d p0i, Point2d p1i, Point2d p0j, Point2d p1j) {
        double[] param = Edge2d.getDirectionParamOfIntersectionPoint(p0i, p1i, p0j, p1j);
        double s = param[0];
        double t = param[1];

        if ((s == 0. && t == 1.) && !p0i.equals(p1j)) {
            return false;
        }
        if ((s == 1. && t == 0.) && !p1i.equals(p0j)) {
            return false;
        }
        return s >= 0. && s <= 1. && t >= 0. && t <= 1.;
    }

    /**
     * Gets the direction parameters of cutpoint of 1st and 2nd edge.
     * 
     * @param P00 1st point of 1st edge.
     * @param P01 2nd point of 1st edge.
     * @param P10 1st point of 2nd edge.
     * @param P11 2nd point of 2nd edge.
     * @return direction parameters of cutpoint of 1st and 2nd edge.
     */
    public static double[] getDirectionParamofCutPoint(Point2d P00, Point2d P01, Point2d P10, Point2d P11) {
        double s = (P00.x * (P10.y - P11.y) + P10.x * (P11.y - P00.y) + P11.x * (P00.y - P10.y)) / (P00.x * (P10.y - P11.y) + P01.x * (P11.y - P10.y) + P10.x * (P01.y - P00.y) + P11.x * (P00.y - P01.y));
        double t = (P00.x * (P01.y - P10.y) + P01.x * (P10.y - P00.y) + P10.x * (P00.y - P01.y)) / (P00.x * (P11.y - P10.y) + P01.x * (P10.y - P11.y) + P10.x * (P00.y - P01.y) + P11.x * (P01.y - P00.y));
        
        return new double[]{s, t};
    }

    /**
     * Gets the direction parameters of intersection point two edges i and j.
     * 
     * @param p0i 1st point of edge i.
     * @param p1i 2nd point of edge i.
     * @param p0j 1st point of edge j.
     * @param p1j 2nd point of edge j.
     * @return direction parameters of intersection point.
     */
    public static double[] getDirectionParamOfIntersectionPoint(Point2d p0i, Point2d p1i, Point2d p0j, Point2d p1j) {
        double s = (p0i.x * (p0j.y - p1j.y) + p0j.x * (p1j.y - p0i.y) + p1j.x * (p0i.y - p0j.y)) /
                (p0i.x * (p0j.y - p1j.y) + p1i.x * (p1j.y - p0j.y) + p0j.x * (p1i.y - p0i.y) + p1j.x * (p0i.y - p1i.y));
        double t = (p0i.x * (p1i.y - p0j.y) + p1i.x * (p0j.y - p0i.y) + p0j.x * (p0i.y - p1i.y)) /
                (p0i.x * (p1j.y - p0j.y) + p1i.x * (p0j.y - p1j.y) + p0j.x * (p0i.y - p1i.y) + p1j.x * (p1i.y - p0i.y));
        return new double[]{s, t};
    }

    /**
     * Gets the intersection point of two edges i and j.
     * 
     * @param p0i 1st point of edge i.
     * @param p1i 2nd point of edge i.
     * @param p0j 1st point of edge j.
     * @param p1j 2nd point of edge j.
     * @return intersection point.
     */
    public static Point2d getIntersectionPoint(Point2d p0i, Point2d p1i, Point2d p0j, Point2d p1j) {
        double[] param = Edge2d.getDirectionParamOfIntersectionPoint(p0i, p1i, p0j, p1j);
        double s = param[0];
        double t = param[1];

        if ((s == 0. && t == 1.) && !p0i.equals(p1j)) {
            return null;
        }
        if ((s == 1. && t == 0.) && !p1i.equals(p0j)) {
            return null;
        }
        if (!(s >= 0. && s <= 1. && t >= 0. && t <= 1.)) {
            return null;
        }
        return getPointFromNorm(p0i, p1i, s);
    }
    
    public static double distance(Point2d p0, Point2d p1, Point2d p) {
        double lx = p1.x - p0.x;
        double ly = p1.y - p0.y;
        double dx = p.x - p0.x;
        double dy = p.y - p0.y;
        double s = (dx * lx + dy * ly) / (lx * lx + ly * ly);

        if (s > 0. && s < 1.) {
            double theta = Math.atan(ly / lx);
            double sin = Math.sin(theta);
            double cos = Math.cos(theta);
            double tmp = -sin * dx + cos * dy;
            dx = -sin * tmp;
            dy = cos * tmp;

        } else if (s >= 1.) {
            dx = p.x - p1.x;
            dy = p.y - p1.y;
        }
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public static double distance(Point2d p0i, Point2d p1i, Point2d p0j, Point2d p1j) {
        if (Edge2d.intersects(p0i, p1i, p0j, p1j)) {
            return 0.;
        }
        double dij0 = Edge2d.distance(p0i, p1i, p0j);
        double d = dij0;
        double dij1 = Edge2d.distance(p0i, p1i, p1j);
        d = (dij1 < d) ? dij1 : d;
        double dji0 = Edge2d.distance(p0j, p1j, p0i);
        d = (dji0 < d) ? dji0 : d;
        double dji1 = Edge2d.distance(p0j, p1j, p1i);
        d = (dji1 < d) ? dji1 : d;
        return d;
    }
    
    public static void scale(Point2d p0, Point2d p1, double s, double anchorx, double anchory) {
        p0.x -= anchorx;
        p0.y -= anchory;
        p1.x -= anchorx;
        p1.y -= anchory;
        p0.x *= s;
        p0.y *= s;
        p1.x *= s;
        p1.y *= s;
        p0.x += anchorx;
        p0.y += anchory;
        p1.x += anchorx;
        p1.y += anchory;
    }

    @Override
    public double[] getNaturalElementCoordinates(EuclideanPoint p) {
        if (p instanceof Point2d)
            return getNaturefromCart((Point2d)p);
        else throw new IllegalArgumentException("Point has to be an instance of Point2d");
    }

    @Override
    public NaturalElementCoordinateFunction[] getLocalCoordinateFunction() {
        return new NaturalElementCoordinateFunction[]{
                    new NaturalElementCoordinateFunction() {

                        @Override
                        public VectorNd getGradient(PointNd x) {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }

                        @Override
                        public double getValue(PointNd p) {
                            Point2d p2d = new Point2d(p.x[0], p.x[1]);
                            if (contains(p2d)) {
                                return p1.distance(p2d) / p1.distance(p0);
                            }
                            return 0.;
                        }

                        @Override
                        public NaturalElement getElement() {
                            return Edge2d.this;
                        }

                        @Override
                        public PointNd getCoordinate() {
                            return new PointNd(p0.x, p0.y);
                        }
                    },
                    new NaturalElementCoordinateFunction() {

                        @Override
                        public VectorNd getGradient(PointNd x) {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }

                        @Override
                        public double getValue(PointNd p) {
                            Point2d p2d = new Point2d(p.x[0], p.x[1]);
                            if (contains(p2d)) {
                                return p0.distance(p2d) / p1.distance(p0);
                            }
                            return 0.;
                        }

                        @Override
                        public NaturalElement getElement() {
                            return Edge2d.this;
                        }

                        @Override
                        public PointNd getCoordinate() {
                            return new PointNd(p1.x, p1.y);
                        }
                    }
                };
    }

    @Override
    public boolean contains(EuclideanPoint p) {
        if(p instanceof Point2d)
            return contains((Point2d)p);
        else if (p instanceof PointNd && ((PointNd)p).dim()==2)
            return contains(new Point2d(((PointNd)p).x[0], ((PointNd)p).x[1]));
        return false;
    }
}
