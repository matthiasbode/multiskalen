package fuzzy.fuzzyset;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * DiscretSet.java stellt Eigenschaften und Methoden fuer diskrete Mengen zur
 * Verfuegung.
 *
 * @author Leibniz Universitaet Hannover<br> Institut fuer Bauinformatik
 */
public class DiscreteSet<E> implements Iterable<E> {

    private LinkedBlockingQueue<E> elements;

    /**
     * Erzeugt eine leere diskrete Menge.
     */
    public DiscreteSet() {
        elements = new LinkedBlockingQueue<E>();
    }

    /**
     * Erzeugt eine diskrete Menge mit Elementen einer anderen diskreten Menge.
     *
     * @param set diskrete Menge.
     */
    public DiscreteSet(DiscreteSet<E> set) {
        if (set == null) {
            throw new NullPointerException("The argument 'set' is null");
        }
        elements = new LinkedBlockingQueue<E>(set.elements);
    }

    /**
     * Liefert die Anzahl der Elemente.
     *
     * @return Anzahl der Elemente.
     */
    public int size() {
        return elements.size();
    }

    /**
     * Testet ob ein Objekt in dieser Menge enthalten ist.
     *
     * @param object Objekt.
     * @return <code>true</code> falls das Objekt in dieser Menge enthalten ist.
     */
    public boolean contains(Object object) {
        return elements.contains(object);
    }

    /**
     * Fuegt ein Element zu dieser Menge hinzu.
     *
     * @param e Element.
     */
    public void add(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        if (!elements.contains(element)) {
            try {
                elements.put(element);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Entfernt ein Element aus dieser Menge.
     *
     * @param e Element.
     */
    public void remove(E element) {
        elements.remove(element);
    }

    /**
     * Liefert einen Durchlauf ueber alle Elemente.
     *
     * @return Durchlauf ueber alle Elemente.
     */
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    /**
     * Testet ob eine Menge Teilmenge dieser Menge ist.
     *
     * @param set diskrete Menge.
     * @return <code>true</code> falls die Menge Teilmenge dieser Menge ist.
     */
    public boolean subset(DiscreteSet<E> set) {
        if (set == null || set.elements.size() > elements.size()) {
            return false;
        }
        for (E e : set) {
            if (!elements.contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Testet ob ein Objekt gleich dieser Menge ist.
     *
     * @param object Objekt.
     * @return <code>true</code> falls das Objekt gleich dieser Menge ist.
     */
    public boolean equals(Object object) {
        if (!(object instanceof DiscreteSet)) {
            return false;
        }
        DiscreteSet<E> set = (DiscreteSet<E>) object;
        if (set.size() != size()) {
            return false;
        }
        for (E e : set) {
            if (!elements.contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Liefert den Durchschnitt dieser Menge mit einer anderen.
     *
     * @param diskrete Menge.
     * @return diskrete Menge.
     */
    public DiscreteSet<E> section(DiscreteSet<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        DiscreteSet<E> section = new DiscreteSet<E>();
        for (E e : set) {
            if (elements.contains(e)) {
                section.elements.add(e);
            }
        }
        return section;
    }

    /**
     * Liefert die Vereinigung dieser Menge mit einer anderen.
     *
     * @param diskrete Menge.
     * @return diskrete Menge.
     */
    public DiscreteSet<E> union(DiscreteSet<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        DiscreteSet<E> union = new DiscreteSet<E>(this);
        for (E e : set) {
            union.add(e);
        }
        return union;
    }

    /**
     * Liefert die Differenz dieser Menge mit einer anderen.
     *
     * @param diskrete Menge.
     * @return diskrete Menge.
     */
    public DiscreteSet<E> difference(DiscreteSet<E> set) {
        if (set == null) {
            throw new NullPointerException();
        }
        DiscreteSet<E> difference = new DiscreteSet<E>(this);
        for (E e : set) {
            if (elements.contains(e)) {
                difference.remove(e);
            }
        }
        return difference;
    }

    /**
     * Liefert die Eigenschaften dieser Menge als Zeichenkette.
     *
     * @return Eigenschaften dieser Menge als Zeichenkette.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer("{");
        Iterator<E> it = elements.iterator();
        if (it.hasNext()) {
            buffer.append(it.next());
        }
        for (; it.hasNext();) {
            buffer.append(", ");
            buffer.append(it.next());
        }
        buffer.append("}");
        return buffer.toString();
    }

    public static void main(String[] args) {
        DiscreteSet<Integer> g = new DiscreteSet<Integer>();
        g.add(1);
        g.add(10);
        System.out.println(g);
        System.out.println(g.contains(1));
        System.out.println(g.contains(2));

        DiscreteSet<Integer> f = new DiscreteSet<Integer>();
        f.add(5);
        f.add(10);
        System.out.println(g.section(f));
    }
}