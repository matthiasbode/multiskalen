/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.json;

import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author behrensd
 */
public class JsonPathSegment {

    private int code;
    private double[] point;

    public int getCode() {
        return code;
    }

    public double[] getPoint() {
        return point;
    }

    public JsonPathSegment(int code, double[] point) {
        this.code = code;
        this.point = point;
    }
    
     public static List<JsonPathSegment> areaToPathSegments(Area area) {
        ArrayList<JsonPathSegment> s = new ArrayList<>();
        for (PathIterator pi = area.getPathIterator(null); !pi.isDone(); pi.next()) {
            double[] coords = new double[2];
            int code = pi.currentSegment(coords);
            s.add(new JsonPathSegment(code, coords));
        }

        return s;
    }
}
