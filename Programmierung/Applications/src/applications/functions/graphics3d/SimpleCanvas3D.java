package applications.functions.graphics3d;

 
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
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
import javax.media.j3d.PickInfo;
import javax.media.j3d.PickShape;
import javax.media.j3d.PointLight;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
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

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.Viewer;
import com.sun.j3d.utils.universe.ViewingPlatform;
 
import java.awt.event.KeyListener;

import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Link;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TransparencyAttributes;


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
 *
 * 
 * 
 * <pre>
 * SimpleCanvas3D c = new SimpleCanvas3D(); // erzeugen des Canvas
 * c.setBackgroundColor(Color.DARK_GRAY);   // setzen der Hintergrundfarbe
 * c.addShape(shape);                       // hinzuf&uuml;gen eines Shapes
 * c.addDefaultLights();                    // Standardbeleuchtung hinzuf&uuml;gen
 * c.setDefaultViewPosition();              // Betrachtungsposition verschieben
 * c.setMouseInteraction(true);             // aktivieren der Mausinteraktion
 * c.compile();<br>
 *</pre>
 * </p>
 * 
 *
 *
 * Maussteuerung ver&auml;ndert  => OrbitBehavior dreht jetzt die ViewPosition<br>
 * View durch Viewer ersetzt... ViewingPlatform etc...<br>
 * eine weitere Methode setViewPosition eingefuegt   <br>
 * <br>
 *
 * Die Maussteuerung ist wie folgt belegt:<br>
 * links          - drehen<br>
 * Mausrad drehen - zoomen<br>
 *  rechts        - verschieben<br>
 * <br>
 *
 * Die Tastatursteuerung ist wie folgt belegt:<br>
 * k              - AchsenKreuz ein oder Ausschalten gr&ouml;sse 10.f<br>
 * d              - DefaultViewPosition()<br>
 * r              - RotationsCenter neu belegen &uuml;ber Picking
 *
 * 
 * @author Jan Stilhammer, David Sch&ouml;ning
 */
public class SimpleCanvas3D  extends Canvas3D{

    private BoundingSphere visibilitySphere; // Fuer Sichtbarkeit und Einflussgrenzen
    private BoundingBox shapeBounds; // Zur Berechnung der translationen
    private Point3d p_max;    // Maximalkoordinaten der BoundingBox
    private Point3d p_min;    // Minimalkoordinaten der BoundingBox
    private Point3d p_center; // Zentralkoordinaten der BoundingBox
    public  ArrayList<Shape3D> shapeList; // Liste aller Shapes
    private ArrayList<Light> lightsList;  // Liste aller Lichter
    private Locale myLocale;
    protected BranchGroup root;
//    private TransformGroup tg_lights;
    private BranchGroup bg_environment;
    private TransformGroup tg_viewTrafo;
    private static final int visibilityScaleFactor = Integer.MAX_VALUE; // scales the visibilitySphere
    private Viewer view;
    private OrbitBehavior orbit;
    private boolean transl = false;
    private double transfactor;
    private static final Color3f bgColor = new Color3f(0, 0, 0);
    private Background background;
    private static final boolean debug = false;
    protected BranchGroup bg_shapes;
    private HashMap<String, BranchGroup> branchGroupMap;
    public static final int VIEW_FRONT = 0;
    public static final int VIEW_BACK = 1;
    public static final int VIEW_TOP = 2;
    public static final int VIEW_BOTTOM = 3;
    public static final int VIEW_LEFT = 4;
    public static final int VIEW_RIGHT = 5;
    private BranchGroup bg_lights;

    private Axis3D axis = null;
    private AxisSystem3D axis3d = null;
    private MyKeyListener myKey;

//------------------------------------------------------------------------------//
    /**
     * Erzeugt ein neues SimpleCanvas3D-Objekt.
     * Das Objekt beeinhaltet zun&auml;chst keinerlei
     * Shape-Objekte oder Lichter.
     */
//------------------------------------------------------------------------------//
    public SimpleCanvas3D() {
        super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(
                new GraphicsConfigTemplate3D()));
                System.out.println("konstruktor");
        this.shapeList = new ArrayList<Shape3D>(1);
        this.lightsList = new ArrayList<Light>(5);
        branchGroupMap = new HashMap<String, BranchGroup>();

        initBounds();
        initUniverse();
        initViewGraph();
        initLightGraph();
        initComponetGraph();
        
        
    }
//------------------------------------------------------------------------------//
    /**
     * Gibt die shapeList in der alle Shape3D Objekte gespeichert sind zur&uuml;ck
     * @return
     */
//------------------------------------------------------------------------------//
     protected ArrayList<Shape3D> getShapeList() {
        return shapeList;
    }

//------------------------------------------------------------------------------//
    /**
     * Die Methode stellt rekursiv sicher, dass alle Shapes die in der Group, <br>
     * welche man in der Methode addGroup &uuml;bergeben hat, in der shapeList <br>
     * gespeichert werden.
     * @param n von der Methode addGroup(Shape3D) &uuml;bergebene Group
     */
//------------------------------------------------------------------------------//
     private void addLeaf(Group n) {
        Object no = null;
        for (Enumeration e = n.getAllChildren(); e.hasMoreElements();) {
            no = e.nextElement();
            if (no instanceof Shape3D) {
//                System.out.println("Shape3D");
                shapeList.add((Shape3D) no);
                updateBounds((Shape3D) no);
            }else if(no instanceof Group){
                addLeaf((Group)no);
            }
        }
    }

//------------------------------------------------------------------------------//
    /**
     *  Die Methode stellt rekursiv sicher, dass wenn man ein Shape aus einer Group <br>
     *  l&ouml;scht, alle leeren Groups auch gel&ouml;scht werden.
     *
     * @param n von der Methode remove(Shape3D) &uuml;bergebene Group
     */
//------------------------------------------------------------------------------//
    private void removeLeaf(Group n) {
        if (n.numChildren() == 0) {
            if (n.getParent() instanceof Group) {
                Group g = (Group) n.getParent();
               for (Enumeration<Node> e = g.getAllChildren(); e.hasMoreElements();){
                   if(e instanceof Shape3D){
                       Shape3D s = (Shape3D)e;
                if(shapeList.contains(s))
                    shapeList.remove(s);
                   }
               }
                g.removeChild(n);
                removeLeaf(g);
            }
        }
    }


//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene Group-Objekt(TransformGroup o.ae.) dem Universum hinzu.
     * @param g eine neues Group Objekt
     */
//------------------------------------------------------------------------------//
     public void addGroup(Group g) {
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
//            System.out.println("bg_shapes detached");
        }
        bg_shapes.addChild(g);
        addLeaf(g);

        if (bg_shapes.getParent() == null) {
            root.addChild(bg_shapes);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene Group-Objekt(TransformGroup o.ae.) der BranchGroup
     * hinzu
     * @param g ein neues Group-Objekt
     * @param branchGroupName name der BranchGroup
     */
//------------------------------------------------------------------------------//
     public void addGroup(Group g,String branchGroupName) {
         BranchGroup bg = branchGroupMap.get(branchGroupName);
        if (bg.isLive()) {
            bg.detach();
//            System.out.println(branchGroupName + " detached");
        }
        bg.addChild(g);
        // update the Bounds
        addLeaf(g);
//        updateBounds();
        
        if (bg.getParent() == null) {
            root.addChild(bg);
        }
    }

     
     public void removeGroup(Group g){
         removeGroup(g, bg_shapes.getName());
     }
//------------------------------------------------------------------------------//
    /**
    * Extra-Methode f&uuml;r JWalker3D
    * @param g
    * @param branchGroupName
    */
//------------------------------------------------------------------------------//
     public void removeGroup(Group g, String branchGroupName){
        BranchGroup bg = branchGroupMap.get(branchGroupName);
        if (bg == null) {
            throw new IllegalArgumentException("No BranchGroup found for <" + branchGroupName + ">");
        }
        if (bg.isLive()) {
            bg.detach();
        }
        if (g.getParent() == bg) {
        for (Enumeration<Node> e = g.getAllChildren(); e.hasMoreElements();){
            Node child = e.nextElement();
                   if(child.getClass().equals(Shape3D.class)){
                       Shape3D s = (Shape3D)child;
                if(shapeList.contains(s))
                    shapeList.remove(s);
                       g.removeChild(s);
                   }
               }
            bg.removeChild(g);
//		 update the Bounds
            updateBounds();
        } else if (g.getParent() instanceof Group) {
            Group g1 = (Group) g.getParent();
            g1.removeChild(g);
            removeLeaf(g1);
        }
//		 update the Bounds
        updateBounds();
        if (bg.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg);
        }
     }

//------------------------------------------------------------------------------//
    /**
     *
     * @param s
     * @param b
     */
//------------------------------------------------------------------------------//
     public void removeShape(Shape3D s,boolean b) {
        if (debug) {
//            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
        }
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
        }
        shapeList.remove(s);
        if (s.getParent() == bg_shapes) {
            bg_shapes.removeChild(s);
//		 update the Bounds
            updateBounds();
        } else if (s.getParent() instanceof Group) {
            Group g = (Group) s.getParent();
            g.removeChild(s);
            removeLeaf(g);
        }

//		translateShapes();
        if (bg_shapes.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg_shapes);
        }
    }
     
                       
//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene Shape-Objekt dem Universum hinzu.
     * @param s Ein neues Shape3D
     */
//------------------------------------------------------------------------------//
public void addShape(Shape3D s) {
        System.out.println("Translate ist ausgeschaltet");
        shapeList.add(s);
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
//            System.out.println("bg_shapes detached");
        }
        s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
        if (s.getName() == null) {
            s.setName("Shape3D");
        }
        bg_shapes.addChild(s);
        // update the Bounds
        updateBounds(s);
       if (bg_shapes.getParent() == null) {
            root.addChild(bg_shapes);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene Shape-Objekt einer BranchGoup hinzu.
     * @param s ein neues Shape3D
     * @param branchGroupName
     */
//------------------------------------------------------------------------------//
public void addShape(Shape3D s, String branchGroupName) {
        shapeList.add(s);
        BranchGroup bg = branchGroupMap.get(branchGroupName);
        if (bg.isLive()) {
            bg.detach();
            System.out.println(branchGroupName + " detached");
        }
        s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
        if (s.getName() == null) {
            s.setName("Shape3D");
        }
        bg.addChild(s);
        // update the Bounds
        updateBounds(s);
        if (bg.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg);
        }
}


@Deprecated
//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene Shape-Objekt dem Universum hinzu.
     * @param s Ein neues Shape
     */
//------------------------------------------------------------------------------//
    public void addShape(Shape3D s, boolean translate) {
//        System.out.println("Translate ist ausgeschaltet");
        addShape(s);

//        nodeList.add(s);
//        if (bg_shapes.isLive()) {
//            bg_shapes.detach();
//            System.out.println("bg_shapes detached");
//        }
//        s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
//        if (s.getName() == null) {
//            s.setName("Shape3D");
//        }
//        bg_shapes.addChild(s);
//
//        // update the Bounds
//        updateBounds(s);
//        if (false) {
//            translateShapes();
//            transl = translate;
//        }
//        if (bg_shapes.getParent() == null) {
////			bg_shapes.compile();
//            tg_translateShapes.addChild(bg_shapes);
//        }
    }

@Deprecated
//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene Shape-Objekt der BranchGroup hinzu.
     * @param s Ein neues Shape
     * @param branchGroupName
     * @param translate
     */
//------------------------------------------------------------------------------//
    public void addShape(Shape3D s, String branchGroupName, boolean translate) {
//    System.out.println("Translate ist ausgeschaltet");
    addShape(s, branchGroupName);
//        nodeList.add(s);
//        BranchGroup bg = branchGroupMap.get(branchGroupName);
//        if (bg.isLive()) {
//            bg.detach();
//            System.out.println(branchGroupName + " detached");
//        }
//        s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
//        if (s.getName() == null) {
//            s.setName("Shape3D");
//        }
//        bg.addChild(s);
//
//        // update the Bounds
//        updateBounds(s);
//        if (translate) {
//            translateShapes();
//            transl = translate;
//        }
//        if (bg.getParent() == null) {
//            //bg_shapes.compile();
//            tg_translateShapes.addChild(bg);
//        }

    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt die &uuml;bergebenen Shape-Objekte aus der Collection dem Universum hinzu.
     * @param coll Ein neues Shape
     */
//------------------------------------------------------------------------------//
    public void addShape(Collection<? extends Shape3D> coll) {
        if (debug) {
//            System.out.println("SimpleCanvas3D.addShape(Collectiony<Shape3D>)");
        }
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
        }
        for (Shape3D s : coll) {
            s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
            if (s.getName() == null) {
                s.setName("Shape3D");
            }
            shapeList.add(s);
            bg_shapes.addChild(s);
        }

        // update the Bounds
        updateBounds();
        if (bg_shapes.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg_shapes);
        }

    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene Shape-Objekt dem Universum hinzu.
     * @param coll Eine Collection aus Shape3D Objekten
     * @param branchGroupName
     */
//------------------------------------------------------------------------------//
    public void addShape(Collection<? extends Shape3D> coll, String branchGroupName) {
        if (debug) {
            System.out.println("SimpleCanvas3D.addShape(Collectiony<Shape3D>)");
        }
        BranchGroup bg = branchGroupMap.get(branchGroupName);
        if (bg.isLive()) {
            bg.detach();
//            System.out.println(branchGroupName + " detached");
        }
        for (Shape3D s : coll) {
            if (s.getName() == null) {
                s.setName("Shape3D");
            }
            s.setCapability(Shape3D.ALLOW_BOUNDS_READ);
            shapeList.add(s);
            bg.addChild(s);
        }

        // update the Bounds
        updateBounds();
        if (bg.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg);
        }

    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene BranchGroup-Objekt dem Universum hinzu.
     * @param bg BranchGroup
     */
//------------------------------------------------------------------------------//
    public void addShapeGroup(BranchGroup bg) {
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
        }
        for (int i = 0; i < bg.numChildren(); i++) {
            if (bg.getChild(i) instanceof Shape3D) {
                shapeList.add((Shape3D) bg.getChild(i));
            }
//            if (bg.getChild(i) instanceof TransformGroup) {
//                shapeList.add((Shape3D) bg.getChild(i));
//            }
        }
        if (bg.getName() == null) {
            bg.setName("addedBranchGroup");
        }

        updateBounds();

        if (bg_shapes.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg_shapes);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt das &uuml;bergebene BranchGroup-Objekt dem Universum hinzu.
     * @param name Die BranchGroup die hunzugef&uuml;gt werden soll
     */
//------------------------------------------------------------------------------//	
    public void addBranchGroup(String name) {
        if (branchGroupMap == null) {
            branchGroupMap = new HashMap<String, BranchGroup>();
        }
        BranchGroup bg = new BranchGroup();
        bg.setCapability(BranchGroup.ALLOW_DETACH);
        bg.setName(name);
        branchGroupMap.put(name, bg);
        bg_shapes.addChild(bg);
    }

//------------------------------------------------------------------------------//
    /**
     * Entfernt das &uuml;bergebene Shape3D-Objekt.
     * @param s Das zu entfernende Shape3D-Objekt
     */
//------------------------------------------------------------------------------//
    public void removeShape(Shape3D s) {
        if (debug) {
            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
        }
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
        }
        shapeList.remove(s);
        if (s.getParent() == bg_shapes) {
            bg_shapes.removeChild(s);
//		 update the Bounds
            updateBounds();
        } else if (s.getParent() instanceof Group) {
            Group g = (Group) s.getParent();
            g.removeChild(s);
            removeLeaf(g);
        }

//		translateShapes();
        if (bg_shapes.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg_shapes);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * Entfernt das Shape-Objekt aus der BranchGroup
     * @param s Das zu entfernende Shape3D-Objekt
     * @param name Die BranchGroup aus der es entfernt werden soll
     */
//------------------------------------------------------------------------------//
    public void removeShape(Shape3D s, String name) {
        if (debug) {
            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
        }
        BranchGroup bg = branchGroupMap.get(name);
        if (bg == null) {
            throw new IllegalArgumentException("No BranchGroup found for <" + name + ">");
        }
        if (bg.isLive()) {
            bg.detach();
        }
        shapeList.remove(s);
        if (s.getParent() == bg) {
            bg.removeChild(s);
//		 update the Bounds
            updateBounds();
        } else if (s.getParent() instanceof Group) {
            Group g = (Group) s.getParent();
            g.removeChild(s);
            removeLeaf(g);
        }
//		 update the Bounds
        updateBounds();
        if (bg.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg);
        }
    }

//------------------------------------------------------------------------------//
    /**
     *  Entfernt alle Shapes die in der Collection abgelegt sind aus dem Universum
     * @param col
     */
//------------------------------------------------------------------------------//
    public void removeShapes(Collection<? extends Shape3D> col) {
        if (debug) {
            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
        }
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
        }
        for (Shape3D s : col) {
            shapeList.remove(s);
            if (s.getParent() == bg_shapes) {
                bg_shapes.removeChild(s);
//		 update the Bounds
                updateBounds();
            } else if (s.getParent() instanceof Group) {
                Group g = (Group) s.getParent();
                g.removeChild(s);
                removeLeaf(g);
            }
        }
//		update the Bounds
        updateBounds();

        if (bg_shapes.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg_shapes);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * Entfernt alle Shapes die in der Collection abgelegt sind aus der genannten BranchGroup
     * @param col
     * @param name Die BranchGroup aus der es entfernt werden soll
     */
//------------------------------------------------------------------------------//
    public void removeShapes(Collection<? extends Shape3D> col, String name) {
        if (debug) {
            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
        }
        BranchGroup bg = branchGroupMap.get(name);
        if (bg == null) {
            throw new IllegalArgumentException("No BranchGroup found for <" + name + ">");
        }
        if (bg.isLive()) {
            bg.detach();
        }
        for (Shape3D s : col) {
            shapeList.remove(s);
            if (s.getParent() == bg) {
                bg.removeChild(s);
//		 update the Bounds
                updateBounds();
            } else if (s.getParent() instanceof Group) {
                Group g = (Group) s.getParent();
                g.removeChild(s);
                removeLeaf(g);
            }
        }
//		update the Bounds
        updateBounds();
        if (bg.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg);
        }
    }

    @Deprecated
//------------------------------------------------------------------------------//
    /**
     *  Entfernt das &uuml;bergebene Shape3D-Objekt aus der BranchGroup name
     * @param s Das zu entfernende Shape3D-Objekt
     * @param name Die BranchGroup aus der es entfernt werden soll
     * @param translate
     */
//------------------------------------------------------------------------------//
    public void removeShape(Shape3D s, String name, boolean translate) {
//        System.out.println("Translate ist ausgeschaltet");
        removeShape(s, name);
//        if (debug) {
//            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
//        }
//        BranchGroup bg = branchGroupMap.get(name);
//        if (bg == null) {
//            throw new IllegalArgumentException("No BranchGroup found for <" + name + ">");
//        }
//        if (bg.isLive()) {
//            bg.detach();
//        }
//        shapeList.remove(s);
//        bg.removeChild(s);
//
////		 update the Bounds
//        updateBounds();
//        if (translate) {
//            translateShapes();
//            transl = translate;
//        }
//        if (bg.getParent() == null) {
//            //bg_shapes.compile();
//            tg_translateShapes.addChild(bg);
//        }
    }

    @Deprecated
//------------------------------------------------------------------------------//
    /**
     * Entfernt das &uuml;bergebene Shape3D-Objekt.
     * @param s Das zu entfernende Shape3D-Objekt
     * @param translate
     */
//------------------------------------------------------------------------------//
    public void removeShapes(Collection<? extends Shape3D> col, boolean translate) {
//        System.out.println("Translate ist ausgeschaltet");
        removeShapes(col);
//        if (debug) {
//            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
//        }
//        if (bg_shapes.isLive()) {
//            bg_shapes.detach();
//        }
//        for (Shape3D s : col) {
//            shapeList.remove(s);
//            bg_shapes.removeChild(s);
//        }
////		update the Bounds
//        updateBounds();
//        if (translate) {
//            translateShapes();
//            transl = translate;
//        }
//        if (bg_shapes.getParent() == null) {
//            //bg_shapes.compile();
//            tg_translateShapes.addChild(bg_shapes);
//        }
    }

    @Deprecated
//------------------------------------------------------------------------------//
    /**
     * Entfernt die &uuml;bergebenen Shape3D-Objekte aus der BranchGroup.
     * @param s Das zu entfernende Shape3D-Objekt
     * @param name Die BranchGroup aus der es entfernt werden soll
     * @param translate
     */
//------------------------------------------------------------------------------//
    public void removeShapes(Collection<? extends Shape3D> col, String name, boolean translate) {
//        System.out.println("Translate ist ausgeschaltet");
        removeShapes(col, name);
//        if (debug) {
//            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
//        }
//        BranchGroup bg = branchGroupMap.get(name);
//        if (bg == null) {
//            throw new IllegalArgumentException("No BranchGroup found for <" + name + ">");
//        }
//        if (bg.isLive()) {
//            bg.detach();
//        }
//        for (Shape3D s : col) {
//            shapeList.remove(s);
//            bg.removeChild(s);
//        }
////		update the Bounds
//        updateBounds();
////        if (translate) {
////            translateShapes();
////            transl = translate;
////        }
//        if (bg.getParent() == null) {
//            //bg_shapes.compile();
//            root.addChild(bg);
//        }
    }

//------------------------------------------------------------------------------//
    /**
     * Entfernt alle Shape3D-Objekte.
     */
//------------------------------------------------------------------------------//
    public void removeAllShapes() {
        if (debug) {
//            System.out.println("SimpleCanvas3D.removeShape(Shape3D)");
        }
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
        }
        //if(true)return;// TODO implementieren!
        for (Shape3D s : shapeList) {
            if (s.getParent() == bg_shapes) {
                bg_shapes.removeChild(s);
//		 update the Bounds
                updateBounds();
            } else if (s.getParent() instanceof Group) {
                Group g = (Group) s.getParent();
                g.removeChild(s);
                removeLeaf(g);
            }
        }
        shapeList.clear();
//		 update the Bounds
        if (bg_shapes.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg_shapes);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * Entfernt das &uuml;bergebene Shape3D-Objekt.
     * @param name
     */
//------------------------------------------------------------------------------//	
    public void removeAllShapes(String name) {
        //if (debug)
        System.out.println("SimpleCanvas3D.removeAllShapes(String name)");
        BranchGroup bg = branchGroupMap.get(name);
        if (bg.isLive()) {
            bg.detach();
        }

        Enumeration en = bg.getAllChildren();
        while (en.hasMoreElements()) {
            Object obj = en.nextElement();
            if (obj instanceof Shape3D) {
                shapeList.remove((Shape3D) obj);
                if (((Shape3D) obj).getParent() == bg_shapes) {
                    bg_shapes.removeChild(((Shape3D) obj));
//		 update the Bounds
                    updateBounds();
                } else if (((Shape3D) obj).getParent().getParent() instanceof Group) {
                    Group g = (Group) ((Shape3D) obj).getParent();
                    g.removeChild(((Shape3D) obj));
                    removeLeaf(g);
                }
            }
        }
        bg.removeAllChildren();
//		 update the Bounds
        if (bg.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg);
        }
    }

//------------------------------------------------------------------------------//
    /**
    * F&uuml;gt der Szenen das &uuml;bergeben Licht hinzu.
    * Der Einflussbereich der Lichtquelle wird automatisch
    * so angepasst, das alle in der Szene enthaltenen Shapes beleuchtet werden.
    *
    * @param l Die neue Lichtquelle
    */
//------------------------------------------------------------------------------//
    public void addLight(Light l) {
        if(bg_lights.isLive()){
            bg_lights.detach();
        }
        l.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        l.setInfluencingBounds(visibilitySphere);
        l.setName("addedLight");
        bg_lights.addChild(l);
        lightsList.add(l);
        if(bg_lights.getParent() == null){
            root.addChild(bg_lights);
        }
    }

//------------------------------------------------------------------------------//
    /**
    * Entfernt eine Lichtquelle
    * !! Bisher noch nicht vollst&auml;ndig implementiert !!
    * @param l
*/
//------------------------------------------------------------------------------//
    public void removeLight(Light l) {
        if(bg_lights.isLive()){
            bg_lights.detach();
        }
        if(l.getParent().equals(bg_lights)){
            bg_lights.removeChild(l);
            lightsList.remove(l);
        }

        if(bg_lights.getParent() == null){
           root.addChild(bg_lights);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * Aktualisiert die BoundingBox der enthaltenen Shapes sowie
     * die Boundingsphere die f&uuml;r die Sichtbarkeit der Objekte sowie
     * die Einflussgrenzen definiert.
     */
//------------------------------------------------------------------------------//
    private void updateBounds() {
        boolean first = true;
        for (Shape3D s : shapeList) {
            if (first) {
                shapeBounds = new BoundingBox(s.getBounds());
                first = false;
            }
            shapeBounds.combine(s.getBounds());
        }
        visibilitySphere = new BoundingSphere(shapeBounds);
        transfactor = visibilitySphere.getRadius() / 50;
        //visibilitySphere.setCenter(new Point3d(0,0,0));
        visibilitySphere.setRadius(visibilitySphere.getRadius() * visibilityScaleFactor);

        p_max = new Point3d();
        shapeBounds.getUpper(p_max);
        p_min = new Point3d();
        shapeBounds.getLower(p_min);
        p_center = new Point3d(p_max.x + p_min.x, p_max.y + p_min.y, p_max.z + p_min.z);
        p_center.scale(0.5);

        if (debug) {
            System.out.println("ShapeBounds update:");
            System.out.println("\tmin (x,y,z): " + p_min.x + ", " + p_min.y + ", " + p_min.z);
            System.out.println("\tmax (x,y,z): " + p_max.x + ", " + p_max.y + ", " + p_max.z);
            System.out.println("\tcen (x,y,z): " + p_center.x + ", " + p_center.y + ", " + p_center.z);

            System.out.println("VisibilitySphere update:");
            Point3d pC = new Point3d();
            visibilitySphere.getCenter(pC);
            System.out.println("\tcenter (x,y,z): " + pC);
            System.out.println("\tradius: " + visibilitySphere.getRadius());
        }

        // Update all Nodes to the new Sphere
        updateInfluences();
    }
    

//------------------------------------------------------------------------------//
    /**
     * Aktualisiert die BoundingBox der enthaltenen Shapes sowie
     * die Boundingsphere die f&uuml;r die Sichtbarkeit der Objekte sowie
     * die Einflussgrenzen definiert wird.
     */
//------------------------------------------------------------------------------//
    private void updateBounds(Shape3D s) {
        if (shapeBounds == null) {
            shapeBounds = new BoundingBox(s.getBounds());
        } else {
            shapeBounds.combine(s.getBounds());
        }
        visibilitySphere = new BoundingSphere(shapeBounds);
        transfactor = visibilitySphere.getRadius() / 50;
        //visibilitySphere.setCenter(new Point3d(0,0,0));
        visibilitySphere.setRadius(visibilitySphere.getRadius() * visibilityScaleFactor);

        p_max = new Point3d();
        shapeBounds.getUpper(p_max);
        p_min = new Point3d();
        shapeBounds.getLower(p_min);
        p_center = new Point3d(p_max.x + p_min.x, p_max.y + p_min.y, p_max.z + p_min.z);
        p_center.scale(0.5);

        if (debug) {
            System.out.println("ShapeBounds update:");
            System.out.println("\tmin (x,y,z): " + p_min.x + ", " + p_min.y + ", " + p_min.z);
            System.out.println("\tmax (x,y,z): " + p_max.x + ", " + p_max.y + ", " + p_max.z);
            System.out.println("\tcen (x,y,z): " + p_center.x + ", " + p_center.y + ", " + p_center.z);

            System.out.println("VisibilitySphere update:");
            Point3d pC = new Point3d();
            visibilitySphere.getCenter(pC);
            System.out.println("\tcenter (x,y,z): " + pC);
            System.out.println("\tradius: " + visibilitySphere.getRadius());
        }

        // Update all Nodes to the new Sphere
        updateInfluences();
    }

//------------------------------------------------------------------------------//
    /**
     * Aktualisiert die Einflussgrenzen der verschiedenen
     * Bestandteile des Universums (Behaviours, Lichter etc.).
     */
//------------------------------------------------------------------------------//
    private void updateInfluences() {
        //Behaviours

        view.getView().setBackClipDistance(visibilitySphere.getRadius());

        BoundingSphere peter = new BoundingSphere(visibilitySphere);
        peter.setRadius(peter.getRadius() * 2);
        //Lights
        for (Light l : lightsList) {
            l.setInfluencingBounds(peter);
        }
        background.setApplicationBounds(visibilitySphere);
    }
@Deprecated
//------------------------------------------------------------------------------//
    /**
     * Aktualisiert die Transformation der TransformGroup tg_shapes.
     * Es wird eine Translation der enthaltenen Shapes durchgef&uuml;hrt,
     * so dass das Zentrum der BoundingBox der Shapes im Koordinatenursprung liegt.
     */
//------------------------------------------------------------------------------//
    public void translateShapes() {
//    System.out.println("Translate Shapes ausgeschaltet");
        //if(0==0)return;
//        Transform3D translation = new Transform3D();
//        translation.setTranslation(new Vector3d(-p_center.x, -p_center.y, -p_center.z));
//        tg_translateShapes.setTransform(translation);
    }

//------------------------------------------------------------------------------//
    /**
     * Verschiebt die ViewingPlatform auf (0, 0, dz),
     * wobei dz so gew&auml;hlt wird, das die BoundingBox der Shapes
     * vollst&auml;ndig sichtbar ist.
     */
//------------------------------------------------------------------------------//
    public void setDefaultViewPosition() {
        if (p_max == null) {
            System.out.println("ERROR in setDefaultViewPosition():\n\t" +
                    "default View-Position could not be set.\n\t" +
                    "Add Shapes first.");
            return;
        }
        BoundingSphere sphere = new BoundingSphere(shapeBounds);
        System.out.println(sphere);
        Transform3D viewTrafo = new Transform3D();
        double dx = sphere.getRadius() * 2.0;//p_max.x - p_min.x;
        //TODO auch dy beruecksichtigen!!
//		double dy = p_max.y - p_min.y;
//
//		if(dx<dy){
//			dx=dy;
//		}

        double dz = (0.5 * dx) / Math.sin(0.5 * view.getView().getFieldOfView());

        if (transl) {
            viewTrafo.setTranslation(new Vector3d(0., 0., dz));
        } else {
            viewTrafo.setTranslation(new Vector3d(p_center.x, p_center.y, dz));
        }
        tg_viewTrafo.setTransform(viewTrafo);
        setRotationCenter(p_center);
    }

//------------------------------------------------------------------------------//
    /**
     * Setzt die Farbe des Hintergrunds.
     * @param color Die neue Hintergrundfarbe
     */
//------------------------------------------------------------------------------//
    public void setBackgroundColor(Color color) {
        background.setColor(new Color3f(color));
        background.setApplicationBounds(new BoundingSphere(new Point3d(), Double.POSITIVE_INFINITY));
    }

//------------------------------------------------------------------------------//
    /**
     * Initialisert die Bounds-Objekte
     */
//------------------------------------------------------------------------------//
    private void initBounds() {
//		shapeBounds = new BoundingBox();
//		
        visibilitySphere = new BoundingSphere();
    }

//------------------------------------------------------------------------------//
    /**
     * Initialisiert die Basis-Objekte des Szene-Graphen.
     * (Universe, Locale, root-BranchGroup)
     */
//------------------------------------------------------------------------------//
    private void initUniverse() {
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
        root.setCapability(BranchGroup.ALLOW_DETACH);
    }

//------------------------------------------------------------------------------//
    /**
     * Initialisiert den View-Ast des Szene-Graphen.
     * (BranchGroup bg_view)
     */
//------------------------------------------------------------------------------//
    private void initViewGraph() {
        BranchGroup bg_view = new BranchGroup();

        view = new Viewer(this);
        orbit = new OrbitBehavior(this, OrbitBehavior.STOP_ZOOM | OrbitBehavior.REVERSE_ALL);
//                orbit.setCapability();
//                orbit.setCapability(OrbitBehavior.ALLOW_LOCAL_TO_VWORLD_READ);
//                orbit.setCapability(OrbitBehavior.ALLOW_BOUNDS_WRITE);
        orbit.setProportionalZoom(true);
        orbit.setSchedulingBounds(new BoundingSphere(new Point3d(), visibilitySphere.getRadius() * visibilityScaleFactor + Double.POSITIVE_INFINITY));

        view.getView().setBackClipDistance(visibilitySphere.getRadius());
        //view.setSceneAntialiasingEnable(true);
        view.getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);

        ViewingPlatform viewPlat = new ViewingPlatform();
        viewPlat.setViewPlatformBehavior(orbit);
        viewPlat.setNominalViewingTransform();
        view.setViewingPlatform(viewPlat);

        tg_viewTrafo = new TransformGroup();
        tg_viewTrafo.setName("tg_viewTrafo");
        tg_viewTrafo.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg_viewTrafo.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        tg_viewTrafo = viewPlat.getViewPlatformTransform();
        bg_view.addChild(viewPlat);
        root.addChild(bg_view);
    }

//------------------------------------------------------------------------------//
    /**
     * Initialisiert den Komponenten-Ast des Szene-Graphen.
     * (BranchGroup bg_shapes)
     */
//------------------------------------------------------------------------------//
    private void initComponetGraph() {
        bg_shapes = new BranchGroup();
        bg_shapes.setName("bg_shapes");
        bg_shapes.setCapability(BranchGroup.ALLOW_DETACH);
        bg_shapes.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bg_shapes.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
        bg_shapes.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        bg_shapes.setCapability(BranchGroup.ALLOW_PARENT_READ);
        bg_shapes.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        branchGroupMap.put(bg_shapes.getName(), bg_shapes);
       
        root.addChild(bg_shapes);

        bg_environment = new BranchGroup();
        bg_environment.setName("bg_environment");
        bg_environment.setCapability(BranchGroup.ALLOW_DETACH);
        bg_environment.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        root.addChild(bg_environment);

    }

//------------------------------------------------------------------------------//
    /**
     * Erm&ouml;glicht Mausinteraktionen.
     *
     * @param b An oder Aus
     */
//------------------------------------------------------------------------------//
    public void setMouseInteraction(boolean b) {
        if (b) {
            if (p_center != null) {

                if (transl) {
                    orbit.setRotationCenter(new Point3d(0, 0, 0));
                } else {
                    orbit.setRotationCenter(p_center);
                }
            }
        }
        orbit.setRotateEnable(b);
        orbit.setRotFactors(0.5, 0.5);
        orbit.setZoomEnable(b);
        orbit.setZoomFactor(0.5);
        orbit.setTranslateEnable(b);
        orbit.setTransFactors(transfactor, transfactor);
    }

    public void setKeyInteraction(boolean b){

        myKey = new MyKeyListener(this);
        if(b){
            addKeyListener(myKey);
        }else if(myKey != null){
            removeKeyListener(myKey);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * Initialisiert den Licht-Ast des Szene-Graphen.
     * (BranchGroup bg_lights)
     */
//------------------------------------------------------------------------------//
    private void initLightGraph() {
        bg_lights = new BranchGroup();
        bg_lights.setName("bg_lights");
        bg_lights.setCapability(BranchGroup.ALLOW_DETACH);
        bg_lights.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        bg_lights.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        System.out.println("initLight");
//        tg_lights = new TransformGroup();
//        tg_lights.setName("bg_lights");
//        tg_lights.setCapability(BranchGroup.ALLOW_DETACH);
        root.addChild(bg_lights);
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @return
     */
//------------------------------------------------------------------------------//	
    public String getXMLRepresentationOfSceneGaph() {
        StringBuffer buff = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        Node n = root;
        buildTree(buff, n, 0);
        return buff.toString();
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param buff
     * @param n
     * @param level
     */
//------------------------------------------------------------------------------//	
    private void buildTree(StringBuffer buff, Node n, int level) {
        if (n instanceof Group) {
            for (int i = 0; i < level; i++) {
                buff.append("\t");
            }
            buff.append("<" + n.getName() + ">\n");
            Group group = (Group) n;
            Enumeration en = group.getAllChildren();
            while (en.hasMoreElements()) {
                buildTree(buff, (Node) en.nextElement(), level + 1);
            }
            for (int i = 0; i < level; i++) {
                buff.append("\t");
            }
            buff.append("</" + n.getName() + ">\n");
        } else if (n instanceof Leaf) {
            Leaf leaf = (Leaf) n;
            for (int i = 0; i < level; i++) {
                buff.append("\t");
            }
            buff.append("<" + leaf.getName() + "/>\n");
        }
    }

//------------------------------------------------------------------------------//
    /**
     * Hinzuf&uuml;gen einer Standard-Beleuchtung
     */
//------------------------------------------------------------------------------//
    public void addDefaultLights() {

        AmbientLight licht = new AmbientLight();
        licht.setName("defLight_ambient");
        licht.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);

        licht.setInfluencingBounds(visibilitySphere);
        addLight(licht);

        DirectionalLight dLicht = new DirectionalLight(new Color3f(Color.white), new Vector3f(-10.f, -50.f, -10.f));
        dLicht.setInfluencingBounds(visibilitySphere);
        dLicht.setName("defLight_direct");
        addLight(dLicht);

        float r = (float) visibilitySphere.getRadius();
        PointLight pLight1 = new PointLight(new Color3f(Color.WHITE), new Point3f(-r, r, r), new Point3f(1.2f, 0f, 0f));
        pLight1.setName("defLight_pLight1");
        pLight1.setInfluencingBounds(visibilitySphere);
        pLight1.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        addLight(pLight1);

        PointLight pLight2 = new PointLight(new Color3f(Color.WHITE), new Point3f(r, r, r), new Point3f(1.2f, 0f, 0f));
        pLight2.setName("defLight_pLight2");
        pLight2.setInfluencingBounds(visibilitySphere);
        pLight2.setCapability(Light.ALLOW_INFLUENCING_BOUNDS_WRITE);
        addLight(pLight2);
        
    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt der Darstellung ein Achsenkreuz hinzu.
     * @param size Die Gr&ouml;sse des Achsenkreuzes
     */
//------------------------------------------------------------------------------//
    public void showAxis(float size) {
        if(bg_environment.isLive())bg_environment.detach();

        axis = new Axis3D(new float[]{0.4f, 0.4f, 0.4f}, size);
        axis.setCapability(BranchGroup.ALLOW_PARENT_READ);
        axis.setName("axis");
//        if(axis.getParent().equals(bg_environment)){
//            System.out.println("bg" +
//                    "hhhh");
//        }

        if(axis.getParent() == null)
        bg_environment.addChild(axis);

        if(bg_environment.getParent() == null){
            root.addChild(bg_environment);
        }
    }

//------------------------------------------------------------------------------//
    /**
     * F&uuml;gt der Darstellung ein Achsenkreuz hinzu.
     * @param size Die Gr&ouml;sse des Achsenkreuzes
     */
//------------------------------------------------------------------------------//
    public void showAxis_v2(float size) {
         if(bg_environment.isLive())bg_environment.detach();

        axis3d = new AxisSystem3D(size, 1f);
        axis3d.setName("axis2");
        axis3d.setCapability(BranchGroup.ALLOW_PARENT_READ);

        if(axis3d.getParent() == null)
        bg_environment.addChild(axis3d);

        if(bg_environment.getParent() == null){
            root.addChild(bg_environment);
        }
    }

    public void removeAxis(){
            if(bg_environment.isLive()) bg_environment.detach();

            if(axis != null){
                bg_environment.removeChild(axis);
                axis = null;
            }
            if(axis3d != null){
                bg_environment.removeChild(axis3d);
                axis3d = null;
            }
                
            if(bg_environment.getParent() == null) root.addChild(bg_environment);

    }

//------------------------------------------------------------------------------//
    /**
     * Kompiliert den Szene-Graphen und f&uuml;gt ihn dem Universum hinzu.
     * Diese Methode muss aufgerufen werden, bevor das Canvas angezeigt werden soll.
     */
//------------------------------------------------------------------------------//
    public void compile() {
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
    public Transform3D getActualViewTrafo(Transform3D trafo) {
        tg_viewTrafo.getTransform(trafo);
        return trafo;
    }

//------------------------------------------------------------------------------//
    /**
     * Transformiert die Darstellung enstprechend dem &uuml;bergebenen Transform3D-Objekt
     * @param trafo
     */
//------------------------------------------------------------------------------//
    public void setActualViewTrafo(Transform3D trafo) {
        tg_viewTrafo.setTransform(trafo);
    }

//------------------------------------------------------------------------------//
    /**
     * Setzt die ViewPlatform auf eine StandardView, definiert durch die
     * Parameter welche in einer Quat4d Matrize gesetzt werden
     *
     * @param x x-Wert der Matrize
     * @param y y-Wert der Matrize
     * @param z z-Wert der Matrize
     * @param w w-Wert der Matrize
     */
//------------------------------------------------------------------------------//
    public void setStandardView(double x, double y, double z, double w) {
        setDefaultViewPosition();
        Transform3D trafo = new Transform3D();
        tg_viewTrafo.getTransform(trafo);
        trafo.setRotation(new Quat4d(x, y, z, w));
        tg_viewTrafo.setTransform(trafo);
    }

//------------------------------------------------------------------------------//
    /**
     * Setzt die ViewPlatform auf eine der 6 vorgegebene StandardViews
     * 
     * @param i ist der Parameter f&uuml;r die Sicht <br>
     *  VIEW_FRONT ; VIEW_BACK    <br>
     *  VIEW_TOP   ; VIEW_BOTTOM  <br>
     *  VIEW_LEFT  ; VIEW_RIGHT   <br>
     * 
     */
//------------------------------------------------------------------------------//
    public void rotatateToStandardView(int i) {
        Transform3D trafo = new Transform3D();
        tg_viewTrafo.getTransform(trafo);
        Matrix3f rot = new Matrix3f();
        switch (i) {
            case VIEW_TOP:    rot.m00 =  1;rot.m11 =  1;rot.m22 =  1;trafo.setRotation(rot);break;
            case VIEW_BOTTOM: rot.m00 =  1;rot.m11 = -1;rot.m22 = -1;trafo.setRotation(rot);break;
            case VIEW_LEFT:   rot.m01 = -1;rot.m12 =  1;rot.m20 = -1;trafo.setRotation(rot);break;
            case VIEW_RIGHT:  rot.m01 =  1;rot.m12 =  1;rot.m20 =  1;trafo.setRotation(rot);break;
            case VIEW_FRONT:  rot.m00 =  1;rot.m12 =  1;rot.m21 = -1;trafo.setRotation(rot);break;
            case VIEW_BACK:   rot.m00 = -1;rot.m12 =  1;rot.m21 =  1;trafo.setRotation(rot);break;
            default:
                break;
        }
        tg_viewTrafo.setTransform(trafo);
    }


    //#######################################//
    //##              PICKING              ##//
    //#######################################//
    
//------------------------------------------------------------------------------//
    /**
     *
     * @return
     */
//------------------------------------------------------------------------------//
    public PickCanvas getPickCanvas() {
        PickCanvas pc = new PickCanvas(this, bg_shapes);
        return pc;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
    public PickCanvas getPickCanvas(String branchGroupName) {
        BranchGroup bg = branchGroupMap.get(branchGroupName);
        PickCanvas pc = new PickCanvas(this, bg);
        return pc;
    }

//------------------------------------------------------------------------------//
    /**
     *
     */
//------------------------------------------------------------------------------//
    public void enablePicking() {
        if (bg_shapes.isLive()) {
            bg_shapes.detach();
        }
        enablePicking(bg_shapes);
        if (bg_shapes.getParent() == null) {
            //bg_shapes.compile();
            root.addChild(bg_shapes);
        }
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param branchGroupName
     */
//------------------------------------------------------------------------------//
    public void enablePicking(String branchGroupName) {
        BranchGroup bg = branchGroupMap.get(branchGroupName);
        if (bg.isLive()) {
            bg.detach();
        }
        enablePicking(bg);
        if (bg.getParent() == null) {
            //bg_shapes.compile();
            bg_shapes.addChild(bg);
        }
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param node
     */
//------------------------------------------------------------------------------//
    @SuppressWarnings("static-access")
    private void enablePicking(Node node) {
        node.setCapability(Node.ALLOW_PICKABLE_WRITE);
        node.setPickable(true);

        node.setCapability(Node.ENABLE_PICK_REPORTING);

        try {

            Group group = (Group) node;

            for (Enumeration e = group.getAllChildren(); e.hasMoreElements();) {

                enablePicking((Node) e.nextElement());

            }

        } catch (ClassCastException e) {
            // if not a group node, there are no children so ignore exception
        }

        try {

            Shape3D shape = (Shape3D) node;

            PickTool.setCapabilities(node, PickTool.INTERSECT_FULL);

            for (Enumeration e = shape.getAllGeometries(); e.hasMoreElements();) {

                Geometry g = (Geometry) e.nextElement();

                g.setCapability(g.ALLOW_INTERSECT);

            }

        } catch (ClassCastException e) {
            // not a Shape3D node ignore exception
        }

    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param pickShape
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
    public int[] pickClosest(PickShape pickShape, String branchGroupName) {
        BranchGroup bg = branchGroupMap.get(branchGroupName);
        //enablePicking(bg);
        System.out.println("BranchGroup=" + bg);
        bg.pickAllSorted(pickShape);
        System.out.println("bg.pickAllSorted=" + bg.pickAllSorted(pickShape));
        PickInfo pickInfo = bg.pickClosest(PickInfo.PICK_GEOMETRY, PickInfo.ALL_GEOM_INFO | PickInfo.LOCAL_TO_VWORLD, pickShape);
        if (pickInfo == null) {
            System.out.println("pickInfo==null");
            return null;
        }
        if (pickInfo.getIntersectionInfos() == null) {
            System.out.println("pickInfo.getIntersectionInfos()==null");
            return null;
        }
        if (pickInfo.getIntersectionInfos().length == 0) {
            System.out.println("pickInfo.getIntersectionInfos().length==0");
            return null;
        }
        if (pickInfo.getIntersectionInfos()[0].getVertexIndices() == null) {
            System.out.println("pickInfo.getIntersectionInfos()[0].getVertexIndices()==null");
            return null;
        }

        int[] vertexIndices = pickInfo.getIntersectionInfos()[0].getVertexIndices();
        return vertexIndices;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param pickShape
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
    public IntersectionInfo getIntersectionInfoOfClosestTriangle(PickShape pickShape, String branchGroupName) {
        BranchGroup bg = branchGroupMap.get(branchGroupName);
        //enablePicking(bg);
        System.out.println("BranchGroup=" + bg);
        bg.pickAllSorted(pickShape);
        System.out.println("bg.pickAllSorted=" + bg.pickAllSorted(pickShape));
        PickInfo pickInfo = bg.pickClosest(PickInfo.PICK_GEOMETRY, PickInfo.ALL_GEOM_INFO | PickInfo.LOCAL_TO_VWORLD, pickShape);
        if (pickInfo == null) {
            System.out.println("pickInfo==null");
            return null;
        }
        if (pickInfo.getIntersectionInfos() == null) {
            System.out.println("pickInfo.getIntersectionInfos()==null");
            return null;
        }
        if (pickInfo.getIntersectionInfos().length == 0) {
            System.out.println("pickInfo.getIntersectionInfos().length==0");
            return null;
        }
        if (pickInfo.getIntersectionInfos()[0].getVertexIndices() == null) {
            System.out.println("pickInfo.getIntersectionInfos()[0].getVertexIndices()==null");
            return null;
        }

        return pickInfo.getIntersectionInfos()[0];
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param e
     * @return
     */
//------------------------------------------------------------------------------//
    public Shape3D getPickedShape(MouseEvent e) {
        enablePicking();
        PickCanvas pickCanvas = getPickCanvas();
        pickCanvas.setMode(PickCanvas.BOUNDS);

        pickCanvas.setTolerance(0);
        pickCanvas.setShapeLocation(e);
        PickResult pres = pickCanvas.pickClosest();

        Shape3D s = (Shape3D) pres.getNode(PickResult.SHAPE3D);

        return s;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param e
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
    public Shape3D getPickedShape(MouseEvent e,String branchGroupName) {
        enablePicking(branchGroupName);
        PickCanvas pickCanvas = getPickCanvas(branchGroupName);
        pickCanvas.setMode(PickCanvas.BOUNDS);

        pickCanvas.setTolerance(0);
        pickCanvas.setShapeLocation(e);
        PickResult pres = pickCanvas.pickClosest();

        Shape3D s = (Shape3D) pres.getNode(PickResult.SHAPE3D);

        return s;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param e
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
     public ArrayList<Shape3D> getPickedShapes(MouseEvent e, String branchGroupName) {
        enablePicking(branchGroupName);
        ArrayList<Shape3D> s = new ArrayList<Shape3D>(1);
        PickCanvas pickCanvas = getPickCanvas(branchGroupName);
        pickCanvas.setMode(PickCanvas.BOUNDS);

        pickCanvas.setShapeLocation(e);
        PickResult[] pres = pickCanvas.pickAll();

        for (int i = 0; i < pres.length; i++) {
            s.add((Shape3D) pres[i].getNode(PickResult.SHAPE3D));
        }
        return s;
    }

//------------------------------------------------------------------------------//
     /**
      *
      * @param e
      * @return
      */
//------------------------------------------------------------------------------//
    public ArrayList<Shape3D> getPickedShapes(MouseEvent e) {
        enablePicking();
        ArrayList<Shape3D> s = new ArrayList<Shape3D>(1);
        PickCanvas pickCanvas = getPickCanvas();
        pickCanvas.setMode(PickCanvas.BOUNDS);

        pickCanvas.setShapeLocation(e);
        PickResult[] pres = pickCanvas.pickAll();

        for (int i = 0; i < pres.length; i++) {
            s.add((Shape3D) pres[i].getNode(PickResult.SHAPE3D));
        }
        return s;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param e
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
    public TransformGroup getPickedNode(MouseEvent e, String branchGroupName){
            enablePicking(branchGroupName);
        TransformGroup s=null;
        PickCanvas pickCanvas = getPickCanvas(branchGroupName);
        pickCanvas.setMode(PickCanvas.BOUNDS);

        pickCanvas.setShapeLocation(e);
        pickCanvas.setTolerance(2);
        PickResult pres = pickCanvas.pickClosest();

        
            s = ((TransformGroup) pres.getNode(PickResult.TRANSFORM_GROUP));
        
        return s;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param e
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
    public Link[] getPickedLinks(MouseEvent e, String branchGroupName){
        
        PickCanvas pickCanvas = getPickCanvas(branchGroupName);

        pickCanvas.setShapeLocation(e);
        pickCanvas.setTolerance(2);
        PickResult[] pres = pickCanvas.pickAll();
        Link[] s  = new Link[pres.length];
        
    for (int i = 0; i < pres.length; i++) {
            s[i] = ((Link) pres[i].getNode(PickResult.LINK));
    }
        return s;
    }

    public Link[] getPickedLinks(MouseEvent e){

        PickCanvas pickCanvas = getPickCanvas();

        pickCanvas.setShapeLocation(e);
        pickCanvas.setTolerance(2);
        PickResult[] pres = pickCanvas.pickAll();
        Link[] s  = new Link[pres.length];

    for (int i = 0; i < pres.length; i++) {
            s[i] = ((Link) pres[i].getNode(PickResult.LINK));
    }
        return s;
    }

//------------------------------------------------------------------------------//
    /**
     *
     * @param e
     * @param branchGroupName
     * @return
     */
//------------------------------------------------------------------------------//
    public Link getPickedLink(MouseEvent e, String branchGroupName){

        PickCanvas pickCanvas = getPickCanvas(branchGroupName);

        pickCanvas.setShapeLocation(e);
        pickCanvas.setTolerance(2);
        PickResult pres = pickCanvas.pickClosest();

        Link s  = ((Link) pres.getNode(PickResult.LINK));
        return s;
    }

//------------------------------------------------------------------------------//
    /**
     * Methode um durch eine 3D-Welt zu wandern. Man &uuml;bergibt einen Standpunkt von dem aus man
     * guckt  und eine Richtung in welche man guckt.
     *
     * @param position  Standpunkt von dem aus man guckt.
     * @param blickrtg  Richtung in die man guckt
     * @param vec       Normalenverktor der ViewPlatform
     */
//------------------------------------------------------------------------------//
    public void setViewPoint(Point3d position, Point3d blickrtg, Vector3d vec) {
        Transform3D tf3 = new Transform3D();
        tf3.lookAt(position, blickrtg, vec);
        tf3.invert(tf3);
        tg_viewTrafo.setTransform(tf3);
    }

//------------------------------------------------------------------------------//
    /**
     * Methode um durch eine 3D-Welt zu wandern. Man &uuml;bergibt einen Standpunkt von dem aus man
     * guckt  und eine Richtung in welche man guckt.
     *
     * @param position  Standpunkt von dem aus man guckt.
     * @param blickrtg  Richtung in die man guckt
     */
//------------------------------------------------------------------------------//
    public void setViewPoint(Point3d position, Point3d blickrtg) {
        Transform3D tf3 = new Transform3D();
        tf3.lookAt(position, blickrtg, new Vector3d(0, 1, 0));
        tf3.invert(tf3);
        tg_viewTrafo.setTransform(tf3);
    }

 
//------------------------------------------------------------------------------//
    /**
     *  Methode um den RotationsMittelPunkt des OrbitBehaviors neu zu setzen
     * @param p MittelPunkt des des Orbitbehaviors
     */
//------------------------------------------------------------------------------//
    public void setRotationCenter(Point3d p) {
        orbit.setRotationCenter(p);
    }


//------------------------------------------------------------------------------//
    /**
     * 
     * @param args
     */
//------------------------------------------------------------------------------//
     public static void main(String[] args) {
		
	    JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 786);

            SimpleCanvas3D canvas = new SimpleCanvas3D();
            frame.add(canvas);
           
//------------------------------------------------------------------------------//
/**
 *  Beispiel f&uuml;r ein 3D-Object(Shape3D)
 *
 * auskommentiert ist ein Beispiel f&uuml;r die Methode
 * setViewPoint()
 *
 */
//------------------------------------------------------------------------------//
        Point3d[] p = new Point3d[24];
        p[0] = new Point3d(2,0,0);                  p[4] = new Point3d(2,10,0);
        p[1] = new Point3d(1,0,0);                  p[5] = new Point3d(1,10,0);
        p[2] = new Point3d(1,0,100);                p[6] = new Point3d(1,10,100);
        p[3] = new Point3d(2,0,100);                p[7] = new Point3d(2,10,100);

        p[8] = new Point3d(7,0,0);                  p[12] = new Point3d(7,10,0);
        p[9] = new Point3d(6,0,0);                  p[13] = new Point3d(6,10,0);
        p[10] = new Point3d(6,0,100);               p[14] = new Point3d(6,10,100);
        p[11] = new Point3d(7,0,100);               p[15] = new Point3d(7,10,100);
       
        p[16] = new Point3d(4,0,-100);              p[20] = new Point3d(9,0,-100);
        p[17] = new Point3d(5,0,-100);              p[21] = new Point3d(10,0,-100);
        p[18] = new Point3d(5,10,-100);             p[22] = new Point3d(10,10,-100);
        p[19] = new Point3d(4,10,-100);             p[23] = new Point3d(9,10,-100);
       
        int[] in = new int[96];

        in[0]  = 0;in[1]  = 1;in[2]  = 2;in[3]  =  3;   in[4]  = 4;in[5]  = 5;in[6]  = 6;in[7]  =  7;
        in[8]  = 0;in[9]  = 4;in[10] = 7;in[11] =  3;   in[12] = 1;in[13] = 5;in[14] = 6;in[15] =  2;
        
        in[16] = 8;in[17] = 9;in[18] = 10;in[19]= 11;   in[20] = 12;in[21]= 13;in[22]= 14;in[23]= 15;  
        in[24] = 8;in[25] = 12;in[26]= 15;in[27]= 11;   in[28] = 9;in[29] = 13;in[30]= 14;in[31]= 10;
        
        in[32] = 0;in[33] = 17;in[34]= 18;in[35]=  4;   in[36] = 1;in[37] = 16;in[38]= 19;in[39]=  5;
        in[40] = 4;in[41] = 18;in[42]= 19;in[43]=  5;   in[44] = 16;in[45]= 17;in[46]= 18;in[47]= 19;
 
        in[48] = 8;in[49] = 21;in[50]= 20;in[51]=  9;   in[52] = 8;in[53] = 21;in[54]= 22;in[55]= 12;
        in[56] = 9;in[57] = 20;in[58]= 23;in[59]= 13;   in[60] = 12;in[61]= 22;in[62]= 23;in[63]= 13;

        in[64] = 2;in[65] =  3;in[66]=  7;in[67]=  6;   in[68] = 10;in[69]= 11;in[70]= 15;in[71]= 14;
        in[72] = 0;in[73] = 17;in[74]= 16;in[75]=  1;   in[76] = 21;in[77]= 22;in[78]= 23;in[79]= 20;
        
        in[80] = 3;in[81] = 10;in[82]=  9;in[83]=  1;   in[84] = 7;in[85] = 14;in[86]= 13;in[87]=  4;
        in[88] = 4;in[89] = 13;in[90]= 23;in[91]= 18;   in[92] = 0;in[93] =  9;in[94]= 20;in[95]= 17;

                
        Shape3D test01 = new Shape3D();
        Shape3D test02 = new Shape3D();
//        Shape3D test03 = new Shape3D();

        IndexedQuadArray geom = new IndexedQuadArray(p.length, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_3, in.length);
        geom.setCoordinates(0, p);
        geom.setCoordinateIndices(0, in);
        test01.setGeometry(geom);
        test02.setGeometry(geom);
//        test03.setGeometry(geom);
        
        Appearance ap = new Appearance();
        PolygonAttributes pattrib = new PolygonAttributes();
        pattrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        pattrib.setCapability(PolygonAttributes.ALLOW_MODE_WRITE);
        pattrib.setCullFace(PolygonAttributes.CULL_NONE);
        pattrib.setBackFaceNormalFlip(true);
        ap.setPolygonAttributes(pattrib);
        ColoringAttributes cattrib = new ColoringAttributes(0.f, 0.f, 1.f,
                ColoringAttributes.NICEST);
        ap.setColoringAttributes(cattrib);
        ap.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.3f));
        ap.setMaterial(new Material(new Color3f(0.2f, 0.2f, 0.2f),
                new Color3f(0f, 0f, 0f),
                new Color3f(1f, 1f, 1f),
                new Color3f(1f, 1f, 1f),
                100f));
        test01.setAppearance(ap);
//        test02.setAppearance(ap);
//        test03.setAppearance(ap);

        canvas.addShape(test01);
        TransformGroup tg = new TransformGroup();
//        TransformGroup tg1 = new TransformGroup();
//        TransformGroup tg2 = new TransformGroup();
//        TransformGroup tg3 = new TransformGroup();
        Transform3D tf = new Transform3D();
        tf.setTranslation(new Vector3d(10,0,0));
        tg.setTransform(tf);
//
//        Transform3D tf1 = new Transform3D();
//        tf1.setTranslation(new Vector3d(0,30,0));
//        tg.setTransform(tf1);
//
//        tg.addChild(tg1);
        tg.addChild(test02);
//        tg1.addChild(tg2);
//        tg2.addChild(tg3);
//        tg3.addChild(test03);
//        canvas.addGroup(tg);
        canvas.addGroup(tg);
        canvas.setBackgroundColor(Color.DARK_GRAY);
        canvas.showAxis(8f);
        canvas.setDefaultViewPosition();
        canvas.setMouseInteraction(true);

//        canvas.removeShape(test02);
        canvas.compile();// DON'T FORGET !

        frame.setVisible(true);


       
// Beispiel f&uuml;r die Methode setViewPoints(...)

//        Point3d pos = new Point3d();
//        Point3d blick = new Point3d();
//           for (double i = 0; i < 299; i += 1) {
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(SimpleCanvas3D_NEU.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            pos.set(4, 5, 150 - i/2);
//            blick.set(4,5,0);
//
//           canvas.setViewPoint(pos, blick);
//        }
//        for (double i = 0; i < 90; i += 1) {
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(SimpleCanvas3D_NEU.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            pos.set(4, 5, 0);
//            blick.set(4+i/30,5,-100);
//
//           canvas.setViewPoint(pos, blick);
//        }
//
//        double n = 200;
//        for (double i = 0; i < n; i += 1) {
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(SimpleCanvas3D_NEU.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            pos.set(4+(3/n)*i, 5, 0 - i/2);
//            blick.set(7,5,-100);
//
//            canvas.setViewPoint(pos, blick);
//        }
    }

    class MyKeyListener implements KeyListener {

        private int aaa = 0;
        private SimpleCanvas3D si;

        public MyKeyListener(SimpleCanvas3D si) {
            this.si = si;
        }


        public void keyTyped(KeyEvent e) {
//            if(e.getKeyChar() == 'r'){
//                si.addMouseListener(new MouseAdapter() {
//
//                    @Override
//                    public void mousePressed(MouseEvent e) {
//                        super.mousePressed(e);
//                        if(getPickedLinks(e) != null){
//                            Link[] l = getPickedLinks(e);
//                            System.out.println(l[0]);
//                        }else if(getPickedShapes(e) != null){
//                            ArrayList<Shape3D> list = getPickedShapes(e);
//                            System.out.println("SHAPE§D     "+list.get(0).getBounds());
//                        }
//                    }
//                });
//
//
//            }
        }

        public void keyPressed(KeyEvent e) {
            if(e.getKeyChar() == 'd'){
                setDefaultViewPosition();
                return;
            }
            if(e.getKeyChar() == 'k'){
                if(aaa == 0){
                    showAxis(10f);
                    aaa++;
                }else if(aaa == 1){
                removeAxis();
                showAxis_v2(10f);
                aaa++;
                }else{
                removeAxis();
                aaa=0;
                }
                return;
            }
        }

        public void keyReleased(KeyEvent e) {

        }
    }
}
