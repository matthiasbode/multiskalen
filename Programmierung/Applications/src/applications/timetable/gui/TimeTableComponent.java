/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.Grade;
import applications.timetable.model.ProblemDefinition;
import applications.timetable.model.TimeTableMatrix.TimeTable;
import applications.timetable.model.TimeTableMatrix.TimeTableElement;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author bode
 */
public class TimeTableComponent extends JPanel {

     
    ProblemDefinition p;
   

    public TimeTableComponent(TimeTable t, ProblemDefinition p) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (Grade grade : p.getModules().keySet()) {
            JLabel jl = new JLabel("<html><h1>"+grade+"</h1></html>");
            this.add(jl);
            TimeTableJTableModel jmodel = new TimeTableJTableModel(grade, p, t);
            JTable table = new JTable(jmodel);
            table.setRowHeight(60);
            JScrollPane jsp = new JScrollPane(table);
            this.add(jsp);
        }
    }

    public static class RendererMehrzeilig
            extends JTextArea implements TableCellRenderer {

        public RendererMehrzeilig() {
            this.setEditable(false);
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setText(value.toString());
            return this;
        }
    }

    public TimeTableComponent(TimeTable t, Grade g, ProblemDefinition p) {
        super(new BorderLayout());
        TimeTableJTableModel jmodel = new TimeTableJTableModel(g, p, t);
        this.p = jmodel.getProblem();
        JTable table = new JTable(jmodel);
        table.setPreferredSize(new Dimension(600, 200));
        JScrollPane jsp = new JScrollPane(table);
        this.add(jsp, BorderLayout.CENTER);
    }

    
}
