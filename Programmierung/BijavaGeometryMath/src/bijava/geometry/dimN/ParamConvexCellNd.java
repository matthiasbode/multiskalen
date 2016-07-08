package bijava.geometry.dimN;

import bijava.geometry.CoordinateValue;
import bijava.geometry.EuclideanPoint;
import bijava.geometry.NaturalElement;
import bijava.math.function.*;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;
import bijava.vecmath.*;


public class ParamConvexCellNd  implements NaturalElement {
	private ConvexPolyhedronNd cell;
	private AbbildungNd abbildung;
	DifferentialScalarFunctionNd[] coordinates;

    
   /**
	* Erstellen einer parametrischen konvexen Zelle aus einer konvexen Menge von Punkten.
	*
	* @param pm Feld der Punkte, aus denen die Zelle zu bilden ist 
	*/
	public ParamConvexCellNd(PointNd[] pm, FormFunctionNd[] formfunctions) {
		this.cell=new ConvexPolyhedronNd(pm);
		coordinates = new NaturalCoordinateFunctionNd[cell.nodes.length]; 
		for (int i=0; i<cell.nodes.length; i++) {
			coordinates[i] = new NaturalCoordinateFunctionNd(cell, cell.nodes[i]);
		}
		this.abbildung=new AbbildungNd();
		for (int i=0; i<formfunctions.length; i++){
			this.abbildung.addFormFunction(formfunctions[i]);
		}
	} 
        
        public ParamConvexCellNd(ConvexPolyhedronNd cell, FormFunctionNd[] formfunctions) {
		this.cell=cell;
		coordinates = new NaturalCoordinateFunctionNd[cell.nodes.length]; 
		for (int i=0; i<cell.nodes.length; i++) {
			coordinates[i] = new NaturalCoordinateFunctionNd(cell, cell.nodes[i]);
		}
		this.abbildung=new AbbildungNd();
		for (int i=0; i<formfunctions.length; i++){
			this.abbildung.addFormFunction(formfunctions[i]);
		}
	} 
    

        public boolean contains(EuclideanPoint point) {
        if (point instanceof PointNd) return contains((PointNd) point);
        return false;
    }
        
       
	public boolean contains(PointNd point){
		return this.cell.contains(point);
	}
    
    
	public PointNd getBaryCenter(){
		return this.cell.getBaryCenter();
	}
    
    
	public int getElementDimension(){
		return this.cell.getElementDimension();
	}
    
    
	public PointNd getReferencePoint(PointNd point){
		return this.abbildung.getReferencePoint(this.coordinates, point);
	}
    
    
	public PointNd getParamPoint(PointNd point){
		return this.abbildung.getParametricPoint(this.coordinates, point);
	}
        

        public CoordinateValue[] getNatElemCoord(EuclideanPoint point){
            if (point instanceof PointNd) return getNatElemCoord((PointNd) point);
            return null;
        } 
        
	public CoordinateValue[] getNatElemCoord(PointNd point){
		return this.cell.getNatElemCoord(point);
	}
        
        
        public double[] getNaturalElementCoordinates(EuclideanPoint point) {
            if (point instanceof PointNd) return this.cell.getNaturalElementCoordinates((PointNd) point);
            return null;
        }
    
    
	public MatrixNd getJacobi(PointNd point){
		MatrixNd jacobi=new MatrixNd(point.dim(), point.dim());
		jacobi.setZero();
		CoordinateValue[] coord = this.cell.getNatElemCoord(point);
		for (int i=0; i<coord.length; i++){
			jacobi.setElement(0,0,jacobi.getElement(0,0)+(coord[i].getGradient()[0]*this.getParamPoint((PointNd)coord[i].getPoint()).x[0]));
			jacobi.setElement(0,1,jacobi.getElement(0,1)+(coord[i].getGradient()[0]*this.getParamPoint((PointNd)coord[i].getPoint()).x[1]));
			jacobi.setElement(1,0,jacobi.getElement(1,0)+(coord[i].getGradient()[1]*this.getParamPoint((PointNd)coord[i].getPoint()).x[0]));
			jacobi.setElement(1,1,jacobi.getElement(1,1)+(coord[i].getGradient()[1]*this.getParamPoint((PointNd)coord[i].getPoint()).x[1]));
		}
		jacobi.add(this.abbildung.getJacobi(this.coordinates, point));
		//jacobi.invert();
		return jacobi; 
		//return this.abbildung.getJacobi(this.coordinates, point);
	}
   
     
    
	public PointNd getNode(int i){
		return this.cell.getNode(i);
	}
    

	/**
	 * Methode zum Erfragen des Punktefeldes des geometrischen Elementes.
	 *
	 * @return Punktefeld des geom. Elementes.
	 */
	public PointNd[] getNodes() {
		return this.cell.getNodes();
	}
    
    
	/**
	 * Methode zum Erfragen der Dimension des Raumes, in der das geometrische
	 * Element sich befindet.
	 *
	 *  @return Dimension des Raumes
	 */
	public int getDimension() {
		return this.cell.getDimension();
	}
    
    
	/**
	 * Methode zur Ermittlung der Volumenmasszahl eines geometrischen Elementes.
	 *
	 * @return das Lebesgue-Mass des geom. Elementes.
	 */
	public double getVolume() {
		int Refinement =4;
		double volume = 0.0;
		ConvexPolyhedronNd[] cells = this.cell.longestSideDecomposition(Refinement);
		for (int i=0; i<cells.length; i++){
			MatrixNd jacobi=this.getJacobi(cells[i].getBaryCenter());
			DMatrix djacobi=new DMatrix(2,2);
			djacobi.setItem(0,0, jacobi.getElement(0,0));
			djacobi.setItem(1,0, jacobi.getElement(1,0));
			djacobi.setItem(0,1, jacobi.getElement(0,1));
			djacobi.setItem(1,1, jacobi.getElement(1,1));
			double js=djacobi.det();
			volume = volume + cells[i].getVolume()*js;
		}
		return volume;
		//return this.cell.getVolume();
	}
    
    
   /** 
	* Rueckgabe einer Repraesentation des geometrischen Elementes als Text.
	*
	* @return String des geom. Elementes. 
	*/ 
	public String toString(){
		return this.cell.toString();
	}
        
        public ParamConvexCellNd[] getFacetsOfOrder(int i) {
        ConvexPolyhedronNd[] cp = cell.getFacetsOfOrder(i);
        ParamConvexCellNd[] pcp = new ParamConvexCellNd[cp.length];
        FormFunctionNd[] f = abbildung.getFormFunctions();
        for(int j=0; j<cp.length; j++)
            pcp[j]=new ParamConvexCellNd(cp[j], f);
        
        return pcp;
    }

    @Override
    public NaturalElementCoordinateFunction[] getLocalCoordinateFunction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
