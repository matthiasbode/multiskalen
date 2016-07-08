package bijava.geometry.dim2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * PolygonalRegion2d.java provides properties and methods for regions
 * in a twodimensional space.
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  Dipl.-Ing. Mario Hoecker
 * @version 1.0, December 2006
 */
public class PolygonalRegion2d implements Iterable<SimplePolygon2d>, Region2d {
    private ArrayList<SimplePolygon2d> polygons; // polygons in this region

    /**
     * Default-Konstruktor
     */
    public PolygonalRegion2d() {
        polygons = new ArrayList<SimplePolygon2d>();
    }

    public PolygonalRegion2d(SimplePolygon2d[] p) {
        polygons = new ArrayList<SimplePolygon2d>();
        for(int i=0;i<p.length;i++)
            polygons.add(p[i]);
    }
    /**
     * Creates a region in a twodimensional space.
     * @param polygon simple polygon.
     */
    public PolygonalRegion2d(SimplePolygon2d polygon) {
        polygons = new ArrayList<SimplePolygon2d>();
        polygons.add(polygon);
    }
    
    /**
     * Gets an iterator over the polygons in this region.
     * @return iterator over the polygons in this region.
     */
    public Iterator<SimplePolygon2d> iterator() {
        return polygons.iterator();
    }
    
    /**
     * Adds a polygon to this region if it's not present yet.
     * @param polygon simple polygon to add.
     */
    public void add(SimplePolygon2d polygon) {
        if (polygon == null)
            throw new NullPointerException();
        if (!polygons.contains(polygon))
            polygons.add(polygon);
    }
    
    /**
     * Removes a polygon from this set if it's present.
     * @param polygon simple polygon to remove.
     */
    public void remove(SimplePolygon2d polygon) {
        polygons.remove(polygon);
    }
    
    /**
     * Gets information if this region has an infinity dimension.
     * @return <code>true</code> if this region has an infinity dimension.
     */
    public boolean isInfinity() {
        // separates positive and negative oriented polygons
        ArrayList<SimplePolygon2d> pos = new ArrayList<SimplePolygon2d>();
        ArrayList<SimplePolygon2d> neg = new ArrayList<SimplePolygon2d>();
        for (SimplePolygon2d polygon : this) {
            if (polygon.isPositiveOriented()) pos.add(polygon);
            else neg.add(polygon);
        }
        
        // searches for a negative oriented polygon not inside a
        // positive oriented polygon
        for (SimplePolygon2d neg_polygon : neg) {
            boolean infinity = true;
            for (SimplePolygon2d pos_polygon : pos)
                if (pos_polygon.contains(neg_polygon)) {
                    infinity = false;
                    break;
                }
            if (infinity) return true;
        }
        return false;
    }
    
    /**
     * Tests if this region contains a point.
     * @param p point.
     * @return <code>true</code> if this region contains the point.
     */
    public boolean contains(Point2d p) {
        if (p == null) return false;
        int size = polygons.size();
        for (int i = size - 1; i > -1; i--) {
            SimplePolygon2d polygon = polygons.get(i);
            if (polygon.contains(p))
                return polygon.isPositiveOriented() ? true : false;
        }
        return this.isInfinity();
    }
    
    /**
     * Tests the equality to an other object.
     * @param object other object.
     * @return <code>true</code> if the other object is an instance
     *  of <code>PolygonalRegion2d</code> with equal polygons.
     */
    public boolean equals(Object object) {
        if (!(object instanceof PolygonalRegion2d))
            return false;
        PolygonalRegion2d region = (PolygonalRegion2d) object;
        if (polygons.size() != region.polygons.size())
            return false;
        for (SimplePolygon2d polygon : region)
            if (!polygons.contains(polygon))
                return false;
        return true;
    }
    
    /**
     * Returns the properties of this set as <code>String</code>.
     * @return properties of this set as <code>String</code>.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer("{");
        Iterator<SimplePolygon2d> it = polygons.iterator();
        if (it.hasNext()) buffer.append(it.next());
        for ( ; it.hasNext(); ) {
            buffer.append(", ");
            buffer.append(it.next());
        }
        buffer.append("}");
        return buffer.toString();
    }
    
    /**
     * Gets the polygons of this region as array.
     * @return polygons of this region as array.
     */
    public SimplePolygon2d[] getPolygons() {
        return polygons.toArray(new SimplePolygon2d[polygons.size()]);
    }
    
//------------------------------------------------------------------------------
    
   
}