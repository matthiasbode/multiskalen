/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.listeners.analysis;

import ga.individuals.Individual;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bode
 */
public class FitnessEvolution<E extends Individual> implements GAListener<E> {

    TreeMap<Integer, double[]> fitness = new TreeMap<>();
    File folder;

    public FitnessEvolution(File folder) {
        this.folder = folder;
    }

    @Override
    public void nextGeneration(GAEvent<E> event) {
        double[] newFitness = event.population.getFittestIndividual().getFitnessVector();
        Integer number = event.populationNumber;
        fitness.put(number, newFitness);
    }

    @Override
    public void finished(GAEvent<E> event) {

        int dimensions = fitness.get(0).length;
        XYSeries[] fitnessfunction = new XYSeries[dimensions];
        for (int i = 0; i < dimensions; i++) {
            fitnessfunction[i] = new XYSeries("Fitness");
        }

        for (Integer generation : fitness.keySet()) {
            double[] val = fitness.get(generation);
            for (int i = 0; i < val.length; i++) {
                double w = val[i];
                XYSeries xs = fitnessfunction[i];
                xs.add(generation, new Double(w));
            }

        }

        for (int i = 0; i < fitnessfunction.length; i++) {
            XYSeries xys = fitnessfunction[i];

            XYSeriesCollection col = new XYSeriesCollection();
            col.addSeries(xys);

            JFreeChart chart = ChartFactory.createXYLineChart("Fitness-Entwicklung", "# Generation", "Fitness", col, PlotOrientation.VERTICAL, true, true, false);
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinePaint(Color.BLACK);
            NumberAxis rangeAxis = new NumberAxis("Fitness");
            //        Welcher Abstand zwischen den Ticks
            rangeAxis.setTickUnit(new NumberTickUnit(1.0));

            plot.setRangeAxis(rangeAxis);
            NumberAxis domainAxis = new NumberAxis("Generations");
            //Welcher Abstand zwischen den Ticks
            domainAxis.setTickUnit(new NumberTickUnit(1.0));
            plot.setDomainAxis(domainAxis);
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            for (int j = 0; j < plot.getSeriesCount(); j++) {
                renderer.setSeriesStroke(j, new BasicStroke(3.5f));
            }
            // Renderer uebern
            // Renderer uebernehmen
            plot.setRenderer(renderer);
            File subFolder = new File(folder, "Optimization");
            subFolder.mkdirs();
            try {
                ChartUtilities.saveChartAsPNG(new File(subFolder, "Fitness_" + i + ".png"), chart, 1000, 500);
            } catch (IOException ex) {
                Logger.getLogger(FitnessEvolution.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                File f = new File(subFolder, "Fitness_" + i + ".dat");
                FileWriter fw = new FileWriter(f);
                BufferedWriter bw = new BufferedWriter(fw);

                for (Integer k : fitness.keySet()) {
                    bw.write(Double.toString(fitness.get(k)[i]));
                    bw.newLine();
                }
                bw.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(FitnessEvolution.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
