/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geometry;

import javax.vecmath.Point3d;
import java.util.ArrayList;
 

/**
 *
 * @author bertholdAmMac
 */
public class ParametricLinearCurve3d {

    // Linearinterpolierte Kurve zwischen Stuetztstellen
    public Point3d[] ptsInSpace;
    // zugeordnete Zeitpunkt (indexgleich)
    public long[] ptsInTime;
    Point3d tmp = new Point3d();
    public double epsForEqualT;

    public ParametricLinearCurve3d(Point3d[] ptsInSpace, long[] ptsInTime) {
        this(ptsInSpace, ptsInTime, 0.);
    }

    public ParametricLinearCurve3d(Point3d[] ptsInSpace, long[] ptsInTime, double epsForEqualT) {
        if (ptsInSpace.length != ptsInTime.length) {
            throw new IllegalArgumentException("dimension must fit");
        }
        if (ptsInSpace.length < 1) {
            throw new IllegalArgumentException("a parametric curve needs at least 1 pts! (here: " + java.util.Arrays.toString(ptsInTime) + "," + java.util.Arrays.toString(ptsInSpace) + ")");
        }

        this.epsForEqualT = epsForEqualT;

        // temporaere Listen fuer ggf. zusammengefasste Punkte
        ArrayList<Point3d> p = new ArrayList<Point3d>(ptsInSpace.length);
        ArrayList<Long> t = new ArrayList<Long>(ptsInTime.length);
        // Werte zeitlich aufsteigend sortiert?
        for (int i = 0; i < ptsInTime.length - 1; i++) {

            // normal
            if (ptsInTime[i] < ptsInTime[i + 1]) {
                p.add(ptsInSpace[i]);
                t.add(ptsInTime[i]);
            } // ungueltig
            else if (ptsInTime[i] > ptsInTime[i + 1]) {
                throw new IllegalArgumentException("Zeitpunkte bei ParametricLinearCurve3d muessen zeitlich aufsteigend sortiert sein: " + java.util.Arrays.toString(ptsInTime));
            } else if (ptsInTime[i] == ptsInTime[i + 1]) {

                Point3d tmpPt = (Point3d) ptsInSpace[i].clone();

                int j = i + 1;
                for (; j < ptsInTime.length; j++) {
                    if (ptsInTime[i] == ptsInTime[j]) {
                        if (!ptsInSpace[i].epsilonEquals(ptsInSpace[j], epsForEqualT)) {
                            throw new IllegalArgumentException("Zeitpunkte bei ParametricLinearCurve3d muessen zeitlich aufsteigend sortiert sein: " + java.util.Arrays.toString(ptsInTime));
                        } else {
                            tmpPt.add(ptsInSpace[j]);
                        }
                    } else {
                        break;
                    }
                }
                tmpPt.scale(1. / (j - i));

                p.add(tmpPt);
                t.add(ptsInTime[i]);
                i = j - 1;
            }
        }

        // ggf. letzten Punkt einfuegen
        if (t.get(t.size() - 1) < ptsInTime[ptsInTime.length - 1]) {
            p.add(ptsInSpace[ptsInSpace.length - 1]);
            t.add(ptsInTime[ptsInTime.length - 1]);
        }

        if (p.size() < 1) {
            throw new IllegalArgumentException("a parametric curve needs at least 1 pts! (here: " + t + "," + p + ") and was: (" + java.util.Arrays.toString(ptsInTime) + "," + java.util.Arrays.toString(ptsInSpace) + ")");
        }

        this.ptsInSpace = p.toArray(new Point3d[0]);
        this.ptsInTime = new long[t.size()];
        for (int i = 0; i < t.size(); i++) {
            this.ptsInTime[i] = t.get(i);
        }
    }

    public Point3d getPoint(double s) {
        return getPoint(DynamicPolygonalRegion.doubleToLong(s));
    }

    /**
     * Liefert die Zeitspanne, ueber der die Bewegung definiert ist:
     * <code>getTimeAt(numberOfPts()-1) - getTimeAt(0)</code>
     *
     * @return Die Dauer der Bewegung in Millisekunden
     */
    public long getDuration() {
        if (numberOfPts() == 1) {
            return 0L;
        }
        return getTimeAt(numberOfPts() - 1) - getTimeAt(0);
    }

    /**
     * Gibt die Gesamtlaenge bzgl. der x-Koordinaten der Kurve zurueck
     *
     * @return
     */
    public double getXLength() {
        double dis = 0.;
        for (int i = 1; i < numberOfPts(); i++) {
            dis += Math.abs(ptsInSpace[i - 1].x - ptsInSpace[i].x);
        }
        return dis;
    }

    /**
     * Gibt die Gesamtlaenge bzgl. der y-Koordinaten der Kurve zurueck
     *
     * @return
     */
    public double getYLength() {
        double dis = 0.;
        for (int i = 1; i < numberOfPts(); i++) {
            dis += Math.abs(ptsInSpace[i - 1].y - ptsInSpace[i].y);
        }
        return dis;
    }

    /**
     * Gibt die Gesamtlaenge bzgl. der z-Koordinaten der Kurve zurueck
     *
     * @return
     */
    public double getZLength() {
        double dis = 0.;
        for (int i = 1; i < numberOfPts(); i++) {
            dis += Math.abs(ptsInSpace[i - 1].z - ptsInSpace[i].z);
        }
        return dis;
    }

    /**
     * Gibt die Gesamtlaenge der Kurve zurueck
     *
     * @return
     */
    public double getLength() {
        double dis = 0.;
        for (int i = 1; i < numberOfPts(); i++) {
            dis += ptsInSpace[i - 1].distance(ptsInSpace[i]);
        }
        return dis;
    }

    public Point3d getPoint(long t) {
        if (t < ptsInTime[0] || t > ptsInTime[ptsInTime.length - 1]) {
            throw new IllegalArgumentException("Punkt fuer Parameter " + t + " nicht definiert.");
        }
        // entsprechendes Segment suchen:
        int i = 0;
        while (t > ptsInTime[i + 1]) {
            i++;
        }
        Point3d res = new Point3d();

        // Lineare Interpolation zwischen p_i und p_i+1 mit lambda t aus t_i, t_i+1
        tmp.sub(ptsInSpace[i + 1], ptsInSpace[i]);
        double scale = (double) (t - ptsInTime[i]) / ((double) (ptsInTime[i + 1] - ptsInTime[i]));
        res.scaleAdd(scale, tmp, ptsInSpace[i]);
        return res;
    }

    public Point3d getPointAt(int i) {
        return (Point3d) ptsInSpace[i].clone();
    }

    public void setPointAt(int i, Point3d p) {
        if (i < 0 || i > ptsInTime.length - 1) {
            throw new IllegalArgumentException("index i=" + i + " out of range");
        }
        ptsInSpace[i] = p;
    }

    public long getTimeAt(int i) {
        return ptsInTime[i];
    }

    public int numberOfPts() {
        return ptsInSpace.length;
    }

    /**
     * In dieser Methode, wird die ParametricLinearCurve3d curve zu dieser
     * hinzuaddiert... Hierzu wird werden als erstes die Intervalle in denen die
     * PLCs liegen ermittelt.
     *
     * Danach werden die PLCs in eine first-PLC und eine last-PLC umbenannt. Je
     * nachdem welche den frueheren Startzeitpunkt hat.
     *
     * Dann wird gezählt, wie viele Stuetzstellen durch die addition der beiden
     * PLCs entstehen. ((eigene Stuetzstellen von this) + (eigene Stuetzstellen
     * von curve) + (gemeinsame Stuetzstellen von this und curve)
     *
     * Danach wird die addition jedes Punktes durchgeführt. Wenn die
     * Stuetzstellen in beiden PLC vorhanden sind, werden die Werte einfach
     * addiert.
     *
     * Wenn eine Stuetzstelle nicht in der der anderen PLC vorhanden ist, wird
     * im Intervall eine lineare Interpolation der Werte verwendet. Wenn die
     * Stuetzstelle hinter, oder vor dem Intervall liegt, wird die jeweils
     * erste, oder letzte Stuetzstelle der anderen PLC zur addition verwendet.
     *
     * @param curve ParametricLinearCurve3d die zu dieser
     * ParametricLinearCurve3d addiert wird.
     * @return addition der beiden ParametricLinearCurve3d s
     *
     */
    public ParametricLinearCurve3d add(ParametricLinearCurve3d curve) {

//        output.CTSO_Logger.println("ParametricLinearCurve3d:add");
//        output.CTSO_Logger.println("this:\n"+this);
//        output.CTSO_Logger.println("curve:\n"+curve);
        Point3d[] pointsInSpace = null;
        long[] pointsInTime = null;
        int anzahl_This = 0, anzahlCurve = 0, anzahlGemeinsam = 0, anzahlGleich = 0;
        long maxIntervall_this = ptsInTime[ptsInTime.length - 1],
                minIntervall_this = ptsInTime[0],
                maxIntervall_cu = curve.ptsInTime[curve.ptsInTime.length - 1],
                minIntervall_cu = curve.ptsInTime[0];

        /*
         * Wie viele Stuetzpunkte(long) von der first PLC3d, liegen ausserhalb(vor) der
         * last PLC3d und innerhalb.
         */
        for (int i = 0; i < ptsInTime.length; i++) {
            long t1 = ptsInTime[i];
            if (t1 < minIntervall_cu || t1 > maxIntervall_cu) {
                anzahlCurve++;
            }
            if (t1 >= minIntervall_cu && t1 <= maxIntervall_cu) {
                anzahlGemeinsam++;
            }
        }
        /*
         * Wie viele Stuetzpunkte(long) von der last PLC3d, liegen ausserhalb(hinter) der
         * first PLC3d und innerhalb.
         */
        for (int i = 0; i < curve.ptsInTime.length; i++) {
            long t1 = curve.ptsInTime[i];
            if (t1 < minIntervall_this || t1 > maxIntervall_this) {
                anzahl_This++;
            }
            if (t1 >= minIntervall_this && t1 <= maxIntervall_this) {
                anzahlGemeinsam++;
            }
        }
        /*
         * Wie viele gleiche Stuetzpunkte gibt es in der Ueberschneidung, der PLC3ds
         */
        for (int i = 0; i < ptsInSpace.length; i++) {
            long point3d = ptsInTime[i];
            for (int j = 0; j < curve.ptsInTime.length; j++) {
                long point3d1 = curve.ptsInTime[j];
                if (point3d == point3d1) {
                    anzahlGleich++;
                }
            }
        }
        /*
         * Hier werden die Stuetzwerte, die in beiden PLCs gleich sind, von der
         * anzahlGemeinsam abgezogen, da sie vorher doppelt gezählt wurden.
         *
         */
        anzahlGemeinsam -= anzahlGleich;
        pointsInTime = new long[anzahlGemeinsam + anzahl_This + anzahlCurve];
        pointsInSpace = new Point3d[anzahlGemeinsam + anzahl_This + anzahlCurve];

        int a = 0, mm = 0, nn = 0, kk = 0;
        for (kk = 0; kk < pointsInTime.length; kk++) {

            if (mm == ptsInSpace.length) {
                a = 0;
            }
            if (nn == curve.ptsInSpace.length) {
                a = 1;
            }

            if (mm < ptsInTime.length && nn < curve.ptsInTime.length) {

                if (ptsInTime[mm] < curve.ptsInTime[nn]) {
                    pointsInSpace[kk] = new Point3d();

                    if (ptsInTime[mm] >= minIntervall_cu && ptsInTime[mm] <= maxIntervall_cu) {
                        pointsInSpace[kk].add(ptsInSpace[mm], curve.getPoint(ptsInTime[mm]));
                    } else if (ptsInTime[mm] < minIntervall_cu) {
                        pointsInSpace[kk].add(ptsInSpace[mm], curve.getPointAt(0));
                    } else if (ptsInTime[mm] > maxIntervall_cu) {
                        pointsInSpace[kk].add(ptsInSpace[mm], curve.getPointAt(curve.ptsInSpace.length - 1));
                    }
                    pointsInTime[kk] = ptsInTime[mm];
                    mm++;
                } else if (ptsInTime[mm] > curve.ptsInTime[nn]) {
                    pointsInSpace[kk] = new Point3d();

                    if (curve.ptsInTime[nn] >= minIntervall_this && curve.ptsInTime[nn] <= maxIntervall_this) {
                        pointsInSpace[kk].add(curve.ptsInSpace[nn], getPoint(curve.ptsInTime[nn]));
                    } else if (curve.ptsInTime[nn] < minIntervall_this) {
                        pointsInSpace[kk].add(curve.ptsInSpace[nn], getPointAt(0));
                    } else if (curve.ptsInTime[nn] > maxIntervall_this) {
                        pointsInSpace[kk].add(curve.ptsInSpace[nn], getPointAt(ptsInSpace.length - 1));
                    }
                    pointsInTime[kk] = curve.ptsInTime[nn];
                    nn++;
                } else if (ptsInTime[mm] == curve.ptsInTime[nn]) {
                    pointsInSpace[kk] = new Point3d();
                    pointsInSpace[kk].add(ptsInSpace[mm], curve.ptsInSpace[nn]);
                    pointsInTime[kk] = ptsInTime[mm];
                    mm++;
                    nn++;
                }
            } else {
                if (a == 0) {
                    mm--;
                    for (int i = nn; i < curve.ptsInTime.length; i++) {
                        pointsInSpace[kk] = new Point3d();
                        pointsInSpace[kk].add(ptsInSpace[mm], curve.ptsInSpace[i]);
                        pointsInTime[kk] = curve.ptsInTime[i];
                        kk++;
                    }
                } else if (a == 1) {
                    nn--;
                    for (int i = mm; i < ptsInTime.length; i++) {
                        pointsInSpace[kk] = new Point3d();
                        pointsInSpace[kk].add(ptsInSpace[i], curve.ptsInSpace[nn]);
                        pointsInTime[kk] = ptsInTime[i];
                        kk++;
                    }
                }
            }
        }
        return new ParametricLinearCurve3d(pointsInSpace, pointsInTime);
    }

    /**
     * In dieser Methode, wird die ParametricLinearCurve3d curve von dieser
     * subtrahiert... Hierzu wird werden als erstes die Intervalle in denen die
     * PLCs liegen ermittelt.
     *
     * Danach werden die PLCs in eine first-PLC und eine last-PLC umbenannt. Je
     * nachdem welche den frueheren Startzeitpunkt hat.
     *
     * Dann wird gezählt, wie viele Stuetzstellen durch die subtraktion der
     * beiden PLCs entstehen. ((eigene Stuetzstellen von this) + (eigene
     * Stuetzstellen von curve) + (gemeinsame Stuetzstellen von this und curve)
     *
     * Danach wird die subtraktion jedes Punktes durchgefuehrt. Wenn die
     * Stuetzstellen in beiden PLC vorhanden sind, werden die Werte einfach
     * subtrahiert.
     *
     * Wenn eine Stuetzstelle nicht in der der anderen PLC vorhanden ist, wird
     * im Intervall eine lineare Interpolation der Werte verwendet. Wenn die
     * Stuetzstelle hinter, oder vor dem Intervall liegt, wird die jeweils
     * erste, oder letzte Stuetzstelle der anderen PLC zur subtraktion
     * verwendet.
     *
     * @param curve ParametricLinearCurve3d die von dieser
     * ParametricLinearCurve3d subtrahiert wird.
     * @return subtraktion der beiden ParametricLinearCurve3d s
     */
    public ParametricLinearCurve3d sub(ParametricLinearCurve3d curve) {

//        output.CTSO_Logger.println("ParametricLinearCurve3d:add");
//        output.CTSO_Logger.println("this:\n"+this);
//        output.CTSO_Logger.println("curve:\n"+curve);
        Point3d[] pointsInSpace = null;
        long[] pointsInTime = null;
        int anzahl_This = 0, anzahlCurve = 0, anzahlGemeinsam = 0, anzahlGleich = 0;
        long maxIntervall_this = ptsInTime[ptsInTime.length - 1],
                minIntervall_this = ptsInTime[0],
                maxIntervall_cu = curve.ptsInTime[curve.ptsInTime.length - 1],
                minIntervall_cu = curve.ptsInTime[0];

        /*
         * Wie viele Stuetzpunkte(long) von der first PLC3d, liegen ausserhalb(vor) der
         * last PLC3d und innerhalb.
         */
        for (int i = 0; i < ptsInTime.length; i++) {
            long t1 = ptsInTime[i];
            if (t1 < minIntervall_cu || t1 > maxIntervall_cu) {
                anzahlCurve++;
            }
            if (t1 >= minIntervall_cu && t1 <= maxIntervall_cu) {
                anzahlGemeinsam++;
            }
        }
        /*
         * Wie viele Stuetzpunkte(long) von der last PLC3d, liegen ausserhalb(hinter) der
         * first PLC3d und innerhalb.
         */
        for (int i = 0; i < curve.ptsInTime.length; i++) {
            long t1 = curve.ptsInTime[i];
            if (t1 < minIntervall_this || t1 > maxIntervall_this) {
                anzahl_This++;
            }
            if (t1 >= minIntervall_this && t1 <= maxIntervall_this) {
                anzahlGemeinsam++;
            }
        }
        /*
         * Wie viele gleiche Stuetzpunkte gibt es in der Ueberschneidung, der PLC3ds
         */
        for (int i = 0; i < ptsInSpace.length; i++) {
            long point3d = ptsInTime[i];
            for (int j = 0; j < curve.ptsInTime.length; j++) {
                long point3d1 = curve.ptsInTime[j];
                if (point3d == point3d1) {
                    anzahlGleich++;
                }
            }
        }
        /*
         * Hier werden die Stuetzwerte, die in beiden PLCs gleich sind, von der
         * anzahlGemeinsam abgezogen, da sie vorher doppelt gezählt wurden.
         *
         */
        anzahlGemeinsam -= anzahlGleich;
        pointsInTime = new long[anzahlGemeinsam + anzahl_This + anzahlCurve];
        pointsInSpace = new Point3d[anzahlGemeinsam + anzahl_This + anzahlCurve];

        int a = 0, mm = 0, nn = 0, kk = 0;
        for (kk = 0; kk < pointsInTime.length; kk++) {

            if (mm == ptsInSpace.length) {
                a = 0;
            }
            if (nn == curve.ptsInSpace.length) {
                a = 1;
            }

            if (mm < ptsInTime.length && nn < curve.ptsInTime.length) {

                if (ptsInTime[mm] < curve.ptsInTime[nn]) {
                    pointsInSpace[kk] = new Point3d();

                    if (ptsInTime[mm] >= minIntervall_cu && ptsInTime[mm] <= maxIntervall_cu) {
                        pointsInSpace[kk].sub(ptsInSpace[mm], curve.getPoint(ptsInTime[mm]));
                    } else if (ptsInTime[mm] < minIntervall_cu) {
                        pointsInSpace[kk].sub(ptsInSpace[mm], curve.getPointAt(0));
                    } else if (ptsInTime[mm] > maxIntervall_cu) {
                        pointsInSpace[kk].sub(ptsInSpace[mm], curve.getPointAt(curve.ptsInSpace.length - 1));
                    }
                    pointsInTime[kk] = ptsInTime[mm];
                    mm++;
                } else if (ptsInTime[mm] > curve.ptsInTime[nn]) {
                    pointsInSpace[kk] = new Point3d();

                    if (curve.ptsInTime[nn] >= minIntervall_this && curve.ptsInTime[nn] <= maxIntervall_this) {
                        pointsInSpace[kk].sub(getPoint(curve.ptsInTime[nn]), curve.ptsInSpace[nn]);
                    } else if (curve.ptsInTime[nn] < minIntervall_this) {
                        pointsInSpace[kk].sub(getPointAt(0), curve.ptsInSpace[nn]);
                    } else if (curve.ptsInTime[nn] > maxIntervall_this) {
                        pointsInSpace[kk].sub(getPointAt(ptsInSpace.length - 1), curve.ptsInSpace[nn]);
                    }
                    pointsInTime[kk] = curve.ptsInTime[nn];
                    nn++;
                } else if (ptsInTime[mm] == curve.ptsInTime[nn]) {
                    pointsInSpace[kk] = new Point3d();
                    pointsInSpace[kk].sub(ptsInSpace[mm], curve.ptsInSpace[nn]);
                    pointsInTime[kk] = ptsInTime[mm];
                    mm++;
                    nn++;
                }
            } else {
                if (a == 0) {
                    mm--;
                    for (int i = nn; i < curve.ptsInTime.length; i++) {
                        pointsInSpace[kk] = new Point3d();
                        pointsInSpace[kk].sub(ptsInSpace[mm], curve.ptsInSpace[i]);
                        pointsInTime[kk] = curve.ptsInTime[i];
                        kk++;
                    }
                } else if (a == 1) {
                    nn--;
                    for (int i = mm; i < ptsInTime.length; i++) {
                        pointsInSpace[kk] = new Point3d();
                        pointsInSpace[kk].sub(ptsInSpace[i], curve.ptsInSpace[nn]);
                        pointsInTime[kk] = ptsInTime[i];
                        kk++;
                    }
                }
            }
        }
        return new ParametricLinearCurve3d(pointsInSpace, pointsInTime);
    }

    public ParametricLinearCurve3d append(ParametricLinearCurve3d extension) {
        if (this.ptsInTime[ptsInTime.length - 1] > extension.ptsInTime[0]) {
            throw new IllegalArgumentException("cannot append " + extension + " to " + this);
        }

        Point3d[] newSpace = null;
        long[] newTime = null;

        // Falls die Zeitpunkte this.t(n-1) und extension.t(0) gleich sind
        if (this.ptsInTime[ptsInTime.length - 1] == extension.ptsInTime[0]) {
            // dann sollen auch die Punkte im Raum gleich sein, damit es keine unstetigkeit gibt
            if (!this.ptsInSpace[ptsInSpace.length - 1].epsilonEquals(extension.ptsInSpace[0], DynamicPolygonalRegion.eps)) {
                throw new IllegalArgumentException("cannot append " + extension + " to " + this);
            }
            // dann den doppelten Punkt nur einmal beruecksichtigen
            newSpace = new Point3d[this.numberOfPts() + extension.numberOfPts() - 1];
            newTime = new long[this.numberOfPts() + extension.numberOfPts() - 1];

            int i = 0;
            for (int j = 0; j < this.numberOfPts(); j++, i++) {
                newSpace[i] = this.ptsInSpace[j];
                newTime[i] = this.ptsInTime[j];
            }

            for (int j = 1; j < extension.numberOfPts(); j++, i++) {
                newSpace[i] = (Point3d) extension.ptsInSpace[j].clone();
                newTime[i] = extension.ptsInTime[j];
            }
            this.ptsInSpace = newSpace;
            this.ptsInTime = newTime;
            return this;
        }

        // sonst einfach beide hintereinander
        // dann den doppelten Punkt nur einmal beruecksichtigen
        newSpace = new Point3d[this.numberOfPts() + extension.numberOfPts()];
        newTime = new long[this.numberOfPts() + extension.numberOfPts()];

        int i = 0;
        for (int j = 0; j < this.numberOfPts(); j++, i++) {
            newSpace[i] = this.ptsInSpace[j];
            newTime[i] = this.ptsInTime[j];
        }

        for (int j = 0; j < extension.numberOfPts(); j++, i++) {
            newSpace[i] = (Point3d) extension.ptsInSpace[j].clone();
            newTime[i] = extension.ptsInTime[j];
        }
        this.ptsInSpace = newSpace;
        this.ptsInTime = newTime;
        return this;
    }

    public String toString() {
        String na = "";
        for (int i = 0; i < ptsInSpace.length; i++) {
            na += ptsInSpace[i].toString() + "    Time: " + ptsInTime[i] + "\n";
        }
        return na;
    }

    public static void main(String[] args) {

        Point3d[] pts1 = new Point3d[]{
            new Point3d(185.9353162097064, 0.0, 0.0),
            new Point3d(192.1853162097064, 0.0, 0.0),
            new Point3d(195.04348302327932, 0.0, 0.0),
            new Point3d(200.04348302327932, 0.0, 0.0),
            new Point3d(205.57714939613348, 0.0, 0.0),};

        long[] t1 = new long[]{
            20000L,
            30000L,
            31500L,
            32000L,
            40000L,};

        Point3d[] pts2 = new Point3d[]{
            new Point3d(180.9353162097064, 0.0, 0.0),
            new Point3d(192.1853162097064, 0.0, 0.0),
            new Point3d(195.04348302327932, 0.0, 0.0),
            new Point3d(200.04348302327932, 0.0, 0.0),
            new Point3d(205.57714939613348, 0.0, 0.0),};

        long[] t2 = new long[]{
            19000L,
            30000L,
            31000L,
            32000L,
            41000L,};

        ParametricLinearCurve3d c1 = new ParametricLinearCurve3d(pts1, t1, Double.POSITIVE_INFINITY);
        ParametricLinearCurve3d c2 = new ParametricLinearCurve3d(pts2, t2, Double.POSITIVE_INFINITY);

//        output.CTSO_Logger.println(java.util.Arrays.toString(pts1));
//        output.CTSO_Logger.println(java.util.Arrays.toString(t1));
    }
}
