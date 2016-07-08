package bijava.math.pde.fvm;

import bijava.geometry.dimN.ConvexPolyhedronNd;
import bijava.geometry.dimN.PointNd;
import java.util.Iterator;
import java.util.Vector;

public class FVolume {

	private ConvexPolyhedronNd geometry;
	private FVDOF dof;
        private Vector<FVFacet> fVFacets = new Vector<FVFacet>();

	public FVolume(ConvexPolyhedronNd geom, FVDOF dof){
            this.geometry = geom;
            this.dof = dof;
	}
        /** construct a FVolume based on a ConvexPolyhedronNd and generate a FVDOF in the voronoiCenter */
        public FVolume(ConvexPolyhedronNd geom, int dofNumber, PointNd voronoiCenter){
            this.geometry = geom;
            this.dof = new FVDOF(dofNumber, voronoiCenter);
            this.dof.setFVolume(this);
            initialFVFacets();
	}
        /** construct a FVolume based on a ConvexPolyhedronNd and generate a FVDOF in the Barycenter */
        public FVolume(ConvexPolyhedronNd geom, int dofNumber){
            this.geometry = geom;
            this.dof = new FVDOF(dofNumber, this.geometry.getBaryCenter());
            this.dof.setFVolume(this);
            initialFVFacets();
	}
        private void initialFVFacets() {
            ConvexPolyhedronNd[] geomFacets = this.geometry.getFacets();
            for (int i = 0; i < geomFacets.length; i++) {
                this.fVFacets.add(new FVFacet(geomFacets[i], this.dof));
            }
        }
        
        public double getVolume() {
            if(geometry.getElementDimension()!=0)
                return geometry.getVolume(); 
            return 1.;
        }
	//**************************
	//* get- und set- Methoden *
	//**************************

	public ConvexPolyhedronNd getConvexPolyhedronNd() {
		return geometry;
	}

	public void setConvexPolyhedronNd(ConvexPolyhedronNd geom) {
		this.geometry = geom;
	}

	public FVDOF getDOF() {
		return this.dof;
	}

	public void setDOF(FVDOF dof) {
		this.dof = dof;
	}
        
        public void addFVFacet(FVFacet f){
            if (!fVFacets.contains(f)) fVFacets.addElement(f);
        }
  
        public FVFacet getFVFacet(int i) {
            return fVFacets.elementAt(i);
        }

        public Iterator<FVFacet> allFVFacets() {
            return this.fVFacets.iterator();
        }
        
        public boolean hasNeighbours() {
            /** Here the fVFacets must be checked if they have any dofs neighbours*/
            return !(fVFacets.isEmpty());
        }
        public FVFacet getFirstFVFacet() {
            return fVFacets.firstElement();
        }

        public FVFacet[] getFVFacetsArray() {
            return fVFacets.toArray(new FVFacet[fVFacets.size()]);
        }

        
}
