package bijava.geometry.dim2;

import java.util.Collection;

/**
 * BoundingBox2d.java provides methods for a twodimensional bounding box.
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dipl.-Ing. Mario Hoecker<br>
 *  Dipl.-Ing. Kai Kaapke<br>
 *  M.Sc. Dipl.-Oz. C. Dorow
 * @version 1.1, october 2006
 */
public class BoundingBox2d implements Cloneable, Region2d {
    
    protected Point2d pmin; // the point with minimal coordinates
    protected double width, height; // width and height
    
    /**
     * Creates a twodimensional bounding box.
     * If the width or the height is less than zero, the method throws an IllegalArgumentException.
     * @param pmin point with minimal coordinates.
     * @param width width.
     * @param height height.
     */
    public BoundingBox2d(Point2d pmin, double width, double height) {
        if (width < 0.) throw new IllegalArgumentException("width < 0");
        if (height < 0.) throw new IllegalArgumentException("height < 0");
        this.pmin = pmin;
        this.width = width;
        this.height = height;
    }
    
    public BoundingBox2d(double xmin, double xmax, double ymin, double ymax) {
        if (xmax < xmin) throw new IllegalArgumentException("xmax < xmin");
        if (ymax < ymin) throw new IllegalArgumentException("ymax < ymin");
        this.pmin = new Point2d(xmin,ymin);
        width = xmax - pmin.x;
        height = ymax - pmin.y;
    }
    
    /**
     * Creates a twodimensional bounding box.
     * If the width or the height is less than zero, the method throws an IllegalArgumentException.
     * @param pmin point with minimal coordinates.
     * @param pmax point with maximal coordinates.
     */
    public BoundingBox2d(Point2d pmin, Point2d pmax) {
        if (pmin.x > pmax.x) throw new IllegalArgumentException("width < 0");
        if (pmin.y > pmax.y) throw new IllegalArgumentException("height < 0");
        this.pmin = pmin;
        width = pmax.x - pmin.x;
        height = pmax.y - pmin.y;
    }
    
    /**
     * Creates a twodimensional bounding box.
     * @param p array of points.
     */
    public BoundingBox2d(Point2d[] p) {
        double xmin, xmax, ymin, ymax;
        xmin = p[0].x;
        xmax = p[0].x;
        ymin = p[0].y;
        ymax = p[0].y;
        
        for(int i = 1; i < p.length; i++){
            if (p[i].x < xmin) xmin = p[i].x;
            if (p[i].x > xmax) xmax = p[i].x;
            if (p[i].y < ymin) ymin = p[i].y;
            if (p[i].y > ymax) ymax = p[i].y;
        }
        
        pmin = new Point2d(xmin, ymin);
        width = xmax - xmin;
        height = ymax - ymin;
    }
    
    /**
     * Creates a twodimensional bounding box.
     * @param p array of points.
     */
    public BoundingBox2d(Collection<? extends Point2d> coll) {
        double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY; 
        double ymin = Double.POSITIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;
        
        for(Point2d p : coll){
            if (p.x < xmin) xmin = p.x;
            if (p.x > xmax) xmax = p.x;
            if (p.y < ymin) ymin = p.y;
            if (p.y > ymax) ymax = p.y;
        }
        
        pmin = new Point2d(xmin, ymin);
        width = xmax - xmin;
        height = ymax - ymin;
    }
    
    /**
     * Gets the point with minimal coordinates of this bounding box..
     * @return point with minimal coordinates.
     */
    public Point2d getP0() {
        return pmin;
    }
    
    /**
     * Gets the point with maximal x-coordinate and minimal y-coordinate of this bounding box.
     * @return point with maximal x-coordinate and minimal y-coordinate.
     */
    public Point2d getP1() {
        return new Point2d(pmin.x + width, pmin.y);
    }
    
    /**
     * Gets the point with maximal coordinates of this bounding box.
     * @return point with maximal coordinates.
     */
    public Point2d getP2() {
        return new Point2d(pmin.x + width, pmin.y + height);
    }
    
    /**
     * Gets the point with minimal x-coordinate and maximal y-coordinate of this bounding box.
     * @return point with minimal x-coordinate and maximal y-coordinate.
     */
    public Point2d getP3() {
        return new Point2d(pmin.x, pmin.y + height);
    }
    
    /**
     * Gets the width of this bounding box.
     * @return width.
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * Gets the height of this bounding box.
     * @return height.
     */
    public double getHeight() {
        return height;
    }
    
    /**
     * Gets the minimal x-coordinate of this bounding box.
     * @return minimal x-coordinate.
     */
    public double getXmin() {
        return pmin.x;
    }
    
    /**
     * Gets the maximal x-coordinate of this bounding box.
     * @return maximal x-coordinate.
     */
    public double getXmax() {
        return pmin.x + width;
    }
    
    /**
     * Gets the minimal y-coordinate of this bounding box.
     * @return minimal y-coordinate.
     */
    public double getYmin() {
        return pmin.y;
    }
    
    /**
     * Gets the maximal y-coordinate of this bounding box.
     * @return maximal y-coordinate.
     */
    public double getYmax() {
        return pmin.y + height;
    }
    
    /**
     * Sets and validates the dimensions of this bounding box.
     * @param pmin point with minimal coordinates.
     * @param width width.
     * @param height height.
     */
    public void set(Point2d pmin, double width, double height) {
        this.pmin = pmin;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Sets the width of this bounding box.
     * @param width width.
     */
    public void setWidth(double width) {
        this.width = width;
    }
    
    /**
     * Sets the height of this bounding box.
     * @param height height.
     */
    public void setHeight(double height) {
        this.height = height;
    }
    
    /**
     * Sets the minimal x-coordinate of this bounding box.
     * The width of this bounding box is modified correspondingly.
     * @param xmin minimal x-coordinate.
     */
    public void setXmin(double xmin) {
        double xmax = pmin.x + width;
        if (xmax - xmin < 0) throw new IllegalArgumentException("invalid width");
        pmin.x = xmin;
        width = xmax - pmin.x;
    }
    
    /**
     * Sets the maximal x-coordinate of this bounding box.
     * The width of this bounding box is modified correspondingly.
     * @param xmin maximal x-coordinate.
     */
    public void setXmax(double xmax) {
        if (xmax - pmin.x < 0) throw new IllegalArgumentException("invalid width");
        width = xmax - pmin.x;
    }
    
    /**
     * Sets the minimal y-coordinate of this bounding box.
     * The height of this bounding box is modified correspondingly.
     * @param ymin minimal y-coordinate.
     */
    public void setYmin(double ymin) {
        double ymax = pmin.y + height;
        if (ymax - ymin < 0) throw new IllegalArgumentException("invalid height");
        pmin.y = ymin;
        height = ymax - pmin.y;
    }
    
    /**
     * Sets the maximal y-coordinate of this bounding box.
     * The height of this bounding box is modified correspondingly.
     * @param ymin maximal y-coordinate.
     */
    public void setYmax(double ymax) {
        if (ymax - pmin.y < 0) throw new IllegalArgumentException("invalid height");
        height = ymax - pmin.y;
    }
    
    /**
     * Overwrites the coordinates. No error handling or integrity checks are added, so only use for fast modification.
     * @param xmin
     * @param ymin
     * @param xmax
     * @param ymax
     */
    public void setValues(final double xmin, final double ymin, final double xmax, final double ymax) {
        this.pmin.x = xmin;
        this.pmin.y = ymin;
        this.width = xmax - xmin;
        this.height = ymax - ymin;
    }
    /**
     * Gets the area of this bounding box.
     * @return area of this bounding box.
     */
    public double getArea() {
        return width * height;
    }
    
    /**
     * Gets the centroid of this bounding box.
     * @return centroid of this bounding box.
     */
    public Point2d getCentroid() {
        return new Point2d(pmin.x + width / 2., pmin.y + height / 2.);
    }
    
    /**
     * Tests if this bounding box contains a point.
     * @param x x-coordinate of the point.
     * @param y y-coordinate of the point.
     * @return <code>true</code> if this bounding box contains the point.
     */
    public boolean contains(double x, double y) {
        if (x < pmin.x) return false;
        if (x > pmin.x + width) return false;
        if (y < pmin.y) return false;
        if (y > pmin.y + height) return false;
        return true;
    }
    
    /**
     * Tests if this bounding box contains a point.
     * @param p twodimensional point.
     * @return <code>true</code> if this bounding box contains the point.
     */
    public boolean contains(Point2d p) {
        if (p == null) return false;
        return this.contains(p.x, p.y);
    }
    
    /**
     * Tests whether the given bounding box interferes with this bounding box.
     * @param b twodimensional bounding box.
     * @return <code>true</code> if the given bounding box interferes with this bounding box.
     */
    public boolean interferes(BoundingBox2d b) {
        if (b == this) return true;
        if (b == null) return false;
        // tests the distance of the center points
        Point2d this_c = this.getCentroid(), b_c = b.getCentroid();
        double dx = Math.abs(b_c.x - this_c.x), dy = Math.abs(b_c.y - this_c.y);
        double width_mid = (width + b.width) / 2., height_mid = (height + b.height) / 2.;
        return dx < width_mid && dy < height_mid;
    }

    public SimplePolygon2d toSimplePolygon2d() {
        return new SimplePolygon2d(new Point2d[]{getP0(),getP1(),getP2(),getP3()});
    }
    
    
    /**
     * Gets the union of this bounding box with an other bounding box.
     * @param b twodimensional bounding box.
     * qreturn union of this bounding box with <code>other</code>.
     */
    public BoundingBox2d union(BoundingBox2d b) {
       // if (b == this || b == null) return new BoundingBox2d(this);
        
        if (b == this || b == null) return this.clone();
        double xmin = pmin.x < b.pmin.x ? pmin.x : b.pmin.x;
        double ymin = pmin.y < b.pmin.y ? pmin.y : b.pmin.y;
        double xmax = this.getXmax() < b.getXmax() ? b.getXmax() : this.getXmax();
        double ymax = this.getYmax() < b.getYmax() ? b.getYmax() : this.getYmax();
        
        return new BoundingBox2d(new Point2d(xmin, ymin), (xmax - xmin), (ymax - ymin));
    }
    
    /**
     * Gets the intersection of this bounding box with an other bounding box.
     * @param b twodimensional bounding box.
     * @return intersection of this bounding box with <code>other</code>.
     */
    public BoundingBox2d intersection(BoundingBox2d b) {
        //if (b == this) return new BoundingBox2d(this);
        if (b == this) return this.clone();
        if (b == null) return null;
        
        BoundingBox2d result = null;
        
        if (this.interferes(b)) {
            System.out.println("Hallo");
            double xmin = pmin.x > b.pmin.x ? pmin.x : b.pmin.x;
            double ymin = pmin.y > b.pmin.y ? pmin.y : b.pmin.y;
            double xmax = this.getXmax() > b.getXmax() ? b.getXmax() : this.getXmax();
            double ymax = this.getYmax() > b.getYmax() ? b.getYmax() : this.getYmax();
            
            result = new BoundingBox2d(new Point2d(xmin, ymin), (xmax - xmin), (ymax - ymin));
        }
        
        return result;
    }
    
    /**
     * Tests this bounding box on equality with other object.
     * @param o object.
     * @return <code>true</code> if this bounding box is equal with other object.
     */
    public boolean equals(Object o) {
        if(o instanceof BoundingBox2d) return (this.equals((BoundingBox2d) o));
        return false;
    }
    
    /**
     * Tests this bounding box on equality with other bounding box.
     * @param b twodimensional bounding box.
     * @return <code>true</code> if this bounding box is equal with other bounding box.
     */
    public boolean equals(BoundingBox2d b) {
        if (b == this) return true;
        if (b == null) return false;
        return this.pmin.equals(b.pmin) && this.width == b.width && this.height == b.height;
    }
    
    /**
     * Gets the clone of this bounding box
     * @return copy of this bounding box 
     */
    public BoundingBox2d clone(){
       Point2d p = new Point2d(pmin); 
       return new BoundingBox2d(p, width, height);
    }
    
    
    /**
     * Returns the attributes of this bounding box as string.
     * @return attributes of this bounding box as string.
     */
    public String toString() {
        return "xmin = " + pmin.x + ", ymin = " + pmin.y + ", width = " + ( width) + ", height = " + ( height);
    }
    
}