/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.GA;

import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.start.debug.Test;
import com.sun.javafx.charts.Legend;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author hagedorn
 */
public class WriteMultiScaleFitnessDiagrammFX extends Application {

    protected Stage stage;
    protected ArrayList<WriteMultiScaleFitnessDiagrammFX.Block> macroBlocks;
    protected ArrayList<WriteMultiScaleFitnessDiagrammFX.Block> microBlocks;
    protected int maxGeneration;

    /*
     Variablen fÃ¼r die Beschriftungen
     */
    final String NAME_ITERATION = "Iteration";
    final String NAME_GENERATION = "Generation";
    final String NAME_MICRO_VALUES_1 = "DNF";
    final String NAME_MICRO_VALUES_2 = "Nicht einplanbare";
    final String NAME_MACRO_VALUES_1 = "Macro Values (DNF)";
    final String NAME_2ND_MACRO_VALUES_1 = "Resource-Leveling-Index";
    final String NAME_TIME = "Zeit";
    /*
     Variablen fÃ¼r die Diagramm-Titel
     */
    final String NAME_DIAGRAMM_1 = "Micro Operationen";
    final String NAME_DIAGRAMM_2 = "Macro Min_Slack";
    final String NAME_DIAGRAMM_3 = "Micro Endzeit";
    final String NAME_DIAGRAMM_4 = "Macro DNF";
    /*
     Variablen fÃ¼r die Achsenbeschriftungen / Einheiten
     */
    final String UNIT_MICRO_VALUES = "# DNF"; //Y-Achsenbeschriftung fÃ¼r Micro-Values
    final String UNIT_MACRO_VALUES = "# DNF";
    final String UNIT_2ND_MACRO_VALUES = "Min Slack [s]";
    /*
     Variablen fÃ¼r die Darstellung
     */
    final String COLOR_RGB_DIAGRAMM_1 = "#00509B";      //Hexadezimal-Farbcode fÃ¼r das 1. MacroDiagramm
    final String COLOR_RGB_DIAGRAMM_2 = "#99B9D8";      //Hexadezimal-Farbcode fÃ¼r das 2. MacroDiagramm
    final String COLOR_RGB_DIAGRAMM_3 = "#C8D317";      //Hexadezimal-Farbcode fÃ¼r das Zeit-Diagramm
    final String COLOR_RGB_DIAGRAMM_4_1 = "#00509B";    //Hexadezimal-Farbcode fÃ¼r das MicroDiagramm 1. Wert
    final String COLOR_RGB_DIAGRAMM_4_2 = "#99B9D8";    //Hexadezimal-Farbcode fÃ¼r das MicroDiagramm 2. Wert
    final boolean SHOW_FRAME = false;   //Variable fÃ¼r Anzeige des Rahmens um die Diagramme
    /*
     Faktor fÃ¼r die Macro2- Darstellung
     */
    final double FACTOR_MACRO_2 = 1.0;// Math.pow(10, -13);
    /*
     Faktor fÃ¼r die Macro2- Darstellung
     */
    final double TICK_DIAGRAMM_1 = 1.0;
    final double TICK_DIAGRAMM_2 = 15.0;
    final double TICK_DIAGRAMM_3 = 2.0;
    final double TICK_DIAGRAMM_4 = 1.0;
    long start;

    @Override
    public void start(Stage primaryStage) {

        InputStream st = Test.class.getResourceAsStream("ergebnisse.txt");
        ArrayList<WriteMultiScaleFitnessDiagrammFX.Block> blocks = WriteMultiScaleFitnessDiagrammFX.read(st);

        DuisburgInputParameters duisburgInputParameters = new DuisburgInputParameters();
        start = duisburgInputParameters.start.getTimeInMillis();

        ScrollPane sp = new ScrollPane();
        sp.prefHeight(800);
        setBlocks(blocks);
        maxGeneration = getNumberOfGenerations();
        sp.setContent(plot(blocks));
        Scene scene = new Scene(sp, 1200, 800);
        stage = primaryStage;
        primaryStage.setTitle("MultiScaleFitnessDiagramm");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setBlocks(ArrayList<WriteMultiScaleFitnessDiagrammFX.Block> blocks) {
        macroBlocks = new ArrayList<>();
        microBlocks = new ArrayList<>();

        for (Block block : blocks) {
            if (block.scale.equals(Scale.macro)) {
                macroBlocks.add(block);
            } else {
                microBlocks.add(block);
            }
        }
        if (Math.abs(macroBlocks.size() - microBlocks.size()) > 1) {
            System.err.println("Anzahl an Macro und Micro Blocks nicht korrekt");
        }
    }

    public GridPane plot(ArrayList<WriteMultiScaleFitnessDiagrammFX.Block> blocks) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(0, 10, 0, 10));

        /* Schleife zur Erstellung der SpaltenÃ¼berschriften */
        for (int i = 0; i < macroBlocks.size(); i++) {

            Text title = new Text(NAME_ITERATION + " " + i);
            title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
            gridPane.add(title, i, 0);
        }

        /* Erstellen der Diagramme fÃ¼r macroValues */
        int i = 0;
        for (Block macroBlock : macroBlocks) {

            gridPane.add(getMacroValuesChart(macroBlock, i), i, 1);
            i++;
        }
        /* Erstellen der Diagramme fÃ¼r macroValues */
        int z = 0;
        for (Block macroBlock : macroBlocks) {

            gridPane.add(getSecondMacroValuesChart(macroBlock, z), z, 2);
            z++;
        }
        /* Erstellen der Diagramme fÃ¼r microValues */
        int g = 0;
        for (Block microBlock : microBlocks) {

            gridPane.add(getMicroValuesChart(microBlock, g), g, 3);
            g++;
        }

        /* Erstellen des Diagramms fÃ¼r Zeit */
        for (int h = 0; h < microBlocks.size(); h++) {

            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis(25, 32, TICK_DIAGRAMM_3);
            final BarChart<String, Number> timeChart;
            timeChart = new BarChart<>(xAxis, yAxis);
            timeChart.setTitle(NAME_DIAGRAMM_3 + "_" + h);
            if (SHOW_FRAME) {
                timeChart.setStyle("-fx-border: 2px solid; -fx-border-color: black;");
            }
            xAxis.setLabel(NAME_GENERATION);
            yAxis.setLabel(NAME_TIME);
            timeChart.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    saveDiagram(timeChart, stage);
                }
            }
            );
            /* Erstellen der Series fÃ¼r microZeit */
            XYChart.Series timeSeries = new XYChart.Series();
            timeSeries.setName(NAME_TIME);
            Block microBlock = microBlocks.get(h);
            int j = 1;
            for (Double[] values : microBlock.values) {

                timeSeries.getData().add(new XYChart.Data(NAME_GENERATION + " " + j, getRelativeTime(values[2]) - 30 * h));

                j++;
            }
            if (microBlock.values.size() < maxGeneration) {
                for (int y = j; y < maxGeneration + 1; y++) {
                    timeSeries.getData().add(new XYChart.Data(NAME_GENERATION + " " + y, getRelativeTime(microBlock.values.get(microBlock.values.size() - 1)[2]) - 30 * h));
                }
            }

            timeChart.getData().add(timeSeries);

            for (Object d : timeSeries.getData()) {
                Data d1 = (Data) d;

                d1.getNode().setStyle("-fx-bar-fill: " + COLOR_RGB_DIAGRAMM_3 + ";");
            }

            Legend legend = (Legend) timeChart.lookup(".chart-legend");
            Legend.LegendItem item = new Legend.LegendItem(NAME_TIME, new Rectangle(10, 10, getRGBColorFromHexString(COLOR_RGB_DIAGRAMM_3)));
            legend.getItems().setAll(item);
            /*  ---------   */
            gridPane.add(timeChart, h, 4);
        }

        return gridPane;
    }

    public BarChart getMicroValuesChart(Block microBlock, int g) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(getMinYValue(), getMaxYValue(), TICK_DIAGRAMM_4);
        final BarChart<String, Number> valueChart;
        valueChart = new BarChart<>(xAxis, yAxis);
        valueChart.setTitle(NAME_DIAGRAMM_1 + "_" + g);
        if (SHOW_FRAME) {
            valueChart.setStyle("-fx-border: 2px solid; -fx-border-color: black;");
        }
        xAxis.setLabel(NAME_GENERATION);
        yAxis.setLabel(UNIT_MICRO_VALUES);
        valueChart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveDiagram(valueChart, stage);
            }
        }
        );
        /* Erstellen einer ChartSeries fÃ¼r microValues */
        XYChart.Series valueSeriesOne = new XYChart.Series();
        valueSeriesOne.setName(NAME_MICRO_VALUES_1);
        XYChart.Series valueSeriesTwo = new XYChart.Series();
        valueSeriesTwo.setName(NAME_MICRO_VALUES_2);

        int j = 1;
        for (Double[] values : microBlock.values) {

            valueSeriesOne.getData().add(new XYChart.Data(NAME_GENERATION + " " + j, values[0] * (-1)));
            valueSeriesTwo.getData().add(new XYChart.Data(NAME_GENERATION + " " + j, values[1] * (-1)));
            j++;
        }
        if (microBlock.values.size() < maxGeneration) {
            for (int y = j; y < maxGeneration + 1; y++) {
                valueSeriesOne.getData().add(new XYChart.Data(NAME_GENERATION + " " + y, microBlock.values.get(microBlock.values.size() - 1)[0] * (-1)));
                valueSeriesTwo.getData().add(new XYChart.Data(NAME_GENERATION + " " + y, microBlock.values.get(microBlock.values.size() - 1)[1] * (-1)));
            }
        }
        valueChart.getData().addAll(valueSeriesOne, valueSeriesTwo);
        for (Object d : valueSeriesOne.getData()) {
            Data d1 = (Data) d;

            d1.getNode().setStyle("-fx-bar-fill: " + COLOR_RGB_DIAGRAMM_4_1 + ";");
        }
        for (Object d : valueSeriesTwo.getData()) {
            Data d1 = (Data) d;

            d1.getNode().setStyle("-fx-bar-fill: " + COLOR_RGB_DIAGRAMM_4_2 + ";");
        }

        Legend legend = (Legend) valueChart.lookup(".chart-legend");
        Legend.LegendItem item = new Legend.LegendItem(NAME_MICRO_VALUES_1, new Rectangle(10, 10, getRGBColorFromHexString(COLOR_RGB_DIAGRAMM_4_1)));
        Legend.LegendItem item2 = new Legend.LegendItem(NAME_MICRO_VALUES_2, new Rectangle(10, 10, getRGBColorFromHexString(COLOR_RGB_DIAGRAMM_4_2)));
        legend.getItems().setAll(item, item2);
        return valueChart;

    }

    public BarChart getMacroValuesChart(Block macroBlock, int i) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(getMinYValue(), getMaxYValue(), TICK_DIAGRAMM_1);
        final BarChart<String, Number> valueChart;
        valueChart = new BarChart<>(xAxis, yAxis);
        valueChart.setTitle(NAME_DIAGRAMM_4 + "_" + i);
        if (SHOW_FRAME) {
            valueChart.setStyle("-fx-border: 2px solid; -fx-border-color: black;");
        }
        xAxis.setLabel(NAME_GENERATION);
        yAxis.setLabel(UNIT_MACRO_VALUES);
        valueChart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveDiagram(valueChart, stage);
            }
        }
        );

        /* Erstellen einer ChartSeries fÃ¼r macroValues */
        XYChart.Series valueSeries = new XYChart.Series();
        valueSeries.setName(NAME_MACRO_VALUES_1);

        int j = 1;
        for (Double[] values : macroBlock.values) {

            valueSeries.getData().add(new XYChart.Data(NAME_GENERATION + " " + j, values[0] * (-1)));
            j++;
        }
        if (macroBlock.values.size() < maxGeneration) {
            for (int y = j; y < maxGeneration + 1; y++) {
                valueSeries.getData().add(new XYChart.Data(NAME_GENERATION + " " + y, macroBlock.values.get(macroBlock.values.size() - 1)[0] * (-1)));
            }
        }
        valueChart.getData().add(valueSeries);
        for (Object d : valueSeries.getData()) {
            Data d1 = (Data) d;

            d1.getNode().setStyle("-fx-bar-fill: " + COLOR_RGB_DIAGRAMM_1 + ";");
        }
        Legend legend = (Legend) valueChart.lookup(".chart-legend");
        Legend.LegendItem item = new Legend.LegendItem(NAME_MACRO_VALUES_1, new Rectangle(10, 10, getRGBColorFromHexString(COLOR_RGB_DIAGRAMM_1)));
        legend.getItems().setAll(item);
        return valueChart;
    }

    public BarChart getSecondMacroValuesChart(Block macroBlock, int i) {
        final CategoryAxis xAxis = new CategoryAxis();

        final NumberAxis yAxis = new NumberAxis(getMinSecondMacro() * FACTOR_MACRO_2, getMaxSecondMacro() * FACTOR_MACRO_2, TICK_DIAGRAMM_2);
        final BarChart<String, Number> valueChart;
        valueChart = new BarChart<>(xAxis, yAxis);
        valueChart.setTitle(NAME_DIAGRAMM_2 + "_" + i);
        if (SHOW_FRAME) {
            valueChart.setStyle("-fx-border: 2px solid; -fx-border-color: black;");
        }
        xAxis.setLabel(NAME_GENERATION);
        yAxis.setLabel(UNIT_2ND_MACRO_VALUES);
        valueChart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveDiagram(valueChart, stage);
            }
        }
        );

        /* Erstellen einer ChartSeries fÃ¼r macroValues */
        XYChart.Series valueSeries = new XYChart.Series();
        valueSeries.setName(NAME_2ND_MACRO_VALUES_1);

        int j = 1;
        for (Double[] values : macroBlock.values) {

            valueSeries.getData().add(new XYChart.Data(NAME_GENERATION + " " + j, (values[1] * FACTOR_MACRO_2 * (1))));
            j++;
        }
        if (macroBlock.values.size() < maxGeneration) {
            for (int y = j; y < maxGeneration + 1; y++) {
                valueSeries.getData().add(new XYChart.Data(NAME_GENERATION + " " + y, macroBlock.values.get(macroBlock.values.size() - 1)[0] * FACTOR_MACRO_2 * (1)));
            }
        }
        valueChart.getData().add(valueSeries);
        for (Object d : valueSeries.getData()) {
            Data d1 = (Data) d;
            d1.getNode().setStyle("-fx-bar-fill: " + COLOR_RGB_DIAGRAMM_2 + ";");
        }
        Legend legend = (Legend) valueChart.lookup(".chart-legend");
        Legend.LegendItem item = new Legend.LegendItem(NAME_2ND_MACRO_VALUES_1, new Rectangle(10, 10, getRGBColorFromHexString(COLOR_RGB_DIAGRAMM_2)));
        legend.getItems().setAll(item);
        return valueChart;
    }

    public static Color getRGBColorFromHexString(String colorStr) {
        String[] str = colorStr.split("#");
        final int[] ret = new int[3];
        for (int i = 0; i < 3; i++) {
            ret[i] = Integer.parseInt(str[1].substring(i * 2, i * 2 + 2), 16);
        }
        return new Color(ret[0] / 255.0, ret[1] / 255.0, ret[2] / 255.0, 1.);
    }

    public static void saveDiagram(BarChart<String, Number> barChart, Stage s) {
        WritableImage wi = new WritableImage((int) barChart.getWidth()*3, (int) barChart.getHeight()*3);
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setTransform(Transform.scale(3, 3));
        barChart.snapshot(snapshotParameters, wi);

        DirectoryChooser f = new DirectoryChooser();
        f.setTitle("Speicherort fÃ¼r Diagramm");
        File directory = f.showDialog(s);
        if (directory != null) {
            RenderedImage ri = SwingFXUtils.fromFXImage(wi, null);
            File file = new File(directory.getAbsolutePath() + "/Screenshot_" + barChart.getTitle() + ".png");
            try {
                ImageIO.write(ri, "png", file);
            } catch (IOException ex) {
                Logger.getLogger(WriteMultiScaleFitnessDiagrammFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.err.println("Gespeichert: " + file.getAbsolutePath());
        } else {
            System.err.println("Nicht gespeichert, da kein Verzeichnis ausgewÃ¤hlt wurde.");
        }

    }

    public int getNumberOfGenerations() {
        int numberOfGenerations = Integer.MIN_VALUE;

        for (Block block : macroBlocks) {
            if (block.values.size() > numberOfGenerations) {
                numberOfGenerations = block.values.size();
            }
        }
        for (Block block : microBlocks) {
            if (block.values.size() > numberOfGenerations) {
                numberOfGenerations = block.values.size();
            }
        }
        System.out.println("Anzahl an Generationen: " + numberOfGenerations);
        return numberOfGenerations;
    }

    public double getMaxYValue() {
        double maxValue = Integer.MIN_VALUE;

        for (Block block : macroBlocks) {
            for (Double[] value : block.values) {
                if (value[0] * (-1) > maxValue) {
                    maxValue = value[0] * (-1);
                }
            }
        }
        for (Block block : microBlocks) {
            for (Double[] value : block.values) {

                if (value[0] * (-1) > maxValue) {
                    maxValue = value[0] * (-1);
                }
            }
        }
        return maxValue;
    }

    public double getMinYValue() {
        double minValue = Integer.MAX_VALUE;

        for (Block block : macroBlocks) {
            for (Double[] value : block.values) {

                if (value[0] * (-1) < minValue) {
                    minValue = value[0] * (-1);
                }
            }
        }
        for (Block block : microBlocks) {
            for (Double[] value : block.values) {

                if (value[0] * (-1) < minValue) {
                    minValue = value[0] * (-1);
                }
            }
        }
        return minValue;
    }

    public double getMaxSecondMacro() {
        double maxValue = Integer.MIN_VALUE;

        for (Block block : macroBlocks) {
            for (Double[] value : block.values) {
                if (value[0] * (1) > maxValue) {
                    maxValue = (value[1] * (1));
                }
            }
        }
        return maxValue;
    }

    public double getMinSecondMacro() {
        double minValue = Integer.MAX_VALUE;

        for (Block block : macroBlocks) {
            for (Double[] value : block.values) {

                if (value[0] * (1) < minValue) {
                    minValue = (value[1] * (1));
                }
            }
        }
        return minValue;
    }

    public double getMaxTime() {
        double maxTime = Integer.MIN_VALUE;

        for (Block block : microBlocks) {
            for (Double[] value : block.values) {
                try {
                    if (getRelativeTime(value[2]) > maxTime) {
                        maxTime = getRelativeTime(value[2]);
                    }
                } catch (NullPointerException e) {

                }
            }
        }
        return maxTime;
    }

    public double getMinTime() {
        double minTime = Integer.MAX_VALUE;

        for (Block block : microBlocks) {
            for (Double[] value : block.values) {

                try {
                    if (getRelativeTime(value[2]) < minTime) {
                        minTime = getRelativeTime(value[2]);
                    }
                } catch (NullPointerException e) {

                }
            }
        }
        return minTime;
    }

    public Long getRelativeTime(Double time) {
        return ((-start - time.longValue()) / (1000L * 60L));
    }

    @SuppressWarnings("null")
    public static ArrayList<Block> read(InputStream f) {
        ArrayList<Block> blocks = new ArrayList<>();
        InputStreamReader ir = null;
        try {
            ir = new InputStreamReader(f);

            BufferedReader br = new BufferedReader(ir);
            String zeile = br.readLine();
            Block currentBlock = null;
            while (zeile != null) {
                switch (zeile) {
                    case "micro":
                        if (currentBlock != null) {
                            blocks.add(currentBlock);
                        }
                        currentBlock = new Block(Scale.micro);
                        break;
                    case "macro":
                        if (currentBlock != null) {
                            blocks.add(currentBlock);
                        }
                        currentBlock = new Block(Scale.macro);
                        break;
                    default:
                        String[] numbersString = zeile.split(",");
                        Double[] values = new Double[numbersString.length];
                        for (int i = 0; i < numbersString.length; i++) {
                            String string = numbersString[i];
                            values[i] = Double.parseDouble(string);
                        }
                        currentBlock.values.add(values);
                        break;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
