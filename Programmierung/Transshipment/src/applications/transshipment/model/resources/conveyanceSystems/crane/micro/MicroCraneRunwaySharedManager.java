/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.micro;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.SharedResourceManager;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import bijava.geometry.dim2.Edge2d;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.PolygonalCurve2d;
import bijava.geometry.dim2.Vector2d;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import math.FieldElement;
import math.geometry.DynamicPolygonalRegion;

/**
 *
 * @author bode
 */
public class MicroCraneRunwaySharedManager implements SharedResourceManager<CraneRunway> {

    CraneRunway cranerunway;
    Crane[] cranes;
    double buffer = 5;//0.005;
    public static int counter = 0;

    public MicroCraneRunwaySharedManager(CraneRunway cranerunway) {
        this.cranerunway = cranerunway;
        this.cranes = new Crane[cranerunway.getSharingResources().size()];
        int i = 0;
        for (Crane crane : cranerunway.getSharingResources()) {
            cranes[i++] = crane;
        }
    }

    public void constrainNeighbours(Schedule s, Crane pivot, DynamicPolygonalRegion c) {
        DynamicPolygonalRegion constraint = c.clone();

        // Index des pivot-Krans suchen
        int index = 0;
        while (cranes[index] != pivot) {
            index++;
            if (index >= cranes.length) {
                throw new RuntimeException("Crane not constructed by Kranbahn. Use Kranbahn-Constructor to create Cranes...");
            }
        }

        // Mindestabstand zweier Krane beruecksichtigen
        PolygonalCurve2d newLeftBound = constraint.getLeftBound();
        for (Point2d p : newLeftBound.getPoints()) {
            p.x -= buffer;
        }
        constraint.setLeftBound(newLeftBound.getPoints());

        PolygonalCurve2d newRightBound = constraint.getRightBound();
        for (Point2d p : newRightBound.getPoints()) {
            p.x += buffer;
        }
        constraint.setRightBound(newRightBound.getPoints());
        // linke und rechte Bound des constraints jeweils oben und unten erweitern.
        // dazu die fahrzeit des Krans zu smax bzw. smin berechnen. Mit Beruecksichtigung
        // der Beschleunigung insofern, dass das Wegfahren mit langsamster Geschwindigkeit
        // moeglich ist.
        // Zeit, die benoetigt wird, um v=vmax von v=0 zu erreichen
        MicroscopicCraneRule rule = (MicroscopicCraneRule) s.getHandler().get(pivot);
        double t_acc = CraneMotionCalculator.getLongitudinalAccelarationDuration(rule.getResource(), 0., pivot.getVmax_crane());

        // Weg, der zurueckgelegt wird waehrend Beschleunigungsphase
        double s_acc = CraneMotionCalculator.getLongitudinalDistance(rule.getResource(), 0., pivot.getVmax_crane());

        // Ausfahrweg nach der Operation (im s,t Diagramm nach oben)
        double t_p1 = t_acc - s_acc / pivot.getVmax_crane();
        // Beschraenkung fuer rechten Nachbarn:
        Point2d p1_r = new Point2d(
                newRightBound.getPointAt(newRightBound.size() - 1).x,
                newRightBound.getPointAt(newRightBound.size() - 1).y + t_p1
        );

        Point2d p2_r = new Point2d(
                pivot.getSmin(),
                p1_r.y + (p1_r.x - pivot.getSmin()) / pivot.getVmax_crane()
        );

        // Beschraenkung fuer linken Nachbarn:
        Point2d p1_l = new Point2d(
                newLeftBound.getPointAt(newLeftBound.size() - 1).x,
                newLeftBound.getPointAt(newLeftBound.size() - 1).y + t_p1
        );

        Point2d p2_l = new Point2d(
                pivot.getSmax(),
                p1_l.y + (pivot.getSmax() - p1_l.x) / pivot.getVmax_crane()
        );

        // Einfahrweg vor der Operation: Beschraenkung nach unten im s,t-Diagramm
        // Beschraenkung fuer rechten Nachbarn:
        Point2d q1_r = new Point2d(
                newRightBound.getPointAt(0).x,
                newRightBound.getPointAt(0).y - t_p1
        );

        Point2d q2_r = new Point2d(
                pivot.getSmin(),
                q1_r.y - (q1_r.x - pivot.getSmin()) / pivot.getVmax_crane()
        );

        // Beschraenkung fuer linken Nachbarn
        Point2d q1_l = new Point2d(
                newLeftBound.getPointAt(0).x,
                newLeftBound.getPointAt(0).y - t_p1
        );

        Point2d q2_l = new Point2d(
                pivot.getSmax(),
                q1_l.y - (pivot.getSmax() - q1_l.x) / pivot.getVmax_crane()
        );

        // Punkte zusammenfassen (jeweils fuer linke und rechte Grenze)
        // rechte Grenze:
        Point2d[] ptsForRightCurve = new Point2d[newRightBound.size() + 4];
        ptsForRightCurve[0] = q2_r;
        ptsForRightCurve[1] = q1_r;
        System.arraycopy(newRightBound.getPoints(), 0, ptsForRightCurve, 2, newRightBound.size());
        ptsForRightCurve[ptsForRightCurve.length - 2] = p1_r;
        ptsForRightCurve[ptsForRightCurve.length - 1] = p2_r;

        // linke Grenze:
        Point2d[] ptsForLeftCurve = new Point2d[newLeftBound.size() + 4];
        ptsForLeftCurve[0] = q2_l;
        ptsForLeftCurve[1] = q1_l;
        System.arraycopy(newLeftBound.getPoints(), 0, ptsForLeftCurve, 2, newLeftBound.size());
        ptsForLeftCurve[ptsForLeftCurve.length - 2] = p1_l;
        ptsForLeftCurve[ptsForLeftCurve.length - 1] = p2_l;

        PolygonalCurve2d rightCurve = new PolygonalCurve2d(ptsForRightCurve);
        PolygonalCurve2d leftCurve = new PolygonalCurve2d(ptsForLeftCurve);

        double leftSmin = constraint.getSmin();
        double rightSmax = constraint.getSmax();

        // jetzt pruefen, wieviele nachbarn nach links bzw. rechts beeinflusst werden
        // und dann auf deren workingArea constrain durchfuehren.
        for (int i = index - 1; i >= 0; i--) {
            // beschraenke nachbarn, falls im Bereich
            if (cranes[i].smax > leftSmin) {
                MicroscopicCraneRule currentRule = (MicroscopicCraneRule) s.getHandler().get((cranes[i]));
                if (!currentRule.getWorkingArea().constrain(leftCurve, true)) {
//                    throw new RuntimeException("This should never happen!");
                }
            }
            // und verschiebe die Grenze um Kranweite nach links...
            for (Point2d p : leftCurve.getPoints()) {
                p.x -= cranes[i].width;
            }
            leftSmin -= cranes[i].width;
        }
        for (int i = index + 1; i < cranes.length; i++) {
            // beschraenke nachbarn, falls im Bereich
            if (cranes[i].smin < rightSmax) {
                MicroscopicCraneRule currentRule = (MicroscopicCraneRule) s.getHandler().get((cranes[i]));
                if (!currentRule.getWorkingArea().constrain(rightCurve, false)) {
//                    throw new RuntimeException("This should never happen!");
                }
            }
            // und verschiebe die Grenze um Kranweite nach rechts...
            for (Point2d p : rightCurve.getPoints()) {
                p.x += cranes[i].width;
            }
            rightSmax += cranes[i].width;
        }
    }

    public void unconstrainNeighbours(Schedule schedule, Crane pivot, Operation o) {
        MicroscopicCraneRule rule = (MicroscopicCraneRule) schedule.getHandler().get(pivot);
        // Index des pivot-Krans suchen
        int index = 0;
        while (cranes[index] != pivot) {
            index++;
            if (index >= cranes.length) {
                throw new RuntimeException("Crane not constructed by Kranbahn. Use Kranbahn-Constructor to create Cranes...");
            }
        }
        // Die neue (rechte) Grenze des linken Nachbarn ergibt sich aus
        // min( min( randbedingung_deleted, thisRightBound-width ), leftNeighbour_smax )
        FieldElement currentStart = schedule.get(o);
        FieldElement currentEnd = currentStart.add(o.getDuration());
        TimeSlot slot = new TimeSlot(currentStart, currentEnd);

        PolygonalCurve2d[] bcs_4deleted = rule.getSettingUpBCsForDeletedTransport(schedule, slot);

        // TODO: erst tmin,tmax-range holen, dann verschieben!
        double tmin = bcs_4deleted[0].getPointAt(0).y;
        double tmax = bcs_4deleted[0].getPointAt(bcs_4deleted[0].size() - 1).y;
        PolygonalCurve2d thisRightBoundMinusWidth = rule.getWorkingArea().getRightBound().clone();
        for (Point2d p : thisRightBoundMinusWidth.getPoints()) {
            p.x -= pivot.width;
        }
        PolygonalCurve2d thisLeftBoundPlusWidth = rule.getWorkingArea().getLeftBound().clone();
        for (Point2d p : thisLeftBoundPlusWidth.getPoints()) {
            p.x += pivot.width;
        }

        Point2d[] thisRightBoundInSlot = DynamicPolygonalRegion.getPointsInRange(thisRightBoundMinusWidth.getPoints(), tmin, tmax);
        Point2d[] thisLeftBoundInSlot = DynamicPolygonalRegion.getPointsInRange(thisLeftBoundPlusWidth.getPoints(), tmin, tmax);

        ArrayList<Point2d> tmpCondition4LeftNeighbour = DynamicPolygonalRegion.getSMinPts(new PolygonalCurve2d(thisRightBoundInSlot), bcs_4deleted[0]);
        // Sicherheitsabstand beachten:
        for (Point2d p : tmpCondition4LeftNeighbour) {
            p.x -= buffer;
        }

//        if (o.toString().equals("TransportOperationImplementation: [LU: ISO354, 2010.01.01 at 22:30:00@generalizedModel.catrin.train.Slot-159 --> 2010.01.01 at 22:30:32@subSDSrow-[ 294 295 ]  Position: (195.29999999999978, 78.0, 0.0) by SimpleCrane-1]"))
//            DynamicPolygonalRegion.showPolygons = true;
//        else
//            DynamicPolygonalRegion.showPolygons = false;
        ArrayList<Point2d> tmpCondition4RightNeighbour = DynamicPolygonalRegion.getSMaxPts(new PolygonalCurve2d(thisLeftBoundInSlot), bcs_4deleted[1]);
        // Sicherheitsabstand beachten:
        for (Point2d p : tmpCondition4RightNeighbour) {
            p.x += buffer;
        }

        // jetzt pruefen, wieviele nachbarn nach links bzw. rechts beeinflusst werden
        // und dann auf deren workingArea unconstrain durchfuehren.
        for (int i = index - 1; i >= 0; i--) {
            MicroscopicCraneRule currentRule = (MicroscopicCraneRule) schedule.getHandler().get((cranes[i]));
            // beschraenke linken nachbarn
            PolygonalCurve2d leftNeighbourSmax = new PolygonalCurve2d(new Point2d(cranes[i].smax, tmin), new Point2d(cranes[i].smax, tmax));

            ArrayList<Point2d> newPtsLeft = null;

            try {
                newPtsLeft = DynamicPolygonalRegion.getSMinPts(new PolygonalCurve2d(tmpCondition4LeftNeighbour.toArray(new Point2d[0])), leftNeighbourSmax);
            } catch (Exception e) {

                e.printStackTrace();
            }

            // in neue Bound jetzt noch den Anfang und das Ende der alten Bound einfuegen
            int j = 0;
            for (Point2d p : currentRule.getWorkingArea().getRightBound().getPoints()) {
                // davor einfuegen
                if (p.y < tmin) {
                    newPtsLeft.add(j++, p);
                } else if (p.y > tmax) {
                    newPtsLeft.add(p);
                }
            }

            currentRule.getWorkingArea().setRightBound(newPtsLeft.toArray(new Point2d[0]));

            break;  //TODO: nur temporaer
        }
        for (int i = index + 1; i < cranes.length; i++) {
            MicroscopicCraneRule currentRule = (MicroscopicCraneRule) schedule.getHandler().get((cranes[i]));
            // beschraenke linken nachbarn
            ArrayList<Point2d> newPtsRight = null;
            PolygonalCurve2d rightNeighbourSmin = new PolygonalCurve2d(new Point2d(cranes[i].smin, tmin), new Point2d(cranes[i].smin, tmax));
//            try {
            newPtsRight = DynamicPolygonalRegion.getSMaxPts(new PolygonalCurve2d(tmpCondition4RightNeighbour.toArray(new Point2d[0])), rightNeighbourSmin);
//            } catch(Exception e) {
//                output.CTSO_Logger.println("o: "+o);
//                output.CTSO_Logger.println("tmpCondition4LeftNeighbour.toArray(new Point2d[0])): "+java.util.Arrays.toString(tmpCondition4LeftNeighbour.toArray(new Point2d[0])));
//                System.exit(1);
//            }

            DynamicPolygonalRegion.checkSortedPts(newPtsRight.toArray(new Point2d[0]));

            // in neue Bound jetzt noch den Anfang und das Ende der alten Bound einfuegen
            int j = 0;
            for (Point2d p : currentRule.getWorkingArea().getLeftBound().getPoints()) {
                // davor einfuegen
                if (p.y < tmin - 0.0001) {
                    newPtsRight.add(j++, p);
                } else if (p.y > tmax + 0.0001) {
                    newPtsRight.add(p);
                }
            }

            DynamicPolygonalRegion.checkSortedPts(newPtsRight.toArray(new Point2d[0]));

            currentRule.getWorkingArea().setLeftBound(newPtsRight.toArray(new Point2d[0]));

            break;  // TODO: nur temporaer beschraenkt
        }
    }

    /**
     * Gibt die Area zurueck, die sich aus den Normalen-Punkten ausgehend von
     * smin bis smax ergibt. Diese Methode funktioniert noch nicht fuer
     * beliebige BaseLines. Daher wird durch den Konstruktor nur eine einfache
     * BaseLine mit Start- und Endpunkt akzeptiert.
     *
     * @param smin
     * @param smax
     * @return
     */
    public Area getArea(double smin, double smax) {
//        if (smin < 0 || smax > 1)
//            throw new IllegalArgumentException("smin,smax must be in [0,1]");
        if (smin >= smax) {
            throw new IllegalArgumentException("smin must be smaller than smax");
        }
//        throw new UnsupportedOperationException("Not implemented yet!");

        double gesamtLaenge = cranerunway.baseLine.getLength();
        Point2d[] boundPointsStart = getBoundPoints(smin/**
         * gesamtLaenge
         */
        );
        Point2d[] boundPointsEnd = getBoundPoints(smax/**
         * gesamtLaenge
         */
        );
        GeneralPath gp = new GeneralPath();
        gp.moveTo(boundPointsStart[0].x, boundPointsStart[0].y);
        gp.lineTo(boundPointsEnd[0].x, boundPointsEnd[0].y);
        gp.lineTo(boundPointsEnd[1].x, boundPointsEnd[1].y);
        gp.lineTo(boundPointsStart[1].x, boundPointsStart[1].y);
        gp.closePath();
        return new Area(gp);

    }

    /**
     * Gibt die Randpunkte, die Orthogonal zur BaseLine liegen wieder. Die
     * Punkte liegen jeweils unter- (pl) und oberhalb (pu) des Punktes P, der
     * sich an der Stelle s auf der Kurve befindet. Der Abstand betraegt jeweils
     * breite/2. Stimmt P mit einem der Punkte x ueberein, so wird die Normale
     * der zur Vorgaenger und Nachfolgerkante ermittelt, die dann zur
     * resultierenden gemittelt werden.
     * <br>
     * x <br>
     * \ pu <br>
     * \ | <br>
     * \ | b/2 <br>
     * \ | <br>
     * x-------P------x----------- ... --------x ----> s<br>
     * | <br>
     * | b/2 <br>
     * | <br>
     * pl <br>
     *
     *
     * @param s Laufparameter auf der Kurve s aus dem Intervall
     * [0,baseLine.getLength()].
     * @return [lowerNormalBound, upperNormalBound]
     */
    private Point2d[] getBoundPoints(double s) {
        // TODO: Aenderung bei lokalen Koordinaten beachten!
        // Hier nur ein Versatz in x-Richtung beruecksichtigt!
        Point2d onCurve = cranerunway.baseLine.getPoint(s);
        // Parameter sl fuer rechten Nachbarpunkt suchen. Falls s genau einen Punkt
        // trifft, so ist sl == s.
        double sl = 0.;
        int i = 0;
        while (sl < s) {
            i++;
            sl = cranerunway.baseLine.getLengthToPoint(i);
        }

        Vector2d normal = null;

        // Jetzt Fallunterscheidung:
        // sl == s: Zentrale differenzen fuer Normale; bei Randpunkt linke bzw. rechte Differenzen
        if (sl == s) {
            Point2d[] baseLinePts = cranerunway.baseLine.getPoints();
            // i ist mittlerer Punkt
            if (i > 0 && i < baseLinePts.length - 1) {

                // Normalen von linker Kante:
                Vector2d normal_left = Edge2d.getNormal(
                        cranerunway.baseLine.getPointAt(i - 1), cranerunway.baseLine.getPointAt(i),
                        cranerunway.breite / 2.
                );
                // Normalen von rechter Kante:
                Vector2d normal_right = Edge2d.getNormal(
                        cranerunway.baseLine.getPointAt(i), cranerunway.baseLine.getPointAt(i + 1),
                        cranerunway.breite / 2.
                );
                // mitteln:
                normal = normal_left.add(normal_right).mult(0.5);
            } // i ist linker Rand-Punkt
            else if (i == 0) {
                Edge2d edge = new Edge2d(cranerunway.baseLine.getPointAt(i), cranerunway.baseLine.getPointAt(i + 1));
                normal = Edge2d.getNormal(edge.p0, edge.p1, cranerunway.breite / 2.);
            } // i ist rechter Rand-Punkt
            else if (i == baseLinePts.length - 1) {
                Edge2d edge = new Edge2d(cranerunway.baseLine.getPointAt(i - 1), cranerunway.baseLine.getPointAt(i));
                normal = Edge2d.getNormal(edge.p0, edge.p1, cranerunway.breite / 2.);
            }
            // Kein Punkt genau getroffen
        } else {
            // i ist jetzt rechter Nachbar zu besagtem Punkt
            Edge2d edge = new Edge2d(cranerunway.baseLine.getPointAt(i - 1), cranerunway.baseLine.getPointAt(i));
            normal = Edge2d.getNormal(edge.p0, edge.p1, cranerunway.breite / 2.);
        }

        // Punkte zu Punkt onCurve in negative und positive Normalenrichtung ermitteln
        Point2d upperNormalBound = onCurve.clone();
        upperNormalBound.add(normal);
//        upperNormalBound.x += baseLine.getPointAt(0).x;
        Point2d lowerNormalBound = onCurve.clone();
        lowerNormalBound.sub(normal);
//        lowerNormalBound.x += baseLine.getPointAt(0).x;
        return new Point2d[]{lowerNormalBound, upperNormalBound};
    }

    @Override
    public CraneRunway getResource() {
        return cranerunway;
    }

}
