/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.Lesson;
import applications.timetable.model.Module;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author bode
 */
public class LessonsJTableModel extends AbstractTableModel {

    private Module m;
    private ArrayList<String> columns = new ArrayList<String>();

    public LessonsJTableModel(Module m) {
        this.m = m;
        columns.add("Bezeichnung");
        columns.add("# Studierende");
        columns.add("Dozenten");
    }

    @Override
    public int getRowCount() {
        return m.getLessons().size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
//        if (columnIndex != 2) {
        return true;
//        }
//        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (m.getLessons().size() <= rowIndex) {
            return "";
        }
        Lesson lesson = m.getLessons().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return lesson.getName();
            case 1:
                return lesson.getNumberOfStudents();
            default:
                return lesson.getLecturers();
        }



    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (m.getLessons().size() <= rowIndex) {
            return;
        }
        Lesson lesson = m.getLessons().get(rowIndex);
        System.out.println(aValue.getClass());
         switch (columnIndex) {
            case 0:
                  lesson.setName((String)aValue);
            case 1:
                  lesson.setNumberOfStudents( (Integer) aValue);
            default:
                   
        }
         
    }
}
