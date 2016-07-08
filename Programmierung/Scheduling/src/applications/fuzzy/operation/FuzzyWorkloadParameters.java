/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.operation;

import fuzzy.number.discrete.interval.FuzzyInterval;

/**
 *
 * @author bode
 */
public class FuzzyWorkloadParameters implements Cloneable {

    public static enum Case {

        noOverlap,
        smallOverlap,
        largeOverlab;
    }

    public Case overlapCase;
    
    /**
     * Lambda-Links
     */
    public double lambdaL;
    /**
     * Lambda-Rechts
     */
    public double lambdaR;
    /**
     * Angepasste Startzeit für die Operation (zumeist vorne abgeschnitten)
     */
    public FuzzyInterval adaptedStart;
   
    public FuzzyWorkloadParameters(double lambdaL) {
        this.lambdaL = lambdaL;
        this.lambdaR = Double.NaN;
     
    }

    @Override
    public FuzzyWorkloadParameters clone() {
        FuzzyWorkloadParameters adaptedLambda = new FuzzyWorkloadParameters(this.lambdaL);
        adaptedLambda.lambdaR = this.lambdaR;
        return adaptedLambda;
    }

    @Override
    public String toString() {
        return "AdaptedLambda{" + "lambdaL=" + lambdaL + ", lambdaR=" + lambdaR + '}';
    }

}
