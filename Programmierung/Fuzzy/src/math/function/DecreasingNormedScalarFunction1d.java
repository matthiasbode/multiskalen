/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math.function;

import bijava.math.function.AbstractBijectiveScalarFunction1d;

/**
 *
 * @author abuabed
 */
public abstract class DecreasingNormedScalarFunction1d extends AbstractBijectiveScalarFunction1d {

    private double fxMax;
    private double fxMin;

    public DecreasingNormedScalarFunction1d(double fxMin, double fxMax) {
        this.fxMin = fxMin;
        this.fxMax = fxMax;
    }

    /**
     * @return the fxMax
     */
    public double getFxMax() {
        return fxMax;
    }

    /**
     * @return the fxMin
     */
    public double getFxMin() {
        return fxMin;
    }

}
