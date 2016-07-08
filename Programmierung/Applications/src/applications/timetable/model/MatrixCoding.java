/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import ga.individuals.Individual;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Matthias
 */
public class MatrixCoding<E> extends Individual<E> {

    private int[] dimensions;
    private int[] multipliers;

    public MatrixCoding() {
    }

    private MatrixCoding(List<E> array, int[] dimensions, int[] multipliers) {
        this.chromosome = array;
        this.dimensions = dimensions;
        this.multipliers = multipliers;
    }

    public MatrixCoding(java.lang.Class<E> cls, int... dimensions) {
        int arraySize = 1;

        multipliers = new int[dimensions.length];
        for (int idx = dimensions.length - 1; idx >= 0; idx--) {
            multipliers[idx] = arraySize;
            arraySize *= dimensions[idx];
        }
        chromosome = new ArrayList<>();
        this.dimensions = dimensions;
    }

    public void clear(E value) {
        for (int i = 0; i < chromosome.size(); i++) {
            chromosome.set(i, value);
        }
    }

    public E get(int... indices) {
        assert indices.length == dimensions.length;
        int internalIndex = 0;

        for (int idx = 0; idx < indices.length; idx++) {
            internalIndex += indices[idx] * multipliers[idx];
        }
        return chromosome.get(internalIndex);
    }

    public E set(E value, int... indices) {
        assert indices.length == dimensions.length;
        int internalIndex = 0;

        for (int idx = 0; idx < indices.length; idx++) {
            internalIndex += indices[idx] * multipliers[idx];
        }
        E tmp = chromosome.get(internalIndex);
        chromosome.set(internalIndex, value);
        return tmp;
    }

    @Override
    public MatrixCoding<E> clone() {
        MatrixCoding<E> matrixCoding = new MatrixCoding<E>(chromosome, dimensions, multipliers);
        return matrixCoding;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public int[] getMultipliers() {
        return multipliers;
    }

    public void setMultipliers(int[] multipliers) {
        this.multipliers = multipliers;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.chromosome);
        hash = 71 * hash + Arrays.hashCode(this.dimensions);
        hash = 71 * hash + Arrays.hashCode(this.multipliers);
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
        final MatrixCoding<E> other = (MatrixCoding<E>) obj;
        if (!Objects.equals(this.chromosome, other.chromosome)) {
            return false;
        }
        if (!Arrays.equals(this.dimensions, other.dimensions)) {
            return false;
        }
        if (!Arrays.equals(this.multipliers, other.multipliers)) {
            return false;
        }
        return true;
    }
}
