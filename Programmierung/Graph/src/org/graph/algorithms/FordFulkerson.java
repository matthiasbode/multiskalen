/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms;

import java.util.Stack;
import java.util.Vector;
import org.graph.Path;
import org.graph.directed.RootedTree;
import org.graph.flow.Flow;
import org.graph.flow.FlowNetwork;
import org.graph.flow.FlowWeight;
import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.util.Pair;

/**
 *
 * @author rinke
 */
public final class FordFulkerson {

    private FordFulkerson() {
    }
    
    
    public static <V> Flow getMaximumFlow(FlowNetwork<V> network) {
        //initialize flow (with zero values)
        Flow<V> maxFlow = new Flow<V>(network);
        
        boolean augmentingPathExists = true;
        while(augmentingPathExists) {
            //residual network
            DefaultWeightedDirectedGraph<V,ResidualNetworkWeight> residualNetwork =
                        getResidualNetwork(network, maxFlow);
            //breadth first search from source
            RootedTree<V> bfsTree = BreadthFirstSearch.computeBreadthSearchTree(
                                          residualNetwork, network.getSource());
            
            //if tree doesn't contain sink
            if(!bfsTree.isDescendant(network.getSink(), network.getSource())) {
                augmentingPathExists = false;
            } else {
                //augmenting path
                Path<V> path =
                        bfsTree.getPath(network.getSource(), network.getSink());
                
                //find maximum increment
                double maxIncrement = Double.POSITIVE_INFINITY;
                for (Pair<V, V> edge : path.getPathEdges()) {
                    maxIncrement = Math.min(maxIncrement,
                                           residualNetwork.getEdgeWeight(edge).flow);
                }
                
                //raise involved flow values by maximum increment
                for (Pair<V, V> edge : path.getPathEdges()) {
                    double newFlow = maxFlow.get(edge) + maxIncrement;
                    maxFlow.put(edge, newFlow);
                }
            }   
        }
        return maxFlow;
    }
    
    public static <V> DefaultWeightedDirectedGraph<V, ResidualNetworkWeight> getResidualNetwork(FlowNetwork<V> network, Flow<V> flow) {
        DefaultWeightedDirectedGraph<V,ResidualNetworkWeight> residualNetwork =
                            new DefaultWeightedDirectedGraph<V, ResidualNetworkWeight>();
        for (V v : network.vertexSet()) {
            residualNetwork.addVertex(v);
        }
        for (Pair<V, V> edge : network.edgeSet()) {
            double edgeFlow = flow.get(edge);
            if(edgeFlow>0)
                residualNetwork.addEdge(edge.getSecond(), edge.getFirst(), new ResidualNetworkWeight(edgeFlow, -network.getEdgeWeight(edge).cost));
            double c_in = network.getEdgeWeight(edge).capacity - edgeFlow;
            if(c_in > 0)
                residualNetwork.addEdge(edge.getFirst(), edge.getSecond(), new ResidualNetworkWeight(c_in, network.getEdgeWeight(edge).cost));
        }
        return residualNetwork;
    }
    
    public static <V> Flow getMaximumFlowWithMinimumCost(FlowNetwork<V> network) {
        long start = System.currentTimeMillis();
        Flow<V> maxFlow = getMaximumFlow(network);
        System.out.println("maximaler Fluss: ");
        int counter = 0;
        for (Pair<V, V> edge : maxFlow.keySet()) {
            if(maxFlow.get(edge)>0. && edge.getFirst() != network.getSource() && edge.getSecond() != network.getSink()) {
                System.out.println(edge.getFirst() + " --> " + edge.getSecond() + " = " + maxFlow.get(edge));
                counter++;
            }
        }
        System.out.println(counter + " Verbindungen");
        boolean costsDecreasingCycleExists = true;
        while(costsDecreasingCycleExists) {
            DefaultWeightedDirectedGraph<V,ResidualNetworkWeight> residualNetwork =
                                           getResidualNetwork(network, maxFlow);
//            Path<V> path = negativeCycle(residualNetwork);
            Path<V> path = negativeCycle2(residualNetwork);
            System.out.println("negative Cycle: " + path);
            if(path==null)
                costsDecreasingCycleExists = false;
            else {
                double maxIncrement = Double.POSITIVE_INFINITY;
                for (Pair<V, V> edge : path.getPathEdges()) {
                    maxIncrement = Math.min(maxIncrement,
                                           residualNetwork.getEdgeWeight(edge).flow);
                }
                for (Pair<V, V> edge : path.getPathEdges()) {
                    if(maxFlow.containsKey(edge))
                        maxFlow.put(edge, maxFlow.get(edge) + maxIncrement);
                    else
                        maxFlow.put(edge.transposition(),maxFlow.get(edge.transposition()) - maxIncrement);
                }
            }
        }
        counter = 0;
        for (Pair<V, V> edge : maxFlow.keySet()) {
            if(maxFlow.get(edge)>0. && edge.getFirst() != network.getSource() && edge.getSecond() != network.getSink()) {
                System.out.println(edge.getFirst() + " --> " + edge.getSecond() + " = " + maxFlow.get(edge));
                counter++;
            }
        }
        System.out.println(counter + " Verbindungen");
        long end = System.currentTimeMillis();
        System.out.println("Dauer: " + (end-start) + " msec!");
        return maxFlow;
    }
    
    public static <V> Path<V> negativeCycle2(DefaultWeightedDirectedGraph<V, ResidualNetworkWeight> graph) {
        if(graph.vertexSet().size() == 1)
            return null;
        
        
        for (V root : graph.vertexSet()) {
            RootedTree<V> tree = new RootedTree<V>(root);

            Stack<V> candidates = new Stack<V>();
            candidates.push(root);

            while (!candidates.isEmpty()) {
                V k = candidates.pop();
                for (V n : graph.getSuccessors(k)) {
                    if (!tree.containsVertex(n)) {
                        candidates.push(n);
                        tree.addChild(n, k);
                    } else if(tree.isDescendant(k, n)) {
                        Path<V> path = tree.getPath(n, k);
                        path.appendVertex(n);
                        double costs = 0.0;
                        for (Pair<V, V> edge : path.getPathEdges()) {
                            costs += graph.getEdgeWeight(edge).cost;
                        }
                        if(costs < 0.0)
                            return path;
                    }
                }
            }
        }
        return null;
    }
    
    public static <V> Path<V> negativeCycle(DefaultWeightedDirectedGraph<V, ResidualNetworkWeight> residualGraph) {
        DefaultWeightedDirectedGraph<V, ResidualNetworkWeight> graph = new DefaultWeightedDirectedGraph<V, ResidualNetworkWeight>();
        for (V v : residualGraph.vertexSet()) {
            graph.addVertex(v);
        }
        for (Pair<V, V> edge : residualGraph.edgeSet()) {
            graph.addEdge(edge.getFirst(), edge.getSecond(), residualGraph.getEdgeWeight(edge));
        }
        int n = residualGraph.numberOfVertices();
        int[][] p = new int[n][n];
        Vector<V> element = new Vector<V>();
        Vector<V> cycle = new Vector<V>();
        double dik, dki, dkj, dij;

//..creates vector of nodes.................................................//
        for (V v : graph.vertexSet()) {
            element.add(v);
        }

//..creates a matrix........................................................//
        for (V v : graph.vertexSet()) {
            int vv = element.indexOf(v);

            for (int i = 0; i < n; i++) {
                p[vv][i] = -1;
            }
            for (V succ : graph.getSuccessors(v)) {
                p[vv][element.indexOf(succ)] = vv;
            }
        }
        for (V k : graph.vertexSet()) {
            for (V i : graph.vertexSet()) {

                if (i == k) {
                    dik = 0.0;
                } else if (graph.containsEdge(i, k)) {
                    dik = graph.getEdgeWeight(i, k).cost;
                } else {
                    dik = Double.POSITIVE_INFINITY;
                }
                if (k == i) {
                    dki = 0.0;
                } else if (graph.containsEdge(k, i)) {
                    dki = graph.getEdgeWeight(k, i).cost;
                } else {
                    dki = Double.POSITIVE_INFINITY;
                }

                if (dik + dki < 0.0) {
                    V v = i;
                    cycle.add(v);
                    while (v != k) {
                        cycle.insertElementAt(v, 1);
                        v = element.get(p[element.indexOf(k)][element.indexOf(v)]);
                    }
                    while (v != i) {
                        cycle.insertElementAt(v, 1);
                        v = element.get(p[element.indexOf(i)][element.indexOf(v)]);
                    }
                    Path<V> pathCycle = new Path<V>();
                    for (V v1 : cycle) {
                        pathCycle.appendVertex(v1);
                    }
                    return pathCycle;
                } else {
                    for (V j : graph.vertexSet()) {
                        if (k == j) {
                            dkj = 0.0;
                        } else if (graph.containsEdge(k, j)) {
                            dkj = graph.getEdgeWeight(k, j).cost;
                        } else {
                            dkj = Double.POSITIVE_INFINITY;
                        }
                        if (i == j) {
                            dij = 0.0;
                        } else if (graph.containsEdge(i, j)) {
                            dij = graph.getEdgeWeight(i, j).cost;
                        } else {
                            dij = Double.POSITIVE_INFINITY;
                        }

                        if (dik + dkj < dij) {
                            if (!graph.containsEdge(i, j)) {
                                graph.addEdge(i, j);
                            }
                            graph.setEdgeWeight(i, j, new ResidualNetworkWeight(0., dik + dkj));
                            int jj = element.indexOf(j);
                            p[element.indexOf(i)][jj] = p[element.indexOf(k)][jj];
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static class ResidualNetworkWeight {
        public double flow;
        public double cost;

        public ResidualNetworkWeight(double flow, double cost) {
            this.flow = flow;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "f: " + flow + ", c: " + cost;
        }
        
        
    }
    
    public static void main(String[] args) {
        FlowNetwork<Integer> network = new FlowNetwork<Integer>(0, 3);
        network.addVertex(1);
        network.addVertex(2);
        network.addEdge(0, 1, new FlowWeight(5, 10));
        network.addEdge(0, 2, new FlowWeight(2, 10));
        network.addEdge(1, 3, new FlowWeight(4, 10));
        network.addEdge(2, 3, new FlowWeight(4, 5));
        network.addEdge(1, 2, new FlowWeight(2, 3));
        System.out.println(getMaximumFlowWithMinimumCost(network));
    }
}

