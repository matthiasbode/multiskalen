/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.Collection;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javax.vecmath.Point3d;
import math.geometry.PositionAndOrientation3DInTime;

/**
 *
 * @author hagedorn
 */
public class Crane3D extends Group {

    protected final PhongMaterial material = new PhongMaterial();
    private PositionAndOrientation3DInTime start;

    public Crane3D(Color color, List<PositionAndOrientation3DInTime> positions) {
        material.setDiffuseColor(color);
        material.setSpecularColor(color);

        this.start = getStartPosition(positions);

        Box boxOberteil = new Box(10, 73, 2);
        boxOberteil.setTranslateX(5);
        boxOberteil.setTranslateX(5);
        boxOberteil.setTranslateY(73 / 2);
        boxOberteil.setTranslateZ(30);
        boxOberteil.setMaterial(material);

        Box boxLinkeStuetze = new Box(10, 2, 30);
        boxLinkeStuetze.setTranslateX(5
        );
        boxLinkeStuetze.setTranslateY(1);
        boxLinkeStuetze.setTranslateZ(15);
        boxLinkeStuetze.setMaterial(material);

        Box boxRechteStuetze = new Box(10, 2, 30);
        boxRechteStuetze.setTranslateX(5);
        boxRechteStuetze.setTranslateY(72);
        boxRechteStuetze.setTranslateZ(15);
        boxRechteStuetze.setMaterial(material);
        
        this.setTranslateX(start.getPosition().getX());
        this.getChildren().addAll(boxLinkeStuetze, boxOberteil, boxRechteStuetze);
    }
    @Deprecated
    public static  PositionAndOrientation3DInTime getStartPosition(Collection<PositionAndOrientation3DInTime> coll) {
        PositionAndOrientation3DInTime start;
        start = new PositionAndOrientation3DInTime(Long.MAX_VALUE, new Point3d(), null);

        for (Object coll1 : coll) {
            PositionAndOrientation3DInTime pos = (PositionAndOrientation3DInTime) coll1;
            if (pos.getTime() < start.getTime()) {
                start = pos;
            }
        }
        return start;

    }

    public PositionAndOrientation3DInTime getStart() {
        return start;
    }

    public void setStart(PositionAndOrientation3DInTime start) {
        this.start = start;
    }

}
