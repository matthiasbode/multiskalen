/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.duisburg;

import applications.transshipment.generator.projekte.ParameterInputFile;
import java.io.File;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class DuisburgInputParameters implements ParameterInputFile {

    public int numberOfCranes = 5;
    public int numberOfShuttleCars = 5;
    public GregorianCalendar start = new GregorianCalendar(2011, 1, 2, 22, 00);
    public GregorianCalendar ende = new GregorianCalendar(2011, 1, 3, 07, 00);

    public int LCS_numberOfHandoverPoints = 11;
    public double LCS_lengthHandoverPoint = 31.7;
    public double LCS_widthHandoverPoint = 5.0;
    public double LCS_distanceHandoverPoints = 27.5;
    public int LCS_numberOfTracks = 4;
    public double LCS_angle = 30;

    public int numberOfTrains = 15;
    public int numberOfWagons = 35;
    public int numberOfSlots = 3;

    public long begutachtungsdauer = 20 * 1000 * 60;
    public long zeitlicherAbstandZwischenZweiZuegen = 20 * 1000 * 60;
    public long gleisneubelegungsdauer = 15 * 1000 * 60;

    public InputStream resource = DuisburgTerminalGenerator.class.getResourceAsStream("transportprogramme/tp1.json");
    public int use_LCS = 1;

    @Override
    public int getNumberOfCranes() {
        return numberOfCranes;
    }

    @Override
    public int getNumberOfAGVs() {
        return numberOfShuttleCars;
    }

    @Override
    public GregorianCalendar getStart() {
        return start;
    }

    @Override
    public GregorianCalendar getEnde() {
        return ende;
    }

    @Override
    public double getDouble(String key) {

        try {
            return DuisburgInputParameters.class.getField(key).getDouble(this);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    }

    @Override
    public int getInt(String key) {
        try {
            return DuisburgInputParameters.class.getField(key).getInt(this);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public long getLong(String key) {
        try {
            return DuisburgInputParameters.class.getField(key).getLong(this);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DuisburgInputParameters.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Long.MAX_VALUE;
    }

    public static void main(String[] args) {
        JSONSerialisierung.exportJSON(new File("/home/bode/Schreibtisch/Parameters.json"), new DuisburgInputParameters(), true);
    }

    @Override
    public String toString() {
        return "DuisburgInputParameters";
    }

}
