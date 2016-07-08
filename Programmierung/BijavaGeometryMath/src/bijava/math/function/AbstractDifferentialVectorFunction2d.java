package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dim2.Point2d;
import bijava.geometry.dim2.Vector2d;
import bijava.geometry.dimN.*;

/**
 *
 * @author milbradt
 */
public abstract class AbstractDifferentialVectorFunction2d extends AbstractVectorFunction2d implements DifferentialVectorFunction2d {
            
    public abstract Vector2d[] getGradient(Point2d x);
    
    public VectorFunction2d[] getDerivation() {
        Deri2d d=new Deri2d(this);
        return d.getDirectionalDerivation();
    }
    
    
    public final AbstractDifferentialVectorFunction2d add(final DifferentialVectorFunction2d f1){
        return new AbstractDifferentialVectorFunction2d(){
            public VectorNd getValue(Point2d p){
                 return AbstractDifferentialVectorFunction2d.this.getValue(p).add(f1.getValue(p));
            }
            public Vector2d[] getGradient(Point2d x) {
                Vector2d[] v1=f1.getGradient(x);
                Vector2d[] v2=AbstractDifferentialVectorFunction2d.this.getGradient(x);
                if (v1.length==v2.length) {
                    Vector2d[] erg=new Vector2d[v1.length];
                    for (int i=0; i<erg.length; i++) {
                        erg[i] = new Vector2d();
                        erg[i].x=v1[i].x+v2[i].x;
                        erg[i].y=v1[i].y+v2[i].y;
                    }
                    return erg;
                }
                else return null;
            }
        };
    }
    public final AbstractDifferentialVectorFunction2d add( final AbstractDifferentialVectorFunction2d g){
        return add((DifferentialVectorFunction2d)g);
    }
    
    public final AbstractDifferentialVectorFunction2d sub(final DifferentialVectorFunction2d f1){
        return new AbstractDifferentialVectorFunction2d(){
            
            public VectorNd getValue(Point2d p){
                return AbstractDifferentialVectorFunction2d.this.getValue(p).sub(f1.getValue(p));
            }
            public Vector2d[] getGradient(Point2d x) {
                Vector2d[] v1=f1.getGradient(x);
                Vector2d[] v2=AbstractDifferentialVectorFunction2d.this.getGradient(x);
                if (v1.length==v2.length) {
                    Vector2d[] erg=new Vector2d[v1.length];
                    for (int i=0; i<erg.length; i++) {
                        erg[i] = new Vector2d();
                        erg[i].x=v1[i].x-v2[i].x;
                        erg[i].y=v1[i].y-v2[i].y;
                    }
                    return erg;
                }
                else return null;
            }
        };
    }
    

    
    public final AbstractDifferentialVectorFunction2d mult(final double scalar){
        return new AbstractDifferentialVectorFunction2d(){
            
            public VectorNd getValue(Point2d p){
                return AbstractDifferentialVectorFunction2d.this.getValue(p).mult(scalar);
            }
            public Vector2d[] getGradient(Point2d x) {
                Vector2d[] v1=AbstractDifferentialVectorFunction2d.this.getGradient(x);
                Vector2d[] erg=new Vector2d[v1.length];
                for (int i=0; i<erg.length; i++) {
                    erg[i] = new Vector2d();
                    erg[i].x=v1[i].x*scalar;
                    erg[i].y=v1[i].y*scalar;
                }
                return erg;
            }
        };
    }
    
    private class Deri2d {
        
        DifferentialVectorFunction2d dvf=null;
        boolean periodic=false;
        int pos;
        
        Deri2d(DifferentialVectorFunction2d f) {
            dvf=f;
            
        }

        VectorFunction2d[] getDirectionalDerivation() {
            VectorFunction2d vcf[]=new VectorFunction2d[2];
            vcf[0]=new TempClass(0);
            vcf[1]=new TempClass(1);
            
            return vcf;
        }
        
        class TempClass implements VectorFunction2d {
            int pos;
            TempClass(int pos) {
                this.pos=pos;
            }
            
            public VectorNd getValue(Point2d p) {
                Vector2d[] grad = dvf.getGradient(p);
                double[] feld = new double[grad.length];
                for (int i=0; i<feld.length; i++)
                    feld[i] = grad[i].getCoord(pos);
                return new VectorNd(feld);
            }
        }
   
    }
}

    
    
    

