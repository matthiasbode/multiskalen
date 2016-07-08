///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.fuzzy.demo;
//
//import applications.fuzzy.functions.LinearizedFunction1d;
//import applications.fuzzy.operation.AdaptedLambda;
//import applications.fuzzy.scheduling.rules.DefaultEarliestFuzzyScheduleRule;
//import applications.fuzzy.scheduling.DefaultFuzzyScheduleRulesBuilder;
//import applications.fuzzy.operation.FuzzyOperation;
//import applications.fuzzy.plotter.FuzzyFunctionPlotter;
//import applications.fuzzy.scheduling.rules.FuzzyDemandUtilities;
//import applications.fuzzy.scheduling.rules.FuzzyUtilizationManager;
//import applications.mmrcsp.model.basics.TimeSlot;
//import applications.mmrcsp.model.resources.Resource;
//import applications.mmrcsp.model.resources.ResourceImplementation;
//import applications.mmrcsp.model.schedule.Schedule;
//import applications.mmrcsp.model.schedule.rules.InstanceHandler;
//import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
//
//import fuzzy.number.discrete.FuzzyFactory;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import math.DoubleValue;
//
///**
// * Einfaches Beispiel. i | 1 2 3 4 ----------------------- p_i | 4 3 5 8 r_i1 |
// * 2 1 2 2 r_i2 | 3 5 2 4
// *
// * Kapazitäten für R1 = 5, R2 = 7
// *
// * @author bode
// */
//public class ContiniousDemo {
//
//    public static void main(String[] args) throws IOException {
//        /**
//         * Zeitfenster efinieren
//         */
//        TimeSlot slot = TimeSlot.create(0, 350);
//
//        /**
//         * Auflösung der Fuzzy-Zahlen festlegen
//         */
//        int resolution = 200;
//
//        //Kapizität einstellen:
//        double kapazitaet = 2.0;
//
//        ArrayList<Resource> resources = new ArrayList<>();
//        Resource r1 = new ResourceImplementation("R1");
//        r1.setTemporalAvailability(slot);
//        resources.add(r1);
//
//        HashMap<Resource, Double> kapa = new HashMap<>();
//        kapa.put(r1, kapazitaet);
//
//        DefaultFuzzyScheduleRulesBuilder defaultFuzzyScheduleRulesBuilder = new DefaultFuzzyScheduleRulesBuilder(kapa);
//        Schedule s = new Schedule(new InstanceHandler(defaultFuzzyScheduleRulesBuilder));
//
//        // Fuzzy-Volumen festlegen
//        DiscretizedFuzzyInterval startA = FuzzyFactory.createLinearInterval(20, 30, 5, 5, resolution);
//        DiscretizedFuzzyInterval durationA = FuzzyFactory.createLinearInterval(40, 45, 3, 3, resolution);
//        DiscretizedFuzzyInterval durationB = FuzzyFactory.createLinearInterval(50, 60, 3, 3, resolution);
//        DiscretizedFuzzyInterval endA = startA.add(durationA);
//        DiscretizedFuzzyInterval startB = endA;
//        DiscretizedFuzzyInterval endB = startB.add(durationB);
//
//        FuzzyOperation<DiscretizedFuzzyInterval> A = new FuzzyOperation(durationA, 0.5);
//        A.setDemand(r1, new DoubleValue(2.0));
//        FuzzyOperation<DiscretizedFuzzyInterval> B = new FuzzyOperation(durationB, 0.5);
//        B.setDemand(r1, new DoubleValue(1.0));
//
//        LinearizedFunction1d pseudoNecessity = FuzzyDemandUtilities.getPseudoNecessity(A, startA, B, endA);
//
//        /**
//         * ############################################################
//         * Funktionen plotten.
//         * ############################################################
//         */
//        FuzzyFunctionPlotter workloadplotter = new FuzzyFunctionPlotter("Aulastungskurven");
//
//        double min = 0.;
//        double max = slot.getUntilWhen().doubleValue();
//        double dx = 0.01;
//
//        DefaultEarliestFuzzyScheduleRule rule = (DefaultEarliestFuzzyScheduleRule) s.getHandler().get(r1);
//        workloadplotter.addFunction(startA.membership, min, max, dx, "StartA");
//        workloadplotter.addFunction(endA.membership, min, max, dx, "StartB");
//        workloadplotter.addFunction(endB.membership, min, max, dx, "EndB");
//        workloadplotter.addFunction(pseudoNecessity, min, max, dx, "PseudoNecessity");
//
//        LinearizedFunction1d funcTest = FuzzyDemandUtilities.buildTmpFunction(pseudoNecessity, A.getDemand(r1).doubleValue(), B.getDemand(r1).doubleValue(), B.getBeta(), A.getBeta());
//        funcTest.getValue(25);
//        
//        workloadplotter.addFunction(funcTest, min, max, dx, "MinFunctions");
//        workloadplotter.plot(1200, 525, slot, true);
//
//        s.schedule(A, startA);
//
//        AdaptedLambda lambda = FuzzyDemandUtilities.getLambdaLeftPrecedenceDependend(s, B, startB);
//        System.out.println(lambda.lambdaL + ":" + lambda.lambdaR);
//
//    }
//}
