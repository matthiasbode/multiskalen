///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes;
//
//import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
//import applications.transshipment.model.operations.transport.RoutingTransportOperation;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//
///**
// *
// * @author bode
// */
//public class StrategyListVergleich implements PropertyChangeListener {
//
//    public HashMap<OperationPriorityRules.Identifier, Double> durchschnitt = new HashMap<>();
//    public int gesamt = 0;
//    public int gefunden = 0;
//
//    public StrategyListVergleich() {
//        List<OperationPriorityRules.Identifier> auswahl = Arrays.asList(OperationPriorityRules.Identifier.values());
//        for (OperationPriorityRules.Identifier identifier : auswahl) {
//            durchschnitt.put(identifier, 0.0);
//        }
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getPropertyName().equals("EligibleSet")) {
//            gesamt++;
//            ParallelScheduleGenerationScheme.Event event = (ParallelScheduleGenerationScheme.Event) evt.getNewValue();
//            List<OperationPriorityRules.Identifier> auswahl = Arrays.asList(OperationPriorityRules.Identifier.values());
//            OperationPriorityRules rules = new OperationPriorityRules(event.graph, event.ealosaes);
//            boolean passender = false;
//            for (OperationPriorityRules.Identifier identifier : auswahl) {
//                Comparator comp = rules.getMap(identifier);
//                ArrayList<RoutingTransportOperation> arrayList = new ArrayList(event.currentOperations);
//                Collections.sort(arrayList, comp);
//                int counter = 0;
//                for (int i = 0; i < Math.min(arrayList.size(), 1); i++) {
//                    if (arrayList.get(i).equals(event.currentOperations.get(i))) {
//                        counter++;
//                    }
//                }
//                 double uebereinstimmung = counter;
////                double uebereinstimmung = new Double(counter) / arrayList.size();
//                if (uebereinstimmung == 1.0) {
//                    passender = true;
//                }
//                if (!Double.isNaN(uebereinstimmung)) {
//                    durchschnitt.put(identifier, durchschnitt.get(identifier) + uebereinstimmung);
//                }
//            }
//            if (passender) {
//                gefunden++;
//            }
//        }
//    }
//
//}
