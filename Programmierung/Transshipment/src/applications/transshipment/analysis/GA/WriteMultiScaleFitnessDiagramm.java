/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.GA;

import applications.transshipment.multiscale.model.Scale;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import applications.transshipment.start.debug.Test;

/**
 *
 * @author Bode
 */
public class WriteMultiScaleFitnessDiagramm {

    public static ArrayList<Block> read(InputStream f) {
        ArrayList<Block> blocks = new ArrayList<>();
        InputStreamReader ir = null;
        try {
            ir = new InputStreamReader(f);

            BufferedReader br = new BufferedReader(ir);
            String zeile = br.readLine();
            Block currentBlock = null;
            while (zeile != null) {
                if (zeile.equals("micro")) {
                    if (currentBlock != null) {
                        blocks.add(currentBlock);
                    }
                    currentBlock = new Block(Scale.micro);

                } else if (zeile.equals("macro")) {
                    if (currentBlock != null) {
                        blocks.add(currentBlock);
                    }
                    currentBlock = new Block(Scale.macro);
                } else {
                    String[] numbersString = zeile.split(",");
                    Double[] values = new Double[numbersString.length];
                    for (int i = 0; i < numbersString.length; i++) {
                        String string = numbersString[i];
                        values[i] = Double.parseDouble(string);
                    }
                    currentBlock.values.add(values);
                }

                zeile = br.readLine();
            }

            for (Block block : blocks) {
                System.out.println(block);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriteMultiScaleFitnessDiagramm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WriteMultiScaleFitnessDiagramm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ir.close();
            } catch (IOException ex) {
                Logger.getLogger(WriteMultiScaleFitnessDiagramm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return blocks;
    }

    public static class Block {

        Scale scale;
        ArrayList<Double[]> values = new ArrayList<>();

        public Block(Scale scale) {
            this.scale = scale;
        }

        @Override
        public String toString() {
            String res = "Block{" + "scale=" + scale + ", \n";
            for (Double[] doubles : values) {
                res += "[";
                for (Double double1 : doubles) {
                    res += double1 + ",";
                }
                res += "]\n";
            }
            res += " '}';";
            return res;

        }

    }

    public static void plot(ArrayList<Block> blocks) {
        CategoryAxis domainAxis = new CategoryAxis();
        final CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainAxis);

        DefaultCategoryDataset datasetDNFMacro = new DefaultCategoryDataset();

        int timeWindow = 0;
        for (Block block : blocks) {
            if (block.scale.equals(Scale.macro)) {
                for (int i = 0; i < block.values.size(); i++) {
                    Double[] doubles = block.values.get(i);
                    double value = -doubles[0];
                    datasetDNFMacro.addValue(value, "Generation " + i, Integer.toString(timeWindow));
                }
                timeWindow++;
            }
        }
        NumberAxis dnfmacro = new NumberAxis();
        dnfmacro.setRange(0, 10);
        CategoryPlot subPlotMacroDNF = new CategoryPlot(datasetDNFMacro, domainAxis, dnfmacro, new BarRenderer());

        DefaultCategoryDataset datasetLevelMacro = new DefaultCategoryDataset();

        timeWindow = 0;
        for (Block block : blocks) {
            if (block.scale.equals(Scale.macro)) {
                for (int i = 0; i < block.values.size(); i++) {
                    Double[] doubles = block.values.get(i);
                    double value = -doubles[1];
                    datasetLevelMacro.addValue(value, "Generation " + i, Integer.toString(timeWindow));
                }
                timeWindow++;
            }
        }
        NumberAxis levelmacro = new NumberAxis();
        levelmacro.setAutoRange(true);
        levelmacro.setTickUnit(new NumberTickUnit(1.0E12));
        CategoryPlot subPlotMacrolevel = new CategoryPlot(datasetLevelMacro, domainAxis, levelmacro, new BarRenderer());

        plot.add(subPlotMacroDNF);
        plot.add(subPlotMacrolevel);

        JFreeChart chart = new JFreeChart(plot);

        ChartFrame f = new ChartFrame("Test", chart);
        f.setSize(800, 600);
        f.setVisible(true);

    }

    public static void main(String[] args) {
        InputStream st = Test.class.getResourceAsStream("ergebnisse.txt");
        ArrayList<Block> blocks = WriteMultiScaleFitnessDiagramm.read(st);
        WriteMultiScaleFitnessDiagramm.plot(blocks);
    }
}
