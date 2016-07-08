package org.util;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author Nils Rinke
 */
public class DiscreteSet<T> extends HashSet<T> {

    public DiscreteSet() {
        super();
    }

    public DiscreteSet(Collection<? extends T> collection) {
        super(collection);
    }


    public DiscreteSet<T> section(DiscreteSet<T> set) {
        DiscreteSet<T> section = new DiscreteSet<T>();
        for (T t : set) {
            if(this.contains(t))
                section.add(t);
        }
        return section;
    }


    public DiscreteSet<T> union(DiscreteSet<T> set) {
        DiscreteSet<T> union = new DiscreteSet<T>(this);
        union.addAll(set);
        return union;

    }


    public DiscreteSet<T> difference(DiscreteSet<T> set) {
        DiscreteSet<T> diff = new DiscreteSet<T>(this);
        for (T t : set) {
            diff.remove(t);
        }
        return diff;
    }
}
