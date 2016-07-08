package bijava.math.function;

import bijava.geometry.dimN.*;
//==========================================================================//
/** The interface "ScalarFunctionNd" provides methods for N dimensional
 *  scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public interface DifferentialScalarFunctionNd extends ScalarFunctionNd {
      
//--------------------------------------------------------------------------//
/** Gets the derivation of an argument p                                  
    * @param p Punkt, an dem die Ableitung berechnet werden soll
    * @return Feld der Ableitungsvektoren */
    public VectorNd getGradient(PointNd p);
   
    /**Gibt die Ableitungen als Funktionen zurueck
     * und die Gradienten in x und y Richtungen
     *
     * @return Ableitungen als Funktionen
     * @author Pick
     * @version 14.02.2005
     */
    public ScalarFunctionNd[] getDerivation();
}
