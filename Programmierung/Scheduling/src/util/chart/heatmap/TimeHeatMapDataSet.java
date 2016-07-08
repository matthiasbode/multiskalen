/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Philipp
 */
public class TimeHeatMapDataSet<C> extends SortableHeatMapDataSet<Long,C>{
    private DateFormat format;
    public TimeHeatMapDataSet(){
        super();
        format = new SimpleDateFormat("HH:mm:ss");
    }
    public TimeHeatMapDataSet(DateFormat format){
        super();
        this.format = format;
    }
    public void setDateFormat(DateFormat format){
        this.format = format;
    }
}
