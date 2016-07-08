/*
 * Created on 14.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bijava.math.function;

/**
 * @author pick
 * @version 14.02.2005
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface DifferentialScalarFunction1d extends ScalarFunction1d
{
//	--------------------------------------------------------------------------//
	/** Gets the derivation of an argument x.                                   */
//	--------------------------------------------------------------------------//
	  public double getGradient(double x);
	  
	  /**Gibt die Ableitung als Funktion zurueck
	   * 
	   * @return Ableitung als Funktion
	   * @author Pick
	   * @version 14.02.2005
	   */
	  public ScalarFunction1d getDerivation();

}
