package bijava.math;

import bijava.geometry.LinearPoint;
import bijava.geometry.MetricPoint;
import bijava.geometry.NormedPoint;
import bijava.geometry.VectorPoint;

/** In mathematics, quaternions are a non-commutative number system
 *  that extends the complex numbers.
 *
 * @author Tim Berthold, Nils Rinke
 * @version 1.0
 */
public class Quaternion implements VectorPoint<Quaternion>, MetricPoint<Quaternion>, NormedPoint<Quaternion> {

    /** Real part of the Quaternion.*/
    public double real;

    /**	first Imaginary part of the Quaternion.*/
    public double i;

    /** second Imaginary part of the Quaternion.*/
    public double j;

    /** third Imaginary part of the Quaternion.*/
    public double k;

    /** String used in converting Quaternion to String.*/
    public final String suffix_i = "i";

    /** String used in converting Quaternion to String.*/
    public final String suffix_j = "j";

    /** String used in converting Quaternion to String.*/
    public final String suffix_k = "k";

    /** Initialises a Quaternion representation in which all imaginary parts are
     * set to 0; the real part is set to the given value.
     * 
     * @param real Real part of the new Quaternion.
     */
    public Quaternion(double real) {
        this.real = real;
        this.i = 0.0;
        this.j = 0.0;
        this.k = 0.0;
    }

    /**
     * Initialises a Quaternion from the given ComplexNumber by extending it by
     * two more imaginary parts which both are set to zero. Real and first
     * complex part are taken from the given complex number.
     * @param c ComplexNumber to initialise the real and first imaginary part of
     * the Quaternion from.
     */
    public Quaternion(ComplexNumber c) {
        this.real = c.real;
        this.i = c.imag;
        this.j = 0.0;
        this.k = 0.0;
    }
    
    /**
     * Initialises a Quaternion with the four given parts.
     * @param real Real part
     * @param i First imaginary part
     * @param j Second imaginary part
     * @param k Third imaginary part
     */
    public Quaternion(double real, double i, double j, double k) {
        this.real = real;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    @Override
    public Quaternion add(Quaternion q) {
        return new Quaternion(this.real+q.real, this.i+q.i, this.j+q.j, this.k+q.k);
    }


    @Override
    public Quaternion sub(Quaternion q) {
        return new Quaternion(this.real-q.real, this.i-q.i, this.j-q.j, this.k-q.k);
    }

    public Quaternion mult(Quaternion q) {
        return new Quaternion(
                this.real*q.real - this.i*q.i - this.j*q.j - this.k*q.k,
                this.real*q.i + this.i*q.real + this.j*q.k - this.k*q.j,
                this.real*q.j - this.i*q.k + this.j*q.real + this.k*q.i,
                this.real*q.k + this.i*q.j - this.j*q.i + this.k*q.real);
    }

    @Override
    public Quaternion mult(double scalar) {
        return new Quaternion(
                this.real*scalar, this.i*scalar,
                this.j*scalar, this.k*scalar
        );
    }

    //TODO: Sonderfall beachten, wenn ein Wert gleich Double.NaN ist.
    public Quaternion div(Quaternion q) {
        return this.mult(q.invert());
    }

    /**
     * Negates all imaginary parts of this Quaternion.
     */
    public void conjugate() {
        this.i = -i;
        this.j = -j;
        this.k = -k;
    }

    /**
     * Returns a new, conjugated Quaternion.
     * @param q Quaternion to be conjugated.
     * @return Conjugated Quaternion as a new object.
     */
    public static Quaternion conjugate(Quaternion q) {
        return new Quaternion(q.real, -q.i, -q.j, -q.k);
    }

    /**
     * Compares all four dimensions to zero.
     * @return <code>True</code>, iff all parts of this quaternion are equal to
     * zero.
     */
    public boolean isZero() {
        return real==0 && i==0 && j==0 && k==0;
    }

    @Override
    public int dim() {
        return 4;
    }

    @Override
    public double getCoord(int i) {
        switch(i) {
            case 0: return real;
            case 1: return this.i;
            case 2: return this.j;
            case 3: return this.k;
            default: throw new IndexOutOfBoundsException("Argument: " + i + " not in 0,...,3");
        }
    }

    @Override
    public double[] getCoords() {
        return new double[]{real, i, j, k};
    }

    @Override
    public void setCoord(int i, double d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double distance(Quaternion q) {
        return this.sub(q).norm();
    }

    @Override
    public double norm() {
        return this.mult(Quaternion.conjugate(this)).real;
    }

    /**
     * Returns the length of this Vector.
     * @return The length of this Quaternion interpreted as a vector.
     */
    public double abs() {
        return Math.sqrt(norm());
    }

    /**
     * Returns a new Quaternion that will result in 1 when multiplied with this
     * Quaternion.
     */
    public Quaternion invert() {
        if(this.isZero())
            throw new ArithmeticException(" / by zero");
        return (Quaternion) Quaternion.conjugate(this).mult(1./this.norm());
    }

    @Override
    public String toString() {
        String msg = "Quaternion: ";
        msg += String.valueOf(real);
        msg += (i < 0.0) ? " " : " +";
        msg += String.valueOf(i) + "*" + suffix_i;
        msg += (j < 0.0) ? " " : " +";
        msg += String.valueOf(j) + "*" +  suffix_j;
        msg += (k < 0.0) ? " " : " +";
        msg += String.valueOf(k) + "*" + suffix_k;

        return msg;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Quaternion) {
            Quaternion q = (Quaternion) obj;
            return real==q.real && i==q.i && j==q.j && k==q.k;
        }
        return false;
    }

    /**
     * Compares the distance between this and the given Quaternion to the given
     * threshold rather than comparing them directly.
     * @param q Quaternion to compare with.
     * @param EPSILON The distance to compare with.
     * @return <code>True</code>, iff the {@link #distance(bijava.math.Quaternion) }
     * between this and the given Quaternion is less than the given threshold.
     */
    public boolean epsilonEquals(Quaternion q, double EPSILON) {
        return this.distance(q)<EPSILON;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.real) ^ (Double.doubleToLongBits(this.real) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.i) ^ (Double.doubleToLongBits(this.i) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.j) ^ (Double.doubleToLongBits(this.j) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.k) ^ (Double.doubleToLongBits(this.k) >>> 32));
        return hash;
    }



    public static void main(String[] args) {
        Quaternion q1 = new Quaternion(0, 1, -2, 3);
        System.out.println(q1);
        System.out.println(q1.norm());
        System.out.println(q1.div(q1));
    }

    
}