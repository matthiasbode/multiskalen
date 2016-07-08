/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import applications.timetable.model.Period;
import applications.timetable.model.ProblemDefinition;
import applications.timetable.model.Room;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author bode
 */
public class PeriodJTableModel extends AbstractTableModel {

    ProblemDefinition problem;
    ArrayList<String> days = new ArrayList<String>();
    Period[][] periods;

    public PeriodJTableModel(ProblemDefinition p) {
        this.problem = p;
        this.periods = p.getPeriodsInGrid();
        for (int i = 0; i < periods.length; i++) {
            Period period = periods[i][0];
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(period.getTimeSlot().getFromWhen());
            days.add(gc.getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SHORT, Locale.GERMAN));
        }
    }

    public int getRowCount() {
        return periods[0].length;
    }

    public int getColumnCount() {
        return periods.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Period period = periods[columnIndex][rowIndex];
        ArrayList<Room> rooms = problem.getFreeRooms().get(period);
        return rooms;

    }

    public ProblemDefinition getProblem() {
        return problem;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
