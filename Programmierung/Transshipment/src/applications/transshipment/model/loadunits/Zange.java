/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.loadunits;

/**
 *
 * @author thees
 */
public class Zange extends Greifer {

    public Zange(double greifweite) {
        super(greifweite);
    }

    /**
     * Der Rangierabstand der Zange in m. Standardwert sind 0.500m.
     */
    protected final double rangierabstand = .300;

    @Override
    public double getRangierabstand() {
        return rangierabstand;
    }

}