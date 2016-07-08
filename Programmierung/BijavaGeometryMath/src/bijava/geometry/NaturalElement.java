package bijava.geometry;

import bijava.math.pde.fem.NaturalElementCoordinateFunction;


/**
 * The Interface NaturalElement describe a geometrical Element, wich has a local coordinate system (the natural element coordinates)
 * @author  Peter Milbradt
 * @version 1.0
 */

public interface NaturalElement {
    
    public double[] getNaturalElementCoordinates(EuclideanPoint p);

    public NaturalElementCoordinateFunction[] getLocalCoordinateFunction();
    
    public boolean contains(EuclideanPoint p); 
}
