/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.operations;

import applications.mmrcsp.model.operations.Operation;
import java.util.Collection;
import java.util.LinkedHashMap;
import math.FieldElement;
import math.LongValue;

/**
 * Diese Klasse verwaltete SubOperationen für eine Operation. Operationen eines
 * Kranes z.B. bestehen aus Unteroperationen, beispielsweise der Reservierung
 * der Kranbahn. Diese werden in dieser Klasse gekapselt und können mit einem
 * zeitlichen Offsett zur Hauptoperation hinterlegt werden.
 *
 * @author bode
 */
public class SubOperations implements Cloneable {

    /**
     * Map zwischen den Operationen und dem zeitlichen Offset zur
     * Hauptoperation.
     */
    private LinkedHashMap<SubOperation, FieldElement> subOperations = new LinkedHashMap<>();

    public SubOperations(LinkedHashMap<SubOperation, FieldElement> subOperations) {
        this.subOperations = subOperations;
    }

    public SubOperations() {
    }

    public LinkedHashMap<SubOperation, FieldElement> getTimeOffset() {
        return subOperations;
    }

    public FieldElement getTimeOffset(SubOperation operation) {
        return subOperations.get(operation);
    }

    public Collection<SubOperation> getSubOperations() {
        return this.subOperations.keySet();
    }

    public void put(SubOperation op, FieldElement offset) {
        this.subOperations.put(op, offset);
    }

    public void put(SubOperation op) {
        this.subOperations.put(op, new LongValue(0));
    }

     
    public void clear(){
        this.subOperations.clear();
    }

    @Override
    public SubOperations clone() {
        LinkedHashMap<SubOperation, FieldElement> subOperationsClone = new LinkedHashMap<>();
        for (SubOperation k : this.subOperations.keySet()) {
            subOperationsClone.put(k.clone(), this.subOperations.get(k).clone());
        }
        SubOperations clone = new SubOperations(subOperationsClone);
        return clone;
    }

}
