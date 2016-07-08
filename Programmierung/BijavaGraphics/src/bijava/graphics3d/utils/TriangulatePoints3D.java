/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bijava.graphics3d.utils;

import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.Triangle2d;
import bijava.geometry.dim3.Point3d;
import bijava.geometry.dim3.Triangle3d;
import bijava.graphics3d.SimpleCanvas3D;
import bijava.math.function.interpolation.TriangulatedScalarFunction2d;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.Color3f;

/**
 *
 * @author behrensd
 */
public class TriangulatePoints3D {

    private ArrayList<Triangle3d> triangles = new ArrayList<>();

    public Shape3D getShape() {
        return shape;
    }

    public ArrayList<Triangle3d> getTriangles() {
        return triangles;
    }
    private Shape3D shape;

    public TriangulatePoints3D(Map<Point3d, Color> map) {

        ArrayList<Point2d> plist = new ArrayList<>();

        HashMap<Point2d, Point3d> pointmap = new HashMap<>();

        for (Point3d p : map.keySet()) {
            Point2d temp = new Point2d(p.x, p.z);
            pointmap.put(temp, p);
            plist.add(temp);
        }

        Point2d[] points = new Point2d[plist.size()];
        points = plist.toArray(points);

        double[] f = new double[plist.size()];

        f[0] = Double.NEGATIVE_INFINITY;
        f[plist.size() - 1] = Double.POSITIVE_INFINITY;

        TriangulatedScalarFunction2d func = new TriangulatedScalarFunction2d(points, f);

        // Map 2d Triangles to 3d Triangles

        for (Triangle2d t : func.triangles) {
            Point2d[] temppoints = t.getPoints();

            Point3d[] points3 = new Point3d[3];

            for (int j = 0; j < 3; j++) {
                points3[j] = pointmap.get(temppoints[j]);
            }

            triangles.add(new Triangle3d(points3[0], points3[1], points3[2]));
        }

        // Create GeometryArray for Visulization

        IndexedTriangleArray triangles3d = new IndexedTriangleArray(map.keySet().size(), GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_COORD_INDEX_ONLY, func.getNumberOfTriangles()*3);

        // Set Coordinates & Colors
        
        HashMap<Point3d, Integer> intmap = new HashMap<>();

        int count = 0;
        for (Point3d p : map.keySet()) {
            triangles3d.setCoordinate(count, p);
            triangles3d.setColor(count, new Color3f(map.get(p)));
            intmap.put(p, count);
            count++;
        }

        // Set Indices

        ArrayList<Point3d> points3 = new ArrayList<>();

        for (Triangle3d t : triangles) {
            List<Point3d> p = Arrays.asList(t.getPoints());
            if (t.getArea() < 0)
                Collections.reverse(p);
            
            points3.addAll(p);

        }

        for (int i = 0; i < points3.size(); i++) {
            triangles3d.setCoordinateIndex(i, intmap.get(points3.get(i)));
        }
        
        // Create Shape and Appearance

        shape = new Shape3D(triangles3d);
        
        Appearance app = new Appearance();
        PolygonAttributes poly = new PolygonAttributes();
        poly.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        poly.setCullFace(PolygonAttributes.CULL_NONE);

        app.setPolygonAttributes(poly);
        
        shape.setAppearance(app);



    }

    public static void main(String[] args) {
        
        // Stumpfes Beispiel

        Point3d p1 = new Point3d(1., 7., 4.);
        Point3d p2 = new Point3d(3., 4., 0.);
        Point3d p3 = new Point3d(5., 2., 0.);
        Point3d p4 = new Point3d(-2., 1., 8.);
        Point3d p5 = new Point3d(-1.,4.,3.);

        HashMap<Point3d, Color> map = new HashMap<>();

        map.put(p1, Color.BLUE);
        map.put(p2, Color.YELLOW);
        map.put(p3, Color.GREEN);
        map.put(p4, Color.RED);
        map.put(p5, Color.WHITE);

        TriangulatePoints3D t = new TriangulatePoints3D(map);


        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 786);

        SimpleCanvas3D canvas = new SimpleCanvas3D();
        frame.add(canvas);
        
        canvas.addShape(t.getShape(),false);
        
        
        canvas.setMouseInteraction(true);
        
        AmbientLight ambLight = new AmbientLight();
		canvas.addLight(ambLight);
        
        canvas.compile();
        
        frame.setVisible(true);




    }
}
