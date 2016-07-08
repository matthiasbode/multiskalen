/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

/**
 *
 * @author Oliver
 */
public class DoubleEdgeWeight implements EdgeWeight<DoubleEdgeWeight> {
    double d;

    public DoubleEdgeWeight(double d) {
        this.d = d;
    }
    
    
    @Override
    public DoubleEdgeWeight product(DoubleEdgeWeight weight) {
        return new DoubleEdgeWeight(d+weight.d);
    }

    @Override
    public DoubleEdgeWeight product(double scalar) {
         return new DoubleEdgeWeight(d+scalar);
    }

    @Override
    public double doubleValue() {
        return d;
    }

    @Override
    public DoubleEdgeWeight getNullElement() {
        return new DoubleEdgeWeight(Double.POSITIVE_INFINITY);
    }

    @Override
    public DoubleEdgeWeight getEinsElement() {
        return new DoubleEdgeWeight(0.);
    }

    @Override
    public int compareTo(DoubleEdgeWeight o) {
        return Double.compare(d, o.d);
    }
    
    @Override
    public String toString() {
        return String.valueOf(d);
    }
    
}
