/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.graph.weighted;

/**
 *
 * @author Nils Rinke
 */
public interface VertexWeight<V, W> {
    
    public void setVertexWeight(V vertex, W weight);
    
    public W getVertexWeight(V vertex);  
    
}
