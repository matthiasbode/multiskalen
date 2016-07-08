package bijava.geometry.dim2;

/**
 * Ellipse2d.java stellt Eigenschaften und Methoden fuer Ellipsen zur Verfuegung.
 * 
 * @author hoecker
 */
public class Ellipse2d implements Region2d {

    public Point2d m; // Mittelpunkt
    public double rx,  ry; // Radien

    /**
     * Erzeugt eine Ellipse bei (0, 0) mit Radius = 0.
     */
    public Ellipse2d() {
        m = new Point2d();
        rx = ry = 0.;
    }

    /**
     * Erzeugt eine Ellipse bei (0, 0) mit bestimmten Radien.
     * 
     * @param rx Radius in x-Richtung.
     * @param ry Radius in y-Richtung.
     */
    public Ellipse2d(double rx, double ry) {
        if (rx < 0. || ry < 0.) {
            throw new IllegalArgumentException("radius < 0");
        }
        m = new Point2d();
        this.rx = rx;
        this.ry = ry;
    }

    /**
     * Erzeugt eine Ellipse mit bestimmten Mittelpunkt und Radien.
     * 
     * @param m Mittelpunkt.
     * @param rx Radius in x-Richtung.
     * @param ry Radius in y-Richtung.
     */
    public Ellipse2d(Point2d m, double rx, double ry) {
        if (rx < 0. || ry < 0.) {
            throw new IllegalArgumentException("radius < 0");
        }
        this.m = m;
        this.rx = rx;
        this.ry = ry;
    }

    /**
     * Liefert den Umfang.
     * 
     * @return Umfang.
     */
    public double getLength() {
        return Math.PI * (1.5 * (rx + ry) - Math.sqrt(rx * ry));
    }

    /**
     * Liefert die Flaeche.
     * 
     * @return Flaeche.
     */
    public double getArea() {
        return Math.PI * rx * ry;
    }

    /**
     * Liefert den Radius bzgl. eines Kreispunktes.
     * 
     * @param t Winkel bzgl. des Kreispunktes [0 <= t <= 2PI].
     * @return Radius bzgl. des Kreispunktes.
     */
    public double getRadius(double t) {
        double rxt = rx * Math.cos(t);
        double ryt = ry * Math.sin(t);

        return Math.sqrt(rxt * rxt + ryt * ryt);
    }

    /**
     * Liefert den Radius bzgl eines Punktes p.
     * 
     * @param p Punkt.
     * @return Radius bzgl. des Punktes.
     */
    private double getRadius(Point2d p) {
        if (p.equals(m)) {
            return Double.NaN;
        }

        // Winkel t des zugehoerigen Kreispunktes berechnen
        double dx = p.x - m.x;
        double dy = p.y - m.y;

        double t;

        if (dy < 0.) {
            t = 2. * Math.PI - Math.acos(dx / Math.sqrt(dx * dx + dy * dy));
        } else {
            t = Math.acos(dx / Math.sqrt(dx * dx + dy * dy));
        }

        return getRadius(t);
    }

    /**
     * Zeigt, ob diese Ellipse einen Punkt p beinhaltet.
     * 
     * @param p Punkt.
     * @return <code>true</code> falls diese Ellipse den Punkt beinhaltet.
     */
    public boolean contains(Point2d p) {
        if (p.equals(m)) {
            return true;
        }

        return m.distance(p) <= getRadius(p);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ellipse2d) {
            return equals((Ellipse2d) o);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    /**
     * Zeigt, ob diese Ellipse einer anderen gleicht.
     * 
     * @param el andere Ellipse.
     * @return <code>true</code> falls diese Ellipse der anderen gleicht.
     */
    public boolean equals(Ellipse2d el) {
        return m.equals(el.m) && rx == el.rx && ry == el.ry;
    }

    @Override
    public String toString() {
        return "[" + m + ", rx = " + rx + ", ry = " + ry + "]";
    }
}
