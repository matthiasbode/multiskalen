package bijava.geometry;

import java.util.*;

/**
 * Class describe a Polyhedron in a linear Vectorspace
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @version 1.1
 */

public class Polyhedron<E extends VectorPoint> {
    protected int order= -1;	// Dimension of the polyhedron
    protected int spaceDim = 0;
    protected E[] nodes = null;
    protected Polyhedron<E>[] facets = null;
    
    protected static final double EPSILON=1.E-7;
    
    protected Polyhedron() {}
    
    public Polyhedron(E p) {
        nodes = (E[]) new VectorPoint[1];
        nodes[0] = p;
        facets = null ;
        order = 0;
        spaceDim = p.dim();
    }
    
    public Polyhedron(E p, E q) {
        if(p.dim()!=q.dim()){
            System.out.println("No Polyhedron constructed: incompatible points p and q");
        } else {
            nodes = (E[]) Arrays.copyOf(new Object[2], 1, this.nodes.getClass());
            nodes[0] = p; nodes[0] = q;
            facets = (Polyhedron<E>[]) new Polyhedron[2];
            facets[0]=new Polyhedron(p);  facets[1]=new Polyhedron(q);
            order = 1;
            spaceDim = p.dim();
        }
    }
    
    public Polyhedron(Polyhedron<E>[] facets) {
        // test ob die Facetten auch wirklich ein Polyeder bilden wird nicht durchgefuehrt
        this.facets = facets;
        Vector<E> nodes = new Vector<E>();
        for(int i=0;i<facets.length;i++){
            E[] p = facets[i].getNodes();
            for (int j=0;j<p.length;j++){
                if(!nodes.contains(p[j])) nodes.add(p[j]);
            }
        }
        
        this.nodes = nodes.toArray(this.nodes);
        
        spaceDim = this.nodes[0].dim();
        order = AlgGeometrie.getLinearHullDimension(this.nodes);
    }
    
    
    public boolean contains(E point) {
        System.out.println("Wir koennen noch nicht Bestimmen ob ein linearer Punkt in einem Polyhedron liegt !!");
        return false;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Polyhedron) return equals((Polyhedron)o);
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.nodes != null ? this.nodes.hashCode() : 0);
        return hash;
    }
    
    public boolean equals(Polyhedron p){
        if(!(order == p.order))     return false;
        if(!(spaceDim == p.spaceDim))     return false;
        if(order==-1) return true;
        if(order==0) return (nodes[0]==p.nodes[0]);
        if(order==1) return (((nodes[0]==p.nodes[0])&&(nodes[1]==p.nodes[1]))
                                || ((nodes[0]==p.nodes[1])&&(nodes[1]==p.nodes[0])));
        return equalFacets(p);
    }
    
    /**
     * Dimension des Elementes
     * @Beispiel
     * 0 (Punkt) 1 (Kante) 2 (Flaeche) ...
     * @since 1.0
     * @version 1.0
     * @return int
     */
    public int getElementDimension() {
        return order;
    }
    
    
    public int getSpaceDimension(){
        return spaceDim;
    }
    
    /**
     * Knoten des Polyeders
     * @return Linear[]
     */
    public E[] getNodes() {
//        E[] pp = (E[]) new Object[nodes.length];
//        for (int i = 0; i < nodes.length; i++) {
//            pp[i] = nodes[i];
//        }
//        return pp;
        return nodes.clone();
    }
    
    /**
     * gibt die Facetten des Polyeders zurueck
     * @return Polyhedron[]
     */
    public Polyhedron[] getFacets() {
        Polyhedron[] pp = new Polyhedron[facets.length];
        for (int i = 0; i < facets.length; i++) {
            pp[i] = facets[i];
        }
        return pp;
    }
    
    private boolean hasEdge(E p){
        for (int i=0; i<nodes.length;i++){
            if (p==nodes[i]) return true;
        }
        return false;
    }
    
    public Polyhedron[] getFacets(E point) {
        Vector<Polyhedron> tmp = new Vector<Polyhedron>();
        for (int i = 0; i < facets.length; i++)
            if (facets[i].hasEdge(point))
                if(!tmp.contains(facets[i])) tmp.add(facets[i]);
        Polyhedron[] result = new Polyhedron[tmp.size()];
        for(int i=0; i<result.length;i++)
            result[i] = tmp.get(i);
        return result;
    }
    
    @Override
    public String toString() {
        String erg = "";
        for (int i = 0; i < nodes.length; i++) {
            erg += ("P" + i + "(" + nodes[i] + ")\n");
        }
        return erg;
    }

    protected boolean equalNodes(Polyhedron<E> p) {
        E[] pp = p.getNodes();
        if(pp.length != nodes.length) return false;
        boolean w = false;
        
        for(int i=0; i<nodes.length; i++){
            w=false;
            for(int j=0; j<pp.length; j++)
                if(nodes[i]==pp[j]) w=true;
            if(w==false) return false;
        }
        
        for(int j=0; j<pp.length; j++){
            w=false;
            for(int i=0; i<nodes.length;i++)
                if(nodes[i]==pp[j]) w=true;
            if(w==false) return false;
        }
        
        return true;
    }

    protected boolean equalFacets(Polyhedron p) {
        
        if(order == 0){
            
            return (nodes[0]==p.nodes[0]);
            
        } else {
            
            Polyhedron[] f = p.getFacets();
            
            if(f.length != facets.length) return false;
            
            boolean w = false;
            
            for(int i=0; i<facets.length; i++){
                w=false;
                for(int j=0; j<f.length; j++)
                    if(facets[i].equals(f[j])) w=true;
                if(w==false) return false;
            }
            
            for(int i=0; i<f.length; i++){
                w=false;
                for(int j=0; j<facets.length; j++)
                    if(f[i].equals(facets[j])) w=true;
                if(w==false) return false;
            }
        }
        
        return true;
        
    }
    
}
