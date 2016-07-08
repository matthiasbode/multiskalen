/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.time.ZoneOffset;

/**
 *
 * @author Matthias
 */
public class Test {
    public static void main(String[] args) {
        for (String availableZoneId : ZoneOffset.getAvailableZoneIds()) {
            System.out.println(availableZoneId);
        }
 
    }
    
}
