package applications.timetable.xml.wrapperPeriod;

import applications.timetable.model.Period;
import applications.timetable.model.Room;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PeriodVsRoomListXmlAdapter extends XmlAdapter<PeriodKeyAndRoomsListValueHolderListWrapper, Map<Period, ArrayList<Room>>> {

    public ArrayList<Period> periods = new ArrayList<Period>();
    
    @Override
    public PeriodKeyAndRoomsListValueHolderListWrapper marshal(Map<Period, ArrayList<Room>> dateVsListOfdreamsMap) throws Exception {
        ArrayList<PeriodKeyAndRoomsListValueHolder> dateKeyAndDreamsListValueHolders = new ArrayList<PeriodKeyAndRoomsListValueHolder>();
        if (dateVsListOfdreamsMap != null) {
            Set<Period> dreamDatekeySet = dateVsListOfdreamsMap.keySet();
            for (Period dreamDate : dreamDatekeySet) {
                dateKeyAndDreamsListValueHolders.add(new PeriodKeyAndRoomsListValueHolder(dreamDate, dateVsListOfdreamsMap.get(dreamDate)));
            }
        }
        PeriodKeyAndRoomsListValueHolderListWrapper listWrapper = new PeriodKeyAndRoomsListValueHolderListWrapper();
        listWrapper.setPeriodKeyAndRoomsListValueHolders(dateKeyAndDreamsListValueHolders);
        return listWrapper;
    }

    @Override
    public Map<Period, ArrayList<Room>> unmarshal(PeriodKeyAndRoomsListValueHolderListWrapper dateKeyAndDreamsListValueHolderList) throws Exception {
        Map<Period, ArrayList<Room>> dateVsListOfdreamsMap = new HashMap<Period, ArrayList<Room>>();
        if (dateKeyAndDreamsListValueHolderList != null) {
            for (PeriodKeyAndRoomsListValueHolder holder : dateKeyAndDreamsListValueHolderList.getPeriodKeyAndRoomsListValueHolders()) {
//                Period p = null;
//                for (Period period : periods) {
//                    if(period.getIndex() == holder.getGrade()){
//                        p = period;
//                        break;
//                    }
//                }
                dateVsListOfdreamsMap.put(holder.getGrade(), holder.getModuleList());
            }
        }
        return dateVsListOfdreamsMap;
    }
}
