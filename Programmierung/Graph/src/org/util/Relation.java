package org.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;


//==========================================================================//
/**
 * Die Klasse Relation bietetet Methoden eines Objektes für binäre Relationen.
 * Eine binäre Relation ist eine Menge von geordneten Paaren.
 * Daher hat eine <tt>Relation</tt>  die selben Methoden wie eine <tt>Menge</tt>.
 * Zusätzlich zeichnet eine Relation noch Methoden für die Transposition einer
 * Relation und das Produkt zweier Relationen aus.
 *
 * Wie eine Menge kann eine Relation extensional durch Angabe der geordneten
 * Paare, beispielsweise R = {(2,18), (1,6), (3,18), (1,18), (1,9)} oder
 * intensional, beispielsweise durch R = {(x,y) &isin; N x N | 5x <= y},
 * angegeben werden.
 */
//==========================================================================//
public class Relation<E1, E2> extends DiscreteSet<Pair<E1, E2>> {
    

    /** Erzeugt eine leere Relation.
     *
     */
    public Relation() {
        super();
    }

    
    /** Erzeugt eine leere Relation und füllt sie mit den geordneten Paaren der
     * übergebenen Relation.
     * <p>
     *
     * Nur die geordneten Paare mit den Referenz zu ihren Elementen werden kopiert.
     *
     *  @param relation Die Referenz zu diesem Objekt wird kopiert.
     *  @throws         Die Methode wirft eine <tt>NullPointerException</tt>,
     *                  falls die Relation null ist.
     */
    public Relation(Relation<E1, E2> relation) {
        super();
        if (relation == null) {
            throw new NullPointerException("The parameter 'relation' is null!");
        }
        for (Iterator<Pair<E1, E2>> i = relation.iterator(); i.hasNext();) {
            super.add(new Pair<E1, E2>(i.next()));
        }
    }

    
    /** Erzeugt eine neue Relation und füllt sie mit den
     *  geordneten Paaren des übergebenen Sets.
     *
     *  @param relation Die Referenzen des übergebenen SimpleSets werden als
     *                  geordnete Paare kopiert.
     *  @throws          Die Methode wirft eine <tt>NullPointerException</tt>,
     *                  falls das übergebene SimpleSet null ist.
     */
    public Relation(Collection<Pair<E1, E2>> relation) {
        super();
        if (relation == null) {
            throw new NullPointerException("The parameter 'relation' is null!");
        }
        for (Iterator<Pair<E1, E2>> i = relation.iterator(); i.hasNext();) {
            super.add(new Pair<E1, E2>(i.next()));
        }
    }

    
    /** Gibt <code>true</code> zurück, falls ein geordnetes Paar mit
     *  <code>first</code> als erstes und <code>second</code> als zweites
     *  Element in dieser Relation enthalten sind.
     *
     *  @param first  erstes Element des geordneten Paares
     *  @param second zweites Element des geordneten Paares
     *  @throws       Diese Methode wirft eine <tt>NullPointerException</tt>,
     *                falls eines der übergebenen Elemente null ist
     *  @return       Diese Methode gibt <code>true</code> zurück,
     *                falls die Relation das geordnete Paar enthält.
     */
    public boolean contains(E1 first, E2 second) {
        return super.contains(new Pair<E1, E2>(first, second));
    }


    /** Fügt das übergebene geordnete Paar der Relation hinzu,
     *  falls es nicht bereits Teil der Relation ist.
     *
     *  @param first  erstes Element des geordneten Paares
     *  @param second zweites Element des geordneten Paares
     *  @throws       Diese Methode wirft eine <tt>NullPointerException</tt>,
     *                falls eines der übergebenen Elemente null ist
     *  @return       Gibt <code>true</code> zurück, falls das
     *                geordnete Paar hinzugefügt wurde.
     */
    public boolean add(E1 first, E2 second) {
        return super.add(new Pair<E1, E2>(first, second));
    }


    /** Entfernt das übergebene geordnete Paar, falls es Bestandteil der
     * Relation ist.
     *
     *
     *  @param first  erstes Element des geordneten Paares
     *  @param second zweites Element des geordneten Paares
     *  @return       Gibt <tt>true</tt> zurück,
     *                falls das geordnete Paar entfernt wurde.
     */
    public boolean remove(E1 first, E2 second) {
        return super.remove(new Pair<E1, E2>(first, second));
    }

    
    /** Gibt die Transponierte dieser Relation zurück.
     *
     *  @return Die Transponierte der Relation.
     */
    public Relation<E2, E1> transposition() {
        Relation<E2, E1> transposition = new Relation<E2, E1>();
        for (Iterator<Pair<E1, E2>> i = super.iterator(); i.hasNext();) {
            transposition.add(i.next().transposition());
        }
        return transposition;
    }


    /** Gibt den Durchschnitt dieser Relation mit einer anderen Relation zurück.
     *
     *  @param relation Die Relation, mit der der Durchschnitt gebildet werden
     *                  soll.
     *                  
     *  @return         Der Durschnitt als Relation
     */
    @Override
    public Relation<E1, E2> section(DiscreteSet<Pair<E1, E2>> relation) {
        return new Relation<E1, E2>(super.section(relation));
    }


    /** Gibt dir Vereinigung mit einer anderen Relation zurück.
     *
     *  @param relation Die Relation, mit der die Vereinigung gebildet werden
     *                  soll
     *  @return         Die Vereinigung als Relation
     */
    @Override
    public Relation<E1, E2> union(DiscreteSet<Pair<E1, E2>> relation) {
        return new Relation<E1, E2>(super.union(relation));
    }


    /** Gibt die Differenz mit einer anderen Relation zurück.
     *
     *  @param relation Die Relation, mit der die Differenz gebildet werden soll
     *  @return         Die Differenz als neue Relation
     */
    @Override
    public Relation<E1, E2> difference(DiscreteSet<Pair<E1, E2>> relation) {
        return new Relation<E1, E2>(super.difference(relation));
    }


    /** Gibt die Verkettung der Relation mit einer anderen Relation an.
     *
     *  @param relation Die Relation, mit der verkettet werden soll.
     *  @return         Die Verkettung als neue Relation.
     */   
    public <E3> Relation<E1, E3> product(Relation<E2, E3> relation) {
        /*
         * Neue Relation anlegen
         */
        Relation<E1, E3> product = new Relation<E1, E3>();

        /*
         * Iterator über diese Relation
         */
        for (Iterator<Pair<E1, E2>> i = iterator(); i.hasNext();) {
            Pair<E1, E2> pair1 = i.next();
            /*Iterator über die übergebene Relation*/
            for (Iterator<Pair<E2, E3>> j = relation.iterator(); j.hasNext();) {
                Pair<E2, E3> pair2 = j.next();
                /*
                 * Falls der das zweite Element der Ersten Relation
                 * gleich dem ersten Elemente der zweiten Relation ist.
                 */
                if (pair1.getSecond().equals(pair2.getFirst())) {
                    /*
                     * Füge zur neuen Relation ein Paar hinzu, dessen
                     * erstes Element das erste Element dieser Relation ist
                     * und dessen zweites Element das zweite Element der
                     * übergebenen Relation ist.
                     */

                    product.add(pair1.getFirst(), pair2.getSecond());
                }
            }
        }
        return product;
    }

    
    @Override
    public String toString() {
        String msg = "Relation: \n";
        msg += super.toString();
        return msg;
    }
}
