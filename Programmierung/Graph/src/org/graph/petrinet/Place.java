package org.graph.petrinet;

/**
 * Eine Stelle (Place) ist ein Container fuer Marken (Token) eines Petrinetzes.
 * Je nach der Art des verwendeten Petrinetzes, repraesentiert eine Stelle
 * eine Bedingung oder einen Zustand bzw. eine Menge von Bedingungen oder Zustaenden.
 * Als Attribut besitzt eine Stelle 
 * ein Objekt, welches fuer Identifizierung und Zeichenkettenausgabe verwendet wird,
 * eine Liste(TreeSet) ihrer Tokens und eine Kapazitaet (maximale Anzahl an aktiven Token). 
 * Inaktive Token benoetigen keine Kapazitaet.
 *  <p><strong>Version: </strong> <br><dd>1.0, November 2006</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Prof. Dr.-Ing. Markus Koenig, Dipl.-Ing. Felix Hofmann</dd></p>   
 * @author  Markus Koenig, Felix Hofmann
 * @version 1.0
 */
public class Place implements Comparable {    
    private static int counter = 0;
    
    private final int id;
    
    
    /**
     * Objekt der Stelle
     */
    protected Object obj;  
    
    
    /**
     * Erzeugt eine Stelle mit dem uebergebenen Objekt
     */
    public Place(Object obj) {
        this.obj = obj;
        this.id = counter++;
    }
    

    /**
     * Vergleicht diese Stelle mit einer anderen Stelle.
     * Wenn das uebergebene Objekt nicht vom Typ Place ist, wird eine ClassCastException geworfen.
     * Wenn das Objekt dieser Stelle das Interface Comparable implementiert, wird die
     * Methode compareTo des Objektes fuer den Vergleich verwendet. 
     * Sonst wird der HashCode der Stellen miteinander verglichen.
     * @param obj Stelle die mit diese Stelle verglichen werden soll
     * @return  0 - wenn die beiden Stellen gleich sind
     *         -1 - wenn diese Stelle kleiner als die uebergebene Stelle ist
     *         +1 - wenn diese Stelle groesser als die uebergebene Stelle ist
     */
    @Override
    public int compareTo(Object obj) {
        if (!(obj instanceof Place)) {
            throw new ClassCastException("no place");
        }
        Place place = (Place) obj;
        if (this.obj instanceof Comparable) {
            return ((Comparable) this.obj).compareTo(place.obj);
        }
        
        if (this.obj.hashCode() <  place.obj.hashCode())
            return -1;
        if (this.obj.hashCode() >  place.obj.hashCode())
            return  1;
        return 0;
    }

   
    /**
     * Prueft ob diese Stelle gleich der uebergebenen Stelle ist.
     * Wenn das uebergebene Objekt nicht vom Typ Place ist, wird <tt>false</tt> zurueckgegeben.
     * Die Gleichheit wird mit Hilfe der Methode compareTo dieser Klasse geprueft.
     * Wenn diese Methode eine 0 zurueckgibt, wird <tt>true</tt> zurueckgegeben, sonst <tt>false</tt>.
     * @param obj Stelle die mit diese Stelle auf Gleichheit geprueft werden soll
     * @return <tt>true</tt> wenn die beiden Stellen gleich sind, sonst <tt>false</tt>
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Place)) {
            return false;
        }
       
        int comp = this.compareTo(obj);
        if (comp == 0)
            return true;
        return false;
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.id;
        return hash;
    }
    
    
    /**
     * Liefert das Objekt der Stelle zurueck.
     * @return Objekt der Stelle
     */
    public Object getObject() {
        return this.obj;
    }
    

    public void setObject(Object obj) {
        this.obj = obj;
    }
    
    
    /**
     * Liefert eine Beschreibung dieser Stelle in Form einer Zeichenkette zurueck.
     * Die Darstellung entspricht der Zeichenkettendarstellung des Objektes der Stelle.
     * @return Zeichenkettendarstellung der Stelle
     */
    @Override
    public String toString() {
        return obj.toString();
    }
}
