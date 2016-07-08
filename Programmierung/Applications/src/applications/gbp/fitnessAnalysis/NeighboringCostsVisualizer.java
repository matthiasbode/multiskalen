/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.gbp.fitnessAnalysis;

 
import applications.gbp.BiPartition;
import applications.gbp.ma.BiPartitionIndividual;
import applications.gbp.ma.Codierer;
import ga.basics.FitnessEvalationFunction;
import ga.fittnessLandscapeAnalysis.fdplots.FitnessDistancePlotter;
import ga.listeners.IndividualEvent;
import ga.listeners.IndividualListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graph.weighted.WeightedDirectedGraph;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import util.SimpleLinkedSet;

/**
 *
 * @author bode
 */
public class NeighboringCostsVisualizer implements IndividualListener<BiPartitionIndividual> {
    XYSeries fitnessMeanCostChangeSeries = new XYSeries("fitnessMeanCostChangeSeries");
    WeightedDirectedGraph<Integer, Double> graph;
    ArrayList<Integer> nodes;
    FitnessEvalationFunction e;
    
    
    public NeighboringCostsVisualizer(WeightedDirectedGraph<Integer, Double> graph,  FitnessEvalationFunction e) {
        this.graph = graph;
        nodes = new ArrayList<Integer>(graph.vertexSet());
        this.e = e;
    }
    
     
    public void finished() {
        final XYSeriesCollection data = new XYSeriesCollection(fitnessMeanCostChangeSeries);
        final NumberAxis domainAxis = new NumberAxis("Fitness");
        final NumberAxis rangeAxis = new NumberAxis("relative costs change");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        final Plot plot = new XYPlot(data, domainAxis, rangeAxis, renderer);
        
        JFreeChart chart = new JFreeChart("Overall Fitness / Mean relative cost change",plot);
         

        try {
            ChartUtilities.saveChartAsPNG(new File("/home/bode/Desktop/N/overview.png"), chart, 1800, 700);
        } catch (IOException ex) {
            Logger.getLogger(FitnessDistancePlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void newIndividual(IndividualEvent<BiPartitionIndividual> event) {
        BiPartitionIndividual individual = event.individual;
        BiPartitionIndividual chrom = individual;
        BiPartition<Integer> bipartition = Codierer.getBipartition(graph, nodes, chrom);
        LinkedHashMap<Integer, Double> changes = new LinkedHashMap<Integer, Double>();
        
        double meanCostChange = 0;
        for (Integer currentNode : nodes) {
            BiPartition<Integer> tmp = new BiPartition<Integer>(bipartition);
            SimpleLinkedSet<Integer> setOfCurrentNode = tmp.getA().contains(currentNode)?tmp.getA(): tmp.getB();
            SimpleLinkedSet<Integer> otherSet = tmp.getA().contains(currentNode)?tmp.getB(): tmp.getA();
           
            SimpleLinkedSet<Integer> iterSet = new SimpleLinkedSet<Integer>(otherSet);
            double totalExternalCosts = tmp.getTotalExternalCosts();
            
            double costChange = 0;
            for (Integer otherNode : iterSet) {
                setOfCurrentNode.remove(currentNode);
                setOfCurrentNode.add(otherNode);
                otherSet.add(currentNode);
                otherSet.remove(otherNode);
                costChange += (tmp.getTotalExternalCosts() - totalExternalCosts);
                otherSet.remove(currentNode);
                otherSet.add(otherNode);
                setOfCurrentNode.add(currentNode);
                setOfCurrentNode.remove(otherNode);
            }
            meanCostChange+= costChange;
            changes.put(currentNode, costChange);
        }
        meanCostChange /= nodes.size();
        
        XYSeries series = new XYSeries("Neighboring costs");
        for (Integer nodeNr : changes.keySet()) {
            series.add(nodeNr, changes.get(nodeNr));
        }
        final XYSeriesCollection data = new XYSeriesCollection(series);
        final NumberAxis domainAxis = new NumberAxis("Knoten");
        final NumberAxis rangeAxis = new NumberAxis("relative costs");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        final Plot plot = new XYPlot(data, domainAxis, rangeAxis, renderer);
        individual.setFitness(e.computeFitness(individual));
        JFreeChart chart = new JFreeChart("MeanCostChange: "+meanCostChange +" Fit: "+individual.getFitness(),plot);
        fitnessMeanCostChangeSeries.add(individual.getFitness().doubleValue(), meanCostChange);

        try {
            ChartUtilities.saveChartAsPNG(new File("/home/bode/Desktop/N/" + individual + ".png"), chart, 1800, 700);
        } catch (IOException ex) {
            Logger.getLogger(FitnessDistancePlotter.class.getName()).log(Level.SEVERE, null, ex);
        }


        
    }
    
    
}
