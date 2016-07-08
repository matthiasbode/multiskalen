package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;





//==========================================================================//
/** The interface "AbstractScalarFunctionNd" provides methods for 1 dimensional
 *  scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, Maerz 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dipl.-Ing. Tobias Pick</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public abstract class AbstractDifferentialFunctionNd extends AbstractScalarFunctionNd implements DifferentialScalarFunctionNd{
    
    /**Funktionswert der Funktion am Punkt p
     * @param p der Punkt, fuer den der Funktionswert zurueckgegeben werden soll
     * @return Funktionswert */

	
	/*Die auskommentirerten Zeilen werden schon durch
	 * die extends vorgeschrieben.
	 * 
	 */
//    public abstract double getValue(PointNd x);	
//	public abstract int getDim();
	
    public abstract VectorNd getGradient(PointNd x);

	
    
	public ScalarFunctionNd[] getDerivation()
	{
		DeriNd d=new DeriNd(this);
		return d.getDirectionalDerivation();
	}   
    
    public final AbstractDifferentialFunctionNd add( final DifferentialScalarFunctionNd f1){
        if(AbstractDifferentialFunctionNd.this.getDim()!=f1.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return AbstractDifferentialFunctionNd.this.getValue(p)+ f1.getValue(p);
            }
            
            public int getDim()
            {
            	return f1.getDim();
            }
            
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=f1.getGradient(x);
        		VectorNd v2=AbstractDifferentialFunctionNd.this.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]+v2.x[i];
        		        	
        		return erg;
            }
           
        };
    }
    public final AbstractDifferentialFunctionNd add( final AbstractDifferentialFunctionNd g){
        return this.add((DifferentialScalarFunctionNd)g);
    }
    
    public final AbstractDifferentialFunctionNd sub(final DifferentialScalarFunctionNd f1){
    	if(AbstractDifferentialFunctionNd.this.getDim()!=f1.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return AbstractDifferentialFunctionNd.this.getValue(p)- f1.getValue(p);
            }
            public int getDim()
            {
            	return f1.getDim();
            }
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=f1.getGradient(x);
        		VectorNd v2=AbstractDifferentialFunctionNd.this.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]-v2.x[i];
        		        	
        		return erg;

            }
           
        };
    }
    public final AbstractDifferentialFunctionNd sub( final AbstractDifferentialFunctionNd g){
        return this.sub((DifferentialScalarFunctionNd)g);
    }
    
    public final AbstractDifferentialFunctionNd mult(final DifferentialScalarFunctionNd f1){
    	if(AbstractDifferentialFunctionNd.this.getDim()!=f1.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return AbstractDifferentialFunctionNd.this.getValue(p)* f1.getValue(p);
            }
            public int getDim()
            {
            	return f1.getDim();
            }
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=f1.getGradient(x);
        		VectorNd v2=AbstractDifferentialFunctionNd.this.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		double fx=AbstractDifferentialFunctionNd.this.getValue(x);
        		double gx=f1.getValue(x);
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]*fx+gx*v2.x[i];
        		
        	
        		return erg;
        	}
            
        };
    }
    
    public final AbstractDifferentialFunctionNd div(final DifferentialScalarFunctionNd f1){
    	if(AbstractDifferentialFunctionNd.this.getDim()!=f1.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return AbstractDifferentialFunctionNd.this.getValue(p)/f1.getValue(p);
            }
            public int getDim()
            {
            	return f1.getDim();
            }
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=f1.getGradient(x);
        		VectorNd v2=AbstractDifferentialFunctionNd.this.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		double u=AbstractDifferentialFunctionNd.this.getValue(x);
        		double v=f1.getValue(x);
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=(-v1.x[i]*u+v*v2.x[i])/(v*v);
        		
        	
        		return erg;
        	}
            
        };
    }
    
    public final AbstractDifferentialFunctionNd mult(final double scalar){
        return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return scalar*AbstractDifferentialFunctionNd.this.getValue(p);
            }
            
            public int getDim()
            {
            	return 1*AbstractDifferentialFunctionNd.this.getDim();
            }
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=AbstractDifferentialFunctionNd.this.getGradient(x);
            	VectorNd erg=new VectorNd(x.dim());
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]*scalar;
        	
        		return erg;
            }
           
        };
    }
    
    public final static AbstractDifferentialFunctionNd add( final DifferentialScalarFunctionNd f, final DifferentialScalarFunctionNd g){
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return f.getValue(p)+ g.getValue(p);
            }
            public int getDim()
            {
            	return f.getDim();
            }
            
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=g.getGradient(x);
        		VectorNd v2=f.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]+v2.x[i];
        		        	
        		return erg;
            }
           
        };
    }
    
    public final static AbstractDifferentialFunctionNd sub( final DifferentialScalarFunctionNd f, final DifferentialScalarFunctionNd g){
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return f.getValue(p)- g.getValue(p);
            }
            public int getDim()
            {
            	return f.getDim();
            }
            
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=g.getGradient(x);
        		VectorNd v2=f.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]-v2.x[i];
        		        	
        		return erg;
            }
           
        };
    }
    
    public final AbstractDifferentialFunctionNd mult(final DifferentialScalarFunctionNd f,final DifferentialScalarFunctionNd g){
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return f.getValue(p)* g.getValue(p);
            }
            public int getDim()
            {
            	return f.getDim();
            }
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=g.getGradient(x);
        		VectorNd v2=f.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		double fx=f.getValue(x);
        		double gx=g.getValue(x);
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]*fx+gx*v2.x[i];
        		
        	
        		return erg;
        	}
            
        };
    }
    
    public final static AbstractDifferentialFunctionNd div(final DifferentialScalarFunctionNd f,final DifferentialScalarFunctionNd g){
    	if(f.getDim()!=g.getDim()) return null;
    	return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return f.getValue(p)/g.getValue(p);
            }
            public int getDim()
            {
            	return f.getDim();
            }
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=g.getGradient(x);
        		VectorNd v2=f.getGradient(x);
        		VectorNd erg=new VectorNd(x.dim());
        		
        		double u=f.getValue(x);
        		double v=g.getValue(x);
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=(-v1.x[i]*u+v*v2.x[i])/(v*v);
        		
        	
        		return erg;
        	}
            
        };
    }
    
    public final static AbstractDifferentialFunctionNd mult(final DifferentialScalarFunctionNd f,final double scalar){
        return new AbstractDifferentialFunctionNd(){
            
            public double getValue(PointNd p){
                return scalar*f.getValue(p);
            }
            public int getDim()
            {
            	return f.getDim();
            }
            public VectorNd getGradient(PointNd x)
            {
            	VectorNd v1=f.getGradient(x);
            	VectorNd erg=new VectorNd(x.dim());
        		
        		for(int i=0;i<x.dim();i++)
        			erg.x[i]=v1.x[i]*scalar;
        	
        		return erg;
            }
           
        };
    }

    private class DeriNd {
    	
    	DifferentialScalarFunctionNd dsf=null;
    	boolean periodic=false;
    	int pos;
    	
    	public DeriNd(DifferentialScalarFunctionNd f)
    	{
    		dsf=f;

    	}
    	private ScalarFunctionNd[] getDirectionalDerivation()
    	{
    		int grad=dsf.getDim();
    		ScalarFunctionNd scf[]=new ScalarFunctionNd[grad];
    		for(int i=0;i<grad;i++)
    			scf[i]=new TempClass(i);
    		
    		
    		return scf;
    	}
    	
    	class TempClass implements ScalarFunctionNd
    	{
    		int pos;
    		public TempClass(int pos)
    		{
    			this.pos=pos;
    		}
    		
    		public double getValue(PointNd p)
    		{
    			return dsf.getGradient(p).getCoord(pos);
    		}
    		
    		public int getDim()
    		{
    			return dsf.getDim();
    		}
    	}

    	

    }
}

