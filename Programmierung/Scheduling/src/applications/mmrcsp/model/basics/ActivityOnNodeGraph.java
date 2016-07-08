/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics;

import applications.mmrcsp.model.operations.Operation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import math.FieldElement;
import org.graph.algorithms.ConnectionComponentGenerator;
import org.graph.algorithms.TopologicalSort;
import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.util.Pair;

/**
 * Ein ActivityOnNodeGraph beschreibt die zeitlichen Restriktionen zwischen
 * Operationen. Die Operationen liegen auf den Knoten, die Kanten werden
 * Gewichtet mit d_{ij}^min bzw. auch mit d_{ij}^max.
 *
 *
 * @author bode
 * @param <E>
 */
public class ActivityOnNodeGraph<E extends Operation> extends DefaultWeightedDirectedGraph<E, FieldElement> {

    /**
     * Für jede Zusammenhangskomponente eine Topologische Sortierung
     */
    protected Map<AoNComponent<E>, List<Set<E>>> connectionComponents;
    protected Map<E, AoNComponent<E>> vertexToComponent;
    private HashMap<E, Integer> operation2NodeClasses;
    private List<Set<E>> nodeClasses;

    public ActivityOnNodeGraph() {
        super();

    }

    public ActivityOnNodeGraph(ActivityOnNodeGraph<E> graph) {
        super(graph);
    }

    public ActivityOnNodeGraph(Collection<E> verticies, Collection<Pair<E, E>> edges, LinkedHashMap<Pair<E, E>, FieldElement> edgeWeights) {
        super(verticies, edges, edgeWeights);
    }

    public void calculateComponentsAndNodeClasses() {

        connectionComponents = new HashMap<>();
        vertexToComponent = new HashMap<>();

        /**
         * Erzeuge Zusammenhangskomponenten und Topologische Sortierungen.
         */
        ConnectionComponentGenerator<E> ccgenerator = new ConnectionComponentGenerator(this);
        Collection<ConnectionComponentGenerator.ConnectionComponent<E>> cComponents = ccgenerator.calculateComponents();
        for (ConnectionComponentGenerator.ConnectionComponent<E> connectionComponent : cComponents) {
            LinkedHashMap<Pair<E, E>, FieldElement> eweights = new LinkedHashMap<>();
            for (Pair<E, E> pair : connectionComponent.getEdges()) {
                eweights.put(pair, this.getEdgeWeight(pair));
            }
            AoNComponent<E> cgraph = new AoNComponent(this, connectionComponent.getNodes(), connectionComponent.getEdges(), eweights);
            List<Set<E>> topoSort = TopologicalSort.topologicalSort(cgraph);
            connectionComponents.put(cgraph, topoSort);
            for (E e : connectionComponent.getNodes()) {
                vertexToComponent.put(e, cgraph);
            }
        }

        operation2NodeClasses = new HashMap<>();
        nodeClasses = new ArrayList<>();

        for (AoNComponent<E> subGraph : connectionComponents.keySet()) {
            List<Set<E>> subTopoSort = connectionComponents.get(subGraph);
            for (int i = 0; i < subTopoSort.size(); i++) {

                Set<E> subNodeClass = subTopoSort.get(i);

                if (i < nodeClasses.size()) {
                    nodeClasses.get(i).addAll(subNodeClass);
                } else {
                    Set<E> nodeClass = new HashSet<>(subNodeClass);
                    nodeClasses.add(nodeClass);
                }

                for (E e : subNodeClass) {
                    this.operation2NodeClasses.put(e, i);
                }
            }
        }

    }

    public Map<E, Integer> getOperation2NodeClasses() {
        if (operation2NodeClasses == null) {
            calculateComponentsAndNodeClasses();
        }
        return operation2NodeClasses;
    }

    public List<Set<E>> getNodeClasses() {
        if (nodeClasses == null) {
            calculateComponentsAndNodeClasses();
        }
        return nodeClasses;
    }

    public Map<AoNComponent<E>, List<Set<E>>> getConnectionComponents() {
        if (connectionComponents == null) {
            calculateComponentsAndNodeClasses();
        }
        return connectionComponents;
    }

    public ActivityOnNodeGraph<E> getSubGraph(Collection<E> verticesOfSubGraph) {
        return new ActivityOnNodeGraph<>(this, verticesOfSubGraph);
    }

    private ActivityOnNodeGraph(ActivityOnNodeGraph<E> ursprungsGraph, Collection<E> verticesOfSubGraph) {
        super();

        for (E vertex : verticesOfSubGraph) {
            this.addVertexWithoutCheckContains(vertex);
        }
        for (E vertex : verticesOfSubGraph) {
            for (E suc : ursprungsGraph.getSuccessors(vertex)) {
                if (!verticesOfSubGraph.contains(suc)) {
                    continue;
                }
                Pair<E, E> pair = new Pair<>(vertex, suc);
                this.addEdgeWithoutCheckContains(pair);
                this.setEdgeWeight(pair, ursprungsGraph.getEdgeWeight(pair));
            }
        }
    }

    public void add(ActivityOnNodeGraph<E> graph) {
        this.connectionComponents = null;
        this.vertexToComponent = null;
        this.operation2NodeClasses = null;
        this.nodeClasses = null;
        this.vertices.addAll(graph.vertices);
        this.vertexMap.putAll(graph.vertexMap);
        this.edges.addAll(graph.edges);
        this.vertexWeights.putAll(graph.vertexWeights);
        this.edgeWeights.putAll(graph.edgeWeights);
    }
}
