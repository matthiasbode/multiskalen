package bijava.geometry.dim3;

//==============================================================================//
/** Interface fuer allgemeine Regionen im 3d.
 *
 *  @author     Leibniz Universitaet Hannover
 *  @author     Institut fuer Bauinformatik
 *  @author     berthold
 *  @version    2008-11-21
 */
//==============================================================================//
public interface Region3d {
    
//------------------------------------------------------------------------------//
/** Ermittelt, ob ein Punkt innerhalb der Region ist oder nicht.
 * 
 *  @param p    3d-Punkt
 *  @return     <code>true</code>, falls der Punkt <code>p</code> innerhalb der
 *              Region liegt; sonst <code>false</code>.                         */
//------------------------------------------------------------------------------//
    public boolean isInside(Point3d p);
    
}
