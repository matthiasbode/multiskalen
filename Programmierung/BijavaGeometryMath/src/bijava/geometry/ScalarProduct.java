package bijava.geometry;

/** 
 *  A Point in a Space with scalarproduct
 * @author milbradt
 */
public interface ScalarProduct<E extends ScalarProduct> extends VectorPoint<E> {

    public double scalarProduct(E point) throws FalseSpaceDimensionException;
}
