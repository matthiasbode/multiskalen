/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import applications.transshipment.model.problem.MultiJobTerminalProblem;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.structs.Terminal;
import gui.CenterPane;
import gui.MenuCreater;
import gui.TabbedCenterPane;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author bode
 */
public class GUI extends Application {

    public WorkingSpace space;
    private BorderPane borderPane;
    public Terminal terminal;
    public MultiJobTerminalProblem problem;

    public LoadUnitJobSchedule schedule;

    public Stage stage;
    TabbedCenterPane sp;

    @Override
    public void start(Stage stage) throws Exception {
        space = new WorkingSpace();
        if (space.getPath() == null || space.getPath().equals("")) {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Workingspace setzen");
            File workingspace = dirChooser.showDialog(this.stage);
            try {
                space.setPath(workingspace.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(MenuCreater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Laden der Informationen.
         */
        this.stage = stage;
        stage.setTitle(java.util.ResourceBundle.getBundle("project/Bundle").getString("TERMINAL3D"));
        borderPane = new BorderPane();
        final Scene scene = new Scene(borderPane, 1400, 900, true);
        //fill Background
        scene.setFill(new RadialGradient(225, 0.85, 300, 300, 500, false,
                CycleMethod.NO_CYCLE, new Stop[]{new Stop(0f, Color.LIGHTBLUE),
                    new Stop(1f, Color.BLUE)}));

        sp = new TabbedCenterPane(this);

        borderPane.setCenter(sp);

        MenuCreater.setMenu(this);
        stage.setScene(scene);
        stage.show();
    }

    public void setTop(Node n) {
        borderPane.setTop(n);
    }

    public void setBottom(Node n) {
        if (n != null) {
            borderPane.setBottom(n);
        }
    }

    public void setLeft(Node n) {
        if (n != null) {
            borderPane.setLeft(n);
        }
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public MultiJobTerminalProblem getProblem() {
        return problem;
    }

    public void addTab(String text, CenterPane n) {
        this.sp.add(text, n);
    }

    public ObservableList<Tab> getTabs() {
        return this.sp.getTabs();
    }

    public Tab getSelectedTab() {
        return this.sp.getSelectionModel().getSelectedItem();
    }

    public LoadUnitJobSchedule getSchedule() {
        return schedule;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
