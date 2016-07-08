/*
 * DiscretizedVectorFunction1d.java
 *
 * Created on 13. Juni 2006, 14:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.function.interpolation;

import bijava.math.function.*;
import bijava.geometry.dimN.*;

/**
 *
 * @author berthold
 * @version 13.06.06
 */
public abstract class DiscretizedVectorFunction1d extends AbstractVectorFunction1d {
    protected double  values[][];                   //    array of sampling points
    protected int     size;                         //   number of sampling points
    protected int     m;                            //    Co-Domain-Dimension 
    protected double  epsilon  = 0.00001;           //    epsilon range of a value
    
    //--------------------------------------------------------------------------//
    /** Creates a finite one dimensional vector value function discretized by n
    *  equidistant sampling points. The value of all arguments is 0.
    *
    *  @param n    If this value is less than 2 the number of sampling points
    *              is 2.
    *  @param xmin If this value is less than <code>xmax</code> the minimum of
    *              the definition range is <code>xmin</code>,
    *              otherwise it is <code>xmax</code>.
    *  @param xmax If this value is greater than <code>xmin</code> the maximum of
    *              the definition range is <code>xmin</code>,
    *              otherwise it is <code>xmin</code>.
    *  @param m    Co-Domain-Dimension. If m is less than 1, m is set to 1*/
    //--------------------------------------------------------------------------//
    public DiscretizedVectorFunction1d(int n, double xmin, double xmax, int m) {
        size              = (n > 1) ? n : 2;
        values            = new double[m+1][size];
        values[0][0]      = Math.min(xmax, xmin);
        values[0][size-1] = Math.max(xmax, xmin);
        this.m = m;

        for (int i = 0; i < size; i++) {
            values[0][i] = values[0][0]+i*(values[0][size-1]-values[0][0])/(size-1);
            for (int j = 1; j < m+1; j++)
                values[j][i] = 0.0;
        } 
    }
    
    //--------------------------------------------------------------------------//
    /** Umwandlungskonstruktor:
     *  Erzeugt aus einem Feld von DiscretizedScalarFunction1d eine
     *  DiscretizedVectorFunction1d. Die Dimension des Wertebereichs, sowie die Stuetzstellen
     *  aller Funktionen muessen uebereinstimmen.
     *  @param functions    skalare Funktionen, die in eine DiscretizedVectorFunction1d
     *                      ueberfuehrt werden sollen.*/
    //--------------------------------------------------------------------------//
    public DiscretizedVectorFunction1d(DiscretizedScalarFunction1d[] functions) {
        // Ersteinmal pruefen, ob die Funktionen alle zueinander passen
        // (1. Ist die Anzahl der Stuetzstellen beider Funktionen gleich?)
        // (2. Stimmen die Stuetzstellen der beiden Funktionen unter Einbeziehung der Epsiolontik ueberein?)
        for (int i=0; i<functions.length; i++) {
            for (int j=i+1; j<functions.length; j++) {
                // Stimmt die Anzahl der Stuetzstellen bei jeder Funktion ueberein?
                if (functions[i].getSizeOfValues() != functions[j].getSizeOfValues())
                    throw new IllegalArgumentException("Anzahl der Stuetzstellen von functions["+i+"] und functions["+j+"] sind ungleich!");
                // Genauere Pruefung: Stimmen die Stuetzstellen der beiden Funktionen
                //                   unter Einbeziehung der Epsiolontik ueberein?
                else {
                    for (int k=0; k<functions[i].getSizeOfValues(); k++) {
                        if (functions[i].getValueAt(k)[0] < functions[j].getValueAt(k)[0]-epsilon
                         || functions[i].getValueAt(k)[0] > functions[j].getValueAt(k)[0]+epsilon)
                            throw new IllegalArgumentException("Die "+k+". Stuetzstellen von functions["+i+"] und functions["+j+"] sind ungleich! ("+functions[i].getValueAt(k)[0]+" != "+functions[j].getValueAt(k)[0]+")");
                    }
                }
            }
        }
        // Scheint in Ordnung zu sein. Also Werte kopieren:
        size   = functions[0].getSizeOfValues();
        m      = functions.length;
        values = new double[m+1][size];
        for (int j=0; j<size; j++)
            values[0][j] = functions[0].getValueAt(j)[0];
        for (int i=0; i<m; i++) {
            for (int j=0; j<size; j++)
                values[i+1][j] = functions[i].getValueAt(j)[1];
        }
    }
    
//--------------------------------------------------------------------------//
/** Creates a finite one dimensional vector value function discretized by n
 *  sampling points. The value of all arguments is 0.
 *
 *  @param arguments This array containing the arguments of the sampling
 *                   points will be copied.
    @param m         Co-Domain-Dimension*/
//--------------------------------------------------------------------------//
    public DiscretizedVectorFunction1d(double[] arguments, int m) {
        size   = arguments.length;
        this.m = m;
        values = new double[m+1][size];
        for (int i = 0; i < size; i++) {
            values[0][i] = arguments[i];
            for (int j=1; j<m+1; j++)
                values[m][i] = 0.0;
        }
    }
    
    //--------------------------------------------------------------------------//
    /** Creates a finite one dimensional vector value function discretized by n
    *  sampling points.
    *
    *  @param values This array containing the arguments and the values of the
    *                sampling points will be copied.                           */
    //--------------------------------------------------------------------------//
    public DiscretizedVectorFunction1d(double[][] values) {
        this.size   = values[0].length;
        this.m      = values.length-1;
        this.values = new double[m+1][size];
        for (int i=0; i<size; i++) {
            for (int j=0; j<m+1; j++)
            this.values[j][i] = values[j][i];
        }
    }
    
    //--------------------------------------------------------------------------//
    /** Gets the values of an argument x.
     *
     *  @param x If the argument isn't in the definition range the methode returns
     *           <code>null</code>                                        */
    //--------------------------------------------------------------------------//
    abstract public VectorNd getValue(double x);
    
    //--------------------------------------------------------------------------//
    /** Gets the values of a sampling point.
     *
     *  @param i The value is the position of the sampling point in this
     *           discretized vector value function.
     *
     *  @return The method returns an array containing the argument and the values.*/
    //--------------------------------------------------------------------------//
    synchronized public double[] getValueAt(int i) {
        if (i < 0 || i >= size)
            throw new IndexOutOfBoundsException("You can't get get a value at i="+ i);
        double[] value = new double[m+1];
        for (int j=0; j<m+1; j++)
            value[j] = values[j][i];
        return   value;
    }
    
    //--------------------------------------------------------------------------//
    /** Sets the value of a sampling point.
    *
    *  @param i     This variable is the position of the sampling point in this
    *               discretized scalar funktion.
    *  @param value This variable is the settig value.                         */
    //--------------------------------------------------------------------------//
    public void setValuesAt(int i, double[] newValues) {
        if (i < 0 || i >= size)
            throw new IndexOutOfBoundsException("You can't set a value at i="+i);
        if (newValues.length != m)
            throw new IllegalArgumentException("size of newValues ("+newValues.length+") != m ("+(m)+")");
        for (int j=0; j<m; j++)
        values[j+1][i] = newValues[j];
    }
    
    //--------------------------------------------------------------------------//
    /** Gets the number of sampling points in the discretized vector value function.  */
    //--------------------------------------------------------------------------//
    public int getSizeOfValues() { return size; }

    //--------------------------------------------------------------------------//
    /** Gets the minimum of the sampling-points' values of the definition
     *  range in the discretized vector value function.                         */
    //--------------------------------------------------------------------------//
    public double getXMin() {
        return values[0][0];
    }

    //--------------------------------------------------------------------------//
    /** Gets the maximum of the sampling-points' values of the definition
     *  range in the discretized vector value function.                         */
    //--------------------------------------------------------------------------//
    public double getXMax() {
        return values[0][size-1];
    }
    
    //--------------------------------------------------------------------------//
    /** Gets the minimum of the sampling-points' values of the value
     *  range in the discretized vector value function.
     *  
     *  @return     The method returns an array containing the arguments of the
     *              minimum and its value.
     *  @param i    the index of the element of the vector value function starting
     *              at 0 to m-1. (Remember: m is the number of values for each
     *              x in the definition range, so that getMin(0) != getXMin(). )*/
    //--------------------------------------------------------------------------//
    public double[] getMin(int i) {
        int index = 0;
        for (int j=1; j<size; j++) {
            if (values[i+1][j] < values[i+1][index])
                index=j;
        }
        double[] res = {values[0][index],values[i+1][index]};
        return res;
    }
    
    //--------------------------------------------------------------------------//
    /** Gets the maximum of the sampling-points' values of the value
     *  range in the discretized vector value function.
     *  
     *  @return     The method returns an array containing the arguments of the
     *              maximum and its value.
     *  @param i    the index of the element of the vector value function starting
     *              at 0 to m-1. (Remember: m is the number of values for each
     *              x in the definition range, so that getMax(0) != getXMax(). )*/
    //--------------------------------------------------------------------------//
    public double[] getMax(int i) {
        int index = 0;
        for (int j=1; j<size; j++) {
            if (values[i+1][j] > values[i+1][index])
                index=j;
        }
        double[] res = {values[0][index],values[i+1][index]};
        return res;
    }

    //--------------------------------------------------------------------------//
    /** Gets the epsilon in the range of an function value.                     */
    //--------------------------------------------------------------------------//
    public double getEpsilon() { return epsilon; }

    //--------------------------------------------------------------------------//
    /** Sets the epsilon in the vicinity of an function value.
    *
    *  @param epsilon If this varaibale is negative or greater than 1.0
    *                 <code>epsilon</code> won't be changed.                    */
    //--------------------------------------------------------------------------//
    public void setEpsilon(double epsilon) {
        if (epsilon < 1.0 && epsilon >= 0.0) this.epsilon = epsilon;
    }
    
    //--------------------------------------------------------------------------//
    /** Gets the front difference of a sampling point for each element of the vector.
    *
    *  @param i The value is the position of the sampling point in this
    *           discretized vector value funktion.                              */
    //--------------------------------------------------------------------------//
    synchronized public VectorNd frontDifference(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
        if (i == (size-1))
            return rearDifference(size-1);
        double[] temp = new double[m];
        for (int j=1; j<m+1; j++)
            temp[j-1] = (values[j][i+1] - values[j][i]) / (values[0][i+1] - values[0][i]);
        return new VectorNd(temp);
    }

    //--------------------------------------------------------------------------//
    /** Gets the rear difference of a sampling point for each element of the vector.
    *
    *  @param i The value is the position of the sampling point in this
    *           discretized vector value funktion.                                   */
    //--------------------------------------------------------------------------//
    synchronized public VectorNd rearDifference(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
        if (i == 0)
            return frontDifference(0);
        double[] temp = new double[m];
        for (int j=1; j<m+1; j++)
            temp[j-1] = (values[j][i] - values[j][i-1]) / (values[0][i] - values[0][i-1]);
        return new VectorNd(temp);
    }

    //--------------------------------------------------------------------------//
    /** Gets the central difference of a sampling point for each element of the vector.
    *
    *  @param i The value is the position of the sampling point in this
    *           discretized vector value funktion.                                   */
    //--------------------------------------------------------------------------//
    synchronized public VectorNd centralDifference(int i)
    { return upwindDifference(i, 0.5); }

    //--------------------------------------------------------------------------//
    /** Gets an upwinding difference of a sampling point for each element of the vector.
    *
    *  <p>The upwinding coefficient <code>alpha</code> weights the part of the
    *  front difference to the rear difference.
    *  <code>alpha = 0.0</code> leads to a front difference,
    *  <code>alpha = 0.5</code> leads to a central difference and
    *  <code>alpha = 1.0</code> leads to a rear difference.</p>
    *
    *  @param i     The value is the position of the sampling point in this
    *               discretized vector value funktion.
    *  @param alpha The Upwinding coefficient has to be greater than 0.0 and
    *               less than 1.0.                                             */
    //--------------------------------------------------------------------------//
    synchronized public VectorNd upwindDifference(int i, double alpha) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
        return (rearDifference(i).mult(alpha)).add(frontDifference(i).mult(1.0 - alpha));
    }

    //--------------------------------------------------------------------------//
    /** Gets the second difference of a sampling point for each element of the vector.
    *
    *  @param i The value is the position of the sampling point in this
    *           discretized vector value funktion.                              */
    //--------------------------------------------------------------------------//	
    public VectorNd secondDifference(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
            double deltaX = (values[0][i+1] - values[0][i-1]);
        return (frontDifference(i).sub(rearDifference(i))).mult(2.0/deltaX);
    }

    //--------------------------------------------------------------------------//
    /** Converts this discretized one dimensional scalar function to a string.  */
    //--------------------------------------------------------------------------//
    public String toString() {
        String s = "";
        for (int i = 0 ;i < size; i++) {
            for (int j=0; j<m+1; j++)
                s += "\t" + values[j][i];
            s+="\n";
        }
        return s;
    }
    
    //--------------------------------------------------------------------------//
    /** Splittet die DiscretizedVectorFunction1d in m DiscretizedScalarFunction1ds auf.*/
    //--------------------------------------------------------------------------//
    public final DiscretizedScalarFunction1d[] split() {
        DiscretizedScalarFunction1d[] res = new DiscretizedScalarFunction1d[m];
        // Definitionsbereich bereitstellen
        double[] def = new double[size];
        System.arraycopy(values[0], 0, def, 0, size);
        for (int i=0; i<m; i++) {
            double[] val = new double[size];
            System.arraycopy(values[i+1], 0, val, 0, size);
            double[][] temp = new double[2][size];
            temp[0] = def;
            temp[1] = val;

            final int pos = i;
            res[i] = new DiscretizedScalarFunction1d(temp) {
                public double getValue(double x) {
                    return this_getValueV(x,pos);
                }

                public double[] getMin() {
                    return this_getMin(pos);
                }

                public double[] getMax() {
                    return this_getMax(pos);
                }

                public double getXMin() {
                    return this_getXMin();
                }

                public double getXMax() {
                    return this_getXMax();
                }
                
            };
        }
        return res;
    }
    
    
    
    private double this_getValueV(double x, int i) {
        return getValue(x).getCoord(i);
    }
    
    private double[] this_getMin(int i) {
        return getMin(i);
    }
    
    private double[] this_getMax(int i) {
        return getMax(i);
    }
    
    private double this_getXMin() {
        return getXMin();
    }
    
    private double this_getXMax() {
        return getXMax();
    }
   
}