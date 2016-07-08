/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Philipp
 *
 */
public class SortableHeatMapDataSet<I extends Comparable, C> extends HeatMapDataSet<I, C> {
    public SortableHeatMapDataSet(){
        super();
    }
    @Override
    public ArrayList<I> getIdentifiers() {
        ArrayList<I> list = super.getIdentifiers();
        Collections.sort(list);
        return list;
    }
}
