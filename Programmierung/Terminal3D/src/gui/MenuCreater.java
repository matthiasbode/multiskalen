/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.analysis.Visualization.KeyFrameWriter;
import applications.transshipment.demo.miniTests.Vergleich_Altes_Transshipment;
import applications.transshipment.ga.implicit.evaluation.MinEvaluationSuperIndividual;
import applications.transshipment.ga.implicit.individuals.ImplicitSuperIndividual;
import applications.transshipment.ga.implicit.individuals.modes.ImplicitModeIndividual;
import applications.transshipment.ga.implicit.individuals.ops.ImplicitOperationIndividual;
import applications.transshipment.generator.LoadUnitGenerator;
import applications.transshipment.generator.LoadUnitGeneratorFromJSON;
import applications.transshipment.generator.projekte.ParameterInputFile;
import applications.transshipment.generator.projekte.TerminalGenerator;
import applications.transshipment.generator.projekte.TrainGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgInputParameters;
import applications.transshipment.generator.projekte.duisburg.DuisburgTerminalGenerator;
import applications.transshipment.generator.projekte.duisburg.DuisburgTrainGenerator;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.basics.util.DefaultRuleMapper;
import applications.transshipment.model.basics.util.LoadUnitJobActivityOnNodeBuilder;
import applications.transshipment.model.basics.util.Mapper;
import applications.transshipment.model.basics.util.MultiJobTerminalProblemFactory;
import applications.transshipment.model.dnf.DNFToStorageTreatment;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.LoadUnitJobPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.OperationPriorityRules;
import applications.transshipment.model.schedule.scheduleSchemes.strategyScheme.StandardParallelStartegyScheduleGenerationScheme;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import applications.transshipment.multiscale.model.Scale;
import applications.transshipment.routing.evaluation.EvalFunction_TransportOperation_TimeMovement;
import applications.transshipment.start.debug.Best;
import applications.transshipment.start.debug.Test;
import com.google.gson.reflect.TypeToken;
import editor.LayoutEditor;
import gantt.Gantt;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import project.GUI;
import util.jsonTools.JSONSerialisierung;
import view.View3D;

/**
 *
 * @author bode
 */
public class MenuCreater {

    public static void setMenu(GUI gui) {

        MenuBar menuBar = new MenuBar();

        Menu menuWorkspace = new Menu("Workspace");
        MenuItem selectWorkspace = new MenuItem("Setze Workspace");
        selectWorkspace.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setInitialDirectory(new File(gui.space.getPath()));
                dirChooser.setTitle("Workingspace setzen");
                File workingspace = dirChooser.showDialog(null);
                if (workingspace != null) {
                    try {
                        if (workingspace.getCanonicalPath() != null) {
                            gui.space.setPath(workingspace.getCanonicalPath());
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MenuCreater.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        menuWorkspace.getItems().add(selectWorkspace);
        menuBar.getMenus().add(menuWorkspace);

        Menu menuP = new Menu(java.util.ResourceBundle.getBundle("project/Bundle").getString("PROBLEM"));
        MenuItem createP = new MenuItem(java.util.ResourceBundle.getBundle("project/Bundle").getString("CREATE PROBLEM"));
        createP.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                DuisburgTerminalGenerator duisburgTerminalGenerator = new DuisburgTerminalGenerator();
                DuisburgTrainGenerator duisburgTrainGenerator = new DuisburgTrainGenerator();
                InputStream resource = DuisburgTerminalGenerator.class.getResourceAsStream("transportprogramme/tp1.json");
                LoadUnitGeneratorFromJSON loadUnitGeneratorFromJSONDuisburg = new LoadUnitGeneratorFromJSON(resource, 0, "Duisburg");
                DefaultRuleMapper defaultRuleMapperFalse = new DefaultRuleMapper();
                DefaultRuleMapper defaultRuleMapperTrue = new DefaultRuleMapper();
                ParameterInputFile parameterInput = new DuisburgInputParameters();

                final ComboBox<ParameterInputFile> inputParameter = new ComboBox<ParameterInputFile>();
                inputParameter.getItems().addAll(
                        parameterInput
                );
                inputParameter.setValue(parameterInput);

                final ComboBox<TerminalGenerator> terminalGenerator = new ComboBox<TerminalGenerator>();
                terminalGenerator.getItems().addAll(
                        duisburgTerminalGenerator
                );
                terminalGenerator.setValue(duisburgTerminalGenerator);

                final ComboBox<TrainGenerator> trainGenerator = new ComboBox<TrainGenerator>();
                trainGenerator.getItems().addAll(
                        duisburgTrainGenerator
                );
                trainGenerator.setValue(duisburgTrainGenerator);

                final ComboBox<LoadUnitGenerator> loadUnitGenerator = new ComboBox<LoadUnitGenerator>();
                loadUnitGenerator.getItems().addAll(
                        loadUnitGeneratorFromJSONDuisburg
                );
                loadUnitGenerator.setValue(loadUnitGeneratorFromJSONDuisburg);

                final ComboBox<Mapper> mapperGenerator = new ComboBox<Mapper>();
                mapperGenerator.getItems().addAll(
                        defaultRuleMapperFalse,
                        defaultRuleMapperTrue
                );
                mapperGenerator.setValue(defaultRuleMapperFalse);

                final ToggleGroup group = new ToggleGroup();

                RadioButton buttonMicro = new RadioButton("Micro");
                buttonMicro.setToggleGroup(group);
                buttonMicro.setSelected(true);

                RadioButton buttonMacro = new RadioButton("Macro");
                buttonMacro.setToggleGroup(group);

                final Stage secondaryStage = new Stage(StageStyle.UTILITY);

//                final FileChooser fileChooser = new FileChooser();
//                final TextField layoutField = new TextField();
//                final Button layoutOpen = new Button(java.util.ResourceBundle.getBundle("project/Bundle").getString("OPEN LAYOUT..."));
//                layoutOpen.setOnAction(
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(final ActionEvent e) {
//                                File file = fileChooser.showOpenDialog(stage);
//                                if (file != null) {
//
//                                }
//                            }
//                        });
                GridPane grid = new GridPane();
                grid.setVgap(4);
                grid.setHgap(10);
                grid.setPadding(new Insets(5, 5, 5, 5));

                Label labelN = new Label("Name: ");

                TextField nameField = new TextField("Test");
                nameField.setPrefWidth(400.);
                grid.add(labelN, 0, 0);
                grid.add(nameField, 1, 0);

                Label labelL = new Label("Layout: ");

                grid.add(labelL, 0, 1);
                terminalGenerator.setPrefWidth(400.);
                grid.add(terminalGenerator, 1, 1);

                grid.add(new Label("Züge: "), 0, 2);
                trainGenerator.setPrefWidth(400.);
                grid.add(trainGenerator, 1, 2);

                grid.add(new Label("Ladeeinheiten: "), 0, 3);
                loadUnitGenerator.setPrefWidth(400.);
                grid.add(loadUnitGenerator, 1, 3);

                grid.add(new Label("Einplanregeln: "), 0, 4);
                mapperGenerator.setPrefWidth(400.);
                grid.add(mapperGenerator, 1, 4);

                grid.add(new Label("Skala: "), 0, 5);

                grid.add(buttonMicro, 1, 6);

                grid.add(buttonMacro, 1, 7);

                grid.add(new Label("Anzahl Routen"), 0, 8);
                TextField textFieldRouten = new TextField("3");
                textFieldRouten.setPrefWidth(400.);
                grid.add(textFieldRouten, 1, 8);

                grid.add(new Label("Parameter: "), 0, 9);

                grid.add(inputParameter, 1, 9);
                inputParameter.setPrefWidth(400.);

                final ProgressBar progressBar = new ProgressBar(0);
                progressBar.setPrefWidth(600.);
                Button createButton = new Button("OK");
                createButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ParameterInputFile parameters = inputParameter.getValue();
                        Terminal terminal = terminalGenerator.getValue().generateTerminal(parameters);
                        List<Train> trains = trainGenerator.getValue().generateTrains(terminal, parameters);
                        List<LoadUnitJob> jobs = loadUnitGenerator.getValue().generateJobs(trains, terminal);
                        Toggle selectedToggle = group.getSelectedToggle();
                        Mapper mapper = mapperGenerator.getValue();
                        int numberOfRoutes = Integer.parseInt(textFieldRouten.getText());

                        Task<MultiJobTerminalProblem> problemTask = createProblemTask(terminal, trains, jobs, selectedToggle, mapper, numberOfRoutes);

                        progressBar.progressProperty().unbind();
                        progressBar.progressProperty().bind(problemTask.progressProperty());

                        Thread thread = new Thread(problemTask);
                        thread.start();

                        problemTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                            @Override
                            public void handle(WorkerStateEvent event) {
                                MultiJobTerminalProblem problem = null;
                                try {
                                    problem = problemTask.get();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(MenuCreater.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (ExecutionException ex) {
                                    Logger.getLogger(MenuCreater.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                gui.problem = problem;
                                gui.terminal = problem.getTerminal();
                                secondaryStage.close();

                                View3D terminalPane = new View3D(gui);
                                terminalPane.setFill(Color.WHITE);
                                gui.addTab(nameField.getText(), terminalPane);
                            }
                        });

                    }
                });

                grid.add(createButton, 1, 10);

//                ColumnConstraints columnConstraints = new ColumnConstraints();
//                columnConstraints.setFillWidth(true);
//                columnConstraints.setHgrow(Priority.ALWAYS);
//                grid.getColumnConstraints().add(columnConstraints);
                BorderPane root = new BorderPane(grid);
                root.setBottom(progressBar);

                secondaryStage.setScene(new Scene(root, 550, 400));
                secondaryStage.setTitle(java.util.ResourceBundle.getBundle("project/Bundle").getString("CREATE PROBLEM"));
                secondaryStage.show();
            }
        });

        menuP.getItems().addAll(createP);

        Menu menuS = new Menu(java.util.ResourceBundle.getBundle("project/Bundle").getString("SOLVING"));

        MenuItem savedSolution = new MenuItem("Saved Solution");
        savedSolution.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                InputStream streamOp = Test.class.getResourceAsStream("micro3DNFOp.txt");
                InputStream streamMode = Test.class.getResourceAsStream("micro3DNFMode.txt");

                Type listType = new TypeToken<ArrayList<OperationPriorityRules.Identifier>>() {
                }.getType();
                List<OperationPriorityRules.Identifier> list = JSONSerialisierung.importJSON(streamOp, listType);
                System.out.println(list);
                ImplicitOperationIndividual opInd = new ImplicitOperationIndividual(list);

                Type listType2 = new TypeToken<ArrayList<LoadUnitJobPriorityRules.Identifier>>() {
                }.getType();
                List<LoadUnitJobPriorityRules.Identifier> list2 = JSONSerialisierung.importJSON(streamMode, listType2);
                System.out.println(list2);
                ImplicitModeIndividual modImplicit = new ImplicitModeIndividual(list2);

                ImplicitSuperIndividual superInd = new ImplicitSuperIndividual(opInd, modImplicit);

                DNFToStorageTreatment dnfToStorageTreatment = new DNFToStorageTreatment(gui.problem.getTerminal().getDnfStorage(), gui.problem.getRouteFinder(), gui.problem);
                StandardParallelStartegyScheduleGenerationScheme sgs = new StandardParallelStartegyScheduleGenerationScheme(dnfToStorageTreatment);
                MinEvaluationSuperIndividual eval = new MinEvaluationSuperIndividual(gui.problem, sgs);

                gui.schedule = eval.getSchedule(superInd);
                KeyFrameWriter kfw = new KeyFrameWriter();
                kfw.analysis(gui.schedule, gui.problem, new File("D:\\Eigene Dateien\\Desktop\\export"));
                System.out.println("Fertig mit Berechnung");
                Tab selectedTab = gui.getSelectedTab();
                if (selectedTab.getContent() instanceof View3D) {
                    View3D tpane = (View3D) selectedTab.getContent();
                    tpane.setSchedule(gui.schedule);
                    System.out.println("Informationen hinzugefügt");
                }

            }
        });

        menuS.getItems().addAll(savedSolution);
        menuBar.getMenus().addAll(menuP, menuS);

        Menu menuEditor = new Menu("Editor");
        MenuItem terminalEditor = new MenuItem("TerminalLayout Editor");
        terminalEditor.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LayoutEditor editorPane = new LayoutEditor(gui);
                editorPane.setFill(Color.WHITE);
                gui.addTab("LayoutEditor", editorPane);
            }
        });

        menuEditor.getItems().add(terminalEditor);
        menuBar.getMenus().add(menuEditor);
//        

        Menu ganttMenue = new Menu("Gantt");
        MenuItem ganttMenueItem = new MenuItem("Gantt Plot anzeigen");
        ganttMenueItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                gui.addTab("GanttChart", new Gantt(gui));
            }
        });

        ganttMenue.getItems().add(ganttMenueItem);
        menuBar.getMenus().add(ganttMenue);
        VBox menus = new VBox();
        menus.getChildren().addAll(menuBar);
        gui.setTop(menus);

    }

    private static Task<MultiJobTerminalProblem> createProblemTask(Terminal terminal,
            List<Train> trains,
            List<LoadUnitJob> jobs,
            Toggle selectedToggle,
            Mapper mapper,
            int numberOfRoutes) {
        return new Task() {

            @Override
            protected MultiJobTerminalProblem call() throws Exception {

                RadioButton chk = (RadioButton) selectedToggle;
                Scale scale = chk.getText().equals("Micro") ? Scale.micro : Scale.macro;
                ScheduleManagerBuilder scheduleRuleBuilder = mapper.getScheduleRuleBuilder(terminal, scale);

                InstanceHandler rules = new InstanceHandler(scheduleRuleBuilder);
                EvalFunction_TransportOperation_TimeMovement eval = new EvalFunction_TransportOperation_TimeMovement(rules, TransshipmentParameter.transportOperation_TimeMovement_DurationWeight);
                final MultiJobTerminalProblem problem = MultiJobTerminalProblemFactory.create(terminal, trains, jobs, mapper, eval, terminal.getTemporalAvailability().getAllOverTimeSlot(), scale);

                /**
                 * Berechne Routen.
                 */
                problem.getRouteFinder().changeSupport.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("COUNT")) {
                            int i = (int) evt.getNewValue();
                            updateProgress(i, problem.getJobs().size());
                        }
                    }
                });
                problem.getRouteFinder().calculateRoutes(numberOfRoutes);

                /**
                 * Berechne großer AON und bildet zeitliche Restriktionen.
                 */
                ActivityOnNodeGraph<RoutingTransportOperation> alloverGraph = LoadUnitJobActivityOnNodeBuilder.buildAlloverGraph(problem);
                problem.setActivityOnNodeDiagramm(alloverGraph);
                return problem;
            }
        };
    }
}
