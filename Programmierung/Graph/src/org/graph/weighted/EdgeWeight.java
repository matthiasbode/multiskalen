package org.graph.weighted;

/**
 *
 * @author rinke
 */
public interface EdgeWeight<E extends EdgeWeight> extends Comparable<E>{
    /**
     * 
     * @param weight
     * @return 
     */
    public E product(E weight);

    
    /**
     * Notwendig um A*-Algorithmus einsetzen zu koennen.
     * @param scalar
     */
    public E product(double scalar);
    
    
    /**
     * 
     * @return 
     */
    public double doubleValue();

    
    /**
     * 
     * @return 
     */
    public E getNullElement();
 
    
    /**
     * 
     * @return 
     */
    public E getEinsElement();
}
