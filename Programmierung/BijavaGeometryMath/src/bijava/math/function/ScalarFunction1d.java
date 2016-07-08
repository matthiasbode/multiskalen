package bijava.math.function;
//==========================================================================//

import java.io.Serializable;

/**
 * The interface "ScalarFunction1d" provides methods for one dimensional
 *  scalar functions.
 * @version 1.1
 * @author Dr.-Ing. habil. Peter Milbradt
 * @author Dr.-Ing. Martin Rose
 * @author Institute of Computer Science in Civil Engineering
 * @author University of Hannover
 */
//==========================================================================//
public interface ScalarFunction1d extends Serializable
{

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.                                        */
//--------------------------------------------------------------------------//
  public double getValue(double x);



//--------------------------------------------------------------------------//
/** Sets the periodicity of the scalar function
 *
 *  @param periodic If the function should be a periodic function this value
 *                  must be <code>true</code>, otherwise <code>false</code>.*/
//--------------------------------------------------------------------------//
/*  public void setPeriodic(boolean periodic);
*/
//--------------------------------------------------------------------------//
/** Tests if this scalar function is periodic.                              */
//--------------------------------------------------------------------------//
 /* public boolean isPeriodic();*/
}
