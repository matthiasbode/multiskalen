/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gantt;

import applications.mmrcsp.model.operations.Operation;
import com.flexganttfx.model.activity.MutableActivityBase;
import java.time.Instant;

/**
 *
 * @author Matthias
 */
public class GanttOperation extends MutableActivityBase<Operation> {

    public GanttOperation(Operation operation, long start) {
        setUserObject(operation);
        setName(operation.toString());
        setStartTime(Instant.ofEpochMilli(start));
        setEndTime(Instant.ofEpochMilli(start + operation.getDuration().longValue()));
    }
}
