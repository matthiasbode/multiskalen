package applications.fuzzy.plotter;

import applications.mmrcsp.model.basics.TimeSlot;
import bijava.math.function.ScalarFunction1d;
import fuzzy.number.discrete.interval.FuzzyInterval;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import util.chart.FunctionPlotter;

/**
 * Klasse zum einfachen Zeichnen von Funktionen mittels JFreeChart.<br>
 * Benutzung:
 * <ol>
 * <li>Erzeugen einer Instanz von FunctionPlotter (mit Beschriftung)
 * <li>Hinzufuegen von Funktionen (mittels <code>addFunction<code>)
 * <li>Zeichnen des Diagramms (mittels <code>plot<code>).
 * </ol>
 *
 * @author Institut fuer Bauinformatik, Leibniz Universitaet Hannover
 *
 * Anpassungen für die Studienarbeit von Sebastian Brandt
 *
 */
public class FuzzyFunctionPlotter {

    /* Funktionen als XYSeries */
    private XYSeriesCollection functions;

    /* Beschriftungen */
    private String title, xDesc, yDesc;

    /**
     * Erzeugt einen FunctionPlotter, mit Titel "Diagramm" und
     * Achsenbeschriftungen laut
     * {@link FunctionPlotter#FunctionPlotter(java.lang.String)}.
     */
    public FuzzyFunctionPlotter() {
        this("Diagramm");
    }

    /**
     * Erzeugt einen FunctionPlotter mit Titel <code>title</code> und
     * Achsenbeschriftungen "x" und "f(x)".
     *
     * @param title Titel des Diagramms.
     */
    public FuzzyFunctionPlotter(String title) {
        this(title, "Zeit [h]", "Auslastung [100%]");
    }

    /**
     * Erzeugt einen FunctionPlotter mit gegebenem Titel und
     * Achsenbeschriftungen.
     *
     * @param title Titel des Diagramms
     * @param xDesc Beschriftung der x-Achse
     * @param yDesc Beschriftung der y-Achse
     */
    public FuzzyFunctionPlotter(String title, String xDesc, String yDesc) {
        this.title = title;
        this.xDesc = xDesc;
        this.yDesc = yDesc;
        functions = new XYSeriesCollection();
    }

    /**
     * Nimmt die Funktion, die durch die Punkte [x_i, y_i=f(x_i)] definiert ist
     * in das Diagramm auf. In der Legende wird diese Funktion mit
     * <code>name</code> gekennzeichnet.
     *
     * @param x Feld mit Stuetzstellen x_i
     * @param y Feld mit Funktionswerten y_i an den zugehoerigen Stuetzstellen
     * x_i
     * @param name Name der Funktion
     */
    public void addFunction(double[] x, double[] y, String name) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("unzulaessig: size of x (" + x.length + ") != size of y (" + y.length + ")");
        }
        XYSeries xys = new XYSeries(name);

        for (int i = 0; i < x.length; i++) {
            xys.add(x[i], y[i], false);
        }
        functions.addSeries(xys);
    }

    /**
     * Nimmt die Funktion f in das Diagramm auf. Dazu muss der zu zeichnende
     * Bereich (<code>[xmin,max]</code>) und die Abtastrate <code>dx</code>
     * festgelegt werden. In der Legende wird diese Funktion mit
     * <code>name</code> gekennzeichnet.
     *
     * @param f zu zeichnende Funktion
     * @param xmin linke Grenze des zu zeichnenden Bereichs
     * @param xmax rechte Grenze des zu zeichnenden Bereichs
     * @param dx Abtastrate fuer die Stuetzstellen
     * @param name Name der Funktion
     */
    public void addFunction(ScalarFunction1d f, double xmin, double xmax, double dx, String name) {
        if (xmin >= xmax) {
            throw new IllegalArgumentException("unzulaessig: xmin (" + xmin + ") >= xmax (" + xmax + ")");
        }
        XYSeries xys = new XYSeries(name);
        for (double x = xmin; x <= xmax; x += dx) {
            xys.add(x, f.getValue(x), false);
        }
        functions.addSeries(xys);
    }

    /**
     * Ruft {@link #plot(int, int) plot(800,600)} auf.
     */
    public void plot() {
        plot(800, 600, null, true);
    }

    /**
     * Ruft {@link #plot(int, int, boolean) plot(800,600, plotPoints)} auf.
     */
    public void plot(boolean plotPoints) {
        plot(800, 600, null, plotPoints);
    }

    public JFreeChart getChart() {
        return getChart(null, null);
    }

    public JFreeChart getChart(TimeSlot ts, Double max) {
        // XY-Linien-Diagramm erstellen
        JFreeChart chart = ChartFactory.createXYLineChart(title, xDesc, yDesc, functions, PlotOrientation.VERTICAL, true, true, false);

        // Fuer Modifikationen am Diagramm
        XYPlot plot = chart.getXYPlot();
        NumberAxis rangeAxis = new NumberAxis("Zugehörigkeit");
        //Welcher Abstand zwischen den Ticks
        rangeAxis.setTickUnit(new NumberTickUnit(0.25));
        plot.setRangeAxis(rangeAxis);

        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);

        // Renderer verwenden, der Shapes und Linien zeichnen kann.
//        XYAreaRenderer renderer = new XYAreaRenderer();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            // Punkte zeichnen (false fuer nicht zeichnen)
            renderer.setSeriesShapesVisible(i, false);
            //Dicke der Linien angeben
            renderer.setSeriesStroke(i, new BasicStroke(3.5f));
        }
        // Renderer uebern
        // Renderer uebernehmen
        plot.setRenderer(renderer);
        if (ts != null) {
            DateAxis domainAxis = new DateAxis("Time [minutes]");
            domainAxis.setRange(new DateRange(new Date(ts.getFromWhen().longValue() - 1), new Date(ts.getUntilWhen().longValue())));
            plot.setDomainAxis(domainAxis);
        }
        if (max != null) {
            NumberAxis range = new NumberAxis("Auslastung");
            range.setRange(0, max);
            plot.setRangeAxis(range);
        }

        return chart;
    }

    public JFreeChart getAreaChart(TimeSlot ts) {
        return getAreaChart(ts, null);
    }

    public JFreeChart getAreaChart(TimeSlot ts, Double max) {
        // XY-Linien-Diagramm erstellen
        JFreeChart chart = ChartFactory.createXYAreaChart(title, xDesc, yDesc, functions, PlotOrientation.VERTICAL, true, true, false);
        // Fuer Modifikationen am Diagramm
        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        // Renderer verwenden, der Shapes und Linien zeichnen kann.

        XYAreaRenderer renderer = new XYAreaRenderer();
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(3.5f));
        }
        // Renderer uebern
        // Renderer uebernehmen
        plot.setRenderer(renderer);
        DateAxis domainAxis = new DateAxis("Time [minutes]");
        if (ts != null) {
            long start = ts.getFromWhen().longValue();
            if (ts.getFromWhen() instanceof FuzzyInterval) {
                FuzzyInterval fi = (FuzzyInterval) ts.getFromWhen();
                start = (long) fi.getC1();
            }
            domainAxis.setRange(new DateRange(new Date(start - 1), new Date(ts.getUntilWhen().longValue())));
        }
        plot.setDomainAxis(domainAxis);
        if (max != null) {
            NumberAxis range = new NumberAxis("Auslastung");
            range.setRange(0, max);
            plot.setRangeAxis(range);
        }
        return chart;
    }

    /**
     * Zeichnet ein Diagramm mit allen zuvor (durch <code>addFunction</code>)
     * hinzugefuegten Funktionen in einem Fenster der Groesse <code>xSize x
     * ySize</code>.
     *
     * @param xSize Pixel in x-Richtung des Fensters
     * @param ySize Pixel in y-Richtung des Fensters
     */
    public void plot(int xSize, int ySize, TimeSlot ts, boolean lineChart) {
        JFreeChart chart;
        if (lineChart) {
            chart = this.getChart(ts, null);
        } else {
            chart = this.getAreaChart(ts);
        }
        // Fenster fuer die Darstellung
        ChartFrame f = new ChartFrame("Vis", chart);
        f.setSize(xSize, ySize);
        f.setVisible(true);

    }

    /**
     * Zeichnet ein Diagramm mit allen zuvor (durch <code>addFunction</code>)
     * hinzugefuegten Funktionen in einem Fenster der Groesse <code>xSize x
     * ySize</code>.
     *
     * @param xSize Pixel in x-Richtung des Fensters
     * @param ySize Pixel in y-Richtung des Fensters
     */
//    public void plot(int xSize, int ySize, boolean plotPoints) {
//
//        // XY-Linien-Diagramm erstellen
//        JFreeChart chart = ChartFactory.createXYLineChart(title, xDesc, yDesc, functions, PlotOrientation.VERTICAL, true, true, false);
//
//        // Fuer Modifikationen am Diagramm
//        XYPlot plot = chart.getXYPlot();
//
//        // Renderer verwenden, der Shapes und Linien zeichnen kann.
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        for (int i = 0; i < plot.getSeriesCount(); i++) {
//            // Linien zeichnen (false fuer nicht zeichnen)
//            renderer.setSeriesLinesVisible(i, true);
//            renderer.setSeriesShapesVisible(i, plotPoints);
//        }
//
//        // Renderer uebernehmen
//        plot.setRenderer(renderer);
//
//        // Fenster fuer die Darstellung
//        ChartFrame f = new ChartFrame("Vis", chart);
//        f.setSize(xSize, ySize);
//        f.setVisible(true);
//    }
//
//    public void drawLegend(boolean b) {
//
//    }
}
