package bijava.math.function;

import bijava.geometry.LinearPoint;
import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;

/**
 *
 * @author berthold
 */
public abstract class AbstractDifferentialVectorFunctionNd extends AbstractVectorFunctionNd implements DifferentialVectorFunctionNd {
    
    public abstract VectorNd[] getGradient(PointNd x);

    public VectorFunctionNd[] getDerivation() {
        DeriNd d=new DeriNd(this);
        return d.getDirectionalDerivation();
    }
   
    
    public final AbstractDifferentialVectorFunctionNd add( final DifferentialVectorFunctionNd f1){
        if(AbstractDifferentialVectorFunctionNd.this.getDim()!=f1.getDim()) return null;
    	return new AbstractDifferentialVectorFunctionNd(){
            
            public VectorNd getValue(PointNd p){
                return AbstractDifferentialVectorFunctionNd.this.getValue(p).add(f1.getValue(p));
            }
            
            public int getDim() {
            	return f1.getDim();
            }
            
            public VectorNd[] getGradient(PointNd x) {
                VectorNd[] v1=f1.getGradient(x);
                VectorNd[] v2=AbstractDifferentialVectorFunctionNd.this.getGradient(x);
                // Anzahl der Elemente der Vektorwertigen Funktion muss gleich sein
                // (Also die Ergebnisfelder von f1(x1,...,xn) und f2(x1,...,xn), nicht die Raumdimension!)
                // "Raumdimensionstest" erfolgt schon oben.
                if (v1.length==v2.length) {
                    VectorNd[] erg=new VectorNd[v1.length];
                    for (int i=0; i<erg.length; i++) {
                        erg[i]=new VectorNd(x.dim());
                        for(int j=0;j<x.dim();j++)
                            erg[i].x[j]=v1[i].x[j]+v2[i].x[j];
                    }
                    return erg;
                }
                else return null;
            }
           
        };
    }
    public final AbstractDifferentialVectorFunctionNd add( final AbstractDifferentialVectorFunctionNd g){
        return add((DifferentialVectorFunctionNd)g);
    }
    
    public final AbstractDifferentialVectorFunctionNd sub(final DifferentialVectorFunctionNd f1){
    	if(AbstractDifferentialVectorFunctionNd.this.getDim()!=f1.getDim()) return null;
    	return new AbstractDifferentialVectorFunctionNd(){
            
            public VectorNd getValue(PointNd p){
                return AbstractDifferentialVectorFunctionNd.this.getValue(p).sub(f1.getValue(p));
            }
            public int getDim() {
            	return f1.getDim();
            }
            public VectorNd[] getGradient(PointNd x)
            {
            	VectorNd[] v1=f1.getGradient(x);
                VectorNd[] v2=AbstractDifferentialVectorFunctionNd.this.getGradient(x);
                // Anzahl der Elemente der Vektorwertigen Funktion muss gleich sein
                // (Also die Ergebnisfelder von f1(x1,...,xn) und f2(x1,...,xn), nicht die Raumdimension!)
                // "Raumdimensionstest" erfolgt schon oben.
                if (v1.length==v2.length) {
                    VectorNd[] erg=new VectorNd[v1.length];

                    for (int i=0; i<erg.length; i++) {
                        erg[i]=new VectorNd(x.dim());
                        for(int j=0;j<x.dim();j++)
                            erg[i].x[j]=v1[i].x[j]-v2[i].x[j];
                    }
                    return erg;
                }
                else return null;
            }
        };
    }
    public final AbstractDifferentialVectorFunctionNd sub( final AbstractDifferentialVectorFunctionNd g){
        return sub((DifferentialVectorFunctionNd)g);
    }
    
    public final AbstractDifferentialVectorFunctionNd mult(final double scalar){
        return new AbstractDifferentialVectorFunctionNd(){
            
            public VectorNd getValue(PointNd p){
                return AbstractDifferentialVectorFunctionNd.this.getValue(p).mult(scalar);
            }
            
            public int getDim() {
            	return AbstractDifferentialVectorFunctionNd.this.getDim();
            }
            public VectorNd[] getGradient(PointNd x) {
                VectorNd[] v1 = AbstractDifferentialVectorFunctionNd.this.getGradient(x);
                VectorNd[] erg=new VectorNd[v1.length];

                for (int i=0; i<erg.length; i++) {
                    erg[i]=new VectorNd(x.dim());
                    for(int j=0;j<x.dim();j++)
                        erg[i].x[j]=v1[i].x[j]*scalar;
                }
                return erg;
            }
           
        };
    }
    


    private class DeriNd {
    	
    	DifferentialVectorFunctionNd dvf=null;
    	boolean periodic=false;
    	int pos;
    	
    	public DeriNd(DifferentialVectorFunctionNd f) {
    		dvf=f;
    	}
    	private VectorFunctionNd[] getDirectionalDerivation() {
            int grad=dvf.getDim();
            VectorFunctionNd vcf[]=new VectorFunctionNd[grad];
            for(int i=0;i<grad;i++)
                vcf[i]=new TempClass(i);

            return vcf;
    	}
    	
    	class TempClass implements VectorFunctionNd {
            int pos;
            public TempClass(int pos) {
                this.pos=pos;
            }

            public VectorNd getValue(PointNd p) {
                VectorNd[] grad = dvf.getGradient(p);
                double[] feld = new double[grad.length];
                for (int i=0; i<grad.length; i++)
                    feld[i] = grad[i].getCoord(pos);
                return new VectorNd(feld);
            }

            public int getDim() {
                return dvf.getDim();
            }
    	}

    	

    }
   
}
