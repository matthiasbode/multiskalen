/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

/**
 *
 * @author bode
 */
public class DoubleValue implements FieldElement<DoubleValue> {

    private double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DoubleValue other = (DoubleValue) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public DoubleValue add(DoubleValue b) {
        return new DoubleValue(this.value + b.doubleValue());
    }

    @Override
    public DoubleValue mult(DoubleValue b) {
        return new DoubleValue(this.value * b.doubleValue());
    }

    @Override
    public DoubleValue sub(DoubleValue b) {
        return new DoubleValue(this.value - b.doubleValue());
    }

    @Override
    public DoubleValue div(DoubleValue b) {
        return new DoubleValue(this.value / b.doubleValue());
    }

    @Override
    public DoubleValue mult(double s) {
        return new DoubleValue(this.value * s);
    }

    @Override
    public DoubleValue negate() {
        return new DoubleValue(-this.value);
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return (long) this.value;
    }

    @Override
    public boolean isGreaterThan(DoubleValue f) {
        return this.doubleValue() > f.doubleValue();
    }

    @Override
    public boolean isLowerThan(DoubleValue f) {
        return this.doubleValue() < f.doubleValue();
    }

    @Override
    public int compareTo(FieldElement o) {
        return Double.compare(this.doubleValue(), o.doubleValue());
    }

    @Override
    public DoubleValue clone() {
        return new DoubleValue(value);
    }

}
