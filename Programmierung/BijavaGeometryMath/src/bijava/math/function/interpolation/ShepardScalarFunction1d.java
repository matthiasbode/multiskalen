package bijava.math.function.interpolation;

import bijava.math.function.*;
//==========================================================================//
/** The class "ShepardScalarFunction1d" provides proverties and methods
 *  for a discretized one dimensional scalar functions which will be
 *  interpolated by the Shepard method.
 *
 *  <p>The dicretization will be represented by an array of sampling points.
 *  The sampling points define a finite interval but the method of Shepeard
 *  extrapolated the function to infinity.</p>
 *
 *  <p><strong>Version: </strong> <br><dd>1.0, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class ShepardScalarFunction1d extends DiscretizedDifferentialScalarFunction1d
{ private double mu;
  private double c;
  private double dmax=Double.NEGATIVE_INFINITY; // maximal distance between two
  private double dmin=Double.POSITIVE_INFINITY;                                               // neighbouring sampling points

//--------------------------------------------------------------------------//
/** Creates a one dimensional Shepard interpolated scalar function discretized
 *  by n equidistant sampling points. The value of all arguments is 0.
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
  public ShepardScalarFunction1d(int n, double xmin, double xmax)
  { super(n, xmin, xmax); mu = 2.0; c  = 0.0; dmin=dmax=(xmax-xmin)/(n-1.); }

//--------------------------------------------------------------------------//
/** Creates a one dimensional Shepard interpolated scalar function discretized
 *  by n sampling points. The value of all arguments is 0.
 *
 *  @param arguments This array containing the arguments of the sampling
 *                   points will be copied.                                 */
//--------------------------------------------------------------------------//
  public ShepardScalarFunction1d(double[] arguments)
  { super(arguments); mu = 2.0; c  = 0.0; }

//--------------------------------------------------------------------------//
/** Creates a one dimensional Shepard interpolated scalar function discretized
 *  by n sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
  public ShepardScalarFunction1d(double[][] values)
  { super(values); mu = 2.0; c  = 0.0; 
//    dmax=0.;
    for (int i = 0; i < size-1; i++) {
        double dif = values[0][i+1]-values[0][i];
        dmax=Math.max(dmax, Math.abs(dif));
        dmin=Math.min(dmin, Math.abs(dif));
    }
     
  }
  
  //--------------------------------------------------------------------------//
/** Creates a one dimensional Shepard interpolated scalar function discretized
 *  by n sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
  public ShepardScalarFunction1d(double[] coord, double[] value)
  { 
    super(coord,value); 
    mu = 2.0; c  = 0.0; 
//    dmax=0.;
    for (int i = 0; i < size-1; i++) 
    {
        double dif = values[0][i+1]-values[0][i];
        dmax=Math.max(dmax, Math.abs(dif));
        dmin=Math.min(dmin, Math.abs(dif));
    }
  }
  

//--------------------------------------------------------------------------//
/** Tests the equality to another object.
 *
 *  @param object If this value is a <code>ShepardScalarFunction1d</code>
 *                with the same definition range and the same function values
 *                in the epsilon range of the discretized scalar function the
 *                method returns <code>true</code>,
 *                otherwise <code>false</code>.                             */
//--------------------------------------------------------------------------//
  synchronized public boolean equals(Object object)
  { if (!(object instanceof ShepardScalarFunction1d))            return false;
    ShepardScalarFunction1d f = (ShepardScalarFunction1d) object;

    for (int i = 0; i < values[0].length; i++)
      if (Math.abs(values[1][i] - f.getValue(values[0][i])) > getEpsilon())
                                                                 return false;
    for (int i = 0; i < f.values[0].length; i++)
      if (Math.abs(f.values[1][i] - getValue(f.values[0][i])) > getEpsilon())
                                                                 return false;
                                                                 return true;
  }

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.
 *
 *  @param x If the argument isn't in the definition range the methode returns
 *           <code>Double.NaN</code>                                        */
//--------------------------------------------------------------------------//
  synchronized public double getValue(double x)
  { int      size   = values[0].length;
    double   value  = 0.0;
    double   sum    = 0.0;
    double   dist   = 0.0;
    double[] weight = new double[size];

    for (int j = 0; j < size; j++)
    { dist = Math.sqrt((x - values[0][j]) * (x - values[0][j]));
      if (Math.abs(dist) < epsilon) return values[1][j]; 
      else { dist      = Math.pow(dist, mu) + c;
             sum      += 1.0 / dist;
             weight[j] = 1.0 / dist;
           }
    }
    for (int i = 0; i < size; i++) value += weight[i] / sum * values[1][i];
    return value;
  }
  
  
  /** angenaeherte Ableitung - noch aendern -> mehr Schritte + Polynom
   */
  synchronized public double getGradient(double x)
  {
    if(Double.isInfinite(dmin))
    {
        for (int i = 1; i < size-1; i++) 
        {
            double dif = values[0][i+1]-values[0][i];
            dmax=Math.max(dmax, Math.abs(dif));
            dmin=Math.min(dmin, Math.abs(dif));
        }
    }   
    double next = getValue(x+(0.1*dmin));
    double prev = getValue(x-(0.1*dmin));    
    return ((next - prev) / 0.2*dmin);
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

//--------------------------------------------------------------------------//
/** Gets the confidence value of an argument x.
 *
 *  @param x If the argument isn't in the definition range the methode returns
 *           <code>0.</code>                                        */
//--------------------------------------------------------------------------//
  synchronized public double getConfidenceValue(double x)
  { int      size   = values[0].length;
    double   value  = 0.0;
    double   dist   = 0.0;
    double mindistance = Double.POSITIVE_INFINITY;

    for (int j = 0; j < size; j++)
    { dist = Math.sqrt((x - values[0][j]) * (x - values[0][j]));
      if (Math.abs(dist) < epsilon) return 1.; 
      else mindistance = Math.min(mindistance,dist);
    }
    return 1.- Math.min(1.,2.* mindistance/dmax);
  }

//--------------------------------------------------------------------------//
/** Gets the minimum of the value range in the linearized scalar function.
 *
 *  @return The method returns an array containing the arguments of the
 *          minimum and its value.                                          */
//--------------------------------------------------------------------------//
  synchronized public double[] getMin()
  { double min[]  = new double[2];
           min[0] = values[0][0];
           min[1] = values[1][0];

    for (int i = 1; i < values[0].length; i++) if (values[1][i] < min[1])
    { min[0] = values[0][i]; min[1] = values[1][i]; }
    return min;
  }

//--------------------------------------------------------------------------//
/** Gets the maximum of the value range in the linearized scalar function.
 *
 *  @return The method returns an array containing the arguments of the
 *          maximum and its value.                                          */
//--------------------------------------------------------------------------//
  synchronized public double[] getMax()
  { double max[]  = new double[2];
           max[0] = values[0][0];
           max[1] = values[1][0];

    for (int i = 1; i < values[0].length; i++) if (values[1][i] < max[1])
    { max[0] = values[0][i]; max[1] = values[1][i]; }
    return max;
  }

//--------------------------------------------------------------------------//
/** Gets the minimaum of the definition range int the discretized scalar
 *  function.                                                               */
//--------------------------------------------------------------------------//
  public double getXMin() { return Double.NEGATIVE_INFINITY; }

//--------------------------------------------------------------------------//
/** Gets the maximaum of the definition range int the discretized scalar
 *  function.                                                               */
//--------------------------------------------------------------------------//
  public double getXMax() { return Double.POSITIVE_INFINITY; }

//--------------------------------------------------------------------------//
/** Gets mu.                                                                */
//--------------------------------------------------------------------------//
  public double getMu() { return mu; }

//--------------------------------------------------------------------------//
/** Sets mu.                                                                */
//--------------------------------------------------------------------------//
  public void setMu(double mu) { this.mu = mu; }

//--------------------------------------------------------------------------//
/** Gets c.                                                                 */
//--------------------------------------------------------------------------//
  public double getC() { return c; }

//--------------------------------------------------------------------------//
/** Sets c.                                                                 */
//--------------------------------------------------------------------------//
  public void setC(double c) { this.c = c; }
  
}