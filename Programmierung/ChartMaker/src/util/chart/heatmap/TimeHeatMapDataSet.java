/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @Override
     public void writeToCSV(File path) throws IOException{
            FileWriter fw = new FileWriter(path);
            PrintWriter pw = new PrintWriter(fw);
            /*
             * Printing Category Names
             */
            for(C cat: categories){
                pw.print(cat.toString());
                pw.print(";");
            }
            pw.println();
            for(Long ide: identifiers){
                DateFormat chartFormatter = new SimpleDateFormat("HH:mm:ss");
                String t = chartFormatter.format(new Date(ide));
                pw.print(t);
                pw.print(";");
                for(C cat:categories){
                    pw.print(this.getValue(ide, cat));
                    pw.print(";");
                }
                pw.println();
            }
            pw.flush();
            pw.close();
            fw.close();
    }
}
