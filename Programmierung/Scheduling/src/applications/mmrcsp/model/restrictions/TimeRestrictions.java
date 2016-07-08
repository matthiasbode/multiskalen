/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.restrictions;

import applications.mmrcsp.model.restrictions.instances.MaximumTimeLag;
import applications.mmrcsp.model.restrictions.instances.MinimumTimeLag;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import math.FieldElement;

/**
 * Eine zeitliche Restriktion kann zwischen zwei Operationen o_i und o_j
 * bestehen. So gibt es z.B. den minimalen zeitlichen Abstand zwischen o_i und
 * o_j, bezeichnet mit d_{ij}^{min} oder aber die maximale Pause zwischen o_i
 * und o_j d_{ij}^{max}
 *
 * @author bode
 */
public class TimeRestrictions<E extends Operation> {

    private Table<E, E, ArrayList<Restriction>> restrictions = HashBasedTable.create();

    public TimeRestrictions() {
    }

    public Map<E, ArrayList<Restriction>> getRestrictionToSuccessors(E o) {
        Map<E, ArrayList<Restriction>> set = restrictions.row(o);
        if (set.isEmpty()) {
            return new HashMap<>();
        }
        return set;
    }

    public Map<E, ArrayList<Restriction>> getRestrictionFromPredecessors(E o) {
        Map<E, ArrayList<Restriction>> set = restrictions.column(o);
        if (set.isEmpty()) {
            return new HashMap<>();
        }
        return set;
    }

    public Map<E, ArrayList<Restriction>> getRestrictionToNeighbours(E o) {
        Map<E, ArrayList<Restriction>> res = getRestrictionFromPredecessors(o);
        res.putAll(getRestrictionToSuccessors(o));
        return res;
    }

    /**
     * Setzt den minimalen Zeitlichen Abstand d_{ij}1^{min} zwischen zwei
     * Operationen o_i und o_j
     *
     * @param o_i
     * @param o_j
     * @param d_ij_min
     */
    public void putMinRestriction(E o_i, E o_j, FieldElement d_ij_min) {
        MinimumTimeLag minimumTimeLag = new MinimumTimeLag(o_i, o_j, d_ij_min);
        putRestriction(o_i, o_j, minimumTimeLag);
    }

    /**
     * Setzt den maximalen Zeitlichen Abstand d_{ij}1^{min} zwischen zwei
     * Operationen o_i und o_j
     *
     * @param o_i
     * @param o_j
     * @param d_ij_min
     */
    public void putMaxRestriction(E o_i, E o_j, FieldElement d_ij_max) {
        MaximumTimeLag maximumTimeLag = new MaximumTimeLag(o_i, o_j, d_ij_max);
        putRestriction(o_i, o_j, maximumTimeLag);
    }

    public void putRestriction(E o_i, E o_j, Restriction r) {
        ArrayList<Restriction> list = restrictions.get(o_i, o_j);
        if (list == null) {
            list = new ArrayList<>();
            restrictions.put(o_i, o_j, list);
        }
        list.add(r);
    }

    public boolean complyRestrictions(Schedule schedule, E o_i, E o_j) {
        ArrayList<Restriction> res = restrictions.get(o_i, o_j);
        if (res == null) {
            return true;
        }
        ArrayList<FieldElement> xVals = new ArrayList<>();
        xVals.add(schedule.get(o_i));
        xVals.add(schedule.get(o_j));
        for (Restriction restriction : res) {
            if (!restriction.comply(xVals)) {
                return false;
            }
        }
        return true;

    }

    public boolean complyRestrictions(Schedule s, Collection<E> ops) {
        for (E o_i : ops) {
            for (E o_j : ops) {
                if (!complyRestrictions(s, o_i, o_j)) {
                    System.out.println("Zeitliche Constaint nicht eingehalten für:\n"
                            + o_i + "\n"
                            + o_j);
                    return false;
                }
            }
        }
        return true;
    }
}
