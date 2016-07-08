/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis;

import ga.algorithms.coevolving.individuals.SuperIndividual;
import ga.basics.Population;
import ga.individuals.Individual;
import ga.listeners.GAEvent;
import ga.listeners.GAListener;
import ga.listeners.analysis.FitnessEvolution;
import ga.listeners.coevolutionary.CoEvoGAEvent;
import ga.metric.Metric;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class Diversity<C extends Individual> implements GAListener<C> {

    HashMap<Class, TreeMap<Integer, Integer>> distances = new HashMap<>();
    HashMap<Class, Metric> metrices;

    File folder;

    public Diversity(File folder, HashMap<Class, Metric> m) {
        this.folder = folder;
        this.metrices = m;
    }

    public int getDistance(Population<C> p, Metric me) {
        ArrayList<C> inds = new ArrayList<C>(p.individuals());
        int distance = 0;
        for (int j = 0; j < inds.size() - 1; j++) {
            C Ij = inds.get(j);
            for (int js = j + 1; js < inds.size(); js++) {
                C Is = inds.get(js);
                distance += me.distance(Ij, Is);
            }
        }
        return distance;
    }

    @Override
    public void nextGeneration(GAEvent<C> event) {
        int distance = getDistance(event.population, metrices.get(event.population.getIndividualType()));
        TreeMap<Integer, Integer> map = distances.get(event.population.getIndividualType());
        if (map == null) {
            map = new TreeMap<>();
            distances.put(event.population.getIndividualType(), map);
        }
        map.put(event.populationNumber, distance);

        if (event instanceof CoEvoGAEvent) {
            CoEvoGAEvent ev = (CoEvoGAEvent) event;
            Collection<Population> pops = ev.subpopulations;
            for (Population subpopulation : pops) {
                Class individualType = subpopulation.getIndividualType();
                Metric metric = metrices.get(individualType);
                int subDistance = getDistance(subpopulation, metric);
                TreeMap<Integer, Integer> submap = distances.get(individualType);
                if (submap == null) {
                    submap = new TreeMap<>();
                    distances.put(individualType, submap);
                }
                submap.put(event.populationNumber, subDistance);
            }
        }
    }

    @Override
    public void finished(GAEvent<C> event) {
        for (Class type : distances.keySet()) {
            FileWriter fw = null;
            try {
                TreeMap<Integer, Integer> distance = distances.get(type);
                XYSeries xys = new XYSeries("Diversity ");
                for (Integer generation : distance.keySet()) {
                    Integer val = distance.get(generation);
                    xys.add(generation, val);
                }
                Integer maxValue = distance.values().iterator().next();
                Integer minValue = distance.values().iterator().next();
                for (Integer double1 : distance.values()) {
                    if (double1 > maxValue) {
                        maxValue = double1;
                    }
                    if (double1 < minValue) {
                        minValue = double1;
                    }
                }
                XYSeriesCollection col = new XYSeriesCollection();
                col.addSeries(xys);
                JFreeChart chart = ChartFactory.createXYLineChart("Diversität-Entwicklung " + type.getSimpleName(), "# Generation", "Diversität", col, PlotOrientation.VERTICAL, true, true, false);
                XYPlot plot = chart.getXYPlot();
                plot.setBackgroundPaint(Color.WHITE);
                plot.setRangeGridlinePaint(Color.BLACK);
                plot.setDomainGridlinePaint(Color.BLACK);
                NumberAxis rangeAxis = new NumberAxis("Diversität");
                //Welcher Abstand zwischen den Ticks
//        rangeAxis.setTickUnit(new NumberTickUnit(1.0));
                rangeAxis.setLowerBound(minValue * 0.95);
                rangeAxis.setUpperBound(maxValue * 1.05);
                plot.setRangeAxis(rangeAxis);
                NumberAxis domainAxis = new NumberAxis("Generations");
                //Welcher Abstand zwischen den Ticks
                domainAxis.setTickUnit(new NumberTickUnit(1.0));
                plot.setDomainAxis(domainAxis);
                XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
                for (int i = 0; i < plot.getSeriesCount(); i++) {
                    renderer.setSeriesStroke(i, new BasicStroke(3.5f));
                }
                // Renderer uebern
                // Renderer uebernehmen
                plot.setRenderer(renderer);
                File subFolder = new File(folder, "Optimization");
                subFolder.mkdirs();
                try {
                    ChartUtilities.saveChartAsPNG(new File(subFolder, "Diversity_" + type.getSimpleName() + ".png"), chart, 1000, 500);
                } catch (IOException ex) {
                    Logger.getLogger(FitnessEvolution.class.getName()).log(Level.SEVERE, null, ex);
                }
                File f = new File(subFolder, "Diversity_" + type.getSimpleName() + ".dat");
                fw = new FileWriter(f);
                BufferedWriter bw = new BufferedWriter(fw);
                for (Integer val : distance.values()) {
                    bw.write(Integer.toString(val));
                    bw.newLine();
                }
                bw.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Diversity.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fw.close();
                } catch (IOException ex) {
                    Logger.getLogger(Diversity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

}
