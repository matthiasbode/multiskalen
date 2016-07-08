package bijava.geometry;

/**
 * describe a convex Polyhedron
 * @author Peter Milbradt
 * @author Institute of Computational Science in civil engineering
 * @version 1.1
 */

public class ConvexPolyhedron<E extends VectorPoint> extends Polyhedron<E> {
    
    protected ConvexPolyhedron() {
        super();
    }
    
    public ConvexPolyhedron(E p) {
        super(p);
    }
    
    public ConvexPolyhedron(E p, E q) {
        super(p,q);
    }
    
    public ConvexPolyhedron(E[] p) {
        super();
        System.out.println("Erzeugen der Facetten ueber die Konvexe Huelle  der linearer Punkte funktioniert noch nicht");
        // ...
    }
    
    /** Gleichheit der Knoten ohne Orientierung */
    public boolean unorderedEquals(ConvexPolyhedron<E> S) {
        
        if(!(order == S.order))     return false;        // Dimensionen stimmen nicht ueberein
        
        ConvexPolyhedron help = new ConvexPolyhedron(S.nodes);
        // test ob das ConvexPolyhedron aus den gleichen Punkten besteht
        int    permutation  = 0;
        int    k            = 1;
        for (int i=0; i<=order; i++) {
            for (int j=i; j<=order; j++) {
                if( (nodes[i] == help.nodes[j] )){
                    permutation++;
                    k=0;
                } 
            }
            if((k==1)) return false; // ueberhaupt kein Knoten ist gleich
            else k=1;
        }
        if(permutation==order+1) return true;
        else return false;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof ConvexPolyhedron) return equals((ConvexPolyhedron<E>)o);
        return false;
    }
    
    /**
     * This Method compute the coordinates of the barycenter 
     * @return double[] the coordinates of the barycenter
     */
    public double[] getCoordsOfBaryCenter(){
        int dim = nodes[0].dim();
        double[] result = new double[dim];
        for(int j=0;j<nodes.length;j++)
            for(int i=0;i<dim;i++)
                result[i] += nodes[j].getCoord(i);
        
        for(int i=0;i<dim;i++)
            result[i] /= nodes.length;
        return result;
    }

}
