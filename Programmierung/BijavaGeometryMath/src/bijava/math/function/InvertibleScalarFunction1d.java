package bijava.math.function;
//==========================================================================//
/**
 * The interface "InvertibleScalarFunction1d" provides methods for one dimensional
 *  invertible scalar functions.
 * @version 0.1
 * @author Institute of Computer Science in Civil Engineering
 * @author leibniz University of Hannover
 */
//==========================================================================//
public interface InvertibleScalarFunction1d extends ScalarFunction1d
{

//--------------------------------------------------------------------------//
/** Gets the preimage of an argument y.                                        */
//--------------------------------------------------------------------------//
  
  public double getInverseValue(double y);
  
}
