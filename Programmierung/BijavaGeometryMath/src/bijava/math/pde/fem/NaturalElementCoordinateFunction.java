package bijava.math.pde.fem;

import bijava.geometry.*;
import bijava.geometry.dimN.PointNd;
import bijava.math.function.AbstractDifferentialFunctionNd;

/**
 *
 * @author milbradt
 */
public abstract class NaturalElementCoordinateFunction extends AbstractDifferentialFunctionNd {

    private NaturalElement element; //Referenz auf das geometrische Element, todo geomele
    private PointNd coordinate; //zugeordnete Koordinate

    protected NaturalElementCoordinateFunction() {
    }

    protected NaturalElementCoordinateFunction(NaturalElement element, PointNd coordinate) {
        this.element = element;
        this.coordinate = coordinate;
    }

    public NaturalElement getElement() {
        return element;
    }

    public void setElement(NaturalElement e) {
        this.element = e;
    }

    public PointNd getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(PointNd p) {
        this.coordinate = p;
    }

    @Override
    public int getDim() {
        return coordinate.dim();
    }
}
