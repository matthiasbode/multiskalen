
package bijava.math.pde.fvm;

import bijava.geometry.dimN.ConvexPolyhedronNd;
import bijava.geometry.dimN.PointNd;
import bijava.geometry.dimN.VectorNd;

public class FVFacet{
    
    private VectorNd normal;
    private FVDOF thisDof = null;
    private FVDOF neighbourDof = null;
    private ConvexPolyhedronNd geometry = null;
    private PointNd intersectionPoint = null;
    
    public FVFacet(ConvexPolyhedronNd geom, FVDOF thisDof) {
        this.geometry = geom;
        this.normal = new VectorNd(geom.getDimension());
        setThisDof(thisDof);
    }
    
    public FVFacet(FVDOF p1, ConvexPolyhedronNd geom, FVDOF p2) {
        this.geometry = geom;
        this.normal = new VectorNd(geom.getDimension());
        setThisDof(p1);
        setNeighbourDof(p2);
        intersectionPoint = (p1.getPoint().add(p2.getPoint())).mult(0.5); // nur korrekt fuer Voronoizerlegungen
    }
    
    public double getVolume(){
        if(geometry.getElementDimension()!=0)
            return geometry.getVolume(); 
        return 1.;
    }
    
    public boolean geometryEquals(FVFacet fVfacet) {
        if (this.geometry.unorderedEquals(fVfacet.geometry))
            return true;
        return false;
    }
    
    public boolean equals(FVFacet fVfacet) {
        if (this.geometryEquals(fVfacet) 
            && this.thisDof == fVfacet.getThisDof() 
            && this.neighbourDof == fVfacet.getNeighbourDof())
            return true;
        return false;
    }
//    public String toString() {
//        String erg = "";
//	erg += "------Conection-------------\n";
//        erg += "    - DOF " + fvol.getNumber() + "(" + fvol.getPoint() + ")\n";
//        for (int i = 0; i < neighbours.length; i++) {
//		erg += "    - DOF " + neighbours[i].getNumber() + "(" + neighbours[i].getPoint() + ")\n";
//	}
//	erg += "  SpaceDimension    [" + getDimension() + "D]\n";
//	erg += "  ConnectionDim.    [" + getElementDimension() + "D]\n";
//	erg += "  Punkteanzahl      [" + getNodes().length + "]\n";
//	for (int i = 0; i < getNodes().length; i++) {
//		erg += "    - P" + i + "(" + getNodes()[i] + ")\n";
//	}
//	return erg;
//    }

    public FVDOF getThisDof() {
        return thisDof;
    }

    public void setThisDof(FVDOF thisDof) {
        this.thisDof = thisDof;
    }

    public FVDOF getNeighbourDof() {
        return neighbourDof;
    }

    public void setNeighbourDof(FVDOF neighbourDof) {
        this.neighbourDof = neighbourDof;
        initNormal();
    }

    public ConvexPolyhedronNd getGeometry() {
        return geometry;
    }

    public void setGeometry(ConvexPolyhedronNd geometry) {
        this.geometry = geometry;
    }

    public PointNd getIntersectionPoint() {
        return intersectionPoint;
    }

    public void setIntersectionPoint(PointNd intersectionPoint) {
        this.intersectionPoint = intersectionPoint;
    }

    public VectorNd getNormal() {
        return normal;
    }
    
    public void setNormal(double[] normal) {
        this.normal = new VectorNd(normal);
    }
    public void setNormal(VectorNd normal) {
        this.normal = normal;
    }
    // Private Methods
    private void initNormal() {
        double ds = this.thisDof.getPoint().distance(this.neighbourDof.getPoint());
        double[] norm = new double[this.geometry.getDimension()];
        for(int i = 0; i < this.geometry.getDimension(); i++) {
            double dx = this.neighbourDof.getPoint().getCoord(i) - this.thisDof.getPoint().getCoord(i);
            norm[i] = dx/ds;
        }
        setNormal(norm);
    }
}
