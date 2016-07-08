/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.lattice;

import applications.mmrcsp.model.resources.SubResource;
import java.awt.geom.Area;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.vecmath.Point2d;

/**
 *
 * @author bode
 */
public class Structured2DGrid extends Lattice<CellResource2D> {

    private Rectangle2D bounds;
    private CellResource2D[][] cells;
    private ArrayList<CellResource2D> backup;

    public Structured2DGrid(Rectangle2D bounds, int numberOfCellsX, int numberOfCellsY) {
        this(bounds, bounds.getWidth() / numberOfCellsX, bounds.getHeight() / numberOfCellsY);
    }

    public int[] getCoordinates(Point2d p) {
        return null;
    }

    public Structured2DGrid(Rectangle2D bounds, double dX, double dY) {
        this.bounds = bounds;
        int numberOfCellsX = (int) (bounds.getWidth() / dX);
        int numberOfCellsY = (int) (bounds.getHeight() / dY);
        this.cells = new CellResource2D[numberOfCellsX][numberOfCellsY];
        int xCount = 0;
        int yCount = 0;
        double x = bounds.getMinX();


        for (int i = 0; i < numberOfCellsX; i++) {
            double y = bounds.getMinY();
            for (int j = 0; j < numberOfCellsY; j++) {
                cells[i][j] = new CellResource2D(new Rectangle2D.Double(x, y, dX, dY));
                y += dY;
            }
            x += dX;
        }
//        for (double x = bounds.getMinX(); x < bounds.getMaxX(); x += dX) {
//            for (double y = bounds.getMinY(); y < bounds.getMaxY(); y += dY) {
//                cells[xCount][yCount] = new CellResource2D(new Rectangle2D.Double(x, y, dX, dY));
//                yCount++;
//            }
//            yCount = 0;
//            xCount++;
//        }
    }

    public ArrayList<CellResource2D> getCells(javax.vecmath.Point2d from, javax.vecmath.Point2d to) {
        ArrayList<CellResource2D> result = new ArrayList<>();

        double dX = bounds.getWidth() / this.cells.length;
        double dY = bounds.getHeight() / this.cells[0].length;

        int startX = (int) ((from.x - bounds.getMinX()) / dX);
        int startY = (int) ((from.y - bounds.getMinY()) / dY);
        int endX = (int) ((to.x - bounds.getMinX()) / dX) + 1;
        int endY = (int) ((to.y - bounds.getMinY()) / dY) + 1;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (x < this.cells.length && y < this.cells[x].length) {
                    CellResource2D cell = this.cells[x][y];
                    if (cell != null) {
                        result.add(cell);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Collection<CellResource2D> getCells() {
        if (this.backup == null) {
            this.backup = new ArrayList<>();
            for (CellResource2D[] cellResource2Ds : cells) {
                this.backup.addAll(Arrays.asList(cellResource2Ds));
            }
        }
        return this.backup;
    }

    @Override
    public void setCells(Collection<CellResource2D> cells) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates. 
    }

    @Override
    public SubResource getSubResource(Area a) {
        if (!a.contains(this.bounds)) {
            return new SubLattice(this, new ArrayList<>());
        }
        LinkedHashSet<CellResource2D> res = new LinkedHashSet<>();
        Rectangle2D areaBounds = a.getBounds2D();
        int[] minLT = getCoordinates(new Point2d(areaBounds.getMinX(), areaBounds.getMinY()));
        int[] minLB = getCoordinates(new Point2d(areaBounds.getMinX(), areaBounds.getMaxY()));
        int[] maxRT = getCoordinates(new Point2d(areaBounds.getMaxX(), areaBounds.getMinY()));

        for (int x = minLT[0]; x < maxRT[0]; x++) {
            for (int y = minLT[1]; x < minLB[1]; y++) {
                Area area = new Area(a);
                area.intersect(cells[x][y].getGeneralOperatingArea());
                if (!area.isEmpty()) {
                    res.add(cells[x][y]);
                }
            }
        }
        return new SubLattice(this, res);
    }
}
