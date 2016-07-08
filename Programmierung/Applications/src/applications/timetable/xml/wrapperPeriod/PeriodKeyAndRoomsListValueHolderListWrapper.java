package applications.timetable.xml.wrapperPeriod;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PeriodKeyAndRoomsListValueHolderListWrapper {

    private ArrayList<PeriodKeyAndRoomsListValueHolder> periodKeyAndRoomsListValueHolders = new ArrayList<PeriodKeyAndRoomsListValueHolder>();

    public PeriodKeyAndRoomsListValueHolderListWrapper() {
    }

    public ArrayList<PeriodKeyAndRoomsListValueHolder> getPeriodKeyAndRoomsListValueHolders() {
        return periodKeyAndRoomsListValueHolders;
    }

    public void setPeriodKeyAndRoomsListValueHolders(ArrayList<PeriodKeyAndRoomsListValueHolder> periodKeyAndRoomsListValueHolders) {
        this.periodKeyAndRoomsListValueHolders = periodKeyAndRoomsListValueHolders;
    }
}
