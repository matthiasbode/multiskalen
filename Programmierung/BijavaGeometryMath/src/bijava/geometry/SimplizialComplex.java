package bijava.geometry;
import java.util.Iterator;

/**
 * 
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @version 0.1
 * @deprecated durch den generischen EuclideanComplex
 */
public class SimplizialComplex  extends EuclideanComplex{
    
    /** Creates a new instance of EuclideanComplex in a space of dimension spaceDim */
    
    public SimplizialComplex(int spaceDim) {
        super();
        this.spaceDim=spaceDim;
    }
    
    public SimplizialComplex(Simplex p) {
        super(p);
    }
    
    public boolean add(Simplex p){
        if (p.getSpaceDimension()!= spaceDim ){
            System.out.println("different SpaceDimensions between SimplizialComplex and Simplex");
            return false;
        }
        if (elements.size() == 0){
            elements.add(p);
            return true;
        }
        // testen ob das simplex P in den euklidischen Komplex passt
        // der durchschnitt mit P ist leer oder ein untersimplex
        // TODO
        elements.add(p);
        return true;
    }
    
    /** Gibt einen Iterator fuer den Durchlauf ueber die Elemente zurueck. */
    public Iterator iterator(){
        return new SCIterator();
    }
    
    
    class SCIterator implements Iterator{
        Iterator iterator;
        public SCIterator(){
            iterator=elements.iterator();
        }
        
        public boolean hasNext(){
            return iterator.hasNext();
        }
        
        public Simplex next(){
            return (Simplex)iterator.next();
        }
        public void remove(){iterator.remove();}
    }
    
}
