package bijava.geometry;

import bijava.geometry.dimN.PointNd;
import bijava.vecmath.*;
import bijava.math.*;
import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

/**
 * Die Klasse Simplex stellt grundlegende Funktionen fuer ein Simplex bereit.
 *
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 * @version 1.1
 */

public class Simplex<E extends VectorPoint> extends ConvexPolyhedron<E>{
    
    /**
     * Erzeugt ein Simplex der Dimension n aus k+1 Punkten. Die Punkte muessen
     * linear unabhaengig sein.
     *
     * @param points ein Feld von k+1 Punkten der Dimesnion n
     */
    public Simplex(E[] points) {
        super(points);
        if (points.length != 0){
            spaceDim = points[0].dim();
            order = points.length-1;
//            nodes = (E[]) new Object[points.length];
            nodes = points.clone();
            for(int i=0;i<points.length;i++) nodes[i] = points[i];
        }
        // geht nicht
        if (!this.linearUnabhaengig())
            throw new NullPointerException("no Simplex");

// folgende Zeile ist notwendig, wenn Simplexe gekastet auf ConvexPolyhedron verwendet werden soll
//        facets = getFacets();
    }
    
    /**
     * Erzeugt ein Simplex der Ordnung 0 aus einem Punkt.
     * @param point Punkten der Dimesnion n
     */
    public Simplex(E point) {
        super(point);
    }
    /**
     * Erzeugt ein Simplex der Ordnung 1 aus zwei Punkten p und q.
     * @param p Punkten der Dimesnion n
     */
    public Simplex(E p, E q) {
        super(p, q);
    }
    
    /** .. */
    public Simplex( Simplex<E> s, E o) {
        if (s.spaceDim == o.dim()){
            this.spaceDim = s.spaceDim;
            this.order = s.order+1;
            
            this.nodes = (E[]) new VectorPoint[order+1];
            for(int i=0;i<s.order+1;i++) this.nodes[i] = s.nodes[i];
            this.nodes[this.order] = o;
            
            this.facets = (Simplex<E>[]) new Simplex[order+1];
            this.facets[0] = s;
            if(this.order==1){
                this.facets[1]= new Simplex<E>(o);
            } else {   
            Simplex<E>[] subsimplex = s.getFacets();
            for (int i = 0; i < subsimplex.length; i++) {
                System.out.println(subsimplex[i].order);
                this.facets[i+1]= new Simplex<E>(subsimplex[i].getTwin(), o);
            }
            }
        }
        
        // geht nicht
        if (!this.linearUnabhaengig())
            throw new NullPointerException("no Simplex");
        
// folgende Zeile ist eventuell notwendig, wenn Simplexe gekastet auf ConvexPolyhedron verwendet werden soll
//        facets = getFacets();   
    }
    
    /** return the subsimplexes of this simplex */
    // effiziente implementierung ueber Matrixdarstellung
    @Override
    public Simplex<E>[] getFacets() {
        
        if( order==0 ) return null;
        
        Simplex<E>[] subs=(Simplex<E>[])new Simplex[order+1];
        
        if( order==1 ) {
            subs[0] = new Simplex<E>( nodes[0] );
            subs[1] = new Simplex<E>( nodes[1] );
            return subs;
        }
        
        for (int i=0; i<order+1; i++) {
            E[] subpoints= (E[]) new VectorPoint[order];
            int count, x;
            
            if(i%2 == 0){
                count=0;
                for (int a=i; a<i+order; a++){
                    subpoints[count++]=nodes[a%(order+1)];
                }
            } else{
                count=0;
                for (int a=i+order+1; a>i+1; a--){
                    subpoints[count++]=nodes[a%(order+1)];
                }
            }
            subs[i]=new Simplex<E>(subpoints);
        }
        return subs;
    }
    
    private Simplex<E>[] getFacetsInEfficient() {
        Simplex<E> subs[] = (Simplex<E>[])new Simplex[ order+1 ];
        
        if( order==0 ) {
            return null;
        }
        
        if( order==1 ) {
            subs[0] = new Simplex<E>( nodes[0] );
            subs[1] = new Simplex<E>( nodes[1] );
            return subs;
        }
        
        if( order==2 ) {
            E punkte[] = (E[])new VectorPoint[2];
            
            punkte[0] = nodes[0];
            punkte[1] = nodes[1];
            subs[0] = new Simplex<E>(punkte );
            
            punkte[0] = nodes[1];
            punkte[1] = nodes[2];
            subs[1] = new Simplex<E>( punkte );
            
            punkte[0] = nodes[2];
            punkte[1] = nodes[0];
            subs[2] = new Simplex<E>(punkte );
        } else {
            // ineffiziente Implementierung
            // nehme die erste Facette und von dieser alle Facetten
            // bilde aus den Zwillinge und dem ueberbleibenden Punkte die weiteren Facetten
            Simplex<E> subsubs[];
            E[] punkte = (E[])new VectorPoint[order];
            for(int i=0;i<order;i++){
                punkte[i] = nodes[i];
            }
            subs[0] = new Simplex<E>(punkte);
            
            subsubs = subs[0].getFacets();
            
            for(int i=1;i<order+1;i++)
                subs[i] = new Simplex<E>( subsubs[i-1].getTwin(),nodes[order] );
            
        }
        return subs;
    }
    
    /** return the edges of this simplex */
    public Simplex<E>[] getEdges() {
        if (order==0) return null;
        
        if(order==1) return ((Simplex<E>[]) new Simplex[]{this});
        
        int i,a,count=0;
        int k=Function.fac(order+1)/(Function.fac(order-1)*2);
        
        Simplex<E>[] edges = (Simplex<E>[])new Simplex[k];
        E[] subpoints;
        
        for (i=0; i<nodes.length-1; i++) {
            for (a=i+1; a<nodes.length; a++) {
                subpoints=(E[]) new VectorPoint[2];
                
                subpoints[0]=nodes[i];
                subpoints[1]=nodes[a];
                
                edges[count++]=new Simplex<E>(subpoints);
            }
        }
        return edges;
    }
    
    public boolean isFacet(Simplex<E> S){
        if (S.order != order-1) return false;
        Simplex<E> [] stmp = getFacets();
        for (int i=0; i<stmp.length; i++)
            if (stmp[i].equals(S)) return true;
        return false;
    }
    
    public Simplex<E>[] getSubSimplexesOfOrder(int order){
        Simplex<E>[] result=null;
        if (order<0) return null;
        if (order==0) {
            result = (Simplex<E>[]) new Simplex[nodes.length];
            for(int i=0; i<nodes.length; i++)
                result[i]=new Simplex<E>(nodes[i]);
        }
        if (order == 1) return getEdges();
        if (order == this.order-1) return getFacets();
        
        return result;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Simplex) return equals((Simplex)o);
        return false;
    }
    
    public boolean equals(Simplex<E> S){
        return orderedEquals(S);
    }
    
    /** Gleichheit der Knoten ohne Orientierung */
    public boolean unorderedEquals(Simplex<E> S) {
        
        if(!(order == S.order))     return false;        // Dimensionen stimmen nicht ueberein
        if(!(spaceDim == S.spaceDim))     return false;
        
        Simplex<E> help = new Simplex<E>(S.nodes);
        // test ob das Simplex aus den gleichen Punkten besteht
        int    permutation  = 0;
        int    k            = 1;
        for (int i=0; i<=order; i++) {
            for (int j=i; j<=order; j++) {
                if( (nodes[i] == help.nodes[j] )){
                    permutation++;
                    k=0;
                }
            }
            if((k==1)) return false; // Ueberhaupt kein Knoten ist gleich
            else k=1;
        }
        if(permutation==order+1) return true;
        else return false;
    }
    
    /** Gleichheit der Knoten mit Orientierung */
    public boolean orderedEquals(Simplex<E> S) {
        
        if(!(order == S.order))     return false;        // Dimensionen stimmen nicht ueberein
        if(!(spaceDim == S.spaceDim))     return false;
        
        Simplex<E> help = new Simplex<E>(S.nodes);
        // test ob das Simplex aus den gleichen Punkten besteht
        int    permutation   = 0;
        int    k            = 1;
        for (int i=0; i<=order; i++) {
            for (int j=i; (j<=order)&&(k==1); j++) {
                if( (nodes[i] == help.nodes[j] )){
                    E tmp = help.nodes[i];
                    help.nodes[i] = help.nodes[j];
                    help.nodes[j] = tmp;
                    if(i!=j)
                        permutation++;
                    k=0;
                }
            }
            if((k==1)) return false; // ueberhaupt kein Knoten ist gleich
            else k=1;
        }
        if( (permutation%2) == 0 ) return true; //gerader Permutation sind die Simplexe gleich....
        else return false;
    }
    
    public Simplex<E> getTwin() {
        
        if(order==0)
            return new Simplex<E>(nodes[0]);
        
        E[] necken = (E[])new VectorPoint[order+1];
        
        necken[0] = nodes[1];
        necken[1] = nodes[0];
        for(int i=2;i<order+1;i++)
            necken[i] = nodes[i];
        
        return new Simplex<E>(necken);
    }
    
    
    /**
     * Rueckgabe einer Repraesentation des Simplex als Text
     *
     * @return String des Simplex
     */
    @Override
    public String toString() {
        String erg = "";
        erg += "Simplex der Ordnung " + order + ":\n";
        for (int i = 0; i < nodes.length; i++)
            erg += "Punkt " + i + ":" + nodes[i] + "\n";
        return erg;
    }
    
    
    /**
     * Methode zum Erfragen, ob ein Punkt in der Ebene des Simplexes liegt.
     *
     * @param point Punkt fuer welchen Ueberprueft werden soll, ob er in der Ebene
     * des Simplex liegt.
     * @return <code>true</code>, wenn der Punkt in der Ebene des Simplexes
     * liegt, <code>false</code>, wenn nicht.
     */
    public boolean isInSubSpace(E point) {
        if (order < spaceDim) {
            E[] tmp2 = (E[])new VectorPoint[nodes.length + 1];
            for (int i = 0; i < nodes.length; i++) {
                tmp2[i] = nodes[i];
            }
            tmp2[nodes.length] = point;
            if (AlgGeometrie.getLinearHullDimension(nodes) == AlgGeometrie.getLinearHullDimension(tmp2)) {
                return true;
            } else
                return false;
        } else
            return false;
    }
    
    /**
     * Methode zum Erfragen, ob ein Simplexe im selben Unterraum liegen.
     *
     * @param simplex Simplexe fuer die ueberprueft werden soll, ob sie
     * in der gleichen Ebene liegen.
     * @return <code>true</code>, wenn der Punkt in der Ebene des Simplexes
     * liegt, <code>false</code>, wenn nicht.
     */
    public boolean isInSubSpace(Simplex<E> simplex) {
        E[] tmp = simplex.getNodes();
        boolean erg = true;
        for (int i = 0; i < tmp.length; i++) {
            erg = erg && isInSubSpace(tmp[i]);
        }
        return erg;
    }
    
    /**
     * Berechnet die Koordinaten des Zentrums der euklidischen Umkugel des Simplex 
     * und entspricht im zweidimensionalen Raum dem Mittelpunkt des Kreises,
     * auf dessen Rand die Punkte (Tupel) liegen.
     * von kugeln kann man eigentlich nur sprechen wenn der Punkt auch eine Metrik hat
     *
     * @return Koordinaten des Zentrum der Umkugel, null, wenn kein Zentrum exisitiert
     */
    public double[] getCoordsOfCircumCircleCenter() {
        
        double[] d = new double[spaceDim];
        switch(order){
            case 0:
                for (int j = 0; j < spaceDim; j++)
                    d[j]=nodes[0].getCoord(j);
                break;
            case 1:
                for (int j = 0; j < spaceDim; j++)
                    d[j]=0.5*(nodes[0].getCoord(j) * nodes[1].getCoord(j));
                break;
            default:
                if (spaceDim != order)
                    System.out.println("Fehler in VecMath.center: Raum-Dimension und Ordnung des Simplex sind nicht gleich.");
                
                GMatrix A = new GMatrix(spaceDim, spaceDim);
                GVector b = new GVector(spaceDim);
                
                // Aufbau des Gleichungssystems
                for (int i = 0; i < spaceDim; i++) {
                    for (int j = 0; j < spaceDim; j++) {
                        A.setElement(i, j, 2.0 * (nodes[i].getCoord(j) - nodes[i + 1].getCoord(j)));
                        b.setElement(i, b.getElement(i) + (nodes[i].getCoord(j) * nodes[i].getCoord(j) - nodes[i + 1].getCoord(j) * nodes[i + 1].getCoord(j)));
                    }
                }
                GVector  x = new GVector(spaceDim);
                int k = A.LUD(A, x);
                x.LUDBackSolve(A, b, x);
                
                for (int i = 0; i < spaceDim; i++) {
                    d[i] = x.getElement(i);
                }
        }
        return d;
    }
    
    private boolean linearUnabhaengig() {
        int i,a;
        DMatrix m=new DMatrix(spaceDim,order+1);
        VectorPoint po=nodes[0];
        for (i=0; i<order+1; i++) {
            for (a=0; a<spaceDim;a++) {
                if (i==order)
                    m.setItem(a,i,0.);
                else
                    m.setItem(a,i,(nodes[i+1].getCoord(a)-po.getCoord(a)));
            }
        }
        DMatrix erg=LGS.gauss(m);
        
        // falls erg mehrere Spalten hat, sind Punkte nicht lin Unabhaengig
        if (erg.getCols()>1)
            return false;
        else
            return true;
    }

    
    /** test-main methode */
    public static void main( String args[] ) {
        double punkt1[] = {1.,0.,0.};
        double punkt2[] = {0.,0.,0.};
        double punkt3[] = {0.,1.,0.};
        double punkt4[] = {0.,0.,1.};
        
        Simplex<PointNd> s = new Simplex<PointNd>(new Simplex<PointNd>(new PointNd[] {new PointNd(punkt1),new PointNd(punkt3),new PointNd(punkt4)}), new PointNd(punkt2));
        
        System.out.println(s);
        Simplex<PointNd>[] subs = s.getFacetsInEfficient();
        
        for(int i=0; i<subs.length; i++){
            System.out.println(subs[i]);
        }
        
        subs = s.getFacets();
        
        for(int i=0; i<subs.length; i++){
            System.out.println(subs[i]);
        }
    }
}
