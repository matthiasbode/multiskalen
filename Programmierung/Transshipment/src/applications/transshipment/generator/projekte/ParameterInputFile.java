/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte;

import java.util.GregorianCalendar;

/**
 *
 * @author bode
 */
public interface ParameterInputFile {

    public int getNumberOfCranes();

    public int getNumberOfAGVs();

    public GregorianCalendar getStart();

    public GregorianCalendar getEnde();

    public double getDouble(String key);

    public int getInt(String key);

    public long getLong(String key);
    

    public static String KEY_LCS_numberOfHandoverPoints = "LCS_numberOfHandoverPoints";
    public static String KEY_LCS_lengthHandoverPoint = "LCS_lengthHandoverPoint";
    public static String KEY_LCS_widthHandoverPoint = "LCS_widthHandoverPoint";
    public static String KEY_LCS_distanceHandoverPoints = "LCS_distanceHandoverPoints";
    public static String KEY_LCS_numberOfTracks = "LCS_numberOfTracks";
    public static String KEY_LCS_angle = "LCS_angle";
    public static String KEY_use_LCS = "use_LCS";

    public static String KEY_numberOfTrains = "numberOfTrains";
    public static String KEY_numberOfWagons = "numberOfWagons";
    public static String KEY_numberOfSlots = "numberOfSlots";

    public static String KEY_begutachtungsdauer = "begutachtungsdauer";
    public static String KEY_zeitlicherAbstandZwischenZweiZuegen = "zeitlicherAbstandZwischenZweiZuegen";
    public static String KEY_gleisneubelegungsdauer = "gleisneubelegungsdauer";

}
