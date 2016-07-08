package bijava.math.function;

import bijava.geometry.dimN.*;
//==========================================================================//
/** The class "ConstantFunctionNd" provides proverties and methods for
 *  constant two dimensional scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class ConstantFunctionNd extends AbstractDifferentialFunctionNd {
    private double value=0;
    private int dim=0;
    
    
    public ConstantFunctionNd(int dimension){ 
        this.dim = dimension;
    }

//--------------------------------------------------------------------------//
/** Creates a constant one dimensional scalar function.
 *
 *  @param value This is the value of each argument.                        */
//--------------------------------------------------------------------------//    
    public ConstantFunctionNd(double value, int dimension){ 
        this.value=value;
        this.dim = dimension;
    }

//--------------------------------------------------------------------------//
/** Tests the equality to another object.
 *
 *  @param object If this value is a <code>ConstantFunctionNd</code> with the
 *                same value the method returns
 *                <code>true</code>, otherwise <code>false</code>.          */
//--------------------------------------------------------------------------//
  public synchronized boolean equals (Object object)
  { if (!(object instanceof ConstantFunctionNd)) return false;
    return (value == ((ConstantFunctionNd) object).value);
  }
  

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.                                        */
//--------------------------------------------------------------------------//  
    public double getValue(PointNd p) {
        if(p.dim()!= this.dim) return Double.NaN;
	return value;
    }
	
//--------------------------------------------------------------------------//
/** Gets the derivation of an argument x.
 *
 *  @return The derivation of a constant function is always [0.0, 0.0].            */
//--------------------------------------------------------------------------//
    public VectorNd getGradient(PointNd p) {
        if(p.dim()!= this.dim) return null;
	return new VectorNd(p.dim());
    }

    public int getDim() {
        return dim;
    }
}
