/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga.individuals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author bode
 */
public class TwoGenListIndividual<K, T> extends Individual<Object> {

    protected List<K> oList;
    protected List<T> sList;

    public TwoGenListIndividual(K[] oList, T[] sList) {
        this.oList = Arrays.asList(oList);
        this.sList = Arrays.asList(sList);
    }

    public TwoGenListIndividual(List<K> oList, List<T> sList) {
        if (oList != null) {
            this.oList = oList;
        }
        if (sList != null) {
            this.sList = sList;
        }
    }

    @Override
    public TwoGenListIndividual clone() {
        return new TwoGenListIndividual(new ArrayList<K>(this.oList), new ArrayList<T>(this.sList));
    }

    @Override
    public int size() {
        return oList.size() + sList.size();
    }

    public List<K> getHead() {
        return oList;
    }

    public List<T> getTail() {
        return sList;
    }

    @Override
    public void set(int index, Object element) {
        if (index < oList.size()) {
            oList.set(index, (K) element);
        }
        if (index >= oList.size()) {
            sList.set(index, (T) element);
        }
    }

    @Override
    public Object get(int index) {

        if (index < oList.size()) {
            return oList.get(index);
        }
        if (index >= oList.size()) {
            return sList.get(index);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "TwoGenListChromosome{" + "oList=" + oList.toString() + ", sList=" + sList.toString() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.oList);
        hash = 17 * hash + Objects.hashCode(this.sList);
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
        final TwoGenListIndividual<K, T> other = (TwoGenListIndividual<K, T>) obj;
        if (!Objects.equals(this.oList, other.oList)) {
            return false;
        }
        if (!Objects.equals(this.sList, other.sList)) {
            return false;
        }
        return true;
    }

    @Override
    public List<Object> getList() {
        ArrayList<Object> newList = new ArrayList<>();
        newList.addAll(this.oList);
        newList.addAll(this.sList);
        return newList;
    }
}
