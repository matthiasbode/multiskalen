/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package applications.transshipment.start.debug;

import applications.mmrcsp.model.basics.TimeSlot;

/**
 *
 * @author Bode
 */
public class TimeCalc {
    public static void main(String[] args) {
        long start = 1439314707671L;
        long end = 1439325599159L;
       
        System.out.println(TimeSlot.longToFormattedDuration(end-start));
    }
}
