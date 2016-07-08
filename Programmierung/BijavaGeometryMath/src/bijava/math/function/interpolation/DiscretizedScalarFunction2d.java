package bijava.math.function.interpolation;
import bijava.geometry.dim2.*;
import bijava.math.function.*;
//==========================================================================//
/** The class "DiscretizedScalarFunction2d" provides proverties and methods
 *  for a discretized two dimensional scalar functions.
 *
 *  <p>The dicretization will be represented by an array of sampling points.
 *  The sampling points define a finite interval. To describe an infinite
 *  discretized scalar function you can declare the value of the first
 *  sampling point as constant to the negative infinity
 *  (<code>setNegativeInifinity(true)</code>) and the value of the
 *  last sampling point as constant to the positive infinity
 *  (<code>setPositiveInifinity(true)</code>).
 *  Periodic functions are always infinite.</p>
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
abstract public class DiscretizedScalarFunction2d extends AbstractScalarFunction2d
{ protected Point2d[] samplingPoints;               //    array of sampling points
  protected double  values[];                       //    array of sampling values
  protected int     size;                         //   number of sampling points
  protected double  epsilon  = 0.00001;           //    epsilon (fuer den Test auf Gleichheit)
  protected double maxValue=Double.NaN, minValue=Double.NaN;
  protected BoundingBox2d bb2d;
  protected boolean hasNaN=false;

  protected DiscretizedScalarFunction2d() {
  }

//--------------------------------------------------------------------------//
/** Creates a finite dwo dimensional scalar function discretized by n
 *  sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
    public DiscretizedScalarFunction2d(Point2d[] points, double[] values) {

        if (points.length != values.length) {
            throw new IndexOutOfBoundsException("error: different length of points [" + points.length + "] and values[" + values.length + "]");
        }
        this.size = values.length;
        this.values = new double[size];
        this.samplingPoints = new Point2d[size];
        maxValue = Double.NEGATIVE_INFINITY;
        minValue = Double.POSITIVE_INFINITY;
        
        double maxXpoint = Double.NEGATIVE_INFINITY, minXpoint = Double.POSITIVE_INFINITY; 
        double maxYpoint = Double.NEGATIVE_INFINITY, minYpoint = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < size; i++) {
            this.samplingPoints[i] = points[i];
            this.values[i] = values[i];


            if (!hasNaN && Double.isNaN(values[i])) {
                hasNaN = true;
            }

            if (!Double.isNaN(values[i])) {

                maxValue = Math.max(maxValue, values[i]);
                minValue = Math.min(minValue, values[i]);
            }
            if(!Double.isNaN(samplingPoints[i].x)){
                maxXpoint = Math.max(maxXpoint, samplingPoints[i].x);
                minXpoint = Math.min(minXpoint, samplingPoints[i].x);
            }
            if(!Double.isNaN(samplingPoints[i].y)){
                maxYpoint = Math.max(maxYpoint, samplingPoints[i].y);
                minYpoint = Math.min(minYpoint, samplingPoints[i].y);
            }
        }
        bb2d = new BoundingBox2d(minXpoint, maxXpoint, minYpoint, maxYpoint);
    }

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.
 *
 *  @param x If the argument isn't in the definition range the methode returns
 *           <code>Double.NaN</code>                                        */
//--------------------------------------------------------------------------//
  abstract public double getValue(Point2d x);

//--------------------------------------------------------------------------//
/** Gets the value of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.
 *
 *  @return The method returns an array with the argument and the value.    */
//--------------------------------------------------------------------------//
  synchronized public double getSamplingValueAt(int i)
  { if (i < 0 || i >= size)
      throw new IndexOutOfBoundsException("You can't get get a value at "+ i);
    return   values[i];
  }
  //--------------------------------------------------------------------------//
/** Gets the value of a sampling point.
 *
 *  @param i The value is the position of the sampling point in this
 *           discretized scalar funktion.
 *
 *  @return The method returns an array with the argument and the value.    */
//--------------------------------------------------------------------------//
  synchronized public Point2d getSamplingPointAt(int i)
  { if (i < 0 || i >= size)
      throw new IndexOutOfBoundsException("You can't get get a value at "+ i);
    return   samplingPoints[i];
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
    values[i] = value;
  }

//--------------------------------------------------------------------------//
/** Sets the values of all sampling points with a values of a specified
 *  scalar function.                                                        */
//--------------------------------------------------------------------------//
  synchronized public void setValues(ScalarFunction2d function)
  { for (int i = 0; i < size; i++)
      values[i] = function.getValue(samplingPoints[i]);
  }

//--------------------------------------------------------------------------//
/** Gets the number of sampling points in the discretized scalar function.  */
//--------------------------------------------------------------------------//
  public int getSizeOfValues() { return size; }

//--------------------------------------------------------------------------//
/** Gets the minimum of the value range in the discretized scalar function.
 *
 *  @return The method returns an array containing the arguments of the
 *          minimum and its value.                                          */
//--------------------------------------------------------------------------//
  public double getMin(){
        return minValue;
    }

//--------------------------------------------------------------------------//
/** Gets the maximum of the value range in the discretized scalar function.
 *
 *  @return The method returns an array containing the arguments of the
 *          maximum and its value.                                          */
//--------------------------------------------------------------------------//
  public double getMax(){
        return maxValue;
    }

  //--------------------------------------------------------------------------//
/**
 *
 *  @return The method returns true, when values contains a Double.NaN
 *                                                                           */
//--------------------------------------------------------------------------//

  public boolean hasNaNValues(){
      return hasNaN;
  }

//--------------------------------------------------------------------------//
/** Gets the confidence region of the discretized scalar
 *  function.                                                               */
//--------------------------------------------------------------------------//
  abstract public SimplePolygon2d getConfidenceRegion();

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
/** Gets the BoundingBox2d of all SamplingPoints without Double.NaN items.
 *
 *  @return The method returns the BoundingBox2d of all SamplingPoints.
 *                                                                          */
//--------------------------------------------------------------------------//

 public BoundingBox2d getBoundingBox2d(){
      return bb2d;
  }

//--------------------------------------------------------------------------//
/** Converts this discretized one dimensional scalar function to a string.  */
//--------------------------------------------------------------------------//
  public  String toString()
  { String s = "";
    for (int i = 0 ;i < size; i++)
      s += " " + samplingPoints[i] + " " + values[i];
    return s;
  }
}
