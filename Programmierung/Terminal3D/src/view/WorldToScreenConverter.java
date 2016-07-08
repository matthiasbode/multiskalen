/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import editor.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 *
 * @author bode
 */
public class WorldToScreenConverter {

    public static List<Transform> updateTransform(Rectangle viewBounds, double width, double height) {
        ArrayList<Transform> transforms = new ArrayList<Transform>();
        /**
         * Skalierung
         */
        Scale transform_scale = new Scale();
        /**
         * Translation
         */
        Translate transform_translate = new Translate();
        /**
         * Spiegelung an der (verschobenen) y-Achse
         */
        Scale transform_flip = new Scale();

        transforms.add(transform_flip);
        transforms.add(transform_translate);
        transforms.add(transform_scale);

        double scaleFactor = Math.min(0.75 * width / viewBounds.getWidth(), 0.75 * height / viewBounds.getHeight());

        transform_scale.setX(scaleFactor);

        transform_scale.setPivotX(viewBounds.getX());
        transform_scale.setY(scaleFactor);
        transform_scale.setZ(-1.0);
        transform_scale.setPivotY(viewBounds.getY());
        transform_flip.setPivotY(height / 2.);
        transform_flip.setY(-1.);
        transform_translate.setX((width - viewBounds.getWidth()) / 2.);
        transform_translate.setY((height - viewBounds.getHeight()) / 2.);

        return transforms;
    }
}
