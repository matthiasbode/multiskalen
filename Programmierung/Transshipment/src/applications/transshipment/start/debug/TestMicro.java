/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.start.debug;

import applications.mmrcsp.ga.populationGenerators.VertexClassStartPopulationGenerator;
import applications.mmrcsp.ga.priority.PriorityDeterminator;
import applications.mmrcsp.ga.priority.StandardPriorityDeterminator;
import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.transshipment.demo.*;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Visualization.CraneView;
import applications.transshipment.ga.direct.decode.ExplicitOperationDecoder;
import applications.transshipment.ga.implicit.decode.ImplicitModeDecoder;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ModeStartPopulationGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.scheduleSchemes.activityListSchemes.ParallelScheduleGenerationScheme;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import static applications.transshipment.multiscale.initializer.MicroInitializerImplicit.initMicro;
import applications.transshipment.multiscale.model.MicroProblem;
import applications.transshipment.multiscale.model.Scale;
import com.google.gson.reflect.TypeToken;
import ga.algorithms.coevolving.GABundle;
import ga.basics.Population;
import ga.individuals.subList.ListIndividual;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.LongValue;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class TestMicro implements TestClass {

    public static int numberOfIndOperations = 1;
    public static int numberOfIndModes = 1;
    public static int GENERATIONS = 1;

    public Scale scale = Scale.micro;

    public LoadUnitJobSchedule bestSchedule;

    public final File folder;

    public TestMicro(File folder) {
        this.folder = folder;
    }

    public static void main(String[] args) {
        File folder = ProjectOutput.create();
        TestMicro ga = new TestMicro(folder);
        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();
        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, ga.scale, 4, true);
        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);

    }

    public void start(MultiJobTerminalProblem problem, int numberOfIndOperations, int numberOfIndModes, int GENERATIONS, boolean parallel) {
        TransshipmentParameter.initializeLogger(Level.FINER);
        TransshipmentParameter.DEBUG = true;

        try {
            Handler loggerHandler = new FileHandler(new File(folder, "Log.txt").getAbsolutePath());
            TransshipmentParameter.logger.addHandler(loggerHandler);
        } catch (IOException ex) {
            Logger.getLogger(TestMicro.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TestMicro.class.getName()).log(Level.SEVERE, null, ex);
        }

        InputStream streamMode = Test.class.getResourceAsStream("MicroMode_-7 _0.txt");
        Type listType2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
        }.getType();
        List<LoadUnitJobPriorityRules.Identifier> list2 = JSONSerialisierung.importJSON(streamMode, listType2);
        System.out.println(list2);
        ImplicitModeIndividual ind = new ImplicitModeIndividual(list2);

        ImplicitModeDecoder d = new ImplicitModeDecoder(ind, problem);
        ActivityOnNodeGraph<RoutingTransportOperation> graph = d.getGraph();
        Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes = d.getEalosaes();

        TimeSlot ts = new TimeSlot(problem.getOptimizationTimeSlot().getFromWhen(), problem.getOptimizationTimeSlot().getFromWhen().add(new LongValue(30 * 60 * 1000)));

        HashSet<RoutingTransportOperation> operationsToSchedule = graph.vertexSet();
        MicroProblem subProblem = MultiJobTerminalProblemFactory.createMicroForMultiScale(ts, null, operationsToSchedule, ealosaes, graph, problem);

        DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(subProblem.getTerminal().getDnfStorage(), problem.getRouteFinder(), problem);
        ParallelScheduleGenerationScheme scheduleScheme = new ParallelScheduleGenerationScheme(dnfToStorageTreatment);//new SerialScheduleGenerationWithStorageConsideration(problem, dnfToStorageTreatment, false);
        PriorityDeterminator priorityDeterminator = new StandardPriorityDeterminator();

        VertexClassStartPopulationGenerator<RoutingTransportOperation> vPop = new VertexClassStartPopulationGenerator(subProblem, operationsToSchedule);
        ListIndividual<RoutingTransportOperation> opsInd = vPop.generatePopulation(1).individuals().iterator().next();

        ExplicitOperationDecoder dd = new ExplicitOperationDecoder(priorityDeterminator, opsInd, d.getEalosaes(), operationsToSchedule, graph, problem, scheduleScheme, ts);
        LoadUnitJobSchedule schedule = dd.getSchedule();
        CraneView cv = new CraneView();
        cv.analysis(schedule, problem, folder);
    }

    @Override
    public LoadUnitJobSchedule getBestSchedule() {
        return bestSchedule;
    }

}
