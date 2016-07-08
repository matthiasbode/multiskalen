package bijava.geometry;

/** describe the propperties of Points of an euclidian space and guarantee that the induction of metric norm from the scalar product is true
 *
 * @author milbradt
 */
public abstract class AbstractEuclidianPoint implements EuclideanPoint{
    @Override
    public abstract double scalarProduct(ScalarProduct point);
    public abstract EuclideanPoint sub(VectorPoint point);
    @Override
    public double norm(){
        return Math.sqrt(this.scalarProduct(this));
    }
    public double distance(EuclideanPoint p){
        return this.sub(p).norm();
    }
}
