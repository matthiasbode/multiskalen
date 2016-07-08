/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.basics;

/**
 *
 * @author bode
 */
public interface FitnessValue<F extends Comparable> extends Comparable<F>{
    public double doubleValue();
}
