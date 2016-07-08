/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import applications.timetable.xml.wrapperGrade.GradeVsModuleListXmlAdapter;
import applications.timetable.xml.wrapperPeriod.PeriodVsRoomListXmlAdapter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author bode
 */
@XmlRootElement
@XmlSeeAlso({Module.class, Room.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class ProblemDefinition {

    private ArrayList<Period> periods = new ArrayList<Period>();
    private ArrayList<Lecturer> lecturers = new ArrayList<Lecturer>();
    private ArrayList<Room> rooms = new ArrayList<Room>();
    @XmlJavaTypeAdapter(GradeVsModuleListXmlAdapter.class)
    private HashMap<Grade, ArrayList<Module>> modules;
    @XmlJavaTypeAdapter(PeriodVsRoomListXmlAdapter.class)
    private HashMap<Period, ArrayList<Room>> freeRooms;
    
    @XmlTransient
    private ArrayList<Lesson> lessons;
            

    public ProblemDefinition() {
    }

    public ProblemDefinition(ArrayList<Lecturer> lecturers, HashMap<Grade, ArrayList<Module>> modules, HashMap<Period, ArrayList<Room>> freeRooms, ArrayList<Room> rooms) {
        this.lecturers = lecturers;
        this.modules = modules;
        this.freeRooms = freeRooms;
        this.rooms = rooms;
    }

    public void test() {
        if (this.freeRooms.isEmpty()) {
            for (Period period : getPeriods()) {
                getFreeRooms().put(period, new ArrayList<Room>());
            }
        } else {
            for (Period period : this.freeRooms.keySet()) {
                ArrayList<Room> rs = this.freeRooms.get(period);
                if (rs == null || rs.isEmpty()) {
                    this.freeRooms.put(period, new ArrayList<Room>());
                }
            }
        }
    }

    public ArrayList<Lesson> getLessons() {
        if (lessons == null) {
            lessons = new ArrayList<Lesson>();
            for (Grade grade : getModules().keySet()) {
                ArrayList<Module> ms = getModules().get(grade);
                for (Module module : ms) {
                    lessons.addAll(module.getLessons());
                    for (Lesson lesson : lessons) {
                        lesson.addGrade(grade);
                    }
                }
            }
        }
        return lessons;
    }

    
    public HashMap<Period, ArrayList<Room>> getFreeRooms() {
        return freeRooms;
    }

    public void setFreeRooms(HashMap<Period, ArrayList<Room>> freeRooms) {
        this.freeRooms = freeRooms;
    }

    public HashMap<Grade, ArrayList<Module>> getModules() {
        return modules;
    }

    public void setModules(HashMap<Grade, ArrayList<Module>> modules) {
        this.modules = modules;
    }

    public ArrayList<Lecturer> getLecturers() {
        return lecturers;
    }

    public void setLecturers(ArrayList<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(ArrayList<Period> periods) {
        this.periods = periods;
    }

    public Period[][] getPeriodsInGrid() {
        int numberOfDays = 1;
        int maxNumberOfPeriodsPerDay = 0;
        Period firstPeriod = getPeriods().get(0);

        GregorianCalendar lastDate = new GregorianCalendar();
        lastDate.setTimeInMillis(firstPeriod.getTimeSlot().getFromWhen());

        int numberOfPeriodsCurrentDay = 0;
        for (Period period : periods) {
            GregorianCalendar date = new GregorianCalendar();
            date.setTimeInMillis(period.getTimeSlot().getFromWhen());
            if (date.get(GregorianCalendar.DAY_OF_WEEK) == lastDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                numberOfPeriodsCurrentDay++;
            } else {
                if (numberOfPeriodsCurrentDay > maxNumberOfPeriodsPerDay) {
                    maxNumberOfPeriodsPerDay = numberOfPeriodsCurrentDay;
                }
                numberOfPeriodsCurrentDay = 0;
                numberOfDays++;
            }
            lastDate = (GregorianCalendar) date.clone();
        }
        System.out.println("NumberOfDays:" + numberOfDays);
        System.out.println("NumberOfPeriodsPerDay:" + maxNumberOfPeriodsPerDay);
        lastDate.setTimeInMillis(firstPeriod.getTimeSlot().getFromWhen());
        Period[][] ps = new Period[numberOfDays][maxNumberOfPeriodsPerDay];
        int i = 0;
        int j = 0;
        for (Period period : periods) {
            GregorianCalendar date = new GregorianCalendar();
            date.setTimeInMillis(period.getTimeSlot().getFromWhen());
            if (date.get(GregorianCalendar.DAY_OF_WEEK) != lastDate.get(GregorianCalendar.DAY_OF_WEEK)) {
                i++;
                j = 0;
            }
            ps[i][j++] = period;
            lastDate = (GregorianCalendar) date.clone();
        }
        return ps;
    }
}
