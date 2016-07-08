package bijava.structs;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
//==========================================================================//
/**
  * Die Klasse "ArrayNd" bietet Methoden und Variablen zur Verwaltung 
  * eines n-dimensionalen Feldes von generischen Objekten, dessen Dimensionierung erst 
  * zur Laufzeit festgelegt werden braucht. 
  * @version 0.1
  * @author Dipl.-Inform. Tino Schonert
  * @author Institute of Computer Science in Civil Engineering
  * @author University of Hannover
  */
public class ArrayNd<E> {
    
    //eindimensionales Feld v
    private E[] array;
    //Groesse der einzelnen Dimensionen 
    private int[] dim;
            
//--------------------------------------------------------------------------//   
/** Erzeugt ein n-dimensionales Feld von generischen Objekten. Die Dimensionen werden durch 
 *  Angabe eines int-Feldes festgelegt.  
 *  @deprecated
 *  @param dimension     int-Feld welches die Array-Dimensionen enth&auml;lt */
//--------------------------------------------------------------------------//
    public ArrayNd(int[] dimension) {
        dim = new int[dimension.length];
        System.arraycopy(dimension, 0, this.dim, 0, dimension.length); //        this.dim=dimension;
        int n=1;
        //bestimme die Groesse des eindimensionalen Arrays 
        for(int i=0;i<this.dim.length;i++) n*=this.dim[i];  //n=dim1*dim2*dim3*...*dimN
        array = (E[]) new Object[n];       //Feld der Groesse n erzeugen
    }
    
    /** die bessere Variante */
    public ArrayNd(int[] dimension, E[] a) {
        dim = new int[dimension.length];
        System.arraycopy(dimension, 0, this.dim, 0, dimension.length); //        this.dim=dimension;
        int n=1;
        //bestimme die Groesse des eindimensionalen Arrays 
        for(int i=0;i<this.dim.length;i++) n*=this.dim[i];  //n=dim1*dim2*dim3*...*dimN
        array = (E[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), n);
    }
    
    public ArrayNd(int[] dimension,Class<E> clazz) {
        dim = new int[dimension.length];
        System.arraycopy(dimension, 0, this.dim, 0, dimension.length); //        this.dim=dimension;
        int n=1;
        //bestimme die Groesse des eindimensionalen Arrays
        for(int i=0;i<this.dim.length;i++) n*=this.dim[i];  //n=dim1*dim2*dim3*...*dimN
        array=(E[])java.lang.reflect.Array.newInstance(clazz,n);
    }
    
//--------------------------------------------------------------------------//   
/** Setzt ein entsprechendes generisches-Object an die durch die Indizes spezifizierte Position. 
 *
 *  @param index     Indizes des entsprechenden Feldelementes               
 *  @param cell      zu setzendes Object                                    */    
//--------------------------------------------------------------------------//        
       public void setElement(int[] index, E cell){
        int k=0;
        for(int j=dim.length-1;j>=0;j--) {
            int n=1;
            for( int i=0;i<j;i++) n*=dim[i]; 
                 k+=n*index[j];
        }
        array[k]=cell;
     }
       
//--------------------------------------------------------------------------//   
/** Gibt die Feldgr&ouml;&szlig;e der spezifizierten Dimension zurueck.
 *
 *  @param index     Index der Dimension                
 *  @return          Feldgr&ouml;&szlig;e in der angegeben Dimension          */    
//--------------------------------------------------------------------------//        
    public int getSizeOfDimension(int i){
        if (i<dim.length) return dim[i];
        return 0;
    }
    
//--------------------------------------------------------------------------//   
/** Gibt die Anzahl der Dimensionen zurueck.
 * 
 *  @return         Anzahl Dimensionen         */    
//--------------------------------------------------------------------------//       
    public int getDimension(){
        return dim.length;        
    }
    
    public int[] getDimSize(){
        int [] rvalue=null;
        System.arraycopy(dim, 0, rvalue, 0, dim.length);
        return rvalue;        
    }
    
//--------------------------------------------------------------------------//   
/** Liefert das entsprechendes generische-Object, welches sich an der durch die 
 *  Indizes spezifizierte Position befindet. 
 *
 *  @param index     Indizes des entsprechenden Feldelementes               
 *  @return cell     CACell-Object an der entsprechendes Position            */    
//--------------------------------------------------------------------------//     
    public E getElement(int[] index){
        int k=0;
        for(int j=dim.length-1;j>=0;j--) {
            int n=1;
            for( int i=0;i<j;i++) n*=dim[i]; 
                 k+=n*index[j];
        }
        return array[k];
     }
    
//--------------------------------------------------------------------------//   
/** Konvertiert das n-dimensionale Feld in einen String - wobei Elemente der
 *  ersten Dimension durch ein Leerzeichen voneinander getrennt alle weiteren Dimensionen 
 *  werden durch entprechend viele Zeilenumbrueche getrennt.  */
//--------------------------------------------------------------------------//       
    public String toString() {
        String erg = "";
        for (int k=0;k<array.length;k++) {
            erg+=array[k].toString()+" ";
            int n=1;
            for(int j=0;j<dim.length;j++){
                n*=dim[j];
                if ((k+1)%n==0) erg+="\n";
            }
                   
        }
        
        return erg;
    }
    
//--------------------------------------------------------------------------//   
/** Liefert alle Elemente als Vector zurueck.
 *             
 *  @return          Vector mit allen Zellen            */    
//--------------------------------------------------------------------------//     
   public Vector<E> toVector(){
        Vector<E> e= new Vector<E>();
        for(int i=0;i<array.length;i++) 
            e.add(array[i]);
        return e;
    }
    
//--------------------------------------------------------------------------//   
/** Liefert alle Elemente als eindimensionale Array zurueck.
 *             
 *  @return          eindimensionales Feld            */    
//--------------------------------------------------------------------------//     
    public E[] toArray1d(){
        return array;
    }
    
    /**
     * Returns an enumeration of the elements of this ArrayNd. The 
     * returned <tt>Enumeration</tt> object will generate all items in 
     * this ArrayNd. 
     *
     * @return  an enumeration of the elements of this ArrayNd.
     * @see     Enumeration
     * @see     Iterator
     */
    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int count = 0;
            
            public boolean hasMoreElements() {
                return count < array.length;
            }
            
            public E nextElement() {
                if (count < array.length) {
                    return array[count++];
                }
                return null;
            }
        };
    }
    
    /** Gibt einen Iterator fuer den Durchlauf ueber die Elemente des ArrayNd zurueck. */
    public Iterator iterator() {
        return new ArrayNdIterator();
    }
    /** Innere Klasse fuer den Iterator. Die innere Klasse kann auf die Attribute
     *  der umgebenden Klasse zugreifen. Vorteil: Z.B. genoetigt bei dieser Art der Umsetzung
     *  der automatische Standardkonstruktor.
     */
    private class ArrayNdIterator implements Iterator {
        private int count = 0;
        
        public boolean hasNext() {
            return count < array.length;
        }
        
        public E next() {
            if (count < array.length) {
                return array[count++];
            }  throw new NoSuchElementException();
        }
        
        public void remove(){
            count++;
        }
    }
    
     public static void main(String[] args) {
    String sql0 = "Sylt";
        double[] v = {5., 8., 12., 16., 20., 24., 28., 32.},
                teta = {-90., -70., -50., -30., -10., 10., 30., 50., 70., 90.},
                eta = {0.};
        int[] dimension = {v.length, teta.length, eta.length};
        ArrayNd raster = new ArrayNd(dimension);
     }

}
