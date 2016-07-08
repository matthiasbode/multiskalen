/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.storage;

import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.resources.LoadUnitResourceGroup;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import math.FieldElement;

/**
 * Diese Klasse fasst mehrere LoadUnitStorages in einer Gruppe zusammen.
 *
 * @author wolf
 * @param <E>
 */
public class LoadUnitStorageGroup<E extends LoadUnitStorage> extends LoadUnitResourceGroup<E> implements LoadUnitStorage {

    public LoadUnitStorageGroup() {
        super();
    }

    public LoadUnitStorageGroup(List<? extends E> loadUnitStorages) {
        super(loadUnitStorages);
    }

    /**
     * Erzeugt eine LoadUnitStorageGroup, die alle Subresources der enthaltenen
     * LoadUnitStorages enthaelt. Gibt <code>null</code> zurueck, falls die
     * Menge der Subresourcen leer ist.
     *
     * @param area Flaeche, auf welche der Arbeitsbereich zu beschraenken ist.
     * @return Resource, die einen Teil der aktuellen Resource abbildet.
     */
    @Override
    public LoadUnitStorage getSubResource(Area area) {

        List<LoadUnitStorage> s = new ArrayList<LoadUnitStorage>();
        for (E e : group) {
            LoadUnitStorage sr = e.getSubResource(area);
            if (sr != null) {
                s.add(sr);
            }
        }
        return (s.isEmpty()) ? null : new LoadUnitStorageGroup<LoadUnitStorage>(s);
    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + ": elements LIKE " + group.get(0) ;//+ "\t" + this.getCenterOfGeneralOperatingArea();
//        String s = this.getClass().getSimpleName()+": elements[";
//        for (int i=0; i<group.size(); i++) {
//            s+=group.get(i);
//            if (i < group.size()-1)
//                s+=",";
//        }
//        s+="]";
        return s;
    }

    @Override
    public Point3d getPosition() {
        Point2d p = getCenterOfGeneralOperatingArea();
        return new Point3d(p.x, p.y, 0);
    }

    @Override
    public FieldElement getDemand(LoadUnit loadUnit) {
        return this.group.get(0).getDemand(loadUnit);
    }

}
