/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.graph.directed.DefaultDirectedGraph;
import org.graph.directed.DirectedGraph;
import org.util.Pair;

/**
 * Die Klasse bestimmt für einen gerichteten Graphen Zusammenhangskomponenten
 * nach dem Algorithmus von Tarjan
 *
 * @see <a
 * href="http://de.wikipedia.org/wiki/Algorithmus_von_Tarjan_zur_Bestimmung_starker_Zusammenhangskomponenten">Algorithmus
 * von Tarjan</a>
 *
 * @author bode
 */
public class ConnectionComponentGenerator<V> {

    private LinkedHashSet<V> unbesuchteKnoten;
    private Stack<V> stack;
    private LinkedHashMap<V, Integer> dfs;
    private LinkedHashMap<V, Integer> lowlink;
    private int maxdfs;
    private DirectedGraph<V> graph;
    private List<ConnectionComponent<V>> components;

    /**
     * Konstruktor, dem der Graph übergeben wird.
     * @param graph 
     */
    public ConnectionComponentGenerator(DirectedGraph<V> graph) {
        this.graph = graph;
    }

    /**
     * Initialisierung der benötigten Datencontainer und Zählvariablen.
     */
    private void init() {
        unbesuchteKnoten = new LinkedHashSet<>(graph.vertexSet());
        stack = new Stack<>();
        dfs = new LinkedHashMap<>();
        lowlink = new LinkedHashMap<>();
        maxdfs = 0;
        components = new ArrayList<>();
    }

    /**
     * Methode, die die Zusammenhangskomponenten zurückgibt. 
     * Die Methode ist noch nicht ganz ausgereift. 
     * Es gibt verschiedene Formen des Zusammenhangs.
     * Um zwischen schwachem und starken Zusammenhangskomponenten 
     * zu wechseln, bitte Änderung an der dafür vorgesehenen Stelle 
     * in der Methode tarjan machen.
     * @return Menge von Zusammenhangskomponenten.
     */
    public List<ConnectionComponent<V>> calculateComponents() {
        init();
        while (!unbesuchteKnoten.isEmpty()) {
            V v = unbesuchteKnoten.iterator().next();
            tarjan(v);
        }
        for (ConnectionComponent<V> connectionComponent : components) {
            for (V v1 : connectionComponent.nodes) {
                for (V v2 : connectionComponent.nodes) {
                    Set<Pair<V, V>> allEdges = graph.getAllEdges(v1, v2);
                    for (Pair<V, V> pair : allEdges) {
                        connectionComponent.addEdge(pair);
                    }
                }
            }
        }
        return components;
    }

    public List<ConnectionComponent<V>> getComponents() {
        if(components == null){
            calculateComponents();
        }
        return components;
    }
    
    

    private void tarjan(V v) {
        dfs.put(v, maxdfs);
        lowlink.put(v, dfs.get(v));
        maxdfs++;
        stack.push(v);
        unbesuchteKnoten.remove(v);
        /**
         * Hier ist die Stelle für die Wahl des Zusammenhangs.
         * Könnte mittels Flag geändert werden.
         * Wenn sowohl Vorgänger als auch Nachfolger in Neighbours, handelt es
         * sich um schwache Zusammenhangskomponenten, falls nur Nachfolger,
         * werden starke Zusammenhangskomponenten gesucht.
         */
        Set<V> neighbours = new HashSet<>(graph.getSuccessors(v));
        neighbours.addAll(graph.getPredecessors(v));
        for (V v_ : neighbours) {
            if (unbesuchteKnoten.contains(v_)) {
                tarjan(v_);
                lowlink.put(v, Math.min(lowlink.get(v), lowlink.get(v_)));
            } else if (stack.contains(v_)) {
                lowlink.put(v, Math.min(lowlink.get(v), dfs.get(v_)));
            }
        }
        if (lowlink.get(v).equals(dfs.get(v))) {
            ConnectionComponent<V> component = new ConnectionComponent<>();

            while (!stack.isEmpty()) {
                V v_ = stack.pop();
                component.addNode(v_);
            }
            components.add(component);
        }

    }

    /**
     * Klasse, die die Zusammenhangskomponenten eines Graphens darstellen.
     * Könnte auch als Subgraph implementiert werden.
     * @param <V> 
     */
    public static class ConnectionComponent<V>{

        private LinkedHashSet<V> nodes;
        private LinkedHashSet<Pair<V, V>> edges;

        public ConnectionComponent() {
            this.nodes = new LinkedHashSet<>();
            this.edges = new LinkedHashSet<>();
        }

        public void addNode(V v) {
            this.nodes.add(v);
        }

        public void addEdge(Pair<V, V> pair) {
            this.edges.add(pair);
        }

        public LinkedHashSet<V> getNodes() {
            return nodes;
        }

        public LinkedHashSet<Pair<V, V>> getEdges() {
            return edges;
        }
    }

    public static void main(String[] args) {
        DefaultDirectedGraph<Integer> graph = new DefaultDirectedGraph<>();
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);

        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(1, 3);

        graph.addEdge(4, 5);
        graph.addEdge(6, 5);

        ConnectionComponentGenerator<Integer> ccg = new ConnectionComponentGenerator(graph);
        Collection<ConnectionComponent<Integer>> connectionComponents = ccg.calculateComponents();

        for (ConnectionComponent<Integer> connectionComponent : connectionComponents) {
            System.out.println(connectionComponent.nodes);
            System.out.println(connectionComponent.edges);
            System.out.println("----");
        }

    }
}
