/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions;

import bijava.geometry.dim2.Point2d;
import bijava.math.function.interpolation.RasterScalarFunction2d;
import applications.functions.graphics3d.SimpleCanvas3D;
import applications.functions.graphics3d.utils.IsoPalette;
import java.awt.Color;
import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

/**
 *
 * @author bode
 */
public class RasterScalarFunction2dPlotter {

    RasterScalarFunction2d function;
    IsoPalette pal;
    Color c;
    double ueberhoehung;

    public RasterScalarFunction2dPlotter(RasterScalarFunction2d function, Color c, double ueberhoehung) {
        this.function = function;
        this.c = c;
        this.ueberhoehung = ueberhoehung;
    }

    public RasterScalarFunction2dPlotter(RasterScalarFunction2d function, IsoPalette pal, double ueberhoehung) {
        this.ueberhoehung = ueberhoehung;
        this.pal = pal;
        this.function = function;
    }

    public SimpleCanvas3D getSimpleCanvas() {
        Shape3D s = createShape();
        SimpleCanvas3D canvas = new SimpleCanvas3D();
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
        PolygonAttributes poly = new PolygonAttributes();
        poly.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        poly.setCullFace(PolygonAttributes.CULL_NONE);

        TransparencyAttributes transAtt = new TransparencyAttributes();
        transAtt.setTransparencyMode(TransparencyAttributes.NICEST);
        transAtt.setTransparency((float) (245 / 255.0));
        ap.setTransparencyAttributes(transAtt);
        ap.setPolygonAttributes(poly);
        return ap;

    }

    private Geometry createGeometry() {


        Point3d[] crispPoints = new Point3d[function.getSizeOfValues()];
        Color4f[] colors = new Color4f[function.getSizeOfValues()];


        for (int i = 0; i < function.getSizeOfValues(); i++) {
            Point2d p = function.getSamplingPointAt(i);
            double[] coords = new double[3];
            coords[0] = p.x;
            coords[1] = p.y;
            coords[2] = function.getValue(p);
            coords[2] *= ueberhoehung;
            if (this.c == null) {
                colors[i] = new Color4f(pal.getColor(coords[2]));
            } else {
                colors[i] = new Color4f(c);
            }
            crispPoints[i] = new Point3d(coords);
        }



        int[] indices = new int[4 * (function.getRowSize() - 1) * (function.getColumnSize() - 1)];
        for (int i = 0; i < function.getRowSize() - 1; i++) {
            for (int j = 0; j < function.getColumnSize() - 1; j++) {
                indices[(i * (function.getColumnSize() - 1) + j) * 4 + 0] = i * function.getColumnSize() + j;
                indices[(i * (function.getColumnSize() - 1) + j) * 4 + 1] = (i + 1) * function.getColumnSize() + j;
                indices[(i * (function.getColumnSize() - 1) + j) * 4 + 2] = (i + 1) * function.getColumnSize() + j + 1;
                indices[(i * (function.getColumnSize() - 1) + j) * 4 + 3] = i * function.getColumnSize() + j + 1;
            }
        }


        //IndexedQuadArray funktioniert nicht mit Farbverlauf
        QuadArray quads = new QuadArray(indices.length, QuadArray.COORDINATES | QuadArray.COLOR_4);
        for (int i = 0; i < indices.length; i++) {
            int punkt = indices[i];
            Point3d p = crispPoints[punkt];
            quads.setCoordinate(i, p);
            quads.setColor(i, colors[punkt]);
        }
        return quads;
    }
}
