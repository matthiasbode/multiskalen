/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.operation;

import fuzzy.number.discrete.FuzzyFactory;

/**
 *
 * @author bode, brandt
 */
public class FuzzyDummyOperation extends FuzzyOperation{
    public boolean  start = true;
    
    
    public FuzzyDummyOperation(int resolution, boolean start) {
        super(FuzzyFactory.createLinearInterval(0., 0., 0., 0., resolution),0.);
        this.start = start;
    }
}
