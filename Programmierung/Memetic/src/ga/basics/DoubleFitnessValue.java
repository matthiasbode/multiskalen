/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.basics;

/**
 *
 * @author bode
 */
public class DoubleFitnessValue implements FitnessValue<DoubleFitnessValue> {

    double fitness;

    public DoubleFitnessValue(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public double doubleValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(DoubleFitnessValue o) {
        return Double.compare(fitness, o.fitness);
    }
}
