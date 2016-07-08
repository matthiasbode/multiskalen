package bijava.geometry.dimN;

import bijava.vecmath.*;
import bijava.geometry.CoordinateValue;
import bijava.math.*;
import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

/**
 * Die Klasse SimplexNd stellt grundlegende Funktionen fuer ein Simplex bereit.
 *
 * @author  Institut fuer Bauinfromatik
 * @author  Peter Milbradt
 * @version 1.0
 *
 */
public class SimplexNd extends ConvexPolyhedronNd {

    protected SimplexNd() {
        super();
    }

    /**
     * Erzeugt ein Simplex der Dimension n aus k+1 Punkten. Die Punkte muessen
     * linear unabhaengig sein.
     *
     * @param points ein Feld von k+1 Punkten der Dimesnion n
     */
    public SimplexNd(PointNd[] points) {
        super();
        if (points.length == 1) {
            nodes = points;
            spaceDim = points[0].dim(); // Warnung null-Pointer Exception
            order = 0; // zunaechst einmal
        } else {
            if (points.length == 2) {
                if ((points[0].dim() == points[1].dim()) && (points[0].distance(points[1]) > 0.)) {
                    nodes = points;
                    spaceDim = points[0].dim(); // Warnung null-Pointer Exception
                    order = 1; // zunaechst einmal
                } else {
                    throw new IllegalArgumentException("no Simplex: the arrgument points is not an affinely independent PointNd set");
                }
            } else {
                if (!AlgGeometryNd.isAffinelyIndependent(points)) {
                    throw new IllegalArgumentException("no Simplex: the arrgument points is not an affinely independent PointNd set");
                }
                nodes = new PointNd[points.length];
                for (int i = 0; i < points.length; i++) {
                    nodes[i] = points[i];
                }
                spaceDim = points[0].dim(); // Warnung null-Pointer Exception
                order = points.length - 1; // zunaechst einmal
            }
        }
    }

    /**
     * Erzeugt ein Simplex der Dimension der Ordnung 1 aus zwei Punkten.
     * @param point Punkten der Dimesnion n
     */
    public SimplexNd(PointNd p1, PointNd p2) {
        super();
        if ((p1.dim() == p2.dim()) && (p1.distance(p2) > 0.)) {
            nodes = new PointNd[]{p1, p2};
            spaceDim = p1.dim(); // Warnung null-Pointer Exception
            order = 1; // zunaechst einmal
        } else {
            throw new NullPointerException("no simplex");
        }
    }

    /**
     * Erzeugt ein Simplex der Dimension der Ordnung 0 aus einem Punkt.
     * @param point Punkten der Dimesnion n
     */
    public SimplexNd(PointNd point) {
        super();
        nodes = new PointNd[]{point};
        spaceDim = point.dim(); // Warnung null-Pointer Exception
        order = 0; // zunaechst einmal
    }

    /** .. */
    public SimplexNd(SimplexNd s, PointNd o) {
        if (s.spaceDim == o.dim()) {
            this.spaceDim = s.spaceDim;
            this.order = s.order + 1;
            this.nodes = new PointNd[this.order + 1];
            for (int i = 0; i < s.order + 1; i++) {
                this.nodes[i] = s.nodes[i];
            }
            this.nodes[this.order] = o;
        }
        // geht nicht
        if (!AlgGeometryNd.isAffinelyIndependent(this.nodes)) {
            throw new IllegalArgumentException("no Simplex: the points of the SimplexNd and the arrgument point are not an affinely independent PointNd set");
        }
    }

    /**
     * Diese Methode prueft, ob sich ein Punkt im Simplex befindet.
     *
     * @param point ein Punkt, fuer den geprueft wird, ob er sich implements
     * Simplex befindet.
     * @return <code>true</code>, wenn der Punkt im Simplex liegt,
     * <code>false</code>, wenn nicht.
     */
    @Override
    public boolean contains(PointNd point) {
        CoordinateValue[] coord = getNatElemCoord(point);
        double sum = 0.;
        for (int i = 0; i < coord.length; i++) {
            sum += coord[i].getValue();
        }

        if (Math.abs(sum - 1.) < EPSILON) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Diese Methode bestimmt den baryzentrischen Schwerpunktes des Simplex.
     *
     *  @return der baryzentrische Schwerpunkt des Simplex
     */
    @Override
    public PointNd getBaryCenter() {
        int dim = nodes[0].dim();
        double[] result = new double[dim];
        for (int j = 0; j < nodes.length; j++) {
            for (int i = 0; i < dim; i++) {
                result[i] += nodes[j].getCoord(i);
            }
        }

        for (int i = 0; i < dim; i++) {
            result[i] /= nodes.length;
        }
        return new PointNd(result);
    }

    /**
     * Die Methode ermittelt die baryzentrischen Koordinaten eines Punktes
     * bezueglich des Simplexes.
     *
     * @deprecated Die Baryzentrischen Koordinaten sind aequivalent zu den
     * Natuerlichen Elementkoordintaten. Die Funktion wird ersetzt durch die
     * Funktion getNatElemCoord(PointNd).
     * @param point der Punkt in kartesischen Koordinaten, dessen baryzentrische
     * Koordinaten ermittelt werden sollen.
     * @return <code>double</code>-Feld mit den baryzentrischen Koordinaten
     */
    public CoordinateValue[] getBaryCoord(PointNd point) {
        return getNatElemCoord(point);
    }

    /**
     * Diese Methode bestimmt die Koordinaten des baryzentrischen Schwerpunktes des Simplex.
     *
     *  @return double[] der baryzentrische Schwerpunkt des Simplex
     */
    public double[] getBaryCenterCoords() {
        int dim = nodes[0].dim();
        double[] result = new double[dim];
        for (int j = 0; j < nodes.length; j++) {
            for (int i = 0; i < dim; i++) {
                result[i] += nodes[j].getCoord(i);
            }
        }

        for (int i = 0; i < dim; i++) {
            result[i] /= nodes.length;
        }
        return result;
    }

    /**
     * Die Methode liefert die Dimension (Ordnung) des Simplex.
     *
     * @return Dimension (Ordnung) des Simples.
     */
    public int getElementDimension() {
        return order;
    }

    /**
     * Die Methode ermittelt die Natuerlichen Elementkoordinaten eines Punktes
     * bezueglich des Simplexes.
     *
     * @param point der Punkt in kartesischen Koordinaten, dessen Natuerliche
     * Elementkoordinaten ermittelt werden sollen.
     * @return <code>double</code>-Feld mit den baryzentrischen Koordinaten
     */
    public CoordinateValue[] getNatElemCoord(PointNd point) {
        CoordinateValue[] coord = new CoordinateValue[nodes.length];
        double[] A = new double[nodes.length];
        double A_ges = this.getVolume();

        PointNd[] p_tmp = new PointNd[nodes.length];
        int hilf = 0;
        for (int i = 0; i < nodes.length; i++) {
            hilf = 0;
            p_tmp[hilf++] = point;
            for (int j = 0; j < nodes.length; j++) {
                if (j != i) {
                    p_tmp[hilf++] = (PointNd) nodes[j];
                }
            }
            try {
                A[i] = (new SimplexNd(p_tmp)).getVolume();
            } catch (Exception e) {
                A[i] = 0.;
            }
            coord[i] = new CoordinateValue((PointNd) nodes[i], A[i] / A_ges);
        }
        return coord;
    }

    /**
     * Die Methode erfragt einen Knoten des Simplex am Index.
     *
     * @param index Index des Knotens, der zurueckgegeben werden soll.
     * @return Knoten am Index.
     */
    public PointNd getNode(int index) {
        return (PointNd) nodes[index];
    }

    /**
     * Methode zum Erfragen der Knoten eines Simplex.
     *
     * @return Feld von Knoten.
     */
    public PointNd[] getNodes() {
        PointNd[] pp = new PointNd[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            pp[i] = (PointNd) nodes[i];
        }
        return pp;
    }

    /**
     * Die Methode liefert die Dimension des Raumes, in der sich das Simplex
     * befindet.
     *
     * @return Dimension des Raumes.
     */
    public int getSpaceDimension() {
        return spaceDim;
    }

    /**
     * Die Methode berechnet die Volumenmasszahl eines m-Simplex im
     * n-dimensionalen euklidischen Raum.
     *
     * @return das Lebesgue-Mass des Simplex.
     */
    public double getVolume() {
        if (order == 0) {
            return 0.;
        }
        if (order == 1) {
            return ((PointNd) nodes[0]).distance((PointNd) nodes[1]);
        }
        if (order == spaceDim) {
            if (spaceDim == 2) {
                return getVolume2D();
            }
        //if (n == 3)
        //	return getVolume3D();
        }
        return getVolumeNd();
    }

    /**
     * Die Methode berechnet die Volumenmasszahl eines m-Simplex im
     * n-dimensionalen euklidischen Raum.
     *
     * @return das Lebesgue-Mass des Simplex
     */
    private double getVolumeNd() {

        double[][] a = new double[order + 2][order + 2];

        for (int l = 0; l < order + 2; l++) {
            a[order + 1][l] = 1.0;
            a[l][order + 1] = 1.0;
            a[l][l] = 0.0;
        }

        for (int j = 0; j < order + 1; j++) {
            for (int m = j; m < order + 1; m++) {
                //a[j][m] = PointNd.distanceSquared(p[j], p[m]);
                a[j][m] = Math.pow(((PointNd) nodes[j]).distance((PointNd) nodes[m]), 2);
                a[m][j] = a[j][m];
            }
        }

        DMatrix aa = new DMatrix(a);

        //..Volumen berechen...............................................

        double faktor =
                Math.pow((double) Function.fac(order), 2) * Math.pow(2., order);
        double det = Math.abs(aa.det());
        return Math.sqrt(det / faktor);
    }

    /**
     * Die Methode berechnet die Flaeche eines 2-Simplex im 2-dimensionalen
     * euklidischen Raum.
     *
     * @return die Flaeche des Simplex
     */
    private double getVolume2D() {
        return 0.5 * Math.abs((nodes[1].getCoord(0) - nodes[0].getCoord(0)) * (nodes[2].getCoord(1) - nodes[0].getCoord(1)) - (nodes[2].getCoord(0) - nodes[0].getCoord(0)) * (nodes[1].getCoord(1) - nodes[0].getCoord(1)));
    }

    /**
     * Die Methode ermittelt das orientierten Volumen eines n-Simplex im
     * n-dimensionalen euklidischen Raum.
     * Stimmen dimension und Ordnung nicht ueberein wird 0 zurueckgegeben
     *
     *  @return die orientierte Volumen des Simplex
     */
    protected double getVolume2DOriented() {
        return 0.5 * (nodes[0].getCoord(0) * nodes[1].getCoord(1) + nodes[1].getCoord(0) * nodes[2].getCoord(1) + nodes[2].getCoord(0) * nodes[0].getCoord(1) - nodes[2].getCoord(0) * nodes[1].getCoord(1) - nodes[2].getCoord(1) * nodes[0].getCoord(0) - nodes[0].getCoord(1) * nodes[1].getCoord(0));
    }

    public double getVolumeOriented() {
        double volume = 0.;

        if (order == ((PointNd) nodes[0]).dim()) {

            double[][] a = new double[order + 1][order + 1];

            for (int l = 0; l < order + 1; l++) {
                a[order][l] = 1.0;
            }

            for (int j = 0; j < order; j++) {
                for (int m = j; m < order + 1; m++) {
                    a[j][m] = ((PointNd) nodes[m]).x[j];
                }
            }

            DMatrix aa = new DMatrix(a);

            //.. Berechnung des orientierten Volumens ...............................................
            double faktor = Function.fac(order);
            double det = Math.abs(aa.det());
            return Math.sqrt(det / faktor);
        }
        return volume;
    }

    /** bestimmt die Orientierung eines Simplex wenn dies moeglich ist
     * ist moeglich wenn raumdimension und Ordnung des Simplex gleich sind, dann giebt es zwei Orientierungen die mit +1 und -1 gekennzeichnet sind.
     * sind die Raumdiemnsion ungleich der Ordnung wird 0 zurueckgegeben
     */
    public int getOrientation() {
        int orientation = 0;
        if (order == ((PointNd) nodes[0]).dim()) {
            if (order == 2) {
                orientation = (int) Math.signum(getVolume2DOriented());
            } else {
                orientation = (int) Math.signum(getVolumeOriented());
            }
        }
        return orientation;
    }

    /** veraendert die Orientierung so, dass sie als positiv betrachtet werden kann.
     * Dies Funktioniert nur, wenn die dimension des Raumes und die Ordnung des Simplex gleich sind
     */
    public boolean setPositivOrientation() {
        boolean rvalue = false;
        if ((order == ((PointNd) nodes[0]).dim()) && (getOrientation() == -1)) {
            PointNd tmp = (PointNd) nodes[0];
            nodes[0] = nodes[1];
            nodes[1] = tmp;
            rvalue = true;
        }
        return rvalue;
    }

    /**
     * Rueckgabe einer Repraesentation des Simplex als Text
     *
     * @return String des Simplex
     */
    public String toString() {
        String erg = "";
        erg += "Simplex der Ordnung " + order + ":\n";
        for (int i = 0; i < nodes.length; i++) {
            erg += "Punkt " + i + ":" + nodes[i] + "\n";
        }
        return erg;
    }

    /**
     * Methode zum Erfragen, ob ein Punkt in der Ebene eines Simplexes liegt.
     *
     * @param simplex Simplex fuer welches Ueberprueft werden soll, ob der Punkt in
     * der Ebene liegt.
     * @param point Punkt f�r welchen �berpr�ft werden soll, ob er in der Ebene
     * des Simplex liegt.
     * @return <code>true</code>, wenn der Punkt in der Ebene des Simplexes
     * liegt, <code>false</code>, wenn nicht.
     */
    public static boolean isInSubSpace(SimplexNd simplex, PointNd point) {
        if (simplex.getElementDimension() < simplex.getSpaceDimension()) {
            PointNd[] tmp1 = simplex.getNodes();
            PointNd[] tmp2 = new PointNd[tmp1.length + 1];
            for (int i = 0; i < tmp1.length; i++) {
                tmp2[i] = tmp1[i];
            }
            tmp2[tmp1.length] = point;
            if (AlgGeometryNd.getLinearHullDimension(tmp1) == AlgGeometryNd.getLinearHullDimension(tmp2)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Methode zum Erfragen, ob zwei Simplexe in derselben Ebene liegen.
     *
     * @param simplex1, simplex2 Simplexe f�r die �berpr�ft werden soll, ob sie
     * in einer Ebene liegen.
     * @return <code>true</code>, wenn der Punkt in der Ebene des Simplexes
     * liegt, <code>false</code>, wenn nicht.
     */
    public static boolean isInSubSpace(SimplexNd simplex1, SimplexNd simplex2) {
        PointNd[] tmp = simplex2.getNodes();
        boolean erg = true;
        for (int i = 0; i < tmp.length; i++) {
            erg = erg && isInSubSpace(simplex1, tmp[i]);
        }
        return erg;
    }

    /** return the subsimplexes of this simplex */
    // effiziente implementierung �ber Matrixdarstellung
    public SimplexNd[] getFacets() {

        if (order == 0) {
            return null;
        }

        SimplexNd[] subs = new SimplexNd[order + 1];

        if (order == 1) {
            subs[0] = new SimplexNd((PointNd) nodes[0]);
            subs[1] = new SimplexNd((PointNd) nodes[1]);
            return subs;
        }

        for (int i = 0; i < order + 1; i++) {
            PointNd[] subpoints = new PointNd[order];
            int count, x;

            if (i % 2 == 0) {
                count = 0;
                for (int a = i; a < i + order; a++) {
                    subpoints[count++] = (PointNd) nodes[a % (order + 1)];
                }
            } else {
                count = 0;
                for (int a = i + order + 1; a > i + 1; a--) {
                    subpoints[count++] = (PointNd) nodes[a % (order + 1)];
                }
            }
            subs[i] = new SimplexNd(subpoints);
        }
        return subs;
    }

    private SimplexNd[] getFacetsInEfficient() {
        SimplexNd subs[] = new SimplexNd[order + 1];

        if (order == 0) {
            return null;
        }

        if (order == 1) {
            subs[0] = new SimplexNd((PointNd) nodes[0]);
            subs[1] = new SimplexNd((PointNd) nodes[1]);
            return subs;
        }

        if (order == 2) {
            PointNd punkte[] = new PointNd[2];

            punkte[0] = (PointNd) nodes[0];
            punkte[1] = (PointNd) nodes[1];
            subs[0] = new SimplexNd(punkte);

            punkte[0] = (PointNd) nodes[1];
            punkte[1] = (PointNd) nodes[2];
            subs[1] = new SimplexNd(punkte);

            punkte[0] = (PointNd) nodes[2];
            punkte[1] = (PointNd) nodes[0];
            subs[2] = new SimplexNd(punkte);
        } else {
            // ineffiziente Implementierung
            // nehme die erste Facette und von dieser alle Facetten
            // bilde aus den Zwillinge und dem �berbleibenden Punkte die weiteren Facetten
            SimplexNd subsubs[];
            PointNd[] punkte = new PointNd[order];
            for (int i = 0; i < order; i++) {
                punkte[i] = (PointNd) nodes[i];
            }
            subs[0] = new SimplexNd(punkte);

            subsubs = subs[0].getFacets();

            for (int i = 1; i < order + 1; i++) {
                subs[i] = new SimplexNd(subsubs[i - 1].getTwin(), (PointNd) nodes[order]);
            }

        }
        return subs;
    }

    public SimplexNd getTwin() {
        SimplexNd result = null;
        PointNd necken[] = new PointNd[order + 1];

        necken[0] = (PointNd) nodes[1];
        necken[1] = (PointNd) nodes[0];
        for (int i = 2; i < order + 1; i++) {
            necken[i] = (PointNd) nodes[i];
        }

        result = new SimplexNd(necken);

        return result;
    }

    public boolean equals(SimplexNd S) {
        return orderedEquals(S);
    }

    public boolean equals(Object o) {
        if (o instanceof SimplexNd) {
            SimplexNd s = (SimplexNd) o;
            return orderedEquals(s);
        }
        if (o instanceof ConvexPolyhedronNd) {
            ConvexPolyhedronNd s = (ConvexPolyhedronNd) o;
            return super.orderedEquals(s);
        }
        return false;
    }

    /** Gleichheit der Knoten ohne Orientierung */
    public boolean unorderedEquals(SimplexNd S) {

        if (!(order == S.order)) {
            return false;
        }        // Dimensionen stimmen nicht ueberein

        SimplexNd help = new SimplexNd((PointNd[]) S.nodes);
        // test ob das Simplex aus den gleichen Punkten besteht
        int permutation = 0;
        int k = 1;
        for (int i = 0; i <= order; i++) {
            for (int j = i; j <= order; j++) {
                if ((nodes[i] == help.nodes[j])) {
                    permutation++;
                    k = 0;
                }
            }
            if ((k == 1)) {
                return false;
            } // �berhaupt kein Knoten ist gleich
            else {
                k = 1;
            }
        }
        if (permutation == order + 1) {
            return true;
        } else {
            return false;
        }
    }

    /** Gleichheit der Knoten mit Orientierung */
    public boolean orderedEquals(SimplexNd S) {

        if (!(order == S.order)) {
            return false;
        }        // Dimensionen stimmen nicht ueberein

        SimplexNd help = new SimplexNd((PointNd[]) S.nodes);
        // test ob das Simplex aus den gleichen Punkten besteht
        int permutation = 0;
        int k = 1;
        for (int i = 0; i <= order; i++) {
            for (int j = i; (j <= order) && (k == 1); j++) {
                if ((nodes[i] == help.nodes[j])) {
                    PointNd tmp = (PointNd) help.nodes[i];
                    help.nodes[i] = help.nodes[j];
                    help.nodes[j] = tmp;
                    if (i != j) {
                        permutation++;
                    }
                    k = 0;
                }
            }
            if ((k == 1)) {
                return false;
            } // �berhaupt kein Knoten ist gleich
            else {
                k = 1;
            }
        }
        if ((permutation % 2) == 0) {
            return true;
        } //gerader Permutation sind die Simplexe gleich....
        else {
            return false;
        }
    }

    /**
     * Berechnet das Zentrums der euklidischen Umkugel des Simplex,
     * entpsricht im zweidimensionalen Raum dem Mittelpunkt des Kreises,
     * auf dessen Rand die Punkte liegen.
     *
     * @return Koordinaten des Zentrum der Umkugel, null, wenn kein Zentrum exisitiert
     */
    public PointNd getCircumCircleCenter() {

        double[] d = new double[spaceDim];
        switch (order) {
            case 0:
                for (int j = 0; j < spaceDim; j++) {
                    d[j] = nodes[0].getCoord(j);
                }
                break;
            case 1:
                for (int j = 0; j < spaceDim; j++) {
                    d[j] = 0.5 * (nodes[0].getCoord(j) * nodes[1].getCoord(j));
                }
                break;
            default:
                if (spaceDim != order) {
                    System.out.println("Fehler in VecMath.center: Raum-Dimension und Ordnung des Simplex sind nicht gleich.");
                }

                GMatrix A = new GMatrix(spaceDim, spaceDim);
                GVector b = new GVector(spaceDim);

                // Aufbau des Gleichungssystems
                for (int i = 0; i < spaceDim; i++) {
                    for (int j = 0; j < spaceDim; j++) {
                        A.setElement(i, j, 2.0 * (nodes[i].getCoord(j) - nodes[i + 1].getCoord(j)));
                        b.setElement(i, b.getElement(i) + (nodes[i].getCoord(j) * nodes[i].getCoord(j) - nodes[i + 1].getCoord(j) * nodes[i + 1].getCoord(j)));
                    }
                }
                GVector x = new GVector(spaceDim);
                int k = A.LUD(A, x);
                x.LUDBackSolve(A, b, x);

                for (int i = 0; i < spaceDim; i++) {
                    d[i] = x.getElement(i);
                }
        }
        return new PointNd(d);
    }
}
