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
public interface ScalarFunctionNd {
    
   /**Funktionswert der Funktion am Punkt p 
    * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
    * @return Funktionswert */
    public double getValue (PointNd p);
    
    public int getDim();
    


}
