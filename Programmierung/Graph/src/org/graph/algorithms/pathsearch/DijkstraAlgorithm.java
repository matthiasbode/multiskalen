package org.graph.algorithms.pathsearch;


import java.util.IdentityHashMap;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedInverseTree;
import org.graph.weighted.WeightedPath;
import org.graph.weighted.WeightedRootedTree;
import org.util.Pair;
import org.util.FibonacciHeap;
import org.util.FibonacciHeapNode;

/**
 * DijkstraAlgorithm.java implementiert den Dijkstra-Algorithmus
 * zur Suche von Wegen in bewerteten schlichten Graphen.
 *
 * @author Nils Rinke
 */
public class DijkstraAlgorithm<V, B extends EdgeWeight<B>>
                                  implements SingleShortestPathAlgorithm<V,B> {

    private final FibonacciHeap<V> fibonacciCandidates;
    private final IdentityHashMap<V,Item> map;

    private WeightedDirectedGraph<V,B> graph;
    
    private B einsWeight;
    
    public DijkstraAlgorithm() {
        fibonacciCandidates = new FibonacciHeap<V>();
        map = new IdentityHashMap<V, Item>();
    }

    public void setGraph(WeightedDirectedGraph<V, B> graph) {
        this.graph = graph;
        einsWeight = graph.getEdgeWeight(
                graph.edgeSet().iterator().next()).getEinsElement();
        fibonacciCandidates.clear();
        map.clear();
    }
    
    
    
    

    @Override
    public WeightedRootedTree<V, B> singleSourceShortestPath(
                        WeightedDirectedGraph<V, B> graph, V source) {
        WeightedRootedTree<V,B> searchTree = new WeightedRootedTree<V,B>(source);
        einsWeight = graph.getEdgeWeight(
                graph.edgeSet().iterator().next()).getEinsElement();
        searchTree.setVertexWeight(source, einsWeight);
        fibonacciCandidates.clear();
        map.clear();

        Item sourceItem = new Item(source, source, einsWeight);

        
        fibonacciCandidates.insert(sourceItem,0);
        map.put(source, sourceItem);
        //while a candidate is in the candidate set
        while (!fibonacciCandidates.isEmpty()) {
            Item candidate = (Item) fibonacciCandidates.removeMin();
            B distance  = candidate.weight;

            //analyses successors of the candidate
            for (V succ : graph.getSuccessors(candidate.getData())) {
                //successor in the initial set
                if (!map.containsKey(succ)) {
                    B edge = graph.getEdgeWeight(candidate.getData(), succ);
                    B dist = distance.product(edge);

                    Item succNode = new Item(succ, candidate.getData(), dist);
                    fibonacciCandidates.insert(succNode, dist.doubleValue());
                    searchTree.addChild(succ, candidate.getData(), dist);
                    searchTree.setVertexWeight(succ, dist);
                    map.put(succ, succNode);
                }
                else {
                    B edge = graph.getEdgeWeight(candidate.getData(), succ);
                    B dist = distance.product(edge);

                    Item succItem = map.get(succ);
                    if(dist.compareTo(succItem.weight) < 0) {
//                            verticesToWeightsMap.put(succ, dist);
                        succItem.parent = candidate.getData();
                        succItem.weight = dist;
                        fibonacciCandidates.decreaseKey(succItem, dist.doubleValue());
                        searchTree.removeVertex(succ);
                        searchTree.setVertexWeight(succ, dist);
                        searchTree.addChild(succ, candidate.getData(), dist);
                    }
                }
            }
        }
        return searchTree;
    }


   
    @Override
    public WeightedRootedTree<V, B> singleDestinationShortestPath(
                        WeightedDirectedGraph<V, B> graph, V source) {
        //TODO Kanten evtl. umkehren (Baumstruktur geht verloren)
        return singleSourceShortestPath(graph.dual(), source);
    }
         
    public WeightedInverseTree<V, B> singlePairShortestPathInverseTree(
                        V bottom, V destination) {
        WeightedInverseTree<V,B> searchTree = new WeightedInverseTree<V, B> (bottom);
        searchTree.setVertexWeight(bottom, einsWeight);

        Item bottomItem = new Item(bottom, bottom, einsWeight);
        
        fibonacciCandidates.insert(bottomItem,0);
        map.put(bottom, bottomItem);
        //while a candidate is in the candidate set
        while (!fibonacciCandidates.isEmpty()) {
            Item candidate = (Item) fibonacciCandidates.removeMin();
            B distance  = candidate.weight;
            if(candidate.getData().equals(destination)) break;

            //analyses successors of the candidate
            for (V pre : graph.getPredecessors(candidate.getData())) {
                //successor in the initial set
                if (!map.containsKey(pre)) {                    
//                    if(!graph.containsEdge(succ, candidate.getData())) continue;
                    B edge = graph.getEdgeWeight(pre, candidate.getData());
                    B dist = distance.product(edge);

                    Item preNode = new Item(pre, candidate.getData(), dist);
                    fibonacciCandidates.insert(preNode, dist.doubleValue());
                    searchTree.addParent(pre, candidate.getData(), graph.getEdgeWeight(pre, candidate.getData()));
                    searchTree.setVertexWeight(pre, dist);
                    map.put(pre, preNode);
                }
                else {
                    B edge = graph.getEdgeWeight(pre, candidate.getData());
                    B dist = distance.product(edge);

                    Item preItem = map.get(pre);
                    if(dist.compareTo(preItem.weight) < 0) {
//                            verticesToWeightsMap.put(succ, dist);
                        preItem.parent = candidate.getData();
                        preItem.weight = dist;
                        fibonacciCandidates.decreaseKey(preItem, dist.doubleValue());
                        searchTree.removeVertex(pre);
                        searchTree.setVertexWeight(pre, dist);
                        searchTree.addParent(pre, candidate.getData(), graph.getEdgeWeight(pre, candidate.getData()));
                    }
                }
            }
        }
        return searchTree;    
    }
    

    @Override
    public WeightedPath<V, B> singlePairShortestPath(
                   WeightedDirectedGraph<V, B> graph, V source, V destination) {
        einsWeight = graph.getEdgeWeight(
                graph.edgeSet().iterator().next()).getEinsElement();
        fibonacciCandidates.clear();
        map.clear();
        
        Item sourceItem = new Item(source, source, einsWeight);
        fibonacciCandidates.insert(sourceItem,0);
        map.put(source, sourceItem);
        int untersuchteKnoten = 1;
        //while a candidate is in the candidate set
        while (!fibonacciCandidates.isEmpty()) {
            Item candidate = (Item) fibonacciCandidates.removeMin();
            B distance  = candidate.weight;

            //returns shortest path if the end is reached
            if (candidate.getData().equals(destination)) {
                WeightedPath<V,B> path =
                                  new WeightedPath<V, B>(distance, destination);
                V parent = map.get(candidate.getData()).parent;
                while(!parent.equals(source)) {
                    path.appendVertexInFront(parent);
                    parent = map.get(parent).parent;
                }
                path.appendVertexInFront(parent);
//                System.out.println("untersuchte Knoten: " + untersuchteKnoten);
                return path;
            }

            //analyses successors of the candidate
            else {
                for (V succ : graph.getSuccessors(candidate.getData())) {
                    //successor in the initial set
                    if (!map.containsKey(succ)) {
                        B edge = graph.getEdgeWeight(candidate.getData(), succ);
                        B dist = distance.product(edge);

                        Item succNode = new Item(succ, candidate.getData(), dist);
                        fibonacciCandidates.insert(succNode, dist.doubleValue());
                        map.put(succ, succNode);
                        untersuchteKnoten++;
                    }
                    else {
                        B edge = graph.getEdgeWeight(candidate.getData(), succ);
                        B dist = distance.product(edge);

                        Item succItem = map.get(succ);
                        if(dist.compareTo(succItem.weight) < 0) {
                            succItem.parent = candidate.getData();
                            succItem.weight = dist;
                            fibonacciCandidates.decreaseKey(succItem, dist.doubleValue());
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public WeightedRootedTree<V,B> singlePairShortestPathTree(V source, V destination) {
        WeightedRootedTree<V,B> searchTree = new WeightedRootedTree<V,B>(source); 
        searchTree.setVertexWeight(source, einsWeight);

        Item sourceItem = new Item(source, source, einsWeight);
        
        fibonacciCandidates.insert(sourceItem,0);
        map.put(source, sourceItem);
        //while a candidate is in the candidate set
        while (!fibonacciCandidates.isEmpty()) {
            Item candidate = (Item) fibonacciCandidates.removeMin();
            B distance  = candidate.weight;
            if(candidate.getData().equals(destination)) break;

            //analyses successors of the candidate
            for (V succ : graph.getSuccessors(candidate.getData())) {
                //successor in the initial set
                if (!map.containsKey(succ)) {
                    B edge = graph.getEdgeWeight(candidate.getData(), succ);
                    B dist = distance.product(edge);

                    Item succNode = new Item(succ, candidate.getData(), dist);
                    fibonacciCandidates.insert(succNode, dist.doubleValue());
                    searchTree.addChild(succ, candidate.getData(), graph.getEdgeWeight(candidate.getData(), succ));
                    searchTree.setVertexWeight(succ, dist);
                    map.put(succ, succNode);
                }
                else {
                    B edge = graph.getEdgeWeight(candidate.getData(), succ);
                    B dist = distance.product(edge);

                    Item succItem = map.get(succ);
                    if(dist.compareTo(succItem.weight) < 0) {
//                            verticesToWeightsMap.put(succ, dist);
                        succItem.parent = candidate.getData();
                        succItem.weight = dist;
                        fibonacciCandidates.decreaseKey(succItem, dist.doubleValue());
                        searchTree.removeVertex(succ);
                        searchTree.setVertexWeight(succ, dist);
                        searchTree.addChild(succ, candidate.getData(), graph.getEdgeWeight(candidate.getData(), succ));
                    }
                }
            }
        }
        return searchTree;                
        
        
//        singlePairShortestPath(source, destination);
//        WeightedRootedTree tree = new WeightedRootedTree(source);             
//        for(V key: map.keySet()) {
//            WeightedPath<V,B> path = new WeightedPath<V, B>();     
//            V parent = map.get(key).parent;
//            if(key.equals(source)) continue;   
//            else path.appendVertexInFront(key);
//            while(!parent.equals(source)) {
//                path.appendVertexInFront(parent);                
//                parent = map.get(parent).parent;                   
//            }
//            //TODO: EDGE WEIGHT SETZEN!!!
//            path.appendVertexInFront(parent);     
//            V lastChild = null;
//            for(Edge<V,V> edge: path.getPathEdges()) {                
//                HashSet<V> children = tree.getChildren(edge.getFirst());
//                if(!children.contains(edge.getSecond())) {
//                    tree.addChild(edge.getSecond(), edge.getFirst());
//                    lastChild = edge.getSecond();
//                }
//            }
//            tree.setVertexWeight(lastChild, path.getWeight());
//        }        
//        return tree;
    }

    public WeightedPath<V, B> singlePairShortestPath(V source, V destination) {
        Item sourceItem = new Item(source, source, einsWeight);
        fibonacciCandidates.insert(sourceItem,0);
        map.put(source, sourceItem);
        
        //while a candidate is in the candidate set
        while (!fibonacciCandidates.isEmpty()) {
            Item candidate = (Item) fibonacciCandidates.removeMin();
            B distance  = candidate.weight;

            //returns shortest path if the end is reached
            if (candidate.getData().equals(destination)) {
                WeightedPath<V,B> path =
                                  new WeightedPath<V, B>(distance, destination);
                V parent = map.get(candidate.getData()).parent;
                while(!parent.equals(source)) {
                    path.appendVertexInFront(parent);
                    parent = map.get(parent).parent;                      
                }
                path.appendVertexInFront(parent);
                System.out.println(map);
                return path;
            }

            //analyses successors of the candidate
            else {
                for (V succ : graph.getSuccessors(candidate.getData())) {
                    //successor in the initial set
                    if (!map.containsKey(succ)) {
                        B edge = graph.getEdgeWeight(candidate.getData(), succ);
                        B dist = distance.product(edge);

                        Item succNode = new Item(succ, candidate.getData(), dist);
                        fibonacciCandidates.insert(succNode, dist.doubleValue());
                        map.put(succ, succNode);
                    }
                    else {
                        B edge = graph.getEdgeWeight(candidate.getData(), succ);
                        B dist = distance.product(edge);

                        Item succItem = map.get(succ);
                        if(dist.compareTo(succItem.weight) < 0) {
                            succItem.parent = candidate.getData();
                            succItem.weight = dist;
                            fibonacciCandidates.decreaseKey(succItem, dist.doubleValue());
                        }
                    }
                }
            }
        }
        return null;
    }
    

    private class Item extends FibonacciHeapNode<V>{
        V parent; 
        B weight;

        Item(V vertex, V parent, B weight) {
            super(vertex);
            this.parent = parent;
            this.weight = weight;
        }
    }


    @Override
    public String toString() {
        return "Dijkstra-Algorithmus";
    }
    
    //alte Versionen
    //        WeightedRootedTree<V,B> searchTree = new WeightedRootedTree<V,B>(source);
//        HashSet<V> initialSet = new HashSet<V>(graph.vertexSet());
//        initialSet.remove(source);
//
//        HashMap<V,B> verticesToWeightsMap = new HashMap<V, B>();
//        comparator =
//                               new VertexComparator<V, B>(verticesToWeightsMap);
//
//        candidates.clear();
//
//
//        B einsWeight = graph.getEdgeWeight(
//                graph.edgeSet().iterator().next()).getEinsElement();
//
//        verticesToWeightsMap.put(source, einsWeight);
////        weightsPerVertex.put(source, einsWeight);
////        searchTree.setVertexWeight(source, einsWeight);
//        candidates.offer(source);
//
////..while a candidate is in the candidate set...............................//
//        while (candidates.size() > 0) {
//            V candidate = candidates.poll();
//            B distance = verticesToWeightsMap.get(candidate);
////            B distance = weightsPerVertex.get(candidate);
//
//
////..analyses successors of the candidate....................................//
//            for (V succ : graph.getSuccessors(candidate)) {
////..successor in the initial set............................................//
//                if (initialSet.contains(succ)) {
//                    initialSet.remove(succ);
//                    B edge = graph.getEdgeWeight(candidate, succ);
//                    B dist = distance.product(edge);
//                    verticesToWeightsMap.put(succ, dist);
////                    weightsPerVertex.put(succ, dist);
//                    candidates.add(succ);
//
////                   searchTree.addChild(succ, candidate, dist);
//                    searchTree.addChild(succ, candidate);
//                    searchTree.setEdgeWeight(succ, candidate, dist);
//                }
////..successor in the candidate set..........................................//
//                else {
//                    B edge = graph.getEdgeWeight(candidate, succ);
//                    B dist = distance.product(edge);
//
//                    if(dist.compareTo(verticesToWeightsMap.get(succ)) < 0) {
////                    if(dist.compareTo(weightsPerVertex.get(succ))<0){
////                        weightsPerVertex.put(succ, dist);
//                        verticesToWeightsMap.put(succ, dist);
//                        candidates.remove(succ);
//                        candidates.offer(succ);
//                    }
//                }
//            }
//        }
//        return searchTree;
}