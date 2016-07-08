package applications.functions.graphics3d;

import javax.media.j3d.*;
import javax.vecmath.*;


/**
 * Dient der Erzeugung eines einfachen Koordinatenkreuzes zum Anzeigen in
 * Java3D-Umgebungen.
 * @author Kai Kaapke, Jan Stilhammer
 *
 */
public class Axis3D extends BranchGroup {

	private float[] color;
	private float scaleFactor = 1.0f;
	
	/**
	 * Erzeugt ein Koordinatenkreuz mit der Achsenfarbe <tt>color</tt> in der Gr&ouml;&szlig;e <tt>size</tt>. 
	 * @param color Farbe der Achsen
	 * @param size  Gr&ouml;&szlig;e des Koordinatenkreuzes
	 */
	public Axis3D(float[] color, float size) {
	
	    super();
	    this.scaleFactor = size;
	    this.color = color;
	    this.addChild(createAxis());
        
	    Font3D font = new Font3D(new java.awt.Font("Helvetia", java.awt.Font.PLAIN, (int)(1*scaleFactor)), new FontExtrusion());        	   
	    Text3D xAchse = new Text3D(font, "x", new Point3f(1.1f*scaleFactor, 0.0f, 0.0f));
	    Text3D yAchse = new Text3D(font, "y", new Point3f(0.0f, 1.1f*scaleFactor, 0.0f));
	    Text3D zAchse = new Text3D(font, "z", new Point3f(0.0f, 0.0f, 1.1f*scaleFactor));	    
	    
            
	    this.addChild(new Shape3D(xAchse));
	    this.addChild(new Shape3D(yAchse));
	    this.addChild(new Shape3D(zAchse));	    
	}
	
	/**
	 * Erzeugt ein Koordinatenkreuz mit der Achsenfarbe <tt>color</tt> der Gr&ouml;&szlig;e 1. 
	 * @param color Farbe der Achsen
	 */
	public Axis3D(float[] color){
		this(color, 1.0f);
	}
	
	/**
	 * Erzeugt ein weiszes Koordinatenkreuz der Groesze 1. 
	 * @param color Farbe der Achsen
	 */
	public Axis3D() {
		this(new float[] { 1.0f, 1.0f, 1.0f } );
	}

	private Shape3D createAxis(){
	    
	    // create line for X axis
	    IndexedLineArray axisLines = new IndexedLineArray(18, GeometryArray.COORDINATES, 30);	    
	    axisLines.setCoordinate( 0, new Point3f(-1.0f*scaleFactor, 0.0f, 0.0f));
	    axisLines.setCoordinate( 1, new Point3f( 1.0f*scaleFactor, 0.0f, 0.0f));
	    axisLines.setCoordinate( 2, new Point3f( 0.9f*scaleFactor, 0.1f*scaleFactor, 0.1f*scaleFactor));
	    axisLines.setCoordinate( 3, new Point3f( 0.9f*scaleFactor,-0.1f*scaleFactor, 0.1f*scaleFactor));
	    axisLines.setCoordinate( 4, new Point3f( 0.9f*scaleFactor, 0.1f*scaleFactor,-0.1f*scaleFactor));
	    axisLines.setCoordinate( 5, new Point3f( 0.9f*scaleFactor,-0.1f*scaleFactor,-0.1f*scaleFactor));
	    axisLines.setCoordinate( 6, new Point3f( 0.0f,-1.0f*scaleFactor, 0.0f));
	    axisLines.setCoordinate( 7, new Point3f( 0.0f, 1.0f*scaleFactor, 0.0f));
	    axisLines.setCoordinate( 8, new Point3f( 0.1f*scaleFactor, 0.9f*scaleFactor, 0.1f*scaleFactor));
	    axisLines.setCoordinate( 9, new Point3f(-0.1f*scaleFactor, 0.9f*scaleFactor, 0.1f*scaleFactor));
	    axisLines.setCoordinate(10, new Point3f( 0.1f*scaleFactor, 0.9f*scaleFactor,-0.1f*scaleFactor));
	    axisLines.setCoordinate(11, new Point3f(-0.1f*scaleFactor, 0.9f*scaleFactor,-0.1f*scaleFactor));
	    axisLines.setCoordinate(12, new Point3f( 0.0f, 0.0f,-1.0f*scaleFactor));
	    axisLines.setCoordinate(13, new Point3f( 0.0f, 0.0f, 1.0f*scaleFactor));
	    axisLines.setCoordinate(14, new Point3f( 0.1f*scaleFactor, 0.1f*scaleFactor, 0.9f*scaleFactor));
	    axisLines.setCoordinate(15, new Point3f(-0.1f*scaleFactor, 0.1f*scaleFactor, 0.9f*scaleFactor));
	    axisLines.setCoordinate(16, new Point3f( 0.1f*scaleFactor,-0.1f*scaleFactor, 0.9f*scaleFactor));
	    axisLines.setCoordinate(17, new Point3f(-0.1f*scaleFactor,-0.1f*scaleFactor, 0.9f*scaleFactor));
	    
	    
	    
	    
	    axisLines.setCoordinateIndex( 0, 0);
	    axisLines.setCoordinateIndex( 1, 1);
	    axisLines.setCoordinateIndex( 2, 2);
	    axisLines.setCoordinateIndex( 3, 1);
	    axisLines.setCoordinateIndex( 4, 3);
	    axisLines.setCoordinateIndex( 5, 1);
	    axisLines.setCoordinateIndex( 6, 4);
	    axisLines.setCoordinateIndex( 7, 1);
	    axisLines.setCoordinateIndex( 8, 5);
	    axisLines.setCoordinateIndex( 9, 1);
	    axisLines.setCoordinateIndex(10, 6);
	    axisLines.setCoordinateIndex(11, 7);
	    axisLines.setCoordinateIndex(12, 8);
	    axisLines.setCoordinateIndex(13, 7);
	    axisLines.setCoordinateIndex(14, 9);
	    axisLines.setCoordinateIndex(15, 7);
	    axisLines.setCoordinateIndex(16,10);
	    axisLines.setCoordinateIndex(17, 7);
	    axisLines.setCoordinateIndex(18,11);
	    axisLines.setCoordinateIndex(19, 7);
	    axisLines.setCoordinateIndex(20,12);
	    axisLines.setCoordinateIndex(21,13);
	    axisLines.setCoordinateIndex(22,14);
	    axisLines.setCoordinateIndex(23,13);
	    axisLines.setCoordinateIndex(24,15);
	    axisLines.setCoordinateIndex(25,13);
	    axisLines.setCoordinateIndex(26,16);
	    axisLines.setCoordinateIndex(27,13);
	    axisLines.setCoordinateIndex(28,17);
	    axisLines.setCoordinateIndex(29,13);
	 
	    Appearance appear = new Appearance();
	    appear.setColoringAttributes(new ColoringAttributes(color[0], color[1], color[2], ColoringAttributes.SHADE_GOURAUD));
	    
	    LineAttributes lineAttrib = new LineAttributes();
	    lineAttrib.setLineWidth(2.0f);
	    appear.setLineAttributes(lineAttrib);
	    
	    return new Shape3D(axisLines, appear);

	} // end of Axis createGeometry()
} // end of class Axis
