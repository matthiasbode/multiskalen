/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.resources.conveyanceSystems.crane.micro;

import javax.vecmath.Point3d;
import math.geometry.ParametricLinearCurve3d;

/**
 *
 * @author bode
 */
/**
 * Besitzt zusaetzlich zur Bewegung des Krans Informationen ueber die Zeitraeume
 * der Rendezvouszeit zum ein- und auslagern, sowie zum Transport.
 *
 * @author berthold
 */
public class CraneMotion3DOverTime extends ParametricLinearCurve3d {

    // PtsInTime for different actions, always [0](start), [1] (end)
    private long[] retrieving, moving, storing;

    public CraneMotion3DOverTime(Point3d[] ptsInSpace, long[] ptsInTime) {
        this(ptsInSpace, ptsInTime, 0.);
    }

    /**
     * Erzeugt eine neue Kranbewegung ueber der Zeit gemaess den gegebenen
     * Punkten in Raum und Zeit. Start- und Ende der Bewegung werden zu Start-
     * und Endzeitpunkt aus ptsInTime gesetzt. Die Zeitraeume fuer Storing und
     * Retrieving werden zu [start,start] bzw. [ende,ende] der Bewegung gesetzt.
     *
     * @param ptsInSpace
     * @param ptsInTime
     * @param epsForEqualT
     */
    public CraneMotion3DOverTime(Point3d[] ptsInSpace, long[] ptsInTime, double epsForEqualT) {
        super(ptsInSpace, ptsInTime, epsForEqualT);
        retrieving = new long[]{ptsInTime[0], ptsInTime[0]};
        moving = new long[]{ptsInTime[0], ptsInTime[ptsInTime.length - 1]};
        storing = new long[]{ptsInTime[ptsInTime.length - 1], ptsInTime[ptsInTime.length - 1]};
    }

    /**
     * Ueberfuehrt eine ParametricLinearCurve3d in eine CraneMotion3DOverTime.
     *
     * @param res
     */
    public CraneMotion3DOverTime(ParametricLinearCurve3d res) {
        this(res.ptsInSpace, res.ptsInTime, res.epsForEqualT);
    }

    /**
     * Setzt den Start- und Endzeitpunkt fuer den Retrievingzeitraum, falls
     * gueltige Werte uebergeben werden. start darf nicht groesser als end sein
     * und beide muessen im Zeitraum der Kurve liegen.
     *
     * @param retStart
     * @param retEnd
     */
    public void setRetrieving(long retStart, long retEnd) {
        if (retStart > retEnd) {
            throw new IllegalArgumentException("retStart (" + retStart + ") must be smaller or equal to retEnd (" + retEnd + ")");
        }
        if (retStart < this.getTimeAt(0) || retEnd > this.getTimeAt(this.numberOfPts() - 1)) {
            throw new IllegalArgumentException("retStart (" + retStart + ") or retEnd (" + retEnd + ") not in Range (" + this.getTimeAt(0) + "," + this.getTimeAt(this.numberOfPts() - 1) + ")");
        }
        retrieving[0] = retStart;
        retrieving[1] = retEnd;
    }

    /**
     * Setzt den Start- und Endzeitpunkt fuer den Storingzeitraum, falls
     * gueltige Werte uebergeben werden. start darf nicht groesser als end sein
     * und beide muessen im Zeitraum der Kurve liegen.
     *
     * @param stoStart
     * @param stoEnd
     */
    public void setStoring(long stoStart, long stoEnd) {
        if (stoStart > stoEnd) {
            throw new IllegalArgumentException("stoStart (" + stoStart + ") must be smaller or equal to stoEnd (" + stoEnd + ")");
        }
        if (stoStart < this.getTimeAt(0) || stoEnd > this.getTimeAt(this.numberOfPts() - 1)) {
            throw new IllegalArgumentException("stoStart (" + stoStart + ") or stoEnd (" + stoEnd + ") not in Range (" + this.getTimeAt(0) + "," + this.getTimeAt(this.numberOfPts() - 1) + ")");
        }
        storing[0] = stoStart;
        storing[1] = stoEnd;
    }

    /**
     * Gibt eine Kopie des Retrievingzeitraums zurueck.
     *
     * @return
     */
    public long[] getRetrieving() {
        return retrieving.clone();
    }

    /**
     * Gibt eine Kopie des Storingzeitraumszurueck.
     *
     * @return
     */
    public long[] getStoring() {
        return storing.clone();
    }

}
