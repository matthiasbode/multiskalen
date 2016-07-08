//==============================================================================//
/**
 * BijectiveScalarFunction1d.java
 *
 * Schnittstelle fuer eine bijektive scalar-Wertige Funktion (1d). Es handelt
 * sich um eine Erweiterung der Schnittstelle ScalarFunction1d, mit der
 * Forderung, dass die Funktion umkehrbar ist (d.h. die Inverse muss
 * existieren).
 *
 * Created on 21. Februar 2007, 15:07
 *
 * @author berthold
 */
//==============================================================================//
package bijava.math.function;

public interface BijectiveScalarFunction1d extends ScalarFunction1d {
//------------------------------------------------------------------------------//

//------------------------------------------------------------------------------//
    /**
     * Gibt die Inverse der Funktion zurueck.
     */
//------------------------------------------------------------------------------//
    public double getInverseFunctionValue(double fx);
}
