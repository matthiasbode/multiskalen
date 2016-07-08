package applications.transshipment.model.resources;

import applications.transshipment.model.loadunits.LoadUnit;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.vecmath.Point2d;

/**
 * Diese Klasse fasst mehrere LoadUnitResources in einer Gruppe zusammen.
 *
 * @author wolf
 */
public class LoadUnitResourceGroup<E extends LoadUnitResource> extends ResourceGroup<E> implements LoadUnitResource {

    protected Area generalOperatingArea;
    protected Point2d centerOfGOA;

    public LoadUnitResourceGroup() {
        super();
        updateAreaAndCenter();
    }

    public LoadUnitResourceGroup(List<? extends E> loadUnitResources) {
        super(loadUnitResources);
        updateAreaAndCenter();
    }

    /**
     * Bestimmt, ob eine LoadUnitResource aus der LoadUnitResourceGroup mit
     * einer Ladeeinheit umgehen kann. Diese kann mit einer LE umgehen, wenn
     * mindestens eine der LoadUnitResources damit umgehen kann.
     *
     * @param loadunit Die Ladeeinheit, fuer die Ueberprueft werden soll, ob sie
     * von der LoadUnitResourceGroup verwendet werden kann.
     * @return          <code>true</code>, wenn die Ladeeinheit verwendet werden kann,
     * sonst <code>false</code>.
     */
    @Override
    public boolean canHandleLoadUnit(LoadUnit loadunit) {
        for (LoadUnitResource r : group) {
            if (r.canHandleLoadUnit(loadunit) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt den Bereich zurueck, in dem die gesamte LoadUnitResourceGroup
     * generell operieren kann.
     *
     * @return Arbeitsbereich aller LoadUnitResources TODO: Performance! Area
     * vorher ausrechnen und clone zurueckgeben
     */
    @Override
    public Area getGeneralOperatingArea() {
        return (Area) generalOperatingArea.clone();
    }

    protected void updateAreaAndCenter() {
        generalOperatingArea = new Area();
        for (LoadUnitResource r : group) {
            generalOperatingArea.add(r.getGeneralOperatingArea());
        }
        double x = this.generalOperatingArea.getBounds2D().getCenterX();
        double y = this.generalOperatingArea.getBounds2D().getCenterY();
        centerOfGOA = new Point2d(x, y);
    }

    @Override
    public boolean remove(E e) {
        boolean success = super.remove(e);
        updateAreaAndCenter();
        return success;
    }

    @Override
    public boolean removeAll(Collection<E> e) {
        boolean success = super.removeAll(e);
        updateAreaAndCenter();
        return success;
    }

    @Override
    public boolean add(E e) {
        boolean success = super.add(e);
        updateAreaAndCenter();
        return success;
    }

    @Override
    public boolean addAll(Collection<? extends E> e) {
        boolean success = super.addAll(e);
        updateAreaAndCenter();
        return success;
    }

    //TODO: vorläufig angenommen, dass getGeneralOpArea einen Rectangle zurückgibt
    @Override
    public Point2d getCenterOfGeneralOperatingArea() {
        return (Point2d) centerOfGOA.clone();
    }

    public Collection<E> getIntersectedElements(Area area) {
        ArrayList<E> intersectedElems = new ArrayList<E>();
        // alle, deren GeneralOperatingArea area schneiden
        for (E e : group) {
            Area gaOriginal = e.getGeneralOperatingArea();
            Area gaTest = (Area) gaOriginal.clone();
            gaTest.subtract(area);
            if (!gaOriginal.equals(gaTest)) {
                intersectedElems.add(e);
            }
        }
        return intersectedElems;
    }

}
