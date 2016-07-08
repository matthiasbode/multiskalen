/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.priorityrules.jobs;

import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.structs.TrainType;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Sortiert die Jobs so, dass kurze Transporte zuerst eingeplant werden.
 *
 * @author Matthias
 */
public class MinDistanceComparator implements Comparator<LoadUnitJob> {

    Collection<LoadUnitJob> jobs;
    HashMap<LoadUnitJob, Double> distance = new HashMap<>();

    public MinDistanceComparator(Collection<LoadUnitJob> jobs) {
        this.jobs = jobs;
        for (LoadUnitJob j : jobs) {
            //Wenn ins Lager
            if (!(j.getDestination() instanceof TrainType)) {
                this.distance.put(j, Double.POSITIVE_INFINITY);
            } //sonst
            else {
                this.distance.put(j, j.getOrigin().getCenterOfGeneralOperatingArea().distance(j.getDestination().getCenterOfGeneralOperatingArea()));
            }
        }
    }

    @Override
    public int compare(LoadUnitJob o1, LoadUnitJob o2) {
        int compare = Double.compare(distance.get(o1), distance.get(o2));
        if (compare == 0) {
            return o1.toString().compareTo(o2.toString());
        }
        return compare;
    }

}
