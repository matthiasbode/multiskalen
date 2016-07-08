package bijava.graphics.canvas2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.*;
import java.io.Serializable;

public class GraphicShape implements Serializable, Shape {

    private static final long serialVersionUID = 1L;
    public String toolTip;
    protected Path2D gp;
    protected float transparency = 1.f;
    protected Color color = Color.BLACK;
    protected Color filledcolor = Color.LIGHT_GRAY;
    protected boolean filled = false;
    public boolean isEntered = false;

    public GraphicShape(Path2D gp) {
        this.gp = gp;
    }

    public GraphicShape(Shape s) {
        gp = new GeneralPath(s);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        color = new Color(color.getColorSpace(), color.getComponents(null), transparency);
    }

    public void setFilledColor(Color color) {
        this.filledcolor = color;
        filledcolor = new Color(filledcolor.getRed(), filledcolor.getGreen(), filledcolor.getBlue(), (int) (transparency * 255.));
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public void setTransparency(float t) {
        transparency = t;
        color = new Color(color.getColorSpace(), color.getComponents(null), t);
        filledcolor = new Color(filledcolor.getColorSpace(), filledcolor.getComponents(null), t);
    }

    public boolean isFilled() {
        return filled;
    }

    public void draw(Graphics2D g, AffineTransform at) {
        Area area = new Area(this);
        area.transform(at);
        
        if (filled) {

            g.setColor(filledcolor);
            g.fill(area);
        }

        g.setColor(color);
        g.draw(area);

    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public Rectangle getBounds() {
        return gp.getBounds();
    }

    public Rectangle2D getBounds2D() {
        return gp.getBounds2D();
    }

    public boolean contains(double x, double y) {
        return gp.contains(x, y);
    }

    public boolean contains(java.awt.geom.Point2D p) {
        return gp.contains(p);
    }

    public boolean intersects(double x, double y, double w, double h) {
        return gp.intersects(x, y, w, h);
    }

    public boolean intersects(Rectangle2D r) {
        return gp.intersects(r);
    }

    public boolean contains(double x, double y, double w, double h) {
        return gp.contains(x, y, w, h);
    }

    public boolean contains(Rectangle2D r) {
        return gp.contains(r);
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return gp.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return gp.getPathIterator(at, flatness);
    }

    /**
     * Returns, if
     * <code>obj</code> is equal to this. Two GraphicShapes are equal, if their
     * attributes are equal. For further information read the documentation of
     * the equals-implementation of
     * <code>Object</code>.
     *
     * @param obj The object, which is tested to be equal to this.
     * @return wheter obj is equal to this or not.
     * @see Object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;                          // not null
        }
        if (this == obj) {
            return true;                           // symmetry
        }
        if (!obj.getClass().equals(getClass())) {
            return false;   // condition for symmetry
        }
        GraphicShape gs = (GraphicShape) obj;
        return gp.equals(gs.gp) && // two GS are equal, if their attributes are equal
                toolTip.equals(gs.toolTip)
                && transparency == gs.transparency
                && color.equals(gs.color)
                && filledcolor.equals(gs.filledcolor)
                && filled == gs.filled
                && isEntered == gs.isEntered;
    }

    /**
     * Auto-implementation of hashCode by NetbeansIDE.
     *
     * @return the hash code of this.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.toolTip != null ? this.toolTip.hashCode() : 0);
        hash = 67 * hash + (this.gp != null ? this.gp.hashCode() : 0);
        hash = 67 * hash + Float.floatToIntBits(this.transparency);
        hash = 67 * hash + (this.color != null ? this.color.hashCode() : 0);
        hash = 67 * hash + (this.filledcolor != null ? this.filledcolor.hashCode() : 0);
        hash = 67 * hash + (this.filled ? 1 : 0);
        hash = 67 * hash + (this.isEntered ? 1 : 0);
        return hash;
    }
}
