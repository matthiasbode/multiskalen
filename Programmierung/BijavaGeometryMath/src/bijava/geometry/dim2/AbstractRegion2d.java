package bijava.geometry.dim2;

/**
 * abstrakte Region die wesentliche Mengenoperartionen implementiert
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 */
public abstract class AbstractRegion2d implements Region2d {

    public abstract boolean contains(Point2d p);

    public final AbstractRegion2d union(final Region2d r) {
        return new AbstractRegion2d() {

            public boolean contains(Point2d p) {
                return AbstractRegion2d.this.contains(p) || r.contains(p);
            }
        };
    }
    
    public static final AbstractRegion2d union(final Region2d r1, final Region2d r2) {
        return new AbstractRegion2d() {

            public boolean contains(Point2d p) {
                return r1.contains(p) || r2.contains(p);
            }
        };
    }

    public final AbstractRegion2d section(final Region2d r) {
        return new AbstractRegion2d() {

            public boolean contains(Point2d p) {
                return AbstractRegion2d.this.contains(p) && r.contains(p);
            }
        };
    }
    
    public static final AbstractRegion2d section(final Region2d r1, final Region2d r2) {
        return new AbstractRegion2d() {

            public boolean contains(Point2d p) {
                return r1.contains(p) && r2.contains(p);
            }
        };
    }
    
    public final AbstractRegion2d complement() {
        return new AbstractRegion2d() {

            public boolean contains(Point2d p) {
                return !AbstractRegion2d.this.contains(p);
            }
        };
    }
    
    public static final AbstractRegion2d complement(final Region2d r) {
        return new AbstractRegion2d() {

            public boolean contains(Point2d p) {
                return !r.contains(p);
            }
        };
    }
}
