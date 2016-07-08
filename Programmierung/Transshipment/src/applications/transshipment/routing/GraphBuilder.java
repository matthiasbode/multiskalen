/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing;

import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 * Bildet für einen Job einen SpecifiedTransportGraph, der alle Möglichkeiten
 * enthält, auf die ein Job ausgeführt werden kann.
 *
 * @author bode
 */
public interface GraphBuilder {

    public TransportGraph getStaticTransportGraph();

    public SpecifiedTransportGraph getGraphForJob(final LoadUnitJob job, long currentTime);

    public SpecifiedTransportGraph getGraphForJob(final LoadUnitJob job, LoadUnitStorage startStorage, LoadUnitStorage zielStorage, long currentTime);
}
