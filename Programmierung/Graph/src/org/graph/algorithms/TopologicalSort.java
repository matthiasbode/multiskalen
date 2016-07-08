/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.graph.directed.DirectedGraph;
import org.graph.directed.DefaultDirectedGraph;

/**
 *
 * @author rinke, bode
 */
public final class TopologicalSort {

    private TopologicalSort() {
    }

    /**
     * Gibt die Zuordnung der Knoten des übergebenen Graphens zu Knotenklassen
     * zurück.
     *
     * @param <V> Datentyp der Knoten.
     * @param graph Betrachteter Graph.
     * @return Abbildung zwischen KnotenKlasse (Integer-Wert) und Menge von
     * Knoten.
     */
    public static <E> List<Set<E>> topologicalSort(DirectedGraph<E> graph) {
        /**
         * Nicht Azyklische Graphen können nicht sortiert werden.
         */
        AcyclicTest<E> test = new AcyclicTest<>(graph);

        if (!test.isAcyclic()) {
            throw new IllegalArgumentException("Graph has Cycles");
        }

        ArrayList<Set<E>> resultList = new ArrayList<>();

        /**
         * Kopie des Graphens. Aus dieser werden die Knoten und Kanten während
         * der Konstruktion der Knotenklassen herausgestrichen. Würde nicht die
         * Kopie benutzt werden, würde der Ursprungsgraph verändert werden und
         * später keinen Knoten und keine Kanten mehr enthalten.
         */
        DefaultDirectedGraph<E> copy = new DefaultDirectedGraph<>(graph);
        //Momentane Knotenklasse, die bestimmt werden soll

        boolean finished = false;
        //Schleife, solange noch Knoten vorhanden sind
        while (!finished) {
            //Leere Knotenklasse anlegen
            HashSet<E> currentSet = new HashSet<E>();
            /**
             * Gehe über alle Knoten des Graphens und teste, ob dieser keine
             * Vorgänger hat.
             */
            for (E node : copy.vertexSet()) {
                //Falls keine Vorgänger--> der k-ten Knotenklasse hinzufügen
                if (copy.getPredecessors(node).isEmpty()) {
                    currentSet.add(node);
                }
            }
            resultList.add(currentSet);
            /**
             * Alle Knoten der momentanen Knotenklasse aus Graphen entfernen
             */
            for (E node : currentSet) {
                copy.removeVertex(node);
            }
            /**
             * Falls keine Knoten mehr vorhanden --> fertig
             */
            if (copy.numberOfVertices() == 0) {
                finished = true;
            }
        }
        return resultList;
    }
}
