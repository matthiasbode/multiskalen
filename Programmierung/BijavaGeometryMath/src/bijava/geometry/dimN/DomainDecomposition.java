package bijava.geometry.dimN;

import bijava.geometry.EuclideanPoint;
import bijava.geometry.NaturalElement;
import java.util.*;


public class DomainDecomposition {
    
    Vector<PointNd> points = new Vector<PointNd>();
    Vector<GeomElementNd> elements = new Vector<GeomElementNd>();
    
    public void addElement(GeomElementNd element) {
        EuclideanPoint[] p = element.getNodes();
        PointNd[] node=null;
        if (p instanceof PointNd[]) 
            node = (PointNd[]) p;
        for (int i = 0; i < node.length; i++)
            if (!points.contains(node[i]))
                points.addElement(node[i]);
        if (!elements.contains(element))
            elements.addElement(element);
    }
    
    public NaturalElement getElement(int i) {
        return elements.elementAt(i);
    }
    
    public Enumeration allElements() {
        return elements.elements();
    }
    
    public int getNumberofElements() {
        return elements.size();
    }
    
    public PointNd getPoint(int i) {
        return points.elementAt(i);
    }
    
    public Enumeration allPoints() {
        return points.elements();
    }
    
    public int getNumberofPoints() {
        return points.size();
    }
}
