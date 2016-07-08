package bijava.geometry;

import java.util.Iterator;
import java.util.ArrayList;

/**
 *
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @version 0.1
 */
public class EuclideanComplex<E extends Polyhedron> {

    protected ArrayList<E> elements = new ArrayList<E>();
    int order = -1;
    int spaceDim = -1;

    protected EuclideanComplex() {
    }

    /** Creates a new instance of EuclideanComplex in a space of dimension spaceDim */
    public EuclideanComplex(int spaceDim) {
        this.spaceDim = spaceDim;
    }

    public EuclideanComplex(E p) {
        this.spaceDim = p.getSpaceDimension();
        elements.add(p);
    }

    public boolean add(E p) {
        if (p.getSpaceDimension() != spaceDim) {
            System.out.println("different SpaceDimensions between EuklideanComplex and Polyhedron");
            return false;
        }
        if (elements.size() == 0) {
            elements.add(p);
            this.order = Math.max(order, p.getElementDimension());
            return true;
        }
        // testen ob das Polyhedron P in den euklidischen Komplex passt
        // der durchschnitt mit P ist leer oder ein unterPolyeder
        elements.add(p);
        this.order = Math.max(order, p.getElementDimension());
        return true;
    }

    public boolean remove(E p) {
        return elements.remove(p);
    }

    /** Gibt einen Iterator fuer den Durchlauf ueber die Elemente zurueck. */
    public Iterator<E> iterator() {
        return elements.iterator();
    }
}
