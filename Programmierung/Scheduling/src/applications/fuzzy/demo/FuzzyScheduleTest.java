//package applications.fuzzy.demo;
//
//import applications.fuzzy.operation.AdaptedLambda;
//import applications.fuzzy.scheduling.DefaultFuzzyScheduleRulesBuilder;
//import applications.fuzzy.scheduling.rules.DefaultEarliestFuzzyScheduleRule;
//import applications.fuzzy.operation.FuzzyDummyOperation;
//import applications.fuzzy.operation.FuzzyOperation;
//import applications.fuzzy.plotter.FuzzyFunctionPlotter;
//import applications.fuzzy.scheduling.rules.FuzzyDemandUtilities;
//import applications.fuzzy.scheduling.rules.FuzzyFunctionBasedRule;
//import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
//import applications.mmrcsp.model.basics.TimeSlot;
//import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
//import applications.mmrcsp.model.operations.Operation;
//import applications.mmrcsp.model.problem.DefaultSchedulingProblem;
//import applications.mmrcsp.model.resources.Resource;
//import applications.mmrcsp.model.resources.ResourceImplementation;
//import applications.mmrcsp.model.restrictions.TimeRestrictions;
//import applications.mmrcsp.model.schedule.Schedule;
//import applications.mmrcsp.model.schedule.rules.InstanceHandler;
//import applications.mmrcsp.model.schedule.rules.ScheduleRule;
//import bijava.math.function.ScalarFunction1d;
//import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
//
//import fuzzy.number.discrete.FuzzyFactory;
//import fuzzy.number.discrete.interval.FuzzyInterval;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Set;
//import math.DoubleValue;
//
///**
// * Dies ist die Umsetzung des Anwendungsbeispiels
// *
// * @author brandt
// */
//public class FuzzyScheduleTest {
//
//    public static void main(String[] args) throws IOException {
//        /**
//         * Zeitfenster efinieren
//         */
//        TimeSlot slot = TimeSlot.create(0, 150);
//
//        // aktuelle Zeit für Dateinamen festhalten
//        java.util.Date now = new java.util.Date();
//        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
//
//        /**
//         * Auflösung der Fuzzy-Zahlen festlegen (Alphacut-Anzahl)
//         */
//        int resolution = 20;
//
//        // globales Pessimissmus-Level festegen (da alle Vorgänge gleich sind)
//        double pesLevel = 0.5;
//        //pesLevel = 0.320305; // entspricht klassicher Sicht
//
//        //Kapizität einstellen:
//        double kapazitaet = 1.0;
//
//        // Varianz-Faktor der Gaußverteilung festlegen
//        double varianz = 3.29; // = 99,9%
//
//        // Nutzleistung für das Anwendungsbeispiel ausgeben lassen (Gauß-Ansatz)
//        Nutzleistung qn = new Nutzleistung();
//        DiscretizedFuzzyInterval Qn_Gaussian = qn.get_Qn_I_gaussian(resolution, varianz);
//        DiscretizedFuzzyInterval Qn_Trapeziodal = qn.get_Qn_I(resolution);
//
//        // Schwerpunkt der Nutzleistung ausgeben
////        LinearizedMembershipFunction1d qn_function = new LinearizedMembershipFunction1d(Qn_Gaussian.getValues());
////        double xs = qn_function.getCenterOfGravityX(Qn_Gaussian.getC1(), Qn_Gaussian.getC2());
////        System.out.println("xS of Qn: "+xs+", mü: "+Qn_I.getMembership(xs));
//        // Nutzleistungsplotter Plotter definieren
////        FunctionPlotter p_q = new FunctionPlotter();
////        p_q.addFunction(Qn_Gaussian.getMembershipFunction(), Qn_Gaussian.getC1(), Qn_Gaussian.getC2(), 0.001, "Qn");
////        p_q.plot(800, 600, true);
//        /**
//         * Definieren der Resourcen
//         */
//        ArrayList<Resource> resources = new ArrayList<>();
//        Resource r1 = new ResourceImplementation("Bagger");
//        Resource r2 = new ResourceImplementation("Fahrer");
//        r1.setTemporalAvailability(slot);
//        r2.setTemporalAvailability(slot);
//        resources.add(r1);
//        resources.add(r2);
//
//        // Volumen für alle Operationen festlegen
//        double v = 4500.; // in m^3 (500) 4500
//
//        // Volumen-Faktor festlegen
//        // Abweichung des Kernbereichs (1%)
//        double VF = 0.01;
//        // Alpha- und Beta-Bereichgröße
//        double VFab = 0.025;
//
//        // Fuzzy-Volumen festlegen
//        DiscretizedFuzzyInterval vFI1 = FuzzyFactory.createFromGauss(v - v * VF, v + v * VF, v * VFab, v * VFab, resolution, varianz);
//
//        // Fuzzy Dauern für alle Operationen ausrechnen
//        DiscretizedFuzzyInterval dFI1 = vFI1.div(Qn_Gaussian);
//
//        // Fuzzy-Dauer Plotter erzeugen
//        HashMap<ScalarFunction1d, String> d = new HashMap();
//        FuzzyFunctionPlotter p_d = new FuzzyFunctionPlotter();
//        p_d.addFunction(dFI1.getMembershipFunction(), dFI1.getC1(), dFI1.getC2(), 0.01, "Dauer");
//        p_d.plot(800, 600,null, false);
//
//        /**
//         * Definieren der FuzzyOperationen
//         */
//        // Ressourcenbedarf festlegen
//        double R_bagger = 1.0; //Bagger
//        double R_fahrer = 1.0; // Baggerfahrer
//
//        ArrayList<Operation> ops = new ArrayList<>();
//        FuzzyOperation o0 = new FuzzyDummyOperation(resolution, true);
//        FuzzyOperation o1 = new FuzzyOperation(dFI1, pesLevel);
//        o1.setDemand(r1, new DoubleValue(R_bagger));
//        o1.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o2 = new FuzzyOperation(dFI1, pesLevel);
//        o2.setDemand(r1, new DoubleValue(R_bagger));
//        o2.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o3 = new FuzzyOperation(dFI1, pesLevel);
//        o3.setDemand(r1, new DoubleValue(R_bagger));
//        o3.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o4 = new FuzzyOperation(dFI1, pesLevel);
//        o4.setDemand(r1, new DoubleValue(R_bagger));
//        o4.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o5 = new FuzzyOperation(dFI1, pesLevel);
//        o5.setDemand(r1, new DoubleValue(R_bagger));
//        o5.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o6 = new FuzzyOperation(dFI1, pesLevel);
//        o6.setDemand(r1, new DoubleValue(R_bagger));
//        o6.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o7 = new FuzzyOperation(dFI1, pesLevel);
//        o7.setDemand(r1, new DoubleValue(R_bagger));
//        o7.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o8 = new FuzzyOperation(dFI1, pesLevel);
//        o8.setDemand(r1, new DoubleValue(R_bagger));
//        o8.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o9 = new FuzzyOperation(dFI1, pesLevel);
//        o9.setDemand(r1, new DoubleValue(R_bagger));
//        o9.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o10 = new FuzzyOperation(dFI1, pesLevel);
//        o10.setDemand(r1, new DoubleValue(R_bagger));
//        o10.setDemand(r2, new DoubleValue(R_fahrer));
//        FuzzyOperation o11 = new FuzzyDummyOperation(resolution, false);
//
//        /**
//         * Hinzufügen der Operationen
//         */
//        ops.add(o0);
//        ops.add(o1);
//        ops.add(o2);
//        ops.add(o3);
//        ops.add(o4);
//        ops.add(o5);
//        ops.add(o6);
//        ops.add(o7);
//        ops.add(o8);
//        ops.add(o9);
//        ops.add(o10);
//        ops.add(o11);
//
//        /**
//         * Zeitrestriktionen zwischen den Operationen
//         */
//        TimeRestrictions tr = new TimeRestrictions();
//        tr.putMinRestriction(o2, o3, o2.getDuration());
//        tr.putMinRestriction(o1, o5, o1.getDuration());
//        tr.putMinRestriction(o3, o5, o3.getDuration());
//        tr.putMinRestriction(o4, o5, o4.getDuration());
//        tr.putMinRestriction(o5, o6, o5.getDuration());
//        tr.putMinRestriction(o6, o7, o6.getDuration());
//        tr.putMinRestriction(o6, o7, o6.getDuration());
//        tr.putMinRestriction(o7, o8, o7.getDuration());
//        tr.putMinRestriction(o8, o9, o8.getDuration());
//        tr.putMinRestriction(o9, o10, o9.getDuration());
//        tr.putMinRestriction(o10, o11, o10.getDuration());
//
//        /**
//         * Einplanregeln für die Ressourcen festlegen
//         */
//        DefaultFuzzyScheduleRulesBuilder builder = new DefaultFuzzyScheduleRulesBuilder();
//        builder.put(r1, kapazitaet);
//        builder.put(r2, kapazitaet * 2);
//
//        /**
//         * Erstelle ActivityOnNodeGraphen
//         */
//        ActivityOnNodeGraph graph = ActivityOnNodeBuilder.build(ops, tr);
//
//        /* *
//         * Komplettes Problem
//         * ops, resourceRestrictions.getResources(), builder
//         */
//        DefaultSchedulingProblem<Operation> problem = new DefaultSchedulingProblem<Operation>(null,ops, resources, builder, graph);
//
//        /**
//         * Verwaltet die Einplanungen für die einezlnen Ressourcen.
//         */
//        InstanceHandler handler = new InstanceHandler(builder);
//
//        /**
//         * Eigentliches Erzeugen.
//         */
//        Schedule  result = new Schedule(handler);
//
//        /**
//         * Fuzzy-Start festlegen
//         */
//        DiscretizedFuzzyInterval start = FuzzyFactory.createFromGauss(-4., 4., 16., 16., resolution, varianz);
//
//        /**
//         * Alle Operationen einplanen!
//         */
//        FuzzyFunctionPlotter z = new FuzzyFunctionPlotter();
//        HashMap<ScalarFunction1d, String> zf = new HashMap();
//
//        Operation oPre = null;
//        for (Operation o : ops) {
//            if (o.getId() > 1) {
//                start = (DiscretizedFuzzyInterval) result.getMapOperationToStartTime().get(oPre).add(oPre.getDuration());
//            }
//            System.out.println("S: " + o.getId() + ": " + start.toString());
//
//            for (Resource resource : (Set<Resource>) o.getRequieredResources()) {
//                DefaultEarliestFuzzyScheduleRule rule = (DefaultEarliestFuzzyScheduleRule) handler.get(resource);
//
//                if (!rule.canSchedule(result, o, start)) {
//                    DiscretizedFuzzyInterval step = FuzzyFactory.createLinearInterval(0.1, 0.1, 0., 0., resolution);
//
//                    boolean noSchedule = true;
//                    while (noSchedule) {
//                        // nächsten Startpunkt erzeugen
//                        start = start.add(step);
//                        noSchedule = !rule.canSchedule(result, o, start);
//                    }
//                }
//            }
//            result.schedule(o, start);
//            if (o.getId() == 1) {
//                zf.put(start.getMembershipFunction(), "S" + o.getId());
//                DiscretizedFuzzyInterval End = start.add((DiscretizedFuzzyInterval) o.getDuration());
//                zf.put(End.getMembershipFunction(), "E" + o.getId());
//                FuzzyOperation oFuz = (FuzzyOperation) o;
//                zf.put(FuzzyDemandUtilities.getPresenceFunction(oFuz,start, new AdaptedLambda(start, pesLevel)), "PP" + o.getId());
//                zf.put(FuzzyDemandUtilities.getPossibilityFunction(oFuz,start), "PF" + o.getId());
//                zf.put(FuzzyDemandUtilities.getNecessityFunction(oFuz,start), "NF" + o.getId());
//            }
//            oPre = o;
//        }
//
//        /**
//         * Funktionen plotten.
//         *
//         */
//        FuzzyFunctionPlotter workloadplotter = new FuzzyFunctionPlotter("Auslastung der Ressource 'Bagger' (β=" + pesLevel + ")");
//        double min = -20.;
//        double max = 825.;
//        double dx = 0.01;
//
//        boolean R1OK = true;
//        boolean R2OK = false;
//
//        /**
//         * Ausgabe der Startzeit nach Ressource.
//         */
//        //System.out.println("##########################");
//        for (Resource resource : problem.getResources()) {
//            //System.out.println("Resource: " + resource);
//            for (Operation operation : result.getOperationsForResource(resource)) {
//                FuzzyOperation ofuz = (FuzzyOperation) operation;
//                FuzzyInterval fuzStart = (FuzzyInterval) result.getStartTimes().get(operation);
//                if ("Bagger".equals(resource.getID())) {
//                }
//                // nur Resourcenbedarf von R1 anzeigen
//                if ("Bagger".equals(resource.getID()) && R1OK) {
//                    workloadplotter.addFunction(FuzzyDemandUtilities.getDemandFunctionAtLambdaLevel(ofuz,resource, fuzStart, result.additionalInformation.get(ofuz)), min, max, dx, "Auslastung durch Vorgang " + ofuz.getId());
//                    System.out.println("Gesamtaufwand O" + ofuz.getId() + ": " + FuzzyDemandUtilities.getDemandFunctionAtLambdaLevel(ofuz,resource, fuzStart, result.additionalInformation.get(ofuz)).getIntegral(-25., 450.));
//                }
//                if ("Fahrer" == resource.getID() && R2OK) {
//                    //workloadplotter.addFunction(ofuz.getDemandFunctionAtPessimisticLevelOfResource(resource, fuzStart), min, max, dx, "Auslastung durch Vorgang "+ofuz.getId());
//                }
//            }
//        }
//
//        String s = "";
//
//        for (Resource resource : problem.getResources()) {
//            ScheduleRule scheduleRule = result.getHandler().get(resource);
//            if (scheduleRule instanceof FuzzyFunctionBasedRule) {
//                FuzzyFunctionBasedRule sfb = (FuzzyFunctionBasedRule) scheduleRule;
//                if ("Bagger".equals(resource.getID()) && R1OK) {
//                    workloadplotter.addFunction(sfb.getWorkloadFunction(), min, max, dx, "Gesamtauslastung");
//                    System.out.println("Gesamtaufwand komplett:" + sfb.getWorkloadFunction().getIntegral(-25., max));
//                    s = workloadplotter.getChart().getTitle() + " (Ages=" + sfb.getWorkloadFunction().getIntegral(-25., max) + ")";
//                }
//                if ("Fahrer".equals(resource.getID()) && R2OK) {
//                    //workloadplotter.addFunction(sfb.getWorkloadFunction(), min, max, dx, "Gesamtauslastung ");
//                }
//
//            }
//
//        }
//
//        // WorkloadFunktion plotten
//        workloadplotter.getChart().setTitle(s);
//        workloadplotter.plot(1200, 525,null, false);
//
//        // Plot von Linguistischen Variabeln
////        FunctionPlotter dr = new FunctionPlotter();
////        FunctionPlotter ga = new FunctionPlotter();
////        // Linguistische Variabeln "Betreibsfaktor: gut"
////        double FI = 0.;
////        double mN  = 0.950;
////        double c1N = 0.900;
////        double c2N = 0.975;
////        // Interval
////        double m1I = mN - (mN-c1N)*FI;
////        double m2I = mN + (c2N-mN)*FI;
////        double c1I = c1N;
////        double c2I = c2N;
////        // Fuzzy-Zahlen definieren
////        DiscretizedFuzzyInterval eta2_d = new DiscretizedFuzzyInterval(m1I, m2I, m1I-c1I, c2I-m2I, resolution);
////        DiscretizedFuzzyInterval eta2_g = new DiscretizedFuzzyInterval(m1I, m2I, m1I-c1I, c2I-m2I, resolution, varianz);
////        
////        dr.addFunction(eta2_d.getMembershipFunction(),eta2_d.getC1(),eta2_d.getC2(),0.0005, "Betriebsfaktor η2 ('gut')");
////        ga.addFunction(eta2_g.getMembershipFunction(),eta2_g.getC1(),eta2_g.getC2(),0.0005, "Betriebsfaktor η2 ('gut')");
////        
////        dr.plot(600, 400, false);
////        ga.plot(600, 400, false);
//    }
//}
