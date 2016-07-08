/*
 * DifferentialVectorFunctionNd.java
 *
 * Created on 18. Mai 2006, 12:24
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
public interface DifferentialVectorFunctionNd extends VectorFunctionNd {
    public VectorNd[] getGradient(PointNd p);
    /**Gibt die RichtungsAbleitungen als Funktionen zurueck
     *
     * @return Ableitungen als Funktionen
     * @author berthold
     * @version 13.06.06
     */
    public VectorFunctionNd[] getDerivation();
}
