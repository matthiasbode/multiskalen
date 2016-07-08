///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.fuzzy.demo;
//
//import applications.fuzzy.scheduling.DefaultFuzzyScheduleRulesBuilder;
//import applications.fuzzy.operation.FuzzyDummyOperation;
//import applications.fuzzy.operation.FuzzyOperation;
//import applications.fuzzy.plotter.FunctionPlotterALT;
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
// * Einfaches Beispiel. i | 1 2 3 4 ----------------------- p_i | 4 3 5 8 r_i1 |
// * 2 1 2 2 r_i2 | 3 5 2 4
// *
// * Kapazitäten für R1 = 5, R2 = 7
// *
// * @author bode
// */
//public class ManualScheduleTestFuzzy_2 {
//
//    public static void main(String[] args) throws IOException {
//        /**
//         * Zeitfenster efinieren
//         */
//        TimeSlot slot = TimeSlot.create(0, 100);
//        
//        /**
//         * Auflösung der Fuzzy-Zahlen festlegen
//         */
//        int resolution = 200;
//        
//        double pesLevel = 0.5;
//        
//        /**
//         * Definieren der Resourcen
//         */
//        ArrayList<Resource> resources = new ArrayList<>();
//        Resource r1 = new ResourceImplementation("R1");
//        Resource r2 = new ResourceImplementation("R2");
//        r1.setTemporalAvailability(slot);
//        r2.setTemporalAvailability(slot);
//        resources.add(r1);
//        resources.add(r2);
//
//        /**
//         * Definieren der FuzzyOperationen
//         */
//        double rd1 = 1.;
//        double rd2 = 5.;
//        
//        ArrayList<Operation> ops = new ArrayList<>();
//        FuzzyOperation o0 = new FuzzyDummyOperation(resolution, true);
//        FuzzyOperation o1 = new FuzzyOperation(FuzzyFactory.createLinearInterval(3.0, 4.0, 1.0, 1.0, resolution),pesLevel);
//        o1.setDemand(r1, new DoubleValue(rd1));
//        o1.setDemand(r2, new DoubleValue(rd2));
//        FuzzyOperation o2 = new FuzzyOperation(FuzzyFactory.createLinearInterval(4.0, 5.0, 1.0, 1.0, resolution),pesLevel);
//        o2.setDemand(r1, new DoubleValue(rd1));
//        o2.setDemand(r2, new DoubleValue(rd2));
//        FuzzyOperation o3 = new FuzzyOperation(FuzzyFactory.createLinearInterval(5.0, 6.0, 1.0, 1.0, resolution),pesLevel);
//        o3.setDemand(r1, new DoubleValue(rd1));
//        o3.setDemand(r2, new DoubleValue(rd2));
//        FuzzyOperation o4 = new FuzzyOperation(FuzzyFactory.createLinearInterval(6.0, 7.0, 1.0, 1.0, resolution),pesLevel);
//        o4.setDemand(r1, new DoubleValue(rd1));
//        o4.setDemand(r2, new DoubleValue(rd2));
//        FuzzyOperation o5 = new FuzzyDummyOperation(resolution, false);
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
//        builder.put(r1, 1.8);
//        builder.put(r2, 7.5);
//
//        /**
//         * Erstelle ActivityOnNodeGraphen
//         */
//        ActivityOnNodeGraph graph = ActivityOnNodeBuilder.build(ops, tr);
//        
//        // aktuelle Zeit für Dateinamen festhalten
//        java.util.Date now = new java.util.Date();
//        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
//        
//        // Graph Plotten
//        //ExportToGraphML.exportToGraphML(graph, "/Users/Brandy/Documents/Studienarbeit/Ausdrucke/"+sdf.format(now)+"ActivityOnNodeGraph.graphml");
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
//         * Fuzzy Start festlegen
//         */
//        DiscretizedFuzzyInterval start = FuzzyFactory.createLinearInterval(1.0, 1.25, 0.25, 0.25, resolution);
//       
//        /**
//         * Alle Operationen einplanen!
//         */
//        Operation oPre = null;
//        for (Operation o : ops) {
//            if (o.getId() > 1) start = (DiscretizedFuzzyInterval) result.getMapOperationToStartTime().get(oPre).add(oPre.getDuration());
//            //boolean test = true;
//            for (Resource resource : (Set<Resource>) o.getRequieredResources()) {
//                ScheduleRule rule = handler.get(resource);
//                System.out.println("Rule:" + rule.toString());
//                if (!rule.canSchedule(result, o, start)) {
//                    //test = false;
//                    
////                    start = (DiscretizedFuzzyInterval) rule.getNextPossibleBundleStartTime(result, o, slot);
////                    System.out.println("Start verschoben!");
//                    throw new IllegalArgumentException("Operation nicht einplanbar!");
//                }
//            }
//            result.schedule(o, start);
//            oPre = o;
//        }
//
//        /**
//         * Funktionen plotten.
//         */
//        FunctionPlotterALT p = new FunctionPlotterALT();
//        HashMap<ScalarFunction1d, String> fs = new HashMap();
//        
//        boolean R1OK = true;
//        boolean R2OK = false;
//        
//        /**
//         * Ausgabe der Startzeit nach Ressource.
//         */
//        //System.out.println("##########################");
//        for (Resource resource : problem.getResources()) {
//            System.out.println("Resource: " + resource);
//            for (Operation operation : result.getOperationsForResource(resource)) {
//                //System.out.println(operation + " ---> " + result.get(operation));
//                FuzzyOperation ofuz = (FuzzyOperation) operation;
//                FuzzyInterval fuzStart = (FuzzyInterval) result.getStartTimes().get(operation);
//                if (resource.getID()=="R1"){
//                    //fs.put(ofuz.getPresenceFunctionAtPessimisticLevel(fuzStart, pesLevel), "P of O"+ofuz.getId());
//                }
//                // nur Resourcenbedarf von R1 anzeigen
//                if (resource.getID()=="R1" && R1OK){
//                    fs.put(FuzzyDemandUtilities.getDemandFunctionAtLambdaLevel(ofuz,resource, fuzStart, result.additionalInformation.get(operation)), "W of O"+ofuz.getId()+"/"+resource.getID());
//                }
//                if (resource.getID()=="R2" && R2OK){
//                    fs.put(FuzzyDemandUtilities.getDemandFunctionAtLambdaLevel(ofuz,resource, fuzStart,result.additionalInformation.get(operation)), "W of O"+ofuz.getId()+"/"+resource.getID());
//                }
//            }
//        }
//        
//        // Funktionen auswählen
//        //fs.put(start.getMembershipFunction(), "Start");
//        //fs.put(ende.getMembershipFunction(), "Finish");
//        //fs.put(o.getPossibilityFunction(start), "Possibility");
//        //fs.put(ops, "Ms of O1");
//        //fs.put(ope, "Me of O1");
////        fs.put(opm, "M of O1");
//        //fs.put(ons, "Ns of O1");
//        //fs.put(one, "Ne of O1");
////        fs.put(onm, "N of O1");
////        fs.put(oLamdaMin, "N (Lmin="+o.getLamdaMin(start)+")");
////        fs.put(oLamdaMax, "M (Lmax="+o.getLamdaMax(start)+")");
////        fs.put(pF, "P (beta="+pesLevel+")");
//        
//
//        for (Resource resource : problem.getResources()) {
//            ScheduleRule scheduleRule = result.getHandler().get(resource);
//            if (scheduleRule instanceof FuzzyFunctionBasedRule) {
//                FuzzyFunctionBasedRule sfb = (FuzzyFunctionBasedRule) scheduleRule;
//                if (resource.getID() == "R1" && R1OK){
//                    fs.put(sfb.getWorkloadFunction(), "WG of R1");
//                }
//                if (resource.getID() == "R2" && R2OK){
//                    fs.put(sfb.getWorkloadFunction(), "WG of R2");
//                }
//                
//                // Ausdruck als PNG
////                JFreeChart createChart = FunctionPlotterALT.createFuzzyChart(
////                        sfb.getWorkloadFunction(), new TimeSlot(new LongValue(0), new LongValue(30)), "Auslastung von "+resource.getID());
////                File f = new File("/Users/Brandy/Documents/Studienarbeit/Ausdrucke/", sdf.format(now)+
////                        "_WorkloadFunction_"+resource.getID()+".png");
////                ChartUtilities.saveChartAsPNG(f, createChart, 900, 600);
//            }
//            
//            
//        }
//        
//        // plotten 
//        p.plotFunctions(fs,0., 30.);
//       
//    }
//}
