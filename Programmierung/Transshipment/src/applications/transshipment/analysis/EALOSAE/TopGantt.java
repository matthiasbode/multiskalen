/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.EALOSAE;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;
import org.graph.algorithms.AcyclicTest;
import org.graph.algorithms.TopologicalSort;

import org.graph.directed.DefaultDirectedGraph;
import org.graph.directed.DirectedGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryLineAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.util.Pair;

/**
 *
 * @author schierm
 */
public class TopGantt {

    enum Output {

        SCREEN, PRINT
    };
    static Output output = Output.SCREEN;

    public static JFreeChart createGantt(DirectedGraph<? extends Operation> subGraph, final Map<? extends Operation, EarliestAndLatestStartsAndEnds> ealosaes) {
        HashMap<Operation, Long> nodePositionMap = new HashMap<>();
        // create Dataset
        String b = "";
        String ee = "Earliest Execution";
        String in = "Intersection";
        String ex = "Possible Execution";
        String le = "Latest Execution";
        FieldElement min = new LongValue(Long.MAX_VALUE);
        FieldElement max = new LongValue(Long.MIN_VALUE);

        ArrayList<Operation> nodes = new ArrayList<>(subGraph.vertexSet());
        ArrayList<Pair<Operation, Operation>> edges = new ArrayList<>();
        for (Pair<? extends Operation, ? extends Operation> pair : subGraph.edgeSet()) {
            edges.add(new Pair<>(pair.getFirst(), pair.getSecond()));
        }

        DirectedGraph<Operation> graph = new DefaultDirectedGraph<>(nodes, edges);

        /**
         * Sortieren der Knoten
         */
        AcyclicTest<Operation> acyclic = new AcyclicTest<>(graph);
        if (acyclic.isAcyclic()) {
            List<Set<Operation>> topologicalSort = TopologicalSort.<Operation>topologicalSort(graph);
            final LinkedHashMap<Operation, Integer> raenge = new LinkedHashMap<>();

            for (int i = 0; i < topologicalSort.size(); i++) {
                for (Operation transportOperation : topologicalSort.get(i)) {
                    raenge.put(transportOperation, i);
                }
            }

            Collections.sort(nodes, new Comparator<Operation>() {
                @Override
                public int compare(Operation arg0, Operation arg1) {
                    return raenge.get(arg0).compareTo(raenge.get(arg1));
                }
            });
        } else {
            Collections.sort(nodes, new Comparator<Operation>() {
                @Override
                public int compare(Operation arg0, Operation arg1) {
                    EarliestAndLatestStartsAndEnds ealosae0 = ealosaes.get(arg0);
                    EarliestAndLatestStartsAndEnds ealosae1 = ealosaes.get(arg1);
                    FieldElement mitte0 = ealosae0.getEarliestStart().add(ealosae0.getLatestEnd().sub(ealosae0.getEarliestStart())).div(new DoubleValue(2));
                    FieldElement mitte1 = ealosae1.getEarliestStart().add(ealosae1.getLatestEnd().sub(ealosae1.getEarliestStart())).div(new DoubleValue(2));
                    return (mitte0).compareTo(mitte1);
                }
            });
        }

        /**
         * Min-Max-Bestimmung
         */
        for (Operation node : nodes) {
            min = min.isLowerThan(ealosaes.get(node).getEarliestStart()) ? min : ealosaes.get(node).getEarliestStart();
            max = max.isGreaterThan(ealosaes.get(node).getLatestEnd()) ? max : ealosaes.get(node).getLatestEnd();
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Operation node : nodes) {
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(node);
            String top = getFormattedString(node);
            long nodePosition;
            dataset.addValue(ealosae.getEarliestStart().longValue(), b, top);
            if (ealosae.getEarliestStart() == ealosae.getLatestStart() && ealosae.getEarliestEnd() == ealosae.getLatestEnd()) {
                dataset.addValue(ealosae.getEarliestEnd().longValue() - ealosae.getEarliestStart().longValue(), ex, top);
                nodePosition = (ealosae.getEarliestEnd().longValue() + ealosae.getEarliestStart().longValue()) / 2;
            } else if (ealosae.getLatestStart().isLowerThan(ealosae.getEarliestEnd())) {
                dataset.addValue(ealosae.getLatestStart().longValue() - ealosae.getEarliestStart().longValue(), ee, top);
                dataset.addValue(ealosae.getEarliestEnd().longValue() - ealosae.getLatestStart().longValue(), in, top);
                dataset.addValue(ealosae.getLatestEnd().longValue() - ealosae.getEarliestEnd().longValue(), le, top);
                nodePosition = (ealosae.getEarliestStart().longValue() + ealosae.getLatestEnd().longValue()) / 2;
            } else {
                dataset.addValue(ealosae.getEarliestEnd().longValue() - ealosae.getEarliestStart().longValue(), ee, top);
                dataset.addValue(ealosae.getLatestStart().longValue() - ealosae.getEarliestEnd().longValue(), ex, top);
                dataset.addValue(ealosae.getLatestEnd().longValue() - ealosae.getLatestStart().longValue(), le, top);
                nodePosition = (ealosae.getEarliestStart().longValue() + ealosae.getLatestEnd().longValue()) / 2;
            }
            nodePositionMap.put(node, nodePosition);
        }

        JFreeChart chart = ChartFactory.createStackedBarChart("Title", "x-Achse", "y-Achse", dataset, PlotOrientation.HORIZONTAL, true, true, true);
        CategoryPlot categoryPlot = chart.getCategoryPlot();

        if (output == Output.PRINT) {
            categoryPlot.setBackgroundPaint(Color.WHITE);
            categoryPlot.setRangeGridlinePaint(Color.black);
            categoryPlot.setDomainGridlinePaint(Color.black);
        }

        DateAxis dateAxis = new DateAxis("DateAxis");
        dateAxis.setRange(new Date(min.longValue()), new Date(max.longValue()));
        categoryPlot.setRangeAxis(dateAxis);

        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setMaximumCategoryLabelLines(5);

        BarRenderer barRenderer = (BarRenderer) categoryPlot.getRenderer();
        barRenderer.setBarPainter(new StandardBarPainter());
        if (output == Output.PRINT) {
            barRenderer.setBarPainter(new StandardBarPainter());
            barRenderer.setDrawBarOutline(false);
        }
        Color color = new Color(0, 0, 0, 0);
        barRenderer.setSeriesPaint(0, color);

        if (output == Output.PRINT) {
            barRenderer.setSeriesPaint(1, new Color(155, 155, 155));
            barRenderer.setSeriesPaint(2, new Color(220, 220, 220));
            barRenderer.setSeriesPaint(3, new Color(140, 140, 140));

            barRenderer.setShadowVisible(false);
        }
        Paint paint = new Color(0, 0, 0);
        Stroke stroke = new ArrowStroke(false);
        Stroke dashedStroke = new ArrowStroke(true);

        for (Operation node : subGraph.vertexSet()) {
            for (Pair<? extends Operation, ? extends Operation> edge : subGraph.edgeSet()) {
                if (edge.getFirst().equals(node)) {
                    if (edge.getFirst() instanceof RoutingTransportOperation) {
                        RoutingTransportOperation f1 = (RoutingTransportOperation) edge.getFirst();
                        RoutingTransportOperation f2 = (RoutingTransportOperation) edge.getSecond();
                        if (f1.getLoadUnit().equals(f2.getLoadUnit())) {
                            categoryPlot.addAnnotation(new CategoryLineAnnotation(getFormattedString(node), nodePositionMap.get(node), getFormattedString(edge.getSecond()), nodePositionMap.get(edge.getSecond()), paint, stroke));
                        } else {
                            categoryPlot.addAnnotation(new CategoryLineAnnotation(getFormattedString(node), nodePositionMap.get(node), getFormattedString(edge.getSecond()), nodePositionMap.get(edge.getSecond()), paint, dashedStroke));
                        }
                    }
                    else{
                        categoryPlot.addAnnotation(new CategoryLineAnnotation(getFormattedString(node), nodePositionMap.get(node), getFormattedString(edge.getSecond()), nodePositionMap.get(edge.getSecond()), paint, dashedStroke));
                    }
                }
            }
        }

        return chart;
    }

    private static String getFormattedString(Operation n) {
        if (n instanceof RoutingTransportOperation) {
            RoutingTransportOperation node = (RoutingTransportOperation) n;
            return node.getLoadUnit().getID() + ": [" + node.getOrigin().toString().substring(0, Math.min(node.getOrigin().toString().length() - 1, 40)) + "] -> [" + node.getDestination().toString().substring(0, Math.min(node.getDestination().toString().length() - 1, 40)) + "] BY: [" + node.getResource() + "]";
        } else {
            return n.toString();

        }
    }

    private static class ArrowStroke implements Stroke {

        final double gamma = Math.PI / 6;
        final double l = 10;
        float[] pattern = {10f};
        float thickness = 3f;
        boolean dash;

        public ArrowStroke(boolean dash) {
            this.dash = dash;
        }

        @Override
        public Shape createStrokedShape(Shape shape) {
            Line2D.Float line = (Line2D.Float) shape;
            Path2D.Float arrow = new Path2D.Float();
            double alpha = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);

            arrow.moveTo(line.x1, line.y1);
            arrow.lineTo(line.x2, line.y2);

            float x = (float) (line.x2 - l * Math.cos(alpha + gamma));
            float y = (float) (line.y2 - l * Math.sin(alpha + gamma));
            arrow.lineTo(x, y);

            arrow.moveTo(line.x2, line.y2);
            x = (float) (line.x2 - l * Math.cos(alpha - gamma));
            y = (float) (line.y2 - l * Math.sin(alpha - gamma));
            arrow.lineTo(x, y);

            BasicStroke basicStroke;
            if (dash) {
                basicStroke = new BasicStroke(thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f, pattern, 0.0f);
            } else {
                basicStroke = new BasicStroke(thickness);
            }

            return basicStroke.createStrokedShape(arrow);
        }
    }
}
