/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzy.number.discrete;

/**
 *
 * @author bode
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author abuabed
 */
public class AlphaCutSet implements Cloneable {

    private double alpha;
    private double min;
    private double max;

    public AlphaCutSet(double alpha, double min, double max) {
        this.alpha = alpha;
        this.min = min;
        this.max = max;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Override
    public AlphaCutSet clone() {
        return new AlphaCutSet(this.alpha, this.min, this.max);
    }

    public AlphaCutSet add(AlphaCutSet point) {
        return new AlphaCutSet(this.alpha, this.min + point.min, this.max + point.max);
    }

    public AlphaCutSet mult(AlphaCutSet point) {
        double newc1 = Math.min(Math.min(this.getMin() * point.getMin(), this.getMin() * point.getMax()), Math.min(this.getMax() * point.getMin(), this.getMax() * point.getMax()));
        double newc2 = Math.max(Math.max(this.getMin() * point.getMin(), this.getMin() * point.getMax()), Math.max(this.getMax() * point.getMin(), this.getMax() * point.getMax()));
        return new AlphaCutSet(this.alpha, newc1, newc2);
    }

    public AlphaCutSet div(AlphaCutSet point) {
        double newc1 = Math.min(Math.min(this.getMin() / point.getMin(), this.getMin() / point.getMax()), Math.min(this.getMax() / point.getMin(), this.getMax() / point.getMax()));
        double newc2 = Math.max(Math.max(this.getMin() / point.getMin(), this.getMin() / point.getMax()), Math.max(this.getMax() / point.getMin(), this.getMax() / point.getMax()));
        return new AlphaCutSet(this.alpha, newc1, newc2);
    }

    public AlphaCutSet sub(AlphaCutSet point) {
        double a = this.min - point.max;
        double b = this.max - point.min;
        return new AlphaCutSet(this.alpha, a, b);
    }

    public AlphaCutSet mult(double scalar) {
        return new AlphaCutSet(alpha, min * scalar, max * scalar);
    }

    @Override
    public String toString() {
        return "Alpha = " + this.alpha
                + " Min = " + this.min + " Max = " + this.max;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AlphaCutSet other = (AlphaCutSet) obj;

        if (this.alpha != other.alpha) {
            return false;
        }
        if (this.min != other.min) {
            return false;
        }
        if (this.max != other.max) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.alpha) ^ (Double.doubleToLongBits(this.alpha) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));

        return hash;
    }

}
