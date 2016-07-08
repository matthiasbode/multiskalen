package bijava.math.function.interpolation;

import bijava.math.function.*;
//==========================================================================//
/** The class "DiscretizedScalarFunction1d" provides proverties and methods
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
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
abstract public class DiscretizedScalarFunction1d extends AbstractScalarFunction1d
{ protected double  values[][];                   //    array of sampling points
  protected int     size;                         //   number of sampling points
  protected double  epsilon  = 0.00001;           //    epsilon range of a value

//--------------------------------------------------------------------------//
/** Creates a finite one dimensional scalar function discretized by n
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
  public DiscretizedScalarFunction1d(int n, double xmin, double xmax)
  { size              = (n > 1) ? n : 2;
    values            = new double[2][size];
    values[0][0]      = Math.min(xmax, xmin);
    values[0][size-1] = Math.max(xmax, xmin);

    for (int i = 0; i < size; i++)
    { values[0][i] = values[0][0]+i*(values[0][size-1]-values[0][0])/(size-1);
      values[1][i] = 0.0;
    }
  }

//--------------------------------------------------------------------------//
/** Creates a finite one dimensional scalar function discretized by n
 *  sampling points. The value of all arguments is 0.
 *
 *  @param arguments This array containing the arguments of the sampling
 *                   points will be copied.                                 */
//--------------------------------------------------------------------------//
  public DiscretizedScalarFunction1d(double[] arguments)
  { size   = arguments.length;
    values = new double[2][size];
    for (int i = 0; i < size; i++)
    { values[0][i] = arguments[i]; values[1][i] = 0.0; }
  }

//--------------------------------------------------------------------------//
/** Creates a finite one dimensional scalar function discretized by n
 *  sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
  public DiscretizedScalarFunction1d(double[][] values)
  { this.size   = values[0].length;
    this.values = new double[2][size];
    for (int i = 0; i < size; i++)
    { this.values[0][i] = values[0][i];
      this.values[1][i] = values[1][i];
    }
  }
  
//--------------------------------------------------------------------------//
  /** Creates a finite one dimensional scalar function discretized by n
   *  sampling points.
   *
   *  @param values This array containing the arguments and the values of the
   *                sampling points will be copied.                           */
//  --------------------------------------------------------------------------//
    public DiscretizedScalarFunction1d(double[] x, double[] values)
    { 
      if (x.length != values.length) throw new IllegalArgumentException("Arrays x and values must have same length!");
      this.size   = x.length;
      this.values = new double[2][size];
      for (int i = 0; i < size; i++)
      { this.values[0][i] = x[i];
        this.values[1][i] = values[i];
      }
    }

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.
 *
 *  @param x If the argument isn't in the definition range the methode returns
 *           <code>Double.NaN</code>                                        */
//--------------------------------------------------------------------------//
  abstract public double getValue(double x);

//--------------------------------------------------------------------------//
/** Gets a mean value of a sampling point and the neighbour sampling points.
 *
 *  @param i This variable is the position of the sampling point in this
 *           discretized scalar funktion.
 *  @param n The mean value will be calculated with the n left and right
 *           neighbours of the sampling point i.                            */
//--------------------------------------------------------------------------//
  public double getSmoothValue(int i, int n)
  { double sum     = 0.0;
    int    zaehler = 0;

    for (int j = i-n; j <= (i+n); j++)
    {
        if (j >= 0    && j < size) { sum += values[1][j];      zaehler++; }
    }
    return (sum / zaehler);
  }

//--------------------------------------------------------------------------//
/** Gets the value of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.
 *
 *  @return The method returns an array with the argument and the value.    */
//--------------------------------------------------------------------------//
  synchronized public double[] getValueAt(int i)
  { if (i < 0 || i >= size)
      throw new IndexOutOfBoundsException("You can't get get a value at "+ i);
    double[] value    = new double[2];
             value[0] = values[0][i];
             value[1] = values[1][i];
    return   value;
  }
  
   synchronized public double[][] getValues()
  {
      double[][] result    = new double[2][size];
      for( int i=0; i<size;i++){
          result[0][i] = values[0][i];
          result[1][i] = values[1][i];
      }
      return   result;
  }

//--------------------------------------------------------------------------//
/** Sets the value of a sampling point.
 *
 *  @param i     This variable is the position of the sampling point in this
 *               discretized scalar funktion.
 *  @param value This variable is the settig value.                         */
//--------------------------------------------------------------------------//
  public void setValueAt(int i, double value)
  { if (i < 0 || i >= size)
      throw new IndexOutOfBoundsException("You can't set get a value at "+i);
    values[1][i] = value;
  }

//--------------------------------------------------------------------------//
/** Sets the values of all sampling points with a values of a specified
 *  scalar function.                                                        */
//--------------------------------------------------------------------------//
  synchronized public void setValues(ScalarFunction1d function)
  { for (int i = 0; i < size; i++)
      values[1][i] = function.getValue(values[0][i]);
  }

//--------------------------------------------------------------------------//
/** Gets the number of sampling points in the discretized scalar function.  */
//--------------------------------------------------------------------------//
  public int getSizeOfValues() { return size; }

//--------------------------------------------------------------------------//
    /** Gets the minimum function value of sampling points 
     *
     *  @return The method returns an array containing the arguments of the
     *          minimum and its value.                                          */
//--------------------------------------------------------------------------//
    public double[] getMin() {
        double min[]  = new double[2];
        min[0] = values[0][0];
        min[1] = values[1][0];
        
        for (int i = 1; i < getSizeOfValues(); i++) if (values[1][i] < min[1]) {
            min[0] = values[0][i];
            min[1] = values[1][i];
        }
        return min;
    }
    
//--------------------------------------------------------------------------//
    /** Gets the maximum function value of sampling points 
     *
     *  @return The method returns an array containing the arguments of the
     *          maximum and its value.                                          */
//--------------------------------------------------------------------------//
    public double[] getMax() {
        double max[]  = new double[2];
        max[0] = values[0][0];
        max[1] = values[1][0];
        
        for (int i = 1; i < getSizeOfValues(); i++) if (values[1][i] > max[1]) {
            max[0] = values[0][i];
            max[1] = values[1][i];
        }
        return max;
    }
    
//--------------------------------------------------------------------------//
    /** Gets the smalest x-value of the sampling points   */
//--------------------------------------------------------------------------//
    public double getXMin() {
        return values[0][0];
    }
    
//--------------------------------------------------------------------------//
    /** Gets the largest x-value of the sampling points   */
//--------------------------------------------------------------------------//
    public double getXMax() {
        return values[0][getSizeOfValues()-1];
    }

//--------------------------------------------------------------------------//
/** Gets the epsilon in the range of an function value.                     */
//--------------------------------------------------------------------------//
  public double getEpsilon() { return epsilon; }

//--------------------------------------------------------------------------//
/** Sets the epsilon in the vicinity of an function value.
 *
 *  @param epsilon If this varaibale is negative or greater than 1.0
 *                 <code>epsilon</code> won't be changed.                   */
//--------------------------------------------------------------------------//
  public void setEpsilon(double epsilon)
  { if (epsilon < 1.0 && epsilon >= 0.0) this.epsilon = epsilon; }

//--------------------------------------------------------------------------//
/** Gets the front difference of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.                                   */
//--------------------------------------------------------------------------//
synchronized public double frontDifference(int i)
  { if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
    if (i == (size-1))
      return rearDifference(size-1);
    return (values[1][i+1] - values[1][i]) / (values[0][i+1] - values[0][i]);
  }

//--------------------------------------------------------------------------//
/** Gets the rear difference of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.                                   */
//--------------------------------------------------------------------------//
synchronized public double rearDifference(int i)
  { if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
    if (i == 0)
      return frontDifference(0);
    return (values[1][i] - values[1][i-1]) / (values[0][i] - values[0][i-1]);          
  }

//--------------------------------------------------------------------------//
/** Gets the central difference of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.                                   */
//--------------------------------------------------------------------------//
synchronized public double centralDifference(int i)
  { return upwindDifference(i, 0.5); }

//--------------------------------------------------------------------------//
/** Gets an upwinding difference of a sampling point.
 *
 *  <p>The upwinding coefficient <code>alpha</code> weights the part of the
 *  front difference to the rear difference.
 *  <code>alpha = 0.0</code> leads to a front difference,
 *  <code>alpha = 0.5</code> leads to a central difference and
 *  <code>alpha = 1.0</code> leads to a rear difference.</p>
 *
 *  @param i     The value is the position of the sampling point in this
 *               discretized scalar funktion.
 *  @param alpha The Upwinding coefficient has to be greater than 0.0 and
 *               less than 1.0.                                             */
//--------------------------------------------------------------------------//
synchronized public double upwindDifference(int i, double alpha)
  { if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
    return alpha * rearDifference(i) + (1.0 - alpha) * frontDifference(i);
  }

//--------------------------------------------------------------------------//
/** Gets the second difference of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.                                   */
//--------------------------------------------------------------------------//	
public double secondDifference(int i)
  { if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
    double deltaX = (values[0][i+1]    - values[0][i-1]);
    return 2.0 * (frontDifference(i) - rearDifference(i)) / deltaX;
  }

  
  
//--------------------------------------------------------------------------//
/** Converts this discretized one dimensional scalar function to a string.  */
//--------------------------------------------------------------------------//
  public  String toString()
  { String s = "";
    for (int i = 0 ;i < size; i++)
      s += " " + values[0][i] + " " + values[1][i];
    return s;
  }
}
