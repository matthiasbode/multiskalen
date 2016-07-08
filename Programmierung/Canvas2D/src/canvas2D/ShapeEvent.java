package canvas2D;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class ShapeEvent {
	
	private GraphicShape s;
	private MouseEvent e;	
	public Point2D world;
	
	public ShapeEvent(GraphicShape s, MouseEvent e, Point2D world){
		this.s = s;
		this.e = e;
		this.world = world;
	}
	
	public GraphicShape getSource(){
		return s; 
	}
	
	public MouseEvent getMouseEvent(){
		return e; 
	}

    public String toString() {
        return "ShapeEvent ["+s+", "+e+", "+world+"]";
    }
}
