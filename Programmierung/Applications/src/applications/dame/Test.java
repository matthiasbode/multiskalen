/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.dame;

import ga.Parameters;
import util.RandomUtilities;

/**
 *
 * @author Matthias Bode
 */
public class Test {

    public static void main(String[] args) {
        for (int i = 0; i < 7; i++) {
            System.out.println(RandomUtilities.getRandomValue(Parameters.getRandom(), 1, 49)
            );
        }
    }
}
