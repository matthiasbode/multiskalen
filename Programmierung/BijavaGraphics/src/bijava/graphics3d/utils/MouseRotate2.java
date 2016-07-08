package bijava.graphics3d.utils;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;

public class MouseRotate2 extends MouseRotate {

	public MouseRotate2(TransformGroup tg) {
		super(tg);
	}
	
	public void transformChanged(Transform3D transform) {
		//System.out.println("Transform3D: " + transform);
		super.transformChanged(transform);
	}
}
