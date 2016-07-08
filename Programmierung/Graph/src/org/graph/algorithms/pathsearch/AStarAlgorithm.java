package org.graph.algorithms.pathsearch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.util.FibonacciHeap;


/**
 * AStarAlgorithm.java implementiert einen A-Stern-Algorithmus ohne ClosedList
 * zur Suche von Wegen in bewerteten schlichten Graphen.<br>
 * Funktion zur Bewertung von Knoten x: F(x) = G(x) + a*H(x)
 * @author  yangli<br>
 *          hoecker<br>
 *          rinke
 */
public class AStarAlgorithm<E, B extends EdgeWeight<B>>
                                implements PairShortestPathAlgorithm<E,B>{

    public Heuristic<E>       Hx;         // Schaetzfunktion
    public double              a = 1.0;   // Schaetzfaktor
    public boolean useClosedList = true;  // Zeiger zum Verwenden einer Liste mit abschliessend untersuchten Knoten


    private final PriorityQueue<Item<E>> openList = new PriorityQueue<Item<E>>();
    private final FibonacciHeap<E> fibonacciCandidates;
    private final HashSet<E> closedList = new HashSet<E>();
    private final HashMap<E, Item<E>> weightPerVertex = new HashMap<E, Item<E>>();

    private boolean used = false;
    /**
     * Erzeugt einen A-Stern-Algorithmus ohne ClosedList.<br>
     * Der Default-Wert des Schaetzfaktors ist 2.0.
     * @param Hx Schaetzfunktion.
     */
    public AStarAlgorithm(Heuristic<E> Hx) {
        this.Hx = Hx;

        fibonacciCandidates = new FibonacciHeap<E>();
    }

    /**
     * Setzt den Zeiger zum Verwenden einer Liste mit abschliessend untersuchten Knoten.
     * @param b boolescher Zeiger.
     */
    public void useClosedList(boolean b) {
        this.useClosedList = b;
    }

    /**
     * Setzt den Schaetzfaktor.
     * @param a Schaetzfaktor.
     */
    public void setA(double a) {
        this.a = a;
    }

    @Override
    public WeightedPath<E,B> singlePairShortestPath(WeightedDirectedGraph<E,B> graph, E source, E destination) {
        if(used) {
            openList.clear();
            closedList.clear();
            weightPerVertex.clear();
        } else
            used = true;

        // Mache den Startknoten bekannt.
        Item<E> sourceItem = new Item<E>(source, graph.getEdgeWeight(
                graph.edgeSet().iterator().next()).getEinsElement(),
                0, null);
//        sourceItem.Gx.setDynamicSearch(useDynamic);
        weightPerVertex.put(source, sourceItem);
        openList.add(sourceItem);
        // Algorithmus mit Liste mit abschliessend untersuchten Knoten.
        if (useClosedList) {
            // Solange die bekannte Menge nicht leer ist:
            while (!openList.isEmpty()) {
                // Entferne den Knoten mit der geringsten Bewertung F(x) aus der bekannten Menge.
                Item<E> currentItem = openList.poll();
                // Falls das Ziel gefunden wurde, dann liefere das Ergebnis zurueck.
                if (currentItem.node == destination) {
                     return this.PathFound(graph, currentItem);
                }
                // Sonst: Nachfolger untersuchen
                for (E suc : graph.getSuccessors(currentItem.node)) {
                    // Falls der Nachfolger abschliessend untersucht ist, dann tue nichts.
                    if (closedList.contains(suc))
                        continue;
                    // Berechne die Bewertung G(x).
                    B Gx = currentItem.Gx.product(graph.getEdgeWeight(currentItem.node, suc));
//                    double Gx = currentItem.Gx + graph.getEdgeWeight(currentItem.node, suc).weightToDouble();
                    // Uberpruefe, ob der Nachfolger bekannt ist.
                    Item<E> sucItem;
                    if(!weightPerVertex.containsKey(suc)) {
                        sucItem = new Item<E>(suc, Gx, a * Hx.getValue(suc, destination),currentItem);
                        weightPerVertex.put(suc, sucItem);
                        openList.add(sucItem);
                    }
                     // Sonst ueberpruefe seine Bewertung G(x) und aktualisiere sie, falls der Weg kuerzer ist.
                    else {
                        sucItem = weightPerVertex.get(suc);
                        if (Gx.compareTo(sucItem.Gx) < 0) {
                            sucItem.Gx = Gx;
                            sucItem.Fx = Gx.product(sucItem.aHx);
                            sucItem.preItem = currentItem;
                        }
                    }
                }
                // Der aktuelle Knoten ist nun abschließend untersucht
                closedList.add(currentItem.node);
            }
        }
        // Algorithmus ohne Liste mit abschliessend untersuchten Knoten.
        else {
            // Solange die bekannte Menge nicht leer ist:
            while (!openList.isEmpty()) {
                // Entferne den Knoten mit der geringsten Bewertung F(x).
                Item<E> currentItem = openList.poll();
                // Fuer alle Nachfolger
                for (E suc : graph.getSuccessors(currentItem.node)) {
                    // Falls der Nachfolger nicht zugleich der Vorgaenger ist:
                    if (!(currentItem.preItem != null && suc == currentItem.preItem.node)) {
                        // Berechne die Bewertung G(x).
                        B Gx = currentItem.Gx.product(graph.getEdgeWeight(currentItem.node, suc));
//                        double Gx = currentItem.Gx + graph.getEdgeWeight(currentItem.node, suc).weightToDouble();
                        // Falls der Endknoten erreicht ist, dann beende die Schleife.
                        if (suc == destination) {
                            Item<E> sucItem = new Item<E>(destination, Gx, a * Hx.getValue(suc, destination), currentItem);
                            return this.PathFound(graph, sucItem);
                        } else {
                            // Uberpruefe, ob der Nachfolger bekannt ist.
                            Item<E> sucItem = weightPerVertex.get(suc);
                            // Falls der Knoten nicht bekannt ist, dann bewerte ihn mit F(x).
                            if (sucItem == null) {
                                sucItem = new Item<E>(suc, Gx, a * Hx.getValue(suc, destination), currentItem);
                                openList.add(sucItem);
                            } // Sonst ueberpruefe seine Bewertung G(x), H(x) ist konstant.
                            else if (Gx.compareTo(sucItem.Gx) < 0) {
                                sucItem.Gx = Gx;
                                sucItem.Fx = Gx.product(sucItem.aHx);
                                sucItem.preItem = currentItem;
                            }
                        }
                    }
                }
            }
        }
        // Ergebnis: Kein Weg gefunden.
        return null;
    }

    /**
     * Liefert einen kuerzesten Weg.
     * @param graph Sichtbarkeitsgraph.
     * @param endItem Ziel-Item.
     * @return kuerzester Weg.
     */
    private WeightedPath<E,B> PathFound(WeightedDirectedGraph<E,B> graph, Item<E> endItem) {
        // Erzeuge den kuerzesten Weg als Graph ueber die Rueckwaertsreferenzen des Endknotens.
        WeightedPath<E,B> Gi = new WeightedPath<E,B>(endItem.Gx, endItem.node);
        // Fuege die Knoten ein und bewerte sie mit F(x).
        for (Item<E> item = endItem; item != null; item = item.preItem) {
            if(item.preItem != null)
                Gi.appendVertexInFront(item.preItem.node);   
        }
//        // Fuege die Kanten ein und bewerte sie PHI(e).
//        for (Item<E> pathItem = endItem; pathItem != null; pathItem = pathItem.preItem) {
//            if (pathItem.preItem != null) {
//                Gi.addEdge(graph.getEdge(pathItem.preItem.node, pathItem.node));
////                Gi.setEdgeWeight(pathItem.preItem.node, pathItem.node, graph.getEdgeWeight(pathItem.preItem.node, pathItem.node));
//            }
//        }
        // Ergebnis
        return Gi;
    }


    /**
     * Item.java ist eine innere Klasse fuer
     * einen Knoten mit minimaler Weglaenge und einem Vorgaenger.
     */
    private class Item<E> implements Comparable<Item<E>> {

        E node;                 // Knoten
        B Fx,Gx;
//        double Fx, Gx;
        double aHx;     // Bewertung
        Item<E> preItem;        // Vorgaenger

        public Item(E node, B Gx, double aHx, Item<E> preItem) {
            this.node = node;
            this.Fx = Gx.product(aHx);
            this.Gx = Gx;
            this.aHx = aHx;
            this.preItem = preItem;
        }

        @Override
        public int compareTo(Item<E> item) {
            return Fx.compareTo(item.Fx);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Item) {
                return node.equals(((Item) obj).node);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (this.node != null ? this.node.hashCode() : 0);
            return hash;
        }
    }

    
    @Override
    public String toString() {
        return "A*-Algorithmus (" + Hx + ", a= " + a + ")";
    }
}