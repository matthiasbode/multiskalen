/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.Lecturer;
import applications.timetable.model.Module;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author bode
 */
public class ModuleComponent extends JComponent{
    private Module module;
    private JTable lessons;
    private ArrayList<Lecturer> allLecturers;
    public ModuleComponent(Module module, ArrayList<Lecturer> allLecturers) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.module = module;
        JLabel title = new JLabel("<html><h1>"+module.getName()+"</h1></html>");
        this.add(title);
        LessonsJTableModel model = new LessonsJTableModel(module);
        lessons = new JTable(model);
        TableColumn lectureColumn = lessons.getColumnModel().getColumn(2);
        lectureColumn.setCellEditor(new ArrayEditor<Lecturer>(allLecturers));
         
        JScrollPane sp = new JScrollPane(lessons);
        this.add(sp);
    }
    
    
}
