/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.functions;

import bijava.geometry.dim2.Point2d;
import bijava.math.function.ScalarFunction2d;
import bijava.math.function.interpolation.RasterScalarFunction2d;
import applications.functions.graphics3d.utils.IsoPalette;
import javax.swing.JFrame;
 
/**
 *
 * @author bode
 */
public class Test {

    public static void main(String[] args) {


        ScalarFunction2d fkt = new ScalarFunction2d() {

            @Override
            public double getValue(Point2d p) {
                 return 0.3*(p.x*p.x-p.y*p.y+(p.y))/(3+Math.cos(p.x+p.y)+Math.sin(p.x-p.y))+Math.exp((Math.cos((p.x)*(p.y))))/1.5;
            }
        };
        RasterScalarFunction2d rsf2d = new RasterScalarFunction2d(-5, -5, 5, 5, 200, 200, fkt);
        IsoPalette pal = new IsoPalette(rsf2d.getMin(), rsf2d.getMax());
        pal.setFarbverlauf(IsoPalette.Palette.Rainbow);
        System.out.println(pal.getMinVal());
        System.out.println(pal.getMaxVal());
        RasterScalarFunction2dPlotter plotter = new RasterScalarFunction2dPlotter(rsf2d, pal, 1.0);
        JFrame f = new JFrame("Funktion");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(plotter.getSimpleCanvas());
        f.setVisible(true);
    }
}
