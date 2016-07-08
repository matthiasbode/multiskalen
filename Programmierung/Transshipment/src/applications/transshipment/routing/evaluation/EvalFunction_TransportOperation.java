  package applications.transshipment.routing.evaluation;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.routing.TransferArea;
import org.util.Pair;


 

/**
 * Bietet Methoden, um eine einzelne {@link TransportOperation} sowie eine
 * {@link TransportOperationSequence} zu bewerten.
 * Wird bei der Routensuche benötigt
 *
 * @author wagenkne
 */
public interface EvalFunction_TransportOperation {

    /**
     * Gibt die Bewertung fuer die uebergebene TransportOperation als
     * double zurueck.
     * Wird für das Suchen der Route benötigt
     *
     * @param transport   Die zu bewertende TransportOperation
     * @return      Die Bewertunng der Operation
     */
    public double evaluate(final Pair<TransferArea, TransferArea> transport, ConveyanceSystem conveyanceSystem, LoadUnit lu);
}