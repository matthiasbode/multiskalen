package bijava.geometry.dimN;

import bijava.geometry.*;

public class PointNd extends AbstractVectorNd<PointNd> implements EuclideanPoint<PointNd> {

    public PointNd() {
        super(1);
    }

    /** Creates a new instance of PointNd with the dimension i*/
    public PointNd(int i) {
        super(i);
    }

    /** Creates a new instance of PointNd */
    public PointNd(double[] d) {
        super(d);
    }

    /** Creates a new instance of PointNd */
    public PointNd(double x, double y, double z) {
        super(new double[]{x, y, z});
    }

    /** Creates a new instance of PointNd */
    public PointNd(double x, double y) {
        super(new double[]{x, y});
    }

    /** Creates a new instance of PointNd */
    public PointNd(double x) {
        super(new double[]{x});
    }

    /** euklidische Norm*/
    @Override
    public double norm() {
        double erg = 0.;
        for (int i = 0; i < x.length; i++) {
            erg += Math.pow(x[i], 2);
        }
        return Math.sqrt(erg);
    }

    /** quadrat der euklidische Norm*/
    public double normSquared() {
        double erg = 0.;
        for (int i = 0; i < x.length; i++) {
            erg += Math.pow(x[i], 2);
        }
        return erg;
    }

    /** Creates a new instance of PointNd */
    public PointNd(PointNd point) {
        super(point);
    }

    @Override
    public double distance(PointNd point) {
        if (this.dim() != point.dim()) {
            throw new IllegalArgumentException("this PointNd has the space dimension of: " + point.dim()
                    + " Required is: " + this.dim() + " D");
        } else {
            return (this.sub(point)).norm();
        }
    }

    public static double distance(PointNd p1, PointNd p2) {
        if (p1.dim() != p2.dim()) {
            throw new IllegalArgumentException("this PointNd has the space dimension of: " + p1.dim()
                    + " Required is: " + p2.dim() + " D");
        } else {
            return p1.distance(p2);
        }
    }

    public double distanceSquared(PointNd point) {
        if (this.dim() != point.dim()) {
            throw new IllegalArgumentException("this PointNd has the space dimension of: " + point.dim()
                    + " Required is: " + this.dim() + " D");
        }
        return (this.sub(point)).normSquared();
    }

    public static double distanceSquared(PointNd p1, PointNd p2) {
        if (p1.dim() != p2.dim()) {
            throw new IllegalArgumentException("this PointNd has the space dimension of: " + p1.dim()
                    + " Required is: " + p2.dim() + " D");
        } else {
            return p1.distanceSquared(p2);
        }
    }

    @Override
    public double scalarProduct(PointNd point) {
        if (this.dim() != point.dim()) {
            throw new IllegalArgumentException("this PointNd has the space dimension of: " + point.dim()
                    + " Required is: " + this.dim() + " D");
        }
        return this.dot(point);
    }

    @Override
    public PointNd mult(double scalar) {
        PointNd result = new PointNd(this);
        for (int i = 0; i < x.length; i++) {
            result.x[i] *= scalar;
        }
        return result;
    }

    @Override
    public PointNd add(PointNd point) {
        if (this.dim() != point.dim()) {
            throw new IllegalArgumentException("this PointNd has the space dimension of: " + point.dim()
                    + " Required is: " + this.dim() + " D");
        } else {
            PointNd result = new PointNd(point);
            for (int i = 0; i < x.length; i++) {
                result.x[i] += x[i];
            }
            return result;
        }
    }

    
    public final PointNd add(VectorPoint p) {
        if (p instanceof PointNd) {
            return add((PointNd) p);
        }
        return null;
    }

    @Override
    public PointNd sub(PointNd point) {
        if (this.dim() != point.dim()) {
            throw new IllegalArgumentException("this PointNd has the space dimension of: " + point.dim()
                    + " Required is: " + this.dim() + " D");
        } else {
            PointNd result = new PointNd(point);
            for (int i = 0; i < x.length; i++) {
                result.x[i] -= x[i];
            }
            return result;
        }

    }

    boolean epsilonEquals(PointNd point, double EPSILON) {
        if (this.dim() != point.dim()) {
            return false;
        } else {
            return this.distance(point) < EPSILON;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PointNd)) {
            return false;
        }
        if (((PointNd) obj).dim() != this.dim()) {
            return false;
        }

        for (int i = 0; i < this.dim(); i++) {
            if (((PointNd) obj).getCoord(i) != this.x[i]) {
                return false;
            }
        }

        return true;
    }
}
