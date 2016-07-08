/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package math.function;

import applications.mmrcsp.model.basics.TimeSlot;
import bijava.math.function.ScalarFunction1d;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;
import math.DoubleValue;
import math.Field;
import math.FieldElement;
import math.LongValue;

/**
 * Treppenfunktion. Es gilt für die Stützstellen -> alle Werte, die
 * größer/gleich diesem Wert sind, bekommen als Rückgabewert genau den Wert der
 * Stützstelle.
 *
 * @author bode
 */
public class StepFunction implements Cloneable, ScalarFunction1d {

    private TreeMap<FieldElement, FieldElement> values;
    final Class<?> classKeys;
    final Class<?> classValues;

    public StepFunction(TreeMap<FieldElement, FieldElement> values) {
        this.values = new TreeMap<>(values);
        classKeys = values.keySet().iterator().next().getClass();
        classValues = values.keySet().iterator().next().getClass();
    }

    public StepFunction(FieldElement from, FieldElement to, FieldElement value) {
        this.values = new TreeMap<>();
        this.values.put(from, value);
        this.values.put(to, Field.getNullElement(value.getClass()));
        classKeys = from.getClass();
        classValues = value.getClass();
    }

    public StepFunction(StepFunction f) {
        this.values = new TreeMap<>(f.values);
        this.classKeys = f.classKeys;
        this.classValues = f.classValues;
    }

    @Override
    public StepFunction clone() throws CloneNotSupportedException {
        return new StepFunction(this);
    }

    public StepFunction(final Class<?> classKeys, final Class<?> classValues) {
        values = new TreeMap<>();
        this.classKeys = classKeys;
        this.classValues = classValues;
    }

    public FieldElement getValue(FieldElement t) {
        if (values.isEmpty()) {
            return new DoubleValue(0);
        }
//        if (values.containsKey(t)) {
//            return values.get(t);
//        }

        /*
         * Der größte Wert, der kleiner ist als der angefragte
         */
        Entry<FieldElement, FieldElement> floorEntry = values.floorEntry(t);
        if (floorEntry == null) {
            return new DoubleValue(0);
        }
        return floorEntry.getValue();

    }

    public StepFunction getFunctionFrom(FieldElement currentTime) {
        FieldElement value = getValue(currentTime);
        TreeMap<FieldElement, FieldElement> subMap = new TreeMap<>(values.subMap(currentTime, true, values.lastKey(), true));
        subMap.put(currentTime, value);
        return new StepFunction(subMap);
    }

    public StepFunction getFunction(FieldElement from, FieldElement to) {
        if (from.equals(to)) {
            StepFunction stepFunction = new StepFunction(this.classKeys, this.classValues);
            FieldElement value = this.getValue(from);
            stepFunction.addSamplingPoint(from, value);
            return stepFunction;
        }
        FieldElement fromValue = getValue(from);
        FieldElement toValue = Field.getNullElement(fromValue.getClass());

        TreeMap<FieldElement, FieldElement> subMap = new TreeMap<>(values.subMap(from, true, to, true));

        subMap.put(from, fromValue);
        subMap.put(to, toValue);
        return new StepFunction(subMap);
    }

    public Collection<FieldElement> getSamplingPoints() {
        return values.keySet();
    }

    public Entry<FieldElement, FieldElement> getMin(FieldElement from, FieldElement to) {
        StepFunction function = getFunction(from, to);
        Entry<FieldElement, FieldElement> min = function.getMin();
        return min;
    }

    public Entry<FieldElement, FieldElement> getMin() {
        if (values.isEmpty()) {
            return null;
        }
        FieldElement minKey = values.firstKey();
        FieldElement minValue = values.get(minKey);
        for (FieldElement fieldElement : values.keySet()) {
            if (values.lastKey().equals(fieldElement)) {
                continue;
            }
            FieldElement currentValue = values.get(fieldElement);
            if (values.get(fieldElement).isLowerThan(minValue)) {
                minKey = fieldElement;
                minValue = currentValue;
            }
        }
        return new ExtremumEntry(minKey, minValue);
    }

    public Entry<FieldElement, FieldElement> getMax() {
        if (values.isEmpty()) {
            return new AbstractMap.SimpleEntry<FieldElement, FieldElement>(new LongValue(0), new DoubleValue(0));
        }

        FieldElement maxValue = values.get(values.firstKey());
        FieldElement maxPos = values.firstKey();

        for (FieldElement key : values.keySet()) {
            FieldElement value = values.get(key);
            if (value.isGreaterThan(maxValue)) {
                maxPos = key;
                maxValue = value;

            }
        }
        return new AbstractMap.SimpleEntry<FieldElement, FieldElement>(maxPos, maxValue);
    }

    public void addSamplingPoint(FieldElement l, FieldElement d) {
        values.put(l, d);
    }

    public StepFunction add(StepFunction b) {
        if (this.values.isEmpty() && !b.values.isEmpty()) {
            return new StepFunction(b.values);
        }
        if (b.values.isEmpty()) {
            return new StepFunction(this.values);
        }
        StepFunction f = new StepFunction(this.classKeys, this.classValues);
        for (FieldElement x : this.values.keySet()) {
            FieldElement v = this.getValue(x).add(b.getValue(x));
            f.addSamplingPoint(x, v);
        }
        for (FieldElement x : b.values.keySet()) {
            if (!f.values.containsKey(x)) {
                FieldElement v = b.getValue(x).add(this.getValue(x));
                f.addSamplingPoint(x, v);
            }
        }
        return f;
    }

    public StepFunction sub(StepFunction b) {
        if (this.values.isEmpty() && !b.values.isEmpty()) {
            return new StepFunction(b.values);
        }
        if (b.values.isEmpty()) {
            return new StepFunction(this.values);
        }
        StepFunction f = new StepFunction(this.classKeys, this.classValues);
        for (FieldElement x : this.values.keySet()) {
            FieldElement v = this.getValue(x).sub(b.getValue(x));
            f.addSamplingPoint(x, v);
        }
        for (FieldElement x : b.values.keySet()) {
            if (!f.values.containsKey(x)) {
                FieldElement v = this.getValue(x).sub(b.getValue(x));
                f.addSamplingPoint(x, v);
            }
        }
        return f;
    }

    /**
     *
     * @param b
     * @return A linear approximization of the product of two functions
     */
    public StepFunction mult(StepFunction b) {
        if (this.values.isEmpty() || b.values.isEmpty()) {
            return new StepFunction(this.classKeys, this.classValues);
        }
        StepFunction f = new StepFunction(this.classKeys, this.classValues);
        for (FieldElement x : this.values.keySet()) {
            FieldElement v = this.getValue(x).mult(b.getValue(x));
            f.addSamplingPoint(x, v);
        }
        for (FieldElement x : b.values.keySet()) {
            if (!f.values.containsKey(x)) {
                FieldElement v = b.getValue(x).mult(this.getValue(x));
                f.addSamplingPoint(x, v);
            }
        }
        return f;
    }

//    public StepFunction mult(StepFunction b, FieldElement dt) {
//        if (this.values.isEmpty() || b.values.isEmpty()) {
//            return new StepFunction();
//        }
//        StepFunction f = new StepFunction();
//        FieldElement min = Math.min(this.getSamplingPoints().get(0), b.getSamplingPoints().get(0));
//        FieldElement max = Math.max(this.getSamplingPoints().get(values.size() - 1), b.getSamplingPoints().get(b.values.size() - 1));
//        for (long t = min; t < max; t += dt) {
//            double v = this.getValue(t) * b.getValue(t);
//            f.addSamplingPoint(t, v);
//        }
//        return f;
//    }
    public StepFunction mult(double d) {
        StepFunction f = new StepFunction(this.classKeys, this.classValues);
        for (FieldElement t : values.keySet()) {
            f.addSamplingPoint(t, values.get(t).mult(d));
        }
        return f;
    }

    public TreeMap<FieldElement, FieldElement> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StepFunction other = (StepFunction) obj;
        if (this.values != other.values && (this.values == null || !this.values.equals(other.values))) {
            return false;
        }
        return true;
    }

    public FieldElement getIntegral(FieldElement from, FieldElement to) {
        StepFunction function = getFunction(from, to);
        FieldElement sum = Field.getNullElement(classValues);
        for (FieldElement first : function.getSamplingPoints()) {
            FieldElement second = function.values.higherKey(first);
            if (second != null) {
                FieldElement dx = second.sub(first);
                FieldElement dx_ = Field.convert(dx, classValues);
                sum = sum.add(dx_.mult(getValue(first)));
            }
        }
//        for (int i = 0; i < function.getSamplingPoints().size() - 1; i++) {
//            FieldElement first = function.getSamplingPoints().get(i);
//            FieldElement second = function.getSamplingPoints().get(i + 1);
//            FieldElement dx = second.sub(first);
//            sum = sum.add(dx.mult(getValue(first)));
//        }
        return sum;
    }

    public ArrayList<Interval> getFreeSlots(FieldElement demand, FieldElement from) {
        ArrayList<Interval> result = new ArrayList<>();
        StepFunction function = this.getFunctionFrom(from);

        FieldElement currentStart = null;
        FieldElement currentEnd = null;
        for (FieldElement fieldElement : function.values.keySet()) {
            FieldElement value = this.getValue(fieldElement);

            if (value.isGreaterThan(demand) || value.equals(demand)) {
                if (currentStart == null) {
                    currentStart = fieldElement;
                }
            } else {
                currentEnd = fieldElement;
                if (currentStart != null && currentEnd != null) {
                    Interval ts = new Interval(currentStart, fieldElement);
                    result.add(ts);
                    currentStart = null;
                    currentEnd = null;
                }
            }
        }

        return result;
    }

    public ArrayList<Interval> getFreeSlots(FieldElement demand, TimeSlot interval) {
        ArrayList<Interval> result = new ArrayList<>();
        StepFunction function = this.getFunction(interval.getFromWhen(), interval.getUntilWhen());

        FieldElement currentStart = null;
        FieldElement currentEnd = null;
        for (FieldElement fieldElement : function.values.keySet()) {
            FieldElement value = this.getValue(fieldElement);

            if (value.isGreaterThan(demand) || value.equals(demand)) {
                if (currentStart == null) {
                    currentStart = fieldElement;
                }
            } else {
                currentEnd = fieldElement;
                if (currentStart != null && currentEnd != null) {
                    Interval ts = new Interval(currentStart, fieldElement);
                    result.add(ts);
                    currentStart = null;
                    currentEnd = null;
                }
            }
        }

        return result;
    }

    @Override
    public double getValue(double x) {
        return getValue(new DoubleValue(x)).doubleValue();
    }

    public static class Interval {

        public FieldElement from;
        public FieldElement to;

        public Interval(FieldElement from, FieldElement to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "Interval{" + "from=" + from + ", to=" + to + '}';
        }
    }

}
