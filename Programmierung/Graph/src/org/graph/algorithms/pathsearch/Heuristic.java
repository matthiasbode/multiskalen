package org.graph.algorithms.pathsearch;

/**
 * Heuristic.java ist eine Schnittstelle fuer Heuristiken.
 * @author  yangli<br>
 *          hoecker
 */
public interface Heuristic<E> {

    /**
     * Berechnet einen heuristischen Wert zwischen zwei Knoten.
     * @param start Knoten, fuer den ein heuristischer Wert berechnet werden soll.
     * @param end Bezugsknoten.
     * @return heuristischer Wert.
     */
    public double getValue(E start, E end);
}
