/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;

import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.lattice.CellResource2D;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.lattice.LatticeManager;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.transshipment.analysis.Analysis;
import bijava.math.function.ScalarFunction1d;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import math.FieldElement;
import math.function.StepFunction;
import util.bucketing.Bucket;
import util.bucketing.Bucketing;
import util.chart.heatmap.HeatMap;
import util.chart.heatmap.TimeHeatMap;
import util.chart.heatmap.TimeHeatMapDataSet;
import util.chart.heatmap.color.RedHeatMapColorScale;

/**
 *
 * @author Philipp
 */
public class HeatMapPlotter implements Analysis {

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {

        TimeSlot allOverTimeSlot = problem.getTerminal().getTemporalAvailability().getAllOverTimeSlot();
        /**
         * Ausgabe des Cranerunways
         */
        CraneRunway craneRunway = null;
        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (conveyanceSystem instanceof Crane) {
                craneRunway = ((Crane) conveyanceSystem).getCraneRunway();
                break;
            }
        }
//        int i = 0;
        /**
         * HeatMap für die detaillierte Auflistung.
         */
        TimeHeatMapDataSet<CellResource2D> data = new TimeHeatMapDataSet<>();
        /**
         * HeatMap für die gemittelte Betrachtung.
         */
        TimeHeatMapDataSet<CellResource2D> bucketData = new TimeHeatMapDataSet<>();
        LatticeManager<CraneRunway> manager = (LatticeManager<CraneRunway>) schedule.getHandler().getSharedManager(craneRunway);
        for (CellResource2D cellResource2D : manager.getGrid().getCells()) {
//            System.out.println(cellResource2D.toString());

            ScheduleRule scheduleRule = schedule.getHandler().get(cellResource2D);

            if (scheduleRule instanceof ScalarFunctionBasedRule) {
                ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

                ScalarFunction1d f = sfb.getWorkloadFunction();
                if (f instanceof StepFunction) {
                    StepFunction workloadFunction = (StepFunction) f;

                    for (FieldElement time : workloadFunction.getSamplingPoints()) {
                        FieldElement value = workloadFunction.getValue(time);
                        double doubleValue = value.doubleValue();
                        data.add(time.longValue(), cellResource2D, doubleValue);
                    }
                    /**
                     * gemittelte Werte
                     */
                    Bucketing b = new Bucketing(cellResource2D, workloadFunction, allOverTimeSlot, 10 * 60 * 1000);
                    for (Bucket bucket : b.getBuckets()) {
                        double toAdd = bucket.getLoad();
                        long start = bucket.getTimeSlot().getFromWhen().longValue();
//                long end = bucket.getTimeSlot().getUntilWhen().longValue();
//                TimePeriod period = new SimpleTimePeriod(start, end);
//                TimePeriodValue item = new TimePeriodValue(period, toAdd);
                        bucketData.add(start, cellResource2D, toAdd);
                    }
                }
            }
        }
//        FunctionPlotter.createCharts(data, folder);
        TimeHeatMap<CellResource2D> hm = new TimeHeatMap<>(data);
        File heatfile = new File(folder, "heatmapDetail.png");
        BufferedImage img = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        Rectangle rec = new Rectangle(0, 0, 2000, 2000);
        g2d.setColor(Color.WHITE);
        g2d.draw(rec);
        g2d.fill(rec);
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.black);
        hm.setColorScale(new RedHeatMapColorScale());
        hm.draw(g2d, null);
        try {
            ImageIO.write(img, "png", heatfile);
        } catch (IOException ex) {
            Logger.getLogger(HeatMap.class.getName()).log(Level.SEVERE, null, ex);
        }

        TimeHeatMap<CellResource2D> hmbucket = new TimeHeatMap<>(bucketData);
        File heatfilebucket = new File(folder, "heatmapBucket.png");
        BufferedImage img2 = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d2 = img2.createGraphics();
        Rectangle rec2 = new Rectangle(0, 0, 2000, 2000);
        g2d2.setColor(Color.WHITE);
        g2d2.draw(rec2);
        g2d2.fill(rec2);
        g2d2.setBackground(Color.WHITE);
        g2d2.setColor(Color.black);
        hmbucket.setColorScale(new RedHeatMapColorScale());
        hmbucket.draw(g2d2, null);
        try {
            ImageIO.write(img2, "png", heatfilebucket);
            bucketData.writeToCSV(new File(folder, "heatmapBucket.csv"));
        } catch (IOException ex) {
            Logger.getLogger(HeatMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
