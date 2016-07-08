/*
 * IntegrableScalarFunction2d.java
 *
 * Created on 17. Februar 2006, 16:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.function;

import bijava.geometry.dim2.SimplePolygon2d;

/**
 *
 * @author Peter Milbradt
 */
public interface IntegrableScalarFunction2d {
    public double getIntegral(SimplePolygon2d polygon);
}
