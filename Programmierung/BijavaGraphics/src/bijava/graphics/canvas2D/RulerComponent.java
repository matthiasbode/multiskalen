package bijava.graphics.canvas2D;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.NumberFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

/**
 * Visuelle Komponente, die einen Maszstab repraesentiert.
 *  
 * @author kaapke
 */
public class RulerComponent extends JComponent {

        public static int HORIZONTAL = 1;
        public static int VERTICAL = 2;
        public static int BOTH = 4;
        
        private double minValue, maxValue;
        private int modus;
        /**
         * siehe set-Methode
         */
        private boolean representsTime = false;
        
        private final double minDis = 20; // minimaler Abstand in Pixeln zwischen Unterteilungsstrichen (ticks)
        private final double maxDis = 60; // minimaler Abstand in Pixeln zwischen Unterteilungsstrichen
       
        private final int border = 4;
        private final int baseline = border;
        private final int tickSmall = 7;
        private final int tickBig = 14;
        private final int textIntervall = 6;
        private int fontSize = 8;
        private int text_baseline = (tickBig + fontSize);
        private int thickness = text_baseline + border;
        
        private Font font = new Font("ARIAL", Font.PLAIN, fontSize);
        private final NumberFormat nf = NumberFormat.getNumberInstance();
        private final AffineTransform rotAT = AffineTransform.getRotateInstance(-Math.PI/2.); 
        private final AffineTransform transAT = new AffineTransform();
        private final AffineTransform localAT = new AffineTransform();
        private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        private Date buffer = new Date();
        
        /**
         *  Defaultmaeszig wird ein horizontales Ruler erzeugt.
         */
        public RulerComponent() {
                this(BOTH);
        }
        
        public RulerComponent(int modus) {
                this.modus = modus;
                nf.setMaximumFractionDigits(1);
        }
        
        public void setMinMax(double minValue, double maxValue) {
                if (maxValue < minValue) throw new IllegalArgumentException("maxValue < minValue");
                this.minValue = minValue;
                this.maxValue = maxValue;
        }
        
        public void paintComponent(Graphics g) {
                
                Graphics2D g2d = (Graphics2D) g;
                
                /* Setze die Schrift */
                Font oldFont = g2d.getFont();
                g2d.setFont(font);
                
                if (modus == HORIZONTAL || modus == BOTH) {
                        paintHorizontal(g2d);
                }
                if (modus == VERTICAL || modus == BOTH) {
                        paintVertical(g2d);
                }
                
                g2d.setFont(oldFont);
        }
        
        private void paint(Graphics2D g2d, double length) {
       
                if (length == 0.) {
                        System.out.println("RulerComponent.paintComponent: length = 0");
                        return;
                }
            
                /* Zeichne die Grundlinie */
                g2d.drawLine(0, baseline, (int) length, baseline);
                
                /* Berechne Anzahl der Ticks, die in die Breite der Komponente passen */
                int tickCountInt = (int) Math.floor(length / (0.5 * (maxDis - minDis)));
                
                /* Berechne Abstand der Ticks */
                double tickDis = length / tickCountInt;
                
                /* Berechne die Textbreite in Pixeln, vereinfacht: nur beim min-Wert */
                double textWidth = g2d.getFontMetrics().stringWidth(formatNumber(minValue));
              
                /* Zeichne den Wert am linken Rand */
                g2d.drawString(formatNumber(minValue), 0, text_baseline);
                
                for (int i=1; i <= tickCountInt; i++) {
                        /* Zeichne die Ticks und ggf. den Wert an der Stelle */
                        double screenX = i * tickDis;
                        if (i > 0 && i%textIntervall == 0) {
                                g2d.drawLine((int) screenX, baseline, (int) screenX, tickBig);
                                double worldX = (minValue) + i * (maxValue - minValue) / tickCountInt;
                                g2d.drawString(formatNumber(worldX), (float) (screenX - 0.5 * textWidth), text_baseline);
                        } else {
                                g2d.drawLine((int) screenX, baseline, (int) screenX, tickSmall);
                        }
                }
                
                /* Zeichne den Wert am rechten Rand */
                g2d.drawString(formatNumber(maxValue), (float) (length - textWidth), text_baseline);
        }
        
        private void paintHorizontal(Graphics2D g2d) {
                paint(g2d, this.getWidth());
        }
        
        private void paintVertical(Graphics2D g2d) {
              
                AffineTransform oldAT = g2d.getTransform();
                
                AffineTransform rot = AffineTransform.getRotateInstance(-Math.PI/2.);
                AffineTransform tra = AffineTransform.getTranslateInstance(- this.getBounds().getHeight(), 0.);
                rot.concatenate(tra);
                
                g2d.transform(rot); 
                
                paint(g2d, this.getBounds().getHeight());
               
                g2d.setTransform(oldAT);
        }
        
        public Dimension getPreferredSize() {
                if (modus == HORIZONTAL) {
                        return new Dimension(0, thickness);
                }
                return new Dimension(thickness, 0);
        }
        
        public Dimension getMinimumSize() {
                return getPreferredSize();
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
            text_baseline = (tickBig + fontSize);
            thickness = text_baseline + border;

            font = new Font("ARIAL", Font.PLAIN, fontSize);        
        }

        /**
         * bei true wird der Wert (long oder double) in Sekunden als Zeit dargestellt
         * @param representsTime 
         */
        public void setRepresentsTime(boolean representsTime) {
            this.representsTime = representsTime;
        }
        
        private String formatNumber(double d) {
            if (! representsTime) 
                return nf.format(d);
            
            long time = (long) (1000 * d);
            buffer.setTime(time);
            return timeFormat.format(buffer);
        }

        
        /* Test */
        public static void main(String[] args) {
                
                JFrame frame = new JFrame("test");
                frame.setSize(1024,768);
                
                JSplitPane panel = new JSplitPane();
                panel.setPreferredSize(new Dimension(1024,768));
                
                RulerComponent ruler1 = new RulerComponent(RulerComponent.HORIZONTAL);
                panel.setLeftComponent(ruler1);
                ruler1.setMinMax(21456.76, 23644.64);
                
                RulerComponent ruler2 = new RulerComponent(RulerComponent.VERTICAL);
                panel.setRightComponent(ruler2);
                ruler2.setMinMax(21456.76, 23644.64);
                
                frame.getContentPane().add(panel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                
        }
}
