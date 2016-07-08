package bijava.math.function.interpolation;


import bijava.geometry.dim2.*;
import bijava.math.function.IntegrableScalarFunction2d;
import java.io.*;
import java.util.ArrayList;

/**
 * The class "TriangulatedScalarFunction2d" provides proverties and methods
 * for a discretized two dimensional scalar function.
 *
 *  <p>The dicretization will be represented by an array of sampling points.
 *  The sampling points define a finite interval. To describe an infinite
 *  discretized scalar function you can declare the value of the first
 *  sampling point as constant to the negative infinity
 *  (<code>setNegativeInifinity(true)</code>) and the value of the
 *  last sampling point as constant to the positive infinity
 *  (<code>setPositiveInifinity(true)</code>).</p>
 *
 * @author University of Hannover
 * @author Institute of Computer Science in Civil Engineering
 * @author Peter Milbradt
 * @author Martin Rose
 * @author Mario Hoecker
 * @version 1.0
 */
public class TriangulatedScalarFunction2d extends DiscretizedScalarFunction2d implements IntegrableScalarFunction2d {
    public Triangle2d[] triangles; // triangles
    private SimplePolygon2d[] bregions = null; // barycentric regions
    private SimplePolygon2d[] _confreg = null;  // confidence region
    private SimplePolygon2d confreg = null;
    
    protected TriangulatedScalarFunction2d() {
    }
    
    public TriangulatedScalarFunction2d(
            Point2d[] points,	// points = Stuetzstellen ...............
            double[] f      	// f[i] = Stuetzwerte ........................
            ) {
        super(points, f);
        this.samplingPoints = new Node[points.length];
        for(int i=0;i<points.length;i++){
            this.samplingPoints[i]=new Node(points[i],i);
        }
        triangulate();
    }
    
    public TriangulatedScalarFunction2d(Triangle2d[] triangles, Point2d[] points,  double[] f){
        super(points, f);
        this.triangles = triangles;
    }
    
    public TriangulatedScalarFunction2d(RasterScalarFunction2d f){
//        int col = f.getColumnSize();
//        int row = f.getRowSize();
        int col = f.getRowSize();
        int row = f.getColumnSize();
        size = col*row;
        samplingPoints = new Node[col*row];
        values = new double[col*row];
        
        for (int j=0; j < row; j++)
            for (int i=0; i < col; i++) {
            samplingPoints[i+col*j] = new Node(f.getSamplingPointAt(i,j), i+col*j);
            values[i+col*j] = f.getSamplingValueAt(i,j);
            }
        
        triangles = new Triangle2d[(col-1)*(row-1)*2];
        for (int j=0;j < row -1; j++)
            for (int i=0;i < col -1; i++) {
            try {
                triangles[i+(col-1)*j] = new Triangle2d(samplingPoints[i+col*j], samplingPoints[i+col*j+1], samplingPoints[i+col*(j+1)]);
                triangles[i+(col-1)*j+(col-1)*(row-1)] = new Triangle2d(samplingPoints[i+col*(j+1)+1], samplingPoints[i+col*(j+1)], samplingPoints[i+col*j+1]);
            } catch(Exception e) {}
            }
    }
    
    /** 
     * Gets the derivation of a point.
     */
    public Vector2d getGradient(Point2d p){
        Vector2d result=null;
        double dfdx=0., dfdy=0.;
        for(int i = 0;i< triangles.length;i++) {
            if(triangles[i].contains(p)){
                for(int j = 0; j < 3; j++) {
                    dfdx += values[((Node) triangles[i].getPointAt(j)).number] * triangles[i].koeffmat[j][1];
                    dfdy += values[((Node) triangles[i].getPointAt(j)).number] * triangles[i].koeffmat[j][2];
                }
                result=new Vector2d(dfdx,dfdy);
                i=triangles.length;
            }
        }
        return result;
    }
    
    public int getNumberOfTriangles(){
        return triangles.length;
    }
    
    public Point2d[] getTrianglePoints(int i){
        Point2d[] result = new Point2d[3];
        result[0]=triangles[i].getPointAt(0);
        result[1]=triangles[i].getPointAt(1);
        result[2]=triangles[i].getPointAt(2);
        return result;
    }
    
    public int[] getTrianglePointNumbers(int i){
        int[] result = new int[3];
        result[0]=((Node)triangles[i].getPointAt(0)).number;
        result[1]=((Node)triangles[i].getPointAt(1)).number;
        result[2]=((Node)triangles[i].getPointAt(2)).number;
        return result;
    }
    
    /** 
     * Computes the barycentric regions of this triangulated scalar function.
     */
    public SimplePolygon2d[] getBarycentricRegions() {
        if (bregions != null)
            return bregions;
//        System.out.println("Baryzentrische Regionen berechnen...");
        int psize = this.size,
                tsize = this.triangles.length;
        // identify neighbouring points
        ArrayList<Point2d>[] npts_unsort = new ArrayList[psize];
        for (int i = 0; i < psize; i++)
            npts_unsort[i] = new ArrayList<Point2d>();
        for (int i = 0; i < tsize; i++) {
            Point2d[] tpts = this.triangles[i].getPoints();
            for (int j = 0; j < 3; j++) {
                npts_unsort[((Node) tpts[j]).number].add(tpts[(j + 1) % 3]);
                npts_unsort[((Node) tpts[j]).number].add(tpts[(j + 2) % 3]);
            }
        }
        // ... sort
        ArrayList<Point2d>[] npts = new ArrayList[psize];
        for (int i = 0; i < psize; i++) {
            npts[i] = new ArrayList<Point2d>();
            int unsort_size = npts_unsort[i].size(),
                    sort_size = 0;
            while (unsort_size > 0) {
                if (sort_size == 0) {
                    npts[i].add(npts_unsort[i].remove(0));
                    npts[i].add(npts_unsort[i].remove(0));
                    unsort_size -= 2;
                    sort_size = 2;
                }
                boolean search = true;
                for (int j = 0; j < unsort_size && search; j += 2) {
                    if (npts[i].get(0).equals(npts_unsort[i].get(j + 1))) {
                        npts[i].add(0, npts_unsort[i].get(j));
                        sort_size++;
                        search = false;
                    } else if (npts[i].get(sort_size - 1).equals(npts_unsort[i].get(j))) {
                        npts[i].add(npts_unsort[i].get(j + 1));
                        sort_size++;
                        search = false;
                    }
                    if (!search) {
                        npts_unsort[i].remove(j);
                        npts_unsort[i].remove(j);
                        unsort_size -= 2;
                    }
                }
            }
        }
        npts_unsort = null;
        // create regions
        ArrayList<SimplePolygon2d> regions = new ArrayList<SimplePolygon2d>();
        ArrayList<Point2d> regpts = new ArrayList<Point2d>(),
                extpts = new ArrayList<Point2d>();
        for (int i = 0; i < psize; i++) {
            Point2d p = this.samplingPoints[i];
            int nsize = npts[i].size();
            // compute barycentric vertices
            for (int j = 0; j < nsize - 1; j++) {
                Point2d p1 = npts[i].get(j), p2 = npts[i].get(j + 1);
                regpts.add(new Point2d((p.x + p1.x + p2.x) / 3., (p.y + p1.y + p2.y) / 3.));
            }
            // close boundary regions
            Point2d p0 = npts[i].get(0), pn = npts[i].get(nsize - 1);
            if (!p0.equals(pn)) {
                regpts.add(new Point2d((pn.x + p.x) / 2., (pn.y + p.y) / 2.));
                regpts.add(p);
                regpts.add(new Point2d((p0.x + p.x) / 2., (p0.y + p.y) / 2.));
                // non-simple region
                Polygon2d temp = new Polygon2d(regpts.toArray(new Point2d[1]));
                if (!temp.isSimple()) {
                    for (int j = 1; j < nsize - 1; j++) {
                        Point2d pnj = npts[i].get(j);
                        regpts.add(2 * j - 1, new Point2d((pnj.x + p.x) / 2., (pnj.y + p.y) / 2.));
                        // save extra points
                        extpts.add(pnj); // region
                        extpts.add(regpts.get(2 * j - 2)); // reference vertice
                        extpts.add(regpts.get(2 * j - 1)); // extra point
                    }
//                    System.out.println("Region " + i + ": non-simple Polygon");
                }
            }
            // add region
            regions.add(new SimplePolygon2d(regpts.toArray(new Point2d[1])));
            regpts.clear();
        }
        // insert extra points
        int extsize = extpts.size();
        for (int i = 0; i < extsize; i += 3) {
            int number = ((Node) extpts.get(i)).number;
            Point2d[] regptsOLD = regions.get(number).getPoints();
            Point2d pref = extpts.get(i + 1);
            for (int j = 0; j < regptsOLD.length; j++) {
                if (regptsOLD[j].equals(pref))
                    regpts.add(extpts.get(i + 2));
                regpts.add(regptsOLD[j]);
            }
            regions.set(number, new SimplePolygon2d(regpts.toArray(new Point2d[1])));
            regpts.clear();
        }
//        System.out.println("Regionen berechnet");
        bregions = regions.toArray(new SimplePolygon2d[psize]);
        return bregions;
    }
    
    /** 
     * Gets the confidence region of this triangulated scalar function.
     * Outer boundarys are positive, interior boundarys (holes)
     * are negative orientated.
     */
    public SimplePolygon2d[] _getConfidenceRegion() {
        if (_confreg != null)
            return _confreg;
        int psize = this.size,
                tsize = this.triangles.length;
        // identify neighbouring points
        ArrayList<Point2d>[] npts_unsort = new ArrayList[psize];
        for (int i = 0; i < psize; i++)
            npts_unsort[i] = new ArrayList<Point2d>();
        for (int i = 0; i < tsize; i++) {
            Point2d[] tpts = this.triangles[i].getPoints();
            for (int j = 0; j < 3; j++) {
                npts_unsort[((Node) tpts[j]).number].add(tpts[(j + 1) % 3]);
                npts_unsort[((Node) tpts[j]).number].add(tpts[(j + 2) % 3]);
            }
        }
        // ... sort
        ArrayList<Point2d>[] npts = new ArrayList[psize];
        for (int i = 0; i < psize; i++) {
            npts[i] = new ArrayList<Point2d>();
            int unsort_size = npts_unsort[i].size(),
                    sort_size = 0;
            while (unsort_size > 0) {
                if (sort_size == 0) {
                    npts[i].add(npts_unsort[i].remove(0));
                    npts[i].add(npts_unsort[i].remove(0));
                    unsort_size -= 2;
                    sort_size = 2;
                }
                boolean search = true;
                for (int j = 0; j < unsort_size && search; j += 2) {
                    if (npts[i].get(0).equals(npts_unsort[i].get(j + 1))) {
                        npts[i].add(0, npts_unsort[i].get(j));
                        sort_size++;
                        search = false;
                    } else if (npts[i].get(sort_size - 1).equals(npts_unsort[i].get(j))) {
                        npts[i].add(npts_unsort[i].get(j + 1));
                        sort_size++;
                        search = false;
                    }
                    if (!search) {
                        npts_unsort[i].remove(j);
                        npts_unsort[i].remove(j);
                        unsort_size -= 2;
                    }
                }
            }
        }
        npts_unsort = null;
        // identify points of boundarys
        ArrayList<Point2d> bpts_unsort = new ArrayList<Point2d>();
        int bsize = 0;
        for (int i = 0; i < psize; i++) {
            boolean isBoundary = !npts[i].get(0).equals(npts[i].get(npts[i].size() - 1));
            if (isBoundary) {
                bpts_unsort.add(this.samplingPoints[i]);
                bpts_unsort.add(npts[i].get(0));
                bsize++;
            }
        }
        // create boundarys
        ArrayList<SimplePolygon2d> temp = new ArrayList<SimplePolygon2d>();
        ArrayList<Point2d> bpts = new ArrayList<Point2d>();
        int unsort_size = bsize * 2,
                sort_size = 0;
        while (unsort_size > 0) {
            if (sort_size == 0) {
                bpts.add(bpts_unsort.remove(0));
                bpts.add(bpts_unsort.remove(0));
                unsort_size -= 2;
                sort_size = 2;
            }
            boolean search = true;
            for (int i = 0; i < unsort_size && search; i += 2) {
                if (bpts.get(0).equals(bpts_unsort.get(i + 1))) {
                    // polygon closed
                    if (bpts.get(sort_size - 1).equals(bpts_unsort.get(i))) {
                        temp.add(new SimplePolygon2d(bpts.toArray(new Point2d[sort_size])));
                        bpts.clear();
                        sort_size = 0;
                    } else {
                        bpts.add(0, bpts_unsort.get(i));
                        sort_size++;
                    }
                    search = false;
                } else if (bpts.get(sort_size - 1).equals(bpts_unsort.get(i))) {
                    bpts.add(bpts_unsort.get(i + 1));
                    sort_size++;
                    search = false;
                }
                if (!search) {
                    bpts_unsort.remove(i);
                    bpts_unsort.remove(i);
                    unsort_size -= 2;
                }
            }
        }
        bpts_unsort = bpts = null;
        _confreg = temp.toArray(new SimplePolygon2d[1]);
        return _confreg;
    }
    
    /** 
     * Gets the confidence region of this triangulated scalar function.
     */
    public SimplePolygon2d getConfidenceRegion() {
        if (confreg != null)
            return confreg;
        if (_confreg == null)
            this._getConfidenceRegion();
        for (int i = 0; i < _confreg.length; i++)
            if (_confreg[i].getOrientation() == 1) {
            confreg = _confreg[i];
            return confreg;
            }
        return null;
    }
    
    /** 
     * Tests if the confidence region contains a point.
     */
    public boolean inConfidenceRegion(Point2d p) {
//        for (int i = 0; i < triangles.length; i++)
//            if (triangles[i].contains(p)) return true;
//        return false;
        if (_confreg == null)
            this._getConfidenceRegion();
        boolean contains = false;
        for (int i = 0; i < _confreg.length; i++) {
            if (_confreg[i].contains(p)) {
                if (_confreg[i].getOrientation() == -1)
                    return false;
                else contains = true;
            }
        }
        return contains;
    }
    
    /** 
     * Gets the value of a point.
     */
    public double getValue(Point2d p){
        double result=Double.NaN;
        double f0,f1,f2;
        for(int i = 0;i< triangles.length;i++) {
            double xmin = Math.min(triangles[i].getPointAt(0).x, Math.min(triangles[i].getPointAt(1).x, triangles[i].getPointAt(2).x)),
                    ymin = Math.min(triangles[i].getPointAt(0).y, Math.min(triangles[i].getPointAt(1).y, triangles[i].getPointAt(2).y)),
                    xmax = Math.max(triangles[i].getPointAt(0).x, Math.max(triangles[i].getPointAt(1).x, triangles[i].getPointAt(2).x)),
                    ymax = Math.max(triangles[i].getPointAt(0).y, Math.max(triangles[i].getPointAt(1).y, triangles[i].getPointAt(2).y));
            
            if (p.x < xmin || p.x > xmax) continue;
            if (p.y < ymin || p.y > ymax) continue;
            // Bis jetzt liegt Punkt innerhalb des das Element umschliessenode
            // Rechtecks XMIN/XMAX, YMIN/YMAX
            
            // Pruefen, ob Punkt wirklich innerhalb DREIECK-Element liegt:
            // BERECHNUNG DER TEILFLAECHEN (ohne 0.5)
            
            f0 =   triangles[i].getPointAt(1).x * (triangles[i].getPointAt(2).y - p.y)
            + triangles[i].getPointAt(2).x * (p.y - triangles[i].getPointAt(1).y)
            + p.x * (triangles[i].getPointAt(1).y - triangles[i].getPointAt(2).y);
            if (f0 <= -0.0001) continue;

            f1 =   triangles[i].getPointAt(2).x * (triangles[i].getPointAt(0).y - p.y)
            + triangles[i].getPointAt(0).x * (p.y - triangles[i].getPointAt(2).y)
            + p.x * (triangles[i].getPointAt(2).y - triangles[i].getPointAt(0).y);
            if (f1 <= -0.0001) continue;

            f2 =   triangles[i].getPointAt(0).x * (triangles[i].getPointAt(1).y - p.y)
            + triangles[i].getPointAt(1).x * (p.y - triangles[i].getPointAt(0).y)
            + p.x * (triangles[i].getPointAt(0).y - triangles[i].getPointAt(1).y);
            if (f2 <= -0.0001) continue;

            double[] natCoord = triangles[i].getNaturalElementCoordinates(p);
            if(natCoord == null) continue;
            
            result= ( f0*values[((Node) triangles[i].getPointAt(0)).number]
                    +f1*values[((Node) triangles[i].getPointAt(1)).number]
                    +f2*values[((Node) triangles[i].getPointAt(2)).number] ) / triangles[i].getArea() /2.;

//            result= ( natCoord[0]*values[((Node) triangles[i].getPointAt(0)).number]
//                    +natCoord[1]*values[((Node) triangles[i].getPointAt(1)).number]
//                    +natCoord[2]*values[((Node) triangles[i].getPointAt(2)).number] );
            break;
        }
        return result;
    }

  
    
    /** 
     * Gets the confidence value of a point.
     */
    synchronized public double getConfidenceValue(Point2d p) {
        double result=0.0;
        double f0,f1,f2;
        for(int i = 0;i< triangles.length;i++) {
            double xmin = Math.min(triangles[i].getPointAt(0).x, Math.min(triangles[i].getPointAt(1).x, triangles[i].getPointAt(2).x)),
                    ymin = Math.min(triangles[i].getPointAt(0).y, Math.min(triangles[i].getPointAt(1).y, triangles[i].getPointAt(2).y)),
                    xmax = Math.max(triangles[i].getPointAt(0).x, Math.max(triangles[i].getPointAt(1).x, triangles[i].getPointAt(2).x)),
                    ymax = Math.max(triangles[i].getPointAt(0).y, Math.max(triangles[i].getPointAt(1).y, triangles[i].getPointAt(2).y));
            
            if (p.x < xmin || p.x > xmax) continue;
            if (p.y < ymin || p.y > ymax) continue;
            // Bis jetzt liegt Punkt innerhalb des das Element umschliessenode
            // Rechtecks XMIN/XMAX, YMIN/YMAX
            
            // Pruefen, ob Punkt wirklich innerhalb DREIECK-Element liegt:
            // BERECHNUNG DER TEILFLAECHEN (ohne 0.5)
            
            f0 =   triangles[i].getPointAt(1).x * (triangles[i].getPointAt(2).y - p.y)
            + triangles[i].getPointAt(2).x * (p.y - triangles[i].getPointAt(1).y)
            + p.x * (triangles[i].getPointAt(1).y - triangles[i].getPointAt(2).y);
            if (f0 <= -0.0001) continue;
            
            f1 =   triangles[i].getPointAt(2).x * (triangles[i].getPointAt(0).y - p.y)
            + triangles[i].getPointAt(0).x * (p.y - triangles[i].getPointAt(2).y)
            + p.x * (triangles[i].getPointAt(2).y - triangles[i].getPointAt(0).y);
            if (f1 <= -0.0001) continue;
            
            f2 =   triangles[i].getPointAt(0).x * (triangles[i].getPointAt(1).y - p.y)
            + triangles[i].getPointAt(1).x * (p.y - triangles[i].getPointAt(0).y)
            + p.x * (triangles[i].getPointAt(0).y - triangles[i].getPointAt(1).y);
            if (f2 <= -0.0001) continue;
            
            result= 3./2. * (Math.max(f0, Math.max(f1, f2)) / triangles[i].getArea()/2. - 1./3.);
//            result= Math.max(f0, Math.max(f1,f2))/ triangles[i].getArea() /2. ;
            break;
        }
        return result;
    }
    
    /* Triangulation */
    private void triangulate() {
        Triangulation2d triang = new Triangulation2d(samplingPoints);
        this.triangles = triang.getTriangles();
        
//        ArrayList<Triangle2d> triangles = new ArrayList<Triangle2d>();
//        for (int i = 0; i < samplingPoints.length - 2; i++) {
//            for (int j = i + 1; j < samplingPoints.length - 1; j++) {
//                for (int k = j + 1; k < samplingPoints.length; k++) {
//                    try {
//                        Triangle2d t = new Triangle2d(samplingPoints[i], samplingPoints[j], samplingPoints[k]);
//                        boolean leer = true;
//                        for (int m = 0; (m < samplingPoints.length)&&leer ; m++)
//                            leer = !t.inDelaunayCircle(samplingPoints[m]);
//                        
//                        if (leer)
//                            triangles.add(t);
//                    } catch (Exception e) {}
//                }
//            }
//        }
//        this.triangles = triangles.toArray(new Triangle2d[triangles.size()]);
//        System.out.println(triangles.size() + " Elemente erzeugt");
    }
    
    public static TriangulatedScalarFunction2d readfromSysDat(String nam) {
        TriangulatedScalarFunction2d tf = new TriangulatedScalarFunction2d();
        int anzr = 0;
        int anzk = 0;
        int anze = 0;
        int pnr;
        
        InputStream is = null;
        try {
            is = new FileInputStream(nam);
        } catch (Exception e) {};
        
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        
        StreamTokenizer st = new StreamTokenizer(r);
        st.eolIsSignificant(true);
        st.commentChar('C');
        
        // read Point3ds
        anzr = NextInt(st);
        anzk = anzr + NextInt(st);
        tf.size = anzk;
        tf.samplingPoints = new Node[anzk];
        tf.values = new double[anzk];
        tf.bb2d = new BoundingBox2d(0,0,0,0);
        
        for(int j = 0;j< anzk;j++) {
            int pos = NextInt(st);
            tf.samplingPoints[pos] = new Node(NextDouble(st), NextDouble(st), pos);
            tf.values[pos] = NextDouble(st);
        }
        
        // read Elements
        anze =  NextInt(st);
        tf.triangles = new Triangle2d[anze];
        for(int j = 0; j < anze; j++) {
            try {
                tf.triangles[j] = new Triangle2d(tf.samplingPoints[NextInt(st)], tf.samplingPoints[NextInt(st)], tf.samplingPoints[NextInt(st)]);
            } catch (Exception e) {}
            NextInt(st);  // Elementkennung
            NextInt(st);  // Elementnumber
        }
//        System.out.println(anze + " Elemente gelesen");

        //  Min- und Max- Value setzen und die Methode hasNaN überprüfen
        //  BoundingBox2d setzen



        tf.maxValue = Double.NEGATIVE_INFINITY;
        tf.minValue = Double.POSITIVE_INFINITY;

        double maxXpoint = Double.NEGATIVE_INFINITY, minXpoint = Double.POSITIVE_INFINITY;
        double maxYpoint = Double.NEGATIVE_INFINITY, minYpoint = Double.POSITIVE_INFINITY;

        for (int i = 0; i < tf.size; i++) {
            if (!tf.hasNaN && Double.isNaN(tf.values[i]))
                tf.hasNaN = true;

            if (!Double.isNaN(tf.values[i])) {
                tf.maxValue = Math.max(tf.maxValue, tf.values[i]);
                tf.minValue = Math.min(tf.minValue, tf.values[i]);
            }
            if(!Double.isNaN(tf.samplingPoints[i].x)){
                maxXpoint = Math.max(maxXpoint, tf.samplingPoints[i].x);
                minXpoint = Math.min(minXpoint, tf.samplingPoints[i].x);
            }
            if(!Double.isNaN(tf.samplingPoints[i].y)){
                maxYpoint = Math.max(maxYpoint, tf.samplingPoints[i].y);
                minYpoint = Math.min(minYpoint, tf.samplingPoints[i].y);
            }
            
        }

        tf.bb2d.setXmax(maxXpoint);
        tf.bb2d.setXmin(minXpoint);
        tf.bb2d.setYmax(maxYpoint);
        tf.bb2d.setYmin(minYpoint);

        return tf;
    }
    
    public void writeSysDat(String filename) {
        try {
            //System.dat schreiben
            FileWriter writer = new FileWriter(filename);
            BufferedWriter bwriter = new BufferedWriter(writer);
            PrintWriter fileWriterOut = new PrintWriter(bwriter);

            //Header schreiben
            fileWriterOut.println("C Erzeugt von TriangulatedScalarFunction");
            fileWriterOut.println("C Anzahl der Randknoten:");
            fileWriterOut.println("\t" + 1);
            fileWriterOut.println("C Anzahl der Gebietsknoten:");
            int anzk = getSizeOfValues();
            fileWriterOut.println("\t" + anzk);
            fileWriterOut.println("C Koordinaten und Skalarwerte der Knoten");
            fileWriterOut.println("C --------------------------------------");
            fileWriterOut.println("C Zuerst die Randknoten  (Anzahl s.o.),");
            fileWriterOut.println("C dann die Gebietsknoten (Anzahl s.o.).");
            fileWriterOut.println("C ------------+-------------+-------------+---------------");
            fileWriterOut.println("C     Nr.     |  x-Koord.   |   y-Koord.  |   Skalarwert");
            fileWriterOut.println("C ------------+-------------+-------------+---------------");
            for (int j = 0; j < anzk; j++) {
                int pos  = j;
                double x = samplingPoints[j].x;
                double y = samplingPoints[j].y;
                double z = super.values[j];
                fileWriterOut.println("\t" + pos + "\t" + (float) x + "\t" + (float) y + "\t" + (float) z);
            }
            int anzElemente = getNumberOfTriangles();
            fileWriterOut.println("C --------------------------+-------------+---------------");
            fileWriterOut.println("C Anzahl der Elemente");
            fileWriterOut.println("\t" + anzElemente);
            fileWriterOut.println("C Elementverzeichnis");
            fileWriterOut.println("C --------------------------+-------------+---------------");
            fileWriterOut.println("C Knoten i  Knoten j  Knoten k	 Kennung	Nr");
            for (int i = 0; i < anzElemente; i++) {
                int [] p = getTrianglePointNumbers(i);
                fileWriterOut.println("\t" + p[0] + "\t" + p[1] + "\t" + p[0] + "\t" + 0 + "\t" + i);
            }
            fileWriterOut.flush();
            fileWriterOut.close();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static int NextInt(StreamTokenizer st) {
        int wert = 0;
        try {
            while (st.nextToken() != StreamTokenizer.TT_NUMBER);
            wert = (int) st.nval;
        } catch (Exception e) {};
        return wert;
    }
    
    private static double NextDouble(StreamTokenizer st) {
        double wert = 0.0;
        try {
            while (st.nextToken() != StreamTokenizer.TT_NUMBER);
            wert = (double) st.nval;
        } catch (Exception e) {};
        return wert;
    }
    
    /** 
     * Computes the integral of this triangulated scalar function over the
     * region of a simple polygon.
     */
    public double getIntegral(SimplePolygon2d polygon) {
        if (polygon == null) return Double.NaN;
        double integral = 0.0;
        for (int i = 0; i < triangles.length; i++) {
            SimplePolygon2d[] section = polygon.section(triangles[i]);
            for (int j = 0; j < section.length; j++) {
                Triangle2d[] triangulation = section[j].getTriangulation();
                for (int k = 0; k < triangulation.length; k++) {
                    double[] f = {this.getValue(triangulation[k].getPointAt(0)),
                    this.getValue(triangulation[k].getPointAt(1)),
                    this.getValue(triangulation[k].getPointAt(2))};
                    integral += triangulation[k].getArea() * (f[0] + f[1] + f[2]) / 3.;
                }
            }
        }
        return integral;
    }
    
    /**
     * Interpolates the function values of a triangulated scalar function.
     */
    public TriangulatedScalarFunction2d interpolate(TriangulatedScalarFunction2d tf) {
        for (int i = 0; i < tf.size; i++)
            tf.values[i] = this.getValue(tf.samplingPoints[i]);
        return tf;
    }
    
    /** 
     * Interpolates the function values of a triangulated scalar function
     * by considering the volume.
     */
    public TriangulatedScalarFunction2d interpolateVolumentreu(TriangulatedScalarFunction2d tf) {
        if (tf.bregions == null)
            tf.getBarycentricRegions();
        for (int i = 0; i < tf.size; i++)
            tf.values[i] = this.getIntegral(tf.bregions[i]) / tf.bregions[i].getArea();
        return tf;
    }
    
    /**
     * Computes a barycentric triangulated scalar function of a
     * raster scalar function.
     */
    public static TriangulatedScalarFunction2d getBarycentricTriangulatedScalarFunction2d(RasterScalarFunction2d rf) {
        int lx = rf.f.length, ly = rf.f[0].length;
        // Knoten und Funktionswerte der Triangulation erzeugen
        int rpts_size = lx * ly, rele_size = (lx - 1) * (ly - 1); // Anzahl Knoten, Elemente des Rasters
        int tpts_size = rpts_size + rele_size;
        Node[] tpts = new Node[tpts_size];
        double[] tvals = new double[tpts_size];
        // Rasterknoten und Funktionswerte zuerst zeilenweise speichern
        int index = 0;
        for (int j = 0; j < ly; j++) {
            double y = rf.ymin + j * rf.dy;
            for (int i = 0; i < lx; i++) {
                double x = rf.xmin + i * rf.dx;
                tpts[index] = new Node(x, y, index);
                tvals[index] = rf.f[i][j];
                index++;
            }
        }
        // Schwerpunkte der Rasterelemente und Funktionswerte dahinter zeilenweise speichern
        for (int j = 0; j < ly - 1; j++) {
            double y = rf.ymin + j * rf.dy + (rf.dy / 2.);
            for (int i = 0; i < lx - 1; i++) {
                double x = rf.xmin + i * rf.dx + (rf.dx / 2.);
                tpts[index] = new Node(x, y, index);
                tvals[index] = (rf.f[i][j] + rf.f[i + 1][j] + rf.f[i + 1][j + 1] + rf.f[i][j + 1]) * 0.25;
                index++;
            }
        }
        // Elemente der Triangulation erzeugen
        Triangle2d[] teles = new Triangle2d[4 * rele_size];
        index = 0;
        for (int j = 0; j < ly - 1; j++) {
            for (int i = 0; i < lx - 1; i++) {
                Point2d p0 = tpts[index + j];
                Point2d p1 = tpts[index + j + 1];
                Point2d p2 = tpts[index + j + lx + 1];
                Point2d p3 = tpts[index + j + lx];
                Point2d sp = tpts[rpts_size + index];
                teles[index] = new Triangle2d(p0, p1, sp);
                teles[rele_size + index] = new Triangle2d(p1, p2, sp);
                teles[2 * rele_size + index] = new Triangle2d(p2, p3, sp);
                teles[3 * rele_size + index] = new Triangle2d(p3, p0, sp);
                index++;
            }
        }
        TriangulatedScalarFunction2d tf = new TriangulatedScalarFunction2d(teles, tpts, tvals);
        return tf;
    }
    
    public static void main( String args[] ) {
        TriangulatedScalarFunction2d tf;
        Point2d[] points = new Point2d[]{
            new Point2d( 3455114.8072 , 5962276.9591 )
            ,new Point2d( 3464113 , 5968643.5 )
            ,new Point2d( 3464438.1211 , 5969194.5791 )
            ,new Point2d( 3461041.3213 , 5979445.5312 )
            ,new Point2d( 3464944.75 , 5958811.5 )
            ,new Point2d( 3458096.25 , 5965251.5 )
            ,new Point2d( 3461728.2788 , 5979313.2618 )
            ,new Point2d( 3464247.75 , 5958992 )
            ,new Point2d( 3450742.75 , 5969042 )
            ,new Point2d( 3462389.6259 , 5979101.6307 )
            ,new Point2d( 3465643.4539 , 5958917.3155 )
            ,new Point2d( 3450141.4766 , 5969049.1539 )
            ,new Point2d( 3458945.1947 , 5979221.6909 )
            ,new Point2d( 3463024.5192 , 5978784.184 )
            ,new Point2d( 3451790.5 , 5966628 )
            ,new Point2d( 3453548.75 , 5964809.5 )
            ,new Point2d( 3454422.835 , 5963606.5532 )
            ,new Point2d( 3455406.75 , 5962464 )
            ,new Point2d( 3456305.2321 , 5960795.5415 )
            ,new Point2d( 3457887.0392 , 5978923.2368 )
            ,new Point2d( 3463499.75 , 5962161 )
            ,new Point2d( 3455906.3893 , 5978217.7998 )
            ,new Point2d( 3454687.25 , 5977437 )
            ,new Point2d( 3463791.6819 , 5977514.3975 )
            ,new Point2d( 3464178 , 5976727 )
            ,new Point2d( 3465431.8228 , 5977276.3125 )
            ,new Point2d( 3466093.17 , 5959737.386 )
            ,new Point2d( 3466701.6094 , 5976271.0649 )
            ,new Point2d( 3467257.141 , 5959737.386 )
            ,new Point2d( 3465749.2695 , 5975477.4483 )
        };
        
        double[] fct = new double[]{
            18.3627 ,
            15.6037 ,
            15.943 ,
            20.6853 ,
            18.9708 ,
            14.8311 ,
            20.7177 ,
            17.4217 ,
            17.4941 ,
            20.7557 ,
            19.6161 ,
            17.0634 ,
            19.3935 ,
            21.028 ,
            11.8905 ,
            18.6667 ,
            16.8184 ,
            18.6269 ,
            19.1325 ,
            19.2081 ,
            20.0379 ,
            16.9894 ,
            13.9721 ,
            19.9022 ,
            19.2831 ,
            19.7986 ,
            18.6061 ,
            19.9849 ,
            18.4993 ,
            19.5742
        };
        
        tf = new TriangulatedScalarFunction2d(points, fct);
        System.out.println(tf.getValue(new Point2d( 3465431.8228 , 5977276.3125 )));

        System.out.println("fertig");
    }
}

class Node extends Point2d{
    int number;
    Node(double x, double y, int i){
        super(x,y);
        number = i;
    }
    Node(Point2d p, int i){
        super(p);
        number = i;
    }
}