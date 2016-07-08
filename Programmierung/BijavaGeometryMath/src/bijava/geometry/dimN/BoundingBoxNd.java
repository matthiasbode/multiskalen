package bijava.geometry.dimN;

import java.util.*;
import java.awt.*;

/**
 * BoundingBoxNd.java provides methods for a N-dimensional bounding box.
 * @author Leibniz University of Hannover<br>
 *  Institute of Computer Science in Civil Engineering<br>
 *  M.Sc. Dipl.-Oz. C. Dorow
 * @version 1.1, october 2006
 */
public class BoundingBoxNd implements Cloneable{
    
    protected PointNd pmin; // the point left down of the bounding box
    protected double[] dimlength; // dimlength of the bounding box
 
    /**
     * Creates a N-dimensional bounding box.
     * If the dimlength is less than zero, the method throws an IllegalArgumentException.
     * @param pmin point with minimal coordinates.
     * @param pmax point with maximal coordinates.
     */
    public BoundingBoxNd(PointNd pmin, PointNd pmax) {
        
        if (pmin.dim() != pmax.dim()) throw new IllegalArgumentException("Dimensions not equal");        
        dimlength = new double[pmin.dim()];
        
        for(int i=0; i<pmin.getSize(); i++){
            
          if (pmin.getCoord(i) > pmax.getCoord(i)){
             throw new IllegalArgumentException("BoundingBoxNd: dimlength of coord " + i + " is lower 0");
          }    
          dimlength[i]=pmax.getCoord(i)- pmin.getCoord(i);
        }
        this.pmin = pmin;      
    }
   
     /**
     * Creates N-dimensional bounding box.
     * @param p array of points.
     */
    public BoundingBoxNd(PointNd[] p) {
      int dim=p[0].dim();
      double[] min = new double[dim]; 
      double[] max = new double[dim];
      dimlength = new double[dim];
      
      for(int i=0; i<dim; i++){
        min[i]=p[0].getCoord(i);  
        max[i]=p[0].getCoord(i);
        
        for (int j=1; j<p.length; j++){
          if (p[j].dim() != dim) throw new IllegalArgumentException("Dimensions not equal");        
 
          min[i]=Math.min(min[i],p[j].getCoord(i));       
          max[i]=Math.max(max[i],p[j].getCoord(i));
        } 
       
        dimlength[i]=max[i]- min[i];  
      }          
      pmin = new PointNd(min);
    }
    
     /**
     * Get the dimlength of the box.
     */   
    public double[] getDimlength() { return dimlength;}
            
     /**
     * Get the dimension of the box.
     */   
    public int getDim() { return pmin.dim();}
    
     /**
     * Gets the minimal point.
     * @return the minimal point of this bounding box.
     */
    public PointNd getMin() {
        return pmin;
    }    
            
        /**
     * Gets the maximal point 
     * @return the maximal point of this bounding box.
     */
    public PointNd getMax() {
        PointNd pmax = new PointNd(pmin.getSize());
        for (int i=0; i< pmax.getSize();i++){
          pmax.setCoord(i,(pmin.getCoord(i)+dimlength[i])); 
        }      
        return pmax;
    }            
                      
     /**
     * Gets the cornerpoints
     * return the cornerpoints
     */
    public PointNd[] getP() {
        int dim=pmin.dim();
        int np = (int) Math.pow(2,dim);
        PointNd[] v = new PointNd[np];
          
        for (int j=0;j<np;j++){
            v[j]= new PointNd(dim);
            for (int k=0;k<dim;k++){                            
              v[j].setCoord(k,((j & (int)Math.pow(2,k))!=0) ? this.getMax().getCoord(k): pmin.getCoord(k));             
            }  
        }
        
        return v;
    }   
    
     /**
     * Sets the minimal point.
     * The dimlength of the box is modified correspondingly.
     */
    public void setMin(PointNd pmin) {    	
    	PointNd pmax = this.getMax();
        double[] d = new double[pmin.dim()];
        
        if (pmin.dim() != pmax.dim()) throw new IllegalArgumentException("Dimensions not equal");
        for (int i=0;i<pmin.dim();i++){
          if (pmin.getCoord(i)>pmax.getCoord(i)) throw new IllegalArgumentException("dimlength < 0");
          d[i]=pmax.getCoord(i)- pmin.getCoord(i);
        }
        this.pmin = pmin;
        dimlength=d;
     }      
    
     /**
     * Sets the maximal point.
     * The dimlength of the box is modified correspondingly. 
     */
    public void setMax(PointNd pmax) {    	
        double[] d= new double[pmin.dim()];  
        if (pmin.dim() != pmax.dim()) throw new IllegalArgumentException("Dimensions not equal");
        for (int i=0;i<pmin.dim();i++){
          if (pmin.getCoord(i)>pmax.getCoord(i)) throw new IllegalArgumentException("dimlength < 0");
           d[i]=pmax.getCoord(i)- pmin.getCoord(i);
        }
        
        dimlength=d;  
    }      
    
   /**
     * Sets and validates the dimensions of this bounding box.
     * @param pmin point with minimal coordinates.
     * @param pmax point with maximal coordinates. 
     */
    public void set(PointNd pmin, PointNd pmax) {
        double[] d= new double[pmin.dim()];
        
        if (pmin.dim() != pmax.dim() || pmin.dim() != dimlength.length ) throw new IllegalArgumentException("Dimensions not equal");        
        
        for(int i=0; i<pmin.getSize(); i++){     
          if (pmin.getCoord(i) > pmax.getCoord(i)) throw new IllegalArgumentException("BoundingBoxNd: dimlength < 0");
          //d[i]=pmax.getCoord(i)- pmin.getCoord(i);
        }
        
        this.pmin = pmin;
        dimlength=d;
    }
    
     /**
     * Sets the dimlength.
     * The maximal point is modified correspondingly.
     * 
     */
    public void setDimlength(double[] dimlength) {    	
        
        if (pmin.dim() != dimlength.length) throw new IllegalArgumentException("Dimensions not equal");
        for (int i=0;i<pmin.dim();i++){
          if (dimlength[i]<0) throw new IllegalArgumentException("dimlength < 0");
        }
        
        this.dimlength=dimlength;  
    }      
    
     /**
     * Gets the volume of this bounding box.
     * @return volume of this bounding box.
     */
     public double getVolume(){
        double volume=dimlength[0];
        for (int i=1;i<pmin.dim();i++){
            volume*=dimlength[i];
        }
      return volume; 
    }
     
     /**
     * Gets the centroid of this bounding box.
     * @return centroid of this bounding box.
     */
     public PointNd getCentroid() {
        PointNd centroid=new PointNd(pmin.dim());
        centroid.setCoord(0, (pmin.getCoord(0) + dimlength[0] / 2.));
        
        for (int i=1;i<pmin.dim();i++){
             centroid.setCoord(i, (pmin.getCoord(i) + dimlength[i] / 2.));
        }
        return centroid;  
         
    }
     
   /**
     * Tests if this bounding box contains a point.
     * @param p N-dimensional point point.
     * @return <code>true</code> if this bounding box contains the point.
     */
    public boolean contains(PointNd p) {        
        boolean t=false;
        
        if (pmin.dim() != p.dim()) throw new IllegalArgumentException("Dimension nicht identisch");
        for (int i=0;i<pmin.dim();i++){
          if (p.getCoord(i)>=this.getMin().getCoord(i) && p.getCoord(i)<=this.getMax().getCoord(i)){
             t=true; 
          }
          else{
              t=false;
              break;
          }
        }
        return t;
    }
      
   
     
     /**
     * Tests whether the given bounding box interferes with this bounding box.
     * @param b N-dimensional bounding box.
     * @return <code>true</code> if the given bounding box interferes with this bounding box.
     */
    public boolean interferes(BoundingBoxNd b) {
        if (b == null) return false;
        if (this.pmin.dim() != b.pmin.dim()) throw new IllegalArgumentException("Dimensions not equal");
        if (b == this) return true;
        // tests the distance of the center points
        PointNd this_c = this.getCentroid(), b_c = b.getCentroid();
        PointNd dx = b_c.sub(this_c);
        
        PointNd dimlength_mid = new PointNd(dimlength);
        dimlength_mid.add(dimlength_mid.mult(0.5));
        
        boolean t=false;
        for(int i=0;i<pmin.dim();i++){
            if (Math.abs(dx.getCoord(i)) < dimlength_mid.getCoord(i)) t=true;
            else{
              t=false;
              break;
            }
        }      
        return t;
    }
    
      /**
     * Gets the union of this bounding box with an other bounding box.
     * @param b N-dimensional bounding box.
     * @return union of this bounding box with <code>other</code>.
     */
    public BoundingBoxNd union(BoundingBoxNd b) {
        if (b == null) return this.clone();
        if (b == this) return this.clone();
        if (this.pmin.dim() != b.pmin.dim()) throw new IllegalArgumentException("Dimensions not equal");
        
        PointNd min = new PointNd(pmin.dim());
        PointNd max = new PointNd(pmin.dim());
               
        for(int i=0;i<pmin.dim();i++){
            min.setCoord(i,(pmin.getCoord(i) < b.pmin.getCoord(i) ? pmin.getCoord(i) : b.pmin.getCoord(i)));
            max.setCoord(i,(this.getMax().getCoord(i) > b.getMax().getCoord(i) ? 
                            this.getMax().getCoord(i) : b.getMax().getCoord(i)));
        }
          
        return new BoundingBoxNd(min,max);
    }          
    
     /**
     * Gets the intersection of this bounding box with an other bounding box.
     * @param b N-dimensional bounding box.
     * @return intersection of this bounding box with <code>other</code>.
     */
    
    public BoundingBoxNd intersection(BoundingBoxNd b) {
        if (b == null) return null;
        if (this.pmin.dim() != b.pmin.dim()) throw new IllegalArgumentException("Dimensions not equal");
        if (b == this) return this.clone();
        
        BoundingBoxNd result = null;
        PointNd min = new PointNd(pmin.dim());
        PointNd max = new PointNd(pmin.dim());
        
        if (this.interferes(b)) {
          for(int i=0;i<pmin.dim();i++){
            min.setCoord(i,(pmin.getCoord(i) > b.pmin.getCoord(i) ? pmin.getCoord(i) : b.pmin.getCoord(i)));
            max.setCoord(i,(this.getMax().getCoord(i) < b.getMax().getCoord(i) ? 
                            this.getMax().getCoord(i) : b.getMax().getCoord(i)));
          }
          
             result = new BoundingBoxNd(min,max);
        } 
       return result;
    }   
    
     /**
     * Gets the clone of this bounding box
     * @return copy of this bounding box 
     */       
    public BoundingBoxNd clone(){
       PointNd p = new PointNd(pmin.x); 
       return new BoundingBoxNd(p, this.getMax());
    }
    
      /**
     * Tests this bounding box on equality with other object.
     * @param o object.
     * @return <code>true</code> if this bounding box is equal with other object.
     */
    public boolean equals(Object o) {
        if(o instanceof BoundingBoxNd) return (this.equals((BoundingBoxNd) o));
        return false;
    }
     /**
     * Tests this bounding box on equality with other bounding box.
     * @param b N-dimensional bounding box.
     * @return <code>true</code> if this bounding box is equal with other bounding box.
     */
    public boolean equals(BoundingBoxNd b) {
        if (b == this) return true;
        if (b == null) return false;
        return this.pmin.equals(b.pmin) && this.getMax().equals(b.getMax());
    }
     
    public String toString() {
        
        String s = "";
        s+=("Min, Max, Laenge \n");
        for (int i=0; i<dimlength.length; i++)
            s+=(pmin.getCoord(i)+", " +(pmin.getCoord(i)+dimlength[i])+", " +dimlength[i]+"  \n");
         return s;       
    }  
    
    public static void main(String[] args) {
         
         double[] d = {0.,0.,5.,5.};
         double[] d1 = {10.,10.,10.,10.};
         //double[] d = {0.};
         //double[] d1 = {10.};
         
         double[] d3 = {10.,10.,10.,20.};
         double[] d4 = {10.,10.,10,30};
         double[] leng; 
         double[] d5 = {0.,0.,};
         double[] d6 = {10.,10.,};
         
         PointNd p= new PointNd(d);
         PointNd p1= new PointNd(d1);
         PointNd p2= new PointNd(d3);
         PointNd p3= new PointNd(d4);
         PointNd p4= new PointNd(d5);
         PointNd p5= new PointNd(d6);
         
         PointNd[] pv = new PointNd[3];
         pv[0] = p;
         pv[1] = p1;
         pv[2] = p2;
         
         BoundingBoxNd b1 = new BoundingBoxNd(p,p1);
         BoundingBoxNd b2 = new BoundingBoxNd(p4,p5);
         
         System.out.println(b1.equals(b2));
         System.out.println(b1);
         PointNd[] bp1=b1.getP();
         for( int i= 0;i<16;i++){
            System.out.println(bp1[i]);
          }

    }
}
