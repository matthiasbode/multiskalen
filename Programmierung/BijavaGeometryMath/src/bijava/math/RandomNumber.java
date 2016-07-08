package bijava.math;
import java.util.Random;
//==========================================================================//
/** The class "RandomNumber" provides properties and methods of objects for
 *  generators of various distributed random numbers.
 *
 *  <p><strong>Version: </strong> <br><dd>1.0, August 1997</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class RandomNumber extends Random
{
//--------------------------------------------------------------------------//
/** Creates a new random number generator.                                  */
//--------------------------------------------------------------------------//
  public RandomNumber() { super(); }

//--------------------------------------------------------------------------//
/** Generates a evenly distributed double value.                            */
//--------------------------------------------------------------------------//
  public double nextEvenly() { return nextDouble(); }

//--------------------------------------------------------------------------//
/** Generates a gaussian distributed double value.
 *
 *  @param mue   mean value
 *  @param sigma standard deviation                                         */
//--------------------------------------------------------------------------//
  public double nextGaussian (double mue, double sigma)
  { return (sigma * nextGaussian() + mue); }

//--------------------------------------------------------------------------//
/** Generates a logarithmic distributed double value.
 *
 *  @param mue   mean value
 *  @param sigma standard deviation
 *  @param a     displacement                                               */
//--------------------------------------------------------------------------//
  public double nextLogarithmic (double mue, double sigma, double a)
  { return (Math.exp(nextGaussian(mue, sigma * sigma)) + a); }

//--------------------------------------------------------------------------//
/** Generates an exponential distributed double value.
 *
 *  @param lambda intensity                                                 */
//--------------------------------------------------------------------------//
  public double nextExponential (double lambda)
  { return (Math.log(nextDouble()) / lambda); }
  
//--------------------------------------------------------------------------//
/** Generates an extreme value distributed double value by Gumbel.
 *
 *  @param alpha > 0 
 *  @param beta  real                                                       */
//--------------------------------------------------------------------------//
  public double nextGumbelian (double alpha, double beta)
  { return (alpha * (-Math.log(-Math.log(nextDouble())))  + beta); }

//--------------------------------------------------------------------------//
/** Generates an weilbullian distributed double value.
 *
 *  @param c     > 0 
 *  @param alpha > 0 
 *  @param beta  real                                                       */
//--------------------------------------------------------------------------//
  public double nextWeilbullian (double c, double alpha, double beta)
  { return (alpha * Math.pow(nextExponential(1.0),(1/c)) + beta); }
}
