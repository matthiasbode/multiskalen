/*
 * DifferentialVectorFunction3d.java
 *
 * Created on 16. Mai 2006, 16:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.function;

import bijava.geometry.dimN.*;
import bijava.geometry.dim3.*;

/**
 *
 * @author berthold
 */
public interface DifferentialVectorFunction3d extends VectorFunction3d {
    
//--------------------------------------------------------------------------//
    /** Gets the derivation of an argument p                                   */
//--------------------------------------------------------------------------//
	  public Vector3d[] getGradient(Point3d p);
	  
	  /**Gibt die Ableitung als Funktion zurueck
	   * 
	   * @return Ableitung als Funktion
	   * @author berthold
	   * @version 15.05.2006
	   */
	  public VectorFunction3d[] getDerivation();
    
}
