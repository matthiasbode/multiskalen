/*
 * DifferentialVectorFunction1d.java
 *
 * Created on 16. Mai 2006, 16:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.function;

import bijava.geometry.dimN.*;

/**
 *
 * @author berthold
 */
public interface DifferentialVectorFunction1d extends VectorFunction1d {
    
//--------------------------------------------------------------------------//
    /** Gets the derivation of an argument x.                                   */
//--------------------------------------------------------------------------//
	  public double[] getGradient(double x);
	  
	  /**Gibt die Ableitung als Funktion zurueck
	   * 
	   * @return Ableitung als Funktion
	   * @author berthold
	   * @version 15.05.2006
	   */
	  public VectorFunction1d getDerivation();
    
}
