/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.lehrte;

import applications.transshipment.generator.projekte.duisburg.*;
import applications.transshipment.generator.projekte.ParameterInputFile;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bode
 */
public class LehrteInputParameters implements ParameterInputFile {

    public int numberOfCranes = 5;
    public int numberOfShuttleCars = 14;
    public GregorianCalendar start = new GregorianCalendar(2015, 6, 1, 22, 00);
    public GregorianCalendar ende = new GregorianCalendar(2015, 6, 2, 07, 00);

    int LCS_numberOfHandoverPoints = 11;
    double LCS_lengthHandoverPoint = 31.7;
    double LCS_widthHandoverPoint = 5.0;
    double LCS_distanceHandoverPoints = 27.5;
    public int LCS_numberOfTracks = 4;
    public double LCS_angle = 30;

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

}
