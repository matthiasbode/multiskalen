package org.util;

//==========================================================================//
/**
 * Die Klasse <code>Edge</code> ist eine Klasse, die ein geordnetes Paar
 * repraesentiert. Die Klasse <code>Edge</code> beschreibt eine Kante in einem
 * Graphen.
 *
 * Zwei Kanten sind gleich, wenn sie den selben ersten (<code>first</code>) und
 * zweiten (<code>second</code>) Knoten haben.
 *
 * @param <E1>
 * @param <E2>
 */
//==========================================================================//
public  class Pair<E1, E2> {

    protected E1 first;                // Erstes Element des geordneten Paares
    protected E2 second;               // Zweites Element des geordneten Paares

    public Pair() {
    }

    /**
     * Erzeugt ein geordnetes Paar.
     *
     * @param e1 Erstes Element des geordneten Paares
     * @param e2 Zweites Element des geordneten Paares
     */
    public Pair(E1 e1, E2 e2) {
        first = e1;
        second = e2;
    }

    /**
     * Erzeugt ein neues geordnetes Paar und füllt es mit den Elementen des zu
     * übergebenen geordneten Paares. Nur die Referenz wird kopiert, nicht das
     * Element selbst.
     *
     * @param edge Die Referenz auf das geordnete Paar, dessen Referenzen auf
     * die Elemente kopiert werden sollen.
     */
    public Pair(Pair<E1, E2> edge) {
        if (edge == null) {
            throw new NullPointerException();
        }
        first = edge.first;
        second = edge.second;
    }

    /**
     * Gibt das erste Element des geordneten Paares zurück.
     *
     *
     * @return Diese Methode gibt das erste Element des geordneten Paares zurück
     */
    public E1 getFirst() {
        return first;
    }

    /**
     * Gibt das zweite Element des geordneten Paares zurück.
     *
     *
     * @return Diese Methode gibt das zweite Element des geordneten Paares
     * zurück
     */
    public E2 getSecond() {
        return second;
    }

    public boolean containsVertex(Object vertex) {
        return first.equals(vertex) || second.equals(vertex);
    }

    /**
     * Gibt die Transposition des geordneten Paares zurück.
     *
     * @return Gibt die Transposition des geordneten Paares zurück.
     */
    public Pair<E2, E1> transposition() {
        return new Pair<E2, E1>(second, first);
    }

    /**
     * Gibt eine String-Repräsentation des geordneten Paares zurück.
     *
     * <p>
     * <IMG SRC="../images/struct/Pair.toString.gif" vspace=20 hspace=20></p>
     *
     * @return String-Repräsentation des geordneten Paares
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Ueberprueft das uebergebene Object mit diesem auf Gleichheit.
     *
     * @param object Das zu vergleichende Objekt
     * @return Die Methode gibt <tt>true</tt> zurück, falls das übergebene
     * Objekt eine Kante ist und die selben Elemente in der selben Reihenfolge
     * hat
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        Pair edge = (Pair) obj;
        if (!first.equals(edge.first)) {
            return false;
        }
        if (!second.equals(edge.second)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 97 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }
}
