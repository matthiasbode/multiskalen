/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import applications.gbp.BiPartition;
import applications.gbp.KernighanLinHeuristic;
import ga.localSearch.LocalSearch;
import java.util.ArrayList;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class KernighanLinLocalSearch<V> implements LocalSearch<BiPartitionIndividual> {

    
    private WeightedDirectedGraph<V, Double> graph;
    private ArrayList<V> nodes;

    public KernighanLinLocalSearch(WeightedDirectedGraph<V, Double> graph, ArrayList<V> nodes) {
        this.graph = graph;
        this.nodes = nodes;
    }
    @Override
    public BiPartitionIndividual localSearch(BiPartitionIndividual start) {
        BiPartition<V> bipartition = Codierer.getBipartition(graph, nodes, start);
        KernighanLinHeuristic.getKOpt(bipartition);
        return Codierer.getChromosome(nodes, bipartition);
    }
    
}
