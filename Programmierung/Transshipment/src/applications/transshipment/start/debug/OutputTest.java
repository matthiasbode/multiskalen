/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start.debug;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.resources.ResourceImplementation;
import java.io.File;
import math.DoubleValue;
import math.LongValue;
import math.function.StepFunction;
import util.bucketing.Bucket;
import util.bucketing.Bucketing;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class OutputTest {

    public static void main(String[] args) {
        Bucketing b = new Bucketing(new ResourceImplementation("dupp"), new StepFunction(new LongValue(0), new LongValue(1), new DoubleValue(5.0)), TimeSlot.create(0, 500), 10);
        JSONSerialisierung.exportJSON(new File("C:\\Users\\Bode\\Documents\\blub.json"), b, true);
    }
}
