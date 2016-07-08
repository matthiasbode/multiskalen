package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dim3.Point3d;
import bijava.geometry.dim3.Vector3d;



//==========================================================================//
/** The interface "AbstractScalarFunction3d" provides methods for 1 dimensional
 *  scalar functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, February 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dipl.-Ing. Tobias Pick</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public abstract class AbstractDifferentialFunction3d extends AbstractScalarFunction3d implements DifferentialScalarFunction3d{
    
    /**Gradient der Funktion am Punkt x
     * @param x der Punkt, fuer den der Gradient zurueckgegeben werden soll
     * @return Funktionswert */
    public abstract Vector3d getGradient(Point3d x);
    
    public ScalarFunction3d[] getDerivation() {
        Deri3d d=new Deri3d(this);
        return d.getDirectionalDerivation();
    }
    
    public final AbstractDifferentialFunction3d add( final DifferentialScalarFunction3d f1){
        return new AbstractDifferentialFunction3d(){
            
            public double getValue(Point3d p){
                return AbstractDifferentialFunction3d.this.getValue(p)+ f1.getValue(p);
            }
            
            public Vector3d getGradient(Point3d x) {
                Vector3d v1=f1.getGradient(x);
                Vector3d v2=AbstractDifferentialFunction3d.this.getGradient(x);
                Vector3d erg=new Vector3d();
                
                erg.x=v1.x+v2.x;
                erg.y=v1.y+v2.y;
                erg.z=v1.z+v2.z;
                
                return erg;
            }
        };
    }
    public final AbstractDifferentialFunction3d add( final AbstractDifferentialFunction3d g){
        return this.add((DifferentialScalarFunction3d)g);
    }
    
    public final AbstractDifferentialFunction3d sub(final DifferentialScalarFunction3d f1){
        return new AbstractDifferentialFunction3d(){
            
            public double getValue(Point3d p){
                return AbstractDifferentialFunction3d.this.getValue(p)- f1.getValue(p);
            }
            public Vector3d getGradient(Point3d x) {
                Vector3d v1=f1.getGradient(x);
                Vector3d v2=AbstractDifferentialFunction3d.this.getGradient(x);
                Vector3d erg=new Vector3d();
                
                erg.x=v1.x-v2.x;
                erg.y=v1.y-v2.y;
                erg.z=v1.z-v2.z;
                
                return erg;
            }
        };
    }
    public final AbstractDifferentialFunction3d sub( final AbstractDifferentialFunction3d g){
        return this.sub((DifferentialScalarFunction3d)g);
    }
    
    public final AbstractDifferentialFunction3d mult(final DifferentialScalarFunction3d f1){
        return new AbstractDifferentialFunction3d(){
            
            @Override
            public double getValue(Point3d p){
                return AbstractDifferentialFunction3d.this.getValue(p)* f1.getValue(p);
            }
            @Override
            public Vector3d getGradient(Point3d x) {
                Vector3d v1=f1.getGradient(x);
                Vector3d v2=AbstractDifferentialFunction3d.this.getGradient(x);
                Vector3d erg=new Vector3d();
                
                erg.x=v1.x*AbstractDifferentialFunction3d.this.getValue(x)+f1.getValue(x)*v2.x;
                erg.y=v1.y*AbstractDifferentialFunction3d.this.getValue(x)+f1.getValue(x)*v2.y;
                erg.z=v1.z*AbstractDifferentialFunction3d.this.getValue(x)+f1.getValue(x)*v2.z;
                
                return erg;
            }
        };
    }
    
    public final AbstractDifferentialFunction3d mult(final double scalar){
        return new AbstractDifferentialFunction3d(){
            
            public double getValue(Point3d p){
                return scalar*AbstractDifferentialFunction3d.this.getValue(p);
            }
            public Vector3d getGradient(Point3d x) {
                Vector3d v1=AbstractDifferentialFunction3d.this.getGradient(x);
                Vector3d erg=new Vector3d();
                
                erg.x=v1.x*scalar;
                erg.y=v1.y*scalar;
                erg.z=v1.z*scalar;
                
                return erg;
            }
        };
    }
    
    private class Deri3d {
        
        DifferentialScalarFunction3d dsf=null;
        boolean periodic=false;
        int pos;
        
        public Deri3d(DifferentialScalarFunction3d f) {
            dsf=f;
            
        }
        
        
        private ScalarFunction3d[] getDirectionalDerivation() {
            ScalarFunction3d scf[]=new ScalarFunction3d[3];
            scf[0]=new TempClass(0);
            scf[1]=new TempClass(1);
            scf[2]=new TempClass(2);
            
            return scf;
        }
        
        class TempClass implements ScalarFunction3d {
            int pos;
            public TempClass(int pos) {
                this.pos=pos;
            }
            
            public double getValue(Point3d p) {
                return dsf.getGradient(p).getCoord(pos);
            }
        }
        
        
        
    }
}

