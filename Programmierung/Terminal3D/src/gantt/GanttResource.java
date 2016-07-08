/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gantt;

import com.flexganttfx.model.Row;

/**
 *
 * @author Matthias
 */
public class GanttResource extends Row<GanttResource, GanttResource, GanttOperation> {

    public GanttResource(String name) {
        super(name);
    }
}
