package applications.timetable.xml.wrapperPeriod;

import applications.timetable.model.Grade;
import applications.timetable.model.Module;
import applications.timetable.model.Period;
import applications.timetable.model.Room;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PeriodKeyAndRoomsListValueHolder {

    private Period grade;
    private ArrayList<Room> moduleList = new ArrayList<Room>();

    public PeriodKeyAndRoomsListValueHolder() {
    }

    public PeriodKeyAndRoomsListValueHolder(Period date, ArrayList<Room> dreamsList) {
        this.grade = date;
        this.moduleList = dreamsList;
    }

    public Period getGrade() {
        return grade;
    }

    public void setGrade(Period date) {
        this.grade = date;
    }

    public ArrayList<Room> getModuleList() {
        return moduleList;
    }

    public void setModuleList(ArrayList<Room> dreamsList) {
        this.moduleList = dreamsList;
    }
}
