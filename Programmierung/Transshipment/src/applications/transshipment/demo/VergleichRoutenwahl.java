/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.demo;

import applications.mmrcsp.model.basics.JoNComponent;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.ga.implicit.decode.ImplicitModeDecoder;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.start.debug.Test;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class VergleichRoutenwahl extends Application {

    public File folder = ProjectOutput.create();

    @Override
    public void start(final Stage primaryStage) throws Exception {

        InputStream i1 = null;
        InputStream i2 = null;
//        TransshipmentParameter.DEBUG = true;
//        FileChooser chooser = new FileChooser();
//        chooser.setTitle("Makro Instanz");
//        File f1 = chooser.showOpenDialog(primaryStage);
//
//        FileChooser chooser2 = new FileChooser();
//        chooser2.setTitle("Mikro Instanz");
//        File f2 = chooser2.showOpenDialog(primaryStage);
//

//
//        i1 = new FileInputStream(f1);
//        i2 = new FileInputStream(f2);
        i1 = Test.class.getResourceAsStream("macroMode2DNF.txt");
        i2 = Test.class.getResourceAsStream("micro3DNFMode.txt");

        Type listTypef1 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
        }.getType();
        List<LoadUnitJobPriorityRules.Identifier> listf1 = JSONSerialisierung.importJSON(i1, listTypef1);
        System.out.println(listf1);
        ImplicitModeIndividual implicitModeIndividualMacro = new ImplicitModeIndividual(listf1);

        Type listTypef2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
        }.getType();
        List<LoadUnitJobPriorityRules.Identifier> listf2 = JSONSerialisierung.importJSON(i2, listTypef2);
        System.out.println(listf2);
        ImplicitModeIndividual implicitModeIndividualMicro = new ImplicitModeIndividual(listf2);

        DuisburgGenerator g = new DuisburgGenerator();
        DuisburgInputParameters parameters = new DuisburgInputParameters();

        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, Scale.micro, TransshipmentParameter.numberOfRoutes, false);

        ImplicitModeDecoder d1 = new ImplicitModeDecoder(implicitModeIndividualMacro, problem);
        ImplicitModeDecoder d2 = new ImplicitModeDecoder(implicitModeIndividualMicro, problem);

        int numbernotEqual = 0;
        int numberEqual = 0;

        int Best = 0;
        int notBest1 = 0;
        int notBest2 = 0;

        HashMap<LoadUnitJob, String> geanderte = new HashMap<LoadUnitJob, String>();

        for (JoNComponent<LoadUnitJob> connectionComponent : problem.getJobOnNodeDiagramm().getConnectionComponents()) {
            ArrayList<LoadUnitJob> jobsOfComponent = new ArrayList<>(connectionComponent.vertexSet());
            Collections.sort(jobsOfComponent, new Comparator<LoadUnitJob>() {

                @Override
                public int compare(LoadUnitJob o1, LoadUnitJob o2) {
                    return o1.getLoadUnit().getID().compareTo(o2.getLoadUnit().getID());
                }
            }
            );

            for (LoadUnitJob job : jobsOfComponent) {
                int routeNumber1 = d1.getRouteNumber(job);
                int routeNumber2 = d2.getRouteNumber(job);
                if (routeNumber1 != routeNumber2) {
                    System.out.println("!!!\t" + job + "\t:" + routeNumber1 + "/" + routeNumber2);
                    numbernotEqual++;
                    geanderte.put(job, Integer.toString(connectionComponent.getNumber()));
                } else {
                    if (routeNumber1 == 0) {
                        Best++;
                    }
                    if (routeNumber1 == 1) {
                        notBest1++;
                    }
                    if (routeNumber2 == 2) {
                        notBest1++;
                    }

                    System.out.println(job + "\t:" + routeNumber1 + "/" + routeNumber2);
                    numberEqual++;
                }
            }

        }

        System.out.println(numberEqual + "/" + numbernotEqual);
        for (LoadUnitJob key : geanderte.keySet()) {
            int routeNumber1 = d1.getRouteNumber(key);
            int routeNumber2 = d2.getRouteNumber(key);
            System.out.println(key + "\t:" + geanderte.get(key) + "\t/" + routeNumber1 + "/" + routeNumber2);

        }

        System.out.println(Best + "/" + notBest1 + "/" + notBest2);

    }

    public static void main(String[] args) {
        launch(args);
    }

}
