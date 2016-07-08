/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.plotter;

import applications.fuzzy.functions.LinearizedFunction1d;
import applications.fuzzy.functions.LinearizedFunction1d;
import applications.mmrcsp.model.basics.TimeSlot;
import bijava.math.function.ScalarFunction1d;
import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JFrame;
import math.FieldElement;
import math.function.StepFunction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
 

/**
 *
 * @author bode
 */
public class FunctionPlotterALT {

    static double dt = 0.005;

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
        JFreeChart chart = ChartFactory.createXYLineChart("", "Zeit [h]", "Auslastung [100%]", col, PlotOrientation.VERTICAL, true, true, true);
        
        JFrame frame = new JFrame();
        frame.setSize(850, 350);
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

    public static JFreeChart createChart(StepFunction function, TimeSlot ts, String title) {
        XYSeries timeSeries = new XYSeries("Zeit");
        for (FieldElement time : function.getSamplingPoints()) {
            FieldElement value = function.getValue(time);
            timeSeries.add(time.longValue(), value.doubleValue());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(timeSeries);

        DateAxis dateAxis = new DateAxis("Zeit");
        DateTickUnit unit = null;
        unit = new DateTickUnit(DateTickUnit.MINUTE, 10);
        DateFormat chartFormatter = new SimpleDateFormat("HH:mm");
        dateAxis.setDateFormatOverride(chartFormatter);
        dateAxis.setTickUnit(unit);
        dateAxis.setAutoRange(false);
        dateAxis.setRange(new DateRange(new Date(ts.getFromWhen().longValue() - 1), new Date(ts.getUntilWhen().longValue())));

        JFreeChart chart = ChartFactory.createXYStepChart(title, "Zeit", "Auslastung", dataset, PlotOrientation.VERTICAL, true, true, true);
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(dateAxis);

        return chart;
    }
    
    public static JFreeChart createFuzzyChart(LinearizedFunction1d function, double min, double max, String title) {
        
        XYSeries f = new XYSeries("Function");
        for (double x = min; x <= max; x += 0.0001) {
            if (x != Double.MAX_VALUE*(-1.) || x != Double.MAX_VALUE){
                double value = function.getValue(x);
                f.add(x, value);
            }
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(f);

        JFreeChart chart = ChartFactory.createXYLineChart(title, "x", "µ(x)", dataset, PlotOrientation.VERTICAL, true, true, true);
        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = (XYPlot) chart.getPlot();

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
