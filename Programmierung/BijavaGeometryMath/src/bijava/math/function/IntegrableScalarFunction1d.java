
package bijava.math.function;

/**
 *
 * @author milbradt
 */
public interface IntegrableScalarFunction1d extends ScalarFunction1d{
    public double getIntegral(double a, double b);
}
