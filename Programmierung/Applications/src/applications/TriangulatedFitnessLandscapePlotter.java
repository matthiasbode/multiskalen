/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications;

import applications.functions.graphics3d.SimpleCanvas3D;
import applications.functions.graphics3d.utils.IsoPalette;
import bijava.geometry.dim3.Point3d;
import ga.individuals.Individual;
import ga.basics.FitnessEvalationFunction;
import ga.individuals.Individual;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point2d;
import util.TriangulatePoints3D;

/**
 *
 * @author bode
 */
public class TriangulatedFitnessLandscapePlotter<C extends Individual> {

    IsoPalette pal;
    Color c;
    double ueberhoehung;
    Map<C, Point2d> pos;
    FitnessEvalationFunction<C> env;

    public TriangulatedFitnessLandscapePlotter(IsoPalette pal, double ueberhoehung, Map<C, Point2d> pos, FitnessEvalationFunction<C> env) {
        this.pal = pal;
        this.ueberhoehung = ueberhoehung;
        this.pos = pos;
        this.env = env;
    }

    public TriangulatedFitnessLandscapePlotter(Color c, double ueberhoehung, Map<C, Point2d> pos, FitnessEvalationFunction<C> env) {
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
//        ap.setPointAttributes(new PointAttributes(3.0f, true));
        return ap;

    }

    private Geometry createGeometry() {

        HashMap<Point3d, Color> map = new HashMap<>();
        int i = 0;
        for (C individual : pos.keySet()) {
            Point2d position = pos.get(individual);
            double fitness = individual.getFitness();
            if (Double.isNaN(fitness)) {
                individual.setFitness(this.env.computeFitness(individual));
                fitness = individual.getFitness();
            }
            Point3d p = new Point3d(position.x, position.y, fitness * ueberhoehung);
 

            if (this.c == null) {
                map.put(p, pal.getColor(fitness));
            } else {
                map.put(p, this.c);
            }
            i++;
        }
        TriangulatePoints3D t = new TriangulatePoints3D(map);
        return t.getGeometry();
    }
}
