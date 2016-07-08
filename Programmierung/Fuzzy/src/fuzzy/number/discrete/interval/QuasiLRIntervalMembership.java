/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.number.discrete.interval;

import bijava.math.function.BijectiveScalarFunction1d;
import bijava.math.function.ScalarFunction1d;
import fuzzy.number.discrete.AlphaCutSet;
import math.function.DecreasingNormedPolynomial1d;
import math.function.DecreasingNormedScalarFunction1d;

public class QuasiLRIntervalMembership implements ScalarFunction1d {

    private DecreasingNormedScalarFunction1d leftFunction;
    private DecreasingNormedScalarFunction1d rightFunction;
    private double m1;
    private double m2;
    private double a;
    private double b;

    public QuasiLRIntervalMembership(final AlphaCutSet[] alphaCutSets) {
        this.addAlphaCuts(alphaCutSets);
    }

    public QuasiLRIntervalMembership(DecreasingNormedScalarFunction1d leftFunction, DecreasingNormedScalarFunction1d rightFunction,
            double m1, double m2, double a, double b) {
        if ((a < 0) || (b < 0)) {
            throw new IllegalArgumentException("alphaL and alphaR must be positive real numbers");
        }
        this.leftFunction = leftFunction;
        this.rightFunction = rightFunction;
        this.m1 = m1;
        this.m2 = m2;
        this.a = a;
        this.b = b;
    }

    public final void addAlphaCuts(final AlphaCutSet[] alphaCutSets) {
        leftFunction = new DecreasingNormedScalarFunction1d(alphaCutSets[0].getAlpha(), 1.) {
            DecreasingNormedPolynomial1d[] polynomials = new DecreasingNormedPolynomial1d[alphaCutSets.length - 1];

            {
                for (int i = 0; i < polynomials.length; i++) {
                    polynomials[i] = new DecreasingNormedPolynomial1d(1., alphaCutSets[i].getAlpha(), alphaCutSets[i + 1].getAlpha());
                }
            }
            double xm = alphaCutSets[alphaCutSets.length - 1].getMin();
            double xl = alphaCutSets[0].getMin();

            @Override
            public double getValue(double x) {
                double xiA;
                double xiB;
                for (int i = alphaCutSets.length - 1; i > 0; i--) {
                    xiA = (xm - alphaCutSets[i].getMin()) / (xm - xl);
                    xiB = (xm - alphaCutSets[i - 1].getMin()) / (xm - xl);
                    if ((x > xiA) && (x <= xiB)) {
                        return polynomials[i - 1].getValue((x - xiA) / (xiB - xiA));
                    }
                }
                return 0.;
            }

            @Override
            public double getInverseFunctionValue(double fx) {
                double xiA;
                double xiB;
                for (int i = 0; i < polynomials.length; i++) {
                    xiB = (xm - alphaCutSets[i].getMin()) / (xm - xl);
                    xiA = (xm - alphaCutSets[i + 1].getMin()) / (xm - xl);
                    if ((fx >= polynomials[i].getFxMin()) && (fx < polynomials[i].getFxMax())) {
                        return polynomials[i].getInverseFunctionValue(fx) * (xiB - xiA) + xiA;
                    }
                }
                return 0.;//here the inverse function must return something else ex. infinity
            }
        };
        rightFunction = new DecreasingNormedScalarFunction1d(alphaCutSets[0].getAlpha(), 1.) {
            DecreasingNormedPolynomial1d[] polynomials = new DecreasingNormedPolynomial1d[alphaCutSets.length - 1];

            {
                for (int i = 0; i < polynomials.length; i++) {
                    polynomials[i] = new DecreasingNormedPolynomial1d(1., alphaCutSets[i].getAlpha(), alphaCutSets[i + 1].getAlpha());
                }
            }
            double xm = alphaCutSets[alphaCutSets.length - 1].getMax();
            double xu = alphaCutSets[0].getMax();

            @Override
            public double getValue(double x) {
                double xiA;
                double xiB;
                for (int i = alphaCutSets.length - 1; i > 0; i--) {
                    xiA = (alphaCutSets[i].getMax() - xm) / (xu - xm);
                    xiB = (alphaCutSets[i - 1].getMax() - xm) / (xu - xm);
                    if ((x > xiA) && (x <= xiB)) {
                        return polynomials[i - 1].getValue((x - xiA) / (xiB - xiA));
                    }
                }
                return 0.;
            }

            @Override
            public double getInverseFunctionValue(double fx) {
                double xiA;
                double xiB;
                for (int i = 0; i < polynomials.length; i++) {
                    xiB = (alphaCutSets[i].getMax() - xm) / (xu - xm);
                    xiA = (alphaCutSets[i + 1].getMax() - xm) / (xu - xm);
                    if ((fx >= polynomials[i].getFxMin()) && (fx < polynomials[i].getFxMax())) {
                        return polynomials[i].getInverseFunctionValue(fx) * (xiB - xiA) + xiA;
                    }
                }
                return 0.;//here the inverse function must return something else ex. infinity
            }
        };
        this.m1 = alphaCutSets[alphaCutSets.length - 1].getMin();
        this.m2 = alphaCutSets[alphaCutSets.length - 1].getMax();
        this.a = alphaCutSets[alphaCutSets.length - 1].getMin() - alphaCutSets[0].getMin();
        this.b = alphaCutSets[0].getMax() - alphaCutSets[alphaCutSets.length - 1].getMax();
    }

    @Override
    public double getValue(double x) {
        if (x >= this.m1 && x <= this.m2) {
            return 1.;
        }
        if ((x < this.m1) && (x > this.m1 - this.a)) {
            return this.leftFunction.getValue((this.m1 - x) / this.a);
        }
        if ((x > this.m2) && (x < this.m2 + this.b)) {
            return this.rightFunction.getValue((x - this.m2) / this.b);
        }
        return 0.;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QuasiLRIntervalMembership other = (QuasiLRIntervalMembership) obj;
        if (this.m1 != other.m1) {
            return false;
        }
        if (this.m2 != other.m2) {
            return false;
        }
        if (this.a != other.a) {
            return false;
        }
        if (this.b != other.b) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.m1) ^ (Double.doubleToLongBits(this.m1) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.m2) ^ (Double.doubleToLongBits(this.m2) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.a) ^ (Double.doubleToLongBits(this.a) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.b) ^ (Double.doubleToLongBits(this.b) >>> 32));
        return hash;
    }

    public AlphaCutSet getAlphaCutSet(double alpha) {
        if ((alpha < this.leftFunction.getFxMin()) && (alpha < this.rightFunction.getFxMin())) {
            return new AlphaCutSet(alpha,
                    this.m1 - this.leftFunction.getInverseFunctionValue(this.leftFunction.getFxMin()) * this.a,
                    this.m2 + this.rightFunction.getInverseFunctionValue(this.rightFunction.getFxMin()) * this.b);
        }

        if (alpha < this.leftFunction.getFxMin()) {
            return new AlphaCutSet(alpha,
                    this.m1 - this.leftFunction.getInverseFunctionValue(this.leftFunction.getFxMin()) * this.a,
                    this.m2 + this.rightFunction.getInverseFunctionValue(alpha) * this.b);
        }

        if (alpha < this.rightFunction.getFxMin()) {
            return new AlphaCutSet(alpha,
                    this.m1 - this.leftFunction.getInverseFunctionValue(alpha) * this.a,
                    this.m2 + this.rightFunction.getInverseFunctionValue(this.rightFunction.getFxMin()) * this.b);
        }
        return new AlphaCutSet(alpha,
                this.m1 - this.leftFunction.getInverseFunctionValue(alpha) * this.a,
                this.m2 + this.rightFunction.getInverseFunctionValue(alpha) * this.b);
    }

    /**
     * @return the m
     */
    public double getM1() {
        return m1;
    }

    public double getM2() {
        return m2;
    }

    /**
     * @return the a
     */
    public double getA() {
        return a;
    }

    /**
     * @return the b
     */
    public double getB() {
        return b;
    }

    /**
     * @return the leftFunction
     */
    public BijectiveScalarFunction1d getLeftFunction() {
        return leftFunction;
    }

    /**
     * @param leftFunction the leftFunction to set
     */
    void setLeftFunction(DecreasingNormedScalarFunction1d leftFunction) {
        this.leftFunction = leftFunction;
    }

    /**
     * @return the rightFunction
     */
    public BijectiveScalarFunction1d getRightFunction() {
        return rightFunction;
    }

    /**
     * @param rightFunction the rightFunction to set
     */
    void setRightFunction(DecreasingNormedScalarFunction1d rightFunction) {
        this.rightFunction = rightFunction;
    }

    /**
     * @param a the a to set
     */
    void setA(double a) {
        this.a = a;
    }

    /**
     * @param b the b to set
     */
    void setB(double b) {
        this.b = b;
    }

}
