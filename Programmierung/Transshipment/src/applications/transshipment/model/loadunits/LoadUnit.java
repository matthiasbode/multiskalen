package applications.transshipment.model.loadunits;

import applications.transshipment.model.resources.storage.LoadUnitStorage;
import java.util.Objects;

/**
 * Eine generelle Ladeeinheit. Die zu transportierenden Elemente beim Terminal
 * umschlag.
 *
 * @author berthold, hofmann, wagenkne, thees
 *
 * TODO: weiter ausbauen.
 */
public abstract class LoadUnit {

    /**
     * Jede LoadUnit benoetigt eine eindeutige ID. Anhand dieser werden auch die
     * Jobs identifiziert.
     */
    protected String id;
    /**
     * Das geplante Ziel dieser Ladeeinheit .
     */
    private LoadUnitStorage destination;
    /**
     * original destination before destonation is changed for example by setting
     * DNF
     */
    private LoadUnitStorage originalDestination = null;
    /**
     * Die Ursprungsresource der Ladeeinheit.
     */
    protected LoadUnitStorage origin;
    /**
     * Laenge der LU in m.
     *//* Standardmass sind 6096.0mm, was einem
     * 20ft ISO-Container entspricht (1 TEU).
     */

    protected final double length;
    /**
     * Breite der LU in m.
     *//* Standardmass sind 2438.4mm, was einem
     * ISO-Container der Breite 8ft entspricht.
     */

    protected final double width;
    /**
     * Hoehe der LU in m.
     *//* Standardmass sind 2590.8mm, was einem
     * ISO-Container der Hoehe (8ft + 15in) entspricht.
     */

    protected final double height;
    /**
     * Gewicht der LU in kg.
     *//* Standard sind 15000 kg, was einem
     * ca. 3/5 gefuellten 20ft ISO-Container (15300 kg) oder einem
     * ca. 4/9 gefuellten 40ft Container (15647 kg) entspricht.
     */

    protected final double weight;
    /**
     * Gibt an, ob es sich bei dieser Ladeeinheit um Gefahrgut handelt.
     * Standardmaessig sollte dies <code>false</code> sein.
     */
    protected final boolean hazardous;
//    /**
//     * eine LoadUnit kennt zwecks Simulation ihren Job. hat sie keinen, ist er
//     * null
//     */
//    protected LoadUnitJob job;

    /**
     * Dieser Konstruktor muss aufgerufen werden, um die konstanten
     * Objekteigenschaften zu setzen. Diese sind bereits oben beschrieben
     * worden. Hier koennen ausserdem Start- und Zielregion angegeben werden.
     * Die aktuelle Position wird dann mit der Startposition initialisiert.
     *
     * @param origin Start der Ladeeinheit
     * @param destination Ziel der Ladeeinheit
     * @param id
     * @param length
     * @param width
     * @param height
     * @param weight
     * @param hazardous
     */
    protected LoadUnit(LoadUnitStorage origin, LoadUnitStorage destination, String id, double length, double width, double height, double weight, boolean hazardous) {
        this.origin = origin;
        setDestination(destination);
        this.id = id;
        this.length = length;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.hazardous = hazardous;
    }

    /**
     * Dieser Konstruktor muss aufgerufen werden, um die konstanten
     * Objekteigenschaften zu setzen. Diese sind bereits oben beschrieben
     * worden.
     *
     * @param id
     * @param length
     * @param width
     * @param height
     * @param weight
     * @param hazardous
     */
    public LoadUnit(String id, double length, double width, double height, double weight, boolean hazardous) {
        this.id = id;
        this.length = length;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.hazardous = hazardous;
    }

    /**
     * @return Die Ursprungsresource der Ladeeinheit
     */
    public LoadUnitStorage getOrigin() {
        return origin;
    }

    /**
     * Setzt die Startressource der Ladeeinheit
     *
     * @param origin
     */
    public void setOrigin(LoadUnitStorage origin) {
        this.origin = origin;
    }

    public LoadUnitStorage getOriginalDestination() {
        return originalDestination;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Die Zielressource der Ladeeinheit
     */
    public LoadUnitStorage getDestination() {
        return destination;
    }

    /**
     * Setzt die Zielressource der Ladeeinheit
     *
     * @param destination
     */
    public final void setDestination(LoadUnitStorage destination) {
        this.destination = destination;
        if (originalDestination == null) {
            originalDestination = destination;
        }
    }

    /**
     * @return Die Laenge der Ladeeinheit in m.
     */
    public double getLength() {
        return length;
    }

    /**
     * @return Die Breite der Ladeeinheit in m.
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return Die Hoehe der Ladeeinheit in m.
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return Das Gewicht der Ladeeinheit in kg.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return Gefahrgut ja / nein.
     */
    public boolean isHazardous() {
        return hazardous;
    }

    /**
     * Gibt an, ob diese Ladeeinheit auf die uebergebene andere Ladeeinheit
     * gestapelt werden darf.
     *
     * @param other Ladeeinheit, auf der gestapelt werden soll.
     * @return <code>true</code>, falls das Stapeln moeglich ist, sonst <code>
     * false</code>
     */
    public abstract boolean canStackOn(LoadUnit other);

    /**
     * Diese Methode gibt an, ob diese Ladeeinheit grundsaetzlich stapelbar ist,
     * sprich: Gibt es ueberhaupt irgendeine andere Ladeeinheit, die man auf
     * diese draufstellen kann oder auf die man diese draufstellen kann?
     *
     * @return <code>true</code>, falls es eine Moeglichkeit gibt, diese
     * Ladeeinheit zu stapeln.
     */
    public abstract boolean isStackable();

    /**
     * Gibt an, welcher Abstand in Laengsrichtung zu anderen Ladeeinheiten
     * eingehalten werden muss.
     *
     * @return Benoetigter Laengsabstand in m.
     */
    public abstract double getLongitudinalDistance();

    /**
     * Gibt an, welcher Abstand in Querrichtung zu anderen Ladeeinheiten
     * eingehalten werden muss.
     *
     * @return Benoetiger Querabstand in m.
     */
    public abstract double getTransversalDistance();

    /**
     *
     * @return
     */
    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", " + length + "x" + width + "x" + height + "]";
    }

//    public LoadUnitJob getJob() {
//        return job;
//    }
//
//    public void setJob(LoadUnitJob job) {
//        this.job = job;
//    }
    /**
     * @return Das fuer die Ladeeinheit benoetigte Greifwerkzeug.
     */
    public abstract Greifer getGreifer();

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LoadUnit other = (LoadUnit) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
