package bijava.geometry.dim2;


/**
 *
 * @author Institute of Computational Science in Civil Engineering
 * @author Peter Milbradt
 */
public interface Region2d {
    
    public boolean contains(Point2d p);  // TODO  sollte besser isInside() heiszen

}
