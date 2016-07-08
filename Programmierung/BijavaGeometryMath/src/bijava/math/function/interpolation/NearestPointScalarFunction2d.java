package bijava.math.function.interpolation;
import bijava.geometry.dim2.*;

public class NearestPointScalarFunction2d extends DiscretizedScalarFunction2d{
    

    
    private double dmax=Double.NEGATIVE_INFINITY; // maximal distance between two
    // neighbouring sampling points

    
    public NearestPointScalarFunction2d(
            Point2d[] points,	// points = Stuetzstellen ...............
            double[] f      	// f[i] = Stuetzwerte ........................
            ) {
        this.samplingPoints = points;
        this.values = f;
        size=points.length;
        
        dmax=0.;
        double dmin=Double.POSITIVE_INFINITY;
        for(int i=0; i<points.length-1;i++){
            dmin=Double.POSITIVE_INFINITY;
            for(int j=i+1; j<points.length;j++)
                dmin=Math.min(dmin,points[i].distance(points[j]));
            dmax=Math.max(dmax,dmin);
        }
    }
    
    //--------------------------------------------------------------------------//
/** Gets the number of sampling points in the discretized scalar function.  */
//--------------------------------------------------------------------------//
  public int getSizeOfValues() { return size; }
  
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
 
    public double getValue(Point2d p)           
    {

        double dist=Double.POSITIVE_INFINITY;
        int ind=-1;
   
        for (int j = 0; j < size; j++) {	         
            double d =p.distance(samplingPoints[j]);
            if (d < epsilon) { 
                return values[j];
            }
                        
            if (d<dist){
              dist =d;
              ind=j;          
            }
        }
        
        return values[ind];   
    }
    
    //--------------------------------------------------------------------------//
    /** Gets the confidence value of an argument x.
     *
     *  @param x If the argument isn't in the definition range the methode returns
     *           <code>0.</code>                                        */
    //--------------------------------------------------------------------------//
    synchronized public double getConfidenceValue(Point2d p) {
       
        double dist=Double.POSITIVE_INFINITY;
        int ind=-1;
   
        for (int j = 0; j < size; j++) {	         
            double d =p.distance(samplingPoints[j]);
            if (d < epsilon) { 
                return 1.;
            }
                        
            if (d<dist){
              dist =d;
              ind=j;          
            }
        }
        
        return 1.- Math.min(1.,2.* dist/dmax);   
  
    }
    
    //--------------------------------------------------------------------------//
/** Gets the confidence region of the discretized scalar
 *  function.                                                               */
//--------------------------------------------------------------------------//
  public SimplePolygon2d getConfidenceRegion(){
        Point2d[] reg = {new Point2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), new Point2d(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), 
                new Point2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY) , new Point2d(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)};
      return new SimplePolygon2d(reg);
  
  }
}