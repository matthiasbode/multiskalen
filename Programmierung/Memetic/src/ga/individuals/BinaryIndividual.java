package ga.individuals;

import ga.Parameters;
import java.util.List;

/**
 * The
 * <code>BinaryCoding</code> is an encoding method using a string of binary
 * digits. The value is represented as a
 * <code>long</code> and has a fixed length in bits. It supports single
 * crossover-over and mutation.
 */
public class BinaryIndividual extends Individual<Boolean> {

    /**
     * Every bit is a bit in a long. Therefore this string is limited to 64 bits
     */
    private long bits;
    /**
     * The number of bits in the binary string
     */
    private int size;
    /**
     * The mask to and bits with every time it is set
     */
    private long mask;

    /**
     * Creates a new
     * <code>BinaryCoding</code> with the specified bit- length and given name.
     *
     * @param size	the length of the binary string in bits
     */
    public BinaryIndividual(int size) {
        setSize(size);
    }

    /**
     * Creates a new
     * <code>BinaryCoding</code> with the specified bit-length and initial value
     * and given name.
     *
     * @param size	the length of the binary string in bits
     * @param value	the initial value for the binary string
     */
    public BinaryIndividual(int size, long value) {
        setSize(size);
        setLong(value);
    }

    /**
     * Creates and returns a clone of the given
     * <code>BinaryCoding</code>. Throws
     * <code>NullPointerException</code> when passed
     * <code>null</code>.
     *
     * @param toClone	the binary string to clone
     */
    public BinaryIndividual(BinaryIndividual toClone) {
        this(toClone.getSize(), toClone.getLong());
    }

    /**
     * Returns the length of this binary string in bits
     *
     * @returns the length of the string in bits
     */
    public int getSize() {
        return size;
    }

    public int size() {
        return size;
    }

    /**
     * Sets the length of this binary string in bits
     *
     * @param size	the length of the binary string in bits
     */
    public void setSize(int size) {
        if (size < 0 || size > 64) {
            // Throw exception
        }

        this.size = size;

        // For example: size = 6:
        // -1<<size = ...1111_1100_0000
        // ~(-1<<size)= ...0000_0011_1111
        this.mask = ~(-1L << size);
    }

    /**
     * Returns the value of this binary string as a
     * <code>long</code>.
     *
     * @returns a <code>long</code> value of this binary string
     */
    public long getLong() {
        return bits;
    }

    /**
     * Sets the binary string to the value as a
     * <code>long</code>. Performs a bit-wise AND (&) to clear all bits above
     * the length of this binary string.
     *
     * @param newVal	a <code>long</code> value to set this to
     */
    public void setLong(long newVal) {
        bits = (newVal & this.mask);
    }

    /**
     * Returns the bit-status of a certain bit in this binary string.
     *
     * @returns	<code>false</code> if the bit is 0; <code>true</code> if the bit
     * is 1;
     */
    public boolean getBit(int bitNum) {
        if (bitNum < 0 || bitNum > this.size) {
            // Throw exception
            return false;
        }

        return (bits & (1L << bitNum)) != 0;
    }

    public Boolean get(int index) {
        return Boolean.valueOf(getBit(index));
    }

    /**
     * Sets a given bit in the binary string
     *
     * @param bitNum	sets the given bit to <code>true</code> ("1")
     */
    public void setBit(int bitNum) {
        if (bitNum < 0 || bitNum > this.size) {
            // Throw exception
            return;
        }

        this.bits |= (1L << bitNum);
    }

    public void set(int index, Boolean element) {
        if (element.booleanValue()) {
            setBit(index);
        } else {
            clearBit(index);
        }

    }

    /**
     * Clears a given bit in the binary string
     *
     * @param bitNum	sets the given bit to <code>false</code> ("0")
     */
    public void clearBit(int bitNum) {
        if (bitNum < 0 || bitNum > this.size) {
            // Throw exception
            return;
        }

        this.bits &= ~(1L << bitNum);
    }

    /**
     * Returns a
     * <code>String</code> representation of this binary string as a string of
     * binary digits. Leading zeroes are trimmed.
     *
     * @returns a <code>String</code> representation in binary
     */
    public String toBinaryString() {
        return Long.toBinaryString(bits);
    }

    /**
     * Returns a
     * <code>String</code> representation of this binary string as a string of
     * binary digits, including leading zeroes
     *
     * @returns a <code>String</code> representation in binary
     */
    public String toFullBinaryString() {
        String full = this.toBinaryString();

        for (int i = full.length(); i < this.size; i++) {
            full = "0" + full;
        }

        return full;
    }

    /**
     * Returns a
     * <code>String</code> representation of this binary string as a string of
     * hex digits. Leading zeroes are trimmed.
     *
     * @returns a <code>String</code> representation in hexadecimal
     */
    public String toHexString() {
        return Long.toHexString(bits);
    }

    /**
     * Returns a
     * <code>String</code> representation of this binary string as a string of
     * hexadecimal digits, including leading zeroes
     *
     * @returns a <code>String</code> representation in hexadecimal
     */
    public String toFullHexString() {
        String full = this.toHexString();
        int expSize = this.size / 4;

        if (this.size % 4 != 0) {
            expSize++;
        }

        for (int i = full.length(); i < expSize; i++) {
            full = "0" + full;
        }

        return full;
    }

    public Individual crossover(Individual other, double xOverRate) {
        if (xOverRate > 0 && xOverRate < 1 && other instanceof BinaryIndividual) {
            return crossover(this, (BinaryIndividual) other);
        } else {
            return clone();
        }
    }

    /**
     * Returns a new
     * <code>BinaryCoding</code> based on two other binary strings of the same
     * length. A crossover-over point is picked at random and all bits before
     * this point are the same as the first parent's and all bits after this
     * point are the same as the second parent's. Neither parent is altered
     * during this call.
     *
     * @returns a new <code>BinaryCoding</code>
     */
    public static BinaryIndividual crossover(BinaryIndividual first, BinaryIndividual second) {
        if (first.getSize() != second.getSize()) {
            // Throw exception
            return null;
        }

        BinaryIndividual n = new BinaryIndividual(first);
        long crossover_pt = -1L << ((long) (Parameters.getRandom().nextDouble() * (double) (first.getSize())));

        n.setLong((first.getLong() & crossover_pt) + (second.getLong() & ~crossover_pt));

        return n;
    }

    /**
     * Mutates a given number of bits in the binary string. The mutation is the
     * inversion of the bit. A non-positive argument results in no change in the
     * string.
     *
     * @param amount the number of bits to invert ("mutate")
     */
    public void mutate(int amount) {
        for (int i = 0; i < amount; i++) {
            bits ^= 1L << ((long) (Parameters.getRandom().nextDouble() * (double) (this.size)));
        }
    }

    /**
     * Mutate the chromosome with given probability of each bit changing.
     *
     * @param mutationRate bit change probability from 0.0 to 1.0.
     * @return this
     */
    public void mutate(double mutationRate) {
        if (mutationRate > 0 && mutationRate <= 1) {
            mutate((int) mutationRate * size);
        }
    }

    @Override
    public BinaryIndividual clone() {
        return new BinaryIndividual(size, bits);
    }

    @Override
    public List<Boolean> getList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
}
