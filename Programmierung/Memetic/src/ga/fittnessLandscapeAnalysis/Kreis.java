/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.fittnessLandscapeAnalysis;

import java.awt.geom.Point2D;

/**
 *
 * @author bode
 */
public class Kreis {
    Point2D m;
    double r;

    public Kreis(Point2D m, double r) {
        this.m = m;
        this.r = r;
    }
    
    public Point2D[] getSchnittPunkte(Kreis k){
        Point2D[] res  = new Point2D[2];
        
        Point2D mp1 = this.m;
        Point2D mp2 = k.m;
        double r1 = this.r;
        double r2 = k.r;
        
        double d = mp1.distance(mp2);
        
        double dx = mp2.getX() - mp1.getX();
        double dy = mp2.getY() - mp1.getY();
                
        double a = (r1*r1  - r2*r2 + d*d) /(2*d);
        double h = Math.sqrt(r1*r1 - a*a);
        
        res[0] = new Point2D.Double(mp1.getX() + (a/d) * dx - (h/d) * dy,  mp1.getY() + (a/d) * dy + (h/d) * dx );
        res[1] = new Point2D.Double(mp1.getX() + (a/d) * dx + (h/d) * dy,  mp1.getY() + (a/d) * dy - (h/d) * dx );
        
        return res;
    }
    
    public static void main(String[] args) {
        Kreis k1 = new Kreis(new Point2D.Double(150, 0), 120);
        Kreis k2 = new Kreis(new Point2D.Double(0, 36), 177);
        Point2D[] schnittPunkte = k1.getSchnittPunkte(k2);
        System.out.println(schnittPunkte[0]);
        System.out.println(schnittPunkte[1]);
        
    }
}
