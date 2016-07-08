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
public class MaximumTimeLag extends Restriction {

    private Operation o1;
    private Operation o2;

    public MaximumTimeLag(Operation o1, Operation o2, FieldElement d_ij_max) {
        super(new LongValue(-1L), new LongValue(1L), d_ij_max);
        this.o1 = o1;
        this.o2 = o2;
    }

    public Operation getO1() {
        return o1;
    }

    public Operation getO2() {
        return o2;
    }

}
