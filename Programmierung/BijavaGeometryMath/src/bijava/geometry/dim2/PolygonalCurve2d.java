package bijava.geometry.dim2;

//==========================================================================//
//  CLASS PolygonalCurve2d                                                  //
//==========================================================================//
/** "PolygonalCurve2d" is a class for a curve as a polygon
 *  in a twodimensional space.
 *
 *  <p><strong>Version:</strong><br>
 *  <dd>1.2, january 2008</dd>
 *  <p><strong>Author:</strong><br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd>
 *  <dd>Dipl.-Ing. Mario Hoecker</dd>                                        */
//==========================================================================//
public class PolygonalCurve2d implements Cloneable, Curve2d {

    protected Point2d[] points; // array of points on the curve

    public PolygonalCurve2d()
    {

    }
    /**
     * Constructs a polygonal curve from two points p1, p2.
     *
     * @param p1 1st point.
     * @param p2 2nd point.
     */
    public PolygonalCurve2d(Point2d p1, Point2d p2) {
        this.points = new Point2d[]{p1,p2};
    }

    /**
     * Constructs a polygonal curve.
     *
     * @param <code>Point2d</code>[] - the array of points on the curve.
     */
    public PolygonalCurve2d(Point2d[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("number of points < 2");
        }
        this.points = points;
    }

    /**
     * Constructs a twodimensional polygonal curve from a polygonal curve pg.
     *
     * @param pg polygonal curve.
     */
    public PolygonalCurve2d(PolygonalCurve2d pg) {
        points = new Point2d[pg.points.length];

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point2d(pg.points[i]);
        }
    }

    @Override
    public PolygonalCurve2d clone() {
        return new PolygonalCurve2d(this);
    }

    public int size(){
        return points.length;
    }


    public void appendPoint(Point2d p)
    {
        if(points!= null)
        {
            Point2d [] newpoints =  new Point2d[points.length+1];
            for (int i = 0; i < points.length; i++) {
                newpoints[i] = points[i];
            }
            newpoints[points.length] = p;
            points = newpoints;
        }
        else
        {
            points = new Point2d[]{p};
        }
    }

    /**
     * Gets the point to an argument s.
     *
     * @param <code>double</code> - the argument s.
     * @return <code>Point2d</code> - the point on the curve to the argument s.
     * @return <code>null</code> if s is negativ or greater than the length of the curve.
     */
    @Override
    public Point2d getPoint(double s) {
        if (points.length == 1 & s == 0.0) {
            return points[0];
        }
        if (s < 0.0 | points.length < 2) {
            return null;
        }
        double l = 0.0;
        double d = 0.0;

        for (int i = 1; i < points.length; i++) {
            d = points[i - 1].distance(points[i]);
            l += d;

            if (l >= s) {
                double sf = 1.0 - ((l - s) / d);

                Point2d p1 = points[i - 1];
                Point2d p2 = points[i];
                Point2d p = new Point2d();

                p.x = p1.x + sf * (p2.x - p1.x);
                p.y = p1.y + sf * (p2.y - p1.y);

                return p;
            }
        }
        return null;
    }

    /**
     * Gets the point at the specified index.
     *
     * @param <code>int</code> - the specified index into the array of points.
     * @return <code>Point2d</code> - the point at the specified index.
     * @return <code>null</code> if the index is negative or not less than the current number of points.
     */
    public Point2d getPointAt(int i) {
        if (i < 0 || i > points.length - 1) {
            throw new IllegalArgumentException("index out of range");
        }
        return points[i];
    }

    /**
     * Gets the points on the curve.
     *
     * @return <code>Point2d</code>[] - the array of points on the curve.
     */
    public Point2d[] getPoints() {
        return points;
    }

    /**
     * Gets the length of the curve.
     *
     * @return <code>double</code> - the length of the curve.
     */
    @Override
    public double getLength() {
        double length = 0.;

        if (points.length < 2) {
            return 0.;
        }
        for (int i = 1; i < points.length; i++) {
            length += points[i - 1].distance(points[i]);
        }
        return length;
    }

    /**
     * Gets the bounding box of the curve.
     *
     * @return <code>BoundingBox2d</code> - the bounding box of the curve.
     */
    public BoundingBox2d getBoundingBox() {
        double xmin = points[0].x;
        double ymin = points[0].y;
        double xmax = points[0].x;
        double ymax = points[0].y;

        for (int i = 1; i < points.length; i++) {
            xmin = Math.min(xmin, points[i].x);
            ymin = Math.min(ymin, points[i].y);
            xmax = Math.max(xmax, points[i].x);
            ymax = Math.max(ymax, points[i].y);
        }
        return new BoundingBox2d(new Point2d(xmin, ymin), (xmax - xmin), (ymax - ymin));
    }

    /**
     * Gets the center.
     *
     * @return center.
     */
    public Point2d getCenter() {
        Point2d cp = new Point2d();

        for (int i = 0; i < points.length; i++) {
            cp.x += points[i].x;
            cp.y += points[i].y;
        }
        cp.x /= points.length;
        cp.y /= points.length;
        return cp;
    }

    /**
     * Gets a part of the curve.
     *
     * @param <code>int</code>, <code>int</code> - the point-indices at begin and end of the part of the curve.
     * @return <code>PolygonalCurve2d</code> - the part of the curve.
     * @return <code>null</code> if at least one index is negative or not less than the current number of points.
     */
    public PolygonalCurve2d getPart(int i0, int i1) {
        if (i0 < 0 || i0 > points.length - 2 || i1 < 1 || i1 > points.length - 1 || i0 >= i1) {
            throw new IllegalArgumentException("index out of range");
        }
        Point2d[] pts = new Point2d[i1 - i0 + 1];

        for (int i = i0; i <= i1; i++) {
            pts[i] = points[i];
        }
        return new PolygonalCurve2d(pts);
    }

    /**
     * Gets a polygon by closing the curve.
     *
     * @return <code>Polygon2d</code> - the polygon by closing the curve.
     */
    public Polygon2d close() {
        return new Polygon2d(points);
    }

    /**
     * Tests the curve for simplicity.
     *
     * @return <code>true</code> if the curve is simple.
     */
    public boolean isSimple() {
        Edge2d ed1 = new Edge2d(null, null);
        Edge2d ed2 = new Edge2d(null, null);

        for (int i = 0; i < points.length - 1; i++) {
            ed1.p0 = points[i];
            ed1.p1 = points[i + 1];

            for (int j = 0; j < i - 1; j++) {
                ed2.p0 = points[j];
                ed2.p1 = points[j + 1];

                if (ed1.intersects(ed2)) {
                    return false;
                }
            }
            for (int j = i + 1; j < points.length - 1; j++) {
                ed2.p0 = points[j];
                ed2.p1 = points[j + 1];

                if (ed1.intersects(ed2)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Tests if this curve contains a point p.
     *
     * @param p point.
     * @return <code>true</code> if this curve contains p.
     */
    public boolean contains(Point2d p) {
        Edge2d edi = new Edge2d(null, null);

        for (int i = 0; i < points.length - 1; i++) {
            edi.p0 = points[i];
            edi.p1 = points[i + 1];

            if (edi.contains(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if <code>this</code> contains an edge ed.
     *
     * @param ed edge.
     * @return <code>true</code> if <code>this</code> contains <code>other</code>.
     */
    public boolean contains(Edge2d ed) {
        Edge2d edi = new Edge2d(null, null);

        for (int i = 0; i < points.length - 1; i++) {
            edi.p0 = points[i];
            edi.p1 = points[i + 1];

            if (edi.contains(ed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if <code>this</code> contains a polygonal curve po.
     *
     * @param po polygonal curve.
     * @return <code>true</code> if <code>this</code> contains <code>other</code>.
     */
    public boolean contains(PolygonalCurve2d po) {
        Edge2d edj = new Edge2d(null, null);

        for (int j = 0; j < po.points.length - 1; j++) {
            edj.p0 = po.points[j];
            edj.p1 = po.points[j + 1];

            if (!this.contains(edj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indicates if <code>this</code> intersects an edge ed.
     *
     * @param ed edge.
     * @return <code>true</code> if <code>this</code> intersects <code>other</code>.
     */
    public boolean intersects(Edge2d ed) {
        Edge2d edi = new Edge2d(null, null);

        for (int i = 0; i < points.length - 1; i++) {
            edi.p0 = points[i];
            edi.p1 = points[i + 1];

            if (edi.intersects(ed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * gibt den Index zurueck an dem die Kante das Polygon das erste mal schneidet
     *
     * @param ed edge.
     * @return <code>index</code>
     */
    public int intersectAt(Edge2d ed) {
        Edge2d edi = new Edge2d(null, null);

        for (int i = 0; i < points.length - 1; i++) {
            edi.p0 = points[i];
            edi.p1 = points[i + 1];

            if (edi.intersects(ed)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Indicates if <code>this</code> intersects a polygonal curve po.
     *
     * @param po polygonal curve.
     * @return <code>true</code> if <code>this</code> intersects <code>other</code>.
     */
    public boolean intersects(PolygonalCurve2d po) {
        Edge2d edj = new Edge2d(null, null);

        for (int j = 0; j < po.points.length - 1; j++) {
            edj.p0 = po.points[j];
            edj.p1 = po.points[j + 1];

            if (this.intersects(edj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * gibt den Index zurueck an dem die Kante das Polygon das erste mal schneidet
     *
     * @param po polygonal curve.
     * @return <code>true</code> if <code>this</code> intersects <code>other</code>.
     */
    public int intersectAt(PolygonalCurve2d po) {
        Edge2d edj = new Edge2d(null, null);

        for (int j = 0; j < po.points.length - 1; j++) {
            edj.p0 = po.points[j];
            edj.p1 = po.points[j + 1];

            if (this.intersects(edj)) {
                return j;
            }
        }
        return -1;
    }

    /**
     * Indicates if <code>this</code> intersects a polygon po.
     *
     * @param po polygon.
     * @return <code>true</code> if <code>this</code> intersects <code>other</code>.
     */
    public boolean intersects(SimplePolygon2d po) {
        if (po.contains(points[0])) {
            return true;
        }
        Edge2d edj = new Edge2d(null, null);

        for (int j = 0; j < po.points.length; j++) {
            edj.p0 = po.points[j];
            edj.p1 = po.points[(j + 1) % po.points.length];

            if (this.intersects(edj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the distance vector to a point p.
     *
     * @param p point.
     * @return distance vector to <code>other</code>.
     */
    public Vector2d distanceVector(Point2d p) {
        Vector2d vMin = null;
        double dMin = Double.POSITIVE_INFINITY;
        Edge2d edi = new Edge2d(null, null);

        for (int i = 0; i < points.length - 1; i++) {
            edi.p0 = points[i];
            edi.p1 = points[i + 1];
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
     * Gets the distance vector to an edge ed.
     *
     * @param ed edge.
     * @return distance vector to <code>other</code>.
     */
    public Vector2d distanceVector(Edge2d ed) {
        Vector2d vMin = null;
        double dMin = Double.POSITIVE_INFINITY;
        Edge2d edi = new Edge2d(null, null);

        for (int i = 0; i < points.length - 1; i++) {
            edi.p0 = points[i];
            edi.p1 = points[i + 1];
            Vector2d vi = edi.distanceVector(ed);
            double di = vi.length();

            if (di < dMin) {
                dMin = di;
                vMin = vi;
            }
        }
        return vMin;
    }

    /**
     * Gets the distance vector to a polygonal curve po.
     *
     * @param po polygonal curve.
     * @return distance vector to <code>other</code>.
     */
    public Vector2d distanceVector(PolygonalCurve2d po) {
        Vector2d v = null;
        double d = Double.POSITIVE_INFINITY;
        Edge2d edj = new Edge2d(null, null);

        for (int j = 0; j < po.points.length - 1; j++) {
            edj.p0 = po.points[j];
            edj.p1 = po.points[j + 1];
            Vector2d vj = this.distanceVector(edj);
            double dj = vj.length();

            if (dj < d) {
                d = dj;
                v = vj;
            }
        }
        return v;
    }

    /**
     * Gets the distance vector to a polygon po.
     *
     * @param po polygon.
     * @return distance vector to <code>other</code>.
     */
    public Vector2d distanceVector(SimplePolygon2d po) {
        if (this.intersects(po)) {
            return new Vector2d();
        }
        Vector2d v = null;
        double d = Double.POSITIVE_INFINITY;
        Edge2d edj = new Edge2d(null, null);

        for (int j = 0; j < po.points.length; j++) {
            edj.p0 = po.points[j];
            edj.p1 = po.points[(j + 1) % po.points.length];
            Vector2d vj = this.distanceVector(edj);
            double dj = vj.length();

            if (dj < d) {
                d = dj;
                v = vj;
            }
        }
        return v;
    }

    /**
     * Tests the curve for congruence.
     *
     * @param <code>PolygonalCurve2d</code> - the polygonal curve to test.
     * @return <code>true</code> if <code>this</code> and <code>other</code> has equal edges.
     * @return <code>null</code> if <code>other</code> is <code>null</code>.
     */
    public boolean isCongruent(PolygonalCurve2d curve) {
        if (curve == null) {
            return false;
        }
        if (points.length != curve.points.length) {
            return false;
        }
        int numEqualEdges = 0;
        int numEqualAntiOriEdges = 0;

        for (int i = 0; i < points.length - 1; i++) {
            for (int j = 0; j < points.length - 1; j++) {
                if (points[i].equals(curve.points[j]) && points[i + 1].equals(curve.points[j + 1])) {
                    numEqualEdges++;
                    break;
                } else if (points[i].equals(curve.points[j + 1]) && points[i + 1].equals(curve.points[j])) {
                    numEqualAntiOriEdges++;
                    break;
                }
            }
        }
        return (numEqualEdges == points.length || numEqualAntiOriEdges == points.length);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PolygonalCurve2d) ? this.equals((PolygonalCurve2d) o) : false;
    }

    // TODO: Was ist das denn?!?!
    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    /**
     * Tests the curve for equality.
     *
     * @param <code>PolygonalCurve2d</code> - the polygonal curve to test.
     * @return <code>true</code>, if all points of <code>this</code> and <code>other</code> are equal.
     * @return <code>null</code> if <code>other</code> is <code>null</code>.
     */
    public boolean equals(PolygonalCurve2d curve) {
        if (curve == null) {
            return false;
        }
        for (int i = 0; i < points.length; i++) {
            if (!this.points[i].equals(curve.points[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String s = super.toString() + ": ";

        for (int i = 0; i < points.length; i++) {
            s += points[i];
        }
        return s;
    }

    // STATISCHE FUNKTIONEN
    public static boolean contains(Point2d[] pts, Point2d p) {
        for (int i = 0; i < pts.length - 1; i++) {
            Point2d p0i = pts[i];
            Point2d p1i = pts[i + 1];

            if (Edge2d.contains(p0i, p1i, p)) {
                return true;
            }
        }
        return false;
    }

    public static boolean intersects(Point2d[] pts, Point2d p0, Point2d p1) {
        for (int i = 0; i < pts.length - 1; i++) {
            Point2d p0i = pts[i];
            Point2d p1i = pts[i + 1];

            if (Edge2d.intersects(p0i, p1i, p0, p1)) {
                return true;
            }
        }
        return false;
    }


    public double getLengthToPoint(int n) {
        double length = 0.;

        if (points.length < 2) {
            return 0.;
        }
        for (int i = 0; i < n; i++) {
            length += points[i].distance(points[i+1]);
        }
        return length;
    }

    public double getLengthToPointProjection(Point2d po) {

        double length=0.;

        Vector2d distanceVector = distanceVector(po);
        Point2d projectionPoint = new Point2d(po.x-distanceVector.x,po.y-distanceVector.y);

        Edge2d edj = new Edge2d(null, null);
        boolean found=false;
        for (int j = 0; j < points.length-1 && !found; j++) {
            edj.p0 = points[j];
            edj.p1 = points[(j + 1) % points.length];
            if(edj.contains(projectionPoint)){
                length+=edj.getLength()*edj.getDirectionParam(projectionPoint);
                return length;
            } else
                length+=edj.getLength();
        }
        return length;
    }

    public int getIndexToPointProjection(Point2d po) {

        Vector2d distanceVector = distanceVector(po);
        Point2d projectionPoint = new Point2d(po.x-distanceVector.x,po.y-distanceVector.y);

        Edge2d edj = new Edge2d(null, null);
        boolean found=false;
        for (int j = 0; j < points.length-1 && !found; j++) {
            edj.p0 = points[j];
            edj.p1 = points[(j + 1) % points.length];
            if(edj.contains(projectionPoint)){
                return j;
            }
        }
        return -1;
    }
    
    public static double distance(Point2d[] pts, Point2d p) {
        if (PolygonalCurve2d.contains(pts, p)) {
            return 0.;
        }
        double d = Double.POSITIVE_INFINITY;

        for (int i = 0; i < pts.length - 1; i++) {
            Point2d p0i = pts[i];
            Point2d p1i = pts[i + 1];

            double di = Edge2d.distance(p0i, p1i, p);
            d = (di < d) ? di : d;
        }
        return d;
    }

    public static double distance(Point2d[] pts, Point2d p0, Point2d p1) {
        if (PolygonalCurve2d.intersects(pts, p0, p1)) {
            return 0.;
        }
        double d = Double.POSITIVE_INFINITY;

        for (int i = 0; i < pts.length - 1; i++) {
            Point2d p0i = pts[i];
            Point2d p1i = pts[i + 1];

            double di = Edge2d.distance(p0i, p1i, p0, p1);
            d = (di < d) ? di : d;
        }
        return d;
    }
}
