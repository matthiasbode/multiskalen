/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.direct.routeDetermination;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.basics.util.TransshipmentEALOSAEBuilder;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.graph.algorithms.TopologicalSort;

/**
 * Diese Klasse erstellt zusammenkomponentweise EALOSAEs. Weiterhin bietet sie
 * die Möglichkeit bei Fehlern beim Erzeugen der EALOSAE darauf zu reagieren,
 * indem Listener angemeldet werden können.
 *
 * @author bode
 */
public class ListenerEmbeddedEALOSAEBuilder {

    private final Map<ActivityOnNodeGraph<RoutingTransportOperation>, List<Set<RoutingTransportOperation>>> connectionComponents;
    private final ArrayList<EALOSAEViolationListener> listeners = new ArrayList<>();
    private final MultiJobTerminalProblem problem;
    private Collection<RoutingTransportOperation> choosenOperations;

    public ListenerEmbeddedEALOSAEBuilder(Collection<RoutingTransportOperation> choosenOperations, MultiJobTerminalProblem problem) {
        this.problem = problem;
        ActivityOnNodeGraph graph = problem.getActivityOnNodeDiagramm().getSubGraph(choosenOperations);
        this.connectionComponents = graph.getConnectionComponents();
        this.choosenOperations = choosenOperations;
    }

    public void addListener(EALOSAEViolationListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(EALOSAEViolationListener listener) {
        this.listeners.remove(listener);
    }

    public Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> getEALOSAES() {
        LinkedHashMap<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> result = new LinkedHashMap<>();
        /**
         * Schleife über die einzelnen Zusammenhangskomponenten, für die die
         * EALOSAES bestimmt werden.
         */
        Stack<ActivityOnNodeGraph<RoutingTransportOperation>> componentsToVerify = new Stack();
        componentsToVerify.addAll(connectionComponents.keySet());

        ComponentLoop:
        while (!componentsToVerify.isEmpty()) {
            /**
             * Hohle erstes Element
             */
            ActivityOnNodeGraph<RoutingTransportOperation> connectionComponent = componentsToVerify.pop();
            List<Set<RoutingTransportOperation>> topoSort = connectionComponents.get(connectionComponent);
            Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> componentEALOSAEs = TransshipmentEALOSAEBuilder.ealosaes(connectionComponent, topoSort, problem.getJobTimeWindows());
            if (componentEALOSAEs == null) {
                //Reagiere
                fireViolationEvent(new ViolationEvent(componentsToVerify, connectionComponent));
                continue ComponentLoop;
            }
            result.putAll(componentEALOSAEs);
        }
//        if (TransshipmentParameter.DEBUG) {
//            for (ActivityOnNodeGraph<RoutingTransportOperation> activityOnNodeGraph : connectionComponents.keySet()) {
//                System.out.println("Zusammenhangskomponente:");
//                for (RoutingTransportOperation routingTransportOperation : activityOnNodeGraph.vertexSet()) {
//                    System.out.println(routingTransportOperation);
//                    System.out.println(result.get(routingTransportOperation).getEarliestStart());
//                    System.out.println(result.get(routingTransportOperation).getLatestStart());
//                    System.out.println("-------------------");
//                }
//            }
//        }
        return result;
    }

    public ActivityOnNodeGraph<RoutingTransportOperation> addNewComponentAndEALOSAE(Collection<RoutingTransportOperation> newOperations) {
        ActivityOnNodeGraph<RoutingTransportOperation> newSupComponent =  problem.getActivityOnNodeDiagramm().getSubGraph(newOperations);
        List newTopoSort = TopologicalSort.topologicalSort(newSupComponent);
        connectionComponents.put(newSupComponent, newTopoSort);
        return newSupComponent;
    }

    public void removeComponentAndEALOSAE(ActivityOnNodeGraph<RoutingTransportOperation> connectionComponent) {
        connectionComponents.remove(connectionComponent);
    }

    public void fireViolationEvent(ViolationEvent event) {
        for (EALOSAEViolationListener eALOSAEViolationListener : listeners) {
            eALOSAEViolationListener.react(event);
        }
    }

    public Map<ActivityOnNodeGraph<RoutingTransportOperation>, List<Set<RoutingTransportOperation>>> getConnectionComponents() {
        return connectionComponents;
    }

}
