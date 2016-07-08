///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package applications.dame.plotter;
//
//import bijava.geometry.dim2.BoundingBox2d;
//import bijava.geometry.dim2.DelaunayTriangulator;
//import bijava.geometry.dim2.Point2d;
//import bijava.math.function.interpolation.TriangulatedScalarFunction2d;
//import com.sun.j3d.utils.geometry.Box;
//import applications.functions.graphics3d.SimpleCanvas3D;
//import ga.basics.Individual;
//import ga.coding.IntegerCoding;
//import ga.listeners.GAEvent.Status;
//import gun.tools.ImportYEDForFitnessLandscape.HelpIndividual;
//import gun.tools.J3DGeometryGenerator;
//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.TreeMap;
//import javax.media.j3d.Appearance;
//import javax.media.j3d.ColoringAttributes;
//import javax.media.j3d.GeometryArray;
//import javax.media.j3d.PolygonAttributes;
//import javax.media.j3d.Shape3D;
//import javax.media.j3d.Transform3D;
//import javax.media.j3d.TransformGroup;
//import javax.media.j3d.TransparencyAttributes;
//import javax.vecmath.Color3f;
//
//import javax.vecmath.Point3d;
//import javax.vecmath.Vector3d;
//
///**
// *
// * @author bode
// */
//public class IntegerIndividualPlotter {
//
//    YEDIndividualListener listener;
//    SimpleCanvas3D canvas = new SimpleCanvas3D();
//    ArrayList<TransformGroup> currentGroups = new ArrayList<TransformGroup>();
//    TreeMap<Integer, HelpIndividual> positions;
//
//    public IntegerIndividualPlotter(YEDIndividualListener listener) {
//        this.positions = listener.positions;
//        this.listener = listener;
//    }
//
//    public SimpleCanvas3D createCanvas() {
//        canvas.addShape(createLandscape());
////        canvas.setBackgroundColor(Color.WHITE);
//        addIndividuals(0, Status.EA);
//        canvas.compile();
//        canvas.setDefaultViewPosition();
//        canvas.setMouseInteraction(true);
//        return canvas;
//    }
//
//    public void addIndividuals(int numberOfGeneration, Status status) {
//        if (status == Status.LS) {
//            ArrayList<Individual<IntegerCoding>> arrayList = listener.localSearchIndividuals.get(numberOfGeneration);
//            for (Individual<IntegerCoding> individual : arrayList) {
//
//                if (individual.getFitness() == 0) {
//                    addGeometry(individual, createAppearance(Color.RED));
//                } else {
//                    addGeometry(individual, createAppearance(Color.YELLOW));
//                }
//            }
//        }
//        if (status == Status.EA) {
//            ArrayList<Individual<IntegerCoding>> arrayList = listener.recombinationIndividuals.get(numberOfGeneration);
//            for (Individual<IntegerCoding> individual : arrayList) {
//
//                if (individual.getFitness() == 0) {
//                    addGeometry(individual, createAppearance(Color.RED));
//                } else {
//                    addGeometry(individual, createAppearance(Color.GREEN));
//                }
//            }
//        }
//    }
//
//    public SimpleCanvas3D getCanvas() {
//        return canvas;
//    }
//
//    private Shape3D createLandscape() {
//
//
//        HashMap<Point2d, Point3d> mapFrom2DTo3D = new HashMap<Point2d, Point3d>();
//
//        for (Integer integer : positions.keySet()) {
//            HelpIndividual indi = positions.get(integer);
//            Point3d p = new Point3d(indi.p.x, indi.p.y, indi.fitness);
//            Point2d p2 = new Point2d(indi.p.x, indi.p.y);
//            mapFrom2DTo3D.put(p2, p);
//        }
//        DelaunayTriangulator<Point2d> triangulator = DelaunayTriangulator.triangulate(mapFrom2DTo3D.keySet());
//        Point2d[] triangles = triangulator.getTriangles();
//        double[] f = new double[triangles.length];
//        for (int i = 0; i < f.length; i++) {
//            f[i] = mapFrom2DTo3D.get(triangles[i]).z;
//        }
//        TriangulatedScalarFunction2d fun = new TriangulatedScalarFunction2d(triangles, f);
//        BoundingBox2d box = fun.getBoundingBox2d();
////        RasterScalarFunction2d funRastered = new RasterScalarFunction2d(box.getXmin()+5, box.getYmin()+5, box.getXmax()-5, box.getYmax()-5, 200, 200, fun);
////
////        RasterScalarFunction2dPlotter plotterLandscape = new RasterScalarFunction2dPlotter(funRastered, Color.WHITE, 1.0);
////        return plotterLandscape.createShape();
//
//
//        GeometryArray ga = J3DGeometryGenerator.getGeometry(fun, GeometryArray.ALLOW_COLOR_READ);
//
//        Shape3D landscape = new Shape3D(ga);
//        landscape.setAppearance(createLandscapeAppearance());
//        return landscape;
//
//    }
//
//    private Appearance createLandscapeAppearance() {
//        Appearance ap = new Appearance();
//        PolygonAttributes poly = new PolygonAttributes();
//        ColoringAttributes col = new ColoringAttributes(new Color3f(Color.WHITE), ColoringAttributes.NICEST);
//        poly.setPolygonMode(PolygonAttributes.POLYGON_LINE);
//        poly.setCullFace(PolygonAttributes.CULL_NONE);
//        ap.setColoringAttributes(col);
//        TransparencyAttributes transAtt = new TransparencyAttributes();
//        transAtt.setTransparencyMode(TransparencyAttributes.NICEST);
//        transAtt.setTransparency((float) (220. / 255.0));
//        ap.setTransparencyAttributes(transAtt);
//        ap.setPolygonAttributes(poly);
//        return ap;
//
//    }
//
//    private Appearance createAppearance(Color c) {
//        Appearance ap = new Appearance();
//        ColoringAttributes col = new ColoringAttributes(new Color3f(c), ColoringAttributes.FASTEST);
//        PolygonAttributes poly = new PolygonAttributes();
//        poly.setPolygonMode(PolygonAttributes.POLYGON_FILL);
//        poly.setCullFace(PolygonAttributes.CULL_NONE);
//        ap.setPolygonAttributes(poly);
//        ap.setColoringAttributes(col);
//        return ap;
//
//    }
//
//    private void addGeometry(Individual<IntegerCoding> individual, Appearance ap) {
//        TransformGroup cctg = new TransformGroup();
//        float width = 0.6f;
//        Box c = new Box(width,width, width, ap);
//        cctg.addChild(c);
//        Transform3D cc3d = new Transform3D();
//        HelpIndividual h = positions.get(individual.number);
//        if (h == null) {
//            return;
//        }
//        Point3d p = h.p;
//        Vector3d v = new Vector3d(p.x -width/2., p.y-width/2., individual.getFitness()-width/2.);
//        cc3d.setTranslation(v);
//        System.out.println("I: " + v);
//        cctg.setTransform(cc3d);
//        canvas.addGroup(cctg);
//        currentGroups.add(cctg);
//    }
//}
