package bijava.math.function;

import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.Vector2d;


//==========================================================================//
/** The interface "AbstractDifferentialFunction2d" provides methods for 2 dimensional
 *  scalar differential functions.
 *
 *  <p><strong>Version: </strong> <br><dd>1.1, February 2005</dd></p>
 *  <p><strong>Author: </strong> <br>
 *  <dd>University of Hannover</dd>
 *  <dd>Institute of Computer Science in Civil Engineering</dd>
 *  <dd>Dipl.-Ing. Tobias Pick</dd>
 *  <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *  <dd>Dr.-Ing. Martin Rose</dd></p>                                       */
//==========================================================================//
public abstract class AbstractDifferentialFunction2d extends AbstractScalarFunction2d implements DifferentialScalarFunction2d{
    
    /**Gradient der Funktion am Punkt x
     * @param x der Punkt, fuer den der Gradient zurueckgegeben werden soll
     * @return Funktionswert */
    @Override
    public abstract Vector2d getGradient(Point2d x);
    
    @Override
    public ScalarFunction2d[] getDerivation() {
        Deri2d d=new Deri2d(this);
        return d.getDirectionalDerivation();
    }

    @Override
    public ScalarFunction2d getTotalDerivation() {

        return new TempTotalDerivation(this);

    }
    
    public final AbstractDifferentialFunction2d add(final DifferentialScalarFunction2d f1){
        return new AbstractDifferentialFunction2d(){
            @Override
            public double getValue(Point2d p){
                return AbstractDifferentialFunction2d.this.getValue(p)+ f1.getValue(p);
            }
            @Override
            public Vector2d getGradient(Point2d x) {
                Vector2d v1=f1.getGradient(x);
                Vector2d v2=AbstractDifferentialFunction2d.this.getGradient(x);
                Vector2d erg=new Vector2d();
                
                erg.x=v1.x+v2.x;
                erg.y=v1.y+v2.y;
                
                return erg;
            }
        };
    }
    public final AbstractDifferentialFunction2d add( final AbstractDifferentialFunction2d g){
        return this.add((DifferentialScalarFunction2d)g);
    }
    
    public final AbstractDifferentialFunction2d sub(final DifferentialScalarFunction2d f1){
        return new AbstractDifferentialFunction2d(){
            
            @Override
            public double getValue(Point2d p){
                return AbstractDifferentialFunction2d.this.getValue(p)- f1.getValue(p);
            }
            @Override
            public Vector2d getGradient(Point2d x) {
                Vector2d v1=f1.getGradient(x);
                Vector2d v2=AbstractDifferentialFunction2d.this.getGradient(x);
                Vector2d erg=new Vector2d();
                
                erg.x=v1.x-v2.x;
                erg.y=v1.y-v2.y;
                
                return erg;
            }
        };
    }
    public final AbstractDifferentialFunction2d sub( final AbstractDifferentialFunction2d g){
        return this.sub((DifferentialScalarFunction2d)g);
    }
    
    public final AbstractDifferentialFunction2d mult(final DifferentialScalarFunction2d f1){
        return new AbstractDifferentialFunction2d(){
            
            public double getValue(Point2d p){
                return AbstractDifferentialFunction2d.this.getValue(p)* f1.getValue(p);
            }
            public Vector2d getGradient(Point2d x) {
                Vector2d v1=f1.getGradient(x);
                Vector2d v2=AbstractDifferentialFunction2d.this.getGradient(x);
                Vector2d erg=new Vector2d();
                
                erg.x=v1.x*AbstractDifferentialFunction2d.this.getValue(x)+f1.getValue(x)*v2.x;
                erg.y=v1.y*AbstractDifferentialFunction2d.this.getValue(x)+f1.getValue(x)*v2.y;
                
                return erg;
            }
        };
    }
    
    public final AbstractDifferentialFunction2d mult(final double scalar){
        return new AbstractDifferentialFunction2d(){
            
            public double getValue(Point2d p){
                return scalar*AbstractDifferentialFunction2d.this.getValue(p);
            }
            public Vector2d getGradient(Point2d x) {
                Vector2d v1=AbstractDifferentialFunction2d.this.getGradient(x);
                Vector2d erg=new Vector2d();
                
                erg.x=v1.x*scalar;
                erg.y=v1.y*scalar;
                
                return erg;
            }
        };
    }


    private class TempTotalDerivation implements ScalarFunction2d {
           
            DifferentialScalarFunction2d f;
            TempTotalDerivation(DifferentialScalarFunction2d f) {
                this.f=f;
            }

            public double getValue(Point2d p) {
                return f.getGradient(p).getCoord(0)+f.getGradient(p).getCoord(1);
            }
        }

    private class Deri2d {
        
        DifferentialScalarFunction2d dsf=null;
        boolean periodic=false;
        int pos;
        
        Deri2d(DifferentialScalarFunction2d f) {
            dsf=f;
            
        }

        ScalarFunction2d[] getDirectionalDerivation() {
            ScalarFunction2d scf[]=new ScalarFunction2d[2];
            scf[0]=new TempClass(0);
            scf[1]=new TempClass(1);
            
            return scf;
        }
     
        class TempClass implements ScalarFunction2d {
            int pos;
            TempClass(int pos) {
                this.pos=pos;
            }
            
            public double getValue(Point2d p) {
                return dsf.getGradient(p).getCoord(pos);
            }
        }

    }
}
