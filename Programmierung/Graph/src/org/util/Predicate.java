/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.util;

/**
 * Predicate is an interface for dividing collections of the same type into
 * partitions. Each element in a partition has the same predicate.
 * 
 * For now, it is not used because of the predicate implementation in
 * com.google.common.base
 * 
 * @author rinke
 */
public interface Predicate<V> {

    /**
     * Evaluates this object, if it's part of a partition with this specified
     * predicate.
     * 
     * @param object
     * @return true, if object has the same predicate, false otherwise. 
     */
    public boolean apply(V object);
}