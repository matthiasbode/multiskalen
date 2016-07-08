package bijava.graphics.canvas2D;


import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

//==============================================================================//
/** Klassenbeschreibung
 *
 *  @author berthold
 */
//==============================================================================//
public class ShapeCollectionEvent {

    private ArrayList<GraphicShape> shapes;
	private MouseEvent e;	
	public Point2D world;

//------------------------------------------------------------------------------//
/** Methodenbeschreibung
 *
 *  @param
 *  @return
 */
//------------------------------------------------------------------------------//
    public ShapeCollectionEvent(MouseEvent e, Point2D world) {
        this.e = e;
        this.world = world;
        shapes = new ArrayList<GraphicShape>();
    }

    public boolean addShape(GraphicShape s) {
        return shapes.add(s);
    }

    public boolean addShape(ShapeEvent se) {
        return shapes.add(se.getSource());
    }

    public boolean removeShape(GraphicShape s) {
        return shapes.remove(s);
    }

    public ArrayList<GraphicShape> getShapes() {
        return shapes;
    }

    public MouseEvent getMouseEvent() {
        return e;
    }

    public Point2D getWorldCoordinates() {
        return world;
    }

    public int getShapeCount() {
        return shapes.size();
    }
}