package bijava.math.function.interpolation;

import bijava.math.function.*;

public class NearestPointScalarFunction1d extends DiscretizedScalarFunction1d
{
  private double dmax=Double.POSITIVE_INFINITY; // maximal distance between two
                                                // neighbouring sampling points

//--------------------------------------------------------------------------//
/** Creates a one dimensional interpolated scalar function discretized
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
  public NearestPointScalarFunction1d(int n, double xmin, double xmax)
  { super(n, xmin, xmax); dmax=(xmax-xmin)/(n-1.); }

//--------------------------------------------------------------------------//
/** Creates a one dimensional interpolated scalar function discretized
 *  by n sampling points. The value of all arguments is 0.
 *
 *  @param arguments This array containing the arguments of the sampling
 *                   points will be copied.                                 */
//--------------------------------------------------------------------------//
  public NearestPointScalarFunction1d(double[] arguments)
  { super(arguments); }

//--------------------------------------------------------------------------//
/** Creates a one dimensional interpolated scalar function discretized
 *  by n sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
  public NearestPointScalarFunction1d(double[][] values)
  { super(values);  
    dmax=0.;
    for (int i = 0; i < size-1; i++) 
        dmax=Math.max(dmax, Math.abs(values[0][i+1]-values[0][i]));
  }
  
  //--------------------------------------------------------------------------//
/** Creates a one dimensional interpolated scalar function discretized
 *  by n sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
  public NearestPointScalarFunction1d(double[] coord, double[] value)
  { 
    super(coord,value); 
    dmax=0.;
    for (int i = 0; i < size-1; i++) 
        dmax=Math.max(dmax, Math.abs(values[0][i+1]-values[0][i]));
  }
  
//--------------------------------------------------------------------------//
/** Tests the equality to another object.
 *
 *  @param object If this value is a <code>NearestPointScalarFunction1d</code>
 *                with the same definition range and the same function values
 *                in the epsilon range of the discretized scalar function the
 *                method returns <code>true</code>,
 *                otherwise <code>false</code>.                             */
//--------------------------------------------------------------------------//
  synchronized public boolean equals(Object object)
  { if (!(object instanceof NearestPointScalarFunction1d))            return false;
    NearestPointScalarFunction1d f = (NearestPointScalarFunction1d) object;

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
  synchronized public double getValue(double x) {
      int      size   = values[0].length;
      double distance = Double.POSITIVE_INFINITY;
      int ind=-1;
      
      for (int j = 0; j < size; j++) {
          double dist = Math.sqrt((x - values[0][j]) * (x - values[0][j]));
          
          if (dist<distance){
              distance = dist;
              ind=j;
          }        
      }
      return values[1][ind]; 
  }

//--------------------------------------------------------------------------//
/** Gets the confidence value of an argument x.
 *
 *  @param x If the argument isn't in the definition range the methode returns
 *           <code>0.</code>                                        */
//--------------------------------------------------------------------------//
  synchronized public double getConfidenceValue(double x)
  { 
      int      size   = values[0].length;
      double distance = Double.POSITIVE_INFINITY;
      int ind;
      
      for (int j = 0; j < size; j++) {
          double dist = Math.sqrt((x - values[0][j]) * (x - values[0][j]));
      
          if (dist < epsilon) return 1.; 
          
          if (dist<distance){
              distance = dist;
              ind=j;
          }
          
      }
      return 1.- Math.min(1.,2.* distance/dmax);
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

 public static void main(String[] args) {  
   double[] x = {1,1.3,1.6,1.9,2.2};
   double[] y = {0.7651977,0.6200860,0.4554022,0.2818186,0.1103623};  
   double[] x1 = {1,2,4,6,10};
   double[] y1 = {1,4,16,36,100};  // f(x)=x^2;
   
   
   NearestPointScalarFunction1d lp = new NearestPointScalarFunction1d(x1,y1);
   System.out.println(lp.getValue(9));
   System.out.println(lp.getConfidenceValue(9));
     
 }
}
  
  


