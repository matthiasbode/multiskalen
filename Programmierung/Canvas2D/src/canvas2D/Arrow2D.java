/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package canvas2D;

/**
 *
 * @author bode
 */
 
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 *
 * @author Kai Steinborn copied from Arrowclass in bijava.graphics.shapes
 */
public class Arrow2D extends GraphicShape {

    private Point2D p1, p2;
    private float l;
    private float b;
    private int type;
    private double l2;
    private float width = 0.015f;

    public Arrow2D(Point2D p1, Point2D p2) {
        this(p1, p2, 15, 10, 1);
//        this(p1, p2, 75, 50, 0);
    }

    public Arrow2D(double x1, double y1, double x2, double y2) {
        this(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
    }

    public Arrow2D(double x1, double y1, double x2, double y2, float l, float b, int type) {
        this(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), l, b, type);
    }

    public Arrow2D(Point2D p1, Point2D p2, float l, float b, int typ) {
        super(new GeneralPath());
        this.p1 = p1;
        this.p2 = p2;
        this.l = l;
        this.b = b;
        this.type = typ;
        l2 = Math.sqrt(l * l + b * b);
        updatePath();
        this.stroke = new BasicStroke(1.4f);
    }

    public void setPoints(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
        updatePath();
    }

    public Point2D getP1() {
        return p1;
    }

    public Point2D getP2() {
        return p2;
    }

    public int getTyp() {
        return type;
    }

    public int getTipWidth() {
        return (int) b;
    }

    public int getTipLength() {
        return (int) l;
    }

    private void updatePath() {
        double w2 = Math.atan2(b / 2, l);

        float x0 = (float) p1.getX();
        float y0 = (float) p1.getY();
        float x1 = (float) p2.getX();
        float y1 = (float) p2.getY();

        double angle = Math.atan2(x1 - x0, y1 - y0);

        gp.reset();


        Point2d[] boxPoints = getBoxPoints(p1, p2, width);
        gp.moveTo(boxPoints[0].x, boxPoints[0].y);
        gp.lineTo(boxPoints[1].x, boxPoints[1].y);
        gp.lineTo(boxPoints[2].x, boxPoints[2].y);
        gp.lineTo(boxPoints[3].x, boxPoints[3].y);
        gp.lineTo(boxPoints[0].x, boxPoints[0].y);

        gp.moveTo((float) (x1 - l2 * Math.sin(angle + w2)), (float) (y1 - l2 * Math.cos(angle + w2)));
        gp.lineTo(x1, y1);
        gp.lineTo((float) (x1 - l2 * Math.sin(angle - w2)), (float) (y1 - l2 * Math.cos(angle - w2)));

        switch (type) {
            case 0:
                break;
            case 1:
                gp.closePath();
                break;
            case 2:
                gp.quadTo((float) (x1 - l2 / 2 * Math.sin(angle)),
                        (float) (y1 - l2 / 2 * Math.cos(angle)),
                        (float) (p2.getX() - l2 * Math.sin(angle + w2)),
                        (float) (p2.getY() - l2 * Math.cos(angle + w2)));
                gp.closePath();
                break;
        }
    }

    public Rectangle2D getBounds2D() {
        return gp.getBounds2D();
    }

    public boolean contains(double x, double y) {
        return gp.contains(x, y);
    }

    public boolean contains(Point2D p) {

        double abs1 = (p1.getX() - p.getX()) * (p1.getX() - p.getX()) + (p1.getY() - p.getY()) * (p1.getY() - p.getY());
        double abs2 = (p2.getX() - p.getX()) * (p2.getX() - p.getX()) + (p2.getY() - p.getY()) * (p2.getY() - p.getY());
        double abs = (p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY());
        if ((Math.sqrt(abs1) + Math.sqrt(abs2)) <= Math.sqrt(abs) + 1) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas can = new Canvas() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Arrow2D a = new Arrow2D(10, 10, 50, 40);
                Graphics2D g2 = (Graphics2D) g;
                g2.draw(a);
            }
        };
        f.add(can, BorderLayout.CENTER);

        f.setVisible(true);
    }

    public static  Point2d[] getBoxPoints(java.awt.geom.Point2D p1, java.awt.geom.Point2D p2, double breite) {
        return getBoxPoints(new Point2d(p1.getX(), p1.getY()), new Point2d(p2.getX(), p2.getY()), breite);
    }

    /**
     * Diese Methode gibt die Punkte der Box an, die um die Kante von p1 zu p2
     * entsteht, wenn man senkrecht zu dieser eine Kante um die Hälfte der
     * Breite nach oben und um die Hälfte der breite nach unten verschiebt. Die
     * Reihenfolge der Punkte ist dabei im Uhrzeigersinn, beginnent beim Knoten,
     * der durch die Addition der halben Senkrechten zum ersten Punkt entsteht,
     * gegeben.
     *
     * @param p1
     * @param p2
     * @return
     */
    public static Point2d[] getBoxPoints(Point2d p1, Point2d p2, double breite) {
        Vector2d vec = new Vector2d(p2.x - p1.x, p2.y - p1.y);
        vec.normalize();
        Vector2d normal = new Vector2d(-vec.y, vec.x);
        normal.scale(breite / 2.0);
        Point2d[] points = new Point2d[4];
        points[0] = new Point2d(p1);
        points[0].add(normal);
        points[3] = new Point2d(p1);
        points[3].sub(normal);
        points[1] = new Point2d(p2);
        points[1].add(normal);
        points[2] = new Point2d(p2);
        points[2].sub(normal);
        return points;
    }
}
