package bijava.geometry.dim3;

//==============================================================================//
/** AbstractRegion3d definiert die Methoden <code>union</code>,
 *  <code>section</code> und <code>complement</code> fuer 3d-Regionen.
 *  Dabei werden jeweils abstrakte 3d-Regionen zurueckgegeben.<br>
 *  Die Methoden stehen jeweils als Objekt- und Klassenmethode zur Verfuegung.
 *
 *  @author     Leibniz Universitaet Hannover
 *  @author     Institut fuer Bauinformatik
 *  @author     berthold
 *  @version    2008-11-21                                                      */
//==============================================================================//
public abstract class AbstractRegion3d implements Region3d {

    // Methode, die von den abgeleiteten Klassen implementiert werden muss
    public abstract boolean isInside(Point3d p);
    
    // private Methode, um nicht in einer Endlosschleife zu enden
    public boolean this_isInside(Point3d p) { return isInside(p); }
    
//------------------------------------------------------------------------------//
/** Gibt eine AbstractRegion3d zurueck, die die Vereinigung zweier Regionen
 *  darstellt. Dabei wird auf die <code>isInside</code>-Methode aus den
 *  jeweiligen Subklassen zurueckgegriffen.
 *
 *  @param r    3d-Region, mit der die Vereinigung gebildet wird
 *  @return     Vereinigung der beiden Regionen                                 */
//------------------------------------------------------------------------------//
    public AbstractRegion3d union(final Region3d r) {
        return new AbstractRegion3d() {

            @Override
            public boolean isInside(Point3d p) {
                return this_isInside(p) || r.isInside(p);
            }
        };
    }

//------------------------------------------------------------------------------//
/** Gibt eine AbstractRegion3d zurueck, die den Durchschnitt zweier Regionen
 *  darstellt. Dabei wird auf die <code>isInside</code>-Methode aus den
 *  jeweiligen Subklassen zurueckgegriffen.
 *
 *  @param r    3d-Region, mit der der Durchschnitt gebildet wird
 *  @return     Durchschnitt der beiden Regionen                                */
//------------------------------------------------------------------------------//
    public AbstractRegion3d section(final Region3d r) {
        return new AbstractRegion3d() {

            @Override
            public boolean isInside(Point3d p) {
                return this_isInside(p) && r.isInside(p);
            }
        };
    }
    
//------------------------------------------------------------------------------//
/** Gibt eine AbstractRegion3d zurueck, die das Komplement der Region
 *  darstellt. Dabei wird auf die <code>isInside</code>-Methode aus der
 *  Subklasse zurueckgegriffen.
 *
 *  @return     Komplement der Region                                           */
//------------------------------------------------------------------------------//
    public AbstractRegion3d complement() {
        return new AbstractRegion3d() {

            @Override
            public boolean isInside(Point3d p) {
                return !this_isInside(p);
            }
        };
    }
    
//------------------------------------------------------------------------------//
/** Gibt eine AbstractRegion3d zurueck, die die Vereinigung zweier Regionen
 *  darstellt. Dabei wird auf die <code>isInside</code>-Methode aus den
 *  jeweiligen Subklassen zurueckgegriffen.
 *
 *  @param r1, r2   3d-Regionen, zwischen denen die Vereinigung gebildet wird.
 *  @return         Vereinigung der beiden Regionen                             */
//------------------------------------------------------------------------------//
    public static AbstractRegion3d union(final Region3d r1, final Region3d r2) {
        return new AbstractRegion3d() {

            @Override
            public boolean isInside(Point3d p) {
                return r1.isInside(p) || r2.isInside(p);
            }
        };
    }
    
//------------------------------------------------------------------------------//
/** Gibt eine AbstractRegion3d zurueck, die den Durchschnitt zweier Regionen
 *  darstellt. Dabei wird auf die <code>isInside</code>-Methode aus den
 *  jeweiligen Subklassen zurueckgegriffen.
 *
 *  @param r1,r2    3d-Regionen, zwischen denen der Durchschnitt gebildet wird.
 *  @return         Durchschnitt der beiden Regionen                            */
//------------------------------------------------------------------------------//
    public static AbstractRegion3d section(final Region3d r1, final Region3d r2) {
        return new AbstractRegion3d() {

            @Override
            public boolean isInside(Point3d p) {
                return r1.isInside(p) && r2.isInside(p);
            }
        };
    }
    
//------------------------------------------------------------------------------//
/** Gibt eine AbstractRegion3d zurueck, die das Komplement der Region
 *  darstellt. Dabei wird auf die <code>isInside</code>-Methode aus der
 *  Subklasse zurueckgegriffen.
 *
 *  @param r    Region, deren Komplement gebildet werden soll.
 *  @return     Komplement der Region                                           */
//------------------------------------------------------------------------------//
    public static AbstractRegion3d complement(final Region3d r) {
        return new AbstractRegion3d() {

            @Override
            public boolean isInside(Point3d p) {
                return !r.isInside(p);
            }
        };
    }

}
