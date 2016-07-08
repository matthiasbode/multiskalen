/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp;

 

import java.util.LinkedHashMap;
import util.SimpleLinkedSet;
import org.graph.weighted.WeightedDirectedGraph;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class BiPartition<V> {

    private final WeightedDirectedGraph<V, Double> graph;
    private SimpleLinkedSet<V> a;
    private SimpleLinkedSet<V> b;
    private LinkedHashMap<V, PartitioningCost> costs;

    public BiPartition(WeightedDirectedGraph<V, Double> graph) {
        this.graph = graph;
        this.a = new SimpleLinkedSet<V>();
        this.b = new SimpleLinkedSet<V>();
    }

    public BiPartition(WeightedDirectedGraph<V, Double> graph, SimpleLinkedSet<V> a, SimpleLinkedSet<V> b) {
        this.graph = graph;
        this.a = a;
        this.b = b;
    }

    public BiPartition(BiPartition<V> bi) {
        this.graph = bi.graph;
        this.a = new SimpleLinkedSet<V>(bi.getA());
        this.b = new SimpleLinkedSet<V>(bi.getB());
    }

    public SimpleLinkedSet<V> getA() {
        return a;
    }

    public SimpleLinkedSet<V> getB() {
        return b;
    }

    public void add2A(V v) {
        a.add(v);
    }

    public void add2B(V v) {
        b.add(v);
    }

    public WeightedDirectedGraph<V, Double> getGraph() {
        return graph;
    }

    public LinkedHashMap<V, PartitioningCost> getPartitioningCosts() {
        if (costs == null) {
            updatePartitioningCosts();
        }
        return costs;
    }
    
    public double getT(){
        int counter = 0;
        for (V ai : a) {
            for (V bi : b) {
                if(this.getGraph().containsEdge(new Pair<V, V>(ai, bi))){
                    counter++;
                }
                if(this.getGraph().containsEdge(new Pair<V, V>(bi, ai))){
                    counter++;
                }
            }
        }
        return counter;
    }

    public LinkedHashMap<V, PartitioningCost> updatePartitioningCosts() {
        LinkedHashMap<V, PartitioningCost> result = new LinkedHashMap<V, PartitioningCost>();
        for (V v : graph.vertexSet()) {
            double internalcost = 0;
            double externalcost = 0;
            SimpleLinkedSet<V> set = a.contains(v) ? getA() : getB();
            for (V neighbour : graph.getSuccessors(v)) {
                if (set.contains(neighbour)) {
                    internalcost += graph.getEdgeWeight(v, neighbour);
                } else {
                    externalcost += graph.getEdgeWeight(v, neighbour);
                }
            }
            for (V neighbour : graph.getPredecessors(v)) {
                if (set.contains(neighbour)) {
                    internalcost += graph.getEdgeWeight(neighbour, v);
                } else {
                    externalcost += graph.getEdgeWeight(neighbour, v);
                }
            }
            result.put(v, new PartitioningCost(internalcost, externalcost));
        }
        this.costs = result;
        return result;
    }
    
    public double getTotalExternalCosts(){
        updatePartitioningCosts();
        double total = 0;
        for (V v : a) {
            total += this.costs.get(v).externalCost;
        }
        return total;
    }

    @Override
    public String toString() {
        return "BiPartition{" + "a=" + a + ", b=" + b + '}';
    }
    
    

    /**
     * Klasse die interne und externe Kosten verwaltet
     */
    public static class PartitioningCost {

        private double internalCost;
        private double externalCost;

        public PartitioningCost(double internalCost, double externalCost) {
            this.internalCost = internalCost;
            this.externalCost = externalCost;
        }

        public double getDifference() {
            return externalCost - internalCost;
        }

        public double getExternalCost() {
            return externalCost;
        }

        public double getInternalCost() {
            return internalCost;
        }
        
        
    }
}