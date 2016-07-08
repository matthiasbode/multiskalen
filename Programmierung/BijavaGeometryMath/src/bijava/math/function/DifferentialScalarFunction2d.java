package bijava.math.function;

import bijava.geometry.dim2.*;

//==========================================================================//
/** The interface "ScalarFunction2d" provides methods for two dimensional
 *  scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public interface DifferentialScalarFunction2d extends ScalarFunction2d {

/** Gets the derivation of an argument x.                                   */
//--------------------------------------------------------------------------//
	public Vector2d getGradient (Point2d p);
	
	/**Gibt die Ableitungen als Funktionen zurueck
	 * und die Gradienten in x und y Richtungen
	   * 
	   * @return Ableitungen als Funktionen
	   * @author Pick
	   * @version 14.02.2005
	   */
	  public ScalarFunction2d[] getDerivation();

      public ScalarFunction2d getTotalDerivation();
}
