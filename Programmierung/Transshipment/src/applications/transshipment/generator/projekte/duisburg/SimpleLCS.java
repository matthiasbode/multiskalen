/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.duisburg;

import applications.transshipment.generator.projekte.ParameterInputFile;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.HandoverPoint;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSHandover;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.RackGroup;
import applications.transshipment.model.resources.conveyanceSystems.lcs.RackNode;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import bijava.geometry.dim2.Point2d;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import org.graph.weighted.DefaultWeightedDirectedGraph;
import org.graph.weighted.WeightedDirectedGraph;

/**
 *
 * Die Klasse SimpleLCS hat eine Methode um eine einfach LCS zurückzugeben, die
 * aus 4 Spuren besteht. Auf den mittleren beiden Spuren herrscht Kreis-Verkehr
 * und zu den außenliegenden Lagerspuren gibt es zu und Abfahrten.
 *
 * @author bode
 */
public class SimpleLCS {

    enum Direction {

        toPoints,
        fromPoints,
        undirected
    };
    static final double breiteRackspur = 5.0;

    /**
     * *
     * Diese Methode erzeugt ein LCSystem mit eine zu definierenden Anzahl an
     * Spuren und Be-/Entladezonen.
     *
     * Die Variable angle gibt den Grad an, in dem die Verbindungen zwischen den
     * Fahrspuren erstellt werden sollen.
     */
    public static void getLCSystemByAngle(LCSystem system, ParameterInputFile parameters) {
        double angle = parameters.getDouble(ParameterInputFile.KEY_LCS_angle);
        int numberOfTracks = parameters.getInt(ParameterInputFile.KEY_LCS_numberOfTracks);
        double rackLength = parameters.getDouble(ParameterInputFile.KEY_LCS_lengthHandoverPoint);
        double rackDistance = parameters.getDouble(ParameterInputFile.KEY_LCS_distanceHandoverPoints);
        int nRacks = parameters.getInt(ParameterInputFile.KEY_LCS_numberOfHandoverPoints);

        Rectangle2D bounding = system.getGeneralOperatingArea().getBounds2D();

        double breiteAGVSpur = (bounding.getHeight() - 2 * breiteRackspur) / (numberOfTracks - 2);

        WeightedDirectedGraph<Point2d, Double> graph = new DefaultWeightedDirectedGraph<>();

        ArrayList<HandoverPoint> racks = new ArrayList<>();
        ArrayList<HandoverPoint> upperRacks = new ArrayList<>();
        ArrayList<HandoverPoint> lowerRacks = new ArrayList<>();

        /**
         * mittlere y-Position der Spuren bestimmen
         */
        double[] ypos = new double[numberOfTracks];
        ypos[0] = bounding.getMinY() + breiteRackspur / 2;
        ypos[1] = bounding.getMinY() + breiteRackspur + breiteAGVSpur / 2;
        ypos[2] = ypos[1] + breiteAGVSpur;
        ypos[3] = bounding.getMaxY() - breiteRackspur / 2;

        double rackStart = (bounding.getWidth() - nRacks * rackLength - (nRacks - 1) * rackDistance) / 2;
        if (rackStart < 0) {
            throw new RuntimeException("Ungueltige LCS-Konfiguration: rackStart darf nicht negativ werden");
        }
        double pos = rackStart;
        for (int j = 0; j < nRacks; j++, pos += (rackLength + rackDistance)) {
            for (int i = 0; i < 2; i++) {
                Rectangle2D rec = null;

                if (i == 0) {
                    rec = new Rectangle2D.Double(pos, bounding.getMinY(), rackLength, breiteRackspur);
                }
                if (i == 1) {
                    rec = new Rectangle2D.Double(pos, bounding.getMaxY() - breiteRackspur, rackLength, breiteRackspur);
                }

                HandoverPoint r = new HandoverPoint(new SimpleStorageRow(rec, 15));

                if (i == 0) {
                    lowerRacks.add(r);
                }

                if (i == 1) {
                    upperRacks.add(r);
                }
            }
        }
        racks.addAll(lowerRacks);
        racks.addAll(upperRacks);

        // Festlegen der Startracks für die AGVs
        if (system.getSharingResources().size() > racks.size()) {
            throw new IllegalArgumentException("More AGVs than Racks!");
        }
        Iterator<Agent> it = system.getSharingResources().iterator();

        int start = (lowerRacks.size()) / 2;
        int move = 0;
        int factor = -1;
        while (it.hasNext()) {
            HandoverPoint next = lowerRacks.get(start + move * factor);
            system.getStartingAgentPositions().put(it.next(), (HandoverPoint.SubHandoverPoint) next.getSubResource(next.getStorageLocations().iterator().next().getGeneralOperatingArea()));
            if (it.hasNext()) {
                next = upperRacks.get(start + move * factor);
                system.getStartingAgentPositions().put(it.next(), (HandoverPoint.SubHandoverPoint) next.getSubResource(next.getStorageLocations().iterator().next().getGeneralOperatingArea()));
            }
            factor *= -1;
            if (factor == 1) {
                move++;
            }
        }

        /**
         * Mittelpunkte zwischen den Racks bestimmen
         */
        TreeSet<Point2d> middleNodesLowerRacks = new TreeSet<Point2d>(new XPointComporator());
        TreeSet<Point2d> middleNodesUpperRacks = new TreeSet<Point2d>(new XPointComporator());

        double posMittelPunkt = rackStart - rackDistance / 2;
        for (int j = 0; j < nRacks + 1; j++, posMittelPunkt += (rackLength + rackDistance)) {
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    middleNodesLowerRacks.add(new Point2d(new Point2d(posMittelPunkt, bounding.getMinY() + (breiteRackspur / 2))));
                }
                if (i == 1) {
                    middleNodesUpperRacks.add(new Point2d(new Point2d(posMittelPunkt, bounding.getMaxY() - (breiteRackspur / 2))));
                }
            }
        }

        /**
         * Alle Punkte auf der Be-/ Entladespur setzen.
         */
        TreeSet<Point2d> allLowerNodes = new TreeSet<>(new XPointComporator());
        allLowerNodes.addAll(middleNodesLowerRacks);

        TreeSet<Point2d> allUpperNodes = new TreeSet<>(new XPointComporator());
        allUpperNodes.addAll(middleNodesUpperRacks);

        /**
         * Be- Entladepunkte auf den Racks setzen
         */
        for (HandoverPoint r : lowerRacks) {
            allLowerNodes.addAll(r.getRackNodes());
        }

        for (HandoverPoint r : upperRacks) {
            allUpperNodes.addAll(r.getRackNodes());
        }

        Iterator<Point2d> upperNodes = allUpperNodes.iterator();
        if (upperNodes.hasNext()) {
            Point2d node1 = upperNodes.next();
            while (upperNodes.hasNext()) {
                Point2d node2 = upperNodes.next();
                graph.addVertex(node1);
                graph.addVertex(node2);
                graph.addEdge(node2, node1, node2.distance(node1));
                graph.addEdge(node1, node2, node2.distance(node1));
                node1 = node2;
            }
        }

        Iterator<Point2d> lowerNodes = allLowerNodes.iterator();
        if (lowerNodes.hasNext()) {
            Point2d node1 = lowerNodes.next();

            while (lowerNodes.hasNext()) {
                Point2d node2 = lowerNodes.next();
                graph.addVertex(node1);
                graph.addVertex(node2);
                graph.addEdge(node1, node2, node2.distance(node1));
                graph.addEdge(node2, node1, node2.distance(node1));
                node1 = node2;
            }
        }

        /**
         * Es werden ArrayLists für die Knoten auf den Fahrspuren angelegt
         */
        TreeSet<Point2d> nodesUpperTrack = new TreeSet<Point2d>(new XPointComporator());
        TreeSet<Point2d> nodesLowerTrack = new TreeSet<Point2d>(new XPointComporator());

        /**
         * Die Querverbindungen für den Krebsgang zu den Racks werden erzeugt
         * und die dadurch auf der Fahrbahn entstandenen Knoten den beiden
         * ArrayLists nodesFahrspurOben und nodesFahrspurUnten hinzugefügt.
         */
        double m = Math.tan(Math.toRadians(-angle));
        TreeSet<Point2d> nodesToRacksUpperTrack = createDiagonalsToRack(graph, middleNodesUpperRacks, ypos[ypos.length - 2], m, Direction.undirected);
        nodesUpperTrack.addAll(nodesToRacksUpperTrack);

        TreeSet<Point2d> nodesToRacksLowerTrack = createDiagonalsToRack(graph, middleNodesLowerRacks, ypos[1], m, Direction.undirected);
        nodesLowerTrack.addAll(nodesToRacksLowerTrack);

        //Rückwärtskanten
        m = Math.tan(Math.toRadians(angle));
        TreeSet<Point2d> nodesFromRacksUpperTrack = createDiagonalsToRack(graph, middleNodesUpperRacks, ypos[ypos.length - 2], m, Direction.undirected);
        nodesUpperTrack.addAll(nodesFromRacksUpperTrack);
        TreeSet<Point2d> nodesFromRacksLowerTrack = createDiagonalsToRack(graph, middleNodesLowerRacks, ypos[1], m, Direction.undirected);
        nodesLowerTrack.addAll(nodesFromRacksLowerTrack);

        /**
         * Weiter Verbindungen zwischen den beiden mittleren Fahrspuren
         */
        //Verbindungen von der unteren zur oberen Fahrspur
        m = Math.tan(Math.toRadians(angle));
        nodesLowerTrack.addAll(createDiagonalsToRack(graph, nodesFromRacksUpperTrack, ypos[1], m, Direction.toPoints));
        nodesUpperTrack.addAll(createDiagonalsToRack(graph, nodesFromRacksLowerTrack, ypos[ypos.length - 2], m, Direction.fromPoints));

        //Verbindungen von der oberen zur unteren Fahrspur
        m = Math.tan(Math.toRadians(-angle));
        nodesUpperTrack.addAll(createDiagonalsToRack(graph, nodesToRacksLowerTrack, ypos[ypos.length - 2], m, Direction.toPoints));
        nodesLowerTrack.addAll(createDiagonalsToRack(graph, nodesToRacksUpperTrack, ypos[1], m, Direction.fromPoints));

        Iterator<Point2d> itUnten = nodesLowerTrack.iterator();
        if (itUnten.hasNext()) {
            Point2d node1 = itUnten.next();

            while (itUnten.hasNext()) {
                Point2d node2 = itUnten.next();
                graph.addEdge(node1, node2, node2.distance(node1));
                node1 = node2;
            }
        }

        Iterator<Point2d> itOben = nodesUpperTrack.iterator();
        if (itOben.hasNext()) {
            Point2d node1 = itOben.next();
            while (itOben.hasNext()) {
                Point2d node2 = itOben.next();
                graph.addEdge(node2, node1, node2.distance(node1));
                node1 = node2;
            }
        }

        system.setGraph(graph);
        RackGroup rackGroup = new RackGroup(racks);
        system.setHandoverPoints(rackGroup);
    }

    /**
     * Erzeugt die diagonalen Verbindungen im Graphen zu der Ladespur. Die
     * Berechnung erfolgt nach: Mittelpunkt zwischen den Racks als Punkt pr
     * Steigung der Geraden vom Punkt m aus: α = -60 Grad => m = tan(α).
     * Gleichung der Fahrspur i y= ypos[i] Punkt auf der Fahrspur P = (x,y) aus
     * Schnittpunkt der beiden Geraden p.y = ypos[i] (p.y - pr.y + m * pr.x) / m
     * = p.x
     *
     * @param graph Graph, dem die neuen Knoten hinzugefügt werden sollen.
     * @param startPoints Knoten der Mittelpunkte zwischen den Racks.
     * @param targetYpos Y-Position der Fahrspur.
     * @param m Steigung der Gerade.
     * @return Knoten, die auf der Fahrspur erzeugt wurden sind.
     */
    private static TreeSet<Point2d> createDiagonalsToRack(WeightedDirectedGraph<Point2d, Double> graph, Collection<Point2d> startPoints, double targetYpos, double m, Direction direction) {
        TreeSet<Point2d> result = new TreeSet<Point2d>(new XPointComporator());
        for (Point2d mNode : startPoints) {
            Point2d n = null;

            double x = (targetYpos - mNode.y + m * mNode.x) / m;
            n = new Point2d(new Point2d(x, targetYpos));

            graph.addVertex(mNode);
            graph.addVertex(n);
            if (direction == Direction.toPoints || direction == Direction.undirected) {
                graph.addEdge(n, mNode, n.distance(mNode));
            }
            if (direction == Direction.fromPoints || direction == Direction.undirected) {
                graph.addEdge(mNode, n, n.distance(mNode));
            }
            //Erzeugte Punkte werden der ArrayList angefügt und später zurückgegeben
            result.add(n);
        }
        return result;
    }

    static class XPointComporator implements Comparator<Point2d> {

        @Override
        public int compare(Point2d t1, Point2d t2) {
            if (t1.x < t2.x) {
                return -1;
            }
            if (t1.x > t2.x) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
