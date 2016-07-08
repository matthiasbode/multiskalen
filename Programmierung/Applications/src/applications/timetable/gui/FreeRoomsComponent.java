/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.ProblemDefinition;
import applications.timetable.model.Room;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author bode
 */
public class FreeRoomsComponent extends JScrollPane {

    JTable table;
    PeriodJTableModel jmodel;
    ProblemDefinition p;

    public FreeRoomsComponent(PeriodJTableModel jmodel) {
        this.jmodel = jmodel;
        this.p = jmodel.getProblem();
        table = new JTable(jmodel);
        table.setPreferredSize(new Dimension(600, 200));
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn lectureColumn = table.getColumnModel().getColumn(i);
            lectureColumn.setCellEditor(new ArrayEditor<Room>(p.getRooms()));
        }

        this.setViewportView(table);
    }
}
