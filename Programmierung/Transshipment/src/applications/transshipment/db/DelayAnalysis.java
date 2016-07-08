/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.db;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author bode
 */
public class DelayAnalysis {

    public static Collection<TrainFromDB> getTrains() {
        try {
            Connection connection = JDBCConnect.getConnection();
            ArrayList<TrainFromDB> res = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT ID, Ankunft_Zeit_soll, Ankunft_Zeit_ist FROM dbo.Eingangszug");
            while (set.next()) {
                int id = set.getInt("ID");
                Timestamp soll = set.getTimestamp("Ankunft_Zeit_soll");
                Timestamp ist = set.getTimestamp("Ankunft_Zeit_ist");
                if (soll != null && ist != null) {
                    TrainFromDB trainFromDB = new TrainFromDB(soll.getTime(), ist.getTime(), id);
                    res.add(trainFromDB);
                }
            }

            return res;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DelayAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DelayAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void buildTrainBasedChart(Collection<TrainFromDB> trains) {
        final DefaultKeyedValues data = new DefaultKeyedValues();

        for (TrainFromDB train : trains) {
            long diff = train.isArival - train.desiredArival;
            diff /= 60 * 1000;
            data.addValue("" + train.nr, diff);
        }
        CategoryDataset dataset = DatasetUtilities.createCategoryDataset("Züge", data);
        JFreeChart chart = ChartFactory.createBarChart("Abweichung der Ankunftszeiten", "Zug", "min", dataset, PlotOrientation.VERTICAL, true, true, true);
        ChartPanel cp = new ChartPanel(chart);
        JFrame f = new JFrame("Abweichung");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(cp, BorderLayout.CENTER);
        f.setVisible(true);
    }

    
    

    public static void buildHistrogrammChart(Collection<TrainFromDB> trains) {
        XYSeries series = new XYSeries("Häufigkeit des Auftretens");
        TreeMap<Long, Integer> his = new TreeMap<>();
        for (long i = -100; i < 1200; i = i + 20) {
            his.put(i, 0);
        }

        for (TrainFromDB train : trains) {
            long diff = train.isArival - train.desiredArival;
            diff /= 60 * 1000;
            Entry<Long, Integer> lowerEntry = his.lowerEntry(diff);
            Integer count = lowerEntry.getValue();
            count++;
            his.put(lowerEntry.getKey(), count);
        }
        for (Long key : his.keySet()) {
            Double anteil = new Double(his.get(key)) / (trains.size());
            series.add(key, anteil);
        }
        
        XYSeriesCollection collection = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYBarChart("Histrogramm Abweichung Züge", "Abweichung in min", false, "Häufigkeit #", collection, PlotOrientation.VERTICAL, true, true, true);
        ChartPanel cp = new ChartPanel(chart);
        JFrame f = new JFrame("Abweichung");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(cp, BorderLayout.CENTER);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        Collection<TrainFromDB> trains = getTrains();
        buildHistrogrammChart(trains);
    }
}
