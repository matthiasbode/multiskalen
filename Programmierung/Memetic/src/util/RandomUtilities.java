/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Random;

/**
 *
 * @author bode
 */
public class RandomUtilities {

    public static int getRandomValue(Random r, int min, int max) {
        double lambda = r.nextDouble();
        int value = (int) (((1 - lambda) * min + lambda * (max + 1)));
        value = Math.min(max, value);
        return value;
    }

}
