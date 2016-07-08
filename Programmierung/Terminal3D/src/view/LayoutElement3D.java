/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import applications.transshipment.generator.json.JsonPathSegment;
import applications.transshipment.generator.json.JsonTerminalResource;
import java.awt.geom.Area;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * Klasse, die ein LayoutElement als Spezialisierung eines javafx.scene.shape.Rectangle beschreibt
 *
 * @author hagedorn
 */
public class LayoutElement3D extends Box {

    /**
     * Verweis auf die zugehörige JsonTerminalResource
     */
    private JsonTerminalResource res;

    /**
     * Konstruktor
     *
     * @param res
    */
    public LayoutElement3D(JsonTerminalResource res) {
        super(res.getArea().getBounds2D().getWidth(), res.getArea().getBounds2D().getHeight(),0.01);
        this.setVisible(true);
        this.setTranslateX(res.getArea().getBounds2D().getMinX()+res.getArea().getBounds2D().getWidth()/2.);       
        this.setTranslateY(res.getArea().getBounds2D().getMinY()+res.getArea().getBounds2D().getHeight()/2.);
        this.setTranslateZ(0); 
        this.res = res;
        this.addTypeColors();

    }

    /**
     * get-Methode für JsonTerminalResource
     *
     * @return
     */
    public JsonTerminalResource getResource() {
        return res;
    }
    

    @Override
    public String toString() {
        return "LayoutElement[" + this.getResource().getBezeichnung() + ", x=" + this.getTranslateX() + ", y=" + this.getTranslateY() + "]"; //To change body of generated methods, choose Tools | Templates.
    }

    public final void addTypeColors() {
        this.setStyle(null);

        switch (res.getBezeichnung()) {
            case JsonTerminalResource.KEY_CRANERUNWAY:
                this.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
                //this.setFill(Color.LIGHTGRAY);
                this.setTranslateZ(0.01);
                this.setOpacity(0.95);
                break;
            case JsonTerminalResource.KEY_HANDOVER:
                this.setMaterial(new PhongMaterial(Color.BEIGE));
                //this.setFill(Color.BEIGE);
                this.setTranslateZ(0.02);
                this.setOpacity(0.9);
                break;
            case JsonTerminalResource.KEY_LCS:
                this.setMaterial(new PhongMaterial(Color.LIGHTBLUE));
                //this.setFill(Color.LIGHTBLUE);
                this.setTranslateZ(0.03);
                this.setOpacity(0.85);
                break;
            case JsonTerminalResource.KEY_RAILROADTRACK:
                this.setMaterial(new PhongMaterial(Color.BLACK));
                //this.setFill(Color.BLACK);
                this.setTranslateZ(0.04);
                this.setOpacity(0.8);
                break;
            case JsonTerminalResource.KEY_STORAGEROW:
                this.setMaterial(new PhongMaterial(Color.DARKGRAY));                
                //this.setFill(Color.GRAY);
                this.setTranslateZ(0.05);
                this.setOpacity(0.75);
                break;
            default:
                System.err.println("Fehler:  Typ kann nicht dargestellt werden.");
        }
    }

    public void setResource(JsonTerminalResource res) {
        this.res = res;
    }

}
