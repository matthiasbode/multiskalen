/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.poly;

import applications.mmrcsp.model.operations.Operation;
import applications.transshipment.model.basics.TransportBundle;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.transport.TransportOperation;

import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import bijava.geometry.dim2.Point2d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import math.FieldElement;
import math.LongValue;
import org.graph.Path;
import org.graph.algorithms.Dijkstra;
import org.util.Pair;
import struct.ConvexPolytope;
import struct.Halfspace;
import struct.Hyperplane;
import struct.PiecewisePolytope;
import struct.Polytope;
import struct.Vector;
import applications.mmrcsp.model.schedule.rules.SharedResourceManager;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSHandover;
import applications.transshipment.model.resources.conveyanceSystems.lcs.MultiScaleLCSTransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.lcs.RackNode;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import struct.Point;

/**
 * Hier kommen die Tests rein, ob die Polytope sich nicht gegenseitig schneiden
 * etc.
 *
 * @author bode
 */
public class AreaManager implements SharedResourceManager<LCSystem> {

    private final Map<Agent, List<PolytopeOperation>> polytopeOperations;
    private final Map<Operation, Polytope> generatedPolytopes;
    private final LCSystem system;
    private final boolean POLYTOPE_BOUND_CHECK = false;

    private final long earliestStart;

    public AreaManager(LCSystem system) {
        polytopeOperations = new HashMap<>();
        generatedPolytopes = new HashMap<>();
        this.system = system;
        this.earliestStart = this.system.getSharingResources().iterator().next().getTemporalAvailability().getFromWhen().longValue();
    }

    public boolean isSchedulable(TransportBundle bundle, FieldElement start) {
        Dijkstra d = new Dijkstra();
        Agent agent = (Agent) bundle.getResource();

        Polytope idlePolytope, transportPolytope;
        TransportOperation top = bundle.getJ();
        IdleSettingUpOperation sqj = bundle.getSqj();
        LoadUnitStorage origin = sqj.getStart();
        LoadUnitStorage destination = sqj.getEnd();
        FieldElement ruestZeit = start.clone();

        {
            // Polytop für erste Rüstfahrt
            RackNode begin = null;
            RackNode end = null;
            if (!(origin instanceof LCSHandover) || !(destination instanceof LCSHandover)) {
                throw new UnsupportedOperationException("Momentan können nur Transporte zwischen Racks bearbeitet werden.");
            }
            LCSHandover rackOrigin = (LCSHandover) origin;
            if (rackOrigin.getStorageLocations().size() == 1) {
                begin = rackOrigin.getNodes().get(rackOrigin.getStorageLocations().iterator().next());
            } else {
                throw new UnsupportedOperationException("Der Origin umfasst mehr als einen Stellplatz. Hierfür muss noch der Racknode zwischen den "
                        + "Stellplätzen bestimmt werden.");
            }
            LCSHandover rackDestination = (LCSHandover) destination;
            if (rackDestination.getStorageLocations().size() == 1) {
                end = rackDestination.getNodes().get(rackDestination.getStorageLocations().iterator().next());
            } else {
                throw new UnsupportedOperationException("Die Destination umfasst mehr als einen Stellplatz. Hierfür muss noch der Racknode zwischen den "
                        + "Stellplätzen bestimmt werden.");
            }

            Path<Point2d> path = d.shortestPath(system.getGraph(), new Point2d(begin.x, begin.y), new Point2d(end.x, end.y));

            for (Pair<Point2d, Point2d> edge : path.getPathEdges()) {
                ruestZeit = ruestZeit.add(getTransportationTime(edge.getFirst(), edge.getSecond(), agent, false));
            }

            idlePolytope = generatePolytope(agent, path, start, false);
            for (List<PolytopeOperation> list : polytopeOperations.values()) {
                for (PolytopeOperation polytopeOperation : list) {
                    try {
                        if (polytopeOperation.getPolytope().intersects(idlePolytope)) {
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        origin = top.getOrigin();
        destination = top.getDestination();
        {

            /**
             * Können zum testen wieder auskommentiert werden, entsprechen den
             * Positionen des RackNodes.
             */
            RackNode begin = null;
            RackNode end = null;

            if (!(origin instanceof LCSHandover) || !(destination instanceof LCSHandover)) {
                throw new UnsupportedOperationException("Momentan können nur Transporte zwischen Racks bearbeitet werden.");
            }
            LCSHandover rackOrigin = (LCSHandover) origin;
            if (rackOrigin.getStorageLocations().size() == 1) {
                begin = rackOrigin.getNodes().get(rackOrigin.getStorageLocations().iterator().next());
            } else {
                throw new UnsupportedOperationException("Der Origin umfasst mehr als einen Stellplatz. Hierfür muss noch der Racknode zwischen den "
                        + "Stellplätzen bestimmt werden.");
            }

            LCSHandover rackDestination = (LCSHandover) destination;
            if (rackDestination.getStorageLocations().size() == 1) {
                end = rackDestination.getNodes().get(rackDestination.getStorageLocations().iterator().next());
            } else {
                throw new UnsupportedOperationException("Die Destination umfasst mehr als einen Stellplatz. Hierfür muss noch der Racknode zwischen den "
                        + "Stellplätzen bestimmt werden.");
            }

            // Polytop für Transportoperation
            Path<Point2d> path = d.shortestPath(system.getGraph(), new Point2d(begin.x, begin.y), new Point2d(end.x, end.y));

            transportPolytope = generatePolytope(agent, path, ruestZeit, true);
            for (List<PolytopeOperation> list : polytopeOperations.values()) {
                for (PolytopeOperation polytopeOperation : list) {
                    if (polytopeOperation.getPolytope().intersects(transportPolytope)) {
                        return false;
                    }
                }
            }
        }

        generatedPolytopes.put(top, transportPolytope);
        generatedPolytopes.put(sqj, idlePolytope);

        return true;
    }

    public void schedule(Operation o, Agent agent, FieldElement start) {
        List<PolytopeOperation> agentOperations = polytopeOperations.get(agent);
        if (agentOperations == null) {
            agentOperations = new ArrayList<>();
            polytopeOperations.put(agent, agentOperations);
        }

        if (o instanceof MultiScaleLCSTransportOperation) {
            MultiScaleLCSTransportOperation top = (MultiScaleLCSTransportOperation) o;
            top.getKeyPoints().clear();
            Polytope polytope = generatedPolytopes.get(top);
            for (Point enumerateVertice : polytope.enumerateVertices()) {
                System.out.println(enumerateVertice);
            }
        }
        agentOperations.add(new PolytopeOperation(generatedPolytopes.get(o), o));
    }

    public void unSchedule(Operation o, Agent agent) {
        List<PolytopeOperation> agentOperations = polytopeOperations.get(agent);
        PolytopeOperation operationToUnschedule = null;
        for (PolytopeOperation polytopeOperation : agentOperations) {
            if (polytopeOperation.operation.equals(o)) {
                operationToUnschedule = polytopeOperation;
            }
        }
        if (operationToUnschedule != null) {
            agentOperations.remove(operationToUnschedule);
        }
    }

    private Polytope generatePolytope(Agent agent, Path<Point2d> path, FieldElement start, boolean transport) {
        ArrayList<Polytope> pieces = new ArrayList<>();

        double opStart = start.longValue() - this.earliestStart;
        for (Pair<Point2d, Point2d> edge : path.getPathEdges()) {
            HashSet<Halfspace> spaces = new HashSet<>();
            double breite = 3;
            double tiefe = 9;
            double opEnd = opStart + getTransportationTime(edge.getFirst(), edge.getSecond(), agent, transport).doubleValue();
            if (!edge.getFirst().equals(edge.getSecond())) {
                Vector v1 = new Vector(opStart, edge.getFirst().x - breite / 2.0, edge.getFirst().y - tiefe / 2.0);
                Vector v2 = new Vector(opStart, edge.getFirst().x - breite / 2.0, edge.getFirst().y + tiefe / 2.0);
                Vector v3 = new Vector(opStart, edge.getFirst().x + breite / 2.0, edge.getFirst().y + tiefe / 2.0);
                Vector v4 = new Vector(opStart, edge.getFirst().x + breite / 2.0, edge.getFirst().y - tiefe / 2.0);

                Vector v5 = new Vector(opEnd, edge.getSecond().x - breite / 2.0, edge.getSecond().y - tiefe / 2.0);
                Vector v6 = new Vector(opEnd, edge.getSecond().x - breite / 2.0, edge.getSecond().y + tiefe / 2.0);
                //Vector v7 = new Vector(opEnd, edge.getSecond().x + breite / 2.0, edge.getSecond().y + tiefe / 2.0);
                Vector v8 = new Vector(opEnd, edge.getSecond().x + breite / 2.0, edge.getSecond().y - tiefe / 2.0);

                // Links
                Vector n1 = v2.sub(v1).cross(v4.sub(v1));
                Halfspace h1 = new Halfspace(new Hyperplane(n1, n1.mult(v1)));
                // Oben
                Vector n2 = v6.sub(v2).cross(v3.sub(v2));
                Halfspace h2 = new Halfspace(new Hyperplane(n2, n2.mult(v2)));
                // Hinten
                Vector n3 = v3.sub(v4).cross(v8.sub(v4));
                Halfspace h3 = new Halfspace(new Hyperplane(n3, n3.mult(v4)));
                // Unten
                Vector n4 = v5.sub(v8).cross(v4.sub(v8));
                Halfspace h4 = new Halfspace(new Hyperplane(n4, n4.mult(v8)));
                // Vorne
                Vector n5 = v6.sub(v5).cross(v1.sub(v5));
                Halfspace h5 = new Halfspace(new Hyperplane(n5, n5.mult(v5)));
                // Rechts
                Vector n6 = v8.sub(v5).cross(v6.sub(v5));
                Halfspace h6 = new Halfspace(new Hyperplane(n6, n6.mult(v5)));

                spaces.add(h1);
                spaces.add(h2);
                spaces.add(h3);
                spaces.add(h4);
                spaces.add(h5);
                spaces.add(h6);

                pieces.add(new ConvexPolytope(POLYTOPE_BOUND_CHECK, spaces));
            }
            opStart = opEnd;
        }
        PiecewisePolytope pwp = new PiecewisePolytope(pieces);
        return pwp;
    }

    public FieldElement getTransportationTime(Point2d origin, Point2d destination, Agent agent, boolean transport) {

        double distance = origin.distance(destination);
        /**
         * Heben uns senken!
         */
        long crabZMovement = 0;
        if (transport) {
            crabZMovement = 10l * 1000l;
        }

        long movementDuration = (long) (distance / agent.getVmax()) * 1000l + crabZMovement;
        return new LongValue(movementDuration);
    }

    @Override
    public LCSystem getResource() {
        return system;
    }

    public List<PolytopeOperation> getOperationsForAgent(Agent agent) {
        List<PolytopeOperation> ops = this.polytopeOperations.get(agent);
        if (ops == null) {
            return new ArrayList<>();
        }
        return ops;
    }
}
