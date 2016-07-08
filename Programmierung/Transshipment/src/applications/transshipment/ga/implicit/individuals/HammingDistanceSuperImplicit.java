/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.ga.implicit.individuals;

import applications.transshipment.ga.implicit.individuals.modes.HammingDistanceMode;
import applications.transshipment.ga.implicit.individuals.ops.*;
import ga.metric.Metric;

/**
 *
 * @author bode
 */
public class HammingDistanceSuperImplicit implements Metric<ImplicitSuperIndividual> {
    HammingDistanceOp metricOp = new HammingDistanceOp();
    HammingDistanceMode metricMode = new HammingDistanceMode();
           
    
    @Override
    public double distance(ImplicitSuperIndividual a, ImplicitSuperIndividual b) {
        int distance = 0;
        distance += metricMode.distance(a.getModeIndividual(), b.getModeIndividual());
        distance += metricOp.distance(a.getOperationIndividual(), b.getOperationIndividual());
        return distance;
    }
    
}
