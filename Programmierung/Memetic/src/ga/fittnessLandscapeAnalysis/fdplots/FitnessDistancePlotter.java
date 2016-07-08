/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis.fdplots;

import ga.individuals.Individual;
import ga.metric.Metric;
import ga.fittnessLandscapeAnalysis.LocalOptimaCollection;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bode
 */
public class FitnessDistancePlotter<C extends Individual> implements GAListener<C> {

    private Metric<C> metric;
    private RangeFunction<C> rangeFunction;
    private C optimum;
    private XYSeries series;
    private JFreeChart chart;
    private LocalOptimaCollection<C> localOptimaCollection;

    public FitnessDistancePlotter(LocalOptimaCollection<C> localOptimaCollection, Metric<C> metric, RangeFunction<C> rangeFunction) {
        this.metric = metric;
        this.rangeFunction = rangeFunction;
        this.localOptimaCollection = localOptimaCollection;
    }

    public void initGraph(String domainAxisTitle, String rangeAxisTitle) {
        this.series = new XYSeries("FDPoints");
        final XYSeriesCollection data = new XYSeriesCollection(this.series);
        final NumberAxis domainAxis = new NumberAxis(domainAxisTitle);
        final NumberAxis rangeAxis = new NumberAxis(rangeAxisTitle);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        final Plot plot = new XYPlot(data, domainAxis, rangeAxis, renderer);
        chart = new JFreeChart(plot);
    }

   

    public void updatePoints() {
        series.clear();
        for (C individual : localOptimaCollection) {
            double distance = metric.distance(individual, optimum);
            double rangeValue = rangeFunction.getValue(individual);
            series.add(distance, rangeValue);
        }
    }

    public JFreeChart getChart() {
        return chart;
    }

    @Override
    public void nextGeneration(GAEvent<C> event) {
    }

    @Override
    public void finished(GAEvent<C> event) {
        this.optimum = event.population.getFittestIndividual();
        rangeFunction.setOptimum(optimum);
        updatePoints();
        try {
            ChartUtilities.saveChartAsPNG(new File("/home/bode/Desktop/fdplot.png"), chart, 1800, 700);
        } catch (IOException ex) {
            Logger.getLogger(FitnessDistancePlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
}
