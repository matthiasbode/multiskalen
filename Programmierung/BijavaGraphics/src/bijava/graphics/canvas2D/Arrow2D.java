package bijava.graphics.canvas2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Kai Steinborn copied from Arrowclass in bijava.graphics.shapes
 */
public class Arrow2D extends GraphicShape{

    private Point2D p1, p2;
    private float l;
    private float b;
    private int type;
    private double l2;
    
    public Arrow2D(Point2D p1, Point2D p2) {
        this(p1, p2, 15, 10, 1);
//        this(p1, p2, 75, 50, 0);
    }
    
    public Arrow2D(double x1, double y1, double x2, double y2) {
        this(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
    }
    
    public Arrow2D(double x1, double y1, double x2, double y2, int l, int b, int type) {
        this(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), l, b, type);
    }
    
    public Arrow2D(Point2D p1, Point2D p2, int l, int b, int typ) {
        super(new GeneralPath());
        this.p1 = p1;
        this.p2 = p2;
        this.l  = l;
        this.b  = b;
        this.type = typ;
        l2  = Math.sqrt(l*l + b*b);
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
	    double w2 = Math.atan2(b/2, l);

        float x0 = (float) p1.getX(); 
        float y0 = (float) p1.getY(); 
        float x1 = (float) p2.getX(); 
        float y1 = (float) p2.getY(); 
	    
	    double angle = Math.atan2(x1-x0, y1-y0);
	    
	    gp.reset();
        gp.moveTo(x0, y0);
        gp.lineTo(x1, y1);
        gp.moveTo((float)(x1-l2*Math.sin(angle+w2)),(float) (y1 - l2*Math.cos(angle+w2)));
        gp.lineTo(x1, y1);
        gp.lineTo((float)(x1-l2*Math.sin(angle-w2)),(float) (y1 - l2*Math.cos(angle-w2)));

        switch (type) {
        	case 0:
        	    break;
        	case 1:
                gp.closePath();	
        	    break;
        	case 2:
                gp.quadTo( (float)(x1 - l2/2*Math.sin(angle)), 
                        	(float)(y1 - l2/2*Math.cos(angle)),
                        	(float)(p2.getX() - l2*Math.sin(angle+w2)), 
                        	(float)(p2.getY() - l2*Math.cos(angle+w2)));
                gp.closePath();	
        	    break;
        }
	} 
    
    
    public Rectangle2D getBounds2D() {    			return gp.getBounds2D();  }
    public boolean contains(double x, double y) {   return gp.contains(x, y); }
    public boolean contains(Point2D p){

        double abs1 = (p1.getX()-p.getX())*(p1.getX()-p.getX())+(p1.getY()-p.getY())*(p1.getY()-p.getY());
        double abs2 = (p2.getX()-p.getX())*(p2.getX()-p.getX())+(p2.getY()-p.getY())*(p2.getY()-p.getY());
        double abs = (p2.getX()-p1.getX())*(p2.getX()-p1.getX())+(p2.getY()-p1.getY())*(p2.getY()-p1.getY());
        if((Math.sqrt(abs1) + Math.sqrt(abs2)) <= Math.sqrt(abs)+1 ){
            return true;
        }
        return false; 
    }
    
    
}
