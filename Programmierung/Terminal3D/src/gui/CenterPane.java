/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import project.GUI;

/**
 *
 * @author Matthias
 */
public abstract class CenterPane extends SubScene {

    public GUI gui;

    public CenterPane(GUI gui, Parent parent) {
        super(parent, 600, 600, true, SceneAntialiasing.BALANCED);
        this.gui = gui;
    }

    public abstract void updateTransform();

    public abstract Node leftElements();
    
    public abstract Node bottomElements();
}
