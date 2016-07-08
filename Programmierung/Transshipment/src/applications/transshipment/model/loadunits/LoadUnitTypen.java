/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.loadunits;

/**
 *
 * @author dorow
 */
public enum LoadUnitTypen {

    Typ20(6.058, 2.440, 2.0),
    Typ30(9.125, 2.440, 2.0),
    Typ40(12.192, 2.440, 2.0),
    Undefied(0, 0, 0);
    public int fixtyp;
    public double length;
    public double width;
    public double height;

    private LoadUnitTypen(double maxlength, double maxwidth, double maxheight) {

        this.length = maxlength;
        this.width = maxwidth;
        this.height = maxheight;
    }
}
