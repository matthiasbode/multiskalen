/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import static applications.transshipment.analysis.Workload.WorkloadPlotter.plotSetup;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import math.FieldElement;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import util.bucketing.Bucket;
import util.bucketing.BucketPlotter;
import util.bucketing.Bucketing;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class VergleichAuslastung extends Application {

    public File folder = ProjectOutput.create();

    @Override
    public void start(final Stage primaryStage) throws Exception {
        TransshipmentParameter.DEBUG = true;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Erste Instanz");
        File f1 = chooser.showOpenDialog(primaryStage);

        FileChooser chooser2 = new FileChooser();
        chooser2.setTitle("Zweite Instanz");
        File f2 = chooser2.showOpenDialog(primaryStage);

        Bucketing bucket1 = JSONSerialisierung.importJSON(new FileInputStream(f1), Bucketing.class);
        Bucketing bucket2 = JSONSerialisierung.importJSON(new FileInputStream(f2), Bucketing.class);
        
        Bucketing diff = new Bucketing("Differenz");
        for (int i = 0; i < bucket1.getBuckets().size(); i++) {
            Bucket b1 = bucket1.getBuckets().get(i);
            Bucket b2 = bucket2.getBuckets().get(i);
            diff.getBuckets().add(new Bucket(b1.getTimeSlot(), b1.getDemand()-b2.getDemand()));
        }
        
        Map<String, Bucketing> functions = new HashMap<>();

        
        
        
//        
//        functions.put("Instanz 1", bucket1);
//        functions.put("Instanz 2", bucket2);
        functions.put("Differenz", diff);

        FieldElement fromWhen = bucket1.getBuckets().get(0).getTimeSlot().getFromWhen();
        FieldElement untilWhen = bucket1.getBuckets().get(bucket1.getBuckets().size() - 1).getTimeSlot().getUntilWhen();
        JFreeChart bucketChart = BucketPlotter.createChart(functions, new TimeSlot(fromWhen, untilWhen), "Für Resource " + bucket1.getResourceName(), null);
        XYPlot plot = (XYPlot) bucketChart.getPlot();

        plot.getRenderer()
                .setSeriesPaint(0, new Color(0, 80, 155));
        plot.getRenderer()
                .setSeriesPaint(1, new Color(200, 211, 23));

        try {
            File f = new File(folder, "Vergleich.png");
            ChartUtilities.saveChartAsPNG(f, bucketChart, 2200, 435);
        } catch (IOException ex) {
            Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
