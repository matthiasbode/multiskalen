/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.algorithms.pathsearch;


import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.graph.weighted.EdgeWeight;
import org.graph.weighted.WeightedDirectedGraph;
import org.graph.weighted.WeightedPath;
import org.graph.weighted.WeightedRootedTree;
import org.util.Pair;
import org.util.FibonacciHeap;
import org.util.FibonacciHeapNode;


/**
 *
 * @author Nils Rinke
 */
public class NewParallelDijkstra<V, B extends EdgeWeight<B>> implements PairShortestPathAlgorithm<V,B> {
    private boolean finish;

    private final CyclicBarrier barrier;
    private final ForwardWorker forwardWorker;
    private final ReverseWorker reverseWorker;

    private Thread forwardThread;
    private Thread reverseThread;

    private B mue;
    private B einsWeight;
    private B nullWeight;
    private Pair<V,V> connectionEdge;

    
    private WeightedDirectedGraph<V, B> wGraph;

    public NewParallelDijkstra() {
        barrier = new CyclicBarrier(2, new Merger());
        forwardWorker = new ForwardWorker();
        forwardThread  = new Thread(forwardWorker);
        reverseWorker = new ReverseWorker();
        reverseThread  = new Thread(reverseWorker);

        forwardWorker.otherWorker = reverseWorker;
        reverseWorker.otherWorker = forwardWorker;
    }
    
    
    public NewParallelDijkstra(WeightedDirectedGraph<V, B> graph) {
        barrier = new CyclicBarrier(2, new Merger());
        forwardWorker = new ForwardWorker();
        forwardThread  = new Thread(forwardWorker);
        reverseWorker = new ReverseWorker();
        reverseThread  = new Thread(reverseWorker);

        forwardWorker.otherWorker = reverseWorker;
        reverseWorker.otherWorker = forwardWorker;
        
        this.wGraph = graph;
        forwardWorker.setGraph(graph);
        reverseWorker.setGraph(graph);
        mue = wGraph.getEdgeWeight(wGraph.edgeSet().iterator().next()).getNullElement();
        einsWeight = mue.getEinsElement();
        nullWeight = mue.getNullElement();
    }


    /** Calculates a shortest path between two vertices in a weighted
     *  directed graph by starting from the source and destination at the same
     *  time.
     *
     *
     *  @param graph        weighted directed graph
     *  @param source       source vertex of the shortest path
     *  @param destination  destination vertex of the shortest path
     *  @return	The method returns the shortest weighted path.
     */
    @Override
    public WeightedPath<V, B> singlePairShortestPath(
                        WeightedDirectedGraph<V, B> graph, V source, V destination) {
        mue = graph.getEdgeWeight(graph.edgeSet().iterator().next()).getNullElement();
        einsWeight = mue.getEinsElement();

        forwardWorker.setComponents(source, graph);
        reverseWorker.setComponents(destination, graph);
        finish = false;
        
        if(forwardThread.getState().equals(Thread.State.TERMINATED))
            forwardThread = new Thread(forwardWorker);
        if(reverseThread.getState().equals(Thread.State.TERMINATED))
            reverseThread = new Thread(reverseWorker);
        if(barrier.isBroken()) {
            barrier.reset();
        }
        forwardThread.start();
        reverseThread.start();

        // Warten, bis der Thread beendet ist: finish
        try {
           reverseThread.join();
        } catch (InterruptedException e) {
           // Thread wurde abgebrochen
        }
        Item firstItem = forwardWorker.map.get(connectionEdge.getFirst());
        Item secondItem = reverseWorker.map.get(connectionEdge.getSecond());
        B weight = firstItem.weight.product(graph.getEdgeWeight(connectionEdge)).product(secondItem.weight);
        WeightedPath<V,B> path = new WeightedPath<V, B>(weight, connectionEdge.getFirst());
            V parent = firstItem.parent;
            while(!parent.equals(source)) {
                path.appendVertexInFront(parent);
                parent = forwardWorker.map.get(parent).parent;
            }
            path.appendVertexInFront(parent);
            path.appendEdge(connectionEdge, graph.getEdgeWeight(connectionEdge));
        
            parent = secondItem.parent;
            while(!parent.equals(destination)) {
                path.appendVertex(parent);
                parent = reverseWorker.map.get(parent).parent;
            }
            path.appendVertex(parent);
        
        return path;
    }
    
    /** Calculates a shortest path between two vertices in a weighted
     *  directed graph by starting from the source and destination at the same
     *  time.
     *
     *
     *  @param source       source vertex of the shortest path
     *  @param destination  destination vertex of the shortest path
     *  @return	The method returns the shortest weighted path.
     */
    public WeightedPath<V, B> singlePairShortestPath(V source, V destination) {
        forwardWorker.setSource(source);
        reverseWorker.setDestination(destination);
        mue = nullWeight;
        connectionEdge=null;
        finish = false;
        
        if(forwardThread.getState().equals(Thread.State.TERMINATED))
            forwardThread = new Thread(forwardWorker);
        if(reverseThread.getState().equals(Thread.State.TERMINATED))
            reverseThread = new Thread(reverseWorker);
        if(barrier.isBroken()) {
            barrier.reset();
        }
        forwardThread.start();
        reverseThread.start();

        // Warten, bis der Thread beendet ist: finish
        try {
           reverseThread.join();
        } catch (InterruptedException e) {
           // Thread wurde abgebrochen
        }
        Item firstItem = forwardWorker.map.get(connectionEdge.getFirst());
        Item secondItem = reverseWorker.map.get(connectionEdge.getSecond());
        B weight = firstItem.weight.product(wGraph.getEdgeWeight(connectionEdge)).product(secondItem.weight);
        WeightedPath<V,B> path = new WeightedPath<V, B>(weight, connectionEdge.getFirst());
            V parent = firstItem.parent;
            while(!parent.equals(source)) {
                path.appendVertexInFront(parent);
                parent = forwardWorker.map.get(parent).parent;
            }
            path.appendVertexInFront(parent);
            path.appendEdge(connectionEdge);
        
            parent = secondItem.parent;
            while(!parent.equals(destination)) {
                path.appendVertex(parent);
                parent = reverseWorker.map.get(parent).parent;
            }
            path.appendVertex(parent);
        return path;
    }

    @Override
    public String toString() {
        return "Dijkstra (neu parallelisiert)";
    }
    
    

    
    private class ForwardWorker implements Runnable {
        V source;
        WeightedDirectedGraph<V, B> graph;
        
        FibonacciHeap<V> candidates;
        IdentityHashMap<V,Item> map;
        

        ReverseWorker otherWorker;

        public ForwardWorker() {
            candidates = new FibonacciHeap<V>();
            map = new IdentityHashMap<V, Item>();
        }
        
        public void setSource(V source) {
            this.source = source;

            if(!candidates.isEmpty())
                candidates.clear();
            Item sourceItem = new Item(source, source, einsWeight);
            candidates.insert(sourceItem,0.);
            if(!map.isEmpty())
                map.clear();
            map.put(source, sourceItem);
        }
        
        public void setGraph(WeightedDirectedGraph<V, B> graph) {
            this.graph = graph;
        }

        public void setComponents(V source, WeightedDirectedGraph<V, B> graph) {
            this.source = source;
            this.graph = graph;
            
            if(!candidates.isEmpty())
                candidates.clear();
            Item sourceItem = new Item(source, source, einsWeight);
            candidates.insert(sourceItem,0.);
            if(!map.isEmpty())
                map.clear();
            map.put(source, sourceItem);
        }


        @Override
        public void run() {
            //while a candidate is in the candidate set
            while (!finish && !candidates.isEmpty()) {
                Item candidate = (Item) candidates.removeMin();
                B distance = candidate.weight;

//            //returns shortest path if the end is reached
//            if (candidate.getData().equals(destination)) {
//                WeightedPath<V,B> path =
//                                  new WeightedPath<V, B>(distance, destination);
//                V parent = map.get(candidate.getData()).parent;
//                while(!parent.equals(source)) {
//                    path.appendVertexInFront(parent);
//                    parent = map.get(parent).parent;
//                }
//                path.appendVertexInFront(parent);
//                return path;
//            }

                //analyses successors of the candidate
//            else {
                for (V succ : graph.getSuccessors(candidate.getData())) {
                    //successor in the initial set
                    if (!map.containsKey(succ)) {
                        B edge = graph.getEdgeWeight(candidate.getData(), succ);
                        B dist = distance.product(edge);

                        Item succNode = new Item(succ, candidate.getData(), dist);
                        candidates.insert(succNode, dist.doubleValue());
                        map.put(succ, succNode);

                        if (otherWorker.map.containsKey(succ)) {
                            B tmpMue = dist.product(otherWorker.map.get(succ).weight);
                            if (tmpMue.compareTo(mue) < 0) {
                                mue = tmpMue;
                                connectionEdge = new Pair<V, V>(candidate.getData(), succ);
                            }
                        }
                    } else {
                        B edge = graph.getEdgeWeight(candidate.getData(), succ);
                        B dist = distance.product(edge);

                        Item succItem = map.get(succ);
                        if (dist.compareTo(succItem.weight) < 0) {
                            succItem.parent = candidate.getData();
                            succItem.weight = dist;
                            candidates.decreaseKey(succItem, dist.doubleValue());
                        }
                    }
                }
                
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
             }
        }
    }
    
    
    private class ReverseWorker implements Runnable {
        V destination;
        WeightedDirectedGraph<V, B> graph;
        
        FibonacciHeap<V> candidates;
        IdentityHashMap<V,Item> map;
        
        ForwardWorker otherWorker;

        public ReverseWorker() {
            candidates = new FibonacciHeap<V>();
            map = new IdentityHashMap<V, Item>();
        }
        
        
        public void setDestination(V destination) {
            this.destination = destination;

            if(!candidates.isEmpty())
                candidates.clear();
            Item destItem = new Item(destination, destination, einsWeight);
            candidates.insert(destItem,0.);
            if(!map.isEmpty())
                map.clear();
            map.put(destination, destItem);
        }
        
        
        public void setGraph(WeightedDirectedGraph<V, B> graph) {
            this.graph = graph;
        }
        

        public void setComponents(V destination, WeightedDirectedGraph<V, B> graph) {
            this.destination = destination;
            this.graph = graph;
            
            if(!candidates.isEmpty())
                candidates.clear();
            Item destItem = new Item(destination, destination, einsWeight);
            candidates.insert(destItem,0.);
            if(!map.isEmpty())
                map.clear();
            map.put(destination, destItem);
        }

        
        @Override
        public void run() {
            //while a candidate is in the candidate set
            while (!finish && !candidates.isEmpty()) {
                Item candidate = (Item) candidates.removeMin();
                B distance = candidate.weight;

                for (V pred : graph.getPredecessors(candidate.getData())) {
                    //successor in the initial set
                    if (!map.containsKey(pred)) {
                        B edge = graph.getEdgeWeight(pred, candidate.getData());
                        B dist = distance.product(edge);

                        Item predNode = new Item(pred, candidate.getData(), dist);
                        candidates.insert(predNode, dist.doubleValue());
                        map.put(pred, predNode);

                        if (otherWorker.map.containsKey(pred)) {
                            B tmpMue = dist.product(otherWorker.map.get(pred).weight);
                            if (tmpMue.compareTo(mue) < 0) {
                                mue = tmpMue;
                                connectionEdge = new Pair<V, V>(pred, candidate.getData());
                            }
                        }
                    } else {
                        B edge = graph.getEdgeWeight(pred, candidate.getData());
                        B dist = distance.product(edge);

                        Item predItem = map.get(pred);
                        if (dist.compareTo(predItem.weight) < 0) {
                            predItem.parent = candidate.getData();
                            predItem.weight = dist;
                            candidates.decreaseKey(predItem, dist.doubleValue());
                        }
                    }
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
             }
        }

//        @Override
//        public void run() {
//            //while a candidate is in the candidate set
//            while (!finish && !candidates.isEmpty()) {
//                Item<V,B> candidate = candidates.poll();
//                B distance = candidate.weight;
//
//                for (V node : graph.getSuccessors(candidate.node)) {
//                    //successor in the initial set
//                    if (initialSet.contains(node)) {
//                        initialSet.remove(node);
//                        B edge = graph.getEdgeWeight(candidate.node, node);
//                        B dist = distance.product(edge);
//                        candidates.add(new Item<V,B>(node, dist));
//                        searchTree.addChild(node, candidate.node, edge);
//
//                        if(otherWorker.searchTree.containsVertex(node)) {
//                            B tmpMue = dist.product(otherWorker.searchTree.getVertexWeight(node));
//                            if(tmpMue.compareTo(mue)<0) {
//                                mue = tmpMue;
//                                if(forward)
//                                    connectionEdge = new Edge<V, V>(candidate.node, node);
//                                else
//                                    connectionEdge = new Edge<V, V>(node, candidate.node);
//                            }
//                        }
//                    }
//    //..successor in the candidate set..........................................//
//                    else {
//                        for (Iterator<Item<V,B>> i = candidates.iterator(); i.hasNext();) {
//                            Item<V,B> c = i.next();
//
//                            if (node.equals(c.node)) {
//                                B edge = graph.getEdgeWeight(candidate.node, node);
//                                B dist = distance.product(edge);
//
//                                if(dist.compareTo(c.weight) < 0) {
//                                    c.weight = dist;
//                                    candidates.remove(c);
//                                    candidates.add(c);
//                                    searchTree.removeVertex(c.node);
//                                    searchTree.addChild(c.node, candidate.node,edge);
//                                }
//                                break;
//                            }
//                        }
//                    }
//                }
//
//                try {
//                    barrier.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (BrokenBarrierException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private class Merger implements Runnable {
        
        @Override
        public void run() {
            if(((Item)forwardWorker.candidates.min()).weight.product(((Item)reverseWorker.candidates.min()).weight).compareTo(mue)>=0) {
                finish = true;
            }
        }
    }

    
    private class Item extends FibonacciHeapNode<V>{
        V parent; 
        B weight;

        Item(V vertex, V parent, B weight) {
            super(vertex);
            this.parent = parent;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Item{" + "parent=" + parent + ", weight=" + weight + '}';
        }
        
        
    }
}