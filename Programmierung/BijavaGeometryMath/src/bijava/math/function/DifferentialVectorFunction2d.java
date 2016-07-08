/*
 * DifferentialVectorFunction2d.java
 *
 * Created on 16. Mai 2006, 16:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.function;

import bijava.geometry.dim2.*;
import bijava.vecmath.*;

/**
 *
 * @author berthold
 */
public interface DifferentialVectorFunction2d extends VectorFunction2d {
    
//--------------------------------------------------------------------------//
    /** Gets the derivation of an argument p.                                   */
//--------------------------------------------------------------------------//
	  public Vector2d[] getGradient(Point2d p);
	  
	  /**Gibt die Ableitung als Funktion zurueck
	   * 
	   * @return Ableitung als Funktion
	   * @author berthold
	   * @version 15.05.2006
	   */
	  public VectorFunction2d[] getDerivation();
    
}