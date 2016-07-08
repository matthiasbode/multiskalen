package bijava.math.function;

import bijava.geometry.dimN.*;
//==========================================================================//
/** The interface "VectorFunction1d" provides methods for one dimensional
 *  vector valued functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public interface VectorFunction1d {
    
    //--------------------------------------------------------------------------//
    //  FUNKTIONSWERT                                                           //
    //--------------------------------------------------------------------------//
    /** Liefert die Werte zu einem Argument x.                                   */
    //--------------------------------------------------------------------------//
    public VectorNd getValue(double x);
    
}
