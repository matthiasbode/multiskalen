/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp;

import ga.Parameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * @author bode
 */
public class Builder {

    public static WeightedDirectedGraph<Integer, Double> buildRandomGraph(int numberOfNodes, double densityMean, double densityDeviation) {
        Random r = Parameters.getRandom();
        WeightedDirectedGraph<Integer, Double> result = new DefaultWeightedDirectedGraph<Integer, Double>();

        ArrayList<Integer> nodes = new ArrayList<Integer>();
        for (int i = 0; i < numberOfNodes; i++) {
            nodes.add(i);
            result.addVertex(i);
        }
        
        Collections.shuffle(nodes, r);

        for (int i = 0; i < nodes.size(); i++) {
            Integer a = nodes.get(i);
            int numberOfNeighbours = (int) Math.rint(getNextGaussian(r, densityMean, densityDeviation));
            if (i > 0) {
                Integer b = nodes.get((int) (r.nextDouble() * (i - 1)));
                while (b.equals(a)) {
                    b = nodes.get((int) (r.nextDouble() * (i - 1)));
                }
                result.addEdge(a,b ,1.); //r.nextDouble()*20
            }

            for (int k = 1; k < numberOfNeighbours; k++) {
                Integer b = nodes.get((int) (r.nextDouble() * (nodes.size() - 1)));
                while (b.equals(a)) {
                    b = nodes.get((int) (r.nextDouble() * (nodes.size() - 1)));
                }
                result.addEdge(a, b,1.);//r.nextDouble()*20);
            }
        }
        return result;
    }

    public static double getNextGaussian(Random r, double mean, double deviation) {
        return r.nextGaussian() * deviation + mean;
    }

    public static void main(String[] args) {
        WeightedDirectedGraph<Integer, Double> buildRandomGraph = Builder.buildRandomGraph(20, 1, 0);
//        ExportToYED.exportToGraphML(buildRandomGraph, "/home/bode/Desktop/test.graphml", true);
        WeightedDirectedGraph<Integer, Double> buildRandomGraph2 = Builder.buildRandomGraph(40, 2, 2);
//        ExportToYED.exportToGraphML(buildRandomGraph2, "/home/bode/Desktop/test2.graphml", true);
    }
}
