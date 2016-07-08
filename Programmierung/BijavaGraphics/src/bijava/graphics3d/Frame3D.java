package bijava.graphics3d;

import java.awt.GraphicsConfiguration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import bijava.graphics3d.utils.CapturingCanvas3D;
import bijava.graphics3d.utils.MouseRotate2;
import bijava.graphics3d.utils.UpdatableGeometry;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * @author kaapke, 14.03.2005
 */
public class Frame3D extends JFrame {

    protected SimpleUniverse universe;
    public SimpleUniverse getUniverse() { return universe; }
    
    protected Canvas3D canvas;
    public Canvas3D getCanvas3D() { return canvas; }
    
    protected BranchGroup root;
    public BranchGroup getBranchGroup() { return root; }
    
    protected TransformGroup lastTransGroup;
    public TransformGroup lastTransformGroup() { return lastTransGroup; }
    
    public TransformGroup mouseTG;
    protected TransformGroup translationTG;
    protected TransformGroup scalingTG;
    
    protected BranchGroup shapeGroup;
    BranchGroup oldClone, newClone;
    
    private boolean whiteBackground;
    
    /**
     * 
     * @param title
     */
    public Frame3D(String title) {
        this(title, false);
    }
    
    public Frame3D(String title, boolean whiteBackground) {
        super(title);
        this.whiteBackground = whiteBackground;
	    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
	    canvas = new CapturingCanvas3D(config);	    	    
	    createUniverse();	    
	    super.getContentPane().add(canvas);
    }
        
    private void detach() {    	
    	if (oldClone != null) translationTG.addChild(oldClone);
    	shapeGroup.detach();    	
    }
    private void reattach() {
    	newClone = (BranchGroup) shapeGroup.cloneTree(true);
    	translationTG.addChild(shapeGroup);
    	if (oldClone != null) oldClone.detach();
    	oldClone = newClone;
    }
    
    public void add(Shape3D comp) {
    	detach();
        shapeGroup.addChild(comp);        
        reattach();
    }
    
    public void add(Group group) {
    	detach();
    	shapeGroup.addChild(group);        
        reattach();       
    }
    
    public void remove(Node comp) {
    	detach();
        shapeGroup.removeChild(comp);        
        reattach();
    }
    
    public void replace(Shape3D comp1, Shape3D comp2) {
    	detach();
        shapeGroup.removeChild(comp1);
        shapeGroup.addChild(comp2);        
        reattach();   
    }    
    
    public void update(UpdatableGeometry comp) {           	
    	detach();    	
    	comp.updateGeometry();    	    	
    	reattach();               
    }
    
    public void update(UpdatableGeometry[] comps) {        
    	detach();    
    	for (UpdatableGeometry comp : comps) 
        	comp.updateGeometry();           	
    	reattach();               
    }    
    
    protected void createUniverse() {
        
        // Einfaches Universum für die 3D Szene
        universe = new SimpleUniverse(canvas);
        
        // Setzt den Abstand des Betrachters vom Screen
        universe.getViewingPlatform().setNominalViewingTransform();                
        universe.getViewer().getView().setBackClipDistance(universe.getViewer().getView().getFrontClipDistance() * 10000);
        
        // Root
        root = new BranchGroup();	  
        root.setCapability(BranchGroup.ALLOW_DETACH);     
        
        mouseTG = new TransformGroup();
        mouseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        mouseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        Transform3D scaleMatrix = new Transform3D();
        scalingTG = new TransformGroup(scaleMatrix);       
        scalingTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        scalingTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);        
        mouseTG.addChild(scalingTG);
        
        Transform3D translationMatrix = new Transform3D();
        translationTG = new TransformGroup(translationMatrix);                      
        translationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        translationTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        scalingTG.addChild(translationTG);
              
        //AmbientLight ambiLight = new AmbientLight();
        //ambiLight.setBounds(new BoundingSphere(new Point3d(), 10000.));
        //ambiLight.setEnable(true);
        //root.addChild(ambiLight);

	    MouseRotate2 drehVerhalten = new MouseRotate2(mouseTG);
	    drehVerhalten.setSchedulingBounds(new BoundingSphere());
	    root.addChild(drehVerhalten);
	    
	    MouseZoom zoomVerhalten = new MouseZoom(mouseTG);
	    zoomVerhalten.setSchedulingBounds(new BoundingSphere());
	    root.addChild(zoomVerhalten);
	    
	    MouseTranslate translateVerhalten = new MouseTranslate(mouseTG);
	    translateVerhalten.setSchedulingBounds(new BoundingSphere());
	    root.addChild(translateVerhalten);				    
	    
	    Background backg = new Background();
	    // Background - white
	    if (whiteBackground) {	    	
	    	backg.setColor(new Color3f(1.0f, 1.0f, 1.0f));
	    	backg.setApplicationBounds(new BoundingSphere(new Point3d(), 1000.));
	    	root.addChild(backg);
	    }
	    
	    // Koordinatenaxen
	    Color3f bgColor = new Color3f();
	    backg.getColor(bgColor);
        translationTG.addChild(new Axis3D(new float[] { 1.f-bgColor.x, 1.f-bgColor.y, 1.f-bgColor.z} ));
        
        // alle TGs einhängen
        root.addChild(mouseTG);
        
        // Shapes müssen an die letzte TG angehängt werden, wenn alle Transformationen für das Shape gelten sollen.        
        shapeGroup = new BranchGroup();
        shapeGroup.setCapability(BranchGroup.ALLOW_DETACH);        
        shapeGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        translationTG.addChild(shapeGroup);
        lastTransGroup = translationTG;
        
	    universe.addBranchGraph(root);
	}
    
    public void inflateZ(double z) {
        Transform3D scaleMatrix = new Transform3D();
        scalingTG.getTransform(scaleMatrix);
        Vector3d v = new Vector3d();
        scaleMatrix.getScale(v);
        v.z *= z;
        scaleMatrix.setScale(v);
        scalingTG.setTransform(scaleMatrix);
    }
    
    public void setContentDimensions(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
        //System.out.println("Frame3D - Abmessungen: " + xmin + ", " + xmax + ", " + ymin + ", " + ymax + ", " + zmin + ", " + zmax);
        
        double scaleXY = 1.9/Math.max((xmax-xmin), (ymax-ymin)); 
        double scaleZ = scaleXY;
        
        Transform3D scaleMatrix = new Transform3D();
        scaleMatrix.setScale(new Vector3d(scaleXY, scaleXY, scaleZ));
        scalingTG.setTransform(scaleMatrix);
        
        Transform3D translationMatrix = new Transform3D();
        translationMatrix.setTranslation(new Vector3d(-(xmin+(xmax-xmin)/2), -(ymin+(ymax-ymin)/2), -(zmin+(zmax-zmin)/2)));       
        translationTG.setTransform(translationMatrix);
    }      
    
    public void setContentDimensions(double[] dim) {
        setContentDimensions(dim[0], dim[1], dim[2], dim[3], dim[4], dim[5]);
    }
    
    
    public void centerShapes() {
    	BoundingSphere b = (BoundingSphere) shapeGroup.getBounds();
    	Point3d tmp = new Point3d();
    	b.getCenter(tmp);
    	System.out.println(tmp);
        Transform3D translationMatrix = new Transform3D();
        translationMatrix.setTranslation(new Vector3d(-tmp.x, -tmp.y, -tmp.z));       
        translationTG.setTransform(translationMatrix);
    }
    
    public void setTransform(Transform3D trafo) {
    	 mouseTG.setTransform(trafo);
    }
}


