/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications;

import applications.functions.graphics3d.SimpleCanvas3D;
import applications.functions.graphics3d.utils.IsoPalette;
import ga.individuals.Individual;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import java.awt.Color;
import java.util.Map;
import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 *
 * @author bode
 */
public class FitnessLandscapePlotter<C extends Individual> {

    IsoPalette pal;
    Color c;
    double ueberhoehung;
    Map<C, Point2d> pos;
    FitnessEvalationFunction<C> env;

    public FitnessLandscapePlotter(IsoPalette pal, double ueberhoehung, Map<C, Point2d> pos, FitnessEvalationFunction<C> env) {
        this.pal = pal;
        this.ueberhoehung = ueberhoehung;
        this.pos = pos;
        this.env = env;
    }

    public FitnessLandscapePlotter(Color c, double ueberhoehung, Map<C, Point2d> pos, FitnessEvalationFunction<C> env) {
        this.c = c;
        this.ueberhoehung = ueberhoehung;
        this.pos = pos;
        this.env = env;
    }

    public SimpleCanvas3D getSimpleCanvas() {
        Shape3D s = createShape();
        SimpleCanvas3D canvas = new SimpleCanvas3D();
        canvas.setBackgroundColor(Color.WHITE);
        canvas.addShape(s);
        canvas.compile();
        canvas.setDefaultViewPosition();
        canvas.setMouseInteraction(true);
        canvas.showAxis(2.0f);
        return canvas;
    }

    public Shape3D createShape() {
        return new Shape3D(createGeometry(), createAppearance());
    }

    private Appearance createAppearance() {
        Appearance ap = new Appearance();
        ap.setPointAttributes(new PointAttributes(3.0f, true));
        return ap;

    }

    private Geometry createGeometry() {

        PointArray pointArray = new PointArray(pos.size(), GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        int i = 0;
        for (C individual : pos.keySet()) {
            Point2d position = pos.get(individual);
            double fitness = individual.getFitness();
            if(Double.isNaN(fitness)){
                individual.setFitness(this.env.computeFitness(individual));
                fitness = individual.getFitness();
            }
            Point3d p = new Point3d(position.x, position.y, fitness * ueberhoehung);
            pointArray.setCoordinate(i, p);

            if (this.c == null) {
                pointArray.setColor(i, new Color3f(pal.getColor(fitness)));
            } else {
                pointArray.setColor(i, new Color3f(c));
            }
            i++;
        }

        return pointArray;
    }
}
