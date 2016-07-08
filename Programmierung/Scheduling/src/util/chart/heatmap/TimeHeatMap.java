/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import static util.chart.heatmap.HeatMap.createStringImage;

/**
 *
 * @author Philipp
 */
public class TimeHeatMap<C> extends HeatMap<Long,C>{
    public TimeHeatMap(TimeHeatMapDataSet ds){
        super(ds);
    }
    @Override
    protected void calculateIdImages(Graphics2D gd){
        HashMap<Long, BufferedImage> map = new HashMap<>();
        for (long id : super.data.getIdentifiers()) {
            DateFormat chartFormatter = new SimpleDateFormat("HH:mm");
            String t = chartFormatter.format(new Date(id));
            map.put(id, createStringImage(gd, t));
        }
        super.idImages = map;
    }
    
}
