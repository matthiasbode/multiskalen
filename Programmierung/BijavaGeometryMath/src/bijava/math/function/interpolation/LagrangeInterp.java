package bijava.math.function.interpolation;

import bijava.math.function.AbstractScalarFunction1d;

/**
 * Diese Klasse ermoeglicht die Interpolation ueber eine 
 * gegebene Anzahl an Punkten mit Hilfe der Lagrangeschen Formel.
 * @url 
 * @author meriem
 *
 */
public class LagrangeInterp extends AbstractScalarFunction1d
{    
    /**
     * Speichert die Koordinaten der Punkte, ueber die man interpoliert.
     */
    private double [] coordinates;
    /**
     * Speichert die Funktionswerte der Punkte, ueber die man interpoliert.
     */
    private double [] functionValues;
  
    private double dmax=0;
    
    public LagrangeInterp(double[] coordinates, double[] functionValues) {
        
          this.coordinates=coordinates;
          this.functionValues=functionValues;  
          
          for(int i=0; i<this.coordinates.length-1;i++){
            dmax=Math.max(dmax,(this.coordinates[i+1]-this.coordinates[i]));
          }
          
          
    }
        
    /**
     * Diese Methoe interpoliert mit der Lagrangeschen Formel und 
     * gibt den interpolierenden Wert zurueck.
     * @param coordinate die Koordinate des Punkts.
     * @return den Funktionswert des Punktes.
     */
    public double getValue(double coordinate)
    {
      /**
      * Alle Iinterpolationspunkte werde geholt.
      */
      int numberOfElements = this.coordinates.length;
            
      /**
       * Die Summe aller Summanden der Langrangeschen Interpolationsformel.
       * Sie ist am Ende der Funktionswert.
       */
      double result = 0.0;
      if( this.coordinates.length == this.functionValues.length)
      {
          /**
           * Es wird ueber alle Punkte durchgegangen. Jeder Punkt hat einen Anteil. 
           */
          for( int counter = 0; counter < numberOfElements; counter++)
          {
              /**
               * Der Funktionswert des Punktes.
               */
              double pointFunctionValue = this.functionValues[counter];
              result += pointFunctionValue * (calculateCounter(counter, coordinate) / calculateDeminator(counter));
                         
          }
          
      }
      
      else throw new IllegalArgumentException("Die Anzahl der Koordinaten entspricht nicht der Anzahl der Funktionswerte"); 
          
      return result; 
      
    }
        
    public double getConfidenceValue(double coordinate) {
         
      for (int i=0; i<coordinates.length-1; i++){  
        
        if (coordinate == coordinates[i]) return 1.0;  
        if(coordinates[i] < coordinate && coordinates[i+1] > coordinate){
          double dmin=Math.min((coordinate-coordinates[i]),(coordinates[i+1]-coordinate));
          return 1-2*dmin/dmax; 
        }
      }      
      return 0;
    }
       
    /**
     * Berechnet fuer jeden Summanden den Nenner.
     * @param index der Index in der Summe.
     * @return den Nenner fuer den i-ten Summanden.
     */
    private double calculateDeminator(int index)
    {
        int numberOfPoints = this.coordinates.length;
        double result = 1.0;
        if( index < numberOfPoints && index >= 0)
        {
            double coordinate = this.coordinates[index];
            for( int counter = 0; counter < numberOfPoints; counter++)
            {
                if( counter != index)
                {
                    double coordinate2 = this.coordinates[counter] ;
                    result *= (coordinate- coordinate2);
                }
                
            }
                     
        }
        return result;
    }
    
    /**
     * Berechnet den Zaehler des i-ten Summanden.
     * @param index ist der Index des Summanden.
     * @param coordinate ist die Koordinate des zu interpolierden Punktes.
     * @return den Zaehler des i-ten Summanden.
     */
    private double calculateCounter(int index, double coordinate)
    {
        int numberOfPoints = this.coordinates.length;
        double result = 1.0;
        if( index < numberOfPoints && index >= 0)
        {
            for( int counter = 0; counter < numberOfPoints; counter++)
            {
                if( counter != index)
                {
                    double coordinate2 = this.coordinates[counter];
                    result *= (coordinate- coordinate2);
                }               
            }                   
        }
        return result;
    }
    /**
     * Gibt die Koordinaten der Stuetzstellen zurueck.
     * @return Returns die Koordinaten der Stuetzstellen.
     */
    public double[] getCoordinates() {
        return coordinates;
    }
    /**
     * Setzt die Koordinaten der Stuetzstellen.
     * @param coordinates die neuen Koordinaten der Stuetzstellen..
     */
    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
    /**
     * Gibt die Funktionswerte der Stuetzstellen.
     * @return Returns die Funktionswerte der Stuetzstellen.
     */
    public double[] getFunctionValues() {
        return functionValues;
    }
    /**
     * Setzt die Funktionswerte der Stuetzstellen.
     * @param functionValues die neuen Funktionswerte der Stuetzstellen..
     */
    public void setFunctionValues(double[] functionValues) {
        this.functionValues = functionValues;
    }
    

   public static void main(String[] args) {
 
   double[] x = {1,1.3,1.6,1.9,2.2};
   double[] y = {0.7651977,0.6200860,0.4554022,0.2818186,0.1103623};  
   //double[] x1 = {1,2,4,6};
   //double[] y1 = {1,4,16,36};  // f(x)=x^2;
   
   double[] x1 = {1,45,80,90};
   double[] y1 = {1,0.7071,0.1736,0};  // 
   
   LagrangeInterp lp = new LagrangeInterp(x1,y1);
   System.out.println(lp.getValue(140));
   System.out.println(lp.getConfidenceValue(140));
   
   System.out.println(lp.getValue(70));
   System.out.println(lp.getConfidenceValue(70));
   
   LagrangeInterp lp1 = new LagrangeInterp(x,y);
   //System.out.println(lp1.getValue(1.5));  // Loesung 0.5118277
     
  }		      
    
}
