/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.number.discrete;

import bijava.math.function.BijectiveScalarFunction1d;
import bijava.math.function.ScalarFunction1d;
import math.function.DecreasingNormedPolynomial1d;
import math.function.DecreasingNormedScalarFunction1d;

public class QuasiLRFuzzyMembership implements ScalarFunction1d {

    private DecreasingNormedScalarFunction1d leftFunction;
    private DecreasingNormedScalarFunction1d rightFunction;
    private double m;
    private double a;
    private double b;

    public QuasiLRFuzzyMembership(double m, double a, double b) {
        this.m = m;
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
        this.m = alphaCutSets[alphaCutSets.length - 1].getMax();
        this.a = alphaCutSets[alphaCutSets.length - 1].getMax() - alphaCutSets[0].getMin();
        this.b = alphaCutSets[0].getMax() - alphaCutSets[alphaCutSets.length - 1].getMax();
    }

    public QuasiLRFuzzyMembership(final AlphaCutSet[] alphaCutSets) {
        this.addAlphaCuts(alphaCutSets);
    }

    public QuasiLRFuzzyMembership(DecreasingNormedScalarFunction1d leftFunction, DecreasingNormedScalarFunction1d rightFunction,
            double m, double a, double b) {
        if ((a < 0) || (b < 0)) {
            throw new IllegalArgumentException("alphaL and alphaR must be positive real numbers");
        }
        this.leftFunction = leftFunction;
        this.rightFunction = rightFunction;
        this.m = m;
        this.a = a;
        this.b = b;
    }

    @Override
    public double getValue(double x) {
        if (x == this.m) {
            return 1.;
        }
        if ((x < this.m) && (x > this.m - this.a)) {
            return this.leftFunction.getValue((this.m - x) / this.a);
        }
        if ((x > this.m) && (x < this.m + this.b)) {
            return this.rightFunction.getValue((x - this.m) / this.b);
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
        final QuasiLRFuzzyMembership other = (QuasiLRFuzzyMembership) obj;
        if (this.m != other.m) {
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
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.m) ^ (Double.doubleToLongBits(this.m) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.a) ^ (Double.doubleToLongBits(this.a) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.b) ^ (Double.doubleToLongBits(this.b) >>> 32));
        return hash;
    }

    public AlphaCutSet getAlphaCutSet(double alpha) {
        if ((alpha < this.leftFunction.getFxMin()) && (alpha < this.rightFunction.getFxMin())) {
            return new AlphaCutSet(alpha,
                    this.m - this.leftFunction.getInverseFunctionValue(this.leftFunction.getFxMin()) * this.a,
                    this.m + this.rightFunction.getInverseFunctionValue(this.rightFunction.getFxMin()) * this.b);
        }

        if (alpha < this.leftFunction.getFxMin()) {
            return new AlphaCutSet(alpha,
                    this.m - this.leftFunction.getInverseFunctionValue(this.leftFunction.getFxMin()) * this.a,
                    this.m + this.rightFunction.getInverseFunctionValue(alpha) * this.b);
        }

        if (alpha < this.rightFunction.getFxMin()) {
            return new AlphaCutSet(alpha,
                    this.m - this.leftFunction.getInverseFunctionValue(alpha) * this.a,
                    this.m + this.rightFunction.getInverseFunctionValue(this.rightFunction.getFxMin()) * this.b);
        }
        return new AlphaCutSet(alpha,
                this.m - this.leftFunction.getInverseFunctionValue(alpha) * this.a,
                this.m + this.rightFunction.getInverseFunctionValue(alpha) * this.b);
    }

    /**
     * @return the m
     */
    public double getM() {
        return m;
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
     * @param m the m to set
     */
    void setM(double m) {
        this.m = m;
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

    public QuasiLRFuzzyMembership concentrate(final double c) {
        return new QuasiLRFuzzyMembership(new DecreasingNormedScalarFunction1d(0., 1.) {

            @Override
            public double getValue(double x) {
                return Math.pow(leftFunction.getValue(x), c);
            }

            @Override
            public double getInverseFunctionValue(double fx) {
                return leftFunction.getInverseFunctionValue(Math.pow(fx, 1. / c));
            }
        }, new DecreasingNormedScalarFunction1d(0., 1.) {

            @Override
            public double getValue(double x) {
                return Math.pow(rightFunction.getValue(x), c);
            }

            @Override
            public double getInverseFunctionValue(double fx) {
                return rightFunction.getInverseFunctionValue(Math.pow(fx, 1. / c));
            }
        }, this.m, this.a, this.b);
    }

}
