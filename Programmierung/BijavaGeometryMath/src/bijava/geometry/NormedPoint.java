package bijava.geometry;

/** 
 + A Point in a linear normed Space
 * @author milbradt
 */
public interface NormedPoint<E extends NormedPoint> extends VectorPoint<E> {

    public double norm();
}
