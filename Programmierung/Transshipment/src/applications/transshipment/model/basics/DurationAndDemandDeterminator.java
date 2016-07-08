/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics;

import applications.mmrcsp.model.operations.Operation;
import java.util.Collection;

/**
 * Weißt einer Collection von Operations ihre Dauer zu.
 * Die Dauer kann aus verschiedenen Quellen stammen.
 * @author bode
 */
public interface DurationAndDemandDeterminator {
    public void determine(Collection<Operation> operations);
}
