/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.lcs.poly;

import applications.mmrcsp.model.operations.Operation;
import struct.Polytope;

/**
 *
 * @author behrensd
 */
public class PolytopeOperation {

    private Polytope polytope;
    Operation operation;

    public PolytopeOperation(Polytope polytope, Operation operation) {
        if (polytope == null) {
            throw new IllegalArgumentException("Kein Polytop bestimmt");
        }
        this.polytope = polytope;
        this.operation = operation;
    }

    public Polytope getPolytope() {
        return polytope;
    }

    public Operation getOperation() {
        return operation;
    }

}
