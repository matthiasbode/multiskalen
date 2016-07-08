/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions.function3d;

import bijava.geometry.dim2.Point2d;
import bijava.math.function.ScalarFunction2d;
import bijava.math.function.interpolation.RasterScalarFunction2d;
import com.sun.j3d.utils.geometry.Box;
import applications.functions.RasterScalarFunction2dPlotter;
import applications.functions.graphics3d.SimpleCanvas3D;
import applications.functions.graphics3d.utils.IsoPalette;
import ga.individuals.DoubleIndividual;
import ga.listeners.IndividualEvent;
import java.awt.Color;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

/**
 *
 * @author bode
 */
public class IndividualPlotter {

    PlotListener<DoubleIndividual> listener;
    SimpleCanvas3D canvas = new SimpleCanvas3D();
    ArrayList<TransformGroup> currentGroups = new ArrayList<TransformGroup>();

    public IndividualPlotter(PlotListener listener) {
        this.listener = listener;
    }

    public SimpleCanvas3D createCanvas() {
        canvas.addShape(createLandscape());
        addIndividuals(0, IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL);
        canvas.compile();
        canvas.setDefaultViewPosition();
        canvas.setMouseInteraction(true);
        return canvas;
    }

    public void addIndividuals(int numberOfGeneration,  IndividualEvent.StatusIndividualEvent status) {
        for (TransformGroup transformGroup : currentGroups) {
            canvas.removeGroup(transformGroup);
        }


        if (status ==  IndividualEvent.StatusIndividualEvent.NEW_LS_INDIVIDUAL) {
            ArrayList<DoubleIndividual> arrayList = listener.localSearchIndividuals.get(numberOfGeneration);
            if(arrayList != null)
            for (DoubleIndividual individual : arrayList) {
                addGeometry(individual, createAppearance(Color.RED));
            }
        }
        if (status ==  IndividualEvent.StatusIndividualEvent.NEW_GA_INDIVIDUAL) {
            ArrayList<DoubleIndividual> arrayList = listener.recombinationIndividuals.get(numberOfGeneration);
            if(arrayList != null)
            for (DoubleIndividual individual : arrayList) {
                addGeometry(individual, createAppearance(Color.GREEN));
            }
        }

    }

    public SimpleCanvas3D getCanvas() {
        return canvas;
    }

    private Shape3D createLandscape() {
        ScalarFunction2d fkt = new ScalarFunction2d() {
            @Override
            public double getValue(Point2d p) {
//                if ((p.y > Math.sqrt(Math.PI) && p.y < Math.sqrt(2 * Math.PI))
//                 && (p.x > Math.PI / 2. && p.x < (3 / 2.) * Math.PI)) {
//                    return (1.5 * Math.cos((p.x)) + Math.sin((p.y) * (p.y)));
//                }
//                return (Math.cos((p.x)) + Math.sin((p.y) * (p.y)));
                //  return (0.2*(p.x*p.x-p.y*p.y)/(3.+Math.cos(p.x+p.y)+Math.sin(p.x-p.y)));
                return 0.3 * (p.x * p.x - p.y * p.y + (p.y)) / (3 + Math.cos(p.x + p.y) + Math.sin(p.x - p.y)) + Math.exp((Math.cos((p.x) * (p.y)))) / 1.5;
            }
        };
        RasterScalarFunction2d rsf2d = new RasterScalarFunction2d(-5, -5, 5, 5, 150, 150, fkt);
        System.out.println("Minimum :" + rsf2d.getMin());
        IsoPalette pal = new IsoPalette(rsf2d.getMin(), rsf2d.getMax());
        pal.setFarbverlauf(IsoPalette.Palette.Weiss);
        System.out.println(pal.getMinVal());
        System.out.println(pal.getMaxVal());
        RasterScalarFunction2dPlotter plotterLandscape = new RasterScalarFunction2dPlotter(rsf2d, pal, 1.0);
        return plotterLandscape.createShape();


    }

    private Appearance createAppearance(Color c) {
        Appearance ap = new Appearance();
        ColoringAttributes col = new ColoringAttributes(new Color3f(c), ColoringAttributes.FASTEST);
        PolygonAttributes poly = new PolygonAttributes();
        poly.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        poly.setCullFace(PolygonAttributes.CULL_NONE);
        ap.setPolygonAttributes(poly);
        ap.setColoringAttributes(col);
        return ap;

    }

    private void addGeometry(DoubleIndividual individual, Appearance ap) {
        TransformGroup cctg = new TransformGroup();
        Box c = new Box(0.03f, 0.03f, 0.03f, ap);
        cctg.addChild(c);
        Transform3D cc3d = new Transform3D();
        cc3d.setTranslation(new Vector3d(individual.get(0), individual.get(1), -individual.getFitness()));
        cctg.setTransform(cc3d);
        canvas.addGroup(cctg);
        currentGroups.add(cctg);
    }
}
