package applications.transshipment.model.eval;

import applications.transshipment.model.operations.storage.StoreOperation;
import java.util.Comparator;

/**
 *
 * @author hofmann
 */
public class StoreOperationComparator implements Comparator<StoreOperation> {

    private EvalFunction_StoreOperation function;

    public StoreOperationComparator(EvalFunction_StoreOperation function) {
        this.function = function;
    }

    @Override
    public int compare(StoreOperation o1, StoreOperation o2) {

        //TODO: fuer Performance Bewertung zwischenspeichern
        if (function.evaluate(o1) > function.evaluate(o2)) {
            return 1;
        }
        if (function.evaluate(o1) < function.evaluate(o2)) {
            return -1;
        } else {
            return Double.compare(o1.getResource().getCenterOfGeneralOperatingArea().getX(), o2.getResource().getCenterOfGeneralOperatingArea().getX());
        }
    }

}
