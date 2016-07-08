package bijava.math.function;
//==========================================================================//
/** The class "ConstantFunction1d" provides proverties and methods for
 *  constant one dimensional scalar functions.
 *
 *  ------------------------------------------------
 *  Funktionen
 *  - setPeriodic(boolean): void
 *  - isPeriodic(): boolean
 *  entfernt. Stattdessen PeriodicalScalarFunction1d
 *  von ScalarFunction1d ableiten.
 *  Berthold, 31.05.06
 *  ------------------------------------------------
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, January 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public class ConstantFunction1d extends AbstractDifferentialFunction1d
{ double value;

//--------------------------------------------------------------------------//
/** Creates a constant one dimensional scalar function.
 *
 *  @param value This is the value of each argument.                        */
//--------------------------------------------------------------------------//
  public ConstantFunction1d(double value) { this.value = value; }

//--------------------------------------------------------------------------//
/** Tests the equality to another object.
 *
 *  @param object If this value is a <code>ConstantFunction1d</code> with the
 *                same value the method returns
 *                <code>true</code>, otherwise <code>false</code>.          */
//--------------------------------------------------------------------------//
  public synchronized boolean equals (Object object)
  { if (!(object instanceof ConstantFunction1d)) return false;
    return (value == ((ConstantFunction1d) object).value);
  }

//--------------------------------------------------------------------------//
/** Gets the value of an argument x.                                        */
//--------------------------------------------------------------------------//
  public double getValue(double x) { return value; }

//--------------------------------------------------------------------------//
/** Gets the derivation of an argument x.
 *
 *  @return The derivation of a constant function is always 0.0.            */
//--------------------------------------------------------------------------//
  public double getGradient(double x) { return 0.0; }
}
