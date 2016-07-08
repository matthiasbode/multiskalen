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
public class ChoiceRouting <E, B extends EdgeWeight<B>>
                                        implements KShortestPathAlgorithm<E, B>{

    public WeightedRootedTree<E,B> sourceTree, destinationTree, plateauxTree;
    public WeightedInverseTree<E,B> inverseTree;
    public ArrayList<WeightedPath<E,B>> completedPlateaux,plateaux;
    public HashMap<WeightedPath<E,B>, WeightedPath<E,B>> PathToPlateauMap;
    public WeightedDirectedGraph<E, B> original;    
    private int k;
    
    private DijkstraAlgorithm da;
    private ArrayList<WeightedPath<E,B>> plateau;
    private Comparator<WeightedPath<E,B>> plateauxComparator, pathComparator;
    private PriorityQueue<WeightedPath<E,B>> prioPlateau;

    public ChoiceRouting() {
       init();
    }    
    
    private void init() {
        completedPlateaux =  new ArrayList<WeightedPath<E, B>>();
        da = new DijkstraAlgorithm();
        plateau = new ArrayList<WeightedPath<E, B>>();
        plateauxComparator = new PlateauComparator();
        prioPlateau =  new PriorityQueue<WeightedPath<E, B>>(100, plateauxComparator);  
        pathComparator = new Comparator<WeightedPath<E, B>>() {
                        @Override
                        public int compare(WeightedPath<E, B> o1, WeightedPath<E, B> o2) {
                            return o1.getWeight().compareTo(o2.getWeight());
                        }
                    };         
    }
    
    private void clear() {
        plateau.clear();        
        completedPlateaux.clear();
        prioPlateau.clear();         
    }
    
    @Override
    public ArrayList<WeightedPath<E, B>> kShortestPaths(WeightedDirectedGraph<E, B> graph, E source, E destination, int K) {       
        this.k=K;     
        original = graph;
        clear();
        
        long wholeStart = System.currentTimeMillis();
        long start = System.currentTimeMillis();
        sourceTree = getSourceTree(graph, source, destination);                
        long end = System.currentTimeMillis();
//        System.out.println("Source-Tree: "+(start-end)+" mSec");        
        inverseTree = getInverseDestinationTree(graph, destination, source);                        
        start = System.currentTimeMillis();
//        System.out.println("Inverse-Tree: "+(end-start)+" mSec");        
        plateaux = identifyPlateaux(sourceTree, inverseTree, source, destination);    
        end = System.currentTimeMillis();
//        System.out.println("Plateaux: "+(start-end)+" mSec");
        
//        PriorityQueue<WeightedPath<E,B>> completedPrioPlateaux = 
//                new PriorityQueue<WeightedPath<E, B>>(plateaux.size(), pathComparator);                    
        int count = 1;
        //Nötig für die Identifizierung des zu einem Weg gehoerigen Plateau für die Einfaerbung dieses!
        //Wird vom Graph Editor gebraucht, um die Plateaux einzufaerben! 
        //Fuer Lafzeitoptimierung kann diese Map und ihre Anwendung deaktiviert werden!
        PathToPlateauMap = new HashMap<WeightedPath<E, B>, WeightedPath<E, B>>();
//        WeightedPath<E,B> shortestPath = null;        
        for(WeightedPath<E,B> path: plateaux) {            
            if(count <= K) {
                WeightedPath<E,B> p = sourceTree.getWeightedPath(source, path.getStartVertex()).clone();
                p.appendPath(path);
                p.appendPath(inverseTree.getWeightedPath(path.getLastVertex(), destination));
//                completedPrioPlateaux.add(p);
                completedPlateaux.add(p);
                PathToPlateauMap.put(p, path);                
                count++;
                
                
//                if(shortestPath == null) {
//                    shortestPath = p;
//                    completedPrioPlateaux.add(p);
//                    PathToPlateauMap.put(p, path);                    
//                }                
//                else {
//                    if(checkOverlappingPercentageByEdgeCount(completedPrioPlateaux, p, 0.7)) {
//                        System.out.println("Zugelassen!");
//                        System.out.println("PlateauEvaluation: "+PlateauEvaluation(shortestPath, path));
//                        completedPrioPlateaux.add(p);
//                        PathToPlateauMap.put(p, path);                        
//                    }
//                    else {
//                        System.out.println("Nicht zugelassen!");
//                        System.out.println("PlateauEvaluation: "+PlateauEvaluation(shortestPath, path));                        
//                    }
//                }
            }            
        }        
//        count=0;
//        while(count<k) {
//            WeightedPath<E,B> tmp = completedPrioPlateaux.poll();
////            if(tmp==null) {
////                count++;
////                continue;                
////            }
//            completedPlateaux.add(tmp);    
//            count++;
//        }
        start = System.currentTimeMillis();
//        System.out.println("Completed: "+(end-start)+" mSec");
//        System.out.println("Algorithmus Choice - Routing: "+(wholeStart-start)+" mSec");
        return completedPlateaux;
    }     
    
//    public double PlateauEvaluation(WeightedPath<E,B> master, WeightedPath<E,B> slave) {
//        return slave.getWeight().weightToDouble() / master.getWeight().weightToDouble();
//    }
//    
//    public boolean checkOverlappingPercentageByEdgeCount(PriorityQueue<WeightedPath<E,B>> masters, WeightedPath<E,B> slave, double percentage) {        
//        for(WeightedPath path: masters) {
//            double masterCount = path.getPathEdges().size();
//            double slaveCount = 0;
//            for(Edge edge: slave.getPathEdges()) {
//                if(path.containsEdge(edge)) 
//                    slaveCount++;
//            }
//            if(slaveCount/masterCount>percentage) return false;
//        }
//        return true;
//    }
    
    private void addIterative(WeightedPath<E,B> path, WeightedRootedTree<E,B> tree, E vertex) {
        path.appendVertexInFront(vertex);
        try {
            if(tree.getParent(vertex)!=null && !vertex.equals(tree.getRoot())) {
                path.setWeight(path.getWeight().product(tree.getEdgeWeight(tree.getParent(vertex), vertex)));
                vertex = tree.getParent(vertex);
                addIterative(path, tree, vertex);
            }
        }
        catch(NoSuchElementException e) {
            
        }
    }
    
    public WeightedRootedTree<E,B> getSourceTree(WeightedDirectedGraph<E, B> graph, E source, E destination) {        
        //SourceTree bestimmen
//        DijkstraAlgorithm da = new DijkstraAlgorithm();
        da.setGraph(graph);
        sourceTree = da.singlePairShortestPathTree(source, destination);        
        return sourceTree;
    }
    
    public WeightedRootedTree<E,B> getDestinationTree(WeightedDirectedGraph<E, B> graph, E destination, E source) {
        //DestinationTree bestimmen
//        DijkstraAlgorithm da = new DijkstraAlgorithm();
        da.setGraph(graph); 
        destinationTree = da.singlePairShortestPathTree(destination, source); 
        return destinationTree;
    }
    
    public WeightedInverseTree<E,B> getInverseDestinationTree(WeightedDirectedGraph<E, B> graph, E destination, E source) {
//        DijkstraAlgorithm da = new DijkstraAlgorithm();
        da.setGraph(graph);         
        inverseTree  = da.singlePairShortestPathInverseTree(destination, source);   
        return inverseTree;
    }
    
    public WeightedRootedTree<E,B> getPlateauTree(WeightedRootedTree<E,B> st, WeightedInverseTree<E,B> inverse) {
        plateauxTree = new WeightedRootedTree<E, B>(st);  
        HashSet<Pair<E,E>> edges = new HashSet<Pair<E, E>>(st.edgeSet());
        for(Pair<E,E> edge: edges) {
            if(!inverse.containsEdge(edge))
                plateauxTree.removeEdge(edge);
        }
        return plateauxTree;
    }
    
    public ArrayList<WeightedPath<E,B>> identifyPlateaux(WeightedRootedTree<E,B> st, WeightedInverseTree<E,B> inverse, E source, E destination) {
        WeightedRootedTree<E,B> pT = getPlateauTree(st, inverse);
//        ArrayList<WeightedPath<E,B>> plateau = new ArrayList<WeightedPath<E, B>>();        
//        Comparator<WeightedPath<E,B>> comparator = new PlateauComparator();
//        PriorityQueue<WeightedPath<E,B>> prioPlateau =  new PriorityQueue<WeightedPath<E, B>>(k, comparator);
//        int count = 1;
        for(E vertex: pT.getLeafs()) {
            if(!pT.getPredecessors(vertex).isEmpty() && pT.getSuccessors(vertex).isEmpty()) {
                WeightedPath<E,B> path = new WeightedPath<E, B>(pT.getEdgeWeight(pT.getParent(vertex), vertex),vertex);
                addIterative(path, pT, pT.getParent(vertex));
                prioPlateau.add(path);
//                System.out.println("Plateau "+count+": "+path);
//                count++;
            }
        }                 
        while(prioPlateau.size()>0) plateau.add(prioPlateau.poll());
        return plateau;
    }
    
    public WeightedDirectedGraph<E, B> getOriginalGraph() {
        return original;
    }
    

    
    class PlateauComparator implements Comparator<WeightedPath<E,B>> {

        public PlateauComparator() {
        }       

        @Override
        public int compare(WeightedPath<E, B> p1, WeightedPath<E, B> p2) {
            if(p1.getWeight().equals(p1.getWeight().getNullElement())) {
                if(p2.getWeight().equals(p2.getWeight().getNullElement()))
                    return 0;
                return 1;
            } else if(p2.getWeight().equals(p2.getWeight().getNullElement())) {
                return -1;
            }

//            if(p1.getPathEdges().size()>p2.getPathEdges().size())
            if(p1.getWeight().doubleValue()>p2.getWeight().doubleValue())
                return -1;
//            if(p2.getPathEdges().size()>p1.getPathEdges().size())
            if(p2.getWeight().doubleValue()>p1.getWeight().doubleValue())
                return 1;
//            return p2.getWeight().compareTo(p1.getWeight());
            return 0;
            
        }
    }    
    
}
