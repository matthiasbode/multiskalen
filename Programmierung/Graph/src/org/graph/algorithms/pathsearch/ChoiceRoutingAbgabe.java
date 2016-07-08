/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms.pathsearch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedInverseTree;
import org.graph.weighted.WeightedPath;
import org.graph.weighted.WeightedRootedTree;
import org.util.Pair;

/**
 *
 * @author Oliver
 */
public class ChoiceRoutingAbgabe <E, B extends EdgeWeight<B>>
                                        implements KShortestPathAlgorithm<E, B>{

    private WeightedRootedTree<E,B> sourceTree, plateauxTree;
    private WeightedInverseTree<E,B> inverseTree;
    private ArrayList<WeightedPath<E,B>> completedPlateaux,plateaux,plateau;    
    private WeightedDirectedGraph<E, B> original;        
    private Comparator<WeightedPath<E,B>> plateauxComparator;
    private PriorityQueue<WeightedPath<E,B>> prioPlateau;
    private int k;   
    private DijkstraAlgorithm da;    

    public ChoiceRoutingAbgabe() {
       init();
    }    
    
    /**
     * Initialisiert alle benoetigten Komponenten
     */
    private void init() {
        completedPlateaux =  new ArrayList<WeightedPath<E, B>>();
        da = new DijkstraAlgorithm();
        plateau = new ArrayList<WeightedPath<E, B>>();
        plateauxComparator = new PlateauComparator();
        prioPlateau =  new PriorityQueue<WeightedPath<E, B>>(100, plateauxComparator);         
    }
    
    /**
     * Leert alle benoetigten Komponenten zum erneuten Aufrufen
     */
    private void clear() {
        plateau.clear();        
        completedPlateaux.clear();
        prioPlateau.clear();         
    }
    
    /**
     * Bestimmt k - kuerzeste Wege im gerichteten gewichteten Graphen graph
     * @param graph Eingangsgraph
     * @param source Startknoten
     * @param destination Zielknoten
     * @param K Anzahl der Wege
     * @return k - kuerzeste Wege von source nach destination
     */
    @Override
    public ArrayList<WeightedPath<E, B>> kShortestPaths(WeightedDirectedGraph<E, B> graph, E source, E destination, int K) {       
        //Parameter speichern und Komponenten leeren
        this.k=K;     
        original = graph;
        clear();
                
        long wholeStart = System.currentTimeMillis();
        long start = System.currentTimeMillis();
        //Source Tree des Graphen bestimmen
        sourceTree = getSourceTree(graph, source, destination);                
        long end = System.currentTimeMillis();
        System.out.println("Source-Tree: "+(start-end)+" mSec");   
        //Destination Tree des Graphen bestimmen
        inverseTree = getInverseDestinationTree(graph, destination, source);                        
        start = System.currentTimeMillis();
        System.out.println("Inverse-Tree: "+(end-start)+" mSec");
        //Plateaux identifizieren
        plateaux = identifyPlateaux(sourceTree, inverseTree, source, destination);    
        end = System.currentTimeMillis();
        System.out.println("Plateaux: "+(start-end)+" mSec");
                         
        int count = 1;              
        //Schleife ueber alle Plateaux, um sie zu Wegen zu vervollstaendigen
        for(WeightedPath<E,B> path: plateaux) {            
            //Nur k Wege sollen bestimmte werden...
            if(count <= K) {
                //Die drei Teile des Weges zusammenfuegen
                WeightedPath<E,B> p = sourceTree.getWeightedPath(source, path.getStartVertex()).clone();
                p.appendPath(path);
                p.appendPath(inverseTree.getWeightedPath(path.getLastVertex(), destination));
                //Neuen kompletten Weg in Liste einfuegen
                completedPlateaux.add(p);                
                count++;                               
            }            
        }        
        start = System.currentTimeMillis();
        System.out.println("Completed: "+(end-start)+" mSec");
        System.out.println("Algorithmus Choice - Routing: "+(wholeStart-start)+" mSec");
        return completedPlateaux;
    }         
    
    /**
     * Dient zum iterativen Aufbau von Wegen
     * @param path bisheriger Weg
     * @param tree Baum aus dem der Weg aufgebaut wird
     * @param vertex aktueller Knoten
     */
    private void addIterative(WeightedPath<E,B> path, WeightedRootedTree<E,B> tree, E vertex) {
        //Knoten anhaengen
        path.appendVertexInFront(vertex);
        try {
            //Falls ein Vorgaenger existiert...
            if(tree.getParent(vertex)!=null && !vertex.equals(tree.getRoot())) {
                //...kette die naechste Kante...
                path.setWeight(path.getWeight().product(tree.getEdgeWeight(tree.getParent(vertex), vertex)));
                //... und fahr mit dem naechsten Knoten fort.
                vertex = tree.getParent(vertex);
                addIterative(path, tree, vertex);
            }
        }
        catch(NoSuchElementException e) {} //Ende des Plateau
    }
    
    /**
     * Bestimmt den Source Tree mit Hilfe des Dijkstra Algorithmus
     * @param graph Graph
     * @param source Startknoten
     * @param destination Zielknoten
     * @return Suchbaum
     */
    public WeightedRootedTree<E,B> getSourceTree(WeightedDirectedGraph<E, B> graph, E source, E destination) {                
        da.setGraph(graph);
        //SourceTree bestimmen
        sourceTree = da.singlePairShortestPathTree(source, destination);        
        return sourceTree;
    }    
    
    /**
     * Bestimmt den Destination Tree als InverseTree
     * @param graph Graph
     * @param destination eigentlicher Endknoten, hier Startknoten
     * @param source eigentlicher Startknoten, hier Endknoten
     * @return  Suchbaum
     */
    public WeightedInverseTree<E,B> getInverseDestinationTree(WeightedDirectedGraph<E, B> graph, E destination, E source) {
        da.setGraph(graph);      
        //Destination Tree bestimmen
        inverseTree  = da.singlePairShortestPathInverseTree(destination, source);   
        return inverseTree;
    }
    
    /**
     * Erzeugt eine Baumstruktur ohne quasistrengen Zusammenhang! Die enthaltenen Pfade sind die Plateaux
     * @param st Source Tree
     * @param inverse Destination Tree
     * @return Plateaux in Baumstruktur
     */
    public WeightedRootedTree<E,B> getPlateauTree(WeightedRootedTree<E,B> st, WeightedInverseTree<E,B> inverse) {
        //Kopie des Source Tree erzeugen
        plateauxTree = new WeightedRootedTree<E, B>(st);  
        //Alle Kanten des Source Tree
        HashSet<Pair<E,E>> edges = new HashSet<Pair<E, E>>(st.edgeSet());
        //Ueber alle Kanten laufen...
        for(Pair<E,E> edge: edges) {
            //...und wenn sie im Destination Tree enthalten sind...
            if(!inverse.containsEdge(edge))
                ///...entfernen.
                plateauxTree.removeEdge(edge);
        }
        return plateauxTree;
    }
    
    /**
     * Bestimmt aus der ausgeduennten Baumstruktur, welche die Plateaux enthaelt die einzelnen Plateau.
     * @param st Source Tree
     * @param inverse Destination Tree
     * @param source Startknoten
     * @param destination Zielknoten
     * @return Plateaux
     */
    public ArrayList<WeightedPath<E,B>> identifyPlateaux(WeightedRootedTree<E,B> st, WeightedInverseTree<E,B> inverse, E source, E destination) {
        //Baumstruktur mit Plateaux erzeugen
        WeightedRootedTree<E,B> pT = getPlateauTree(st, inverse);
        int count = 1;
        //Plateauxenden als Blaetter des Baumes interpretieren und ueber sie iterieren
        for(E vertex: pT.getLeafs()) {
            //Ein Plateauende hat Vorgaenger aber keine Nachfolger...
            if(!pT.getPredecessors(vertex).isEmpty() && pT.getSuccessors(vertex).isEmpty()) {
                //Neues Plateau beginnen...
                WeightedPath<E,B> path = new WeightedPath<E, B>(pT.getEdgeWeight(pT.getParent(vertex), vertex),vertex);
                //... und iterativ aufbauen.
                addIterative(path, pT, pT.getParent(vertex));
                //Neues Plateau einfuegen.
                prioPlateau.add(path);
                count++;
            }
        }                 
        //Von Prioritaetsliste in ArrayList ueberfuehren
        while(prioPlateau.size()>0) plateau.add(prioPlateau.poll());
        return plateau;
    }
    
    /**
     * Uebergibt des Ausgangsgraph
     * @return 
     */
    public WeightedDirectedGraph<E, B> getOriginalGraph() {
        return original;
    }
    
    /**
     * Der PlateauComparator bewertet Plateaux. Die Bewertung entspricht dem
     * Gewicht des Weges, also der Summe der Kantengewichte.
     */
    class PlateauComparator implements Comparator<WeightedPath<E,B>> {

        public PlateauComparator() {
        }       

        /**
         * Vergleicht zwei Plateau
         * @param p1 Plateau eins
         * @param p2 Plateau zwei
         * @return Vergleich
         */
        @Override
        public int compare(WeightedPath<E, B> p1, WeightedPath<E, B> p2) {
            if(p1.getWeight().equals(p1.getWeight().getNullElement())) {
                if(p2.getWeight().equals(p2.getWeight().getNullElement()))
                    return 0;
                return 1;
            } else if(p2.getWeight().equals(p2.getWeight().getNullElement())) {
                return -1;
            }

            if(p1.getWeight().doubleValue()>p2.getWeight().doubleValue())
                return -1;
            if(p2.getWeight().doubleValue()>p1.getWeight().doubleValue())
                return 1;
            return 0;
            
        }
    }        
}
