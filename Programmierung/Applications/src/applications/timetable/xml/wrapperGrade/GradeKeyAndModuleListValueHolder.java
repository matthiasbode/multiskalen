package applications.timetable.xml.wrapperGrade;

import applications.timetable.model.Grade;
import applications.timetable.model.Module;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class GradeKeyAndModuleListValueHolder {

    private Grade grade;
    private ArrayList<Module> moduleList = new ArrayList<Module>();

    public GradeKeyAndModuleListValueHolder() {
    }

    public GradeKeyAndModuleListValueHolder(Grade date, ArrayList<Module> dreamsList) {
        this.grade = date;
        this.moduleList = dreamsList;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade date) {
        this.grade = date;
    }

    public ArrayList<Module> getModuleList() {
        return moduleList;
    }

    public void setModuleList(ArrayList<Module> dreamsList) {
        this.moduleList = dreamsList;
    }
}
