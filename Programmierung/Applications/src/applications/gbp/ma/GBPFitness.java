/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import applications.gbp.BiPartition;
import ga.basics.FitnessEvalationFunction;
import java.util.ArrayList;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class GBPFitness<V> implements FitnessEvalationFunction<BiPartitionIndividual> {

    private WeightedDirectedGraph<V, Double> graph;
    private ArrayList<V> nodes;

    public GBPFitness(WeightedDirectedGraph<V, Double> graph, ArrayList<V> nodes) {
        this.graph = graph;
        this.nodes = nodes;
    }

    public double[] computeFitness(BiPartitionIndividual i) {
        BiPartition<V> bipartition = Codierer.getBipartition(graph, nodes, i);
        return new double[]{-bipartition.getTotalExternalCosts()};
    }
}
