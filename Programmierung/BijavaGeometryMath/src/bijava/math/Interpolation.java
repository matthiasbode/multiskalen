package bijava.math;

import bijava.geometry.dimN.PointNd;
import bijava.math.pde.DOF;
import bijava.math.pde.ModelData;
import bijava.structs.ArrayNd;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Interpolation.java stellt Eigenschaften und Methoden zur interpolativen Simulation 
 * bestimmter Ereignisse zur Verfuegung.
 * Beachte: Es werden keine Periodizitaeten beruecksichtigt, die z.B. bei Winkelfunktionen vorkommen.
 * Ein Raster sollte je Dimension vom Minimum zum Maximum aufsteigend angelegt sein.
 * @author Dipl.-Ing. Mario Hoecker
 * @version 1.1, Mai 2006
 */
public class Interpolation {
    
    /** 
     * Interpoliert zwischen zwei Freiheitsgraden an einem n-dimensionalen Punkt in einer 
     * bestimmten Dimension.
     * @param dof0 erster Freiheitsgrad.
     * @param dof1 zweiter Freiheitsgrad.
     * @param p n-dimensionaler Punkt.
     * @param i Dimension.
     * @return Freiheitsgrad.
     */
    public static DOF interpolate(DOF dof0, DOF dof1, PointNd p, int i) {
        // Ueberpruefung der Gleichheit der Dimensionen
        int dim = p.dim();
        if (dim != dof0.getPoint().dim() || dim != dof1.getPoint().dim()) return null;
        if (i < 0 || i > dim - 1) return null;
        
        // Berechnung der Wichtungsfaktoren
        double lambda0 = (p.getCoord(i) - dof1.getPoint().getCoord(i)) / (dof0.getPoint().getCoord(i) - dof1.getPoint().getCoord(i));
        
        // Ueberpruefung der Lokation des Punktes
        if (lambda0 < 0. || lambda0 > 1.) return null;
        
        // Interpolation
        DOF result = new DOF(p);
        Iterator<ModelData> modeldatas0 = dof0.allModelDatas();
        while (modeldatas0.hasNext()) {
            ModelData modeldata0 = modeldatas0.next();
            result.addModelData(modeldata0.mult(lambda0).add(dof1.getModelData(modeldata0).mult(1. - lambda0)));
        }
        
        return result;
    }
    
    /** 
     * Interpoliert innerhalb eines n-dimensionalen Rasters aus Freiheitsgraden an einem 
     * n-dimensionalen Punkt.
     * @param raster n-dimensionales Raster aus Freiheitsgraden.
     * @param p n-dimensionaler Punkt.
     * @return Freiheitsgrad.
     */
    public static DOF interpolate(ArrayNd raster, PointNd p) {
//        return Interpolation.interpolate1(raster, p);
        return Interpolation.interpolate2(raster, p);
    }
    
    /** 
     * Interpoliert innerhalb eines n-dimensionalen Rasters aus Freiheitsgraden an einem 
     * n-dimensionalen Punkt. Die Funktion berechnet pro Interpolationsschritt einen neuen
     * Freiheitsgrad und ruft dabei die Funktion zur Interpolation zwischen zwei Freiheitsgraden auf.
     * @param raster n-dimensionales Raster aus Freiheitsgraden.
     * @param p n-dimensionaler Punkt.
     * @return Freiheitsgrad.
     */
    public static DOF interpolate1(ArrayNd raster, PointNd p) {
        // Ueberpruefung der Gleichheit der Dimensionen
        int dim = raster.getDimension();
        if (dim != p.dim()) return null;
        
        // Raster-Element bestimmen, in welchem der Punkt liegt
        int[] index = new int[dim];
        boolean[] exist = new boolean[dim];
        for (int i = 0; i < dim; i++) {
            index[i] = 0;
            exist[i] = false;
        }
        double fp, fdof0, fdof1;
        int size;
        for (int i = 0; i < dim; i++) {
            size = raster.getSizeOfDimension(i);
            fp = p.getCoord(i);
            for (int j = 0; j < size; j++) {
                index[i] = j;
                fdof0 = ((DOF) raster.getElement(index)).getPoint().getCoord(i);
                if (fp == fdof0) {
                    exist[i] = true;
                    break;
                } else if (j < size - 1) {
                    index[i]++;
                    fdof1 = ((DOF) raster.getElement(index)).getPoint().getCoord(i);
                    if (fp > fdof0 && fp < fdof1) {
                        index[i]--;
                        break;
                    }
                } else if (j == size - 1) {
                    return null; // Punkt liegt nicht im Raster
                }
            }
        }
        
        // Benoetigte Freiheitsgrade markieren
        ArrayList<DOF> dofs = new ArrayList<DOF>();
        int[] indexI = new int[dim];
        boolean b;
        for (int i = 0; i < Math.pow(2., dim); i++) {
            for (int j = 0; j < index.length; j++)
                indexI[j] = index[j];
            b = true;
            for (int j = 0; j < dim && b; j++)
                if ((i % Math.pow(2., (double)(j + 1))) / Math.pow(2., (double)(j + 1))  >= 0.5) {
                    if (exist[j]) b = false;
                    else indexI[j]++;
                }
            if (b) dofs.add((DOF) raster.getElement(indexI));
        }
        
        // Paarweise Interpolation zwischen den benoetigten Freiheitsgraden: 
        // Solange jeweils zwei Freiheitsgrade durch einen ersetzen, bis insgesamt ein Freiheitsgrad uebrig ist.
        ArrayList<DOF> temp;
        PointNd pj;
        for (int i = 0; i < dim; i++)
            if (!exist[i]) {
                temp = new ArrayList<DOF>();
                for (int j = 0; j < dofs.size(); j += 2) {
                    pj = new PointNd(dofs.get(j).getPoint());
                    pj.setCoord(i, p.getCoord(i));
                    temp.add(Interpolation.interpolate(dofs.get(j), dofs.get(j + 1), pj, i));
                }
                dofs = temp;
            }
        
        return dofs.get(0);
    }
    
    /** 
     * Interpoliert innerhalb eines n-dimensionalen Rasters aus Freiheitsgraden an einem 
     * n-dimensionalen Punkt. Die Funktion berechnet die Gewichte der benoetigten Freiheitsgrade 
     * und erzeugt damit einen Freiheitsgrad ohne die Funktion zur Interpolation zwischen zwei 
     * Freiheitsgraden aufzurufen.
     * @param raster n-dimensionales Raster aus Freiheitsgraden.
     * @param p n-dimensionaler Punkt.
     * @return Freiheitsgrad.
     */
    public static DOF interpolate2(ArrayNd raster, PointNd p) {
        // Ueberpruefung der Gleichheit der Dimensionen
        int dim = raster.getDimension();
        if (dim != p.dim()) return null;
        
        // Raster-Element bestimmen, in welchem der Punkt liegt
        int[] index = new int[dim];
        double[] lambda = new double[dim];
        boolean[] exist = new boolean[dim];
        for (int i = 0; i < dim; i++) {
            index[i] = 0;
            exist[i] = false;
        }
        double fp, fdof0, fdof1;
        int size;
        for (int i = 0; i < dim; i++) {
            size = raster.getSizeOfDimension(i);
            fp = p.getCoord(i);
            for (int j = 0; j < size; j++) {
                index[i] = j;
                fdof0 = ((DOF) raster.getElement(index)).getPoint().getCoord(i);
                if (fp == fdof0) {
                    exist[i] = true;
                    lambda[i] = 1.;
                    break;
                } else if (j < size - 1) {
                    index[i]++;
                    fdof1 = ((DOF) raster.getElement(index)).getPoint().getCoord(i);
                    if (fp > fdof0 && fp < fdof1) {
                        index[i]--;
                        lambda[i] = (fp - fdof1) / (fdof0 - fdof1);
                        break;
                    }
                } else if (j == size - 1) {
                    return null; // Punkt liegt nicht im Raster
                }
            }
        }
        
        // Benoetigte Freiheitsgrade markieren
        ArrayList<DOF> dofs = new ArrayList<DOF>();
        int[] indexI = new int[dim];
        boolean b;
        for (int i = 0; i < Math.pow(2., dim); i++) {
            for (int j = 0; j < index.length; j++)
                indexI[j] = index[j];
            b = true;
            for (int j = 0; j < dim && b; j++)
                if ((i % Math.pow(2., (double)(j + 1))) / Math.pow(2., (double)(j + 1))  >= 0.5) {
                    if (exist[j]) b = false;
                    else indexI[j]++;
                }
            if (b) dofs.add((DOF) raster.getElement(indexI));
        }
        
        // Paarweise Interpolation zwischen den benoetigten Freiheitsgraden: 
        // Solange jeweils zwei Freiheitsgrade durch einen ersetzen, bis insgesamt 
        // ein Freiheitsgrad uebrig ist.
        ArrayList<DOF> temp;
        PointNd pj;
        DOF result = dofs.get(0);
        for (int i = 0; i < dim; i++)
            if (!exist[i]) {
                temp = new ArrayList<DOF>();
                for (int j = 0; j < dofs.size(); j += 2) {
                    pj = new PointNd(dofs.get(j).getPoint());
                    pj.setCoord(i, p.getCoord(i));
                    result = new DOF(pj);
                    Iterator<ModelData> modeldatas0 = dofs.get(j).allModelDatas();
                    while (modeldatas0.hasNext()) {
                        ModelData modeldata0 = modeldatas0.next();
                        result.addModelData(modeldata0.mult(lambda[i]).add(dofs.get(j + 1).getModelData(modeldata0).mult(1. - lambda[i])));
                    }
                    temp.add(result);
                }
                dofs = temp;
            }
        
        return result;
    }
        
}