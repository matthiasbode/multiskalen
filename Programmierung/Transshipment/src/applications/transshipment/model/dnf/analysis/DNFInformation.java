/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.dnf.analysis;

import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
/**
 *
 * @author bode
 */
public class DNFInformation {

    
    public int indexInRouting;
    public int indexInList;
    public int indexOfPreviousInList;
    public int indexOfOperationOnResource;
    public int numberOfOperationsSinceES;
    public double prevEALOSAEpercent;
    public double percentResource;
    public RoutingTransportOperation operation;
    public RoutingTransportOperation prevOperation;
    

    public DNFInformation(RoutingTransportOperation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "DNFInformation{" + "prevEALOSAEpercent=" + TransshipmentParameter.doubleFormat.format(prevEALOSAEpercent) + ",\t percentResource=" + TransshipmentParameter.doubleFormat.format(percentResource) + ",\t operation=" + operation + '}';
    }
}
