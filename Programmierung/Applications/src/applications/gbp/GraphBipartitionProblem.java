/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp;

import org.graph.weighted.WeightedDirectedGraph;
 




/**
 * Beim GraphBipartitionProblem soll ein Graph in zwei Teilgraphen 
 * geteilt werden, wobei beiden entstehenden Knotenmengen gleich groß
 * sind und die Anzahl an Kanten, die beide neu entstandenen Teilgraphen
 * verbinden soll minimiert werden.
 * @author bode
 */
public class GraphBipartitionProblem {
    
    public static <V> BiPartition<V> getPartitions(WeightedDirectedGraph<V, Double> graph) {
        BiPartition<V> result = KernighanLinHeuristic.getOpt(graph);
        return result;
    }

    public static void main(String[] args) {
        WeightedDirectedGraph<Integer, Double> buildRandomGraph = Builder.buildRandomGraph(8, 1, 1);
//        ExportToYED.exportToGraphML(buildRandomGraph, "/home/bode/Desktop/test.graphml", true);
        BiPartition<Integer> partitions = getPartitions(buildRandomGraph);
        System.out.println(partitions);
    }
   
}
