/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import applications.transshipment.model.loadunits.LoadUnit;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author hagedorn
 */
public class Container3D extends Box {

    public final PhongMaterial material = new PhongMaterial();
    public LoadUnit loadUnit;
    
    public Container3D(LoadUnit loadUnit) {
        super(loadUnit.getLength(), loadUnit.getWidth(), loadUnit.getHeight());
        this.setRed();
        this.setMaterial(material);
        this.setVisible(false);
        this.loadUnit=loadUnit;
    }

    public final void setGreen() {
        material.setDiffuseColor(Color.GREEN);
        material.setSpecularColor(Color.DARKGREEN);
    }
     public final void setRed() {
        material.setDiffuseColor(Color.RED);
        material.setSpecularColor(Color.DARKRED);
    }
}
