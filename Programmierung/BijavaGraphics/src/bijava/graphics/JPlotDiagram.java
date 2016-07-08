package bijava.graphics;

import bijava.graphics.shapes.Arrow;
import bijava.math.function.ScalarFunction1d;
import bijava.math.function.interpolation.DiscretizedScalarFunction1d;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Die Klasse JPlotDiagramm stellt eine Komponente
 * zum Zeichnen von Kurven dar.
 *
 * @author  Schonert<br>
 *          Hoecker<br>
 *          Rinke
 * @version 1.1
 */
public class JPlotDiagram extends JComponent {

    //variables declaration
    private double xmin;                                                        //
    private double xmax;                                                        // Bounding-Box
    private double ymin;                                                        //
    private double ymax;                                                        //
    private double dx;                                                          // Maschenweite des Darstellungsrasters
    private double dy;                                                          // Maschenhoehe des Darstellungsrasters
    private int nx;                                                             // Anzahl der Rastermaschen in x-Richtung
    private int ny;                                                             // Anzahl der Rastermaschen in y-Richtung
    private int xminFrame;                                                      //
    private int xmaxFrame;                                                      // Fensterabmessungen
    private int yminFrame;                                                      //
    private int ymaxFrame;                                                      //
    private int numberOfNodes                   = 100;                          // Anzahl der zu zeichnenden Stuetzstellen der stetigen Funktionen

    //view attributes
    private String title;                                                       // Title des Diagramms
    private String x_axis;                                                      // Bezeichung der Groesse x
    private String x_axis_unit;                                                 // Bezeichung der Einheit der Groesse x
    private String y_axis;                                                      // Bezeichung der Groesse y
    private String y_axis_unit;                                                 // Bezeichung der Einheit der Groesse y

    //view-commands
    private boolean drawGrid                    = true;                         // Zeiger zum Zeichnen der Gitterpunkte
    private boolean drawQuadrGridCells          = false;                        // Zeiger zum Zeichnen eines quadratischen Gitters
    private boolean drawGridByNumAxisIntervals  = true;                         // Zeiger zum Zeichnen eines Gitters ueber die Anzahl der Rastermaschen
    private boolean drawTitle                   = true;                         // Zeiger zum Zeichnen des Diagrammtitels
    private boolean drawAxisLabel               = false;                        // Zeiger zum Zeichnen der Achsenbeschriftungen
    private boolean drawLegend                  = false;                        // Zeiger zum Zeichnen der Legende
    private boolean drawCOOSOriginAtZero        = true;                         // Zeiger zum Zeichnen des Rasters bzgl. (0,0)

    //function content
    private ArrayList<PlotCurve> plotCurves     = new ArrayList<PlotCurve>();   // Liste aller in der Komponente befindlichen Funktionen
    private int numberOfPlotCurves              = 0;                            // Anzahl der Funktionen
    private ArrayList<Color> plotColors         = new ArrayList<Color>();       // Liste mit Standardfarben

    //calculating constant
    private static final double EPSILON         = 1E-4;                         // Epsilontik
    private static final double VERTICAL        = 3 * Math.PI / 2.;             // Winkel zur vertikalen Aschsenbeschriftung
    private AffineTransform at                  = new AffineTransform();
//    private ArrayList<AffineTransform> at_old   = new ArrayList<AffineTransform>();
    //private AffineTransform at_old = new AffineTransform();

    /**
     * Erzeugt ein Diagramm mit einem umgebenden Rechteck
     * und bestimmten Abmessungen fuer das Darstellungsgitter.
     *
     * @param xmin minimaler x-Wert.
     * @param xmax maximaler x-Wert.
     * @param dx   Maschenweite des Darstellungsgitters.
     * @param ymin minimaler y-Wert.
     * @param ymax maximaler y-Wert.
     * @param dy   Maschenhoehe des Darstellungsgitters.
     */
    public JPlotDiagram(double xmin, double xmax, double dx, double ymin, double ymax, double dy) {
        this.setTitles("", "", "", "", "");
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.setRangeOfAxisIntervals(dx, dy);
        this.initStandardPlotColors();
        new PlotController(this);
    }

    /**
     * Erzeugt ein leeres Diagramm.
     *
     * @param title Titel des Diagramms.
     */
    public JPlotDiagram(String title) {
        this(title, "", "", "", "");
    }

    /**
     * Erzeugt ein Diagramm mit einer vollstaendigen Beschriftung.
     *
     * @param title Titel des Diagramms.
     * @param x_axis Bezeichnung der Groesse in x-Richtung.
     * @param x_axis_unit Bezeichung der Einheit der Groesse in x-Richtung.
     * @param y_axis Bezeichnung der Groesse in y-Richtung.
     * @param y_axis_unit Bezeichung der Einheit der Groesse in y-Richtung.
     */
    public JPlotDiagram(String title, String x_axis, String x_axis_unit, String y_axis, String y_axis_unit) {
        this.title = title;
        this.x_axis = x_axis;
        this.x_axis_unit = x_axis_unit;
        this.y_axis = y_axis;
        this.y_axis_unit = y_axis_unit;
        xmin = ymin = Double.POSITIVE_INFINITY;
        xmax = ymax = Double.NEGATIVE_INFINITY;
        this.setNumberOfAxisIntervals(10, 10);
        this.initStandardPlotColors();
        new PlotController(this);
    }

    /**
     * Setzt Standardfarben zur Darstellung von Kurven.
     */
    private void initStandardPlotColors() {
        plotColors.add(Color.BLUE);
        plotColors.add(Color.GREEN);
        plotColors.add(Color.RED);
        plotColors.add(Color.ORANGE);
        plotColors.add(Color.MAGENTA);
        plotColors.add(Color.CYAN);
        plotColors.add(Color.PINK);
        plotColors.add(Color.YELLOW);
    }

    /**
     * Liefert eine Standardfarbe zur Darstellung von Kurven.<br>
     * Falls die Liste leer ist, wird SCHWARZ zurueckgegeben.
     *
     * @return Standardfarbe zur Darstellung von Kurven.
     */
    private Color getStandardPlotColor() {
        while (plotColors.size() > 0) {
            Color c = plotColors.remove(0);

            if (!this.containsPlotColor(c)) {
                return c;
            }
        }
        return Color.BLACK;
    }

    /**
     * Ueberprueft, ob eine Farbe bereits zur Darstellung einer Kurve verwendet wird.
     *
     * @param c Farbe.
     * @return <code>true</code>, falls die Farbe zur Darstellung einer Kurve verwendet wird.
     */
    private boolean containsPlotColor(Color c) {
        for (PlotCurve pc : plotCurves) {
            if (pc.color.equals(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Setzt das umgebende Rechteck.
     *
     * @param xmin minimaler x-Wert.
     * @param xmax maximaler x-Wert.
     * @param ymin minimaler y-Wert.
     * @param ymax maximaler y-Wert.
     */
    public void setBoundingBox(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    /**
     * Ueberprueft ob eine hinuzugefuegte Kurve die BoundingBox erweitert.
     * Wird automatisch beim hinzufuegen einer Kurve aufgerufen.
     *
     * @param pc hinzugefuegte Kurve
     */
    private void checkBoundingBox(PlotCurve pc) {
        if (pc.x != null) {
            for (int i = 0; i < pc.x.length; i++) {
                xmin = (pc.x[i] < xmin) ? pc.x[i] : xmin;
                xmax = (pc.x[i] > xmax) ? pc.x[i] : xmax;
                ymin = (pc.y[i] < ymin) ? pc.y[i] : ymin;
                ymax = (pc.y[i] > ymax) ? pc.y[i] : ymax;
            }
        } else {
            for (double i = xmin; i <= xmax; i++) {
                ymin = (pc.func.getValue(i) < ymin) ? pc.func.getValue(i) : ymin;
                ymax = (pc.func.getValue(i) > ymax) ? pc.func.getValue(i) : ymax;
            }
        }
    }

    /**
     * Setzt die Anzahl an Intervallen der Koordinatenachsen.
     *
     * @param nx Anzahl an Intervallen in x-Richtung.
     * @param ny Anzahl an Intervallen in y-Richtung.
     */
    public void setNumberOfAxisIntervals(int nx, int ny) {
        this.nx = nx;
        this.ny = ny;
        drawGridByNumAxisIntervals = true;
    }

    /**
     * Setzt die Groesse der Intervalle der Koordinatenachsen.
     *
     * @param dx Groesse der Intervalle in x-Richtung.
     * @param dy Groesse der Intervalle in y-Richtung.
     */
    public void setRangeOfAxisIntervals(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        drawGridByNumAxisIntervals = false;
    }

    /**
     * Setzt die Anzahl der Stuetzstellen zum zeichnen der skalaren Funktionen.
     *
     * @param numberOfNodes Anzahl der Stuetzstellen (default = 100).
     */
    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    /**
     * Setzt die Beschriftung des Diagramms.
     *
     * @param title Titel des Diagramms.
     * @param x_axis Bezeichnung der Groesse in x-Richtung.
     * @param x_axis_unit Bezeichung der Einheit der Groesse in x-Richtung.
     * @param y_axis Bezeichnung der Groesse in y-Richtung.
     * @param y_axis_unit Bezeichung der Einheit der Groesse in y-Richtung.
     */
    public void setTitles(String title, String x_axis, String x_axis_unit, String y_axis, String y_axis_unit) {
        this.title = title;
        this.x_axis = x_axis;
        this.x_axis_unit = x_axis_unit;
        this.y_axis = y_axis;
        this.y_axis_unit = y_axis_unit;
    }

    /**
     * Zeiger Bestimmt, ab das Hilfsgitter gezeichnet werden soll.
     *
     * @param b Zeiger zum Zeichnen des Hilfgitters (default = true).
     */
    public void drawGrid(boolean b) {
        drawGrid = b;
    }

    /**
     * Bestimmt, ab das Hilfsgitter quadratisch gezeichnet werden soll.<br>
     * Wenn ja, dann wird das Gitter relativ zur Fenstergroesse angepasst.
     *
     * @param b Zeiger zum Zeichnen eines quadratischen Hilfgitters (default = false).
     */
    public void drawQuadrGridCells(boolean b) {
        drawQuadrGridCells = b;
    }

    /**
     * Bestimmt, ob die Beschriftungen des Diagramms und der Achsen gezeichnet werden sollen.
     *
     * @param b Zeiger zum Zeichnen der Beschriftungen des Diagramms und der Achsen (default = false).
     */
    public void drawTitles(boolean b) {
        drawTitle = b;
        drawAxisLabel = b;
    }

    /**
     * Bestimmt, ob der Diagrammtitel gezeichnet werden soll.
     *
     * @param b Zeiger zum Zeichnen der Beschriftung des Diagramms (default = true).
     */
    public void drawTitle(boolean b) {
        drawTitle = b;
    }

    /**
     * Bestimmt, ob die Beschriftungen der Achsen gezeichnet werden sollen.
     *
     * @param b Zeiger zum Zeichnen der Beschriftungen der Achsen (default = true).
     */
    public void drawAxisLabel(boolean b) {
        drawAxisLabel = b;
    }

    /**
     * Bestimmt, ob eine Legende gezeichnet werden soll.
     *
     * @param b Zeiger zum Zeichnen einer Legende (default = false).
     */
    public void drawLegend(boolean b) {
        drawLegend = b;
    }

    /**
     * Bestimmt, ab die Koordinatenachse im Ursprung gezeichnet werden soll.
     * Ansonsten wird das Koordinatensystem in der linken unteren Ecke der
     * Komponenete gezeichnet.
     *
     * @param b Zeigger zum Zeichnen des Diagramms mit dem Ursprung in (0,0) (default = true).
     */
    public void drawOriginAtZero(boolean b) {
        drawCOOSOriginAtZero = b;
    }

    /**
     * Bestimmt, ob die Knoten der Kurven gezeichnet werden sollen.
     *
     * @param b Zeiger zum Zeichnen der Knoten der Kurven (default = true).
     */
    public void drawNodes(boolean b) {
        for (PlotCurve pc : plotCurves) {
            pc.drawNodes = b;
        }
    }

    /**
     * Setzt den Zeiger zum Zeichnen der Knoten einer Kurve.
     *
     * @param title Bezeichung einer Kurve.
     * @param b boolescher Zeiger zum Zeichnen der Knoten der Kurve.
     */
    public void drawNodes(String title, boolean b) {
        for (PlotCurve pc : plotCurves) {
            if (pc.title.equals(title)) {
                pc.drawNodes = b;
            }
        }
    }

    /**
     * Bestimmt, ob die Linien der Kurven gezeichnet werden sollen.
     *
     * @param b Zeiger zum Zeichnen der Linien der Kurven (default = true).
     */
    public void drawLines(boolean b) {
        for (PlotCurve pc : plotCurves) {
            pc.drawLines = b;
        }
    }

    /**
     * Setzt den Zeiger zum Zeichnen der Linien einer Kurve.
     *
     * @param title Bezeichung einer Kurve.
     * @param b boolescher Zeiger zum Zeichnen der Linien der Kurve.
     */
    public void drawLines(String title, boolean b) {
        for (PlotCurve pc : plotCurves) {
            if (pc.title.equals(title)) {
                pc.drawLines = b;
            }
        }
    }

    /**
     * Fuegt eine Funktion aus bestehenden Punkten dem Diagramm hinzu.
     *
     * @param x x-Werte der Punkte.
     * @param y y-Werte der Punkte.
     */
    public void addCurve(double[] x, double[] y) {
        this.addCurve(x, y, new String("Function_" + numberOfPlotCurves), this.getStandardPlotColor());
    }

    /**
     * Fuegt eine Funktion aus bestehenden Punkten dem Diagramm hinzu.
     *
     * @param x x-Werte der Punkte.
     * @param y y-Werte der Punkte.
     * @param title Bezeichnung der Funktion.
     */
    public void addCurve(double[] x, double[] y, String title) {
        this.addCurve(x, y, title, this.getStandardPlotColor());
    }

    /**
     * Fuegt eine Funktion aus bestehenden Punkten dem Diagramm hinzu.
     *
     * @param x x-Werte der Punkte.
     * @param y y-Werte der Punkte.
     * @param col Darstellungsfarbe der Funktion.
     */
    public void addCurve(double[] x, double[] y, Color col) {
        this.addCurve(x, y, new String("Function_" + numberOfPlotCurves), col);
    }

    /**
     * Fuegt eine Funktion aus bestehenden Punkten dem Diagramm hinzu.
     *
     * @param x x-Werte der Punkte.
     * @param y y-Werte der Punkte.
     * @param title Bezeichnung der Funktion.
     * @param col Darstellungsfarbe der Funktion.
     */
    public void addCurve(double[] x, double[] y, String title, Color col) {
        PlotCurve pc = new PlotCurve(x, y, title, col);
        this.checkBoundingBox(pc);
        plotCurves.add(pc);
        numberOfPlotCurves++;
    }

    /**
     * Fuegt eine skalare Funktion dem Diagramm hinzu.
     *
     * @param function skalare Funktion.
     */
    public void addCurve(ScalarFunction1d function) {
        this.addCurve(function, new String("Function_" + numberOfPlotCurves), this.getStandardPlotColor());
    }

    /**
     * Fuegt eine skalare Funktion dem Diagramm hinzu.
     *
     * @param function skalare Funktion.
     * @param title Bezeichnung der Funktion.
     */
    public void addCurve(ScalarFunction1d function, String title) {
        this.addCurve(function, title, this.getStandardPlotColor());
    }

    /**
     * Fuegt eine skalare Funktion dem Diagramm hinzu.
     *
     * @param function skalare Funktion.
     * @param col Darstellungsfarbe der Funktion.
     */
    public void addCurve(ScalarFunction1d function, Color col) {
        this.addCurve(function, new String("Function_" + numberOfPlotCurves), col);
    }

    /**
     * Fuegt eine skalare Funktion dem Diagramm hinzu.
     *
     * @param function skalare Funktion.
     * @param title Bezeichnung der Funktion.
     * @param col Darstellungsfarbe der Funktion.
     */
    public void addCurve(ScalarFunction1d function, String title, Color col) {
        PlotCurve pc = new PlotCurve(function, title, col);
//        this.checkBoundingBox(pc);
        plotCurves.add(pc);
        numberOfPlotCurves++;
    }

    /**
     * Loescht eine Funktion aus dem Diagramm.
     *
     * @param index Stelle der Funktion in der Liste.
     * @return <code>true</code>, falls die Funktion geloescht wurde.
     */
    public boolean removeCurve(int index) {
        if (index < 0 || index > plotCurves.size() - 1) {
            return false;
        }
        plotCurves.remove(index);
        numberOfPlotCurves--;
        return true;
    }

    /**
     * Loescht eine Funktion aus dem Diagramm mittels ihrer Bezeichnung.
     *
     * @param title Bezeichnung der Funktion.
     * @return <code>true</code>, falls die Funktion geloescht wurde.
     */
    public boolean removeCurve(String title) {
        for (PlotCurve pc : plotCurves) {
            if (pc.title.equals(title)) {
                plotCurves.remove(pc);
                numberOfPlotCurves--;
                return true;
            }
        }
        return false;
    }

    /**
     * Liefert eine Liste mit den Bezeichungen der gespeicherten Kurven.
     *
     * @return Liste mit den Bezeichungen der gespeicherten Kurven.
     */
    public ArrayList<String> allCurveTitles() {
        ArrayList<String> titles = new ArrayList<String>(plotCurves.size());
        for (PlotCurve pc : plotCurves) {
            titles.add(pc.title);
        }
        return titles;
    }

    /**
     * Private Methode zum Transformieren der x-Koordinaten
     * auf Bildschirmkoordinaten.
     *
     * @param value zu transformierender x-Wert.
     * @return Bildschirmkoordinate.
     */
    private int transformX(double value) {
        return (int) (xminFrame + (value - xmin) / (xmax - xmin) * (xmaxFrame - xminFrame));
    }

    /**
     * Private Methode zum Transformieren der y-Koordinaten
     * auf Bildschirmkoordinaten.
     *
     * @param value zu transformierender y-Wert.
     * @return Bildschirmkoordinate.
     */
    private int transformY(double value) {
        return (int) (yminFrame + (value - ymin) / (ymax - ymin) * (ymaxFrame - yminFrame));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setTransform(at);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = this.getWidth();
        int H = this.getHeight();
        int outerWidth = (int) (0.98 * (double) W);
        int outerHeight = (int) (0.98 * (double) H);

        xminFrame = (int) (W * 0.04);
        xmaxFrame = (int) (W * 0.97);
        yminFrame = (int) (H * 0.94);
        ymaxFrame = (int) (H * 0.04);

        if (drawTitle) {
            ymaxFrame = (int) (H * 0.10);
        }
        if (drawLegend && numberOfPlotCurves > 0) {
            xmaxFrame = (int) (W * 0.75);
        }
        if (drawAxisLabel) {
            xminFrame = (int) (W * 0.10);
            yminFrame = (int) (H * 0.90);
        }
        //Darstellungsflaeche weiss fuellen und umrahmen
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.WHITE);
        g2.fillRect((int) (W * 0.01), (int) (H * 0.01), outerWidth, outerHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect((int) (W * 0.01), (int) (H * 0.01), outerWidth, outerHeight);

        Font std_font = g2.getFont();
        int schriftgroesse = (int) (getHeight() * .04);
        g2.setFont(new Font("", Font.PLAIN, schriftgroesse));

        //Diagrammtitel zeichnen
        if (drawTitle) {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.25f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int titleString_x = (int) (W * .50 - g2.getFontMetrics().stringWidth(title) / 2.);
            int titleString_y = (int) (H * .04 + g2.getFontMetrics().getHeight() / 2.);
            g2.drawString(title, titleString_x, titleString_y);

            int titlebox_x = (int) (titleString_x - (0.005 * W));
            int titlebox_y = (int) (titleString_y - g2.getFont().getSize() * .90);
            int titleWidth = (int) (g2.getFontMetrics().stringWidth(title) * 1.1);
            int titleHeight = (int) (g2.getFont().getSize() * 1.2);
            g2.drawRect(titlebox_x, titlebox_y, titleWidth, titleHeight);
        }
        //Legende zeichnen
        if (drawLegend && numberOfPlotCurves > 0) {
            int longestString = 0;
            
            for (PlotCurve pc : plotCurves) {
                int stringlength = g2.getFontMetrics().stringWidth(pc.title);
                
                if (longestString < stringlength) {
                    longestString = stringlength;
                }
            }
            double widthFactor = 0.195 * getWidth() / longestString;
            double heightFactor = (double)(yminFrame - ymaxFrame)/ (numberOfPlotCurves * g2.getFontMetrics().getHeight());
            double factor = widthFactor < heightFactor ? widthFactor : heightFactor;
            g2.setFont(new Font("", Font.PLAIN, (int) (g2.getFont().getSize() * factor)));

            int legendboxWidth = (int) (W * 0.225);
            int legendboxHeight = (int) (numberOfPlotCurves * g2.getFontMetrics().getHeight());
            int legendbox_x = (int) (W * 0.76);
            int legendbox_y = (int) ((ymaxFrame + yminFrame) / 2. - legendboxHeight / 2.);

            g2.drawRect(legendbox_x, legendbox_y, legendboxWidth, legendboxHeight);

            for (int i = 0; i < numberOfPlotCurves; i++) {
                PlotCurve pc = plotCurves.get(i);
                g2.setColor(pc.color);
                g2.drawLine((int) (W * 0.77), (int) (legendbox_y + (i + .5) * g2.getFontMetrics().getHeight()),
                        (int) (W * 0.78), (int) (legendbox_y + (i + .5) * g2.getFontMetrics().getHeight()));
                g2.drawString(pc.title, (int) (W * 0.79), (int) (legendbox_y + (i + 1) * g2.getFontMetrics().getHeight() - 5));
            }
            g2.setColor(Color.BLACK);
        }

        //Achsenbeschriftung zeichnen
        if (drawAxisLabel) {
            schriftgroesse = (int) (H * .035);
            g2.setFont(new Font("", Font.PLAIN, schriftgroesse));

            String x_axis_title = x_axis + " [" + x_axis_unit + "]";
            int x_axis_title_x = (int) ((xmaxFrame + xminFrame) / 2. - g2.getFontMetrics().stringWidth(x_axis_title) / 2.);
            int x_axis_title_y = (int) (H * .95 + g2.getFontMetrics().getHeight() / 2.);
            g2.drawString(x_axis_title, x_axis_title_x, x_axis_title_y);

            String y_axis_title = y_axis + " [" + y_axis_unit + "]";
            int y_axis_title_x = (int) (W * 0.05);
            int y_axis_title_y = (int) ((ymaxFrame + yminFrame) / 2. + g2.getFontMetrics().stringWidth(y_axis_title) / 2.);
            drawRotatedString(y_axis_title, g2, y_axis_title_x, y_axis_title_y, VERTICAL);
        }
        g2.setStroke(new BasicStroke());
        g2.setFont(std_font);

        //Setzt die Anzahl der Netzmaschen
        //dynamisch oder statisch (10 Maschen)
        if (this.drawGridByNumAxisIntervals) {
            ny = (drawQuadrGridCells) ? ((int) (1. * (yminFrame - ymaxFrame) / (xmaxFrame - xminFrame) * nx)) : 10;
            dx = (xmax - xmin) / nx;
            dy = (ymax - ymin) / ny;
        } else {
            nx = (int) ((xmax - xmin) / dx);
            nx = (xmin + nx * dx < xmax) ? nx + 1 : nx;
            ny = (int) ((ymax - ymin) / dy);
            ny = (ymin + ny * dy < ymax) ? ny + 1 : ny;
        }
        //Achsenkreuz zeichnen
        g2.setStroke(new BasicStroke(1.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.BLACK);

        //Dimension bestimmen
        double dimension_x = xmax - xmin;
        DecimalFormat df_x = (dimension_x >= 20.) ? new DecimalFormat("#0") : new DecimalFormat("#0.0");

        double dimension_y = ymax - ymin;
        DecimalFormat df_y = (dimension_y >= 10.) ? new DecimalFormat("#0.0") : (dimension_y % 1 > EPSILON) ? new DecimalFormat("#0.00") : new DecimalFormat("#0.0");

        int ursprung_x = this.transformX(0.);
        int ursprung_y = this.transformY(0.);

        int longestStringSize_x = 0;
        for (double i = xmin; i <= xmax + EPSILON; i += dx) {
            double string = i + EPSILON;
            String value = df_x.format(string);
            int stringsize = g2.getFontMetrics().stringWidth(value);

            if (stringsize > longestStringSize_x) {
                longestStringSize_x = stringsize;
            }
        }
        int gridwidth_tr = this.transformX(dx) - this.transformX(0.);
        double factor_x = 1. * longestStringSize_x / gridwidth_tr;
        int delta_x = (int) (factor_x + 1.1);

        int longestStringSize_y = 0;
        for (double i = ymin; i <= ymax + EPSILON; i += dy) {
            double string = i + EPSILON;
            String value = df_y.format(string);
            int stringsize = g2.getFontMetrics().stringWidth(value);

            if (stringsize > longestStringSize_y) {
                longestStringSize_y = stringsize;
            }
        }
        int gridheight_tr = this.transformY(0.) - this.transformY(dy);
        double factor_y = 1. * longestStringSize_y / gridheight_tr;
        int delta_y = (int) (factor_y + 1.1);

        if (!drawCOOSOriginAtZero) {
            Arrow arrow_x = new Arrow(xminFrame, yminFrame, xmaxFrame + 10, yminFrame, 5, 5, 2);
            g2.draw(arrow_x);
            g2.fill(arrow_x);
            Arrow arrow_y = new Arrow(xminFrame, yminFrame, xminFrame, ymaxFrame - 10, 5, 5, 2);
            g2.draw(arrow_y);
            g2.fill(arrow_y);

            for (int i = 0; i <= nx; i++) {
                double value = xmin + i * dx;
                int pos = this.transformX(value);
                double string = value + EPSILON;
                //System.out.println(pos + " ; " + string);

                if (i % delta_x == 0) {
                    int stringsize = g2.getFontMetrics().stringWidth(df_x.format(string));
                    g2.drawString(df_x.format(string), (int) (pos - stringsize / 2.), yminFrame + 15);
                    g2.drawLine(pos, yminFrame, pos, yminFrame + 3);
                }
            }
            for (int i = 0; i <= ny; i++) {
                double value = ymin + i * dy;
                int pos = transformY(value);
                double string = value + EPSILON;

                if (i % delta_y == 0) {
                    int stringsize = g2.getFontMetrics().stringWidth(df_y.format(string));
                    drawRotatedString(df_y.format(string), g2, xminFrame - 10, (int) (pos + stringsize / 2.), VERTICAL);
                    g2.drawLine(xminFrame - 3, pos, xminFrame, pos);
                }
            }
        } else {
            Arrow arrow_x = new Arrow(xminFrame, ursprung_y - 3, xmaxFrame + 10, ursprung_y - 3, 5, 5, 2);
            g2.draw(arrow_x);
            g2.fill(arrow_x);
            Arrow arrow_y = new Arrow(ursprung_x, yminFrame, ursprung_x, ymaxFrame - 10, 5, 5, 2);
            g2.draw(arrow_y);
            g2.fill(arrow_y);

            for (int i = 0; i <= nx; i++) {
                double value = xmin + i * dx;
                int pos = this.transformX(value);
                double string = value + EPSILON;
                //System.out.println(pos + " ; " + string);

                if (i % delta_x == 0 && Math.abs(value) > EPSILON) {
                    int stringsize = g2.getFontMetrics().stringWidth(df_x.format(string));
                    g2.drawString(df_x.format(string), (int) (pos - stringsize / 2.), ursprung_y + 15);
                    g2.drawLine(pos, ursprung_y, pos, ursprung_y + 3);
                }
            }

            for (int i = 0; i <= ny; i++) {
                double value = ymin + i * dy;
                int pos = this.transformY(value);
                double string = value + EPSILON;

                if (i % delta_y == 0 && Math.abs(value) > EPSILON) {
                    int stringsize = g2.getFontMetrics().stringWidth(df_y.format(string));
                    drawRotatedString(df_y.format(string), g2, ursprung_x - 10, (int) (pos + stringsize / 2.), VERTICAL);
                    g2.drawLine(ursprung_x - 3, pos, ursprung_x, pos);
                }
            }
//            //Achsenwerte zeichnen
//            for (double i = xmin; i <= xmax; i+= dx) {
//                int pos = transformX(i);
//                g.drawLine(pos, ursprung_y, pos, ursprung_y + 3);
//                if(i < 0 || i > 9)
//                    g.drawString(String.valueOf((int)i), pos - 7, ursprung_y+15);
//                else
//                    g.drawString(String.valueOf((int)i), pos - 3, ursprung_y+15);
//            }
//            for (double i = ymin; i <= ymax; i+= dy) {
//                if(i != 0) {
//                    int pos = transformY(i);
//                    g.drawLine(ursprung_x - 3, pos, ursprung_x, pos);
//                    if(i > 9)
//                        g.drawString(String.valueOf((int)i), ursprung_x-20, pos+4);
//                    else if(i < 0)
//                        g.drawString(String.valueOf((int) (i - .5)), ursprung_x-20, pos+4);
//                    else
//                        g.drawString(String.valueOf((int)i), ursprung_x-11, pos+4);
//                }
//            }
        }
        //Grid zeichnen, falls eingestellt
        if (drawGrid) {
            g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4, 4}, 0));
            g2.setColor(Color.LIGHT_GRAY);

            for (double i = xmin; i <= xmax + EPSILON; i += dx) {
                int pos_x = this.transformX(i);
                g2.drawLine(pos_x, yminFrame, pos_x, ymaxFrame);
            }
            for (double i = ymin; i <= ymax + EPSILON; i += dy) {
                int pos_y = this.transformY(i);
                g2.drawLine(xminFrame, pos_y, xmaxFrame, pos_y);
            }
            g2.setStroke(new BasicStroke());
        }
        //Funktionen zeichnen
        g2.setColor(Color.BLACK);

        if (numberOfPlotCurves > 0) {
            double[] x_scalar = new double[numberOfNodes];

            for (int i = 0; i < x_scalar.length; i++) {
                x_scalar[i] = xmin + (i * (xmax - xmin) / (x_scalar.length - 1));
            }
            for (PlotCurve pc : plotCurves) {
                g2.setColor(pc.color);
                int xi0, yi0, xi1, yi1;
                boolean fin = false;

                if (pc.x != null) {
                    for (int i = 0; i < pc.x.length - 1; i++) {
                        xi0 = this.transformX(pc.x[i]);
                        yi0 = this.transformY(pc.y[i]);
                        xi1 = this.transformX(pc.x[i + 1]);
                        yi1 = this.transformY(pc.y[i + 1]);

                        if (pc.drawNodes) {
                            g2.fillRect(xi0 - 2, yi0 - 2, 4, 4);

                            if (i == pc.x.length - 2) {
                                g2.fillRect(xi1 - 2, yi1 - 2, 4, 4);
                            }
                        }
                        if (pc.drawLines) {
                            g2.drawLine(xi0, yi0, xi1, yi1);
                        }

                    }
                }
                if (pc.func != null) {
                    for (int i = 0; i < x_scalar.length - 1; i++) {
                        xi0 = this.transformX(x_scalar[i]);
                        yi0 = this.transformY(pc.func.getValue(x_scalar[i]));
                        xi1 = this.transformX(x_scalar[i + 1]);
                        yi1 = this.transformY(pc.func.getValue(x_scalar[i + 1]));

                        if (pc.drawLines) {
                            g2.drawLine(xi0, yi0, xi1, yi1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Private Methode, welche aufgerufen wird, um einen String in einem
     * bestimmten Winkel zu zeichnen.
     */
    private void drawRotatedString(String text, Graphics2D g2, int x, int y, double angle) {
        AffineTransform old = g2.getTransform();
        AffineTransform rotated = AffineTransform.getRotateInstance(angle, x, y);
        g2.transform(rotated);
        g2.drawString(text, x, y);
        g2.setTransform(old);
    }

    /**
     * Private Methode zum zoomen mit dem Mausrad.
     */
    private void zoom(Point point, double scale) {
        Point2D.Float p;
        try {
            p = (Float) at.inverseTransform(point, null);
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return;
        }
        at.setToIdentity();
        at.translate(point.getX(), point.getY());
        at.scale(scale, scale);
        at.translate(-p.getX(), -p.getY());
        this.repaint();
    }

    /**
     * Private Methode zum zoomen in ein ausgewaehltes Rechteck.
     * Ist noch in Bearbeitung, deswegen vorerst noch nicht moeglich.
     */
    private void zoom(Rectangle box) {
//        double scale_x = 1.0 * getWidth() / box.width;
//        double scale_y = 1.0 * getHeight() / box.height;
//        at_old.add(new AffineTransform(at));
//        at.scale(scale_x, scale_y);
//        at.translate(-box.x, -box.y);
//        this.repaint();
    }

    /**
     * Private Methode zum herauszoomen aus einem zuvor getaetigten Zoom.
     * vorerst wird bei Benutzung wieder die gesamte Komponente gezeichnet.
     */
    private void zoomOut() {
//        if (at_old.size() > 0) {
//            at = new AffineTransform(at_old.get(at_old.size() - 1));
//            at_old.remove(at_old.size() - 1);
//        } else {
//            at.setToIdentity();
//        }
        at.setToIdentity();
        this.repaint();
    }

    /**
     * Erzeugt ein Fenster, in welchem das vorhandene Diagramm
     * dargestellt wird.
     * @param title Fenstertitel.
     * @param width Fensterbreite.
     * @param height Fensterhoehe.
     */
    public void plot(String title, int width, int height) {
        // Fenster erzeugen und Abmessungen setzen
        JFrame fr = new JFrame(title);
        fr.setSize(width, height);
        // Diagramm hinzufuegen
        fr.getContentPane().add(this);
        // Fenster auf dem Bildschirm sichbar machen
        fr.setVisible(true);
        // Fensterkreuz zum Schliessen des Fensters und Beenden des Programms benutzen
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Innere Klasse, in welcher die Attribute einer Funktion
     * eingespeichert werden.
     */
    class PlotCurve {
        //modelling attributes
        double[] x;
        double[] y;
        ScalarFunction1d func;
        String title;

        //color attributes
        Color color;

        //draw attributes
        boolean drawNodes = true;
        boolean drawLines = true;

        /**
         * Erzeugt eine PlotCurve durch eine Wertetabelle.
         *
         * @param x x-Werte der Punkte.
         * @param y y-Werte der Punkte.
         * @param title Bezeichnung der Funktion.
         * @param col Darstellungsfarbe der Funktion.
         */
        PlotCurve(double[] x, double[] y, String title, Color col) {
            this.x = x;
            this.y = y;
            this.title = title;
            color = col;
        }

        /**
         * Erzeugt eine PlotCurve mittels einer skalaren Funktion.
         *
         * @param func skalare Funktion.
         * @param title Bezeichnung der Funktion.
         * @param col Darstellungsfarbe der Funktion.
         */
        PlotCurve(ScalarFunction1d func, String title, Color col) {
            this.func = func;
            this.title = title;
            color = col;

            if (func instanceof DiscretizedScalarFunction1d) {
                DiscretizedScalarFunction1d dsf = (DiscretizedScalarFunction1d) func;
                double[][] dsfValues = dsf.getValues();
                x = new double[dsfValues[0].length];
                y = new double[dsfValues[1].length];

                for (int i = 0; i < x.length; i++) {
                    x[i] = dsfValues[0][i];
                    y[i] = dsfValues[1][i];
                }
            }
        }
    }

    /**
     * Innere Klasse, mit der Mausereignisse verarbeitet werden.
     */
    class PlotController extends MouseAdapter {

        JPlotDiagram pd;
        Rectangle box = new Rectangle();
        double scale = 1.0;
        Point start;

        PlotController(JPlotDiagram pd) {
            this.pd = pd;
            pd.addMouseListener(this);
            pd.addMouseMotionListener(this);
            pd.addMouseWheelListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent me) {
            if (me.getButton() == 3) {
                pd.zoomOut();
                scale = 1.0;
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (me.getButton() == 1) {
                start = me.getPoint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (me.getButton() == 1) {
//                pd.zoom(box);
            }
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (me.getButton() == 1) {
//                // neues Rechteck
//                Point p = me.getPoint();
//                box.x = (start.x < p.x) ? start.x : p.x;
//                box.y = (start.y < p.y) ? start.y : p.y;
//                box.width = Math.abs(start.x - p.x);
//                box.height = Math.abs(start.y - p.y);
//                // Zeichne das Rechteck in seiner neuen Positon
//                Graphics2D g2 = (Graphics2D) pd.getGraphics();
//                g2.setColor(Color.blue);
//                g2.draw(box);
//                AlphaComposite ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
//                g2.setComposite(ac1);
//                g2.fill(box);
//                pd.repaint();
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            scale -= (0.2 * mwe.getWheelRotation());
            scale = Math.max(0.2, scale);
            pd.zoom(mwe.getPoint(), scale);
        }
    }

    public static void main(String[] args) {
        JPlotDiagram diagram = new JPlotDiagram("Diagramm");
        diagram.drawLegend(true);

        double[] x = new double[]{-10., 1., 2., 5.};
        double[] y = new double[]{-1., 1., 4., 12.};
        diagram.addCurve(x, y, "Wertetabelle", Color.MAGENTA);

        diagram.addCurve(new ScalarFunction1d() {

            @Override
            public double getValue(double x) {
                return 2 * Math.sin(x);
            }
        }, "ScalarFunction", Color.GREEN);

        double[][] values = new double[2][5];
        for (int i = 0; i < 5; i++) {
            values[0][i] = i * 5 - 10;
            values[1][i] = Math.pow(i * 5 - 10, 2) / 10.;

        }
        DiscretizedScalarFunction1d dsf = new DiscretizedScalarFunction1d(values) {

            @Override
            public double getValue(double x) {
                return x * x / 10.;
            }
        };
        diagram.addCurve(dsf, "DiscretizedScalarFunction", Color.BLUE);
        diagram.plot("Fenster", 800, 600);
    }
}