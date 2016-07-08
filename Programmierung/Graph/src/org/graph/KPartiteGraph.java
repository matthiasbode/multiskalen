/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph;

import com.google.common.base.Predicate;
import java.util.Collection;

/**
 * In Anlehnung an "jung" (Java Universal Network/Graph Framework)
 * 
 * @author rinke
 */
public interface KPartiteGraph<V> extends Graph<V> {
    
    
    public Collection<V> getVertices(Predicate<V> predicate);
    
    public Collection<Predicate<V>> getPartitions();
}
