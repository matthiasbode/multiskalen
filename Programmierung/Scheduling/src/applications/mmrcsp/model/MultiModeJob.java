/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model;

import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.modes.JobOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Beschreibt einen Job innerhalb eines RCPSPs. Ein Job J_i besteht aus
 * verschiedenen Routings P_ij, die der Enge P_i aneghören. Diese verschiedenen
 * Routings können zur Abarbeitung des Jobs ausgeführt werden. Die einzelnen
 * Routings sind Operationensequenzen, die aus den Operationen o_ij1, bis o_ijk
 * bestehen.
 *
 * Die verschiedenen Routings werden über eine Routensuche gefunden
 *
 * @author bode
 * @param <E>
 */
public class MultiModeJob<E extends JobOperation> {

    private final int number;
    private static int count = 0;

    public MultiModeJob() {
        number = count++;
    }

    /**
     * Liste der einzelnen Möglichkeiten, einen Job auszuführen
     */
    private List<JobOperationList<E>> routings = new ArrayList<>();

    public List<JobOperationList<E>> getRoutings() {
        return routings;
    }

    public boolean addRouting(JobOperationList<E> r) {
        boolean add = this.routings.add(r);
        return add;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.number;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MultiModeJob<?> other = (MultiModeJob<?>) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    public int getNumber() {
        return number;
    }
}
