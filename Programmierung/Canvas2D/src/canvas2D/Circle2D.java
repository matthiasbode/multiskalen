package canvas2D;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

public class Circle2D extends GraphicShape{
	

	public Circle2D(double xm, double ym, double r){
		super(new GeneralPath(new Ellipse2D.Double(xm-r,ym-r,2*r,2*r)));
	}
}