package bijava.geometry.dimN;

import bijava.geometry.VectorPoint;


public abstract class TupleNd<T extends TupleNd> implements VectorPoint<T> {
    
    public double[]     x;      // Koordinaten des Tupels

    
    /** Konstruieren und initialisieren eines 1-Tupels mit (0.0). */
    public TupleNd() {
        x = new double[1];
    }
    
    /** Konstruieren und initialisieren eines N-Tuples mit (0.0,...,0.0).
     *@param N Dimension des Tupels */
    public TupleNd( int N ) {
        x = new double[N];
    }
    
    /**Konstruieren und initialisieren eines N-Tuples mit den Werten x.
     * Das uebergebene Feld wird kopiert.
     *@param x zu initialisierende Werte des Tupels */
    public TupleNd( double[] x) {
        this.x=new double[x.length];
        for(int i=0;i<x.length;i++) {
            this.x[i]=x[i];
        }
    }
    
    /**Konstruieren und initialisieren eines 2-Tuples mit den Werten x1, x2.
     * Das uebergebene Feld wird ohne Kopie abgelegt.
     *@param x1 erster zu initialisierender Wert des Tupels
     *@param x2 zweiter zu initialisierender Wert des Tupels */
    public TupleNd( double x1, double x2 ) {
        this.x = new double[] {x1, x2};
    }
    
    /**Konstruieren und initialisieren eines 3-Tuples mit den Werten x1, x2, x3.
     * Das uebergebene Feld wird ohne Kopie abgelegt.
     *@param x1 erster zu initialisierender Wert des Tupels
     *@param x2 zweiter zu initialisierender Wert des Tupels
     *@param x3 dritter zu initialisierender Wert des Tupels*/
    public TupleNd( double x1, double x2, double x3 ) {
        this.x = new double[] {x1, x2, x3};
    }
    
    /**Kopierkonstruktor eines n-Tupels.
     *@param t zu kopierendes Tupel. */
    public TupleNd( TupleNd t ) {
        x = new double[t.x.length];
        System.arraycopy(t.x, 0, x, 0, x.length);
    }
    
    @Override
    public int dim() {
        return x.length;
    }
    
    @Override
    public double getCoord(int i) {
        return x[i];
    }
    
    public double getTupleNdlement(int i) {
        return x[i];
    }
    
    @Override
    public double[] getCoords() {
        return x;
    }
    
    public int getSize() {
        return x.length;
    }
    
    @Override
    public void setCoord(int i,double value) {
        x[i]=value;
    }
    
    public void setTupleNdlement(int i,double value) {
        x[i]=value;
    }
    
    
    public final void set(double[] d){
        this.x = d;
    }
    
    /** Abfrage auf Gleicheit.
     *@param zu vergleichednes Tupel
     *return true wenn die Tupel gelich sind */
    public boolean epsilonTupleNdquals( TupleNd t ,double eps) {
        //    	System.out.println("bin in epsilonTupleNdquals");
        for (int i=0; i<x.length; i++)
            if (Math.abs(x[i]-t.x[i])>eps)
                return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.x != null ? this.x.hashCode() : 0);
        return hash;
    }
    
    /** Abfrage auf Gleicheit.
     *@param zu vergleichednes Tupel
     *@return true wenn die Tupel gleich sind */
    @Override
    public boolean equals( Object tt ) {
        if(! (tt instanceof TupleNd)) return false;
        TupleNd t=(TupleNd) tt;
        if (t.dim()!=dim()) return false;
        for (int i=0; i<x.length; i++)
            if (x[i]!=t.x[i])
                return false;
        
        return true;
    }
    
    /** Abfrage auf Gleicheit.
     *@param zu vergleichednes Tupel
     *@return true wenn die Tupel gleich sind */
    public boolean equals( TupleNd t ) {
        if (t.dim()!=dim()) return false;
        for (int i=0; i<x.length; i++)
            if (x[i]!=t.x[i])
                return false;
        
        return true;
    }
    
    public final void set(TupleNd t) {
        x = new double[t.dim()];
        System.arraycopy(t.x, 0, x, 0, t.x.length);
    }
    
    public void scale(double s) {
        for(int i=0;i<x.length;i++)
            x[i]*=s;
    }
    
    @Override
    public String toString() {
        String s = super.toString()+" : ";
        for (int i=0; i<x.length-1; i++)
            s+=(x[i]+",");
        s+=(x[x.length-1]);
        return s;
    }
    
}


