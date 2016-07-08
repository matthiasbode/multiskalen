package bijava.math.function;

import bijava.geometry.dim3.*;
//==========================================================================//
/** The interface "ScalarFunction3d" provides methods for three dimensional
 *  scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public interface DifferentialScalarFunction3d extends ScalarFunction3d {
//--------------------------------------------------------------------------//
/** Gets the derivation of an argument x.                                   */
//--------------------------------------------------------------------------//
	public Vector3d getGradient (Point3d p);
	
	/**Gibt die Ableitungen als Funktionen zurueck
	 * und die Gradienten in x und y Richtungen
	 * 
	 * @return Ableitungen als Funktionen
	 * @author Pick
	 * @version 14.02.2005
	 */
	 public ScalarFunction3d[] getDerivation();


}
