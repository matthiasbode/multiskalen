package applications.timetable.xml.wrapperGrade;

import applications.timetable.model.Grade;
import applications.timetable.model.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class GradeVsModuleListXmlAdapter extends XmlAdapter<GradeKeyAndModulesListValueHolderListWrapper, Map<Grade, ArrayList<Module>>> {

    @Override
    public GradeKeyAndModulesListValueHolderListWrapper marshal(Map<Grade, ArrayList<Module>> dateVsListOfdreamsMap) throws Exception {
        ArrayList<GradeKeyAndModuleListValueHolder> dateKeyAndDreamsListValueHolders = new ArrayList<GradeKeyAndModuleListValueHolder>();
        if (dateVsListOfdreamsMap != null) {
            Set<Grade> dreamDatekeySet = dateVsListOfdreamsMap.keySet();
            for (Grade dreamDate : dreamDatekeySet) {
                dateKeyAndDreamsListValueHolders.add(new GradeKeyAndModuleListValueHolder(dreamDate, dateVsListOfdreamsMap.get(dreamDate)));
            }
        }
        GradeKeyAndModulesListValueHolderListWrapper listWrapper = new GradeKeyAndModulesListValueHolderListWrapper();
        listWrapper.setGradeKeyAndModulesListValueHolders(dateKeyAndDreamsListValueHolders);
        return listWrapper;
    }

    @Override
    public Map<Grade, ArrayList<Module>> unmarshal(GradeKeyAndModulesListValueHolderListWrapper dateKeyAndDreamsListValueHolderList) throws Exception {
        Map<Grade, ArrayList<Module>> dateVsListOfdreamsMap = new HashMap<Grade, ArrayList<Module>>();
        if (dateKeyAndDreamsListValueHolderList != null) {
            for (GradeKeyAndModuleListValueHolder holder : dateKeyAndDreamsListValueHolderList.getGradeKeyAndModulesListValueHolders()) {
                dateVsListOfdreamsMap.put(holder.getGrade(), holder.getModuleList());
            }
        }
        return dateVsListOfdreamsMap;
    }
}
