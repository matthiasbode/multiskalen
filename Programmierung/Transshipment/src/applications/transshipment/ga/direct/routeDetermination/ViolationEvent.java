/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.routeDetermination;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.util.Stack;

/**
 *
 * @author bode
 */
public class ViolationEvent {

    public Stack<ActivityOnNodeGraph<RoutingTransportOperation>> componentsToVerify;
    public ActivityOnNodeGraph<RoutingTransportOperation> connectionComponent;

    public ViolationEvent(Stack<ActivityOnNodeGraph<RoutingTransportOperation>> componentsToVerify, ActivityOnNodeGraph<RoutingTransportOperation> connectionComponent) {
        this.componentsToVerify = componentsToVerify;
        this.connectionComponent = connectionComponent;
    }
}
