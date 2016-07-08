package bijava.geometry;

import java.io.Serializable;

/** describe a point of a linear space
 *
 * @author milbradt
 * @param <E>
 */
public interface LinearPoint<E extends LinearPoint> extends Serializable {

    public E add(E point) throws FalseSpaceDimensionException;

    public E sub(E point) throws FalseSpaceDimensionException; // weg ?

    public E mult(double scalar);

//    public void scale(double scalar); //pendant zu mult()  
//
//    public void translate(E point) throws FalseSpaceDimensionException; //pendant zu add()  
}
