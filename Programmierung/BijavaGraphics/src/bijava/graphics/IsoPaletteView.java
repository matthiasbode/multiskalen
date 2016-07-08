package bijava.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.NumberFormat;
import javax.swing.JComponent;
import javax.swing.JFrame;

//==============================================================================//
/** Stellt eine IsoPalette grafisch dar. Diese "Farbskala" ist von JComponent
 *  abgeleitet, so dass sie als Komponente in eine grafische Nutzeroberflaeche
 *  direkt eingebunden werden kann. Die Orientierung (vertikal oder horizontal)
 *  kann durch gegebene Methoden beeinflusst werden, ebenso wie das Darstellen
 *  der Extremwerte (an oder aus).
 *
 *  @author berthold
 *  @version 2009-02-04                                                         */
//==============================================================================//
public class IsoPaletteView extends JComponent {
    IsoPalette isopalette;

    
    public enum Orientation{HORIZONTAL, VERTICAL};
    Orientation o = Orientation.VERTICAL;
    boolean showNumbers = true;
    String description;
    NumberFormat format;


//------------------------------------------------------------------------------//
/** Erzeugt eine Darstellung fuer die gegebene IsoPalette. Die Orientierung ist
 *  vertikal, die Extremwerte werden angezeigt.
 *
 *  @param  palette darzustellende IsoPalette                                   */
//------------------------------------------------------------------------------//
    public IsoPaletteView(IsoPalette palette) {
        isopalette = palette;
        format = new java.text.DecimalFormat();
    }

//------------------------------------------------------------------------------//
/** Erzeugt eine Darstellung fuer die gegebene IsoPalette. Die Orientierung ist
 *  vertikal, die Extremwerte werden angezeigt und mit gegebener <code>description
 *  </code> beschrieben.
 *
 *  @param  palette darzustellende IsoPalette
 *  @param  description Beschreibung der Werte.                                 */
//------------------------------------------------------------------------------//
    public IsoPaletteView(IsoPalette palette, String description) {
        isopalette = palette;
        this.description = description;
        format = new java.text.DecimalFormat();
    }

//------------------------------------------------------------------------------//
/** Erzeugt eine Darstellung fuer die gegebene IsoPalette mit angegebener
 *  Orientierung (vertikal oder horizontal). die Extremwerte werden angezeigt.
 *
 *  @param  palette darzustellende IsoPalette
 *  @param  o       Orientierung der Farbskala
 *                  (Orientation.VERTICAL, Orientation.HORIZONTAL)              */
//------------------------------------------------------------------------------//
    public IsoPaletteView(IsoPalette palette, Orientation o) {
        isopalette = palette;
        this.o = o;
        format = new java.text.DecimalFormat();
    }

//------------------------------------------------------------------------------//
/** Erzeugt eine Darstellung fuer die gegebene IsoPalette mit angegebener
 *  Orientierung (vertikal oder horizontal) und gegebener Einstellung fuer die
 *  Beschriftung.
 *
 *  @param  palette darzustellende IsoPalette
 *  @param  o       Orientierung der Farbskala
 *                  (Orientation.VERTICAL, Orientation.HORIZONTAL)
 *  @param  showLabels  Gibt an, ob die Beschriftung angezeigt wird oder nicht. */
//------------------------------------------------------------------------------//
    public IsoPaletteView(IsoPalette palette, Orientation o, boolean showLabels) {
        isopalette = palette;
        this.o = o;
        showNumbers = showLabels;
        format = new java.text.DecimalFormat();
    }

    /**
     * Setzt eine neue Isopalette in den aktuellen View
     * @param isopalette
     */
    public void setPalette(IsoPalette isopalette) {
        this.isopalette = isopalette;
        this.repaint();
    }


    public void setFormat(NumberFormat format) {
        this.format = format;
    }

//------------------------------------------------------------------------------//
/** Setzt die Bezeichnung fuer die Werte, die mittig angezeigt wird.
 *
 *  @param  description Bezeichnung der Werte. Falls <code>description == null
 *                      </code> wird keine Bezeichnung gemalt.                  */
//------------------------------------------------------------------------------//
    public void setDescription(String description) {
        this.description = description;
    }

//------------------------------------------------------------------------------//
/** Zeichnet die Farbskala. (vgl. JComponent)                                   */
//------------------------------------------------------------------------------//
    public void paintComponent(Graphics g) {

        int textoffset = 0;
        if (showNumbers) {
            if (o == o.VERTICAL) {
                String maxString = format.format(isopalette.isoValues[isopalette.isoValues.length-1]);
                FontMetrics fm = g.getFontMetrics();
                int maxCharWidth = fm.charWidth('0');
                int desclength = 0;
                if (description != null)
                    desclength = fm.stringWidth(description);
                int maxStringWidth = Math.max(maxCharWidth*maxString.length(), desclength);
                textoffset = maxStringWidth;
            } else
                textoffset = g.getFont().getSize();

        }

        int xp = 0;
        int yp = 1;
        int h = this.getHeight()/isopalette.isoValues.length;
        int w = this.getWidth() - textoffset;

        if (o == o.HORIZONTAL) {

            xp = 1;
            yp = 0;
            h = this.getHeight() - textoffset;
            w = this.getWidth()/isopalette.isoValues.length;
        }

        
        if (o == o.VERTICAL) {
            double delta = (isopalette.isoValues[isopalette.isoValues.length-1]-isopalette.isoValues[0])/this.getHeight();
            for (int i=0; i<this.getHeight(); i++) {
                g.setColor(isopalette.getColor(isopalette.isoValues[0] + i*delta));
                g.fillRect(0, i, w, 1);
            }
        }

        if (o == o.HORIZONTAL) {
            double delta = (isopalette.isoValues[isopalette.isoValues.length-1]-isopalette.isoValues[0])/this.getWidth();
            for (int i=0; i<this.getWidth(); i++) {
                g.setColor(isopalette.getColor(isopalette.isoValues[0] + i*delta));
                g.fillRect(i, 0, 1, h);
            }
        }

//        for (int i=0; i<isopalette.isoValues.length; i++) {
//            g.setColor(isopalette.colors[i]);
//            g.fillRect(xp*i*w, yp*i*h, w, h);
//        }

        if (showNumbers){
            g.setColor(Color.BLACK);

            if (o == o.VERTICAL) {
                g.drawString(""+format.format(isopalette.isoValues[0]), w, g.getFont().getSize());
                g.drawString(""+format.format(isopalette.isoValues[isopalette.isoValues.length-1]), w, this.getHeight());

                // Bezeichnung in die Mitte malen
                if (description != null) {
                    g.drawString(description, w, (this.getHeight()+g.getFont().getSize())/2);
                }
            }
            else if (o == o.HORIZONTAL) {
                g.drawString(""+format.format(isopalette.isoValues[0]), 0, h+g.getFont().getSize());
                int tl = g.getFontMetrics().stringWidth(format.format(isopalette.isoValues[isopalette.isoValues.length-1]));
                g.drawString(""+format.format(isopalette.isoValues[isopalette.isoValues.length-1]), this.getWidth()-tl, h+g.getFont().getSize());
            }

        }


    }

//------------------------------------------------------------------------------//
/** Setzt die gegebenen Orientierung.
 *
 *  @param  o       Orientierung der Farbskala
 *                  (Orientation.VERTICAL, Orientation.HORIZONTAL)              */
//------------------------------------------------------------------------------//
    public void setOrientation(Orientation o) {
        this.o = o;
    }

//------------------------------------------------------------------------------//
/** Erfragt die aktuelle Orientierung.
 *
 *  @return o       Orientierung der Farbskala
 *                  (Orientation.VERTICAL, Orientation.HORIZONTAL)              */
//------------------------------------------------------------------------------//
    public Orientation getOrientation() {
        return o;
    }

//------------------------------------------------------------------------------//
/** Schaltet die Darstellung der Extremwerte an oder aus.
 *
 *  @param  show    <code>true</code> zum Anzeigen der Extremwerte,
 *                  <code>false</code> zum Verbergen der Extremwerte.           */
//------------------------------------------------------------------------------//
    public void showLabels(boolean show) {
        this.showNumbers = show;
    }

//------------------------------------------------------------------------------//
/** Erfragt den Zustand zum Anzeigen der Extremwerte
 *
 *  @return <code>true</code>, wenn die Extremwerte dargestellt werden, sonst
 *          <code>false</code>.                                                 */
//------------------------------------------------------------------------------//
    public boolean showLabels() {
        return showNumbers;
    }

    public static void main(String[] args) {
//        IsoPalette ip = IsoPalette.BlueWhiteRed(1., 20., 20);
        IsoPalette ip = new IsoPalette(0., 10.);
        JFrame f = new JFrame("Farbskala -- Test");
        f.setSize(100,300);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        IsoPaletteView ipv = new IsoPaletteView(ip, Orientation.VERTICAL);
        ipv.description = "Zeit [Jahre]";
        ipv.setFormat(new java.text.DecimalFormat("0000"));
        f.add(ipv);
        f.setVisible(true);
    }
}
