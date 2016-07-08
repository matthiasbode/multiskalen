/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.Grade;
import applications.timetable.model.Period;
import applications.timetable.model.ProblemDefinition;
import applications.timetable.model.TimeTableMatrix.TimeTable;
import applications.timetable.model.TimeTableMatrix.TimeTableElement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author bode
 */
public class TimeTableJTableModel extends AbstractTableModel {

    TimeTable daten;
    ProblemDefinition p;
    ArrayList<String> days = new ArrayList<String>();
    Period[][] periods;
    Grade c;

    public ProblemDefinition getProblem() {
        return p;
    }

    public TimeTableJTableModel(Grade c, ProblemDefinition p, TimeTable daten) {
        this.c = c;
        this.p = p;
        this.daten = daten;
        this.periods = p.getPeriodsInGrid();
        
        for (int i = 0; i < periods.length; i++) {
            Period period = periods[i][0];
            GregorianCalendar gc= new GregorianCalendar();
            gc.setTimeInMillis(period.getTimeSlot().getFromWhen());
            days.add(gc.getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT, Locale.GERMAN));
        }
        
        

    }

    @Override
    public int getRowCount() {
        return periods[0].length;
    }

    @Override
    public int getColumnCount() {
        return periods.length;
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
        return days.get(column);
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        TimeTableElement element = daten.get(c, periods[columnIndex][rowIndex]);
        if (element == null) {
            return "-";
        }
        return element;

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TimeTableElement element = daten.get(c, periods[columnIndex][rowIndex]);

    }
}
