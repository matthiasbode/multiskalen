/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.restrictions.instances;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.restrictions.Restriction;
import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class MinimumTimeLag extends Restriction {

    private Operation o1;
    private Operation o2;

    public MinimumTimeLag(Operation o1, Operation o2, FieldElement d_ij_min) {
        super(new LongValue(1), new LongValue(-1), d_ij_min.negate());
        this.o1 = o1;
        this.o2 = o2;
    }

    public Operation getO1() {
        return o1;
    }

    public Operation getO2() {
        return o2;
    }

    @Override
    public String toString() {
        return "MinimumTimeLag{"+o1.getId() +"-->" + o2.getId()+":" + getB() +"}";
    }
    
    
}
