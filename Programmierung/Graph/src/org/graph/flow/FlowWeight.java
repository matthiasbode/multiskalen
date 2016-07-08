/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.flow;

/**
 *
 * @author bode
 */
public class FlowWeight {
    public double capacity;
    public double cost;

    public FlowWeight(double capacity, double cost) {
        this.capacity = capacity;
        this.cost = cost;
    }
}