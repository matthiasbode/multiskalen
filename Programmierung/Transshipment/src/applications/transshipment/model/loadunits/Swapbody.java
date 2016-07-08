/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.loadunits;

import applications.transshipment.model.resources.storage.LoadUnitStorage;

/**
 * Objekte dieser Klasse repraesentieren Wechselbruecken.
 *
 * @author thees
 */
public class Swapbody extends LoadUnit {

    /**
     * Standardwert fuer die Laenge. Wird eingesetzt, wenn keine explizite
     * Angabe gemacht wurde. Hier: 7150mm
     */
    public static final double stdLength = 7.150;
    /**
     * Standardwert fuer die Breite. Wird eingesetzt, wenn keine explizite
     * Angabe gemacht wurde. Hier: 2500mm
     */
    public static final double stdWidth = 2.500;
    /**
     * Standardwert fuer die Hoehe. Wird eingesetzt, wenn keine explizite Angabe
     * gemacht wurde. Hier: 2500mm
     */
    public static final double stdHeight = 2.500;
    /**
     * Standardwert fuer das Gewicht. Wird eingesetzt, wenn keine explizite
     * Angabe gemacht wurde. Hier: 13500kg
     */
    public static final double stdWeight = 13500.;
    /**
     * Standardwert der Gefahrguteinstufung. Wird eingesetzt, wenn keine
     * explizite Angabe gemacht wurde. Hier: <code>false</code>
     */
    public static final boolean stdHazardous = false;

    /**
     * Diese Zahl wird {@link #String_id} angehaengt, wenn die ID gebildet wird.
     * Danach wird diese Zahl inkrementiert.
     */
    private static int int_id = 100;
    /**
     * Dieser String wird als Praefix der ID genutzt.
     */
    public static final String String_id = "SBY";

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
    protected final Zange zange;

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Dieser Konstruktor ruft {@link LoadUnit#LoadUnit(generalizedModel.core.resources.LoadUnitStorage, generalizedModel.core.resources.LoadUnitStorage, java.lang.String, double, double, double, double, boolean)
     * }
     * auf, um die konstanten Eigenschaften zu setzen.
     *
     * @param origin Anfangsposition dieser Ladeeinheit
     * @param destination Ziel dieser Ladeeinheit
     * @param length Laenge dieser Ladeeinheit
     * @param width Breite dieser Ladeeinheit
     * @param height Hoehe dieser Ladeeinheit
     * @param weight Gewicht dieser Ladeeinheit
     * @param hazardous Gefahrgut ja / nein
     */
    public Swapbody(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            double width,
            double height,
            double weight,
            boolean hazardous) {
        super(origin, destination, String_id + int_id++, length, width, height, weight, hazardous);
        zange = new Zange(this.width);
        longitudinalDistance = (hazardous) ? 1.500 : .0;
        double rng = zange.getRangierabstand();
        transversalDistance = (hazardous) ? Math.max(rng, 1.500) : rng;
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Die restlichen Werte werden aus den Standardwerten uebernommen.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public Swapbody(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            double height,
            double weight,
            boolean hazardous) {
        this(origin, destination, length, stdWidth, height, weight, hazardous);
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Die restlichen Werte werden aus den Standardwerten uebernommen.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public Swapbody(LoadUnitStorage origin,
            LoadUnitStorage destination,
            double length,
            double weight,
            boolean hazardous) {
        this(origin, destination, length, stdWidth, stdHeight, weight, hazardous);
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Die restlichen Werte werden aus den Standardwerten uebernommen.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public Swapbody(LoadUnitStorage origin,
            LoadUnitStorage destination,
            boolean hazardous) {
        this(origin, destination, stdLength, stdWidth, stdHeight, stdWeight, hazardous);
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Die restlichen Werte werden aus den Standardwerten uebernommen.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(generalizedModel.core.resources.LoadUnitStorage,
     * generalizedModel.core.resources.LoadUnitStorage, double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public Swapbody(LoadUnitStorage origin,
            LoadUnitStorage destination) {
        this(origin, destination, stdLength, stdWidth, stdHeight, stdWeight, stdHazardous);
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Dieser Konstruktor ruft {@link LoadUnit#LoadUnit(java.lang.String, double,
     * double, double, double, boolean) } auf, um die konstanten Eigenschaften
     * zu setzen.
     *
     * @param length Laenge dieser Ladeeinheit
     * @param width Breite dieser Ladeeinheit
     * @param height Hoehe dieser Ladeeinheit
     * @param weight Gewicht dieser Ladeeinheit
     * @param hazardous Gefahrgut ja / nein
     */
    public Swapbody(double length,
            double width,
            double height,
            double weight,
            boolean hazardous) {
        super(String_id + int_id++, length, width, height, weight, hazardous);
        zange = new Zange(this.width);
        longitudinalDistance = (hazardous) ? 1.500 : .0;
        double rng = zange.getRangierabstand();
        transversalDistance = (hazardous) ? Math.max(rng, 1.500) : rng;
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Die restlichen Werte werden aus den Standardwerten uebernommen.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public Swapbody(double length,
            double height,
            double weight,
            boolean hazardous) {
        this(length, stdWidth, height, weight, hazardous);
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Die restlichen Werte werden aus den Standardwerten uebernommen.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public Swapbody(double length,
            double weight,
            boolean hazardous) {
        this(length, stdWidth, stdHeight, weight, hazardous);
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den angegebenen
     * Werten. Die restlichen Werte werden aus den Standardwerten uebernommen.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(double, double, double,
     * double, boolean) } auf. Fuer die Beschreibung der Parameter siehe dort.
     */
    public Swapbody(boolean hazardous) {
        this(stdLength, stdWidth, stdHeight, stdWeight, hazardous);
    }

    public Swapbody(double length, boolean hazardous) {
        this(length, stdWidth, stdHeight, stdWeight, hazardous);
    }

    /**
     * Erstellt eine neue Wechselbruecken-Ladeeinheit mit den Standardwerten.
     * Dazu ruft dieser Konstruktor {@link #Semitrailer(double, double, double,
     * double, boolean) } auf.
     */
    public Swapbody() {
        this(stdLength, stdWidth, stdHeight, stdWeight, stdHazardous);
    }

    /**
     * Diese Methode gibt immer <code>false</code> aus, da es keine stapelbaren
     * Wechselbruecken gibt.
     */
    @Override
    public boolean canStackOn(LoadUnit other) {
        return false;
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    /**
     * Gibt eine Zange aus, die als Greifweite die Breite dieser Wechselbruecke
     * hat.
     *
     * @return Zange der benoetigten Groesse.
     */
    @Override
    public Zange getGreifer() {
        return zange;
    }

    /**
     * Gibt den in Laengsrichtung benoetigten Abstand aus. Dieser ist 0, wenn es
     * sich nicht um Gefahrgut handelt, sonst 1500.
     *
     * @return Benoetigter Abstand in Laengsrichtung.
     */
    @Override
    public double getLongitudinalDistance() {
        return longitudinalDistance;
    }

    /**
     * Gibt den in Querrichtung benoetigten Abstand aus. Dieser entspricht dem
     * Rangierabstand des Greifers oder dem Sicherheitsabstand durch Gefahrgut,
     * je nach dem, was groesser ist.
     *
     * @return Benoetigter seitlicher Abstand.
     */
    @Override
    public double getTransversalDistance() {
        return transversalDistance;
    }

}
