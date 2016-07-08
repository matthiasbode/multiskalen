package bijava.graphics3d;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.HiResCoord;
import javax.media.j3d.Leaf;
import javax.media.j3d.Light;
import javax.media.j3d.Locale;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PickInfo;
import javax.media.j3d.PickShape;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.media.j3d.PickInfo.IntersectionInfo;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickTool;


/**
 * <p>Die Klasse <tt>SimpleCanvas3D</tt> stellt eine einfache Standard-Umgebung zur Darstellung von 3D-Objekten
 * zur verf&uuml;gung. Da diese Klasse direkt von <tt>Canvas3D</tt> abgeleitet ist, kann sie direkt 
 * als Visualisierungskomponente in ein GUI eingebunden werden. Die Objekte die diesem Objekt hinzugef&uuml;gt
 * werden befinden sich vor dem Betrachter und k&ouml;nnen mit der Maus verschoben und gedreht werden.
 * Hierzu werden sie zun&auml;chst so verschoben, das das Zentrum eines alle Shapes umfassenden Rechtecks
 * im Koordinatenursprung liegt.</p>
 * <p>Objekte dieser Klasse verf&uuml;gen &uuml;ber einen vollst&auml;ndigen Scenengraphen der wie folgt aufgebaut ist:<br>
 * <img style="align:center;" src="SceneGraph.jpg" /></p>
 * <p>Einem <tt>SimpleCanvas3D</tt>-Objekt k&ouml;nnen <tt>Shape3D</tt>-Objekte mittels der Methode <tt>addShape(Shape3D s)</tt> hinzugef&uuml;gt werden.
 * Es stehen weitere Methoden f&uuml;r Standard-Aufgaben zur Verf&uuml;gung. Bevor ein Objekt dieser Klasse dargestellt werden kann,
 * ist die Methode <tt>compile()</tt> aufzrufen. Ein Beispiel f&uuml;r den Einsatz dieser Klasse zeigt
 * der folgene Code-Schnippsel:
 * <pre>
 * SimpleCanvas3D c = new SimpleCanvas3D(); // erzeugen des Canvas
 * c.setBackgroundColor(Color.DARK_GRAY);   // setzen der Hintergrundfarbe
 * c.addShape(shape);                       // hinzuf&uuml;gen eines Shapes
 * c.setMouseInteraction(true);             // aktivieren der Mausinteraktion
 * c.addDefaultLights();                    // Standardbeleuchtung hinzuf&uuml;gen
 * c.setDefaultViewPosition();              // Betrachtungsposition verschieben
 * c.compile();                             // wichtig!
 * </pre>
 * </p>
 * <p>
 * Die Maussteuerung ist wie folgt belegt:<br>
 * links      - drehen<br>
 * alt-links  - zoomen<br>
 * strg-links - verschieben<br>
 * </p>
 * @author Jan Stilhammer
 *
 */
public class SimpleCanvas3D extends Canvas3D {
	
	private BoundingSphere visibilitySphere; // Fuer Sichtbarkeit und Einflussgrenzen
	private BoundingBox shapeBounds; // Zur Berechnung der translationen
	private Point3d p_max;    // Maximalkoordinaten der BoundingBox
	private Point3d p_min;    // Minimalkoordinaten der BoundingBox
	private Point3d p_center; // Zentralkoordinaten der BoundingBox
	
	private ArrayList<Shape3D> shapeList; // Liste aller Shapes
	private ArrayList<Light> lightsList;  // Liste aller Lichter
	
	private Locale myLocale;
	private BranchGroup root;
	private TransformGroup bg_lights;
	private BranchGroup bg_environment;
	private TransformGroup tg_translateShapes;
	private TransformGroup tg_viewTrafo;
	
	private static final int visibilityScaleFactor = 1000; // scales the visibilitySphere
	private View view;
	private MouseRotate rotate;
	private MouseTranslate translate;
	private MouseZoom zoom;
	
	private static final Color3f bgColor = new Color3f(0,0,0);
	private Background background;
	private TransformGroup tg_interaction;
	
	private static final boolean debug = false;
	private BranchGroup bg_shapes;
	private HashMap<String,BranchGroup> branchGroupMap;
	
	public static final int VIEW_FRONT = 0;
	public static final int VIEW_BACK = 1;
	public static final int VIEW_TOP = 2;
	public static final int VIEW_BOTTOM = 3;
	public static final int VIEW_LEFT = 4;
	public static final int VIEW_RIGHT = 5;
	

	/**
	 * Erzeugt ein neues SimpleCanvas3D-Objekt.
	 * Das Objekt beeinhaltet zun&auml;chst keinerlei
	 * Shape-Objekte oder Lichter. 
	 */
	public SimpleCanvas3D(){
		super(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getBestConfiguration(
						new GraphicsConfigTemplate3D()));
		
		this.shapeList = new ArrayList<Shape3D>(1);
		this.lightsList = new ArrayList<Light>(5);
		
		initBounds();  
		initUniverse();
		initViewGraph();
		initLightGraph();
		initComponetGraph();
		
	}
	
	/**
	 * F&uuml;gt das &uuml;bergeben Shape-Objekt dem Universum hinzu.
	 * @param s Ein neues Shape
	 */
	public void addShape(Shape3D s,boolean translate){
		shapeList.add(s);
		if(bg_shapes.isLive()){
			bg_shapes.detach();
			System.out.println("bg_shapes detached");
		}
		s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
		if(s.getName()==null)s.setName("Shape3D");
		bg_shapes.addChild(s);
		
		// update the Bounds
		updateBounds(s);
		if(translate)translateShapes();
		if(bg_shapes.getParent()==null){
//			bg_shapes.compile();
			tg_translateShapes.addChild(bg_shapes);
		}
	}
	
	/**
	 * F&uuml;gt das &uuml;bergeben Shape-Objekt dem Universum hinzu.
	 * @param s Ein neues Shape
	 */
	public void addShape(Shape3D s, String branchGroupName, boolean translate){
		shapeList.add(s);
		BranchGroup bg = branchGroupMap.get(branchGroupName);
		if(bg.isLive()){
			bg.detach();
			System.out.println(branchGroupName +" detached");
		}
		s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
		if(s.getName()==null)s.setName("Shape3D");
		bg.addChild(s);
		
		// update the Bounds
		updateBounds(s);
		if(translate)translateShapes();
		if(bg.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg);
		}
		
	}
	/**
	 * F&uuml;gt das &uuml;bergeben Shape-Objekt dem Universum hinzu.
	 * @param s Ein neues Shape
	 */
	public void addShape(Collection<? extends Shape3D> coll){
		if (debug)
			System.out.println("SimpleCanvas3D.addShape(Collectiony<Shape3D>)");
		if(bg_shapes.isLive()){
			bg_shapes.detach();
		}
		for (Shape3D s : coll) {
			s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
			if(s.getName()==null)s.setName("Shape3D");
			shapeList.add(s);
			bg_shapes.addChild(s);
		}
		
		// update the Bounds
		updateBounds();
//		translateShapes();
		if(bg_shapes.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg_shapes);
		}
		
	}
	
	/**
	 * F&uuml;gt das &uuml;bergeben Shape-Objekt dem Universum hinzu.
	 * @param s Ein neues Shape
	 */
	public void addShape(Collection<? extends Shape3D> coll, String branchGroupName){
		if (debug)
			System.out.println("SimpleCanvas3D.addShape(Collectiony<Shape3D>)");
		BranchGroup bg = branchGroupMap.get(branchGroupName);
		if(bg.isLive()){
			bg.detach();
			System.out.println(branchGroupName +" detached");
		}
		for (Shape3D s : coll) {
			if(s.getName()==null)s.setName("Shape3D");
			s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
			shapeList.add(s);
			bg.addChild(s);
		}
		
		// update the Bounds
		updateBounds();
//		translateShapes();
		if(bg.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg);
		}
		
	}
	
	public void addShapeGroup(BranchGroup bg){
		if(bg_shapes.isLive()){
			bg_shapes.detach();
		}
		for (int i = 0; i < bg.numChildren(); i++) {
			if(bg.getChild(i) instanceof Shape3D) {
				shapeList.add((Shape3D)bg.getChild(i));
			}
			if (bg.getChild(i) instanceof TransformGroup) {
				shapeList.add((Shape3D)bg.getChild(i));
			}
		}
		if(bg.getName()==null)bg.setName("addedBranchGroup");
		
		updateBounds();
//		translateShapes();
		
		if(bg_shapes.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg_shapes);
		}
	}
	
	
	
	public void addBranchGroup(String name){
		if(branchGroupMap == null)
			branchGroupMap = new HashMap<String,BranchGroup>();
		BranchGroup bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.setName(name);
		branchGroupMap.put(name, bg);
		tg_translateShapes.addChild(bg);
	}
	
	
	
	/**
	 * Entfernt das &uuml;bergebene Shape3D-Objekt.
	 * @param s Das zu entfernende Shape3D-Objekt
	 */
	public void removeShape(Shape3D s){
		if (debug)
			System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
		if(bg_shapes.isLive()){
			bg_shapes.detach();
		}
		shapeList.remove(s);
		bg_shapes.removeChild(s);
		
//		 update the Bounds
		updateBounds();
//		translateShapes();
		if(bg_shapes.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg_shapes);
		}
	}
	
	/**
	 * Entfernt das &uuml;bergebene Shape3D-Objekt.
	 * @param s Das zu entfernende Shape3D-Objekt
	 */
	public void removeShape(Shape3D s, String name, boolean translate){
		if (debug)
			System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
		BranchGroup bg = branchGroupMap.get(name);
		if(bg==null)throw new IllegalArgumentException("No BranchGroup found for <"+name+">");
		if(bg.isLive()){
			bg.detach();
		}
		shapeList.remove(s);
		bg.removeChild(s);
		
//		 update the Bounds
		updateBounds();
		if(translate)translateShapes();
		if(bg.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg);
		}
	}
	
	/**
	 * Entfernt das &uuml;bergebene Shape3D-Objekt.
	 * @param s Das zu entfernende Shape3D-Objekt
	 */
	public void removeShapes(Collection<? extends Shape3D> col, boolean translate){
		if (debug)
			System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
		if(bg_shapes.isLive()){
			bg_shapes.detach();
		}for(Shape3D s : col){
			shapeList.remove(s);
			bg_shapes.removeChild(s);
		}
//		update the Bounds
		updateBounds();
		if(translate)translateShapes();
		if(bg_shapes.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg_shapes);
		}
	}
	
	/**
	 * Entfernt das &uuml;bergebene Shape3D-Objekt.
	 * @param s Das zu entfernende Shape3D-Objekt
	 */
	public void removeShapes(Collection<? extends Shape3D> col, String name, boolean translate){
		if (debug)
			System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
		BranchGroup bg = branchGroupMap.get(name);
		if(bg==null)throw new IllegalArgumentException("No BranchGroup found for <"+name+">");
		if(bg.isLive()){
			bg.detach();
		}for(Shape3D s : col){
			shapeList.remove(s);
			bg.removeChild(s);
		}
//		update the Bounds
		updateBounds();
		if(translate)translateShapes();
		if(bg.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg);
		}
	}
	
	
	/**
	 * Entfernt das &uuml;bergebene Shape3D-Objekt.
	 * @param s Das zu entfernende Shape3D-Objekt
	 */
	public void removeAllShapes(){
		if (debug)
			System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
		if(bg_shapes.isLive()){
			bg_shapes.detach();
		}
		//if(true)return;// TODO implementieren!
		for(Shape3D s: shapeList){
			bg_shapes.removeChild(s);
		}
		shapeList.clear();
//		 update the Bounds
		if(bg_shapes.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg_shapes);
		}
	}
	
	public void removeAllShapes(String name){
		//if (debug)
			System.out.println("SimpleCanvas3D.removeAllShapes(String name)");
		BranchGroup bg = branchGroupMap.get(name);
		if(bg.isLive()){
			bg.detach();
		}
		
		Enumeration en = bg.getAllChildren();
		while(en.hasMoreElements()){
			Object obj = en.nextElement();
			if(obj instanceof Shape3D){
				shapeList.remove((Shape3D)obj);
				bg.removeChild((Shape3D)obj);
			}
		}
		bg.removeAllChildren();
//		 update the Bounds
		if(bg.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg);
		}
	}
	
	
	
	/**
	 * F&uuml;gt der Szenen das &uuml;bergeben Licht hinzu.
	 * Der Einflussbereich der Lichtquelle wird automatisch
	 * so angepasst, das alle in der Szene enthaltenen Shapes beleuchtet werden.
	 * 
	 * @param l Die neue Lichtquelle
	 */
	public void addLight(Light l){
		l.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
		l.setInfluencingBounds(visibilitySphere);
		l.setName("addedLight");
		bg_lights.addChild(l);
		lightsList.add(l);
	}
	
	/**
	 * Entfernt eine Lichtquelle
	 * !! Bisher noch nicht vollst&auml;ndig implementiert !!
	 * @param l
	 */
	public void removeLight(Light l){
		if(true)return;// TODO implementieren!
		bg_lights.removeChild(l);
		lightsList.remove(l);
	}
	
	/**
	 * Aktualisiert die BoundingBox der enthaltenen Shapes sowie
	 * die Boundingsphere die f&uuml;r die Sichtbarkeit der Objekte sowie
	 * die Einflussgrenzen definiert.
	 */
	private void updateBounds(){
		boolean first = true;
		for(Shape3D s : shapeList){
			if(first){
				shapeBounds = new BoundingBox(s.getBounds());
				first=false;
			}
			shapeBounds.combine(s.getBounds());
		}
		visibilitySphere = new BoundingSphere(shapeBounds);
		//visibilitySphere.setCenter(new Point3d(0,0,0));
		visibilitySphere.setRadius(visibilitySphere.getRadius()*visibilityScaleFactor);
		
		p_max = new Point3d();
			shapeBounds.getUpper(p_max);
		p_min = new Point3d();
			shapeBounds.getLower(p_min);
		p_center = new Point3d(p_max.x+p_min.x,p_max.y+p_min.y,p_max.z+p_min.z);
			p_center.scale(0.5);
		
		if(debug){
			System.out.println("ShapeBounds update:");
			System.out.println("\tmin (x,y,z): "+ p_min.x+", "+p_min.y+", "+p_min.z);
			System.out.println("\tmax (x,y,z): "+ p_max.x+", "+p_max.y+", "+p_max.z);
			System.out.println("\tcen (x,y,z): "+ p_center.x+", "+p_center.y+", "+p_center.z);
	
			System.out.println("VisibilitySphere update:");
			Point3d pC = new Point3d(); visibilitySphere.getCenter(pC);
			System.out.println("\tcenter (x,y,z): "+ pC);
			System.out.println("\tradius: "+ visibilitySphere.getRadius());
		}

		// Update all Nodes to the new Sphere
		updateInfluences();
	}
	
	/**
	 * Aktualisiert die BoundingBox der enthaltenen Shapes sowie
	 * die Boundingsphere die f&uuml;r die Sichtbarkeit der Objekte sowie
	 * die Einflussgrenzen definiert.
	 */
	private void updateBounds(Shape3D s){
		if(shapeBounds==null)shapeBounds=new BoundingBox(s.getBounds());
		else shapeBounds.combine(s.getBounds());
		visibilitySphere = new BoundingSphere(shapeBounds);
		//visibilitySphere.setCenter(new Point3d(0,0,0));
		visibilitySphere.setRadius(visibilitySphere.getRadius()*visibilityScaleFactor);
		
		p_max = new Point3d();
			shapeBounds.getUpper(p_max);
		p_min = new Point3d();
			shapeBounds.getLower(p_min);
		p_center = new Point3d(p_max.x+p_min.x,p_max.y+p_min.y,p_max.z+p_min.z);
			p_center.scale(0.5);
		
		if(debug){
			System.out.println("ShapeBounds update:");
			System.out.println("\tmin (x,y,z): "+ p_min.x+", "+p_min.y+", "+p_min.z);
			System.out.println("\tmax (x,y,z): "+ p_max.x+", "+p_max.y+", "+p_max.z);
			System.out.println("\tcen (x,y,z): "+ p_center.x+", "+p_center.y+", "+p_center.z);
	
			System.out.println("VisibilitySphere update:");
			Point3d pC = new Point3d(); visibilitySphere.getCenter(pC);
			System.out.println("\tcenter (x,y,z): "+ pC);
			System.out.println("\tradius: "+ visibilitySphere.getRadius());
		}

		// Update all Nodes to the new Sphere
		updateInfluences();
	}
	
	/**
	 * Aktualisiert die Einflussgrenzen der verschiedenen
	 * Bestandteile des Universums (Behaviours, Lichter etc.).
	 */
	private void updateInfluences(){
		//Behaviours
		if(translate!=null)translate.setSchedulingBounds(visibilitySphere);
		if(rotate!=null)rotate.setSchedulingBounds(visibilitySphere);
		if(zoom!=null)zoom.setSchedulingBounds(visibilitySphere);
		
		view.setBackClipDistance(visibilitySphere.getRadius());
		
		BoundingSphere peter = new BoundingSphere(visibilitySphere);
		peter.setRadius(peter.getRadius()*2);
		//Lights
		for(Light l : lightsList){
			l.setInfluencingBounds(peter);
		}
		background.setApplicationBounds(visibilitySphere);
		
	}
	
	/**
	 * Aktualisiert die Transformation der TransformGroup tg_shapes.
	 * Es wird eine Translation der enthaltenen Shapes durchgef&uuml;hrt,
	 * so dass das Zentrum der BoundingBox der Shapes im Koordinatenursprung liegt.
	 */
	public void translateShapes(){
		//if(0==0)return;
		Transform3D translation = new Transform3D();
		translation.setTranslation(new Vector3d(-p_center.x, -p_center.y, -p_center.z));
		tg_translateShapes.setTransform(translation);
	}
	
	/**
	 * Verschiebt die ViewingPlatform auf (0, 0, dz),
	 * wobei dz so gew&auml;hlt wird, das die BoundingBox der Shapes
	 * vollst&auml;ndig sichtbar ist. 
	 */
	public void setDefaultViewPosition(){
		if(p_max==null){
			System.out.println( "ERROR in setDefaultViewPosition():\n\t" +
								"default View-Position could not be set.\n\t" +
							   	"Add Shapes first." );
			return;
		}
		BoundingSphere sphere = new BoundingSphere(shapeBounds);
		Transform3D viewTrafo = new Transform3D();
		double dx = sphere.getRadius()*1.5;//p_max.x - p_min.x;
		//TODO auch dy beruecksichtigen!!
//		double dy = p_max.y - p_min.y;
		
//		if(dx<dy){
//			dx=dy;
//		}
		
		double dz = (0.5 * dx)/Math.sin(0.5*view.getFieldOfView());
		viewTrafo.setTranslation(new Vector3d(0.,0., dz));
		tg_viewTrafo.setTransform(viewTrafo);
	}
	
	/**
	 * Setzt die Farbe des Hintergrunds.
	 * @param color Die neue Hintergrundfarbe
	 */
	public void setBackgroundColor(Color color){
		background.setColor(new Color3f(color));
		background.setApplicationBounds(new BoundingSphere(new Point3d(),Double.MAX_VALUE));
	}
	
	/**
	 * Initialisert die Bounds-Objekte
	 */
	private void initBounds(){
//		shapeBounds = new BoundingBox();
//		
		visibilitySphere = new BoundingSphere();
	}
	
	/**
	 * Initialisiert die Basis-Objekte des Szene-Graphen.
	 * (Universe, Locale, root-BranchGroup)
	 */
	private void initUniverse(){
		VirtualUniverse myUniverse = new VirtualUniverse();
		HiResCoord origin = new HiResCoord();
		myLocale = new Locale(myUniverse, origin);
		root = new BranchGroup();
		root.setName("root");
		background = new Background(bgColor);
		background.setName("background");
		background.setCapability(Background.ALLOW_APPLICATION_BOUNDS_WRITE);
		
		
		root.addChild(background);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	}
	
	/**
	 * Initialisiert den View-Ast des Szene-Graphen.
	 * (BranchGroup bg_view)
	 */
	private void initViewGraph(){
		BranchGroup bg_view = new BranchGroup();
		
		view = new View();
		//view.setLocalEyeLightingEnable(true);
		
		PhysicalBody physbody = new PhysicalBody();
		PhysicalEnvironment physen = new PhysicalEnvironment();
		
		view.addCanvas3D(this);
		view.setBackClipDistance(visibilitySphere.getRadius());
		//view.setSceneAntialiasingEnable(true);
		view.setPhysicalBody(physbody);
		view.setPhysicalEnvironment(physen);
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		
		ViewPlatform viewPlat = new ViewPlatform();
		view.attachViewPlatform(viewPlat);
		
		tg_viewTrafo = new TransformGroup();
		tg_viewTrafo.setName("tg_viewTrafo");
		tg_viewTrafo.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg_viewTrafo.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		tg_viewTrafo.addChild(viewPlat);
		bg_view.addChild(tg_viewTrafo);
		root.addChild(bg_view);
	}
	
	/**
	 * Initialisiert den Komponenten-Ast des Szene-Graphen.
	 * (BranchGroup bg_shapes)
	 */
	private void initComponetGraph(){
		bg_shapes = new BranchGroup();
		bg_shapes.setName("bg_shapes");
		bg_shapes.setCapability(BranchGroup.ALLOW_DETACH);
		bg_shapes.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		tg_translateShapes = new TransformGroup();
		tg_translateShapes.setName("tg_translateShapes");
		tg_translateShapes.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg_translateShapes.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg_translateShapes.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		tg_translateShapes.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		tg_translateShapes.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		
		//tg_translateShapes.setCapability(TransformGroup.ALL)
		//tg_translateShapes.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		
		
		
		tg_interaction = new TransformGroup();
		tg_interaction.setName("tg_interaction");
		tg_interaction.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg_interaction.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		tg_translateShapes.addChild(bg_shapes);
		tg_interaction.addChild(tg_translateShapes);
		root.addChild(tg_interaction);
		
		bg_environment = new BranchGroup();
		bg_environment.setName("bg_environment");
		tg_interaction.addChild(bg_environment);
		
	}
	
	/**
	 * Erm&ouml;glicht Mausinteraktionen.
	 * 
	 * @param b An oder Aus
	 */
	public void setMouseInteraction(boolean b){
		if(b){
			
			if(rotate==null){
				rotate = new MouseRotate(tg_interaction);
				rotate.setName("MouseRotate");
				rotate.setFactor(0.05);
				rotate.setSchedulingBounds(visibilitySphere);
			} tg_interaction.addChild(rotate);
			
			
			
			if(translate==null){
				translate = new MouseTranslate(tg_interaction);
				translate.setName("MouseTranslate");
				translate.setFactor(2); // 2
				tg_interaction.addChild(translate);
			} translate.setSchedulingBounds(visibilitySphere);
			
			if(zoom==null){
				zoom = new MouseZoom(tg_interaction);
				zoom.setName("MouseZoom");
				zoom.setFactor(2); // 2
				tg_viewTrafo.addChild(zoom);
				//bg_lights.addChild(zoom);
			} zoom.setSchedulingBounds(visibilitySphere);
		} else {
			if(rotate!=null)   tg_interaction.removeChild(rotate);
			if(translate!=null)tg_interaction.removeChild(translate);
			if(zoom!=null)     tg_viewTrafo.removeChild(zoom);
			
		}
		
	}
	
	
	
	/**
	 * Initialisiert den Licht-Ast des Szene-Graphen.
	 * (BranchGroup bg_lights)
	 */
	private void initLightGraph(){
		bg_lights = new TransformGroup();
		bg_lights.setName("bg_lights");
		bg_lights.setCapability(BranchGroup.ALLOW_DETACH);
		root.addChild(bg_lights);
	}
	
	public String getXMLRepresentationOfSceneGaph(){
		StringBuffer buff = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		Node n = root;
		buildTree(buff, n,0);
		return buff.toString();
	}
	
	private void buildTree(StringBuffer buff, Node n, int level){
		if (n instanceof Group) {
			for(int i = 0; i < level; i++)buff.append("\t");
			buff.append("<"+n.getName()+">\n");
			Group group = (Group) n;
			Enumeration en = group.getAllChildren();
			while(en.hasMoreElements())buildTree(buff,(Node)en.nextElement(),level+1);
			for(int i = 0; i < level; i++)buff.append("\t");
			buff.append("</"+n.getName()+">\n");
		}else if (n instanceof Leaf) {
			Leaf leaf = (Leaf) n;
			for(int i = 0; i < level; i++)buff.append("\t");
			buff.append("<"+leaf.getName()+"/>\n");
		}
		
		
	}
	/**
	 * Hinzuf&uuml;gen einer Standard-Beleuchtung 
	 */
	public void addDefaultLights(){
		
		AmbientLight licht = new AmbientLight();
		licht.setName("defLight_ambient");
		licht.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
		
		licht.setInfluencingBounds(visibilitySphere);
		bg_lights.addChild(licht);
		lightsList.add(licht);
				
		DirectionalLight dLicht = new DirectionalLight(new Color3f(Color.white), new Vector3f(-10.f, -50.f, -10.f));
		dLicht.setInfluencingBounds(visibilitySphere);
		dLicht.setName("defLight_direct");
		bg_lights.addChild(dLicht);
		
		float r = (float)visibilitySphere.getRadius();
		PointLight pLight1 = new PointLight(new Color3f(Color.WHITE),new Point3f(-r,r,r),new Point3f(1.2f,0f,0f));
		pLight1.setName("defLight_pLight1");
		pLight1.setInfluencingBounds(visibilitySphere);
		bg_lights.addChild(pLight1);
		lightsList.add(pLight1);
		pLight1.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
		
		PointLight pLight2 = new PointLight(new Color3f(Color.WHITE),new Point3f(r,r,r),new Point3f(1.2f,0f,0f));
		pLight2.setName("defLight_pLight2");
		pLight2.setInfluencingBounds(visibilitySphere);
		bg_lights.addChild(pLight2);
		lightsList.add(pLight2);
		pLight2.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
		
	}
	
	/**
	 * F&uuml;gt der Darstellung ein Achsenkreuz hinzu.
	 * @param size Die Gr&uuml;&szlig;e des Achsenkreuzes
	 */
	public void showAxis(float size){
		Axis3D axis = new Axis3D(new float[]{0.4f,0.4f,0.4f},size);
		axis.setName("axis");
		bg_environment.addChild(axis);
	}
	
	/**
	 * F&uuml;gt der Darstellung ein Achsenkreuz hinzu.
	 * @param size Die Gr&uuml;&szlig;e des Achsenkreuzes
	 */
	public void showAxis_v2(float size){
		AxisSystem3D axis = new AxisSystem3D(size,1f);
		axis.setName("axis2");
		bg_environment.addChild(axis);
	}
	
	
	/**
	 * Kompiliert den Szene-Graphen und f&uuml;gt ihn dem Universum hinzu.
	 * Diese Methode muss aufgerufen werden, bevor das Canvas angezeigt werden soll.
	 */
	public void compile(){
		//root.compile();
		myLocale.addBranchGraph(root);
	}
	
	//------------------------------------------------------------------------------//
	/**
	 * Speichert die Transformationsmatrix in das &uuml;bergebene Transform3D-Objekt 
	 * @param trafo Tranform3D-Objekt in das die aktuelle Tranformation gespeichert werden soll.
	 * @return
	 */
	//------------------------------------------------------------------------------//
	public Transform3D getActualViewTrafo(Transform3D trafo){
		tg_interaction.getTransform(trafo);
		return trafo;
	}
	
	//------------------------------------------------------------------------------//
	/**
	 * Transformiert die Darstellung enstprechend dem &uuml;bergebenen Transform3D-Objekt
	 * @param trafo
	 */
	//------------------------------------------------------------------------------//
	public void setActualViewTrafo(Transform3D trafo){
		tg_interaction.setTransform(trafo);
	}
	
	public void setStandardView(double x, double y, double z, double w){
		setDefaultViewPosition();
		Transform3D trafo = new Transform3D();
		tg_interaction.getTransform(trafo);
		trafo.setRotation(new Quat4d(x,y,z,w));
		tg_interaction.setTransform(trafo);
	}
	
	public void rotatateToStandardView(int i){
		Transform3D trafo = new Transform3D();
		tg_interaction.getTransform(trafo);
		Matrix3f rot = new Matrix3f();
		switch (i) {
			case VIEW_TOP:		rot.m00= 1; rot.m11= 1; rot.m22= 1; trafo.setRotation(rot); break;
			case VIEW_BOTTOM:	rot.m00= 1; rot.m11=-1; rot.m22=-1; trafo.setRotation(rot); break;
			case VIEW_LEFT:		rot.m01=-1; rot.m12= 1; rot.m20=-1; trafo.setRotation(rot); break;
			case VIEW_RIGHT:	rot.m01= 1; rot.m12= 1; rot.m20= 1; trafo.setRotation(rot); break;
			case VIEW_FRONT:	rot.m00= 1; rot.m12= 1; rot.m21=-1; trafo.setRotation(rot); break;
			case VIEW_BACK:		rot.m00=-1; rot.m12= 1; rot.m21= 1; trafo.setRotation(rot); break;
			default: break;
		}
		tg_interaction.setTransform(trafo);
	}
	
	public void zoom(double z){
		Transform3D trafo = new Transform3D();
		tg_interaction.getTransform(trafo);
		
		
		
		
	}
	
	
	
	//#######################################//
	//##              PICKING              ##//
	//#######################################//
	
	public PickCanvas getPickCanvas(){
		PickCanvas pc = new PickCanvas(this,bg_shapes);
		return pc;
	}
	
	public PickCanvas getPickCanvas(String branchGroupName){
		BranchGroup bg = branchGroupMap.get(branchGroupName);
		PickCanvas pc = new PickCanvas(this,bg);
		return pc;
	}
	
	public void enablePicking(){
		if(bg_shapes.isLive()){
			bg_shapes.detach();
		}
		enablePicking(bg_shapes);
		if(bg_shapes.getParent()==null){
			//bg_shapes.compile();
			root.addChild(bg_shapes);
		}
	}
	
	public void enablePicking(String branchGroupName){
		BranchGroup bg = branchGroupMap.get(branchGroupName);
		if(bg.isLive()){
			bg.detach();
		}
		enablePicking(bg);
		if(bg.getParent()==null){
			//bg_shapes.compile();
			tg_translateShapes.addChild(bg);
		}
	}
	
    @SuppressWarnings("static-access")
	private void enablePicking(Node node) {
		node.setCapability(Node.ALLOW_PICKABLE_WRITE);
	    node.setPickable(true);

	    node.setCapability(Node.ENABLE_PICK_REPORTING);

	    try {

	       Group group = (Group) node;

	       for (Enumeration e = group.getAllChildren(); e.hasMoreElements();) {

	          enablePicking((Node)e.nextElement());

	       }

	    }

	    catch(ClassCastException e) {

	        // if not a group node, there are no children so ignore exception

	    }

	    try {

	          Shape3D shape = (Shape3D) node;

	          PickTool.setCapabilities(node, PickTool.INTERSECT_FULL);

	          for (Enumeration e = shape.getAllGeometries(); e.hasMoreElements();) {

	             Geometry g = (Geometry)e.nextElement();

	             g.setCapability(g.ALLOW_INTERSECT);

	          }

	       }

	    catch(ClassCastException e) {

	       // not a Shape3D node ignore exception

	    }

	}

	public int[] pickClosest(PickShape pickShape, String branchGroupName) {
		BranchGroup bg = branchGroupMap.get(branchGroupName);
		//enablePicking(bg);
		System.out.println("BranchGroup="+bg);
		bg.pickAllSorted(pickShape);
		System.out.println("bg.pickAllSorted="+bg.pickAllSorted(pickShape));
		PickInfo pickInfo = bg.pickClosest(PickInfo.PICK_GEOMETRY, PickInfo.ALL_GEOM_INFO | PickInfo.LOCAL_TO_VWORLD, pickShape);
		if(pickInfo==null){System.out.println("pickInfo==null");return null;}
		if(pickInfo.getIntersectionInfos()==null){System.out.println("pickInfo.getIntersectionInfos()==null");return null;}
		if(pickInfo.getIntersectionInfos().length==0){System.out.println("pickInfo.getIntersectionInfos().length==0");return null;}
		if(pickInfo.getIntersectionInfos()[0].getVertexIndices()==null){System.out.println("pickInfo.getIntersectionInfos()[0].getVertexIndices()==null");return null;}
		
		int[] vertexIndices = pickInfo.getIntersectionInfos()[0].getVertexIndices();
		return vertexIndices;
	}
	
	public IntersectionInfo getIntersectionInfoOfClosestTriangle(PickShape pickShape, String branchGroupName) {
		BranchGroup bg = branchGroupMap.get(branchGroupName);
		//enablePicking(bg);
		System.out.println("BranchGroup="+bg);
		bg.pickAllSorted(pickShape);
		System.out.println("bg.pickAllSorted="+bg.pickAllSorted(pickShape));
		PickInfo pickInfo = bg.pickClosest(PickInfo.PICK_GEOMETRY, PickInfo.ALL_GEOM_INFO | PickInfo.LOCAL_TO_VWORLD, pickShape);
		if(pickInfo==null){System.out.println("pickInfo==null");return null;}
		if(pickInfo.getIntersectionInfos()==null){System.out.println("pickInfo.getIntersectionInfos()==null");return null;}
		if(pickInfo.getIntersectionInfos().length==0){System.out.println("pickInfo.getIntersectionInfos().length==0");return null;}
		if(pickInfo.getIntersectionInfos()[0].getVertexIndices()==null){System.out.println("pickInfo.getIntersectionInfos()[0].getVertexIndices()==null");return null;}
		
		return pickInfo.getIntersectionInfos()[0];
	}
	
	/** ADD A FUCKING EXAMPLE NEXT TIME !!!! */
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024,786);

		SimpleCanvas3D canvas = new SimpleCanvas3D();
		frame.add(canvas);
		
		canvas.setMouseInteraction(true);
		
		// Licht
		AmbientLight ambLight = new AmbientLight();
		canvas.addLight(ambLight);
		DirectionalLight d1 = new DirectionalLight();
		canvas.addLight(d1);
		DirectionalLight d2 = new DirectionalLight(new Color3f(Color.BLACK), new Vector3f(3,10,4));
		canvas.addLight(d2);
		DirectionalLight d3 = new DirectionalLight(new Color3f(Color.BLACK), new Vector3f(-0.1f,0.1f,-1f));
		canvas.addLight(d3);
		
		// Achsen
		canvas.showAxis(1f);
	
//		Shape3D meshShape = ...
//		canvas.addShape(meshShape, false);
//		
//		Shape3D curvShape = ...
//		canvas.addShape(curvShape, false);
		
		canvas.compile();					 // DON'T FORGET !
		frame.setVisible(true);
	}
	
	
	
	
	

}
