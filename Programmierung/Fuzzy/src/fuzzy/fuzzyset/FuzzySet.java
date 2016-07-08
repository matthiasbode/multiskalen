/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.fuzzyset;

/**
 * Dieses Interface schreibt Methoden zur Modellierung
 * einer Unscharfen Fuzzy-Menge vor. 
 * @author Matthias
 */
public interface FuzzySet<E> {
    /**
     * Gib den Grad der Zugehörigkeit für ein Element x an.
     * @param x Element, dessen Zugehörigkeitsgrad zu dieser Menge zurückgegeben werden soll.
     * @return Zugehörigkeitsgrad des Elementes x.
     */
    public double getMembership(E x);
    /**
     * Gibt den Durschnitt dieser Fuzzymenge mit der Menge s als neue 
     * Fuzzy-Menge zurück.
     * @param s die Fuzzy-Menge, mit der verschnitten werden soll.
     * @return der Durchschnitt beider Mengen als neue Fuzzy-Menge
     */
    public FuzzySet<E> section(FuzzySet<E> s);
        /**
     * Gibt die Vereinigung dieser Fuzzymenge mit der Menge s als neue 
     * Fuzzy-Menge zurück.
     * @param s die Fuzzy-Menge, mit der vereinigt werden soll.
     * @return die Vereinigung beider Mengen als neue Fuzzy-Menge
     */
    public FuzzySet<E> union(FuzzySet<E> s);
}
