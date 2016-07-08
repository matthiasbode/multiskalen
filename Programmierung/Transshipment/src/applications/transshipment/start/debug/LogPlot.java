/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start.debug;

import bijava.math.function.ScalarFunction1d;
import util.chart.FunctionPlotter;

/**
 *
 * @author Bode
 */
public class LogPlot {

    public static void main(String[] args) {
        ScalarFunction1d d1 = new ScalarFunction1d() {
            double mue = 1;
            double sigma = 0.5;
            

            @Override
            public double getValue(double x) {
                return 1.0 / ((Math.sqrt(2 * Math.PI) * sigma*x) * Math.exp(- Math.pow(Math.log(x) - mue * mue,2)  / (2 * sigma * sigma)));
            }
        };
        
        FunctionPlotter.plotFunction(d1, 0, 120);
    }
}
