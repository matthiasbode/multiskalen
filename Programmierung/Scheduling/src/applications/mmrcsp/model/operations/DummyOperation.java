/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.operations;

import math.FieldElement;
import math.LongValue;

/**
 *
 * @author bode
 */
public class DummyOperation extends OperationImplementation {

    public boolean start = true;
    private int number;
    static int counter = 0;

    public DummyOperation(boolean start) {
        super(new LongValue(0));
        this.start = start;
        this.number = counter++;
    }

    public DummyOperation(int number, boolean start) {
        super(new LongValue(0));
        this.number = number;
        counter = number + 1;
        this.start = start;
    }

    @Override
    public String toString() {
        return "DummyOperation{" + number + '}';
    }

}
