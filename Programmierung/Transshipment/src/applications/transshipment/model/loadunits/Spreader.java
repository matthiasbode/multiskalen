/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.model.loadunits;

/**
 *
 * @author thees
 */
public class Spreader extends Greifer {

    public Spreader(double greifweite, double grabLength) {
        super(greifweite);
        this.grabLength = grabLength;
    }

    /**
     * Der vom Spreader benoetigte Rangierabstand in mm. Standard sind 100mm.
     */
    protected final double rangierabstand = .100;

    @Override
    public double getRangierabstand() {
        return rangierabstand;
    }


    protected double grabLength = 6.060;

    /**
     * Gibt den Abstand an, den die Corner Castings zueinander haben müssen.
     * Dieser Wert muss entsprechend der Ladeeinheit angepasst werden.
     * @return Der Abstand der Corner Castings, auf den dieser Spreader
     * eingestellt ist.
     */
    public double getGrabLength() {
        return grabLength;
    }

    /**
     * Stellt diesen Spreader auf eine neue Laenge ein. Der Rückgabewert gibt an,
     * wie lange dieser Umruestprozess dauert.
     * @param grabLength Neu eingestellt Laenge
     * @return Umruestzeit
     */
    public long setGrabLength(double grabLength) {
        long r = (long)Math.abs(this.grabLength - grabLength)*5000;
        this.grabLength = grabLength;
        return r;
    }
}