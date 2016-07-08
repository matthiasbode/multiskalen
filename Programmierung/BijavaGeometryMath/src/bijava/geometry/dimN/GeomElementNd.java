/*
 * GeomElementNd.java
 *
 * Created on 10. Maerz 2003, 11:12
 */

package bijava.geometry.dimN;
import bijava.geometry.*;

/**
 * Die abstrakte Klasse GeomElement stellt Methoden dar, die fuer alle
 * Geometrische Elemente gelten .
 *
 * @author  schierba
 * @version 1.0
 * @since   1.0
 */

public abstract class GeomElementNd implements NaturalElement{
     protected PointNd[] nodes;   // Ausgangspunktmenge
    
    /**
     * Methode zum Erfragen, ob sich ein Punkt innerhalb des geometrischen
     * Elementes befindet.
     *
     * @param point der Punkt, der ueberprueft werden soll.
     * @return <code>true</code>, wenn sich der Punkt innerhalb befindet,
     * <code>false</code>, wenn nicht.
     */
    public abstract boolean contains(PointNd point);
    
    
    /**
     * Methode zur Bestimmung des baryzentrischen Schwerpunktes des
     * geometrischen Elementes.
     *
     * @return der baryzentrische Schwerpunkt des geom. Elements.
     */
    public abstract PointNd getBaryCenter();
    
    
    /**
     * Methode zum Erfragen der Dimension (Ordnung) des geometrischen Elementes.
     *
     * @return Dimension (Ordnung) des geom. Elementes
     */
    public abstract int getElementDimension();
    
    
    /**
     * Methode zur Ermittlung Natuerlichen Elementkoordinaten eines Punktes
     * bezueglich eines Simplexes.
     *
     * @param point der Punkt in kartesischen Koordinaten, dessen Natuerliche
     * Elementkoordinaten ermittelt werden sollen.
     * @return <code>double</code>-Feld mit den natuerlichen Elementkoordinaten.
     */
    public abstract CoordinateValue[] getNatElemCoord(PointNd p);
    
    
    /**
     * Methode zum Erfragen eines Knotens des geometrischen Elementes.
     *
     * @param i Index des Punktes, der zurueckgegeben werden soll.
     * @return Punkt des geom. Elementes.
     */
    public abstract PointNd getNode(int i);
    //
    //	return node[i];
    //    }
    
    
    /**
     * Methode zum Erfragen des Punktefeldes des geometrischen Elementes.
     *
     * @return Punktefeld des geom. Elementes.
     */
    public abstract PointNd[] getNodes();
    //	return node;
    //    }
    
    
    /**
     * Methode zum Erfragen der Dimension des Raumes, in der das geometrische
     * Element sich befindet.
     *
     *  @return Dimension des Raumes
     */
    public abstract int getSpaceDimension();
    
    
    /**
     * Methode zur Ermittlung der Volumenmasszahl eines geometrischen Elementes.
     *
     * @return das Lebesgue-Mass des geom. Elementes.
     */
    public abstract double getVolume();
    
    
    /**
     * Rueckgabe einer Repraesentation des geometrischen Elementes als Text.
     *
     * @return String des geom. Elementes.
     */
    public abstract String toString();
}
