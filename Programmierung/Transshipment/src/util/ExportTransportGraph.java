package util;

import applications.transshipment.routing.TransferArea;
import applications.transshipment.routing.TransportGraph;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Set;
import javax.vecmath.Point2d;
import org.graph.weighted.Weighted;
import org.util.Pair;

/**
 *
 *
 * /**
 * Klasse, die eine DirectedGraph und alle davon abgeleiteten Klassen in einer
 * Datei für YED schreibt.
 *
 * In YED kann dann das Layout des Graphens angepasst werden. (Menüpunkt:
 * Layout)
 *
 * @author bode
 */
public class ExportTransportGraph {

    public static <E> void exportToGraphML(TransportGraph graph, String filename) {
        String s = "";

        s += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                + "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:y=\"http://www.yworks.com/xml/graphml\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">\n"
                + "<!--Created by yFiles for Java 2.7-->\n"
                + "<key for=\"graphml\" id=\"d0\" yfiles.type=\"resources\"/>\n"
                + "<key attr.name=\"url\" attr.type=\"string\" for=\"node\" id=\"d1\"/>\n"
                + "<key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d2\"/>\n"
                + "<key for=\"node\" id=\"d3\" yfiles.type=\"nodegraphics\"/>\n"
                + "<key attr.name=\"Beschreibung\" attr.type=\"string\" for=\"graph\" id=\"d4\">\n"
                + "<default/>\n"
                + "</key>\n"
                + "<key attr.name=\"url\" attr.type=\"string\" for=\"edge\" id=\"d5\"/>\n"
                + "<key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d6\"/>\n"
                + "<key for=\"edge\" id=\"d7\" yfiles.type=\"edgegraphics\"/>\n"
                + "<graph edgedefault=\"directed\" id=\"G\">";

        ArrayList<TransferArea> sortedVertices = new ArrayList<TransferArea>(graph.vertexSet());

        for (int i = 0; i < sortedVertices.size(); i++) {
            TransferArea vertex = sortedVertices.get(i);
            Point2d p = vertex.getCenterOfGeneralOperatingArea();
            double x = p.x*10;
            double y = p.y*10;

            s += "<node id=\"n" + i + "\">\n"
                    + "<data key=\"d3\">\n"
                    + "<y:ShapeNode>\n"
                    + "<y:Geometry height=\"30.0\" width=\"500.0\" x=\"" + x + "\" y=\"" + y + "\"/>\n"
                    + "<y:Fill color=\"#FFCC00\" transparent=\"false\"/>\n"
                    + "<y:BorderStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n"
                    + "<y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" height=\"17.96875\" modelName=\"internal\" modelPosition=\"c\" textColor=\"#000000\" visible=\"true\">" + vertex.toString() + "</y:NodeLabel>\n"
                    + "<y:Shape type=\"rectangle\"/>\n"
                    + "</y:ShapeNode>\n"
                    + "</data>\n"
                    + "</node>\n";

        }

        int ei = 0;
        Set<Pair<TransferArea, TransferArea>> edges = graph.edgeSet();

        for (Pair<TransferArea, TransferArea> pair : edges) {
            int source = 0;
            int target = 0;
            for (int i = 0; i < sortedVertices.size(); i++) {
                TransferArea vertex = sortedVertices.get(i);
                if (vertex.equals(pair.getFirst())) {
                    source = i;
                }
                if (vertex.equals(pair.getSecond())) {
                    target = i;
                }
            }

            String gewicht = null;
            if (graph instanceof Weighted) {
                Weighted wg = (Weighted) graph;
                if (wg.getEdgeWeight(pair) != null) {
                    gewicht = wg.getEdgeWeight(pair).toString();
                }
            }

            s += "<edge id=\"e" + ei++ + "\" source=\"n" + source + "\" target=\"n" + target + "\">\n"
                    + "<data key=\"d7\">\n"
                    + "<y:PolyLineEdge>\n"
                    + "<y:Path sx=\"0.0\" sy=\"0.0\" tx=\"0.0\" ty=\"0.0\"/>\n"
                    + "<y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\"/>\n";

            s += "<y:Arrows source=\"none\" target=\"standard\"/>\n";

            if (gewicht != null) {
                s += "<y:EdgeLabel alignment=\"center\" distance=\"2.0\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\"  modelName=\"six_pos\" modelPosition=\"tail\" preferredPlacement=\"anywhere\" ratio=\"0.5\" textColor=\"#000000\" visible=\"true\">" + gewicht + "</y:EdgeLabel>\n";
            }
            s += "<y:BendStyle smoothed=\"false\"/>\n"
                    + "</y:PolyLineEdge>\n"
                    + "</data>\n"
                    + "</edge>";
        }

        s += "  </graph>"
                + "<data key=\"d0\">"
                + "<y:Resources/>"
                + "</data>"
                + "</graphml>";

        Writer fw = null;

        try {
            fw = new FileWriter(filename);
            fw.write(s);
        } catch (IOException e) {
            System.err.println("Konnte Datei nicht erstellen");
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
