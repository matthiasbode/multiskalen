/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart;

import applications.fuzzy.functions.LinearizedFunction1d;
import applications.mmrcsp.model.basics.TimeSlot;
import bijava.math.function.ScalarFunction1d;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import math.function.StepFunction;
import math.FieldElement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bode
 */
public class FunctionPlotter {

    static double dt = 0.001;

//    public static void main(String[] args) {
//        System.out.println(new Date(2015, 0, 1, 0, 0, 0).getTime());
//    }
    public static void plotFunctions(HashMap<ScalarFunction1d, String> functions, double min, double max) {
        XYSeriesCollection col = new XYSeriesCollection();

        for (ScalarFunction1d function : functions.keySet()) {
            XYSeries f = new XYSeries(functions.get(function));
            for (double x = min; x <= max; x += dt) {
                double value = function.getValue(x);
                f.add(x, value);
            }
            col.addSeries(f);
        }
        JFreeChart chart = ChartFactory.createXYLineChart("Test", "", "", col, PlotOrientation.VERTICAL, true, true, true);
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        ChartPanel panel = new ChartPanel(chart);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void plotFunction(ScalarFunction1d function, double min, double max) {
        XYSeries f = new XYSeries("Function");
        for (double x = min; x <= max; x += dt) {
            double value = function.getValue(x);
            f.add(x, value);
        }
        XYSeriesCollection col = new XYSeriesCollection();
        col.addSeries(f);
        JFreeChart chart = ChartFactory.createXYLineChart("Test", "", "", col, PlotOrientation.VERTICAL, true, true, true);
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        ChartPanel panel = new ChartPanel(chart);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static JFreeChart createAreaDateChart(StepFunction function, TimeSlot ts, String title) {
        return FunctionPlotter.createAreaDateChart(function, ts, null, title, "Zeit", "Auslastung");
    }

    public static JFreeChart createAreaDateChart(StepFunction function, TimeSlot ts, Double max, String title) {
        return FunctionPlotter.createAreaDateChart(function, ts, max, title, "Zeit", "Auslastung");
    }

    public static JFreeChart createAreaDateChart(StepFunction function, TimeSlot ts, Double max, String title, String xLabel, String yLabel) {
        XYSeries timeSeries = new XYSeries("Zeit");
        for (FieldElement time : function.getSamplingPoints()) {
            FieldElement value = function.getValue(time);
            timeSeries.add((time.longValue()), value.doubleValue());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(timeSeries);

        JFreeChart chart = ChartFactory.createXYStepChart(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true, true);
        XYPlot plot = (XYPlot) chart.getPlot();

        XYStepAreaRenderer renderer = new XYStepAreaRenderer();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(3.5f));
        }
        // Renderer uebern
        // Renderer uebernehmen
        plot.setRenderer(renderer);

        DateAxis domainAxis = new DateAxis("Time [minutes]");
        domainAxis.setRange(new DateRange(new Date(ts.getFromWhen().longValue() - 1), new Date(ts.getUntilWhen().longValue() + 1)));
        plot.setDomainAxis(domainAxis);

        if (max != null) {
            NumberAxis range = new NumberAxis("Auslastung");
            range.setRange(0, max);
            plot.setRangeAxis(range);
        }
        return chart;
    }

    public static JFreeChart createAreaDateChart(Map<String, StepFunction> functions, TimeSlot ts, String title) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (String k : functions.keySet()) {
            XYSeries timeSeries = new XYSeries(k);
            for (FieldElement time : functions.get(k).getSamplingPoints()) {
                FieldElement value = functions.get(k).getValue(time);
                timeSeries.add((time.longValue()), value.doubleValue());
            }
            dataset.addSeries(timeSeries);
        }

        
        JFreeChart chart = ChartFactory.createXYStepChart(title, "Zeit", "Auslastung", dataset, PlotOrientation.VERTICAL, true, true, true);
        XYPlot plot = (XYPlot) chart.getPlot();
       
        XYStepAreaRenderer renderer = new XYStepAreaRenderer();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(3.5f));
        }
        // Renderer uebern
        // Renderer uebernehmen
        plot.setRenderer(renderer);

        DateAxis domainAxis = new DateAxis("Time [minutes]");
        domainAxis.setRange(new DateRange(new Date(ts.getFromWhen().longValue() - 1), new Date(ts.getUntilWhen().longValue())));
        plot.setDomainAxis(domainAxis);

        return chart;
    }

    public static JFreeChart createChart(LinearizedFunction1d function, TimeSlot ts, String title) {
        XYSeries timeSeries = new XYSeries("Zeit");
        for (Double time : function.getValues().keySet()) {
            double value = function.getValue(time);
            timeSeries.add((time.longValue()), value);
            Double higherKey = function.getValues().higherKey(time);
            if (higherKey != null) {
                double time2 = (time + higherKey) / 2;
                double value2 = function.getValue(time2);
                timeSeries.add(time2, value2);
            }
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(timeSeries);

        JFreeChart chart = ChartFactory.createXYStepChart(title, "Zeit", "Auslastung", dataset, PlotOrientation.VERTICAL, true, true, true);
        return chart;
    }

//
//    public static void createCharts(SortableHeatMapDataSet<Long, CellResource2D> set, File folder) {
//        /*
//         * Making seperate Plots for the Workload of distinct times per CellResource
//         */
//        int i = 0;
//        for (long time : set.getIdentifiers()) {
//            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//            int j = 0;
//            for (CellResource2D res : set.getCategories()) {
//                dataset.addValue(set.getValue(time, res), "Workload", "Zelle" + j);
//                j++;
//            }
//            DateFormat chartFormatter = new SimpleDateFormat("HH:mm");
//            DateFormat chartFormatter2 = new SimpleDateFormat("HH.mm");
//            String t = chartFormatter.format(new Date(time));
//            String t2 = chartFormatter2.format(new Date(time));
//            JFreeChart chart = ChartFactory.createBarChart(
//                    "Workload for t= " + t, // chart title
//                    "CellResources", // doamin axis label
//                    "Workload", // range axis label
//                    dataset, //data
//                    PlotOrientation.VERTICAL, // orientation
//                    true, //include legend
//                    false, //tooltips
//                    false //URLs
//            );
//            File f = new File(folder, "Zeit " + t2 + ".png");
//            try {
//                ChartUtilities.saveChartAsPNG(f, chart, 1900, 1000);
//            } catch (IOException ex) {
//                Logger.getLogger(FunctionPlotter.class.getName()).log(Level.SEVERE, "bla", ex);
//            }
//            i++;
//        }
//    }
}
