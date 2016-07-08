/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import applications.transshipment.generator.json.JsonPathSegment;
import applications.transshipment.generator.json.JsonTerminalResource;
import java.awt.geom.Area;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Klasse, die ein LayoutElement als Spezialisierung eines javafx.scene.shape.Rectangle beschreibt
 *
 * @author hagedorn
 */
public class LayoutElement extends Rectangle {

    /**
     * Verweis auf die zugehörige JsonTerminalResource
     */
    private JsonTerminalResource res;

    /**
     * Konstruktor
     *
     * @param type Typ des LayoutElements
     * @param height Hoehe des Rechtecks
     * @param width Breite des Rechtecks
     * @param posX X-Koordinate upper-left corner
     * @param posY Y-Koordinate upper-left corner
     */
    public LayoutElement(String type, double height, double width, double posX, double posY) {
        super(posX, posY, width, height);
        this.setRessource(type, this.convertLayoutElementToRectangle());
        this.addTypeColors();
    }

    public LayoutElement(JsonTerminalResource res) {
        super(res.getArea().getBounds2D().getWidth(), res.getArea().getBounds2D().getHeight());
        this.setVisible(true);
        this.setX(res.getArea().getBounds2D().getMinX());
        this.setY(res.getArea().getBounds2D().getMinY());
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


    public Group getTransformationRectangles() {
        double s;
        if (this.getWidth() <= 20 || this.getHeight() <= 20) {
            s = 3;
        } else {
            s = 10;
        }
        Group transformationRectangles = new Group();
        Rectangle r1 = new Rectangle(s, s, Color.RED);
        r1.setX(this.getX());
        r1.setY(this.getY());
        Rectangle r2 = new Rectangle(s, s, Color.RED);
        r2.setX(this.getX() + this.getWidth() - s);
        r2.setY(this.getY());
        Rectangle r3 = new Rectangle(s, s, Color.RED);
        r3.setX(this.getX());
        r3.setY(this.getY() + this.getHeight() - s);
        Rectangle r4 = new Rectangle(s, s, Color.RED);
        r4.setX(this.getX() + this.getWidth() - s);
        r4.setY(this.getY() + this.getHeight() - s);

        transformationRectangles.getChildren().addAll(r1, r2, r3, r4);
        transformationRectangles.setTranslateZ(10);

        return transformationRectangles;
    }

    @Override
    public String toString() {
        return "LayoutElement[" + this.getResource().getBezeichnung() + ", x=" + this.getX() + ", y=" + this.getY() + "]"; //To change body of generated methods, choose Tools | Templates.
    }

    public final void addTypeColors() {
        this.setStyle(null);

        switch (res.getBezeichnung()) {
            case JsonTerminalResource.KEY_CRANERUNWAY:
                this.setFill(Color.LIGHTGRAY);
                this.setTranslateZ(1);
                this.setOpacity(0.95);
                break;
            case JsonTerminalResource.KEY_HANDOVER:
                this.setFill(Color.BEIGE);
                this.setTranslateZ(2);
                this.setOpacity(0.9);
                break;
            case JsonTerminalResource.KEY_LCS:
                this.setFill(Color.LIGHTBLUE);
                this.setTranslateZ(3);
                this.setOpacity(0.85);
                break;
            case JsonTerminalResource.KEY_RAILROADTRACK:
                this.setFill(Color.BLACK);
                this.setTranslateZ(4);
                this.setOpacity(0.8);
                break;
            case JsonTerminalResource.KEY_STORAGEROW:
                this.setFill(Color.GRAY);
                this.setTranslateZ(5);
                this.setOpacity(0.75);
                break;
            default:
                System.err.println("Fehler:  Typ kann nicht dargestellt werden.");
        }
    }

    public java.awt.geom.Rectangle2D.Double convertLayoutElementToRectangle() {
        return new java.awt.geom.Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public void setResource(JsonTerminalResource res) {
        this.res = res;
    }

    public void setRessource(String type, java.awt.geom.Rectangle2D.Double rect) {

        List<JsonPathSegment> seg = JsonPathSegment.areaToPathSegments(new Area(rect));

        if (res == null) {
            this.res = new JsonTerminalResource(type, seg);
        } else {
            this.res.setBezeichnung(type);
            this.res.setSegments(seg);
        }
    }

    public void updateLayoutElement(double width, double height, double x, double y, String type) {
        this.setWidth(width);
        this.setHeight(height);
        this.setX(x);
        this.setY(y);
        this.setRessource(type, this.convertLayoutElementToRectangle());
    }

}
