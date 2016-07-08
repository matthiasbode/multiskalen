/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.routing.baeiko;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.LoadUnitResource;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.routing.GraphBuilder;
import applications.transshipment.routing.RouteFinder;
import applications.transshipment.routing.SpecifiedTransportGraph;
import applications.transshipment.routing.TransferArea;
import applications.transshipment.routing.evaluation.EvalFunction_TransportOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Set;
import org.util.Pair;

/**
 *
 * @author Matthias
 */
public class DefaultRouteFinder extends RouteFinder {

    public static int maxTransportsPerRoute = 5;

    public DefaultRouteFinder(MultiJobTerminalProblem problem, List<LoadUnitResource> availableResources, EvalFunction_TransportOperation bewertung) {
        super(problem, availableResources, bewertung);
    }

    public DefaultRouteFinder(MultiJobTerminalProblem problem, GraphBuilder graphBuiler) {
        super(problem, graphBuiler);
    }

    @Override
    public List<JobOperationList<RoutingTransportOperation>> calcRoutes(LoadUnitJob job, SpecifiedTransportGraph graphForJob, int numberOfRoutes) {
        List<JobOperationList<RoutingTransportOperation>> result = new ArrayList<>();
        /**
         * Gewichten des Graphens
         */

        TimeSlot temporalAvailabilityOrigin = graphForJob.getJob().getOrigin().getTemporalAvailability().getAllOverTimeSlot();
        TimeSlot temporalAvailabilityDestination = graphForJob.getJob().getDestination().getTemporalAvailability().getAllOverTimeSlot();

        boolean notDirectTransportable = false;
        if (temporalAvailabilityOrigin.section(temporalAvailabilityDestination) == null) {
            notDirectTransportable = true;
        }
        job.setNotDirectlyTransportable(notDirectTransportable);

        SpecifiedTransportGraph specifiedTransportGraph = getSpecifiedTransportGraph(job);

        int counter = 0;
        while (result.size() < numberOfRoutes) {
            JobOperationList<RoutingTransportOperation> findWay = findWay(job, specifiedTransportGraph, maxTransportsPerRoute);
 
            if (findWay == null) {
                throw new NoSuchElementException("Es gibt nicht mehr als " + counter + " Routen für diesen Job");
            }
            RoutingTransportOperation firstRTOP = findWay.get(0);
            Transport edgeToDelete = new Transport(firstRTOP.getFirst(), firstRTOP.getSecond(), firstRTOP.getResource());
            boolean removeEdge = specifiedTransportGraph.removeEdge(edgeToDelete);
            if (!removeEdge) {
                throw new NoSuchElementException("Transport nicht im Graph auffindbar!");
            }
            if (TransshipmentParameter.ignoreNotDirectTrasportableAtRouting && counter == 0 && notDirectTransportable) {
                counter++;
                continue;
            }
            result.add(findWay);
            counter++;
        }

        return result;
    }

    public JobOperationList<RoutingTransportOperation> findWay(LoadUnitJob job, SpecifiedTransportGraph graphForJob, int max) { //diese Methode muss komplett erneuert werden;
        /**
         * Prioritaetswarteschlange, in der die Listen der noch zu besuchenden
         * Wege abgelegt werden.
         */ 
       final PriorityQueue<DijkstraPriorityContent<TransferArea, Pair<TransferArea, TransferArea>>> queue = new PriorityQueue<>();

        //Startknoten einfuegen
        final DijkstraPriorityContent<TransferArea, Pair<TransferArea, TransferArea>> startDijkstraPriorityContent = new DijkstraPriorityContent<TransferArea, Pair<TransferArea, TransferArea>>(
                graphForJob.getStart(), // Knoten
                0, // Bewertung
                new ArrayList<Pair<TransferArea, TransferArea>>()); // Bisher gegangener Weg
        queue.offer(startDijkstraPriorityContent);

        /*
         * Solange die Queue noch Elemente enthaelt
         */
        while (!queue.isEmpty()) {
            /*
             * activePriorityContent = Den PriorityContent mit dem geringsten
             * Abstand aus der Warteschlange nehmen
             */
            DijkstraPriorityContent<TransferArea, Pair<TransferArea, TransferArea>> activePriorityContent = queue.poll();

            /*
             * Wenn wir gerade beim Ziel angekommen sind, wird der Weg zurueckgegeben.
             */
            if (graphForJob.getZiel().equals(activePriorityContent.getNode())) {
                return (getRouting(job, graphForJob, activePriorityContent.getPathToThis(), activePriorityContent.getValue()));

            } else {

                /**
                 * wird der Weg zu lang, kann an dieser Stelle abgebrochen
                 * werden maxTransportsPerRoute +2 bis zum Ziel zulässig wegen
                 * virtueller Start- und Endkanten also hier noch +1 zulässig
                 */
                if (activePriorityContent.getPathToThis().size() > max + 1) {
                    continue;
                }

                ConveyanceSystem obligatoryNextConveyanceSystem = null;
                if (activePriorityContent.getNode() != graphForJob.getStart()) {
                    obligatoryNextConveyanceSystem = activePriorityContent.getNode().getConveyanceSystem1();
                    ConveyanceSystem lastUsedConveyanceSystem = graphForJob.getConveyanceSystem(activePriorityContent.getPathToThis().get(activePriorityContent.getPathToThis().size() - 1));
                    if (lastUsedConveyanceSystem != null && lastUsedConveyanceSystem == obligatoryNextConveyanceSystem) {
                        obligatoryNextConveyanceSystem = activePriorityContent.getNode().getConveyanceSystem2();
                    }

                }
                Set<Pair<TransferArea, TransferArea>> successorsEdges = graphForJob.outgoingEdgesOf(activePriorityContent.getNode());

//                Iterator<Transport> it = dynamicTransportGraph.edges(); //TODO: schneller, wenn Knoten ihre Nachfolger kennen
//                while (it.hasNext()) {
                for (Pair<TransferArea, TransferArea> dijkstraEdge : successorsEdges) {

                    /*
                     * Wenn der erste Knoten der Kante der aktuelle Knoten ist
                     */
                    ConveyanceSystem conveyanceSystem = graphForJob.getConveyanceSystem(dijkstraEdge);

                    if (conveyanceSystem != null && conveyanceSystem != obligatoryNextConveyanceSystem) {
                        continue;
                    }

                    final TransferArea secondNode = dijkstraEdge.getSecond();
                    /*
                     * Zweiter Knoten ist noch nicht in dieser Reihenfolge besucht worden
                     */

                    boolean createNewDpc = true;
                    ArrayList<Pair<TransferArea, TransferArea>> path = new ArrayList<>(activePriorityContent.getPathToThis());

                    //Zyklen rausfiltern
                    if (!path.isEmpty()) {
                        if (path.get(0).getFirst() == secondNode) {
                            createNewDpc = false;
                        } else {
                            for (Pair<TransferArea, TransferArea> trans : path) {
                                if (trans.getSecond() == secondNode) {
                                    createNewDpc = false;
                                    break;
                                }
                            }
                        }
                    }

                    if (createNewDpc) {
                        /*
                         * Neuen PriorityContent hinzufuegen.
                         */
                        path.add(dijkstraEdge);

                        DijkstraPriorityContent<TransferArea, Pair<TransferArea, TransferArea>> newDpc = new DijkstraPriorityContent<>(
                                secondNode, // Knoten
                                activePriorityContent.getValue() + graphForJob.getEdgeWeight(dijkstraEdge).doubleValue(), // Bewertung
                                path); // Gegangener Weg
//                            output.CTSO_Logger.println("Adding a new DPC: " + newDpc);
                        queue.offer(newDpc);
                        //replaceIfShorter(queue, newDpc);
                    }

                }
            }
//            output.CTSO_Logger.println(queue);
        }
        /* Kein Ziel gefunden bzw. es existiert kein Weg! */
        System.err.println("Keinen Weg gefunden fuer " + job);
        return null;
    }

    private JobOperationList<RoutingTransportOperation> getRouting(LoadUnitJob job, SpecifiedTransportGraph graph, ArrayList<Pair<TransferArea, TransferArea>> path, double value) {
        JobOperationList<RoutingTransportOperation> routing = new JobOperationList(job);

        path.remove(0);
        path.remove(path.size() - 1);
        for (Pair<TransferArea, TransferArea> transport : path) {

            ConveyanceSystem cs = graph.getConveyanceSystem(transport);
            if (cs == null) {
                throw new NoSuchElementException("Kein zugehöriges ConveyanceSystem gefunden!");
            }
            RoutingTransportOperation rtop = new RoutingTransportOperation(transport.getFirst(), transport.getSecond(), job, cs);
            routing.add(rtop);

        }
        routing.setWeight(value);
        return routing;
    }
}
