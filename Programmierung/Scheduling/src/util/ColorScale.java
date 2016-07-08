/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Color;

/**
 *
 * @author Philipp
 */
public class ColorScale {
    /**
     * Gibt die Farbe auf einer Skala von 0 bis 1 für die angegebene Auslastung zurück.
     * 
     */
    public static Color getColorInverted(double auslastung){
        double H = (1.-auslastung)*0.7; //Hue
        double S = 0.9; //Saturation
        double B = 0.9; //Brightness
        return Color.getHSBColor((float)H,(float)S,(float)B);
    }
    /**
     * Gibt die Farbe auf einer Skala von 0 bis maxVal für die angegebene Auslastung zurück.
     * 
     */
    public static Color getColorInverted(double auslastung,double maxVal){
        double H = (1.-(auslastung/maxVal))*0.7; //Hue
        double S = 0.9; //Saturation
        double B = 0.9; //Brightness
        return Color.getHSBColor((float)H,(float)S,(float)B);
    }
}
