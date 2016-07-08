/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment;

import applications.transshipment.analysis.Analysis;
import applications.transshipment.analysis.Workload.CraneAnalysis;
import applications.transshipment.analysis.Workload.WorkloadPlotter;
import applications.transshipment.demo.ProjectOutput;
import applications.transshipment.demo.TestGAImplicitOp_PermutationMode;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.multiscale.model.Scale;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextAreaBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import util.Console;

/**
 *
 * @author Matthias
 */
public class GA_GUI extends Application {

    WorkingSpace space;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        space = new WorkingSpace();
        if (space.getPath() == null || space.getPath().equals("")) {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Workingspace setzen");
            File workingspace = dirChooser.showDialog(primaryStage);
            try {
                space.setPath(workingspace.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(GA_GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (space.getPath() == null) {
            throw new RuntimeException("Kein Ort zum Speichern angegeben");
        }

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Label labelO = new Label("Anzahl Operationsindividuen: ");

        final TextField opField = new TextField("20");
        opField.setPrefWidth(150.);
        grid.add(labelO, 0, 0);
        grid.add(opField, 1, 0);

        Label labelM = new Label("Anzahl Modenindividuen: ");
        final TextField MField = new TextField("20");
        MField.setPrefWidth(150.);
        grid.add(labelM, 0, 1);
        grid.add(MField, 1, 1);

        Label labelG = new Label("Anzahl Generationen: ");
        final TextField GField = new TextField("20");
        GField.setPrefWidth(150.);
        grid.add(labelG, 0, 2);
        grid.add(GField, 1, 2);

        final ToggleGroup group = new ToggleGroup();

        final RadioButton buttonMicro = new RadioButton("Micro");
        buttonMicro.setToggleGroup(group);
        buttonMicro.setSelected(true);

        final RadioButton buttonMacro = new RadioButton("Macro");
        buttonMacro.setToggleGroup(group);

        grid.add(new Label("Skala: "), 0, 3);

        grid.add(buttonMicro, 0, 4);
        grid.add(buttonMacro, 1, 4);

        final Scene scene = new Scene(grid, 350, 300, true);
        primaryStage.setScene(scene);
        primaryStage.show();

        Label ergebnisLabel = new Label("Ausgabeverzeichnis");
        grid.add(ergebnisLabel, 0, 5);

        final TextField path = new TextField(space.getPath());
        grid.add(path, 0, 6);

        GridPane.setColumnSpan(path, 2);

        Button pathMod = new Button("Ändern");
        pathMod.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Workingspace setzen");
                File workingspace = dirChooser.showDialog(primaryStage);
                try {
                    space.setPath(workingspace.getCanonicalPath());
                    path.setText(space.getPath());
                } catch (IOException ex) {
                    Logger.getLogger(GA_GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        GridPane.setHalignment(pathMod, HPos.RIGHT);
        grid.add(pathMod, 1, 7);

        Button calc = new Button("Run");
        GridPane.setHalignment(calc, HPos.RIGHT);
        grid.add(calc, 1, 9);

        calc.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                RadioButton chk = (RadioButton) group.getSelectedToggle();
                final Scale scale = chk.getText().equals("Micro") ? Scale.micro : Scale.macro;
                final File folder = ProjectOutput.create(space.getFolder(), scale.toString());

                final int numberOfIndOperations = Integer.parseInt(opField.getText());
                final int numberOfIndModes = Integer.parseInt(MField.getText());
                final int GENERATIONS = Integer.parseInt(GField.getText());

                Task<Void> task = new Task() {
                    @Override
                    protected Void call() throws Exception {
                        System.err.println("Start");
                        TestGAImplicitOp_PermutationMode ga = new TestGAImplicitOp_PermutationMode(folder);
                        DuisburgGenerator g = new DuisburgGenerator();
                        DuisburgInputParameters parameters = new DuisburgInputParameters();
                        MultiJobTerminalProblem problem = g.generateTerminalProblem(parameters, scale, 3, false);
                        ga.start(problem, numberOfIndOperations, numberOfIndModes, GENERATIONS, true);
                        ArrayList<Analysis> analyser = new ArrayList<>();
                        analyser.add(new WorkloadPlotter());
                        analyser.add(new CraneAnalysis());
                        for (Analysis a : analyser) {
                            a.analysis(ga.bestSchedule, problem, folder);
                        }
                        return null;
                    }
                };

                TextArea ta = TextAreaBuilder.create()
                        .prefWidth(800)
                        .prefHeight(600)
                        .wrapText(true)
                        .build();

                Console console = new Console(ta);
                PrintStream ps = new PrintStream(console, true);

                Console consoleERR = new Console(ta);
                PrintStream psERR = new PrintStream(consoleERR, true);

                System.setOut(ps);
                System.setErr(psERR);

                Scene app = new Scene(ta);
                Stage s = new Stage();
                s.setScene(app);
                s.show();

                Thread th = new Thread(task);
                th.start();
            }
        });

    }
}
