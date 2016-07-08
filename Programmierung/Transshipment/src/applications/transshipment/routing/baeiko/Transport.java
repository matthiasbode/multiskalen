package applications.transshipment.routing.baeiko;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.routing.TransferArea;
import org.util.Pair;

/**
 * Stellt eine Kante in einem Transportgraphen dar.
 *
 * @author wagenkne
 */
public class Transport extends Pair<TransferArea, TransferArea> {

    /**
     * Das zugehoerige Transportsystem
     */
    private ConveyanceSystem conveyanceSystem;
    /**
     * Die Bewertung der Kante
     */
    private double evaluatedValue;

    /**
     * Erstellt eine neue Kante
     *
     * @param first Von Lagersystem
     * @param second Zu Lagersystem
     * @param conveyanceSystem Via Transportsystem
     */
    public Transport(TransferArea first, TransferArea second, ConveyanceSystem conveyanceSystem) {
        super(first, second);
        this.conveyanceSystem = conveyanceSystem;
    }

    /**
     * Erstellt eine neue Kante als Kopie einer existierenden Kante.
     *
     * @param transport1 Die zu kopierende Kante
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Transport(Transport transport1) {
        super(transport1.getFirst(), transport1.getSecond());
        this.conveyanceSystem = transport1.getConveyanceSystem();
        this.setEvaluatedValue(transport1.getEvaluatedValue());
    }

    /**
     * Gibt die Bewertung der Kante zurueck.
     *
     * @return Bewertung der Kante.
     */
    public double getEvaluatedValue() {
        return evaluatedValue;
    }

    /**
     * Setzt die Bewertung der Kante. Darf nur nicht-negative Zahlen uebergeben
     * bekommen.
     *
     * @param evaluatedValue Neue Bewertung der Kante
     */
    public void setEvaluatedValue(double evaluatedValue) {
        if (evaluatedValue < 0) {
            throw new IllegalArgumentException("Die Bewertung darf nicht negativ sein!");
        }
        this.evaluatedValue = evaluatedValue;
    }

    /**
     * Gibt das der Kante zugeordnete Transportsystem zurueck.
     *
     * @return Das der Kante zugeordnete Transportsystem
     */
    public ConveyanceSystem getConveyanceSystem() {
        return conveyanceSystem;
    }

    @Override
    public TransferArea getFirst() {
        return (TransferArea) super.getFirst();
    }

    @Override
    public TransferArea getSecond() {
        return (TransferArea) super.getSecond();
    }

    @Override
    public Transport transposition() {
        return new Transport(this.getSecond(), this.getFirst(), conveyanceSystem);
    }

    @Override
    public String toString() {
        String res = "";

        res += getFirst() + " -(" + conveyanceSystem + ")-> " + getSecond();
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transport other = (Transport) obj;
        if (this.getFirst() != other.getFirst() && (this.getFirst() == null || !this.getFirst().equals(other.getFirst()))) {
            return false;
        }
        if (this.getSecond() != other.getSecond() && (this.getSecond() == null || !this.getSecond().equals(other.getSecond()))) {
            return false;
        }
        if (this.conveyanceSystem != other.conveyanceSystem && (this.conveyanceSystem == null || !this.conveyanceSystem.equals(other.conveyanceSystem))) {
            return false;
        }
        if (Double.doubleToLongBits(this.evaluatedValue) != Double.doubleToLongBits(other.evaluatedValue)) {
            return false;
        }
        return true;
    }

//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 97 * hash + (this.conveyanceSystem != null ? this.conveyanceSystem.hashCode() : 0);
//        hash = 97 * hash + (int) (Double.doubleToLongBits(this.evaluatedValue) ^ (Double.doubleToLongBits(this.evaluatedValue) >>> 32));
//        return hash;
//    }
}
