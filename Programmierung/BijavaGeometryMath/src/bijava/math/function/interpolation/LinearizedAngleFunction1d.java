package bijava.math.function.interpolation;

import bijava.math.function.*;

public class LinearizedAngleFunction1d extends DiscretizedScalarFunction1d
{

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
  public LinearizedAngleFunction1d(int n, double xmin, double xmax)
  { super(n, xmin, xmax); }

//--------------------------------------------------------------------------//
/** Creates a finite one dimensional scalar function linearized by n
 *  sampling points. The value of all arguments is 0.
 *
 *  @param arguments This array containing the arguments of the sampling
 *                   points will be copied.                                 */
//--------------------------------------------------------------------------//
  public LinearizedAngleFunction1d(double[] arguments)
  { super(arguments); }

//--------------------------------------------------------------------------//
/** Creates a finite one dimensional scalar function linearized by n
 *  sampling points.
 *
 *  @param values This array containing the arguments and the values of the
 *                sampling points will be copied.                           */
//--------------------------------------------------------------------------//
  public LinearizedAngleFunction1d(double[][] values)
  { super(values);
    int counter = 0;
    for (int i = 0; i < values[1].length; i++) {
        if (values[1][i] > 360. || values[1][i] < -180.) counter++;
    }
    if (counter > 0)
        System.out.println("WARNUNG: "+counter+" ungueltige Werte bei Initialisierung von LinearizedAngleFunction1d gefunden!");
  }

//--------------------------------------------------------------------------//
/** Tests the equality to another object.
 *
 *  @param object If this value is a <code>LinearizedAngleFunction1d</code>
 *                with the same definition range and the same function values
 *                in the epsilon range of the linearized angle function the
 *                method returns <code>true</code>,
 *                otherwise <code>false</code>.                             */
//--------------------------------------------------------------------------//
  synchronized public boolean equals(Object object)
  { if (!(object instanceof LinearizedAngleFunction1d))         return false;
    LinearizedAngleFunction1d f = (LinearizedAngleFunction1d) object;

    int n =   getSizeOfValues();
    int m = f.getSizeOfValues();

    if (Math.abs(values[0][0] - f.values[0][0]) > getEpsilon())  return false;
    if (Math.abs(values[0][n-1] - f.values[0][m-1]) > getEpsilon())
                                                                 return false;
    for (int i = 0; i < n; i++)
      if (Math.abs((values[1][i]+360.)%360. - (f.getValue(values[0][i])+360.)%360.) > getEpsilon())
                                                                 return false;
    for (int i = 0; i < m; i++)
      if (Math.abs((f.values[1][i]+360.)%360. - (getValue(f.values[0][i])+360.)%360.) > getEpsilon())
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
        int size = getSizeOfValues();
        if (x < values[0][0])
            return Double.NaN;
        if (x > values[0][size-1])
            return Double.NaN;

        int pos = size;
        for (int i = 0; i < size; i++)
        { if (values[0][i] == x) return values[1][i];
          if (values[0][i] >  x) { pos = i-1; i = size; }
        }

        if (pos < size)
        { double x1 = values[0][pos]; double y1 = values[1][pos];
          double x2 = values[0][pos]; double y2 = values[1][pos];

          for (int i = pos; i < size; i++)
            if (values[0][i]>x) { x2 = values[0][i]; y2 = values[1][i]; break; }

          // Sonderfaelle abfangen:
          // Wenn Diff(y1,y2) > 180, setze min(y1,y2)+=360;
          if (Math.abs(y1-y2) > 180) {
              if (y1 < y2)
                  y1+=360.;
              else
                  y2+=360.;
          }

          return (x1 != x2) ? (y1 + ((x - x1) / (x2 - x1)) * (y2 - y1))%360. : y1%360.;
        }
        return Double.NaN;
  }
  
  //--------------------------------------------------------------------------//
/** Gets the derivation of an argument x.
 *
 *  @param x If the argument isn't in the definition range the methode returns
 *           <code>Double.NaN</code>                                        */
//--------------------------------------------------------------------------//
  synchronized public double getGradient(double x)
  { int size = getSizeOfValues();
    if (x < values[0][0])
        return Double.NaN;
    if (x > values[0][size-1])
        return Double.NaN;
    
    int pos = size;
    for (int i=0; i<size; i++) if (values[0][i] > x) { pos = i-1; i = size; }

    if (pos < size)
    { double x1 = values[0][pos]; double y1 = values[1][pos];
      double x2 = values[0][pos]; double y2 = values[1][pos];
      
      for (int j = pos; j < size; j++)
        if (values[0][j]>x) { x2 = values[0][j]; y2 = values[1][j];j = size; }
                
      // Sonderfaelle abfangen:
      // Wenn Diff(y1,y2) > 180, setze min(y1,y2)+=360;
      if (Math.abs(y1-y2) > 180) {
          if (y1 < y2)
              y1+=360.;
          else
              y2+=360.;
      }
      
      //-x liegt auf einer Stuetzstelle
      if (x1 == x) {
          //-- es liegt eine Sprungstelle vor
          if (y1 != y2 && x == x2)
              return (y2-y1) > 0. ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

          //-- es handelt sich um einen Knick => Mittelwert von linkem und rechtem Differenzenquotient
          //----- x ist erste Stuetzstelle => hinterer Differenzenquotient
          if (pos == 0)
              return rearDifference(pos);

          //----- x ist letzte Stuetzstelle => vorderer Differenzenquotient
          if (pos == size-1)
              return frontDifference(pos);

          //----- sonst => zentraler Differenzenquotient
        //  return centralDifference(pos);
          throw new RuntimeException("getGradient arbeitet noch nicht perfekt!");
      }
      
      //-x liegt zwischen 2 Stuetzstellen
      if (x1 != x2)
          return (y2-y1)%360. / (x2-x1);
    }
    if (x == values[0][size-1])
        return frontDifference(size-1);
    return Double.NaN;
  }
  
  synchronized public double frontDifference(int i)
  { if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
    if (i == (size-1))
      return rearDifference(size-1);
    double x1 = values[0][i  ]; double y1 = values[1][i  ];
    double x2 = values[0][i+1]; double y2 = values[1][i+1];
    if (Math.abs(y1-y2) > 180.) {
        if (y1 < y2)
            y1+=360.;
        else
            y2+=360.;
    }
    return (y2-y1)/(x2-x1);
  }
  
  synchronized public double rearDifference(int i)
  { if (i < 0 || i >= size) throw new IndexOutOfBoundsException();
    if (i == 0)
      return frontDifference(0);
    double x1 = values[0][i-1]; double y1 = values[1][i-1];
    double x2 = values[0][i  ]; double y2 = values[1][i  ];
    if (Math.abs(y1-y2) > 180.) {
        if (y1 < y2)
            y1+=360.;
        else
            y2+=360.;
    }
    return (y2-y1)/(x2-x1);
  }
  

//--------------------------------------------------------------------------//
/** Gets the minimum of the value range in the linearized angle function.
 *
 *  @return The method returns an array containing the arguments of the
 *          minimum and its value.                                          */
//--------------------------------------------------------------------------//
  synchronized public double[] getMin()
  { double min[]  = new double[2];
           min[0] = values[0][0];
           min[1] = values[1][0];

    for (int i = 1; i < getSizeOfValues(); i++) if (values[1][i] < min[1])
    { min[0] = values[0][i]; 
      min[1] = values[1][i];
    }
    return min;
  }

//--------------------------------------------------------------------------//
/** Gets the maximum of the value range in the linearized angle function.
 *
 *  @return The method returns an array containing the arguments of the
 *          maximum and its value.                                          */
//--------------------------------------------------------------------------//
  synchronized public double[] getMax()
  { double max[]  = new double[2];
           max[0] = values[0][0];
           max[1] = values[1][0];

    for (int i = 1; i < getSizeOfValues(); i++) if (values[1][i] < max[1])
    { max[0] = values[0][i]; 
      max[1] = values[1][i];
    }
    return max;
  }

//--------------------------------------------------------------------------//
/** Gets the minimum of the definition range in the linearized angle
 *  function.                                                               */
//--------------------------------------------------------------------------//
  public double getXMin()
  { return values[0][0]; }

//--------------------------------------------------------------------------//
/** Gets the maximum of the definition range in the linearized angle
 *  function.                                                               */
//--------------------------------------------------------------------------//
  public double getXMax()
  { return values[0][getSizeOfValues()-1];
  }
  
//--------------------------------------------------------------------------//
/** Transforms each value of this LinearizedAngleFunction1d using a formula
 *  that calculates the mathematic angle starting from a meteorologic angle.*/
//--------------------------------------------------------------------------//
    public void setMet2math() {
        for (int i=0; i < this.getSizeOfValues(); i++) {
            values[1][i] = met2math(values[1][i]);
        }
    }
    
//--------------------------------------------------------------------------//
/** Calculates the mathematic angle starting from a meteorologic angle.
 *  @param dir  the meteorologic angle, that is to be transformed into the
 *              mathematical angle.
 *  @return     the mathematical angle of dir.                              */
//--------------------------------------------------------------------------//   
    public static double met2math(double dir) {
        double wwx = (double) (Math.sin(dir * Math.PI / 180.));
        double wwy = (double) (Math.cos(dir * Math.PI / 180.));

        double wx = -wwx;
        double wy = -wwy;

        double wave_dir_m = (double) (sign(wy) * Math.acos(wx) * 180. / Math.PI);
        if (sign(wave_dir_m) < 0.)
            wave_dir_m = 360. + wave_dir_m;
        return wave_dir_m;
    }

//---------------------------------------------------------------------------------------------------------------//
//---------------------------------------------------------------------------------------------------------------//        

    private static double sign(double value) {
        if (value > 0.)
            return 1.0;
        else
            return -1.0;
    }

}
