package bijava.geometry.dimN;

import bijava.geometry.CoordinateValue;
import bijava.geometry.EuclideanPoint;
import bijava.geometry.NaturalElement;

import bijava.math.Function;
import bijava.math.pde.fem.NaturalElementCoordinateFunction;
import bijava.vecmath.DMatrix;
import bijava.vecmath.LGS;
import java.util.Enumeration;
import java.util.Vector;

public class ConvexPolyhedronNd extends PolyhedronNd implements NaturalElement {
    
    private Vector<Voronoi.VoronoiVertex> voronoiVertex = new Vector<Voronoi.VoronoiVertex>();
    
    private Vector tmpfacets = new Vector();
    
    protected ConvexPolyhedronNd() {
        super();
    }
    
    public ConvexPolyhedronNd(PointNd p) {
        super(p);
    }
    
    public ConvexPolyhedronNd(PolyhedronNd[] facets) {
        super(facets);
    }
    
    public ConvexPolyhedronNd(PointNd[] p) {
        
        nodes = checkPoints(p);
        
        spaceDim = nodes[0].dim();
        order = getLinearHullDimension(p);
        
        if (nodes != null && nodes.length > 1) {
            voronoiVertex = new Voronoi().getVoronoiVertex((PointNd[]) nodes);
            initFacets();
            tmpfacets.removeAllElements();
        } else {
            voronoiVertex = null;
            order = -1;
        }
    }
    
    /* kann bei diesem Konstruktor nicht gleich jede Facette generiert werden ? */
    public ConvexPolyhedronNd(ConvexPolyhedronNd poly, PointNd point) {
        
        PointNd[] p1 = poly.getNodes();
        PointNd[] p = new PointNd[p1.length+1];
        System.arraycopy(p1,0, p, 0, p1.length);
        p[p1.length]=point;
        nodes = checkPoints(p);
        
        spaceDim = nodes[0].dim();
        order = getLinearHullDimension(p);
        
        if (nodes != null && nodes.length > 1) {
            voronoiVertex = new Voronoi().getVoronoiVertex((PointNd[]) nodes);
            initFacets();
            tmpfacets.removeAllElements();
        } else {
            voronoiVertex = null;
            order = -1;
        }
    }
    
    public boolean contains(EuclideanPoint point) {
        if (point instanceof PointNd) return contains((PointNd) point);
        return false;
    }
    public boolean contains(PointNd point) {
        SimplexNd[] simplexe = new SimplexNd[voronoiVertex.size()]; // Anzahl
        // der Simplexe ist gleich der Anzahl der Ecken
        for (int i = 0; i < simplexe.length; i++) {
            Voronoi.VoronoiVertex voro = (Voronoi.VoronoiVertex) voronoiVertex.get(i);
            simplexe[i] = new SimplexNd(voro.associatedNode);
        }
        for (int i = 0; i < simplexe.length; i++)
            if (simplexe[i].contains((PointNd) point)) // ACHTUNG !!!!
                return true;
        return false;
    }
    
    private void initFacets() {
        ConvexPolyhedronNd[] cells;
        if (order == 0)
            facets = null;
        else if (order == 1) {
            cells = new ConvexPolyhedronNd[super.getNodes().length];
            for (int i = 0; i < cells.length; i++) {
                PointNd[] points = new PointNd[1];
                points[0] = super.getNodes()[i];
                cells[i] = new ConvexPolyhedronNd(points);
            }
            facets = cells;
        } else if (order > 1) {
            cells = new ConvexPolyhedronNd[tmpfacets.size()];
            for (int i = 0; i < cells.length; i++) {
                Voronoi.Facet facet = (Voronoi.Facet) tmpfacets.get(i);
                cells[i] = facet.getFacet();
            }
            facets = cells;
        }
    }
    
    public String toString() {
        String erg = "";
        
        erg += "----------------------------\n";
        erg += "  ElementDimension  [" + getElementDimension() + "D]\n";
        erg += "  SpaceDimension    [" + getDimension() + "D]\n";
        erg += "  Volumen           [" + getVolume() + "]\n";
        // erg += " BaryCenter\n";
        // erg += " - BC(" + getBaryCenter() + ")\n";
        erg += "  Punkteanzahl      [" + getNodes().length + "]\n";
        for (int i = 0; i < getNodes().length; i++) {
            erg += "    - P" + i + "(" + getNodes()[i] + ")\n";
        }
        erg += "----------------------------\n";
        return erg;
        
    }
    
    /**
     * Berechnet die Dimension der linearen H�lle der gegebenen Punktmenge p
     *
     * @param p
     *            ein Feld von Punkten, deren Dimension der linearen H�lle
     *            berechnet werden soll
     * @return Dimension der linearen H�lle der Punktmenge p
     */
    private int getLinearHullDimension(PointNd[] p) {
        if (p.length == 0)
            return -1;
        if (p.length == 1)
            return 0;
        if ((p.length == 2)&&(p[0].distance(p[1])>0.))
            return 1;
        int m = p.length - 1;
        int n = p[0].dim();
        double[][] mat = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = p[i + 1].getCoord(j) - p[0].getCoord(j);
            }
        }
        int erg = LGS.rangMat(new DMatrix(mat));
        return erg;
    }
    
    public int getElementDimension() {
        // if (order < 2) return order;
        // System.out.println(" In Polyhedron funktioniert die Bestimmung der
        // Dimension noch nicht !!");
        // return -96;
        return order;
    }
    
    /** Gleichheit der Knoten mit Orientierung 
     * funktioniert noch nicht
     */
    public boolean orderedEquals(ConvexPolyhedronNd p) {
        
        if(!(order == p.order))     return false;        // Dimensionen stimmen nicht ueberein
         // funktioniert noch nicht
        System.out.println("funktioniert noch nicht");
        
        return false;
    }
    
    /** Gleichheit der Knoten ohne Orientierung */
    public boolean unorderedEquals(ConvexPolyhedronNd p) {
        
        if(!(order == p.order))     return false;        // Dimensionen stimmen nicht ueberein
        
        PointNd[] point = p.getNodes();
        
        int k=0;
        for (int i=0; i<=order; i++)
            for (int j=0; j<=order; j++)
                if( (point[i] == nodes[j] )){
                    k++;
                    j=order;
                }
        
        if(k==order+1) return true;
        return false;
    }
    
    
    public double[] getNaturalElementCoordinates(EuclideanPoint p) {
        if (p instanceof PointNd) return getNaturalElementCoordinates((PointNd) p);
        return null;
    }
    
    public double[] getNaturalElementCoordinates(PointNd p) {
        CoordinateValue[] c = getNatElemCoord(p);
        double[] rValue = new double[c.length];
        for(int i=0; i<c.length; i++)
            rValue[i]=c[i].value;
        return rValue;
    }

    public CoordinateValue[] getNatElemCoord(EuclideanPoint pp) {
        if (pp instanceof PointNd) return getNatElemCoord((PointNd) pp);
        return null;
    }
    
    /**
     * Methode zur Ermittlung der nat�rlichen Elementkoordinaten eines Punktes
     * bez�glich der konvexen Zelle
     *
     * @param PunktNd p,
     *            der innerhalb der konvexen Zelle liegen muss
     * @return CoordinateValue[] -Feld mit den nat�rlichen
     *         Elementkoordinaten.
     */
    public CoordinateValue[] getNatElemCoord(PointNd p){
        CoordinateValue[] coord = new CoordinateValue[nodes.length];
        Vector<Voronoi.VoronoiVertex> newVoronoiVertex = new Voronoi().getVoronoiVertex(p);
        Vector<Voronoi.VoronoiVertex> tmp = new Vector<Voronoi.VoronoiVertex>();
        Vector<Voronoi.VoronoiVertex> tmp2 = new Vector<Voronoi.VoronoiVertex>();
        
        boolean bool = false;
        for (int i = 0; i < this.nodes.length; i++) {
            coord[i] = new CoordinateValue((PointNd) this.nodes[i], 0.);
            if (p.equals(this.nodes[i])) {
                coord[i].value = 1.0;
                bool = true;
            }
        }
        if (bool)
            return coord;
        
        for (int i = 0; i < newVoronoiVertex.size(); i++) {
            Voronoi.VoronoiVertex voroVertex = (Voronoi.VoronoiVertex) newVoronoiVertex
                    .get(i);
            for (int j = 0; j < voroVertex.associatedNode.length; j++)
                if (voroVertex.associatedNode[j].equals(p)) {
                tmp.add(newVoronoiVertex.get(i));
                break;
                } else {
                if (j == voroVertex.associatedNode.length - 1)
                    tmp2.add(newVoronoiVertex.get(i));
                }
        }
        PointNd[] points = new PointNd[tmp.size()];
        for (int i = 0; i < points.length; i++)
            points[i] = (PointNd) tmp.get(i);
        double _Rx = new ConvexPolyhedronNd(points).getVolume();
        double[] dA = new double[2];
        double[][] gradlkoord = new double[nodes.length][order];
        for (int i = 0; i < nodes.length; i++) {
            PointNd point = (PointNd) nodes[i];
            Vector<Voronoi.VoronoiVertex> temp = new Vector<Voronoi.VoronoiVertex>();
            for (int j = 0; j < voronoiVertex.size(); j++) {
                Voronoi.VoronoiVertex voro = (Voronoi.VoronoiVertex) voronoiVertex
                        .get(j);
                for (int k = 0; k < voro.associatedNode.length; k++)
                    if (voro.associatedNode[k].equals(point)) {
                    temp.add(voro);
                    break;
                    }
            }
            
            for (int j = 0; j < newVoronoiVertex.size(); j++) {
                Voronoi.VoronoiVertex voro = (Voronoi.VoronoiVertex) newVoronoiVertex
                        .get(j);
                for (int k = 0; k < voro.associatedNode.length; k++)
                    if (voro.associatedNode[k].equals(point)) {
                    temp.add(voro);
                    break;
                    }
            }
            
            Vector<Voronoi.VoronoiVertex> delete = new Vector<Voronoi.VoronoiVertex>();
            for (int j = 0; j < temp.size(); j++) {
                Voronoi.VoronoiVertex voro = (Voronoi.VoronoiVertex) temp
                        .get(j);
                for (int k = 0; k < tmp2.size(); k++) {
                    Voronoi.VoronoiVertex voro2 = (Voronoi.VoronoiVertex) tmp2
                            .get(k);
                    if (voro.equals(voro2))
                        delete.add(voro);
                }
            }
            
            for (int j = 0; j < delete.size(); j++)
                temp.remove(delete.get(j));
            Voronoi.VoronoiVertex[] points2 = new Voronoi.VoronoiVertex[temp
                    .size()];
            for (int l = 0; l < points2.length; l++)
                points2[l] = (Voronoi.VoronoiVertex) temp.get(l);
            ConvexPolyhedronNd cell = new ConvexPolyhedronNd(points2);
            double _Rxi = 0.0;
            if (this.order == cell.order)
                _Rxi = cell.getVolume();
            coord[i] = new CoordinateValue(point, (_Rxi / _Rx));
            if (temp.size() == 3) {
                gradlkoord[i] = gradVolume2D(points2, p);
            }
            if (temp.size() > 3) {
                ConvexPolyhedronNd tempCell = new ConvexPolyhedronNd(points2);
                for (int j = 0; j < tempCell.voronoiVertex.size(); j++) {
                    Voronoi.VoronoiVertex[] points2neu = new Voronoi.VoronoiVertex[3];
                    Voronoi.VoronoiVertex tempVoro = (Voronoi.VoronoiVertex) tempCell.voronoiVertex
                            .get(j);
                    for (int k = 0; k < tempVoro.associatedNode.length; k++) {
                        for (int l = 0; l < points2.length; l++) {
                            if (points2[l]
                                    .equalsCoordinate(tempVoro.associatedNode[k])) {
                                points2neu[k] = points2[l];
                                break;
                            }
                        }
                    }
                    double[] grad = gradVolume2D(points2neu, p);
                    gradlkoord[i][0] += grad[0];
                    gradlkoord[i][1] += grad[1];
                }
            }
            dA[0] += gradlkoord[i][0];
            dA[1] += gradlkoord[i][1];
        }
        for (int i = 0; i < gradlkoord.length; i++) {
            coord[i].gradient[0] = (gradlkoord[i][0] * _Rx - coord[i].value
                    * dA[0] * _Rx)
                    / (_Rx * _Rx);
            coord[i].gradient[1] = (gradlkoord[i][1] * _Rx - coord[i].value
                    * dA[1] * _Rx)
                    / (_Rx * _Rx);
        }
        return coord;
    }
    
    /**
     * Methode zum Abfragen eines Knotens an der Stelle i des geometrischen
     * Elementes.
     *
     * @param i
     *            Index des Punktes, der zur_ckgegeben werden soll.
     * @return Punkt des geometrischen Elementes.
     */
    public PointNd getNode(int i) {
        return (PointNd) nodes[i];
    }
    
    /**
     * Knoten der konvexen Zelle
     *
     * @return PointNd[]
     */
    public PointNd[] getNodes() {
        return (PointNd[]) nodes;
    }
    
    public ConvexPolyhedronNd[] getFacets() {
        return (ConvexPolyhedronNd[]) facets;
    }
    
    // public ConvexPolyhedronNd[] getFacets() {
    // ConvexPolyhedronNd[] cells;
    // if (order == 0)
    // return null;
    // if (order == 1) {
    // cells = new ConvexPolyhedronNd[nodes.length];
    // for (int i = 0; i < cells.length; i++) {
    // PointNd[] points = new PointNd[1];
    // points[0] = nodes[i];
    // cells[i] = new ConvexPolyhedronNd(points);
    // }
    // return cells;
    // }
    // cells = new ConvexPolyhedronNd[facets.length];
    // for (int i = 0; i < cells.length; i++) {
    // Voronoi.Facet facet = new Voronoi.Facet(facets[i].nodes);
    // cells[i] = facet.getFacet();
    // }
    // return cells;
    // }
    
    // public ConvexPolyhedronNd[] getFacets(PointNd point) {
    // Vector tmp = new Vector();
    // ConvexPolyhedronNd[] cells = getFacets();
    // for (int i = 0; i < cells.length; i++)
    // if (cells[i].contains(point))
    // tmp.add(cells[i]);
    // ConvexPolyhedronNd[] cells2 = new ConvexPolyhedronNd[tmp.size()];
    // for (int i = 0; i < cells2.length; i++) {
    // ConvexPolyhedronNd cell = (ConvexPolyhedronNd) tmp.get(i);
    // cells2[i] = cell;
    // }
    // return cells2;
    // }
    
    public ConvexPolyhedronNd[] getFacetsOfOrder(int grad) {
        if(getElementDimension()<grad) return null;
        if(getElementDimension()==grad) return new ConvexPolyhedronNd[]{this};
        ConvexPolyhedronNd[] facets=getFacets();
        if(facets!=null) {
            if(facets[0].getElementDimension()==grad) return facets;
            boolean weiter=true;
            
            Vector<ConvexPolyhedronNd> alle = new Vector<ConvexPolyhedronNd>();
            for(int i=0;i<facets.length;i++)
                alle.add(facets[i]);
            
            
            while(weiter) {
                Vector<ConvexPolyhedronNd> tmp = new Vector<ConvexPolyhedronNd>();
                for(int i=0;i<alle.size();i++) {
                    ConvexPolyhedronNd feld[]=alle.get(i).getFacets();
                    
                    if(feld[0].order==grad) weiter=false;
                    for(int j=0;j<feld.length;j++) {
                        if(!tmp.contains(feld[j])) tmp.add(feld[j]);
                    }
                }
                alle=new Vector<ConvexPolyhedronNd>();
                for(int i=0;i<tmp.size();i++)
                    alle.add(tmp.get(i));
                
            }
            ConvexPolyhedronNd[] erg=new ConvexPolyhedronNd[alle.size()];
            for(int i=0;i<erg.length;i++)
                erg[i]=alle.get(i);
            return erg;
        }
        return null;
    }
 
    
    /**
     * Die Methode berechnet die Volumenma_zahl
     * Funktioniert noch nicht sicher
     * @return das Lebesgue-Masz
     */
    public double getVolume() {
        if (nodes==null) return Double.NaN;
        if (nodes.length==0) return Double.NaN;
        if (nodes.length==1) return 0.;
        if (nodes.length==2) return ((PointNd)nodes[0]).distance((PointNd)nodes[1]);
        if (nodes.length==3) return (new SimplexNd((PointNd[])nodes)).getVolume();
        System.out.println("Volumenberechnung funktioniert noch nicht sicher!");
        double volume = 0.0;
        for (int i = 0; i < voronoiVertex.size(); i++) {
            Voronoi.VoronoiVertex voro = (Voronoi.VoronoiVertex) voronoiVertex.get(i);
            PointNd[] p = voro.associatedNode;
            if (voro.dim() == 2)
                volume += 0.5 * Math.abs((p[1].getCoord(0) - p[0].getCoord(0)) 
                                            * (p[2].getCoord(1) - p[0].getCoord(1))
                                        - (p[2].getCoord(0) - p[0].getCoord(0))
                                            * (p[1].getCoord(1) - p[0].getCoord(1)));
            if (voro.dim() > 2) {
                double[][] a = new double[(voro.associatedNode.length + 1)][(voro.associatedNode.length + 1)];
                for (int l = 0; l < (voro.associatedNode.length + 1); l++) {
                    a[voro.associatedNode.length][l] = 1.0;
                    a[l][voro.associatedNode.length] = 1.0;
                    a[l][l] = 0.0;
                }
                for (int j = 0; j < (voro.associatedNode.length); j++)
                    for (int m = j; m < (voro.associatedNode.length); m++) {
                        a[j][m] = p[j].distance(p[m]);
                        a[m][j] = a[j][m];
                    }
                DMatrix aa = new DMatrix(a);
                double faktor = Math.pow((double) Function.fac((voro.associatedNode.length - 1)), 2)
                                    * Math.pow(2., (voro.associatedNode.length - 1));
                double det = Math.abs(aa.det());
                volume += Math.sqrt(det / faktor);
            }
        }
        return volume;
    }
    
    /**
     * Diese Methode bestimmt den baryzentrischen Schwerpunktes des konvexen Polyhedrons.
     *
     *  @return der baryzentrische Schwerpunkt des Simplex
     */
    public PointNd getBaryCenter() {
        int dim = nodes[0].dim();
        double[] result = new double[dim];
        for(int j=0;j<nodes.length;j++)
            for(int i=0;i<dim;i++)
                result[i] += nodes[j].getCoord(i);
        
        for(int i=0;i<dim;i++)
            result[i] /= nodes.length;
        return new PointNd(result);
    }
    
    
    public SimplexNd[] longestSideDecomposition(int grad) {
        SimplexNd[] bary;
        if(this instanceof SimplexNd ) {
            bary=new SimplexNd[]{(SimplexNd)this};
        } else {
            ConvexPolyhedronNd[] c=baryConvexDecomposition(1);
            bary=new SimplexNd[c.length];
            for(int i=0;i<c.length;i++)
                bary[i]=(SimplexNd) c[i];
        }
        if(grad<=1) return bary;
        SimplexNd[][] alle=new SimplexNd[bary.length][];
        int anz=0;
        for(int i=0;i<bary.length;i++) {
            alle[i]=bary[i].longestSideDecomposition(grad-1);
            anz+=alle[i].length;
        }
        SimplexNd []erg=new SimplexNd[anz];
        anz=0;
        for(int i=0;i<bary.length;i++) {
            for(int j=0;j<alle[i].length;j++) {
                erg[anz++]=alle[i][j];
            }
        }
        return erg;
        
    }
    
    
    //Das Problem an der Minimalen
    //Simplizialzerlegung ist, das es sich um ein
    //Simplex handel k_nnte aber eine ConvexeCelle ist
    
    public SimplexNd[] minimalSimplicialBarycentricDecomposition() {
        
        SimplexNd erg[]=null;
//		System.out.println("order= "+order);
        if(order==0) return new SimplexNd[] {new SimplexNd((PointNd)nodes[0])};
        if((this instanceof SimplexNd)) return new SimplexNd[] {(SimplexNd) this};
        else {
            //Pr_fe ob es vielleicht doch ein Simplex ist
            System.out.println((this.order+1)+" "+this.nodes.length);
            if(this.order+1==this.nodes.length) {
//                System.out.println("im test");
                try {
                    System.out.println(this);
                    SimplexNd neu=new SimplexNd((PointNd[])this.nodes);
                    return new SimplexNd[] {neu};
                } catch(Exception e) {
//                    System.out.println("war doch kein simp");
                    System.out.println(e);
                }
            }
            PointNd b=getBaryCenter();
            ConvexPolyhedronNd facets[]=getFacets();
            Vector<SimplexNd> simp=new Vector<SimplexNd>();
            for(int i=0;i<facets.length;i++) {
                SimplexNd feld[]=facets[i].minimalSimplicialBarycentricDecomposition();
                for(int j=0;j<feld.length;j++)
                    simp.add(feld[j]);
            }
            erg=new SimplexNd[simp.size()];
            for(int i=0;i<simp.size();i++)
                erg[i]=new SimplexNd(simp.get(i),b);
        }
        return erg;
    }
    
    public SimplexNd[] barycentricDecomposition() {
        SimplexNd erg[]=null;
//		System.out.println("order= "+order);
        if(order==0) return new SimplexNd[] {new SimplexNd((PointNd)nodes[0])};
        PointNd b=getBaryCenter();
        if(order>1) {
            ConvexPolyhedronNd facets[]=getFacets();
            Vector<SimplexNd> simp=new Vector<SimplexNd>();
            for(int i=0;i<facets.length;i++) {
                SimplexNd feld[]=facets[i].barycentricDecomposition();
                for(int j=0;j<feld.length;j++)
                    simp.add(feld[j]);
            }
            erg=new SimplexNd[simp.size()];
            for(int i=0;i<simp.size();i++)
                erg[i]=new SimplexNd(simp.get(i),b);
        } else{
            erg = new SimplexNd[] {new SimplexNd(new PointNd[]{(PointNd)nodes[0],b}), new SimplexNd(new PointNd[]{b,(PointNd)nodes[1]})};}
        return erg;
    }
    
    public ConvexPolyhedronNd[] baryConvexDecomposition(int stop) {
        ConvexPolyhedronNd erg[]=null;
//		System.out.println("order= "+order);
        if(order>stop) {
            PointNd b=getBaryCenter();
            ConvexPolyhedronNd facets[]=(ConvexPolyhedronNd[])getFacets();
            Vector<ConvexPolyhedronNd> simp=new Vector<ConvexPolyhedronNd>();
            for(int i=0;i<facets.length;i++) {
                ConvexPolyhedronNd feld[]=facets[i].baryConvexDecomposition(stop);
                for(int j=0;j<feld.length;j++)
                    simp.add(feld[j]);
            }
            
            erg=new ConvexPolyhedronNd[simp.size()];
            for(int i=0;i<simp.size();i++) {
                PointNd ps[]=(PointNd[])simp.get(i).nodes;
                PointNd pfeld[]=new PointNd[ps.length+1];
                for(int j=0;j<ps.length;j++)
                    pfeld[j]=ps[j];
                pfeld[pfeld.length-1]=b;
                if(pfeld.length==order+1) erg[i]=new SimplexNd(pfeld);
                else erg[i]=new ConvexPolyhedronNd(pfeld);
            }
        } else erg=new ConvexPolyhedronNd[] {this};
        return erg;
    }
    
    public ConvexPolyhedronNd getShrunkClone(double grad) {
        PointNd mitte=getBaryCenter();
        ConvexPolyhedronNd neu;
        
        PointNd punkte[]=new PointNd[nodes.length];
        for(int i=0;i<punkte.length;i++) {
            PointNd diff=((PointNd)nodes[i]).sub(mitte);
            PointNd stauch=diff.mult(grad);
            punkte[i]=mitte.add(stauch);
        }
        neu=new ConvexPolyhedronNd(punkte);
        
        return neu;
    }
    
    
    private PointNd[] checkPoints(PointNd[] points) {
        if (points != null && points.length > 1) {
            Vector<PointNd> tmp = new Vector<PointNd>();
            tmp.add(points[0]);
            boolean doppelt = false;
            for (int i = 0; i < points.length; i++) {
                doppelt = false;
                for (int j = 0; j < tmp.size(); j++) {
                    PointNd point = (PointNd) tmp.get(j);
                    if (points[i].equals(point)) {
                        doppelt = true;
                        break;
                    }
                }
                if (!doppelt)
                    tmp.add(points[i]);
            }
            PointNd[] newPoints = new PointNd[tmp.size()];
            for (int i = 0; i < tmp.size(); i++)
                newPoints[i] = (PointNd) tmp.get(i);
            return newPoints;
        }
        return points;
    }
    
    private double[] gradVolume2D(Voronoi.VoronoiVertex[] points, PointNd p) {
        double EPSILON = 1.E-7;
        if (new SimplexNd(points).getVolume2DOriented() < EPSILON)
            return simplex_volume2D_dx(points[1], points[0], points[2], p);
        else
            return simplex_volume2D_dx(points[0], points[1], points[2], p);
    }
    
    private double[] simplex_volume2D_dx(Voronoi.VoronoiVertex a,
            Voronoi.VoronoiVertex b, Voronoi.VoronoiVertex c, PointNd p) {
        double[] dx = new double[2];
        if (a.contains(p)) {
            double[][] dsdx = ds_dx(a);
            dx[0] += 0.5 * ((b.getCoord(1) - c.getCoord(1)) * dsdx[0][0] + (c
                    .getCoord(0) - b.getCoord(0))
                    * dsdx[0][1]);
            dx[1] += 0.5 * ((b.getCoord(1) - c.getCoord(1)) * dsdx[1][0] + (c
                    .getCoord(0) - b.getCoord(0))
                    * dsdx[1][1]);
        }
        
        if (b.contains(p)) {
            double[][] dsdx = ds_dx(b);
            dx[0] += 0.5 * ((c.getCoord(1) - a.getCoord(1)) * dsdx[0][0] + (a
                    .getCoord(0) - c.getCoord(0))
                    * dsdx[0][1]);
            dx[1] += 0.5 * ((c.getCoord(1) - a.getCoord(1)) * dsdx[1][0] + (a
                    .getCoord(0) - c.getCoord(0))
                    * dsdx[1][1]);
        }
        
        if (c.contains(p)) {
            double[][] dsdx = ds_dx(c);
            dx[0] += 0.5 * ((a.getCoord(1) - b.getCoord(1)) * dsdx[0][0] + (b
                    .getCoord(0) - a.getCoord(0))
                    * dsdx[0][1]);
            dx[1] += 0.5 * ((a.getCoord(1) - b.getCoord(1)) * dsdx[1][0] + (b
                    .getCoord(0) - a.getCoord(0))
                    * dsdx[1][1]);
        }
        return dx;
    }
    
    private double[][] ds_dx(Voronoi.VoronoiVertex s) {
        double[][] erg = new double[2][2];
        
        PointNd a = s.associatedNode[0];
        PointNd b = s.associatedNode[1];
        PointNd x = s.associatedNode[2];
        
        double d = 4.0 * (a.getCoord(0) - b.getCoord(0))
        * (b.getCoord(1) - x.getCoord(1)) - 4.0
                * (b.getCoord(0) - x.getCoord(0))
                * (a.getCoord(1) - b.getCoord(1));
        double d1 = 2.0
                * (a.getCoord(0) * a.getCoord(0) + a.getCoord(1)
                * a.getCoord(1) - b.getCoord(0) * b.getCoord(0) - b
                .getCoord(1)
                * b.getCoord(1))
                * (b.getCoord(1) - x.getCoord(1))
                - 2.0
                * (b.getCoord(0) * b.getCoord(0) + b.getCoord(1)
                * b.getCoord(1) - x.getCoord(0) * x.getCoord(0) - x
                .getCoord(1)
                * x.getCoord(1)) * (a.getCoord(1) - b.getCoord(1));
        double d2 = 2.0
                * (b.getCoord(0) * b.getCoord(0) + b.getCoord(1)
                * b.getCoord(1) - x.getCoord(0) * x.getCoord(0) - x
                .getCoord(1)
                * x.getCoord(1))
                * (a.getCoord(0) - b.getCoord(0))
                - 2.0
                * (a.getCoord(0) * a.getCoord(0) + a.getCoord(1)
                * a.getCoord(1) - b.getCoord(0) * b.getCoord(0) - b
                .getCoord(1)
                * b.getCoord(1)) * (b.getCoord(0) - x.getCoord(0));
        
        double ddx1 = 4.0 * (a.getCoord(1) - b.getCoord(1));
        double ddx2 = 4.0 * (b.getCoord(0) - a.getCoord(0));
        
        double d1dx1 = 4.0 * (a.getCoord(1) - b.getCoord(1)) * x.getCoord(0);
        double d1dx2 = 2.0
                * (b.getCoord(0) * b.getCoord(0) + b.getCoord(1)
                * b.getCoord(1) - a.getCoord(0) * a.getCoord(0) - a
                .getCoord(1)
                * a.getCoord(1)) + 4.0
                * (a.getCoord(1) - b.getCoord(1)) * x.getCoord(1);
        
        double d2dx1 = 2.0
                * (-b.getCoord(0) * b.getCoord(0) - b.getCoord(1)
                * b.getCoord(1) + a.getCoord(0) * a.getCoord(0) + a
                .getCoord(1)
                * a.getCoord(1)) + 4.0
                * (b.getCoord(0) - a.getCoord(0)) * x.getCoord(0);
        double d2dx2 = 4.0 * (b.getCoord(0) - a.getCoord(0)) * x.getCoord(1);
        
        erg[0][0] = (d1dx1 * d - d1 * ddx1) / (d * d);
        erg[1][0] = (d1dx2 * d - d1 * ddx2) / (d * d);
        erg[0][1] = (d2dx1 * d - d2 * ddx1) / (d * d);
        erg[1][1] = (d2dx2 * d - d2 * ddx2) / (d * d);
        
        return erg;
    }

    public NaturalElementCoordinateFunction[] getLocalCoordinateFunction() {
        NaturalElementCoordinateFunction[] erg = new NaturalElementCoordinateFunction[nodes.length];
        for (int i = 0; i < erg.length; i++) {
            final int ip = i;
            erg[i] = new NaturalElementCoordinateFunction(this, nodes[i]) {

                @Override
                public VectorNd getGradient(PointNd x) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public double getValue(PointNd p) {
                    return getNaturalElementCoordinates(p)[ip];
                }
            };
        }
        return erg;
    }

    
    // public String toString() {
    // String erg = "";
    // for (int i = 0; i < nodes.length; i++) {
    // PointNd p = (PointNd) nodes[i];
    // erg += ("P" + i + "(" + p + ")\n");
    // }
    // return erg;
    // }
    
    // ************************************************************************************
    //
    // innere Klasse: Voronoi
    //
    // ************************************************************************************
    
    class Voronoi {
        private Algorithmic alg;
        
        private Vector myVq = new Vector();
        
        private Vector myEq = new Vector();
        
        private Vector voronoiVertex = new Vector();
        
        public Voronoi() {
        }
        
        public Vector<Voronoi.VoronoiVertex> getVoronoiVertex(PointNd[] nodes) {
            initFirstStructure();
            for (int i = 0; i < nodes.length; i++)
                addPoint((PointNd) nodes[i]);
            removeFirstStructure();
            allocateAdjacentVertex();
            return (Vector<Voronoi.VoronoiVertex>) voronoiVertex.clone();
        }
        
        public Vector<Voronoi.VoronoiVertex> getVoronoiVertex(PointNd p) {
            voronoiVertex = (Vector<Voronoi.VoronoiVertex>) ConvexPolyhedronNd.this.voronoiVertex.clone();
            addPoint(p);
            allocateAdjacentVertex();
            return voronoiVertex;
        }
        
        // Aufbau der Anfangsstruktur f_r den Algorithmus aus dem Script
        // "Bauinformatik II - Geometrische Algorithmen"
        private void initFirstStructure() {
            // minmax f?r BoundingBox
            PointNd p0 = (PointNd) getNodes()[0];
            int z = 1000;
            double[][] minmax = new double[2][p0.dim()];
            for (int i = 0; i < p0.dim(); i++) {
                minmax[0][i] = p0.getCoord(i);
                minmax[1][i] = p0.getCoord(i);
            }
            for (int i = 0; i < getNodes().length; i++) {
                PointNd pi = (PointNd) getNodes()[i];
                for (int j = 0; j < p0.dim(); j++) {
                    if (pi.getCoord(j) - z < minmax[0][j])
                        minmax[0][j] = pi.getCoord(j);
                    if (pi.getCoord(j) + z > minmax[1][j])
                        minmax[1][j] = pi.getCoord(j);
                }
            }
            
            for (int i = 0; i < p0.dim(); i++) {
                minmax[0][i] -= z;
                minmax[1][i] += z;
            }
            
            alg = new Algorithmic(minmax[0], minmax[1]);
            
            for (int i = 0; i < alg.myPoints.size(); i++) {
                VoronoiVertex voroEcke = new VoronoiVertex(p0.dim());
                voroEcke.associatedNode = (PointNd[]) alg.myPoints.get(i);
                voroEcke.set(AlgGeometryNd.centerNd(
                        (PointNd[]) voroEcke.associatedNode).getCoords());
                voronoiVertex.add(voronoiVertex.size(), voroEcke);
            }
        }
        
        private void addPoint(PointNd p) {
            createVq(p);
            createEq(p);
            createVoronoiVertex(p);
        }
        
        private void removeFirstStructure() {
                        /*
                         * Vector delete = new Vector(); //Suchen nach Hilfspunkten in den
                         * VoronoiEcken for (int i = 0; i < voronoiVertex.size(); i++) {
                         * VoronoiVertex voroEcke = (VoronoiVertex) voronoiVertex.get(i);
                         * for (int j = 0; j < voroEcke.associatedNode.length; j++) for (int
                         * k = 0; k < voroEcke.associatedNode[j].n; k++) for (int l = 0; l <
                         * alg.myHilfspunkte.size(); l++) if
                         * (voroEcke.associatedNode[j].equals(alg.myHilfspunkte.get(l)))
                         * delete.add(voroEcke); } //L_schen der Voronoipunkte die
                         * Hilfspunkte enthalten for (int i = 0; i < delete.size(); i++)
                         * voronoiVertex.remove(delete.get(i));
                         * System.out.println("fertsch");
                         */
            
            Vector tmp = new Vector();
            for (int i = 0; i < voronoiVertex.size(); i++) {
                VoronoiVertex voroEcke = (VoronoiVertex) voronoiVertex.get(i);
                int l = getNodesInVertex(voroEcke).length;
                int o = order;
                if (getNodesInVertex(voroEcke).length == order + 1) {
                    VoronoiVertex voroEcke2 = new VoronoiVertex(order);
                    PointNd[] points = getNodesInVertex(voroEcke);
                    voroEcke2.associatedNode = points;
                    voroEcke2.set(AlgGeometryNd.center(voroEcke2.associatedNode)
                    .getCoords());
                    boolean doppelt = false;
                    for (int j = 0; j < tmp.size(); j++) {
                        VoronoiVertex voro = (VoronoiVertex) tmp.get(j);
                        if (voro.equals(voroEcke2)) {
                            doppelt = true;
                            break;
                        }
                    }
                    if (!doppelt)
                        tmp.add(voroEcke2);
                }
            }
            voronoiVertex = tmp;
        }
        
        private void createVq(PointNd point) {
            myVq.removeAllElements();
            for (int i = 0; i < voronoiVertex.size(); i++) {
                VoronoiVertex myVoronoiEcke = (VoronoiVertex) voronoiVertex
                        .get(i);
                if ((myVoronoiEcke.distance(myVoronoiEcke.associatedNode[0]) - myVoronoiEcke
                        .distance(point)) > EPSILON)
                    myVq.add(myVoronoiEcke);
            }
        }
        
        private void createEq(PointNd point) {
            myEq.removeAllElements();
            
            // Aufbau von Eq
            for (int i = 0; i < myVq.size(); i++) {
                VoronoiVertex temp = (VoronoiVertex) this.myVq.get(i);
                PointNd[][] points = new PointNd[temp.associatedNode.length][temp.associatedNode.length - 1];
                for (int j = 1; j <= temp.associatedNode.length - 1; j++)
                    for (int k = 0; k < temp.associatedNode.length; k++)
                        points[k][j - 1] = temp.associatedNode[j];
                for (int j = 0; j < temp.associatedNode.length - 1; j++)
                    for (int k = 0; k < (temp.associatedNode.length - 1 - j); k++)
                        points[k][j] = temp.associatedNode[j];
                for (int j = 0; j < temp.associatedNode.length; j++) {
                    PointNd[] p = new PointNd[temp.associatedNode.length - 1];
                    for (int k = 0; k < temp.associatedNode.length - 1; k++)
                        p[k] = points[j][k];
                    myEq.add(p);
                }
            }
            
            // Suchen der doppelten Eintr_gen in Eq und Speicherung im Vector
            // "delete"
            // gleiche Reihenfolge der Knoten vorausgesetzt
            Vector delete = new Vector();
            for (int i = 0; i < myEq.size(); i++) {
                PointNd[] points1 = (PointNd[]) myEq.get(i);
                for (int j = 0; j < myEq.size(); j++) {
                    boolean doppelt = true;
                    PointNd[] points2 = (PointNd[]) myEq.get(j);
                    for (int k = 0; k < points1.length; k++)
                        if (points1[k] != points2[k])
                            doppelt = false;
                    if (i != j && doppelt) {
                        delete.add(points1);
                        delete.add(points2);
                    }
                }
            }
            // L_schen der doppelten Eintr_gen in Eq
            for (int i = 0; i < delete.size(); i++)
                myEq.remove(delete.get(i));
        }
        
        private void createVoronoiVertex(PointNd point) {
            // L_schen der VoronoiEcken, die durch den neuen Punkt hinf_llig
            // geworden sind
            for (int i = 0; i < myVq.size(); i++)
                voronoiVertex.remove(myVq.get(i));
            // Generieren der neuen VoronoiEcken aus der Menge Eq und dem neuen
            // Punkt
            for (int i = 0; i < myEq.size(); i++) {
                PointNd[] oldPoints = (PointNd[]) myEq.get(i);
                PointNd[] newPoints = new PointNd[oldPoints.length + 1];
                for (int j = 0; j < oldPoints.length; j++)
                    newPoints[j] = oldPoints[j];
                newPoints[oldPoints.length] = point;
                VoronoiVertex voroEcke = new VoronoiVertex(point.dim());
                voroEcke.associatedNode = newPoints;
                voroEcke.set(AlgGeometryNd.center(
                        (PointNd[]) voroEcke.associatedNode).getCoords());
                voronoiVertex.add(voronoiVertex.size(), voroEcke);
            }
        }
        
        private void allocateAdjacentVertex() {
            PointNd[] p1 = null;
            for (int h = 0; h < voronoiVertex.size(); h++) {
                VoronoiVertex voroVertex = (VoronoiVertex) voronoiVertex.get(h);
                for (int i = 0; i < voroVertex.associatedNode.length; i++) {
                    p1 = new PointNd[voroVertex.associatedNode.length - 1];
                    int z = 0;
                    for (int j = 0; j < voroVertex.associatedNode.length; j++)
                        if (i != j)
                            p1[z++] = voroVertex.associatedNode[j];
                    // ConvexPolyhedronNd facet = new ConvexPolyhedronNd(p1);
                    Facet facet = new Facet(p1);
                    for (int g = 0; g < voronoiVertex.size(); g++) {
                        if (g != h) {
                            VoronoiVertex voroVertex2 = (VoronoiVertex) voronoiVertex
                                    .get(g);
                            for (int k = 0; k < voroVertex2.associatedNode.length; k++) {
                                PointNd[] p2 = new PointNd[voroVertex2.associatedNode.length - 1];
                                int z2 = 0;
                                for (int l = 0; l < voroVertex2.associatedNode.length; l++)
                                    if (k != l)
                                        p2[z2++] = voroVertex2.associatedNode[l];
                                boolean equals = true;
                                for (int m = 0; m < p2.length; m++)
                                    if (p1[m] != p2[m]) {
                                    equals = false;
                                    break;
                                    }
                                if (equals) {
                                    voroVertex.adjacentVertex[i] = voroVertex2;
                                    facet = null;
                                }
                            }
                        }
                    }
                    if (facet != null)
                        checkFacets(facet);
                }
            }
        }
        
        private void checkFacets(Facet facet) {
            if (tmpfacets.size() == 0)
                tmpfacets.add(facet);
            else {
                boolean gleich = false;
                SimplexNd simplex = new SimplexNd(facet.nodes);
                for (int i = 0; i < tmpfacets.size(); i++) {
                    Facet facet2 = (Facet) tmpfacets.get(i);
                    SimplexNd simplex2 = new SimplexNd(facet2.nodes);
                    if (SimplexNd.isInSubSpace(simplex, simplex2)) {
                        gleich = true;
                        Vector points = new Vector();
                        for (int j = 0; j < simplex.getNodes().length; j++)
                            points.add(simplex.getNode(j));
                        for (int j = 0; j < simplex2.getNodes().length; j++) {
                            if (!simplex.contains(simplex2.getNode(j))) {
                                points.add(simplex2.getNode(j));
                            }
                        }
                        PointNd[] tmp = new PointNd[points.size()];
                        for (int k = 0; k < points.size(); k++)
                            tmp[k] = (PointNd) points.get(k);
                        Facet facet3 = new Facet(tmp);
                        tmpfacets.remove(facet);
                        tmpfacets.remove(facet2);
                        tmpfacets.add(facet3);
                        break;
                    }
                }
                if (!gleich)
                    tmpfacets.add(facet);
            }
        }
        
        private PointNd[] getNodesInVertex(VoronoiVertex voronoiVertex) {
            Vector tmp = new Vector();
            for (int i = 0; i < voronoiVertex.associatedNode.length; i++)
                for (int j = 0; j < nodes.length; j++)
                    if (voronoiVertex.associatedNode[i].equals(nodes[j]))
                        tmp.add(voronoiVertex.associatedNode[i]);
            PointNd[] points = new PointNd[tmp.size()];
            for (int i = 0; i < points.length; i++)
                points[i] = (PointNd) tmp.get(i);
            return points;
        }
        
        // ************************************************************************************
        //
        // innere Klasse: VoronoiEcke
        //
        // ************************************************************************************
        
        class VoronoiVertex extends PointNd {
            
            private PointNd[] associatedNode; // assoziierte Knoten
            
            private VoronoiVertex[] adjacentVertex; // benachbarte Ecken
            
            public VoronoiVertex(int dim) {
                super(dim);
                associatedNode = new PointNd[dim + 1];
                adjacentVertex = new VoronoiVertex[dim + 1];
            }
            
            public boolean contains(PointNd point) {
                for (int i = 0; i < associatedNode.length; i++)
                    if (associatedNode[i].equals(point))
                        return true;
                return false;
            }
            
            public boolean equals(VoronoiVertex voro) {
                for (int i = 0; i < this.associatedNode.length; i++) {
                    boolean equals = false;
                    for (int j = 0; j < voro.associatedNode.length; j++) {
                        if (this.associatedNode[i] == voro.associatedNode[j]) {
                            equals = true;
                            break;
                        }
                    }
                    if (!equals)
                        return false;
                }
                return true;
            }
            
            public boolean equalsCoordinate(PointNd point) {
                if (this.dim() == point.dim()) {
                    for (int i = 0; i < dim(); i++)
                        if (this.getCoord(i) != point.getCoord(i))
                            return false;
                    return true;
                }
                return false;
            }
            
            public String toString() {
                String erg = "VoronoiEcke: \n";
                for (int i = 0; i < this.dim(); i++) {
                    erg += " x[" + i + "] " + this.getCoord(i) + "\n";
                }
                return erg;
            }
        }
        
        // ************************************************************************************
        //
        // innere Klasse: Facet
        //
        // ************************************************************************************
        
        class Facet {
            
            PointNd[] nodes;
            
            public Facet(PointNd[] points) {
                this.nodes = points;
            }
            
            public ConvexPolyhedronNd getFacet() {
                return new ConvexPolyhedronNd(nodes);
            }
        }
        
        // ************************************************************************************
        //
        // innere Klasse: Algorithmus
        //
        // ************************************************************************************
        
        class Algorithmic {
            
            Vector<PointNd> myHilfspunkte = new Vector<PointNd>(); // Punkte der BoundingBox
            
            Vector<PointNd[]> myPoints = new Vector<PointNd[]>();
            
            // Punkte der VoronoiEcken f_r die Ausgangsstruktur
            PointNd minPoint;
            
            PointNd maxPoint;
            
            public Algorithmic(double[] p1, double[] p2) {
                minPoint = new PointNd(p1);
                maxPoint = new PointNd(p2);
                createPoints(p1);
            }
            
            private void createPoints(double[] p1) {
                int[] feld = new int[p1.length];
                for (int i = 0; i < feld.length; i++)
                    feld[i] = 1;
                boolean b[] = new boolean[p1.length];
                for (int i = 0; i < b.length; i++)
                    b[i] = true;
                Paket p = new Paket(feld, b);
                stufe(p);
            }
            
            private int getHilfspunktIndex(PointNd p) {
                for (int i = 0; i < myHilfspunkte.size(); i++) {
                    PointNd point = (PointNd) myHilfspunkte.get(i);
                    if (point.equals(p))
                        return i;
                }
                return -1;
            }
            
            private PointNd allocate(Paket p) {
                double[] koord = new double[p.pos.length];
                for (int i = 0; i < p.pos.length; i++)
                    if (p.pos[i])
                        koord[i] = maxPoint.getCoord(i);
                    else
                        koord[i] = minPoint.getCoord(i);
                return (PointNd) myHilfspunkte
                        .get(getHilfspunktIndex(new PointNd(koord)));
            }
            
            private void stufe(Paket p) {
                PointNd nextPoint = p.getNextPoint(minPoint, maxPoint);
                if (getHilfspunktIndex(nextPoint) == -1)
                    myHilfspunkte.add(nextPoint);
                if (p.getAnzahl() == 0) {
                    PointNd[] points = new PointNd[p.vor.size() + 1];
                    points[0] = (PointNd) myHilfspunkte.get(p.vor.size());
                    for (int i = 1; i < p.vor.size() + 1; i++)
                        points[i] = allocate((Paket) p.vor.get(i - 1));
                    myPoints.add(points);
                } else
                    for (int i = 0; i < p.getAnzahl(); i++)
                        stufe(p.setPos(i));
            }
            
            // ************************************************************************************
            //
            // innere Klasse von innerer Klasse "Algorithmus": Paket
            //
            // ************************************************************************************
            
            class Paket {
                
                boolean pos[];
                
                int alle[];
                
                Vector<Paket> vor = new Vector<Paket>();
                
                public Paket(int a[], boolean b[]) {
                    alle = a;
                    pos = b;
                }
                
                public Paket(Paket p) {
                    pos = new boolean[p.pos.length];
                    alle = new int[p.alle.length];
                    for (int i = 0; i < pos.length; i++)
                        pos[i] = p.pos[i];
                    for (int i = 0; i < alle.length; i++)
                        alle[i] = p.alle[i];
                    Paket f[] = new Paket[p.vor.size()];
                    int j = 0;
                    for (Enumeration enum1 = p.vor.elements(); enum1
                            .hasMoreElements();)
                        f[j++] = (Paket) enum1.nextElement();
                    vor = new Vector<Paket>();
                    for (int i = 0; i < f.length; i++)
                        vor.add(f[i]);
                    vor.add(p);
                }
                
                public PointNd getNextPoint(PointNd min, PointNd max) {
                    double[] koord = new double[min.dim()];
                    for (int i = 0; i < pos.length; i++)
                        if (pos[i])
                            koord[i] = max.getCoord(i);
                        else
                            koord[i] = min.getCoord(i);
                    return new PointNd(koord);
                }
                
                public int getAnzahl() {
                    int anz = 0;
                    for (int i = 0; i < pos.length; i++)
                        if (pos[i])
                            anz++;
                    return anz;
                }
                
                public Paket setPos(int posit) {
                    Paket p = new Paket(this);
                    int anz = 0;
                    for (int i = 0; i < pos.length; i++)
                        if (pos[i]) {
                        if (anz == posit) {
                            p.pos[i] = false;
                            p.alle[i] = 0;
                        }
                        anz++;
                        }
                    return p;
                }
            }
        }
    }
}
