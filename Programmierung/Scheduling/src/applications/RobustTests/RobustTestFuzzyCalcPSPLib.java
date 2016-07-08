/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.RobustTests;

import applications.PSPLib.demo.CalcPSPLib;
import applications.fuzzy.PSPLib.MinMakeSpanEval;
import applications.fuzzy.operation.FuzzyOperation;
import applications.fuzzy.scheduling.rules.defaultImplementation.DefaultEarliestFuzzyScheduleRule;
import applications.fuzzy.scheduling.rules.defaultImplementation.FuzzyDemandUtilities;
import applications.fuzzy.scheduling.rules.FuzzyFunctionBasedRule;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.ga.priority.StandardPriorityDeterminator;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.problem.timeRestricted.DefaultTimeRestictedSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.scheduleSchemes.ParallelScheduleSchemeWithTime;
import applications.mmrcsp.model.schedule.test.OrderValid;
import com.google.common.collect.TreeMultimap;
import com.google.gson.reflect.TypeToken;
import fuzzy.number.FuzzyNumber;
import fuzzy.number.discrete.interval.FuzzyInterval;
import ga.Parameters;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.subList.ListIndividual;
import ga.individuals.subList.SubListIndividual;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import util.input.SingleModeFuzzyFile;
import util.jsonTools.JSONSerialisierung;
import util.workspace.ProjectOutput;
import java.util.Collections;
import java.util.Comparator;
import math.FieldElement;
import org.util.ExportToYed;
import util.ScheduleFitnessEvalationFunction;

/**
 *
 * @author bode
 */
public class RobustTestFuzzyCalcPSPLib {

    public static int numberOfInd = 1;
    public static int GENERATIONS = 1;
    static TreeMultimap<Integer, Operation> order;
    public static String f;

    public static void main(String[] args) throws FileNotFoundException {

        InputStream stream = RobustTestFuzzyCalcPSPLib.class.getResourceAsStream("../PSPLib/30/j301_3.sm");
        f = "/home/bode/Dokumente/Promo/Ergebnisse/Individidual.txt";
        f = "D:\\Eigene Dateien\\Eigene Dokumente\\Promo\\Ergebnisse\\Individidual.txt";
//        f = "C:\\Users\\bode\\Documents\\Promo\\Ergebnisse\\Individidual.txt";
        DeprecatedTestCalcPSPLib.run(stream, new FileInputStream(f), false);
        order = DeprecatedTestCalcPSPLib.order;
        for (Integer a : order.asMap().keySet()) {
            System.out.println(a + "-->" + order.asMap().get(a));
        }

        System.out.println("Auswertung");
        int sec = 1;
        stream = RobustTestFuzzyCalcPSPLib.class.getResourceAsStream("../PSPLib/30/j301_3.sm");
        SingleModeFuzzyFile.DurationFuzzyness = sec * 1000;
        run(stream);
        sec = 5;
        SingleModeFuzzyFile.DurationFuzzyness = sec * 1000;
        while (SingleModeFuzzyFile.DurationFuzzyness <= 60 * 1000) {
            stream = RobustTestFuzzyCalcPSPLib.class.getResourceAsStream("../PSPLib/30/j301_3.sm");
            run(stream);
            SingleModeFuzzyFile.DurationFuzzyness += sec * 1000;
        }

    }

    public static void run(InputStream stream) throws FileNotFoundException {

        Parameters.initializeLogger(Level.INFO);
        File folder = ProjectOutput.create("Study");//create();

        if (stream == null) {
            final JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            try {
                stream = new FileInputStream(fc.getSelectedFile());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(RobustTestFuzzyCalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        SingleModeFuzzyFile smf = new SingleModeFuzzyFile(stream);

        /**
         * Problem
         */
        DefaultTimeRestictedSchedulingProblem<Operation> problem = smf.getScheduleFromFile();
        ExportToYed.exportToGraphML(problem.getActivityOnNodeDiagramm(), new File(folder, "graph.graphml").getAbsolutePath());
        

        /**
         * Initialisiere ScheduleScheme
         */
//        ScheduleGenerationScheme sgs = new SerialScheduleScheme();
        ParallelScheduleSchemeWithTime sgs = new ParallelScheduleSchemeWithTime(order);
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();
        ScheduleFitnessEvalationFunction<ListIndividual<Operation>> eval = new MinMakeSpanEval(sgs, problem, priorityDeterminator, problem.getOptimizationTimeSlot());// RessourceLeveling(sgs, problem, priorityDeterminator, problem.getOptimizationTimeSlot());

        Type listType = new TypeToken<ArrayList<ArrayList<Integer>>>() {
        }.getType();
        ArrayList<ArrayList<Integer>> importList = JSONSerialisierung.importJSON(new FileInputStream(f), listType);

        ListIndividual<Operation> fittestIndividual = new ListIndividual<>();
        for (ArrayList<Integer> topoClass : importList) {
            SubListIndividual<Operation> subInd = new SubListIndividual<>();
            for (Integer opID : topoClass) {
                for (Operation operation : problem.getOperations()) {
                    if (opID.equals(operation.getId())) {
                        subInd.getChromosome().add(operation);
                        break;
                    }
                }
            }
            fittestIndividual.getChromosome().add(subInd);
        }

        fittestIndividual.setFitness(eval.computeFitness(fittestIndividual));

        System.out.println("Fitness:" +fittestIndividual.getFitness() + "(" +SingleModeFuzzyFile.DurationFuzzyness +")");
        Schedule schedule = eval.getSchedule(fittestIndividual);

        FuzzyInterval fromWhen = (FuzzyInterval) problem.getOptimizationTimeSlot().getFromWhen();

        TimeSlot ts = TimeSlot.create((long) fromWhen.longValue(), problem.getOptimizationTimeSlot().getFromWhen().longValue() + 100 * 60 * 1000);

        FuzzyNumber fuzStart = (FuzzyNumber) problem.getOptimizationTimeSlot().getFromWhen();
        FuzzyNumber fuzEnde = (FuzzyNumber) problem.getOptimizationTimeSlot().getUntilWhen();

        double dx = 1000 * 1;

        //System.out.println("##########################");
        for (Resource resource : problem.getResources()) {
            applications.fuzzy.plotter.FuzzyFunctionPlotter workloadplotter = new applications.fuzzy.plotter.FuzzyFunctionPlotter("Auslastung der Ressource '" + resource + "' (β=" + SingleModeFuzzyFile.pesLevel + ")");
            ScheduleRule rule = schedule.getHandler().get(resource);
            DefaultEarliestFuzzyScheduleRule fuzzyRule = (DefaultEarliestFuzzyScheduleRule) rule;
            /**
             * Für die einzelenen Operationen
             */
            //System.out.println("Resource: " + resource);
            List<Operation> list = new ArrayList<>(schedule.getOperationsForResource(resource));
            Collections.sort(list, new Comparator<Operation>() {

                @Override
                public int compare(Operation o1, Operation o2) {
                    return Integer.compare(o1.getId(), o2.getId());
                }
            });
            for (Operation operation : list) {
                FuzzyOperation ofuz = (FuzzyOperation) operation;
                FuzzyInterval fuzStartOp = (FuzzyInterval) schedule.getStartTimes().get(operation);
                workloadplotter.addFunction(FuzzyDemandUtilities.getDemandFunctionAtPessimisticLevelOfResourceWithLambda(ofuz, resource, fuzStartOp, schedule.fuzzyWorkloadParameters.get(operation)), fuzStart.getC1(), fuzEnde.getC2(), dx, "Auslastung durch Vorgang " + ofuz.getId());
            }

            JFreeChart aC1 = workloadplotter.getAreaChart(ts, fuzzyRule.getMax().doubleValue() + 0.25);
//            workloadplotter.plot();
            
            aC1.removeLegend();
            try {
                ChartUtilities.saveChartAsPNG(new File(folder, "Einzeln" + resource.toString() + SingleModeFuzzyFile.DurationFuzzyness + ".png"), aC1, 1200, 435);
            } catch (IOException ex) {
                Logger.getLogger(CalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
            }

            /**
             * Gesamt
             */
            applications.fuzzy.plotter.FuzzyFunctionPlotter workloadplotterGes = new applications.fuzzy.plotter.FuzzyFunctionPlotter("Gesamtauslastung der Ressource '" + resource + "' (β=" + SingleModeFuzzyFile.pesLevel + ")" + fuzzyRule.capacity + " max");

            ScheduleRule scheduleRule = schedule.getHandler().get(resource);
            FuzzyFunctionBasedRule sfb = (FuzzyFunctionBasedRule) scheduleRule;
            workloadplotterGes.addFunction(sfb.getWorkloadFunction(), fuzStart.getC1(), fuzEnde.getC2(), dx, "Gesamtauslastung");

            JFreeChart areaChart = workloadplotterGes.getAreaChart(ts, sfb.getMax().doubleValue() + 0.25);
            areaChart.removeLegend();
            try {
                ChartUtilities.saveChartAsPNG(new File(folder, "Gesamt_" + resource.toString() + SingleModeFuzzyFile.DurationFuzzyness + ".png"), areaChart, 1200, 435);
            } catch (IOException ex) {
                Logger.getLogger(CalcPSPLib.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("Gesamtauslastung hinzugefügt.");

        }

        ScheduleWriter writer = new ScheduleWriter();
        writer.analysis(schedule, problem, new File(folder, "Schedule" + SingleModeFuzzyFile.DurationFuzzyness));

        System.out.println("anzeigen");
        OrderValid.testSchedule(problem, schedule);

        for (Operation k : schedule.fuzzyWorkloadParameters.keySet()) {
            System.out.println(schedule.fuzzyWorkloadParameters.get(k));

        }

    }

}
