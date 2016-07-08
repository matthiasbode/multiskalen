/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.routing.baeiko.Transport;
import java.util.LinkedHashMap;
import java.util.Map;
import org.graph.weighted.DoubleEdgeWeight;
import org.graph.weighted.WeightedMultigraph;
import org.util.Pair;

/**
 * Graph, der die möglichen Transporte eines Systems abbildet. Eine zusätzliche
 * Map gibt an, über welches ConveyanceSystem der Transport (die Kante des
 * Graphens) abgefertigt wird.
 *
 * @author bode
 */
public class TransportGraph extends WeightedMultigraph<TransferArea, DoubleEdgeWeight> {

    private Map<Pair<TransferArea, TransferArea>, ConveyanceSystem> conveyanceSystems;

    public TransportGraph() {
        this.conveyanceSystems = new LinkedHashMap<>();
    }

    public TransportGraph(TransportGraph graph) {
        super(graph);
        this.conveyanceSystems = new LinkedHashMap<>(graph.conveyanceSystems);
    }

    public ConveyanceSystem getConveyanceSystem(Pair<TransferArea, TransferArea> pair) {
        return conveyanceSystems.get(pair);
    }

    public boolean addTransport(Pair<TransferArea, TransferArea> pair, ConveyanceSystem cs) {
        if (pair instanceof Transport) {
            Transport t = (Transport) pair;
            conveyanceSystems.put(t, t.getConveyanceSystem());
            return super.addEdge(t);
        } else {
            Transport t = new Transport(pair.getFirst(), pair.getSecond(), cs);
            if (t.getFirst() != TransferArea.startTransferArea && t.getSecond() != TransferArea.endTransferArea) {
                if (cs == null) {
                    throw new IllegalArgumentException("Kein ConveyanceSystem gesetzt");
                }
            }
            ConveyanceSystem old = this.conveyanceSystems.put(t, cs);
            if (old != null && !old.equals(cs)) {
                throw new IllegalArgumentException("Zwei verschiedenen CS für einen Transport gesetzt. " + old + "\t" + cs);
            }
            boolean addEdge = super.addEdge(t);
            return addEdge;
        }

    }

    @Override
    public boolean addEdge(Pair<TransferArea, TransferArea> edge) {
        throw new UnsupportedOperationException("Über diese Methode können keine Kanten hinzugefügt werden. Bitte die addTransport-Methode nutzen.");
    }

    @Override
    public boolean removeEdge(Pair<TransferArea, TransferArea> e) {
        if (e instanceof Transport) {
            Transport t = (Transport) e;
            conveyanceSystems.remove(t);
            return super.removeEdge(t);
        }
        return false;
    }

}
