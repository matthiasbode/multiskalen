/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math.function;

/**
 *
 * @author abuabed
 */
public class DecreasingNormedPolynomial1d extends DecreasingNormedScalarFunction1d {

    private final double grade;

    public DecreasingNormedPolynomial1d() {
        super(0., 1.);
        this.grade = 1.;
    }

    public DecreasingNormedPolynomial1d(double grade) {
        super(0., 1.);
        this.grade = grade;
    }

    public DecreasingNormedPolynomial1d(double grade, double fxMin, double fxMax) {
        super(fxMin, fxMax);
        this.grade = grade;
    }

    @Override
    public double getInverseFunctionValue(double fx) {
        if ((fx < this.getFxMin()) || fx > this.getFxMax()) {
            throw new IllegalArgumentException("fx must be within [fxMin,fxMax] = " + "[" + this.getFxMin() + "," + this.getFxMax() + "]");
        }
        if (this.getFxMin() == this.getFxMax()) {
            return 1.;
        }
        return 1. - Math.pow((fx - this.getFxMin()) / (this.getFxMax() - this.getFxMin()), 1. / this.getGrade());
    }

    @Override
    public double getValue(double x) {
        if ((x < 0.) || x > 1.) {
            throw new IllegalArgumentException("x must be within [0,1]");
        }
        return Math.pow(1. - x, this.getGrade()) * (this.getFxMax() - this.getFxMin()) + this.getFxMin();
    }

    /**
     * @return the grade
     */
    public double getGrade() {
        return grade;
    }

}
