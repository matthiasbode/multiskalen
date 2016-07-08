/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.EALOSAE;

import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.problem.multiMode.MultiModeJobProblem;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.analysis.Analysis;
import static applications.transshipment.analysis.EALOSAE.TopGantt.output;
import applications.transshipment.ga.direct.decode.ExplicitModeDecoder;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.FieldElement;
import math.LongValue;
import org.graph.algorithms.AcyclicTest;
import org.graph.algorithms.TopologicalSort;
import org.graph.directed.DefaultDirectedGraph;
import org.graph.directed.DirectedGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryLineAnnotation;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
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
 * @author bode
 */
public class JobEalosaeAnalysis implements Analysis {

    public static JFreeChart createGantt(final LoadUnitJobSchedule schedule, DefaultDirectedGraph<RoutingTransportOperation> subGraph, List<Set<RoutingTransportOperation>> sort, final Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes) {
        HashMap<RoutingTransportOperation, Long> nodePositionMap = new HashMap<>();
        // create Dataset
        String b = "";
        String DNFb = "(DNF)";
        String ee = "Earliest Execution";
        String in = "Intersection";
        String ex = "Possible Execution";
        String le = "Latest Execution";
        String DNFee = "Earliest Execution (DNF)";
        String DNFin = "Intersection (DNF)";
        String DNFex = "Possible Execution (DNF)";
        String DNFle = "Latest Execution (DNF)";
        FieldElement min = new LongValue(Long.MAX_VALUE);
        FieldElement max = new LongValue(Long.MIN_VALUE);

        ArrayList<RoutingTransportOperation> nodes = new ArrayList<>(subGraph.vertexSet());
        ArrayList<Pair<RoutingTransportOperation, RoutingTransportOperation>> edges = new ArrayList<>();
        for (Pair<RoutingTransportOperation, RoutingTransportOperation> pair : subGraph.edgeSet()) {
            edges.add(new Pair<>(pair.getFirst(), pair.getSecond()));
        }

        DirectedGraph<RoutingTransportOperation> graph = new DefaultDirectedGraph<>(nodes, edges);

        /**
         * Sortieren der Knoten
         */
        AcyclicTest<RoutingTransportOperation> acyclic = new AcyclicTest<>(graph);
        if (acyclic.isAcyclic()) {
            List<Set<RoutingTransportOperation>> topologicalSort = TopologicalSort.<RoutingTransportOperation>topologicalSort(graph);
            final LinkedHashMap<RoutingTransportOperation, Integer> raenge = new LinkedHashMap<>();

            for (int i = 0; i < topologicalSort.size(); i++) {
                for (RoutingTransportOperation transportOperation : topologicalSort.get(i)) {
                    raenge.put(transportOperation, i);
                }
            }

            Collections.sort(nodes, new Comparator<RoutingTransportOperation>() {
                @Override
                public int compare(RoutingTransportOperation arg0, RoutingTransportOperation arg1) {
                    int compareTo = raenge.get(arg0).compareTo(raenge.get(arg1));
//                    return compareTo;
                    if (compareTo == 0) {
                        return Long.compare(ealosaes.get(arg0).getEarliestStart().longValue(), ealosaes.get(arg1).getEarliestStart().longValue());
                    }
                    return compareTo;
                }
            });
        } else {
            Collections.sort(nodes, new Comparator<RoutingTransportOperation>() {
                @Override
                public int compare(RoutingTransportOperation arg0, RoutingTransportOperation arg1) {
                    EarliestAndLatestStartsAndEnds ealosae0 = ealosaes.get(arg0);
                    EarliestAndLatestStartsAndEnds ealosae1 = ealosaes.get(arg1);
                    long mitte0 = ealosae0.getEarliestStart().longValue() + (ealosae0.getLatestEnd().longValue() - ealosae0.getEarliestStart().longValue()) / 2;
                    long mitte1 = ealosae1.getEarliestStart().longValue() + (ealosae1.getLatestEnd().longValue() - ealosae1.getEarliestStart().longValue()) / 2;
                    return ((Long) mitte0).compareTo(mitte1);
                }
            });
        }

        /**
         * Min-Max-Bestimmung
         */
        for (RoutingTransportOperation node : nodes) {
            min = min.isLowerThan(ealosaes.get(node).getEarliestStart()) ? min : ealosaes.get(node).getEarliestStart();
            max = max.isGreaterThan(ealosaes.get(node).getLatestEnd()) ? max : ealosaes.get(node).getLatestEnd();
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        boolean hasNonDNF = false;

        for (RoutingTransportOperation node : nodes) {
            EarliestAndLatestStartsAndEnds ealosae = ealosaes.get(node);
            String top = getFormattedString(node);
            long nodePosition;
            FieldElement startTime = schedule.get(node);
            if (startTime != null) {
                hasNonDNF = true;
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
            } else {

                dataset.addValue(ealosae.getEarliestStart().longValue(), DNFb, top);
                if (ealosae.getEarliestStart() == ealosae.getLatestStart() && ealosae.getEarliestEnd() == ealosae.getLatestEnd()) {
                    dataset.addValue(ealosae.getEarliestEnd().longValue() - ealosae.getEarliestStart().longValue(), DNFex, top);
                    nodePosition = (ealosae.getEarliestEnd().longValue() + ealosae.getEarliestStart().longValue()) / 2;
                } else if (ealosae.getLatestStart().isLowerThan(ealosae.getEarliestEnd())) {
                    dataset.addValue(ealosae.getLatestStart().longValue() - ealosae.getEarliestStart().longValue(), DNFee, top);
                    dataset.addValue(ealosae.getEarliestEnd().longValue() - ealosae.getLatestStart().longValue(), DNFin, top);
                    dataset.addValue(ealosae.getLatestEnd().longValue() - ealosae.getEarliestEnd().longValue(), DNFle, top);
                    nodePosition = (ealosae.getEarliestStart().longValue() + ealosae.getLatestEnd().longValue()) / 2;
                } else {
                    dataset.addValue(ealosae.getEarliestEnd().longValue() - ealosae.getEarliestStart().longValue(), DNFee, top);
                    dataset.addValue(ealosae.getLatestStart().longValue() - ealosae.getEarliestEnd().longValue(), DNFex, top);
                    dataset.addValue(ealosae.getLatestEnd().longValue() - ealosae.getLatestStart().longValue(), DNFle, top);
                    nodePosition = (ealosae.getEarliestStart().longValue() + ealosae.getLatestEnd().longValue()) / 2;
                }
                nodePositionMap.put(node, nodePosition);
            }

        }

        JFreeChart chart = ChartFactory.createStackedBarChart("Title", "x-Achse", "y-Achse", dataset, PlotOrientation.HORIZONTAL, true, true, true);
        CategoryPlot categoryPlot = chart.getCategoryPlot();

        if (output == TopGantt.Output.PRINT) {
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

        barRenderer.setDrawBarOutline(false);

        if (hasNonDNF) {
            barRenderer.setSeriesPaint(0, new Color(0, 0, 0, 0));
            barRenderer.setSeriesPaint(1, new Color(200, 211, 23));
            barRenderer.setSeriesPaint(2, new Color(0, 80, 155));
            barRenderer.setSeriesPaint(3, Color.RED);
            barRenderer.setSeriesPaint(4, new Color(0, 0, 0, 0));
            barRenderer.setSeriesPaint(5, new Color(200, 211, 23, 100));
            barRenderer.setSeriesPaint(6, new Color(0, 80, 155, 100));
            barRenderer.setSeriesPaint(7, new Color(255, 0, 0, 100));
        } else {
            barRenderer.setSeriesPaint(0, new Color(0, 0, 0, 0));
            barRenderer.setSeriesPaint(1, new Color(200, 211, 23, 100));
            barRenderer.setSeriesPaint(2, new Color(0, 80, 155, 100));
            barRenderer.setSeriesPaint(3, new Color(255, 0, 0, 100));
        }

        barRenderer.setShadowVisible(false);
        Paint paint = new Color(0, 0, 0);
        Stroke stroke = new ArrowStroke(false);
        Stroke dashedStroke = new ArrowStroke(true);

        for (RoutingTransportOperation node : subGraph.vertexSet()) {
            FieldElement startTime = schedule.get(node);
            if (startTime != null) {
                categoryPlot.addAnnotation(new CategoryPointerAnnotation("Eingeplant", getFormattedString(node), startTime.longValue(), Math.toRadians(90)));
            }
            for (Pair<RoutingTransportOperation, RoutingTransportOperation> edge : subGraph.edgeSet()) {
                if (edge.getFirst().equals(node)) {
                    if (edge.getFirst().getLoadUnit().equals(edge.getSecond().getLoadUnit())) {
                        categoryPlot.addAnnotation(new CategoryLineAnnotation(getFormattedString(node), nodePositionMap.get(node), getFormattedString(edge.getSecond()), nodePositionMap.get(edge.getSecond()), paint, stroke));
                    } else {
                        categoryPlot.addAnnotation(new CategoryLineAnnotation(getFormattedString(node), nodePositionMap.get(node), getFormattedString(edge.getSecond()), nodePositionMap.get(edge.getSecond()), paint, dashedStroke));
                    }
                }
            }
        }

        return chart;
    }

    private static String getFormattedString(RoutingTransportOperation node) {
        return node.getLoadUnit().getID() + " (" + node.getId() + ")" + ":  BY: [" + node.getResource() + "]";
    }

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {

        File subFolder = new File(folder, "Gantts");
        subFolder.mkdir();
        int i = 0;
//        for (Map.Entry<ActivityOnNodeGraph<RoutingTransportOperation>, List<Set<RoutingTransportOperation>>> entry : schedule.connectionComponents.entrySet()) {
//            JFreeChart chart = JobEalosaeAnalysis.createGantt(schedule, entry.getKey(), entry.getValue(), schedule.getEalosaes());
//            String name = "ZZ";
//            for (RoutingTransportOperation unscheduledTransportOperation : entry.getKey().vertexSet()) {
//                String id = unscheduledTransportOperation.getLoadUnit().getID();
//                if (id.compareTo(name) < 0) {
//                    name = id;
//                }
//            }
//            try {
//                ChartUtilities.saveChartAsPNG(new File(subFolder, name + ".png"), chart, 900, 500);
//            } catch (IOException ex) {
//                Logger.getLogger(ExplicitDecoder.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

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
