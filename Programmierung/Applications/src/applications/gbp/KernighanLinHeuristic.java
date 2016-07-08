/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp;

import applications.gbp.BiPartition.PartitioningCost;
import ga.Parameters;
 
import java.util.*;

import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.graph.weighted.WeightedDirectedGraph;
import org.util.Pair;
import util.SimpleLinkedSet;

/**
 * Implementierung der KernighanLinHeuristic zum Lösen des
 * GraphBiPartionProblems.
 *
 * @author bode
 */
public class KernighanLinHeuristic {

    private KernighanLinHeuristic() {
    }

    public static <V> BiPartition<V> getOpt(WeightedDirectedGraph<V, Double> graph) {
        BiPartition<V> p_start = getRandomInitial(graph);
        Parameters.logger.finer("Start:" + p_start);
        return getOpt(p_start);
    }

    public static <V> BiPartition<V> getOpt(BiPartition<V> p_start) {
        BiPartition<V> result = new BiPartition<V>(new DefaultWeightedDirectedGraph<V, Double>(p_start.getGraph()), p_start.getA(), p_start.getB());
        double gmax;
        Parameters.logger.finer("Schleifendurchlauf");
        do {
            gmax = getKOpt(result);
            Parameters.logger.finer("GMax:"+gmax);
        } while (gmax > 0);
        return result;
    }

    public static <V> double getKOpt(BiPartition<V> result) {
        /**
         * Ergebnisse für diesen Durchgang
         */
        SimpleLinkedSet<V> a_strich = new SimpleLinkedSet<V>(result.getA());
        SimpleLinkedSet<V> b_strich = new SimpleLinkedSet<V>(result.getB());

        /**
         * p_strich zur Berechnung der neuen Differenzen
         */
        BiPartition<V> p_strich = new BiPartition<V>(new DefaultWeightedDirectedGraph<V, Double>(result.getGraph()), new SimpleLinkedSet<V>(result.getA()), new SimpleLinkedSet<V>(result.getB()));

        /**
         *
         */
        ArrayList<Exchange<V>> elementsToSwap = new ArrayList<Exchange<V>>();

        for (int i = 0; i < result.getGraph().numberOfVertices() / 2;) {
            /**
             * Aktuelle Kosten
             */
            p_strich.updatePartitioningCosts();

            /**
             * Das Paar a_i, b_i, bei dem g_i maximal ist
             */
            Exchange<V> giExchange = getGiExchange(p_strich);
            if (giExchange.a == null) {
                break;
            }
            Parameters.logger.finest("Austausch: " + giExchange);


            /**
             * Entfernen aus der Differenzenbetrachtung
             */
            p_strich.getGraph().removeVertex(giExchange.a);
            p_strich.getGraph().removeVertex(giExchange.b);
            p_strich.getA().remove(giExchange.a);
            p_strich.getB().remove(giExchange.b);


            /**
             * Tauschen der Objekte in den Ergebnismengen a_i und b_i
             */
            a_strich.remove(giExchange.a);
            b_strich.add(giExchange.a);
            a_strich.add(giExchange.b);
            b_strich.remove(giExchange.b);



            /**
             * Wird zu den Elementen, die getauscht werden sollen hinzugefügt
             */
            elementsToSwap.add(giExchange);
        }
        
        double gmax = 0;
        int k = 0;
        for (Exchange<V> pair : elementsToSwap) {
            double gmaxNew = gmax + pair.gi;
            if (gmaxNew > gmax) {
                gmax = gmaxNew;
                k++;
            } else {
                break;
            }
        }
        Parameters.logger.finest("Gmax bei k=" + k + ": " + gmax);
        if (gmax > 0) {
            for (int ik = 0; ik < k; ik++) {
                Exchange<V> pair = elementsToSwap.get(ik);
                result.getA().remove(pair.a);
                result.getB().add(pair.a);
                result.getA().add(pair.b);
                result.getB().remove(pair.b);
            }
        }

        Parameters.logger.finest("Momentan:" + result + "T: " + result.getT());
        return gmax;
    }

    /**
     * Erzeugt eine Zufällige Aufteilung der Knoten in 2 Knotenmengen
     *
     * @param <V> Datentyp der Knoten
     * @param graph Graph, dessen Knoten aufgeteilt werden sollen.
     * @return zufällige BiPartition des Graphens
     */
    public static <V> BiPartition<V> getRandomInitial(WeightedDirectedGraph<V, Double> graph) {
        Random r = new Random(13574l);
        BiPartition<V> result = new BiPartition<V>(graph);
        ArrayList<V> verticies = new ArrayList<V>(graph.vertexSet());
        Collections.shuffle(verticies, r);
        SimpleLinkedSet<V> currentSet = result.getA();
        while (!verticies.isEmpty()) {
            currentSet.add(verticies.remove(0));
            currentSet = currentSet.equals(result.getA()) ? result.getB() : result.getA();
        }
        return result;
    }

    public static <V> Exchange<V> getGiExchange(BiPartition<V> partition) {
        LinkedHashMap<V, PartitioningCost> partitioningCosts = partition.getPartitioningCosts();

        Exchange pair = new Exchange(null, null, Double.NEGATIVE_INFINITY);

        for (V a : partition.getA()) {
            for (V b : partition.getB()) {
                Pair<V, V> currentPair = null;

                if (partition.getGraph().containsEdge(a, b)) {
                    currentPair = new Pair<V, V>(a, b);
                }
                if (partition.getGraph().containsEdge(b, a)) {
                    currentPair = new Pair<V, V>(b, a);
                }
                double c = 0;

                if (currentPair != null) {
                    c = partition.getGraph().getEdgeWeight(currentPair);
                }

                double dAi = partitioningCosts.get(a).getDifference();
                double dBi = partitioningCosts.get(b).getDifference();

                double gi = dAi + dBi - 2 * c;
                Parameters.logger.finest("==============");
                Parameters.logger.finest(a + "/" + b + ": " + gi);
                Parameters.logger.finest("dAi: " + dAi + " dBi: " + dBi + " c: " + partition.getGraph().getEdgeWeight(currentPair));
                Parameters.logger.finest("Ai: " + partitioningCosts.get(a).getExternalCost() + "/" + partitioningCosts.get(a).getInternalCost());
                Parameters.logger.finest("Bi: " + partitioningCosts.get(b).getExternalCost() + "/" + partitioningCosts.get(b).getInternalCost());
                Parameters.logger.finest("==============");
                if (gi > pair.gi) {
                    pair.a = a;
                    pair.b = b;
                    pair.gi = gi;
                }


            }
        }
        return pair;
    }

    public static class Exchange<V> {

        public V a;
        public V b;
        public double gi;

        public Exchange(V a, V b, double gi) {
            this.a = a;
            this.b = b;
            this.gi = gi;
        }

        @Override
        public String toString() {
            return "Exchange{" + "a=" + a + ", b=" + b + ", gi=" + gi + '}';
        }
    }
}
