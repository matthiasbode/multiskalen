/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.fuzzy.scheduling.rules.alternative;

import applications.fuzzy.operation.*;
import fuzzy.number.discrete.interval.FuzzyInterval;

/**
 *
 * @author bode
 */
public class FuzzyWorkloadParameters_alternative implements Cloneable {

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

    public double DPI;
    public double DN;
    public Block block;
    public double zielFlaeche;

    public FuzzyWorkloadParameters_alternative(FuzzyInterval start, BetaOperation op, double lambdaL, double lambdaR, double DPI, double DN, double zielFlaeche) {
        this.lambdaL = lambdaL;
        this.lambdaR = lambdaR;
        this.DPI = DPI;
        this.DN = DN;

        this.zielFlaeche = zielFlaeche;

        FuzzyInterval duration = (FuzzyInterval) op.getDuration();
        FuzzyInterval ende = start.add(duration);
        this.block = new Block(start, ende);
        
       
        if (block.ds <= block.af) {
            overlapCase = FuzzyWorkloadParameters_alternative.Case.noOverlap;
        } else if (block.ds > block.af && block.cs < block.bf) {
            overlapCase = FuzzyWorkloadParameters_alternative.Case.smallOverlap;
        } else {
            overlapCase = FuzzyWorkloadParameters_alternative.Case.largeOverlab;
        }
        
        double w = duration.getC1();
        double z = duration.getC2();

        if (DPI < z) {
            if (overlapCase.equals(FuzzyWorkloadParameters_alternative.Case.noOverlap)) {
                block.bs = block.bs - 2 * (z - DPI) * (block.bs - block.as) / (block.bs - block.as + block.df - block.cf);
                block.cf = block.cf + 2 * (z - DPI) * (block.df - block.cf) / (block.bs - block.as + block.df - block.cf);
            }
            if (overlapCase.equals(FuzzyWorkloadParameters_alternative.Case.smallOverlap)) {
                block.bs = block.bs - 2 * (z - DPI) * (block.bs - block.as) / (block.bs - block.as + block.df - block.cf);
                block.cf = block.cf + 2 * (z - DPI) * (block.df - block.cf) / (block.bs - block.as + block.df - block.cf);
            }
        }

        if (DN > w) {
            if (overlapCase.equals(FuzzyWorkloadParameters_alternative.Case.noOverlap)) {
                block.cs = block.cs + 2 * (DN - w) * (block.ds - block.cs) / (block.ds - block.cs + block.bf - block.af);
                block.bf = block.bf + 2 * (DN - w) * (block.bf - block.af) / (block.ds - block.cs + block.bf - block.af);
            }
            if (overlapCase.equals(FuzzyWorkloadParameters_alternative.Case.smallOverlap)) {
                double beta0 = 0;
                double betaStrich = 0;

                double theta = (1 - beta0) / (1 - betaStrich);

                block.cs = theta * block.cs + (1 - theta) * block.ds;
                block.bf = theta * block.bf + (1 - theta) * block.af;
            }
        }
    }

    /**
     * Angepasste Startzeit für die Operation (zumeist vorne abgeschnitten)
     */
//    public FieldElement adaptedStart;
    public FuzzyWorkloadParameters_alternative(double lambdaL) {
        this.lambdaL = lambdaL;
        this.lambdaR = lambdaL;
//        this.adaptedStart = adaptedStart;
    }

    public FuzzyWorkloadParameters_alternative() {
        this.lambdaL = 0;
        this.lambdaR = 0;
    }

    public FuzzyWorkloadParameters_alternative(double lambdaL, double lambdaR) {
        this.lambdaL = lambdaL;
        this.lambdaR = lambdaR;
    }

    @Override
    public String toString() {
        return "AdaptedLambda{" + "lambdaL=" + lambdaL + ", lambdaR=" + lambdaR + '}';
    }

    public static class Block {

        public double as, bs, cs, ds;
        public double af, bf, cf, df;

        public Block(FuzzyInterval start, FuzzyInterval ende) {

            as = start.getC1();
            bs = start.getM1();
            cs = start.getM2();
            ds = start.getC2();

            af = ende.getC1();
            bf = ende.getM1();
            cf = ende.getM2();
            df = ende.getC2();
        }

    }
}
