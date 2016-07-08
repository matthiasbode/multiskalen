package applications.transshipment.model.eval;

import applications.transshipment.model.operations.storage.StoreOperation;

 

/**
 *
 * @author hofmann
 */
public interface EvalFunction_StoreOperation {

    public double evaluate (StoreOperation sto);

}
