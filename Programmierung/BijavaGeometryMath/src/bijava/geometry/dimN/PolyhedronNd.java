package bijava.geometry.dimN;

import java.util.*;
import bijava.geometry.*;

/**
 * class for a n-dimensional polytop
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 * @version 1.0
 */

public class PolyhedronNd /*extends Polyhedron */ {
    
    protected static final double EPSILON=1.E-7;
    
    protected int order=-1;	// Dimension of the polyhedron
    protected int spaceDim=0;
    protected PointNd[] nodes=null;
    protected PolyhedronNd[] facets=null;
    
    public PolyhedronNd(PointNd p) {
        nodes = new PointNd[1];
        nodes[0]=p;
        order = 0;
        spaceDim = p.dim();
    }
    
    protected PolyhedronNd() {
    }
    
    public PolyhedronNd(PolyhedronNd[] facets) {
        this.facets = facets;
        Vector<PointNd> nodes = new Vector<PointNd>();
        for(int i=0;i<facets.length;i++){
            PointNd[] p = facets[i].getNodes();
            for (int j=0;j<p.length;j++){
                if(!nodes.contains(p[j])) nodes.add(p[j]);
            }
        }
        
        this.nodes = nodes.toArray(new PointNd[nodes.size()]);
        
        spaceDim = this.nodes[0].dim();
        order = AlgGeometrie.getLinearHullDimension(this.nodes);
    }
    
    /** berechnet die Koordinaten des Barycenters */
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
    
    
    public boolean contains(PointNd point) {
        System.out.println("Wir koennen noch nicht Bestimmen ob ein linearer Punkt in einem Polyhedron liegt !!");
        return false;
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
        if (order < 2) return order;
        System.out.println(" In Polyhedron funktioniert die Bestimmung der Dimension noch nicht !!");
        return -96;
    }
    
    /**
	 * Methode zum Erfragen der Dimension des Raumes, in der das geometrische
	 * Element sich befindet.
	 *
	 *  @return Dimension des Raumes
	 */
    public int getDimension() {
        return spaceDim;
    }
    
    public int getNumberofNodes(){
        return nodes.length;
    }
    
    /**
     * Methode zum Abfragen eines Knotens an der Stelle i des geometrischen Elementes.
     *
     * @param i Index des Punktes, der zurueckgegeben werden soll.
     * @return Punkt des geometrischen Elementes.
     */
    public PointNd getNode(int i) {
        return (PointNd) nodes[i];
    }
    
    /**
     * Knoten der konvexen Zelle
     * @return Linear[]
     */
    public PointNd[] getNodes() {
        return (PointNd[]) nodes;
    }
    
    public PolyhedronNd[] getFacets() {
        return (PolyhedronNd[]) facets;
    }
    
    public PolyhedronNd[] getFacetsOfOrder(int grad) {
        if(getElementDimension()<grad) return null;
        if(getElementDimension()==grad) return new PolyhedronNd[]{this};
        PolyhedronNd[] facets=getFacets();
        if(facets!=null) {
            if(facets[0].getElementDimension()==grad) return facets;
            boolean weiter=true;
            
            Vector<PolyhedronNd> alle = new Vector<PolyhedronNd>();
            for(int i=0;i<facets.length;i++)
                alle.add(facets[i]);
            
            
            while(weiter) {
                Vector<PolyhedronNd> tmp = new Vector<PolyhedronNd>();
                for(int i=0;i<alle.size();i++) {
                    PolyhedronNd feld[]=alle.get(i).getFacets();
                    
                    if(feld[0].order==grad) weiter=false;
                    for(int j=0;j<feld.length;j++) {
                        if(!tmp.contains(feld[j])) tmp.add(feld[j]);
                    }
                }
                alle=new Vector<PolyhedronNd>();
                for(int i=0;i<tmp.size();i++)
                    alle.add(tmp.get(i));
                
            }
            PolyhedronNd[] erg=new PolyhedronNd[alle.size()];
            for(int i=0;i<erg.length;i++)
                erg[i]=alle.get(i);
            return erg;
        }
        return null;   
    }
    
    private boolean hasEdge(PointNd p){
        for (int i=0; i<nodes.length;i++){
            if (p==nodes[i]) return true;
        }
        return false;
    }
    
    public PolyhedronNd[] getFacets(PointNd point) {
        Vector<PolyhedronNd> tmp = new Vector<PolyhedronNd>();
        for (int i = 0; i < facets.length; i++)
            if (((PolyhedronNd)facets[i]).hasEdge(point))
                if(!tmp.contains(facets[i])) tmp.add((PolyhedronNd)facets[i]);
        PolyhedronNd[] result = new PolyhedronNd[tmp.size()];
        for(int i=0; i<result.length;i++)
            result[i] = tmp.get(i);
        return result;
    }

    public String toString() {
        String erg = "";
        for (int i = 0; i < nodes.length; i++) {
            PointNd p = (PointNd) nodes[i];
            erg += ("P" + i + "(" + p + ")\n");
        }
        return erg;
    }
}
