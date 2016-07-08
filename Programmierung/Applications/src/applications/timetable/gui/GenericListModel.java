/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author bode
 */
public class GenericListModel<T> extends AbstractListModel<T> {

    private List<T> data;

    public GenericListModel() {
        super();
    }

    public GenericListModel(List<T> data) {
        this.data = data;
    }
    

    public final void setData(final List<T> newData) {
        data.clear();
        data.addAll(newData);
        fireContentsChanged(this, 0, data.size() - 1);
    }

    public final List<T> getData() {
        return new ArrayList<T>(data);
    }

    public final void add(final T value) {
        int changeIndex = data.size();
        data.add(value);
        fireIntervalAdded(this, changeIndex, changeIndex);
    }

    public final boolean remove(final T value) {
        boolean result = false;
        int index = data.indexOf(value);
        if (index >= 0) {
            result = data.remove(value);
            fireIntervalRemoved(this, index, index);
        }
        return result;
    }

    public final T getElementAt(int index) {
        return data.get(index);
    }

    public final int getSize() {
        return data.size();
    }
}