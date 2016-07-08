package bijava.geometry.dimN;

import bijava.vecmath.DMatrix;
import java.util.ArrayList;

/**
 *
 * @author abuabed
 */
public class SimplizialConvexHullNd {
    private ConvexPolyhedronNd surface;
    private ArrayList<PointNd> pointSet;
    
    /**
     * Creates a new instance of SimplizialConvexHullNd
     */
    public SimplizialConvexHullNd() {
        this.pointSet = new ArrayList<PointNd>();
    }
    /**
     * Creates a new instance of SimplizialConvexHullNd
     */
    public SimplizialConvexHullNd(PointNd[] pointSet) {
        this.pointSet = new ArrayList<PointNd>();
        for(PointNd p: pointSet) {
            this.pointSet.add(p);
        }
        //initialize the surface
        applyBeneathBeyondAlgorithm();
    }

    public ConvexPolyhedronNd getSurface() {
        return surface;
    }

    public PointNd[] getPointSet() {
        return this.pointSet.toArray(new PointNd[this.pointSet.size()]);
    }
    
    public PointNd[] copyPointSet() {
        PointNd[] pointSetCopy = new PointNd[this.pointSet.size()];
        for(int i = 0; i < this.pointSet.size(); i++) {
            pointSetCopy[i] = new PointNd(this.pointSet.get(i));
        }
        return pointSetCopy;
    }

    public void setPointSet(PointNd[] pointSet) {
        if (this.pointSet.size() != 0) this.pointSet.clear();
        for (PointNd p: pointSet) {
            this.pointSet.add(p);
        }
        //actualise the surface
    }
    
    public void addPoint(PointNd p) {
        this.pointSet.add(p);
        //actualise the Surface
        updateConvexHull(p);
    }
    
    private void applyBeneathBeyondAlgorithm() {
        //1. Startsimplex
        ArrayList<PointNd> pointSetIncr = new ArrayList<PointNd>(this.pointSet);
        this.surface = new SimplexNd(AlgGeometryNd.getAffinelyIndependentPointNdSet(pointSetIncr.toArray(new PointNd[0])));
        //remove the simplex nodes out of pointSetIncr
        for(int i = 0; i < this.surface.nodes.length; i++) {
            pointSetIncr.remove(this.surface.nodes[i]);
        }
        
        for(PointNd p: pointSetIncr) {
            updateConvexHull(p);
        }
    }
    
    private void updateConvexHull(PointNd p) {
        this.surface = update(this.surface,p);

    }
    
    protected ConvexPolyhedronNd update(ConvexPolyhedronNd poly, PointNd p) {
        ConvexPolyhedronNd[][] newOldFacets = look(poly,p);
        
        ArrayList<ConvexPolyhedronNd> newFacets = new ArrayList<ConvexPolyhedronNd>();
        for(int i = 0; i < poly.facets.length; i++) {
            newFacets.add(poly.getFacets()[i]);
        }
        for(int i = 0; i < newOldFacets[1].length; i++) {
            newFacets.remove(newOldFacets[1][i]);
        }
        for(int i = 0; i < newOldFacets[0].length; i++) {
            newFacets.add(newOldFacets[0][i]);
        }
        return new ConvexPolyhedronNd(newFacets.toArray(new ConvexPolyhedronNd[0]));
    }

    private ConvexPolyhedronNd[][] look(ConvexPolyhedronNd poly, PointNd p) {
        ConvexPolyhedronNd[][] newOldFacets = new ConvexPolyhedronNd[2][];
        
        ArrayList<ConvexPolyhedronNd> visibleFacets = new ArrayList<ConvexPolyhedronNd>();
        ArrayList<ConvexPolyhedronNd> newFacets = new ArrayList<ConvexPolyhedronNd>();
        
        ArrayList<ConvexPolyhedronNd> transparentFacets = new ArrayList<ConvexPolyhedronNd>();
        
        if(poly.order != 0){
            for(ConvexPolyhedronNd facet: poly.getFacets()){
                int visibility = getVisibility(facet,poly,p);
                if (visibility == 0) {
                    transparentFacets.add(facet);
                    visibleFacets.add(facet);
                    newFacets.add(update(facet, p));
                } else if (visibility == -1) {
                    visibleFacets.add(facet);
                }
            }
        } else {
            if (!poly.nodes[0].equals(p)) newFacets.add(new ConvexPolyhedronNd(p));
        }
        
        newOldFacets[1] = new ConvexPolyhedronNd[visibleFacets.size()];
        newOldFacets[1] = visibleFacets.toArray(new ConvexPolyhedronNd[0]);
        
        ArrayList<ConvexPolyhedronNd> newSubFacets = new ArrayList<ConvexPolyhedronNd>();
        ConvexPolyhedronNd visibleFacetToTest;
        while (!visibleFacets.isEmpty() && !(visibleFacets.size() == transparentFacets.size())) {
            visibleFacetToTest = visibleFacets.get(visibleFacets.size()-1);
            if( !transparentFacets.contains(visibleFacetToTest)){
                visibleFacets.remove(visibleFacetToTest);
                boolean isShared;
                ConvexPolyhedronNd subFacet;
                for(ConvexPolyhedronNd visibleFacet: visibleFacets) {
                    for(int i = 0; i < visibleFacetToTest.facets.length; i++) {
                        isShared = false;
                        subFacet = visibleFacetToTest.getFacets()[i];
                        for(int j = 0; j < visibleFacet.facets.length; j++) {
                            if(subFacet.unorderedEquals(visibleFacet.getFacets()[j])) isShared = true;
                        }
                        if(!isShared) newSubFacets.add(subFacet);
                    }
                }
            }
        }
        
        ArrayList<PointNd> simplexPoints = new ArrayList<PointNd>();
        simplexPoints.add(p);
        for (ConvexPolyhedronNd subFacet: newSubFacets) {
            for(PointNd node: subFacet.nodes) {
                simplexPoints.add(node);
            }
            newFacets.add(new SimplexNd(simplexPoints.toArray(new PointNd[0])));
        }
        if(newSubFacets.isEmpty()) newFacets.add(new SimplexNd(p));
        
        newOldFacets[0] = new ConvexPolyhedronNd[newFacets.size()];
        newOldFacets[0] = newFacets.toArray(new ConvexPolyhedronNd[0]);
        
        return newOldFacets;
    }
    
    protected int getVisibility(ConvexPolyhedronNd facet, ConvexPolyhedronNd poly, PointNd p) {
        PointNd[] coordinateSys = getCoordinateSystem(facet,getOppositePoint(facet,poly));
        
        DMatrix m = new DMatrix(coordinateSys.length,coordinateSys.length);
	double[] rightHand = new double[coordinateSys.length];
	double[] result = new double[coordinateSys.length];


/*	Matrix aufbauen 
	1. Richtungsvektor ist derjeneinge, welcher durch die Kante gegeben 
	wird  */

	for (int i = 0; i < coordinateSys.length; i++)	{
            for (int j = 0; j < coordinateSys.length; j++) {
                m.setItem(i,j,coordinateSys[j].getCoord(i));
            }

            rightHand[i] = (p.getCoord(i) - facet.nodes[0].getCoord(i));

	 }

/*	Gleichungssystem loesen, im Loesungsvektor stehen die Koordinaten des
	Betrachtungspunktes im neuen Koordinatensystem
	1. Koordinate massgebend, ob Simplex sichtbar
*/
	try {
            result = m.solve(rightHand);
        } catch (ArithmeticException ae) {
            return 0;
        }

	if ((result[0] > -1.e-5) && (result[0] < 1.e-5)) return 0;

	if (result[0] < 0) return -1;
	  else return 1;
    }
    
    protected PointNd getOppositePoint(ConvexPolyhedronNd facet, ConvexPolyhedronNd poly) {
        boolean isOpposite = true;
        for (PointNd pP: poly.nodes) {
            isOpposite = true;
            for (PointNd pF: facet.nodes) {
                if (pF == pP){
                    isOpposite = false;
                    break;
                }
            }
            if (isOpposite) return pP;
        }
        return null;
    }
    
    //here the CoordianteSystem must be of VectorNd type
    protected PointNd[] getCoordinateSystem(ConvexPolyhedronNd facet, PointNd oppositePoint) {
        ArrayList<PointNd> coordSys = new ArrayList<PointNd>();

        coordSys.add(facet.nodes[0].sub(oppositePoint));

        for (int i = 1; i < facet.nodes.length; i++) {
            coordSys.add(facet.nodes[0].sub(facet.nodes[i]));
            if (coordSys.size() > 1)
            if(!AlgGeometryNd.isLinearlyIndependent(coordSys.toArray(new PointNd[0])))
                coordSys.remove(coordSys.size()-1);
        }
        int i = 0;
        while (coordSys.size() < oppositePoint.dim()) {
            double[] x = new double[oppositePoint.dim()];
            x[i] = 1.;
            coordSys.add(new PointNd(x));
            if (coordSys.size() > 1)
            if(!AlgGeometryNd.isLinearlyIndependent(coordSys.toArray(new PointNd[0])))
                coordSys.remove(coordSys.size()-1);
            i++;
        }

        return coordSys.toArray(new PointNd[0]);
    }    
}
