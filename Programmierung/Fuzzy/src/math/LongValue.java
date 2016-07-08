/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

/**
 *
 * @author bode
 */
public class LongValue implements FieldElement<LongValue> {

    private long value;

    public LongValue(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (this.value ^ (this.value >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LongValue other = (LongValue) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public LongValue add(LongValue b) {
        return new LongValue(this.value + b.longValue());
    }

    @Override
    public LongValue mult(LongValue b) {
        return new LongValue(this.value * b.longValue());
    }

    @Override
    public LongValue sub(LongValue b) {
        return new LongValue(this.value - b.longValue());
    }

    @Override
    public LongValue div(LongValue b) {
        return new LongValue(this.value / b.longValue());
    }

    @Override
    public LongValue mult(double s) {
        return new LongValue((long) (this.value * s));
    }

    @Override
    public LongValue negate() {
        return new LongValue(-this.value);
    }

    @Override
    public double doubleValue() {
        return (double) value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public boolean isGreaterThan(LongValue f) {
        return this.longValue() > f.longValue();
    }

    @Override
    public boolean isLowerThan(LongValue f) {
        return this.longValue() < f.longValue();
    }

    @Override
    public int compareTo(FieldElement o) {
        return Long.compare(value, o.longValue());
    }

    @Override
    public LongValue clone() {
        return new LongValue(value);
    }

}
