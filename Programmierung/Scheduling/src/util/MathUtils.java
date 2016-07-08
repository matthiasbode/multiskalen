/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author Matthias
 */
public class MathUtils {
 /**
	 * Generates an array of successive values from <em>begin</em> to <em>end</em> with step
	 * size <em>pitch</em>.
	 * @param begin First value in sequence
	 * @param pitch Step size of sequence
	 * @param end Last value of sequence
	 * @return Array of successive values
	 */
	public static double[] increment(double begin, double pitch, double end) {
		double[] array = new double[(int) ((end - begin) / pitch) +1];
		for (int i = 0; i < array.length; i++) {
			array[i] = begin + i * pitch;
		}
		return array;
	}   
}
