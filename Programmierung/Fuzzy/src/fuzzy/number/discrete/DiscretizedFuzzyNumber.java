package fuzzy.number.discrete;

import bijava.math.function.ScalarFunction1d;
import fuzzy.number.FuzzyNumber;
import java.util.Arrays;
import math.FieldElement;

/**
 *
 * @author abuabed, bode
 */
public class DiscretizedFuzzyNumber extends FuzzyNumber {

    private AlphaCutSet[] alphaCutSets;

    public DiscretizedFuzzyNumber(double m, double alpha, double beta, int resolution) {
        super(new QuasiLRFuzzyMembership(m, alpha, beta));

        if ((alpha < 0) || (beta < 0)) {
            throw new IllegalArgumentException("alphaL and alphaR must be positive real numbers");
        }

        /**
         * Lineares Interpolieren für linken Ast
         */
        double xL = m - alpha;
        double xR = m + beta;
        double dxL = alpha / resolution;
        double dxR = beta / resolution;

        this.alphaCutSets = new AlphaCutSet[resolution + 1];
        for (int i = 0; i < resolution + 1; i++) {
            double niveau = (1.0 / resolution) * i;
            double min = xL + dxL * i;
            double max = xR - dxR * i;
            alphaCutSets[i] = new AlphaCutSet(niveau, min, max);
        }
        QuasiLRFuzzyMembership member = (QuasiLRFuzzyMembership) super.membership;
        member.addAlphaCuts(alphaCutSets);
    }

    public DiscretizedFuzzyNumber(final AlphaCutSet[] alphaCutSets) {
        super(new QuasiLRFuzzyMembership(alphaCutSets));
        this.alphaCutSets = alphaCutSets;
    }

    public AlphaCutSet getAlphaCutSet(int numAlphaCut) {
        if (numAlphaCut > this.alphaCutSets.length - 1) {
            throw new IllegalArgumentException("there is only " + this.alphaCutSets.length + " alphaCutSets");
        }
        return this.alphaCutSets[numAlphaCut];
    }

    public int getNumberOfAlphaCuts() {
        return this.alphaCutSets.length;
    }

    @Override
    public String toString() {
        String s = new String();
        for (int i = 0; i < this.alphaCutSets.length; i++) {
            s = s.concat(this.alphaCutSets[i].getAlpha() + ", " + this.alphaCutSets[i].getMin() + ", " + this.alphaCutSets[i].getMax() + "\n");
        }
        return s;
    }

    @Override
    public DiscretizedFuzzyNumber mult(double scalar) {
        AlphaCutSet[] multAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
        for (int i = 0; i < this.alphaCutSets.length; i++) {
            multAlphaCutSets[i] = this.alphaCutSets[i].mult(scalar);
        }
        return new DiscretizedFuzzyNumber(multAlphaCutSets);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiscretizedFuzzyNumber other = (DiscretizedFuzzyNumber) obj;
        if (!Arrays.deepEquals(this.alphaCutSets, other.alphaCutSets)) {
            return false;
        }
        return true;
    }

    @Override
    public double getMean() {
        return getAlphaCutSet(getNumberOfAlphaCuts() - 1).getMax();
    }

    @Override
    public double getC1() {
        return getAlphaCutSet(0).getMin();
    }

    @Override
    public double getC2() {
        return getAlphaCutSet(0).getMax();
    }

    @Override
    public DiscretizedFuzzyNumber clone() {
        AlphaCutSet[] newSet = new AlphaCutSet[alphaCutSets.length];
        for (int i = 0; i < alphaCutSets.length; i++) {
            newSet[i] = alphaCutSets[i].clone();
        }
        return new DiscretizedFuzzyNumber(newSet);
    }

    @Override
    public DiscretizedFuzzyNumber add(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyNumber) {
            DiscretizedFuzzyNumber point = (DiscretizedFuzzyNumber) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].add(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyNumber(addAlphaCutSets);
        }
        return null;
    }

    @Override
    public DiscretizedFuzzyNumber mult(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyNumber) {
            DiscretizedFuzzyNumber point = (DiscretizedFuzzyNumber) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].mult(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyNumber(addAlphaCutSets);
        }
        return null;
    }

    @Override
    public DiscretizedFuzzyNumber sub(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyNumber) {
            DiscretizedFuzzyNumber point = (DiscretizedFuzzyNumber) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].sub(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyNumber(addAlphaCutSets);
        }
        return null;
    }

    @Override
    public DiscretizedFuzzyNumber div(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyNumber) {
            DiscretizedFuzzyNumber point = (DiscretizedFuzzyNumber) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].div(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyNumber(addAlphaCutSets);
        }
        return null;
    }

    @Override
    public DiscretizedFuzzyNumber negate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double doubleValue() {
        return this.getMean();
    }

    @Override
    public long longValue() {
        return (long) this.getMean();
    }

    @Override
    public boolean isGreaterThan(FuzzyNumber f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLowerThan(FuzzyNumber f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(FieldElement o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public DiscretizedFuzzyNumber min(DiscretizedFuzzyNumber b) {

        if (this.getNumberOfAlphaCuts() == b.getNumberOfAlphaCuts()) {
            AlphaCutSet[] res = new AlphaCutSet[this.getNumberOfAlphaCuts()];
            for (int i = 0; i < this.getNumberOfAlphaCuts(); i++) {
                AlphaCutSet alphaCutSetA = this.getAlphaCutSet(i);
                AlphaCutSet alphaCutSetB = b.getAlphaCutSet(i);
                res[i] = new AlphaCutSet(alphaCutSetA.getAlpha(), Math.min(alphaCutSetA.getMin(), alphaCutSetB.getMin()), Math.min(alphaCutSetA.getMax(), alphaCutSetB.getMax()));
            }
            return new DiscretizedFuzzyNumber(res);
        }
        return null;
    }

    public DiscretizedFuzzyNumber max(DiscretizedFuzzyNumber b) {

        if (this.getNumberOfAlphaCuts() == b.getNumberOfAlphaCuts()) {
            AlphaCutSet[] res = new AlphaCutSet[this.getNumberOfAlphaCuts()];
            for (int i = 0; i < this.getNumberOfAlphaCuts(); i++) {
                AlphaCutSet alphaCutSetA = this.getAlphaCutSet(i);
                AlphaCutSet alphaCutSetB = b.getAlphaCutSet(i);
                res[i] = new AlphaCutSet(alphaCutSetA.getAlpha(), Math.max(alphaCutSetA.getMin(), alphaCutSetB.getMin()), Math.max(alphaCutSetA.getMax(), alphaCutSetB.getMax()));
            }
            return new DiscretizedFuzzyNumber(res);
        }
        return null;
    }

}
