package bijava.math;

import bijava.geometry.MetricPoint;
import bijava.geometry.VectorPoint;
import bijava.geometry.NormedPoint;

/**
 * This class implements complex number.
 * @author Christian Kollenberg, Peter Milbradt
 * @version 1.0 - 13.04.2005
 */
public class ComplexNumber implements VectorPoint<ComplexNumber>, NormedPoint<ComplexNumber>, MetricPoint<ComplexNumber>, java.io.Serializable, Cloneable {//TODO extends Number?

    /**
     *	@serial Real part of the Complex.
     */
    public double real;
    /**
     *	@serial Imaginary part of the Complex.
     */
    public double imag;
    /**
     *	Serialization ID
     */
    static final long serialVersionUID = -633126172485117692L;
    /**
     *  String used in converting Complex to String.
     *  Default is "i", but sometimes "j" is desired.
     *  Note that this is set for the class, not for
     *  a particular instance of a Complex.
     */
    public static String suffix = "i";
    private final static long negZeroBits =
            Double.doubleToLongBits(1.0 / Double.NEGATIVE_INFINITY);

    /**
     *	Constructs a Complex equal to the argument.
     *	@param	z	A Complex object
     *			If z is null then a NullPointerException is thrown.
     */
    public ComplexNumber(ComplexNumber z) {
        real = z.real;
        imag = z.imag;
    }

    /**
     *	Constructs a Complex with real and imaginary parts given
     *	by the input arguments.
     *	@param	real	A double value equal to the real part of the Complex number.
     *	@param	imag	A double value equal to the imaginary part of the Complex number.
     */
    public ComplexNumber(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     *	Constructs a Complex equal to zero.
     */
    public ComplexNumber() {
        this.real = 0.0;
        this.imag = 0.0;
    }

    /**
     *	Constructs a Complex with a zero imaginary part.
     *	@param	real	A double value equal to the real part of the Complex number.
     */
    public ComplexNumber(double real) {
        this.real = real;
        this.imag = 0.0;
    }

    @Override
    public ComplexNumber clone() {
//        return new Complex(real,imag);
        try {
            return (ComplexNumber) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable 
            throw new InternalError();
        }
    }

    private static boolean isFinite(double x) {
        return !(Double.isInfinite(x) || Double.isNaN(x));
    }

    /**
     *	Tests if this is a complex Not-a-Number (NaN) value.
     *	@return  True if either component of the Complex number is NaN;
     *	false, otherwise.
     */
    private boolean isNaN() {
        return (Double.isNaN(real) || Double.isNaN(imag));
    }

    /**
     *	Compares with another Complex.
     *	<p><em>Note: To be useful in hashtables this method
     *	considers two NaN double values to be equal. This
     *	is not according to IEEE specification.</em>
     *	@param	z	A Complex object.
     *	@return True if the real and imaginary parts of this object
     *			are equal to their counterparts in the argument; false, otherwise.
     */
    public boolean equals(ComplexNumber c) {
        if (isNaN() && c.isNaN()) {
            return true;
        } else {
            return (real == c.real && imag == c.imag);
        }
    }

    /**
     *	Compares this object against the specified object.
     *	<p><em>Note: To be useful in hashtables this method
     *	considers two NaN double values to be equal. This
     *	is not according to IEEE specification</em>
     *	@param	obj	The object to compare with.
     *	@return True if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof ComplexNumber) {
            return equals((ComplexNumber) obj);
        } else {
            return false;
        }
    }

    public boolean epsilonEquals(ComplexNumber c, double EPSILON) {
        return this.distance(c)<EPSILON;
    }

    /**
     *	Returns the magnitude of this Complex number (is the same as norm())
     *	@return A double value equal to the magnitude of this Complex number.
     */
    public double getMagnitude() {
        return norm();
    }
    
    /**
     *	Returns the phase (argument) of this Complex number, in radians.
     *	It is in the interval [0.0, 2*PI]. (is the same as argument())
     *	It's possible to display a complex number as a position vector in the
     *	complex number field. The argument is the angle between this vector
     *	and the real axis.
     *	@return A double value equal to the argument (or phase) of a this
     *			Complex number. It is in the interval [0.0, 2*PI].
     */
    public double getPhase() {
        return argument();
    }

    public void setPolar(double r, double theta) {
        real = r * Math.cos(theta);
        imag = r * Math.sin(theta);
    }

    /**
     *	Negates this Complex number.
     */
    public void negate() {
        this.real = -this.real;
        this.imag = -this.imag;
    }

    @Override
    public int dim() {
        return 2;
    }

    @Override
    public double getCoord(int i) {
        if (i < 0 || i >= 2) {
            throw new IndexOutOfBoundsException("bad coordinate index " + i);
        }
        if (i == 0) {
            return real;
        } else {
            return imag;
        }
    }

    @Override
    public void setCoord(int i, double d) {
        if (i < 0 || i >= 2) {
            throw new IndexOutOfBoundsException("bad coordinate index " + i);
        }
        if (i == 0) {
            real = d;
        } else {
            imag = d;
        }
    }

    @Override
    public double[] getCoords() {
        return new double[]{real, imag};
    }

    /**
     *	Returns the negative of a Complex number, -c.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to
     *	the negative of the argument.
     */
    public static ComplexNumber negative(ComplexNumber c) {
        return new ComplexNumber(-c.real, -c.imag);
    }

    /**
     *	Converts this Complex opject to the according conjugated
     *	complex number.
     */
    public void conjugate() {
        this.imag = -this.imag;
    }

    /**
     *	Returns the complex conjugate of a Complex number.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to complex conjugate of c.
     */
    public static ComplexNumber conjugate(ComplexNumber c) {
        return new ComplexNumber(c.real, -c.imag);
    }

    /**
     *	Returns the sum of this Complex and another Complex, this + c.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to this + c.
     */
    @Override
    public ComplexNumber add(ComplexNumber c) {
        return new ComplexNumber(real + c.real, imag + c.imag);
    }

    /**
     *	Returns the sum of this Complex and a double value, this + d.
     *	@param	d	A double value.
     *	@return A newly constructed Complex initialized to this + d.
     */
    public ComplexNumber add(double d) {
        return new ComplexNumber(real + d, imag);
    }

    /**
     *	Returns the sum of two Complex numbers, c1 + c2.
     *	@param	c1	A Complex number.
     *	@param	c2	A Complex number.
     *	@return A newly constructed Complex initialized to c1 + c2.
     */
    public static ComplexNumber add(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.real + c2.real, c1.imag + c2.imag);
    }

    /**
     *	Returns the sum of a Complex number and a double value, c + d.
     *	@param	c	A Complex number.
     *	@param	d	A double value.
     *	@return A newly constructed Complex initialized to c + d.
     */
    public static ComplexNumber add(ComplexNumber c, double d) {
        return new ComplexNumber(c.real + d, c.imag);
    }

    /**
     *	Returns the sum of a double value and a Complex number, d + c.
     *	@param	d	A double value.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to d + c.
     */
    public static ComplexNumber add(double d, ComplexNumber c) {
        return new ComplexNumber(d + c.real, c.imag);
    }

    /**
     *	Returns the difference of this Complex number and
     *	another Complex number, this - c.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to this - c.
     */
    @Override
    public ComplexNumber sub(ComplexNumber c) {
        return new ComplexNumber(real - c.real, imag - c.imag);
    }

    /**
     *	Subtracts a double value from this Complex and returns the difference, this - d.
     *	@param	d	A double value.
     *	@return A newly constructed Complex initialized to this - d.
     */
    public ComplexNumber sub(double d) {
        return new ComplexNumber(real - d, imag);
    }

    /**
     *	Returns the difference of two Complex numbers, c1 - c2.
     *	@param	c1	A Complex number.
     *	@param	c2	A Complex number.
     *	@return A newly constructed Complex initialized to c1 - c2.
     */
    public static ComplexNumber sub(ComplexNumber c1, ComplexNumber c2) {
        return new ComplexNumber(c1.real - c2.real, c1.imag - c2.imag);
    }

    /**
     *	Returns the difference of a Complex number and a double value, c - d.
     *	@param	c	A Complex number.
     *	@param	d	A double value.
     *	@return A newly constructed Complex initialized to c - d.
     */
    public static ComplexNumber sub(ComplexNumber c, double d) {
        return new ComplexNumber(c.real - d, c.imag);
    }

    /**
     *	Returns the difference of a double value and a Complex number, d - c.
     *	@param	d	A double value.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to d - c.
     */
    public static ComplexNumber sub(double d, ComplexNumber c) {
        return new ComplexNumber(d - c.real, -c.imag);
    }

    /**
     * 	Returns the product of this Complex number and another Complex number, this*c.
     * 	@param	c	A Complex number.
     * 	@return	A newly constructed Complex initialized to this*c.
     */
    public ComplexNumber mult(ComplexNumber c) {
        return mult(this, c);
    }

    /**
     *	Returns the product of this Complex number and a double value, this*d.
     *	@param	d	A double value.
     *	@return A newly constructed Complex initialized to this*d.
     */
    public ComplexNumber mult(double d) {
        return new ComplexNumber(real * d, imag * d);
    }

    /**
     *	Returns the product of two Complex numbers, c1 * c2.
     *	@param	c1	A Complex number.
     *	@param	c2	A Complex number.
     *	@return A newly constructed Complex initialized to c1 * c2.
     */
    public static ComplexNumber mult(ComplexNumber c1, ComplexNumber c2) {
        ComplexNumber result = new ComplexNumber(c1.real * c2.real - c1.imag * c2.imag, c1.real * c2.imag + c1.imag * c2.real);
        /*------------*/        if (Double.isNaN(result.real) && Double.isNaN(result.imag)) /*------------*/ {
            multNaN(c1, c2, result);
        }
        return result;
    }

    /*
     *	Returns sign(b)*|a|.
     */
    /*----*/
    private static double copysign(double a, double b) /*----*/ {
        /*----*/ double abs = Math.abs(a);
        /*----*/ return ((b < 0) ? -abs : abs);
    /*----*/    }

    /**
     *	Recovers infinities when computed x*y = NaN+i*NaN.
     *	This code is not part of mult(), so that mult
     *	could be inlined by an optimizing compiler.
     *	<p>
     *	This algorithm is adapted from the C9x Annex G:
     *	"IEC 559-compatible complex arithmetic."
     *	@param	x	First Complex operand.
     *	@param	y	Second Complex operand.
     *	@param	t	The product x*y, computed without regard to NaN.
     *				The real and/or the imaginary part of t is
     *				expected to be NaN.
     *	@return	The corrected product of x*y.
     */
    /*----*/
    private static void multNaN(ComplexNumber x, ComplexNumber y, ComplexNumber t) /*----*/ {
        /*----*/ boolean recalc = false;
        /*----*/ double a = x.real;
        /*----*/ double b = x.imag;
        /*----*/ double c = y.real;
        /*----*/ double d = y.imag;
        /*----*/
        /*----*/ if (Double.isInfinite(a) || Double.isInfinite(b)) {
            /*----*/		// x is infinite
            /*----*/ a = copysign(Double.isInfinite(a) ? 1.0 : 0.0, a);
            /*----*/ b = copysign(Double.isInfinite(b) ? 1.0 : 0.0, b);
            /*----*/ if (Double.isNaN(c)) {
                c = copysign(0.0, c);
            }
            /*----*/ if (Double.isNaN(d)) {
                d = copysign(0.0, d);
            }
            /*----*/ recalc = true;
        /*----*/        }
        /*----*/
        /*----*/ if (Double.isInfinite(c) || Double.isInfinite(d)) {
            /*----*/		// x is infinite
            /*----*/ a = copysign(Double.isInfinite(c) ? 1.0 : 0.0, c);
            /*----*/ b = copysign(Double.isInfinite(d) ? 1.0 : 0.0, d);
            /*----*/ if (Double.isNaN(a)) {
                a = copysign(0.0, a);
            }
            /*----*/ if (Double.isNaN(b)) {
                b = copysign(0.0, b);
            }
            /*----*/ recalc = true;
        /*----*/        }
        /*----*/
        /*----*/ if (!recalc) {
            /*----*/ if (Double.isInfinite(a * c) || Double.isInfinite(b * d) ||
                    /*----*/ Double.isInfinite(a * d) || Double.isInfinite(b * c)) {
                /*----*/			// Change all NaNs to 0
                /*----*/ if (Double.isNaN(a)) {
                    a = copysign(0.0, a);
                }
                /*----*/ if (Double.isNaN(b)) {
                    b = copysign(0.0, b);
                }
                /*----*/ if (Double.isNaN(c)) {
                    c = copysign(0.0, c);
                }
                /*----*/ if (Double.isNaN(d)) {
                    d = copysign(0.0, d);
                }
                /*----*/ recalc = true;
            /*----*/            }
        /*----*/        }
        /*----*/
        /*----*/ if (recalc) {
            /*----*/ t.real = Double.POSITIVE_INFINITY * (a * c - b * d);
            /*----*/ t.imag = Double.POSITIVE_INFINITY * (a * d + b * c);
        /*----*/        }
    /*----*/    }

    /**
     *	Returns the product of a Complex number and a double value, c * d.
     *	@param	c	A Complex number.
     *	@param	d	A double value.
     *	@return  A newly constructed Complex initialized to c * d.
     */
    public static ComplexNumber mult(ComplexNumber c, double d) {
        return new ComplexNumber(c.real * d, c.imag * d);
    }

    /**
     *	Returns the product of a double value and a Complex number, d * c.
     *	@param	d	A double value.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to d * c.
     */
    public static ComplexNumber mult(double d, ComplexNumber c) {
        return new ComplexNumber(d * c.real, d * c.imag);
    }

    /**
     *	Returns this Complex number divided by another Complex number, this / c.
     *	@param	c	The denominator, a Complex number.
     *	@return A newly constructed Complex initialized to this / c.
     */
    public ComplexNumber div(ComplexNumber c) {
        return div(this, c);
    }

    /**
     *	Returns this Complex number divided by double, this / d.
     *	@param	d	The denominator, a double value.
     *	@return  A newly constructed Complex initialized to this / d.
     */
    public ComplexNumber div(double d) {
        return div(this, d);
    }

    /**
     *	Returns a double dividied by this Complex number, d / this.
     *	@param	d	The numerator, a double value.
     *	@return A newly constructed Complex initialized to d / this.
     */
    public ComplexNumber divReverse(double d) {
        double den, t;
        ComplexNumber z;
        if (Math.abs(real) > Math.abs(imag)) {
            t = imag / real;
            den = real + imag * t;
            z = new ComplexNumber(d / den, -d * t / den);
        } else {
            t = real / imag;
            den = imag + real * t;
            z = new ComplexNumber(d * t / den, -d / den);
        }
        return z;
    }

    /**
     *	Returns Complex number divided by a Complex number, c1 / c2.
     *	@param	c1	The numerator, a Complex number.
     *	@param	c2	The denominator, a Complex number.
     *	@return A newly constructed Complex initialized to c1 / c2.
     */
    public static ComplexNumber div(ComplexNumber c1, ComplexNumber c2) {
        double a = c1.real;
        double b = c1.imag;
        double c = c2.real;
        double d = c2.imag;

        double scale = Math.max(Math.abs(c), Math.abs(d));
        boolean isScaleFinite = isFinite(scale);
        if (isScaleFinite) {
            c /= scale;
            d /= scale;
        }

        double den = c * c + d * d;
        ComplexNumber z = new ComplexNumber((a * c + b * d) / den, (b * c - a * d) / den);

        if (isScaleFinite) {
            z.real /= scale;
            z.imag /= scale;
        }

        // Recover infinities and zeros computed as NaN+iNaN.
        if (Double.isNaN(z.real) && Double.isNaN(z.imag)) {
            if (den == 0.0 && (!Double.isNaN(a) || !Double.isNaN(b))) {
                double s = copysign(Double.POSITIVE_INFINITY, c);
                z.real = s * a;
                z.imag = s * b;

            } else if ((Double.isInfinite(a) || Double.isInfinite(b)) &&
                    isFinite(c) && isFinite(d)) {
                a = copysign(Double.isInfinite(a) ? 1.0 : 0.0, a);
                b = copysign(Double.isInfinite(b) ? 1.0 : 0.0, b);
                z.real = Double.POSITIVE_INFINITY * (a * c + b * d);
                z.imag = Double.POSITIVE_INFINITY * (b * c - a * d);

            } else if (Double.isInfinite(scale) &&
                    isFinite(a) && isFinite(b)) {
                c = copysign(Double.isInfinite(c) ? 1.0 : 0.0, c);
                d = copysign(Double.isInfinite(d) ? 1.0 : 0.0, d);
                z.real = 0.0 * (a * c + b * d);
                z.imag = 0.0 * (b * c - a * d);
            }
        }
        return z;
    }

    /**
     *	Returns Complex number divided by a double, c / d.
     *	@param	c	The numerator, a Complex number.
     *	@param	d	The denominator, a double value.
     *	@return A newly constructed Complex initialized to c / d.
     */
    public static ComplexNumber div(ComplexNumber c, double d) {
        return new ComplexNumber(c.real / d, c.imag / d);
    }

    /**
     *	Returns a double divided by a Complex number, d / c.
     *	@param	d	The numerator, a double value.
     *	@param	c	The denominator, a Complex number.
     *	@return A newly constructed Complex initialized to d / d.
     */
    public static ComplexNumber div(double d, ComplexNumber c) {
        /*----*/ return c.divReverse(d);
    }

    /**
     *	Returns the absolute value of this Complex number
     *	@return A double value equal to the absolute value of this Complex number.
     */
    public double norm() {
        double r = Math.abs(real);
        double i = Math.abs(imag);
        if (Double.isInfinite(r) || Double.isInfinite(i)) {
            return Double.POSITIVE_INFINITY;
        } else {
            return Math.sqrt(r * r + i * i);
        }
    }

    /**
     *	Returns the absolute value of a Complex, |c|.
     *	@param	c	A Complex number.
     *	@return A double value equal to the absolute value of the argument.
     */
    public static double abs(ComplexNumber c) {
        double r = Math.abs(c.real);
        double i = Math.abs(c.imag);
        if (Double.isInfinite(r) || Double.isInfinite(i)) {
            return Double.POSITIVE_INFINITY;
        } else {
            return Math.sqrt(r * r + i * i);
        }
    }

    /**
     *	Returns the argument (phase) of this Complex number, in radians.
     *	It is in the interval [0.0, 2*PI].
     *	It's possible to display a complex number as a position vector in the
     *	complex number field. The argument is the angle between this vector
     *	and the real axis.
     *	@return A double value equal to the argument (or phase) of a this
     *			Complex number. It is in the interval [0.0, 2*PI].
     */
    public double argument() {
        double argument = Math.atan2(imag, real);
        if (argument < 0.0) {
            argument += 2 * Math.PI;
        }
        return argument;
    }

    /**
     *	Returns the argument (phase) of a Complex, in radians. It is in the
     *	interval [0.0, 2*PI].
     *	It's possible to display a complex number as a position vector in the
     *	complex number field. The argument is the angle between this vector
     *	and the real axis.
     *	@param	c	A Complex number.
     *	@return A double value equal to the argument (or phase) of a Complex.
     *			It is in the interval [0.0, 2*PI].
     */
    public static double argument(ComplexNumber c) {
        double argument = Math.atan2(c.imag, c.real);
        if (argument < 0.0) {
            argument += 2 * Math.PI;
        }
        return argument;
    }

    /**
     *	Returns this Complex number raised to the Complex c power.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized
     *			to this<SUP><FONT SIZE="1">c</FONT></SUP><FONT SIZE="3">.
     */
    public ComplexNumber pow(ComplexNumber c) {
        return exp(mult(c, this.log()));
    }

    /**
     *	Returns the Complex c1 raised to the Complex c2 power.
     *	@param	c1	A Complex number.
     *	@param	c2	A Complex number.
     *	@return A newly constructed Complex initialized
     *			to c1<SUP><FONT SIZE="1">c2</FONT></SUP><FONT SIZE="3">.
     */
    public static ComplexNumber pow(ComplexNumber c1, ComplexNumber c2) {
        return exp(mult(c2, log(c1)));
    }

    /**
     *	Returns the square root of this Complex number,
     *	with a branch cut along the negative real axis.
     *	@return A newly constructed Complex initialized
     *			to square root of this Complex.
     *			Its real part is non-negative.
     */
    public ComplexNumber sqrt() {
        ComplexNumber result = new ComplexNumber();
        if (Double.isInfinite(imag)) {
            result.real = Double.POSITIVE_INFINITY;
            result.imag = imag;
        } else if (Double.isNaN(real)) {
            result.real = result.imag = Double.NaN;
        } else if (Double.isNaN(imag)) {
            if (Double.isInfinite(real)) {
                if (real > 0) {
                    result.real = real;
                    result.imag = imag;
                } else {
                    result.real = imag;
                    result.imag = Double.POSITIVE_INFINITY;
                }
            } else {
                result.real = result.imag = Double.NaN;
            }
        } else {
            // Numerically correct version of formula 3.7.27
            // in the NBS Hanbook, as suggested by Pete Stewart.
            double t = this.norm();
            if (Math.abs(real) <= Math.abs(imag)) {
                // No cancellation in these formulas
                result.real = Math.sqrt(0.5 * (t + real));
                result.imag = Math.sqrt(0.5 * (t - real));
            } else {
                // Stable computation of the above formulas
                if (real > 0) {
                    result.real = t + real;
                    result.imag = Math.abs(imag) * Math.sqrt(0.5 / result.real);
                    result.real = Math.sqrt(0.5 * result.real);
                } else {
                    result.imag = t - real;
                    result.real = Math.abs(imag) * Math.sqrt(0.5 / result.imag);
                    result.imag = Math.sqrt(0.5 * result.imag);
                }
            }
            if (imag < 0) {
                result.imag = -result.imag;
            }
        }
        return result;
    }

    /**
     *	Returns the square root of a Complex,
     *	with a branch cut along the negative real axis.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized
     *			to square root of c. Its real part is
     *			non-negative.
     */
    public static ComplexNumber sqrt(ComplexNumber c) {
        ComplexNumber result = new ComplexNumber();
        if (Double.isInfinite(c.imag)) {
            result.real = Double.POSITIVE_INFINITY;
            result.imag = c.imag;
        } else if (Double.isNaN(c.real)) {
            result.real = result.imag = Double.NaN;
        } else if (Double.isNaN(c.imag)) {
            if (Double.isInfinite(c.real)) {
                if (c.real > 0) {
                    result.real = c.real;
                    result.imag = c.imag;
                } else {
                    result.real = c.imag;
                    result.imag = Double.POSITIVE_INFINITY;
                }
            } else {
                result.real = result.imag = Double.NaN;
            }
        } else {
            // Numerically correct version of formula 3.7.27
            // in the NBS Hanbook, as suggested by Pete Stewart.
            double t = abs(c);
            if (Math.abs(c.real) <= Math.abs(c.imag)) {
                // No cancellation in these formulas
                result.real = Math.sqrt(0.5 * (t + c.real));
                result.imag = Math.sqrt(0.5 * (t - c.real));
            } else {
                // Stable computation of the above formulas
                if (c.real > 0) {
                    result.real = t + c.real;
                    result.imag = Math.abs(c.imag) * Math.sqrt(0.5 / result.real);
                    result.real = Math.sqrt(0.5 * result.real);
                } else {
                    result.imag = t - c.real;
                    result.real = Math.abs(c.imag) * Math.sqrt(0.5 / result.imag);
                    result.imag = Math.sqrt(0.5 * result.imag);
                }
            }
            if (c.imag < 0) {
                result.imag = -result.imag;
            }
        }
        return result;
    }

    /**
     *	Returns the exponential of this Complex number.
     *	@return A newly constructed Complex initialized to exponential
     *			of this Complex number.
     */
    public ComplexNumber exp() {
        ComplexNumber result = new ComplexNumber();
        double r = Math.exp(real);
        double cosa = Math.cos(imag);
        double sina = Math.sin(imag);

        if (Double.isInfinite(imag) || Double.isNaN(imag) || Math.abs(cosa) > 1) {
            cosa = sina = Double.NaN;
        }

        if (Double.isInfinite(real) || Double.isInfinite(r)) {
            if (real < 0) {
                r = 0;
                if (Double.isInfinite(imag) || Double.isNaN(imag)) {
                    cosa = sina = 0;
                } else {
                    cosa /= Double.POSITIVE_INFINITY;
                    sina /= Double.POSITIVE_INFINITY;
                }
            } else {
                r = real;
                if (Double.isNaN(imag)) {
                    cosa = 1;
                }
            }
        }

        if (imag == 0.0) {
            result.real = r;
            result.imag = imag;
        } else {
            result.real = r * cosa;
            result.imag = r * sina;
        }
        return result;
    }

    /**
     *	Returns the exponential of a Complex c, exp(c).
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to exponential
     *			of the argument.
     */
    public static ComplexNumber exp(ComplexNumber c) {
        ComplexNumber result = new ComplexNumber();
        double r = Math.exp(c.real);
        double cosa = Math.cos(c.imag);
        double sina = Math.sin(c.imag);
        if (Double.isInfinite(c.imag) || Double.isNaN(c.imag) || Math.abs(cosa) > 1) {
            cosa = sina = Double.NaN;
        }
        if (Double.isInfinite(c.real) || Double.isInfinite(r)) {
            if (c.real < 0) {
                r = 0;
                if (Double.isInfinite(c.imag) || Double.isNaN(c.imag)) {
                    cosa = sina = 0;
                } else {
                    cosa /= Double.POSITIVE_INFINITY;
                    sina /= Double.POSITIVE_INFINITY;
                }
            } else {
                r = c.real;
                if (Double.isNaN(c.imag)) {
                    cosa = 1;
                }
            }
        }
        if (c.imag == 0.0) {
            result.real = r;
            result.imag = c.imag;
        } else {
            result.real = r * cosa;
            result.imag = r * sina;
        }
        return result;
    }

    /**
     *	Returns the logarithm of this Complex number,
     *	with a branch cut along the negative real axis.
     *	@return  A newly constructed Complex initialized to logarithm
     *			of this object. Its imaginary part is in the
     *			interval [-i*pi,i*pi].
     */
    public ComplexNumber log() {
        ComplexNumber result = new ComplexNumber();
        if (Double.isNaN(real)) {
            result.real = result.imag = real;
            if (Double.isInfinite(imag)) {
                result.real = Double.POSITIVE_INFINITY;
            }
        } else if (Double.isNaN(imag)) {
            result.real = result.imag = imag;
            if (Double.isInfinite(real)) {
                result.real = Double.POSITIVE_INFINITY;
            }
        } else {
            result.real = Math.log(this.norm());
            result.imag = this.argument();
        }
        return result;
    }

    /**
     *	Returns the logarithm of a Complex c,
     *	with a branch cut along the negative real axis.
     *	@param	c	A Complex number.
     *	@return  A newly constructed Complex initialized to logarithm
     *			of the argument. Its imaginary part is in the
     *			interval [-i*pi,i*pi].
     */
    public static ComplexNumber log(ComplexNumber c) {
        ComplexNumber result = new ComplexNumber();
        if (Double.isNaN(c.real)) {
            result.real = result.imag = c.real;
            if (Double.isInfinite(c.imag)) {
                result.real = Double.POSITIVE_INFINITY;
            }
        } else if (Double.isNaN(c.imag)) {
            result.real = result.imag = c.imag;
            if (Double.isInfinite(c.real)) {
                result.real = Double.POSITIVE_INFINITY;
            }
        } else {
            result.real = Math.log(abs(c));
            result.imag = argument(c);
        }
        return result;
    }

    /**
     *	Returns the sine of this Complex number.
     *	@return A newly constructed Complex initialized to sine of this Complex number.
     */
    public ComplexNumber sin() {
        // sin(c) = -i*sinh(i*c)
        ComplexNumber ic = new ComplexNumber(-imag, real);
        ComplexNumber s = ic.sinh();
        double r = s.imag;
        s.imag = -s.real;
        s.real = r;
        return s;
    }

    /**
     *	Returns the sine of a Complex.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to sine of the argument.
     */
    public static ComplexNumber sin(ComplexNumber c) {
        // sin(c) = -i*sinh(i*c)
        ComplexNumber ic = new ComplexNumber(-c.imag, c.real);
        ComplexNumber s = sinh(ic);
        double r = s.imag;
        s.imag = -s.real;
        s.real = r;
        return s;
    }

    /**
     *	Returns the cosine of this Complex number.
     *	@return A newly constructed Complex initialized to cosine of this Complex number.
     */
    public ComplexNumber cos() {
        // cos(c) = cosh(i*c)
        return cosh(new ComplexNumber(-imag, real));
    }

    /**
     *	Returns the cosine of a Complex.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to cosine of the argument.
     */
    public static ComplexNumber cos(ComplexNumber c) {
        // cos(c) = cosh(i*c)
        return cosh(new ComplexNumber(-c.imag, c.real));
    }

    /**
     *	Returns the tangent of a this Complex number.
     *	@return A newly constructed Complex initialized
     *			to tangent of the argument.
     */
    public ComplexNumber tan() {
        // tan = -i*tanh(i*c)
        ComplexNumber ic = new ComplexNumber(-imag, real);
        ComplexNumber result = tanh(ic);
        double r = result.imag;
        result.imag = -result.real;
        result.real = r;
        return result;
    }

    /**
     *	Returns the tangent of a Complex number.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized
     *			to tangent of the argument.
     */
    public static ComplexNumber tan(ComplexNumber c) {
        // tan = -i*tanh(i*c)
        ComplexNumber ic = new ComplexNumber(-c.imag, c.real);
        ComplexNumber result = tanh(ic);
        double r = result.imag;
        result.imag = -result.real;
        result.real = r;
        return result;
    }

    /**
     *	Returns the inverse sine (arc sine) of this Complex number,
     *	with branch cuts outside the interval [-1,1] along the
     *	real axis.
     *	@return A newly constructed Complex initialized to inverse
     *			(arc) sine of this Complex number. The real part of the
     *			result is in the interval [-pi/2,+pi/2].
     */
    public ComplexNumber asin() {
        ComplexNumber result = new ComplexNumber();
        double r = this.norm();
        if (Double.isInfinite(r)) {
            boolean infiniteX = Double.isInfinite(real);
            boolean infiniteY = Double.isInfinite(imag);
            if (infiniteX) {
                double pi2 = 0.5 * Math.PI;
                result.real = (real > 0 ? pi2 : -pi2);
                if (infiniteY) {
                    result.real /= 2;
                }
            } else if (infiniteY) {
                result.real = real / Double.POSITIVE_INFINITY;
            }
            if (Double.isNaN(imag)) {
                result.imag = -real;
                result.real = imag;
            } else {
                result.imag = imag * Double.POSITIVE_INFINITY;
            }
            return result;
        } else if (Double.isNaN(r)) {
            result.real = result.imag = Double.NaN;
            if (real == 0) {
                result.real = real;
            }
        } else if (r < 2.58095e-08) {
            // sqrt(6.0*dmach(3)) = 2.58095e-08
            result.real = real;
            result.imag = imag;
        } else if (real == 0) {
            result.real = 0;
            result.imag = Sfun.asinh(imag);
        } else if (r <= 0.1) {
            ComplexNumber c2 = this.mult(this);
            //log(eps)/log(rmax) = 8 where rmax = 0.1
            for (int i = 1; i <= 8; i++) {
                double twoi = 2 * (8 - i) + 1;
                result = mult(mult(result, c2), twoi / (twoi + 1.0));
                result.real += 1.0 / twoi;
            }
            result = result.mult(this);
        } else {
            // A&S 4.4.26
            // asin(z) = -i*log(z+sqrt(1-z)*sqrt(1+z))
            // or, since log(iz) = log(z) +i*pi/2,
            // asin(z) = pi/2 - i*log(z+sqrt(z+1)*sqrt(z-1))
            ComplexNumber w = ((imag < 0) ? negative(this) : this);
            ComplexNumber sqzp1 = sqrt(add(w, 1.0));
            if (sqzp1.imag < 0.0) {
                sqzp1 = negative(sqzp1);
            }
            ComplexNumber sqzm1 = sqrt(sub(w, 1.0));
            result = log(add(w, mult(sqzp1, sqzm1)));
            double rx = result.real;
            result.real = 0.5 * Math.PI + result.imag;
            result.imag = -rx;
        }
        if (result.real > 0.5 * Math.PI) {
            result.real = Math.PI - result.real;
            result.imag = -result.imag;
        }
        if (result.real < -0.5 * Math.PI) {
            result.real = -Math.PI - result.real;
            result.imag = -result.imag;
        }
        if (imag < 0) {
            result.real = -result.real;
            result.imag = -result.imag;
        }
        return result;
    }

    /**
     *	Returns the inverse sine (arc sine) of a Complex,
     *	with branch cuts outside the interval [-1,1] along the
     *	real axis.
     *	@param	c	A Complex number.
     *	@return A newly constructed Complex initialized to inverse
     *			(arc) sine of the argument. The real part of the
     *			result is in the interval [-pi/2,+pi/2].
     */
    public static ComplexNumber asin(ComplexNumber c) {
        ComplexNumber result = new ComplexNumber();
        double r = abs(c);
        if (Double.isInfinite(r)) {
            boolean infiniteX = Double.isInfinite(c.real);
            boolean infiniteY = Double.isInfinite(c.imag);
            if (infiniteX) {
                double pi2 = 0.5 * Math.PI;
                result.real = (c.real > 0 ? pi2 : -pi2);
                if (infiniteY) {
                    result.real /= 2;
                }
            } else if (infiniteY) {
                result.real = c.real / Double.POSITIVE_INFINITY;
            }
            if (Double.isNaN(c.imag)) {
                result.imag = -c.real;
                result.real = c.imag;
            } else {
                result.imag = c.imag * Double.POSITIVE_INFINITY;
            }
            return result;
        } else if (Double.isNaN(r)) {
            result.real = result.imag = Double.NaN;
            if (c.real == 0) {
                result.real = c.real;
            }
        } else if (r < 2.58095e-08) {
            // sqrt(6.0*dmach(3)) = 2.58095e-08
            result.real = c.real;
            result.imag = c.imag;
        } else if (c.real == 0) {
            result.real = 0;
            result.imag = Sfun.asinh(c.imag);
        } else if (r <= 0.1) {
            ComplexNumber c2 = mult(c, c);
            //log(eps)/log(rmax) = 8 where rmax = 0.1
            for (int i = 1; i <= 8; i++) {
                double twoi = 2 * (8 - i) + 1;
                result = mult(mult(result, c2), twoi / (twoi + 1.0));
                result.real += 1.0 / twoi;
            }
            result = result.mult(c);
        } else {
            // A&S 4.4.26
            // asin(z) = -i*log(z+sqrt(1-z)*sqrt(1+z))
            // or, since log(iz) = log(z) +i*pi/2,
            // asin(z) = pi/2 - i*log(z+sqrt(z+1)*sqrt(z-1))
            ComplexNumber w = ((c.imag < 0) ? negative(c) : c);
            ComplexNumber sqzp1 = sqrt(add(w, 1.0));
            if (sqzp1.imag < 0.0) {
                sqzp1 = negative(sqzp1);
            }
            ComplexNumber sqzm1 = sqrt(sub(w, 1.0));
            result = log(add(w, mult(sqzp1, sqzm1)));
            double rx = result.real;
            result.real = 0.5 * Math.PI + result.imag;
            result.imag = -rx;
        }
        if (result.real > 0.5 * Math.PI) {
            result.real = Math.PI - result.real;
            result.imag = -result.imag;
        }
        if (result.real < -0.5 * Math.PI) {
            result.real = -Math.PI - result.real;
            result.imag = -result.imag;
        }
        if (c.imag < 0) {
            result.real = -result.real;
            result.imag = -result.imag;
        }
        return result;
    }

    /**
     *	Returns the inverse cosine (arc cosine) of a Complex,
     *	with branch cuts outside the interval [-1,1] along the
     *	real axis.
     *	@param	z	A Complex number.
     *	@return A newly constructed Complex initialized to
     *			inverse (arc) cosine of the argument.
     *			The real part of the result is in the interval [0,pi].
     */
    public static ComplexNumber acos(ComplexNumber z) {
        ComplexNumber result = new ComplexNumber();
        double r = abs(z);

        if (Double.isInfinite(z.real) && Double.isNaN(z.imag)) {
            result.real = Double.NaN;
            result.imag = Double.NEGATIVE_INFINITY;
        } else if (Double.isInfinite(r)) {
            result.real = Math.atan2(Math.abs(z.imag), z.real);
            result.imag = z.imag * Double.NEGATIVE_INFINITY;
        } else if (r == 0) {
            result.real = Math.PI / 2;
            result.imag = -z.imag;
        } else {
            result = sub(Math.PI / 2, asin(z));
        }
        return result;
    }

    /**
     * Returns the inverse tangent (arc tangent) of a Complex,
     * with branch cuts outside the interval [-i,i] along the
     * imaginary axis.
     * @param	z	A Complex number.
     * @return  A newly constructed Complex initialized to
     *			inverse (arc) tangent of the argument.
     *			Its real part is in the interval [-pi/2,pi/2].
     */
    public static ComplexNumber atan(ComplexNumber z) {
        ComplexNumber result = new ComplexNumber();
        double r = abs(z);

        if (Double.isInfinite(r)) {
            double pi2 = 0.5 * Math.PI;
            double imag = (Double.isNaN(z.imag) ? 0 : z.imag);
            result.real = (z.real < 0 ? -pi2 : pi2);
            result.imag = (imag < 0 ? -1 : 1) / Double.POSITIVE_INFINITY;
            if (Double.isNaN(z.real)) {
                result.real = z.real;
            }
        } else if (Double.isNaN(r)) {
            result.real = result.imag = Double.NaN;
            if (z.imag == 0) {
                result.imag = z.imag;
            }
        } else if (r < 1.82501e-08) {
            // sqrt(3.0*dmach(3)) = 1.82501e-08
            result.real = z.real;
            result.imag = z.imag;
        } else if (r < 0.1) {
            ComplexNumber z2 = mult(z, z);
            // -0.4343*log(dmach(3))+1 = 17
            for (int k = 0; k < 17; k++) {
                ComplexNumber temp = mult(z2, result);
                int twoi = 2 * (17 - k) - 1;
                result.real = 1.0 / twoi - temp.real;
                result.imag = -temp.imag;
            }
            result = result.mult(z);
        } else if (r < 9.0072e+15) {
            // 1.0/dmach(3) = 9.0072e+15
            double r2 = r * r;
            result.real = 0.5 * Math.atan2(2 * z.real, 1.0 - r2);
            result.imag = 0.25 * Math.log((r2 + 2 * z.imag + 1) / (r2 - 2 * z.imag + 1));
        } else {
            result.real = ((z.real < 0.0) ? -0.5 * Math.PI : 0.5 * Math.PI);
        }
        return result;
    }

    /**
     * Returns the hyperbolic sine of this Complex number.
     * @return  A newly constructed Complex initialized to hyperbolic
     *			sine of this Complex number
     */
    public ComplexNumber sinh() {
        double coshx = Math.cosh(real);
        double sinhx = Math.sinh(real);
        double cosy = Math.cos(imag);
        double siny = Math.sin(imag);
        boolean infiniteX = Double.isInfinite(coshx);
        boolean infiniteY = Double.isInfinite(imag);
        ComplexNumber result;
        if (imag == 0) {
            result = new ComplexNumber(Math.sinh(real));
        } else {
            // A&S 4.5.49
            result = new ComplexNumber(sinhx * cosy, coshx * siny);
            if (infiniteY) {
                result.imag = Double.NaN;
                if (real == 0) {
                    result.real = 0;
                }
            }
            if (infiniteX) {
                result.real = real * cosy;
                result.imag = real * siny;
                if (imag == 0) {
                    result.imag = 0;
                }
                if (infiniteY) {
                    result.real = imag;
                }
            }
        }
        return result;
    }

    /**
     * Returns the hyperbolic sine of a Complex.
     * @param	c	A Complex number.
     * @return  A newly constructed Complex initialized to hyperbolic
     *			sine of the argument.
     */
    public static ComplexNumber sinh(ComplexNumber c) {
        double coshx = Math.cosh(c.real);
        double sinhx = Math.sinh(c.real);
        double cosy = Math.cos(c.imag);
        double siny = Math.sin(c.imag);
        boolean infiniteX = Double.isInfinite(coshx);
        boolean infiniteY = Double.isInfinite(c.imag);
        ComplexNumber result;
        if (c.imag == 0) {
            result = new ComplexNumber(Math.sinh(c.real));
        } else {
            // A&S 4.5.49
            result = new ComplexNumber(sinhx * cosy, coshx * siny);
            if (infiniteY) {
                result.imag = Double.NaN;
                if (c.real == 0) {
                    result.real = 0;
                }
            }
            if (infiniteX) {
                result.real = c.real * cosy;
                result.imag = c.real * siny;
                if (c.imag == 0) {
                    result.imag = 0;
                }
                if (infiniteY) {
                    result.real = c.imag;
                }
            }
        }
        return result;
    }

    /**
     * Returns the hyperbolic cosh of this Complex number.
     * @return  A newly constructed Complex initialized to
     *			the hyperbolic cosine of this Complex number.
     */
    public ComplexNumber cosh() {
        if (imag == 0) {
            return new ComplexNumber(Math.cosh(real));
        }
        double coshx = Math.cosh(real);
        double sinhx = Math.sinh(real);
        double cosy = Math.cos(imag);
        double siny = Math.sin(imag);
        boolean infiniteX = Double.isInfinite(coshx);
        boolean infiniteY = Double.isInfinite(imag);
        // A&S 4.5.50
        ComplexNumber result = new ComplexNumber(coshx * cosy, sinhx * siny);
        if (infiniteY) {
            result.real = Double.NaN;
        }
        if (real == 0) {
            result.imag = 0;
        } else if (infiniteX) {
            result.real = real * cosy;
            result.imag = real * siny;
            if (imag == 0) {
                result.imag = 0;
            }
            if (Double.isNaN(imag)) {
                result.real = real;
            } else if (infiniteY) {
                result.real = imag;
            }
        }
        return result;
    }

    /**
     * Returns the hyperbolic cosh of a Complex.
     * @param	c	A Complex number.
     * @return  A newly constructed Complex initialized to
     *			the hyperbolic cosine of the argument.
     */
    public static ComplexNumber cosh(ComplexNumber c) {
        if (c.imag == 0) {

            return new ComplexNumber(Math.cosh(c.real));
        }
        double coshx = Math.cosh(c.real);
        double sinhx = Math.sinh(c.real);
        double cosy = Math.cos(c.imag);
        double siny = Math.sin(c.imag);
        boolean infiniteX = Double.isInfinite(coshx);
        boolean infiniteY = Double.isInfinite(c.imag);
        // A&S 4.5.50
        ComplexNumber result = new ComplexNumber(coshx * cosy, sinhx * siny);
        if (infiniteY) {
            result.real = Double.NaN;
        }
        if (c.real == 0) {
            result.imag = 0;
        } else if (infiniteX) {
            result.real = c.real * cosy;
            result.imag = c.real * siny;
            if (c.imag == 0) {
                result.imag = 0;
            }
            if (Double.isNaN(c.imag)) {
                result.real = c.real;
            } else if (infiniteY) {
                result.real = c.imag;
            }
        }
        return result;
    }

    /**
     * Returns the hyperbolic tanh of a Complex.
     * @param	c	A Complex number.
     * @return  A newly constructed Complex initialized to
     *			the hyperbolic tangent of the argument.
     */
    public static ComplexNumber tanh(ComplexNumber c) {
        double sinh2x = Math.sinh(2 * c.real);
        if (c.imag == 0) {
            return new ComplexNumber(Math.tanh(c.real));
        } else if (sinh2x == 0) {
            return new ComplexNumber(0, Math.tan(c.imag));
        }

        double cosh2x = Math.cosh(2 * c.real);
        double cos2y = Math.cos(2 * c.imag);
        double sin2y = Math.sin(2 * c.imag);
        boolean infiniteX = Double.isInfinite(cosh2x);

        // Workaround for bug in JDK 1.2beta4
        if (Double.isInfinite(c.imag) || Double.isNaN(c.imag)) {
            cos2y = sin2y = Double.NaN;
        }

        if (infiniteX) {
            return new ComplexNumber(c.real > 0 ? 1 : -1);
        }

        // A&S 4.5.51
        double den = (cosh2x + cos2y);
        return new ComplexNumber(sinh2x / den, sin2y / den);
    }

    /**
     *	Returns the Complex c raised to the d power,
     *	with a branch cut for the first parameter (c) along the
     *	negative real axis.
     *	@param	c	A Complex number.
     *	@param	d	A double value.
     *	@return	A newly constructed Complex initialized to c to the power d.
     */
    public static ComplexNumber pow(ComplexNumber c, double d) {
        double absc = abs(c);
        ComplexNumber result = new ComplexNumber();

        if (absc == 0.0) {
            result = c;
        } else {
            double a = c.argument();
            double e = Math.pow(absc, d);
            result.real = e * Math.cos(d * a);
            result.imag = e * Math.sin(d * a);
        }
        return result;
    }

    /**
     *	Returns the inverse hyperbolic sine (arc sinh) of a Complex,
     *	with a branch cuts outside the interval [-i,i].
     *	@param	z	A Complex number.
     *	@return A newly constructed Complex initialized to
     *			inverse (arc) hyperbolic sine of the argument.
     *			Its imaginary part is in the interval [-i*pi/2,i*pi/2].
     */
    public static ComplexNumber asinh(ComplexNumber z) {
        // asinh(z) = i*asin(-i*z)
        ComplexNumber miz = new ComplexNumber(z.imag, -z.real);
        ComplexNumber result = asin(miz);
        double rx = result.imag;
        result.imag = result.real;
        result.real = -rx;
        return result;
    }

    /**
     *	Returns the inverse hyperbolic cosine (arc cosh) of a Complex,
     *	with a branch cut at values less than one along the real axis.
     *	@param	z	A Complex number.
     *	@return A newly constructed Complex initialized to
     *			inverse (arc) hyperbolic cosine of the argument.
     *			The real part of the result is non-negative and its
     *			imaginary part is in the interval [-i*pi,i*pi].
     */
    public static ComplexNumber acosh(ComplexNumber z) {
        ComplexNumber result = acos(z);
        double rx = -result.imag;
        result.imag = result.real;
        result.real = rx;
        if (result.real < 0 || isNegZero(result.real)) {
            result.real = -result.real;
            result.imag = -result.imag;
        }
        return result;
    }

    /**
     *	Returns true is x is a negative zero.
     */
    private static boolean isNegZero(double x) {
        return (Double.doubleToLongBits(x) == negZeroBits);
    }

    /**
     *	Returns the inverse hyperbolic tangent (arc tanh) of a Complex,
     *	with a branch cuts outside the interval [-1,1] on the real axis.
     *	@param	z	A Complex number.
     *	@return	A newly constructed Complex initialized to
     *			inverse (arc) hyperbolic tangent of the argument.
     *			The imaginary part of the result is in the interval
     *			[-i*pi/2,i*pi/2].
     */
    public static ComplexNumber atanh(ComplexNumber z) {
        // atanh(z) = i*atan(-i*z)
        ComplexNumber miz = new ComplexNumber(z.imag, -z.real);
        ComplexNumber result = atan(miz);
        double rx = result.imag;
        result.imag = result.real;
        result.real = -rx;
        return result;

    }

    /**
     *	Returns a String representation for the specified Complex.
     *	@return A String representation for this object.
     */
    @Override
    public String toString() {
        if (imag == 0.0) {
            return "Complex: " + String.valueOf(real);
        }

        if (real == 0.0) {
            return "Complex: " + String.valueOf(imag) + "i";
        }

        String sign = (imag < 0.0) ? "" : "+";
        return ("Complex: " + (String.valueOf(real) + sign + String.valueOf(imag) + "i"));
    }

    /**
     *	Parses a string into a Complex.
     *	@param	s	The string to be parsed.
     *	@return A newly constructed Complex initialized to the value represented
     *			by the string argument.
     *	@exception NumberFormatException	If the string does not contain a parsable Complex number.
     *  @exception NullPointerException		If the input argument is null.
     */
    public static ComplexNumber valueOf(String s) throws NumberFormatException {
        String input = s.trim();
        int iBeginNumber = 0;
        ComplexNumber z = new ComplexNumber();
        int state = 0;
        int sign = 1;
        boolean haveRealPart = false;

        /*
         * state values
         *	0	Initial State
         *	1	After Initial Sign
         *	2	In integer part
         *	3	In fractional part
         *	4	In exponential part (after 'e' but fore sign or digits)
         *	5	In exponential digits
         */
        for (int k = 0; k < input.length(); k++) {

            char ch = input.charAt(k);

            switch (ch) {

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (state == 0 || state == 1) {
                        state = 2;
                    } else if (state == 4) {
                        state = 5;
                    }
                    break;

                case '-':
                case '+':
                    sign = ((ch == '+') ? 1 : -1);
                    if (state == 0) {
                        state = 1;
                    } else if (state == 4) {
                        state = 5;
                    } else {
                        if (!haveRealPart) {
                            // have the real part of the number
                            z.real = Double.valueOf(input.substring(iBeginNumber, k)).doubleValue();
                            haveRealPart = true;
                            // perpare to part the imaginary part
                            iBeginNumber = k;
                            state = 1;
                        } else {
                            throw new NumberFormatException(input);
                        }
                    }
                    break;

                case '.':
                    if (state == 0 || state == 1 || state == 2) {
                        state = 3;
                    } else {
                        throw new NumberFormatException(input);
                    }
                    break;

                case 'i':
                case 'I':
                case 'j':
                case 'J':
                    if (k + 1 != input.length()) {
                        throw new NumberFormatException(input);
                    } else if (state == 0 || state == 1) {
                        z.imag = sign;
                        return z;
                    } else if (state == 2 || state == 3 || state == 5) {
                        z.imag = Double.valueOf(input.substring(iBeginNumber, k)).doubleValue();
                        return z;
                    } else {
                        throw new NumberFormatException(input);
                    }


                case 'e':
                case 'E':
                case 'd':
                case 'D':
                    if (state == 2 || state == 3) {
                        state = 4;
                    } else {
                        throw new NumberFormatException(input);
                    }
                    break;

                default:
                    throw new NumberFormatException(input);
            }

        }

        if (!haveRealPart) {
            z.real = Double.valueOf(input).doubleValue();
            return z;
        } else {
            throw new NumberFormatException(input);
        }
    }

    @Override
    public double distance(ComplexNumber y) {
        return this.sub(y).norm();
    }
}
