/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

/**
 *
 * @author bode
 */
public class Tools {

    public static FieldElement max(FieldElement a, FieldElement b) {
        if(!a.getClass().equals(b.getClass())){
            System.out.println("angucken!");
        }
        return a.isGreaterThan(b) ? a : b;
    }

    public static FieldElement min(FieldElement a, FieldElement b) {
        return a.isLowerThan(b) ? a : b;
    }
}
