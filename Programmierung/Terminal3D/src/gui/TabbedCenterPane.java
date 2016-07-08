/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import project.GUI;

/**
 *
 * @author Matthias
 */
public class TabbedCenterPane extends TabPane {

    GUI gui;

    public TabbedCenterPane(GUI gui) {
        this.gui = gui;
        this.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                         CenterPane cp = (CenterPane) newValue.getContent();
                         gui.setLeft(cp.leftElements());
                         gui.setBottom(cp.bottomElements());
                    }
                }
        );
    }

    @Override
    protected void layoutChildren() {
        for (Tab tab : getTabs()) {
            CenterPane cp = (CenterPane) tab.getContent();
            cp.setWidth(this.getWidth());
            cp.setHeight(this.getHeight());
            cp.updateTransform();
        }

        super.layoutChildren(); //To change body of generated methods, choose Tools | Templates.
    }

    public void add(String text, CenterPane pane) {
        Tab tab = new Tab(text);
        tab.setContent(pane);
        this.getTabs().add(tab);
    }

}
