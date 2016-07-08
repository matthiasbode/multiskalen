package bijava.geometry.dim3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;



/**
 * @author Jan Stilhammer
 */
public class ConvexHull3d {

    /**
     * Berechnet die Konvexe Hlle der bergebenen Punktmenge ber inkrementellen
     * Aufbau nach einem Sichtbarkeitskriterium.
     *
     * @param pts
     * @return
     */
    public ArrayList<Triangle3d> getHull(ArrayList<Point3d> pts) {
        holeStack hole = new holeStack(); // dient der Speicherung des Lochs
        HashMap<Triangle3d, HalfSpace> halfSpaces = new HashMap<Triangle3d, HalfSpace>();
        ArrayList<Triangle3d> faces = new ArrayList<Triangle3d>();
        if (pts.size() < 2) {
            System.out.println("Only two Points");
            return faces;
        }
        // Initialisierung mit zwei jeweils unterschiedlich orientierten Dreiecken
        // bestehend aus den ersten drei Koordinaten
        Triangle3d t0 = new Triangle3d(pts.get(0), pts.get(1), pts.get(2));
        faces.add(t0);
        halfSpaces.put(t0, new HalfSpace(t0));
        Triangle3d t1 = new Triangle3d(pts.get(0), pts.get(2), pts.get(1));
        faces.add(t1);
        halfSpaces.put(t1, new HalfSpace(t1));

        // Hauptschleife: Hinzufgen der verbleibenden Punkte
        for (int i = 3; i < pts.size(); i++) {
            // Entfernen der Dreiecke aus der bisherigen Hlle,
            // in deren Halfspace der aktuelle Punkt liegt
            // diese Dreiecke sind zu entfernen
            ArrayList<Triangle3d> removeThese = new ArrayList<Triangle3d>();
            for (Triangle3d currTriangle : faces) {
                if (halfSpaces.get(currTriangle).inside(pts.get(i))) {
                    // Dreieck das vom aktuellen Knoten i
                    // sichtbar ist zum Loch hinzufgen
                    hole.push(currTriangle);
                    removeThese.add(currTriangle);
                } // else System.out.println(" -> outside");
            }
            // Wenn es Dreiecke gibt die Enfertn werden mssen,
            // enferne diese und aktualisiere die Hlle neu
            if (removeThese.size() != 0) {
                for (Triangle3d t : removeThese) {
                    faces.remove(t);
                    halfSpaces.remove(t);
                }

                for (Pair<Point3d, Point3d> pair : hole) {
                    // Neues Dreick bestehend aus der aktuellen Kante des Lochs
                    // und dem aktuellen Punkt i
                    Triangle3d t = new Triangle3d(pair.getFirst(), pair.getSecond(), pts.get(i));
                    faces.add(t);
                    halfSpaces.put(t, new HalfSpace(t));
                }
            }
            hole.clear();  // Loch-Stack zurcksetzen
        }
        return faces;
    }

    /**
     * Stack fr Kanten, arbeitet wie normaler Stack, auer dass das hinzufgen
     * einer Kante (a,b) wenn schon eine Kante (b,a) enthalten ist dazu fhrt
     * dass die Kante (b,a) aus dem Stack entfernt wird und (a,b) nicht
     * hinzugefgt wird.
     *
     * @author Jan
     *
     */
    private class holeStack implements Iterable<Pair<Point3d, Point3d>> {

        private ArrayList<Pair<Point3d, Point3d>> data;

        public holeStack() {
            data = new ArrayList<Pair<Point3d, Point3d>>();
        }

        private void push(Point3d a, Point3d b) {
            Pair<Point3d, Point3d> pair0 = new Pair<Point3d, Point3d>(a, b);
            Pair<Point3d, Point3d> pair1 = new Pair<Point3d, Point3d>(b, a);
            //if(data.remove(pair0))return;
            if (data.remove(pair1)) {
                return;
            }
            if (!data.contains(pair0)) {
                data.add(pair0);
            }
        }

        public void push(Triangle3d t) {
            push(t.points[0], t.points[1]);
            push(t.points[1], t.points[2]);
            push(t.points[2], t.points[0]);
        }

        public void clear() {
            data.clear();
        }

        @Override
        public Iterator<Pair<Point3d, Point3d>> iterator() {

            return data.iterator();
        }
    }

    /**
     * Simpler HalfSpace, bestehend aus einem Normalenvektor und einem Skalaren
     * Abstandswert
     *
     * @author Jan
     *
     */
    private class HalfSpace {

        Vector3d normal; // Normale auf der Grenzflche
        double d; // eqn of half space is normal.x - d > 0

        public HalfSpace(Triangle3d t) {
            Vector3d v0 = new Vector3d(t.points[0].x, t.points[0].y, t.points[0].z);
            Vector3d v1 = new Vector3d(t.points[1].x, t.points[1].y, t.points[1].z);
            Vector3d v2 = new Vector3d(t.points[2].x, t.points[2].y, t.points[2].z);
            init(v0, v1, v2);

        }

        public HalfSpace(Vector3d a, Vector3d b, Vector3d c) {
            init(a, b, c);
        }

        public HalfSpace(javax.vecmath.Point3d a, javax.vecmath.Point3d b, javax.vecmath.Point3d c) {
            Vector3d v0 = new Vector3d(a.x, a.y, a.z);
            Vector3d v1 = new Vector3d(b.x, b.y, b.z);
            Vector3d v2 = new Vector3d(c.x, c.y, c.z);
            init(v0, v1, v2);
        }

        private void init(Vector3d a, Vector3d b, Vector3d c) {
            normal = b.sub(a);
            normal.cross(normal, c.sub(a));
            // normal.normalize(); // Wird nicht bentigt fr Sichtbarkeitskritrium
            d = normal.dot(a);
        }

        public boolean inside(Point3d p) {
            return (normal.x * p.x + normal.y * p.y + normal.z * p.z) > d;
        }
    }
}