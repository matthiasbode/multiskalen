/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.algorithms.pathsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.graph.Path;
import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.graph.weighted.WeightedRootedTree;
import org.util.DHeap;
import org.util.Pair;

/**
 *
 * @author lange
 */
public class EppsteinAlgorithm <E, B extends EdgeWeight<B>>
                                        implements KShortestPathAlgorithm<E, B>{

    /**
     *
     */
    private DijkstraAlgorithm<E, B> dijkstraAlgorithm;


    /**
     *
     */
    private Comparator<WeightedPath<E,B>> pathComparator;

    public EppsteinAlgorithm(){
        dijkstraAlgorithm = new DijkstraAlgorithm<E, B>();
    }

    public void setPathComparator(Comparator<WeightedPath<E, B>> pathComparator) {
        this.pathComparator = pathComparator;
    }

    WeightedRootedTree<E, B> sdsp;
    WeightedDirectedGraph<E, B> g_t;
    WeightedDirectedGraph<E, B> graph;

    
    //TODO Path-Vergleich: BreadthFirstSearch in Eppstein umlagern und PathListe zurueckgeben

    @Override
    public ArrayList<WeightedPath<E, B>> kShortestPaths(final WeightedDirectedGraph<E, B> graph, E source, E destination, int K) {

        ArrayList<WeightedPath<E, B>> shortestPaths = new ArrayList<WeightedPath<E, B>>();
        this.graph = graph;

        sdsp = dijkstraAlgorithm.singleDestinationShortestPath(graph, destination);
        WeightedPath<E, B> shortestPath = dijkstraAlgorithm.singlePairShortestPath(graph, source, destination);

        //G-T Graph erstellen mit delta(e) als Gewicht
        g_t = new DefaultWeightedDirectedGraph<E, B>(graph);
        ArrayList<Pair<E, E>> toRemove = new ArrayList<Pair<E, E>>();
        for (Pair<E, E> edge : graph.edgeSet()) {
            if(sdsp.containsVertex(edge.getSecond()) && sdsp.containsVertex(edge.getFirst())){
                double d_head = sdsp.getVertexWeight(edge.getSecond()).doubleValue();
                double d_tail = sdsp.getVertexWeight(edge.getFirst()).doubleValue();
                double diff = d_head-d_tail;
                B weight = graph.getEdgeWeight(edge).product(diff*130./3.6);  //delta(e) berechnen
                g_t.setEdgeWeight(edge, weight);
            }
            else{   //Kante loeschen, wenn Start o. Ende nicht im SDSP-Tree enthalten
                toRemove.add(edge);
            }
        }
        for (Pair<E, E> edge : sdsp.edgeSet()) {  //alle Kanten des SDSP-Trees aus dem G-T Graphen loeschen
//            if(!sdsp.containsEdge(edge.transposition()))
                g_t.removeEdge(edge.transposition());
        }
        for (Pair<E, E> edge : toRemove) {
            g_t.removeEdge(edge);
        }
        
        // 2-Heap Hout(v) erstellen
        Comparator<Pair<E, E>> comparator = new Comparator<Pair<E, E>>() {

            @Override
            public int compare(Pair<E, E> e1, Pair<E, E> e2) {
                return g_t.getEdgeWeight(e1).compareTo(g_t.getEdgeWeight(e2));
            }
        };
        
        HashMap<E, DHeap> vToHout = new HashMap<E, DHeap>();
        
        for (E vertex : g_t.vertexSet()) {
            DHeap hout = new DHeap(comparator, 2);
            for (Pair<E, E> edge : g_t.outgoingEdgesOf(vertex)) {  //fuer jeden Knoten
                hout.insert(edge);                                 //ein 2-Heap, der alle von diesem Knoten ausgehenden
            }                                                      //sidetracks sortiert
            vToHout.put(vertex, hout);
        }

        //HT erstellen
        HashMap<E, DHeap> vToH_T = new HashMap<E, DHeap>();
        
        DHeap ht = new DHeap(comparator, 2);
        if(!vToHout.get(destination).isEmpty())
            ht.insert(vToHout.get(destination).findMin());        //HT Heap des Ziels
        vToH_T.put(destination, ht);
        ArrayList<E> done = new ArrayList<E>();
        for (E v : sdsp.getLeafs()) {
            WeightedPath<E, B> subpath = sdsp.getWeightedPath(destination, v);  //fuer jeden shortestPath
            for (int i = 1; i<subpath.getNumberOfVertices(); i++) {
                E vertex = subpath.getVertexAt(i);                              //fuer jeden Knoten v
                if(!done.contains(vertex)){                                     //nur einmal
                    done.add(vertex);
                    DHeap h_T = vToH_T.get(subpath.getVertexAt(i-1));           //in den HT Heap des Vorgaengers
                    DHeap clone = h_T.clone();
                    if(!vToHout.get(vertex).isEmpty())                          //den kuerzesten aus v ausgehendem sidetrack einfuegen
                        clone.insert(vToHout.get(vertex).findMin());
                    vToH_T.put(vertex, clone);
                }
            }
        }


        //D(G) erstellen & Hv-Map erstellen
        D_Graph<Pair<E, E>, B> dG = new D_Graph();
        HashMap<E, Integer> vToHv = new HashMap<E, Integer>();

        for (E v : g_t.vertexSet()) {                                           //fuer jeden Knoten
            if(vToH_T.containsKey(v)){
                DHeap h_t = vToH_T.get(v);
                if(!h_t.isEmpty()){
                    dG.addDHeap(h_t);                                           //in den Graphen den HT Heap einfuegen
                    vToHv.put(v, dG.heaps.size()-1);                            //sowie fuer jeden Knoten den Index des Heaps im Graphen speichern
                    DHeap clone = h_t.clone();
                    while(!clone.isEmpty()){
                        Pair<E, E> akt = (Pair<E, E>)clone.deleteMin();         //dann fuer jeden Knoten im HT-Heap
                        E first = akt.getFirst();
                        DHeap hout = vToHout.get(first);                        //den Hout Heap in den Graphen einfuegen,
                        if(!dG.heaps.contains(hout))                            //sofern dieser nicht bereits eingefuegt wurde
                            dG.addDHeap(hout);
                        dG.addIndex(akt, dG.heaps.indexOf(hout));               //und den Index von Hout abspeichern
                    }
                }
            }
        }


        //P(G) erstellen
        D_Graph<Pair<E, E>, B> pG = dG.clone();                                                             //P(G) enthaelt dieselben heaps wie D(G)
        HashSet<Pair<Pair<E, E>, Pair<E, E>>> cr_edges = new HashSet<Pair<Pair<E, E>, Pair<E, E>>>();       //dient der Identifizierung von cross_edges
        for (DHeap heap : pG.heaps) {
            DHeap cl = heap.clone();
            while(!cl.isEmpty()){
                Pair<E, E> v = (Pair<E, E>)cl.deleteMin();                      //und zusaetzliche Verbindungen(cross_edges) zwischen
                E second = v.getSecond();                                       //jedem Knoten eines Heaps(Knoten repräsentiert sidetrack)
                Integer index = vToHv.get(second);                              //und dem Heap des Knotens, den man erreicht,
                pG.addCrossEdge(heap, v, index);                                //wenn man entsprechenden sidetrack benutzt
                cr_edges.add(new Pair(v, (Pair<E, E>)dG.heaps.get(index).findMin()));
            }
        }

        //BreadthFirstSearch
//        RootedTree<Edge<E, E>> tree = BreadthFirstSearch.<Edge<E, E>>computeBreadthSearchTree(pG, vToH_T.get(source), K);
        ArrayList<Path<Pair<E, E>>> breadth_paths = computeBreadthSearchTree(pG, vToH_T.get(source), K);
        ArrayList<WeightedPath<E, B>> paths = new ArrayList<WeightedPath<E, B>>();
        paths.add(shortestPath);
        pathComparator = new PathComparator(paths);
        PriorityQueue<WeightedPath<E, B>> pathqueue = new PriorityQueue<WeightedPath<E, B>>( K*shortestPath.getNumberOfVertices(), pathComparator);
//        ArrayList<Edge<E, E>> vertices = new ArrayList<Edge<E, E>>();
//        vertices.add(tree.getRoot());                                           //kuerzesten Weg nach Dijkstra einfuegen
//        while (!vertices.isEmpty()) {
        for (Path<Pair<E, E>> path : breadth_paths) {
//            Edge<E, E> vertex = vertices.get(0);
//            vertices.remove(0);
//            for (Edge<E, E> edge1 : tree.getChildren(vertex)) {                 //fuer alle Knoten des Baumes
//                vertices.add(edge1);
//            }
//            Path<Edge<E, E>> path = tree.getPath(tree.getRoot(), vertex);       //den Pfad von der Wurzel zum Knoten betrachten
            ArrayList<Pair<E, E>> pathseq = new ArrayList<Pair<E, E>>();
            for (Pair<Pair<E, E>, Pair<E, E>> edge : path.getPathEdges()) {     //von allen Kanten des Pfades,
                if(cr_edges.contains(edge))                                     //die ein cross_edge sind,
                    pathseq.add(edge.getFirst());                               //den sidetrack, der auf den cross_edge fuehrt, in die Pathseq einfuegen
            }
            pathseq.add(path.getLastVertex());                                  //den letzten genommenen sidetrack in die Pathseq einfuegen
            WeightedPath<E, B> p = getPath(pathseq, source, destination);       //bildet den zugehörigen Pfad im Graphen aus der Folge von sidetracks
            if(!turnAround(p)){   
                pathqueue.offer(p);
            }
        }

        shortestPaths.add(shortestPath);
        for (int i = 0; i < K-1; i++) {                                           //die k kuerzesten Wege aus der Liste von Pfaden nehmen
            if(pathqueue.isEmpty()){
                System.out.println("Nur " + (i+1) + "Weg(e) gefunden");
                break;
            }
            shortestPaths.add(pathqueue.poll());
            ArrayList<WeightedPath<E,B>> tmpStore = new ArrayList<WeightedPath<E, B>>();
            while(!pathqueue.isEmpty()) {
                tmpStore.add(pathqueue.poll());
            }
            for (WeightedPath<E, B> weightedPath : tmpStore) {
                pathqueue.offer(weightedPath);
            }
        }
        return shortestPaths;                                                   //gibt die k kuerzesten Wege zurueck
    }

    /**
     * ueberprueft, ob der Weg p Wenden enthaelt
     * 
     * @param p(WeightedPath)
     * @return boolean
     */
    public boolean turnAround(WeightedPath<E, B> p){
        for (Pair<E, E> edge : p.getPathEdges()) {
            if(p.containsEdge(edge.transposition()))
                return true;
        }
        return false;
    }

    /**
     * BreadthFirstSearch, die anstelle eines Baumes, eine Liste von Pfaden
     * zurueckgibt, die den Pfaden von der Wurzel zu jedem weiteren Knoten des
     * Baumes entsprechen
     * 
     * @param graph(D_Graph)
     * @param heap(DHeap)
     * @param K(int)
     * @return ArrayList<Path<Edge<E, E>>>
     */
    public ArrayList<Path<Pair<E, E>>> computeBreadthSearchTree(D_Graph graph, DHeap heap, int K){

        ArrayList<Path<Pair<E, E>>> paths = new ArrayList<Path<Pair<E, E>>>();
        Queue<Pair<E, E>> candidates = new ConcurrentLinkedQueue<Pair<E, E>>();
        HashMap<Pair<E, E>, DHeap> vertexToHeap = new HashMap<Pair<E, E>, DHeap>();
        HashMap<Pair<E, E>, Integer> vertexToPath = new HashMap<Pair<E, E>, Integer>();
        Pair<E, E> heapRoot = (Pair<E, E>)heap.findMin();
        vertexToHeap.put(heapRoot, heap);
        Path<Pair<E, E>> rootpath = new Path<Pair<E, E>>(heapRoot);
        paths.add(new Path<Pair<E, E>>(rootpath));
        vertexToPath.put(heapRoot, 0);
        candidates.offer(heapRoot);

        while(!candidates.isEmpty()){
            Pair<E, E> cand  = candidates.poll();
            DHeap cand_heap = vertexToHeap.get(cand);
            Path<Pair<E, E>> p = paths.get(vertexToPath.get(cand));
            for (Object object : cand_heap.getChildren(cand)) {                 //nimmt alle Kinder aus dem HT-Heap von cand
                Pair<E, E> succ = (Pair<E, E>) object;
                if(!succ.equals(cand) && ! p.containsVertex(succ)){
                Path<Pair<E, E>> cand_path = new Path<Pair<E, E>>(p);
                cand_path.appendVertex(succ);
                    paths.add(cand_path);
                    vertexToHeap.put(succ, cand_heap);
                    vertexToPath.put(succ, paths.size()-1);
                    candidates.offer(succ);
                }
            }
            if(!graph.isHout(cand_heap)){                                       //nimmt den Hout-Heap von cand(wenn der cand_heap selbst kein Hout Heap ist)
                if(graph.getHout(cand)!=null){
                    DHeap hout = graph.getHout(cand).clone();
                    hout.deleteMin();                                           //loescht den kuerzesten sidetrack aus dem Heap, da schon in Ht
                    if(!hout.isEmpty()){
                        Pair<E, E> out = (Pair<E, E>)hout.findMin();            //nimmt den 2. kuerzesten sidetrack aus dem Heap
                        if(!out.equals(cand) && ! p.containsVertex(out)){
                        Path<Pair<E, E>> cand_path = new Path<Pair<E, E>>(p);
                        cand_path.appendVertex(out);
                            paths.add(cand_path);
                            vertexToHeap.put(out, graph.getHout(cand));         //speichert diesen mit Hout als eigenem Heap ab
                            vertexToPath.put(out, paths.size()-1);
                            candidates.offer(out);
                        }
                    }
                }
            }
            DHeap cross_heap = graph.getSecondOfCrossEdge(cand_heap, cand);     //nimmt den cross_heap, der durch nehmen des sidetracks cand erreicht wird
            Pair<E, E> s = (Pair<E, E>)cross_heap.findMin();
            if(!s.equals(cand) && ! p.containsVertex(s)){
            Path<Pair<E, E>> cand_path = new Path<Pair<E, E>>(p);
            cand_path.appendVertex(s);
                paths.add(cand_path);
                vertexToHeap.put(s, cross_heap);                                //speichert den kuerzesten sidetrack mit dem cross_heap ab
                vertexToPath.put(s, paths.size()-1);
                candidates.offer(s);
            }
//
//            if(paths.size()>100*K)                                               //nur begrenzte Anzahl an Pfaden, da sonst zu viel Zeit benötigt wird
//                return paths;
        }
        return paths;
    }


    /**
     * Jede Folge von sidetracks definiert eindeutig einen Pfad im Graphen,
     * Die Methode erstellt diesen Pfad aus einer gegebenen Folge von sidetracks
     * 
     * @param sidetracks
     * @param source
     * @param destination
     * @return WeightedPath
     */
    public WeightedPath<E, B> getPath(ArrayList<Pair<E, E>> sidetracks, E source, E destination){
        WeightedPath<E, B> path = new WeightedPath<E, B>();
        for (Pair<E, E> edge : sidetracks) {                  //fuer alle sidetracks
            E start = edge.getFirst();                        //Knoten, von dem der erste sidetrack abgeht
            WeightedPath<E, B> sub;
            if(path.getNumberOfVertices()==0){                //Pfad noch leer
                sub = sdsp.getWeightedPath(start, source);    //nimmt den kuerzesten Weg vom Start zu jenem Knoten
                path = sub.invertPath();                      //und invertiert diesen
            }
            else {                                                              //Pfad nicht mehr leer
                sub = sdsp.getWeightedPath(start, path.getEndVertex());         //nimmt den kuerzesten Weg vom letzten Knoten des Pfades
                WeightedPath<E, B> sub_inverted = sub.invertPath();             //zu jenem Knoten, invertiert diesen
                for (Pair<E, E> edge1 : sub_inverted.getPathEdges()) {
                    path.appendEdge(edge1, g_t.getEdgeWeight(edge1));           //und haengt ihn an den aktuellen Pfad dran
                }
            }
            path.appendEdge(edge, g_t.getEdgeWeight(edge));                     //anschliessend Anhaengen des sidetracks
        }
        WeightedPath<E, B> last = sdsp.getWeightedPath(destination, path.getLastVertex()).invertPath();   //kuerzester Weg vom letzten Knoten bis zum Ziel
        for (Pair<E, E> edge : last.getPathEdges()) {                                                     //Anhaengen
            path.appendEdge(edge, graph.getEdgeWeight(edge));
        }
        return path;
    }

    /**
     *
     * ein Graph, der sich aus einer Liste von DHeaps zusammensetzt, und somit
     * denselben Knoten mehrfach und mit unterschiedlichen Verbindungen
     * enthalten kann
     */
    public class D_Graph<V, B extends EdgeWeight<B>>{
        
        private ArrayList<DHeap> heaps;                                         //Liste der Heaps
        private HashMap<V, Integer> indices;                                    //speichert fuer jeden Knoten den Index des dazugehoerigen Hout-Heaps in der Liste
        private HashMap<DHeap, HashMap<V, Integer>> cross_edges;                //speichert fuer jeden Knoten und jeden Heap den Index des Heaps in der Liste,
                                                                                //der durch nehmen des sidetracks erreicht wird

        public D_Graph(){
            heaps = new ArrayList<DHeap>();
            indices = new HashMap<V, Integer>();
            cross_edges = new HashMap<DHeap, HashMap<V, Integer>>();
        }

        /**
         * Gibt den Heap zurueck, der von dem cross_edge erreicht wird.
         *
         * @param h(DHeap)
         * @param v(Knoten)
         * @return DHeap
         */
        public DHeap getSecondOfCrossEdge(DHeap h, V v){
            int i = cross_edges.get(h).get(v);
            return heaps.get(i);
        }

        /**
         * Gibt den Hout Heap des Knoten zurück.
         * 
         * @param v(Knoten)
         * @return DHeap
         */
        public DHeap getHout(V v){
            if(indices.containsKey(v)){
                int i = indices.get(v);
                return heaps.get(i);
            }
            return null;
        }

        /**
         * Ueberprueft, ob es sich beim gegebenen Heap um einen
         * Hout Heap handelt.
         * 
         * @param h(DHeap)
         * @return boolean
         */
        public boolean isHout(DHeap h){
            if(indices.containsValue(heaps.indexOf(h)))
                return true;
            return false;
        }

      

        /**
         * Fuegt einen neuen DHeap hinzu.
         * 
         * @param h(DHeap)
         */
        public void addDHeap(DHeap h){
            heaps.add(h);
        }

        /**
         * Speichert fuer den Knoten den Index des zugehoerigen Hout Heaps ab.
         * 
         * @param v(Knoten)
         * @param i(Integer)
         */
        public void addIndex(V v, int i){
            indices.put(v, i);
        }

        /**
         * Speichert einen neuen cross_edge ab.
         * 
         * @param h(DHeap)
         * @param v(Knoten)
         * @param i(Integer)
         */
        public void addCrossEdge(DHeap h, V v, int i){
            if(!cross_edges.containsKey(h)){
                HashMap<V, Integer> map = new HashMap<V, Integer>();
                map.put(v, i);
                cross_edges.put(h, map);
            }
            else{
                HashMap<V, Integer> map = cross_edges.get(h);
                map.put(v, i);
                cross_edges.remove(h);
                cross_edges.put(h, map);
            }
        }

        /**
         * Gibt den Heap an der Stelle i in der Liste wieder.
         * 
         * @param i(Integer)
         * @return DHeap
         */
        public DHeap getHeap(int i){
            return heaps.get(i);
        }


        @Override
        public D_Graph<V, B> clone(){
            D_Graph<V, B> clone = new D_Graph<V, B>();
            for (DHeap heap : this.heaps) {
                clone.addDHeap(heap);
            }
            for (V v : this.indices.keySet()) {
                clone.addIndex(v, this.indices.get(v));
            }
            return clone;
        }

    }

    class PathComparator implements Comparator<WeightedPath<E,B>> {
        ArrayList<WeightedPath<E, B>> shortestPaths;

        public PathComparator(ArrayList<WeightedPath<E, B>> shortestPaths) {
            this.shortestPaths = shortestPaths;
        }


        @Override
        public int compare(WeightedPath<E, B> p1, WeightedPath<E, B> p2) {
            if(shortestPaths.isEmpty())
                return p1.getWeight().compareTo(p2.getWeight());
            if(p1.getWeight().equals(p1.getWeight().getNullElement())) {
                if(p2.getWeight().equals(p2.getWeight().getNullElement()))
                    return 0;
                return 1;
            } else if(p2.getWeight().equals(p2.getWeight().getNullElement())) {
                return -1;
            }

            double edgeOverlapping = getProcentualOverlap(p1);
//            System.out.println("overlap: " + edgeOverlapping);
            double weight = shortestPaths.get(0).getWeight().doubleValue()/
                            p1.getWeight().doubleValue();
//            System.out.println("weight: " + weight);
            double c1 = edgeOverlapping*weight;
//            weightPerCandidate.put(p1, c1);

            edgeOverlapping = getProcentualOverlap(p2);
            weight = shortestPaths.get(0).getWeight().doubleValue()/
                     p2.getWeight().doubleValue();
            double c2 = edgeOverlapping*weight;
//            weightPerCandidate.put(p2, c2);
            if(c1 > c2)
                return -1;
            if(c1 == c2)
                return 0;
            else
                return +1;
        }


        private double getProcentualOverlap(WeightedPath<E, B> path) {
            ArrayList<Double> overlapping = new ArrayList<Double>();
            double edgeOverlapping = 0;
            for (WeightedPath<E, B> weightedPath : shortestPaths) {
                int overlap=0;
                for (Pair<E, E> edge : weightedPath.getPathEdges()) {
                    if(path.getPathEdges().contains(edge))
                        overlap++;
                }
                edgeOverlapping += 1.*overlap/weightedPath.getNumberOfEdges();
                overlapping.add(1.*overlap/weightedPath.getNumberOfEdges());
            }
            return 1-(edgeOverlapping/shortestPaths.size());
//            return 1-Collections.max(overlapping);
        }
    }

//    public class Pathcomparator<E, B extends EdgeWeight<B>> implements Comparator<WeightedPath<E, B>> {
//
//        Collection<WeightedPath<E, B>> shortestPaths;
//
//        public Pathcomparator(Collection<WeightedPath<E, B>> shortestPaths) {
//            this.shortestPaths = shortestPaths;
//        }
//
//        @Override
//        public int compare(WeightedPath<E, B> p1, WeightedPath<E, B> p2) {
//            if(shortestPaths.isEmpty())
//                return p1.getWeight().compareTo(p2.getWeight());
//            if(p1.getWeight().equals(p1.getWeight().getNullElement())) {
//                if(p2.getWeight().equals(p2.getWeight().getNullElement()))
//                    return 0;
//                return 1;
//            } else if(p2.getWeight().equals(p2.getWeight().getNullElement())) {
//                return -1;
//            }
//
//            double c1 = p1.getWeight().weightToDouble();
//            double c2 = p2.getWeight().weightToDouble();
//            if(c1 > c2)
//                return 1;
//            if(c1 == c2)
//                return 0;
//            else
//                return -1;
//        }
//    }

   
}
