/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author M. Bode
 * @param <E> Datentyp der Elemente der Menge
 */
public class SimpleLinkedSet<E> extends LinkedHashSet<E>{

    public SimpleLinkedSet(Collection<? extends E> c) {
        super(c);
    }

    public SimpleLinkedSet() {
    }

    
    /**
     * Gibt die Durchschnitsmenge mit einer anderen Menge zurÃ¼ck.
     *
     * @param set andere Menge
     * @return Durchschnitt als neue Menge
     */
    public  SimpleLinkedSet<E> section(Set<E> set) {
        SimpleLinkedSet<E> result = new SimpleLinkedSet<E>();
        for (E element : set) {
            if (this.contains(element)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Gibt die Vereinigung mit einer anderen Menge zurÃ¼ck.
     *
     * @param set andere Menge
     * @return die Vereinigung als neue Menge
     */
    public SimpleLinkedSet<E> union(Set<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        SimpleLinkedSet<E> union = new SimpleLinkedSet<E>();
        union.addAll(this);
        for (E e : set) {
            union.add(e);
        }
        return union;
    }

    /**
     * Gibt die Differenz mit einer anderen Menge zurÃ¼ck.
     *
     * @param set andere Menge
     * @return die Differenz als neue Menge
     */
    public SimpleLinkedSet<E> difference(Set<E> set) {
        SimpleLinkedSet<E> result = new SimpleLinkedSet<E>();
        result.addAll(this);
        for (E element : set) {
            if (result.contains(element)) {
                result.remove(element);
            }
        }

        return result;
    }

    /**
     * Testet, ob dieses Set Teilmenge eines weiteren Sets ist.
     *
     * @param set anderes SimpleSet.
     * @return
     * <code>true</code> wenn this Teilmenge von set ist.
     */
    public boolean isSubsetOf(Set<E> set) {
        if (set == null || set.size() < this.size()) {
            return false;
        }
        for (E e : this) {
            if (!set.contains(e)) {
                return false;
            }
        }
        return true;
    }
}
