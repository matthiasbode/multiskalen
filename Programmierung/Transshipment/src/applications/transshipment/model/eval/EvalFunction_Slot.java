/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.eval;

import applications.transshipment.model.structs.Slot;

/**
 *
 * @author bode
 */
public interface EvalFunction_Slot {

    public double evaluate(Slot sto);

}
