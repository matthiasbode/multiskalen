/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.micro;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.PolygonalCurve2d;
import fuzzy.number.FuzzyNumber;
import javax.vecmath.Point3d;
import math.geometry.DynamicPolygonalRegion;
import math.geometry.ParametricLinearCurve3d;

/**
 *
 * @author bode
 */
public class CraneMotionCalculator {

    private CraneMotionCalculator() {

    }

    public static ParametricLinearCurve3d getTransportMotion(Crane c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit lu) {

        ParametricLinearCurve3d curve = getTransportMotionInLocalCoordinates(c, from, to, lu);

        if (curve == null) {
            return null;
        }

        for (int i = 0; i < curve.numberOfPts(); i++) {
            curve.setPointAt(i, c.getCraneRunway().localToWorld(curve.getPointAt(i)));
        }
        return curve;

    }

    public static CraneMotion3DOverTime getTransportMotionInLocalCoordinates(Crane c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit lu) {

        // TODO: Sonderfall abfangen, wenn die Rendezvouszeiten 0 sind.
        long[] t_ret = new long[2];
        long[] t_sto = new long[2];

        // Start- und Ziel, sowie Positionen des Aufnehmens (retrieving)
        // und Abstellens (storing) ermitteln bestimmen:
        // (alles in lokalen Koordinaten)
        Point3d centerFrom = getCenter(from);
        Point3d centerTo = getCenter(to);
        Point3d p_retrieving = c.getCraneRunway().worldToLocal(centerFrom);
        Point3d p_storing = c.getCraneRunway().worldToLocal(centerTo);

        Point3d p_start = c.getCraneRunway().worldToLocal(getCrossOverPosition(from, c));
        Point3d p_end = c.getCraneRunway().worldToLocal(getCrossOverPosition(to, c));

        long dt = 0L;
        ParametricLinearCurve3d res = null;

        // BEGINN: RendezvouszeitRetrieving (= runterfahren, positionieren, hochfahren)
        // Bewegung von p_start nach p_retrieving
        t_ret[0] = dt;
        ParametricLinearCurve3d m_start_retrieving = getMotionInLocalCoordinates(c, p_start, p_retrieving);
        if (m_start_retrieving != null) {
            res = m_start_retrieving;
            dt = m_start_retrieving.getTimeAt(m_start_retrieving.numberOfPts() - 1);
        }

        // Verweilen beim Aufnehmen (beginnt erst, wenn Absenken erfolgt ist
        ParametricLinearCurve3d m_PosAufwandRetrieving = new ParametricLinearCurve3d(
                new Point3d[]{
                    (Point3d) p_retrieving.clone(),
                    (Point3d) p_retrieving.clone()
                },
                new long[]{
                    dt,
                    dt + c.getPositionierungsAufwandRetrieving(from, to, lu)
                }
        );

        if (m_PosAufwandRetrieving != null) {
            if (res != null) {
                res.append(m_PosAufwandRetrieving);
            } else {
                res = m_PosAufwandRetrieving;
            }
            dt = m_PosAufwandRetrieving.getTimeAt(m_PosAufwandRetrieving.numberOfPts() - 1);
        }

        // Der eigentliche Transport mit LE beginnt erst nach Aufnehmen der LE zum Zeitpunkt dt
        // Hochheben mit Last
        ParametricLinearCurve3d m_retrieving_start = getMotionInLocalCoordinates(c, p_retrieving, p_start, dt);
        if (m_retrieving_start != null) {
            if (res != null) {
                res.append(m_retrieving_start);
            } else {
                res = m_retrieving_start;
            }
            dt = m_retrieving_start.getTimeAt(m_retrieving_start.numberOfPts() - 1);
        }
        // ENDE: RendezvouszeitRetrieving (= runterfahren, positionieren, hochfahren)
        t_ret[1] = dt;

        // Transportweg ermitteln (vom Aufnehmen der Last bis abladen (beides exklusive))
        ParametricLinearCurve3d m_transport = getMotionInLocalCoordinates(c, p_start, p_end, dt);
        if (m_transport != null) {
            if (res != null) {
                res.append(m_transport);
            } else {
                res = m_transport;
            }
            dt = m_transport.getTimeAt(m_transport.numberOfPts() - 1);
        }

        // BEGINN: RendezvouszeitStoring (= runterfahren, positionieren, hochfahren)
        // senken beim Ziel mit Last
        t_sto[0] = dt;
        ParametricLinearCurve3d m_end_storing = getMotionInLocalCoordinates(c, p_end, p_storing, dt);
        if (m_end_storing != null) {
            if (res != null) {
                res.append(m_end_storing);
            } else {
                res = m_end_storing;
            }
            dt = m_end_storing.getTimeAt(m_end_storing.numberOfPts() - 1);
        }

        // Rendezvouszeit fuer Storing (beginnt nach Transport)
        ParametricLinearCurve3d m_PosAufwandStoring = new ParametricLinearCurve3d(
                new Point3d[]{
                    (Point3d) p_storing.clone(),
                    (Point3d) p_storing.clone()
                },
                new long[]{
                    m_end_storing.getTimeAt(m_end_storing.numberOfPts() - 1),
                    m_end_storing.getTimeAt(m_end_storing.numberOfPts() - 1) + c.getPositionierungsAufwandStoring(from, to, lu)
                }
        );
        if (m_PosAufwandStoring != null) {
            if (res != null) {
                res.append(m_PosAufwandStoring);
            } else {
                res = m_PosAufwandStoring;
            }
            dt = m_PosAufwandStoring.getTimeAt(m_PosAufwandStoring.numberOfPts() - 1);
        }

        // hochfahren des Greifwerkzeugs (beginnt nach Abladen
        ParametricLinearCurve3d m_storing_end = getMotionInLocalCoordinates(c, p_storing, p_end, dt);
        if (m_storing_end != null) {
            if (res != null) {
                res.append(m_storing_end);
            } else {
                res = m_storing_end;
            }
            dt = m_storing_end.getTimeAt(m_storing_end.numberOfPts() - 1);
        }
        // ENDE: RendezvouszeitStoring (= runterfahren, positionieren, hochfahren)
        t_sto[1] = dt;
        CraneMotion3DOverTime exactRes = new CraneMotion3DOverTime(res);
        exactRes.setRetrieving(t_ret[0], t_ret[1]);
        exactRes.setStoring(t_sto[0], t_sto[1]);

        return exactRes;
    }

    public static ParametricLinearCurve3d getMotionInLocalCoordinates(Crane c, Point3d from_local, Point3d to_local, long t0) {

        ParametricLinearCurve3d curveX = null;
        ParametricLinearCurve3d curveY = null;
        ParametricLinearCurve3d curveZ = null;
        ParametricLinearCurve3d curveXYZ = null;

        // in x-Richtung
        double[][] motionX = null;
        int numPtsX = 0;

        if (from_local.x != to_local.x) {
            motionX = getMotion(from_local.x, to_local.x, c.vmax_crane, c.amax_crane, c.regardAcceleration);
            numPtsX = motionX[0].length;
        }

        // in y-Richtung
        double[][] motionY = null;
        int numPtsY = 0;

        if (from_local.y != to_local.y) {
            motionY = getMotion(from_local.y, to_local.y, c.vmax_crab, c.amax_crab, c.regardAcceleration);
            numPtsY = motionY[0].length;
        }

        // in z-Richtung
        double[][] motionZ = null;
        int numPtsZ = 0;
        double dt_z = 0.;

        if (from_local.z != to_local.z) {
            motionZ = getMotion(from_local.z, to_local.z, c.vmax_z, c.amax_z, c.regardAcceleration);
            numPtsZ = motionZ[0].length;
            // Fahrt in z-Richtung muss abgeschlossen sein, bevor in eine andere Richtung
            // gefahren wird. In x- und y-Richtung kann gleichzeitig gefahren werden
            dt_z = motionZ[0][numPtsZ - 1];
        }

        // anpassen der x- und y-Zeiten
        for (int i = 0; i < numPtsX; i++) {
            motionX[0][i] += dt_z;
        }
        for (int i = 0; i < numPtsY; i++) {
            motionY[0][i] += dt_z;
        }

        // Kurve in x-Richtung erstellen (falls noetig)
        if (motionX != null) {
            Point3d[] ptsX = new Point3d[numPtsX];
            long[] timesX = new long[numPtsX];

            for (int i = 0; i < numPtsX; i++) {
                ptsX[i] = new Point3d(motionX[1][i], 0., 0.);
                timesX[i] = secondsToMillis(motionX[0][i] + 0.0005) + t0;
            }
            curveX = new ParametricLinearCurve3d(ptsX, timesX, 0.1);
        }

        // Kurve in y-Richtung erstellen (falls noetig)
        if (motionY != null) {
            Point3d[] ptsY = new Point3d[numPtsY];
            long[] timesY = new long[numPtsY];

            for (int i = 0; i < numPtsY; i++) {
                ptsY[i] = new Point3d(0., motionY[1][i], 0.);
                timesY[i] = secondsToMillis(motionY[0][i] + 0.0005) + t0;
            }
            curveY = new ParametricLinearCurve3d(ptsY, timesY, 0.1);
        }

        // Kurve in z-Richtung erstellen (falls noetig)
        if (motionZ != null) {
            Point3d[] ptsZ = new Point3d[numPtsZ];
            long[] timesZ = new long[numPtsZ];

            for (int i = 0; i < numPtsZ; i++) {
                ptsZ[i] = new Point3d(0., 0., motionZ[1][i]);
                timesZ[i] = secondsToMillis(motionZ[0][i] + 0.0005) + t0;
            }
            curveZ = new ParametricLinearCurve3d(ptsZ, timesZ, 0.1);
        }

        // Ueberlagerung der Kurven
//        try {
        if (curveX != null) {
            curveXYZ = curveX;
        }
        if (curveY != null) {
            if (curveXYZ == null) {
                curveXYZ = curveY;
            } else {
                curveXYZ = curveXYZ.add(curveY);
            }
        }
        if (curveZ != null) {
            if (curveXYZ == null) {
                curveXYZ = curveZ;
            } else {
                curveXYZ = curveXYZ.add(curveZ);
            }
        }

        // In Weltkoordinaten transformieren und ggf. x,y,z addieren
        double x_const = 0.;
        if (curveX == null) {
            x_const = from_local.x;
        }
        double y_const = 0.;
        if (curveY == null) {
            y_const = from_local.y;
        }
        double z_const = 0.;
        if (curveZ == null) {
            z_const = from_local.z;
        }

        Point3d offset = new Point3d(x_const, y_const, z_const);

        if (curveXYZ != null) {
            for (int i = 0; i < curveXYZ.numberOfPts(); i++) {
                Point3d p_i = curveXYZ.getPointAt(i);
                p_i.add(offset);
                curveXYZ.setPointAt(i, p_i);
            }
        }

        return curveXYZ;
    }

    /**
     * Liefert die Darstellung von <code>t<code> in Millisekunden als Long.
     *
     * @param t Zeitpunkt in Sekunden (als double)
     * @return Zeitpunkt in Millisekunden (als long)
     */
    protected static long secondsToMillis(double t) {
        if (Double.isInfinite(t)) {
            return (t < 0) ? TimeSlot.MAX_FROM : TimeSlot.MAX_UNTIL;
        }
        return (long) (t * 1000.);
    }

    public static Point3d getCrossOverPosition(LoadUnitStorage lus, Crane c) {
        javax.vecmath.Point2d center = lus.getCenterOfGeneralOperatingArea();
        Point3d co_pos = new Point3d(center.x, center.y, 0.);
        co_pos.z = c.getCrossOverHeight(lus);
        return co_pos;
    }

    public static ParametricLinearCurve3d getMotion(Crane c, Point3d from, Point3d to) {
        return getMotion(c, from, to, 0L);
    }

    /**
     * Bestimmt die Bewegung der Katze ueber der Zeit, um von Punkt
     * <code>from</code> nach Punkt <code>to</code> zu gelangen. Bei der
     * Fahrbewegung wird zuerst die Bewegung in z-Richtung abgeschlossen, dann
     * wird gleichzeitig in x- und y-Richtung gefahren. Es wird jeweils von der
     * maximalen Beschleunigung ausgegangen.
     * <br>
     *
     * Diese Methode beruecksichtigt den Parameter {@link #regardAcceleration}
     * in folgender Weise:
     *
     * <ul>
     * <li>true: Die Bewegung in x-,y- und z-Richtung wird unter
     * Beruecksichtigung der Beschleunigung ermittelt
     * <li>false: Die Bewegung in x-,y- und z-Richtung wird ohne
     * Beruecksichtigung der Beschleunigung ermittelt (nach s=vmax*t)
     * </ul>
     *
     * @param from
     * @param to
     * @param t0 dieser Offset in millisekunden wird zu jedem Zeitpunkt
     * hizugerechnet
     * @return
     */
    public static ParametricLinearCurve3d getMotion(Crane c, Point3d from, Point3d to, long t0) {
        // Transformation in lokale Koordinaten:
        Point3d from_local = c.getCraneRunway().worldToLocal(from);
        Point3d to_local = c.getCraneRunway().worldToLocal(to);

        ParametricLinearCurve3d curve = getMotionInLocalCoordinates(c, from_local, to_local, t0);
        if (curve == null) {
            return null;
        }
        for (int i = 0; i < curve.numberOfPts(); i++) {
            Point3d pointAt = curve.getPointAt(i);
            Point3d localToWorld = c.getCraneRunway().localToWorld(pointAt);
            curve.setPointAt(i, localToWorld);
        }
        return curve;
    }

    /**
     * Gibt die Bewegung in lokalen Koordinaten zwischen from_local und to_local
     * an.
     *
     * @param from_local
     * @param to_local
     * @return
     */
    public static ParametricLinearCurve3d getMotionInLocalCoordinates(Crane c, Point3d from_local, Point3d to_local) {
        return getMotionInLocalCoordinates(c, from_local, to_local, 0L);
    }

    /**
     * Ist die LoadUnitStorage eine ExactLoadUnitPosition, wird die Position
     * zurueckgegeben. Ansonsten der Mittelpunkt der Bounds, z=0. TODO: Methode
     * an richtigen Ort oder in Interface LoadUnitStorage
     *
     * @param lus
     * @return
     */
    public static Point3d getCenter(LoadUnitStorage lus) {
        if (lus == null) {
            throw new UnsupportedOperationException("LoadUnitStorage darf nicht null sein.");
        }
        return lus.getPosition();
    }

    /**
     *
     * Ermittelt die Positionen bzgl. einer Koordinatenrichtung zu den
     * unterschiedlichen Zeitpunkten. Es wird mit lokalen Koordinaten
     * gearbeitet. Dabei werden 2 Faelle unterschieden:
     *
     * <ol>
     * <li>Die Maximalgeschwindigkeit </code>vmax</code> wird nicht erreicht.
     * Dann wird nur der Start und End-Punkt (from_local und to_local)
     * zurueckgegeben mit den entsprechenden Zeitpunkten.
     * <li>Die Maximalgeschwindigkeit wird erreicht. Dann werden 4 Positionen
     * zurueckgegeben: 1. Startpunkt, 2. Position an der vmax erreicht wird, 3.
     * Ende der Fahrt mit vmax, 4. Endpunkt (jeweils mit entsprechenden Zeiten).
     * </ol>
     *
     * @param from_local
     * @param to_local
     * @param vmax
     * @param acc
     * @param regardAcc gibt an, ob die Beschleunigung beruecksichtigt werden
     * soll oder nicht. regardAcc == false: Berechnet die Dauer der Bewegung
     * nach s=v*t
     * @return [2][n]-double-Feld: an den Stellen [0][i] stehen die Zeitpunkte
     * t_i in sec an den Stellen [1][i] stehen die Positionen p_i in m (in
     * lokalen Koordinaten)
     */
    public static double[][] getMotion(double from_local, double to_local, double vmax, double acc, boolean regardAcc) {

        // Wir arbeiten nur mit lokalen koordinaten...
        // Fuer die Laengsbewegung:
        double dis_total = Math.abs(from_local - to_local);

        // Wenn die Beschleunigung nicht beruecksichtigt wird
        if (!regardAcc) {
            double[][] res = new double[2][2];
            // Startpunkt
            res[0][0] = 0.;
            res[1][0] = from_local;

            // endpunkt
            // Dauer zum Ziel mit s=v*t ausrechnen
            res[0][1] = dis_total / vmax;
            res[1][1] = to_local;

            return res;
        }

        // Mit beruecksichtigung der Beschleunigung
        // positiv, wenn from_local < to_local, sonst negativ
        // wird gebraucht, um die neuen Positionen zu berechnen.
        double sign = 1.0;
        if (from_local > to_local) {
            sign = -1.0;
        }

        // Distanz, die zurueckgelegt wird um von 0 auf v_max zu kommen:
        double dis_acc = getDistance(0., vmax, acc);

        // Weg reicht nicht, um in volle Fahrt zu kommen
        if (dis_total < 2. * dis_acc) {

            double[][] res = new double[2][2];

            // Dauer des Weges:
            double t_dis_total = 2. * getMovingTimeRegardingAcc(0., dis_total / 2., acc);

            // Startpunkt
            res[0][0] = 0.;
            res[1][0] = from_local;

            // Endpunkt
            res[0][1] = t_dis_total;
            res[1][1] = to_local;

            return res;
        }

        // Die Maximalgeschwindigkeit wird erreicht:
        // Weg, der unter vmax zurueckgelegt wird:
        double dis_vmax = dis_total - 2. * dis_acc;
        // Zeit, die dafuer benoetigt wird:
        double t_vmax_move = dis_vmax / vmax;

        // Zeit, die zum Beschleunigen gebraucht wird:
        double t_acc = getAccelerationDuration(0., vmax, acc);

        // Nun ergibt sich der Weg aus 4 Punkten
        double res[][] = new double[2][4];

        // Startpunkt
        res[0][0] = 0.;
        res[1][0] = from_local;

        // vmax erreicht:
        res[0][1] = t_acc;
        res[1][1] = from_local + sign * dis_acc;

        // Uebergang Ende vmax-Bereich/Anfang Bremsen
        res[0][2] = res[0][1] + t_vmax_move;
        res[1][2] = res[1][1] + sign * dis_vmax;

        // Endpunkt
        res[0][3] = res[0][2] + t_acc;
        res[1][3] = to_local;

        return res;

    }

    /**
     * Bestimmt die Dauer die benoetigt wird, um eine Strecke <code>s</code>[m]
     * zurueckzulegen. Es wird davon ausgegangen, dass die Startgeschwindigkeit
     * <code>v_0</code>[m/sec] betraegt und konstant mit
     * <code>acc</code>[m/sec^2] beschleunigt wird.
     *
     * @param v_0 Startgeschwindigkeit in [m/sec]
     * @param s zurueckzulegender Weg in [m]
     * @param acc zugrunde liegende (konstante Beschleunigung) in [m/sec^2]
     * @return benoetigte Dauer in [sec] fuer die Strecke <code>s</code>.
     */
    protected static double getMovingTimeRegardingAcc(double v_0, double s, double acc) {
        return -v_0 / acc + Math.sqrt(Math.pow(v_0 / acc, 2.0) + 2. / acc * s);
    }

    public static double getAccelerationDuration(double v_s, double v_e, double acc) {
        return Math.abs(v_s - v_e) / acc;
    }

    /**
     * zurueckgelegter Weg in [m], wenn eine konstante Beschleunigung
     * (positiv/negativ) <code>acc</code> herrscht, um von v_s auf v_e zu
     * kommen.
     *
     * @param v_s
     * @param v_e
     * @return zurueckgelegter Weg in [m] waehrend konstanter Beschleunigung
     */
    public static double getDistance(double v_s, double v_e, double acc) {
        double t = getAccelerationDuration(v_s, v_e, acc);
        double s = 0.5 * acc * t * t + v_s * t;
        return Math.abs(s);
    }

    /**
     * Ermittelt die Dauer der Ruestfahrt von from nach to. Eine Ruestfahrt
     * beginnt bei from (auf crossOver-Hoehe) und endet bei to (auf
     * crossOver-Hoehe).
     *
     * @param from
     * @param to
     * @return
     */
    public static long getSettingUpTime(Crane c, LoadUnitStorage from, LoadUnitStorage to) {
        ParametricLinearCurve3d motion = getSettingUpMotion(c, from, to);
        return (motion != null) ? motion.getDuration() : 0L;
    }

    /**
     * Ermittelt die Ruestfahrbewegung beginnend bei der crossOverPosition von
     * from und endend bei der crossOverPosition von to.
     *
     * @param from Lagerressource, bei der die Ruestfahrt beginnt
     * @param to Lagerressource, bei der die Ruestfahrt endet
     * @return die Bewegung der Ruestfahrt von from nach to; <code>null</code>,
     * wenn keine Bewegung durchgefuehrt wird.
     */
    public static ParametricLinearCurve3d getSettingUpMotion(Crane c, LoadUnitStorage from, LoadUnitStorage to) {

        Point3d from_over = getCrossOverPosition(from, c);
        Point3d to_over = getCrossOverPosition(to, c);

        return getMotion(c, from_over, to_over, 0);
    }

    /**
     * Gibt eine DynamicPolygonalRegion zurück, die die TransportOperation
     * beschreibt.
     *
     * @param top TransportOperation für die die Polygonale Region zurückgegeben
     * werden siol
     * @return
     */
    public static DynamicPolygonalRegion getWorkingAreaRepresentation(TransportOperation top, Schedule s) {
        DynamicPolygonalRegion res = null;
//        if (top.getOrigin() instanceof StartEndPosition && top.getDestination() instanceof StartEndPosition) {
//            Point3d start = ((StartEndPosition) top.getOrigin()).getPosition();
//            Point3d end = ((StartEndPosition) top.getDestination()).getPosition();
//            if (!start.epsilonEquals(end, 0.00001)) {
//                throw new RuntimeException("unbekannter Fehler");
//            }
//            res = new DynamicPolygonalRegion(start.x - c.width / 2., start.x + c.width / 2., top.getCurrentExecutionStart(), top.getCurrentExecutionEnd());
//        } else {
        res = getWorkingAreaRepresentation((Crane) top.getResource(), top.getOrigin(), top.getDestination(), top.getLoadUnit());
        // zeitlich noch verschieben:
        res.moveInTime(s.get(top).longValue());
//        }
        return res;
    }

    public static long getTransportationTime(Crane c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit loadUnit) {
        ParametricLinearCurve3d tm = getTransportMotionInLocalCoordinates(c, from, to, loadUnit);
        return (tm != null) ? tm.getDuration() : 0L;
    }

    /**
     * Gibt eine polygonal berandete Region zurück, die für eine Rüstfahrt die
     * Bewegung in Raum und Zeit repräsentiert.
     *
     * @param origin
     * @param destination
     * @param loadUnit
     * @return
     */
    public static DynamicPolygonalRegion getWorkingAreaRepresentationWithSettingUpCorridor(Crane c, LoadUnitStorage origin, LoadUnitStorage destination, LoadUnit loadUnit) {
        DynamicPolygonalRegion workingAreaRepresentation = getWorkingAreaRepresentation(c, origin, destination, loadUnit);
        return getWorkingAreaRepresentationWithSettingUpCorridor(c, workingAreaRepresentation);
    }

    /**
     * Gibt eine polygonal berandete Region zurück, die für eine Rüstfahrt die
     * Bewegung in Raum und Zeit repräsentiert.
     *
     * @param origin
     * @param destination
     * @param loadUnit
     * @return
     */
    public static DynamicPolygonalRegion getWorkingAreaRepresentationWithSettingUpCorridor(Crane c, DynamicPolygonalRegion res) {

        Point2d[] left = res.getLeftBound().getPoints();
        Point2d[] right = res.getRightBound().getPoints();

        // + rein/rausfahrzeit
        Point2d[] extLeft = new Point2d[left.length + 2];
        Point2d[] extRight = new Point2d[right.length + 2];

        // Zeit, die benoetigt wird, um v=vmax von v=0 zu erreichen
        double t_acc = getLongitudinalAccelarationDuration(c, 0., c.vmax_crane);

        // Weg, der zurueckgelegt wird waehrend Beschleunigungsphase
        double s_acc = getLongitudinalDistance(c, 0., c.vmax_crane);

        // Zeitdifferenz bis zum Schnittpunkt
        double t_p1 = t_acc - s_acc / c.vmax_crane;

        extLeft[0] = left[0].clone();
        extLeft[0].y -= t_p1;
        System.arraycopy(left, 0, extLeft, 1, left.length);
        extLeft[extLeft.length - 1] = left[left.length - 1].clone();
        extLeft[extLeft.length - 1].y += t_p1;

        extRight[0] = right[0].clone();
        extRight[0].y -= t_p1;
        System.arraycopy(right, 0, extRight, 1, right.length);
        extRight[extRight.length - 1] = right[right.length - 1].clone();
        extRight[extRight.length - 1].y += t_p1;

        return new DynamicPolygonalRegion(
                new PolygonalCurve2d(extLeft),
                new PolygonalCurve2d(extRight)
        );
    }

    /**
     * Berechnet die Dauer (in Sekunden), die fuer den Geschwindigkeitswechsel
     * von v_s zu v_e gebraucht wird. Dazu wird die Beschleunigung in
     * Laengsrichtung (reine Kranfahrt, ohne Katze) (amax_crane) verwendet.
     *
     * @param v_s Start-Geschwindigkeit (m/sec)
     * @param v_e End-Geschwindigkeit (m/sec)
     * @return Zeit, die fuer den Geschwindigkeitswechsel benoetigt wird.
     */
    public static double getLongitudinalAccelarationDuration(Crane c, double v_s, double v_e) {
        return getAccelerationDuration(v_s, v_e, c.amax_crane);
    }

    /**
     * zurueckgelegter Weg in [m], wenn maximal beschleunigt/gebremst
     * (amax_crane) wird, um von v_s auf v_e zu kommen.
     *
     * @param v_s
     * @param v_e
     * @return
     */
    public static double getLongitudinalDistance(Crane c, double v_s, double v_e) {
        return getDistance(v_s, v_e, c.amax_crane);
    }

    /**
     * Gibt eine polygonal berandete Region zurück, die eine Bewegung von from
     * zu to beschreibt.
     *
     * @param from
     * @param to
     * @param loadUnit
     * @return
     */
    public static DynamicPolygonalRegion getWorkingAreaRepresentation(Crane c, LoadUnitStorage from, LoadUnitStorage to, LoadUnit loadUnit) {
        ParametricLinearCurve3d motion = getTransportMotionInLocalCoordinates(c, from, to, loadUnit);
        return getWorkingAreaRepresentation(c, motion, loadUnit);
    }

    /**
     * Gibt eine polygonal berandete Region zurück, die eine Bewegung von from
     * zu to beschreibt.
     *
     * @return
     */
    private static DynamicPolygonalRegion getWorkingAreaRepresentation(Crane c, ParametricLinearCurve3d motion, LoadUnit loadUnit) {

        // ueberfuehren in s,t darstellung:
        int numPts = motion.numberOfPts();
        // motion beschreibt den Mittelpunkt
        Point2d[] lPts = new Point2d[numPts];
        Point2d[] rPts = new Point2d[numPts];
        for (int i = 0; i < numPts; i++) {
            lPts[i] = new Point2d(motion.getPointAt(i).x - c.width / 2., DynamicPolygonalRegion.longToDouble(motion.getTimeAt(i)));
            rPts[i] = new Point2d(motion.getPointAt(i).x + c.width / 2., DynamicPolygonalRegion.longToDouble(motion.getTimeAt(i)));
        }

        PolygonalCurve2d leftBound = new PolygonalCurve2d(lPts);
        PolygonalCurve2d rightBound = new PolygonalCurve2d(rPts);

        return new DynamicPolygonalRegion(leftBound, rightBound);
    }

//    public DynamicPolygonalRegion getWorkingAreaRepresentationWithSettingUpCorridor(TransportOperation top, Schedule s) {
//        DynamicPolygonalRegion res = getWorkingAreaRepresentationWithSettingUpCorridor(top.getOrigin(), top.getDestination(), top.getLoadUnit());
//        // zeitlich noch verschieben:
//        res.moveInTime(s.get(top).longValue());
//        return res;
//    }
}
