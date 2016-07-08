/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Philipp
 * Datensatz für eine HeatMap. 
 * Eine Heatmap ermöglicht den Vergleich mehrerer Identifier in verschiedenen 
 * Kategorien.
 * I Identifier
 * C Category
 */
public class HeatMapDataSet<I, C> {

    protected HashMap<I, HashMap<C, Double>> data;
    protected ArrayList<I> identifiers;
    protected ArrayList<C> categories;
    protected boolean isEmpty = true;

    public HeatMapDataSet() {
        data = new HashMap<I, HashMap<C, Double>>();
        identifiers = new ArrayList<I>();
        categories = new ArrayList<C>();
    }

    public double getValue(I identifier, C category) {
        if (!data.containsKey(identifier)) {
            return 0;
        } else {
            if (!data.get(identifier).containsKey(category)) {
                return 0;
            } else {
                return data.get(identifier).get(category);
            }
        }
    }

    public ArrayList<I> getIdentifiers() {
        return identifiers;
    }

    public ArrayList<C> getCategories() {
        return categories;
    }

    public void add(I identifier, C category, double value) {
        if (!identifiers.contains(identifier)) {
            identifiers.add(identifier);
        }
        if (!categories.contains(category)) {
            categories.add(category);
        }
        if (data.containsKey(identifier)) {
            data.get(identifier).put(category, value);
        } else {
            HashMap<C, Double> map = new HashMap<C, Double>();
            map.put(category, value);
            data.put(identifier, map);
        }
        isEmpty =false;
    }
    public boolean isEmpty(){
        return isEmpty;
    }
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
            for(I ide: identifiers){
                pw.print(ide.toString());
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
    public static HeatMapDataSet<String,String> readFromCSV(File path) throws FileNotFoundException, IOException{
        HeatMapDataSet data = new HeatMapDataSet();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line ="";
        ArrayList<String> categories = new ArrayList<String>();
        if((line= br.readLine())!=null){
            String[] cats = line.split(";");
            for(int i =0;i<cats.length;i++){
                categories.add(cats[i]);
            }
        }
        while((line=br.readLine()) !=null){
            String[] dat = line.split(";");
            String ide = dat[0];
            for(int i=1;i<dat.length;i++){
                data.add(ide, categories.get(i-1), Double.parseDouble(dat[i]));
            }
        }
        return data;
    }
    @Override
    public String toString(){
        String res ="HeatMapDataSet:{";
        res += "\n";
        res +="Categories:{";
        for(C cat:this.getCategories()){
                res+= cat+",";
            }
        res += "}";
        res +="\n";
        for(I ide :this.getIdentifiers()){
            res +=ide +": {";
            for(C cat:this.getCategories()){
                res += this.getValue(ide, cat)+",";
            }
            res +="} \n";
        }
        return res;
    }
}
