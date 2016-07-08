package applications.transshipment.analysis.Problem;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.structs.RailroadTrack;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Train;
import applications.transshipment.model.structs.Wagon;
import bijava.graphics.canvas2D.Arrow2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import javax.vecmath.Vector2d;
import util.GraphicTools;

/**
 * @author bode
 */
public class GeneratorAuswertung implements ImageObserver {

    List<Train> trains;
    List<LoadUnit> loadUnits;
    List<RailroadTrack> tracks;
    private AffineTransform transform;
    static int dicke = 4;
    static int hoehe = 16;
    static double hoeheGleis = 50;
    static int spaceForText = 12;
    private int anzahlGleise;
    private int tickWidth = 15 * 60 * 1000;
    private int majorTick = 4; // jeder 4. ist ein majortick
    private ArrayList<Rectangle2D.Double> gleisRecs = new ArrayList<Rectangle2D.Double>();
    private HashMap<TrainElement, Rectangle2D> timeLinesBounds = new HashMap<TrainElement, Rectangle2D>();
    private HashMap<Train, Rectangle2D> train2timeLinesBounds = new HashMap<Train, Rectangle2D>();
    private HashMap<LoadUnitStorage, HashMap<LoadUnitStorage, Integer>> entladungToValue = new HashMap<LoadUnitStorage, HashMap<LoadUnitStorage, Integer>>();
    private HashMap<LoadUnitStorage, HashMap<LoadUnitStorage, Integer>> beladungFromValue = new HashMap<LoadUnitStorage, HashMap<LoadUnitStorage, Integer>>();
    private List<Train> trainsToVis;

    public static void generatePath(String file) {
        File fausw_a = new File(file);
        if (!fausw_a.exists()) {
            fausw_a.mkdirs();
        }
    }

    public enum Modus {

        beladung,
        entladung
    }

    public GeneratorAuswertung(List<Train> trainsToVis, List<Train> trains, List<LoadUnit> loadUnits, List<RailroadTrack> tracks) {
        init(trainsToVis, trains, loadUnits, tracks);
    }

    private void init(List<Train> trainsToVis, List<Train> trains, List<LoadUnit> loadUnits, List<RailroadTrack> tracks) {
        SimpleStorage dummyStore = new SimpleStorage(new ArrayList());
        this.trainsToVis = trainsToVis;
        this.trains = trains;
        this.loadUnits = loadUnits;
        this.tracks = tracks;
        for (Train t : trains) {
            timeLinesBounds.put(new TrainElement(t), null);
            train2timeLinesBounds.put(t, null);
        }
        this.anzahlGleise = tracks.size();

        for (Train t : trains) {
            HashMap<LoadUnitStorage, Integer> entladungen = new HashMap<LoadUnitStorage, Integer>();
            HashMap<LoadUnitStorage, Integer> beladungen = new HashMap<LoadUnitStorage, Integer>();
            entladungToValue.put(t, entladungen);
            beladungFromValue.put(t, beladungen);
        }

        for (LoadUnit l : loadUnits) {

            LoadUnitStorage finalOrigin = null;
            LoadUnitStorage finalDestination = null;

            LoadUnitStorage origin = l.getOrigin();
            LoadUnitStorage destination = l.getDestination();

            /**
             * Origin setzen
             */
            if (origin instanceof Slot) {
                Slot s = (Slot) origin;
                finalOrigin = (Train) ((Wagon) s.getSuperResource()).getSuperResource();
            } else if (origin instanceof SimpleStorage || origin instanceof SimpleStorageRow) {
                finalOrigin = dummyStore;
            } else {
                finalOrigin = origin;
            }

            /**
             * Destination
             */
            if (destination instanceof Slot) {
                Slot s = (Slot) destination;
                finalDestination = (Train) ((Wagon) s.getSuperResource()).getSuperResource();
            } else if (destination instanceof SimpleStorage || destination instanceof SimpleStorageRow) {
                finalDestination = dummyStore;
            } else {
                finalDestination = destination;
            }

            /**
             * Setzen
             */
            if (finalOrigin != null && finalDestination != null) {
                /**
                 * Entladungen
                 */
                HashMap<LoadUnitStorage, Integer> entladungen = entladungToValue.get(finalOrigin);

                if (entladungen == null) {
                    continue;
                }
                if (entladungen.containsKey(finalDestination)) {
                    Integer value = entladungen.get(finalDestination);
                    value++;
                    entladungen.put(finalDestination, value);
                } else {
                    entladungen.put(finalDestination, 1);
                }
                /**
                 * Beladungen
                 */
                HashMap<LoadUnitStorage, Integer> beladungen = beladungFromValue.get(finalDestination);
                if (beladungen == null) {
                    continue;
                }
                if (beladungen.containsKey(finalOrigin)) {
                    Integer value = beladungen.get(finalOrigin);
                    value++;
                    beladungen.put(finalOrigin, value);
                } else {
                    beladungen.put(finalOrigin, 1);
                }
            }

        }
    }

    public ArrayList<Rectangle2D.Double> getGleisRecs() {
        return gleisRecs;
    }

    public void setGleisRecs(ArrayList<Rectangle2D.Double> gleisRecs) {
        this.gleisRecs = gleisRecs;
    }

    public AffineTransform getTransform() {
        return this.transform;
    }

    public class TrainElement {

        public Train z;

        public TrainElement(Train z) {
            this.z = z;
        }

        public long getStart() {
            return z.getTemporalAvailability().getFromWhen().longValue();
        }

        public long getBegutachtungsStart() {
            return z.getTemporalAvailability().getUntilWhen().longValue();
        }

        public long getEnde() {
            return z.getTemporalAvailability().getUntilWhen().longValue();
        }

        public int getTrack() {
            for (int i = 0; i < GeneratorAuswertung.this.tracks.size(); i++) {
                RailroadTrack rt = GeneratorAuswertung.this.tracks.get(i);
                if (rt.equals(z.getTrack())) {
                    return i;
                }
            }
            return -1;
        }

        public BufferedImage getTimeline(AffineTransform trans) {

            Point2D p1 = trans.transform(new Point2D.Double(getStart(), 0), null);
            p1.setLocation(p1.getX(), hoehe / 2. + spaceForText);

            Point2D p2 = trans.transform(new Point2D.Double(getEnde(), 0), null);
            p2.setLocation(p2.getX() - p1.getX(), hoehe / 2. + spaceForText);

            Point2D p3 = trans.transform(new Point2D.Double(getBegutachtungsStart(), 0), null);
            p3.setLocation(p3.getX() - p1.getX(), hoehe / 2. + spaceForText);

            Line2D standdauerView = new Line2D.Double(new Point2D.Double(0, hoehe / 2. + spaceForText), p2);

            Shape[] shapes = new Shape[4];
            shapes[0] = new Rectangle2D.Double(standdauerView.getX1(), standdauerView.getY1() - dicke / 2, standdauerView.getX2() - standdauerView.getX1(), dicke);
            shapes[1] = new Rectangle2D.Double(standdauerView.getX1() - dicke / 2, standdauerView.getY1() - hoehe / 2, dicke, hoehe);
            shapes[2] = new Rectangle2D.Double(p3.getX() - dicke / 2, standdauerView.getY1() - hoehe / 2, dicke, hoehe);
            shapes[3] = new Rectangle2D.Double(standdauerView.getX2() - dicke / 2, standdauerView.getY1() - hoehe / 2, dicke, hoehe);

            if (p2.getX() <= 0) {
                return null;
            }
            BufferedImage image = new BufferedImage((int) (p2.getX()), hoehe + spaceForText, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = image.createGraphics();

            g.setColor(Color.black);
            g.setStroke(new BasicStroke(0.5f));
            for (Shape s : shapes) {
                g.fill(s);
            }
            g.drawString(Integer.toString(z.getNumber()), 2, spaceForText);
            return image;
        }
    }

    public BufferedImage getPlot(Modus modus) {
        HashMap<LoadUnitStorage, HashMap<LoadUnitStorage, Integer>> ladungsmap = entladungToValue;
        if (modus == Modus.beladung) {
            ladungsmap = beladungFromValue;
        }
        BufferedImage image = new BufferedImage(1800, 1600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setPaint(Color.BLACK);
        int n = this.anzahlGleise;
        long minx = timeLinesBounds.keySet().iterator().next().getStart();
        long maxx = timeLinesBounds.keySet().iterator().next().getEnde();
        for (TrainElement telem : timeLinesBounds.keySet()) {
            if (telem.getStart() < minx) {
                minx = telem.getStart();
            }
            if (telem.getEnde() > maxx) {
                maxx = telem.getEnde();
            }
        }

        transform = GraphicTools.getW2SXScaled(new Rectangle2D.Double(minx, 0, maxx - minx, n), image.getWidth() - 40, 20, 0, (-image.getHeight() - 50) / n);
        // Timescale
        long startTime = (long) (minx / (double) tickWidth) * tickWidth;
        long majorTickStart = (long) (minx / ((double) tickWidth * majorTick)) * tickWidth * majorTick;
        BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5, 10}, 0);
        int offset = 0;
        int fHeight = g.getFontMetrics(g.getFont()).getHeight();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        for (long t = startTime; t <= maxx; t += tickWidth) {
            long tickNo = (t - majorTickStart) / tickWidth;
            Point2D res = transform.transform(new Point2D.Double(t, 0), null);
            if (tickNo % majorTick == 0) {
                g.setColor(new Color(0, 0, 0, 0.5f));
                g.setStroke(new BasicStroke());
                offset = (int) (1.5f * fHeight);
                String str = sdf.format(new Date(t));
                double strW = g.getFontMetrics(g.getFont()).getStringBounds(str, g).getWidth();
                g.drawString(str, (int) res.getX() - (int) (strW / 2d), fHeight);
            } else {
                g.setColor(new Color(0, 0, 0, 0.3f));
                g.setStroke(dashed);
                offset = 0;
            }
            g.drawLine((int) res.getX(), offset, (int) res.getX(), image.getHeight());
        }

        if (n != Integer.MAX_VALUE) {
            gleisRecs = new ArrayList<Rectangle2D.Double>();
            Font origFont = g.getFont();
            g.setFont(g.getFont().deriveFont((float) (hoeheGleis / 2)));
            for (int i = 0; i < n; i++) {
                double y = ((new Double(i) / n) * image.getHeight() + 50);
                Rectangle2D.Double rec = new Rectangle2D.Double(0, y, image.getWidth(), hoeheGleis);
                gleisRecs.add(rec);
                g.setColor(new Color(150, 150, 177));
                g.fill(rec);
                g.setColor(new Color(0, 0, 0, 0.25f));
                String str = "Track " + (i);

                g.drawString(str, (int) hoeheGleis / 2, (int) (y - g.getFontMetrics().getHeight() / 2d + hoeheGleis));
            }
            g.setFont(origFont);
        }

        g.setColor(Color.black);

        for (TrainElement telem : timeLinesBounds.keySet()) {
            BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

            g.setStroke(stroke);
            double pos = gleisRecs.get(telem.getTrack()).getCenterY() - hoehe / 2 - spaceForText;
            Point2D p1 = transform.transform(new Point2D.Double(telem.getStart(), 0), null);

            BufferedImage telemimage = telem.getTimeline(transform);
            if (image != null) {
                timeLinesBounds.put(telem, new Rectangle2D.Double((int) p1.getX(), (int) pos, telemimage.getWidth(), telemimage.getHeight()));
                train2timeLinesBounds.put(telem.z, timeLinesBounds.get(telem));
                g.drawImage(telemimage, (int) p1.getX(), (int) pos, this);
            }
        }

        for (Train t : trainsToVis) {
            Rectangle2D rec = train2timeLinesBounds.get(t);
            Point2D p = new Point2D.Double(rec.getCenterX(), rec.getCenterY());
            HashMap<LoadUnitStorage, Integer> ladungen = ladungsmap.get(t);
            int sum = 0;
            for (LoadUnitStorage des : ladungen.keySet()) {
                sum += ladungen.get(des);

                if (des instanceof Train) {
                    Train tdes = (Train) des;
                    Rectangle2D recdes = train2timeLinesBounds.get(tdes);
                    Point2D pdes = new Point2D.Double(recdes.getMinX() + (((tdes.getNumber() % new Double(tracks.size())) / new Double(tracks.size())) * recdes.getWidth()), recdes.getCenterY());
                    Arrow2D pfeil = new Arrow2D(p, pdes);
                    if (modus == Modus.beladung) {
                        pfeil = new Arrow2D(pdes, p);
                    }
                    g.setPaint(Color.BLUE);
                    g.draw(pfeil);
//                AffineTransform fontAT = new AffineTransform();
//                fontAT.rotate(Math.tan((-pdes.getY()+p.getY())/(-pdes.getX()+p.getX())));
//                fontAT.translate(p.getX()+(Math.abs(pdes.getX()-p.getX()))/2., p.getY()+(Math.abs(pdes.getY()-p.getY()))/2.);
                    Font font = new Font("ARIAL", Font.PLAIN, 26);
//                Font rotatedFont = font.deriveFont(fontAT);
                    g.setFont(font);
                    Vector2d richtung = new Vector2d(pdes.getX() - p.getX(), pdes.getY() - p.getY());

                    richtung.scale(0.5);
                    g.setColor(Color.blue);
                    g.drawString(Integer.toString(ladungen.get(tdes)), (float) (p.getX() + richtung.getX() + 5), (float) (p.getY() + richtung.getY()) + 5);
                } else if (des instanceof SimpleStorage) {
                    Integer numberOfLUs2Storage = ladungen.get(des);
                    Font font = new Font("ARIAL", Font.PLAIN, 26);
                    g.setFont(font);
                    g.setColor(Color.blue);
                    g.drawString("LUs2Storage:" + Integer.toString(numberOfLUs2Storage), (float) (image.getWidth() / 2.), (float) (image.getHeight() - 10));
                } else {
                    throw new NoSuchElementException("Unbekannte Destination:" + des);
                }
            }
            Font font = new Font("ARIAL", Font.PLAIN, 26);
            g.setFont(font);
            g.setColor(Color.blue);
            g.drawString("LU gesamt:" + Integer.toString(sum), (float) (image.getWidth() / 2.), (float) (20));
        }

        return image;
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return true;
    }

}
