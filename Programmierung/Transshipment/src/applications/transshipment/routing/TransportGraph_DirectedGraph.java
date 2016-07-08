/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.graph.weighted.DoubleEdgeWeight;
import org.util.Pair;

/**
 * Graph, der die möglichen Transporte eines Systems abbildet. Eine zusätzliche
 * Map gibt an, über welches ConveyanceSystem der Transport (die Kante des
 * Graphens) abgefertigt wird.
 *
 * @author bode
 */
public class TransportGraph_DirectedGraph extends DefaultWeightedDirectedGraph<TransferArea, DoubleEdgeWeight> {

    private Map<Pair<TransferArea, TransferArea>, ConveyanceSystem> conveyanceSystems;

    public TransportGraph_DirectedGraph() {
        this.conveyanceSystems = new LinkedHashMap<>();
    }

    public TransportGraph_DirectedGraph(TransportGraph_DirectedGraph graph) {
        super(graph);
        this.conveyanceSystems = new LinkedHashMap<>(graph.conveyanceSystems);
    }

    public ConveyanceSystem getConveyanceSystem(Pair<TransferArea, TransferArea> pair) {
        return conveyanceSystems.get(pair);
    }

    public boolean addTransport(Pair<TransferArea, TransferArea> pair, ConveyanceSystem cs) {
        if (cs == null) {
            throw new IllegalArgumentException("Kein ConveyanceSystem gesetzt");
        }
        ConveyanceSystem old = this.conveyanceSystems.put(pair, cs);
        if (old != null && !old.equals(cs)) {
            throw new IllegalArgumentException("Zwei verschiedenen CS für einen Transport gesetzt. " + old + "\t" + cs);
        }
        boolean addEdge = this.addEdge(pair);
        if (addEdge == false) {
            throw new IllegalArgumentException("Kante konnte nicht hinzugefügt werden.");
        }
        return addEdge;
    }

}
