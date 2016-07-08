package bijava.graphics.shapes;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import static java.lang.Math.*;

/**
 * <P><B>ES_Arrow.java</B> <I>(engl. )</I>: Ein ES_Arrow.java ist ... </P>
 *
 * Typen:	-->		0
 * 			--|>	1
 * 			--)>	2
 *
 * @author Axel
 */
public class Arrow implements Shape, Serializable {

    private GeneralPath path;
    private Point2D p1, p2;
    private float l;
    private float b;
    private int type;
    private double l2;
    
    public Arrow(Point2D p1, Point2D p2) {
        this(p1, p2, 15, 10, 1);
//        this(p1, p2, 75, 50, 0);
    }
    
    public Arrow(double x1, double y1, double x2, double y2) {
        this(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
    }
    
    public Arrow(double x1, double y1, double x2, double y2, int l, int b, int type) {
        this(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), l, b, type);
    }
    
    public Arrow(Point2D p1, Point2D p2, int l, int b, int typ) {
        this.p1 = p1;
        this.p2 = p2;
        this.l  = l;
        this.b  = b;
        this.type = typ;
        l2  = sqrt(l*l + b*b);
        path = new GeneralPath();
        updatePath();
    }
    
    public void setPoints(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
        updatePath();
    }
    
    public Point2D getP1() { return p1; }
    public Point2D getP2() { return p2; }
    public int getTyp()    { return type; }
    public int getTipWidth(){	return (int)b;	}
    public int getTipLength() {	return (int)l;   }
    
    
	private void updatePath() {   
	    double w2 = atan2(b/2, l);

        float x0 = (float) p1.getX(); 
        float y0 = (float) p1.getY(); 
        float x1 = (float) p2.getX(); 
        float y1 = (float) p2.getY(); 
	    
	    double angle = atan2(x1-x0, y1-y0);
	    
	    path.reset();
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
        path.moveTo((float)(x1-l2*sin(angle+w2)),(float) (y1 - l2*cos(angle+w2)));
        path.lineTo(x1, y1);
        path.lineTo((float)(x1-l2*sin(angle-w2)),(float) (y1 - l2*cos(angle-w2)));

        switch (type) {
        	case 0:
        	    break;
        	case 1:
                path.closePath();	
        	    break;
        	case 2:
                path.quadTo( (float)(x1 - l2/2*sin(angle)), 
                        	(float)(y1 - l2/2*cos(angle)),
                        	(float)(p2.getX() - l2*sin(angle+w2)), 
                        	(float)(p2.getY() - l2*cos(angle+w2)));
                path.closePath();	
        	    break;
        }
	} 
    
    public Rectangle getBounds() {        			return path.getBounds();    }
    public Rectangle2D getBounds2D() {    			return path.getBounds2D();  }
    public boolean contains(double x, double y) {   return path.contains(x, y); }
    public boolean contains(Point2D p){

        double abs1 = (p1.getX()-p.getX())*(p1.getX()-p.getX())+(p1.getY()-p.getY())*(p1.getY()-p.getY());
        double abs2 = (p2.getX()-p.getX())*(p2.getX()-p.getX())+(p2.getY()-p.getY())*(p2.getY()-p.getY());
        double abs = (p2.getX()-p1.getX())*(p2.getX()-p1.getX())+(p2.getY()-p1.getY())*(p2.getY()-p1.getY());
        if((Math.sqrt(abs1) + Math.sqrt(abs2)) <= Math.sqrt(abs)+1 ){
            return true;
        }
        return false; 
    }
    public boolean intersects(double x, double y, double w, double h) {        	return path.intersects(x, y, w, h);    }
    public boolean intersects(Rectangle2D r) {   	return path.intersects(r); }
    public boolean contains(double x, double y, double w, double h) {        	return path.contains(x, y, w, h);    }
    public boolean contains(Rectangle2D r) {     	return path.contains(r);   }
    public PathIterator getPathIterator(AffineTransform at) {        return path.getPathIterator(at);    }
    public PathIterator getPathIterator(AffineTransform at, double flatness) {	return path.getPathIterator(at, flatness);    }
}
