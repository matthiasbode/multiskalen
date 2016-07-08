package bijava.math.function;

import bijava.geometry.dim2.*;

//==========================================================================//
/** The class "ConstantFunction2d" provides proverties and methods for
 *  constant two dimensional scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class ConstantFunction2d extends AbstractDifferentialFunction2d {
    private double value=0;

//--------------------------------------------------------------------------//
/** Creates a constant one dimensional scalar function.
 *
 *  @param value This is the value of each argument.                        */
//--------------------------------------------------------------------------//    
    public ConstantFunction2d(double value){ this.value=value;}

//--------------------------------------------------------------------------//
/** Tests the equality to another object.
 *
 *  @param object If this value is a <code>ConstantFunction2d</code> with the
 *                same value the method returns
 *                <code>true</code>, otherwise <code>false</code>.          */
//--------------------------------------------------------------------------//
  public synchronized boolean equals (Object object)
  { if (!(object instanceof ConstantFunction2d)) return false;
    return (value == ((ConstantFunction2d) object).value);
  }
  

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.                                        */
//--------------------------------------------------------------------------//  
    public double getValue(Point2d p) {
	return value;
    }
	
//--------------------------------------------------------------------------//
/** Gets the derivation of an argument x.
 *
 *  @return The derivation of a constant function is always [0.0, 0.0].            */
//--------------------------------------------------------------------------//
    public Vector2d getGradient(Point2d p) {
	return new Vector2d(0.0,0.0);
    }
}
