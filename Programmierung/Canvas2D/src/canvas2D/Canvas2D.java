package canvas2D;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class Canvas2D extends JComponent {

    private ArrayList<ShapeListener> shapeListener;
    private static final long serialVersionUID = 1L;
    private static final int ZOOM_MODE_RISE_PERCENTAGE = 0;
    private double zoom = 1;
    private double transX = Double.MAX_VALUE;
    private double transY = Double.MAX_VALUE;
    private double rand = 0.0;
    private ArrayList<String> layerReihenfolge;
    private HashMap<String, Layer> layerMap;
    private ArrayList<Action> scaleActions;
    private Rectangle2D bounds;
    private MouseListener_ZOOM01 mouseListener_ZOOM01;
    private MouseListener_ZOOM02 mouseListener_ZOOM02;
    private MouseListener_ZOOM03 mouseListener_ZOOM03;
    private RulerComponent ruler_x;
    private RulerComponent ruler_y;
    private boolean showRuler_x = false;
    private boolean showRuler_y = false;
    private boolean updateBounds = true;
    private HashMap<Object, Shape> objectToShapeMap = new HashMap<>();

    public Canvas2D() {
        shapeListener = new ArrayList<ShapeListener>();
        layerMap = new HashMap<String, Layer>();
        layerReihenfolge = new ArrayList<String>();
        scaleActions = new ArrayList<Action>();
        addShapeLayer("default");
        init();
    }

    public Shape getShape(Object o) {
        return this.objectToShapeMap.get(o);
    }

    public void addShapeListener(ShapeListener sl) {
        shapeListener.add(sl);
    }

    public void removeShapeListener(ShapeListener sl) {
        shapeListener.remove(sl);
    }

    private void init() {
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        this.addComponentListener(new FensterListener());
        this.addMouseMotionListener(new DefaultMouseMotionListener());
        this.addMouseListener(new DefaultMouseListener());
    }

    public void addShapeLayer(String layername) {
        addShapeLayer(layername, false, null, 0);
    }

    public void addShapeLayer(String layername, Collection<Shape> col) {
        addShapeLayer(layername, col, 512);
    }

    public void addShapeLayer(String layername, Collection<Shape> col, int maxNumberOfelmtsPerCell) {
        addShapeLayer(layername, true, col, maxNumberOfelmtsPerCell);
    }

    public void addShapeLayer(String layername, boolean quadTreeManaged, Collection<Shape> col, int maxNumberOfelmtsPerCell) {
        layerReihenfolge.add(layername);
        layerMap.put(layername, new Layer(layername, quadTreeManaged, col, maxNumberOfelmtsPerCell));
        moveToTop("default");
    }

    public void addShapeLayer(String layername, int maxNumberOfelmtsPerCell, double xmin, double xmax, double ymin, double ymax) {
        layerReihenfolge.add(layername);
        layerMap.put(layername, new Layer(layername, true, maxNumberOfelmtsPerCell, xmin, xmax, ymin, ymax));
        moveToTop("default");
        extendBounds(new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin));
        System.out.println("Bounds:" + new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin));

    }

    /**
     * Entfernt ein Layer
     *
     * @param layername der Name des zu entfernenden Layers
     */
    public void removeLayer(String layername) {
        if (!layername.equals("default")) {
            layerMap.remove(layername);
            layerReihenfolge.remove(layername);
        } else {
            System.out.println("Layer default kann nicht geloescht werden!");
        }
        if (updateBounds) {
            validateBounds();
        }
    }

    /**
     * Entfernt alle Layer, ausser "default"
     */
    public void removeAllLayer() {
        for (Layer l : getLayers()) {
            if (!l.name.equals("default")) {
                removeLayer(l.name);
            }
        }
    }

    public void removeAllShapes(String layer) {
        Layer l = layerMap.get(layer);
        if (l != null) {
            l.removeAllShapes();
            if (updateBounds) {
                validateBounds();
            }
        }
    }

    public void removeShape(Shape s, String layer) {
        Layer l = layerMap.get(layer);
        if (l != null) {
            l.removeShape(s);
            if (updateBounds) {
                validateBounds();
            }
        }
    }

    public void removeShape(Shape s) {
        Layer l = layerMap.get("default");
        if (l != null) {
            l.removeShape(s);
            if (updateBounds) {
                validateBounds();
            }
        }
    }

    /**
     * Sets the position of the passed in layer to the passed in postion of the
     * layer stack. If the passed in position is higher than the number of
     * layers, the layer's position is set to the top of the layers stack.
     *
     * @param layername The name of the Layer.
     * @param pos The new position of the layer
     */
    public void setLayerPosition(String layername, int pos) {
        layerReihenfolge.remove(layername);
        layerReihenfolge.add(pos, layername);
    }

    /**
     * Sets the position of the passed in layer to the top of the layer stack.
     *
     * @param layername The name of the Layer.
     */
    public void moveToTop(String layername) {
        layerReihenfolge.remove(layername);
        layerReihenfolge.add(layername);
    }

    /**
     * Sets the position of the passed in layer to the bottom of the layer
     * stack.
     *
     * @param layer The name of the Layer.
     */
    public void moveToBottom(String layername) {
        layerReihenfolge.remove(layername);
        layerReihenfolge.add(0, layername);
    }

    /**
     * Raises the position of the passed in layer by one
     *
     * @param layername The name of the Layer.
     */
    public void moveUp(String layername) {
        int i = layerReihenfolge.indexOf(layername);
        layerReihenfolge.remove(layername);
        layerReihenfolge.add(i + 1, layername);
    }

    /**
     * Lowers the position of the passed in Layer by one
     *
     * @param layer The name of the Layer.
     */
    public void moveDown(String layername) {
        int i = layerReihenfolge.indexOf(layername);
        layerReihenfolge.remove(layername);
        layerReihenfolge.add(i - 1, layername);
    }

    public void drawQuadTree(String layername, boolean mode) {
        layerMap.get(layername).drawQuadTree = mode;
    }

    public void setRand(double r) {
        this.rand = r;
    }

    public void setUpdateBounds(boolean update) {
        this.updateBounds = update;
    }

    public boolean getUpdateBounds() {
        return updateBounds;
    }

    /**
     * F�gt das Shape s dem Shapevector des Layers mit dem �bergeben Namen hinzu
     *
     * @param s
     * @param layername
     */
    public boolean addShape(Shape s, String layername) {

        Layer sv = layerMap.get(layername);
        boolean added = sv.add(s);
        if (updateBounds) {
            extendBounds(s);
        }
        firePropertyChange("Canvas2D.addShape", false, true);
        return added;
    }

    public boolean addShape(Shape s) {
        Layer sv = layerMap.get("default");
        boolean added = sv.add(s);
        if (updateBounds) {
            extendBounds(s);
        }
        firePropertyChange("Canvas2D.addShape", false, true);
        return added;
    }

    public void addShape(Collection<Shape> s, String layername) {
        Layer sv = layerMap.get(layername);
        Iterator<Shape> it = s.iterator();
        while (it.hasNext()) {
            Shape tmp = it.next();
            sv.add(tmp);
            if (updateBounds) {
                extendBounds(tmp);
            }
        }
        firePropertyChange("Canvas2D.addShape", false, true);
    }

    public void addShape(Shape[] s, String layername) {
        Layer sv = layerMap.get(layername);
        for (int i = 0; i < s.length; i++) {
            sv.add(s[i]);
            if (updateBounds) {
                extendBounds(s[i]);
            }
        }

        firePropertyChange("Canvas2D.addShape", false, true);
    }

    private void extendBounds(Shape s) {
        extendBounds(s.getBounds2D());
    }

    private void extendBounds(Rectangle2D rect) {
        if (bounds == null) {
            bounds = new Rectangle2D.Double();
            bounds.setRect(rect);
            return;
        }

        if (!bounds.contains(rect)) {
            double minX = Math.min(bounds.getMinX(), rect.getMinX());
            double maxX = Math.max(bounds.getMaxX(), rect.getMaxX());
            double minY = Math.min(bounds.getMinY(), rect.getMinY());
            double maxY = Math.max(bounds.getMaxY(), rect.getMaxY());
            bounds.setRect(minX, minY, maxX - minX, maxY - minY);
        }
    }

    public void validateBounds() {
        bounds = null;
        for (Layer l : layerMap.values()) {
            if (l.visible && l.bounds != null) {
                extendBounds(l.bounds);
            }
        }
    }

    @Override
    public synchronized void paintComponent(Graphics g) {

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;

        AffineTransform screen = g2.getTransform();

        if (transX == Double.MAX_VALUE || transY == Double.MAX_VALUE) {
            zoomFit();
        }

        AffineTransform at = new AffineTransform();
        at.translate(transX, transY);
        at.scale(zoom, -zoom);

        for (String s : layerReihenfolge) {
            Layer sv = layerMap.get(s);
            if (sv.bounds != null && sv.visible == true) {

                ArrayList<Shape> a = sv.getShapes();
                for (int i = 0; i < a.size(); i++) {
                    if (a.get(i) instanceof GraphicShape) {
                        GraphicShape shape = (GraphicShape) a.get(i);
                        shape.draw(g2, at);
                    } else {
                        g2.setColor(Color.BLACK);
                        g2.draw(a.get(i));
                    }
                }
                if (sv.drawQuadTree) {
                    g2.setColor(Color.GREEN);
                    sv.quad.draw(g2);
                }

            }

        }

        // Graphics2D-Objekt anpassen fuer RulerComponents
        if (showRuler_x || showRuler_y) {
            g2.setTransform(screen);
            Rectangle2D worldbounds = getVisibleWorldBounds();

            if (showRuler_x) {
                ruler_x.setSize(this.getSize());
                ruler_x.setMinMax(worldbounds.getMinX(), worldbounds.getMaxX());
            }
            if (showRuler_y) {
                ruler_y.setSize(this.getSize());
                ruler_y.setMinMax(worldbounds.getMinY(), worldbounds.getMaxY());
            }
        }

    }

    /**
     * Stellt die Zoomstufe und die Translation so ein, das sämtliche in den
     * Shapelayern befindliche Shapes dargestellt werden.
     */
    public void zoomFit() {
        try {
            zoom = Math.min((this.getWidth() - 2 * rand) / (bounds.getWidth()), (this.getHeight() - 2 * rand) / (bounds.getHeight()));
            transY = getHeight() + ((bounds.getCenterY() * zoom) - this.getHeight() / 2);
            transX = -((bounds.getCenterX() * zoom) - this.getWidth() / 2);
            zoom(-0.1);
            repaint();
        } catch (Exception e) {
        }
    }

    public void zoomFitMin() {
        zoom = Math.max((this.getWidth() - 2 * rand) / (bounds.getWidth()), (this.getHeight() - 2 * rand) / (bounds.getHeight()));
        transY = getHeight() + ((bounds.getCenterY() * zoom) - this.getHeight() / 2);
        transX = -((bounds.getCenterX() * zoom) - this.getWidth() / 2);
        repaint();
    }

    public void zoom(double step) {
        step = zoom * step;
        double mx = (-transX + this.getWidth() / 2) / zoom;
        double my = (transY - this.getHeight() / 2) / zoom;
        zoom += step;
        centerOnPoint(mx, my);
        repaint();
    }

    /**
     * Ver�ndert die aktuelle Zoomstufe. Es stehen verschiedene Modi f�r diesen
     * Vorgang zur Verf�gung die �ber den Parameter Mode gewechselt werden
     * k�nnen
     *
     * @param mode
     * @param value
     */
    public void zoom(int mode, double value) {
        if (mode == ZOOM_MODE_RISE_PERCENTAGE) {
            value = zoom * value;
            double mx = (-transX + this.getWidth() / 2) / zoom;
            double my = (transY - this.getHeight() / 2) / zoom;
            zoom += value;
            centerOnPoint(mx, my);
        }
        repaint();
    }

    public void move(double x, double y) {
        transY += y;
        transX += x;
        repaint();
    }

    public void centerOnPoint(double x, double y) {
        transX = -(x * zoom - this.getWidth() / 2);
        transY = y * zoom + this.getHeight() / 2;
        repaint();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getTransX() {
        return transX;
    }

    public double getTransY() {
        return transY;
    }

    public Graphics2D getGraphics2D() {
        return (Graphics2D) this.getGraphics();
    }

    /**
     * Liefert die Boundingbox s�mtlicher Shapes der sichtbaren Layer
     *
     * @return
     */
    public Rectangle2D getAreaBounds() {
        return bounds;
    }

    /**
     * Liefert die Boundingbox s�mtlicher Shapes der sichtbaren Layer
     *
     * @return
     */
    public void setAreaBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }

    public void showLayer(String layer, boolean visible) {
        Layer l = layerMap.get(layer);
        if (l != null) {
            l.visible = visible;
        }
    }

    public void activateRulerX(boolean b) {
        if (showRuler_x == b) {
            return;
        }
        showRuler_x = b;
        if (showRuler_x) {
            ruler_x = new RulerComponent(RulerComponent.HORIZONTAL);
            add(ruler_x);
        } else {
            remove(ruler_x);
        }
    }

    public void activateRulerY(boolean b) {
        if (showRuler_y == b) {
            return;
        }
        showRuler_y = b;
        if (showRuler_y) {
            ruler_y = new RulerComponent(RulerComponent.VERTICAL);
            add(ruler_y);
        } else {
            remove(ruler_y);
        }
    }

    public RulerComponent getRuler_x() {
        return ruler_x;
    }

    public RulerComponent getRuler_y() {
        return ruler_y;
    }

    /**
     * Steuert die M�glichkeit zur Kontrolle der Darstellung mittels
     * Mauseingaben. MOUSE_MOVE: Bewegen des Darstellungsbereichs mittels
     * Mausklick. Mausklick zentriert die Darstellung auf den angeklickten Punkt
     * MOUSE_ZOOM: Aufziehen eines einzuzoomenden Bereichs mit der Maus.
     *
     * @param type Art der Mauskontrolle.
     * @param mode An oder aus.
     */
    public void activateMouseControl(boolean mode) {
        if (mode) {
            if (mouseListener_ZOOM01 == null) {
                mouseListener_ZOOM01 = new MouseListener_ZOOM01();
            }
            if (mouseListener_ZOOM02 == null) {
                mouseListener_ZOOM02 = new MouseListener_ZOOM02();
            }
            if (mouseListener_ZOOM03 == null) {
                mouseListener_ZOOM03 = new MouseListener_ZOOM03();
            }
            addMouseListener(mouseListener_ZOOM01);
        } else {
            removeMouseListener(mouseListener_ZOOM01);
        }
    }

    /**
     * Aktiviert die Kontrolle der Darstellungskomponente mittels
     * Tastatureingaben Nummernblock: Bewegung in alle richtungen, 5=ZoomFit,
     * +-=Zoom Richtungstasten: Bewegen
     *
     * @param mode
     */
    public void activateKeyboardControl(boolean mode) {
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap2 = this.getActionMap();
        if (mode) {
            inputMap.put(KeyStroke.getKeyStroke(107, 0), "zoomIn");
            scaleActions.add(new ScaleAction(ScaleAction.ZOOM_IN));
            actionMap2.put("zoomIn", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(109, 0), "zoomOut");
            scaleActions.add(new ScaleAction(ScaleAction.ZOOM_OUT));
            actionMap2.put("zoomOut", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(65368, 0), "zoomFit");
            scaleActions.add(new ScaleAction(ScaleAction.ZOOM_FIT));
            actionMap2.put("zoomFit", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(224, 0), "moveUp");
            inputMap.put(KeyStroke.getKeyStroke(38, 0), "moveUp");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_UP));
            actionMap2.put("moveUp", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(225, 0), "moveDown");
            inputMap.put(KeyStroke.getKeyStroke(40, 0), "moveDown");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_DOWN));
            actionMap2.put("moveDown", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(226, 0), "moveLeft");
            inputMap.put(KeyStroke.getKeyStroke(37, 0), "moveLeft");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_LEFT));
            actionMap2.put("moveLeft", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(227, 0), "moveRight");
            inputMap.put(KeyStroke.getKeyStroke(39, 0), "moveRight");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_RIGHT));
            actionMap2.put("moveRight", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(36, 0), "moveLeftUp");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_LEFTUP));
            actionMap2.put("moveLeftUp", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(33, 0), "moveRightUp");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_RIGHTUP));
            actionMap2.put("moveRightUp", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(35, 0), "moveLeftDown");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_LEFTDOWN));
            actionMap2.put("moveLeftDown", scaleActions.get(scaleActions.size() - 1));
            inputMap.put(KeyStroke.getKeyStroke(34, 0), "moveRightDown");
            scaleActions.add(new ScaleAction(ScaleAction.MOVE_RIGHTDOWN));
            actionMap2.put("moveRightDown", scaleActions.get(scaleActions.size() - 1));
        } else {
            inputMap.clear();
            scaleActions.clear();
            actionMap2.clear();
        }
    }

    public double getRand() {
        return rand;
    }

    public void showArea(Rectangle2D rect) {
        zoom = Math.min(this.getWidth() / rect.getWidth(), this.getHeight() / rect.getHeight());
        centerOnPoint(rect.getCenterX(), rect.getCenterY());
    }

    /**
     * Erster Mauslistener f�r das Zoomen mittels Mauseingaben
     *
     */
    private class MouseListener_ZOOM01 extends MouseAdapter {

        private Graphics g;
        private Point start;

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (start == null) {
                    start = e.getPoint();
                }
                g = getGraphics();
                mouseListener_ZOOM02.setStart(start);
                mouseListener_ZOOM02.setGraphics(g);
                addMouseMotionListener(mouseListener_ZOOM02);
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (start == null) {
                    start = e.getPoint();
                }
                g = getGraphics();
                mouseListener_ZOOM03.setGraphics(g);
                mouseListener_ZOOM03.setStart(start);
                addMouseMotionListener(mouseListener_ZOOM03);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                zoomFit();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                mouseListener_ZOOM02.setStart(null);
                mouseListener_ZOOM02.setLast(null);
                removeMouseMotionListener(mouseListener_ZOOM02);
                Point p = e.getPoint();
                int sx = Math.min(start.x, p.x);
                int sy = Math.max(start.y, p.y);
                int w = Math.abs(p.x - start.x);
                int h = Math.abs(p.y - start.y);
                if (w < 2 || h < 2) {
                    start = null;
                    return;
                }
                Rectangle2D rect = new Rectangle2D.Double((sx - transX) / zoom,
                        (transY - sy) / zoom, w / zoom, h / zoom);
                start = null;
                showArea(rect);
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                mouseListener_ZOOM03.setStart(null);
                mouseListener_ZOOM03.setLast(null);
                removeMouseMotionListener(mouseListener_ZOOM03);
                start = null;
            }
        }
    }

    /**
     * Zweiter Mauslistener f�r das Zoomen mittels Mauseingabe. Wird vom ersten
     * Mauslistener aktiviert.
     *
     *
     *
     */
    private class MouseListener_ZOOM02 extends MouseMotionAdapter {

        private Point start;
        private Point last;
        private Graphics g;

        @Override
        public void mouseDragged(MouseEvent e) {

            if (e.getButton() == MouseEvent.NOBUTTON) {
                if (last == null) {
                    last = start;
                } else {
                    int sx = Math.min(start.x, last.x);
                    int sy = Math.min(start.y, last.y);
                    g.setXORMode(new Color(255, 255, 255, 0));
                    g.setColor(Color.BLUE);

                    g.drawRect(sx, sy, Math.abs(last.x - start.x), Math.abs(last.y
                            - start.y));
                    last = e.getPoint();
                    sx = Math.min(start.x, last.x);
                    sy = Math.min(start.y, last.y);
                    g.drawRect(sx, sy, Math.abs(last.x - start.x), Math.abs(last.y
                            - start.y));
                }

            }
        }

        public void setGraphics(Graphics g) {
            this.g = g;
        }

        public void setLast(Point p) {
            this.last = p;
        }

        public void setStart(Point p) {
            this.start = p;
        }
    }

    private class MouseListener_ZOOM03 extends MouseMotionAdapter {

        private Graphics g;
        private Point start;
        private Point last;

        @Override
        public void mouseDragged(MouseEvent e) {

            if (e.getButton() == MouseEvent.NOBUTTON) {
                if (last == null) {
                    last = start;
                }
                g.setXORMode(new Color(255, 255, 255, 0));
                g.setColor(Color.BLUE);

                double xs = (start.x) / zoom;
                double ys = (start.y) / zoom;
                start = last;
                last = e.getPoint();
                double xl = (last.x) / zoom;
                double yl = (last.y) / zoom;
                move(xl - xs, yl - ys);
            }
        }

        public void setGraphics(Graphics g) {
            this.g = g;
        }

        public void setStart(Point start) {
            this.start = start;
        }

        public void setLast(Point last) {
            this.last = last;
        }
    }

    /**
     * Mauslistener f�r das Bewegen innerhalb der Darstellungsomponente mittles
     * Mauseingaben
     *
     *
     *
     */
    private class MouseListener_MOVE extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            Point p1 = e.getPoint();
            double x = (p1.x - transX) / zoom;
            double y = (transY - p1.y) / zoom;
            centerOnPoint(x, y);

        }
    }

    private class DefaultMouseMotionListener extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            for (String s : layerReihenfolge) {
                Layer sv = layerMap.get(s);
                if (sv.bounds != null && sv.visible == true) {
                    if (!sv.quadTreeManaged) {
                        for (Shape sh : sv.shapes) {
                            if (sh instanceof GraphicShape) {
                                GraphicShape gs = (GraphicShape) sh;
                                if (sh.contains(screen2world(e.getPoint()))) {
                                    if (!gs.isEntered) {
                                        gs.isEntered = true;
                                        ShapeEvent se = new ShapeEvent(gs, e, screen2world(e.getPoint()));
                                        for (int i = 0; i < shapeListener.size(); i++) {
                                            shapeListener.get(i).entered(se);
                                        }
                                    }
                                } else {
                                    if (gs.isEntered) {
                                        gs.isEntered = false;
                                        ShapeEvent se = new ShapeEvent(gs, e,
                                                screen2world(e.getPoint()));
                                        for (int i = 0; i < shapeListener.size(); i++) {
                                            shapeListener.get(i).exited(se);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class DefaultMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            ArrayList<Shape> shapes = getShapes(e.getPoint());

            // ggf. ShapeCollectionEvent feuern
            ShapeCollectionEvent tmpEvent = new ShapeCollectionEvent(e, screen2world(e.getPoint()));

            for (int i = 0; i < shapes.size(); i++) {
                if (shapes.get(i) instanceof GraphicShape) {
                    GraphicShape gs = (GraphicShape) shapes.get(i);

                    tmpEvent.addShape(gs);

                    ShapeEvent se = new ShapeEvent(gs, e, screen2world(e.getPoint()));
                    for (int j = 0; j < shapeListener.size(); j++) {
                        shapeListener.get(j).clicked(se);
                    }
                }
            }
        }
    }

    private ArrayList<Shape> getShapes(Point p) {
        ArrayList<Shape> result = new ArrayList<Shape>();
        for (String s : layerReihenfolge) {
            Layer sv = layerMap.get(s);
            if (!sv.quadTreeManaged) {
                if (sv.bounds != null && sv.visible == true) {
                    for (Shape sh : sv.shapes) {
                        if (sh.contains(screen2world(p))) {
                            result.add(sh);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Action f�r die Belegung der Keyboardtasten
     *
     *
     *
     */
    private class ScaleAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        public static final int MOVE_LEFTDOWN = 1;
        public static final int MOVE_DOWN = 2;
        public static final int MOVE_RIGHTDOWN = 3;
        public static final int MOVE_LEFT = 4;
        public static final int ZOOM_FIT = 5;
        public static final int MOVE_RIGHT = 6;
        public static final int MOVE_LEFTUP = 7;
        public static final int MOVE_UP = 8;
        public static final int MOVE_RIGHTUP = 9;
        public static final int ZOOM_IN = 10;
        public static final int ZOOM_OUT = 11;
        private int type;

        public ScaleAction(int type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e.getModifiers());
            System.out.println("type=" + type);
            if (type == MOVE_DOWN) {
                move(0.0, +5.0);
            } else if (type == MOVE_LEFT) {
                move(-5.0, 0.0);
            } else if (type == MOVE_RIGHT) {
                move(+5.0, 0.0);
            } else if (type == MOVE_UP) {
                move(0.0, -5.0);
            } else if (type == ZOOM_FIT) {
                zoomFit();
            } else if (type == ZOOM_IN) {
                zoom(0.1);
            } else if (type == ZOOM_OUT) {
                zoom(-0.1);
            } else if (type == MOVE_LEFTDOWN) {
                move(-5.0, +5.0);
            } else if (type == MOVE_LEFTUP) {
                move(-5.0, -5.0);
            } else if (type == MOVE_RIGHTDOWN) {
                move(+5.0, +5.0);
            } else if (type == MOVE_RIGHTUP) {
                move(+5.0, -5.0);
            }

        }
    }

    /**
     * Fensterlistener zur Reaktion auf Fenstergr��enver�nderung :) Bisher wird
     * auf eine ver�nderung der Fenstergr��e so reagiert, das ein ZoomFit
     * gemacht wird. Sch�ner w�re eine Darstellung des vorher gezeigten
     * Bereichs...
     *
     *
     *
     */
    private class FensterListener extends ComponentAdapter {

        @Override
        public void componentResized(ComponentEvent e) {

            zoomFit();

        }
    }

    public class Layer {

        public boolean drawQuadTree = false;
        public boolean visible;
        public boolean quadTreeManaged = false;
        public String name;
        public Color color = Color.BLACK;
        public ArrayList<Shape> shapes;
        public ShapeQuadTree<Shape> quad;
        public Rectangle2D bounds;

        public final class ShapeQuadTree<E extends Shape> implements Iterable<E> {

            public int cellCounter = 0;
            public int cellCounter2 = 0;

            private final class Cell {

                public final Rectangle2D.Double bounds;
                protected List<Cell> children = null;
                protected List<E> shapes;
                public final int depth;

                /**
                 * Koordinaten P(links unten), P(rechts oben)
                 */
                public Cell(double xmin, double ymin, double xmax, double ymax, int depth) {
                    bounds = new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);
                    this.depth = depth;
                    cellCounter++;
                }

                public boolean isLeaf() {
                    return children == null;
                }

                public boolean isEmptyLeaf() {
                    return children == null && shapes == null;
                }

                public boolean contains_geom(Rectangle2D rect) {
                    return bounds.contains(rect);
                }

                public boolean contains_geom(E s) {
                    return contains_geom(s.getBounds2D());
                }

                public boolean contains_elmt(E s) {
                    if (children != null) {
                        for (Cell l : children) {
                            if (l.contains_geom(s)) {
                                return l.contains_elmt(s);
                            }
                        }
                        for (Cell l : children) {
                            if (l.intersects(s)) {
                                return l.contains_elmt(s);
                            }
                        }
                    } else if (shapes != null) {
                        return shapes.contains(s);
                    }
                    return false;
                }

                public boolean intersects(E s) {
                    return s.intersects(bounds);

                }

                public boolean intersects(Rectangle2D rect) {
                    return rect.intersects(bounds);

                }

                public boolean addShape(E s) {
                    boolean added = false;
//                    if(!contains(s))
//                    	if(!intersects(s))throw new IllegalArgumentException("Shape not intersects Link");
                    if (children != null) {
                        for (Cell l : children) {
                            if (l.contains_geom(s)) {
                                added = l.addShape(s);
                                break;
                            }
                        }
                        if (!added) {
                            for (Cell l : children) {
                                if (l.intersects(s)) {
                                    added = added || l.addShape(s);
                                }
                            }

                        }
                        if (!added) {
                            throw new RuntimeException("No intersecting child found");
                        }
                    } else {
                        if (shapes == null) {
                            shapes = new ArrayList<E>(maximumNumberOfElmtsInCell / 2);
                        }
                        if (!shapes.contains(s)) {
                            if (shapes.size() == maximumNumberOfElmtsInCell) {
                                //System.out.println("Calling Refine");
                                refine();
                                return this.addShape(s);
                            }
                            //System.out.println("-- Shape s = " + s + "\n     added in: " + this);
                            return shapes.add(s);
                        } else {
                            return false;
                        }

                    }
                    return added;
                }

                /**
                 *
                 */
                private void refine() {
                    if (children != null) {
                        throw new RuntimeException("Darf nicht sein!");
                    }
                    children = new ArrayList<Cell>(4);
                    double xmin = bounds.getMinX(), xmax = bounds.getMaxX(),
                            ymin = bounds.getMinY(), ymax = bounds.getMaxY();
                    double xmid = xmin + (xmax - xmin) / 2.;
                    double ymid = ymin + (ymax - ymin) / 2.;

                    Cell nw = new Cell(xmin, ymid, xmid, ymax, depth + 1);
                    Cell ne = new Cell(xmid, ymid, xmax, ymax, depth + 1);
                    Cell sw = new Cell(xmin, ymin, xmid, ymid, depth + 1);
                    Cell se = new Cell(xmid, ymin, xmax, ymid, depth + 1);

                    children.add(nw);
                    children.add(ne);
                    children.add(sw);
                    children.add(se);

                    boolean added;
                    for (E s : shapes) {
                        added = false;
                        for (Cell cell : children) {
                            if (cell.contains_geom(s)) {
                                added = cell.addShape(s);
                                break;
                            }

                        }
                        if (!added) {
                            for (Cell cell : children) {
                                if (cell.intersects(s)) {
                                    added = added || cell.addShape(s);
                                }
                            }
                        }
                        if (!added) {
                            throw new RuntimeException("Shit happend!");
                        }

                    }

                    shapes = null;
                }

                public Collection<E> getShapes() {
                    Collection<E> result = new ArrayList<E>();
                    collectShapes(result);
                    return result;
                }

                public void collectShapes(Collection<E> col) {
                    if (shapes != null) {
                        col.addAll(shapes);

                    } else if (children != null) {
                        for (Cell l : children) {
                            l.collectShapes(col);
                        }
                    }
                }

                public void getShapes(List<E> list, Rectangle2D bounds) {
                    if (children != null) {
                        for (Cell l : children) {
                            if (l.contains_geom(bounds)) {
                                l.getShapes(list, bounds);
                            } else if (bounds.contains(l.bounds)) {
                                l.getShapes(list, bounds);
                            } else if (l.intersects(bounds)) {
                                l.getShapes(list, bounds);
                            }
                        }
                    } else if (shapes != null) {
                        for (E s : shapes) {
                            if (bounds.contains(s.getBounds2D())) {
                                list.add(s);
                            } else if (s.intersects(bounds)) {
                                list.add(s);
                            }
                        }
                    }
                }

                // no recoarsening implemented yet
                public boolean remove(E q) {
                    boolean removed = false;
                    if (children != null) {
                        for (Cell l : children) {
                            if (l.contains_geom(q)) {
                                removed = l.remove(q);
                                break;
                            }
                        }
                        if (!removed) {
                            for (Cell l : children) {
                                removed = removed || l.remove(q);
                            }
                        }
                    } else {
                        if (shapes != null) {
                            this.shapes.remove(q);

                        }
                    }
                    // for compiler
                    return removed;
                }

                @Override
                public String toString() {
                    StringBuffer buf = new StringBuffer();
                    buf.append("Link [");
                    buf.append(this.hashCode());
                    buf.append("]: (" + this.bounds.getMinX() + ", " + this.bounds.getMinY() + "), (" + this.bounds.getMaxX() + "," + this.bounds.getMaxY() + ")");
                    buf.append(", children:" + (children == null ? "null" : children.size()));
                    buf.append(", shapes:" + (shapes == null ? "null" : shapes.size()));
                    return buf.toString();
                }

                public void draw(Graphics2D g2d) {
                    g2d.draw(bounds);
                    if (children != null) {
                        for (Cell l : children) {
                            l.draw(g2d);
                        }
                    }
                }

                public Rectangle2D getBounds() {
                    return this.bounds;
                }
            }
            // END of inner class Link
            // -------------------------------------------------------------------------
            //  -------------------------------------------------------------------------
            //  -------------------------------------------------------------------------
            //  -------------------------------------------------------------------------
            protected Cell topLevelCell;
            protected int n;
            public double[] mins, maxs;
            private final int maximumNumberOfElmtsInCell;

            /**
             *
             */
            public ShapeQuadTree() {
                this(512);
            }

            public ShapeQuadTree(Rectangle2D bounds) {
                this(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX() - bounds.getMinX(), bounds.getMaxY() - bounds.getMinY(), 512);
            }

            public ShapeQuadTree(int elmtsPerCell) {
                this(-(Double.MAX_VALUE / 2. - 1.), -(Double.MAX_VALUE / 2. - 1.), (Double.MAX_VALUE / 2.), (Double.MAX_VALUE / 2.), elmtsPerCell);
            }

            public ShapeQuadTree(double xmin, double ymin, double xmax, double ymax) {
                this(xmin, ymin, xmax, ymax, 512);
            }

            public ShapeQuadTree(double xmin, double ymin, double xmax, double ymax, int elmtsPerCell) {
                topLevelCell = new Cell(xmin, ymin, xmax, ymax, 0);
                maximumNumberOfElmtsInCell = elmtsPerCell;
            }

            public boolean add(E p) {
                return this.addShape(p);
            }

            public boolean addShape(E p) {

                if (p == null) {
                    System.out.println("PQT.addPoint(NULL).");
                    return false;
                }

                if (topLevelCell.addShape(p)) {
                    n++;
                    return true;
                } else {
                    System.out.println("PQT.addPoint (" + p + ") - bereits vorhanden.");
                    return false;
                }
            }

            /**
             * Fuegt alle Punkte der Collection ins Grid
             */
            public void addAll(Collection<E> col) {
                for (E p : col) {
                    addShape(p);
                }
            }

            public Object remove(E q) {
                if (topLevelCell.remove(q)) {
                    n--;
                    return true;
                } else {
                    return false;
                }
            }

            public boolean contains(E q) {
                return topLevelCell.contains_elmt(q);
            }

            public int size() {
                return n;
            }

            public ArrayList<E> getShapes() {
                ArrayList<E> allShapes = new ArrayList<E>(n);
                topLevelCell.collectShapes(allShapes);
                return allShapes;
            }

            public List<E> getShapes(Rectangle2D bounds) {
                List<E> list = new ArrayList<E>();
                topLevelCell.getShapes(list, bounds);
                return list;
            }

            public Iterator<E> iterator() {
                return getShapes().iterator();
            }

            /**
             * Setzt den Wurzelknoten des Grids auf den ersten Knoten, der mehr
             * als einen Zweig hat, am dem Punkte haengen. (Dadurch werden die
             * Abmessungen des Grids von (0,0)-(MAX,MAX) auf realisitische Werte
             * gestutzt.)
             */
            public void chooseRoot() {
                Cell tmp = chooseRoot(topLevelCell);
                topLevelCell = tmp;
            }

            public Cell getRoot() {
                return topLevelCell;
            }

            public void draw(Graphics2D g2d) {
                topLevelCell.draw(g2d);
            }

            /*
             * Gibt den Knoten zurueck, der als erstes im Baum mehr als einen Zweig hat, an dem Punkte haengen.
             */
            protected Cell chooseRoot(Cell l) {

                if (l.children == null) {
                    return l;
                }

                int i = 0;
                Cell next = null;
                for (Cell child : l.children) {
                    if (!child.isEmptyLeaf()) {
                        i++;
                        next = child;
                    }
                    if (i > 1) {
                        return l;
                    }
                }
                return chooseRoot(next);
            }

            public void clear() {
                topLevelCell.children = null;
                topLevelCell.shapes = null;
            }
        }

        public Layer(String name) {
            this(name, false, null, 0);
        }

        public Layer(String name, Collection<Shape> col) {
            this(name, true, col, 512);
        }

        public Layer(String name, Collection<Shape> col, int maxNumberOfElmtsPerCell) {
            this(name, true, col, maxNumberOfElmtsPerCell);
        }

        public Layer(String name, boolean quadTreeManagement, Collection<Shape> col, int maxNumberOfElmtsPerCell) {
            this.quadTreeManaged = quadTreeManagement;
            if (quadTreeManaged) {
                double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE,
                        ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
                for (Shape s : col) {
                    Rectangle2D b = s.getBounds2D();
                    xmin = Math.min(xmin, b.getMinX());
                    xmax = Math.max(xmax, b.getMaxX());
                    ymin = Math.min(ymin, b.getMinY());
                    ymax = Math.max(ymax, b.getMaxY());
                }
                quad = new ShapeQuadTree<Shape>(xmin - 1, ymin - 1, xmax + 1, ymax + 1, maxNumberOfElmtsPerCell);
                quad.addAll(col);
                extendLayerBounds(new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin));
                extendBounds(this.bounds);
            } else {
                shapes = new ArrayList<Shape>();
                if (col != null) {
                    for (Shape s : col) {
                        this.add(s);
                    }
                    extendBounds(this.bounds);
                }
            }
            this.name = name;
            visible = true;
        }

        public Layer(String name, boolean quadTreeManagement, int maxNumberOfElmtsPerCell, double xmin, double xmax, double ymin, double ymax) {
            this.quadTreeManaged = quadTreeManagement;
            if (quadTreeManaged) {
                quad = new ShapeQuadTree<Shape>(xmin - 1, ymin - 1, xmax + 1, ymax + 1, maxNumberOfElmtsPerCell);
            } else {
                shapes = new ArrayList<Shape>();

            }
            this.name = name;
            visible = true;
        }

        public List<Shape> getVisibleShapes(Rectangle2D bounds) {
            if (quadTreeManaged) {
                return quad.getShapes(bounds);
            } else {
                return shapes;
            }
        }

        public void removeAllElements() {
            if (quadTreeManaged) {
                quad.clear();
            } else {
                shapes.clear();
            }
        }

        public Iterator<Shape> iterator() {
            if (quadTreeManaged) {
                return null; //TODO implement rastered
            } else {
                return shapes.iterator();
            }
        }

        public ArrayList<Shape> getShapes() {
            if (quadTreeManaged) {
                return quad.getShapes();
            } else {
                return shapes;
            }
        }

        public boolean add(Shape s) {
            boolean added = false;
            if (quadTreeManaged) {
                if (quad.getRoot().getBounds().contains(s.getBounds2D())) {
                    added = quad.add(s);
                } else {
                    throw new IllegalArgumentException("Shape outside bounds of QuadTree root");
                }
            } else {
                added = shapes.add(s);
            }
            extendLayerBounds(s);
            if (added) {
                if (s instanceof ObjectShape) {
                    ObjectShape os = (ObjectShape) s;
                    Canvas2D.this.objectToShapeMap.put(os.getObject(), s);
                }
            }
            return added;
        }

        public void removeShape(Shape s) {
            if (quadTreeManaged) {
                quad.remove(s);
            } else {
                shapes.remove(s);
            }
            bounds = null;
            for (Shape a : shapes) {
                this.extendLayerBounds(a);
            }
            if (s instanceof ObjectShape) {
                ObjectShape os = (ObjectShape) s;
                Canvas2D.this.objectToShapeMap.remove(os.getObject());
            }
        }

        public void removeAllShapes() {
            if (quadTreeManaged) {
                quad.clear();
            } else {
                shapes.clear();
            }
            bounds = null;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Layer) {
                Layer s = (Layer) o;
                if (s.name.equals(this.name)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        private void extendLayerBounds(Shape s) {
            Rectangle2D sb = s.getBounds2D();
            if (bounds == null) {
                bounds = new Rectangle2D.Double();
                bounds.setRect(sb);
                return;
            }
            if (!bounds.contains(sb)) {
                double minX = Math.min(bounds.getMinX(), sb.getMinX());
                double maxX = Math.max(bounds.getMaxX(), sb.getMaxX());
                double minY = Math.min(bounds.getMinY(), sb.getMinY());
                double maxY = Math.max(bounds.getMaxY(), sb.getMaxY());
                bounds.setRect(minX, minY, maxX - minX, maxY - minY);
            }
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public ArrayList<Layer> getVisibleLayers() {
        ArrayList<Layer> layers = new ArrayList<Layer>();
        for (String name : layerReihenfolge) {
            Layer l = layerMap.get(name);
            if (l.visible) {
                layers.add(l);
            }
        }
        return layers;
    }

    public ArrayList<Layer> getLayers() {
        ArrayList<Layer> layers = new ArrayList<Layer>();
        for (String name : layerReihenfolge) {
            Layer l = layerMap.get(name);
            layers.add(l);
        }
        return layers;
    }

    public ArrayList<Shape> getAllShapes() {
        ArrayList<Shape> allshapes = new ArrayList<Shape>();
        for (String name : layerReihenfolge) {
            Layer l = layerMap.get(name);
            if (l.visible) {
                allshapes.addAll(l.shapes);
            }
        }
        return allshapes;
    }

    protected Rectangle2D getVisibleWorldBounds() {
        Point2D min = screen2world(new Point(0, this.getHeight()));
        Point2D max = screen2world(new Point(this.getWidth(), 0));

        return new Rectangle2D.Double(min.getX(), min.getY(), max.getX() - min.getX(), max.getY() - min.getY());
    }

    public ArrayList<Shape> getShapes(String layer) {
        Layer l = layerMap.get(layer);
        if (l != null) {
            return l.shapes;
        }
        return null;
    }

    public Layer getLayer(String layer) {
        Layer l = layerMap.get(layer);
        if (l != null) {
            return l;
        }
        return null;
    }

    public void setVisible(String layer, boolean visible) {
        getLayer(layer).visible = visible;
    }

    public ArrayList<Shape> getVisibleShapes(Rectangle2D bounds) {
        ArrayList<Layer> visibleLayers = getVisibleLayers();
        ArrayList<Shape> res = new ArrayList<Shape>();
        for (Layer layer : visibleLayers) {
            for (Shape s : layer.getShapes()) {
                if (s.intersects(bounds)) {
                    res.add(s);
                }
            }
        }
        return res;
    }

    public Point2D.Double screen2world(Point p) {
        return new Point2D.Double((p.x - transX) / zoom, (transY - p.y) / zoom);
    }

    public Point2D.Double screen2world(Point2D.Double p) {
        return new Point2D.Double((p.x - transX) / zoom, (transY - p.y) / zoom);
    }

    void viewMetaInfo(GraphicShape gs) {
        this.setToolTipText(gs.toolTip);
    }

    void removeMetaInfo() {
        this.setToolTipText(null);
    }

    public static void main(String[] args) {

        JFrame f = new JFrame();
        f.setSize(1000, 500);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Komponenten werden im canvas dargestellt
        Canvas2D canvas = new Canvas2D();

        // Zoom per Maus aktivieren
        canvas.activateMouseControl(true);

        canvas.addShape(new Rectangle2D.Double(100, 1296678808100l, 80, 21600));
        canvas.activateRulerY(true);
        canvas.getRuler_y().setFontSize(12);
        canvas.getRuler_y().setRepresentsTime(true);

        f.add(canvas);
        f.setVisible(true);

    }
}
