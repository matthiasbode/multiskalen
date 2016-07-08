package applications.timetable.xml.wrapperGrade;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class GradeKeyAndModulesListValueHolderListWrapper {

    private ArrayList<GradeKeyAndModuleListValueHolder> gradeKeyAndModulesListValueHolders = new ArrayList<GradeKeyAndModuleListValueHolder>();

    public GradeKeyAndModulesListValueHolderListWrapper() {
    }

    public ArrayList<GradeKeyAndModuleListValueHolder> getGradeKeyAndModulesListValueHolders() {
        return gradeKeyAndModulesListValueHolders;
    }

    public void setGradeKeyAndModulesListValueHolders(ArrayList<GradeKeyAndModuleListValueHolder> gradeKeyAndModulesListValueHolders) {
        this.gradeKeyAndModulesListValueHolders = gradeKeyAndModulesListValueHolders;
    }
}
