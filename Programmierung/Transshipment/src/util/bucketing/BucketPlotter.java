/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.bucketing;

import applications.mmrcsp.model.basics.TimeSlot;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimeTableXYDataset;

/**
 *
 * @author bode
 */
public class BucketPlotter {

    public static JFreeChart createChart(Bucketing function, TimeSlot ts, String title, Double max) {
        HashMap<String, Bucketing> hashMap = new HashMap<>();
        hashMap.put("Transport", function);
        return createChart(hashMap, ts, title, max);
    }

    public static JFreeChart createChart(Map<String, Bucketing> functions, TimeSlot ts, String title, Double max) {

        TimeTableXYDataset dataset = new TimeTableXYDataset();
        for (String keySet : functions.keySet()) {

            for (Bucket bucket : functions.get(keySet).getBuckets()) {
                double toAdd = bucket.getLoad();
                long start = bucket.getTimeSlot().getFromWhen().longValue();
                long end = bucket.getTimeSlot().getUntilWhen().longValue();
                TimePeriod period = new SimpleTimePeriod(start, end);
                dataset.add(period, toAdd, keySet);
            }
//            dataset.addSeries(values);
        }

        StackedXYBarRenderer renderer = new StackedXYBarRenderer(0.0);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);

        DateAxis dateAxis = new DateAxis("Zeit");
        DateTickUnit unit = null;
        unit = new DateTickUnit(DateTickUnit.MINUTE, 10);
        DateFormat chartFormatter = new SimpleDateFormat("HH:mm");
        dateAxis.setDateFormatOverride(chartFormatter);
        dateAxis.setTickUnit(unit);
        dateAxis.setAutoRange(false);
        dateAxis.setRange(new DateRange(new Date(ts.getFromWhen().longValue() - 1), new Date(ts.getUntilWhen().longValue())));

        NumberAxis vAxis = new NumberAxis("Auslastung");
        if (max != null) {
            vAxis.setRange(0.0, max + 0.2);
        }
//        XYPlot plot = new XYPlot(
//                dataset,
//                dateAxis,
//                vAxis,
//                renderer);

        JFreeChart chart = ChartFactory.createXYBarChart(title, "Zeit", true, "Auslastung", dataset, PlotOrientation.VERTICAL, true, true, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRenderer(renderer);
        plot.setDomainAxis(dateAxis);
        plot.setRangeAxis(vAxis);
        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setUpperMargin(0.0);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        chart.setTitle(title);

        return chart;
    }
}
