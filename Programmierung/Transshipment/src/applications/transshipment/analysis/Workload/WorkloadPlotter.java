/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.analysis.Workload;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.SubResource;
import applications.mmrcsp.model.resources.SuperResource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScalarFunctionBasedRule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.transshipment.analysis.Analysis;
import applications.transshipment.model.operations.transport.TransportOperation;

import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.problem.TerminalProblem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.CraneMotionCalculator;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.multiscale.model.Scale;
import java.awt.Color;
import util.bucketing.BucketPlotter;
import util.bucketing.Bucket;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.function.StepFunction;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.bucketing.Bucketing;
import math.DoubleValue;
import math.FieldElement;
import math.LongValue;
import math.geometry.ParametricLinearCurve3d;
import org.jfree.chart.plot.XYPlot;
import util.chart.FunctionPlotter;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode Auslastungsplotter für die einzelnen Ressourcen über
 * Zeitintervalle
 */
public class WorkloadPlotter implements Analysis {

    public static long dt = 10 * 60 * 1000;
    public static boolean plotSetup = true;

    @Override
    public void analysis(LoadUnitJobSchedule schedule, MultiJobTerminalProblem problem, File folder) {

        TimeSlot allOverTimeSlot = problem.getOptimizationTimeSlot();

        for (ConveyanceSystem conveyanceSystem : problem.getTerminal().getConveyanceSystems()) {
            if (conveyanceSystem instanceof LCSystem) {
                LCSystem lcSystem = (LCSystem) conveyanceSystem;
                for (Agent agent : lcSystem.getSharingResources()) {
                    getPlot(agent, allOverTimeSlot, schedule, problem, folder, true);
                    getBucketPlot(agent, allOverTimeSlot, schedule, problem, folder, dt);
                }
            }
            getPlot(conveyanceSystem, allOverTimeSlot, schedule, problem, folder, false);
            getBucketPlot(conveyanceSystem, allOverTimeSlot, schedule, problem, folder, dt);
        }
        for (LoadUnitStorage storage : problem.getTerminal().getStorages()) {
            String s = storage.toString().replace(" ", "_");
            s = s.split("\\(|\\)")[0];
            s = s.replace("\t", "");
            File f = new File(folder, "StoragePlot_" + s + ".png");
            getStoragePlot(storage, allOverTimeSlot, schedule, problem, f, false);

        }
    }

    public void getPlot(ConveyanceSystem conveyanceSystem, TimeSlot allOverTimeSlot, Schedule schedule, TerminalProblem problem, File folder, boolean detail) {
        File f = new File(folder, "Detail_Agent" + conveyanceSystem.toString() + ".png");
        if (schedule == null) {
            return;
        }
        ScheduleRule scheduleRule = schedule.getHandler().get(conveyanceSystem);
        if (scheduleRule == null) {
            return;
        }
        if (conveyanceSystem instanceof Crane) {

            StepFunction functionTransport = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));
            StepFunction functionSetup = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));
            Collection<Operation> operationsForResource = schedule.getOperationsForResource(conveyanceSystem);
            List<Operation> list = new ArrayList<>(operationsForResource);
            Collections.sort(list, new Comparator<Operation>() {

                @Override
                public int compare(Operation o1, Operation o2) {
                    return Integer.compare(o1.getId(), o2.getId());
                }
            });

            HashMap<String, StepFunction> forTasks = new HashMap<>();
            for (Operation operation : list) {
                if (operation instanceof TransportOperation) {
                    FieldElement start = schedule.get(operation);
                    FieldElement end = start.add(operation.getDuration());
                    StepFunction subFunction = new StepFunction(start, end, new DoubleValue(1.0));
                    forTasks.put("O_" + operation.getId(), subFunction);
                    functionTransport = functionTransport.add(subFunction);
                }
            }

            if (plotSetup) {
                for (Operation operation : list) {
                    if (operation instanceof TransportOperation) {
                        TransportOperation top1 = (TransportOperation) operation;
                        FieldElement start = schedule.get(operation);
                        FieldElement end = start.add(operation.getDuration());
                        TransportOperation top2 = null;
                        if (problem.getScale().equals(Scale.micro)) {
                            Operation higher = schedule.getOperationsForResourceAsNavigableSet(conveyanceSystem).higher(top1);
                            if (higher != null) {
                                top2 = (TransportOperation) higher;
                            }
                        } else {
                            Operation higher = schedule.getOperationsForResourceAsNavigableSet(conveyanceSystem).higher(top1);
                            higher = schedule.getOperationsForResourceAsNavigableSet(conveyanceSystem).higher(higher);
                            if (higher != null) {
                                top2 = (TransportOperation) higher;
                            }
                        }
                        if (top2 != null) {

                            ParametricLinearCurve3d transportMotion = CraneMotionCalculator.getSettingUpMotion((Crane) conveyanceSystem, top1.getDestination(), top2.getOrigin());
                            if (transportMotion != null) {
                                long duration = transportMotion.getDuration();
                                StepFunction subFunction = new StepFunction(end, end.add(new LongValue(duration)), new DoubleValue(1.0));
                                forTasks.put("O_Setup" + operation.getId(), subFunction);
                                functionSetup = functionSetup.add(subFunction);
                            }
                        }
                    }

                }
            }

            Map<String, StepFunction> functions = new HashMap<>();
            functions.put("Transport", functionTransport);
            if (plotSetup) {
                functions.put("Setup", functionSetup);
            }

            JFreeChart createChart = FunctionPlotter.createAreaDateChart(functions, allOverTimeSlot, "Für Resource " + conveyanceSystem.toString());
            XYPlot plot = (XYPlot) createChart.getPlot();
            plot.getRenderer().setSeriesPaint(0, new Color(0, 80, 155));
            plot.getRenderer().setSeriesPaint(1, new Color(200, 211, 23));

            if (detail) {
                createChart = FunctionPlotter.createAreaDateChart(forTasks, allOverTimeSlot, "Für Resource " + conveyanceSystem.toString());
            }
            try {
                ChartUtilities.saveChartAsPNG(f, createChart, 2200, 435);
            } catch (IOException ex) {
                Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (scheduleRule instanceof ScalarFunctionBasedRule) {
            ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

            StepFunction workloadFunction = (StepFunction) sfb.getWorkloadFunction();

            JFreeChart createChart = FunctionPlotter.createAreaDateChart(workloadFunction, allOverTimeSlot, "Für Resource " + conveyanceSystem.toString());
            XYPlot plot = (XYPlot) createChart.getPlot();
            plot.getRenderer().setSeriesPaint(0, new Color(0, 80, 155));

            try {
                ChartUtilities.saveChartAsPNG(f, createChart, 2200, 435);
            } catch (IOException ex) {
                Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void getBucketPlot(ConveyanceSystem conveyanceSystem, TimeSlot allOverTimeSlot, Schedule schedule, TerminalProblem problem, File folder, long dt) {
        File f = new File(folder, "Bucketing_" + conveyanceSystem.toString() + ".png");
        StepFunction function = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));
        StepFunction functionSetup = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));

        Collection<Operation> operationsForResource = schedule.getOperationsForResource(conveyanceSystem);
        for (Operation operation : operationsForResource) {
            if (operation instanceof TransportOperation) {
                FieldElement start = schedule.get(operation);
                FieldElement end = start.add(operation.getDuration());
                StepFunction subFunction = new StepFunction(start, end, new DoubleValue(1.0));
                function = function.add(subFunction);
            }
        }
        if (plotSetup) {
            if (conveyanceSystem instanceof Crane) {
                for (Operation operation : operationsForResource) {
                    if (operation instanceof TransportOperation) {
                        TransportOperation top1 = (TransportOperation) operation;
                        FieldElement start = schedule.get(operation);
                        FieldElement end = start.add(operation.getDuration());

                        TransportOperation top2 = null;
                        if (problem.getScale().equals(Scale.micro)) {
                            Operation higher = schedule.getOperationsForResourceAsNavigableSet(conveyanceSystem).higher(top1);
                            if (higher != null) {
                                top2 = (TransportOperation) higher;
                            }
                        } else {
                            Operation higher = schedule.getOperationsForResourceAsNavigableSet(conveyanceSystem).higher(top1);
                            higher = schedule.getOperationsForResourceAsNavigableSet(conveyanceSystem).higher(higher);
                            if (higher != null) {
                                top2 = (TransportOperation) higher;
                            }
                        }

                        if (top2 != null) {
                            ParametricLinearCurve3d transportMotion = CraneMotionCalculator.getSettingUpMotion((Crane) conveyanceSystem, top1.getDestination(), top2.getOrigin());
                            if (transportMotion != null) {
                                long duration = transportMotion.getDuration();
                                StepFunction subFunction = new StepFunction(end, end.add(new LongValue(duration)), new DoubleValue(1.0));
                                functionSetup = functionSetup.add(subFunction);
                            }
                        }
                    }
                }
            }
        }

        double max = function.getMax().getValue().doubleValue();
        ScheduleRule r = schedule.getHandler().get(conveyanceSystem);
        if (r instanceof ScalarFunctionBasedRule) {
            ScalarFunctionBasedRule rule = (ScalarFunctionBasedRule) r;
            max = rule.getMax();
        }
        Bucketing transportBucket = new Bucketing(conveyanceSystem, function, allOverTimeSlot, dt);
        Bucketing setupBucket = new Bucketing(conveyanceSystem, functionSetup, allOverTimeSlot, dt);
        Map<String, Bucketing> functions = new HashMap<>();

        File fjt = new File(folder, "Bucketing_Transport_" + conveyanceSystem.toString() + ".json");
        File fjs = new File(folder, "Bucketing_Setup_" + conveyanceSystem.toString() + ".json");
        JSONSerialisierung.exportJSON(fjt, transportBucket, true);
        JSONSerialisierung.exportJSON(fjs, setupBucket, true);

        functions.put("Transport", transportBucket);
        if (conveyanceSystem instanceof Crane && plotSetup) {
            functions.put("Setup", setupBucket);
        }
        JFreeChart bucketChart = BucketPlotter.createChart(functions, allOverTimeSlot, "Für Resource " + conveyanceSystem.toString(), max);
        XYPlot plot = (XYPlot) bucketChart.getPlot();

        plot.getRenderer()
                .setSeriesPaint(0, new Color(0, 80, 155));
        plot.getRenderer()
                .setSeriesPaint(1, new Color(200, 211, 23));

        try {
            ChartUtilities.saveChartAsPNG(f, bucketChart, 2200, 435);
        } catch (IOException ex) {
            Logger.getLogger(WorkloadPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getStoragePlot(LoadUnitStorage storage, TimeSlot allOverTimeSlot, Schedule schedule, TerminalProblem problem, File folder, boolean detail) {
        File f = new File(folder, "Bucketing Agent" + storage.toString() + ".png");

        if (schedule == null) {
            return;
        }
        ScheduleRule scheduleRule = schedule.getHandler().get(storage);
        if (scheduleRule == null) {
            return;
        }
        if (scheduleRule instanceof ScalarFunctionBasedRule) {
            ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) scheduleRule;

            StepFunction workloadFunction = (StepFunction) sfb.getWorkloadFunction();

            JFreeChart createChart = FunctionPlotter.createAreaDateChart(workloadFunction, allOverTimeSlot, "Für Resource " + storage.toString());
            XYPlot plot = (XYPlot) createChart.getPlot();
            plot.getRenderer().setSeriesPaint(0, new Color(0, 80, 155));
            try {
                ChartUtilities.saveChartAsPNG(f, createChart, 2200, 435);

            } catch (IOException ex) {
                Logger.getLogger(WorkloadPlotter.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else if (storage instanceof SuperResource && !(storage instanceof SubResource)) {

            StepFunction function = new StepFunction(allOverTimeSlot.getFromWhen(), allOverTimeSlot.getUntilWhen(), new DoubleValue(0));

            for (Resource resource : schedule.getResources()) {
                if (resource instanceof SubResource) {

                    Resource result = resource;
                    while ((result instanceof SubResource)) {
                        SubResource sr = (SubResource) result;
                        result = (LoadUnitStorage) sr.getSuperResource();
                    }

                    SuperResource sr = (SuperResource) result;
                    if (sr.equals(storage)) {
                        ScheduleRule rule = schedule.getHandler().get(resource);
                        if (rule instanceof ScalarFunctionBasedRule) {
                            ScalarFunctionBasedRule sfb = (ScalarFunctionBasedRule) rule;
                            if (sfb.getWorkloadFunction() instanceof StepFunction) {
                                function = function.add((StepFunction) sfb.getWorkloadFunction());
                            }

                        }
                    }
                }
            }

            int capInt = Integer.MIN_VALUE;
            if (storage instanceof LocationBasedStorage) {
                LocationBasedStorage LStor = (LocationBasedStorage) storage;

                capInt = LStor.getNumberOfStorageLocations();
            }
            String cap = "";
            if (capInt != Integer.MIN_VALUE) {
                cap = "Kapazität: " + capInt;
            }
            JFreeChart createChart = FunctionPlotter.createAreaDateChart(function, allOverTimeSlot, "Für Resource " + storage.toString() + " " + cap);
            XYPlot plot = (XYPlot) createChart.getPlot();
            plot.getRenderer().setSeriesPaint(0, new Color(0, 80, 155));
            if (capInt != Integer.MIN_VALUE) {
                plot.getRangeAxis().setUpperBound(capInt);
            }

            try {
                ChartUtilities.saveChartAsPNG(f, createChart, 2200, 435);

            } catch (IOException ex) {
                Logger.getLogger(WorkloadPlotter.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
