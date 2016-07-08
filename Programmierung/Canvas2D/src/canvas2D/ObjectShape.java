package canvas2D;

import java.awt.Shape;

public class ObjectShape<T> extends GraphicShape {

    private static final long serialVersionUID = 1L;
    private T object;

    public ObjectShape(Shape s, T object) {
        this(new GraphicShape(s), object);
    }

    public ObjectShape(GraphicShape s, T object) {
        super(s.gp);
        this.object = object;
        this.toolTip = object.toString();
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
