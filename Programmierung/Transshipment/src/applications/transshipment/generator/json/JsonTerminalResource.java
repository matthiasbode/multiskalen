/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.json;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.List;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author behrensd
 */
public class JsonTerminalResource {

    private String type;
    private List<JsonPathSegment> segments;

    public static final String KEY_CRANERUNWAY = "Kranbahn";
    public static final String KEY_LCS = "LCSystem";
    public static final String KEY_RAILROADTRACK = "RailRoadTrack";
    public static final String KEY_HANDOVER = "HandoverPoint";
    public static final String KEY_STORAGEROW = "StorageRow";

    public JsonTerminalResource(String bezeichnung) {
        this.type = bezeichnung;
    }

    public JsonTerminalResource(String bezeichnung, Rectangle s) {
        this.type = bezeichnung;
        this.segments = null; //Include some magic
    }

    public JsonTerminalResource(String bezeichnung, List<JsonPathSegment> segments) {
        this.type = bezeichnung;
        this.segments = segments;
    }

    public String getBezeichnung() {
        return type;
    }

    public void setBezeichnung(String bezeichnung) {
        this.type = bezeichnung;
    }

    public Area getArea() {
        Path2D.Double path = new Path2D.Double();

        for (JsonPathSegment jsonPathSegment : segments) {
            if (jsonPathSegment.getCode() == PathIterator.SEG_MOVETO) {
                path.moveTo(jsonPathSegment.getPoint()[0], jsonPathSegment.getPoint()[1]);
            } else if (jsonPathSegment.getCode() == PathIterator.SEG_LINETO) {
                path.lineTo(jsonPathSegment.getPoint()[0], jsonPathSegment.getPoint()[1]);
            } else if (jsonPathSegment.getCode() == PathIterator.SEG_CLOSE) {
                path.closePath();
            }
        }

        return new Area(path);
    }

    public List<JsonPathSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<JsonPathSegment> segments) {
        this.segments = segments;
    }

}
