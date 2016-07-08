package math.geometry;

import applications.mmrcsp.model.basics.TimeSlot;
import bijava.geometry.dim2.Edge2d;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.PolygonalCurve2d;
import bijava.geometry.dim2.SimplePolygon2d;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Zeitabhaengiger Arbeitsbereich eines Kranes. Er wird durch einen Parameter sl
 * und sr beschrieben (linke und rechte Begrenzung des Arbeitsbereiches). Grund-
 * saetzlich gibt es maximale Grenzen in Form von smin und smax, die nie
 * ueberschritten werden koennen.
 *
 * t
 * ^ sl sr |.o o |. o o |. o o |. o o. |. o o . |. o o . |.o o . |o o . |o o .
 * -o----------------------o--.-> s smin smax
 *
 *
 * @author berthold
 */
public class DynamicPolygonalRegion implements Cloneable {

    private PolygonalCurve2d leftBound;
    private PolygonalCurve2d rightBound;
    static double eps = 0.00001;

    private static TimeSlot infinityTS = TimeSlot.getMaximumTimeSlot();
//    private final double timeFactor;

    /**
     * Erzeugt einen neue DynamicPolygonalRegion, die ueberall in der Zeit durch
     * smin und smax begrenzt wird.
     *
     * @param smin linke Grenze
     * @param smax rechte Grenze
     */
    public DynamicPolygonalRegion(double smin, double smax) {
        this(smin, smax, infinityTS.getFromWhen().longValue(), infinityTS.getUntilWhen().longValue());
    }

    /**
     * Erzeugt eine DynamicPolygonalRegion, die im Bereich von tmin bis tmax
     * ueberall durch smin und smax begrenzt wird.
     *
     * @param smin Start (in m?)
     * @param smax Ende (in m?)
     * @param tmin zeitlicher Definitionsbeginn (in Millisekunden)
     * @param tmax zeitliches Definitionsende (in Millisekunden)
     */
    public DynamicPolygonalRegion(double smin, double smax, long tmin, long tmax) {
        if (smin > smax) {
            throw new IllegalArgumentException("smin must not be greater than smax");
        }
        if (tmin > tmax) {
            throw new IllegalArgumentException("tmin must not be greater than tmax");
        }

//        timeFactor = 10./(tmax-tmin);
        double tmin_sec = longToDouble(tmin);
        double tmax_sec = longToDouble(tmax);
        leftBound = new PolygonalCurve2d(
                new Point2d[]{
                    new Point2d(smin, tmin_sec),
                    new Point2d(smin, tmax_sec)
                }
        );
        rightBound = new PolygonalCurve2d(
                new Point2d[]{
                    new Point2d(smax, tmin_sec),
                    new Point2d(smax, tmax_sec)
                }
        );

    }

    /**
     * Erzeugt eine neue DynamicPolygonalRegion mit der linken Grenze
     * <code>leftBound</code> und der rechten Grenze <code>rightBound</code>.
     * Von <code>leftBound</code> und <code>rightBound</code> muessen die
     * y-Koordinaten (die hier als zeitliche Komponente gehandelt werden) des
     * jeweils 1. und letzten Punktes uebereinstimmen. Ausserdem muss fuer alle
     * Zeitpunkte (t), in der PolygonalCurve2d die y-Koordinate eines jeden
     * punktes gelten: leftBound(t) kleiner rightBound(t).
     *
     * @param leftBound
     * @param rightBound
     */
    public DynamicPolygonalRegion(PolygonalCurve2d leftBound, PolygonalCurve2d rightBound) {
        // Gleicher Zeitpunkt der Start-punkte
        if (leftBound.getPointAt(0).y < rightBound.getPointAt(0).y - eps
                || leftBound.getPointAt(0).y > rightBound.getPointAt(0).y + eps) {
            throw new IllegalArgumentException("tmin (y) of both Bounds of first Point must be equal (left: " + leftBound.getPointAt(0).y + ", right: " + rightBound.getPointAt(0).y + ")");
        }
        // Gleicher Zeitpunkt der End-Punkte
        if (leftBound.getPointAt(leftBound.size() - 1).y < rightBound.getPointAt(rightBound.size() - 1).y - eps
                || leftBound.getPointAt(leftBound.size() - 1).y > rightBound.getPointAt(rightBound.size() - 1).y + eps) {
            throw new IllegalArgumentException("tmax (y) of both Bounds of last Point must be equal");
        }
        // linke Seite zeitlich sortiert
//        for (int i=0; i<leftBound.size()-1; i++)
//            if (leftBound.getPointAt(i).y >= leftBound.getPointAt(i+1).y)
//                throw new IllegalArgumentException("Points of leftBound must be in ascending order regarding y-coordinate! "+leftBound);
        checkSortedPts(leftBound.getPoints());
        // rechte Seite zeitlich sortiert
//        for (int i=0; i<rightBound.size()-1; i++)
//            if (rightBound.getPointAt(i).y >= rightBound.getPointAt(i+1).y)
//                throw new IllegalArgumentException("Points of rightBound must be in ascending order regarding y-coordinate! "+rightBound);
        checkSortedPts(rightBound.getPoints());
        // keine Ueberschneidung der Bounds
        if (leftBound.intersects(rightBound)) {
            throw new IllegalArgumentException("sl(t) must be smaller than sr(t) for all t!");
        }
        // und linke Grenze auf der linken seite
        if (leftBound.getPointAt(0).x >= rightBound.getPointAt(0).x) {
            throw new IllegalArgumentException("sl(t) must be smaller than sr(t) for all t! (here sl(" + leftBound.getPointAt(0).y + ")=" + leftBound.getPointAt(0).x + ", sr(" + rightBound.getPointAt(0).y + ")=" + rightBound.getPointAt(0).x + ")");
        }
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    /**
     * Liefert die interne Darstellung von <code>t</code> in Sekunden.
     *
     * @param t Zeitpunkt in Millisekunden
     * @return Zeitpunkt in Sekunden.
     */
    public static double longToDouble(long t) {
        if (t == infinityTS.getUntilWhen().longValue()) {
            return Double.POSITIVE_INFINITY;
        }
        if (t == infinityTS.getFromWhen().longValue()) {
            return Double.NEGATIVE_INFINITY;
        }
        return ((double) t) / 1000.;
    }

    /**
     * Liefert die Darstellung von <code>t<code> in Millisekunden als Long.
     *
     * @param t Zeitpunkt in Sekunden (als double)
     * @return Zeitpunkt in Millisekunden (als long)
     */
    public static long doubleToLong(double t) {
        if (Double.isInfinite(t)) {
            return (t < 0) ? infinityTS.getFromWhen().longValue() : infinityTS.getUntilWhen().longValue();
        }
        return (long) (t * 1000.);
    }

    /**
     * Gibt eine Kopie des Attributs leftBound zurueck.
     *
     * @return
     */
    public PolygonalCurve2d getLeftBound() {
        return leftBound.clone();
    }

    /**
     * Gibt eine Kopie des Attributs rightBound zurueck.
     *
     * @return
     */
    public PolygonalCurve2d getRightBound() {
        return rightBound.clone();
    }

    /**
     * Setzt die Punkte der leftBound auf die gegebenen Punkte pts.
     * <br>
     * es wird eine neue PolygonalCurve2d fuer leftBound erzeugt, falls keiner
     * der folgenden Faelle zutrifft:
     *
     * <ul>
     * <li>Ungleiche Startzeitpunkte:
     * <code>pts[0].y != getTminInSeconds()</code> (auf eps getestet)
     * <li>Ungleiche Endzeitpunkte:
     * <code>pts[pts.length-1].y != getTmaxInSeconds()</code> (auf eps getestet)
     * <li>Punkte in pts hinsichtlich y unsortiert:
     * <code>pts[i].y >= pts[i+1].y</code>
     * <li>Ueberschneidung mit rechter Grenze: Schnitt zwischen PolygonalCurve2d
     * aus pts und rightBound
     * </ul>
     *
     * Es wird davon ausgegangen, dass die y-Koordinaten der Punkte in pts in
     * Sekunden gegeben sind.
     *
     * @param pts
     */
    public void setLeftBound(Point2d[] pts) {
//        if (doubleToLong(pts[1].y) < 0)
//            throw new RuntimeException("aha...");

        // Gleiche Start-Zeitpunkte
        if (!doubleEpsEquals(pts[0].y, this.getTminInSeconds(), eps)) {
            throw new IllegalArgumentException("can't set pts for leftBound: pts[0].y != getTminInSeconds()");
        }
        // Gleiche End-Zeitpunkte
        if (!doubleEpsEquals(pts[pts.length - 1].y, this.getTmaxInSeconds(), eps)) {
            throw new IllegalArgumentException("can't set pts for leftBound: pts[pts.length-1].y != getTmaxInSeconds()");
        }
        // Zeitlich sortiert
        checkSortedPts(pts);

        // Keine Ueberschneidung mit rechter Grenze:
        PolygonalCurve2d newLeftBound = new PolygonalCurve2d(pts);
        if (newLeftBound.intersects(this.rightBound)) {
            throw new IllegalArgumentException("can't set pts for leftBound: intersection with rightBound");
        }
        this.leftBound = newLeftBound;
    }

    /**
     * Setzt die Punkte der rightBound auf die gegebenen Punkte pts.
     * <br>
     * es wird eine neue PolygonalCurve2d fuer rightBound erzeugt, falls keiner
     * der folgenden Faelle zutrifft:
     *
     * <ul>
     * <li>Ungleiche Startzeitpunkte:
     * <code>pts[0].y != getTminInSeconds()</code> (auf eps getestet)
     * <li>Ungleiche Endzeitpunkte:
     * <code>pts[pts.length-1].y != getTmaxInSeconds()</code> (auf eps getestet)
     * <li>Punkte in pts hinsichtlich y unsortiert:
     * <code>pts[i].y >= pts[i+1].y</code>
     * <li>Ueberschneidung mit linker Grenze: Schnitt zwischen PolygonalCurve2d
     * aus pts und leftBound
     * </ul>
     *
     * Es wird davon ausgegangen, dass die y-Koordinaten der Punkte in pts in
     * Sekunden gegeben sind.
     *
     * @param pts
     */
    public void setRightBound(Point2d[] pts) {
        // Gleiche Start-Zeitpunkte
        if (!doubleEpsEquals(pts[0].y, this.getTminInSeconds(), eps)) {
            throw new IllegalArgumentException("can't set pts for rightBound: pts[0].y != getTminInSeconds()");
        }
        // Gleiche End-Zeitpunkte
        if (!doubleEpsEquals(pts[pts.length - 1].y, this.getTmaxInSeconds(), eps)) {
            throw new IllegalArgumentException("can't set pts for rightBound: pts[pts.length-1].y != getTmaxInSeconds()");
        }
        // Zeitlich sortiert
        checkSortedPts(pts);
//
//        for (int i=0; i<pts.length-1; i++)
//            if (pts[i].y >= pts[i+1].y)
//                throw new IllegalArgumentException("can't set pts for rightBound: points of rightBound must be in ascending order regarding y-coordinate! "+pts);
        // Keine Ueberschneidung mit rechter Grenze:
        PolygonalCurve2d newRightBound = new PolygonalCurve2d(pts);
        if (newRightBound.intersects(this.leftBound)) {
            throw new IllegalArgumentException("can't set pts for leftBound: intersection with leftBound");
        }
        this.rightBound = newRightBound;
    }

    /**
     * Ermittelt den zeitlichen Ausschnitt dieser DynamicPolygonalRegion und
     * gibt ein neues Objekt zurueck (Punkte werden tief kopiert)
     *
     * @param ts Zeitliche Grenzen des Ausschnitts
     * @return
     */
    public DynamicPolygonalRegion getSection(TimeSlot ts) {
        return getSection(longToDouble(ts.getFromWhen().longValue()), longToDouble(ts.getUntilWhen().longValue()));
    }

    /**
     * Ermittelt den zeitlichen Ausschnitt dieser DynamicPolygonalRegion und
     * gibt ein neues Objekt zurueck (Punkte werden tief kopiert)
     *
     * @param tmin Zeit in Sekunden seit der Epoche
     * @param tmax Zeit in Sekunden seit der Epoche
     * @return
     */
    public DynamicPolygonalRegion getSection(double tmin, double tmax) {
        Point2d[] l_section = getPointsInRange(leftBound.getPoints(), tmin, tmax);
        Point2d[] r_section = getPointsInRange(rightBound.getPoints(), tmin, tmax);

        return new DynamicPolygonalRegion(new PolygonalCurve2d(l_section), new PolygonalCurve2d(r_section));
    }

    /**
     * Verschiebt die Region um <code>dt</code> in der Zeit.
     *
     * @param dt in Milli-Sekunden
     */
    public void moveInTime(long dt) {
//        if (dt == TimeSlot.MAX_FROM || dt == TimeSlot.MAX_UNTIL)
//            throw new RuntimeException("nicht gepruefter Fall");
        // TODO: pruefen der zeitlichen Verfuegbarkeit!
        // TODO: Inf abfangen
        double dt_sec = longToDouble(dt);
        for (Point2d p : leftBound.getPoints()) {
            p.y += dt_sec;
        }
        for (Point2d p : rightBound.getPoints()) {
            p.y += dt_sec;
        }
    }

    public void moveToZero() {
        double start = leftBound.getPoints()[0].y;
        double startR = rightBound.getPoints()[0].y;
        if(start != startR){
            throw new UnknownError("Unterschiedlich verschoben?");
        }
        for (Point2d p : leftBound.getPoints()) {
            p.y -= start;
        }
        for (Point2d p : rightBound.getPoints()) {
            p.y -= start;
        }
    }

    /**
     * Verschiebt die Region um <code>dt</code> in der Zeit.
     *
     * @param dt in Sekunden
     */
    void moveInTime(double dt) {
        // TODO: pruefen der zeitlichen Verfuegbarkeit!
        if (Double.isInfinite(dt)) {
            throw new RuntimeException("aha!!!");
        }
        for (Point2d p : leftBound.getPoints()) {
            p.y += dt;
        }
        for (Point2d p : rightBound.getPoints()) {
            p.y += dt;
        }
    }

    /**
     * Gibt die Zeitfenster zurück, in die die angefragte DynamicPolygonalRegion
     * reg reinpasst.
     *
     * @param reg
     * @return
     */
    public TimeSlot[] getTimeSlots(DynamicPolygonalRegion reg) {
        // angefragter Bereich zeitlich zu gross
        if (reg.getTmaxInSeconds() - reg.getTminInSeconds() > this.getTmaxInSeconds() - getTminInSeconds()) {
            return new TimeSlot[]{};
        }

        // Auf gleiche zeitliche Hoehe bringen (Tmin sollen gleich sein)
        reg.moveInTime(this.getTminInSeconds() - reg.getTminInSeconds());

        double thisSmin = this.getSmin();
        double thisSmax = this.getSmax();
        double regSmin = reg.getSmin();
        double regSmax = reg.getSmax();

        // reg ragt nach rechts ueber this (contains nie moeglich)
        if (thisSmax < regSmax) {
            return new TimeSlot[]{};
        }
        // reg ragt nach links ueber this (contains nie moeglich)
        if (regSmin < thisSmin) {
            return new TimeSlot[]{};
        }

        ArrayList<TimeSlot> timeSlots = new ArrayList<TimeSlot>();

        // Fuer jedes Zeitfenster...
        boolean weiter = true;
        int step = 1;
        while (weiter) {
            if (reg.getTmaxInSeconds() > this.getTmaxInSeconds()) {
                break;
            }

            double[] hmtm = this.howMuchToMove(reg);

            // 1. Schritt: solange eine Ueberschneidung besteht an einer der Kanten,
            // verschiebe reg soweit nach oben, dass sich die entsprechenden Kanten
            // nicht mehr schneiden, dann pruefen, ob sich reg innerhalb oder ausserhalb
            // von this befindet:
            // falls reg innerhalb this, mache weiter bei schritt 3
            // falls reg schon zu weit oben
            if (step == 1) {
                while (hmtm[0] != 0.) {

                    reg.moveInTime(hmtm[0]);

                    // reg aus gefragtem Bereich raus
                    if (reg.getTmaxInSeconds() > this.getTmaxInSeconds()) {
                        return timeSlots.toArray(new TimeSlot[0]);
                    }
                    hmtm = this.howMuchToMove(reg);
                }
                // Jetzt befindet sich reg nicht mehr im Schnittbereich und es
                // muessen zwei Faelle unterschieden werden:
                // 1.: reg ausserhalb von this, dann weiter mit step2
                // 2.: reg innerhalb this, dann weiter mit step3

                double[] thisValues = this.getValues(reg.getTminInSeconds());
                double regSLeft = reg.getLeftBound().getPointAt(0).x;
                double regSRight = reg.getRightBound().getPointAt(0).x;
                if (regSRight > thisValues[1] || regSLeft < thisValues[0]) {
                    step = 2;
                } else {
                    step = 3;
                }
            }

            // 2. Schritt: reg ist noch nicht in this "enthalten" (ohne Beruecksichtigung des Randes)
            // soweit nach oben schieben, dass reg this geradeso schneidet und dann zu schritt 1 zurueck
            if (step == 2) {

                double[] mindt = this.getMinDt(reg);
                // Sonderfall abfangen, wenn reg unendlich verschoben werden soll.
                if (Double.isInfinite(mindt[0])) {
//                    // Hier ist eine Fallunterscheidung notwendig:
//                    // Entweder ist reg schon ausserhalb der Grenzen von this,
//                    // dann muss ein return erfolgen
//                    double[] thisValues = this.getValues(reg.getTminInSeconds());
//                    double regSLeft = reg.getLeftBound().getPointAt(0).x;
//                    double regSRight = reg.getRightBound().getPointAt(0).x;
//                    if (regSRight > thisValues[1] || regSLeft < thisValues[0]) {
//                        return timeSlots.toArray(new TimeSlot[0]);
//                    }
//                    // sonst wird hier abgebrochen
                    return timeSlots.toArray(new TimeSlot[0]);
                }

                reg.moveInTime(mindt[0]);

                reg.moveInTime(eps);

                step = 1;
            }

            // 3. Schritt: Startzeit ermitteln und reg so weit hochschieben,
            //             bis es gerade keine Ueberschneidung gibt,
            //             Endzeit ermitteln und Timeslot erzeugen,
            //             dann naechst kleineren weg hochschieben. und zu schritt
            //             1
            if (step == 3) {
                double tStart = reg.getTminInSeconds();
                double[] thisValues;

                double[] mindt = this.getMinDt(reg);

                // Sonderfall abfangen, wenn reg unendlich verschoben werden soll.
                if (Double.isInfinite(mindt[0])) {
//                    output.CTSO_Logger.println("#################################################");
//                    output.CTSO_Logger.println("###### reg soll unendlich verschoben werden #####");
//                    output.CTSO_Logger.println("this: "+this);
//                    output.CTSO_Logger.println("reg:  "+reg);
//                    output.CTSO_Logger.println("#################################################");
                    // Hier ist eine Fallunterscheidung notwendig:
                    // Entweder ist reg schon ausserhalb der Grenzen von this,
                    // dann muss ein return erfolgen
                    thisValues = this.getValues(reg.getTminInSeconds());
                    double regSLeft = reg.getLeftBound().getPointAt(0).x;
                    double regSRight = reg.getRightBound().getPointAt(0).x;
                    if (regSRight > thisValues[1] || regSLeft < thisValues[0]) {
                        throw new RuntimeException("should never happen???");
//                        return timeSlots.toArray(new TimeSlot[0]);
                    }
                    // sonst muss ein uneendlicher Wert als TimeSlotEnde
                    // eingefuegt werden
                    double tEnd = Double.POSITIVE_INFINITY;
//                    timeSlots.add(new TimeSlot(doubleToLong(tStart)+1, doubleToLong(tEnd)));
                    // TODO!!!
                    timeSlots.add(TimeSlot.create(doubleToLong(tStart + reg.leftBound.getPointAt(1).y - reg.leftBound.getPointAt(0).y) + 1, doubleToLong(tEnd)));
                    return timeSlots.toArray(new TimeSlot[0]);
//                    // TODO: +1,-1 korrekt? hier nur millisekunden, aber evtl. problematisch...
//                    // gueltiger timeslot gefunden; hinzufuegen...
                }

                reg.moveInTime(mindt[0]);

                // am Ende angekommen, durch tmax gegeben
                double tEnd = reg.getTmaxInSeconds();

                // TODO: +1,-1 korrekt? hier nur millisekunden, aber evtl. problematisch...
                // gueltiger timeslot gefunden; hinzufuegen...
//                timeSlots.add(new TimeSlot(doubleToLong(tStart)+1, doubleToLong(tEnd)-1));
                // TODO!!!
                timeSlots.add(TimeSlot.create(doubleToLong(tStart + reg.leftBound.getPointAt(1).y - reg.leftBound.getPointAt(0).y) + 1, doubleToLong(tEnd)));

                // kleinste moeglichkeit weiterschieben
                reg.moveInTime(eps);
                step = 1;
            }

        }

        return timeSlots.toArray(new TimeSlot[0]);
    }

    /**
     * Diese Methode bestimmt eine Kurve aus pc1 und pc2, die immer das Minimum
     * bzgl. der s-Koordinate (x) von pc1 und pc2 ist. Es wird vorausgesetzt,
     * dass die Punkte in pc1 und pc2 je zeitlich aufsteigend sortiert sind
     * (y-Koordinate) und gleiche Start- und End-Punkte bzgl. der Zeit (y)
     * besitzen. Dies wird in der Regel durch den vorherigen Aufruf von
     * getPointsInRange() mit jeweils gleichen Parametern erreicht.
     *
     * @param pc1
     * @param pc2
     * @return Das Minium von pc1 und pc2 bzgl. der x-Koordinate.
     */
    public static ArrayList<Point2d> getSMinPts(PolygonalCurve2d pc1, PolygonalCurve2d pc2) {

        // Kurven zeitlich sortiert?
        checkSortedPts(pc1.getPoints());
        checkSortedPts(pc2.getPoints());

        // Zeitlich ungleiche Start- und Endpunkte
        if (pc1.getPointAt(0).y != pc2.getPointAt(0).y || pc1.getPointAt(pc1.size() - 1).y != pc2.getPointAt(pc2.size() - 1).y) {
            throw new IllegalArgumentException("Pts of curve1 and curve2 must be in the same interval with respect to t (here y-coordinate)");
        }

        // RueckgabeListe:
        ArrayList<Point2d> minPts = new ArrayList<Point2d>();

        // pivot und other bestimmen
        // pivot ist die Kurve mit dem aktuell kleineren (oder auch gleichen) s, other die andere
        PolygonalCurve2d pivot = pc1;
        PolygonalCurve2d other = pc2;
        // ggf. pivot tauschen
        if (pc2.getPointAt(0).x < pc1.getPointAt(0).x) {
            pivot = pc2;
            other = pc1;
        }

        // laufvariablen fuer beide Kurven:
        int pi = 0; // fuer pivot
        int oi = 0; // fuer other

        while (pivot.size() > pi || other.size() > oi) {

            // Suche aktuell kleineres t (von den Punkten: y)
            // pivot zaehlt auch als der kleinere, wenn beide gleich sind
            // mintCurve: pivot, bzw other, minti: pi bzw. oi
            PolygonalCurve2d mintCurve = pivot;
            int minti = pi;
            if (other.getPointAt(oi).y < pivot.getPointAt(pi).y) {
                mintCurve = other;
                minti = oi;
            }

            // Bestimme s-Werte beider Kurven zum Zeitpunkt mintCurve.getPointAt(minti).y
            double pivot_s = getValue(pivot.getPoints(), mintCurve.getPointAt(minti).y);    // Optimierungspotential: den einen s-Wert direkt ueber den Punkt p_minti bestimmen
            double other_s = getValue(other.getPoints(), mintCurve.getPointAt(minti).y);

            // Fallunterscheidung: entweder ist pivot_s <= other_s oder nicht
            if (pivot_s <= other_s + eps) {
                // Dann den aktuellen pivot punkt hinzufuegen, wenn pivot == tmin
                if (pivot == mintCurve) {
                    minPts.add(pivot.getPointAt(pi).clone());
                }
            } // sonst gibt es einen Schnittpunkt der beiden Kurven
            else {
                // Schnittpunkt bestimmen und einfuegen
                Point2d cutPoint = getIntersectionPoint(
                        pivot.getPointAt(pi),
                        pivot.getPointAt(pi - 1),
                        other.getPointAt(oi),
                        other.getPointAt(oi - 1)
                );
                // Sicherheitscheck: Falls cutPoint null, exception (sollte nie vorkommen)
                if (cutPoint == null) {
                    // Aufgrund von Rechenungenauigkeiten, Berechnung mit Epsilontik durchfuehren
                    cutPoint = getIntersectionPoint(
                            pivot.getPointAt(pi - 1),
                            pivot.getPointAt(pi),
                            other.getPointAt(oi - 1),
                            other.getPointAt(oi),
                            eps
                    );
                    if (cutPoint == null) {
                         
                        throw new RuntimeException("Unbekannter Sonderfall in getSminPts_new aufgetreten!"
                                + "\nunerwartet kein Schnittpunkt zwischen den Geraden "
                                + "[" + pivot.getPointAt(pi - 1) + "," + pivot.getPointAt(pi) + "] "
                                + "und [" + other.getPointAt(oi - 1) + "," + other.getPointAt(oi) + "] "
                                + "gefunden."
                        );
                    }
                }
                // Nur hinzufuegen, wenn noch nicht enthalten
                if (!minPts.get(minPts.size() - 1).epsilonEquals(cutPoint, eps)) {
                    minPts.add(cutPoint);
                }

                // falls other == tmin (bzw. wenn other.y == tmin ist) auch noch den other punkt einfuegen
                if (other.getPointAt(oi).y == mintCurve.getPointAt(minti).y) {
                    minPts.add(other.getPointAt(oi).clone());
                }

                // pivot und other tauschen
                PolygonalCurve2d tmpCurve = other;
                other = pivot;
                pivot = tmpCurve;
                int tmpi = oi;
                oi = pi;
                pi = tmpi;
            }

            // Zaheler anpassen: der mit kleinerem t/y wird erhoeht, bei gleichheit beide
            if (pivot.getPointAt(pi).y < other.getPointAt(oi).y) {
                pi++;
            } else if (other.getPointAt(oi).y < pivot.getPointAt(pi).y) {
                oi++;
            } else {
                pi++;
                oi++;
            }
        }

        checkSortedPts(minPts.toArray(new Point2d[0]));

        return minPts;

    }

    /**
     * Diese Methode bestimmt eine Kurve aus pc1 und pc2, die immer das Maximum
     * bzgl. der s-Koordinate (x) von pc1 und pc2 ist. Es wird vorausgesetzt,
     * dass die Punkte in pc1 und pc2 je zeitlich aufsteigend sortiert sind
     * (y-Koordinate) und gleiche Start- und End-Punkte bzgl. der Zeit (y)
     * besitzen. Dies wird in der Regel durch den vorherigen Aufruf von
     * getPointsInRange() mit jeweils gleichen Parametern erreicht.
     *
     * @param pc1
     * @param pc2
     * @return Das Maxium von pc1 und pc2 bzgl. der x-Koordinate.
     */
    public static ArrayList<Point2d> getSMaxPts(PolygonalCurve2d pc1, PolygonalCurve2d pc2) {

        // Kurven zeitlich sortiert?
        checkSortedPts(pc1.getPoints());
        checkSortedPts(pc2.getPoints());

        // Zeitlich ungleiche Start- und Endpunkte
        if (pc1.getPointAt(0).y != pc2.getPointAt(0).y || pc1.getPointAt(pc1.size() - 1).y != pc2.getPointAt(pc2.size() - 1).y) {
            throw new IllegalArgumentException("Pts of curve1 and curve2 must be in the same interval with respect to t (here y-coordinate)");
        }

        // RueckgabeListe:
        ArrayList<Point2d> maxPts = new ArrayList<Point2d>();

        // pivot und other bestimmen
        // pivot ist die Kurve mit dem aktuell groesseren (oder auch gleichen) s, other die andere
        PolygonalCurve2d pivot = pc1;
        PolygonalCurve2d other = pc2;
        // ggf. pivot tauschen
        if (pc2.getPointAt(0).x > pc1.getPointAt(0).x) {
            pivot = pc2;
            other = pc1;
        }

        // laufvariablen fuer beide Kurven:
        int pi = 0; // fuer pivot
        int oi = 0; // fuer other

        while (pivot.size() > pi || other.size() > oi) {

            // Suche aktuell kleineres t (von den Punkten: y)
            // pivot zaehlt auch als der kleinere, wenn beide gleich sind
            // mintCurve: pivot, bzw other, minti: pi bzw. oi
            PolygonalCurve2d mintCurve = pivot;
            int minti = pi;
            if (other.getPointAt(oi).y < pivot.getPointAt(pi).y) {
                mintCurve = other;
                minti = oi;
            }

            // Bestimme s-Werte beider Kurven zum Zeitpunkt mintCurve.getPointAt(minti).y
            double pivot_s = getValue(pivot.getPoints(), mintCurve.getPointAt(minti).y);    // Optimierungspotential: den einen s-Wert direkt ueber den Punkt p_minti bestimmen
            double other_s = getValue(other.getPoints(), mintCurve.getPointAt(minti).y);

            // Fallunterscheidung: entweder ist pivot_s >= other_s oder nicht
            if (pivot_s >= other_s - eps) {
                // Dann den aktuellen pivot punkt hinzufuegen, wenn pivot == tmin
                if (pivot == mintCurve) {
                    maxPts.add(pivot.getPointAt(pi).clone());
                }
            } // sonst gibt es einen Schnittpunkt der beiden Kurven
            else {
                // Schnittpunkt bestimmen und einfuegen
                Point2d cutPoint = getIntersectionPoint(
                        pivot.getPointAt(pi),
                        pivot.getPointAt(pi - 1),
                        other.getPointAt(oi),
                        other.getPointAt(oi - 1)
                );
                // Sicherheitscheck: Falls cutPoint null, exception (sollte nie vorkommen)
                if (cutPoint == null) {
                    // Aufgrund von Rechenungenauigkeiten, Schnittpunkt nochmal mit epsilon-Kriterium berechnen
                    cutPoint = getIntersectionPoint(
                            pivot.getPointAt(pi - 1),
                            pivot.getPointAt(pi),
                            other.getPointAt(oi - 1),
                            other.getPointAt(oi),
                            eps
                    );

                    if (cutPoint == null) {
                        
                        throw new RuntimeException(
                                "Unbekannter Sonderfall in getSmaxPts_new aufgetreten! "
                                + "\nunerwartet kein Schnittpunkt zwischen den Geraden "
                                + "[" + pivot.getPointAt(pi - 1) + "," + pivot.getPointAt(pi) + "] "
                                + "und [" + other.getPointAt(oi - 1) + "," + other.getPointAt(oi) + "] "
                                + "gefunden."
                        );
                    }
                }
                // Nur hinzufuegen, wenn noch nicht enthalten
                if (!maxPts.get(maxPts.size() - 1).epsilonEquals(cutPoint, eps)) {
                    maxPts.add(cutPoint);
                }

                // falls other == tmin (bzw. wenn other.y == tmin ist) auch noch den other punkt einfuegen
                if (other.getPointAt(oi).y == mintCurve.getPointAt(minti).y) {
                    maxPts.add(other.getPointAt(oi).clone());
                }

                // pivot und other tauschen
                PolygonalCurve2d tmpCurve = other;
                other = pivot;
                pivot = tmpCurve;
                int tmpi = oi;
                oi = pi;
                pi = tmpi;
            }

            // Zaheler anpassen: der mit kleinerem t/y wird erhoeht, bei gleichheit beide
            if (pivot.getPointAt(pi).y < other.getPointAt(oi).y) {
                pi++;
            } else if (other.getPointAt(oi).y < pivot.getPointAt(pi).y) {
                oi++;
            } else {
                pi++;
                oi++;
            }
        }

        checkSortedPts(maxPts.toArray(new Point2d[0]));

        return maxPts;

    }

    /**
     *
     * @param bc
     * @param fromRight
     * @return
     */
    public boolean constrain(PolygonalCurve2d bc, boolean fromRight) {

//        for (int i=0; i<bc.size()-1; i++)
//            if (bc.getPointAt(i).y >= bc.getPointAt(i+1).y)
//                throw new IllegalArgumentException("Pts of boundaryCondition must be ordered with respect to y-coordinate! "+bc);
        checkSortedPts(bc.getPoints());
        // Zeitlich keine Ueberschneidung
        if (bc.getPointAt(0).y > this.getTmaxInSeconds() || bc.getPointAt(bc.size() - 1).y < this.getTminInSeconds()) {
            return false;
        }

        // Zeitlich ueberschneidende Intervalle finden:
        double tmin = Math.max(this.getTminInSeconds(), bc.getPointAt(0).y);
        double tmax = Math.min(this.getTmaxInSeconds(), bc.getPointAt(bc.size() - 1).y);

        // zeitlich begrenzte bounds bestimmen
        PolygonalCurve2d thisLeftBound = new PolygonalCurve2d(
                getPointsInRange(this.getLeftBound().getPoints(), tmin, tmax)
        );
        PolygonalCurve2d thisRightBound = new PolygonalCurve2d(
                getPointsInRange(this.getRightBound().getPoints(), tmin, tmax)
        );
        PolygonalCurve2d boundaryCondition = new PolygonalCurve2d(
                getPointsInRange(bc.getPoints(), tmin, tmax)
        );

        // Ueberschneidung zwischen kontraeren Bounds testen (linke mit rechter und umgekehrt)
        if (fromRight && thisLeftBound.intersects(boundaryCondition)) {
            return false;
        }
        if (!fromRight && thisRightBound.intersects(boundaryCondition)) {
            return false;
        }

        // Pruefen, ob die bc am Anfang und Ende jeweils wieder aus Arbeitsbereich raus ist...
        // Ende
        if (this.getTmax() >= bc.getPointAt(bc.size() - 1).y) {
            double[] thisValues = this.getValues(bc.getPointAt(bc.size() - 1).y);
            // beschraenken durch rechten Nachbar
            if (fromRight) {
                if (thisValues[1] > bc.getPointAt(bc.size() - 1).x) {
                    throw new IllegalArgumentException("Kann this nach oben nicht richtig beschraenken (Kranausfahrt des rechten Nachbar-Kranes fehlt...)");
                }
            } // beschraenken durch linken Nachbar
            else {
                if (thisValues[0] < bc.getPointAt(bc.size() - 1).x) {
                    throw new IllegalArgumentException("Kann this nach oben nicht richtig beschraenken (Kranausfahrt des linken Nachbar-Kranes fehlt...)");
                }
            }
        }

        // Anfang
        if (this.getTmin() <= bc.getPointAt(0).y) {
            double[] thisValues = this.getValues(bc.getPointAt(0).y);
            // beschraenken durch rechten Nachbar
            if (fromRight) {
                if (thisValues[1] > bc.getPointAt(0).x) {
                    throw new IllegalArgumentException("Kann this nach unten nicht richtig beschraenken (Kraneinfahrt des rechten Nachbar-Kranes fehlt...)");
                }
            } // beschraenken durch linken Nachbar
            else {
                if (thisValues[0] < bc.getPointAt(0).x) {
                    throw new IllegalArgumentException("Kann this nach unten nicht richtig beschraenken (Kraneinfahrt des linken Nachbar-Kranes fehlt...)");
                }
            }
        }

        // Jetzt rechte bzw. linke Grenze "beschneiden"
        ArrayList<Point2d> newBound = null;

        if (fromRight) {

            newBound = getSMinPts(thisRightBound, boundaryCondition);

            // in neue Bound jetzt noch den Anfang und das Ende der alten Bound einfuegen
            int i = 0;
            for (Point2d p : this.getRightBound().getPoints()) {
                // davor einfuegen
                if (p.y < tmin) {
                    newBound.add(i++, p);
                } else if (p.y > tmax) {
                    newBound.add(p);
                }
            }
        } // =================================== //
        // fromLeft: "dat gleiche in Gruen"... //
        // =================================== //
        else {

            newBound = getSMaxPts(thisLeftBound, boundaryCondition);
            checkSortedPts(newBound.toArray(new Point2d[0]));

            // in neue Bound jetzt noch den Anfang und das Ende der alten Bound einfuegen
            int i = 0;
            for (Point2d p : this.getLeftBound().getPoints()) {
                // davor einfuegen
                if (p.y < tmin) {
                    newBound.add(i++, p);
                } else if (p.y > tmax) {
                    newBound.add(p);
                }
            }
        }

        // Ueberfluessige Punkte rausschmeissen
        for (int i = 1; i < newBound.size() - 1; i++) {
//            if (newBound.get(i-1).x == newBound.get(i  ).x &&
//                newBound.get(i  ).x == newBound.get(i+1).x)
            if (doubleEpsEquals(newBound.get(i - 1).x, newBound.get(i).x, eps)
                    && doubleEpsEquals(newBound.get(i).x, newBound.get(i + 1).x, eps)) {
                newBound.remove(i);
                i--;
            }
        }

        if (fromRight) {
            this.setRightBound(newBound.toArray(new Point2d[]{}));
        } //            this.rightBound = new PolygonalCurve2d(newBound.toArray(new Point2d[]{}));
        else {
            try {
                this.setLeftBound(newBound.toArray(new Point2d[]{}));
            } catch (IllegalArgumentException e) {
                System.exit(0);
            }
        }
//            this.leftBound = new PolygonalCurve2d(newBound.toArray(new Point2d[]{}));
        return true;
    }

    public boolean unconstrain(PolygonalCurve2d bc, boolean fromRight) {

//        for (int i=0; i<bc.size()-1; i++)
//            if (bc.getPointAt(i).y >= bc.getPointAt(i+1).y)
//                throw new IllegalArgumentException("Pts of boundaryCondition must be ordered with respect to y-coordinate! "+bc);
        checkSortedPts(bc.getPoints());
        // Zeitlich keine Ueberschneidung
        if (bc.getPointAt(0).y > this.getTmaxInSeconds() || bc.getPointAt(bc.size() - 1).y < this.getTminInSeconds()) {
            return false;
        }

        // Zeitlich ueberschneidende Intervalle finden:
        double tmin = Math.max(this.getTminInSeconds(), bc.getPointAt(0).y);
        double tmax = Math.min(this.getTmaxInSeconds(), bc.getPointAt(bc.size() - 1).y);

        // zeitlich begrenzte bounds bestimmen
        PolygonalCurve2d thisLeftBound = new PolygonalCurve2d(
                getPointsInRange(this.getLeftBound().getPoints(), tmin, tmax)
        );
        PolygonalCurve2d thisRightBound = new PolygonalCurve2d(
                getPointsInRange(this.getRightBound().getPoints(), tmin, tmax)
        );
        PolygonalCurve2d boundaryCondition = new PolygonalCurve2d(
                getPointsInRange(bc.getPoints(), tmin, tmax)
        );

        // Ueberschneidung zwischen kontraeren Bounds testen (linke mit rechter und umgekehrt)
        if (fromRight && thisLeftBound.intersects(boundaryCondition)) {
            return false;
        }
        if (!fromRight && thisRightBound.intersects(boundaryCondition)) {
            return false;
        }

        // Pruefen, ob die bc am Anfang und Ende jeweils wieder aus Arbeitsbereich raus ist...
        // Ende
        if (this.getTmax() >= bc.getPointAt(bc.size() - 1).y) {
            double[] thisValues = this.getValues(bc.getPointAt(bc.size() - 1).y);
            // beschraenken durch rechten Nachbar
            if (fromRight) {
                if (thisValues[1] > bc.getPointAt(bc.size() - 1).x) {
                    throw new IllegalArgumentException("Kann this nach oben nicht richtig beschraenken (Kranausfahrt des rechten Nachbar-Kranes fehlt...)");
                }
            } // beschraenken durch linken Nachbar
            else {
                if (thisValues[0] < bc.getPointAt(bc.size() - 1).x) {
                    throw new IllegalArgumentException("Kann this nach oben nicht richtig beschraenken (Kranausfahrt des linken Nachbar-Kranes fehlt...)");
                }
            }
        }

        // Anfang
        if (this.getTmin() <= bc.getPointAt(0).y) {
            double[] thisValues = this.getValues(bc.getPointAt(0).y);
            // beschraenken durch rechten Nachbar
            if (fromRight) {
                if (thisValues[1] > bc.getPointAt(0).x) {
                    throw new IllegalArgumentException("Kann this nach unten nicht richtig beschraenken (Kraneinfahrt des rechten Nachbar-Kranes fehlt...)");
                }
            } // beschraenken durch linken Nachbar
            else {
                if (thisValues[0] < bc.getPointAt(0).x) {
                    throw new IllegalArgumentException("Kann this nach unten nicht richtig beschraenken (Kraneinfahrt des linken Nachbar-Kranes fehlt...)");
                }
            }
        }

        // Jetzt rechte bzw. linke Grenze "beschneiden"
        ArrayList<Point2d> newBound = null;

        if (fromRight) {

            newBound = getSMinPts(thisRightBound, boundaryCondition);

            // in neue Bound jetzt noch den Anfang und das Ende der alten Bound einfuegen
            int i = 0;
            for (Point2d p : this.getRightBound().getPoints()) {
                // davor einfuegen
                if (p.y < tmin) {
                    newBound.add(i++, p);
                } else if (p.y > tmax) {
                    newBound.add(p);
                }
            }
        } // =================================== //
        // fromLeft: "dat gleiche in Gruen"... //
        // =================================== //
        else {

            newBound = getSMaxPts(thisLeftBound, boundaryCondition);

            // in neue Bound jetzt noch den Anfang und das Ende der alten Bound einfuegen
            int i = 0;
            for (Point2d p : this.getLeftBound().getPoints()) {
                // davor einfuegen
                if (p.y < tmin) {
                    newBound.add(i++, p);
                } else if (p.y > tmax) {
                    newBound.add(p);
                }
            }
        }

        // Ueberfluessige Punkte rausschmeissen
        for (int i = 1; i < newBound.size() - 1; i++) {
            if (newBound.get(i - 1).x == newBound.get(i).x
                    && newBound.get(i).x == newBound.get(i + 1).x) {
                newBound.remove(i);
                i--;
            }
        }

        if (fromRight) {
            this.setRightBound(newBound.toArray(new Point2d[]{}));
        } //            this.rightBound = new PolygonalCurve2d(newBound.toArray(new Point2d[]{}));
        else {
            this.setLeftBound(newBound.toArray(new Point2d[]{}));
        }
//            this.leftBound = new PolygonalCurve2d(newBound.toArray(new Point2d[]{}));
        return true;
    }

    /**
     * Ermittelt die Schnittregion zwischen <code>this</code> und
     * <code>other</code>. Falls keine gueltige Schnittregion gebildet werden
     * kann, wird <code>null</code> zurueckgegeben. Eine gueltige Schnittregion
     * muss die folgenden Eigenschaften erfuellen:
     *
     * <ol>
     * <li>Start- und End-Zeitpunkte der linken und rechten Bounds stimmen
     * jeweils ueberein.
     * <li>Dazu muss sichergestellt werden, dass die Regionen ueberhaupt einen
     * zeitlichen ueberschneidungsbereich haben.
     * <li>Auszerdem duerfen sich kontraere Grenzen (linke von der einen und
     * rechte von der anderen und umgekehrt) nicht schneiden.
     * <li>Es muss eine geometrische Ueberschneidung beider Regionen vorhanden
     * sein
     * </ol>
     *
     * @param other
     * @return die Schnittregion von this und other, falls eine gueltige
     * existiert, sonst null.
     */
    public DynamicPolygonalRegion intersection(DynamicPolygonalRegion other) {
        // Zeitlich keine Ueberschneidung
        if (other.getTminInSeconds() > this.getTmaxInSeconds() || other.getTmaxInSeconds() < this.getTminInSeconds()) {
            return null;
        }

        // Zeitlich ueberschneidende Intervalle finden:
        double tmin = Math.max(this.getTminInSeconds(), other.getTminInSeconds());
        double tmax = Math.min(this.getTmaxInSeconds(), other.getTmaxInSeconds());

        // zeitlich begrenzte bounds bestimmen
        PolygonalCurve2d thisLeftBound = new PolygonalCurve2d(
                getPointsInRange(this.getLeftBound().getPoints(), tmin, tmax)
        );
        PolygonalCurve2d thisRightBound = new PolygonalCurve2d(
                getPointsInRange(this.getRightBound().getPoints(), tmin, tmax)
        );
        PolygonalCurve2d otherLeftBound = new PolygonalCurve2d(
                getPointsInRange(other.getLeftBound().getPoints(), tmin, tmax)
        );
        PolygonalCurve2d otherRightBound = new PolygonalCurve2d(
                getPointsInRange(other.getRightBound().getPoints(), tmin, tmax)
        );

        // Ueberschneidung zwischen kontraeren Bounds testen (linke mit rechter und umgekehrt)
        if (thisLeftBound.intersects(otherRightBound)) {
            return null;
        }
        if (thisRightBound.intersects(otherLeftBound)) {
            return null;
        }

        // Pruefen, ob geometrische Ueberschneidung vorhanden:
        if (thisLeftBound.getPointAt(0).x >= otherRightBound.getPointAt(0).x) {
            return null;
        }
        if (thisRightBound.getPointAt(0).x <= otherLeftBound.getPointAt(0).x) {
            return null;
        }

        // Hier angekommen bedeutet, dass es einen zeitlichen und zulaessigen
        // geometrischen Ueberschneidungsbereich gibt...
        if (!thisLeftBound.intersects(otherLeftBound) && !thisRightBound.intersects(otherRightBound)) {
            // Wenn sich die jeweiligen Grenzen nie schneiden, dann ist die Schnittmenge
            // Beschrieben durch die jeweils inneren Grenzen
            PolygonalCurve2d newLeft = (thisLeftBound.getPointAt(0).x < otherLeftBound.getPointAt(0).x) ? otherLeftBound : thisLeftBound;
            PolygonalCurve2d newRight = (thisRightBound.getPointAt(0).x > otherRightBound.getPointAt(0).x) ? otherRightBound : thisRightBound;
            return new DynamicPolygonalRegion(newLeft, newRight);
        }

        // Sonst muessen die Grenzen neu berechnet werden
        // Linke seite
        boolean weiter = true;
        ArrayList<Point2d> leftPts = new ArrayList<Point2d>();
        int thisIndexLeft = 0;
        int otherIndexLeft = 0;
        Point2d pivot = null;
        while (weiter) {
            // Punkte in zeitlicher Reihenfolge
            // this-Zeipunkt kleiner
            if (thisLeftBound.getPointAt(thisIndexLeft).y < otherLeftBound.getPointAt(otherIndexLeft).y) {
                pivot = thisLeftBound.getPointAt(thisIndexLeft);
                if (thisIndexLeft >= thisLeftBound.size() - 1) {
                    weiter = false;
                }
                thisIndexLeft++;
            } // Other zeitpunkt kleiner
            //            output.CTSO_Logger.println("thisIndexLeft="+thisIndexLeft+"(size="+thisLeftBound.size()+")   otherIndexLeft="+otherIndexLeft+"(size="+otherLeftBound.size()+")");
            else if (thisLeftBound.getPointAt(thisIndexLeft).y > otherLeftBound.getPointAt(otherIndexLeft).y) {
                pivot = otherLeftBound.getPointAt(otherIndexLeft);
                if (otherIndexLeft >= otherLeftBound.size() - 1) {
                    weiter = false;
                }
                otherIndexLeft++;
            } // beide Zeitpunkte gleich
            else if (thisLeftBound.getPointAt(thisIndexLeft).y == otherLeftBound.getPointAt(otherIndexLeft).y) {
                pivot = thisLeftBound.getPointAt(thisIndexLeft);
                if (thisIndexLeft >= thisLeftBound.size() - 1 || otherIndexLeft >= otherLeftBound.size() - 1) {
                    weiter = false;
                }
                thisIndexLeft++;
                otherIndexLeft++;
            }

            // Values vergleichen
            double thisValue = getValue(thisLeftBound.getPoints(), pivot.y);
            double otherValue = getValue(otherLeftBound.getPoints(), pivot.y);

            if (Double.isNaN(thisValue) || Double.isNaN(otherValue)) {
                continue;
            }

            leftPts.add(new Point2d(Math.max(thisValue, otherValue), pivot.y));

        }

        // Rechte Seite
        weiter = true;
        ArrayList<Point2d> rightPts = new ArrayList<Point2d>();
        int thisIndexRight = 0;
        int otherIndexRight = 0;
        pivot = null;
        while (weiter) {
            // Punkte in zeitlicher Reihenfolge
            // this-Zeipunkt kleiner
            if (thisRightBound.getPointAt(thisIndexRight).y < otherRightBound.getPointAt(otherIndexRight).y) {
                pivot = thisRightBound.getPointAt(thisIndexRight);
                if (thisIndexRight >= thisRightBound.size() - 1) {
                    weiter = false;
                }
                thisIndexRight++;
            } // Other zeitpunkt kleiner
            else if (thisRightBound.getPointAt(thisIndexRight).y > otherRightBound.getPointAt(otherIndexRight).y) {
                pivot = otherRightBound.getPointAt(otherIndexRight);
                if (otherIndexRight >= otherRightBound.size() - 1) {
                    weiter = false;
                }
                otherIndexRight++;
            } // beide Zeitpunkte gleich
            else if (thisRightBound.getPointAt(thisIndexRight).y == otherRightBound.getPointAt(otherIndexRight).y) {
                pivot = thisRightBound.getPointAt(thisIndexRight);
                if (thisIndexRight >= thisRightBound.size() - 1 || otherIndexRight >= otherRightBound.size() - 1) {
                    weiter = false;
                }
                thisIndexRight++;
                otherIndexRight++;
            }

            // Values vergleichen
            double thisValue = getValue(thisRightBound.getPoints(), pivot.y);
            double otherValue = getValue(otherRightBound.getPoints(), pivot.y);

            rightPts.add(new Point2d(Math.min(thisValue, otherValue), pivot.y));

        }

        Point2d[] lp = new Point2d[leftPts.size()];
        leftPts.toArray(lp);
        Point2d[] rp = new Point2d[rightPts.size()];
        rightPts.toArray(rp);

        return new DynamicPolygonalRegion(
                new PolygonalCurve2d(lp),
                new PolygonalCurve2d(rp)
        );

    }

    public boolean contains(DynamicPolygonalRegion other) {
        // Pruefen ob, other in this passt:
        if (!this.boundaryIntersects(other)) {
            // Kein schnitt, also testen ob irgendein punkt innerhalb liegt
            Point2d testPt = other.getLeftBound().getPointAt(0);
            double[] thisValues = this.getValues(testPt.y);
            // liegt innerhalb
            if (thisValues[0] <= testPt.x && thisValues[1] >= testPt.x) {
                return true;
            } // liegt draussen
            else {
                return false;
            }
        }
        // Schnitt der bereiche
        return false;
    }

    /**
     * Testet, ob sich die Raender von this und other schneiden.
     *
     * @param other
     * @return
     */
    public boolean boundaryIntersects(DynamicPolygonalRegion other) {

        SimplePolygon2d thisPoly = this.getPolygonalRepresentation(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        SimplePolygon2d otherPoly = other.getPolygonalRepresentation(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (int i = 0; i < thisPoly.size(); i++) {

            Point2d q0 = thisPoly.getPointAt(i);
            Point2d q1 = thisPoly.getPointAt((i + 1) % thisPoly.size());

            for (int j = 0; j < otherPoly.size(); j++) {

                Point2d p0 = otherPoly.getPointAt(j);
                Point2d p1 = otherPoly.getPointAt((j + 1) % otherPoly.size());
                try {
                    if (intersects(p0, p1, q0, q1)) {
                        return true;
                    }
                } catch (IllegalArgumentException ex) {
                    System.err.println("boundaryIntersects: intersects(" + p0 + ", " + p1 + ", " + q0 + ", " + q1 + ") uebersprungen wegen IllegalArgumentException...");
                }
            }
        }
        return false;
    }

    public static Point2d getIntersectionPoint(Point2d p0, Point2d p1, Point2d q0, Point2d q1) {
        return getIntersectionPoint(p0, p1, q0, q1, 0.);
    }

    public static Point2d getIntersectionPoint(Point2d p0, Point2d p1, Point2d q0, Point2d q1, double epsilon) {

        // Hier ist eine Sonderbehandlung bei der Schnittpunktermittlung notwendig,
        // falls unendliche Koordinaten im Spiel sind (Edge2d kann damit nicht umgehen)
        // Anzahl der x-Koordinaten von der Kante p, die unendlich sind
        int pEdgeXInf = 0;
        if (Double.isInfinite(p0.x)) {
            pEdgeXInf++;
        }
        if (Double.isInfinite(p1.x)) {
            pEdgeXInf++;
        }
        // Anzahl der y-Koordinaten von der Kante p, die unendlich sind
        int pEdgeYInf = 0;
        if (Double.isInfinite(p0.y)) {
            pEdgeYInf++;
        }
        if (Double.isInfinite(p1.y)) {
            pEdgeYInf++;
        }
        // Anzahl der x-Koordinaten von der Kante q, die unendlich sind
        int qEdgeXInf = 0;
        if (Double.isInfinite(q0.x)) {
            qEdgeXInf++;
        }
        if (Double.isInfinite(q1.x)) {
            qEdgeXInf++;
        }
        // Anzahl der y-Koordinaten von der Kante q, die unendlich sind
        int qEdgeYInf = 0;
        if (Double.isInfinite(q0.y)) {
            qEdgeYInf++;
        }
        if (Double.isInfinite(q1.y)) {
            qEdgeYInf++;
        }

        // Wenn p bzgl. x und y unendliche Koordinaten hat, dann kann kein Schnittpunkt berechnet werden
        if (pEdgeXInf > 0 && pEdgeYInf > 0) {
            throw new IllegalArgumentException("cannot determine IntersectionPoint"
                    + " of Edges, because p has infinite"
                    + " coordinates in x and y!");
        }
        // Wenn q bzgl. x und y unendliche Koordinaten hat, dann kann kein Schnittpunkt berechnet werden
        if (qEdgeXInf > 0 && qEdgeYInf > 0) {
            throw new IllegalArgumentException("cannot determine IntersectionPoint"
                    + " of Edges, because q has infinite"
                    + " coordinates in x and y!");
        }

        // 1. Fall: keine unendlichen Koordinaten:
        if (pEdgeXInf == 0 && pEdgeYInf == 0 && qEdgeXInf == 0 && qEdgeYInf == 0) {
            return getIntersectionPointEps(p0, p1, q0, q1, epsilon);
        }
//            return Edge2d.getIntersectionPoint(p0, p1, q0, q1);
        // falls es doch unendliche Koordinaten gibt, koennen einige Faelle
        // behandelt werden:

        // 2. Fall: p liegt parallel zur y-Achse und q parallel zur x-Achse
        //          mit sich ueberschneidenden Intervallen
        //          Dann ist der Schnittpunkt: (p.x,q.y)
        if ((pEdgeYInf == 1 || (pEdgeYInf == 2 && Math.signum(p0.y) != Math.signum(p1.y))) // Mindestens eine y-Koordinate von p ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && (qEdgeXInf == 1 || (qEdgeXInf == 2 && Math.signum(q0.x) != Math.signum(q1.x))) // Mindestens eine x-Koordinate von q ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && doubleEpsEquals(p0.x, p1.x, eps) // p parallel zur y-Achse (p0.x und p1.x bis auf eps gleich)
                && doubleEpsEquals(q0.y, q1.y, eps) // q parallel zur x-Achse (q0.y und q1.y bis auf eps gleich)
                && q0.y >= Math.min(p1.y, p0.y) && q0.y <= Math.max(p1.y, p0.y) // q liegt innerhalb des Intervalls von p bzgl. y
                && Math.max(q0.x, q1.x) >= p0.x && Math.min(q0.x, q1.x) <= p0.x) // p liegt innerhalb des Intervalls von q bzgl. x
        {
            return new Point2d(p0.x, q0.y);
        }

        // 3. Fall: p liegt parallel zur x-Achse und q parallel zur y-Achse
        //          mit sich ueberschneidenden Intervallen
        //          Dann ist der Schnittpunkt: (q.x,p.y)
        if ((pEdgeXInf == 1 || (pEdgeXInf == 2 && Math.signum(p0.x) != Math.signum(p1.x))) // Mindestens eine x-Koordinate von p ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && (qEdgeYInf == 1 || (qEdgeYInf == 2 && Math.signum(q0.y) != Math.signum(q1.y))) // Mindestens eine y-Koordinate von q ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && doubleEpsEquals(p0.y, p1.y, eps) // p parallel zur x-Achse (p0.y und p1.y bis auf eps gleich)
                && doubleEpsEquals(q0.x, q1.x, eps) // q parallel zur y-Achse (q0.x und q1.x bis auf eps gleich)
                && q0.x >= Math.min(p1.x, p0.x) && q0.x <= Math.max(p1.x, p0.x) // q liegt innerhalb des Intervalls von p bzgl. x
                && Math.max(q0.y, q1.y) >= p0.y && Math.min(q0.y, q1.y) <= p0.y) // p liegt innerhalb des Intervalls von q bzgl. y
        {
            return new Point2d(q0.x, p0.y);
        }

        // 4. Fall: p liegt parallel zur x-Achse, q nicht parallel
        //          mit sich ueberschneidenden Intervallen
        //          Dann kann der Schnittpunkt ermittelt werden ueber Edge2d (mit angepassten Koordinaten)
        if ((pEdgeXInf == 1 || (pEdgeXInf == 2 && Math.signum(p0.x) != Math.signum(p1.x))) // Mindestens eine x-Koordinate von p ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && qEdgeYInf == 0 && qEdgeXInf == 0 // Keine y-Koordinate von q ist unendlich
                && doubleEpsEquals(p0.y, p1.y, eps) // p parallel zur x-Achse (p0.y und p1.y bis auf eps gleich)
                && Math.min(q0.x, q1.x) >= Math.min(p1.x, p0.x) && Math.max(q0.x, q1.x) <= Math.max(p1.x, p0.x) // q liegt innerhalb des Intervalls von p bzgl. x
                && Math.max(q0.y, q1.y) >= p0.y && Math.min(q0.y, q1.y) <= p0.y) // p liegt innerhalb des Intervalls von q bzgl. y
        {
            return Edge2d.getIntersectionPoint(new Point2d(Math.min(q0.x, q1.x) - eps, p0.y), new Point2d(Math.max(q0.x, q1.x) + eps, p1.y), q0, q1);
        }

        // 5. Fall: p liegt parallel zur y-Achse und q nicht unendlich
        //          mit sich ueberschneidenden Intervallen
        //          Dann kann der Schnittpunkt ermittelt werden ueber Edge2d (mit angepassten Koordinaten)
        if ((pEdgeYInf == 1 || (pEdgeYInf == 2 && Math.signum(p0.y) != Math.signum(p1.y))) // Mindestens eine y-Koordinate von p ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && qEdgeYInf == 0 && qEdgeXInf == 0 // Keine y-Koordinate von q ist unendlich
                && doubleEpsEquals(p0.x, p1.x, eps) // p parallel zur y-Achse (p0.x und p1.x bis auf eps gleich)
                && Math.min(q0.x, q1.x) <= p0.x && Math.max(q0.x, q1.x) >= p0.x // p liegt innerhalb des Intervalls von q bzgl. x
                && Math.max(q0.y, q1.y) <= Math.max(p0.y, p1.y) && Math.min(q0.y, q1.y) >= Math.min(p0.y, p1.y)) // q liegt innerhalb des Intervalls von p bzgl. y
        {
            return Edge2d.getIntersectionPoint(new Point2d(p0.x, Math.min(q0.y, q1.y) - eps), new Point2d(p1.x, Math.max(q0.y, q1.y) + eps), q0, q1);
        }

        // 6. Fall: q liegt parallel zur x-Achse und p nicht unendlich
        //          mit sich ueberschneidenden Intervallen
        //          Dann kann der Schnittpunkt ermittelt werden ueber Edge2d (mit angepassten Koordinaten)
        if ((qEdgeXInf == 1 || (qEdgeXInf == 2 && Math.signum(q0.x) != Math.signum(q1.x))) // Mindestens eine x-Koordinate von q ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && pEdgeYInf == 0 && pEdgeXInf == 0 // Keine y-Koordinate von p ist unendlich
                && doubleEpsEquals(q0.y, q1.y, eps) // q parallel zur x-Achse (q0.y und q1.y bis auf eps gleich)
                && Math.min(p0.x, p1.x) >= Math.min(q1.x, q0.x) && Math.max(p0.x, p1.x) <= Math.max(q1.x, q0.x) // p liegt innerhalb des Intervalls von q bzgl. x
                && Math.max(p0.y, p1.y) >= q0.y && Math.min(p0.y, p1.y) <= q0.y) // q liegt innerhalb des Intervalls von p bzgl. y
        {
            return Edge2d.getIntersectionPoint(new Point2d(Math.min(p0.x, p1.x) - eps, q0.y), new Point2d(Math.max(p0.x, p1.x) + eps, q1.y), p0, p1);
        }

        // 7. Fall: q liegt parallel zur y-Achse und p nicht unendlich
        //          mit sich ueberschneidenden Intervallen
        //          Dann kann der Schnittpunkt ermittelt werden ueber Edge2d (mit angepassten Koordinaten)
        if ((qEdgeYInf == 1 || (qEdgeYInf == 2 && Math.signum(q0.y) != Math.signum(q1.y))) // Mindestens eine y-Koordinate von q ist unendlich und wenn beide, dann mit unterschiedlichem Vorzeichen
                && pEdgeYInf == 0 && pEdgeXInf == 0 // Keine y-Koordinate von p ist unendlich
                && doubleEpsEquals(q0.x, q1.x, eps) // q parallel zur y-Achse (q0.x und q1.x bis auf eps gleich)
                && Math.min(p0.x, p1.x) <= q0.x && Math.max(p0.x, p1.x) >= q0.x // q liegt innerhalb des Intervalls von p bzgl. x
                && Math.max(p0.y, p1.y) <= Math.max(q0.y, q1.y) && Math.min(p0.y, p1.y) >= Math.min(q0.y, q1.y)) // p liegt innerhalb des Intervalls von q bzgl. y
        {
            return Edge2d.getIntersectionPoint(new Point2d(q0.x, Math.min(p0.y, p1.y) - eps), new Point2d(q1.x, Math.max(p0.y, p1.y) + eps), p0, p1);
        }

//        output.CTSO_Logger.println("this should never happen:\np0="+p0+"\np1="+p1+"\nq0="+q0+"\nq1="+q1);
        return null;

        /*
        
         */
    }

    public static boolean doubleEpsEquals(double a, double b, double eps) {
        return a - eps <= b && a + eps >= b;
    }

    public static boolean intersects(Point2d p0, Point2d p1, Point2d q0, Point2d q1) {
        return getIntersectionPoint(p0, p1, q0, q1) != null;

    }

    private static Point2d[] getCorrectedInfinityEdgePts(Point2d p0, Point2d p1) {
//        output.CTSO_Logger.println("Warnung: Diese Methode benutzt zur Berechnung der korrigierten Punkte festgelegte min/max-Groessen, die nicht skaliert werden...");
        // corrected Pts
        Point2d pc0 = p0;
        Point2d pc1 = p1;

        double negInf_correctionValue = -1.E12;
        double posInf_correctionValue = +1.E12;

        // Entweder ist mindestens eine der x-Koordinaten unendlich oder mindestens eine der y-Koordinaten
        boolean pEdgeXInf = (Double.isInfinite(p0.x) || Double.isInfinite(p1.x));
        boolean pEdgeYInf = (Double.isInfinite(p0.y) || Double.isInfinite(p1.y));
        // Falls x und y unendliche Komponenten haben, kann ich nichts tun
        if (pEdgeXInf && pEdgeYInf) {
            throw new IllegalArgumentException("can't determine intersection point (p0=" + p0 + ", p1=" + p1 + ")");
        }
        // Wenn entweder x oder y unendliche Komponenten hat, korrigieren
        if (pEdgeXInf) {
            // wenn beide x-Koordinaten unendlich:
            if (Double.isInfinite(p0.x) && Double.isInfinite(p1.x)) {
                // und zusaetzlich gleiches Vorzeichen, pech gehabt
                if (Math.signum(p0.x) == Math.signum(p1.x)) {
                    throw new IllegalArgumentException("can't determine intersection point (p0=" + p0 + ", p1=" + p1 + ")");
                }
                // und zusaetzlich y-Koordinaten ungleich, pech gehabt
                if (p0.y != p1.y) {
                    throw new IllegalArgumentException("can't determine intersection point (p0=" + p0 + ", p1=" + p1 + ")");
                }
                // Korrektur der unendlichen Koordinaten erfolgt ausserhalb
            }
            // undendliche Koordinaten Korrigieren
            if (Double.isInfinite(p0.x)) {
                pc0 = p0.clone();
                pc0.x = (Math.signum(p0.x) < 0) ? negInf_correctionValue : posInf_correctionValue;
                pc0.y = p1.y;
            }
            if (Double.isInfinite(p1.x)) {
                pc1 = p1.clone();
                pc1.x = (Math.signum(p1.x) < 0) ? negInf_correctionValue : posInf_correctionValue;
                pc1.y = p0.y;
            }
        }

        if (pEdgeYInf) {
            // wenn beide y-Koordinaten unendlich:
            if (Double.isInfinite(p0.y) && Double.isInfinite(p1.y)) {
                // und zusaetzlich gleiches Vorzeichen, pech gehabt
                if (Math.signum(p0.y) == Math.signum(p1.y)) {
                    throw new IllegalArgumentException("can't determine intersection point (p0=" + p0 + ", p1=" + p1 + ")");
                }
                // und zusaetzlich x-Koordinaten ungleich, pech gehabt
                if (p0.x != p1.x) {
                    throw new IllegalArgumentException("can't determine intersection point (p0=" + p0 + ", p1=" + p1 + ")");
                }
                // Korrektur der unendlichen Koordinaten erfolgt ausserhalb
            }
            // undendliche Koordinaten Korrigieren
            if (Double.isInfinite(p0.y)) {
                pc0 = p0.clone();
                pc0.y = (Math.signum(p0.y) < 0) ? negInf_correctionValue : posInf_correctionValue;
                pc0.x = p1.x;
            }
            if (Double.isInfinite(p1.y)) {
                pc1 = p1.clone();
                pc1.y = (Math.signum(p1.y) < 0) ? negInf_correctionValue : posInf_correctionValue;
                pc1.x = p0.x;
            }
        }

        return new Point2d[]{pc0, pc1};
    }

    public class BoundComparator implements Comparator<PolygonalCurve2d> {

        @Override
        public int compare(PolygonalCurve2d o1, PolygonalCurve2d o2) {
            // Keine zeitliche Ueberschneidung:
            if (o1.getPointAt(0).y > o2.getPointAt(o2.size() - 1).y) {
                return 2;   // o1 zeitlich nach o2, also groesser
            }
            if (o1.getPointAt(o1.size() - 1).y < o2.getPointAt(0).y) {
                return -2;  // o1 zeitlich vor o2, also kleiner
            }
            // Es gibt zeitliche Ueberschneidung

            // Geometrische Ueberschneidung testen:
            if (o1.intersects(o2)) {
                return 0;       // Es gibt eine Ueberschneidung der Kurven, gleichwertig
            }
            // gemeinsamen Zeitpunkt bestimmen:
            double tmin = Math.max(o1.getPointAt(0).y, o2.getPointAt(0).y);
            double tmax = Math.min(o1.getPointAt(o1.size() - 1).y, o2.getPointAt(o2.size() - 1).y);

            Point2d[] o1Pts = getPointsInRange(o1.getPoints(), tmin, tmax);
            Point2d[] o2Pts = getPointsInRange(o2.getPoints(), tmin, tmax);
            if (o1Pts[0].x > o2Pts[0].x) {
                return 1;   // o1 geometrisch groesser
            }
            return -1;      // o1 geometrisch kleiner

        }

    }

    /**
     * Ermittelt die Anzahl der Punkte, die diese Polygonalberandete Region
     * beschreiben.
     *
     * @return Anzahl der Punkte.
     */
    public int numberOfPoints() {
        return rightBound.size() + leftBound.size();
    }

    public double getSmin() {
        double smin = Double.POSITIVE_INFINITY;
        for (Point2d p : leftBound.getPoints()) {
            if (p.x < smin) {
                smin = p.x;
            }
        }
        return smin;
    }

    public double getSmax() {
        double smax = Double.NEGATIVE_INFINITY;
        for (Point2d p : rightBound.getPoints()) {
            if (p.x > smax) {
                smax = p.x;
            }
        }
        return smax;
    }

    /**
     * Gibt den maximalen Zeipunkt zurueck
     *
     * @return maximaler Zeipunkt (entspricht y-Koordinate des letzten punktes)
     * in Milli-Sekunden
     */
    public long getTmax() {
        return doubleToLong(leftBound.getPointAt(leftBound.size() - 1).y);
    }

    /**
     * Gibt den minimalen Zeitpunkt zurueck.
     *
     * @return minimaler Zeipunkt (entspricht y-Koordinate des ersten punktes)
     * in Milli-Sekunden
     */
    public long getTmin() {
        return doubleToLong(leftBound.getPointAt(0).y);
    }

    /**
     * Gibt den maximalen Zeipunkt zurueck
     *
     * @return maximaler Zeipunkt (entspricht y-Koordinate des letzten punktes)
     * in Sekunden
     */
    public double getTmaxInSeconds() {
        return leftBound.getPointAt(leftBound.size() - 1).y;
    }

    /**
     * Gibt den minimalen Zeipunkt zurueck
     *
     * @return minimaler Zeipunkt (entspricht y-Koordinate des ersten punktes)
     * in Sekunden
     */
    public double getTminInSeconds() {
        return leftBound.getPointAt(0).y;
    }

    /**
     * Gegeben ist das bezueglich der y-Koordinate aufsteigend sortierte Feld
     * von Punkten. Die y-Koordinate wird als Zeit t interpretiert. Dann muss
     * fuer die Sortierung des Feldes gelten:<br>
     *
     * <code>sortedPts[i].y < sortedPts[i+1]</code> fuer alle i.<br>
     *
     * Der Algorithmus gibt ein Punktfeld zurueck, das alle Punkte innerhalb des
     * Intervalls [tmin,tmax] beinhaltet. Fuer die Randpunkte werden die
     * entsprechenden Schnittpunkte ermittelt.
     *
     * @param sortedPts
     * @param tmin
     * @param tmax
     * @return
     */
    public static Point2d[] getPointsInRange(Point2d[] sortedPts, double tmin, double tmax) {

        // Pruefen, ob Punkte sortiert nach y
        checkSortedPts(sortedPts);

        // Range testen
        if (tmin >= tmax) {
            throw new IllegalArgumentException("tmin (" + tmin + ") must be smaller than tmax (" + tmax + ")");
        }
        if (tmin >= sortedPts[sortedPts.length - 1].y || // [tmin,tmax] gesamt groeszer als Range
                tmax <= sortedPts[0].y) // [tmin,tmax] gesamt kleiner als Range
        {
            return null;            // Bounds in angefragtem [tmin,tmax] nicht definiert
        }
        ArrayList<Point2d> pts = new ArrayList<Point2d>();

        for (int j = 0; j < sortedPts.length - 1; j++) {

            // Kein Handlungsbedarf, wenn beide Punkte pi und pi+1 unterhalb tmin liegen
            if (sortedPts[j + 1].y <= tmin) {
                continue;
            }

            // Hier angekommen bedeutet, dass mindestens ein (Schnitt-)Punkt interessant ist
            // Fall 1: pi <= tmin und pi+1 > tmin => Schnittpunkt berechnen
            if (sortedPts[j].y < tmin && sortedPts[j + 1].y > tmin) {
                // Schnittpunkt berechnen:
                Point2d cutPoint = getIntersectionPoint(
                        sortedPts[j], sortedPts[j + 1], // Kante der Bound
                        new Point2d(Double.NEGATIVE_INFINITY, tmin), new Point2d(Double.POSITIVE_INFINITY, tmin) // Edge auf Hoehe von tmin
                );
                // cutPoint hinzufuegen
                pts.add(cutPoint);
            }
            // Fall 2: pi liegt innerhalb des Intervalls [tmin,tmax)
            // pi einfuegen
            if (sortedPts[j].y >= tmin && sortedPts[j].y < tmax) {
                pts.add(sortedPts[j].clone());
            }
            // Fall 3: pi <= tmax und pi+1 > tmax => Schnittpunkt berechnen
            if (sortedPts[j].y <= tmax && sortedPts[j + 1].y > tmax) {
                // Schnittpunkt berechnen:
                Point2d cutPoint = getIntersectionPoint(
                        sortedPts[j], sortedPts[j + 1], // Kante der Bound
                        new Point2d(Double.NEGATIVE_INFINITY, tmax), new Point2d(Double.POSITIVE_INFINITY, tmax) // Edge auf Hoehe von tmax
                );
                // cutPoint hinzufuegen
//                output.CTSO_Logger.println("cutPoint for tmax = "+cutPoint);
                pts.add(cutPoint);
            }

            // ggf. letzten Punkt einfuegen, wenn der letzte Punkt noch in tmin,tmax liegt:
            if (j == sortedPts.length - 2) {
                if (sortedPts[j + 1].y <= tmax) {
                    pts.add(sortedPts[j + 1].clone());
                }
            }

            // Abbruchbedingung
            if (sortedPts[j + 1].y > tmax) {
                break;
            }
        }
        Point2d[] res = new Point2d[pts.size()];
        pts.toArray(res);
        // ggf. Rundungsfehler beseitigen
        if (tmin - eps <= res[0].y && res[0].y <= tmin + eps) {
            res[0].y = tmin;
        }
        if (tmax - eps <= res[res.length - 1].y && res[res.length - 1].y <= tmax + eps) {
            res[res.length - 1].y = tmax;
        }
        return res;
    }

    /**
     * Liefert eine Represenatation dieses Objektes als einfaches geschlossenes
     * Polygon. Die x-Koordinaten entsprechen der Lage entlang des
     * Laufparameters s waehrend die y-Koordinate den zeitlichen Verlauf in
     * Sekunden beschreibt.
     *
     * @param tmin Beginn des angefragten Intervalls in Milli-Sekunden
     * @param tmax Ende des angefragten Intervalls in Milli-Sekunden
     * @return
     */
    public SimplePolygon2d getPolygonalRepresentation(long tmin, long tmax) {
        return getPolygonalRepresentation(longToDouble(tmin), longToDouble(tmax));
    }

    /**
     * Liefert eine Represenatation dieses Objektes als einfaches geschlossenes
     * Polygon. Die x-Koordinaten entsprechen der Lage entlang des
     * Laufparameters s waehrend die y-Koordinate den zeitlichen Verlauf in
     * Sekunden beschreibt.
     *
     * @param tmin Beginn des angefragten Intervalls in Sekunden
     * @param tmax Ende des angefragten Intervalls in Sekunden
     * @return
     */
    private SimplePolygon2d getPolygonalRepresentation(double tmin, double tmax) {
        Point2d[] leftPts = getPointsInRange(this.getLeftBound().getPoints(), tmin, tmax);
        Point2d[] rightPts = getPointsInRange(this.getRightBound().getPoints(), tmin, tmax);

        Point2d[] pts = new Point2d[leftPts.length + rightPts.length];

        int index = 0;
        for (int j = leftPts.length - 1; j >= 0; j--) {
            pts[index++] = leftPts[j];
        }
        System.arraycopy(rightPts, 0, pts, index, rightPts.length);
        return new SimplePolygon2d(pts);
    }

    public static boolean checkSortedPts(Point2d[] sortedPts) {
        // Pruefen, ob Punkte sortiert nach y
        for (int j = 0; j < sortedPts.length - 1; j++) {
            if (sortedPts[j + 1].y <= sortedPts[j].y) {
                throw new IllegalArgumentException("sortedPts must be in ascending order with respect to y-coordinate (" + sortedPts[j + 1] + " <= " + sortedPts[j] + " at pos " + j + " of " + sortedPts.length + "): " + java.util.Arrays.toString(sortedPts));
            }
        }
        return true;
    }

    public double[] getValues(double t) {
        if (t > getTmaxInSeconds() || t < getTminInSeconds()) {
//            output.CTSO_Logger.println("t: "+t+", tmin="+getTminInSeconds()+" tmax="+getTmaxInSeconds());
            return new double[]{Double.NaN, Double.NaN};

        }
        // Schnittpunkte mit uendlicher t-Geraden berechnen
        Point2d tL = new Point2d(Double.NEGATIVE_INFINITY, t);
        Point2d tR = new Point2d(Double.POSITIVE_INFINITY, t);

        double[] values = new double[2];

        PolygonalCurve2d thisLeft = this.getLeftBound();
        PolygonalCurve2d thisRight = this.getRightBound();

        // TODO: einmalige Kopie von Left- und Right Bound anlegen
        // Linke Seite
        for (int i = 0; i < thisLeft.size() - 1; i++) {
            if (thisLeft.getPointAt(i).y <= t && thisLeft.getPointAt(i + 1).y > t) {
                values[0] = getIntersectionPoint(
                        tL, tR,
                        thisLeft.getPointAt(i), thisLeft.getPointAt(i + 1)
                ).x;
                break;
            }
        }
        // Rechte Seite
        for (int i = 0; i < thisRight.size() - 1; i++) {
            if (thisRight.getPointAt(i).y <= t && thisRight.getPointAt(i + 1).y > t) {
                values[1] = getIntersectionPoint(
                        tL, tR,
                        thisRight.getPointAt(i), thisRight.getPointAt(i + 1)
                ).x;
                break;
            }
        }
        return values;

    }

    public double[] getValues(long t) {
        return getValues(longToDouble(t));
    }

    private static double getValue(Point2d[] orderedPts, double t) {
        if (t > orderedPts[orderedPts.length - 1].y + eps || t < orderedPts[0].y - eps) {
            return Double.NaN;
        }

        // Extremfaelle abfangen:
        // Wenn t = -Inf: orderedPts[0].x zurueckgeben, falls orderedPts[0].y = -Inf
        // und das gleiche mit +Inf am Ende
        if (Double.isInfinite(t)) {
            if (t == Double.NEGATIVE_INFINITY) {
                return (orderedPts[0].y == Double.NEGATIVE_INFINITY) ? orderedPts[0].x : Double.NaN;
            } else if (t == Double.POSITIVE_INFINITY) {
                return (orderedPts[orderedPts.length - 1].y == Double.POSITIVE_INFINITY) ? orderedPts[orderedPts.length - 1].x : Double.NaN;
            }
        }

        // Schnittpunkte mit uendlicher t-Geraden berechnen
        Point2d tL = new Point2d(Double.NEGATIVE_INFINITY, t);
        Point2d tR = new Point2d(Double.POSITIVE_INFINITY, t);

        for (int i = 0; i < orderedPts.length - 1; i++) {
            if (orderedPts[i].y <= t && orderedPts[i + 1].y >= t) {
                return getIntersectionPoint(tL, tR, orderedPts[i], orderedPts[i + 1]).x;
            }
        }
        return Double.NaN;
    }

    /**
     * Gegeben sind die zwei Kanten (p0,p1) und (q0,q1). Die Methode ermittelt
     * die Distanz bzgl. der y-Kante (hier zeitlich), die Kante (p0,p1) nach
     * oben (positiv) bzw. unten (negativ) verschoben werden muss, so dass sich
     * die beiden Kanten nicht mehr schneiden. Gibt es keinen Schnittpunkt
     * werden die Distanzen 0 und 0 zurueckgegeben.
     *
     * @param p0 1. Punkt der Kante (p0,p1)
     * @param p1 2. Punkt der Kante (p0,p1)
     * @param q0 1. Punkt der Kante (q0,q1)
     * @param q1 2. Punkt der Kante (q0,q1)
     * @return Ein Feld mit 2 Werten: [0]: Distanz, die (p0,p1) positiv
     * verschoben werden muss [1]: Distanz, die (p0,p1) negativ verschoben
     * werden muss
     *
     */
    public static double[] howMuchToMove(Point2d p0, Point2d p1, Point2d q0, Point2d q1) {
        double[] hmm = new double[2];
//        output.CTSO_Logger.println("howMuchToMove: ("+p0+","+p1+"), ("+q0+","+q1+")");
        Point2d secPt = getIntersectionPoint(p0, p1, q0, q1);
//        output.CTSO_Logger.println("   IntersectionPt: "+secPt);
        // es gibt eine Ueberschneidung
        if (secPt != null) {
            // Berechne Schnittpunkte zwischen zeitlich unendlichen geraden von
            // p0.x und q0q1 und p1.x und q0q1
            Point2d p0SecPt = getIntersectionPoint(
                    new Point2d(p0.x, Double.NEGATIVE_INFINITY),
                    new Point2d(p0.x, Double.POSITIVE_INFINITY),
                    q0, q1
            );
            Point2d p1SecPt = getIntersectionPoint(
                    new Point2d(p1.x, Double.NEGATIVE_INFINITY),
                    new Point2d(p1.x, Double.POSITIVE_INFINITY),
                    q0, q1
            );
            // und q0.x und p0p1 und q1.x und p0p1
            Point2d q0SecPt = getIntersectionPoint(
                    new Point2d(q0.x, Double.NEGATIVE_INFINITY),
                    new Point2d(q0.x, Double.POSITIVE_INFINITY),
                    p0, p1
            );
            Point2d q1SecPt = getIntersectionPoint(
                    new Point2d(q1.x, Double.NEGATIVE_INFINITY),
                    new Point2d(q1.x, Double.POSITIVE_INFINITY),
                    p0, p1
            );

            double p0dt = 0.;
            double p1dt = 0.;
            double q0dt = 0.;
            double q1dt = 0.;
            if (p0SecPt != null) {
                p0dt = p0SecPt.y - p0.y;
            }
            if (p1SecPt != null) {
                p1dt = p1SecPt.y - p1.y;
            }
            if (q0SecPt != null) {
                q0dt = q0.y - q0SecPt.y;
            }
            if (q1SecPt != null) {
                q1dt = q1.y - q1SecPt.y;
            }
            hmm[0] = Math.max(Math.max(p0dt, p1dt), Math.max(q0dt, q1dt));
            hmm[1] = Math.min(Math.min(p0dt, p1dt), Math.min(q0dt, q1dt));

            // Epsilontik, da Schnittpunkte nicht zuverlaessig bestimmt werden:
            // Die move-Differenz resultiert manchmal in 0, obwohl Schnittpunkt
            // und Kantenpunkt nicht genau uebereinstimmen (parameterofCutpoint ist dann
            // 0.99999.... statt 1)
            hmm[0] = Math.max(hmm[0], eps);
            hmm[1] = Math.min(hmm[1], -eps);
        }
//        output.CTSO_Logger.println("   dtpos: "+hmm[0]);
//        output.CTSO_Logger.println("   dtneg: "+hmm[1]);
        return hmm;
    }

    /**
     * Ermittelt die zeitliche Verschiebung (in y-Richtung), die reg verschoben
     * werden muss (positiv wie negativ), so dass sich this und reg nicht mehr
     * schneiden. Gibt [0.,0.] zurueck, wenn sich this und reg nicht schneiden.
     *
     * @param reg
     * @return die zeitliche positive [0] und negative [1] Verschiebung.
     */
    protected double[] howMuchToMove(DynamicPolygonalRegion reg) {
        double[] hmm = new double[2];
        double regSmin = reg.getSmin();
        double regSmax = reg.getSmax();

        // Alle Kanten mit allen Kanten von reg schneiden und maximale Distanz ermitteln:
//        Iterator<Point2d> thisIt = this.pointsForEdgeIterator();
//        Iterator<Point2d> regIt  = null;
//
//
//        Point2d q0 = thisIt.next();
//        while (thisIt.hasNext()) {
//
//            Point2d q1 = thisIt.next();
//
//            double smin = Math.min(q0.x,q1.x);
//            double smax = Math.max(q0.x,q1.x);
//            if (smin > regSmax || smax < regSmin)
//                continue;
////            double tmin = Math.min(q0.y,q1.y);
////            double tmax = Math.max(q0.y,q1.y);
////            if (tmin > regTmax || tmax < regTmin)
////                continue;
//
//            regIt =  reg.pointsForEdgeIterator();
//            Point2d p0 = regIt.next();
//            while (regIt.hasNext()) {
//                Point2d p1 = regIt.next();
//
//                double[] tmp = howMuchToMove(p0, p1, q0, q1);
//                hmm[0] = Math.max(hmm[0], tmp[0]);
//                hmm[1] = Math.min(hmm[1], tmp[1]);
//                p0 = p1;
//            }
//            q0 = q1;
//        }
//        return hmm;
//    }
        SimplePolygon2d thisPoly = this.getPolygonalRepresentation(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        SimplePolygon2d regPoly = reg.getPolygonalRepresentation(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (int i = 0; i < thisPoly.size(); i++) {

            Point2d q0 = thisPoly.getPointAt(i);
            Point2d q1 = thisPoly.getPointAt((i + 1) % thisPoly.size());
            double smin = Math.min(q0.x, q1.x);
            double smax = Math.max(q0.x, q1.x);
            if (smin > regSmax || smax < regSmin) {
                continue;
            }
//            double tmin = Math.min(q0.y,q1.y);
//            double tmax = Math.max(q0.y,q1.y);
//            if (tmin > regTmax || tmax < regTmin)
//                continue;

            for (int j = 0; j < regPoly.size(); j++) {

                Point2d p0 = regPoly.getPointAt(j);
                Point2d p1 = regPoly.getPointAt((j + 1) % regPoly.size());

                double[] tmp = howMuchToMove(p0, p1, q0, q1);
                hmm[0] = Math.max(hmm[0], tmp[0]);
                hmm[1] = Math.min(hmm[1], tmp[1]);
            }
        }
        return hmm;
    }

    protected double[] getMinDt(DynamicPolygonalRegion reg) {
        double[] mindt = new double[]{
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
        };

        SimplePolygon2d regPoly = reg.getPolygonalRepresentation(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        for (int i = 0; i < regPoly.size(); i++) {
            Point2d p0 = regPoly.getPointAt(i);
            Point2d p1 = regPoly.getPointAt((i + 1) % regPoly.size());
            double[] tmp = getMinDt(p0, p1);
            mindt[0] = Math.min(mindt[0], tmp[0]);
            mindt[1] = Math.max(mindt[1], tmp[1]);
        }
        return mindt;

    }

    private double[] getMinDt(Point2d p0, Point2d p1) {
        double mindt[] = new double[]{
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
        };
        SimplePolygon2d thisPoly = getPolygonalRepresentation(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        double smin = Math.min(p0.x, p1.x);
        double smax = Math.max(p0.x, p1.x);

        Point2d negInfBuffer = new Point2d(0., Double.NEGATIVE_INFINITY);
        Point2d posInfBuffer = new Point2d(0., Double.POSITIVE_INFINITY);
        Point2d isPtBuffer = new Point2d();
        double dtBuffer = 0.;

        for (int i = 0; i < thisPoly.size(); i++) {
            Point2d q0 = thisPoly.getPointAt(i);
            Point2d q1 = thisPoly.getPointAt((i + 1) % thisPoly.size());

            // Keine Ueberschneidung hinsichtlich s
            if (Math.min(q0.x, q1.x) > smax || Math.max(q0.x, q1.x) < smin) {
                continue;
            }

            // Fuer p0
            negInfBuffer.x = p0.x;
            posInfBuffer.x = p0.x;
            isPtBuffer = getIntersectionPoint(negInfBuffer, posInfBuffer, q0, q1);
            if (isPtBuffer != null) {
                dtBuffer = isPtBuffer.y - p0.y;
                if (dtBuffer > 0) {
                    mindt[0] = Math.min(mindt[0], dtBuffer);
                } else if (dtBuffer < 0) {
                    mindt[1] = Math.max(mindt[1], dtBuffer);
                }
            }

            // Fuer p1
            negInfBuffer.x = p1.x;
            posInfBuffer.x = p1.x;
            isPtBuffer = getIntersectionPoint(negInfBuffer, posInfBuffer, q0, q1);
            if (isPtBuffer != null) {
                dtBuffer = isPtBuffer.y - p1.y;
                if (dtBuffer > 0) {
                    mindt[0] = Math.min(mindt[0], dtBuffer);
                } else if (dtBuffer < 0) {
                    mindt[1] = Math.max(mindt[1], dtBuffer);
                }
            }

            // Fuer q0
            negInfBuffer.x = q0.x;
            posInfBuffer.x = q0.x;
            isPtBuffer = getIntersectionPoint(negInfBuffer, posInfBuffer, p0, p1);
            if (isPtBuffer != null) {
                dtBuffer = q0.y - isPtBuffer.y;
                if (dtBuffer > 0) {
                    mindt[0] = Math.min(mindt[0], dtBuffer);
                } else if (dtBuffer < 0) {
                    mindt[1] = Math.max(mindt[1], dtBuffer);
                }
            }

            // Fuer q1
            negInfBuffer.x = q1.x;
            posInfBuffer.x = q1.x;
            isPtBuffer = getIntersectionPoint(negInfBuffer, posInfBuffer, p0, p1);
            if (isPtBuffer != null) {
                dtBuffer = q1.y - isPtBuffer.y;
                if (dtBuffer > 0) {
                    mindt[0] = Math.min(mindt[0], dtBuffer);
                } else if (dtBuffer < 0) {
                    mindt[1] = Math.max(mindt[1], dtBuffer);
                }
            }

        }
        return mindt;
    }

    /**
     * Quick and dirty implementierung zum Bestimmen der schneidenden Kanten die
     * sich durch pi, pi+1 des Punktfeldes ergeben (geschnitten mit allen Kanten
     * von this)
     *
     * @param other
     * @return Feld von pts.length-1 booleans. Der Boolean gibt fuer den Index
     * der Kante an, ob diese durch this geschnitten wird.
     */
    public boolean[] getIntersectingEdgesOfPts(Point2d[] pts) {
        boolean[] isIntersected = new boolean[pts.length - 1];

        PolygonalCurve2d thisLeft = this.getLeftBound();
        PolygonalCurve2d thisRight = this.getRightBound();

        for (int j = 0; j < pts.length - 1; j++) {
            // linke Seite abklappern:
            for (int i = 0; i < thisLeft.size() - 1; i++) {
                if (intersects(
                        thisLeft.getPointAt(i), thisLeft.getPointAt(i + 1),
                        pts[j], pts[j + 1])) {
                    isIntersected[j] = true;
                    break;
                }
            }
            // rechte Seite abklappern:
            for (int i = 0; i < thisRight.size() - 1; i++) {
                if (intersects(
                        thisRight.getPointAt(i), thisRight.getPointAt(i + 1),
                        pts[j], pts[j + 1])) {
                    isIntersected[j] = true;
                    break;
                }
            }
            // untere Edge
            if (intersects(
                    thisLeft.getPointAt(0), thisRight.getPointAt(0),
                    pts[j], pts[j + 1])) {
                isIntersected[j] = true;
                break;
            }
            // obere Edge
            if (intersects(
                    thisLeft.getPointAt(thisLeft.size() - 1), thisRight.getPointAt(thisRight.size() - 1),
                    pts[j], pts[j + 1])) {
                isIntersected[j] = true;
                break;
            }
        }
        return isIntersected;
    }

    public Iterator<Point2d> pointsForEdgeIterator() {
        return new PointsForEdgeIterator();
    }

    public class PointsForEdgeIterator implements Iterator<Point2d> {

        PolygonalCurve2d thisLeft = DynamicPolygonalRegion.this.getLeftBound();
        PolygonalCurve2d thisRight = DynamicPolygonalRegion.this.getRightBound();

        int currentIndex = thisLeft.size() - 1;
        PolygonalCurve2d currentBound = thisLeft;
        boolean oneLeft = false;
        boolean goOn = true;

        @Override
        public boolean hasNext() {
            return goOn;
        }

        @Override
        public Point2d next() {
            Point2d p = null;
            if (oneLeft) {
                p = thisLeft.getPointAt(thisLeft.size() - 1);
                goOn = false;
            } else if (currentBound == thisLeft) {
                p = thisLeft.getPointAt(currentIndex);
                if (currentIndex > 0) {
                    currentIndex--;
                } else {
                    currentIndex = 0;
                    currentBound = thisRight;
                }
            } else if (currentBound == thisRight) {
                p = thisRight.getPointAt(currentIndex);
                currentIndex++;
                if (currentIndex >= thisRight.size()) {
                    oneLeft = true;
                }
            }
            return p;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported!");
        }

    }

    public static Point2d getIntersectionPointEps(Point2d p0i, Point2d p1i, Point2d p0j, Point2d p1j, double eps) {
        double[] param = Edge2d.getDirectionParamOfIntersectionPoint(p0i, p1i, p0j, p1j);
        double s = param[0];
        double t = param[1];

        if ((s == 0. && t == 1.) && !p0i.equals(p1j)) {
            return null;
        }
        if ((s == 1. && t == 0.) && !p1i.equals(p0j)) {
            return null;
        }
        if (!(s >= 0. - eps && s <= 1. + eps && t >= 0. - eps && t <= 1. + eps)) {
//        if (!(s >= 0. && s <= 1. && t >= 0 && t <= 1.)) {
            return null;
        }
        return Edge2d.getPointFromNorm(p0i, p1i, s);
    }

    @Override
    public DynamicPolygonalRegion clone() {
        PolygonalCurve2d lB = leftBound.clone();
        PolygonalCurve2d rB = rightBound.clone();
        return new DynamicPolygonalRegion(lB, rB);
    }

    @Override
    public String toString() {
        return "DynamicPolygonalRegion{" + "leftBound=" + leftBound + ", rightBound=" + rightBound + '}';
    }

}
