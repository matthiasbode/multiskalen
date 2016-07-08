///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.fuzzy.demo;
//
//import applications.fuzzy.scheduling.rules.DefaultEarliestFuzzyScheduleRule;
//import applications.fuzzy.scheduling.DefaultFuzzyScheduleRulesBuilder;
//import applications.fuzzy.operation.FuzzyDummyOperation;
//import applications.fuzzy.operation.FuzzyOperation;
//import applications.fuzzy.plotter.FuzzyFunctionPlotter;
//import applications.fuzzy.scheduling.rules.FuzzyDemandUtilities;
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
//import bijava.math.function.ScalarFunction1d;
//import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
//import fuzzy.number.discrete.DiscretizedFuzzyNumber;
//import fuzzy.number.discrete.FuzzyFactory;
//import fuzzy.number.discrete.interval.FuzzyInterval;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Set;
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
//public class BaggerTest {
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
//         * Auflösung der Fuzzy-Zahlen festlegen
//         */
//        int resolution = 2;
//
//        double pesLevel = 0.5;
//        //pesLevel = 0.320305;
//
//        //Kapizität einstellen:
//        double kapazitaet = 100.0;
//
//        // Nutzleistung für Anwendungsbeispiel ausgeben lassen
////        Nutzleistung qn = new Nutzleistung();
////        DiscretizedFuzzyInterval Qn_I = qn.get_Qn_I(resolution);
////        DiscretizedFuzzyNumber Qn_N = qn.get_Qn_N(resolution);
//
//        // Nutzleistungsplotter Plotter definieren
////        HashMap<ScalarFunction1d, String> q = new HashMap();
////        FunctionPlotter p_q = new FunctionPlotter();
////        q.put(Qn_I.getMembershipFunction(), "QnI");
////        q.put(Qn_N.getMembershipFunction(), "QnN");
////        p_q.plotFunctions(q, Qn_I.getC1()-1., Qn_I.getC2()+1.);
//        /**
//         * Definieren der Resourcen
//         */
//        ArrayList<Resource> resources = new ArrayList<>();
//        Resource r1 = new ResourceImplementation("Bagger");
//        
//        r1.setTemporalAvailability(slot);
//        
//        resources.add(r1);
//        
//
//        // Volumen festlegen
//        // Operation 1:
//        double v1 = 4500.; // in m^3 (500) 4500
//        // Operation 2:
////        double v2 = 20.0 *  20.0 * 2.5; // in m^3 (1000)
////        // Operation 3:
////        double v3 = 10.0 * 100.0 * 2.5; // in m^3 (2500)
////        // Operation 4:
////        double v4 = 50.0 *  50.0 * 2.0; // in m^3 (5000)
//
//        // Volumen-Faktor festlegen
//        double VF = 0.01;
//        double VFab = 0.025;
//
//        double dx = 0.01;
//
//        // Fuzzy-Volumen festlegen
//        DiscretizedFuzzyInterval vFI1 = FuzzyFactory.createLinearInterval(v1 - v1 * VF, v1 + v1 * VF, v1 * VFab, v1 * VFab, resolution);
////        DiscretizedFuzzyInterval vFI2 = new DiscretizedFuzzyInterval(v2-v2*VF, v2+v2*VF, v2*VFab, v2*VFab, resolution);
////        DiscretizedFuzzyInterval vFI3 = new DiscretizedFuzzyInterval(v3-v3*VF, v3+v3*VF, v3*VFab, v3*VFab, resolution);
////        DiscretizedFuzzyInterval vFI4 = new DiscretizedFuzzyInterval(v4-v4*VF, v4+v4*VF, v4*VFab, v4*VFab, resolution);
//
//        // Ausdruck als PNG
////        JFreeChart createChart = FunctionPlotter.createFuzzyChart(new LinearizedMembershipFunction1d(vFI1.getValues()), vFI1.getC1()-10., vFI1.getC2()+10., "Volumen: 4500");
////        File f = new File("/Users/Brandy/Documents/Studienarbeit/Ausdrucke/", sdf.format(now)+"_V=4500.png");
////        createChart.removeLegend();
////        ChartUtilities.saveChartAsPNG(f, createChart, 500, 500);
//        // Fuzzy Dauern ausrechnen
////        DiscretizedFuzzyInterval dFI1 = vFI1.div(Qn_I);
////        System.out.println(dFI1.getMean());
////        DiscretizedFuzzyInterval dFI2 = vFI1.div(Qn_I);
////        DiscretizedFuzzyInterval dFI3 = vFI1.div(Qn_I);
////        DiscretizedFuzzyInterval dFI4 = vFI1.div(Qn_I);
//
//        // Fuzzy-Dauer Plotter definieren
////        FuzzyFunctionPlotter p_d = new FuzzyFunctionPlotter();
////        p_d.addFunction(dFI1.getMembershipFunction(), dFI1.getC1(), dFI1.getC2(), dx, "Dauer");
////        d.put(dFI2.getMembershipFunction(), "d2");
////        d.put(dFI3.getMembershipFunction(), "d3");
////        d.put(dFI4.getMembershipFunction(), "d4");
////        p_d.plot(800, 600, null, false);
//
//        // Fuzzy-Dauer Plotter definieren // VOLUMEN plotten
////        HashMap<ScalarFunction1d, String> v = new HashMap();
////        FunctionPlotter p_v = new FunctionPlotter();
////        v.put(vFI1.getMembershipFunction(), "Volumen");
////        p_v.plotFunctions(v, vFI1.getC1()-1., vFI1.getC2()+1.);
//        /**
//         * Definieren der FuzzyOperationen
//         */
//        // Ressourcenbedarf festlegen
//        double rd1 = 1.0;
//        
//
//        ArrayList<Operation> ops = new ArrayList<>();
//        FuzzyOperation o0 = new FuzzyDummyOperation(resolution, true);
//        FuzzyOperation o1 = new FuzzyOperation(FuzzyFactory.createLinearInterval(60*1000, 20*1000), pesLevel);
//        o1.setDemand(r1, new DoubleValue(rd1));
//        
//        FuzzyOperation o2 = new FuzzyOperation(FuzzyFactory.createLinearInterval(60*1000, 20*1000), pesLevel);
//        o2.setDemand(r1, new DoubleValue(rd1));
//        
//        FuzzyOperation o3 = new FuzzyOperation(FuzzyFactory.createLinearInterval(60*1000, 20*1000), pesLevel);
//        o3.setDemand(r1, new DoubleValue(rd1));
//        
//        FuzzyOperation o4 = new FuzzyOperation(FuzzyFactory.createLinearInterval(60*1000, 20*1000), pesLevel);
//        o4.setDemand(r1, new DoubleValue(rd1));
//        
//        FuzzyOperation o5 = new FuzzyDummyOperation(resolution, false);
//
//        /**
//         * Hinzufügen der Operationen
//         */
//        ops.add(o0);
//        ops.add(o1);
////        ops.add(o2);
////        ops.add(o3);
////        ops.add(o4);
////        ops.add(o5);
//
//        /**
//         * Zeitrestriktionen zwischen den Operationen
//         */
//        TimeRestrictions tr = new TimeRestrictions();
//        tr.putMinRestriction(o2, o3, o2.getDuration());
//        tr.putMinRestriction(o1, o5, o1.getDuration());
//        tr.putMinRestriction(o3, o5, o3.getDuration());
//        tr.putMinRestriction(o4, o5, o4.getDuration());
//
//        /**
//         * Einplanregeln für die Ressourcen festlegen
//         */
//        DefaultFuzzyScheduleRulesBuilder builder = new DefaultFuzzyScheduleRulesBuilder();
//        builder.put(r1, kapazitaet);
// 
//
//        /**
//         * Erstelle ActivityOnNodeGraphen
//         */
//        ActivityOnNodeGraph graph = ActivityOnNodeBuilder.build(ops, tr);
//
//        // Graph Plotten
//        //ExportToGraphML.exportToGraphML(graph, "/Users/Brandy/Documents/Studienarbeit/Ausdrucke/"+sdf.format(now)+"ActivityOnNodeGraph.graphml");
//
//        /* *
//         * Komplettes Problem
//         * ops, resourceRestrictions.getResources(), builder
//         */
//        DefaultSchedulingProblem<Operation> problem = new DefaultSchedulingProblem<Operation>(null, ops, resources, builder, graph);
//
//        /**
//         * Verwaltet die Einplanungen für die einezlnen Ressourcen.
//         */
//        InstanceHandler handler = new InstanceHandler(builder);
//
//        /**
//         * Eigentliches Erzeugen.
//         */
//        Schedule result = new Schedule(handler);
//
//        /**
//         * Fuzzy Start festlegen
//         */
//        //DiscretizedFuzzyInterval start = new DiscretizedFuzzyInterval((-5./60.)*0.15, (5./60.)*0.15, (5./60.)-(5./60.)*0.15, (5./60.)-(5./60.)*0.15,resolution); System.out.println("S_global: "+start.toString());
//        DiscretizedFuzzyInterval start = FuzzyFactory.createLinearInterval(-4., 4., 16., 16., resolution);
//
//        Operation oPre = null;
//        for (Operation o : ops) {
//            if (o.getId() > 1) {
//                start = (DiscretizedFuzzyInterval) result.getMapOperationToStartTime().get(oPre).add(oPre.getDuration());
//            }
//            System.out.println("S: " + o.getId() + ": " + start.toString());
//
//            //boolean test = true;
//            for (Resource resource : (Set<Resource>) o.getRequieredResources()) {
//                DefaultEarliestFuzzyScheduleRule rule = (DefaultEarliestFuzzyScheduleRule) handler.get(resource);
//
//                if (!rule.canSchedule(result, o, start)) {
//                    //test = false;
//                    DiscretizedFuzzyInterval step = FuzzyFactory.createLinearInterval(0.1, 0.1, 0., 0., resolution);
//
//                    boolean noSchedule = true;
//                    while (noSchedule) {
//                        // nächsten Startpunkt erzeugen
//                        start = start.add(step);
//                        noSchedule = !rule.canSchedule(result, o, start);
//                        //System.out.println("Aktueller Versuch ("+o.getId()+"): "+start.toString());
//                    }
//
////                    start = (DiscretizedFuzzyInterval) rule.getNextPossibleBundleStartTime(result, resource, (FuzzyOperation) o, start, step);
////                    System.out.println("Start verschoben: "+start.toString());
//                    //throw new IllegalArgumentException("Operation nicht einplanbar!");
//                }
//            }
//            result.schedule(o, start);
//            oPre = o;
//        }
//
//        /**
//         * ############################################################
//         * Funktionen plotten.
//         * ############################################################
//         */
//        //HashMap<ScalarFunction1d, String> fs = new HashMap();
//        double min = -20.;
//        double max = 325.;
// 
//        
//        for (Resource resource : problem.getResources()) {
//            FuzzyFunctionPlotter workloadplotter = new FuzzyFunctionPlotter("Auslastung der Ressource 'Bagger' (β=" + pesLevel + ")");
//            for (Operation operation : result.getOperationsForResource(resource)) {
//                //System.out.println(operation + " ---> " + result.get(operation));
//                FuzzyOperation ofuz = (FuzzyOperation) operation;
//                FuzzyInterval fuzStart = (FuzzyInterval) result.getStartTimes().get(operation);
//
//                workloadplotter.addFunction(fuzStart.membership, min, max, dx, "Start durch Vorgang " + ofuz.getId());
//                workloadplotter.addFunction(fuzStart.add(ofuz.getDuration()).membership, min, max, dx, "Ende durch Vorgang " + ofuz.getId());
//
//                workloadplotter.addFunction(FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(ofuz, resource, fuzStart, result.fuzzyWorkloadParameters.get(ofuz)), min, max, dx, "Auslastung durch Vorgang " + ofuz.getId());
//                workloadplotter.addFunction(FuzzyDemandUtilities.getNecessityFunction(ofuz, fuzStart), min, max, dx, "Notwendigkeit durch Vorgang " + ofuz.getId());
//                workloadplotter.addFunction(FuzzyDemandUtilities.getPossibilityFunction(ofuz, fuzStart), min, max, dx, "Möglichkeit durch Vorgang " + ofuz.getId());
//
//                workloadplotter.addFunction(FuzzyDemandUtilities.getLambdaMinFunction(ofuz, fuzStart), min, max, dx, "LambdaMin durch Vorgang " + ofuz.getId());
//                workloadplotter.addFunction(FuzzyDemandUtilities.getLambdaMaxFunction(ofuz, fuzStart), min, max, dx, "LambdaMax durch Vorgang " + ofuz.getId());
//            }
//            // plotten 
//            workloadplotter.getChart().setTitle(resource.toString());
//            workloadplotter.plot(1200, 525, null, true);
//        }
//
//    }
//}
