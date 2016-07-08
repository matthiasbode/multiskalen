/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.ma;

import applications.gbp.BiPartition;
import java.util.ArrayList;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class Codierer {

    private Codierer() {
    }

    public static <V> BiPartitionIndividual getChromosome(ArrayList<V> nodes, BiPartition<V> partition) {
        BiPartitionIndividual chrom = new BiPartitionIndividual(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            V v = nodes.get(i);
            if (partition.getA().contains(v)) {
                chrom.set(i,0);
            } else if (partition.getB().contains(v)) {
                chrom.set(i,1);
            }
        }
        return chrom;
    }

    public static <V> BiPartition<V> getBipartition(WeightedDirectedGraph<V, Double> graph, ArrayList<V> nodes, BiPartitionIndividual chrom) {
        BiPartition<V> result = new BiPartition<V>(graph);
        for (int i = 0; i < nodes.size(); i++) {
            V v = nodes.get(i);
            Integer set = chrom.get(i);
            if (set == 0) {
                result.add2A(v);
            } else if (set == 1) {
                result.add2B(v);
            }
        }
        return result;
    }
}
