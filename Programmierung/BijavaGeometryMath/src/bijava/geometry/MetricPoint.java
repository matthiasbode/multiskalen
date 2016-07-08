package bijava.geometry;

/** describe the propreties of a element in a metric space 
 * @author milbradt
 */
public interface MetricPoint<E extends MetricPoint> {
	public double distance(E y);
}
