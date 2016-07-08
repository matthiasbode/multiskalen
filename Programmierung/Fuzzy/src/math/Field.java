/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import fuzzy.number.discrete.AlphaCutSet;
import fuzzy.number.discrete.DiscretizedFuzzyNumber;
import fuzzy.number.discrete.FuzzyFactory;

/**
 *
 * @author bode
 */
public class Field {

    public static <V extends FieldElement> V convert(FieldElement value, Class<?> cls) {
        if (cls.equals(DoubleValue.class)) {
            return (V) new DoubleValue(value.doubleValue());
        }
        if (cls.equals(LongValue.class)) {
            return (V) new LongValue(value.longValue());
        }
        return null;
    }

    public static FieldElement getNullElement(Class<?> cls) {
        if (cls.equals(DoubleValue.class)) {
            return new DoubleValue(0);
        }
        if (cls.equals(LongValue.class)) {
            return new LongValue(0L);
        }
        if (cls.equals(DiscretizedFuzzyInterval.class)) {
            return FuzzyFactory.createCrispValue(0);
        }
        return null;
    }

    public static FieldElement getEinsElement(Class<?> cls) {
        if (cls.equals(DoubleValue.class)) {
            return new DoubleValue(1);
        }
        if (cls.equals(LongValue.class)) {
            return new LongValue(1L);
        }
        if (cls.equals(DiscretizedFuzzyInterval.class)) {
            return FuzzyFactory.createCrispValue(1);
        }
        return null;
    }

    public static FieldElement min(FieldElement a, FieldElement b) {
            return a.isGreaterThan(b) ? b : a;
    }

    public static FieldElement max(FieldElement a, FieldElement b) {
            return a.isGreaterThan(b) ? a : b;
    }
}
