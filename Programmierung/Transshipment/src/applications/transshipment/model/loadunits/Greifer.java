/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.loadunits;

/**
 * Greifwerkzeug, dass von Kranen verwendet und von Ladeeinheiten benoetigt wird.
 * Es hat eine Greifweite, die an die Ausmasse der Ladeeinheit angepasst werden
 * muss. Es verfuegt ausserdem ueber einen Rangierabstand, der je nach
 * Implementierung umgesetzt werden muss.
 * @author thees
 */
public abstract class Greifer {

    /**
     * Die Greifweite in mm, wird mit 6000mm initialisiert.
     */
    protected double greifweite = 6.000;

    protected Greifer(double greifweite) {
        this.greifweite = greifweite;
    }

    /**
     * Gibt die fuer diesen Greifer eingestellte Greifweite aus.
     */
    public double getGreifweite() {
        return greifweite;
    }
    /**
     * Aendert die Greifweite dieses Greifers auf den uebergebenen Wert. Der
     * Rueckgabewert gibt an, wie lange dieser Prozess der Umstellung dauert.
     * @param newVal Neue Greifweite
     * @return Umruestzeit
     */
    public long setGreifweite(double newVal) {
        long r = ((long)Math.abs(greifweite - newVal))*5000;
        greifweite = newVal;
        return r;
    }

    /**
     * Gibt den in Breitenrichtung benoetigten Rangierabstand dieses Greifers
     * aus.
     * @return Der benoetigte Rangierabstand
     */
    public abstract double getRangierabstand();
}