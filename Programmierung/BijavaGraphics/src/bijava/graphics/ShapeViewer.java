package bijava.graphics;

import bijava.graphics.canvas2D.ShapeListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * TODO: Kommentieren
 *
 * @author berthold
 */
public class ShapeViewer extends JComponent {
    
    private ArrayList<ShapeListener> shapeListener = new ArrayList<ShapeListener>();

    private ArrayList<Shape> shapes = new ArrayList<Shape>();
    
    private Map<Shape, Object> objectMap = new IdentityHashMap<Shape, Object>();
    
    private Map<Shape, AffineTransform> transformMap = new IdentityHashMap<Shape, AffineTransform>();
    
    private Rectangle2D boundingBox;
    
    private double borderFactor = 0.0;
    
    private AffineTransform wTSBuffer = new AffineTransform();
    
    public ShapeViewer() {
    }
    
    public void zoomTotal(double borderFactor) {
        updateBoundingBox();
        this.borderFactor = borderFactor; 
    }
    
    public void addShapeListener(ShapeListener sl){
        shapeListener.add(sl);
    }

    public void removeShapeListener(ShapeListener sl){
        shapeListener.remove(sl);
    }
        
    public AffineTransform getWorldToScreen() {
        
        // TODO: performant und schoen machen (Rundungsfehler, Ausrichtung unten links, etc.)
        
        AffineTransform ret = new AffineTransform();
        
        
        // Isotrope Skalierung
        double i_scale = 1.;
        
        if (boundingBox != null) {
            double x_scale = (this.getWidth())/((1+borderFactor)*boundingBox.getWidth());
            double y_scale = (this.getHeight())/((1+borderFactor)*boundingBox.getHeight());
            i_scale = Math.min(x_scale, y_scale);
        }
        
        
        ret.translate(0., boundingBox.getHeight()*i_scale);
        ret.scale(1., -1.);
        ret.scale(i_scale, i_scale);
        ret.translate(-(boundingBox.getMinX() - borderFactor/2.*boundingBox.getWidth()), -(boundingBox.getMinY() + borderFactor/2.*boundingBox.getHeight()));
        
        return ret;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        // Merken der aktuellen Transformation
        AffineTransform temp = g2.getTransform();
        
        // world-to-screen Trafo
        wTSBuffer = getWorldToScreen();
        
        for (Shape s : shapes) {
            g2.setTransform(wTSBuffer);
            g2.transform(transformMap.get(s));
            g2.draw(s);
        }
        
        // Setzen der alten Transformation
        g2.setTransform(temp);
    }
    
    
    
    public void addShape(Shape shape) {
        addShape(shape, null);
    }
    
    public void addShape(Shape shape, Object obj) {
        if (shapes.contains(shape))
            return;
        shapes.add(shape);
        associateObject(shape, obj);
        setTransform(shape, null);
    }
    
    public void associateObject(Shape s, Object o) {
        if (!shapes.contains(s))
            return;
        if (o == null)
            objectMap.remove(s);
        else
            objectMap.put(s, o);
    }
    
    public void setTransform(Shape s, AffineTransform at) {
        if (!shapes.contains(s))
            return;
        if (at == null)
            transformMap.put(s, new AffineTransform());
        else
            transformMap.put(s, at);
       }
    
    private void updateBoundingBox() {
        boundingBox = null;
        Rectangle2D temp = null;
        for (Shape s : shapes) {
            temp = transformMap.get(s).createTransformedShape(s.getBounds2D()).getBounds2D();
            if (boundingBox == null)
                boundingBox = temp;
            else
                boundingBox.add(temp);
        }
    }
    
    public void testContains(Point2D.Double p) {
        Point2D.Double p_ = new Point2D.Double();
        for (Shape s : shapes) {
            try {
                transformMap.get(s).createInverse().transform(p, p_);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(ShapeViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("shape contains "+p_+": "+s.contains(p_));
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        JFrame f = new JFrame();
        final ShapeViewer sv = new ShapeViewer();

        Point2D p_start = new Point2D.Double(  0.0,   0.0);
        Point2D p_ctrl1 = new Point2D.Double(  0.0, 200.0);
        Point2D p_ctrl2 = new Point2D.Double(200.0,   0.0);
        Point2D p_end   = new Point2D.Double(  0.0,   0.0);
        
        CubicCurve2D qc = new CubicCurve2D.Double(
                p_start.getX(), p_start.getY(), 
                p_ctrl1.getX(), p_ctrl1.getY(),
                p_ctrl2.getX(), p_ctrl2.getY(),
                p_end.getX(),   p_end.getY()
        );
        
        sv.addShape(qc);
        sv.addShape(new Ellipse2D.Double(-50., -50., 100., 100.));
        
        sv.zoomTotal(0.1);
        
        f.add(sv);
        f.setSize(500,500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        
        
       
    }
    
}
