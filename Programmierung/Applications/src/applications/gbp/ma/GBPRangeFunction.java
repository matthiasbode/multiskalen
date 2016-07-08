/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import applications.gbp.BiPartition;
import ga.individuals.Individual;
import ga.fittnessLandscapeAnalysis.fdplots.RangeFunction;
import java.util.ArrayList;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class GBPRangeFunction<V> implements RangeFunction<BiPartitionIndividual> {

    private BiPartitionIndividual optimum;
    private WeightedDirectedGraph<V, Double> graph;
    private ArrayList<V> nodes;
    

    public GBPRangeFunction(WeightedDirectedGraph<V, Double> graph, ArrayList<V> nodes) {
        this.graph = graph;
        this.nodes = nodes;
    }

    @Override
    public void setOptimum(BiPartitionIndividual optimum) {
        this.optimum = optimum;
    }
       
    
    @Override
    public double getValue(BiPartitionIndividual individual) {
        BiPartition<V> bipartition = Codierer.getBipartition(graph, nodes, individual);
        return bipartition.getTotalExternalCosts() + optimum.getFitness();
    }
}
