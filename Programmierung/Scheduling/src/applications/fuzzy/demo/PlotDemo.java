///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.fuzzy.demo;
//
//import applications.fuzzy.functions.LinearizedFunction1d;
//import applications.fuzzy.operation.AdaptedLambda;
//import applications.fuzzy.operation.FuzzyOperation;
//import applications.fuzzy.plotter.FuzzyFunctionPlotter;
//import applications.fuzzy.scheduling.rules.FuzzyDemandUtilities;
//import applications.mmrcsp.model.resources.Resource;
//import applications.mmrcsp.model.resources.ResourceImplementation;
//import fuzzy.number.discrete.FuzzyFactory;
//import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
//import math.DoubleValue;
// 
//
///**
// *
// * @author Matthias
// */
//public class PlotDemo {
//
//    public static void main(String[] args) {
//        int resolution = 200;
//        double v1 = 4500.;
//        double VF = 0.01;
//        double VFab = 0.025;
//
//        Nutzleistung qn = new Nutzleistung();
//        DiscretizedFuzzyInterval Qn_I = qn.get_Qn_I(resolution);
//
//        // Fuzzy-Volumen festlegen
//        DiscretizedFuzzyInterval vFI1 = FuzzyFactory.createLinearInterval(v1 - v1 * VF, v1 + v1 * VF, v1 * VFab, v1 * VFab, resolution);
//
//        DiscretizedFuzzyInterval duration = vFI1.div(Qn_I);
//        duration = FuzzyFactory.createLinearInterval(30,40,5,5, resolution);
//        DiscretizedFuzzyInterval start = FuzzyFactory.createLinearInterval(10, 15, 4,4, resolution);
//        DiscretizedFuzzyInterval ende = start.add(duration);
//        double c2 = ende.getC2();
//
//        Resource r1 = new ResourceImplementation("Bagger");
//        double rd1 = 1.0;
//        FuzzyOperation o1 = new FuzzyOperation(duration, 0.5);
//        o1.setDemand(r1, new DoubleValue(rd1));
//
//   
//        double dt = 0.5;
//      
//
//        
//        
//        LinearizedFunction1d function_0_5 = FuzzyDemandUtilities.getPresenceFunction(o1,start, new AdaptedLambda(start, 0.5));
//        LinearizedFunction1d function_1_0 = FuzzyDemandUtilities.getPresenceFunction(o1,start, new AdaptedLambda(start, 1.0));
//        LinearizedFunction1d function_0_0 = FuzzyDemandUtilities.getPresenceFunction(o1,start, new AdaptedLambda(start, 0.0));
//        
//        
//        System.out.println("maximale Duration: "+duration.getC2());
//        System.out.println("minimale Duration: "+duration.getC1());
//        System.out.println("lambda_min: "+ FuzzyDemandUtilities.getLambdaMin(o1,start));
//        System.out.println("lambda_max: "+ FuzzyDemandUtilities.getLambdaMax(o1,start));
//        System.out.println("A_PI: "+ FuzzyDemandUtilities.getSizeOfPossibilityArea(o1,start));
//        System.out.println("A_N: "+ FuzzyDemandUtilities.getSizeOfNecessityArea(o1,start));
//        
//        
//         
//        
//        
//        FuzzyFunctionPlotter plotter = new FuzzyFunctionPlotter("Auslastungskurven mit verschiedenen beta-Werten");
//        plotter.addFunction(start.membership, 0, c2, dt, "Start");
//        plotter.addFunction(ende.membership, 0, c2, dt, "Ende");
////        plotter.addFunction(duration.membership, 0, c2, dt, "Dauer");
////        plotter.addFunction(o1.getLamdaMinFunction(start), 0, c2, dt, "Lambda_min");
////        plotter.addFunction(o1.getLamdaMaxFunction(start), 0, c2, dt, "Lambda_max");
//        
//        plotter.addFunction(function_0_5, 0, c2, dt, "Auslastung 0.5");
//        plotter.addFunction(function_0_0, 0, c2, dt, "Auslastung 0.0");
//        
//        plotter.addFunction(function_1_0, 0, c2, dt, "Auslastung 1.0");
//        plotter.plot();
//        
//       
// 
//    }
//
//}
