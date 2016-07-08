/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.loadunits;

import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 *
 * @author bode
 */
public class TwistLockLoadUnit extends LoadUnit {

    /**
     * Standardwert fuer die Laenge. Wird eingesetzt, wenn keine explizite
     * Angabe gemacht wurde. Hier: 6.060m
     */
    public static final double stdLength = 6.060;
    /**
     * Standardwert fuer die Breite. Wird eingesetzt, wenn keine explizite
     * Angabe gemacht wurde. Hier: 2.438m
     */
    public static final double stdWidth = 2.438;
    /**
     * Standardwert fuer die Hoehe. Wird eingesetzt, wenn keine explizite Angabe
     * gemacht wurde. Hier: 2.591m
     */
    public static final double stdHeight = 2.591;
    /**
     * Standardwert fuer das Gewicht. Wird eingesetzt, wenn keine explizite
     * Angabe gemacht wurde. Hier: 20000kg
     */
    public static final double stdWeight = 20000.;
    /**
     * Standardwert fuer den Abstand der Corner Castings. Wird eingesetzt, wenn
     * keine explizite Angabe gemacht wurde, <b>sofern nicht darauf hingewiesen
     * wird, dass die angegebene Laenge verwendet wird</b>. Hier: 6.060m
     */
    public static final double stdGrabLength = 6.060;
    /**
     * Standardwert der Gefahrguteinstufung. Wird eingesetzt, wenn keine
     * explizite Angabe gemacht wurde. Hier:
     * <code>false</code>
     */
    public static final boolean stdHazardous = false;
    /**
     * Gibt an, ob diese Ladeeinheit stapelbar ist. Wird standardmaessig auf
     * true gesetzt.
     */
    private boolean stackable = true;
    /**
     * Diese Zahl wird {@link #String_id} angehaengt, wenn die ID gebildet wird.
     * Danach wird diese Zahl inkrementiert.
     */
    private static int int_id = 100;
    /**
     * Dieser String wird als Praefix der ID genutzt.
     */
    public static final String String_id = "ISO";
    /**
     * Der Abstand der Corner Castings dieser Ladeeinheit.
     */
    protected final double grabLength;
    /**
     * Einzuhaltender Abstand in Laengsrichtung. Wird im Konstruktor gesetzt.
     */
    protected final double longitudinalDistance;
    /**
     * Einzuhaltender Abstand in Querrichtung. Wird im Konstruktor gesetzt.
     */
    protected final double transversalDistance;
    /**
     * Das verwendete Greifwerkzeug. Wird im Konstruktor erstellt.
     */
    protected final Spreader spreader;

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Dieser Konstruktor ruft {@link LoadUnit#LoadUnit(generalizedModel.core.resources.LoadUnitStorage, generalizedModel.core.resources.LoadUnitStorage, java.lang.String, double, double, double, double, boolean)
     * }
     * auf, um die konstanten Eigenschaften zu setzen.
     *
     * @param origin Anfangsposition dieser Ladeeinheit
     * @param destination Ziel dieser Ladeeinheit
     * @param length Laenge dieser Ladeeinheit
     * @param width Breite dieser Ladeeinheit
     * @param height Hoehe dieser Ladeeinheit
     * @param grabLength Abstand der Corner Castings in Laengsrichtung
     * @param weight Gewicht dieser Ladeeinheit
     * @param hazardous Gefahrgut ja / nein
     */
    public TwistLockLoadUnit(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            double width,
            double height,
            double grabLength,
            double weight,
            boolean hazardous) {
        super(origin, destination, String_id + int_id++, length, width, height, weight, hazardous);
        this.grabLength = grabLength;
        longitudinalDistance = (hazardous) ? 1.500 : .0;
        spreader = new Spreader(this.width, this.grabLength);
        double r = spreader.getRangierabstand();
        transversalDistance = (hazardous) ? Math.max(r, 1.500) : r;
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public TwistLockLoadUnit(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            double height,
            double grabLength,
            double weight,
            boolean hazardous) {
        this(origin, destination, length, stdWidth, height, grabLength, weight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public TwistLockLoadUnit(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            double grabLength,
            double weight,
            boolean hazardous) {
        this(origin, destination, length, stdWidth, stdHeight, grabLength, weight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     * <b>Als Greifweite wird die uebergebene Laenge uebernommen.</b>
     */
    public TwistLockLoadUnit(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            double weight,
            boolean hazardous) {
        this(origin, destination, length, stdWidth, stdHeight, length, weight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     * <b>Als Greifweite wird die uebergebene Laenge uebernommen.</b>
     */
    public TwistLockLoadUnit(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            boolean hazardous) {
        this(origin, destination, length, stdWidth, stdHeight, length, stdWeight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     * <b>Als Greifweite wird der Standardwert uebernommen.</b>
     */
    public TwistLockLoadUnit(LoadUnitStorage origin, LoadUnitStorage destination) {
        this(origin, destination, stdLength, stdWidth, stdHeight, stdGrabLength, stdWeight, stdHazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Dieser Konstruktor ruft {@link LoadUnit#LoadUnit(java.lang.String, double, double, double, double, boolean)
     * }
     * auf, um die konstanten Eigenschaften zu setzen.
     *
     * @param length Laenge dieser Ladeeinheit
     * @param width Breite dieser Ladeeinheit
     * @param height Hoehe dieser Ladeeinheit
     * @param grabLength Abstand der Corner Castings in Laengsrichtung
     * @param weight Gewicht dieser Ladeeinheit
     * @param hazardous Gefahrgut ja / nein
     */
    public TwistLockLoadUnit(double length,
            double width,
            double height,
            double grabLength,
            double weight,
            boolean hazardous) {
        super(String_id + int_id++, length, width, height, weight, hazardous);
        this.grabLength = grabLength;
        longitudinalDistance = (hazardous) ? 1.500 : .0;
        spreader = new Spreader(this.width, this.grabLength);
        double r = spreader.getRangierabstand();
        transversalDistance = (hazardous) ? Math.max(r, 1.500) : r;
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public TwistLockLoadUnit(double length,
            double height,
            double grabLength,
            double weight,
            boolean hazardous) {
        this(length, stdWidth, height, grabLength, weight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public TwistLockLoadUnit(double length,
            double grabLength,
            double weight,
            boolean hazardous) {
        this(length, stdWidth, stdHeight, grabLength, weight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     * <b>Als Greifweite wird die uebergebene Laenge uebernommen.</b>
     */
    public TwistLockLoadUnit(double length,
            double weight,
            boolean hazardous) {
        this(length, stdWidth, stdHeight, length, weight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den angegebenen Werten.
     * Die restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     * <b>Als Greifweite wird die uebergebene Laenge uebernommen.</b>
     */
    public TwistLockLoadUnit(double length,
            boolean hazardous) {
        this(length, stdWidth, stdHeight, length, stdWeight, hazardous);
    }

    /**
     * Erstellt eine neue ISO-Container-Ladeeinheit mit den Standardwerten. Die
     * restlichen Werte werden aus den Standardwerten uebernommen. Dazu ruft
     * dieser Konstruktor {@link #TwistLockLoadUnit(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     * <b>Als Greifweite wird der Standardwert uebernommen.</b>
     */
    public TwistLockLoadUnit() {
        this(stdLength, stdWidth, stdHeight, stdGrabLength, stdWeight, stdHazardous);
    }

    /**
     * Gibt
     * <code>true</code> aus, falls die andere Ladeeinheit ebenfalls eine
     * TwistLockLoadUnit ist und keine von beiden Ladeeinheiten Gefahrgut
     * beinhalten.
     *
     * @param other Ladeeinheit, auf der gestapelt werden soll.
     * @return <code>true</code>, falls dieser Stapelvorgang zulaessig ist.
     */
    @Override
    public boolean canStackOn(LoadUnit other) {
        return other instanceof TwistLockLoadUnit && !(hazardous | other.isHazardous());
    }

    @Override
    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean s) {
        stackable = s;
    }

    @Override
    public double getLongitudinalDistance() {
        return longitudinalDistance;
    }

    @Override
    public double getTransversalDistance() {
        return transversalDistance;
    }

    @Override
    public Greifer getGreifer() {
        return spreader;
    }

   
    
}