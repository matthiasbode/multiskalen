package bijava.graphics.canvas2D;

public class ObjectShape<T> extends GraphicShape{

	private static final long serialVersionUID = 1L;
	
	private T object;

	public ObjectShape(GraphicShape s, T object){
		super(s.gp);
		this.object = object;
		this.toolTip = object.toString();
	}
	
	public T getObject(){
		return object;
	}

    public void setObject(T object) {
        this.object = object;
    }
        
}
