package bijava.math.function.interpolation;

import bijava.math.function.*;

// Fuer ImTG benoetigt, da Methode getGradient() und die abfrage der Stuetzstellen 
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
abstract public class DiscretizedDifferentialScalarFunction1d extends DiscretizedScalarFunction1d implements DifferentialScalarFunction1d
{ 

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
  public DiscretizedDifferentialScalarFunction1d(int n, double xmin, double xmax)
  { super(n,xmin,xmax);
  }

//--------------------------------------------------------------------------//
/** Creates a finite one dimensional scalar function discretized by n
 *  sampling points. The value of all arguments is 0.
 *
 *  @param arguments This array containing the arguments of the sampling
 *                   points will be copied.                                 */
//--------------------------------------------------------------------------//
  public DiscretizedDifferentialScalarFunction1d(double[] arguments)
  { super(arguments);
  }

//--------------------------------------------------------------------------//
/** Creates a finite one dimensional scalar function discretized by n
 *  sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
  public DiscretizedDifferentialScalarFunction1d(double[][] values)
  { 
    super(values);
  }
  
//--------------------------------------------------------------------------//
  /** Creates a finite one dimensional scalar function discretized by n
   *  sampling points.
   *
   *  @param values This array containing the arguments and the values of the
   *                sampling points will be copied.                           */
//  --------------------------------------------------------------------------//
    public DiscretizedDifferentialScalarFunction1d(double[] x, double[] values)
    { 
      super(x,values);  
    }

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.
 *
 *  @param x If the argument isn't in the definition range the methode returns
 *           <code>Double.NaN</code>                                        */
//--------------------------------------------------------------------------//
  abstract public double getGradient(double x);
  
}
