package bijava.math.function;

//==========================================================================//
/**
 * The interface "AbstractInvertibleScalarFunction1d" provides methods for 1 dimensional
 *  scalar functions, that can build the inverse Function of itself.
 * 
 * @autor University of Hannover
 * @autor Institute of Computer Science in Civil Engineering
 * @autor Dr.-Ing. Tobias Pick
 */
//==========================================================================//
public abstract class AbstractInvertibleScalarFunction1d implements InvertibleScalarFunction1d {
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */
    @Override
    public abstract double getValue(double x);
    @Override
    public abstract double getInverseValue(double y);
    
    
    public final AbstractInvertibleScalarFunction1d getInverseFunction() {
    	return new AbstractInvertibleScalarFunction1d() {
          
            @Override
          public double getValue(double x) {
              return AbstractInvertibleScalarFunction1d.this.getInverseValue(x);
          }
          
            @Override
          public double getInverseValue(double x) {
            return AbstractInvertibleScalarFunction1d.this.getValue(x);
        }
      };
    }
}
