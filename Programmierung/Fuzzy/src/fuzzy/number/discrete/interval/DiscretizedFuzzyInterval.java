/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.number.discrete.interval;

import fuzzy.number.discrete.AlphaCutSet;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class DiscretizedFuzzyInterval extends FuzzyInterval {

    private final AlphaCutSet[] alphaCutSets;

    public DiscretizedFuzzyInterval(final AlphaCutSet[] alphaCutSets) {
        super(new QuasiLRIntervalMembership(alphaCutSets));
        this.alphaCutSets = alphaCutSets;
    }

    public AlphaCutSet getAlphaCutSet(int numAlphaCut) {
        if (numAlphaCut > this.alphaCutSets.length - 1) {
            throw new IllegalArgumentException("there is only " + this.alphaCutSets.length + " alphaCutSets");
        }
        return this.alphaCutSets[numAlphaCut];
    }

    @Override
    public QuasiLRIntervalMembership getMembershipFunction() {
        return (QuasiLRIntervalMembership) super.getMembershipFunction(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getNumberOfAlphaCuts() {
        return this.alphaCutSets.length;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy;HH:mm:ss");
        String s = new String();
//        for (int i = 0; i < this.alphaCutSets.length; i++) {
//            s = s.concat(this.alphaCutSets[i].getAlpha() + ", " + this.alphaCutSets[i].getMin() + ", " + this.alphaCutSets[i].getMax() + "\n");
//        }
        s += format.format(new Date((long) this.getC1())).concat("\t" + format.format(new Date((long) this.getM1()))).concat("\t" + format.format(new Date((long) this.getM2()))).concat("\t" + format.format(new Date((long) this.getC2())));
        s += "\n" + ((long) this.getC1());
        s += "\t" + ((long) this.getM1());
        s += "\t" + ((long) this.getM2());
        s += "\t" + ((long) this.getC2());
        return s;
    }

    @Override
    public DiscretizedFuzzyInterval mult(double scalar) {
        AlphaCutSet[] multAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
        for (int i = 0; i < this.alphaCutSets.length; i++) {
            multAlphaCutSets[i] = this.alphaCutSets[i].mult(scalar);
        }
        return new DiscretizedFuzzyInterval(multAlphaCutSets);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiscretizedFuzzyInterval other = (DiscretizedFuzzyInterval) obj;
        if (!Arrays.deepEquals(this.alphaCutSets, other.alphaCutSets)) {
            return false;
        }
        return true;
    }

    @Override
    public double getMean() {
        return (getAlphaCutSet(getNumberOfAlphaCuts() - 1).getMax() + getAlphaCutSet(getNumberOfAlphaCuts() - 1).getMin()) / 2.;
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
    public DiscretizedFuzzyInterval clone() {
        AlphaCutSet[] newSet = new AlphaCutSet[alphaCutSets.length];
        for (int i = 0; i < alphaCutSets.length; i++) {
            newSet[i] = alphaCutSets[i].clone();
        }
        return new DiscretizedFuzzyInterval(newSet);
    }

    @Override
    public DiscretizedFuzzyInterval add(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval point = (DiscretizedFuzzyInterval) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].add(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyInterval(addAlphaCutSets);
        }
        throw new UnsupportedOperationException("Momentan nur Operationen mit DiscreteFuzzyIntervals möglich");
    }

    @Override
    public DiscretizedFuzzyInterval mult(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval point = (DiscretizedFuzzyInterval) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].mult(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyInterval(addAlphaCutSets);
        }
        throw new UnsupportedOperationException("Momentan nur Operationen mit DiscreteFuzzyIntervals möglich");
    }

    @Override
    public DiscretizedFuzzyInterval sub(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval point = (DiscretizedFuzzyInterval) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].sub(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyInterval(addAlphaCutSets);
        }
        throw new UnsupportedOperationException("Momentan nur Operationen mit DiscreteFuzzyIntervals möglich");
    }

    public DiscretizedFuzzyInterval interactiveSub(DiscretizedFuzzyInterval other) {
        AlphaCutSet[] thisCuts = this.getAlphaCutSets();
        AlphaCutSet[] otherCuts = other.getAlphaCutSets();
        AlphaCutSet[] result = new AlphaCutSet[thisCuts.length];

        for (int i = 0; i < result.length; i++) {
            AlphaCutSet tA = thisCuts[i];
            AlphaCutSet oA = otherCuts[i];
            double a = tA.getMin() - oA.getMin();
            double b = tA.getMax() - oA.getMax();
            result[i] = new AlphaCutSet(tA.getAlpha(), Math.min(a, b), Math.max(a, b));
        }
        return new DiscretizedFuzzyInterval(result);
    }

    @Override
    public DiscretizedFuzzyInterval div(FuzzyNumber b) {
        if (b instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval point = (DiscretizedFuzzyInterval) b;
            AlphaCutSet[] addAlphaCutSets = new AlphaCutSet[this.alphaCutSets.length];
            for (int i = 0; i < alphaCutSets.length; i++) {
                addAlphaCutSets[i] = this.alphaCutSets[i].div(point.getAlphaCutSet(i));
            }
            return new DiscretizedFuzzyInterval(addAlphaCutSets);
        }
        throw new UnsupportedOperationException("Momentan nur Operationen mit DiscreteFuzzyIntervals möglich");
    }

    @Override
    public DiscretizedFuzzyInterval negate() {
        return this.mult(-1.);
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
        return compareTo(f) > 0;
    }

    @Override
    public boolean isLowerThan(FuzzyNumber f) {
        return compareTo(f) < 0;
    }

    public double getLeftIntegral(double lambda) {
        double me_ = this.getC1();
        double ml_ = this.getMembershipFunction().getAlphaCutSet(lambda).getMin();
        double m_ = this.getMembershipFunction().getM1();
        return 0.5 * (ml_ + m_ + (me_ - m_) * lambda);
    }

    public double getRightIntegral(double lambda) {
        double meO = this.getC2();
        double mlO = this.getMembershipFunction().getAlphaCutSet(lambda).getMax();
        double mO = this.getMembershipFunction().getM2();
        return 0.5 * (mlO + mO + (meO - mO) * lambda);
    }

    @Override
    public int compareTo(FieldElement f) {
        if (f instanceof DiscretizedFuzzyInterval) {
            DiscretizedFuzzyInterval df = (DiscretizedFuzzyInterval) f;
            /**
             * Annahme, dass lambda = beta = 0.5 gilt Siehe Wang(2004) A fuzzy
             * robust scheduling approach for product development projects
             */
            double thisIntegral = 0.5 * getLeftIntegral(0.5) + 0.5 * getRightIntegral(0.5);
            double fIntegral = 0.5 * df.getLeftIntegral(0.5) + 0.5 * df.getRightIntegral(0.5);
            if (Double.isNaN(thisIntegral) || Double.isNaN(fIntegral)) {
                return Double.compare(this.doubleValue(), f.doubleValue());
            }
            return Double.compare(thisIntegral, fIntegral);

        }
        throw new UnsupportedOperationException("Kein Vergleich möglich."); //To change body of generated methods, choose Tools | Templates.

//        if (f instanceof DiscretizedFuzzyInterval) {
//            DiscretizedFuzzyInterval o = (DiscretizedFuzzyInterval) f;
//
//            // die Zahlen sind gleich!
//            if (this.equals(o)) {
//                return 0;
//            }
//            // die Mitten sind entscheidend
//            if (this.getMean() < o.getMean()) {
//                return -1;
//            }
//            if (this.getMean() < o.getMean()) {
//                return 1;
//            }
//            // wenn die mitten festgelegt sind, entscheiden c1 und c2
//            if ((this.getC1() < o.getC1()) && (this.getC2() < o.getC2())) {
//                return -1;
//            }
//            if ((this.getC1() > o.getC1()) && (this.getC2() > o.getC2())) {
//                return 1;
//            }
//        } else {
//            return Long.compare(this.longValue(), f.longValue());
//        }
//        throw new UnsupportedOperationException("Kein Vergleich möglich."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getM1() {
        return getAlphaCutSet(getNumberOfAlphaCuts() - 1).getMin();
    }

    @Override
    public double getM2() {
        return getAlphaCutSet(getNumberOfAlphaCuts() - 1).getMax();
    }

    public AlphaCutSet[] getAlphaCutSets() {
        return alphaCutSets;
    }

    public DiscretizedFuzzyInterval min(DiscretizedFuzzyInterval b) {

        if (this.getNumberOfAlphaCuts() == b.getNumberOfAlphaCuts()) {
            AlphaCutSet[] res = new AlphaCutSet[this.getNumberOfAlphaCuts()];
            for (int i = 0; i < this.getNumberOfAlphaCuts(); i++) {
                AlphaCutSet alphaCutSetA = this.getAlphaCutSet(i);
                AlphaCutSet alphaCutSetB = b.getAlphaCutSet(i);
                res[i] = new AlphaCutSet(alphaCutSetA.getAlpha(), Math.min(alphaCutSetA.getMin(), alphaCutSetB.getMin()), Math.min(alphaCutSetA.getMax(), alphaCutSetB.getMax()));
            }
            return new DiscretizedFuzzyInterval(res);
        }
        return null;
    }

    public static DiscretizedFuzzyInterval max(DiscretizedFuzzyInterval a, DiscretizedFuzzyInterval b) {
        if (a.getNumberOfAlphaCuts() == b.getNumberOfAlphaCuts()) {
            AlphaCutSet[] res = new AlphaCutSet[a.getNumberOfAlphaCuts()];
            for (int i = 0; i < a.getNumberOfAlphaCuts(); i++) {
                AlphaCutSet alphaCutSetA = a.getAlphaCutSet(i);
                AlphaCutSet alphaCutSetB = b.getAlphaCutSet(i);
                res[i] = new AlphaCutSet(alphaCutSetA.getAlpha(), Math.max(alphaCutSetA.getMin(), alphaCutSetB.getMin()), Math.max(alphaCutSetA.getMax(), alphaCutSetB.getMax()));
            }
            return new DiscretizedFuzzyInterval(res);
        }
        return null;
    }

}
