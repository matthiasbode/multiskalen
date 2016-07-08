/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs;

import applications.mmrcsp.model.MultiModeJob;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author bode
 */
public class MaxTotalSuccessor<J extends MultiModeJob> implements Comparator<J> {

    HashMap<J, Integer> numberOfSucs;

    public MaxTotalSuccessor(HashMap<J, Integer> numberOfSucs) {
        this.numberOfSucs = numberOfSucs;
    }

    @Override
    public int compare(J o1, J o2) {
        if (numberOfSucs.get(o1) > (numberOfSucs.get(o2))) {
            return -1;
        }
        if (numberOfSucs.get(o1) < (numberOfSucs.get(o2))) {
            return 1;
        } else {
            return o1.toString().compareTo(o2.toString());
        }
    }

}
