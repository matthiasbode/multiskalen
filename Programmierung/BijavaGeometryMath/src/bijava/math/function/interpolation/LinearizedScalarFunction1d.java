package bijava.math.function.interpolation;


import bijava.math.function.*;
//==========================================================================//
/** The class "LinearizedScalarFunction1d" provides properties and methods
 *  for a discretized one dimensional scalar functions.
 *
 *  <p>The dicretization will be represented by an array of sampling points.
 *  The sampling points define a finite interval. To describe an infinite
 *  discretized scalar function you can declare the value of the first
 *  sampling point as constant to the negative infinity
 *  (<code>setNegativeInifinity(true)</code>) and the value of the
 *  last sampling point as constant to the positive infinity
 *  (<code>setPositiveInifinity(true)</code>).</p>
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, Juni 2006</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class LinearizedScalarFunction1d extends DiscretizedDifferentialScalarFunction1d implements DifferentialScalarFunction1d, IntegrableScalarFunction1d {
    private boolean positiveInfinite;     // flag: function is positive infinite
    private boolean negativeInfinite;     // flag: function is negative infinite
    private int     actPosition = 0;
    
//--------------------------------------------------------------------------//
    /** Creates a finite one dimensional scalar function linearized by n
     *  equidistant sampling points. The value of all arguments is 0.
     *
     *  @param n    If this value is less than 2 the number of sampling points
     *              is 2.
     *  @param xmin If this value is less than <code>xmax</code> the minimum of
     *              the definition range is <code>xmin</code>,
     *              otherwise it is <code>xmax</code>.
     *  @param xmax If this value is greater than <code>xmin</code> the maximum of
     *              the definition range is <code>xmin</code>,
     *              otherwise it is <code>xmin</code>.                          */
//--------------------------------------------------------------------------//
    public LinearizedScalarFunction1d(int n, double xmin, double xmax) {
        super(n, xmin, xmax); positiveInfinite = negativeInfinite = false; }
    
//--------------------------------------------------------------------------//
    /** Creates a finite one dimensional scalar function linearized by n
     *  sampling points. The value of all arguments is 0.
     *
     *  @param arguments This array containing the arguments of the sampling
     *                   points will be copied.                                 */
//--------------------------------------------------------------------------//
    public LinearizedScalarFunction1d(double[] arguments) {
        super(arguments); positiveInfinite = negativeInfinite = false; }
    
//--------------------------------------------------------------------------//
    /** Creates a finite one dimensional scalar function linearized by n
     *  sampling points.
     *
     *  @param values This array containing the arguments and the values of the
     *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
    public LinearizedScalarFunction1d(double[][] values) {
        super(values); positiveInfinite = negativeInfinite = false; }
    
    
   //--------------------------------------------------------------------------//
    public LinearizedScalarFunction1d(double[] coord, double[] values) {
        super(coord, values); positiveInfinite = negativeInfinite = false; }
    
    
//--------------------------------------------------------------------------//
    /** Tests the equality to another object.
     *
     *  @param object If this value is a <code>LinearizedScalarFunction1d</code>
     *                with the same definition range and the same function values
     *                in the epsilon range of the linearized scalar function the
     *                method returns <code>true</code>,
     *                otherwise <code>false</code>.                             */
//--------------------------------------------------------------------------//
    synchronized public boolean equals(Object object) {
        if (!(object instanceof LinearizedScalarFunction1d))         return false;
        LinearizedScalarFunction1d f = (LinearizedScalarFunction1d) object;
        
        if (negativeInfinite != f.negativeInfinite)                  return false;
        if (positiveInfinite != f.positiveInfinite)                  return false;
        if (!negativeInfinite &&
                Math.abs(values[0][0] - f.values[0][0]) > getEpsilon())  return false;
        if (!positiveInfinite &&
                Math.abs(values[0][this.size-1] - f.values[0][f.size-1]) > getEpsilon())
            return false;
        for (int i = 0; i < this.size; i++)
            if (Math.abs(values[1][i] - f.getValue(values[0][i])) > getEpsilon())
                return false;
        for (int i = 0; i < f.size; i++)
            if (Math.abs(f.values[1][i] - getValue(f.values[0][i])) > getEpsilon())
                return false;
        return true;
    }
    
//--------------------------------------------------------------------------//
    /** Gets the value of an argument x.
     *
     *  @param x If the argument isn't in the definition range the methode returns
     *           <code>Double.NaN</code>                                    */
//--------------------------------------------------------------------------//
    synchronized public double getValue(double x) {
        if (x < values[0][0])
            return negativeInfinite ? values[1][0] : Double.NaN;
        if (x > values[0][size-1])
            return positiveInfinite ? values[1][size-1] : Double.NaN;
        
        // Sequentielle Suche, falls x > x von letzgesuchtem Index
        if (actPosition < size-1) {
            if (x > getValueAt(actPosition)[0] && x < getValueAt(actPosition+1)[0]) {
                double x1 = values[0][actPosition];   double y1 = values[1][actPosition];
                double x2 = values[0][actPosition+1]; double y2 = values[1][actPosition+1];
                return (x1 != x2) ? y1 + ((x - x1) / (x2 - x1)) * (y2 - y1) : y1;
            }
        }

        // Sonst binaere Suche aus java.util.Arrays
        // geändert Christoph: java.util.Arrays-Methoden ausnutzen
	int insertion_point=java.util.Arrays.binarySearch(values[0],x);
	if (insertion_point>=0) {
            actPosition = insertion_point;
            return values[1][insertion_point];
        }
	else {
            actPosition = (-1)*insertion_point-2;
            double x1 = values[0][actPosition];   double y1 = values[1][actPosition];
            double x2 = values[0][actPosition+1]; double y2 = values[1][actPosition+1];
            return (y1 + ((x-x1)/(x2-x1))*(y2-y1));
	}
    }

 
    
    // wenn das Intervall bekannt ist : z.B. in den Methoden add und sub
    protected double getValue(int i, double x){
        double x1 = values[0][i];   double y1 = values[1][i];
        double x2 = values[0][i+1]; double y2 = values[1][i+1];
        return (x1 != x2) ? y1 + ((x - x1) / (x2 - x1)) * (y2 - y1) : y1;
    }
    
//--------------------------------------------------------------------------//
    /** Gets the derivation of an argument x.
     *
     *  @param x If the argument isn't in the definition range the methode returns
     *           <code>Double.NaN</code>                                        */
//--------------------------------------------------------------------------//
    public double getGradient(double x) {
        if (x < values[0][0])
            return negativeInfinite ? values[1][0] : Double.NaN;
        if (x > values[0][size-1])
            return positiveInfinite ? values[1][size-1] : Double.NaN;
        
        int pos = size;
        for (int i=0; i<size; i++) if (values[0][i] > x) { pos = i-1; i = size; }
        
        if (pos < size) {
            double x1 = values[0][pos]; double y1 = values[1][pos];
            double x2 = values[0][pos]; double y2 = values[1][pos];
            
            for (int j = pos; j < size; j++)
                if (values[0][j]>x) { x2 = values[0][j]; y2 = values[1][j];j = size; }
            
            return (x1 != x2) ? (y2-y1) / (x2-x1) : (((y2-y1) > 0.) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
        }
        return Double.NaN;
    }
    
    /**Gibt die Ableitung als Funktion zurueck
     *
     * kopiert aus AbstractDifferentialFunction1d.
     * Bessere Loesung gesucht!
     */
    public AbstractScalarFunction1d getDerivation() {
        return new AbstractScalarFunction1d() {
            public double getValue(double p) {
                return getGradient(p);
            }
        };
    }

    public LinearizedScalarFunction1d getLinearizedDerivation() {
        double[][] grad = new double[2][2 * values[0].length - 1];
        for (int i = 0; i < size; i++) {
            if (i < size - 1) {
                grad[0][i * 2 + 1] = (values[0][i + 1] + values[0][i]) / 2.;
                grad[1][i * 2 + 1] = getGradient(grad[0][i * 2 + 1]);
            }
            grad[0][i * 2] = values[0][i];
            if ((i > 0) && (i < size - 1)) {
                grad[1][i * 2] = (grad[1][i * 2 - 1] + grad[1][i * 2 + 1]) / 2.;
            } else {
                if (i == 0) {
                    grad[1][i * 2] = (grad[1][i * 2 + 1]) / 2.;
                }
                if (i == size - 1) {
                    grad[1][i * 2] = (grad[1][i * 2 - 1]) / 2.;
                }
            }
        }
        return new LinearizedScalarFunction1d(grad);
    }
    
//--------------------------------------------------------------------------//
    /** Gets the minimaum of the definition range int the discretized scalar
     *  function.                                                               */
//--------------------------------------------------------------------------//
    public double getXMin() {
        return (negativeInfinite) ? Double.NEGATIVE_INFINITY : values[0][0]; }
    
//--------------------------------------------------------------------------//
    /** Gets the maximaum of the definition range int the discretized scalar
     *  function.                                                               */
//--------------------------------------------------------------------------//
    public double getXMax() {
        return (positiveInfinite) ? Double.POSITIVE_INFINITY
                : values[0][size-1];
    }
    
//--------------------------------------------------------------------------//
    /** Gets a smooth form of this dicretized scalar function.
     *
     *  @param n The Method calculate a mean value with the method
     *           <code>getSmoothValue(i, n)</code>.                             */
//--------------------------------------------------------------------------//
    public LinearizedScalarFunction1d getSmoothFunction(int n) {
        double[][] w = new double[2][size]; w[0] = values[0];
        for (int i = 0; i < size; i++) w[1][i] = getSmoothValue(i, n);
        return new LinearizedScalarFunction1d(w);
    }
    
//--------------------------------------------------------------------------//
    /** Sets a the positive infinity of this scalar function.
     *
     *  @param infinite If this variable is <code>true</code> the value of the
     *                  last sampling point is the constant value of all greater
     *                  arguments. If this variable is <code>false</code> the
     *                  value of all greater arguments than the last sampling
     *                  point is <code>NaN</code>.                              */
//--------------------------------------------------------------------------//
    public void setPositiveInifinity(boolean infinite) {
        positiveInfinite = infinite;
    }
    
//--------------------------------------------------------------------------//
    /** Tests if this scalar function is positive infinite.                     */
//--------------------------------------------------------------------------//
    public boolean isPositiveInifinite() { return positiveInfinite; }
    
//--------------------------------------------------------------------------//
    /** Sets a the negative infinity of this scalar function.
     *
     *  @param infinite If this variable is <code>true</code> the value of the
     *                  first sampling point is the constant value of all less
     *                  arguments. If this variable is <code>false</code> the
     *                  value of all less arguments than the first sampling
     *                  point is <code>NaN</code>.                              */
//--------------------------------------------------------------------------//
    public void setNegativeInifinity(boolean infinite) {
        negativeInfinite = infinite;
    }
    
//--------------------------------------------------------------------------//
    /** Tests if this scalar function is negative infinite.                     */
//--------------------------------------------------------------------------//
    public boolean isNegativeInifinite() { return negativeInfinite; }
    
    
    /** berechnet das bestimmte Integral von xa bis xb */
    public double getIntegral(double xa, double xb){
        /* Sonderfallbehandlung */
        int ia = sucheStreckenSegment_binaer(xa);
        int ib = sucheStreckenSegment_binaer(xb);
        if(ia==-1 || ib==-1) return Double.NaN;
        
        double summe=(values[0][ia+1]-xa)*(values[1][ia+1]-getValue(xa));
        
        for (int j=ia+1; j<ib+1; j++) summe+=(values[0][j+1]-values[0][j])*(values[1][j+1]-values[1][j]);
        
        summe+=(values[0][ib+1]-xb)*(values[1][ib+1]-getValue(xb));
        
        return summe;
    }
    
    public int sucheStreckenSegment_binaer(double x0){
        if (size == 0 || x0 < values[0][0] || x0 > values[0][size-1]) return -1; /* ausserhalb */
        int i, j, m;
        for (i=0, j=size-1, m=(i+j)/2; i<m; m=(i+j)/2)
            if (x0<=values[0][m]) j=m;
            else          i=m;
        return i;
    }
    
//    double x1 = values[0][actPosition];   double y1 = values[1][actPosition];
    
    @SuppressWarnings("empty-statement")
    public LinearizedScalarFunction1d sub(LinearizedScalarFunction1d q){
        int m, n, k, M, N, K, mmin, nmin;
        double xmin, xmax;
        
        //..Bestimme den gemeinsamen x-Bereich beider Polygonzuege
        M = this.size;
        N = q.size;
        if (this.values[0][0] >= q.values[0][0]) xmin = this.values[0][0]; else xmin = q.values[0][0];
        if (this.values[0][M-1] <= q.values[0][N-1]) xmax = this.values[0][M-1]; else xmax = q.values[0][N-1];
        if (xmax < xmin) return null;
        
        //..Uebergehen der Stuetzstellen von p und q mit x < xmin
        for (mmin=0; values[0][mmin] < xmin; mmin++);
        for (nmin=0; q.values[0][nmin] < xmin; nmin++);
        
        //..Bestimme Anzahl Stuetzstellen der Verschmelzung
        //  von p und q
        for (m=mmin, n=nmin, K=0; m<M && n<N; K++)
            if      (values[0][m] < q.values[0][n]) m++;
            else if (values[0][m] > q.values[0][n]) n++;
            else { m++;  n++;}
        
        //..Neuen Polygonzug mit K Punkten erzeugen
        LinearizedScalarFunction1d r = new LinearizedScalarFunction1d(K, xmin, xmax);
        
        //..Bestimme Polygonzug r durch Verschmelzung
        for (m=mmin, n=nmin, k=0; k<K; k++)
            
            //......Stuetzstelle des Polygonzuges this
            if (values[0][m] < q.values[0][n])
            {   r.values[0][k] = values[0][m];
                r.values[1][k] = values[1][m] - q.getValue(this.values[0][m]);
                m++ ;
            }
        //......Stuetzstelle des Polygonzuges q
            else if (q.values[0][n] < values[0][m])
            {   r.values[0][k] = q.values[0][n];
                r.values[1][k] = this.getValue(q.values[0][n]) - q.values[1][n];
                n++ ;
            }
        //......Gleiche Stuetzstelle der Polygonzuege p und q
            else
            {   r.values[0][k] = values[0][m];
                r.values[1][k] = values[1][m] - q.values[1][n];
                m++;  n++;
            }
        //..Rueckgabe des Polygonzuges
        return r;
    }
    
    public LinearizedScalarFunction1d add(LinearizedScalarFunction1d q){
        int m, n, k, M, N, K, mmin, nmin;
        double xmin, xmax;
        
        //..Bestimme den gemeinsamen x-Bereich beider Polygonzuege
        M = this.size;
        N = q.size;
        if (this.values[0][0] >= q.values[0][0]) xmin = this.values[0][0]; else xmin = q.values[0][0];
        if (this.values[0][M-1] <= q.values[0][N-1]) xmax = this.values[0][M-1]; else xmax = q.values[0][N-1];
        if (xmax < xmin) return null;
        
        //..Uebergehen der Stuetzstellen von p und q mit x < xmin
        for (mmin=0; values[0][mmin] < xmin; mmin++);
        for (nmin=0; q.values[0][nmin] < xmin; nmin++);
        
        //..Bestimme Anzahl Stuetzstellen der Verschmelzung
        //  von p und q
        for (m=mmin, n=nmin, K=0; m<M && n<N; K++)
            if      (values[0][m] < q.values[0][n]) m++;
            else if (values[0][m] > q.values[0][n]) n++;
            else { m++;  n++;}
        
        //..Neuen Polygonzug mit K Punkten erzeugen
        LinearizedScalarFunction1d r = new LinearizedScalarFunction1d(K, xmin, xmax);
        
        //..Bestimme Polygonzug r durch Verschmelzung
        for (m=mmin, n=nmin, k=0; k<K; k++)
            
            //......Stuetzstelle des Polygonzuges this
            if (values[0][m] < q.values[0][n])
            {   r.values[0][k] = values[0][m];
//                r.values[1][k] = values[1][m] + q.getValue(this.values[0][m]);
                r.values[1][k] = values[1][m] + q.getValue(n-1,this.values[0][m]);
                m++ ;
            }
        //......Stuetzstelle des Polygonzuges q
            else if (q.values[0][n] < values[0][m])
            {   r.values[0][k] = q.values[0][n];
//                r.values[1][k] = this.getValue(q.values[0][n]) + q.values[1][n];
                r.values[1][k] = this.getValue(m-1,q.values[0][n]) + q.values[1][n];
                n++ ;
            }
        //......Gleiche Stuetzstelle der Polygonzuege p und q
            else
            {   r.values[0][k] = values[0][m];
                r.values[1][k] = values[1][m] + q.values[1][n];
                m++;  n++;
            }
        //..Rueckgabe des Polygonzuges
        return r;
    }
    
    @Override
    public LinearizedScalarFunction1d mult(double d) {
        double[][] w = new double[2][size];
        for (int i = 0; i < size; i++){
            w[0][i] = values[0][i];
            w[1][i] = d*values[1][i];
        }
        return new LinearizedScalarFunction1d(w);
    }
    
}
