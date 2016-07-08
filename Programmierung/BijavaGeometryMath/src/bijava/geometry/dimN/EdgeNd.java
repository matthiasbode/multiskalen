package bijava.geometry.dimN;

public class EdgeNd extends SimplexNd {

    double EPSILON = 1.E-7;
    
    public EdgeNd() {
        super();
    }

    public EdgeNd(PointNd[] p) {
        super(p);
        if (p.length!=2)
            System.out.println("(EdgeNd) Eine Kante muss genau zwei Punkte besitzen.");
    }
   
    public boolean contains(PointNd point) {
        PointNd tmp1 = ((PointNd)nodes[0]).sub((PointNd)nodes[1]).mult(1./((PointNd)nodes[0]).sub((PointNd)nodes[1]).norm());
        PointNd tmp2 = ((PointNd)nodes[0]).sub(point).mult(1./((PointNd)nodes[0]).sub(point).norm());
        double relative = ((PointNd)nodes[0]).sub(point).norm()/((PointNd)nodes[0]).sub((PointNd)nodes[1]).norm();
//System.out.println("normalized: "+tmp1+", "+tmp2+",  :"+relative);
        if (tmp1.epsilonEquals(tmp2, EPSILON))
        if (relative>=0 && relative<=1)
        {
            return true;
        }
        return false;
    }

    public String toString() {
        String erg = "";
	erg += "------EdgeNd----------------\n";
	erg += "  ElementDimension  [" + getElementDimension() + "D]\n";
	erg += "  SpaceDimension    [" + getDimension() + "D]\n";
	erg += "  Length            [" + getLength() + "]\n";//expanse
	erg += "  Punkteanzahl      [" + getNodes().length + "]\n";
	for (int i = 0; i < getNodes().length; i++) {
		erg += "    - P" + i + "(" + getNodes()[i] + ")\n";
	}
	erg += "----------------------------";
	return erg;
    }

    public int getElementDimension() {
        return order;
    }
    
    public int getDimension() {
        return spaceDim;
    }

    /**
    * Methode zum Abfragen eines Knotens an der Stelle i des geometrischen
    * Elementes.
    * 
    * @param i
    *            Index des Punktes, der zurueckgegeben werden soll.
    * @return Punkt des geometrischen Elementes.
    */
    public PointNd getNode(int i) {
        if (i<2 && i>-1) return (PointNd) nodes[i];
        else
        {
            System.out.println("(EdgeNd) Es gibt keinen Punkt an der Stelle "+i+".");
            return null;
        }
    }
    public double getLength() {
	return  super.getVolume();
    }
    
}
