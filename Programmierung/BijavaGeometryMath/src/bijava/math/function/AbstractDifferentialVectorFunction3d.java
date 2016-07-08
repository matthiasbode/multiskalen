/*
 * AbstractDifferentialVectorFunction3d.java
 *
 * Created on 30. Mai 2006, 10:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dim3.Point3d;
import bijava.geometry.dim3.Vector3d;
import bijava.geometry.dimN.*;

/**
 *
 * @author berthold
 */
public abstract class AbstractDifferentialVectorFunction3d extends AbstractVectorFunction3d implements DifferentialVectorFunction3d {

    public abstract Vector3d[] getGradient(Point3d x);
    
    public VectorFunction3d[] getDerivation() {
        Deri3d d=new Deri3d(this);
        return d.getDirectionalDerivation();
    }
    
    
    public final AbstractDifferentialVectorFunction3d add( final DifferentialVectorFunction3d f1){
        return new AbstractDifferentialVectorFunction3d(){
            
            public VectorNd getValue(Point3d p){
                return AbstractDifferentialVectorFunction3d.this.getValue(p).add(f1.getValue(p));
            }
            
            public Vector3d[] getGradient(Point3d x) {
                Vector3d[] v1=f1.getGradient(x);
                Vector3d[] v2=AbstractDifferentialVectorFunction3d.this.getGradient(x);
                if (v1.length==v2.length) {
                    Vector3d[] erg=new Vector3d[v1.length];
                    for (int i=0; i<erg.length; i++) {
                        erg[i].x=v1[i].x+v2[i].x;
                        erg[i].y=v1[i].y+v2[i].y;
                        erg[i].z=v1[i].z+v2[i].z;
                    }
                    return erg;
                }
                else return null;
            }
        };
    }
    public final AbstractDifferentialVectorFunction3d add( final AbstractDifferentialVectorFunction3d g){
        return add((DifferentialVectorFunction3d)g);
    }
    
    public final AbstractDifferentialVectorFunction3d sub(final DifferentialVectorFunction3d f1){
        return new AbstractDifferentialVectorFunction3d(){
            
            public VectorNd getValue(Point3d p){
                return AbstractDifferentialVectorFunction3d.this.getValue(p).sub(f1.getValue(p));
            }
            public Vector3d[] getGradient(Point3d x) {
                Vector3d[] v1=f1.getGradient(x);
                Vector3d[] v2=AbstractDifferentialVectorFunction3d.this.getGradient(x);
                if (v1.length==v2.length) {
                    Vector3d[] erg=new Vector3d[v1.length];
                    for (int i=0; i<erg.length; i++) {
                        erg[i].x=v1[i].x-v2[i].x;
                        erg[i].y=v1[i].y-v2[i].y;
                        erg[i].z=v1[i].z-v2[i].z;
                    }
                    return erg;
                }
                else return null;
            }
        };
    }
    public final AbstractDifferentialVectorFunction3d sub( final AbstractDifferentialVectorFunction3d g){
        return sub((DifferentialVectorFunction3d)g);
    }
    
    
    public final AbstractDifferentialVectorFunction3d mult(final double scalar){
        return new AbstractDifferentialVectorFunction3d(){
            
            public VectorNd getValue(Point3d p){
                return AbstractDifferentialVectorFunction3d.this.getValue(p).mult(scalar);
            }
            public Vector3d[] getGradient(Point3d x) {
                Vector3d[] v1=AbstractDifferentialVectorFunction3d.this.getGradient(x);
                Vector3d[] erg=new Vector3d[v1.length];
                for (int i=0; i<erg.length; i++) {
                    erg[i].x=v1[i].x*scalar;
                    erg[i].y=v1[i].y*scalar;
                    erg[i].z=v1[i].z*scalar;
                }
                return erg;
            }
        };
    }
    
    private class Deri3d {
        
        DifferentialVectorFunction3d dvf=null;
        boolean periodic=false;
        int pos;
        
        public Deri3d(DifferentialVectorFunction3d f) {
            dvf=f;
            
        }
        
        
        private VectorFunction3d[] getDirectionalDerivation() {
            VectorFunction3d vcf[]=new VectorFunction3d[3];
            vcf[0]=new TempClass(0);
            vcf[1]=new TempClass(1);
            vcf[2]=new TempClass(2);
            
            return vcf;
        }
        
        class TempClass implements VectorFunction3d {
            int pos;
            public TempClass(int pos) {
                this.pos=pos;
            }
            
            public VectorNd getValue(Point3d p) {
                Vector3d[] grad = dvf.getGradient(p);
                double[] feld = new double[grad.length];
                for (int i=0; i<grad.length; i++)
                    feld[i] = grad[i].getCoord(pos);
                return new VectorNd(feld);
            }
        }
        
        
        
    }
    
}
