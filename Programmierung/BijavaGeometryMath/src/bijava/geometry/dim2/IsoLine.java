/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bijava.geometry.dim2;

import java.util.List;

/**
 *
 * @author dorow
 */
public class IsoLine extends Polygon2d{

    double isoval;
    /**
     * Constructs a twodimensional po from three points p0, p1, p2 and a isovalue.
     * 
     * @param p0 1st point.
     * @param p1 2nd point.
     * @param p2 3rd point.
     * @param isovalue 4rd double.
     * 
     */
    public IsoLine(Point2d p0, Point2d p1, Point2d p2,double isoval) {
        super(p0,p1,p2);
        this.isoval=isoval;
    }

    /**
     * Constructs a twodimensional po from an array of points pts.
     * 
     * @param pts array of points.
     * @param isovalue 
     */
    public IsoLine(Point2d[] pts, double isoval) {
        super(pts);
        this.isoval=isoval;
    }

    /**
     * Constructs a twodimensional po from a collection of points.
     * 
     * @param coll collection of points.
     * @param isovalue
     */
    public IsoLine(List<? extends Point2d> coll,double isoval) {
        super(coll);
        this.isoval=isoval;
    }
    
    public double getIsovalue(){
      return isoval;
    }
    
}
