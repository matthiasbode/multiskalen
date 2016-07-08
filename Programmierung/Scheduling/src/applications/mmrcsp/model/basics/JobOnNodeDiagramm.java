/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics;

import applications.mmrcsp.model.MultiModeJob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import org.graph.algorithms.ConnectionComponentGenerator;
import org.graph.directed.DefaultDirectedGraph;
import org.util.Pair;
import org.util.VertexContainer;

/**
 *
 * @author bode
 */
public class JobOnNodeDiagramm<J extends MultiModeJob> extends DefaultDirectedGraph<J> {

    private List<JoNComponent<J>> connectionComponents;

    public JobOnNodeDiagramm() {
    }

    public JobOnNodeDiagramm(JobOnNodeDiagramm<J> original, Collection<J> vertexToAdd) {
        super(original, vertexToAdd);
        this.connectionComponents = new ArrayList<>();
        for (JoNComponent<J> connectionComponent : original.connectionComponents) {
            this.connectionComponents.add(connectionComponent.clone());
        }

        for (J ver : original.vertices) {
            if (!vertexToAdd.contains(ver)) {
                this.removeVertex(ver);
                for (JoNComponent<J> connectionComponent : connectionComponents) {
                    if (connectionComponent.containsVertex(ver)) {
                        connectionComponent.removeVertex(ver);
                    }
                }
            }
        }
    }

    public JobOnNodeDiagramm(Collection<J> verticies, Collection<Pair<J, J>> edges) {
        super(verticies, edges);
    }

    public JobOnNodeDiagramm(Collection<J> verticies, Collection<Pair<J, J>> edges, List<JoNComponent<J>> connectionComponents) {
        super(verticies, edges);
        this.connectionComponents = connectionComponents;
    }

    public List<JoNComponent<J>> getConnectionComponents() {
        if (this.connectionComponents == null) {
            this.connectionComponents = new ArrayList<>();
            /**
             * Erzeuge Zusammenhangskomponenten und Topologische Sortierungen.
             */
            ConnectionComponentGenerator<J> ccgenerator = new ConnectionComponentGenerator(this);
            List<ConnectionComponentGenerator.ConnectionComponent<J>> cComponents = ccgenerator.calculateComponents();
            for (ConnectionComponentGenerator.ConnectionComponent<J> connectionComponent : cComponents) {
                this.connectionComponents.add(new JoNComponent<>(this, connectionComponent.getNodes(), connectionComponent.getEdges()));
            }
        }
        return connectionComponents;
    }

}
