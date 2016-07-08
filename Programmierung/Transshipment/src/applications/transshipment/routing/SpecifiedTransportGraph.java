/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing;

import applications.transshipment.model.LoadUnitJob;
import java.util.List;

/**
 * Angepasster TransportGraph für einen bestimmten LoadUnitJob. Auf diesem
 * findet später die eigentliche Routensuche statt.
 *
 * @author bode
 */
public class SpecifiedTransportGraph extends TransportGraph {

    private LoadUnitJob job;
    private TransferArea start;
    private TransferArea ziel;

   
    public SpecifiedTransportGraph(LoadUnitJob job, TransportGraph graph, TransferArea start, TransferArea ziel) {
        super(graph);
        this.job = job;
        this.start = start;
        this.ziel = ziel;
    }

   

    public SpecifiedTransportGraph(LoadUnitJob job, TransportGraph graph) {
        super(graph);
        this.job = job;
        this.start = TransferArea.startTransferArea;
        this.ziel = TransferArea.endTransferArea;
    }

    public TransferArea getStart() {
        return start;
    }

    public void setStart(TransferArea start) {
        this.start = start;
        this.addVertex(start);
    }

    public TransferArea getZiel() {
        return ziel;
    }

    public void setZiel(TransferArea ziel) {
        this.ziel = ziel;
        this.addVertex(ziel);
    }

    
    public LoadUnitJob getJob() {
        return job;
    }
}
