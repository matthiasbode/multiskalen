package bijava.math.pde;

import bijava.geometry.LinearPoint;

//Parant-Class
public  interface ModelData<E extends ModelData> extends LinearPoint<E> {

    public abstract E clone();
    /** erzeugt neue Modelldaten und initalisiert diese mit 0 */
    public abstract E initialNew(); // TODO: Konstruktor bzw. Klassenmethode?
    /**  addieren als Voraussetung bei der Interpolation */
    public abstract E add(E md);
    /** skalar multiplizieren  als Voraussetung bei der Interpolation */
    public abstract E mult(double scalar);
    
    public  E sub(E md);
    
}
