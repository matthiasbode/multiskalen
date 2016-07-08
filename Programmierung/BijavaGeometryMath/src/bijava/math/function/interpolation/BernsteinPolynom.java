package bijava.math.function.interpolation;

/**
 *
 * @author Institute of Computer Science in Civil Engineering
 */
public class BernsteinPolynom extends Polynom {
        
        protected int n;        // Polynom-Ordnung
        protected int i;        // Polynom-Index
     
        private double chooseValue;    // Puffer fuer den berechneten Binomialkoeffizienten
      
        /** 
         * Beim Bernstein-Polynom macht die Berechnung klassischier
         * Koeffizienten a0...an keinen Sinn.  
         * Daher wird dem Superkonstruktor null uebergeben und 
         * die getValue-Methode redefiniert (ueberschrieben).
         */
        public BernsteinPolynom(int n, int i) {
               this.n = n;
               this.i = i;
               chooseValue = choose(n, i);
       }
       
    @Override
       public double getValue(double t) {
               return chooseValue * Math.pow(t, i) * Math.pow(1-t, n-i) ;
       }

       /** Berechnet (n ueber i) */
       private double choose(int n, int i) {
               if (n == 0 || i == 0 || i == n) return 1.;
               return choose(n-1, i-1) + choose(n-1, i);
       }
    
}
