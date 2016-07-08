/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

/**
 * Die Klasse FieldElement beschreibt ein Element eines Körpers. Ein Körper
 * besteht aus einer Menge K von Elementen, so wie die Instanzen dieses
 * Interfaces und zwei zweistelligen Verknüpfungen + und * die üblicherweise als
 * Addition und Multiplikation.
 *
 * @author bode
 * @param <E>
 */
public interface FieldElement<E extends FieldElement> extends Comparable<FieldElement>, Cloneable {

    /**
     * Gibt das Ergebnis der Addition zweier Elemente eines Körpers zurück.
     *
     * @param b
     * @return
     */
    public E add(E b);

    public E mult(E b);

    public E sub(E b);

    public E div(E b);

    public E mult(double s);

    public E negate();

    public double doubleValue();

    public long longValue();

    public boolean isGreaterThan(E f);

    public boolean isLowerThan(E f);

    public E clone();

}
